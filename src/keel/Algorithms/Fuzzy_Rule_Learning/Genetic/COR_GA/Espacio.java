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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.COR_GA;

import java.util.ArrayList;

/**
 * <p>Title: Space</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author A. Fernandez
 * @version 1.0
 */
public class Espacio {

    myDataset datos;
    BaseD baseDatos;
    int agrupa_ejem, consec_candi;
    ArrayList<SubEspacio> subEspacio;
    int pos_var_salida;

    public Espacio(int agrupa_ejem, int consec_candi, myDataset datos,
                   BaseD baseDatos) {
        this.datos = datos;
        this.baseDatos = baseDatos;
        this.agrupa_ejem = agrupa_ejem;
        this.consec_candi = consec_candi;
        subEspacio = new ArrayList<SubEspacio>();
        pos_var_salida = datos.getnInputs();
    }

    private void CompruebaEjemplos(int[] Antecedente)
    /* We check if the combination of antecedents Antecedente, has any positive example. If so, 
    it is store in the Pob_Reglas structure */
    {
        int i, j, n_ejemplos = 0;
        double tmp;
        /* We iterate the training set to find a positive example for the rule */
        boolean entrar = false;
        for (i = 0; i < datos.getnData(); i++) {
            //System.err.println("");
            if (agrupa_ejem == 1) {
                /******* EXAPLES(WM) ****************/
                double max;
                int k, mejor_cons = 0;
                for (j = 0; j < pos_var_salida; j++) {
                    max = 0.0;
                    double[] example = datos.getExample(i);
                    for (k = 0; k < baseDatos.getnLabels(j); k++) {
                        tmp = BaseR.Fuzzifica(example[j],
                                              baseDatos.getParticion(j, k));
                        //System.err.println("cubrimiento -> "+tmp);
                        if (tmp > max) {
                            max = tmp;
                            mejor_cons = k;
                        }
                    }
                    if (mejor_cons != Antecedente[j]) {
                        break;
                    }
                }
                if (j == datos.getnInputs()) { //valor clase
                    n_ejemplos++;
                    entrar = true;
                }
            } else {
                /******* GRID(CH) ****************/
                if (baseDatos.AntecedenteCubreEjemplo(Antecedente,
                        datos.getExample(i)) > 0.0) {
                    entrar = true;
                    n_ejemplos++;
                }
            }
        }
        /* for */

        /* If the combination of antecedents has some examples, we store it in the Pob_Reglas set */
        if (entrar) { /* there are examples */
            //System.err.println("Ejemplos -> "+n_ejemplos);
            SubEspacio s = new SubEspacio(Antecedente, n_ejemplos);
            subEspacio.add(s);
        }
    }

    void RecorreAntecedentes(int[] Regla_act, int pos)
    /* Iterates all the possible antecedent combinations to obtain the set of them that
    have some positive examples. The algorithm is based on a backtraking technique to perform
    an exhaustive iteration for all the possible antecedent combinations. */
    {
        if (pos == pos_var_salida) {
            CompruebaEjemplos(Regla_act);
        } else {
            for (Regla_act[pos] = 0; Regla_act[pos] < baseDatos.getnLabels(pos);
                                  Regla_act[pos]++) {
                RecorreAntecedentes(Regla_act, pos + 1);
            }
        }
    }

    public void generate()
    /* It generates the set of antecedent combinations that covers the training set */
    {
        int[] Regla_act = new int[datos.getnVars()];
        RecorreAntecedentes(Regla_act, 0);
    }

    public void calculaConsecuentes() {
        double mejor, cubrimiento, tmp;
        int mejor_cons;
        Integer pos = new Integer(0);

        mejor = cubrimiento = 0.0;
        for (int i = 0; i < subEspacio.size(); i++) {
            for (int j = 0; j < datos.getnData(); j++) {

                if (agrupa_ejem == 1) {
                    /******* EXAMPLES (WM) *****************/
                    double max = tmp = 0.0;
                    int mejor_subespacio = 0;

                    for (int k = 0; k < subEspacio.size(); k++) {
                        tmp = baseDatos.AntecedenteCubreEjemplo(this.subEspacio.
                                get(k).
                                getAntecedente(), datos.getExample(j));
                        if (tmp > max) {
                            max = tmp;
                            mejor_subespacio = k;
                        }
                    }
                    if (mejor_subespacio == i) {
                        cubrimiento = max;
                    } else {
                        cubrimiento = 0.0;
                    }
                } else {
                    /******* GRID(CH) ****************/
                    cubrimiento = baseDatos.AntecedenteCubreEjemplo(this.
                            subEspacio.get(i).
                            getAntecedente(), datos.getExample(j));
                }
                /* If j is a positive example in the i-th subspace */
                if (cubrimiento > 0.0) {

                    if (consec_candi == 1) {
                        /******* EXAMPLES (WM) ****************/
                        // Selection of the best consequent for the j-th example
                        mejor_cons = 0;
                        mejor = 0.0;
                        for (int k = 0;
                                     k < baseDatos.getnLabels(datos.getnInputs());
                                     k++) {
                            tmp = Math.min(cubrimiento,
                                           BaseR.Fuzzifica(datos.
                                    getOutputAsReal(j),
                                    baseDatos.getParticion(pos_var_salida, k)));
                            if (tmp > mejor) {
                                mejor_cons = k;
                                mejor = tmp;
                            }
                        }

                        // We check if the consequent is already in the consequent list for the i-th subspace
                        // If not, we add it
                        if (!subEspacio.get(i).estaConsecuente(mejor_cons, pos)) {
                            subEspacio.get(i).incluir(mejor_cons, mejor);
                        }
                    } else {
                        /******* GRID(CH) ****************/
                        for (int k = 0; k < baseDatos.getnLabels(pos_var_salida);
                                     k++) {
                            tmp = BaseR.Fuzzifica(datos.getOutputAsReal(j),
                                                  baseDatos.getParticion(
                                    pos_var_salida, k));

                            if (tmp != 0.0) {
                                // We check if the consequent is already in the consequent list for the i-th subspace
                                // If not, we add it
                                if (!subEspacio.get(i).estaConsecuente(k, pos)) {
                                    subEspacio.get(i).incluir(k, tmp);
                                } else
                                if (subEspacio.get(i).getGrado(pos.intValue()) >
                                    tmp) {
                                    subEspacio.get(i).setGrado(pos.intValue(),
                                            tmp);
                                }
                            }
                        }
                    }
                }
                /* if it is a positive example */
            }//we iterate all examples 
        } //Rules

    }

    public int size() {
        return subEspacio.size();
    }

    public SubEspacio get(int pos) {
        return subEspacio.get(pos);
    }

    public int numConsecuentes(int pos) {
        return subEspacio.get(pos).numConsecuentes();
    }

    public void incluirBorrado() {
        for (int i = 0; i < this.size(); i++) {
            subEspacio.get(i).ponConsecuente( -1);
        }
    }

}

