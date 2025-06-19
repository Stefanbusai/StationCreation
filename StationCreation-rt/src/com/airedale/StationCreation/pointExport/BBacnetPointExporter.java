package com.airedale.StationCreation.pointExport;

import com.airedale.StationCreation.utils.BAcisWorker;
import com.airedale.StationCreation.utils.FileUtils;
import com.airedale.StationCreation.utils.StringUtils;

import javax.baja.bacnet.export.BBacnetAnalogValueDescriptor;
import javax.baja.bacnet.export.BBacnetPointDescriptor;
import javax.baja.collection.BITable;
import javax.baja.collection.TableCursor;
import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import javax.baja.naming.SlotPath;
import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraProperty;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusString;
import javax.baja.sys.*;
import javax.baja.util.BFormat;
import javax.baja.util.IFuture;
import javax.baja.util.Invocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@NiagaraType
@NiagaraProperty(
        name = "status",
        type = "BStatusString",
        defaultValue = "new BStatusString(\"\")",
        flags = Flags.SUMMARY | Flags.READONLY)
@NiagaraProperty(
        name = "bacnetNetworkOrd",
        type = "BOrd",
        defaultValue = "BOrd.make(\"station:|slot:/Drivers/BacnetNetwork\")",
        flags = Flags.SUMMARY)
@NiagaraProperty(
        name = "pointNameFormat",
        type = "BFormat",
        defaultValue = "BFormat.make(\"%name%\")",
        flags = Flags.SUMMARY)
@NiagaraProperty(
        name = "csvFile",
        type = "BOrd",
        defaultValue = "BOrd.make(\"file:^points_to_export_to_bacnet.csv\")",
        flags = Flags.SUMMARY)
@NiagaraProperty(
        name = "worker",
        type = "BAcisWorker",
        defaultValue = "new BAcisWorker()",
        flags = Flags.HIDDEN)
@NiagaraAction(
        name = "exportPoints",
        flags = Flags.ASYNC
)
@NiagaraAction(
        name = "reset")
public class BBacnetPointExporter
        extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.airedale.StationCreation.pointExport.BBacnetPointExporter(392740591)1.0$ @*/
