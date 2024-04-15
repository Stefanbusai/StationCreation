package com.airedale.StationCreation.wrappers;

import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.wrappers.bacnet.BacnetPointWrapper;
import com.airedale.StationCreation.wrappers.modbus.ModbusPointWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusCore.point.BModbusProxyExt;

import javax.baja.bacnet.BBacnetDevice;
import javax.baja.bacnet.point.BBacnetProxyExt;
import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import java.io.IOException;
import java.util.logging.Logger;

public class DeviceWrapper 
{


    protected String deviceName;
    protected Integer pointsCount;
    protected BControlPoint[] points;
    protected String pointsListFile;
    protected ObjectMapper mapper = new ObjectMapper();
    protected ObjectNode jsonDeviceNode = mapper.createObjectNode();;
    protected ObjectNode jsonPointsNode = mapper.createObjectNode();

    protected String createPointsListCSV() throws IOException {
        logger.fine("createPointsListCSV");
        StringBuilder CSVToPrint = new StringBuilder();
        //build headers
        String COMMA = ",";
        CSVToPrint.append("PointName").append(COMMA);       // 0
        CSVToPrint.append("PointAddress").append(COMMA);    // 1
        CSVToPrint.append("PointType").append(COMMA);       // 2
        CSVToPrint.append("DataType").append(COMMA);        // 3
        CSVToPrint.append("Writable").append(COMMA);        // 4
        CSVToPrint.append("Type").append(COMMA);            // 5
        CSVToPrint.append("Facets").append(COMMA);          // 6
        CSVToPrint.append("Conversion").append(COMMA);      // 7
        CSVToPrint.append("HasAlarm").append(COMMA);        // 8
        CSVToPrint.append("AlarmName").append(COMMA);       // 9
        CSVToPrint.append("AlarmClass").append(COMMA);      // 10
        CSVToPrint.append("AlarmClass").append(COMMA);      // 11
        CSVToPrint.append("HasHistory").append(COMMA);      // 12
        CSVToPrint.append("HistoryName").append(COMMA);     // 13
        CSVToPrint.append("HistoryInterval").append(COMMA); // 14
        CSVToPrint.append("HistoryCapacity").append("\n");  // 15

        for (BControlPoint point : points) {
            PointWrapper pointWrapper;
            if (point.getProxyExt() instanceof BBacnetProxyExt) {
                pointWrapper = new BacnetPointWrapper(point);
            } else if (point.getProxyExt() instanceof BModbusProxyExt) {
                pointWrapper = new ModbusPointWrapper(point);
            } else {
                // Handle other types of BControlPoint if needed
                continue; // Skip this point and continue with the next one
            }


            CSVToPrint.append(pointWrapper.getPointName()).append(COMMA);
            CSVToPrint.append(pointWrapper.getPointAddress()).append(COMMA);
            CSVToPrint.append(pointWrapper.getPointType()).append(COMMA);
            CSVToPrint.append(pointWrapper.getDataType()).append(COMMA);
            CSVToPrint.append(pointWrapper.getWritable()).append(COMMA);
            CSVToPrint.append(pointWrapper.getType()).append(COMMA);
            CSVToPrint.append(pointWrapper.getFacets()).append(COMMA);
            CSVToPrint.append(pointWrapper.getConversion()).append(COMMA);
            CSVToPrint.append(pointWrapper.getHasAlarm()).append(COMMA);
            CSVToPrint.append(pointWrapper.getAlarmName()).append(COMMA);
            CSVToPrint.append(pointWrapper.getAlarmClass()).append(COMMA);
            CSVToPrint.append(pointWrapper.getAlarmHyperlink()).append(COMMA);
            CSVToPrint.append(pointWrapper.getHasHistory()).append(COMMA);
            CSVToPrint.append(pointWrapper.getHistoryName()).append(COMMA);
            CSVToPrint.append(pointWrapper.getHistoryInterval()).append(COMMA);
            CSVToPrint.append(pointWrapper.getHistoryCapacity()).append("\n");
        }
        return CSVToPrint.toString();
    }

    protected void addPointsToJSON() throws IOException {
        for (BControlPoint point : points) {
            PointWrapper pointWrapper = new PointWrapper(point);
            jsonPointsNode.set(point.getName(), pointWrapper.getJsonPointNode());
        }
        jsonDeviceNode.set("points", jsonPointsNode);
    }

    protected void printPointsListToCSV() throws IOException {
        String CSVtoPrint = createPointsListCSV();
        BOrd fileORD = BOrd.make("file:^" + pointsListFile);
        FileUtils.deleteFileIfExists(pointsListFile);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(pointsListFile, CSVtoPrint,false);
    }


    public String getDeviceName() {
        return deviceName;
    }
    public ObjectNode getJsonDeviceNode() {
        return jsonDeviceNode;
    }

    private static final Logger logger = Logger.getLogger("DeviceWrapper");


}
