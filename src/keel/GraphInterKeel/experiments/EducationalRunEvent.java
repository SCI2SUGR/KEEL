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
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.experiments;

import java.util.EventObject;

public class EducationalRunEvent<A extends EducationalRun> extends EventObject {

    /**
     * <p>
     * Events for EjecucionDocente (GUI)
     * </p>
     */

    /////////////////////////////////////////////////////////////////
    // --------------------------------------- Serialization constant
    /////////////////////////////////////////////////////////////////
    /**
     * Generado por Eclipse
     */
    private static final long serialVersionUID = 1L;

    /////////////////////////////////////////////////////////////////
    // -------------------------------------------------- Propiedades
    /////////////////////////////////////////////////////////////////
    protected A educationalRun;
    protected Exception exception;

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------ Constructores
    /////////////////////////////////////////////////////////////////
    public EducationalRunEvent(A educationalRun) {
        this(educationalRun, null);
    }

    public EducationalRunEvent(A educationalRun, Exception exception) {
        super(educationalRun);
        this.educationalRun = educationalRun;
        this.exception = exception;
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------------ Metodos
    /////////////////////////////////////////////////////////////////
    public final A getEducationlRun() {
        return educationalRun;
    }

    public final Exception getException() {
        return exception;
    }
}


