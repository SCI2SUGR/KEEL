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
 * Created on 08-feb-2004
 *
 */

/**
 * @author Jesus Alcala Fernandez
 */

package keel.Algorithms.RE_SL_Methods.MamWM;

import java.io.*;
import org.core.*;
import keel.Dataset.*;
import java.util.*;

public class MiDataset {

    public TTABLA[] datos;
    public int n_variables, n_var_estado, n_var_control, long_tabla;
    public int no_cubiertos;
    public TipoIntervalo[] extremos;
    public String fichero;
    public InstanceSet IS;
    public boolean noOutputs;
    public boolean salir;


    // Stores in memory the contents of the data file "f"
    public MiDataset(String f, boolean train) {
        fichero = f;
        IS = new InstanceSet();

        try {
            processModelDataset(f, train);
        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }


    public void processModelDataset(String nfejemplos, boolean train) throws
            IOException {
        int i, j, k;

        try {

            // Load in memory a dataset that contains a regression problem
            IS.readSet(nfejemplos, train);

            // We read the number of instances and variables
            long_tabla = IS.getNumInstances();
            n_var_estado = Attributes.getInputNumAttributes();
            n_var_control = Attributes.getOutputNumAttributes();
            no_cubiertos = long_tabla;

            // Check that there is only one output variable and
            // it is nominal

            if (n_var_control > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
            }

            boolean noOutputs = false;
            if (n_var_control < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
            }

            n_var_control = 1;
            n_variables = n_var_estado + n_var_control;

            // Initialice and fill our own tables
            datos = new TTABLA[long_tabla];

            // Maximum and minimum of inputs/output data
            extremos = new TipoIntervalo[n_variables];
            for (i = 0; i < n_variables; i++) {
                extremos[i] = new TipoIntervalo();
            }

            /*check if there aren't continous attributes*/
            /*if (Attributes.hasNominalAttributes()) {
                System.err.println("MamWM can only handle real attributes.");
                salir = true;
            } else {*/
                salir = false;
                // All values are casted into double/integer
                for (i = 0, k = 0; i < long_tabla; i++) {
                    Instance inst = IS.getInstance(i);

                    if (inst.existsAnyMissingValue() == true) {
                        System.out.println(
                                "This algorithm can not process missing values");
                        System.out.println(
                                "This algorithm don't use the instance " +
                                (i + 1) +
                                ". You have to apply before a preprocess method");
                    } else {
                        datos[k] = new TTABLA(n_variables);
                        for (j = 0; j < n_var_estado; j++) {
                            datos[k].ejemplo[j] = IS.getInputNumericValue(i, j);
                            if (datos[k].ejemplo[j] > extremos[j].max || k == 0) {
                                extremos[j].max = datos[k].ejemplo[j];
                            }
                            if (datos[k].ejemplo[j] < extremos[j].min || k == 0) {
                                extremos[j].min = datos[k].ejemplo[j];
                            }
                        }

                        if (noOutputs) {
                            datos[k].ejemplo[j] = 0;
                        } else {
                            datos[k].ejemplo[j] = IS.getOutputNumericValue(i, 0);
                        }
                        if (datos[k].ejemplo[j] > extremos[j].max) {
                            extremos[j].max = datos[k].ejemplo[j];
                        }
                        if (datos[k].ejemplo[j] < extremos[j].min) {
                            extremos[j].min = datos[k].ejemplo[j];
                        }
                        k++;
                    }
                }

                long_tabla = k;
            //}
        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }

    // It returns the header
    public String getCabecera() {
        return (IS.getHeader());
    }
}

