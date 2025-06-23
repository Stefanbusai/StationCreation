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
    private static final String HYPHEN = "-";

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
                    String networkName = removeDoubleQuotes(networkJsonNode.get("networkName").toString());

                    String devicesFileName = "StationRead/" + networkName + "_devices.csv";

                    csvToPrint.append(networkName).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("networkID") ? removeDoubleQuotes(networkJsonNode.get("networkID").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("port") ? removeDoubleQuotes(networkJsonNode.get("port").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("baudRate") ? removeDoubleQuotes(networkJsonNode.get("baudRate").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("dataBits") ? removeDoubleQuotes(networkJsonNode.get("dataBits").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("parity") ? removeDoubleQuotes(networkJsonNode.get("parity").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("stopBits") ? removeDoubleQuotes(networkJsonNode.get("stopBits").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(networkJsonNode.has("deviceCount") ? removeDoubleQuotes(networkJsonNode.get("deviceCount").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(devicesFileName).append("\n");

                    if (networkJsonNode.has("devices"))
                    {
                        // create a separate CSV file for the devices
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
                    csvToPrint.append(removeDoubleQuotes(deviceJsonNode.get("deviceName").toString())).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceFullAddress") ? removeDoubleQuotes(deviceJsonNode.get("deviceFullAddress").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceMacAddress") ? removeDoubleQuotes(deviceJsonNode.get("deviceMacAddress").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceNetwork") ? removeDoubleQuotes(deviceJsonNode.get("deviceNetwork").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("objectID") ? removeDoubleQuotes(deviceJsonNode.get("objectID").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceAddress") ? removeDoubleQuotes(deviceJsonNode.get("deviceAddress").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("deviceIPAddress") ? removeDoubleQuotes(deviceJsonNode.get("deviceIPAddress").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("pointsCount") ? removeDoubleQuotes(deviceJsonNode.get("pointsCount").toString()) : HYPHEN).append(COMMA);
                    csvToPrint.append(deviceJsonNode.has("pointsListFile") ? removeDoubleQuotes(deviceJsonNode.get("pointsListFile").toString()) : HYPHEN).append("\n");
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
            String[] parts = line.split(COMMA);

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
            if (!networkID.equals(HYPHEN)) objectNode.put("networkID", networkID);
            if (!port.equals(HYPHEN)) objectNode.put("port", port);
            if (!baudRate.equals(HYPHEN)) objectNode.put("baudRate", baudRate);
            if (!dataBits.equals(HYPHEN)) objectNode.put("dataBits", dataBits);
            if (!parity.equals(HYPHEN)) objectNode.put("parity", parity);
            if (!stopBits.equals(HYPHEN)) objectNode.put("stopBits", stopBits);
            if (!deviceCount.equals(HYPHEN)) objectNode.put("deviceCount", Integer.parseInt(deviceCount));
            // the devices are contained in a separate CSV file
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
            String[] parts = line.split(COMMA);

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
            if (!deviceFullAddress.equals(HYPHEN)) objectNode.put("deviceFullAddress", deviceFullAddress);
            if (!deviceMacAddress.equals(HYPHEN)) objectNode.put("deviceMacAddress", deviceMacAddress);
            if (!deviceNetwork.equals(HYPHEN)) objectNode.put("deviceNetwork", deviceNetwork);
            if (!objectID.equals(HYPHEN)) objectNode.put("objectID", objectID);
            if (!deviceAddress.equals(HYPHEN)) objectNode.put("deviceAddress", Integer.parseInt(deviceAddress));
            if (!deviceIPAddress.equals(HYPHEN)) objectNode.put("deviceIPAddress", deviceIPAddress);
            if (!pointsCount.equals(HYPHEN)) objectNode.put("pointsCount", Integer.parseInt(pointsCount));
            if (!pointsListFile.equals(HYPHEN)) objectNode.put("pointsListFile", pointsListFile);

            rootObjectNode.set(deviceName, objectNode);
        }

        return rootObjectNode;
    }

    /**
     * Remove double quotes from the specified string.
     */
    private String removeDoubleQuotes(String str)
    {
        return str.replace("\"", "");
    }

}
