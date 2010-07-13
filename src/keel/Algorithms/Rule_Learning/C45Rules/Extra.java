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
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 12/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45Rules;

class Extra {
/**
 * <p>
 * Compute the additional errors if the error rate increases to the
 * upper limit of the confidence level.  The coefficient is the
 * square of the number of standard deviations corresponding to the
 * selected confidence level.  (Taken from Documenta Geigy Scientific
 * Tables (Sixth Edition), p185 (with modifications).)
 * [Taken from "C4.5:Programs for Machine Learning", Ross Quinlan,
 * p278-279]
 * </p>
 */
	

 private static double Val[] = {  0,  0.001, 0.005, 0.01, 0.05, 0.10, 0.20, 0.40, 1.00};
 private static double Dev[] = {4.0,  3.09,  2.58,  2.33, 1.65, 1.28, 0.84, 0.25, 0.00};
 static double Coeff=0;

 public static double AddErrs(int N,int e,double CF){

     double Val0, Pr;

     if ( Coeff ==  0 )
     {
         /*  Compute and retain the coefficient value, interpolating from
             the values in Val and Dev  */

         int i;

         i = 0;
         while ( CF > Val[i] ) i++;

         Coeff = Dev[i-1] +
                   (Dev[i] - Dev[i-1]) * (CF - Val[i-1]) /(Val[i] - Val[i-1]);
         Coeff = Coeff * Coeff;
     }

     if ( e < 1E-6 )
     {
         return N * (1 - Math.exp((Math.log(CF)/Math.log(2)) / N));
     }
     else
     if ( e < 0.9999 )
     {
         Val0 = N * (1 - Math.exp((Math.log(CF)/Math.log(2)) / N));
         return Val0 + e * (AddErrs(N, 1,CF) - Val0);
     }
     else
     if ( e + 0.5 >= N )
     {
         return 0.67 * (N - e);
     }
     else
     {
         Pr = (e + 0.5 + Coeff/2
                 + Math.sqrt(Coeff * ((e + 0.5) * (1 - (e + 0.5)/N) + Coeff/4)) )
              / (N + Coeff);
         return (N * Pr - e);
     }
 }

}
