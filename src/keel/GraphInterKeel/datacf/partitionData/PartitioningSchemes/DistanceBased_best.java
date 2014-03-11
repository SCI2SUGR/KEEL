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
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.GraphInterKeel.datacf.partitionData.PartitioningSchemes;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import org.core.Files;
import org.core.Randomize;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;





/**
 * <p>
 * This class implements a stratified scheme (equal number of examples of each class in each partition) to partition a dataset
 * </p>
 */
public class DistanceBased_best{

	private Instance[] instances;
	private Vector[] partitions;
	private Instance[][] trainPartition;
	private Instance[][] testPartition;
	int nclasses, ninstances, nattributes, numPartitions;
	
	boolean used[];
	int[] numExClass;
	int[] sortedIndex;
	int[] numUsedPerClass;
	
	double[] stdDev;
	double[][][] nominalDistance;
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It reads the training set and creates the partitions
	 * </p>
	 */
	public DistanceBased_best(String source_file, int np){
		
		InstanceSet is = new InstanceSet();

		Attributes.clearAll();
		try {	
			is.readSet(source_file, true);
        }catch(Exception e){
            System.exit(1);
        }
        
        instances = is.getInstances();
        nclasses = Attributes.getOutputAttribute(0).getNumNominalValues();
        nattributes = Attributes.getInputAttributes().length;
        ninstances = instances.length;
        
        numPartitions = np;
        
        calculo_previo_hvdm();
        
        createPartitions();
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates the partitions from the original training set
	 * </p>
	 */
	private void createPartitions(){

       	// 1) to count the number of examples of each class
        numExClass = new int[nclasses];
        Arrays.fill(numExClass, 0);
        for(int i = 0 ; i < ninstances ; i++)
        	numExClass[instances[i].getOutputNominalValuesInt(0)]++;
               
        // 2) to sort the indexes of examples per class
        sortedIndex = new int[ninstances];
        int k = 0;
        for (int i = 0; i < nclasses ; i++)
            for (int j = 0; j < ninstances ; j++)
                if (instances[j].getOutputNominalValuesInt(0) == i)
                	sortedIndex[k++] = j;

        // 3) to shuffle the examples of each class
        
        int tmp;
        k = 0;
        for(int i = 0 ; i < nclasses ; i++){
            for(int j = 0 ; j < numExClass[i] ; j++){
                int randPos = Randomize.Randint(j, numExClass[i]);
                tmp = sortedIndex[j+k];
                sortedIndex[j+k] = sortedIndex[randPos+k];
                sortedIndex[randPos+k] = tmp;
            }
            k += numExClass[i];
        }
        

        // 4) to create the partitions 
        partitions = new Vector[numPartitions];
        for (int i = 0; i < numPartitions; i++)
        	partitions[i] = new Vector();
        
        
        
        // meter instancias en cada particion
        used = new boolean[ninstances];
        Arrays.fill(used, false);
      
    	numUsedPerClass = new int[nclasses];
    	Arrays.fill(numUsedPerClass, 0);
    	
		int num_vecinos = numPartitions-1;
    	int neighbors[] = new int[num_vecinos];
    	        

        for(int cl = 0 ; cl < nclasses ; ++cl){
        	        	

        	while(quedanEjemplos(cl)){
        		
        		//System.out.println("CLASE = " + cl + ", " + Attributes.getOutputAttribute(0).getNominalValue(cl));
            	int instancia =  instanciaNoUsada(cl);
            	//System.out.println("Instancia no usada = "+instancia);
                boolean distance = false; //hvdm distance
            	
                evaluationKNNClass (num_vecinos, instancia, nclasses, distance, neighbors,cl);
            	// meto la instancia no usada en el primer fold y el resto en los otros folds
            	partitions[0].add(new Integer(instancia));
            	used[instancia] = true;
            	numUsedPerClass[cl]++;
            	for (int i = 0; i < num_vecinos && neighbors[i] != -1; i++){
            		//System.out.println(neighbors[i]);
            		partitions[i+1].add(new Integer(neighbors[i]));
            		used[neighbors[i]] = true;
            		numUsedPerClass[cl]++;
            	}
        	}
        	
        }
   
        // 5) create the training and test partitions
        getTrainTest();
        
        //System.out.println("\n\n\nHOLA!!!");
	}
	
//*******************************************************************************************************************************
  	
  	/**
  	 * <p>
  	 * Main method
  	 * </p>
  	 * @param args the command line arguments
  	 */
  	private void getTrainTest(){
  		
  		trainPartition = new Instance[numPartitions][];
  		testPartition = new Instance[numPartitions][];

  		for(int par = 0 ; par < numPartitions ; ++par){
  			
  			// count the number of instances in train number par
  	  		int tam = 0;
  	  		for(int i = 0 ; i < numPartitions ; ++i)
  	  			if(i != par)
  	  				tam += partitions[i].size();
  			
  	  		trainPartition[par] = new Instance[tam];
  	  		testPartition[par] = new Instance[partitions[par].size()];
  	  		
  	  		// create the training partition
  	  		int size = 0;
  	  		for(int i = 0 ; i < numPartitions ; ++i)
  	  			if(i != par){
  	  				for(int j = 0 ; j < partitions[i].size() ; ++j)
  	  					trainPartition[par][size++] = instances[(Integer)partitions[i].get(j)];
  	  			}
  	  		
  	  		// create the test partition
  	  		for(int j = 0 ; j < partitions[par].size() ; ++j)
  	  			testPartition[par][j] = instances[(Integer)partitions[par].get(j)];
  		}

  	}
  	
//*******************************************************************************************************************************

	/**
	 * <p>
	 * It returns the training partition specified
	 * </p>
	 * @param num number of the partition
	 * @return the training partition
	 */
  	public Instance[] getTrainPartition(int num){
  		Instance[] res = new Instance[trainPartition[num].length];
  		for(int i = 0 ; i < res.length ;++i ){
  			res[i] = new Instance(trainPartition[num][i]);
  		}  			
  		return res;
  	}

//*******************************************************************************************************************************
  	
	/**
	 * <p>
	 * It returns the test partition specified
	 * </p>
	 * @param num number of the partition
	 * @return the test partition
	 */
  	public Instance[] getTestPartition(int num){
  		Instance[] res = new Instance[testPartition[num].length];
  		for(int i = 0 ; i < res.length ;++i ){
  			res[i] = new Instance(testPartition[num][i]);
  		}  			
  		return res;
  	}
  	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns all the original instances
	 * </p>
	 * @param the instances
	 */
	public Instance[] getInstances(){
		return instances;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns the indexes of the original instances in all partitions
	 * </p>
	 * @param the indexes of the instances in each partition
	 */
	public Vector[] getPartitions(){
		return partitions;
	}
  	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates the files of each training and test partition
	 * </p>
	 */
  	public void createPartitionFiles(String _carpeta, String _ds){
  		
		String sep = System.getProperty("file.separator");
  		
		Attribute []att = Attributes.getInputAttributes();

  		String header = "";
  		header = "@relation " + Attributes.getRelationName() + "\n";
  		header += Attributes.getInputAttributesHeader();
  		header += Attributes.getOutputAttributesHeader();
  		header += Attributes.getInputHeader() + "\n";
  		header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";
        
        String outputTrain = "", outputTest = "";

        for (int i = 0; i < numPartitions ; i++) {
        	
        	outputTest = header;
        	outputTrain = header;
        	
        	// create test partition-----------------------------
        	for(int j = 0 ; j < testPartition[i].length ; ++j){
        		
        		boolean[] missing = testPartition[i][j].getInputMissingValues();
				String newInstance = "";
				
				for(int ak = 0 ; ak < nattributes ; ak++){
					
					if(missing[ak])
						newInstance += "?";
					
					else{
						if(att[ak].getType() == Attribute.REAL)
							newInstance += testPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.INTEGER)
							newInstance += (int)testPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.NOMINAL)
							newInstance += testPartition[i][j].getInputNominalValues(ak);
					}
					
					newInstance += ", "; 
				}
				
				String className = testPartition[i][j].getOutputNominalValues(0);
				newInstance += className + "\n";
        		
        		outputTest += newInstance;
        	}

        	// create train partition-----------------------------
        	for(int j = 0 ; j < trainPartition[i].length ; ++j){
        		
        		boolean[] missing = trainPartition[i][j].getInputMissingValues();
				String newInstance = "";
				
				for(int ak = 0 ; ak < nattributes ; ak++){
					
					if(missing[ak])
						newInstance += "?";
					
					else{
						if(att[ak].getType() == Attribute.REAL)
							newInstance += trainPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.INTEGER)
							newInstance += (int)trainPartition[i][j].getInputRealValues(ak);
						if(att[ak].getType() == Attribute.NOMINAL)
							newInstance += trainPartition[i][j].getInputNominalValues(ak);
					}
					
					newInstance += ", "; 
				}
				
				String className = trainPartition[i][j].getOutputNominalValues(0);
				newInstance += className + "\n";
        		
        		outputTrain += newInstance;
        	}

            Files.addToFile(_carpeta + sep + _ds + "-" + numPartitions + "dobscv-" + String.valueOf(i + 1) + "tra.dat", outputTrain);
            Files.addToFile(_carpeta + sep + _ds + "-" + numPartitions + "dobscv-" + String.valueOf(i + 1) + "tst.dat", outputTest);
        }
  		
  	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It deletes the files of each training and test partition
	 * </p>
	 */	
  	public void deletePartitionFiles(){

  		for(int i = 0 ; i < numPartitions ; ++i){

			File fichero = new File("train"+(i+1)+".dat");
			fichero.delete();
			fichero = new File("test"+(i+1)+".dat");
			fichero.delete();
		}
  	}
  	
//*******************************************************************************************************************************
	
