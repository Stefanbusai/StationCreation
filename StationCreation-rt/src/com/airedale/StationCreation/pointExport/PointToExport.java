package com.airedale.StationCreation.pointExport;

import javax.baja.bacnet.datatypes.BBacnetObjectIdentifier;
import javax.baja.bacnet.export.*;
import javax.baja.control.*;
import javax.baja.naming.BOrd;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import java.util.logging.Logger;

public class PointToExport {
    protected String csvLine;
    protected BOrd sourceOrd;
    protected int address;
    protected boolean writable;
    protected boolean valid = true;

    //getters
    public boolean isValid() {
        return valid;
    }
    public BOrd getSourceOrd() {
        return sourceOrd;
    }
    public int getAddress() {
        return address;
    }
    public boolean isWritable() {
        return writable;
    }


    public PointToExport(String csvLine) {
        this.csvLine = csvLine;
        String[] pointDetails = csvLine.split(",");
        // check the line length
        if (pointDetails.length != 3){
            logger.warning("point to export details not correct length: " + pointDetails.length);
            valid = false;
            return;
        }
        // parse the source ORD
        try{
            sourceOrd = BOrd.make(pointDetails[0]);
        } catch (Exception e){
            logger.warning("point to export ORD not valid: " + csvLine);
            valid = false;
            return;
        }
        // parse the address
        try{
            address = Integer.parseInt(pointDetails[1]);
        } catch (Exception e){
            logger.warning("point to export address not valid: " + csvLine);
            valid = false;
            return;
        }
        // parse the writable boolean
        try{
            writable = Boolean.parseBoolean(pointDetails[2]);
        } catch (Exception e){
            logger.warning("point to export writable boolean not valid: " + csvLine);
            valid = false;
            return;
        }
    }

    private static final Logger logger = Logger.getLogger("PointToExport");

    public BBacnetPointDescriptor createPointDescriptor(String pointName, Context cx) {
        // resolve the point in the SourceORD
        BBacnetPointDescriptor bacnetPointDescriptor;
        BControlPoint sourcePoint = (BControlPoint) sourceOrd.resolve(Sys.getStation(), cx).getComponent();
        if (sourcePoint.getType().equals(BNumericPoint.TYPE) || sourcePoint.getType().equals(BNumericWritable.TYPE)){
            if(writable){
                bacnetPointDescriptor = new BBacnetAnalogOutputDescriptor();
                bacnetPointDescriptor.setObjectId(BBacnetObjectIdentifier.make(1,address)); // 1 is the object type for AnalogOutput
            }
            else{
                bacnetPointDescriptor = new BBacnetAnalogValueDescriptor();
                bacnetPointDescriptor.setObjectId(BBacnetObjectIdentifier.make(2,address)); // 2 is the object type for AnalogValue
            }
        } else if (sourcePoint.getType().equals(BBooleanPoint.TYPE) || sourcePoint.getType().equals(BBooleanWritable.TYPE)){
            if(writable){
                bacnetPointDescriptor = new BBacnetBinaryOutputDescriptor();
                bacnetPointDescriptor.setObjectId(BBacnetObjectIdentifier.make(4,address)); // 4 is the object type for BinaryOutput
            }
            else{
                bacnetPointDescriptor = new BBacnetBinaryValueDescriptor();
                bacnetPointDescriptor.setObjectId(BBacnetObjectIdentifier.make(5,address)); // 5 is the object type for BinaryValue
            }
        }
        else {
            logger.warning("point to export source point type not supported: " + sourcePoint.getType());
            valid = false;
            return null;
        }
        // set the source ORD
        bacnetPointDescriptor.setPointOrd(sourceOrd);

        bacnetPointDescriptor.setObjectName(pointName+"@"+address);
        return bacnetPointDescriptor;
    }
}
