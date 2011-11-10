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
 * Datos.java
 *
 * @author Manuel Chica Serrano (University of Jaen) 22/8/2005
 * @author Modified by Ignacio Robles Paiz (University of Granada) 27/06/2010
 * @author Modified by Ignacio Robles Paiz (University of Granada) 02/07/2010
 */

package keel.Algorithms.Preprocess.Feature_Selection;

import keel.Dataset.*;
import java.util.*;
import org.core.Fichero;
import keel.Algorithms.Preprocess.Basic.CheckException;


public class Datos {

    /** the pathnames of training and test datasets */
    private String trainFileNames;
    private String testFileNames;


    /** instance sets for the training data */
    private InstanceSet train;


    /** instance sets for the test data */
    private InstanceSet test;


    /** matrix with the normalized training data [0..1] */
    private double trainInputNormalized[][];


    /** matrix with the normalized test data [0..1] */
    private double testInputNormalized[][];

    /** arrays with the training and test classes */
    private int trainOutput[];
    private int testOutput[];


    /** number of nearest neighbours for the KNN algorithm  */
    private int paramKNN = 1;


    /** this property indicates if datasets are discretized */
    private boolean areDiscretized;


    /** Creates a new instance of Datos */
    public Datos(String trainFileNames1, String testFileNames1, int k) {


        trainFileNames = trainFileNames1;
        testFileNames = testFileNames1;


        /** saves the knn parameter */
        if(k >= 1) paramKNN = k;
        else {
            System.err.println("WARNING: k-parameter for the KNN Classifier is fewer than 1");
            System.err.println("It will be assigned to 1");
            paramKNN = 1;
        }

        /** creates the training and test instance sets from the training and test files */
        /** normalizes the instance sets too */


        try{

            train = new InstanceSet();
            train.readSet(trainFileNames, true);          

            normalizeTraining();

        } catch(Exception e){

            System.err.println(e);
            System.exit(0);

        }

        try{

            test = new InstanceSet();
            test.readSet(testFileNames, false);

            normalizeTest();

        } catch(Exception e){

            System.err.println(e);
            System.exit(0);

        }  

        areDiscretized = true;

        /* checks if datasets are discretized */
        for(int i=0; i<Attributes.getInputNumAttributes() && areDiscretized; i++ )
            if(Attributes.getInputAttribute(i).getType()==Attribute.INTEGER ||
                Attributes.getInputAttribute(i).getType()==Attribute.REAL)
                areDiscretized = false;


    }


    /** returns the number of features of the datasets 
     *  @return the number of input features */
    public int returnNumFeatures(){

        if(train == null || test == null) {
            System.err.println("ERROR: Dataset hasn't already read. It's not possible to return the number of features");
            System.exit(0); 
        }

        return Attributes.getInputNumAttributes();
    }

    /** returns the number of instances of the datasets 
     *  @return the number of input instances */
    public int returnNumInstances(){

        if(train == null || test == null) {
            System.err.println("ERROR: Dataset hasn't already read. It's not possible to return the number of instances");
            System.exit(0); 
        }

        return train.getNumInstances();
    }


    /** saves the normalized instance sets in the trainNormalized member [0..1]
     *  Also, it saves the training classes in trainOutput 
     *  The train attribute is not normalized
     *  @throws CheckException */
    private void normalizeTraining() throws CheckException {
        int i, j, k;
        Instance temp;
        double caja[];
        boolean nulls[];
        double vectorInputs[];

        /* Check if dataset corresponding with a classification problem*/

        if (Attributes.getOutputNumAttributes() < 1) {
            throw new CheckException("This dataset hasn't outputs, so it's not corresponding to a classification problem.");
        } else if (Attributes.getOutputNumAttributes() > 1) {
            throw new CheckException("This dataset has more than an output.");
        }

        if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
            throw new CheckException("This dataset has an input attribute with floating values, " +
                    "so it's not corresponding to a classification problem.");
        }

        trainInputNormalized = new double[train.getNumInstances()][Attributes.getInputNumAttributes()];
        trainOutput = new int[train.getNumInstances()];
        caja = new double[1];

