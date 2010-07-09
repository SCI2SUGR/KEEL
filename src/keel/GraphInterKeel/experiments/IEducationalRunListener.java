package keel.GraphInterKeel.experiments;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */



public interface IEducationalRunListener<A extends EducationalRun>
{

 /*
 * <p>
 * Listener interface for EducationalRunEven
 * Is used when the GUI of partitions is closed by used
 * </p>
 */

	/**
     * <p>
	 * Window of partitions is closed by user, then this method is invoqued
     * </p>
     *
     * @param Event  Event
	 */	
	public void closedEducationalExec(EducationalRunEvent<A> event);
}
