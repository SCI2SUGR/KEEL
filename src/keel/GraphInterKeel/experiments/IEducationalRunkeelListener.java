package keel.GraphInterKeel.experiments;

import keel.RunKeelTxtDocente.EducationalRunKeelTxt;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez and(University of Córdoba) 07/07/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


public interface IEducationalRunkeelListener<A extends EducationalRunKeelTxt> {

    /**
     * <p>
     *  Is invoqued when all partitions have finished
     * </p>
     * @param event Event
     */
    public void runKeelFinished(EducationalRunkeelEvent<A> event);


    /**
     * <p>
     * Is invoqued when a partition has finished
     * </p>
     * @param event Event
     */
    public void runKeelIterationCompleted(EducationalRunkeelEvent<A> event);
}
