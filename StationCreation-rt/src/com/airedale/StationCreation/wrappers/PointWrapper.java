package com.airedale.StationCreation.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.exporttags.tags.BHistoryImportTag;
import com.tridium.exporttags.tags.BPointTag;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.BControlPoint;
import javax.baja.control.BIWritablePoint;
import javax.baja.history.ext.BCovHistoryExt;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.history.ext.BIntervalHistoryExt;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BWsAnnotation;
import java.io.IOException;

public class PointWrapper {

    public static final String EMPTY_VALUE = "-";
    protected BControlPoint controlPoint;

    protected String pointName;
    protected String pointAddress;
    protected String pointType;
    protected Type type;
    protected String dataType = "";
    protected Boolean writable = Boolean.FALSE;
    protected String facets;
    protected String conversion;
    protected Boolean hasAlarm = Boolean.FALSE;
    protected String alarmName = EMPTY_VALUE;
    protected String alarmClass = EMPTY_VALUE;
    protected String alarmHyperlink = EMPTY_VALUE;
    protected Boolean hasHistory = Boolean.FALSE;
    protected String historyName = EMPTY_VALUE;
    protected String historyInterval = EMPTY_VALUE;
    protected String historyCapacity = EMPTY_VALUE;
    protected Boolean hasPointExportTag = Boolean.FALSE;
    protected String exportTagSupervisor = EMPTY_VALUE;
    protected String pointExportTagSlotPath = EMPTY_VALUE;
    protected Boolean hasHistoryExportTag = Boolean.FALSE;
    protected String wsAnnotation = EMPTY_VALUE;



    protected String subFolder = EMPTY_VALUE;

    public String getWsAnnotation() {
        return wsAnnotation;
    }
    public String getSubFolder() {
        return subFolder;
    }





    protected final ObjectMapper mapper = new ObjectMapper();
    protected ObjectNode jsonPointNode = mapper.createObjectNode();

    public PointWrapper(BControlPoint controlPoint) throws IOException {
        this.controlPoint = controlPoint;
        readProxyExt(controlPoint);
        readPointProperties();
        buildJSONNode();
    }


    protected void readProxyExt(BControlPoint controlPoint){

    }

    protected void readPointProperties() throws IOException{

    }

    protected void buildJSONNode() {
        // add details to json node
        jsonPointNode.put("pointName", pointName);                  //0
        jsonPointNode.put("pointAddress", pointAddress);            //1
        jsonPointNode.put("pointType", pointType);                  //2
        jsonPointNode.put("dataType", dataType);                    //3
        jsonPointNode.put("writable", writable);                    //4
        jsonPointNode.put("type", type.toString());                 //5
        jsonPointNode.put("facets", facets);                        //6
        jsonPointNode.put("conversion", conversion);                //7
        jsonPointNode.put("hasAlarm", hasAlarm);                    //8
        jsonPointNode.put("alarmName", alarmName);                  //9
        jsonPointNode.put("alarmClass", alarmClass);                //10
        jsonPointNode.put("alarmHyperlink", alarmHyperlink);        //11
        jsonPointNode.put("hasHistory", hasHistory);                //12
        jsonPointNode.put("historyName", historyName);              //13
        jsonPointNode.put("historyInterval", historyInterval);      //14
        jsonPointNode.put("historyCapacity", historyCapacity);      //15
        jsonPointNode.put("hasPointExportTag", hasPointExportTag);  //16
        jsonPointNode.put("pointExportTagSlotPath", pointExportTagSlotPath);  //17
        jsonPointNode.put("hasHistoryExportTag", hasHistoryExportTag);  //18
        jsonPointNode.put("wsAnnotation", wsAnnotation);                //19
        jsonPointNode.put("subFolder", wsAnnotation);                   //20
    }

    protected void determineSubFolder(){
        String pointSlotPath = controlPoint.getSlotPathOrd().encodeToString();
        String folderName = extractFolder(pointSlotPath);

        this.subFolder = folderName;

    }
    private static String extractFolder(String input) {
        String keyword = "points/";
        int pointsIndex = input.indexOf(keyword);

        if (pointsIndex == -1) {
            return EMPTY_VALUE;
        }

        // Get substring starting after "points/"
        String pathAfterPoints = input.substring(pointsIndex + keyword.length());

        // Split remaining path into segments
        String[] segments = pathAfterPoints.split("/");

        // Ensure there are at least two segments after "points"
        if (segments.length < 2) {
            return EMPTY_VALUE;
        }

        // Join all but the last segment with '/'
        StringBuilder folderPath = new StringBuilder();
        for (int i = 0; i < segments.length - 1; i++) {
            folderPath.append(segments[i]).append("/");
        }
        return folderPath.toString();
    }
    protected void determineFacets() throws IOException {
        // facets
        BFacets pointFacets = controlPoint.getFacets();
        this.facets = pointFacets.encodeToString().replace(",", "|"); // PH: replace commas to avoid confusing CSV format
    }

