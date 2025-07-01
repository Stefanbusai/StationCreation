package com.airedale.StationCreation.wrappers.bacnet;

import com.airedale.StationCreation.wrappers.PointWrapper;

import javax.baja.bacnet.point.BBacnetProxyExt;
import javax.baja.control.BControlPoint;
import javax.baja.naming.SlotPath;
import javax.baja.util.BWsAnnotation;
import java.io.IOException;
import java.util.logging.Logger;

public class BacnetPointWrapper extends PointWrapper {

    private BBacnetProxyExt proxyExt;

    public BacnetPointWrapper(BControlPoint controlPoint) throws IOException {
        super(controlPoint);
    }
    @Override
    protected void readProxyExt(BControlPoint controlPoint) {
        this.proxyExt = (BBacnetProxyExt) controlPoint.getProxyExt();
    }

    @Override
    protected void readPointProperties() throws IOException {
        this.pointName = SlotPath.unescape(controlPoint.getName());
        this.pointAddress = String.valueOf(proxyExt.getObjectId()).split(":")[1];
        this.pointType = String.valueOf(controlPoint.getType()).split(":")[1];
        this.type = controlPoint.getType();
        determineFacets();
        this.conversion = proxyExt.getConversion().toString();
        determineIfWritable();
        determineDataType();

        determinePointType();

        determineExtensions();
        determineWsAnnotation();
        determineSubFolder();

    }

    private static final Logger logger = Logger.getLogger("BacnetDeviceWrapper");



}
