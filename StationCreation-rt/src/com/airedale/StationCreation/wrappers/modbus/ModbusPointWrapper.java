package com.airedale.StationCreation.wrappers.modbus;

import com.airedale.StationCreation.wrappers.PointWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tridium.modbusCore.point.BModbusProxyExt;

import javax.baja.alarm.ext.BAlarmSourceExt;
import javax.baja.control.BControlPoint;
import javax.baja.control.BIWritablePoint;
import javax.baja.history.ext.BHistoryExt;
import javax.baja.history.ext.BIntervalHistoryExt;
import javax.baja.sys.BComponent;
import javax.baja.sys.BFacets;
import javax.baja.sys.BInteger;
import javax.baja.sys.Type;
import javax.baja.units.BUnit;
import java.io.IOException;

public class ModbusPointWrapper extends PointWrapper  {
    private BModbusProxyExt proxyExt;

    public ModbusPointWrapper(BControlPoint controlPoint) throws IOException {
        super(controlPoint);
    }

    @Override
    protected void readProxyExt(BControlPoint controlPoint) {
        this.proxyExt = (BModbusProxyExt)controlPoint.getProxyExt();
    }

    @Override
    protected void readPointProperties() throws IOException {
        this.pointName = controlPoint.getName();
        this.pointAddress = proxyExt.getDataAddress().getAddress();
        this.pointType = String.valueOf(controlPoint.getType()).split(":")[1];
        this.type = controlPoint.getType();
        determineFacets();

        this.conversion = proxyExt.getConversion().toString();
        determineIfWritable();
        determineDataType();

        determinePointType();

        determineExtensions();
    }

}