/* Generated Thu Jun 19 14:26:57 BST 2025 by Slot-o-Matic (c) Tridium, Inc. 2012-2025 */

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

  //region Property "bacnetNetworkOrd"

  /**
   * Slot for the {@code bacnetNetworkOrd} property.
   * @see #getBacnetNetworkOrd
   * @see #setBacnetNetworkOrd
   */
  public static final Property bacnetNetworkOrd = newProperty(Flags.SUMMARY, BOrd.make("station:|slot:/Drivers/BacnetNetwork"), null);

  /**
   * Get the {@code bacnetNetworkOrd} property.
   * @see #bacnetNetworkOrd
   */
  public BOrd getBacnetNetworkOrd() { return (BOrd)get(bacnetNetworkOrd); }

  /**
   * Set the {@code bacnetNetworkOrd} property.
   * @see #bacnetNetworkOrd
   */
  public void setBacnetNetworkOrd(BOrd v) { set(bacnetNetworkOrd, v, null); }

  //endregion Property "bacnetNetworkOrd"

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
  public static final Property csvFile = newProperty(Flags.SUMMARY, BOrd.make("file:^points_to_export_to_bacnet.csv"), null);

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
  public static final Type TYPE = Sys.loadType(BBacnetPointExporter.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

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
    private final String BQLbacnetPointDescriptor = "bql:select * from bacnet:BacnetPointDescriptor";
    private List<BBacnetPointDescriptor> listOfExistingExportPoints = new ArrayList<>();
    public void doExportPoints(Context cx){
        logger.info("Starting export points creation");
        setStatus(new BStatusString("Starting export", BStatus.ok));

        findAllBacnetExportedPoints();

        // read csv file and loop through list
        List<String> listOfPointLinesToExport = FileUtils.readLinesFromFileAsArrayList(getCsvFile());
        listOfPointLinesToExport.remove(0);
        for (String pointLineToExport : listOfPointLinesToExport){
            PointToExport pointToExport = new PointToExport(pointLineToExport);
            if (pointToExport.isValid()){
                logger.info("Processing point line: " + pointLineToExport);
                processExportPoint(pointToExport, cx);
            }
        }


        // if it exists

        setStatus(new BStatusString("Ready", BStatus.ok));
    }

    private void findAllBacnetExportedPoints() {
        listOfExistingExportPoints = new ArrayList<>();

        BITable<? extends BIObject> table =
                (BITable<? extends BIObject>) BOrd.make(BQLbacnetPointDescriptor).get(Sys.getStation());
        try (TableCursor<? extends BIObject> cursor = table.cursor()) {
            while (cursor.next()) {
                try {
                    BBacnetPointDescriptor point = (BBacnetPointDescriptor) cursor.get();

//                    logger.info("Point parent ord: " + ((BComponent) point.getParent()).getSlotPathOrd());
//                    BOrd pointSlotPath = (BOrd) cursor.get();
//                    BControlPoint point = (BControlPoint) pointSlotPath.resolve(Sys.getStation(), cx).getComponent();
                    listOfExistingExportPoints.add(point);
                } catch (Exception e) {
//                    logger.info("Point not an BOrd");
                }
            }
        }
        logger.info("Found "+ listOfExistingExportPoints.size() + " existing export points");
    }

    private void processExportPoint(PointToExport pointToExport, Context cx) {
        BBacnetPointDescriptor existingPoint = getExistingPoint(pointToExport);
        if (existingPoint != null) {
            logger.info("An export Point already exists for " + pointToExport.getSourceOrd());
            // if it exists and it is different, delete the existing and re-create
            if (pointToExport.getAddress() != existingPoint.getObjectId().getInstanceNumber()) {
                BComponent ExportTableParentFolder = (BComponent) existingPoint.getParent(); // getParentComponent() does not work
                String pointName = existingPoint.getName();
                ExportTableParentFolder.remove(pointName);
                // add pointToExport.createPointDescriptor if not Null
                BBacnetPointDescriptor bacnetPointDescriptor = pointToExport.createPointDescriptor(pointName, cx);
                if (bacnetPointDescriptor != null) {
                    ExportTableParentFolder.add(pointName, bacnetPointDescriptor);
                    logger.info("Amended: " + bacnetPointDescriptor.toString(cx));
                }
            }
            // and it is identical, do nothing
        }
        // if it doesn't exist, create it
        else {
            BOrd exportTableOrd = BOrd.make(getBacnetNetworkOrd().encodeToString() + "/localDevice/exportTable");
            BComponent exportTable = (BComponent) exportTableOrd.resolve(Sys.getStation()).get();

            BComponent sourceComponent = (BComponent) pointToExport.getSourceOrd().resolve(Sys.getStation()).get();
            String pointName = createFormattedPointName(sourceComponent, cx);

            logger.info("Creating new export point with name: " + pointName);

            BBacnetPointDescriptor bacnetPointDescriptor = pointToExport.createPointDescriptor(pointName, cx);
            if (bacnetPointDescriptor != null) {
                exportTable.add(pointName, bacnetPointDescriptor);
                logger.info("Created: " + bacnetPointDescriptor.toString(cx));
            }
        }
    }

    private BBacnetPointDescriptor getExistingPoint(PointToExport pointToExport) {
        BOrd pointToExportORD = pointToExport.getSourceOrd();
        for ( BBacnetPointDescriptor existingExportPoint: listOfExistingExportPoints){
            if (existingExportPoint.getPointOrd().equals(pointToExportORD) ||
                existingExportPoint.getHandleOrd().equals(pointToExportORD)){
                return existingExportPoint;
            }
        }
        return null;
    }

    /**
     * Create a formatted point name.
     */
    private String createFormattedPointName(BComponent component, Context cx)
    {
        String formattedPointName = getPointNameFormat().format(component, cx);

        return StringUtils.insertSpecialCharacters(formattedPointName);
    }

    private static final Logger logger = Logger.getLogger("BacnetPointExporter");

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