        for (i=0; i<train.getNumInstances(); i++) {
            temp = train.getInstance(i);
            nulls = temp.getInputMissingValues();

            /* copies in trainInputNormalized the ith instance data */
            trainInputNormalized[i] = new double[Attributes.getInputNumAttributes()];
            vectorInputs = train.getInstance(i).getAllInputValues(); 
            for(j=0; j<vectorInputs.length; j++)
                trainInputNormalized[i][j] = vectorInputs[j];

            for (j=0; j<nulls.length; j++)
                if (nulls[j])
                    trainInputNormalized[i][j]=0.0;
            caja = train.getInstance(i).getAllOutputValues();
            trainOutput[i] = (int) caja[0];
            for (k = 0; k < trainInputNormalized[i].length; k++) {
                if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
                    /* checks if denominator is 0 */
                    if((Attributes.getInputAttribute(k).getNominalValuesList().size() - 1)==0)
                        trainInputNormalized[i][k] = 0;
                    else
                        trainInputNormalized[i][k] /= Attributes.getInputAttribute(k).
                            getNominalValuesList().size() - 1;

                } else {
                    trainInputNormalized[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
                    /* checks if denominator is 0 */
                    if((Attributes.getInputAttribute(k).getMaxAttribute() -
                            Attributes.getInputAttribute(k).getMinAttribute())==0)
                        trainInputNormalized[i][k] = 0;
                    else
                        trainInputNormalized[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() -
                            Attributes.getInputAttribute(k).getMinAttribute();
                }
            }
        }

    }


    /** saves the normalized instance sets in the testNormalized member [0..1]
     *  Also, it saves the test classes in testOutput 
     *  The test attribute is not normalized
     *  @throws CheckException */
    private void normalizeTest() throws CheckException{
        double vectorInputs[];
        int i, j, k;
        Instance temp;
        double caja[];
        boolean nulls[];

        /*Check if dataset corresponding with a classification problem*/

        if (Attributes.getOutputNumAttributes() < 1) {
            throw new CheckException("This dataset hasn't outputs, so it's not corresponding to a classification problem.");
        } else if (Attributes.getOutputNumAttributes() > 1) {
            throw new CheckException("This dataset has more than an output.");
        }

        if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
            throw new CheckException("This dataset has an input attribute with floating values, " +
                    "so it's not corresponding to a classification problem.");
        }

