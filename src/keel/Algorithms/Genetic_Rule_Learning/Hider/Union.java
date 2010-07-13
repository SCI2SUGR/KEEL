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
 * Created on 13-abr-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 */
public class Union {
    private Corte corte = new Corte();
    private double bondad = 0;
    private int clase = -1;

    /**
     * Empty constructor
     *
     */
    public Union() {
        this.corte = new Corte();
        this.bondad = 0;
        this.clase = -1;
    }

    /**
     * Constructor
     * @param corte punto de corte
     * @param bondad valor de bondad
     * @param clase clase a la que pertence
     */
    public Union(Corte corte, double bondad, int clase) {
        this.corte = corte;
        this.bondad = bondad;
        this.clase = clase;
    }

    /**
     * @return Returns the bondad.
     */
    public double getBondad() {
        return this.bondad;
    }

    /**
     * @param bondad The bondad to set.
     */
    public void setBondad(double bondad) {
        this.bondad = bondad;
    }

    /**
     * @return Returns the corte.
     */
    public Corte getCorte() {
        return this.corte;
    }

    /**
     * @param corte The corte to set.
     */
    public void setCorte(Corte corte) {
        this.corte = corte;
    }

    public String toString() {
        String s = "";
        s += "Corte: " + this.corte.getCorte() + " ";
        s += "Bondad: " + this.bondad + " ";
        s += "Clase: " + this.clase;

        return s;
    }

    /**
     * @return Returns the clase.
     */
    public int getClase() {
        return this.clase;
    }

    /**
     * @param clase The clase to set.
     */
    public void setClase(int clase) {
        this.clase = clase;
    }
}

