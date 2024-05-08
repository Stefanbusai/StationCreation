package com.airedale.StationCreation.utils.links;

import com.airedale.StationCreation.utils.FileUtils;
import javafx.util.Pair;

import javax.baja.converters.*;
import javax.baja.naming.BOrd;
import javax.baja.status.*;
import javax.baja.sys.*;
import javax.baja.util.BConverter;
import javax.baja.util.BFormat;
import javax.baja.util.BNullConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.airedale.StationCreation.utils.FileUtils.readLinesFromFileAsArrayList;

public class LinkManager {

    private int depth = 0;
    private List<BLink> links = new ArrayList<BLink>();


    public List<BLink> findAllLinks(Context cx) {
        logger.info("Finding all links");
        BObject base = Sys.getStation();
        BOrd baseORD = BOrd.make("slot:/");
        BComponent rootComponent = (BComponent) baseORD.resolve(base).get();
        readLinksInThisAndAllSubComponents(rootComponent, cx);
        logger.info("Found: " + links.size()  + " links");
        return links;
    }
    private void readLinksInThisAndAllSubComponents(BComponent root, Context cx){
        readLinksFromComponent(root, cx);
        depth ++;
        for(BComponent child : root.getChildComponents()){
            readLinksInThisAndAllSubComponents(child, cx);
        }
        depth --;
    }
    private void readLinksFromComponent(BComponent component, Context cx){

        BLink[] linksFromComponent = component.getLinks();
        if(linksFromComponent.length > 0) {
            logger.info(linksFromComponent.length + " links found in " + component.getSlotPath().toString());
        }
        for (BLink link : linksFromComponent) {
            links.add(link);
            logger.info(links.size() + " total links so far");
        }
    }

    public static void writeLinksToCSVFile(List<BLink> links, String fileName, Context cx) {
        StringBuilder  CSVToPrint = new StringBuilder();
        //build headers
        String COMMA = ",";
        CSVToPrint.append("SourceORD").append(COMMA);
        CSVToPrint.append("SourceSlot").append(COMMA);
        CSVToPrint.append("TargetORD").append(COMMA);
        CSVToPrint.append("TargetSlot").append("\n");

        for (BLink link : links) {
            String sourceORDString = link.getSourceComponent().getSlotPathOrd().encodeToString();
            String sourceSlotString = link.getSourceSlotName();
            String targetORDString = link.getTargetComponent().getSlotPathOrd().encodeToString();
            String targetSlotString = link.getTargetSlotName();

            CSVToPrint.append(sourceORDString).append(COMMA);
            CSVToPrint.append(sourceSlotString).append(COMMA);
            CSVToPrint.append(targetORDString).append(COMMA);
            CSVToPrint.append(targetSlotString).append("\n");
        }
        BOrd fileORD = BOrd.make("file:^" + fileName);
        FileUtils.deleteFileIfExists(fileName);
        FileUtils.createNewFile(fileORD);
        FileUtils.printToFile(fileName, CSVToPrint.toString(), false);
    }

    public List<BLink> readAndCreateLinksFromCSVFile(String fileName, Context cx) {
        BOrd linksFileOrd = BOrd.make("file:^" + fileName);
        List<String> linesFromFileAsList = readLinesFromFileAsArrayList(linksFileOrd);
        linesFromFileAsList.remove(0);
        logger.info("The file contains " + linesFromFileAsList.size() + " links:");
        for (String line : linesFromFileAsList) {
            logger.info("    " + line);
        }
        ArrayList<BLink> linksToReturn = new ArrayList<BLink>();

        BObject base = Sys.getStation();
        for (String line : linesFromFileAsList) {
            logger.info("Processing links line: " + line);
            String[] linkDetails = line.split(",");
            if (linkDetails.length != 4) {
                continue;
            }

            String sourceORDString = linkDetails[0];

            String sourceSlotName = linkDetails[1];
            String targetORDString = linkDetails[2];
            String targetSlotName = linkDetails[3];

            BOrd sourceORD = BOrd.make(sourceORDString);
            BOrd targetORD = BOrd.make(targetORDString);
            BComponent source = ((BComponent) sourceORD.resolve(base).get());
            BOrd sourceHandle = source.getHandleOrd();
            BComponent target = ((BComponent) targetORD.resolve(base).get());

            if (linkIsValid(source, sourceSlotName, target, targetSlotName)) {
                BConverter converter = getConverterForLink(source, sourceSlotName, target, targetSlotName);
                Slot sourceSlot = source.getSlot(sourceSlotName);
                Slot targetSlot = target.getSlot(targetSlotName);
                BLink link = target.makeLink(source, sourceSlot, targetSlot, cx);
                target.add(null, link);
            }
        }
        return linksToReturn;
    }