    /**
     * <p>
     * Computes the k nearest neighbors of a given item belonging to a fixed class.
     * With that neighbors a suggested class for the item is returned.
     * </p>
     *
     * @param nvec  Number of nearest neighbors that are going to be searched
     * @param conj  Matrix with the data of all the items in the dataset
     * @param real  Matrix with the data associated to the real attributes of the dataset
     * @param nominal   Matrix with the data associated to the nominal attributes of the dataset
     * @param nulos Matrix with the data associated to the missing values of the dataset
     * @param clases    Array with the associated class for each item in the dataset
     * @param ejemplo   Array with the data of the specific item in the dataset used
     * as a reference in the nearest neighbor search
     * @param ejReal    Array with the data of the real attributes of the specific item in the dataset
     * @param ejNominal Array with the data of the nominal attributes of the specific item in the dataset
     * @param ejNulos   Array with the data of the missing values of the specific item in the dataset
     * @param nClases   Class of the specific item in the dataset
     * @param distance  Kind of distance used in the nearest neighbors computation.
     * If true the distance used is the euclidean, if false the HVMD distance is used
     * @param vecinos   Array that will have the nearest neighbours id for the current specific item
     * @param clase Class of the neighbours searched for the item
     * @return the majority class for all the neighbors of the item
     */
  	//evaluationKNNClass (num_vecinos, instancia, 2, distance, neighbors,cl);
    public int evaluationKNNClass (int nvec, int instancia, int nClases, boolean distance, int vecinos[], int clase) {

            int i, j, l;
            boolean parar = false;
            int vecinosCercanos[];
            double minDistancias[];
            int votos[];
            double dist;
            int votada, votaciones;

            if (nvec > ninstances)
                    nvec = ninstances;

            votos = new int[nClases];
            vecinosCercanos = new int[nvec];
            minDistancias = new double[nvec];
            for (i=0; i<nvec; i++) {
                    vecinosCercanos[i] = -1;
                    minDistancias[i] = Double.POSITIVE_INFINITY;
            }

            for (i=0; i<ninstances; i++) {
            	
            	if(!used[i]){
            	
                    dist = distancia(i, instancia);
                    if (dist > 0 && instances[i].getOutputNominalValuesInt(0) == clase) {
                            parar = false;
                            for (j = 0; j < nvec && !parar; j++) {
                                    if (dist < minDistancias[j]) {
                                            parar = true;
                                            for (l = nvec - 1; l >= j+1; l--) {
                                                    minDistancias[l] = minDistancias[l - 1];
                                                    vecinosCercanos[l] = vecinosCercanos[l - 1];
                                            }
                                            minDistancias[j] = dist;
                                            vecinosCercanos[j] = i;
                                    }
                            }
                    }
                    
            	}
            }

            for (j=0; j<nClases; j++) {
                    votos[j] = 0;
            }

            for (j=0; j<nvec; j++) {
                    if (vecinosCercanos[j] >= 0)
                            votos[instances[vecinosCercanos[j]].getOutputNominalValuesInt(0)] ++;
            }

            votada = 0;
            votaciones = votos[0];
            for (j=1; j<nClases; j++) {
                    if (votaciones < votos[j]) {
                            votaciones = votos[j];
                            votada = j;
                    }
            }

            for (i=0; i<vecinosCercanos.length; i++)
                    vecinos[i] = vecinosCercanos[i];

            return votada;
    }

	
	boolean quedanEjemplos (int clase){
		return (numUsedPerClass[clase]<numExClass[clase]);
	}

