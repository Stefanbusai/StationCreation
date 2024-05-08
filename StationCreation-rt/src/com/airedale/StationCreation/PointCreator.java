package com.airedale.StationCreation;

import com.tridium.exporttags.tags.BHistoryImportTag;
import com.tridium.exporttags.tags.BPointTag;
import com.tridium.modbusCore.client.point.BModbusClientBooleanProxyExt;
import com.tridium.modbusCore.client.point.BModbusClientNumericProxyExt;
import com.tridium.modbusCore.client.point.BModbusClientProxyExt;
import com.tridium.modbusCore.datatypes.BFlexAddress;
import com.tridium.modbusCore.enums.BAddressFormatEnum;
import com.tridium.modbusCore.enums.BDataTypeEnum;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.alarm.ext.fault.BOutOfRangeFaultAlgorithm;
import javax.baja.alarm.ext.offnormal.BBooleanChangeOfStateAlgorithm;
import javax.baja.alarm.ext.offnormal.BOutOfRangeAlgorithm;
import javax.baja.bacnet.datatypes.BBacnetObjectIdentifier;
import javax.baja.bacnet.enums.BBacnetObjectType;
import javax.baja.bacnet.point.BBacnetBooleanProxyExt;
import javax.baja.bacnet.point.BBacnetNumericProxyExt;
import javax.baja.bacnet.point.BBacnetProxyExt;
import javax.baja.control.*;
import javax.baja.history.BCapacity;
import javax.baja.history.BHistoryConfig;
import javax.baja.history.ext.BBooleanCovHistoryExt;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.history.ext.BNumericIntervalHistoryExt;
import javax.baja.naming.BOrd;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.tag.Tag;
import javax.baja.tag.Tags;
import javax.baja.units.BUnit;
import javax.baja.util.BFormat;
import javax.baja.util.BWsAnnotation;
import java.util.logging.Logger;

class PointCreator
{

    /**
     * Constructor.
     */
    PointCreator()
    {

    }
    private static final Logger logger = Logger.getLogger("PointCreator");

    /**
     * Create a new point.
     */
    String getPointNameFromCSVLine ( String pointCsvLine){
        // the points details are comma separated, built in createPointsListCSV()
        String[] pointDetails = pointCsvLine.split(",");
        String pointName              = pointDetails[0];
        return pointName;
    }

    public void addNullProxyControlPointFromCSVLine(String pointCsvLine, Context cx){
        String[] pointDetails = pointCsvLine.split(",");
        if (pointDetails.length != 6){
            logger.warning("Point details not correct length: " + pointDetails.length);
            return;
        }
        String pointName              = pointDetails[0];
        String pointSlotPath          = pointDetails[1];
        String pointFacetsString      = pointDetails[2];
        String pointTypeString        = pointDetails[3];
        String pointFallbackString    = pointDetails[4];
        String wsAnnotationString     = pointDetails[5];


        switch (pointTypeString){
            case "control:NumericWritable":
                if (!pointExists(pointSlotPath, cx)) {
                    addNumericWritablePoint(pointName, pointSlotPath, pointFacetsString, pointFallbackString,wsAnnotationString, cx);
                }
                break;
            case "control:BooleanWritable":
                if (!pointExists(pointSlotPath, cx)) {
                    addBooleanWritablePoint(pointName, pointSlotPath, pointFacetsString, pointFallbackString,wsAnnotationString, cx);
                }
                break;
            case "control:StringWritable":
                if (!pointExists(pointSlotPath, cx)) {
                    addStringWritablePoint(pointName, pointSlotPath, pointFacetsString, pointFallbackString,wsAnnotationString, cx);
                }
                break;
            case "control:EnumWritable":
                if (!pointExists(pointSlotPath, cx)) {
                    addEnumWritablePoint(pointName, pointSlotPath, pointFacetsString, pointFallbackString,wsAnnotationString, cx);
                }
                break;
        }
    }

    private void addEnumWritablePoint(String pointName, String pointSlotPath, String pointFacetsString, String pointFallbackString,String wsAnnotationString,  Context cx) {
        BEnumWritable enumWritablePoint = new BEnumWritable();
        addFacetsToPoint(enumWritablePoint, false, false, pointFacetsString);
        if (!isNullFallback(pointFallbackString)){
            Integer fallbackOrdinalInt  = Integer.parseInt(pointFallbackString);
            BDynamicEnum enumFallback = BDynamicEnum.make(fallbackOrdinalInt);
            BStatusEnum fallbackValue = new BStatusEnum(enumFallback, BStatus.ok);
            enumWritablePoint.setFallback(fallbackValue);
        }
        enumWritablePoint = (BEnumWritable) addWsAnnotationToControlPoint(wsAnnotationString, enumWritablePoint);
        addPoint(enumWritablePoint, pointName, pointSlotPath, cx );
    }

