package com.airedale.StationCreation;


import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.links.LinkManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tridium.modbusAsync.BModbusAsyncDevice;
import com.tridium.modbusAsync.BModbusAsyncNetwork;
import com.tridium.modbusCore.BModbusDevice;
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
import javax.baja.control.BControlPoint;
import javax.baja.driver.BDriverContainer;
import javax.baja.driver.point.BPointDeviceExt;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.serial.*;
import javax.baja.sys.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NiagaraType
@NiagaraAction(
        name = "write"
)
public class BStationWriter extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.airedale.StationCreation.BStationWriter(1655491992)1.0$ @*/
/* Generated Tue Mar 12 14:00:49 GMT 2024 by Slot-o-Matic (c) Tridium, Inc. 2012-2024 */

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

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStationWriter.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  private final ObjectMapper objectMapper = new ObjectMapper();
  private JsonNode jsonNodeFromFile;
  private static final String NETWORKS_FILE = "StationRead/networks.json";
  private static final String NULL_PROXY_POINTS_FILE = "StationRead/nullProxyPoints.csv";
  private static final String LINKS_FILE = "StationRead/links.csv";
  private BDriverContainer driverContainer;
  private final LinkManager linkManager = new LinkManager();


  // Main method. Calls all the other
  public void doWrite(Context cx) throws IOException {
    getDriverContainer(cx);
    getJsonNodeFromFile();
    if (jsonNodeFromFile == null) {
        logger.severe(NETWORKS_FILE + " file is empty. Aborting");
        return;
    }
    createAndAddNetworks(cx);
    addNullProxyControlPoints(cx);
    addLinks(cx);
  }

  private void addLinks(Context cx) {

    List<BLink> links = linkManager.readAndCreateLinksFromCSVFile(LINKS_FILE, cx);
//    linkManager.addLinks(links, cx);
  }

  private void addNullProxyControlPoints(Context cx) {
    BOrd nullProxyControlPointsListFileOrd = BOrd.make("file:^" + NULL_PROXY_POINTS_FILE);
    // get the points list from the file
    List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(nullProxyControlPointsListFileOrd);
    pointsList.remove(0);
    PointCreator pointCreator = new PointCreator();
    for (String point : pointsList) {
      pointCreator.addNullProxyControlPointFromCSVLine(point, cx);
    }

  }

  private void createAndAddNetworks(Context cx) {
    createAndAddBacnetNetwork(driverContainer, jsonNodeFromFile, cx);
    createAndAddModbusAsyncNetworks(driverContainer, jsonNodeFromFile, cx);
    createAndAddModbusTcpNetworks(driverContainer, jsonNodeFromFile, cx);
  }

  private void createAndAddBacnetNetwork(BDriverContainer driverContainer, JsonNode jsonNode, Context cx) {
    try {
      JsonNode bacnetNode = searchJsonNode(jsonNode, "Bacnet").get(0);
      BBacnetNetwork bacnetNetwork = createBacnetNetwork(bacnetNode, cx);
      addNetwork(driverContainer, bacnetNetwork, bacnetNode.get("networkName").textValue(), cx);
    }
    catch (IndexOutOfBoundsException e) {
        logger.warning("No Bacnet network found in " + NETWORKS_FILE);
        return;
    }
  }

  private void createAndAddModbusAsyncNetworks(BDriverContainer driverContainer, JsonNode jsonNode, Context cx) {
    List<JsonNode> modbusAsyncNetworkNodes = searchJsonNode(jsonNode, "ModbusAsync");
    for (JsonNode node : modbusAsyncNetworkNodes) {
      String name = node.get("networkName").textValue();
      BModbusAsyncNetwork modbusAsyncNetwork = createModbusAsyncNetwork(node, cx);
      addNetwork(driverContainer, modbusAsyncNetwork, name, cx);
    }
  }

  private void createAndAddModbusTcpNetworks(BDriverContainer driverContainer, JsonNode jsonNode, Context cx) {
    List<JsonNode> modbusTcpNetworkNodes = searchJsonNode(jsonNode, "ModbusTcp");
    for (JsonNode node : modbusTcpNetworkNodes) {
      String name = node.get("networkName").textValue();
      BModbusTcpNetwork modbusTcpNetwork = createModbusTcpNetwork(node, cx);
      addNetwork(driverContainer, modbusTcpNetwork, name, cx);
    }
  }

  private void addNetwork(BDriverContainer driverContainer, BModbusTcpNetwork network, String name, Context cx) {
    if (!networkExists(driverContainer, name)) {
      driverContainer.add(name, network, cx);
      logger.info("Added Modbus TCP network: " + name);
    } else {
      logger.warning("Modbus TCP network already exists: " + name);
    }
  }

  private void addNetwork(BDriverContainer driverContainer, BModbusAsyncNetwork network, String name, Context cx) {
    if (!networkExists(driverContainer, name)) {
      driverContainer.add(name, network, cx);
      logger.info("Added Modbus Async network: " + name);
    } else {
      logger.warning("Modbus Async network already exists: " + name);
    }
  }

  private void addNetwork(BDriverContainer driverContainer, BBacnetNetwork network, String name, Context cx) {
    if (!networkExists(driverContainer, name)) {
      driverContainer.add(name, network, cx);
      logger.info("Added BACnet network: " + name);
    } else {
      logger.warning("BACnet network already exists: " + name);
    }
  }

  private boolean networkExists(BDriverContainer driverContainer, String name) {
    return driverContainer.getProperties().stream()
            .anyMatch(property -> property.getName().equals(name));
  }

  private void addModbusTcpNetwork(BModbusTcpNetwork modbusTcpNetwork, String name, Context cx) {
    // check if modbus TCP network exists already
      if (!modbusTCPNetworkExists(name)){
          driverContainer.add(name, modbusTcpNetwork, cx);
          logger.info("Added Modbus TCP network: " + name);
      }
      else {
          logger.warning("This Modbus TCP network already exists.: " + name);
      }
  }

  private BModbusTcpNetwork createModbusTcpNetwork(JsonNode node, Context cx) {

    String networkName = node.get("networkName").textValue();
    Integer deviceCount = node.get("deviceCount").intValue();

    JsonNode devicesNode = node.get("devices");
    List<JsonNode> devicesList = new ArrayList<>();
    Iterator<String> fieldNames = devicesNode.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      devicesList.add(devicesNode.get(fieldName));
    }

    BModbusTcpNetwork modbusTcpNetwork = new BModbusTcpNetwork();
    // add devices
    for (int i = 0; i < deviceCount; i++) {
      JsonNode deviceNode = devicesList.get(i);
      BModbusTcpDevice modbusTcpDevice = new BModbusTcpDevice();
      String deviceName = deviceNode.get("deviceName").textValue();
      String pointsListFile =  deviceNode.get("pointsListFile").textValue();

      //TODO get IP address  from station reader
      String deviceIPAddress = deviceNode.get("deviceIPAddress").textValue();
      modbusTcpDevice.setIpAddress(deviceIPAddress);
      Integer deviceAddressString = deviceNode.get("deviceAddress").intValue();
      modbusTcpDevice.setDeviceAddress(deviceAddressString);

      // add points to the device
      BModbusTcpDevice modbusTcpDeviceWithPoints = (BModbusTcpDevice) addPointsToModbusDevice(modbusTcpDevice, pointsListFile, cx);

      modbusTcpNetwork.add(deviceName, modbusTcpDevice, cx);
    }
    return modbusTcpNetwork;
  }

  private BModbusDevice addPointsToModbusDevice(BModbusDevice modbusTcpDevice, String pointsListFile, Context cx) {

    // get points folder
    BPointDeviceExt pointsFolder = modbusTcpDevice.getPointDeviceExt();
    // turn file string into ORD
    BOrd pointsListFileOrd = BOrd.make("file:^" +pointsListFile);
    // get the points list from the file
    List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(pointsListFileOrd);
    // remove the first line as that is the headers
    pointsList.remove(0);

    PointCreator  pointCreator = new PointCreator();
    // add the points to the device
    for (String pointCsvLine : pointsList) {
      // if the string is not null or empty
      if ((pointCsvLine == null)||(pointCsvLine.equals(""))) {
            continue;
        }
        // log the pointCsvLine being processed
      logger.info("Processing pointCsvLine: " + pointCsvLine);
      BControlPoint controlPoint = pointCreator.createProxyPointFromCSVLine(pointCsvLine, "modbus");
      String pointName = pointCreator.getPointNameFromCSVLine(pointCsvLine);
      pointsFolder.add(pointName, controlPoint, cx);
    }
    return modbusTcpDevice;
  }

  private void addModbusAsyncNetwork(BModbusAsyncNetwork modbusAsyncNetwork, String modbusAsyncNetworkName, Context cx) {
    // check if modbusAsyncNetwork network exists already
    if (!modbusAsyncNetworkExists(modbusAsyncNetworkName)){
      driverContainer.add(modbusAsyncNetworkName, modbusAsyncNetwork, cx);
      logger.info("Added Modbus Async network: " + modbusAsyncNetworkName);
    }
    else {
      logger.warning("This Modbus Async network already exists.: " + modbusAsyncNetworkName);
    }
  }



  private BModbusAsyncNetwork createModbusAsyncNetwork(JsonNode node, Context cx) {
    String networkName = node.get("networkName").textValue();
    String portString = node.get("port").textValue();
    String baudRateString = node.get("baudRate").textValue();
    Integer  baudRate = extractNumber(baudRateString);
    String dataBitsString = node.get("dataBits").textValue();
    Integer dataBits = extractNumber(dataBitsString);
    String parity = node.get("parity").textValue();
    String stopBitsString = node.get("stopBits").textValue();
    Integer stopBits = extractNumber(stopBitsString);

    Integer deviceCount = node.get("deviceCount").intValue();

    JsonNode devicesNode = node.get("devices");
    List<JsonNode> devicesList = new ArrayList<>();
    Iterator<String> fieldNames = devicesNode.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      devicesList.add(devicesNode.get(fieldName));
    }

    BModbusAsyncNetwork modbusAsyncNetwork = new BModbusAsyncNetwork();
    // build the serialHelper
    BSerialHelper serialHelper = new BSerialHelper();
    serialHelper.setPortName(portString);
    BSerialBaudRate serialBaudRate = BSerialBaudRate.make(baudRate);
    serialHelper.setBaudRate(serialBaudRate);
    BSerialDataBits  serialDataBits = BSerialDataBits.make(dataBits);
    serialHelper.setDataBits(serialDataBits);
    //TODO figure out how to pass the actual parity
    BSerialParity   serialParity = BSerialParity.make(0);
    serialHelper.setParity(serialParity);
    BSerialStopBits serialStopBits = BSerialStopBits.make(stopBits);
    serialHelper.setStopBits(serialStopBits);

    modbusAsyncNetwork.setSerialPortConfig(serialHelper);

    //add devices
    for (int i = 0; i < deviceCount; i++) {
      JsonNode deviceNode = devicesList.get(i);
      BModbusAsyncDevice modbusAsyncDevice = new BModbusAsyncDevice();
      String deviceName = deviceNode.get("deviceName").textValue();
      String pointsListFile =  deviceNode.get("pointsListFile").textValue();

      Integer deviceAddress = deviceNode.get("deviceAddress").intValue();
      modbusAsyncDevice.setDeviceAddress(deviceAddress);

      // add points to the device
      BModbusAsyncDevice modbusAsyncDeviceWithPoints = (BModbusAsyncDevice) addPointsToModbusDevice(modbusAsyncDevice, pointsListFile, cx);

      modbusAsyncNetwork.add(deviceName, modbusAsyncDeviceWithPoints, cx);
    }

    return modbusAsyncNetwork;
  }

  private void addBacnetNetwork(BBacnetNetwork bacnetNetwork, String name, Context cx) {

      // check if BACnet network exists already
      if (!bacnetNetworkExists(driverContainer)){
        driverContainer.add(name, bacnetNetwork, cx);
        logger.info("Added BACnet network: " + name);
      }
      else {
          logger.warning("BACnet network already exists.");
      }

  }

  private boolean bacnetNetworkExists(BDriverContainer driverContainer) {
    for (Property property : driverContainer.getProperties()) {
          if (property.getType() == BBacnetNetwork.TYPE) {
              return true;
          }
      }
      return false;
  }
  private boolean modbusAsyncNetworkExists(String modbusAsyncNetworkName) {
    for (Property property : driverContainer.getProperties()) {

      if ((property.getType().equals(BModbusAsyncNetwork.TYPE)) &&
              (property.getName().equals(modbusAsyncNetworkName))) {
        return true;
      }
    }
    return false;
  }
  private boolean modbusTCPNetworkExists(String modbusTcpNetworkname) {
    for (Property property : driverContainer.getProperties()) {
          if ((property.getType().equals(BModbusTcpNetwork.TYPE)) &&
                  (property.getName().equals(modbusTcpNetworkname))) {
              return true;
          }
      }
      return false;
  }


  private void getDriverContainer(Context cx) {
    // try catch block to catch the exception if the driver container is not found.
    try {
      driverContainer = (BDriverContainer) BOrd.make("slot:/Drivers").get(Sys.getStation(), cx);
    }
    catch (Exception e) {
      System.out.println("Driver container not found.");
      return; // return from the method.
    }
  }
  private BBacnetNetwork createBacnetNetwork(JsonNode bacnetNetowrkNode, Context cx) {
    String networkName = bacnetNetowrkNode.get("networkName").textValue();
    String networkIDString = bacnetNetowrkNode.get("networkID").textValue();
    Integer networkID = Integer.parseInt(networkIDString.split(":")[1]);
    Integer deviceCount = bacnetNetowrkNode.get("deviceCount").intValue();

    JsonNode devicesNode = bacnetNetowrkNode.get("devices");
    List<JsonNode> devicesList = new ArrayList<>();
    Iterator<String> fieldNames = devicesNode.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      devicesList.add(devicesNode.get(fieldName));
    }

    BBacnetNetwork bacnetNetwork = new BBacnetNetwork();
    BLocalBacnetDevice localBacnetDevice = new BLocalBacnetDevice();
    BBacnetObjectIdentifier  objectId = BBacnetObjectIdentifier.make(8,networkID);

    localBacnetDevice.setObjectId(objectId);
    bacnetNetwork.setLocalDevice(localBacnetDevice);

    //add devices
    for (int i = 0; i < deviceCount; i++) {
      JsonNode deviceNode = devicesList.get(i);
      BBacnetDevice bacnetDevice = new BBacnetDevice();
      String deviceName = deviceNode.get("deviceName").textValue();
      String pointsListFile =  deviceNode.get("pointsListFile").textValue();
      Integer networkNumber =  deviceNode.get("deviceNetwork").intValue();
      String macAddressString = deviceNode.get("deviceMacAddress").textValue();
      String objectIDString =  deviceNode.get("objectID").textValue();
      Integer deviceID = Integer.parseInt(objectIDString.split(":")[1]);

      BBacnetAddress bacnetAddress = new BBacnetAddress();
      bacnetAddress.setNetworkNumber(networkNumber);

      byte[] macAddressOctet = BBacnetOctetString.stringToBytes(macAddressString);
      bacnetAddress.setMacAddress(BBacnetOctetString.make(macAddressOctet));

      bacnetDevice.setAddress(bacnetAddress);

      // set the object ID for the device.
      BBacnetConfigDeviceExt bacnetConfigDeviceExt  = new BBacnetConfigDeviceExt();
      BBacnetDeviceObject  bacnetDeviceObject = new BBacnetDeviceObject();
      BBacnetObjectIdentifier  objectIdentifier = BBacnetObjectIdentifier.make(8, deviceID);
      bacnetDeviceObject.setObjectId(objectIdentifier);
      bacnetConfigDeviceExt.setDeviceObject(bacnetDeviceObject);
      bacnetDevice.setConfig(bacnetConfigDeviceExt);

      // add points to the device
      BBacnetDevice bacnetDeviceWithPoints = addPointsToBacnetDevice(bacnetDevice, pointsListFile, cx);

      bacnetNetwork.add(deviceName, bacnetDeviceWithPoints, cx);
    }
    return bacnetNetwork;
  }

  private BBacnetDevice addPointsToBacnetDevice(BBacnetDevice bacnetDevice, String pointsListFile, Context cx) {

    // get points folder
    BPointDeviceExt pointsFolder = bacnetDevice.getPoints();
    // turn file string into ORD
    BOrd pointsListFileOrd = BOrd.make("file:^" + pointsListFile);
    // get the points list from the file
    List<String> pointsList = FileUtils.readLinesFromFileAsArrayList(pointsListFileOrd);
    // remove the first line as that is the headers
    pointsList.remove(0);

    PointCreator  pointCreator = new PointCreator();
    // add the points to the device
    for (String pointCsvLine : pointsList) {
      // if the string is not null or empty
      if ((pointCsvLine == null)||(pointCsvLine.equals(""))) {
        continue;
      }
      // log the pointCsvLine being processed
      logger.finest("Processing pointCsvLine: " + pointCsvLine);
      BControlPoint controlPoint = pointCreator.createProxyPointFromCSVLine(pointCsvLine, "bacnet");
      String pointName = pointCreator.getPointNameFromCSVLine(pointCsvLine);
      pointsFolder.add(pointName, controlPoint, cx);
    }
    return bacnetDevice;
  }

  private void  getJsonNodeFromFile() throws JsonProcessingException {
    // Read JSON string from file return json node
    BOrd fileORD = BOrd.make("file:^" + NETWORKS_FILE);
    String jsonString = FileUtils.readLinesFromFileAsSring(fileORD);
    jsonNodeFromFile = objectMapper.readTree(jsonString);
  }

  private List<JsonNode> searchJsonNode(JsonNode jsonNode, String searchString) {
    List<JsonNode> matchedNodes = new ArrayList<>();

    // Iterate over fields of the root JSON node
    for (JsonNode entry : jsonNode) {
      String fieldName = entry.get("networkName").textValue();
      if (fieldName.contains(searchString)) {
          matchedNodes.add(jsonNode.get(fieldName)); // Add the JsonNode corresponding to the matched field to the list
      }
    }
    return matchedNodes;
  }

  public static Integer extractNumber(String input) {
    // Define a pattern to match digits
    Pattern pattern = Pattern.compile("\\d+");
    // if null
    if (input == null) {
          return 0;
      }
      // Create a matcher object with the input string and the pattern matcher object
    Matcher matcher = pattern.matcher(input);

    // Check if the matcher finds a match
    if (matcher.find()) {
      // Extract the matched number
      String numberString = matcher.group();
      // Convert the string to an integer
      return Integer.parseInt(numberString);
    } else {
      logger.warning("extractNumber : No number found in the input string: "+ input);

     throw new IllegalArgumentException("No number found in the input string: "+ input );
    }
  }

  private static final Logger logger = Logger.getLogger("StationWriter");

}
