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

package keel.Algorithms.Decision_Trees.C45_Binarization;

import java.util.ArrayList;

import keel.Dataset.Attributes;

import org.core.Files;


/**
 * <p>Title: OVO</p>
 * <p>Description: This class implements the Binarization methodology (OVO and OVO )
 * <p>Company: KEEL </p>
 * @author Mikel Galar (University of Navarra) 21/10/2010
 * @author Alberto Fernandez (University of Jaen) 15/05/2014
 * @version 1.2
 * @since JDK1.6
 */
public class OVO {

  Multiclassifier classifier;
  int       nClasses, count;
  String    sOvo;
  BTS       bts;
  Nesting   nesting;
  double[][] tablas; // ascending order (DynOVO)
  int kvecinos; //DynOVO
  ENN metodo;
  boolean test,dynOVO;
  

  /**
   * It constructs the new OVO instance depending on the aggregation that is
   * going to be used
   * @param classifier the multi-classifier containing the base classifiers
   * @param sOvo the aggregation to be used
   * @param dynOVO whether the final aggregation is the dynamic OVO
   */
  public OVO(Multiclassifier classifier, String sOvo, boolean dynOVO) {
      this.classifier = classifier;
      nClasses = classifier.nClasses;
      this.sOvo = sOvo;
      this.dynOVO = dynOVO;

      /* See whether the selected aggregation is correct or not */
      System.out.println("go to computeClassScores");
      try {computeClassScores(null);}
      catch(NullPointerException e){}
      System.out.println("finished computeClassScores");

      System.out.println("Finishing operations...");
      if (sOvo.equals("BTS")) {
          bts = new BTS(classifier, classifier.threshold, this);
      }
      else if (sOvo.equals("NESTING"))
          nesting = new Nesting(classifier, this);
      
      tablas =  new double[classifier.train.getnData()][nClasses * nClasses]; //DynOVO
      kvecinos = nClasses;
      createConfFile();
      metodo = new ENN("ENNConf.txt");
      System.out.println("OVO CREATED!");
  }

  private void createConfFile(){
	  String content = new String();
	  String train = classifier.input_validation_name;
	  String test = classifier.input_test_name;
	  content += "algorithm = Decremental Reduction Optimization Procedure 3\n";
	  content += "inputData = \""+train+"\" \""+test+"\" \"tst.dat\"\n"; 
	  content += "outputData = \"training.txt\" \"tstOutput.dat\"\n\n"; 
	  content += "Number of Neighbors = 3\n";
	  content += "Distance Function = HVDM\n";

	  Files.writeFile("ENNConf.txt", content);
  }
  
  public void clearTables(boolean test){
	  count = 0;
	  this.test = test;
  }

  /**
   * It finishes the operations needed before classifying but after 
   * constructing the classifiers (for BTS!)
   */ 
  public void classifierTrainFinished() {
      /* Initialize and construct the binary tree if BTS is the selected aggregation */
      if (sOvo.equals("BTS")) {
          bts.initialize();
          bts.construct();
      }
  }
  
