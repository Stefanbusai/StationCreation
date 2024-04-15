package com.airedale.StationCreation.wrappers.modbus;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.wrappers.DeviceWrapper;
import com.airedale.StationCreation.wrappers.bacnet.BacnetPointWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusAsync.BModbusAsyncDevice;

import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import java.io.IOException;

public class ModbusAsyncDeviceWrapper extends DeviceWrapper
{
    private BModbusAsyncDevice device;
    private Integer deviceAddress;

    public ModbusAsyncDeviceWrapper(BModbusAsyncDevice device) throws IOException {
        extractDeviceProperties(device);
        buildDeviceJSONNode();
        printPointsListToCSV();
    }

    private void extractDeviceProperties(BModbusAsyncDevice device) {
        this.device = device;
        this.deviceName = device.getName();
        this.deviceAddress = device.getDeviceAddress();
        this.pointsCount = device.getPoints().getPoints().length;
        this.points = device.getPoints().getPoints();
        this.pointsListFile = "StationRead/PointsLists/ModbusAsync/" + deviceName + "_points.csv";
    }


    private void buildDeviceJSONNode() {
        jsonDeviceNode = mapper.createObjectNode();
        jsonDeviceNode.put("deviceName", deviceName);
        jsonDeviceNode.put("deviceAddress", deviceAddress);
        jsonDeviceNode.put("pointsCount", pointsCount);
        jsonDeviceNode.put("pointsListFile", pointsListFile);
    }

    public BModbusAsyncDevice getDevice() {
        return device;
    }



    public Integer getDeviceAddress() {
        return deviceAddress;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setJsonDeviceNode(ObjectNode jsonDeviceNode) {
        this.jsonDeviceNode = jsonDeviceNode;
    }
}