    protected void determineWsAnnotation() throws IOException {
        BWsAnnotation wsAnnotation =  (BWsAnnotation) controlPoint.get("wsAnnotation");
        if (wsAnnotation == null) {
            this.wsAnnotation = EMPTY_VALUE;
        } else {
            this.wsAnnotation = wsAnnotation.encodeToString().replace(",", ":");
        }
    }

    protected void determineIfWritable() {
        if (type.is(BIWritablePoint.TYPE)){
            writable = Boolean.TRUE;
        }
    }

    protected void determineExtensions() {
        BComponent[] components = controlPoint.getChildComponents();

        for (BComponent component : components){
            // search for alarm extensions
            if (component.getType().is(BAlarmSourceExt.TYPE)){
                hasAlarm = true;
                alarmName = ((BAlarmSourceExt) component).getSourceName().toString();
                alarmClass = String.valueOf(((BAlarmSourceExt) component).getAlarmClass());
                alarmHyperlink = ((BAlarmSourceExt) component).getHyperlinkOrd().encodeToString();
            }
            // search for history extensions
            else if( component.getType().is(BHistoryExt.TYPE)){
                hasHistory = true;
                historyName = ((BHistoryExt) component).getHistoryName().toString();
                if (component.getType().is(BIntervalHistoryExt.TYPE)) {
                    historyInterval = String.valueOf(((BIntervalHistoryExt) component).getInterval().getMinutes());
                } else if (component.getType().is(BCovHistoryExt.TYPE)) {
                    historyInterval = "cov";
                }
                historyCapacity = String.valueOf(((BHistoryExt) component).getHistoryConfig().getCapacity().getMaxRecords());
                //search for history export tags
                BComponent[] historyExtChildren = component.getChildComponents();
                for (BComponent historyExtChild : historyExtChildren){
                    if (historyExtChild.getType().is(BHistoryImportTag.TYPE)){
                        hasHistoryExportTag = true;
                    }
                }
            }
            // search for point tag extensions
            else if (component.getType().is(BPointTag.TYPE)){
                hasPointExportTag = true;
                pointExportTagSlotPath = ((BPointTag)component).getStationSlotPath().toString();
            }
        }
    }

    protected void determinePointType() {
        if  (pointType.contains("Numeric")) {
            pointType = "numeric";
        } else if (pointType.contains("Boolean")) {
            pointType = "boolean";
        }
    }

    protected void determineDataType() {
        switch (pointType){
            case "NumericPoint":
            case "NumericWritable":
                dataType = "float";
                break;
            case "BooleanPoint":
            case "BooleanWritable":
                dataType = "boolean";
                break;
            case "StringPoint":
            case "StringWritable":
                dataType = "string";
                break;
            case "EnumPoint":
            case "EnumWritable":
                dataType = "enum";
                break;
            default:
                dataType = EMPTY_VALUE;
                break;
        }
    }

    // Getters for common properties
    public ObjectNode getJsonPointNode() {
        return jsonPointNode;
    }

    public String getJSONString(){
        return jsonPointNode.toPrettyString();
    }

    public String getPointName() {
        return pointName;
    }

    public String getPointAddress() {
        return pointAddress;
    }

    public String getPointType() {
        return pointType;
    }

    public Type getType() {
        return type;
    }

    public String getDataType() {
        return dataType;
    }

    public Boolean getWritable() {
        return writable;
    }

    public String getFacets() {
        return facets;
    }

    public String getConversion() {
        return conversion;
    }

    public Boolean getHasAlarm() {
        return hasAlarm;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getAlarmClass() {
        return alarmClass;
    }

    public Boolean getHasHistory() {
        return hasHistory;
    }

    public String getHistoryName() {
        return historyName;
    }

    public String getHistoryInterval() {
        return historyInterval;
    }

    public String getHistoryCapacity() {
        return historyCapacity;
    }

    public String getAlarmHyperlink() {
        return alarmHyperlink;
    }
    public Boolean getHasPointExportTag() {return hasPointExportTag;}

    public String getExportTagSupervisor() {return exportTagSupervisor;}

    public String getPointExportTagSlotPath() {return pointExportTagSlotPath;}

    public Boolean getHasHistoryExportTag() {return hasHistoryExportTag;}
}
