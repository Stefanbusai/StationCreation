package com.airedale.StationCreation.pointExport;

import com.tridium.modbusCore.datatypes.BFlexAddress;
import com.tridium.modbusCore.enums.BAddressFormatEnum;
import com.tridium.modbusCore.enums.BDataTypeEnum;
import com.tridium.modbusCore.enums.BRegisterTypeEnum;
import com.tridium.modbusCore.enums.BStatusTypeEnum;
import com.tridium.modbusCore.server.point.BModbusServerBooleanProxyExt;
import com.tridium.modbusCore.server.point.BModbusServerNumericProxyExt;
import com.tridium.modbusCore.server.point.BModbusServerProxyExt;

import javax.baja.bacnet.datatypes.BBacnetObjectIdentifier;
import javax.baja.bacnet.export.*;
import javax.baja.control.*;
import javax.baja.naming.BOrd;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import java.util.logging.Logger;

public class PointToExport
{
    protected String csvLine;
    protected BOrd sourceOrd;
    protected int address;
    protected boolean writable;
    protected boolean valid = true;

    //getters
    public boolean isValid()
    {
        return valid;
    }

    public BOrd getSourceOrd()
    {
        return sourceOrd;
    }

    public int getAddress()
    {
        return address;
    }

    public boolean isWritable()
    {
        return writable;
    }


    public PointToExport(String csvLine)
    {
        this.csvLine = csvLine;
        String[] pointDetails = csvLine.split(",");
        // check the line length
        if (pointDetails.length != 3)
        {
            logger.warning("point to export details not correct length: " + pointDetails.length);
            valid = false;
            return;
        }
        // parse the source ORD
        try
        {
            sourceOrd = BOrd.make(pointDetails[0]);
        }
        catch (Exception e)
        {
            logger.warning("point to export ORD not valid: " + csvLine);
            valid = false;
            return;
        }
        // parse the address
        try
        {
            address = Integer.parseInt(pointDetails[1]);
        }
        catch (Exception e)
        {
            logger.warning("point to export address not valid: " + csvLine);
            valid = false;
            return;
        }
        // parse the writable boolean
        try
        {
            writable = Boolean.parseBoolean(pointDetails[2]);
        }
        catch (Exception e)
        {
            logger.warning("point to export writable boolean not valid: " + csvLine);
            valid = false;
            return;
        }
    }

    private static final Logger logger = Logger.getLogger("stationCreation.PointToExport");

    /**
     * Create a Bacnet point descriptor.
     */
    public BBacnetPointDescriptor createBacnetPointDescriptor(String pointName, Context cx)
    {
        // resolve the point in the SourceORD
        BBacnetPointDescriptor bacnetPointDescriptor;

        BControlPoint sourcePoint = (BControlPoint) sourceOrd.resolve(Sys.getStation(), cx).getComponent();

        if (sourcePoint.getType().equals(BNumericPoint.TYPE))
        {
            bacnetPointDescriptor = createBacnetAnalogValueDescriptor();
        }
        else if (sourcePoint.getType().equals(BNumericWritable.TYPE))
        {
            if (writable)
            {
                bacnetPointDescriptor = createBacnetAnalogOutputDescriptor();
            }
            else
            {
                bacnetPointDescriptor = createBacnetAnalogValueDescriptor();
            }
        }
        else if (sourcePoint.getType().equals(BBooleanPoint.TYPE))
        {
            bacnetPointDescriptor = createBacnetBinaryValueDescriptor();
        }
        else if (sourcePoint.getType().equals(BBooleanWritable.TYPE))
        {
            if (writable)
            {
                bacnetPointDescriptor = createBacnetBinaryOutputDescriptor();
            }
            else
            {
                bacnetPointDescriptor = createBacnetBinaryValueDescriptor();
            }
        }
        else
        {
            logger.warning("point to export source point type not supported: " + sourcePoint.getType());
            valid = false;
            return null;
        }
        // set the source ORD
        bacnetPointDescriptor.setPointOrd(sourceOrd);

        bacnetPointDescriptor.setObjectName(pointName + "@" + address);

        return bacnetPointDescriptor;
    }

    /**
     * Create a Bacnet analog output descriptor.
     */
    private BBacnetPointDescriptor createBacnetAnalogOutputDescriptor()
    {
        BBacnetPointDescriptor bacnetPointDescriptor;
        bacnetPointDescriptor = new BBacnetAnalogOutputDescriptor();
        bacnetPointDescriptor.setObjectId(
                BBacnetObjectIdentifier.make(1, address)); // 1 is the object type for AnalogOutput
        ((BBacnetAnalogOutputDescriptor) bacnetPointDescriptor).setBacnetWritable("in10");
        return bacnetPointDescriptor;
    }

    /**
     * Create a Bacnet analog value descriptor.
     */
    private BBacnetPointDescriptor createBacnetAnalogValueDescriptor()
    {
        BBacnetPointDescriptor bacnetPointDescriptor;
        bacnetPointDescriptor = new BBacnetAnalogValueDescriptor();
        bacnetPointDescriptor.setObjectId(
                BBacnetObjectIdentifier.make(2, address)); // 2 is the object type for AnalogValue

        return bacnetPointDescriptor;
    }