  /**
   * It computes the confidence vector for the classes using the method 
   * indicated in the config file
   * @param example the example to be classified
   * @return the confidence vector
   */
  protected String computeClassScores(double[] example) {
    String output = new String("?");

    /* In case of DDAG or BTS, use the example */
    if (sOvo.equals("DDAG"))
        return computeClassDDAG(example);
    else if (sOvo.equals("BTS"))
        return bts.computeClass(example);
    
    
    double[][] tabla = classifier.ovo_table(example);
    for (int i = 0; i < nClasses; i ++){
    	for (int j = 0; j < nClasses; j++){
    		tablas[count][j + i * nClasses] = tabla[i][j];
    	}
    }
    	
    double[] max = null;
    
    /* In case the rest of the cases, use the obtained score matrix */
    if (sOvo.equals("VOTE"))
        max = computeClassScoresVote(tabla);
    else if (sOvo.equals("WEIGHTED"))
        max = computeClassScoresWeighted(tablas[count]); //for DynOVO extension
    else if (sOvo.equals("PC"))
        max = computeClassScoresPC(tabla);
    else if (sOvo.equals("ND"))
        max = computeClassScoresND(tabla);
    else if (sOvo.equals("LVPC"))
        max = computeClassScoresLVPC(tabla);
    else if (sOvo.equalsIgnoreCase("WuJMLR"))
       max = method2WuJMLR(tabla);
    else if (sOvo.equals("NESTING")) {
        max = computeClassScoresVote(tabla);
        // See if there is a tie using voting strategy
        int nMax = 1;
        double maxi = max[0];
        for (int i = 1; i < max.length; i++) {
            if (maxi < max[i]) {
                maxi = max[i];
                nMax = 1;
            }
            else if (maxi == max[i])
                nMax++;
        }
        /**
         * It depends on the phase: if we are classifying in train (creating the nesting)
         * we return the "tie", otherwise, we use the nested OVO!
         */
        if (nesting.creating && nMax > 1) {
            System.out.println("There is a Tie!");
            return "tie";
        }
        else if (nMax > 1) {
            output =  nesting.method.ovo.computeClassScores(example);
            return output;
        }
    }
    else {
        System.out.println("The OVO method is not correct");
        System.exit(-1);
    }
    if (dynOVO)
    	return computeClassScoresDynamic(count++);
    else
    	output = getOutputTies(max);

    return output;
  }

  /**
   * It computes the confidence vector for the classes using the DDAG approach
   * @param example the example to be classified
   * @return the confidence vector
   */
  protected String computeClassDDAG(double[] example)
  {
      ArrayList<Integer> list = new ArrayList<Integer>();
      for (int i = 0; i < nClasses; i++)
          list.add(new Integer(i));

      int clase;
      int x, y;
      int eliminado = -1;
      while (list.size() != 1)
      {
          /* Delete a class from the list in each iteration */
          for (int i = 0; i < list.size() - 1 && eliminado == -1; i++)
          {
              for (int j = list.size() - 1; j > i && eliminado == -1; j--)
              {
                  x = list.get(i).intValue();
                  y = list.get(j).intValue();
                  clase = classifier.obtainClass(x, y, example);
                  if (clase == x)
                      eliminado = list.remove(list.size() - 1);
                  else if (clase == y)
                      eliminado = list.remove(0);
              }
          }
          /* If there is no class deleted, obtain the output class */
          if (eliminado == -1) {
              double[] max = new double[nClasses];
              for (int k = 1; k < nClasses; k++)
                  if (list.contains(new Integer(k)))
                      max[k] = 1;
              return getOutputTies(max);
          }
          else
              eliminado = -1;
      }
      return classifier.train.getOutputValue(list.get(0));

  }

