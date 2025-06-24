package com.airedale.StationCreation.wrappers.modbus;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.wrappers.DeviceWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusTcp.BModbusTcpDevice;

import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import java.io.IOException;

public class ModbusTCPDeviceWrapper extends DeviceWrapper
{
    private BModbusTcpDevice device;
    private Integer deviceAddress;
    private String deviceIPAddress;

    public ModbusTCPDeviceWrapper(BModbusTcpDevice device) throws IOException {
        extractDeviceProperties(device);
        buildDeviceJSONNode();
    }

    private void extractDeviceProperties(BModbusTcpDevice device) {
        this.device = device;
        this.deviceName = device.getName();
        this.deviceAddress = device.getDeviceAddress();
        this.deviceIPAddress = device.getIpAddress();
        this.pointsCount = device.getPoints().getPoints().length;
        this.points = device.getPoints().getPoints();
        pointsListFile = "StationRead/PointsLists/ModbusTCP/" + deviceName + "_points.csv";
    }


    private void buildDeviceJSONNode() {
        jsonDeviceNode.put("deviceName", deviceName);
        jsonDeviceNode.put("deviceAddress", deviceAddress);
        jsonDeviceNode.put("deviceIPAddress", deviceIPAddress);
        jsonDeviceNode.put("pointsCount", pointsCount);
        jsonDeviceNode.put("pointsListFile", pointsListFile);
    }

    public BModbusTcpDevice getDevice() {
        return device;
    }


    public Integer getDeviceAddress() {
        return deviceAddress;
    }


    public void setJsonDeviceNode(ObjectNode jsonDeviceNode) {
        this.jsonDeviceNode = jsonDeviceNode;
    }
}
