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

package keel.Algorithms.PSO_Learning.LDWPSO;

/**
 * <p>Title: Crono</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 *
 * @author Jose A. Saez Munoz
 */

public class Crono{ 

	private Calendar inicio;
    private Calendar fin;
    private Calendar diferencia;



    //*********************************************************************
    //***************** Constructor ***************************************
    //*********************************************************************

    public Crono(){
    }


    //*********************************************************************
    //***************** Initialization and stop ***************************
    //*********************************************************************

    public void inicializa() { 
        inicio = new GregorianCalendar();
     }

    
    public void fin(){

        fin = new GregorianCalendar();
        long diff = fin.getTimeInMillis() - inicio.getTimeInMillis();
        diferencia = new GregorianCalendar();
        diferencia.setTimeInMillis(diff);
    }


    //*********************************************************************
    //***************** Print total time **********************************
    //*********************************************************************

   public String tiempoTotal(){
	   
	   String tpo="";
       tpo=	diferencia.get(Calendar.MINUTE) + " min. " +
       		diferencia.get(Calendar.SECOND) + " seg. " + diferencia.get(Calendar.MILLISECOND) + " miliseg.";  
       return tpo;
   }

}
