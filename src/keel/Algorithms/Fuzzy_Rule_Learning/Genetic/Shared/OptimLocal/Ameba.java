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
 * @author Written by Luciano Sánchez (University of Oviedo) 03/03/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


// Local Optimization package
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;

public class Ameba {
/**
 * <p>
 * Class 	
 * Need: 
 */
	// This algorithm is applied with the crossover operator. 
	// This algorithm is an optimization of Nelder and Mead Simplex.
	
	boolean debug=false;
	
	/**
	 * <p>
	 * </p>
	 * @param f1
	 * @param ctes 
	 * @param max_iter The maximum number of iterations
	 * @return The fitness
	 */
	public double itera(FUN f1, double [] ctes, int max_iter) {
		
		
		int i;
		final double volsimplex=1;  // Initial side for simplex algorithm
		double end_sumdi=0;
		
		double[][]x = new double[ctes.length+1][ctes.length];     // x is the simplex
		double[] xf = new double[ctes.length+1];                  // xf are fitness values
		
		// Initial simples. The last values is the centroid.
		x[x.length-1]=duplicate(ctes);
		xf[x.length-1]=f1.evaluate(ctes);
		
		for (i=0;i<xf.length-1;i++) {  x[i]= duplicate(x[x.length-1]); x[i][i]+=volsimplex; }
		int iter=0; 
		double max,min; int best,worst;
		double []xc;
		double []xr;
		double []xe;
		double []xp;
		double []xi;
		double []xo;
		
		double tmpfit,tmpfit1;
		double fit_o=-1; // Solution fitness
		xo = duplicate(ctes);
		
		boolean compress;
		for (i=0;i<xf.length;i++) xf[i]=f1.evaluate(x[i]);
                
		
		double oldmin;
		do {
			
			compress=false;
			//Best and worst simplex point
			max=xf[0]; min=xf[0]; worst=0; best=0;
			for (i=1;i<xf.length;i++) {
				if (max<=xf[i]) { worst=i; max=xf[i]; }
				if (xf[i]<min) { best=i; min=xf[i]; }
			}
			
			if (debug) {
				System.out.println("Debug ameba=");
				for (i=0;i<xf.length;i++) {
					System.out.println(" fitness="+xf[i]);
				}
			}

			
			// Simplex centroid, worst point excluded
			xc=new double[ctes.length]; 
			for (i=0;i<x.length;i++) if (i!=worst) xc=OPV.sum(xc,x[i]);
			xc=OPV.multiply(1.0/(x.length-1),xc);
			
			// Reflected point
			xr=OPV.sum(xc,OPV.sum(xc,OPV.multiply(-1.0,x[worst]))); 
			tmpfit=f1.evaluate(xr);
			
			// if the reflected point is the best one, you must
                        // calcute the expanded one 
			if (tmpfit<xf[best]) {
				
				xe=OPV.sum(xr,OPV.sum(xr,OPV.multiply(-1.0,xc)));                         
				tmpfit1=f1.evaluate(xe);
				
				// One of both is the solution
				if (tmpfit1<xf[best]) { xo=duplicate(xe); fit_o=tmpfit1; } else { xo=duplicate(xr); fit_o=tmpfit; }
				
			} else { 
			        // Reflected one is not the best
				// The reflected point is not point: Let's count how much points are best of worst than original one.
				
				int mu=0; for (i=0;i<x.length;i++) if (tmpfit<xf[i]) mu++;
				
				// If there are more than one point, the solution is the reflected one
				if (mu>1) { xo=duplicate(xr); fit_o=tmpfit; } 
				else if (mu==1) {
					// There is only point worst than reflected one
					// Partial contraction to exterior
				
					xp=OPV.multiply(0.5,OPV.sum(xc,xr)); 
					tmpfit1=f1.evaluate(xp);
					// If point is better than reflected one, xp is the solution
					if (tmpfit1<tmpfit) { 
						xo=duplicate(xp); fit_o=tmpfit1; 
					} 
					else 
					{
						//Partial contraction to interior 
						xi=OPV.multiply(0.5,OPV.sum(xc,x[worst])); tmpfit1=f1.evaluate(xi);
						
						// It's the solution If the worst is improved
						if (tmpfit1<xf[worst]) 
						{ 
							xo=duplicate(xi);  
							fit_o=tmpfit1; 
						} 
						else compress=true;
						
					}
					
				} else compress=true; 
				
				if (compress) {
					
					// Total contraction: Simplex size reduction 
					xo=duplicate(x[best]); 
					for (i=0;i<x.length;i++) {
						x[i]=OPV.multiply(0.5,OPV.sum(xo,x[i]));
						xf[i]=f1.evaluate(x[i]);
					}
				}        
				
			} 
			
			if (!compress) { x[worst]=duplicate(xo); xf[worst]=fit_o; } 
			
			iter++;
			
			// if fitness has reduced volume, it's so much
			double faverage=0,sumdi=0; 
			for (i=0;i<xf.length;i++) faverage+=xf[i];      
			for (i=0;i<xf.length;i++) sumdi+=Math.abs(xf[i]-faverage);
			if (debug) System.out.println("** It="+iter+" Fitness="+xf[best]+" Suma dif="+sumdi);
			if (sumdi<0.0001) break;
			if (sumdi==end_sumdi) break;
			end_sumdi=sumdi;
		} while(iter<max_iter);
		
		// Solution is searched
		min=xf[0]; best=0;
		for (i=1;i<x.length;i++) { if (xf[i]<min) { best=i; min=xf[i]; } }
		
		// Argument is modified
		for (i=0;i<ctes.length;i++) ctes[i]=x[best][i];
		
		// Fitness is returned
		return min;
		
    }
	
	/**
	 * <p>
	 * The method duplicate a set of double values
	 * </p>
	 * @param x The set of double values
	 * @return The duplicate set
	 */
	
	double[] duplicate(double []x) {
	
		double[] result = new double[x.length];
		for (int i=0;i<x.length;i++) result[i]=x[i];
		return result;
	}
	
	
}