    /**
     * Create a Bacnet binary output descriptor.
     */
    private BBacnetPointDescriptor createBacnetBinaryOutputDescriptor()
    {
        BBacnetPointDescriptor bacnetPointDescriptor;
        bacnetPointDescriptor = new BBacnetBinaryOutputDescriptor();
        bacnetPointDescriptor.setObjectId(
                BBacnetObjectIdentifier.make(4, address)); // 4 is the object type for BinaryOutput
        ((BBacnetBinaryOutputDescriptor) bacnetPointDescriptor).setBacnetWritable("in10");
        return bacnetPointDescriptor;
    }

    /**
     * Create a Bacnet binary value descriptor.
     */
    private BBacnetPointDescriptor createBacnetBinaryValueDescriptor()
    {
        BBacnetPointDescriptor bacnetPointDescriptor;
        bacnetPointDescriptor = new BBacnetBinaryValueDescriptor();
        bacnetPointDescriptor.setObjectId(
                BBacnetObjectIdentifier.make(5, address)); // 5 is the object type for BinaryValue
        return bacnetPointDescriptor;
    }

    /**
     * Create a Modbus server control point.
     */
    public BControlPoint createModbusServerControlPoint(String pointName, Context cx)
    {
        BControlPoint controlPoint;
        BControlPoint sourcePoint = (BControlPoint) sourceOrd.resolve(Sys.getStation(), cx).getComponent();

        if (sourcePoint.getType().equals(BNumericPoint.TYPE))
        {
            controlPoint = createModbusSlaveReadOnlyNumericPoint();
        }
        else if (sourcePoint.getType().equals(BNumericWritable.TYPE))
        {
            if (writable)
            {
                controlPoint = createModbusSlaveWritableNumericPoint();
            }
            else
            {
                controlPoint = createModbusSlaveReadOnlyNumericPoint();
            }
        }
        else if (sourcePoint.getType().equals(BBooleanPoint.TYPE))
        {
            controlPoint = createModbusSlaveReadOnlyBooleanPoint();
        }
        else if (sourcePoint.getType().equals(BBooleanWritable.TYPE))
        {
            if (writable)
            {
                controlPoint = createModbusSlaveWritableBooleanPoint();
            }
            else
            {
                controlPoint = createModbusSlaveReadOnlyBooleanPoint();
            }
        }
        else
        {
            logger.warning("point to export source point type not supported: " + sourcePoint.getType());
            valid = false;
            return null;
        }

        BModbusServerProxyExt modbusServerProxyExt = (BModbusServerProxyExt) controlPoint.getProxyExt();
        BFlexAddress flexAddress = new BFlexAddress();
        flexAddress.setAddressFormat(BAddressFormatEnum.decimal);
        flexAddress.setAddressFromInt(address);
        modbusServerProxyExt.setDataAddress(flexAddress);

        return controlPoint;
    }

    /**
     * Create a Modbus slave read only numeric point.
     */
    private BControlPoint createModbusSlaveReadOnlyNumericPoint()
    {
        BControlPoint controlPoint = new BNumericWritable();

        BModbusServerNumericProxyExt modbusServerNumericProxyExt = new BModbusServerNumericProxyExt();
        modbusServerNumericProxyExt.setRegType(BRegisterTypeEnum.input);
        modbusServerNumericProxyExt.setDataType(BDataTypeEnum.integerType);

        controlPoint.setProxyExt(modbusServerNumericProxyExt);

        return controlPoint;
    }

    /**
     * Create a Modbus slave writable numeric point.
     */
    private BControlPoint createModbusSlaveWritableNumericPoint()
    {
        BControlPoint controlPoint = new BNumericWritable();

        BModbusServerNumericProxyExt modbusServerNumericProxyExt = new BModbusServerNumericProxyExt();
        modbusServerNumericProxyExt.setRegType(BRegisterTypeEnum.holding);
        modbusServerNumericProxyExt.setDataType(BDataTypeEnum.integerType);

        controlPoint.setProxyExt(modbusServerNumericProxyExt);

        return controlPoint;
    }

    /**
     * Create a Modbus slave read only boolean point.
     */
    private BControlPoint createModbusSlaveReadOnlyBooleanPoint()
    {
        BControlPoint controlPoint = new BBooleanWritable();

        BModbusServerBooleanProxyExt modbusServerBooleanProxyExt = new BModbusServerBooleanProxyExt();
        modbusServerBooleanProxyExt.setStatusType(BStatusTypeEnum.input);

        controlPoint.setProxyExt(modbusServerBooleanProxyExt);

        return controlPoint;
    }

    /**
     * Create a Modbus slave writable boolean point.
     */
    private BControlPoint createModbusSlaveWritableBooleanPoint()
    {
        BControlPoint controlPoint = new BBooleanWritable();

        BModbusServerBooleanProxyExt modbusServerBooleanProxyExt = new BModbusServerBooleanProxyExt();
        modbusServerBooleanProxyExt.setStatusType(BStatusTypeEnum.coil);

        controlPoint.setProxyExt(modbusServerBooleanProxyExt);

        return controlPoint;
    }

}
