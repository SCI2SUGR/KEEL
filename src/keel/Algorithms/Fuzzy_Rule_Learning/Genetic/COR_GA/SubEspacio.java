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

/**
 * <p>Title: Subespace</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author A. Fernandez
 * @version 1.0
 */
public class SubEspacio {

    int[] ant; // number of labels of the antecedents of the subspace
    int[] cons; /* number of the labels of the candidates of the consequent*/
    double[] grado; /* degree of each consequent */
    int n_cons; /* number of possible consequent for this subspace */

    public SubEspacio(int[] antecedente, int n_ejemplos) {
        ant = new int[antecedente.length-1];
        for (int i = 0; i < ant.length; i++) {
            ant[i] = antecedente[i];
        }
        cons = new int[0];
        grado = new double[0];
        n_cons = 0;
    }

    public int[] getAntecedente() {
        return ant;
    }

    public int getAntecedente(int pos){
        return ant[pos];
    }

    public boolean estaConsecuente(int mejor_cons, Integer pos) {
        // We check if the consequent is already in the list of consequents for the i-th subspace
        for (int k = 0; k < cons.length; k++) {
            if (mejor_cons == cons[k]) {
                pos = new Integer(k);
                return true;
            }
        }
        pos = new Integer(0);
        return false;

    }

    public void incluir (int consecuente, double grado){
        ponConsecuente(consecuente);
        ponGrado(grado);
        //this.cons[n_cons] = consecuente;
        //this.grado[n_cons] = grado;
        //n_cons++;
    }

    public void ponConsecuente(int c){
        int [] aux = new int[cons.length];
        for (int i = 0; i < cons.length; i++){
            aux[i] = cons[i];
        }
        cons = new int[cons.length+1];
        for (int i = 0; i < aux.length; i++){
            cons[i] = aux[i];
        }
        cons[cons.length-1] = c;
        n_cons++;
    }

    private void ponGrado(double g){
        double [] aux = new double[grado.length];
        for (int i = 0; i < grado.length; i++){
            aux[i] = grado[i];
        }
        grado = new double[grado.length+1];
        for (int i = 0; i < aux.length; i++){
            grado[i] = aux[i];
        }
        grado[grado.length-1] = g;
    }

    public int getConsecuente(int pos){
        return cons[pos];
    }

    public void setGrado(int pos, double g){
        grado[pos] = g;
    }

    public double getGrado(int pos){
        return grado[pos];
    }

    public int numConsecuentes(){
        return cons.length;
    }


}