        testInputNormalized = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];
        testOutput = new int[test.getNumInstances()];
        caja = new double[1];

        for (i=0; i<test.getNumInstances(); i++) {
            temp = test.getInstance(i);
            nulls = temp.getInputMissingValues();

            /* copies in testInputNormalized the ith instance data */
            testInputNormalized[i] = new double[Attributes.getInputNumAttributes()];
            vectorInputs = test.getInstance(i).getAllInputValues(); 
            for(j=0; j<vectorInputs.length; j++)
                testInputNormalized[i][j] = vectorInputs[j];

            for (j=0; j<nulls.length; j++)
                if (nulls[j])
                    testInputNormalized[i][j]=0.0;
            caja = test.getInstance(i).getAllOutputValues();
            testOutput[i] = (int)caja[0];
            for (k = 0; k < testInputNormalized[i].length; k++) {
                if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
                    /* checks if denominator is 0 */
                    if((Attributes.getInputAttribute(k).getNominalValuesList().size()-1)==0)
                        testInputNormalized[i][k] = 0;
                    else
                        testInputNormalized[i][k] /= Attributes.getInputAttribute(k).getNominalValuesList().size()-1;
                } else {
                    testInputNormalized[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
                    /* checks if denominator is 0 */
                    if((Attributes.getInputAttribute(k).getMaxAttribute() - 
                            Attributes.getInputAttribute(k).getMinAttribute())==0)
                        testInputNormalized[i][k] = 0;
                    else
                        testInputNormalized[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - 
                                                        Attributes.getInputAttribute(k).getMinAttribute();
                }
            }
        }
    }


    /** calculates the distance between a couple of examples or instances. This distance is calculated
     *  according to selected features
     *  @param ej1 is the first example or instance
     *  @param ej2 is the second example or instance
     *  @param featuresVector is a boolean array that has the selected features
     *  @return returns a double value with the distance value */
    private static double distancia(double ej1[], double ej2[], boolean featuresVector[]) {

        int i;
        double suma = 0;

        for (i=0; i<ej1.length; i++)
            if(featuresVector[i] == true) suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);

        suma = Math.sqrt(suma);

        return suma;

    }


    /** calculates the precision (errors/total_instances) in the prediction of the instance class.
        This is the Leaving One Out algorithm
     *  @param featuresVector a boolean array with selected features
     *  @return returns a double value with the error  (n_errors/total_instances) */
    public double LVO(boolean featuresVector[]){
        double min,media;
        double distancias[];
        int kClases[];
        int i,j,k,posMin,errores;

        errores = 0;

        /* this array saves the distances between an instance and its neighbours */
        distancias = new double[train.getNumInstances()];
        /* this array contains the classes of the k-nearest neighbours */
        kClases = new int[paramKNN];

        /* for each example, it calculates its nearest neighbours for predict the output class */

        for(i=0; i<train.getNumInstances(); i++) {

            for(j=0; j<train.getNumInstances(); j++) {
                /* saves in an array the distances between an example and its nearest neighbours */
                if(i!=j)
                    distancias[j] = distancia(trainInputNormalized[i], trainInputNormalized[j], featuresVector);
                else distancias[j] = Double.MAX_VALUE;
            }

            /* obtains the k nearest neighbours and classifies the example.  */
            for(k=0; k<paramKNN; k++) {
                min = Double.MAX_VALUE;
                posMin = -1;
                for(j=0; j<distancias.length; j++)
                    if(distancias[j]<min){
                    min = distancias[j];
                    posMin = j;
                    }
                distancias[posMin] = Double.MAX_VALUE;
                kClases[k] = trainOutput[posMin];
            }
            media = 0;
            for(k=0; k<paramKNN; k++)
                media += kClases[k];
            media /= paramKNN;

            /* increases the error count if the classifier fails */
            if(trainOutput[i] != (int)Math.round(media)) errores++;

        }
        return ((double)errores/train.getNumInstances());

    }


    /** calculates the inconcistency ratio.
     *  @param featuresVector a boolean array with the selected features
     *  @return returns a double value with the inconsistency ratio (0..1) */
    public double medidaInconsistencia(boolean featuresVector[]){
        int i, j, cuenta, max;
        boolean caracsIguales;
        double ratioInconsistencias = 0;
        boolean vMarcados[];
        int inconClase[];

        /* checks if data are discretized. */
        if(!areDiscretized){
            System.err.println("ERROR: Data values aren't discretized!. This is an algorithm precondition");
            System.exit(0);
        }

        /* this array contains the number of inconsistencies for each class. Its length is equal than the possibles 
           values for the class*/
        inconClase = new int[Attributes.getOutputAttribute(0).getNominalValuesList().size()];
        for(i=0; i<inconClase.length; i++) inconClase[i] = 0;

        /* boolean array that indicates what instances have been checked */
        vMarcados = new boolean[train.getNumInstances()];

        i = 0;
        while(i<train.getNumInstances()){
            if(!vMarcados[i]){                
                inconClase[(int)train.getOutputNumericValue(i, 0)]++;
                vMarcados[i] = true;
                for(j=i+1; j<train.getNumInstances(); j++)
                    if(!vMarcados[j]){
                        /* checks if ith and jth instances have the same attributes */
                        caracsIguales = true;
                        for(int k=0; k<Attributes.getInputNumAttributes() && caracsIguales; k++)
                            if(featuresVector[k]==true && train.getInputNumericValue(i, k)!=train.getInputNumericValue(j, k) )
                                caracsIguales = false;

                        /* if the features values are equal, increase the inconsistency array */
                        if(caracsIguales){
                            inconClase[(int)train.getOutputNumericValue(j, 0)]++;

                            /* sets the jth as checked */
                            vMarcados[j] = true;
                        }


                    }

                /* find the max value and substract it to the inconsistency count */                

                max = 0;
                for(j=cuenta=0; j<inconClase.length; j++){
                    if(inconClase[j] > max) 
                        max = inconClase[j];
                    cuenta += inconClase[j];
                }
                cuenta = cuenta - max;

                /* adds the inconsistency count to the total count */
                ratioInconsistencias += cuenta;

                /* initializes the inconsistency array to 0 for the next loop */
                for(j=0; j<inconClase.length; j++) inconClase[j] = 0;
            }

            i++;           

        }

        ratioInconsistencias /= train.getNumInstances();

        return ratioInconsistencias;       

    }


     /** calculates the inconsistent example pairs ratio (IEP)
     *  @param featuresVector a boolean array with the selected features
     *  @return returns a double value with the inconsistency ratio (0..1) */
    public double measureIEP(boolean featuresVector[]){
        int i, j, cuenta, max;
        boolean caracsIguales;
        double ratioInconsistencias = 0;
        boolean vMarcados[];
        int inconClase[];

        /* checks if data are discretized. */
        if(!areDiscretized){
            System.err.println("ERROR: Data values aren't discretized!. This is an algorithm precondition");
            System.exit(0);
        }

        /* this array contains the number of inconsistencies for each class. Its length is equal than the possibles
           values for the class*/
        inconClase = new int[Attributes.getOutputAttribute(0).getNominalValuesList().size()];
        for(i=0; i<inconClase.length; i++) inconClase[i] = 0;

        /* boolean array that indicates what instances have been checked */
        vMarcados = new boolean[train.getNumInstances()];

        i = 0;
        while(i<train.getNumInstances()){
            if(!vMarcados[i]){
                inconClase[(int)train.getOutputNumericValue(i, 0)]++;
                vMarcados[i] = true;
                for(j=i+1; j<train.getNumInstances(); j++)
                    if(!vMarcados[j]){
                        /* checks if ith and jth instances have the same attributes */
                        caracsIguales = true;
                        for(int k=0; k<Attributes.getInputNumAttributes() && caracsIguales; k++)
                            if(featuresVector[k]==true && train.getInputNumericValue(i, k)!=train.getInputNumericValue(j, k) )
                                caracsIguales = false;

                        /* if the features values are equal, increase the inconsistency array */
                        if(caracsIguales){
                            inconClase[(int)train.getOutputNumericValue(j, 0)]++;

                            /* sets the jth as checked */
                            vMarcados[j] = true;
                        }


                    }

                /* find the max value and substract it to the inconsistency count */

                max = 0;
                for(j=cuenta=0; j<inconClase.length; j++){
                    if(inconClase[j] > max)
                        max = inconClase[j];
                    cuenta += inconClase[j];
                }
                cuenta = cuenta - max;

                /* adds the number of inconsistent pairs count to the total count */
                ratioInconsistencias += (cuenta * (cuenta-1) / 2);

                /* initializes the inconsistency array to 0 for the next loop */
                for(j=0; j<inconClase.length; j++) inconClase[j] = 0;
            }

            i++;

        }

        ratioInconsistencias /= (train.getNumInstances() * (train.getNumInstances()-1)/2);

        return ratioInconsistencias;

    }


    /** calculates the mutual information measure between the variables and the class. This method will be applied
     *  only at the beginning
        @return a double array with the MI between ith variable and the class */
    public double[] obtenerIMVarsClase(){
        double I[], pf, pcf, pc, suma;
        int nc[], nf[], ncf[][];
        int i,j,posNC,posNF;

        /* checks if data are discretized. */
        if(!areDiscretized){
            System.err.println("ERROR: Data values aren't discretized!. This is an algorithm precondition");
            System.exit(0);
        }

        /* saves the MI between the features values and the class */
        I = new double[Attributes.getInputNumAttributes()];

        /* calculates the number of intervals and the possible values of the class */
        int max_num_inter = -1;        
        for(int k=0; k<I.length; k++){        
            if(Attributes.getInputAttribute(k).getNominalValuesList().size() > max_num_inter){
                max_num_inter = Attributes.getInputAttribute(k).getNominalValuesList().size();
            }
        }
        int numeroIntervalos = max_num_inter;                       
        int numeroValoresClase = Attributes.getOutputAttribute(0).getNominalValuesList().size();

        /* k indicates the feature selected  */
        for(int k=0; k<I.length; k++){
                /* nc, nf & ncf are auxiliar arrays for saving the differentes occurrences for each class value according to
                 the features values */
                
                nc = new int[numeroValoresClase];
                nf = new int[numeroIntervalos];	
                ncf = new int[numeroValoresClase][numeroIntervalos];

                for(i=0; i<train.getNumInstances(); i++){
                        /* increases the value count for each class value */
                        posNC = (int)train.getOutputNumericValue(i,0);
                        posNF = Integer.valueOf(train.getInputNominalValue(i,k)).intValue();
                        nc[posNC]++;
                        nf[posNF]++;
                        ncf[posNC][posNF]++;
                }

                /* calculates the MI for the kth feature */
                suma = 0;
                for(i=0; i<numeroValoresClase; i++){
                        pc = ((double)nc[i]/train.getNumInstances());
                        for(j=0; j<numeroIntervalos; j++){
                                pcf = ((double)ncf[i][j]/train.getNumInstances());
                                pf = ((double)nf[j]/train.getNumInstances());
                                if(pf!=0 && pcf!=0 && pc!=0)
                                        suma += pcf * (Math.log10(pcf/(pc*pf))/Math.log10(2)); 
                        }
                }

                /* 'suma' saves the IM value */
                I[k] = suma;
        }

        return I;

    }


    /** modulo que calcula la informacion mutua entre cada dos variables. Tambien se realizarÃ¡ una Ãºnica vez cuando se
        utilice la medida de IM 
        @return matriz de valores double con IM entre la variable i  y la variable j */
    public double[][]obtenerIMVars(){
        double IMV[][];
        int i,j, posNX, posNY;
        int nx[], ny[], nxy[][];
        double px, py, pxy, suma;

        /* primero hay que comprobar que los datos estï¿½ discretizados, es decir, no hay ningun valor no entero */
        if(!areDiscretized){
            System.err.println("ERROR: Data values aren't discretized! This is an algorithm precondition");
            System.exit(0);
        }

        /* en I guardaremos la informacion mutua entre cada dos caracteristicas */
        IMV = new double[Attributes.getInputNumAttributes()][Attributes.getInputNumAttributes()];

        /* calculamos el numero de intervalos */
        int max_num_inter = -1;        
        for(int k=0; k<Attributes.getInputNumAttributes(); k++){        
            if(Attributes.getInputAttribute(k).getNominalValuesList().size() > max_num_inter){
                max_num_inter = Attributes.getInputAttribute(k).getNominalValuesList().size();
            }
        }
        int numeroIntervalos = max_num_inter;                       

        /* k y l van a tomar valores dentro del numero de caracteristicas */
        for(int k=0; k<Attributes.getInputNumAttributes(); k++){
            for (int l = 0; l < Attributes.getInputNumAttributes(); l++) {
        
                /* iniciamos los vectores necesarios para calcular la INF. MUTUA */
                nx = new int[numeroIntervalos];
                ny = new int[numeroIntervalos];
                nxy = new int[numeroIntervalos][numeroIntervalos];
        
                /* pasamos por todas las instancias incrementando los contadores nx, ny y nxy */
                for (i = 0; i < train.getNumInstances(); i++) {
                    posNX = Integer.valueOf(train.getInputNominalValue(i, k)).intValue();
                    posNY = Integer.valueOf(train.getInputNominalValue(i, l)).intValue();
                    nx[posNX]++;
                    ny[posNY]++;
                    nxy[posNX][posNY]++;
                }
        
                /* ahora calculamos la IM entre las dos variables k y l */
                suma = 0;
                for (i = 0; i < numeroIntervalos; i++) {
                    px = (double) nx[i] / train.getNumInstances();
                    for (j = 0; j < numeroIntervalos; j++) {
                        py = (double) ny[j] / train.getNumInstances();
                        pxy = (double) nxy[i][j] / train.getNumInstances();
                        if (px != 0 && py != 0 && pxy != 0)
                            /* el logaritmo tiene que ser en base 2, por eso dividimos entre log10(2)*/
                            suma += pxy * (Math.log10(pxy / (px * py)) / Math.log10(2));
                    }
                }
        
                /* guardamos la IMV en la matriz, que es el valor de suma */
                IMV[k][l] = suma;
        
            }
        }
        return IMV;
    }


    /** returns the nearest instance according to the instance passed as an argument. It is neccesary that their classes
        are the same. The nearest instance will be an instance that has the minimum euclidean distance
        @param posI is the instance position to find the nearest
        @return returns the instance position of the nearest instance found */
    public int findNearestHit(int posI){
        int j, posMin;
        boolean featuresVector[];
        double distancias[], min;

        /* the vector 'distancias' saves the distance between posI instance and the rest of them, in order to select the
           nearest. featuresVector will be contain the feature selected */
        distancias = new double[train.getNumInstances()];
        featuresVector = new boolean[returnNumFeatures()];
        for(j=0; j<featuresVector.length; j++) featuresVector[j] = true;

        for(j=0; j<distancias.length; j++) 
            /* saves in an array the distances between the example and its neighbours */
            if(posI!=j)
                distancias[j] = distancia(trainInputNormalized[posI], trainInputNormalized[j], featuresVector);
            else distancias[j] = Double.MAX_VALUE;


        /* selects the nearest neighbours if the class is the same */
        min = Double.MAX_VALUE;
        posMin = -1;
        for(j=0; j<distancias.length; j++)
            if(distancias[j]<min && 
                    train.getInstance(j).getOutputRealValues(0) == train.getInstance(posI).getOutputRealValues(0)){
                min = distancias[j];
                posMin = j;
            }

        return posMin;

    }


    /** returns the nearest instance according to the instance passed as an argument. It is neccesary that their classes
        are NOT the same. The nearest instance will be an instance that has the minimum euclidean distance
        @param posI is the instance position to find the nearest
        @return returns the instance position of the nearest instance found */
    public int findNearestMiss(int posI){        
        int j, posMin;
        boolean featuresVector[];
        double distancias[], min;

        /* the vector 'distancias' saves the distance between posI instance and the rest of them, in order to select the
           nearest. featuresVector will be contain the feature selected */
        distancias = new double[train.getNumInstances()];
        featuresVector = new boolean[returnNumFeatures()];
        for(j=0; j<featuresVector.length; j++) featuresVector[j] = true;

        for(j=0; j<distancias.length; j++) 
            /* saves in an array the distances between the example and its neighbours */
            if(posI!=j)
                distancias[j] = distancia(trainInputNormalized[posI], trainInputNormalized[j], featuresVector);
            else distancias[j] = Double.MAX_VALUE;


        /* selects the nearest neighbours if the class is NOT the same */
        min = Double.MAX_VALUE;
        posMin = -1;
        for(j=0; j<distancias.length; j++)
            if(distancias[j]<min && 
                    train.getInstance(j).getOutputRealValues(0) != train.getInstance(posI).getOutputRealValues(0)){
                min = distancias[j];
                posMin = j;
            }

        return posMin;
    }

    private double[] getAllClassesTrain(){
        double [] classes;
        HashSet<Double> c = new HashSet<Double>(0);
        Double d;

        for(int i=0; i<train.getNumInstances(); i++){
            d = new Double(train.getInstance(i).getOutputRealValues(0));
            if(!c.contains(d))
                c.add(d);
        }

        classes = new double[c.size()];
        Iterator<Double> it = c.iterator();
        int i = 0;

        while(it.hasNext()){
            classes[i] = it.next().doubleValue();
            i++;
        }

        return classes;

    }

    private double probPriori(double theClass){
        int count = 0;
        for(int i=0; i<train.getNumInstances(); i++)
            if(train.getInstance(i).getOutputRealValues(0) == theClass)
                count++;

        return count/(double)train.getNumInstances();
    }
    

    public double sumDifferentClasses(int posExample, int feature){
        double [] classes = getAllClassesTrain();
        double exampleClass = train.getInstance(posExample).getOutputRealValues(0);
        double sum = 0.0;
        int posMiss = findNearestMiss(posExample);


        for(int i=0; i<classes.length; i++)
            if(classes[i] != exampleClass)
                sum += probPriori(classes[i]) * diff(feature, posExample, posMiss);


        return sum;
    }


    /** data must be discretized. it is used by the RELIEF method. returns 1 if the feature value passed
     *  as argument is equal in both instances (also passed as arguments),0 in other case.
     *  @param numCarac is the number of feature to check
     *  @param numInstancia1 is the position of the first instance
     *  @param numInstancia2 is the position of the second instance
     *  @return return 1 if the feature value passed as argument is equal in both instances */
    public int diff(int numCarac, int numInstancia1, int numInstancia2){

        if(!areDiscretized){
            System.err.println("ERROR: Data values aren't discretized! This is an algorithm precondition ");
            System.exit(0);
        }

        if(train.getInstance(numInstancia1).getInputRealValues(numCarac) == 
                train.getInstance(numInstancia2).getInputRealValues(numCarac))
            return 0;
        else 
            return 1;

    }


    /** calculates the precision (errors/total_instances) in the classification of all instances in the TEST DATASET
        using the given features and THE SAME TEST DATASET TO PREDICT. Uses the Leaving One Out algorithm
     *  @param featuresVector a boolean array with the selected features
     *  @return returns a double value with the calculated error (n.errors/total) */
    public double LVOTest(boolean featuresVector[]){
        double min,media;
        double distancias[];
        int kClases[];
        int i,j,k,posMin,errores;

        errores = 0;

        /* this array helps us to save the distances between an example and its neighbours */
        distancias = new double[test.getNumInstances()];
        /* contains the classes of the k nearest neighbours */
        kClases = new int[paramKNN];

        /* for each example checks the nearest neighbour or neighbours calculating their distances to predict
           the class*/

        for(i=0; i<test.getNumInstances(); i++) {

            for(j=0; j<test.getNumInstances(); j++) {
                /* saves in an array the distances of an example with its neighbours */
                if(i!=j)
                    distancias[j] = distancia(testInputNormalized[i], testInputNormalized[j], featuresVector);
                else distancias[j] = Double.MAX_VALUE;
            }

            /* selects the classes of the k nearest neighbours */
            for(k=0; k<paramKNN; k++) {
                min = Double.MAX_VALUE;
                posMin = -1;
                for(j=0; j<distancias.length; j++)
                    if(distancias[j]<min){
                    min = distancias[j];
                    posMin = j;
                    }
                distancias[posMin] = Double.MAX_VALUE;
                kClases[k] = testOutput[posMin];
            }
            media = 0;
            for(k=0; k<paramKNN; k++)
                media += kClases[k];
            media /= paramKNN;

            /* if the classes aren't the same, increase the error count */
            if(testOutput[i] != (int)Math.round(media)) errores++;

        }
        return ((double)errores/test.getNumInstances());

    }


    /** calculates the precision (errors/total_instances) in the classification of all instances in the TEST DATASET
        using the given features and THE TRAINING DATASET TO PREDICT. Uses the Leaving One Out algorithm
     *  @param featuresVector a boolean array with the selected features
     *  @return returns a double value with the calculated error (n.errors/total) */
    public double validacionCruzada(boolean featuresVector[]){
        double min,media;
        double distancias[];
        int kClases[];
        int i,j,k,posMin,errores;

        errores = 0;

        /* this array helps us to save the distances between an example and its neighbours */
        distancias = new double[train.getNumInstances()];
        /* contains the classes of the k nearest neighbours */
        kClases = new int[paramKNN];


        /* for each example checks the nearest neighbour or neighbours calculating their distances to predict
           the class*/
        for(i=0; i<test.getNumInstances(); i++) {

            for(j=0; j<train.getNumInstances(); j++)
                /* saves in an array the distances between the examples of TEST and the examples of TRAINING */
                distancias[j] = distancia(testInputNormalized[i], trainInputNormalized[j], featuresVector);

            /* selects the classes of the k nearest neighbours */
            for(k=0; k<paramKNN; k++) {
                min = Double.MAX_VALUE;
                posMin = -1;
                for(j=0; j<distancias.length; j++)
                    if(distancias[j]<min){
                    min = distancias[j];
                    posMin = j;
                    }
                distancias[posMin] = Double.MAX_VALUE;
                kClases[k] = trainOutput[posMin];
            }
            media = 0;
            for(k=0; k<paramKNN; k++)
                media += kClases[k];
            media /= paramKNN;

            /* if the classes aren't the same, increase the error count */
            if(testOutput[i] != (int)Math.round(media)) errores++;

        }

        return ((double)errores/test.getNumInstances());

    }


    /** this method generates the output files .tra and .tst, removing the non-selected features
        @param ficheroTrainSalida is a string with the pathname of the output training file
        @param ficheroTestSalida is a string with the pathname of the output test file
        @param solucion is a boolean array with the selected features */
    public void generarFicherosSalida(String ficheroTrainSalida, String ficheroTestSalida, boolean solucion[]){
        int i, k;
        Fichero fic = new Fichero();

        /* removes the non-selected features from the dataset */
        i = k = 0;

        while(i<Attributes.getInputNumAttributes()){
            if(!solucion[k])
                train.removeAttribute(test, true, i);                
            else i++;
            k++;
        }

        /* creates a string with all information of train */
        String cadena;
        cadena = train.getNewHeader();
        cadena += "@data\n";

        for(i=0; i<train.getNumInstances(); i++)
            cadena += train.getInstance(i).toString() + "\n";

        /* writes the previous string in the output training file */
        fic.escribeFichero(ficheroTrainSalida, cadena);


        /* creates a string with all information of test */
        cadena = test.getNewHeader();
        cadena += "@data\n";

        for(i=0; i<test.getNumInstances(); i++)
            cadena += test.getInstance(i).toString() + "\n";


        /* writes the previous string in the output test file */
        fic.escribeFichero(ficheroTestSalida, cadena);

    }

}

