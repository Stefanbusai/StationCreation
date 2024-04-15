package com.airedale.StationCreation;

import javax.baja.nre.annotations.NiagaraAction;
import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.*;
import java.util.logging.Logger;

@NiagaraType
@NiagaraAction(
        name = "print"
)
public class BRecursiveTreePrint extends BComponent
{
//region /*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
//@formatter:off
/*@ $com.tridiumuniversity.devTrafficLights.BRecursiveTreePrint(2862381912)1.0$ @*/
/* Generated Wed Mar 06 10:17:02 GMT 2024 by Slot-o-Matic (c) Tridium, Inc. 2012-2024 */

  //region Action "print"

  /**
   * Slot for the {@code print} action.
   * @see #print()
   */
  public static final Action print = newAction(0, null);

  /**
   * Invoke the {@code print} action.
   * @see #print
   */
  public void print() { invoke(print, null, null); }

  //endregion Action "print"

  //region Type

  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BRecursiveTreePrint.class);

  //endregion Type

//@formatter:on
//endregion /*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

    public void doPrint(Context cx){
        BComponent root = this.getParent().getParentComponent();
        if (root == null) {
            log.warning(String.format("Root is null at %s", root.getSlotPath()));
            return;
        }
        log.info(String.format("Starting printing at %s", root.getSlotPath()));
        Thread thread = new Thread(() -> recurseComponent(root, cx), "Print_Thread");
        thread.start();
    }

    private void recurseComponent(BComponent root, Context cx){
        printComponent(root, cx, depth);
        depth ++;
        for(BComponent child : root.getChildComponents()){
            log.fine(String.format("Recursing into %s", child.getSlotPath()));
            recurseComponent(child, cx);
            printComponent(child, cx, depth);
        }
        depth --;
    }

    private void printComponent(BComponent component, Context cx, int depth) {
        String name = component.getName();
        String type = component.getType().toString();
        String startingTabs = repeatTab(depth);

        // Ensure name is not null
        if (name == null) {
            name = "";
        }

        // Calculate the number of tabs needed for alignment
        int numTabs = Math.max(0, 12 - depth - name.length() / 8); // Assuming tab width is 8 spaces

        // Create tabs for alignment
        StringBuilder middleTabs = new StringBuilder();
        for (int i = 0; i < numTabs; i++) {
            middleTabs.append("\t");
        }

        System.out.printf("%s%s%s(%s)%n", startingTabs, name, middleTabs.toString(), type);
    }


    public static String repeatDash(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("--");
        }
        return sb.toString();
    }

    public static String repeatTab(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
//            sb.append("    ");
        }
        return sb.toString();
    }
    private final Logger log = Logger.getLogger("hider");

    @Override
    public BIcon getIcon() {return  BIcon.std("r2/console.png");}

    private int depth = 0;


}
