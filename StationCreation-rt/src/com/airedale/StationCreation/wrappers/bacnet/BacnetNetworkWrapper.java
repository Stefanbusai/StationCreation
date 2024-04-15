package com.airedale.StationCreation.wrappers.bacnet;

import com.airedale.StationCreation.wrappers.NetworkWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.baja.bacnet.BBacnetNetwork;
import javax.baja.bacnet.datatypes.BBacnetObjectIdentifier;
import java.io.IOException;

public class BacnetNetworkWrapper extends NetworkWrapper
{
    private BBacnetNetwork bacnetNetwork;

    private BBacnetObjectIdentifier networkID;

    private BacnetDeviceWrapper[] devices;

    private String ipPort;


    public BacnetNetworkWrapper(BBacnetNetwork bacnetNetwork) throws IOException {
        extractNetworkProperties(bacnetNetwork);
        buildNetworkJSONNode();
    }

    private void extractNetworkProperties(BBacnetNetwork bacnetNetwork) {
        this.bacnetNetwork = bacnetNetwork;
        this.networkName = this.bacnetNetwork.getName();
        this.networkID  = this.bacnetNetwork.getObjectId();
        this.deviceCount = this.bacnetNetwork.getDeviceList().length;
        this.devices = new BacnetDeviceWrapper[this.deviceCount];
    }

    private void buildNetworkJSONNode() throws IOException {
        jsonNetworkNode.put("networkName", networkName);
        jsonNetworkNode.put("networkID", networkID.toString());
        jsonNetworkNode.put("deviceCount", deviceCount);
        buildDevicesJSONNode();
        jsonNetworkNode.put("devices", jsonDevicesNode);
    }

    private void buildDevicesJSONNode() throws IOException {
        for (int i = 0; i < this.devices.length; i++)
        {
            BacnetDeviceWrapper device = new BacnetDeviceWrapper(this.bacnetNetwork.getDeviceList()[i]);
            this.devices[i] = device;
            ObjectNode jsonSingleDeviceNode = device.getJsonDeviceNode();
            jsonDevicesNode.put(device.getDeviceName(), jsonSingleDeviceNode);
        }
    }

    public BBacnetNetwork getBacnetNetwork() {
        return bacnetNetwork;
    }
    public void setBacnetNetwork(BBacnetNetwork bacnetNetwork) {
        this.bacnetNetwork = bacnetNetwork;
    }

}
