/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 07/07/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.experiments;

import keel.RunKeelTxtDocente.EducationalRunKeelTxt;

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