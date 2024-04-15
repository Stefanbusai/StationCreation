package com.airedale.StationCreation.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.BControlPoint;
import javax.baja.control.BIWritablePoint;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.history.ext.BIntervalHistoryExt;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.Type;
import java.io.IOException;

public class PointWrapper {

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
    protected String alarmName = "-";
    protected String alarmClass = "-";
    protected String alarmHyperlink = "-";
    protected Boolean hasHistory = Boolean.FALSE;
    protected String historyName = "-";
    protected String historyInterval = "-";
    protected String historyCapacity = "-";



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
    }

    protected void determineFacets() throws IOException {
        // facets
        BFacets pointFacets = controlPoint.getFacets();
        this.facets = pointFacets.encodeToString();
    }

    protected void determineIfWritable() {
        if (type.is(BIWritablePoint.TYPE)){
            writable = Boolean.TRUE;
        }
    }

    protected void determineExtensions() {
        BComponent[] components = controlPoint.getChildComponents();

        for (BComponent component : components){

            if (component.getType().is(BAlarmSourceExt.TYPE)){
                hasAlarm = true;
                alarmName = ((BAlarmSourceExt) component).getSourceName().toString();
                alarmClass = String.valueOf(((BAlarmSourceExt) component).getAlarmClass());
                alarmHyperlink = ((BAlarmSourceExt) component).getHyperlinkOrd().encodeToString();
                break;
            }
            else if( component.getType().is(BHistoryExt.TYPE)){
                hasHistory = true;
                // TODO distinguish interval from COV
                historyName = ((BHistoryExt) component).getHistoryName().toString();
                historyInterval = String.valueOf(((BIntervalHistoryExt) component).getInterval().getMinutes());
                historyCapacity = String.valueOf(((BHistoryExt) component).getHistoryConfig().getCapacity().getMaxRecords());
                break;
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
                dataType = "-";
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
}
