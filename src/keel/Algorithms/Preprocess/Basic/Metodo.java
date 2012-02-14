/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
 * 
 * File: Metodo.java
 * 
 * An auxiliary class to initialize Instance Selection algorithms
 * 
 * @author Written by Salvador Garc?a (University of Granada) 20/07/2004 
 * @author Modified by Isaac Triguero (University of Granada) 22/06/2010 
 * @author Modified by Victoria Lopez (University of Granada) 21/09/2010 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Basic;

import keel.Dataset.*;

import java.util.StringTokenizer;

public class Metodo {

	  /*Path and names of I/O files*/
	  protected String ficheroTraining;
	  protected String ficheroValidation;
	  protected String ficheroTest;
	  protected String ficheroSalida[];

	  /*Data Structures*/
	  protected InstanceSet training;
	  protected InstanceSet test;
	  protected Attribute entradas[];
	  protected Attribute salida;
	  protected int nEntradas;
	  protected String relation;

	  /*Data Matrix*/
	  protected double datosTrain[][];
	  protected int clasesTrain[];
          
          
          	  /*Data Matrix*/
	  protected double datosTest[][];
	  protected double realTest[][];
	  protected int clasesTest[];

	  /*Extra*/
	  protected boolean nulosTrain[][];
	  protected int nominalTrain[][];
	  protected double realTrain[][];
	  
	  protected boolean distanceEu;
	  
	  static protected double nominalDistance[][][];
	  static protected double stdDev[];
	  
  	/**
	 * Default builder
	 */
	public Metodo () {} //end-method

	/**
	 * Builder. Creates the basic structures of the algorithm
	 *
	 * @param ficheroScript Configuration script
	 */
	public Metodo (String ficheroScript) {

		int nClases, i, j, l, m, n;
		double VDM;
		int Naxc, Nax, Nayc, Nay;
		double media, SD;	
		
		distanceEu = false;
		  
		/*Read of the script file*/
		leerConfiguracion (ficheroScript);

		/*Read of data files*/
		try {
		  training = new InstanceSet();
		  training.readSet(ficheroTraining, true);



	
		} catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}

