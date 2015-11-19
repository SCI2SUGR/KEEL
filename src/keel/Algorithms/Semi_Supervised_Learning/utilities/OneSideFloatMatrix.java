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

package keel.Algorithms.Semi_Supervised_Learning.utilities;

import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import org.core.*;
import java.util.*;

/**
 * Implements a one side float matrix.
 * @author diegoj
 * @param <IndexType> Indeces type.
 */
public class OneSideFloatMatrix<IndexType>
{
    HashMap<IndexType,HashMap<IndexType, Double>> matrix;
    
    /**
     * Default constructor. The HashMap matrix will be initialized
     */
    public OneSideFloatMatrix()
    {
        matrix = new HashMap<IndexType,HashMap<IndexType, Double>>();
    }
    
    /**
     * Initializes the HashMap matrix with indeces given.
     * @param origin rows indeces.
     * @param destiny colums indeces.
     */
    public OneSideFloatMatrix(ArrayList<IndexType>origin, ArrayList<IndexType>destiny)
    {
        for(IndexType o : origin)
            for(IndexType d : destiny)
                if(o != d)//testing references
                {
                    matrix.put(o, new HashMap<IndexType, Double>());
                    NumericFunction f = new NumericFunction<IndexType>(o,d);
                    matrix.get(o).put(d, f.make());
                }
    }
    
    /**
     * Adds a new index (row) to the matrix.
     * @param a the index of the row added.
     */
    public void add(IndexType a)
    {
        matrix.put(a, new HashMap<IndexType, Double>());
    }

}
