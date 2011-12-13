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

package keel.Algorithms.Discretizers.CADD_Discretizer;

import java.util.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


/**
 * <p>
 * 
 * <p>
 * This class implements the CADD discretizer.
 * </p>
 * 
 * @author Written by Salvador García (University of Granada - Granada) 04/01/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 * </p>
 */
public class CADDDiscretizer extends Discretizer {

	double BIGX = 20.0;
	double confidenceThreshold;
	int numIntervals;

	/**
	* Builder
	* @param _conf  Confidence threshold
	* @param _nint  Number of intervals
	*/
	public CADDDiscretizer (double _conf, int _nint) {
		confidenceThreshold=_conf;
		numIntervals = _nint;
	}

	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end) {
		
		int numInt;
		int sumaAbajo[], sumaDerecha[], total[], quanta[][];
		int ordenados[];
		double fitness, mejorFitness;
		boolean parar = false;
		double partialRCA;
		double test;
		
		numInt = (end - begin + 1) / (3 * Parameters.numClasses);
		if (numIntervals > 0) {
			if (numInt < numIntervals && numIntervals <= (end - begin + 1)) {
				numInt = numIntervals;
			}
		} 
		
		double quota = (end - begin + 1) / (double) numInt;
		double dBound = 0.0;
		int i, j;
		int oldBound = 0;
		boolean saCabo = false;

		Vector <Double> cp = new Vector <Double>();
		Vector <Double> cpTmp;
		Vector <Double> mejorCP;
		
		/*First step: Uniform Frequency discretizer with fixed num. intervals*/

		for (i=0; i<numInt - 1 && !saCabo; i++) {
			dBound += quota;
			int iBound = (int) Math.round(dBound);
			if (iBound <= oldBound)
				continue;
			if (realValues[attribute][values[iBound-1]] != realValues[attribute][values[iBound]]) {
				double cutPoint=realValues[attribute][values[iBound-1]];
				cp.addElement(new Double(cutPoint));
			} else {
				double val = realValues[attribute][values[iBound]];
				int numFW = 1;
				while (iBound + numFW <= end && realValues[attribute][values[iBound + numFW]] == val) numFW++;
				if (iBound + numFW > end) numFW = end - begin + 2;
				int numBW = 1;
				while (iBound - numBW > oldBound && realValues[attribute][values[iBound - numBW]] == val) numBW++;
				if (iBound - numBW == oldBound) numBW = end - begin + 2;

				if (numFW < numBW) {
					iBound += numFW;
				} else if (numBW < numFW) {
					iBound -= numBW;
				} else {
					if (numFW == end - begin + 2) {
						saCabo = true;
					}
					if (Rand.getReal() < 0.5) {
						iBound += numFW;
					} else {
						iBound -= numBW;
						iBound++;
					}
				}
				if (!saCabo) {
					double cutPoint = realValues[attribute][values[iBound-1]];
					cp.addElement(new Double(cutPoint));
				}
			}
			oldBound=iBound;
		}
		
		quanta = new int[Parameters.numClasses][cp.size()+1];
		sumaAbajo = new int[cp.size()+1];
		sumaDerecha = new int[Parameters.numClasses];
		total = new int[1];
		
		ordenados = new int[end - begin + 1];
		for (i=begin, j=0; i<=end; i++, j++) {
			ordenados[j] = values[i];
		}
		
		mejorCP = new Vector <Double>(cp);
		
		/*Second step: Local Search changing the cut points*/
		while (!parar) {
			construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cp, ordenados, attribute);
			mejorFitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
			parar = true;
			for (i=0; i<cp.size(); i++) {
				cpTmp = cambiaIntervalo (cp, ordenados, attribute, i, false);
				construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cpTmp, ordenados, attribute);
				fitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
				if (fitness > mejorFitness) {
					mejorFitness = fitness;
					mejorCP = new Vector <Double>(cpTmp);
					parar = false;
				}

				cpTmp = cambiaIntervalo (cp, ordenados, attribute, i, true);
				construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cpTmp, ordenados, attribute);
				fitness = computeFitness(quanta, sumaAbajo, sumaDerecha, total[0]);
				if (fitness > mejorFitness) {
					mejorFitness = fitness;
					mejorCP = new Vector <Double>(cpTmp);
					parar = false;
				}
			}
			cp = new Vector <Double>(mejorCP);
		}
		
		for (i=1; i<cp.size(); i++) {
			if (cp.elementAt(i-1).doubleValue() >= cp.elementAt(i).doubleValue()) {
				cp.remove(i);
				i--;
			}
		}
		
		/*Third step: remove intervals which are statistically independent*/
		parar = false;
		while (!parar && cp.size() > (numIntervals-1)) {
			parar = true;
			construyeQuanta(quanta, sumaAbajo, sumaDerecha, total, cp, ordenados, attribute);
			for (i=0; i<cp.size() && parar; i++) {
				 partialRCA = computeRCA(quanta, sumaAbajo, i);
				 test = computeTest(quanta, sumaAbajo, i);
				 if (partialRCA >= test) {					 
					 parar = false;
					 cp.remove(i);
				 }
			}
		}

		return cp;
	}
	
	private void construyeQuanta (int quanta[][], int sumaAbajo[], int sumaDerecha[], int total[], Vector <Double> cutPoints, int ordenados[], int attribute) {
		
		int i, j;
		int intervalo = 0;
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				quanta[i][j] = 0;
				sumaAbajo[j] = 0;
			}
			sumaDerecha[i] = 0;
		}
		total[0] = 0;
		
		for (i=0; i<ordenados.length; i++) {
			if (intervalo < cutPoints.size()) {
				if (realValues[attribute][ordenados[i]] >= cutPoints.elementAt(intervalo)) {
					intervalo++;
				}
			} else {
				intervalo = cutPoints.size();
			}
			quanta[classOfInstances[ordenados[i]]][intervalo]++;
		}
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				sumaAbajo[j] += quanta[i][j];
				sumaDerecha[i] += quanta[i][j];
				total[0] += quanta[i][j];
			}
		}		
	}	
	
	private double computeFitness (int quanta[][], int sumaAbajo[], int sumaDerecha[], int total) {
		
		int i, j;
		double ICA = 0;
		double HCA = 0;
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				if (quanta[i][j] > 0)
					ICA += (double)quanta[i][j]/(double)total * log2(((double)quanta[i][j] / (double)total) / (((double)sumaDerecha[i] / (double)total) * ((double)sumaAbajo[j] / (double)total)));
			}
		}
		
		for (i=0; i<quanta.length; i++) {
			for (j=0; j<quanta[i].length; j++) {
				if (quanta[i][j] > 0)
					HCA += (double)quanta[i][j]/(double)total * log2((double)quanta[i][j]/(double)total);
			}
		}
		
		HCA = -1.0 * HCA;
		
		return ICA / HCA;		
	}

	private double computeRCA (int quanta[][], int sumaAbajo[], int intervalo) {
		
		int i, j;
		double ICA = 0;
		double HCA = 0;
		int total;
		int sumaDerecha[] = new int[Parameters.numClasses];
		
		total = sumaAbajo[intervalo] + sumaAbajo[intervalo+1];
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				sumaDerecha[i] += quanta[i][j];
			}
		}		
	
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					ICA += (double)quanta[i][j]/(double)total * log2(((double)quanta[i][j] / (double)total) / (((double)sumaDerecha[i] / (double)total) * ((double)sumaAbajo[j] / (double)total)));
			}
		}
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					HCA += (double)quanta[i][j]/(double)total * log2((double)quanta[i][j]/(double)total);
			}
		}
		
		HCA = -1.0 * HCA;
		
		return ICA / HCA;		
	}

	private double computeTest (int quanta[][], int sumaAbajo[], int intervalo) {
		
		int i, j;
		double HCA = 0;
		int total;
		int sumaDerecha[] = new int[Parameters.numClasses];
		
		total = sumaAbajo[intervalo] + sumaAbajo[intervalo+1];
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				sumaDerecha[i] += quanta[i][j];
			}
		}		
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					HCA += (double)quanta[i][j]/(double)total * log2((double)quanta[i][j]/(double)total);
			}
		}
		
		HCA = -1.0 * HCA;
		
		return critchi(confidenceThreshold, Parameters.numClasses-1) / (2 * total * HCA);		
	}
	
	private Vector <Double> cambiaIntervalo (Vector <Double> cp, int ordenados[], int attribute, int intervalo, boolean sentido) {
		
		Vector <Double> res = new Vector <Double>();
		int i, j;
		double v;
		
		for (i=0; i<cp.size(); i++) {
			if (i == intervalo) {
				v = cp.elementAt(i);
				for (j=0; j<ordenados.length && realValues[attribute][ordenados[j]] < v; j++);
				if (sentido) {
					for ( ;j<ordenados.length && realValues[attribute][ordenados[j]] == v; j++);
					if (j == ordenados.length) {
						j--;
					}
				} else {
					if (j>0) {
						j--;
					}
				}
				res.addElement(realValues[attribute][ordenados[j]]);
			} else {
				res.addElement(cp.elementAt(i));
			}
		}
		
		return res;
	} 
	
	private double log2 (double x) {		
		return Math.log(x) / Math.log(2);
	}
	
    private double critchi(double p, double df) {
        double CHI_EPSILON = 0.000001;   /* Accuracy of critchi approximation */
        double CHI_MAX = 99999.0;        /* Maximum chi-square value */
        double minchisq = 0.0;
        double maxchisq = CHI_MAX;
        double chisqval;
        
        if (p <= 0.0) {
            return maxchisq;
        } else {
            if (p >= 1.0) {
                return 0.0;
            }
        }
        
        chisqval = df / Math.sqrt(p);    /* fair first value */
        while ((maxchisq - minchisq) > CHI_EPSILON) {
            if (pochisq(chisqval, df) < p) {
                maxchisq = chisqval;
            } else {
                minchisq = chisqval;
            }
            chisqval = (maxchisq + minchisq) * 0.5;
        }
        return chisqval;
    }
    
    private double pochisq(double x, double df) {
        double a, y=0.0, s;
        double e, c, z;
        boolean even;                     /* True if df is an even number */

        double LOG_SQRT_PI = 0.5723649429247000870717135; /* log(sqrt(pi)) */
        double I_SQRT_PI = 0.5641895835477562869480795;   /* 1 / sqrt(pi) */
        
        if (x <= 0.0 || df < 1) {
            return 1.0;
        }
        
        a = 0.5 * x;
        even = !(df % 1 == 1);
        if (df > 1) {
            y = ex(-a);
        }
        s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
        if (df > 2) {
            x = 0.5 * (df - 1.0);
            z = (even ? 1.0 : 0.5);
            if (a > BIGX) {
                e = (even ? 0.0 : LOG_SQRT_PI);
                c = Math.log(a);
                while (z <= x) {
                    e = Math.log(z) + e;
                    s += ex(c * z - a - e);
                    z += 1.0;
                }
                return s;
            } else {
                e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
                c = 0.0;
                while (z <= x) {
                    e = e * (a / z);
                    c = c + e;
                    z += 1.0;
                }
                return c * y + s;
            }
        } else {
            return s;
        }
    }
    
    private double ex(double x) {
        return (x < -BIGX) ? 0.0 : Math.exp(x);
    }   
    
    private double poz(double z) {
        double y, x, w;
        double Z_MAX = 6.0;              /* Maximum meaningful z value */
        
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y >= (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                         - 0.001075204047) * w + 0.005198775019) * w
                         - 0.019198292004) * w + 0.059054035642) * w
                         - 0.151968751364) * w + 0.319152932694) * w
                         - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                               + 0.000152529290) * y - 0.000019538132) * y
                               - 0.000676904986) * y + 0.001390604284) * y
                               - 0.000794620820) * y - 0.002034254874) * y
                               + 0.006549791214) * y - 0.010557625006) * y
                               + 0.011630447319) * y - 0.009279453341) * y
                               + 0.005353579108) * y - 0.002141268741) * y
                               + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }
    
	
}

