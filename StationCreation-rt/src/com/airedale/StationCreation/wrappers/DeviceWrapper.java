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

    public String createPointsListCSV() throws IOException {
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
        CSVToPrint.append("HistoryCapacity").append(COMMA);  // 15
        CSVToPrint.append("HasPointExportTag").append(COMMA);  // 16
        CSVToPrint.append("PointExportTagSlotPath").append(COMMA);  // 17
        CSVToPrint.append("HasHistoryExportTag").append(COMMA);  // 18
        CSVToPrint.append("wsAnnotation").append(COMMA);        // 19
        CSVToPrint.append("subFolder").append("\n");            // 20

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


            CSVToPrint.append(pointWrapper.getPointName()).append(COMMA); //0
            CSVToPrint.append(pointWrapper.getPointAddress()).append(COMMA); //1
            CSVToPrint.append(pointWrapper.getPointType()).append(COMMA); //2
            CSVToPrint.append(pointWrapper.getDataType()).append(COMMA); //3
            CSVToPrint.append(pointWrapper.getWritable()).append(COMMA); //4
            CSVToPrint.append(pointWrapper.getType()).append(COMMA); //5
            CSVToPrint.append(pointWrapper.getFacets()).append(COMMA); //6
            CSVToPrint.append(pointWrapper.getConversion()).append(COMMA); //7
            CSVToPrint.append(pointWrapper.getHasAlarm()).append(COMMA); //8
            CSVToPrint.append(pointWrapper.getAlarmName()).append(COMMA); //9
            CSVToPrint.append(pointWrapper.getAlarmClass()).append(COMMA); //10
            CSVToPrint.append(pointWrapper.getAlarmHyperlink()).append(COMMA); //11
            CSVToPrint.append(pointWrapper.getHasHistory()).append(COMMA); //12
            CSVToPrint.append(pointWrapper.getHistoryName()).append(COMMA); //13
            CSVToPrint.append(pointWrapper.getHistoryInterval()).append(COMMA); //14
            CSVToPrint.append(pointWrapper.getHistoryCapacity()).append(COMMA); //15
            CSVToPrint.append(pointWrapper.getHasPointExportTag()).append(COMMA); //16
            CSVToPrint.append(pointWrapper.getPointExportTagSlotPath()).append(COMMA); //17
            CSVToPrint.append(pointWrapper.getHasHistoryExportTag()).append(COMMA); //18
            CSVToPrint.append(pointWrapper.getWsAnnotation()).append(COMMA); //19
            CSVToPrint.append(pointWrapper.getSubFolder()).append("\n"); //20
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

    public void printPointsListToCSV() throws IOException {
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

    public String getPointsListFile()
    {
        return pointsListFile;
    }

    public void setPointsListFile(String pointsListFile)
    {
        this.pointsListFile = pointsListFile;
    }

    private static final Logger logger = Logger.getLogger("DeviceWrapper");


}
