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
 * Created on 22-jul-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 *
 */
public class Resultado {

    private int claseEjemplo;
    private int claseRegla;


    /**
     * @param claseEjemplo
     * @param claseRegla
     */
    public Resultado(int claseEjemplo, int claseRegla) {
        this.claseEjemplo = claseEjemplo;
        this.claseRegla = claseRegla;
    }

    /**
     * @return Returns the claseEjemplo.
     */
    public int getClaseEjemplo() {
        return this.claseEjemplo;
    }

    /**
     * @param claseEjemplo The claseEjemplo to set.
     */
    public void setClaseEjemplo(int claseEjemplo) {
        this.claseEjemplo = claseEjemplo;
    }

    /**
     * @return Returns the claseRegla.
     */
    public int getClaseRegla() {
        return this.claseRegla;
    }

    /**
     * @param claseRegla The claseRegla to set.
     */
    public void setClaseRegla(int claseRegla) {
        this.claseRegla = claseRegla;
    }

    public String toString() {
        return "ClaseRegla=" + claseRegla + " ClaseEjemplo=" + claseEjemplo;
    }
}