    private void addStringWritablePoint(String pointName, String pointSlotPath, String pointFacetsString, String pointFallbackString,String wsAnnotationString , Context cx) {
        BStringWritable stringWritablePoint = new BStringWritable();
        if (!isNullFallback(pointFallbackString)) {
            BStatusString fallbackValue = new BStatusString(pointFallbackString, BStatus.ok);
            stringWritablePoint.setFallback(fallbackValue);
        }
        stringWritablePoint = (BStringWritable) addWsAnnotationToControlPoint(wsAnnotationString, stringWritablePoint);
        addPoint(stringWritablePoint, pointName, pointSlotPath, cx);
    }
    private void addBooleanWritablePoint(String pointName, String pointSlotPath, String pointFacetsString, String pointFallbackString,String wsAnnotationString, Context cx) {
        BBooleanWritable booleanWritablePoint = new BBooleanWritable();
        addFacetsToPoint(booleanWritablePoint, false, true, pointFacetsString);
        if (!isNullFallback(pointFallbackString)) {
            Boolean fallbackBoolean = Boolean.parseBoolean(pointFallbackString);
            BStatusBoolean fallbackValue = new BStatusBoolean(fallbackBoolean, BStatus.ok);
            booleanWritablePoint.setFallback(fallbackValue);
        }
        booleanWritablePoint = (BBooleanWritable) addWsAnnotationToControlPoint(wsAnnotationString, booleanWritablePoint);
        addPoint(booleanWritablePoint, pointName, pointSlotPath, cx);
    }

    private void addNumericWritablePoint(String pointName, String pointSlotPath, String pointFacetsString, String pointFallbackString, String wsAnnotationString, Context cx) {
        BNumericWritable numericWritablePoint = new BNumericWritable();
        addFacetsToPoint(numericWritablePoint, true, false, pointFacetsString);
        if (!isNullFallback(pointFallbackString)){
            Double fallbackDouble  = Double.parseDouble(pointFallbackString);
            BStatusNumeric fallbackValue = new BStatusNumeric(fallbackDouble, BStatus.ok);
            numericWritablePoint.setFallback(fallbackValue);
        }

        numericWritablePoint = (BNumericWritable) addWsAnnotationToControlPoint(wsAnnotationString, numericWritablePoint);
        addPoint(numericWritablePoint, pointName, pointSlotPath, cx );
    }

    private BControlPoint addWsAnnotationToControlPoint(String wsAnnotationString, BControlPoint currentPoint) {
        String[] wsAnnotationStringArray = wsAnnotationString.split(":");
        if (wsAnnotationStringArray.length == 3){
            int wsAnnotation_p = Integer.parseInt(wsAnnotationStringArray[0]);
            int wsAnnotation_q = Integer.parseInt(wsAnnotationStringArray[1]);
            int wsAnnotation_w = Integer.parseInt(wsAnnotationStringArray[2]);
            currentPoint.add("wsAnnotation", BWsAnnotation.make(wsAnnotation_p,wsAnnotation_q,wsAnnotation_w));
        }
        return currentPoint;
    }

    private static void addPoint(BControlPoint booleanWritablePoint, String pointName, String pointSlotPath, Context cx) {
        BOrd pointOrd = BOrd.make(pointSlotPath);
        BOrd parentOrd = pointOrd.getParent();
        BComponent parent = (BComponent) parentOrd.get(Sys.getStation(), cx);
        parent.add(pointName, booleanWritablePoint);
    }
    
