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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.LVQ;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * LVQ2.1 algorithm calling.
 * @author diegoj
 */
public class LVQ2_1Algorithm extends PrototypeGenerationAlgorithm<LVQ2_1>
{
    /**
     * Builds a new LVQ2.1 object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQ2_1 buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQ2_1(train, params);    
    }
    
    /**
     * Main method. Executes LVQ2.1 method.
     * @param args Console arguments of the method.
     */      
    public static void main(String args[])
    {
        LVQ2_1Algorithm algo = new LVQ2_1Algorithm();
        algo.execute(args);
    }
}