    private BConverter getConverterForLink(BComponent source, String sourceSlotName, BComponent target, String targetSlotName) {
        try {
            Type sourceSlotType = source.get(sourceSlotName).getType();
            Type targetSlotType = target.get(targetSlotName).getType();
            BConverter converter = CONVERTER_MAP.get(new Pair<>(sourceSlotType, targetSlotType));
            // returns a new instance of the type of converter because reusing the same instance causes an "already parented" exception
            return (BConverter) converter.getType().getInstance();


        } catch(Exception e) {
            logger.warning("Could not get slot types for " + sourceSlotName + " and " + targetSlotName + "error: " + e);
            return null;
        }


    }


    private boolean linkIsValid(BComponent source, String sourceSlotName, BComponent target, String targetSlotName) {
        return !targetAlreadyHasLink(source, sourceSlotName, target, targetSlotName);
    }

    private boolean targetAlreadyHasLink(BComponent source, String sourceSlotName, BComponent target, String targetSlotName){

        BLink[] targetLinks = target.getLinks();
        for (BLink actualLink : targetLinks) {
            if (actualLink.getSourceSlotName().equals(sourceSlotName) &&
                    actualLink.getTargetSlotName().equals(targetSlotName)) {
                return true;
            }
        }
        return false;
    }


    private static boolean componentExists(BOrd ORD) {
        BObject base = Sys.getStation();
        try {
            BComponent target = ((BComponent) ORD.resolve(base).get());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static void deleteLink(BLink link, Context cx) {
        //TODO

    }

    public void addLink(BLink link, Context cx) {


        BOrd targetORD = link.getTargetComponent().getSlotPathOrd();
        BOrd sourceORD = link.getSourceComponent().getSlotPathOrd();
        String sourceSlotName = link.getSourceSlotName();
        String linkTargetSlotName = link.getTargetSlotName();
        logger.info("Adding link from " + sourceSlotName + " to " + linkTargetSlotName);

        BComponent target =  link.getTargetComponent();
        BComponent source = link.getSourceComponent();
        BOrd sourceHandle = source.getHandleOrd();
        BLink linkWithHandle = new BLink(sourceHandle,sourceSlotName, linkTargetSlotName, true);
        target.add(null, linkWithHandle);

    }

    public void addLinkFromLine(String line, Context cx) {
        BObject base = Sys.getStation();
        String[] linkDetails = line.split(", ");
        if ( linkDetails.length!=4){
            logger.warning("Link details are not 4 long");
            return;
        }

        String sourceORDString =  linkDetails[0];
        String sourceSlotString = linkDetails[1];
        String targetORDString = linkDetails[2];
        String targetSlotString = linkDetails[3];
        BOrd sourceORD = BOrd.make(sourceORDString);
        BOrd targetORD = BOrd.make(targetORDString);

        BComponent sourceComponent = ((BComponent) sourceORD.resolve(base).get());
        BOrd sourceHandle = sourceComponent.getHandleOrd();
        BComponent targetComponent = ((BComponent) targetORD.resolve(base).get());
        targetComponent.add(null, new BLink(sourceHandle, sourceSlotString, targetSlotString, true));
    }



    private final Logger logger = Logger.getLogger("LinkManager");

    public void addLinks(List<BLink> links, Context cx) {

        //TODO
    }

    public void filterBadLinks(List<BLink> links, Context cx) {


    }

    private static final Map<Pair<Type, Type>, BConverter> CONVERTER_MAP = new HashMap<>();

    static {
        // Populate the converter map
        // For each combination of source and target types, map to the appropriate converter
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BStatusEnum.TYPE),        new BStatusNumericToStatusEnum());
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BStatusString.TYPE),      new BStatusNumericToStatusString());
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BStatusBoolean.TYPE),     new BStatusNumericToStatusBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BBoolean.TYPE),           new BStatusNumericToBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BDouble.TYPE),            new BStatusNumericToNumber());
        CONVERTER_MAP.put(new Pair<>(BStatusNumeric.TYPE, BAbsTime.TYPE),           new BStatusNumericToAbsTime());

        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BStatusNumeric.TYPE),        new BStatusEnumToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BStatusString.TYPE),         new BStatusEnumToStatusString());
        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BString.TYPE),               new BStatusEnumToString());
        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BStatusBoolean.TYPE),        new BStatusEnumToStatusBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BBoolean.TYPE),              new BStatusEnumToBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusEnum.TYPE, BDouble.TYPE),               new BStatusEnumToNumber());

        CONVERTER_MAP.put(new Pair<>(BStatusBoolean.TYPE, BStatusNumeric.TYPE),     new BStatusBooleanToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BStatusBoolean.TYPE, BStatusEnum.TYPE),        new BStatusBooleanToStatusEnum());
        CONVERTER_MAP.put(new Pair<>(BStatusBoolean.TYPE, BStatusString.TYPE),      new BStatusBooleanToString());
        CONVERTER_MAP.put(new Pair<>(BStatusBoolean.TYPE, BDouble.TYPE),            new BStatusBooleanToNumber());
        CONVERTER_MAP.put(new Pair<>(BStatusBoolean.TYPE, BBoolean.TYPE),           new BStatusBooleanToBoolean());

        CONVERTER_MAP.put(new Pair<>(BBoolean.TYPE, BStatusNumeric.TYPE),           new BBooleanToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BBoolean.TYPE, BStatusEnum.TYPE),              new BBooleanToStatusEnum());
        CONVERTER_MAP.put(new Pair<>(BBoolean.TYPE, BStatusString.TYPE),            new BBooleanToStatusString());
        CONVERTER_MAP.put(new Pair<>(BBoolean.TYPE, BStatusBoolean.TYPE),           new BBooleanToStatusBoolean());
        CONVERTER_MAP.put(new Pair<>(BBoolean.TYPE, BDouble.TYPE),                  new BBooleanToNumber());

        CONVERTER_MAP.put(new Pair<>(BStatusString.TYPE, BStatusNumeric.TYPE),      new BStatusStringToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BStatusString.TYPE, BOrd.TYPE),                new BStatusStringToOrd());
        CONVERTER_MAP.put(new Pair<>(BStatusString.TYPE, BStatusBoolean.TYPE),      new BStatusStringToStatusBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusString.TYPE, BBoolean.TYPE),            new BStatusStringToBoolean());
        CONVERTER_MAP.put(new Pair<>(BStatusString.TYPE, BFormat.TYPE),             new BStatusStringToFormat());

        CONVERTER_MAP.put(new Pair<>(BAbsTime.TYPE, BStatusNumeric.TYPE),           new BAbsTimeToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BAbsTime.TYPE, BDouble.TYPE),                  new BAbsTimeToStatusNumeric());
        CONVERTER_MAP.put(new Pair<>(BAbsTime.TYPE, BStatusString.TYPE),            new BAbsTimeToStatusString());
        CONVERTER_MAP.put(new Pair<>(BAbsTime.TYPE, BString.TYPE),                  new BAbsTimeToString());

        CONVERTER_MAP.put(new Pair<>(BOrd.TYPE, BStatusString.TYPE),                new BOrdToStatusString());
        CONVERTER_MAP.put(new Pair<>(BOrd.TYPE, BString.TYPE),                      new BObjectToString());

        CONVERTER_MAP.put(new Pair<>(BFormat.TYPE, BStatusString.TYPE),             new BFormatToStatusString());
        CONVERTER_MAP.put(new Pair<>(BFormat.TYPE, BString.TYPE),                   new BFormatToString());

    }
}