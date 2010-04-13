package keel.GraphInterKeel.experiments;

import keel.RunKeelTxtDocente.EducationalRunKeelTxt;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 07/07/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class EducationalPartitionsRun extends Thread {

    /**
     * <p>
     * This class uses a Runkeetxt object with a thread. Run the partitions
     * of the experiments in iterative way
     * </p>
     */
/////////////////////////////////////////////////////////////////
//--------------------------------------------------- Properties
/////////////////////////////////////////////////////////////////	

    protected Runtime rt;
    protected Process proc;
    protected IEducationalRunkeelListener<EducationalRunKeelTxt> listener;
    private EducationalRunKeelTxt runkeel;
    protected boolean finalizado;


///////////////////////////////////////////////////////////////////
//	------------------------------------------------- Constructors
///////////////////////////////////////////////////////////////////		
    public EducationalPartitionsRun(EducationalRunKeelTxt runkeeltxt, IEducationalRunkeelListener<EducationalRunKeelTxt> listener) {
        super();
        rt = null;
        proc = null;
        this.runkeel = runkeeltxt;
        this.listener = listener;
        finalizado = false;
    }

//////////////////////////////////////////////////////////////////
//---------------------------------------------------- Method run
//////////////////////////////////////////////////////////////////		
    public void run() {
        while (!runkeel.isFinished() && finalizado == false) {
            //Ejecucion de una particion
            runkeel.doIterate();

            //Fire runKeelIterationCompleted event
            fireIterationCompleted();
        }
        //Fire RunkeelFinished event
        fireRunkeelFinished();
    }


/////////////////////////////////////////////////////////////////
//------------------------------------------------------- Methods
/////////////////////////////////////////////////////////////////

    /**
     * <p>
     * Thread is stop in natural way, but the partitions finish
     * </p>
     */
    public void pararRun() {
        finalizado = true;
    }

    /**
     * <p>
     * This method is invoqued when a partition is finished
     * </p>
     */
    public void fireIterationCompleted() {
        EducationalRunkeelEvent<EducationalRunKeelTxt> event = new EducationalRunkeelEvent<EducationalRunKeelTxt>(runkeel);
        listener.runKeelIterationCompleted(event);
    }

    /*
     * <p>
     * This method is invoqued when all partitions have finished
     * </p>
     */
    public void fireRunkeelFinished() {
        EducationalRunkeelEvent<EducationalRunKeelTxt> event = new EducationalRunkeelEvent<EducationalRunKeelTxt>(runkeel);
        listener.runKeelFinished(event);
    }
}