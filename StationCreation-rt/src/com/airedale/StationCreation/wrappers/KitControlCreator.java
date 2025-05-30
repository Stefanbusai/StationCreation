package com.airedale.StationCreation.wrappers;

import com.airedale.StationCreation.PointCreator;
import com.tridium.kitControl.BLoopPoint;
import com.tridium.kitControl.constants.BNumericConst;
import com.tridium.kitControl.hvac.BLeadLagRuntime;
import com.tridium.kitControl.hvac.BSequenceLinear;
import com.tridium.kitControl.hvac.BTstat;
import com.tridium.kitControl.logic.*;
import com.tridium.kitControl.math.*;
import com.tridium.kitControl.timer.BBooleanDelay;
import com.tridium.kitControl.timer.BNumericDelay;
import com.tridium.kitControl.timer.BOneShot;
import com.tridium.kitControl.util.*;

import javax.baja.control.BControlPoint;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatus;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.*;
import javax.baja.util.BWsAnnotation;
import javax.baja.util.BWsTextBlock;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class KitControlCreator extends PointCreator {

    private static final Set<String> KIT_CONTROL_BOOLEAN_FOUR_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:And", "kitControl:Or", "kitControl:Xor", "kitControl:BqlExprComponent"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_ONE_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:AbsValue", "kitControl:ArcCosine", "kitControl:ArcSine", "kitControl:ArcTangent",
            "kitControl:Cosine", "kitControl:Sine", "kitControl:Tangent", "kitControl:Exponential",
            "kitControl:Factorial", "kitControl:LogBase10", "kitControl:LogNatural", "kitControl:Negative",
            "kitControl:SquareRoot"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_TWO_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:GreaterThan", "kitControl:LessThan", "kitControl:GreaterThanEqual", "kitControl:LessThanEqual",
            "kitControl:Equal", "kitControl:NotEqual", "kitControl:Divide", "kitControl:Modulus", "kitControl:Power",
            "kitControl:Subtract"
    ));
    private static final Set<String> KIT_CONTROL_NUMERIC_FOUR_INPUT_TYPES = new HashSet<>(Arrays.asList(
            "kitControl:Add", "kitControl:Average", "kitControl:Maximum", "kitControl:Minimum",
            "kitControl:Multiply"
    ));
    public KitControlCreator(){
    }

    private static final Logger logger = Logger.getLogger("KitControlCreator");

    public void addKitControlPointFromCSVLine(String kitControlPointString, Context cx) throws IOException {
        String[] pointDetails = kitControlPointString.split(",");
        if (pointDetails.length != 11){
            logger.warning("Kit Control points details not correct length: " + pointDetails.length);
            return;
        }
        String pointName = pointDetails[0];
        String parentSlotPathString = pointDetails[1];
        String pointSlotPath= parentSlotPathString + "/" + pointName;
        String pointFacets = pointDetails[2];
        String pointTypeString = pointDetails[3];
        String wsAnnotationString = pointDetails[4];
        String slot1_String = pointDetails[5];
        String slot2_String = pointDetails[6];
        String slot3_String = pointDetails[7];
        String slot4_String = pointDetails[8];
        String slot5_String = pointDetails[9];
        String slot6_String = pointDetails[10];

        boolean isControlPoint = true;
        // only does something if there isn't a component there already
        // TODO decide if it should delete the existing and re-create it, to allow for edits
        if (!pointExists(pointSlotPath, cx)) {
            // the control point is always BControlPoint
            // the particular type is determined by pointTypeString
            // most kitControl points have similar parameters and so are processed in the same way
            BControlPoint controlPoint = null;
            switch (pointTypeString) {
                case "kitControl:And": controlPoint = new BAnd();break;
                case "kitControl:Equal": controlPoint = new BEqual();break;
                case "kitControl:GreaterThan": controlPoint = new BGreaterThan();break;
                case "kitControl:GreaterThanEqual": controlPoint = new BGreaterThanEqual();break;
                case "kitControl:LessThan": controlPoint = new BLessThan();break;
                case "kitControl:LessThanEqual": controlPoint = new BLessThanEqual();break;
                case "kitControl:NotEqual": controlPoint = new BNotEqual();break;
                case "kitControl:Or": controlPoint = new BOr();break;
                case "kitControl:Xor": controlPoint = new BXor();break;
                case "kitControl:Not": controlPoint = new BNot();break;
                case "kitControl:AbsValue": controlPoint = new BAbsValue();break;
                case "kitControl:Add": controlPoint = new BAdd();break;
                case "kitControl:ArcCosine": controlPoint = new BArcCosine();break;
                case "kitControl:ArcSine": controlPoint = new BArcSine();break;
                case "kitControl:ArcTangent": controlPoint = new BArcTangent();break;
                case "kitControl:Average": controlPoint = new BAverage();break;
                case "kitControl:Cosine": controlPoint = new BCosine();break;
                case "kitControl:Divide": controlPoint = new BDivide();break;
                case "kitControl:Exponential": controlPoint = new BExponential();break;
                case "kitControl:Factorial": controlPoint = new BFactorial();break;
                case "kitControl:LogNatural": controlPoint = new BLogNatural();break;
                case "kitControl:Maximum": controlPoint = new BMaximum();break;
                case "kitControl:Minimum": controlPoint = new BMinimum();break;
                case "kitControl:Modulus": controlPoint = new BModulus();break;
                case "kitControl:Multiply": controlPoint = new BMultiply();break;
                case "kitControl:Negative": controlPoint = new BNegative();break;
                case "kitControl:Power": controlPoint = new BPower();break;
                case "kitControl:Reset": controlPoint = new BReset();break;
                case "kitControl:Sine": controlPoint = new BSine();break;
                case "kitControl:SquareRoot": controlPoint = new BSquareRoot();break;
                case "kitControl:Subtract": controlPoint = new BSubtract();break;
                case "kitControl:Tangent": controlPoint = new BTangent();break;
                case "kitControl:BooleanSwitch": controlPoint = new BBooleanSwitch();break;
                case "kitControl:EnumSwitch": controlPoint = new BEnumSwitch();break;
                case "kitControl:NumericSwitch": controlPoint = new BNumericSwitch();break;
                case "kitControl:Ramp": controlPoint = new BRamp();break;
                case "kitControl:Random": controlPoint = new BRandom();break;
                case "kitControl:SineWave": controlPoint = new BSineWave();break;
                case "kitControl:LoopPoint": controlPoint = new BLoopPoint();break;
                case "kitControl:Tstat": controlPoint = new BTstat();break;

                default:
                    isControlPoint = false;
                    break;
            }
            if (isControlPoint) {
                if (KIT_CONTROL_BOOLEAN_FOUR_INPUT_TYPES.contains(pointTypeString)) {
                    controlPoint.set("inA", (new BStatusBoolean(Boolean.parseBoolean(slot1_String), BStatus.ok)));
                    controlPoint.set("inB", (new BStatusBoolean(Boolean.parseBoolean(slot2_String), BStatus.ok)));
                    controlPoint.set("inC", (new BStatusBoolean(Boolean.parseBoolean(slot3_String), BStatus.ok)));
                    controlPoint.set("inD", (new BStatusBoolean(Boolean.parseBoolean(slot4_String), BStatus.ok)));
                } else if (KIT_CONTROL_NUMERIC_ONE_INPUT_TYPES.contains(pointTypeString)) {
                    controlPoint.set("inA", (new BStatusNumeric(Double.parseDouble(slot1_String), BStatus.ok)));
                } else if (KIT_CONTROL_NUMERIC_TWO_INPUT_TYPES.contains(pointTypeString)) {
                    controlPoint.set("inA", (new BStatusNumeric(Double.parseDouble(slot1_String), BStatus.ok)));
                    controlPoint.set("inB", (new BStatusNumeric(Double.parseDouble(slot2_String), BStatus.ok)));
                } else if (KIT_CONTROL_NUMERIC_FOUR_INPUT_TYPES.contains(pointTypeString)) {
                    controlPoint.set("inA", (new BStatusNumeric(Double.parseDouble(slot1_String), BStatus.ok)));
                    controlPoint.set("inB", (new BStatusNumeric(Double.parseDouble(slot2_String), BStatus.ok)));
                    controlPoint.set("inC", (new BStatusNumeric(Double.parseDouble(slot3_String), BStatus.ok)));
                    controlPoint.set("inD", (new BStatusNumeric(Double.parseDouble(slot4_String), BStatus.ok)));
                }


                controlPoint = addWsAnnotationToControlPoint(wsAnnotationString, controlPoint);
                // TODO handle facets. Groups components by their output type: numeric or boolean
//        addFacetsToPoint(controlPoint,);

                addPoint(controlPoint, pointName, pointSlotPath, cx);
            }
        }
        // TODO handle the special types: Reset, Timers, Counter, Status Demux and more
        if(!bComponentExists(pointSlotPath,cx)){
            BComponent component = null;
            switch (pointTypeString) {
                case "kitControl:Reset":
                    BReset bReset = new BReset();
                    bReset.setInputLowLimit(new BStatusNumeric(Double.parseDouble(slot1_String), BStatus.ok));
                    bReset.setInputHighLimit(new BStatusNumeric(Double.parseDouble(slot2_String), BStatus.ok));
                    bReset.setOutputLowLimit(new BStatusNumeric(Double.parseDouble(slot3_String), BStatus.ok));
                    bReset.setOutputHighLimit(new BStatusNumeric(Double.parseDouble(slot4_String), BStatus.ok));
                    component = bReset;
                    break;
                case "kitControl:BooleanDelay":
                    BBooleanDelay bBooleanDelay = new BBooleanDelay();
                    bBooleanDelay.setOnDelay(BRelTime.make(slot1_String));
                    bBooleanDelay.setOffDelay(BRelTime.make(slot2_String));
                    component = bBooleanDelay;
                    break;
                case "kitControl:NumericDelay":
                    BNumericDelay bNumericDelay = new BNumericDelay();
                    bNumericDelay.setUpdateTime(BRelTime.make(slot1_String));
                    bNumericDelay.setMaxStepSize(Double.parseDouble(slot2_String));
                    component = bNumericDelay;
                    break;
                case "kitControl:OneShot":
                    BOneShot bOneShot = new BOneShot();
                    bOneShot.setTime(BRelTime.make(slot1_String));
                    component = bOneShot;
                    break;
                case "kitControl:Counter":
                    BCounter bCounter = new BCounter();
                    bCounter.setPresetValue(new BStatusNumeric(Double.parseDouble(slot1_String), BStatus.ok));
                    bCounter.setCountIncrement(Float.parseFloat(slot2_String));
                    component = bCounter;
                    break;
                case "kitControl:StatusDemux": component = new BStatusDemux();break;
                case "kitControl:NumericToBitsDemux": component = new BNumericToBitsDemux();break;
                case "kitControl:DigitalInputDemux": component = new BDigitalInputDemux();break;
                default:
                    logger.info("Component cannot handle this type yet: " + pointTypeString);
                    return;
            }
            component = addWsAnnotationToComponent(wsAnnotationString,component);
            addComponent(component,pointName,pointSlotPath,cx);
        }
    }

    private boolean bComponentExists(String componentSlotPath, Context cx) {
        try{
            BOrd componentOrd = BOrd.make(componentSlotPath);
            BComponent component = (BComponent) componentOrd.get(Sys.getStation(), cx);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private static void addComponent(BComponent component, String componentName, String pointSlotPath, Context cx) {
        BOrd pointOrd = BOrd.make(pointSlotPath);
        BOrd parentOrd = pointOrd.getParent();
        BComponent parent = (BComponent) parentOrd.get(Sys.getStation(), cx);
        parent.add(componentName, component);
    }

    private BComponent addWsAnnotationToComponent(String wsAnnotationString, BComponent component) {
        String[] wsAnnotationStringArray = wsAnnotationString.split(":");
        if (wsAnnotationStringArray.length == 3){
            int wsAnnotation_p = Integer.parseInt(wsAnnotationStringArray[0]);
            int wsAnnotation_q = Integer.parseInt(wsAnnotationStringArray[1]);
            int wsAnnotation_w = Integer.parseInt(wsAnnotationStringArray[2]);
            component.add("wsAnnotation", BWsAnnotation.make(wsAnnotation_p,wsAnnotation_q,wsAnnotation_w));
        }
        return component;
    }
}
