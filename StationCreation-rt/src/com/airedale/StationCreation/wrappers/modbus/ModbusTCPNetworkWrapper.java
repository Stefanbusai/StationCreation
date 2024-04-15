package com.airedale.StationCreation.wrappers.modbus;

import com.airedale.StationCreation.wrappers.NetworkWrapper;
import com.tridium.modbusTcp.BModbusTcpDevice;
import com.tridium.modbusTcp.BModbusTcpNetwork;

import java.io.IOException;

public class ModbusTCPNetworkWrapper extends NetworkWrapper
{
    private BModbusTcpNetwork modbusTCPNetwork;
    private ModbusTCPDeviceWrapper[] devices;

    public ModbusTCPNetworkWrapper(BModbusTcpNetwork network) throws IOException {
        extractNetworkProperties(network);
        buildNetworkJSONNode();
    }

    private void extractNetworkProperties(BModbusTcpNetwork network) {
        this.modbusTCPNetwork = network;
        this.networkName = network.getName();
        this.deviceCount = this.modbusTCPNetwork.getDevices().length;
        this.devices = new ModbusTCPDeviceWrapper[this.deviceCount];
    }

    private void buildNetworkJSONNode() throws IOException {
        jsonNetworkNode.put("networkName", networkName);
        jsonNetworkNode.put("deviceCount", deviceCount);
        buildDevicesJSONNode();
        jsonNetworkNode.put("devices", jsonDevicesNode);
    }

    private void buildDevicesJSONNode() throws IOException {
        for (int i = 0; i < this.devices.length; i++)
        {
            ModbusTCPDeviceWrapper device = new ModbusTCPDeviceWrapper((BModbusTcpDevice) this.modbusTCPNetwork.getDevices()[i]);
            this.devices[i] = device;
            jsonDevicesNode.put(device.getDeviceName(), device.getJsonDeviceNode());
        }
    }


}