	int instanciaNoUsada(int clase){
		
		for(int i = 0 ; i < ninstances ; ++i){
			if(!used[sortedIndex[i]] && instances[sortedIndex[i]].getOutputNominalValuesInt(0)==clase)
				return sortedIndex[i];
		}
		
		return -1;
	}
	
	
 	/** 
	 * Calculates the HVDM distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej1Real First instance (Real valued)	 
	 * @param ej1Nom First instance (Nominal valued)	
	 * @param ej1Nul First instance (Null values)		 
	 * @param ej2 Second instance
	 * @param ej2Real First instance (Real valued)	 
	 * @param ej2Nom First instance (Nominal valued)	
	 * @param ej2Nul First instance (Null values)	
	 * @param Euc Use euclidean distance instead of HVDM
	 *
	 * @return The HVDM distance 
	 */
	
	//KNN.distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
	public double distancia (int ej1, int ej2) {

		int i;
		double suma = 0;
			
		for (i=0; i<nattributes; i++) {
			if (instances[ej1].getInputMissingValues(i) == true || instances[ej2].getInputMissingValues(i) == true) {
				suma += 1;
			} else if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
				suma += nominalDistance[i][instances[ej1].getInputNominalValuesInt(i)][instances[ej2].getInputNominalValuesInt(i)];
			} else {
				suma += Math.abs(instances[ej1].getInputRealValues(i)-instances[ej2].getInputRealValues(i)) / 4*stdDev[i];
			}
		}
		
