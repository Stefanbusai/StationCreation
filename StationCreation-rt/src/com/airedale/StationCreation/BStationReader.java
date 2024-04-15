package com.airedale.StationCreation;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.links.LinkManager;
import com.airedale.StationCreation.wrappers.bacnet.BacnetNetworkWrapper;
import com.airedale.StationCreation.wrappers.modbus.ModbusAsyncNetworkWrapper;
import com.airedale.StationCreation.wrappers.modbus.ModbusTCPNetworkWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import javax.baja.sys.*;
import javax.baja.util.BWsAnnotation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@NiagaraType
@NiagaraAction(
        name = "read"
)
public class BStationReader extends BComponent
{
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
    private static final String NETWORKS_FILE = "StationRead/networks.json";
    private static final String NULL_PROXY_POINTS_FILE = "StationRead/nullProxyPoints.csv";
    private static final String LINKS_FILE = "StationRead/links.csv";
    private ObjectNode jsonNetworksNode = mapper.createObjectNode();


    private BDriverContainer driverContainer;

    public void doRead(Context cx) throws IOException {
        getDriverContainer(cx);
        addNetworksToJson();
        printJSONFile();
        processNullProxyControlPoints(cx);
        readLinks(cx);
    }


    private void readLinks(Context cx) {
        LinkManager linkManager = new LinkManager();
        List<BLink> links = linkManager.findAllLinks(cx);
        LinkManager.writeLinksToCSVFile(links, LINKS_FILE, cx);
    }

    private void processNullProxyControlPoints(Context cx) throws IOException {
        List<BControlPoint> nullProxyControlArrayList = findNullProxyControlPoints(cx);
        logger.info("Null Proxy Control Points: " + nullProxyControlArrayList.size());
        StringBuilder CSVToPrint = new StringBuilder();
        //build headers
        String COMMA = ",";
        CSVToPrint.append("PointName").append(COMMA);       // 0
        CSVToPrint.append("pointSlotPath").append(COMMA);   // 1
        CSVToPrint.append("Facets").append(COMMA);          // 2
        CSVToPrint.append("PointType").append(COMMA);       // 3
        CSVToPrint.append("Fallback").append(COMMA);       // 4
        CSVToPrint.append("wsAnnotation").append("\n");    // 5

        for (BControlPoint controlPoint : nullProxyControlArrayList) {
            String pointName = controlPoint.getName();
            String pointSlotPath = controlPoint.getSlotPath().toString();
            String pointFacets = controlPoint.getFacets().encodeToString();
            //replace , with  | in facets so as not to confuse the CSV format
            pointFacets = pointFacets.replace(",", "|");
            String pointType = controlPoint.getType().toString();

            String wsAnnotationString;
            BWsAnnotation  wsAnnotation = (BWsAnnotation) controlPoint.get("wsAnnotation");
            if (wsAnnotation == null){
                wsAnnotationString = "-";
            }else {
                wsAnnotationString = wsAnnotation.encodeToString().replace(",", ":");
            }



            String pointFallback;
            switch (pointType) {
                case "control:NumericWritable":
                    BNumericWritable numericWritable = (BNumericWritable) controlPoint;
                    pointFallback = String.valueOf(numericWritable.getFallback());
                    if (!isNullFallback(pointFallback)){
                        pointFallback = String.valueOf(numericWritable.getFallback().getValue());
                    }
                    break;
                case "control:BooleanWritable":
                    BBooleanWritable booleanWritable = (BBooleanWritable) controlPoint;
                    pointFallback = String.valueOf(booleanWritable.getFallback());
                    if (!isNullFallback(pointFallback)){
                        pointFallback = String.valueOf(booleanWritable.getFallback().getValue());
                    }
                    break;
                case "control:EnumWritable":
                    BEnumWritable enumWritable = (BEnumWritable) controlPoint;
                    pointFallback = String.valueOf(enumWritable.getFallback());
                    if (!isNullFallback(pointFallback)){
                        pointFallback = String.valueOf(enumWritable.getFallback().getEnum().getOrdinal());
                    }
                    break;
                case "control:StringWritable":
                    BStringWritable stringWritable = (BStringWritable) controlPoint;
                    pointFallback = String.valueOf(stringWritable.getFallback());
                    if (!isNullFallback(pointFallback)){
                        pointFallback = stringWritable.getFallback().getValue();
                    }
                    break;
                default:
                    pointFallback = "N/A";
                    break;
            }
            CSVToPrint.append(pointName).append(COMMA);         // 0
            CSVToPrint.append(pointSlotPath).append(COMMA);     // 1
            CSVToPrint.append(pointFacets).append(COMMA);       // 2
            CSVToPrint.append(pointType).append(COMMA);         // 3
            CSVToPrint.append(pointFallback).append(COMMA);     // 4
            CSVToPrint.append(wsAnnotationString).append("\n"); // 5
        }
        BOrd fileORD = BOrd.make("file:^" + NULL_PROXY_POINTS_FILE);
        FileUtils.deleteFileIfExists(NULL_PROXY_POINTS_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(NULL_PROXY_POINTS_FILE, CSVToPrint.toString(),false);
    }

    private Boolean isNullFallback(String fallbackString){
        return fallbackString.equals("- {null}");
    }



    private List<BControlPoint> findNullProxyControlPoints(Context cx) {
        List<BControlPoint> controlPointArrayList = new ArrayList<>();
        // create BQL query to find all control points with null proxy
//        String bqlQuery = "bql:select slotPath,displayName, type, fallback.value as 'Fallback', facets" +
//                "  from control:ControlPoint where proxyExt.type = control:NullProxyExt";
        String bqlQuery = "bql:select from control:ControlPoint where proxyExt.type = control:NullProxyExt";
        BITable<? extends BIObject> table =
                (BITable<? extends BIObject>) BOrd.make(bqlQuery).get(Sys.getStation(), cx);
        try (TableCursor<? extends BIObject> cursor = table.cursor())
        {
            while (cursor.next())
            {
                BControlPoint point = (BControlPoint) cursor.get();
                controlPointArrayList.add(point);
            }
        }
        return controlPointArrayList;
    }

    private void addNetworksToJson() throws IOException {
        // get the driver container where all the networks will be
        SlotCursor<Property> driverContainerProperties = driverContainer.getProperties();
        // iterate over the properties of the driver container
        for ( Property property : driverContainerProperties ) {
            // if the property is of type BBacnetNetwork...
            if ( property.getType() == BBacnetNetwork.TYPE ) {
                addBACnetNetworkToJSON(property);
            }
            // if the property is of type BModbusAsyncNetwork...
            if ( property.getType() == BModbusAsyncNetwork.TYPE ) {
                addModbusAsyncNetworkToJSON(property);
            }
            // if the property is of type BModbusTcpNetwork...
            if ( property.getType() == BModbusTcpNetwork.TYPE ) {
                addModbusTCPNetworkToJSON(property);
            }
        }
        logger.info(jsonNetworksNode.toPrettyString());
    }

    private void printJSONFile() throws IOException {

        BOrd fileORD = BOrd.make("file:^" + NETWORKS_FILE);
        FileUtils.deleteFileIfExists(NETWORKS_FILE);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(NETWORKS_FILE, jsonNetworksNode.toPrettyString(), false);
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
