package com.airedale.StationCreation;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.links.LinkManager;
import com.airedale.StationCreation.wrappers.bacnet.BacnetNetworkWrapper;
import com.airedale.StationCreation.wrappers.modbus.ModbusAsyncNetworkWrapper;
import com.airedale.StationCreation.wrappers.modbus.ModbusTCPNetworkWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.kitControl.timer.BBooleanDelay;
import com.tridium.kitControl.timer.BNumericDelay;
import com.tridium.kitControl.timer.BOneShot;
import com.tridium.modbusAsync.BModbusAsyncNetwork;
import com.tridium.modbusTcp.BModbusTcpNetwork;

import javax.baja.bacnet.BBacnetNetwork;
import javax.baja.collection.BITable;
import javax.baja.collection.TableCursor;
import javax.baja.control.*;
import javax.baja.driver.BDriverContainer;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;
import javax.baja.util.BWsAnnotation;
import javax.baja.util.BWsTextBlock;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@NiagaraType
@NiagaraAction(
        name = "read"
)

public class BStationReader extends BComponent {
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.airedale.StationCreation.BStationReader(3637998100)1.0$ @*/
/* Generated Fri Mar 08 14:22:50 GMT 2024 by Slot-o-Matic (c) Tridium, Inc. 2012-2024 */

  //region Action "read"

  /**
   * Slot for the {@code read} action.
   * @see #read()
   */
  public static final Action read = newAction(0, null);

  /**
   * Invoke the {@code read} action.
   * @see #read
   */
  public void read() { invoke(read, null, null); }

  //endregion Action "read"

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BStationReader.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String NETWORKS_JSON_FILE = "StationRead/networks.json";
    private static final String NETWORKS_CSV_FILE = "StationRead/networks.csv";
    private static final String NULL_PROXY_POINTS_FILE = "StationRead/nullProxyPoints.csv";
    private static final String TEXT_BOX_FILE = "StationRead/textBoxes.csv";
    private static final String KIT_CONTROL_FILE = "StationRead/kitControlPoints.csv";
    private static final String LINKS_FILE = "StationRead/links.csv";
    public static final String COMMA=",";

