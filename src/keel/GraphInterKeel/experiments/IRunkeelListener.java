package keel.GraphInterKeel.experiments;

import keel.RunKeelTxtDocente.EducationalRunKeelTxt;

/* 
 * General description:
 * Interface to listen RunkeelEvent. 
 * It is used by EjecutarParticiones class. 
 * 
 * Author: Juan Carlos Fern�ndez Caballero
 * 
 */
public interface IRunkeelListener<A extends EducationalRunKeelTxt> {
    /*
     *Is invoqued when all partitions have finished
     */

    public void runKeelFinished(RunkeelEvent<A> event);

    /*
     * Is invoqued when a partition has finished
     */
    public void runKeelIterationCompleted(RunkeelEvent<A> event);
}
