package com.airedale.StationCreation;


import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.links.LinkManager;
import com.airedale.StationCreation.wrappers.KitControlCreator;
import com.airedale.StationCreation.wrappers.TextBoxCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusAsync.BModbusAsyncDevice;
import com.tridium.modbusAsync.BModbusAsyncNetwork;
import com.tridium.modbusCore.BModbusDevice;
import com.tridium.modbusCore.client.point.BModbusClientPointFolder;
import com.tridium.modbusTcp.BModbusTcpDevice;
import com.tridium.modbusTcp.BModbusTcpNetwork;

import javax.baja.bacnet.BBacnetDevice;
import javax.baja.bacnet.BBacnetNetwork;
import javax.baja.bacnet.config.BBacnetConfigDeviceExt;
import javax.baja.bacnet.config.BBacnetDeviceObject;
import javax.baja.bacnet.datatypes.BBacnetAddress;
import javax.baja.bacnet.datatypes.BBacnetObjectIdentifier;
import javax.baja.bacnet.datatypes.BBacnetOctetString;
import javax.baja.bacnet.export.BLocalBacnetDevice;
import javax.baja.bacnet.point.BBacnetPointFolder;
import javax.baja.control.BControlPoint;
import javax.baja.driver.BDriverContainer;
import javax.baja.driver.point.BPointDeviceExt;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.serial.*;
import javax.baja.sys.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NiagaraType
@NiagaraAction(
        name = "write"
)
@NiagaraAction(
        name = "writeNetworks"
)
@NiagaraAction(
        name = "writeNullProxyPoints"
)
@NiagaraAction(
        name = "writeTextBoxes"
)
@NiagaraAction(
        name = "writeKitControlPoints"
)
@NiagaraAction(
        name = "writeLinks"
)
public class BStationWriter extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.airedale.StationCreation.BStationWriter(327587409)1.0$ @*/
/* Generated Mon Jun 02 16:05:07 BST 2025 by Slot-o-Matic (c) Tridium, Inc. 2012-2025 */

  //region Action "write"

  /**
   * Slot for the {@code write} action.
   * @see #write()
   */
  public static final Action write = newAction(0, null);

  /**
   * Invoke the {@code write} action.
   * @see #write
   */
  public void write() { invoke(write, null, null); }

  //endregion Action "write"

  //region Action "writeNetworks"

  /**
   * Slot for the {@code writeNetworks} action.
   * @see #writeNetworks()
   */
  public static final Action writeNetworks = newAction(0, null);

  /**
   * Invoke the {@code writeNetworks} action.
   * @see #writeNetworks
   */
  public void writeNetworks() { invoke(writeNetworks, null, null); }

  //endregion Action "writeNetworks"

  //region Action "writeNullProxyPoints"

  /**
   * Slot for the {@code writeNullProxyPoints} action.
   * @see #writeNullProxyPoints()
   */
  public static final Action writeNullProxyPoints = newAction(0, null);

  /**
   * Invoke the {@code writeNullProxyPoints} action.
   * @see #writeNullProxyPoints
   */
  public void writeNullProxyPoints() { invoke(writeNullProxyPoints, null, null); }

  //endregion Action "writeNullProxyPoints"

  //region Action "writeTextBoxes"

  /**
   * Slot for the {@code writeTextBoxes} action.
   * @see #writeTextBoxes()
   */
  public static final Action writeTextBoxes = newAction(0, null);

  /**
   * Invoke the {@code writeTextBoxes} action.
   * @see #writeTextBoxes
   */
  public void writeTextBoxes() { invoke(writeTextBoxes, null, null); }

  //endregion Action "writeTextBoxes"

  //region Action "writeKitControlPoints"

  /**
   * Slot for the {@code writeKitControlPoints} action.
   * @see #writeKitControlPoints()
   */
  public static final Action writeKitControlPoints = newAction(0, null);

  /**
   * Invoke the {@code writeKitControlPoints} action.
   * @see #writeKitControlPoints
   */
  public void writeKitControlPoints() { invoke(writeKitControlPoints, null, null); }

  //endregion Action "writeKitControlPoints"

  //region Action "writeLinks"

  /**
   * Slot for the {@code writeLinks} action.
   * @see #writeLinks()
   */
  public static final Action writeLinks = newAction(0, null);

  /**
   * Invoke the {@code writeLinks} action.
   * @see #writeLinks
   */
  public void writeLinks() { invoke(writeLinks, null, null); }

  //endregion Action "writeLinks"

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStationWriter.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode networksJsonNodeFromFile;
    private static final String NETWORKS_JSON_FILE = "StationRead/networks.json";
    private static final String NETWORKS_CSV_FILE = "StationRead/networks.csv";
    private static final String NULL_PROXY_POINTS_FILE = "StationRead/nullProxyPoints.csv";
    private static final String LINKS_FILE = "StationRead/links.csv";
    private static final String TEXT_BOX_POINTS_FILE = "StationRead/textBoxes.csv";
    private static final String KIT_CONTROL_FILE = "StationRead/kitControlPoints.csv";
    private BDriverContainer driverContainer;
    private final LinkManager linkManager = new LinkManager();


    // Main method. Calls all the other
    public void doWrite(Context cx) throws IOException
    {
        createAndAddNetworks(cx);
        addNullProxyControlPoints(cx);
        addTextBoxes(cx);
        addKitControlPoints(cx);
        addLinks(cx);
    }

    public void doWriteNetworks(Context cx)
    {
        createAndAddNetworks(cx);
    }

    public void doWriteNullProxyPoints(Context cx)
    {
        addNullProxyControlPoints(cx);
    }

    public void doWriteTextBoxes(Context cx)
    {
        addTextBoxes(cx);
    }

    public void doWriteKitControlPoints(Context cx) throws IOException
    {
        addKitControlPoints(cx);
    }

    public void doWriteLinks(Context cx)
    {
        addLinks(cx);
    }


    private void addKitControlPoints(Context cx) throws IOException
    {
        try
        {
            BOrd kitControlListFileOrd = BOrd.make("file:^" + KIT_CONTROL_FILE);
            List<String> kitControlPointsList = FileUtils.readLinesFromFileAsArrayList(kitControlListFileOrd);
            kitControlPointsList.remove(0);
            KitControlCreator kitControlCreator = new KitControlCreator();
            for (String kitControlPointLine : kitControlPointsList)
            {
                if (kitControlPointLine.isEmpty())
                {
                    continue;
                }
                try
                {
                    kitControlCreator.addKitControlPointFromCSVLine(kitControlPointLine, cx);
                }
                catch (Exception ex)
                {
                    logger.warning(
                            "Error processing KitControl point: " + kitControlPointLine + ", error: " + ex.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            logger.warning("Error processing file: " + KIT_CONTROL_FILE + ", error: " + ex.getMessage());
        }
    }

    private void addTextBoxes(Context cx)
    {
        try
        {
            BOrd textBoxesListFileOrd = BOrd.make("file:^" + TEXT_BOX_POINTS_FILE);
            List<String> textBoxesList = FileUtils.readLinesFromFileAsArrayList(textBoxesListFileOrd);
            textBoxesList.remove(0);
            TextBoxCreator textBoxCreator = new TextBoxCreator();
            for (String textBoxString : textBoxesList)
            {
                if (textBoxString.isEmpty())
                {
                    continue;
                }
                try
                {
                    textBoxCreator.addTextBoxFromCSVLine(textBoxString, cx);
                }
                catch (Exception ex)
                {
                    logger.warning("Error processing text box: " + textBoxString + ", error: " + ex.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            logger.warning("Error processing file: " + TEXT_BOX_POINTS_FILE + ", error: " + ex.getMessage());
        }
    }

    private void addLinks(Context cx)
    {
        try
        {
            List<BLink> links = linkManager.readAndCreateLinksFromCSVFile(LINKS_FILE, cx);
        }
        catch (Exception ex)
        {
            logger.warning("Error processing file: " + LINKS_FILE + ", error: " + ex.getMessage());
        }
    }

    private void addNullProxyControlPoints(Context cx)
    {
        try
        {
            BOrd nullProxyControlPointsListFileOrd = BOrd.make("file:^" + NULL_PROXY_POINTS_FILE);
            // get the points list from the file
            List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(nullProxyControlPointsListFileOrd);
            pointsList.remove(0);
            PointCreator pointCreator = new PointCreator();
            for (String point : pointsList)
            {
                if (point.isEmpty())
                {
                    continue;
                }
                try
                {
                    pointCreator.addNullProxyControlPointFromCSVLine(point, cx);
                }
                catch (Exception ex)
                {
                    logger.warning(
                            "Error processing null proxy control point: " + point + ", error: " + ex.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            logger.warning("Error processing file: " + NULL_PROXY_POINTS_FILE + ", error: " + ex.getMessage());
        }
    }

    private void createAndAddNetworks(Context cx)
    {
        getDriverContainer(cx);

//        try
//        {
//            getNetworksJsonNodeFromFile();
//        }
//        catch (JsonProcessingException ex)
//        {
//            logger.severe("Error processing " + NETWORKS_JSON_FILE + ", " + ex.getMessage());
//            return;
//        }
//
//        if (networksJsonNodeFromFile == null)
//        {
//            logger.severe(NETWORKS_JSON_FILE + " file is empty");
//            return;
//        }

        DevicesHandler devicesHandler = new DevicesHandler();
        networksJsonNodeFromFile = devicesHandler.createNetworksJsonNodeFromCsvFiles(NETWORKS_CSV_FILE);

        logger.info("---------------------------------------");
        logger.info(networksJsonNodeFromFile.toPrettyString());
        logger.info("---------------------------------------");

        createAndAddBacnetNetwork(driverContainer, networksJsonNodeFromFile, cx);
        createAndAddModbusAsyncNetworks(driverContainer, networksJsonNodeFromFile, cx);
        createAndAddModbusTcpNetworks(driverContainer, networksJsonNodeFromFile, cx);
    }

    private void createAndAddBacnetNetwork(BDriverContainer driverContainer, JsonNode jsonNode, Context cx)
    {
        try
        {
            JsonNode bacnetNode = searchJsonNode(jsonNode, "Bacnet").get(0);
            BBacnetNetwork bacnetNetwork = createBacnetNetwork(bacnetNode, cx);
            addNetwork(driverContainer, bacnetNetwork, bacnetNode.get("networkName").textValue(), cx);
        }
        catch (IndexOutOfBoundsException e)
        {
//        logger.warning("No Bacnet network found in " + NETWORKS_FILE);
            return;
        }
    }

    private void createAndAddModbusAsyncNetworks(BDriverContainer driverContainer, JsonNode jsonNode, Context cx)
    {
        List<JsonNode> modbusAsyncNetworkNodes = searchJsonNode(jsonNode, "ModbusAsync");
        for (JsonNode node : modbusAsyncNetworkNodes)
        {
            String name = node.get("networkName").textValue();
            BModbusAsyncNetwork modbusAsyncNetwork = createModbusAsyncNetwork(node, cx);
            addNetwork(driverContainer, modbusAsyncNetwork, name, cx);
        }
    }

    private void createAndAddModbusTcpNetworks(BDriverContainer driverContainer, JsonNode jsonNode, Context cx)
    {
        List<JsonNode> modbusTcpNetworkNodes = searchJsonNode(jsonNode, "ModbusTcp");
        for (JsonNode node : modbusTcpNetworkNodes)
        {
            String name = node.get("networkName").textValue();
            BModbusTcpNetwork modbusTcpNetwork = createModbusTcpNetwork(node, cx);
            addNetwork(driverContainer, modbusTcpNetwork, name, cx);
        }
    }

    private void addNetwork(BDriverContainer driverContainer, BModbusTcpNetwork network, String name, Context cx)
    {
        if (networkExists(driverContainer, name))
        {
            driverContainer.remove(name);
        }
        driverContainer.add(name, network, cx);
        logger.info("Added Modbus TCP network: " + name);
    }

    private void addNetwork(BDriverContainer driverContainer, BModbusAsyncNetwork network, String name, Context cx)
    {
        if (networkExists(driverContainer, name))
        {
            driverContainer.remove(name);
        }
        driverContainer.add(name, network, cx);
        logger.info("Added Modbus Async network: " + name);
    }

    private void addNetwork(BDriverContainer driverContainer, BBacnetNetwork network, String name, Context cx)
    {
        if (networkExists(driverContainer, name))
        {
            driverContainer.remove(name);
        }
        driverContainer.add(name, network, cx);
        logger.info("Added BACnet network: " + name);
    }

    private boolean networkExists(BDriverContainer driverContainer, String name)
    {
        return driverContainer.getProperties().stream()
                              .anyMatch(property -> property.getName().equals(name));
    }

    private void addModbusTcpNetwork(BModbusTcpNetwork modbusTcpNetwork, String name, Context cx)
    {
        // check if modbus TCP network exists already
        if (!modbusTCPNetworkExists(name))
        {
            driverContainer.add(name, modbusTcpNetwork, cx);
            logger.info("Added Modbus TCP network: " + name);
        }
        else
        {
            logger.warning("This Modbus TCP network already exists.: " + name);
        }
    }

    private BModbusTcpNetwork createModbusTcpNetwork(JsonNode node, Context cx)
    {

        String networkName = node.get("networkName").textValue();
        Integer deviceCount = node.get("deviceCount").intValue();

        JsonNode devicesNode = node.get("devices");
        List<JsonNode> devicesList = new ArrayList<>();
        Iterator<String> fieldNames = devicesNode.fieldNames();
        while (fieldNames.hasNext())
        {
            String fieldName = fieldNames.next();
            devicesList.add(devicesNode.get(fieldName));
        }

        BModbusTcpNetwork modbusTcpNetwork = new BModbusTcpNetwork();
        // add devices
        for (int i = 0; i < deviceCount; i++)
        {
            JsonNode deviceNode = devicesList.get(i);
            BModbusTcpDevice modbusTcpDevice = new BModbusTcpDevice();
            String deviceName = deviceNode.get("deviceName").textValue();
            String pointsListFile = deviceNode.get("pointsListFile").textValue();

            //TODO get IP address  from station reader
            String deviceIPAddress = deviceNode.get("deviceIPAddress").textValue();
            modbusTcpDevice.setIpAddress(deviceIPAddress);
            Integer deviceAddressString = deviceNode.get("deviceAddress").intValue();
            modbusTcpDevice.setDeviceAddress(deviceAddressString);

            // add points to the device
            BModbusTcpDevice modbusTcpDeviceWithPoints = (BModbusTcpDevice) addPointsToModbusDevice(modbusTcpDevice,
                    pointsListFile, cx);

            modbusTcpNetwork.add(deviceName, modbusTcpDevice, cx);
        }
        return modbusTcpNetwork;
    }

    private BModbusDevice addPointsToModbusDevice(BModbusDevice modbusTcpDevice, String pointsListFile, Context cx)
    {

        // get points folder
        BPointDeviceExt pointsFolder = modbusTcpDevice.getPointDeviceExt();
        // turn file string into ORD
        BOrd pointsListFileOrd = BOrd.make("file:^" + pointsListFile);
        // get the points list from the file
        List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(pointsListFileOrd);
        // remove the first line as that is the headers
        pointsList.remove(0);

        PointCreator pointCreator = new PointCreator();
        // add the points to the device
        for (String pointCsvLine : pointsList)
        {
            // if the string is not null or empty
            if ((pointCsvLine == null) || (pointCsvLine.equals("")))
            {
                continue;
            }
            // log the pointCsvLine being processed
            logger.info("Processing pointCsvLine: " + pointCsvLine);
            BControlPoint controlPoint = pointCreator.createProxyPointFromCSVLine(pointCsvLine, "modbus");
            String pointName = pointCreator.getPointNameFromCSVLine(pointCsvLine);
            BModbusClientPointFolder bacnetPointFolder = createModbusSubFoldersIfTheyDoNotExist(pointCsvLine,
                    modbusTcpDevice, cx);
            if (controlPoint == null)
            {
                logger.warning(
                        "Trying to add Modbus point: " + pointName + " but BControlPoint is null"); // PH: need to find out why it's null
            }
            else
            {
                if (bacnetPointFolder == null)
                {
                    pointsFolder.add(pointName, controlPoint, cx);
                }
                else
                {
                    bacnetPointFolder.add(pointName, controlPoint, cx);
                }
            }
        }
        return modbusTcpDevice;
    }

    private void addModbusAsyncNetwork(BModbusAsyncNetwork modbusAsyncNetwork, String modbusAsyncNetworkName,
                                       Context cx)
    {
        // check if modbusAsyncNetwork network exists already
        if (!modbusAsyncNetworkExists(modbusAsyncNetworkName))
        {
            driverContainer.add(modbusAsyncNetworkName, modbusAsyncNetwork, cx);
            logger.info("Added Modbus Async network: " + modbusAsyncNetworkName);
        }
        else
        {
            logger.warning("This Modbus Async network already exists.: " + modbusAsyncNetworkName);
        }
    }


    private BModbusAsyncNetwork createModbusAsyncNetwork(JsonNode node, Context cx)
    {
        String networkName = node.get("networkName").textValue();
        String portString = node.get("port").textValue();
        String baudRateString = node.get("baudRate").textValue();
        Integer baudRate = extractNumber(baudRateString);
        String dataBitsString = node.get("dataBits").textValue();
        Integer dataBits = extractNumber(dataBitsString);
        String parity = node.get("parity").textValue();
        String stopBitsString = node.get("stopBits").textValue();
        Integer stopBits = extractNumber(stopBitsString);

        Integer deviceCount = node.get("deviceCount").intValue();

        JsonNode devicesNode = node.get("devices");
        List<JsonNode> devicesList = new ArrayList<>();
        Iterator<String> fieldNames = devicesNode.fieldNames();
        while (fieldNames.hasNext())
        {
            String fieldName = fieldNames.next();
            devicesList.add(devicesNode.get(fieldName));
        }

        BModbusAsyncNetwork modbusAsyncNetwork = new BModbusAsyncNetwork();
        // build the serialHelper
        BSerialHelper serialHelper = new BSerialHelper();
        serialHelper.setPortName(portString);
        BSerialBaudRate serialBaudRate = BSerialBaudRate.make(baudRate);
        serialHelper.setBaudRate(serialBaudRate);
        BSerialDataBits serialDataBits = BSerialDataBits.make(dataBits);
        serialHelper.setDataBits(serialDataBits);
        //TODO figure out how to pass the actual parity
        BSerialParity serialParity = BSerialParity.make(0);
        serialHelper.setParity(serialParity);
        BSerialStopBits serialStopBits = BSerialStopBits.make(stopBits);
        serialHelper.setStopBits(serialStopBits);

        modbusAsyncNetwork.setSerialPortConfig(serialHelper);

        //add devices
        for (int i = 0; i < deviceCount; i++)
        {
            JsonNode deviceNode = devicesList.get(i);
            BModbusAsyncDevice modbusAsyncDevice = new BModbusAsyncDevice();
            String deviceName = deviceNode.get("deviceName").textValue();
            String pointsListFile = deviceNode.get("pointsListFile").textValue();

            Integer deviceAddress = deviceNode.get("deviceAddress").intValue();
            modbusAsyncDevice.setDeviceAddress(deviceAddress);

            // add points to the device
            BModbusAsyncDevice modbusAsyncDeviceWithPoints = (BModbusAsyncDevice) addPointsToModbusDevice(
                    modbusAsyncDevice, pointsListFile, cx);

            modbusAsyncNetwork.add(deviceName, modbusAsyncDeviceWithPoints, cx);
        }

        return modbusAsyncNetwork;
    }

    private void addBacnetNetwork(BBacnetNetwork bacnetNetwork, String name, Context cx)
    {

        // check if BACnet network exists already
        if (!bacnetNetworkExists(driverContainer))
        {
            driverContainer.add(name, bacnetNetwork, cx);
            logger.info("Added BACnet network: " + name);
        }
        else
        {
            logger.warning("BACnet network already exists.");
        }

    }

    private boolean bacnetNetworkExists(BDriverContainer driverContainer)
    {
        for (Property property : driverContainer.getProperties())
        {
            if (property.getType() == BBacnetNetwork.TYPE)
            {
                return true;
            }
        }
        return false;
    }

    private boolean modbusAsyncNetworkExists(String modbusAsyncNetworkName)
    {
        for (Property property : driverContainer.getProperties())
        {

            if ((property.getType().equals(BModbusAsyncNetwork.TYPE)) &&
                    (property.getName().equals(modbusAsyncNetworkName)))
            {
                return true;
            }
        }
        return false;
    }

    private boolean modbusTCPNetworkExists(String modbusTcpNetworkname)
    {
        for (Property property : driverContainer.getProperties())
        {
            if ((property.getType().equals(BModbusTcpNetwork.TYPE)) &&
                    (property.getName().equals(modbusTcpNetworkname)))
            {
                return true;
            }
        }
        return false;
    }


    private void getDriverContainer(Context cx)
    {
        // try catch block to catch the exception if the driver container is not found.
        try
        {
            driverContainer = (BDriverContainer) BOrd.make("slot:/Drivers").get(Sys.getStation(), cx);
        }
        catch (Exception e)
        {
            System.out.println("Driver container not found.");
            return; // return from the method.
        }
    }

    private BBacnetNetwork createBacnetNetwork(JsonNode bacnetNetowrkNode, Context cx)
    {
        String networkName = bacnetNetowrkNode.get("networkName").textValue();
        String networkIDString = bacnetNetowrkNode.get("networkID").textValue();
        Integer networkID = Integer.parseInt(networkIDString.split(":")[1]);
        Integer deviceCount = bacnetNetowrkNode.get("deviceCount").intValue();

        JsonNode devicesNode = bacnetNetowrkNode.get("devices");
        List<JsonNode> devicesList = new ArrayList<>();
        Iterator<String> fieldNames = devicesNode.fieldNames();
        while (fieldNames.hasNext())
        {
            String fieldName = fieldNames.next();
            devicesList.add(devicesNode.get(fieldName));
        }

        BBacnetNetwork bacnetNetwork = new BBacnetNetwork();
        BLocalBacnetDevice localBacnetDevice = new BLocalBacnetDevice();
        BBacnetObjectIdentifier objectId = BBacnetObjectIdentifier.make(8, networkID);

        localBacnetDevice.setObjectId(objectId);
        bacnetNetwork.setLocalDevice(localBacnetDevice);

        //add devices
        for (int i = 0; i < deviceCount; i++)
        {
            JsonNode deviceNode = devicesList.get(i);
            BBacnetDevice bacnetDevice = new BBacnetDevice();
            String deviceName = deviceNode.get("deviceName").textValue();
            String pointsListFile = deviceNode.get("pointsListFile").textValue();
            Integer networkNumber = deviceNode.get("deviceNetwork").intValue();
            String macAddressString = deviceNode.get("deviceMacAddress").textValue();


            String objectIDString = deviceNode.get("objectID").textValue();
            Integer deviceID = Integer.parseInt(objectIDString.split(":")[1]);

            BBacnetAddress bacnetAddress = new BBacnetAddress();
            bacnetAddress.setNetworkNumber(networkNumber);
            try{
                String iPAddressString = deviceNode.get("deviceFullAddress").textValue();
                byte[] macAddressOctet = ipStringToBytes(iPAddressString);
//            byte[] macAddressOctet = BBacnetOctetString.stringToBytes(macAddressString);
                bacnetAddress.setMacAddress(BBacnetOctetString.make(macAddressOctet));
            } catch (Exception e) {
                logger.warning("Invalid IP address format for  " + deviceName);
            }
            bacnetDevice.setAddress(bacnetAddress);

            // set the object ID for the device.
            BBacnetConfigDeviceExt bacnetConfigDeviceExt = new BBacnetConfigDeviceExt();
            BBacnetDeviceObject bacnetDeviceObject = new BBacnetDeviceObject();
            BBacnetObjectIdentifier objectIdentifier = BBacnetObjectIdentifier.make(8, deviceID);
            bacnetDeviceObject.setObjectId(objectIdentifier);
            bacnetConfigDeviceExt.setDeviceObject(bacnetDeviceObject);
            bacnetDevice.setConfig(bacnetConfigDeviceExt);

            // add points to the device
            BBacnetDevice bacnetDeviceWithPoints = addPointsToBacnetDevice(bacnetDevice, pointsListFile, cx);

            bacnetNetwork.add(deviceName, bacnetDeviceWithPoints, cx);
        }
        return bacnetNetwork;
    }

    private byte[] ipStringToBytes(String iPAddressString) {
        //takes an input of form "192.168.1.1" and outputs a byte array of length 6 of form c0 a8 01 01 ba c0
        String[] parts = iPAddressString.split("\\.");
        if (parts.length != 4) {
            return null;
        }
        byte[] bytes = new byte[6];
        for (int i = 0; i < 4; i++) {
            try {
                bytes[i] = (byte) Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                logger.warning("Invalid IP address: " + iPAddressString);
                return null;
            }
        }
        // Set the last two bytes to 0xBA and 0xC0 as per the original code
        bytes[4] = (byte) 0xBA;
        bytes[5] = (byte) 0xC0;
        return bytes;
    }

    private BBacnetDevice addPointsToBacnetDevice(BBacnetDevice bacnetDevice, String pointsListFile, Context cx)
    {

        // get points folder
        BPointDeviceExt pointsFolder = bacnetDevice.getPoints();
        // turn file string into ORD
        BOrd pointsListFileOrd = BOrd.make("file:^" + pointsListFile);
        // get the points list from the file
        List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(pointsListFileOrd);
        // remove the first line as that is the headers
        pointsList.remove(0);

        PointCreator pointCreator = new PointCreator();
        // add the points to the device
        for (String pointCsvLine : pointsList)
        {
            // if the string is not null or empty
            if ((pointCsvLine == null) || (pointCsvLine.equals("")))
            {
                continue;
            }
            // log the pointCsvLine being processed
            logger.info("Processing pointCsvLine: " + pointCsvLine);
            BControlPoint controlPoint = pointCreator.createProxyPointFromCSVLine(pointCsvLine, "bacnet");
            String pointName = pointCreator.getPointNameFromCSVLine(pointCsvLine);
            BBacnetPointFolder bacnetPointFolder = createBacnetSubFoldersIfTheyDoNotExist(pointCsvLine, bacnetDevice,
                    cx);
            if (controlPoint == null)
            {
                logger.warning(
                        "Trying to add BACnet point: " + pointName + " but BControlPoint is null"); // PH: need to find out why it's null
            }
            else
            {
                if (bacnetPointFolder == null)
                {
                    pointsFolder.add(pointName, controlPoint, cx);
                }
                else
                {
                    bacnetPointFolder.add(pointName, controlPoint, cx);
                }
            }
        }
        return bacnetDevice;
    }

    /**
     * Ensures that the folder path exists under the given parent.
     * If a folder in the path doesn't exist, it is created.
     *
     * @param bacnetDevice The starting bacnet device to search in
     * @param pointCsvLine The line out of the points list containing the subfolder path like "Folder1/Folder2/Folder3/"
     * @return The deepest BBacnetPointFolder in the path
     */
    private BBacnetPointFolder createBacnetSubFoldersIfTheyDoNotExist(String pointCsvLine, BBacnetDevice bacnetDevice,
                                                                      Context cx)
    {
        // extract the subfolder String from the CSV line
        String[] pointDetails = pointCsvLine.split(",");
        String pointName = pointDetails[0];
        String subFolders = pointDetails[20];
        if (Objects.equals(subFolders, "-"))
        {
            return null;
        }
        String[] folders = subFolders.split("/");
        BComponent current = bacnetDevice.getPoints();
        for (String folderName : folders)
        {
            if (folderName.trim().isEmpty())
            {
                continue; // skip empty segments
            }
            BComponent next = Arrays.stream(current.getChildComponents())
                                    .filter(child -> folderName.equals(
                                            child.getName()) && child instanceof BBacnetPointFolder)
                                    .findFirst()
                                    .orElse(null);
            if (next == null)
            {
                // Folder does not exist, so create it
                BBacnetPointFolder newFolder = new BBacnetPointFolder();
                current.add(folderName, newFolder);
                current = newFolder;
            }
            else if (next instanceof BBacnetPointFolder)
            {
                current = next;
            }
            else
            {
                throw new IllegalStateException(
                        "Component with name '" + folderName + "' exists but is not a BBacnetPointFolder.");
            }
        }

        return (BBacnetPointFolder) current;
    }

    /**
     * Ensures that the folder path exists under the given parent.
     * If a folder in the path doesn't exist, it is created.
     *
     * @param modbusDevice The starting modbus device to search in
     * @param pointCsvLine The line out of the points list containing the subfolder path like "Folder1/Folder2/Folder3/"
     * @return The deepest BBacnetPointFolder in the path
     */
    private BModbusClientPointFolder createModbusSubFoldersIfTheyDoNotExist(String pointCsvLine,
                                                                            BModbusDevice modbusDevice, Context cx)
    {
        // extract the subfolder String from the CSV line
        String[] pointDetails = pointCsvLine.split(",");
        String pointName = pointDetails[0];
        String subFolders = pointDetails[20];
        if (Objects.equals(subFolders, "-"))
        {
            return null;
        }
        String[] folders = subFolders.split("/");
        BComponent current = modbusDevice.getPointDeviceExt();
        for (String folderName : folders)
        {
            if (folderName.trim().isEmpty())
            {
                continue; // skip empty segments
            }
            BComponent next = Arrays.stream(current.getChildComponents())
                                    .filter(child -> folderName.equals(
                                            child.getName()) && child instanceof BModbusClientPointFolder)
                                    .findFirst()
                                    .orElse(null);
            if (next == null)
            {
                // Folder does not exist, so create it
                BModbusClientPointFolder newFolder = new BModbusClientPointFolder();
                current.add(folderName, newFolder);
                current = newFolder;
            }
            else if (next instanceof BModbusClientPointFolder)
            {
                current = next;
            }
            else
            {
                throw new IllegalStateException(
                        "Component with name '" + folderName + "' exists but is not a BModbusClientPointFolder.");
            }
        }

        return (BModbusClientPointFolder) current;
    }

    private void getNetworksJsonNodeFromFile() throws JsonProcessingException
    {
        // Read JSON string from file return json node
        BOrd fileORD = BOrd.make("file:^" + NETWORKS_JSON_FILE);
        String jsonString = FileUtils.readLinesFromFileAsSring(fileORD);
        networksJsonNodeFromFile = objectMapper.readTree(jsonString);
    }

    private List<JsonNode> searchJsonNode(JsonNode jsonNode, String searchString)
    {
        List<JsonNode> matchedNodes = new ArrayList<>();

        // Iterate over fields of the root JSON node
        for (JsonNode entry : jsonNode)
        {
            String fieldName = entry.get("networkName").textValue();
            if (fieldName.contains(searchString))
            {
                matchedNodes.add(
                        jsonNode.get(fieldName)); // Add the JsonNode corresponding to the matched field to the list
            }
        }
        return matchedNodes;
    }

    public static Integer extractNumber(String input)
    {
        // Define a pattern to match digits
        Pattern pattern = Pattern.compile("\\d+");
        // if null
        if (input == null)
        {
            return 0;
        }
        // Create a matcher object with the input string and the pattern matcher object
        Matcher matcher = pattern.matcher(input);

        // Check if the matcher finds a match
        if (matcher.find())
        {
            // Extract the matched number
            String numberString = matcher.group();
            // Convert the string to an integer
            return Integer.parseInt(numberString);
        }
        else
        {
            logger.warning("extractNumber : No number found in the input string: " + input);

            throw new IllegalArgumentException("No number found in the input string: " + input);
        }
    }

    private static final Logger logger = Logger.getLogger("StationWriter");

}