    private static final Set<String> KIT_CONTROL_BOOLEAN_FOUR_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:And", "kitControl:Or", "kitControl:Xor", "kitControl:BqlExprComponent"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_ONE_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:AbsValue", "kitControl:ArcCosine", "kitControl:ArcSine", "kitControl:ArcTangent",
            "kitControl:Cosine", "kitControl:Sine", "kitControl:Tangent", "kitControl:Exponential",
            "kitControl:Factorial", "kitControl:LogBase10", "kitControl:LogNatural", "kitControl:Negative",
            "kitControl:SquareRoot"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_TWO_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:GreaterThan", "kitControl:LessThan", "kitControl:GreaterThanEqual", "kitControl:LessThanEqual",
            "kitControl:Equal", "kitControl:NotEqual", "kitControl:Divide", "kitControl:Modulus", "kitControl:Power",
            "kitControl:Subtract"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_FOUR_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:Add", "kitControl:Average", "kitControl:Maximum", "kitControl:Minimum",
            "kitControl:Multiply"
    ));


    private ObjectNode jsonNetworksNode = mapper.createObjectNode();


    private BDriverContainer driverContainer;

    public void doRead(Context cx) throws IOException {
        getDriverContainer(cx);
        addNetworksToJson();
        printNetworksJsonFile();
        printNetworksCsvFile();
        processNullProxyControlPoints(cx);
        processTextBoxes(cx);
        processKitControlBlocks(cx);
        readLinks(cx);
    }

    private void readLinks(Context cx) {
        LinkManager linkManager = new LinkManager();
        List<BLink> links = linkManager.findAllLinks(cx);
        LinkManager.writeLinksToCSVFile(links, LINKS_FILE, cx);
    }

    private void processKitControlBlocks(Context cx) throws IOException {
        List<BComponent> kitControlComponentsList = findKitControlPoints();
        logger.info("kitControl Components: " + kitControlComponentsList.size());
        StringBuilder CSVToPrint = new StringBuilder();
        //build headers
        CSVToPrint.append("PointName").append(COMMA);       // 0
        CSVToPrint.append("parentSlotPath").append(COMMA);   // 1
        CSVToPrint.append("Facets").append(COMMA);          // 2
        CSVToPrint.append("PointType").append(COMMA);       // 3
        CSVToPrint.append("wsAnnotation").append(COMMA);    // 4
        CSVToPrint.append("Slot1").append(COMMA);       // 5
        CSVToPrint.append("Slot2").append(COMMA);       // 6
        CSVToPrint.append("Slot3").append(COMMA);       // 7
        CSVToPrint.append("Slot4").append(COMMA);       // 8
        CSVToPrint.append("Slot5").append(COMMA);       // 9
        CSVToPrint.append("Slot6").append("\n");    // 10

        for (BComponent kitControlComponent : kitControlComponentsList) {
            // extract properties of that control point
            String name = kitControlComponent.getName();
            String type = kitControlComponent.getType().toString();
            String slotPath = ((BComponent) kitControlComponent.getParent()).getSlotPathOrd().encodeToString();
            String wsAnnotationString;
            String pointType = kitControlComponent.getType().toString();

            BWsAnnotation wsAnnotation = (BWsAnnotation) kitControlComponent.get("wsAnnotation");
            if (wsAnnotation == null) {
                wsAnnotationString = "-";
            } else {
                wsAnnotationString = wsAnnotation.encodeToString().replace(",", ":");
            }
            String pointFacets = "-";
            if (isBControlPoint(kitControlComponent)) {
                pointFacets = ((BControlPoint)kitControlComponent).getFacets().encodeToString();
            }
            //replace , with  | in facets so as not to confuse the CSV format
            pointFacets = pointFacets.replace(",", "|");

            String slot1 = "-";
            String slot2 = "-";
            String slot3 = "-";
            String slot4 = "-";
            String slot5 = "-";
            String slot6 = "-";
            boolean pointHasOnlySimplyInputs = true;
            if (KIT_CONTROL_BOOLEAN_FOUR_INPUT_TYPES.contains(pointType)) {
                slot1 = String.valueOf(((BStatusBoolean) kitControlComponent.get("inA")).getValue());
                slot2 = String.valueOf(((BStatusBoolean) kitControlComponent.get("inB")).getValue());
                slot3 = String.valueOf(((BStatusBoolean) kitControlComponent.get("inC")).getValue());
                slot4 = String.valueOf(((BStatusBoolean) kitControlComponent.get("inD")).getValue());
            }
            else if (KIT_CONTROL_NUMERIC_ONE_INPUT_TYPES.contains(pointType)) {
                slot1 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inA")).getValue());
            }else if (KIT_CONTROL_NUMERIC_TWO_INPUT_TYPES.contains(pointType)) {
                slot1 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inA")).getValue());
                slot2 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inB")).getValue());
            }
            else if (KIT_CONTROL_NUMERIC_FOUR_INPUT_TYPES.contains(pointType)) {
                slot1 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inA")).getValue());
                slot2 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inB")).getValue());
                slot3 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inC")).getValue());
                slot4 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inD")).getValue());
            }
            else pointHasOnlySimplyInputs = false;

            if (!pointHasOnlySimplyInputs){
                switch (pointType) {
                    case "kitControl:Reset":
                        slot1 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inputLowLimit")).getValue());
                        slot2 = String.valueOf(((BStatusNumeric) kitControlComponent.get("inputHighLimit")).getValue());
                        slot3 = String.valueOf(((BStatusNumeric) kitControlComponent.get("outputLowLimit")).getValue());
                        slot4 = String.valueOf(((BStatusNumeric) kitControlComponent.get("outputHighLimit")).getValue());
                        break;
                    case "kitControl:BooleanDelay":
                        BBooleanDelay bBooleanDelay;
                        assert kitControlComponent instanceof BBooleanDelay;
                        bBooleanDelay = (BBooleanDelay) kitControlComponent;
                        slot1 = bBooleanDelay.getOnDelay().encodeToString();
                        slot2 = bBooleanDelay.getOffDelay().encodeToString();
                        break;
                    case "kitControl:NumericDelay":
                        BNumericDelay bNumericDelay = new BNumericDelay();
                        assert kitControlComponent instanceof BNumericDelay;
                        bNumericDelay = (BNumericDelay) kitControlComponent;
                        slot1 = bNumericDelay.getUpdateTime().encodeToString();
                        slot2 = String.valueOf(bNumericDelay.getMaxStepSize());
                        break;
                    case "kitControl:OneShot":
                        BOneShot bOneShot;
                        assert kitControlComponent instanceof BOneShot;
                        bOneShot = (BOneShot) kitControlComponent;
                        slot1 = bOneShot.getTime().encodeToString();
                        break;
                    case "kitControl:Counter":
                        slot1 = ((BRelTime) kitControlComponent.get("presetValue")).encodeToString();
                        slot2 = String.valueOf(((BStatusNumeric) kitControlComponent.get("counterIncrement")).getValue());
                        break;
                }
            }

            CSVToPrint.append(name).append(COMMA);         // 0
            CSVToPrint.append(slotPath).append(COMMA);     // 1
            CSVToPrint.append(pointFacets).append(COMMA);       // 2
            CSVToPrint.append(pointType).append(COMMA);         // 3
            CSVToPrint.append(wsAnnotationString).append(COMMA);    // 4
            CSVToPrint.append(slot1).append(COMMA);       // 5
            CSVToPrint.append(slot2).append(COMMA);       // 6
            CSVToPrint.append(slot3).append(COMMA);       // 7
            CSVToPrint.append(slot4).append(COMMA);       // 8
            CSVToPrint.append(slot5).append(COMMA);       // 9
            CSVToPrint.append(slot6).append("\n");    // 10

        }
        BOrd fileORD = BOrd.make("file:^" + KIT_CONTROL_FILE);
        FileUtils.deleteFileIfExists(KIT_CONTROL_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(KIT_CONTROL_FILE, CSVToPrint.toString(), false);
    }

    private boolean isBControlPoint(BComponent component) {
        return component.getType().equals(BControlPoint.TYPE);
    }


    private void processTextBoxes(Context cx) throws IOException {
        List<BWsTextBlock> textBoxesList = findTextBoxes(cx);
        logger.info("Text Boxes: " + textBoxesList.size());
        StringBuilder CSVToPrint = new StringBuilder();
        //build headers
        CSVToPrint.append("Name").append(COMMA);         // 0
        CSVToPrint.append("Foreground").append(COMMA);   // 1
        CSVToPrint.append("Background").append(COMMA);   // 2
        CSVToPrint.append("Border").append(COMMA);       // 3
        CSVToPrint.append("WsAnnotation").append(COMMA); // 4
        CSVToPrint.append("Font").append(COMMA);         // 5
        CSVToPrint.append("SlotPath").append(COMMA);     // 6
        CSVToPrint.append("Text").append("\n");          // 7

        for (BWsTextBlock textBox : textBoxesList){
            // extract properties of that text box
            String name = textBox.getName();
            String foreground = textBox.getForeground();
            String background = textBox.getBackground();
            boolean border = textBox.getBorder();
            String wsAnnotationString;
            BWsAnnotation wsAnnotation = textBox.getWsAnnotation();
            if (wsAnnotation == null) {
                wsAnnotationString = "-";
            } else {
                wsAnnotationString = wsAnnotation.encodeToString().replace(",", ":");
            }
            String font = textBox.getFont();
            String slotPath = ((BComponent) textBox.getParent()).getSlotPathOrd().encodeToString();
            String text = textBox.getText().replaceAll("\n", ";");
            // PH: replace commas to avoid upsetting CSV format
            text = text.replaceAll(",", "COMMA");
            // PH: handle empty text
            if (text.trim().isEmpty()) {
                text = "-";
            }

            // append properties to CSV
            CSVToPrint.append(name).append(COMMA);         // 0
            CSVToPrint.append(foreground).append(COMMA);   // 1
            CSVToPrint.append(background).append(COMMA);   // 2
            CSVToPrint.append(border).append(COMMA);       // 3
            CSVToPrint.append(wsAnnotationString).append(COMMA); // 4
            CSVToPrint.append(font).append(COMMA);         // 5
            CSVToPrint.append(slotPath).append(COMMA);     // 6
            CSVToPrint.append(text).append("\n");          // 7
        }
        //write to CSV file
        BOrd fileORD = BOrd.make("file:^" + TEXT_BOX_FILE);
        FileUtils.deleteFileIfExists(TEXT_BOX_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(TEXT_BOX_FILE, CSVToPrint.toString(), false);
    }
    private void processNullProxyControlPoints(Context cx) throws IOException {
        List<BControlPoint> nullProxyControlArrayList = findNullProxyControlPoints(cx);
        logger.info("Null Proxy Control Points: " + nullProxyControlArrayList.size());
        StringBuilder CSVToPrint = new StringBuilder();
        //build headers
        CSVToPrint.append("PointName").append(COMMA);       // 0
        CSVToPrint.append("parentSlotPath").append(COMMA);   // 1
        CSVToPrint.append("Facets").append(COMMA);          // 2
        CSVToPrint.append("PointType").append(COMMA);       // 3
        CSVToPrint.append("Fallback").append(COMMA);       // 4
        CSVToPrint.append("wsAnnotation").append("\n");    // 5

        for (BControlPoint controlPoint : nullProxyControlArrayList) {
            String pointName = controlPoint.getName();
//            String pointSlotPath = controlPoint.getSlotPath().toString();
            String parentSlotPath = ((BComponent) controlPoint.getParent()).getSlotPathOrd().encodeToString();
            String pointFacets = controlPoint.getFacets().encodeToString();
            //replace , with  | in facets so as not to confuse the CSV format
            pointFacets = pointFacets.replace(",", "|");
            String pointType = controlPoint.getType().toString();

            String wsAnnotationString;
            BWsAnnotation wsAnnotation = (BWsAnnotation) controlPoint.get("wsAnnotation");
            if (wsAnnotation == null) {
                wsAnnotationString = "-";
            } else {
                wsAnnotationString = wsAnnotation.encodeToString().replace(",", ":");
            }


            String pointFallback;
            switch (pointType) {
                case "control:NumericWritable":
                    BNumericWritable numericWritable = (BNumericWritable) controlPoint;
                    pointFallback = String.valueOf(numericWritable.getFallback());
                    if (!isNullFallback(pointFallback)) {
                        pointFallback = String.valueOf(numericWritable.getFallback().getValue());
                    }
                    break;
                case "control:BooleanWritable":
                    BBooleanWritable booleanWritable = (BBooleanWritable) controlPoint;
                    pointFallback = String.valueOf(booleanWritable.getFallback());
                    if (!isNullFallback(pointFallback)) {
                        pointFallback = String.valueOf(booleanWritable.getFallback().getValue());
                    }
                    break;
                case "control:EnumWritable":
                    BEnumWritable enumWritable = (BEnumWritable) controlPoint;
                    pointFallback = String.valueOf(enumWritable.getFallback());
                    if (!isNullFallback(pointFallback)) {
                        pointFallback = String.valueOf(enumWritable.getFallback().getEnum().getOrdinal());
                    }
                    break;
                case "control:StringWritable":
                    BStringWritable stringWritable = (BStringWritable) controlPoint;
                    pointFallback = String.valueOf(stringWritable.getFallback());
                    if (!isNullFallback(pointFallback)) {
                        pointFallback = stringWritable.getFallback().getValue();
                    }
                    break;
                default:
                    pointFallback = "N/A";
                    break;
            }
            CSVToPrint.append(pointName).append(COMMA);         // 0
            CSVToPrint.append(parentSlotPath).append(COMMA);     // 1
            CSVToPrint.append(pointFacets).append(COMMA);       // 2
            CSVToPrint.append(pointType).append(COMMA);         // 3
            CSVToPrint.append(pointFallback).append(COMMA);     // 4
            CSVToPrint.append(wsAnnotationString).append("\n"); // 5
        }
        BOrd fileORD = BOrd.make("file:^" + NULL_PROXY_POINTS_FILE);
        FileUtils.deleteFileIfExists(NULL_PROXY_POINTS_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(NULL_PROXY_POINTS_FILE, CSVToPrint.toString(), false);
    }

    private Boolean isNullFallback(String fallbackString) {
        return fallbackString.equals("- {null}");
    }

    private List<BComponent> findKitControlPoints() {
        List<BComponent> kitControlPoints = new ArrayList<>();
        // create BQL query to find all text boxes
        String bqlQuery = "bql:select where type like 'kitControl*'";
        BITable<? extends BIObject> table =
                (BITable<? extends BIObject>) BOrd.make(bqlQuery).get(Sys.getStation());
        try (TableCursor<? extends BIObject> cursor = table.cursor()) {
            while (cursor.next()) {
                try {
                    BComponent kitControlPoint = (BComponent) cursor.get();

//                    BOrd pointSlotPath = (BOrd) cursor.get();
//                    BControlPoint point = (BControlPoint) pointSlotPath.resolve(Sys.getStation(), cx).getComponent();
                    kitControlPoints.add(kitControlPoint);
                } catch (Exception ignored) {}
            }
        }
        return kitControlPoints;
    }
    private List<BWsTextBlock> findTextBoxes(Context cx) {
        List<BWsTextBlock> textBoxesArrayList = new ArrayList<>();
        // create BQL query to find all text boxes
        String bqlQuery = "bql:select * from baja:WsTextBlock";
        BITable<? extends BIObject> table =
                (BITable<? extends BIObject>) BOrd.make(bqlQuery).get(Sys.getStation());
        try (TableCursor<? extends BIObject> cursor = table.cursor()) {
            while (cursor.next()) {
                try {
                    BWsTextBlock textBox = (BWsTextBlock) cursor.get();

//                    BOrd pointSlotPath = (BOrd) cursor.get();
//                    BControlPoint point = (BControlPoint) pointSlotPath.resolve(Sys.getStation(), cx).getComponent();
                    textBoxesArrayList.add(textBox);
                } catch (Exception ignored) {}
            }
        }
        return textBoxesArrayList;
    }
    private List<BControlPoint> findNullProxyControlPoints(Context cx) {
        List<BControlPoint> controlPointArrayList = new ArrayList<>();
        // create BQL query to find all control points with null proxy
//        String bqlQuery = "bql:select slotPath,displayName, type, fallback.value as 'Fallback', facets" +
//                "  from control:ControlPoint where proxyExt.type = control:NullProxyExt";
        String bqlQuery = "bql:select * from control:ControlPoint where proxyExt.type = control:NullProxyExt and type != nss:CertificateExpiryPoint";
        BITable<? extends BIObject> table =
                (BITable<? extends BIObject>) BOrd.make(bqlQuery).get(Sys.getStation());
        try (TableCursor<? extends BIObject> cursor = table.cursor()) {
            while (cursor.next()) {
                try {
                    BControlPoint point = (BControlPoint) cursor.get();

//                    logger.info("Point parent ord: " + ((BComponent) point.getParent()).getSlotPathOrd());
//                    BOrd pointSlotPath = (BOrd) cursor.get();
//                    BControlPoint point = (BControlPoint) pointSlotPath.resolve(Sys.getStation(), cx).getComponent();
                    controlPointArrayList.add(point);
                } catch (Exception e) {
//                    logger.info("Point not an BOrd");
                }
            }
        }
        return controlPointArrayList;
    }


    private void addNetworksToJson() throws IOException {
//        logger.info("Reading networks and devices");
        // get the driver container where all the networks will be
        SlotCursor<Property> driverContainerProperties = driverContainer.getProperties();
        // iterate over the properties of the driver container
        for ( Property property : driverContainerProperties ) {
            // if the property is of type BBacnetNetwork...
            if ( property.getType() == BBacnetNetwork.TYPE ) {
//                logger.info("Found Bacnet Network at: " + property.getName());
                addBACnetNetworkToJSON(property);
            }
            // if the property is of type BModbusAsyncNetwork...
            if ( property.getType() == BModbusAsyncNetwork.TYPE ) {
//                logger.info("Modbus Async Network at: " + property.getName());
                addModbusAsyncNetworkToJSON(property);
            }
            // if the property is of type BModbusTcpNetwork...
            if ( property.getType() == BModbusTcpNetwork.TYPE ) {
//                logger.info("Modbus TCP Network at: " + property.getName());
                addModbusTCPNetworkToJSON(property);
            }
        }
//        logger.info(jsonNetworksNode.toPrettyString());
    }

    private void printNetworksJsonFile() throws IOException {

        BOrd fileORD = BOrd.make("file:^" + NETWORKS_JSON_FILE);
        FileUtils.deleteFileIfExists(NETWORKS_JSON_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(NETWORKS_JSON_FILE, jsonNetworksNode.toPrettyString(), false);
    }

    /**
     * Print the networks CSV file.
     */
    private void printNetworksCsvFile() throws IOException
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
                        printNetworkDevicesCsvFile(devicesFileName, networkJsonNode.get("devices"));
                    }
                }
            }
        }

        BOrd fileORD = BOrd.make("file:^" + NETWORKS_CSV_FILE);
        FileUtils.deleteFileIfExists(NETWORKS_CSV_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(NETWORKS_CSV_FILE, csvToPrint.toString(), false);
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

    private void addModbusTCPNetworkToJSON(Property property) throws IOException {
        String modbusTCPNetworkName = property.getName();
        // resolve ord);
        BModbusTcpNetwork modbusTCPNetwork =  (BModbusTcpNetwork) this.driverContainer.get(modbusTCPNetworkName);
        // get the BacnetNetwork object from the property.
        ModbusTCPNetworkWrapper modbusTCPNetworkWrapper = new ModbusTCPNetworkWrapper(modbusTCPNetwork);
        ObjectNode  jsonModbusTCPNetworkNode = modbusTCPNetworkWrapper.getJsonNetworkNode();
        jsonNetworksNode.put(modbusTCPNetworkName, jsonModbusTCPNetworkNode);
    }

    private void addModbusAsyncNetworkToJSON(Property property) throws IOException {
        String modbusAsyncNetworkName = property.getName();
        // resolve ord
        BModbusAsyncNetwork modbusAsyncNetwork =  (BModbusAsyncNetwork) this.driverContainer.get(modbusAsyncNetworkName);
        // get the BacnetNetwork object from the property.
        ModbusAsyncNetworkWrapper modbusAsyncNetworkWrapper = new ModbusAsyncNetworkWrapper(modbusAsyncNetwork);
        ObjectNode  jsonModbusAsyncNetworkNode = modbusAsyncNetworkWrapper.getJsonNetworkNode();
        jsonNetworksNode.put(modbusAsyncNetworkName, jsonModbusAsyncNetworkNode);
    }

    private void addBACnetNetworkToJSON(Property property) throws IOException {
        String bacnetNetworkName = property.getName();
        // resolve ord
        BBacnetNetwork bacnetNetwork =  (BBacnetNetwork) this.driverContainer.get(bacnetNetworkName);
        // get the BacnetNetwork object from the property.
        BacnetNetworkWrapper bacnetNetworkWrapper = new BacnetNetworkWrapper(bacnetNetwork);
        ObjectNode  jsonModbusBacnetNetworkNode = bacnetNetworkWrapper.getJsonNetworkNode();
        jsonNetworksNode.put(bacnetNetworkName, jsonModbusBacnetNetworkNode);
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

    private static final Logger logger = Logger.getLogger("StationReader");
}