    private boolean pointExists(String pointSlotPath, Context cx) {
        try{
            BOrd pointOrd = BOrd.make(pointSlotPath);
            BControlPoint controlPoint = (BControlPoint) pointOrd.get(Sys.getStation(), cx);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private Boolean isNullFallback(String fallbackString){
        return fallbackString.equals("- {null}");
    }
    public BControlPoint createProxyPointFromCSVLine(String pointCsvLine, String modbusOrBacnet){
        // the points details are comma separated, built in createPointsListCSV()
        String[] pointDetails = pointCsvLine.split(",");
        String pointName              = pointDetails[0];
        String pointAddresString      = pointDetails[1];
        String pointTypeString        = pointDetails[2];
        String dataTypeString         = pointDetails[3];
        String writableString         = pointDetails[4];
        String typeString             = pointDetails[5];
        String facetsString           = pointDetails[6];
        String conversionString       = pointDetails[7];
        String hasAlarmString         = pointDetails[8];
        String alarmNameString        = pointDetails[9];
        String alarmClassString       = pointDetails[10];
        String alarmHyperlinkORD      = pointDetails[11];
        String hasHistoryString       = pointDetails[12];
        String historyNameString      = pointDetails[13];
        String historyIntervalString  = pointDetails[14];
        String historyCapacityString  = pointDetails[15];
        boolean isNumeric   = pointTypeString.equalsIgnoreCase("numeric");
        boolean isBoolean   = pointTypeString.equalsIgnoreCase("boolean");
        boolean isWritable  = writableString.equalsIgnoreCase("true");
        boolean hasAlarm    = hasAlarmString.equalsIgnoreCase("true");
        boolean hasHistory  = hasHistoryString.equalsIgnoreCase("true");
        int historyIntervalMinutes = historyIntervalString.equals("-") ? 0 : Integer.parseInt(historyIntervalString);
        int historyCapacity = historyCapacityString.equals("-") ? 500 : Integer.parseInt(historyCapacityString);

        BControlPoint point;

        if (!isNumeric && !isBoolean) {
            return null;
        }

        if (isNumeric) {
            point = isWritable ? new BNumericWritable() : new BNumericPoint();
        } else {
            point = isWritable ? new BBooleanWritable() : new BBooleanPoint();
        }

        boolean isBacnet = modbusOrBacnet.equalsIgnoreCase("bacnet");
        boolean isModbus = modbusOrBacnet.equalsIgnoreCase("modbus");



        addFacetsToPoint(point, isNumeric, isBoolean, facetsString);

        addProxyExtentionToPoint(point, isBacnet, isModbus, pointAddresString, isNumeric, isBoolean, dataTypeString);
        if (hasHistory) {
            addHistoryExtensionToPoint(point, isNumeric, isBoolean, historyIntervalMinutes, historyNameString, historyCapacity);
        }
        if (hasAlarm) {
            addAlarmExtensionToPoint(point, isNumeric, isBoolean, alarmNameString, alarmClassString, alarmHyperlinkORD);
        }


        return point;
    }

    /**
     * Add facets to the specified point.
     */
    private void addFacetsToPoint(BControlPoint point,
                                  boolean isNumeric,
                                  boolean isBoolean,
                                  String facetsStr)
    {
        if (facetsStr.equals("-"))
        {
            return;
        }

        String[] facetsStrParts = facetsStr.split("\\|");

        BFacets facets = null;

        if (isNumeric)
        {
            // facet string would look like units=u:percent;%;;;|precision=i:1|min=d:-inf|max=d:+inf
            String unitName = facetsStrParts[0].split(";")[0].split(":")[1];

            int precision = Integer.parseInt(facetsStrParts[1].split(":")[1]);
            String minString = facetsStrParts[2].split(":")[1];
            String maxString = facetsStrParts[3].split(":")[1];

            BDouble min = minString.equals("-inf") ? BDouble.NEGATIVE_INFINITY : BDouble.make(Double.parseDouble(minString));
            BDouble max = maxString.equals("+inf") ? BDouble.POSITIVE_INFINITY : BDouble.make(Double.parseDouble(maxString));

            BUnit unit = BUnit.getUnit(unitName);
            facets = BFacets.makeNumeric(unit, precision,min.getDouble(), max.getDouble());
        }
        else if (isBoolean)
        {
            String trueText = facetsStrParts[0].split(":")[1];
            String falseText = facetsStrParts[1].split(":")[1];

            facets = BFacets.makeBoolean(trueText, falseText);
        }
        else if (facetsStr.contains("range")){
            if (!facetsStr.equals("range=E:{}")){
                // Extracting ordinals and tags from facetStr
                String[] parts = facetsStr.split(":");

                String[] values = facetsStr.split(":")[1].substring(1, parts[1].length() - 1).split("\\|");
                int[] ordinals = new int[values.length];
                String[] tags = new String[values.length];

                for (int i = 0; i < values.length; i++) {
                    String[] pair = values[i].split("=");
                    tags[i] = pair[0];
                    ordinals[i] = Integer.parseInt(pair[1]);
                }
                // Creating BEnumRange
                BEnumRange range = BEnumRange.make(ordinals, tags);
                facets = BFacets.makeEnum(range);
            }
        }

        if (facets != null)
        {
            point.setFacets(facets);
        }
    }

    /**
     * Add a proxy extension to the specified point.
     */
    private void addProxyExtentionToPoint(BControlPoint point,
                                           boolean isBacnet,
                                           boolean isModbus,
                                           String pointAddress,
                                           boolean isNumeric,
                                           boolean isBoolean,
                                           String dataType)
    {
        if (isBacnet)
        {
            addBacnetProxyExtensionToPoint(point, pointAddress, isNumeric, isBoolean);
        }
        else if (isModbus)
        {
            addModbusProxyExtensionToPoint(point, pointAddress, isNumeric, isBoolean, dataType);
        }
    }

    /**
     * Add a Bacnet proxy extension to the specified point.
     */
    private void addBacnetProxyExtensionToPoint(BControlPoint point, String pointAddress, boolean isNumeric, boolean isBoolean)
    {
        BBacnetProxyExt proxyExt = null;

        int objectType = -1;
        if (isNumeric)
        {
            proxyExt = new BBacnetNumericProxyExt();
            objectType = BBacnetObjectType.ANALOG_VALUE;
        }
        else if(isBoolean)
        {
            proxyExt = new BBacnetBooleanProxyExt();
            objectType = BBacnetObjectType.BINARY_VALUE;
        }

        if (proxyExt != null)
        {
            int objectId = Integer.parseInt(pointAddress);
            int instanceNumber = objectId & 4194303;

            BBacnetObjectIdentifier oid = BBacnetObjectIdentifier.make(objectType, instanceNumber);

            proxyExt.setObjectId(oid);

            point.setProxyExt(proxyExt);
        }
    }

    //

    /**
     * Add a Modbus proxy extension to the specified point.
     */
    private void addModbusProxyExtensionToPoint(BControlPoint point, String pointAddress, boolean isNumeric, boolean isBoolean, String dataType)
    {
        BModbusClientProxyExt proxyExt = null;

        if (isNumeric)
        {
            proxyExt = new BModbusClientNumericProxyExt();

            BModbusClientNumericProxyExt numericProxyExt = (BModbusClientNumericProxyExt) proxyExt;

            boolean isIntegerType = dataType.equalsIgnoreCase("integer");
            boolean isLongType = dataType.equalsIgnoreCase("long");
            boolean isFloatType = dataType.equalsIgnoreCase("float");
            boolean isSignedIntegerType = dataType.equalsIgnoreCase("signedinteger");
            boolean isUnsignedLongType = dataType.equalsIgnoreCase("unsignedlong");

            if (isIntegerType)
            {
                numericProxyExt.setDataType(BDataTypeEnum.integerType);
            }
            else if (isLongType)
            {
                numericProxyExt.setDataType(BDataTypeEnum.longType);
            }
            else if (isFloatType)
            {
                numericProxyExt.setDataType(BDataTypeEnum.floatType);
            }
            else if (isSignedIntegerType)
            {
                numericProxyExt.setDataType(BDataTypeEnum.signedInteger);
            }
            else if (isUnsignedLongType)
            {
                numericProxyExt.setDataType(BDataTypeEnum.unsignedLong);
            }
        }
        else if (isBoolean)
        {
            proxyExt = new BModbusClientBooleanProxyExt();
        }

        if (proxyExt != null)
        {
            BFlexAddress flexAddress = new BFlexAddress();
            flexAddress.setAddress(pointAddress);

            if (pointAddress.contains("x"))
            {
                flexAddress.setAddressFormat(BAddressFormatEnum.hex);
            }
            else if (pointAddress.length() == 5)
            {
                flexAddress.setAddressFormat(BAddressFormatEnum.modbus);
            }
            else
            {
                flexAddress.setAddressFormat(BAddressFormatEnum.decimal);
            }

            proxyExt.setDataAddress(flexAddress);

            point.setProxyExt(proxyExt);
        }
    }

    /**
     * Add an alarm extension to the specified point.
     */
    void addAlarmExtensionToPoint(BControlPoint point, boolean isNumeric, boolean isBoolean, String alarmSourceName, String alarmClass, String alarmHyperlinkORDString)
    {
        BAlarmSourceExt alarmSourceExt = new BAlarmSourceExt();
        String alarmExtensionName = null;

        if (isBoolean)
        {
            alarmSourceExt.setOffnormalAlgorithm(new BBooleanChangeOfStateAlgorithm());
            alarmExtensionName = "BooleanChangeOfStateAlarmExt";
        }
        else if (isNumeric)
        {
            alarmSourceExt.setFaultAlgorithm(new BOutOfRangeFaultAlgorithm());
            alarmSourceExt.setOffnormalAlgorithm(new BOutOfRangeAlgorithm());

            alarmExtensionName = "OutOfRangeAlarmExt";
        }

        if (alarmExtensionName != null)
        {
            if (!alarmSourceName.equals("-"))
            {
                alarmSourceExt.setSourceName(BFormat.make(alarmSourceName));
            }
            if (!alarmHyperlinkORDString.equals("-"))
            {
                BOrd alarmHyperlinkORD = BOrd.make(alarmHyperlinkORDString);
                alarmSourceExt.setHyperlinkOrd(alarmHyperlinkORD);
            }
            alarmSourceExt.setAlarmClass(alarmClass);
            point.add(alarmExtensionName, alarmSourceExt);
        }
    }

    /**
     * Add a history extension to the specified point.
     */
    void addHistoryExtensionToPoint(BControlPoint point, boolean isNumeric, boolean isBoolean, int historyIntervalMinutes, String historySourceName, int historyCapacity)
    {
        BHistoryExt historyExt = null;
        String historyExtensionName = null;

        if (isBoolean)
        {
            historyExt = new BBooleanCovHistoryExt();

            historyExtensionName = "BooleanCov";
        }
        else if (isNumeric)
        {
            historyExt = new BNumericIntervalHistoryExt();

            if (historyIntervalMinutes > 0)
            {
                BNumericIntervalHistoryExt numericIntervalHistoryExt = (BNumericIntervalHistoryExt) historyExt;
                numericIntervalHistoryExt.setInterval(BRelTime.makeMinutes(historyIntervalMinutes));
                BHistoryConfig historyConfig = new BHistoryConfig();
                BCapacity capacity = BCapacity.makeByRecordCount(historyCapacity);
                historyConfig.setCapacity(capacity);
                numericIntervalHistoryExt.setHistoryConfig(historyConfig);
            }

            historyExtensionName = "NumericInterval_" + historyIntervalMinutes + "m_capacity_" + historyCapacity;
        }

        if (historyExt != null)
        {
            if (!historySourceName.equals("-"))
            {
                historyExt.setHistoryName(BFormat.make(historySourceName));
            }
            addHistoryExportTag(historyExt);
            historyExt.setEnabled(true);
            point.add(historyExtensionName, historyExt);
        }
    }


    void addStringStatusSlot(BControlPoint point, String slotName, String slotValue, boolean summaryFlag){
        BStatusString statusString  = new BStatusString( slotValue, BStatus.ok );
        if (summaryFlag){
            point.add(slotName, statusString, Flags.SUMMARY);
        }
        else{
            point.add(slotName, statusString);
        }
    }
    void addStringSlot(BControlPoint point, String slotName, String slotValue, boolean summaryFlag){
        BString stringSlot  = BString.make(slotValue);
        if (summaryFlag){
            point.add(slotName, stringSlot, Flags.SUMMARY);
        }
        else{
            point.add(slotName, stringSlot);
        }
    }

    void addMarkerTag(BControlPoint point,String markerName){
//        point.add(markerName, BMarker.MARKER ,Flags.METADATA);
        Tags tags = point.tags();
        Tag newTag = new Tag(markerName, BMarker.MARKER);
        tags.set(newTag);
    }

    void addStringTag(BControlPoint point, String tagName, String tagValue){
        point.add(tagName, BString.make(tagValue), Flags.METADATA);
    }

    void addExportPointTag ( BControlPoint point){
        BPointTag pointTag = new BPointTag();
        point.add("pointTag", pointTag);
    }

    void addHistoryExportTag ( BHistoryExt historyExt){
        BHistoryImportTag historyImportTag = new BHistoryImportTag();
        historyExt.add("historyImportTag", historyImportTag);

    }



}
