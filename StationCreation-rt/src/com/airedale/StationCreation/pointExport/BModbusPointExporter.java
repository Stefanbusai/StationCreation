package com.airedale.StationCreation.pointExport;

import com.airedale.StationCreation.utils.BAcisWorker;
import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.StringUtils;
import com.airedale.StationCreation.utils.links.LinkManager;
import com.tridium.modbusCore.server.point.BModbusServerPointDeviceExt;

import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import javax.baja.util.BFormat;
import javax.baja.util.IFuture;
import javax.baja.util.Invocation;
import java.util.List;
import java.util.logging.Logger;

@NiagaraType

@NiagaraProperty(
        name = "status",
        type = "BStatusString",
        defaultValue = "new BStatusString(\"\")",
        flags = Flags.SUMMARY | Flags.READONLY)

@NiagaraProperty(
        name = "modbusSlaveDeviceOrd",
        type = "BOrd",
        defaultValue = "BOrd.make(\"station:|slot:/Drivers/ModbusSlaveNetwork/ModbusSlaveDevice\")",
        flags = Flags.SUMMARY)

@NiagaraProperty(
        name = "pointNameFormat",
        type = "BFormat",
        defaultValue = "BFormat.make(\"%name%\")",
        flags = Flags.SUMMARY)

@NiagaraProperty(
        name = "csvFile",
        type = "BOrd",
        defaultValue = "BOrd.make(\"file:^points_to_export_to_modbus.csv\")",
        flags = Flags.SUMMARY)

@NiagaraProperty(
        name = "worker",
        type = "BAcisWorker",
        defaultValue = "new BAcisWorker()",
        flags = Flags.HIDDEN)

@NiagaraAction(
        name = "exportPoints",
        flags = Flags.ASYNC)

@NiagaraAction(
        name = "reset")

public class BModbusPointExporter
        extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.airedale.StationCreation.pointExport.BModbusPointExporter(1290310795)1.0$ @*/