  /**
   * It computes the confidence vector for the classes using the (classic) voting method
   * @param tabla array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  protected double[] computeClassScoresVote(double[][] tabla)
  {
    double[] max = new double[nClasses];
    for (int i = 0; i < nClasses; i++) {
      double sum_clase = 0.0;
      for (int j = 0; j < nClasses; j++) {
        sum_clase += tabla[i][j] > tabla[j][i] ? 1 : 0;
      }
      max[i] = sum_clase;
    }
    return max;
  }

  /**
   * It computes the confidence vector for the classes using the Weighted voting method
   * @param tabla array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  protected double[] computeClassScoresWeighted(double[][] tabla)
  {
    double [] max = new double[tabla.length];
    for (int i = 0; i < tabla.length; i++) {
      double sum_clase = 0.0;
      for (int j = 0; j < tabla.length; j++) {
        sum_clase += tabla[i][j];
      }
      max[i] = sum_clase / (double)tabla.length;
    }
    return max;
  }

  /**
   * It computes the confidence vector for the classes using Pairwise Coupling method
   * @param r array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  protected double[] computeClassScoresPC(double[][] r) // r = tabla
  {
    double[][] n = new double[nClasses][nClasses];
     for (int i = 0; i < r.length; i++) {
        for (int j = 0; j < r.length; j++) {
           n[i][j] = classifier.train.numberOfExamples(i) + classifier.train.numberOfExamples(j);
           if (r[i][j] == 0 && r[j][i] == 0 && i != j)
           {
               r[i][j] = r[j][i] = 0.5;
           }
           //else
          //     n[i][j] = n[i][j] / classifier.train.getnData();
         //  n[i][j] = n[i][j] / (r[i][j] * (1 - r[i][j]));
        }
     }
    // Initialize p and u array
    double[] p = new double[r.length];
    for (int i = 0; i < p.length; i++) {
        for (int j = 0; j < p.length; j ++)
            if (i != j)
                p[i] += r[i][j];
        p[i] = p[i] * (2 / (double)nClasses) /  (double)(nClasses - 1);
    }

    double[][] u = new double[r.length][r.length];
    for (int i = 0; i < r.length; i++) {
      for (int j = i + 1; j < r.length; j++) {
        u[i][j] = p[i] / (p[i] + p[j]);
      }
    }

    // firstSum doesn't change
    double[] firstSum = new double[p.length];
    for (int i = 0; i < p.length; i++) {
      for (int j = i + 1; j < p.length; j++) {
        firstSum[i] += n[i][j] * r[i][j];
        firstSum[j] += n[i][j] * (1 - r[i][j]);
      }
    }

    // Iterate until convergence
    boolean changed;
    do {
      changed = false;
      double[] secondSum = new double[p.length];
      for (int i = 0; i < p.length; i++) {
        for (int j = i + 1; j < p.length; j++) {
          secondSum[i] += n[i][j] * u[i][j];
          secondSum[j] += n[i][j] * (1 - u[i][j]);
        }
      }
      for (int i = 0; i < p.length; i++) {
        if ( (firstSum[i] == 0) || (secondSum[i] == 0)) {
          if (p[i] > 0) {
            changed = true;
          }
          p[i] = 0;
        }
        else {
          double factor = firstSum[i] / secondSum[i];
          double pOld = p[i];
          p[i] *= factor;
          if (Math.abs(pOld - p[i]) > 1.0e-3) {
            changed = true;
          }
        }
      }
      normalize(p);
      for (int i = 0; i < r.length; i++) {
        for (int j = i + 1; j < r.length; j++) {
          u[i][j] = p[i] / (p[i] + p[j]);
        }
      }
    }
    while (changed);
    return p;
  }

  /**
   * It computes the confidence vector for the classes using the second method 
   *  from the multiclass_prob paper by Wu, Lin, and Weng
   * @param r array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  private double[] method2WuJMLR(double[][] r)
  {

     int k = this.nClasses;
     double[] p = new double[k];
          int t,j;
          int iter = 0, max_iter=Math.max(100,k);
          double[][] Q=new double[k][k];
          double[] Qp= new double[k];
          double pQp, eps=0.005/k;

          for (int i = 0; i < r.length; i++)
             for (int i2 = 0; i2 < r.length; i2++)
             {
                if (i != i2 && r[i][i2] == 0 & r[i2][i] == 0)
                {
                   r[i][i2] = 0.5;
                   r[i2][i] = 1 - r[i][i2];
                }
                if (i != i2 && r[i][i2] == 0)
                {
                   r[i][i2] = 0.000000000000001;//Double.MIN_VALUE;
                   r[i2][i] = 1 - r[i][i2];//0.999999999999999;
                }
               /* else if (i == i2)
                   r[i][i2] = 1.0; */
             }

          for (t=0;t<k;t++)
          {
                  p[t]=1.0/k;  // Valid if k = 1
                  Q[t][t]=0;
                  for (j=0;j<t;j++)
                  {
                          Q[t][t]+=r[j][t]*r[j][t];
                          Q[t][j]=Q[j][t];
                  }
                  for (j=t+1;j<k;j++)
                  {
                          Q[t][t]+=r[j][t]*r[j][t];
                          Q[t][j]=-r[j][t]*r[t][j];
                  }
          }
          for (iter=0;iter<max_iter;iter++)
          {
                  // stopping condition, recalculate QP,pQP for numerical accuracy
                  pQp=0;
                  for (t=0;t<k;t++)
                  {
                          Qp[t]=0;
                          for (j=0;j<k;j++)
                                  Qp[t]+=Q[t][j]*p[j];
                          pQp+=p[t]*Qp[t];
                  }
                  double max_error=0;
                  for (t=0;t<k;t++)
                  {
                          double error=Math.abs(Qp[t]-pQp);
                          if (error>max_error)
                                  max_error=error;
                  }
                  if (max_error<eps)
                     break;

                  for (t=0;t<k;t++)
                  {
                          double diff=(-Qp[t]+pQp)/Q[t][t];
                          p[t]+=diff;
                          pQp=(pQp+diff*(diff*Q[t][t]+2*Qp[t]))/(1+diff)/(1+diff);
                          for (j=0;j<k;j++)
                          {
                                  Qp[j]=(Qp[j]+diff*Q[t][j])/(1+diff);
                                  p[j]/=(1+diff);
                          }
                  }
          }
          if (iter>=max_iter)
                  System.out.println("Exceeds max_iter in multiclass_prob\n");
          return p;
  }
  
  /**
   * It computes the confidence vector for the classes using the non-dominance criterion
   * @param tabla array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  protected double[] computeClassScoresND(double[][] tabla)
  {
      /* Compute the strict preference relation */
      for (int x = 0; x < nClasses; x++)
          for(int y = x + 1; y < nClasses; y++)
          {
                double aux_tabla = tabla[x][y];
                tabla[x][y] -= tabla[y][x];
                tabla[y][x] -= aux_tabla;
                if (tabla[x][y] < 0) {
                  tabla[x][y] = 0;
                }
                if (tabla[y][x] < 0) {
                  tabla[y][x] = 0;
                }
          }

      /* Compute the non-cominance degree of each class */
        double[] max = new double[nClasses];
        double max_clase;
         for (int i = 0; i < nClasses; i++) {
          max_clase = -1.0;
          for (int j = 0; j < nClasses; j++) {
            if (tabla[j][i] > max_clase) {
              max_clase = tabla[j][i];
            }
          }
          max[i] = 1.0 - max_clase;
        }
        return max;

  }

  /**
   * It computes the confidence vector for the classes using LVPC (Learning
   * valued preference for Classification) method
   * @param tabla array containing the outputs of the classifiers for the instance
   * @return the confidence vector
   */
  protected double[] computeClassScoresLVPC(double[][] tabla) {

    // retrieve the P,C,I matrices
    double[][][] relationSet = relationsForInstance(tabla);
    double[][] preferenceRelation = relationSet[0];
    double[][] conflictRelation = relationSet[1];
    double[][] ignoranceRelation = relationSet[2];

    // calculate the class scores
    double[] classScores = new double[preferenceRelation.length];
    for (int i = 0; i < preferenceRelation.length; i++) {
      for (int j = 0; j < preferenceRelation[i].length; j++) {
        if (i != j) {
          classScores[i] +=
              preferenceRelation[i][j] +
              conflictRelation[i][j] * 0.5 +
              ignoranceRelation[i][j] *
              (classifier.aprioriClassDistribution[i] /
               (classifier.aprioriClassDistribution[i] +
                classifier.aprioriClassDistribution[j]))
              ;

        }
      }
      if (Double.isNaN(classScores[i])) {
        classScores[i] = 0;
      }
    }

    if (sum(classScores) == 0) {
      return classScores;
    }

    normalize(classScores);
    return classScores;
  }

  protected double[] computeClassScoresWeighted(double[] tabla)
  {
    double [] max = new double[nClasses];
    for (int i = 0; i < nClasses; i++) {
      double sum_clase = 0.0;
      for (int j = 0; j < nClasses; j++) {
        sum_clase += tabla[i * nClasses + j];// > tabla[j * nClasses + i] ? tabla[i * nClasses + j] : 0;
      }
      max[i] = sum_clase / (double)nClasses;
    }
    return max;
  }  
  
  protected String computeClassScoresDynamic(int i) {
	    // String output = new String("?");
	    double[] max;
	    String output = "?";
	        
	    double[] tabla = tablas[i];
	    boolean distEu = true;
	    if (Attributes.hasNominalAttributes())
	        distEu = false;
	    if (Attributes.hasRealAttributes() || Attributes.hasIntegerAttributes())
	        distEu = true;
	    
	    kvecinos = (int)(nClasses * 3); // 3 está probado!
	    int kvecinosAux = kvecinos;
	    metodo.setK(kvecinos);
	    int[] vecinos = null, votos = null;
	    double[] distancias = null;
	    int classesInNeighborhood = 0;
	    double limit = 1;
	    
	    while (classesInNeighborhood <= limit && kvecinosAux <= kvecinos * 2) {
	        vecinos = new int[kvecinosAux];
	        distancias = new double[kvecinosAux];
	        votos = new int[nClasses];
	        metodo.evaluaKNN(i, vecinos, distancias, votos, test, distEu);
	        classesInNeighborhood = 0;
	        for (int j = 0; j < votos.length; j++) {
	            if (votos[j] > 0)
	                classesInNeighborhood++;
	        }
	        
	        kvecinosAux++;
	        metodo.setK(kvecinosAux);
	    }
	    
	    double probs[] = new double[nClasses];
	    double sum_probs = 0;   
	    int nClassesTest = 0;
	    for (int j = 0; j < vecinos.length; j++) {
	        if (vecinos[j] != -1) {
	            probs[classifier.train.getOutputAsInteger()[vecinos[j]]] += 1.0 / distancias[j];
	            sum_probs += 1.0 / distancias[j];
	            
	        }
	    }
	    if (sum_probs > 0){
	        for (int j = 0; j < nClasses; j++) {
	            if (votos[j] > 0)
	                nClassesTest++;
	            probs[j] /= sum_probs;
	        }
	    }
	    
	    double[][] tablaNew = new double[nClassesTest][nClassesTest];
	    int fila = 0;
	    if (sum_probs > 0) { // (classesInNeighborhood > 1) {//
	        max = new double[nClasses];
	        for (int j = 0; j < nClasses; j++) {
	            double sum_clase = 0;
	            double tot = 0;
	            if (votos[j] > 0) {
	                int columna = 0;
	                for (int k = 0; k < nClasses; k++) {
	                    if (votos[k] > 0) {
	                        sum_clase += tabla[j * nClasses + k];
	                        tot++;
	                        tablaNew[fila][columna] = tabla[j * nClasses + k];
	                        columna++;
	                    }
	                    else
	                        tabla[j * nClasses + k] = 0;
	                }
	                fila++;
	            }
	            else {
	                for (int k = 0; k < nClasses; k++) {
	                    tabla[j * nClasses + k] = 0;
	                }
	            }
	            if (tot != 0)
	                max[j] = sum_clase / tot;//* probs[j]; //(double)nClasses;// * probs[j];
	            else
	                max[j] = -1;
	        }
	        double[] max2 = null; //this.computeClassScoresPC(tablaNew); //method2WuJMLR
	        max2 = computeClassScoresWeighted(tablaNew);
	        
	        fila = 0;
	        max = new double[nClasses];
	        for (int j = 0; j < nClasses; j++) {
	            if (votos[j] > 0) {
	                max[j] = max2[fila];
	                fila++;
	            }
	            else
	                max[j] = -1;
	        }
	    }
	    else {
	         max = computeClassScoresWeighted(tabla);
	    }
	    
	    output = getOutputTies(max);
	    return output;

	  }
  
  
  /**
   * It obtains the class scores for the OVO scheme
   * @param example
   * @return
   */
  protected String computeClassScoresOVA(double[] example) {
	  String output = new String("?");
	  double[] grado_asoc = classifier.ova_table(example);
	  output = getOutputTies(grado_asoc);
	  return output;
  } 

  /**
   * Retrieves the preference, conflict and ignorance matrices for a single instance
   * @param inst The instance for which the PCI-matrices shall be created
   * @return [0][][] preference matrix, [1][][] conflict matrix, [2][][] ignorance matrix
   * @throws Exception
   */
  public double[][][] relationsForInstance(double[][] tabla) {
    double[][] prefRelation = new double[nClasses][nClasses];
    double[][] conflictRelation = new double[nClasses][nClasses];
    double[][] ignoranceRelation = new double[nClasses][nClasses];

    double s0, s1;

    for (int x = 0; x < nClasses; x++)
        for (int y = 0; y < nClasses; y++)
        {
            s0 = tabla[x][y];
            s1 = tabla[y][x];

            double min = s0 < s1 ? s0 : s1;
            double max = s0 > s1 ? s0 : s1;

            prefRelation[x][y] = s0 - min;
            prefRelation[y][x] = s1 - min;
            conflictRelation[x][y] = min;
            conflictRelation[y][x] = min;
            ignoranceRelation[x][y] = 1 - max;
            ignoranceRelation[y][x] = 1 - max;

        }

    return new double[][][] {
        prefRelation, conflictRelation,
        ignoranceRelation};
  }


  /**
   * It returns the output class for the array containing the confidences for each class
   * @param max Array with the confidences for each class
   * @return The output class
   */
  String getOutputTies(double[] max) {

    /*
     * Tie-breaking step 1: Find out which classes gain the maximum score
     */
    double maxValue = max[maxIndex(max)];
    double[] ties = new double[max.length];
    for (int i = 0; i < max.length; i++) {
      if (max[i] == maxValue) {
        ties[i] = classifier.aprioriClassDistribution[i];
      }
    }

    max = new double[max.length];
    max[maxIndex(ties)] = 1;

    /*
     * Tie-breaking step 2: Check whether the tying classes have the same a priori
     * class probability and count these classes.
     */
    int tieValues = 0;
    maxValue = ties[maxIndex(ties)];
    for (int i = 0; i < ties.length; i++) {
      if (ties[i] == maxValue) {
        tieValues++;
      }
    }

    /*
     * Tie-breaking step 3: If the tying classes have the same a priori probabilities,
     * then use randomization to determine the winner among these classes
     */
    if (tieValues > 1) {
      tieValues = 0;
      maxValue = ties[maxIndex(ties)];
      int[] stillTying = new int[ties.length];

      for (int i = 0; i < max.length; i++) {
        if (ties[i] == maxValue) {
          stillTying[tieValues] = i;
          tieValues++;
        }
      }
      return classifier.train.getOutputValue(stillTying[0]);
    }
    return classifier.train.getOutputValue(maxIndex(max));
  }


  /**
   * Normalizes the doubles in the array by their sum.
   *
   * @param doubles the array of double
   * @exception IllegalArgumentException if sum is Zero or NaN
   */
  public static void normalize(double[] doubles) {

    double sum = 0;
    for (int i = 0; i < doubles.length; i++) {
        if (!Double.isNaN(doubles[i]))
            sum += doubles[i];
    }
    normalize(doubles, sum);
  }

  /**
   * Normalizes the doubles in the array using the given value.
   *
   * @param doubles the array of double
   * @param sum the value by which the doubles are to be normalized
   * @exception IllegalArgumentException if sum is zero or NaN
   */
  public static void normalize(double[] doubles, double sum) {

    if (Double.isNaN(sum)) {
      throw new IllegalArgumentException("Can't normalize array. Sum is NaN.");
    }
    if (sum == 0) {
        return;
    }
    for (int i = 0; i < doubles.length; i++) {
        doubles[i] /= sum;
    }
  }

  /**
   * It obtains the sum of the values of an array
   * @param array the array containing the values
   * @return the sum of the values in the array
   */
  double sum(double[] array) {
    double sum = 0.0;
    for (int i = 0; i < array.length; i++) {
      sum += array[i];
    }
    return sum;
  }

  /**
   * This returns the index containing the maximum value of an array
   * @param array the array from which to obtain the index of the maximum value
   * @return the index containing the maximum value of the array
   */
  int maxIndex(double[] array) {
    double max = array[0];
    int index = 0;
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
        index = i;
      }
    }
    return index;
  }

}
