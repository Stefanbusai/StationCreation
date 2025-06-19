package com.airedale.StationCreation.utils;

import javax.baja.nre.annotations.NiagaraType;
import javax.baja.sys.NotRunningException;
import javax.baja.sys.Sys;
import javax.baja.sys.Type;
import javax.baja.util.BWorker;
import javax.baja.util.CoalesceQueue;
import javax.baja.util.Worker;

/**
 * Slotomatic.
 */ 
@NiagaraType

/**
 * This class defines a custom worker thread.
 * 
 * @version 4.0
 * @author Phil Holden
 * @copyright Airedale International Air Conditioning Ltd.
 */
public class BAcisWorker
    extends BWorker
{
/*+ ------------ BEGIN BAJA AUTO GENERATED CODE ------------ +*/
/*@ $com.airedale.StationCreation.utils.BAcisWorker(2979906276)1.0$ @*/
/* Generated Mon May 23 09:05:46 BST 2016 by Slot-o-Matic (c) Tridium, Inc. 2012 */

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////
  
  @Override
  public Type getType() { return TYPE; }
  public static final Type TYPE = Sys.loadType(BAcisWorker.class);

/*+ ------------ END BAJA AUTO GENERATED CODE -------------- +*/

  public Worker getWorker()
  {
    if (worker == null)
    {
      queue = new CoalesceQueue(1000);
      worker = new Worker(queue);
    }
    return worker;
  }
  
  public boolean workerReady()
  {
	 return isRunning() && queue != null;
  }
  
  /**
  * Post an action to be run asynchronously.
  */
  public void postAsync(Runnable r)
  {
    if (!isRunning() || queue == null)
    {
	    throw new NotRunningException();
    }
    queue.enqueue(r);
  }
  
  /**
   * Get the size of the queue.
   */
  public int getQueueSize()
  {
    return queue.size();
  }

  /**
  * Clear the queue
  */
  public void clearQueue() {
    queue.clear();
  }

  /**
   * Default implementation is <code>getWorker().start(toPathString())</code>.
   */
  public void startAcisWorker()
  {
    getWorker().start(getAcisWorkerThreadName());
  }

  /**
   * Default implementation is <code>getWorker().stop()</code>.
   */
  public void stopAcisWorker()
  {              
    getWorker().stop();
    worker = null;
  }

  /**
   * Get the thread name used to start the worker.
   */
  public String getAcisWorkerThreadName()
  {                                 
    return toPathString();
  }

  
//////////////////////////////////////////////////////////
//Attributes
//////////////////////////////////////////////////////////
  
  private CoalesceQueue queue;
  private Worker worker;
}