/* Generated Fri Jun 20 13:25:03 BST 2025 by Slot-o-Matic (c) Tridium, Inc. 2012-2025 */

  //region Property "status"

  /**
   * Slot for the {@code status} property.
   * @see #getStatus
   * @see #setStatus
   */
  public static final Property status = newProperty(Flags.SUMMARY | Flags.READONLY, new BStatusString(""), null);

  /**
   * Get the {@code status} property.
   * @see #status
   */
  public BStatusString getStatus() { return (BStatusString)get(status); }

  /**
   * Set the {@code status} property.
   * @see #status
   */
  public void setStatus(BStatusString v) { set(status, v, null); }

  //endregion Property "status"

  //region Property "modbusSlaveDeviceOrd"

  /**
   * Slot for the {@code modbusSlaveDeviceOrd} property.
   * @see #getModbusSlaveDeviceOrd
   * @see #setModbusSlaveDeviceOrd
   */
  public static final Property modbusSlaveDeviceOrd = newProperty(Flags.SUMMARY, BOrd.make("station:|slot:/Drivers/ModbusSlaveNetwork/ModbusSlaveDevice"), null);

  /**
   * Get the {@code modbusSlaveDeviceOrd} property.
   * @see #modbusSlaveDeviceOrd
   */
  public BOrd getModbusSlaveDeviceOrd() { return (BOrd)get(modbusSlaveDeviceOrd); }

  /**
   * Set the {@code modbusSlaveDeviceOrd} property.
   * @see #modbusSlaveDeviceOrd
   */
  public void setModbusSlaveDeviceOrd(BOrd v) { set(modbusSlaveDeviceOrd, v, null); }

  //endregion Property "modbusSlaveDeviceOrd"

  //region Property "pointNameFormat"

  /**
   * Slot for the {@code pointNameFormat} property.
   * @see #getPointNameFormat
   * @see #setPointNameFormat
   */
  public static final Property pointNameFormat = newProperty(Flags.SUMMARY, BFormat.make("%name%"), null);

  /**
   * Get the {@code pointNameFormat} property.
   * @see #pointNameFormat
   */
  public BFormat getPointNameFormat() { return (BFormat)get(pointNameFormat); }

  /**
   * Set the {@code pointNameFormat} property.
   * @see #pointNameFormat
   */
  public void setPointNameFormat(BFormat v) { set(pointNameFormat, v, null); }

  //endregion Property "pointNameFormat"

  //region Property "csvFile"

  /**
   * Slot for the {@code csvFile} property.
   * @see #getCsvFile
   * @see #setCsvFile
   */
  public static final Property csvFile = newProperty(Flags.SUMMARY, BOrd.make("file:^points_to_export_to_modbus.csv"), null);

  /**
   * Get the {@code csvFile} property.
   * @see #csvFile
   */
  public BOrd getCsvFile() { return (BOrd)get(csvFile); }

  /**
   * Set the {@code csvFile} property.
   * @see #csvFile
   */
  public void setCsvFile(BOrd v) { set(csvFile, v, null); }

  //endregion Property "csvFile"

  //region Property "worker"

  /**
   * Slot for the {@code worker} property.
   * @see #getWorker
   * @see #setWorker
   */
  public static final Property worker = newProperty(Flags.HIDDEN, new BAcisWorker(), null);

  /**
   * Get the {@code worker} property.
   * @see #worker
   */
  public BAcisWorker getWorker() { return (BAcisWorker)get(worker); }

  /**
   * Set the {@code worker} property.
   * @see #worker
   */
  public void setWorker(BAcisWorker v) { set(worker, v, null); }

  //endregion Property "worker"

  //region Action "exportPoints"

  /**
   * Slot for the {@code exportPoints} action.
   * @see #exportPoints()
   */
  public static final Action exportPoints = newAction(Flags.ASYNC, null);

  /**
   * Invoke the {@code exportPoints} action.
   * @see #exportPoints
   */
  public void exportPoints() { invoke(exportPoints, null, null); }

  //endregion Action "exportPoints"

  //region Action "reset"

  /**
   * Slot for the {@code reset} action.
   * @see #reset()
   */
  public static final Action reset = newAction(0, null);

  /**
   * Invoke the {@code reset} action.
   * @see #reset
   */
  public void reset() { invoke(reset, null, null); }

  //endregion Action "reset"

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BModbusPointExporter.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    private static final Logger logger = Logger.getLogger(TYPE.getModule().getModuleName() + "." + TYPE.getTypeName());

    /**
     * Called by the framework when the component is started.
     */
    @Override
    public void started() throws Exception
    {
        // If the station is already at steady state when the
        // component is started, invoke the atSteadyState() callback.
        if (Sys.atSteadyState())
        {
            atSteadyState();
        }
    }

    /**
     * Called by the framework during station bootstrap, after the steady state timeout has expired.
     */
    @Override
    public void atSteadyState() throws Exception
    {
        reset();
    }

    /**
     * Called by the framework when the component is stopped.
     */
    @Override
    public void stopped() throws Exception
    {
        // do nothing
    }

    /**
     * Reset action.
     */
    public void doReset()
    {
        setStatus(new BStatusString("Ready", BStatus.ok));
    }

    /**
     * ExportPoints action.
     */
    public void doExportPoints(Context cx){
        logger.info("Starting export points creation");
        setStatus(new BStatusString("Starting export", BStatus.ok));

        List<String> listOfPointLinesToExport = FileUtils.readLinesFromFileAsArrayList(getCsvFile());
        listOfPointLinesToExport.remove(0);
        for (String pointLineToExport : listOfPointLinesToExport){
            PointToExport pointToExport = new PointToExport(pointLineToExport);
            if (pointToExport.isValid()){
                logger.info("Processing point line: " + pointLineToExport);
                processExportPoint(pointToExport, cx);
            }
        }

        setStatus(new BStatusString("Ready", BStatus.ok));
    }

    /**
     * Process an export point.
     */
    private void processExportPoint(PointToExport pointToExport, Context cx) {
        BComponent sourceComponent = (BComponent) pointToExport.getSourceOrd().resolve(Sys.getStation()).get();
        String pointName = createFormattedPointName(sourceComponent, cx);

        logger.info("Creating new export point with name: " + pointName);

        BControlPoint controlPoint = pointToExport.createModbusServerControlPoint(pointName, cx);
        if (controlPoint != null) {
            BOrd modbusServerPointDeviceExtOrd = BOrd.make(getModbusSlaveDeviceOrd().encodeToString() + "/points");
            BModbusServerPointDeviceExt modbusServerPointDeviceExt = (BModbusServerPointDeviceExt) modbusServerPointDeviceExtOrd.resolve(Sys.getStation()).get();
            modbusServerPointDeviceExt.add(pointName, controlPoint);
            logger.info("Created control point with proxy ext: " + controlPoint.getProxyExt().toString(cx));

            if (!LinkManager.targetAlreadyHasLink(sourceComponent, "out", controlPoint, "in10"))
            {
                Slot sourceSlot = sourceComponent.getSlot("out");
                Slot targetSlot = controlPoint.getSlot("in10");
                BLink link = controlPoint.makeLink(sourceComponent, sourceSlot, targetSlot, cx);
                controlPoint.add(null, link);
            }
        }
    }

    /**
     * Create a formatted point name.
     */
    private String createFormattedPointName(BComponent component, Context cx)
    {
        String formattedPointName = getPointNameFormat().format(component, cx);

        return StringUtils.insertSpecialCharacters(formattedPointName);
    }

    /**
     * Use the custom worker thread for actions that have the "async" flag set.
     */
    @Override
    public IFuture post(Action action,
                        BValue argument,
                        Context context)
    {
        getWorker().postAsync(new Invocation(this, action, argument, context));
        return null;
    }

}