		suma = Math.sqrt(suma);       	
		
		return suma;  
	}
	
	
	
	public void calculo_previo_hvdm(){
		
		double VDM, Nax, Nay,Naxc,Nayc,media, SD;
		stdDev = new double[Attributes.getInputNumAttributes()];
		nominalDistance = new double[Attributes.getInputNumAttributes()][][];
		int nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
    
    
		for (int i=0; i<nominalDistance.length; i++) {
			if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
                        nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];
                        for (int j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 
                                nominalDistance[i][j][j] = 0.0;

                        }
                        for (int j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
                                for (int l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
                                        VDM = 0.0;
                                        Nax = Nay = 0;
                                        for (int m=0; m<ninstances; m++) {
                                                if ( instances[m].getInputNominalValuesInt(i) == j) {
                                                        Nax++;

                                                }
                                                if (instances[m].getInputNominalValuesInt(i) == l) {
                                                        Nay++;


                                                }
                                        }
                                        for (int m=0; m<nClases; m++) {
                                                Naxc = Nayc = 0;
                                                for (int n=0; n<ninstances; n++) {
                                                        if ( instances[n].getInputNominalValuesInt(i) == j && instances[n].getOutputNominalValuesInt(0) == m) {
                                                                Naxc++;

                                                        }
                                                        
                                                        if ( instances[n].getInputNominalValuesInt(i) == l && instances[n].getOutputNominalValuesInt(0) == m) {
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
                        for (int j=0; j<ninstances; j++) {
                                media += instances[j].getInputRealValues(i);
                                SD += instances[j].getInputRealValues(i)*instances[j].getInputRealValues(i);

                        }
                        media /= (double)ninstances;
                        stdDev[i] = Math.sqrt((SD/((double)ninstances)) - (media*media));



                }
        }
     
	
	
}
	
	
    
}