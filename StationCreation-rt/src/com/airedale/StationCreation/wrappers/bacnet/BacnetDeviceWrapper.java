package com.airedale.StationCreation.wrappers.bacnet;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.wrappers.DeviceWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.baja.bacnet.BBacnetDevice;
import javax.baja.naming.BOrd;
import java.io.IOException;
import java.util.logging.Logger;

public class BacnetDeviceWrapper extends DeviceWrapper
{
    private BBacnetDevice device;
    private String deviceFullAddress;
    private String deviceMacAddress;
    private String deviceNetwork;
    private String deviceObjectId;


    public BacnetDeviceWrapper(BBacnetDevice device) throws IOException {
        extractDeviceProperties(device);
        buildDeviceJSONNode();
    }

    private void extractDeviceProperties(BBacnetDevice device) {
        this.device = device;
        this.deviceName = device.getName();
        this.deviceFullAddress = device.getAddress().toString();
        this.deviceMacAddress = device.getAddress().getMacAddress().toString();
        this.deviceNetwork = String.valueOf(device.getAddress().getNetworkNumber());
        this.deviceObjectId = device.getObjectId().toString();
        this.pointsCount = device.getPoints().getPropertyCount();
        this.points = device.getPoints().getPoints();
        this.pointsListFile = "StationRead/PointsLists/BACnet/" + deviceName + "_points.csv";
    }

    private void buildDeviceJSONNode() {

        jsonDeviceNode.put("deviceName", deviceName);
        jsonDeviceNode.put("deviceFullAddress", deviceFullAddress);
        jsonDeviceNode.put("deviceMacAddress",  deviceMacAddress);
        jsonDeviceNode.put("deviceNetwork",  deviceNetwork);
        jsonDeviceNode.put("objectID",  deviceObjectId );
        jsonDeviceNode.put("pointsCount", pointsCount);
        jsonDeviceNode.put("pointsListFile", pointsListFile);
    }

    public BBacnetDevice getDevice() {
        return device;
    }

    public void setDevice(BBacnetDevice device) {
        this.device = device;
    }


    public String getJSONString(){
        return jsonDeviceNode.toPrettyString();
    }


    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceFullAddress() {
        return deviceFullAddress;
    }

    public void setDeviceFullAddress(String deviceFullAddress) {
        this.deviceFullAddress = deviceFullAddress;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceNetwork() {
        return deviceNetwork;
    }

    public void setDeviceNetwork(String deviceNetwork) {
        this.deviceNetwork = deviceNetwork;
    }

    public String getDeviceObjectId() {
        return deviceObjectId;
    }

    public void setDeviceObjectId(String deviceObjectId) {
        this.deviceObjectId = deviceObjectId;
    }

    public void setJsonDeviceNode(ObjectNode jsonDeviceNode) {
        this.jsonDeviceNode = jsonDeviceNode;
    }

    private static final Logger logger = Logger.getLogger("BacnetDeviceWrapper");



}
