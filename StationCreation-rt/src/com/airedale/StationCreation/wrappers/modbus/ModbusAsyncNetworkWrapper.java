package com.airedale.StationCreation.wrappers.modbus;

import com.airedale.StationCreation.wrappers.DeviceWrapper;
import com.airedale.StationCreation.wrappers.NetworkWrapper;
import com.airedale.StationCreation.wrappers.bacnet.BacnetDeviceWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusAsync.BModbusAsyncDevice;
import com.tridium.modbusAsync.BModbusAsyncNetwork;

import javax.baja.serial.BSerialHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModbusAsyncNetworkWrapper extends NetworkWrapper
{
    private BModbusAsyncNetwork modbusAsyncNetwork;
    private String port;
    private String baudRate;
    private String dataBits;
    private String parity;
    private String stopBits;

    private ModbusAsyncDeviceWrapper[] devices;

    public ModbusAsyncNetworkWrapper(BModbusAsyncNetwork network) throws IOException {
        extractNetworkProperties(network);
        buildNetworkJSONNode();
    }

    private void buildNetworkJSONNode() throws IOException {
        jsonNetworkNode.put("networkName", networkName);
        jsonNetworkNode.put("port", port);
        jsonNetworkNode.put("baudRate", baudRate);
        jsonNetworkNode.put("dataBits", dataBits);
        jsonNetworkNode.put("parity", parity);
        jsonNetworkNode.put("stopBits", stopBits);
        jsonNetworkNode.put("deviceCount", deviceCount);
        buildDevicesJSONNode();
        jsonNetworkNode.put("devices", jsonDevicesNode);
    }

    private void buildDevicesJSONNode() throws IOException {
        Map<String, String> pointsListCsvMap = new HashMap<>();

        for (int i = 0; i < this.devices.length; i++)
        {
            ModbusAsyncDeviceWrapper device = new ModbusAsyncDeviceWrapper((BModbusAsyncDevice)this.modbusAsyncNetwork.getDevices()[i]);
            this.devices[i] = device;

            ObjectNode jsonSingleDeviceNode = device.getJsonDeviceNode();
            checkForDuplicatePointsList(pointsListCsvMap, device, jsonSingleDeviceNode);
            device.printPointsListToCSV();

            jsonDevicesNode.put(device.getDeviceName(), jsonSingleDeviceNode);
        }
    }

    private void extractNetworkProperties(BModbusAsyncNetwork network) {
        this.modbusAsyncNetwork = network;
        this.networkName = network.getName();
        // Serial port
        this.port = network.getSerialPortConfig().getPortName();
        this.baudRate = network.getSerialPortConfig().getBaudRate().toString();
        this.dataBits = network.getSerialPortConfig().getDataBits().toString();
        this.parity = network.getSerialPortConfig().getParity().toString();
        this.stopBits = network.getSerialPortConfig().getStopBits().toString();
        this.deviceCount = this.modbusAsyncNetwork.getDevices().length;
        this.devices = new ModbusAsyncDeviceWrapper[this.deviceCount];
    }

}
