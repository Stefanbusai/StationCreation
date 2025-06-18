package com.airedale.StationCreation.wrappers;

import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import javax.baja.sys.BComponent;
import javax.baja.sys.Context;
import javax.baja.sys.Sys;
import javax.baja.util.BWsAnnotation;
import javax.baja.util.BWsTextBlock;
import java.util.logging.Logger;

public class TextBoxCreator {
    public TextBoxCreator(){
    }

    private static final Logger logger = Logger.getLogger("TextBoxCreator");

    public void addTextBoxFromCSVLine(String textBoxString, Context cx) {
        String[] pointDetails = textBoxString.split(",");
        if (pointDetails.length != 8){
            logger.warning("Text Box details not correct length: " + pointDetails.length + ", " + textBoxString);
            return;
        }
        String name = pointDetails[0];
        String foreground = pointDetails[1];
        String background = pointDetails[2];
        boolean border = pointDetails[3].equalsIgnoreCase("true");
        String wsAnnotationString = pointDetails[4];
        String font = pointDetails[5];
        String parentSlotPathString = pointDetails[6];
        String slotPathString= parentSlotPathString + "/" + name;
        String textSingleLine = pointDetails[7];
        // PH: re-instate commas that were replaced
        textSingleLine = textSingleLine.replaceAll("COMMA", ",");
        // PH: if original text box was empty
        if (textSingleLine.equals("-")) {
            textSingleLine = "";
        }
        // only does something if there isn't a text box there already
        // TODO decide if it should delete the existing and re-create it, to allow for edits
        if(!textBoxExists(slotPathString,cx)) {
            BWsTextBlock wsTextBlock = new BWsTextBlock();
            wsTextBlock.setBackground(background);
            wsTextBlock.setForeground(foreground);
            wsTextBlock.setBorder(border);
            wsTextBlock.setFont(font);
            addWsAnnotationToTextBox(wsAnnotationString, wsTextBlock);
            String textMultiLine = textSingleLine.replaceAll(";", "\n");
            wsTextBlock.setText(textMultiLine);

            addTextBox(wsTextBlock, name, slotPathString, cx);
        }
    }

    private BWsTextBlock addWsAnnotationToTextBox(String wsAnnotationString, BWsTextBlock textBlock) {
        String[] wsAnnotationStringArray = wsAnnotationString.split(":");
        if (wsAnnotationStringArray.length == 4){
            int p = Integer.parseInt(wsAnnotationStringArray[0]);
            int q = Integer.parseInt(wsAnnotationStringArray[1]);
            int w = Integer.parseInt(wsAnnotationStringArray[2]);
            int h = Integer.parseInt(wsAnnotationStringArray[3]);
            textBlock.setWsAnnotation(BWsAnnotation.make(p,q,w,h));
//            textBlock.add("wsAnnotation", BWsAnnotation.make(wsAnnotation_p,wsAnnotation_q,wsAnnotation_w,wsAnnotation_h));
        }
        return textBlock;
    }

    private static void addTextBox(BWsTextBlock textBox, String textBoxName, String textBoxSlotPath, Context cx) {
        BOrd textBoxOrd = BOrd.make(textBoxSlotPath);
        BOrd parentOrd = textBoxOrd.getParent();
        BComponent parent = (BComponent) parentOrd.get(Sys.getStation(), cx);
        parent.add(textBoxName, textBox);
    }

    protected boolean textBoxExists(String pointSlotPath, Context cx) {
        try{
            BOrd textBoxORD = BOrd.make(pointSlotPath);
            BWsTextBlock controlPoint = (BWsTextBlock) textBoxORD.get(Sys.getStation(), cx);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
