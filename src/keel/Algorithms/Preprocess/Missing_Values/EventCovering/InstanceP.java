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
 * @author Written by Julián Luengo Martín 14/05/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;
import java.util.*;
import keel.Dataset.*;

/**
 * <p>
 * This class stores an instance with a P(x) value associated
 * </p>
 */
public class InstanceP {
    public Instance inst;
    public double Px;
    public int index;
    /** Creates a new instance of InstanceP */
    public InstanceP() {
        inst = null;
        Px = 0;
    }
    
    /**
     * <p>
     * Creates a new InstanceP with the arguments passed
     * </p>
     * @param i the proper instace (referenced)
     * @param p the p value associated
     * @param in the index of the instance
     */
    public InstanceP(Instance i,double p,int in){
        inst = i;
        Px = p;
        index = in;
    }
}

