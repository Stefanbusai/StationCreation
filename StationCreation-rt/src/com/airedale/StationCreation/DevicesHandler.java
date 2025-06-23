package com.airedale.StationCreation;

import com.airedale.StationCreation.utils.FileUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.baja.naming.BOrd;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DevicesHandler
{

    private static final String COMMA = ",";

    /**
     * Constructor.
     */
    public DevicesHandler()
    {
    }

    /**
     * Print the networks CSV file.
     */
    public void printNetworksCsvFile(ObjectNode jsonNetworksNode, String networksCsvFile)
    {
        StringBuilder csvToPrint = new StringBuilder();
        csvToPrint.append("networkName").append(COMMA);      // 0
        csvToPrint.append("networkID").append(COMMA);        // 1
        csvToPrint.append("port").append(COMMA);             // 2
        csvToPrint.append("baudRate").append(COMMA);         // 3
        csvToPrint.append("dataBits").append(COMMA);         // 4
        csvToPrint.append("parity").append(COMMA);           // 5
        csvToPrint.append("stopBits").append(COMMA);         // 6
        csvToPrint.append("deviceCount").append(COMMA);      // 7
        csvToPrint.append("devicesFile").append("\n");       // 8

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNetworksNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> jsonField = fields.next();
            JsonNode networkJsonNode = jsonField.getValue();
            if (networkJsonNode.isObject()) {
                if (networkJsonNode.has("networkName"))
                {
                    String networkName = networkJsonNode.get("networkName").toString().replace("\"", "");

                    String devicesFileName = "StationRead/" + networkName + "_devices.csv";

                    csvToPrint.append(networkName).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("networkID") ? networkJsonNode.get("networkID").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("port") ? networkJsonNode.get("port").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("baudRate") ? networkJsonNode.get("baudRate").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("dataBits") ? networkJsonNode.get("dataBits").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("parity") ? networkJsonNode.get("parity").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("stopBits") ? networkJsonNode.get("stopBits").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(networkJsonNode.has("deviceCount") ? networkJsonNode.get("deviceCount").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(devicesFileName).append("\n");

                    if (networkJsonNode.has("devices"))
                    {
                        // create a separate CSV file for each device
                        printNetworkDevicesCsvFile(devicesFileName, networkJsonNode.get("devices"));
                    }
                }
            }
        }

        BOrd fileORD = BOrd.make("file:^" + networksCsvFile);
        FileUtils.deleteFileIfExists(networksCsvFile);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(networksCsvFile, csvToPrint.toString(), false);
    }

    /**
     * Print a network devices CSV file.
     */
    private void printNetworkDevicesCsvFile(String devicesFileName, JsonNode devicesJsonNode)
    {
        StringBuilder csvToPrint = new StringBuilder();
        csvToPrint.append("deviceName").append(COMMA);              // 0
        csvToPrint.append("deviceFullAddress").append(COMMA);       // 1
        csvToPrint.append("deviceMacAddress").append(COMMA);        // 2
        csvToPrint.append("deviceNetwork").append(COMMA);           // 3
        csvToPrint.append("objectID").append(COMMA);                // 4
        csvToPrint.append("deviceAddress").append(COMMA);           // 5
        csvToPrint.append("deviceIPAddress").append(COMMA);         // 6
        csvToPrint.append("pointsCount").append(COMMA);             // 7
        csvToPrint.append("pointsListFile").append("\n");           // 8

        Iterator<Map.Entry<String, JsonNode>> fields = devicesJsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> jsonField = fields.next();
            JsonNode deviceJsonNode = jsonField.getValue();
            if (deviceJsonNode.isObject()) {
                if (deviceJsonNode.has("deviceName"))
                {
                    csvToPrint.append(deviceJsonNode.get("deviceName").toString().replace("\"", "")).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceFullAddress") ? deviceJsonNode.get("deviceFullAddress").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceMacAddress") ? deviceJsonNode.get("deviceMacAddress").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceNetwork") ? deviceJsonNode.get("deviceNetwork").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("objectID") ? deviceJsonNode.get("objectID").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceAddress") ? deviceJsonNode.get("deviceAddress").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceIPAddress") ? deviceJsonNode.get("deviceIPAddress").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("pointsCount") ? deviceJsonNode.get("pointsCount").toString().replace("\"", "") : "-").append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("pointsListFile") ? deviceJsonNode.get("pointsListFile").toString().replace("\"", "") : "-").append("\n");
                }
            }
        }

        BOrd fileORD = BOrd.make("file:^" + devicesFileName);
        FileUtils.deleteFileIfExists(devicesFileName);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(devicesFileName, csvToPrint.toString(), false);
    }

    /**
     * Create the JSON node from CSV files.
     */
    public ObjectNode createNetworksJsonNodeFromCsvFiles(String networksCsvFile)
    {
        BOrd fileORD = BOrd.make("file:^" + networksCsvFile);

        List<String> lines = FileUtils.readLinesFromFileAsArrayList(fileORD);
        lines.remove(0);

        ObjectNode rootObjectNode = JsonNodeFactory.instance.objectNode();

        for (String line : lines)
        {
            String[] parts = line.split(",");

            if (parts.length != 9)
            {
                continue;
            }

            String networkName = parts[0];
            String networkID = parts[1];
            String port = parts[2];
            String baudRate = parts[3];
            String dataBits = parts[4];
            String parity = parts[5];
            String stopBits = parts[6];
            String deviceCount = parts[7];
            String devicesFile = parts[8];

            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

            objectNode.put("networkName", networkName);
            if (!networkID.equals("-")) objectNode.put("networkID", networkID);
            if (!port.equals("-")) objectNode.put("port", port);
            if (!baudRate.equals("-")) objectNode.put("baudRate", baudRate);
            if (!dataBits.equals("-")) objectNode.put("dataBits", dataBits);
            if (!parity.equals("-")) objectNode.put("parity", parity);
            if (!stopBits.equals("-")) objectNode.put("stopBits", stopBits);
            if (!deviceCount.equals("-")) objectNode.put("deviceCount", Integer.parseInt(deviceCount));
            objectNode.set("devices", createDevicesJsonNodes(devicesFile));

            rootObjectNode.set(networkName, objectNode);
        }

        return rootObjectNode;
    }

    /**
     * Create devices JSON nodes.
     */
    private ObjectNode createDevicesJsonNodes(String devicesFile)
    {
        BOrd fileORD = BOrd.make("file:^" + devicesFile);

        List<String> lines = FileUtils.readLinesFromFileAsArrayList(fileORD);
        lines.remove(0);

        ObjectNode rootObjectNode = JsonNodeFactory.instance.objectNode();

        for (String line : lines)
        {
            String[] parts = line.split(",");

            if (parts.length != 9)
            {
                continue;
            }

            String deviceName = parts[0];
            String deviceFullAddress = parts[1];
            String deviceMacAddress = parts[2];
            String deviceNetwork = parts[3];
            String objectID = parts[4];
            String deviceAddress = parts[5];
            String deviceIPAddress = parts[6];
            String pointsCount = parts[7];
            String pointsListFile = parts[8];

            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

            objectNode.put("deviceName", deviceName);
            if (!deviceFullAddress.equals("-")) objectNode.put("deviceFullAddress", deviceFullAddress);
            if (!deviceMacAddress.equals("-")) objectNode.put("deviceMacAddress", deviceMacAddress);
            if (!deviceNetwork.equals("-")) objectNode.put("deviceNetwork", deviceNetwork);
            if (!objectID.equals("-")) objectNode.put("objectID", objectID);
            if (!deviceAddress.equals("-")) objectNode.put("deviceAddress", Integer.parseInt(deviceAddress));
            if (!deviceIPAddress.equals("-")) objectNode.put("deviceIPAddress", deviceIPAddress);
            if (!pointsCount.equals("-")) objectNode.put("pointsCount", Integer.parseInt(pointsCount));
            if (!pointsListFile.equals("-")) objectNode.put("pointsListFile", pointsListFile);

            rootObjectNode.set(deviceName, objectNode);
        }

        return rootObjectNode;
    }

}