		try {
		  test = new InstanceSet();
		  test.readSet(ficheroTest, false);
                     
                  
        	} catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}

               
             
                try{
                    normalizar();
                }catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}
                
                /*Previous computation for HVDM distance*/
            if (distanceEu == false) {    	
                stdDev = new double[Attributes.getInputNumAttributes()];
                nominalDistance = new double[Attributes.getInputNumAttributes()][][];
                        nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
                     
                        
                for (i=0; i<nominalDistance.length; i++) {
                        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
                                nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];
                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 
                                        nominalDistance[i][j][j] = 0.0;

                                }
                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
                                        for (l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
                                                VDM = 0.0;
                                                Nax = Nay = 0;
                                                for (m=0; m<training.getNumInstances(); m++) {
                                                        if (nominalTrain[m][i] == j) {
                                                                Nax++;

                                                        }
                                                        if (nominalTrain[m][i] == l) {
                                                                Nay++;


                                                        }
                                                }
                                                for (m=0; m<nClases; m++) {
                                                        Naxc = Nayc = 0;
                                                        for (n=0; n<training.getNumInstances(); n++) {
                                                                if (nominalTrain[n][i] == j && clasesTrain[n] == m) {
                                                                        Naxc++;

                                                                }
                                                                if (nominalTrain[n][i] == l && clasesTrain[n] == m) {
                                                                        Nayc++;


                                                                }
                                                        }
                                                        VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));

                                                }
                                                nominalDistance[i][j][l] = Math.sqrt(VDM);
                                                nominalDistance[i][l][j] = Math.sqrt(VDM);



                                        }
                                }
                        } else {
                                media = 0;
                                SD = 0;
                                for (j=0; j<training.getNumInstances(); j++) {
                                        media += realTrain[j][i];
                                        SD += realTrain[j][i]*realTrain[j][i];

                                }
                                media /= (double)realTrain.length;
                                stdDev[i] = Math.sqrt((SD/((double)realTrain.length)) - (media*media));



                        }
                }
            }    

  
	} //end-method
	
	
	public Metodo (String ficheroScript, InstanceSet train) {

		int nClases, i, j, l, m, n;
		double VDM;
		int Naxc, Nax, Nayc, Nay;
		double media, SD;	
		
		distanceEu = false;
		  
		/*Read of the script file*/
		leerConfiguracion (ficheroScript);

		/*Read of data files*/
		try {
		  training = new InstanceSet(train);
	
		} catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}

		try {
		  test = new InstanceSet(train);
         } catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}

          try{
              normalizar();
          }catch (Exception e) {
		  System.err.println(e);
		  System.exit(1);
		}

               
                /*Previous computation for HVDM distance*/
            if (distanceEu == false) {    	
                stdDev = new double[Attributes.getInputNumAttributes()];
                nominalDistance = new double[Attributes.getInputNumAttributes()][][];
                        nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
                        
                for (i=0; i<nominalDistance.length; i++) {
                        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
                                nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];
                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 
                                        nominalDistance[i][j][j] = 0.0;

                                }
                                for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
                                        for (l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
                                                VDM = 0.0;
                                                Nax = Nay = 0;
                                                for (m=0; m<training.getNumInstances(); m++) {
                                                        if (nominalTrain[m][i] == j) {
                                                                Nax++;

                                                        }
                                                        if (nominalTrain[m][i] == l) {
                                                                Nay++;


                                                        }
                                                }
                                                for (m=0; m<nClases; m++) {
                                                        Naxc = Nayc = 0;
                                                        for (n=0; n<training.getNumInstances(); n++) {
                                                                if (nominalTrain[n][i] == j && clasesTrain[n] == m) {
                                                                        Naxc++;

                                                                }
                                                                if (nominalTrain[n][i] == l && clasesTrain[n] == m) {
                                                                        Nayc++;


                                                                }
                                                        }
                                                        VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));

                                                }
                                                nominalDistance[i][j][l] = Math.sqrt(VDM);
                                                nominalDistance[i][l][j] = Math.sqrt(VDM);



                                        }
                                }
                        } else {
                                media = 0;
                                SD = 0;
                                for (j=0; j<training.getNumInstances(); j++) {
                                        media += realTrain[j][i];
                                        SD += realTrain[j][i]*realTrain[j][i];

                                }
                                media /= (double)realTrain.length;
                                stdDev[i] = Math.sqrt((SD/((double)realTrain.length)) - (media*media));



                        }
                }
            }    

           
	} //end-method

	/** 
	 * This function builds the data matrix for reference data and normalizes inputs values
	 */	
	protected void normalizar () throws CheckException {

		int i, j, k;
		Instance temp;
		double caja[];
		StringTokenizer tokens;
		boolean nulls[];

		/*Check if dataset corresponding with a classification problem*/

		if (Attributes.getOutputNumAttributes() < 1) {
		  throw new CheckException ("This dataset haven?t outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
		  throw new CheckException ("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
		  throw new CheckException ("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
		}


      
        
		entradas = Attributes.getInputAttributes();
		salida = Attributes.getOutputAttribute(0);
		nEntradas = Attributes.getInputNumAttributes();
		tokens = new StringTokenizer (training.getHeader()," \n\r");
		tokens.nextToken();
		relation = tokens.nextToken();

		  
		
		datosTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];
		clasesTrain = new int[training.getNumInstances()];
		caja = new double[1];

		nulosTrain = new boolean[training.getNumInstances()][Attributes.getInputNumAttributes()];
		nominalTrain = new int[training.getNumInstances()][Attributes.getInputNumAttributes()];
		realTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];

	
		for (i=0; i<training.getNumInstances(); i++) {
			temp = training.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTrain[i] = training.getInstance(i).getAllInputValues();
			for (j=0; j<nulls.length; j++)
				if (nulls[j]) {
					datosTrain[i][j]=0.0;
					nulosTrain[i][j] = true;
				}
			//caja = training.getInstance(i).getOutputRealValues(0)  //.getAllOutputValues();
			caja[0]= training.getInstance(i).getOutputNominalValuesInt(0);
			clasesTrain[i] = (int) caja[0];
			for (k = 0; k < datosTrain[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					nominalTrain[i][k] = (int)datosTrain[i][k]; 
					datosTrain[i][k] /= Attributes.getInputAttribute(k).
					getNominalValuesList().size() - 1;
				} else {
					realTrain[i][k] = datosTrain[i][k];
					datosTrain[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
					datosTrain[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() -
					Attributes.getInputAttribute(k).getMinAttribute();
					if (Double.isNaN(datosTrain[i][k])){
						datosTrain[i][k] = realTrain[i][k];
			    }
				}
			}
		} 

		
        datosTest = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];
        realTest = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];
		clasesTest = new int[test.getNumInstances()];
        caja = new double[1];
    
        for (i=0; i<test.getNumInstances(); i++) {
			temp = test.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTest[i] = test.getInstance(i).getAllInputValues().clone();
			
			for (k = 0; k < datosTest[i].length; k++) {
				
				if (Attributes.getInputAttribute(k).getType() != Attribute.NOMINAL) {
					realTest[i][k] = datosTest[i][k];
					datosTest[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
					datosTest[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();
					if (Double.isNaN(datosTest[i][k])){
						datosTest[i][k] = realTest[i][k];
					}
				}
		    }
			
			for (j=0; j<nulls.length; j++)
				if (nulls[j]) {
					datosTest[i][j]=0.0;
				}
			caja = test.getInstance(i).getAllOutputValues();
			clasesTest[i] = (int) caja[0];
		} 
                		
	} //end-method

	/** 
	 * Reads the parameters of the algorithm. 
	 * Must be implemented in the subclass.
	 * 
	 * @param ficheroScript Configuration script
	 * 
	 */
	public void leerConfiguracion (String ficheroScript) {

	} //end-method

} //end-class
