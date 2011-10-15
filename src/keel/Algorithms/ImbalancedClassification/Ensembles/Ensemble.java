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
* @author Written by Mikel Galar Idoate (Universidad Pública de Navarra) 30/5/2010
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.ImbalancedClassification.Ensembles;


import keel.Algorithms.ImbalancedClassification.Ensembles.SMOTE.MSMOTE;
import keel.Algorithms.ImbalancedClassification.Ensembles.SMOTE.SMOTE;
import keel.Algorithms.ImbalancedClassification.Ensembles.SPIDER.SPIDER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.core.Randomize;

/**
 * Class to implement the different ensemble methods for class imbalance problems
   @author Mikel Galar Idoate (UPNA)
   @version 1.1 (17-05-10)
 */
class Ensemble {
    /* Parameters used by the ensemble */
   String ensembleType;
   String trainMethod;
   int nClassifier;
   /* Iteration counter */
   int t;

   /* For Cost-Sensitive */
   /* Wether the costs are setled manually or they are configured depending on the IR */
   String costType;
   /* Costs for the majority and minority classes */
   double CostMaj, CostMin;      
   


    /* References to the original and the actual (modified by adaboost's data-distribution change) datasets */
   myDataset originalDS, actualDS;
   /* weights of the instances for adaboost and weight of each classifier in the voting*/
   double[] weights, alfa;
   
   /* Backup of the weights prior to the preprocessing, needed for RUSBoost and SMOTEBoost */
   double[] weightsBackup; 
   /* Percentage (N%) of the instances from the mayority class used in RUSBoost
    * Quantity of balancing for SMOTEBoosting and MSMOTEBoosting if N > 100 (else, the classes are balanced
    * in SMOTEBagging and MSMOTEBagging the classes are always balanced 
    */
   int N;
   
   /* Number of instances */
   int nData;

   /* Number of majority and minority examples and their corresponding integer value*/
   int nMaj, nMin;
   int majC, minC;

   /* Number of bags for EasyEnsemble and BalanceCascade techniques */
   int nBags;
   
   /* Number of boosting iterations for EasyEnsemble and BalanceCascade */
   int nBoostIterations;
   /* Theta values used in BalanceCascade for eliminating instances from the data-set after completing a bag */
   double[] teta;
   
   /* Array to store the indexes of the previous data-set in the new one */
   int[] selected;

   /* b parameters used in UnderOverBagging */ //ATENCION CREO QUE A NO SE USA
   int b; //UNDEROVERBAGGING and SMOTE/MSMOTEBagging

   

   /* Type of SPIDER preprocssing  WEAK / RELABEL / STRONG */
   String spiderType;
   
   /* Out-of-Bag estimation */
   double e;
   /* ArrayList containg wether an instance was used to train a classifier of the ensemble or not 
    * it is used in the computation of the Out-of-Bag  error estimation
    */
   ArrayList<boolean[]> trainingSetsOOB;
   /* Array for IIVOTES, it stores wether an instance in the out-of-bag estimation was correctly predicted or not 
    * (if the instance was used for training, the prediction is false */
   boolean[] predictions;

   /*  Wether the preparation of the data-set is needed or not */
   boolean prepareDSNeeded;
   
   /* Wether to develop a resampling in boosting procedures or to use the weights to train each tree */
   boolean resampling;

   /* Reference to the base classifiers */
   multi_C45 classifier;

   /** Constructor of the ensemble
    *
    * @param originalDS original data-set to perform the ensemble learning
    * @param nClassifier number of maximum classifiers in to boost
    * @param classifier the reference to the base classifiers
    */
   public Ensemble(String ensembleType, myDataset originalDS, int nClassifier, multi_C45 classifier) {
      /* Reading the ensemble type and the original data-set */
      this.ensembleType = ensembleType;
      this.originalDS = originalDS;

      /* Initialization of helper variables */
      nData = originalDS.getnData();
      majC = originalDS.claseNumerica(originalDS.claseMasFrecuente());
      nMaj = originalDS.numberInstances(majC);
      minC = majC == 0 ? 1 : 0;
      nMin = originalDS.numberInstances(minC);

      /* Initialize the weightes uniformly */
      weights = new double[nData];
      for (int i = 0; i < nData; i++)
         weights[i] = 1.0 / (float)nData;
 
      /* Initialize the data-set */
      actualDS = originalDS;
      this.nClassifier = nClassifier;
      
      /* First iteration*/
      t = 0;
      alfa = new double[nClassifier];
      this.classifier = classifier;
      prepareDSNeeded = false;

      /*************************************************************************
       *            Read the configuration depending on the ensemble type      *
       *************************************************************************
       */
      int nextParameter = 6;
      /* IIVotes ensemble, IVotes + SPIDER! */
      if (ensembleType.equalsIgnoreCase("IIVOTES")) 
      {
         /* The data-set has to be preprocessed each time with SPIDER */
         prepareDSNeeded = true;
         
         /* Read the seed to initialize the randomization */
         Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
         
         /* Read the type of SPIDER preprocessing */
         spiderType = classifier.parameters.getParameter(nextParameter++);
         
         /* Set the initial out-of-bag estimation to 0.5 and initialize
          * the structures used in OOB estimation 
          */
         e = 0.5;
         trainingSetsOOB = new ArrayList<boolean[]>();
         predictions = new boolean[nData];
         
         /* This method does not require more parameters */
         return;
      }

      /* Bagging-based ensembles */
      if  (ensembleType.contains("BAG"))
      {
          /* Read the seed to initialize the randomization  to perform the bootstrapping */
         Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
         
         /* Multiples for UnderOverbagging and SMOTE/MSMOTEBagging, as recommended by their authors */
         b = 10;
      }
      /* Boosting-, and Hybrid-based ensembles */
      else if (ensembleType.contains("ADA") || ensembleType.contains("BOOST")
              || ensembleType.equalsIgnoreCase("EASYENSEMBLE")
              || ensembleType.equalsIgnoreCase("BALANCECASCADE"))
      {
         /* Read the train method:
           * RESAMPLING: resampling to obtain the desired data distribution 
           * NORESAMPLING: use the weights to construct each tree 
           */
         trainMethod = classifier.parameters.getParameter(nextParameter++);
         if (trainMethod.equalsIgnoreCase("RESAMPLING"))
         {
            resampling = true;
            /* If resampling is used, we need to prepare the data-set and to read the seed */
            prepareDSNeeded = true;
            Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
         }
         
           /* AdaC2 ensemble, Cost-sensitive Adaboost (version 2 with costs inside the exponent part of AdaBoost */
          if (ensembleType.equalsIgnoreCase("ADAC2"))   
          {
                   /* Configure the costs, adaptive or manual */
                  costType = classifier.parameters.getParameter(nextParameter++);
                  if (costType.equalsIgnoreCase("ADAPTIVE")) // Adaptive costs
                  {
                     CostMaj = (double)nMin /  (double)nMaj;
                     CostMin = 1.0;
                  }
                  else                                       // Manual costs
                  {
                      CostMaj = Float.parseFloat(classifier.parameters.getParameter(nextParameter++));
                      CostMin = Float.parseFloat(classifier.parameters.getParameter(nextParameter++));
                  }
          }
      

         /* RUSBoost, SMOTE/MSMOTEBoost, that is, Boosting with preprocessing */
         if (ensembleType.contains("RUSBOOST") || ensembleType.contains("SMOTEBOOST"))
         {
            /* Read the percentage of the majority class instances in the new data-set */
            N = Integer.parseInt(classifier.parameters.getParameter(nextParameter++));
            prepareDSNeeded = true;
            Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
         }
         /* DATABOOST-IM algorithm */
         else if (ensembleType.equalsIgnoreCase("DATABOOST-IM"))
            prepareDSNeeded = true;
         /* Hybrid-based ensembles, EasyEnsemble and BalanceCascade, 
          * its unique difference is that BalanceCascade removes some instances from the data-set each iteration
          */
         else if (ensembleType.equalsIgnoreCase("EASYENSEMBLE") || ensembleType.equalsIgnoreCase("BALANCECASCADE"))
         {
             /* Preparation is neede */
            prepareDSNeeded = true;
            Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
            
            /* Read the number of bags */
            nBags = Integer.parseInt(classifier.parameters.getParameter(nextParameter++));
            /* nClassifier is the number of boosting iterations in this case */
            nBoostIterations = nClassifier;
            /* The final number of classifiers is the number of bags times the number of adaboost iterations used in each bag */
            this.nClassifier = nClassifier * nBags;
            alfa = new double[this.nClassifier];
            /* In BalanceCascade we need to initialize the thetas */
            if (ensembleType.equalsIgnoreCase("BALANCECASCADE"))
               teta = new double[this.nClassifier];
         }
      }    
   }

   /** Method to perform the voting strategy
    *
    * @param example instance to be predicted
    * @return the predicted class
    */
   String computeClassScores(double[] example) {
      double sum = 0;       // Weighted voting sum
      double confidence = 1; // Initial confidence is 1, it is used for the ensembles not using classifiers' confidences.
      /* Each classifier votes */
      for (int t = 0; t < nClassifier; t++)
      {
         /* if alfa is 0, the classifier has not been initialized yet */
         if (alfa[t] != 0)
         {
             /* Ensembles which do not use the confidences */
            if (!(ensembleType.equalsIgnoreCase("ADAC2")
                    || ensembleType.equalsIgnoreCase("ADABOOST")
                    || ensembleType.equalsIgnoreCase("EASYENSEMBLE")
                    || ensembleType.equalsIgnoreCase("BALANCECASCADE")
                    || ensembleType.equalsIgnoreCase("ADABOOST.M1")
                    || ensembleType.equalsIgnoreCase("DATABOOST-IM")))
               confidence = classifier.obtainConfidence(t, example);
            /* A positive or a negative vote given depending on the predicted class */
            if (classifier.obtainClass(t, example) == 0)
               sum += confidence * alfa[t];
            else
               sum -= confidence * alfa[t];
         }
         /* The adjusted theta is used in BalanceCascade */
         if (teta != null)
            sum -= teta[t];
      }
      /* The output class is selected depending on the sign of the weighted voting */
      if (sum >= 0)
         return originalDS.getOutputValue(0);
      else
         return originalDS.getOutputValue(1);
   }

   /** The next iteration of the ensemble is performed depending on the type of ensemble 
    *
    * @return true if the ensemble construction is finished
    */
   boolean nextIteration()
   {
      boolean fin = false;  // Wether the ensembles is finished or not */
      
      /* For boosting-, and hybrid-based ensembles, the weights are updated */
      if (ensembleType.contains("ADA") || ensembleType.contains("BOOST")
              || ensembleType.contains("EASYENSEMBLE") || ensembleType.contains("BALANCECASCADE"))
         fin = modifyWeights();
      /* Bagging-based ensembles always use alfa = 1 */
      else if (ensembleType.contains("BAG"))
         alfa[t] = 1;
      /* IIVotes needs to estimate the Out-of-bag error */
      if (ensembleType.contains("IIVOTES"))
      {
         alfa[t] = 1;
         // OUT-OF-BAG ESTIMATION OF e(i)
         double e_i = outOfBagEstimation(originalDS, predictions); // if e(i) >= e(i - 1) then fin = true
         System.out.println("OOB error before = " + e);
         System.out.println("OOB error = " + e_i);
         e_i = 0.75 * e + 0.25 * e_i;
         
         System.out.println("OOB error = " + e_i);
         if (e_i < e)
            e = e_i;
         else
         {
            fin = true;
            alfa[t] = 0;
         }


      }
      /* The iteration counter is increased */
      t++;
      /* The errors of the actual ensemble are computed and shown */
      double total = classifier.prueba(actualDS);
      System.out.println("Train err = " + total);
      total = classifier.prueba(originalDS);
      System.out.println("Train original err = " + total);
      total = classifier.prueba(classifier.test);
      System.out.println("Test err = " + total);
      return fin;
   }

   /** Theta is adjusted for the corresponding bag of BalanceCascade, given that the adjustation algorithm
    * is not explained, we eliminate those instances from the majority class correctly classified
    * in the current bag (with the highest confidence)
    * 
    * @param bagNumber The bag number for which theta has to be adjusted
    * @return instances which has been correctly predicted by the current bag
    */
   private boolean[] adjustTheta(int bagNumber)
   {

      boolean[] aciertos = new boolean[originalDS.getnData()];

      double f = Math.pow((double)originalDS.numberInstances(minC) / (double)originalDS.numberInstances(majC), 1.0 / ((double)nBoostIterations - 1.0));
      final Integer[] indexes = new Integer[originalDS.getnData()];
      final double[] outputs = new double[originalDS.getnData()];
      for (int i = 0; i < originalDS.getnData(); i++)
      {
         double[] example = originalDS.getExample(i);
         double sum = 0;
         double confidence = 1;
         for (int t = bagNumber * nBoostIterations; t < (bagNumber + 1) * nBoostIterations; t++)
         {
            if (alfa[t] != 0)
            {
               if (classifier.obtainClass(t, example) == 0)
                  sum += confidence * alfa[t];
               else
                  sum -= confidence * alfa[t];
            }
         }
         outputs[i] = sum;
         indexes[i] = i;
      }

      Arrays.sort(indexes, new Comparator<Integer>() {
         @Override public int compare(final Integer o1, final Integer o2) {
            return Double.compare(outputs[o2], outputs[o1]);
          }
      });

      double FPrate = fprate(outputs,  0, aciertos);
      System.out.println(f + " == " +FPrate);

      return aciertos;
   }

   /** Computes the FPrate 
    * 
    * @param outputs the outputs of each instance using the current bag
    * @param teta theta parameter
    * @param corrects wether the instance has been correctly classified or not
    * @return FPrate
    */
   double fprate(double[] outputs, double teta, boolean[] corrects)
   {
      double TP = 0, FP = 0, FN = 0, TN = 0;
      for (int i = 0; i < originalDS.getnData(); i++) {

         int c = (outputs[i] - teta >= 0 ? 0 : 1);
         int cReal = originalDS.getOutputAsInteger(i);
         if (c == cReal)
            corrects[i] = true;
         else
            corrects[i] = false;
           if (c == cReal && cReal == majC)
              TN++;
           else if (c == cReal && cReal != majC)
              TP++;
           else if (c != cReal && cReal == majC)
              FP++;
           else
              FN++;
        }

       return FP / (FP + TN);
   }

   /** Preparation of the data-set for Hybrid-based methods 
    *   The data-set is only prepared nBag times, that is, when t % nBoostIterations == 0
    *   For BalanceCascade, the theta has to be adjusted and the corresponding instances are eliminated
    */
   private void prepareDatasetEasyEnsembleBalanceCascade()
   {
       /* Solo se prepara el dataset cuando nBag veces, para cascade, cuando termina
        * Hay que ajustar teta y eliminar del original
        */
      if (t % nBoostIterations == 0)
      {
         if(ensembleType.equalsIgnoreCase("BALANCECASCADE") && t > 0)
         {
            /* If t > 0, adjust theta such that FPrate = FP / (Fp + TN) = f = \sqrt{T-1}{nMin/nMaj}
             * The adjust procedure is not well-explained in the paper, so we delete 
             * the Majority class that have been correctly classified by the current classifier (only those that were used in training)
             */
            boolean[] correct = adjustTheta(t / nBoostIterations - 1);

            // Delete the examples from the Majority class
            originalDS.deleteExamples(correct, selected, minC);
            nData = originalDS.getnData();
         }
         // Create the actualDS with RandomUnderSampling  of the majority class (50%)
         actualDS = new myDataset(originalDS);
         selected = actualDS.randomUnderSampling(originalDS, majC, 50); //N% of the total will be from the majority class
         
         /* The weights are uniformly initialized for the adaboost ensemble */
         weights = new double[actualDS.size()];
         for (int i = 0; i < actualDS.size(); i++)
            weights[i] = 1.0 / (float)actualDS.size();
      }
      
   }
   
   /** Preparation of the data-set for RUSBoost
    *   The data-set is resampled and the weight distribution is changed 
    *   in order to form a distribution with the remaining weights
    */
   private void prepareDatasetRUSBoost()
   {
      weightsBackup = weights.clone();
      // Create the actualDS with RandomUnderSampling (N%, usually 50%) of the majority class 
      actualDS = new myDataset(originalDS);
      selected = actualDS.randomUnderSampling(originalDS, majC, N); //N% of the total will be from the majority class
      // The original weights are stored and the new weights are recalculated
      // Selected has the indexes of the instances from the previous data-set in the new one 

      weights = new double[selected.length];
      double Z = 0;
      for (int i = 0; i < selected.length; i++)
      {
         weights[i] = weightsBackup[selected[i]];
         Z += weights[i];
      }

      for (int i = 0; i < selected.length; i++)
         weights[i] /= Z;
   }

   /** Preparation of the data-set for SMOTEBoost and MSMOTEBoost
    *   First, the data-set is preprocessed with SMOTE or MSMOTE and then 
    *   the weight distribution is changed in order to form a distribution with the new instances
    */
   private void prepareDatasetSMOTEBoost()
   {
     System.out.println("Applying Preprocessing...[" + t + "]");
     actualDS = null;
     if (ensembleType.contains("MSMOTE"))
     {
        originalDS.getIS().setAttributesAsNonStatic();
        // MSMOTE configuration, the seed, kClean = 3, k = 5 or nMin, use both classes, balance, quantity of balancing, and distance = HVDM
        MSMOTE preprocess = new MSMOTE(originalDS.getIS(),
                Math.round(Randomize.Randdouble(0, 12345678.0)), 3,         
                this.nMin > 5 ? 5 : nMin, 0, true,
                N < 100 ? 1.0 : (double)N / 100.0, "HVDM");
        preprocess.ejecutar();
        preprocess = null;
     }
     else
     {
         // SMOTE configuration, the seed, k = 5 or nMin, use both classes, balance, quantity of balancing, and distance = HVDM
        SMOTE preprocess = new SMOTE(originalDS.getIS(),            
                Math.round(Randomize.Randdouble(0, 12345678.0)),
                this.nMin > 5 ? 5 : nMin, 0, true,
                N < 100 ? 1.0 : (double)N / 100.0, "HVDM");
        preprocess.ejecutar();
        preprocess = null;
     }
     try {
         /* Read the preprocessed data-set */
       actualDS = new myDataset();
       actualDS.readClassificationSet("train.tra", false);
     }catch (IOException e) {
       System.err.println("There was a problem while reading the input preprocessed data-sets: " + e);
     }
     
     /* Store the original weights */
     weightsBackup = weights.clone();
         
     /* Recompute the weights with the new instances */
      weights = new double[actualDS.getnData()];
      for (int i = 0; i < actualDS.getnData(); i++)
      {
         if (i < nData) // old ones
            weights[i] = weightsBackup[i] * (double)nData / (double)actualDS.getnData();
         else // new ones
            weights[i] = 1.0 / (double)actualDS.getnData();
      }

   }

   /** Preparation of the data-set for IIVotes (with SPIDER preprocessing)
    *   First, the data-set is preprocessed with SMOTE or MSMOTE and then 
    *   the weight distribution is changed in order to form a distribution with the new instances
    */   
   private void prepareDatasetSPIDER()
   {
     System.out.println("Applying Preprocessing...[" + t + "]");
     actualDS = null;

     
     /* The new data-set is formed using importance sampling which depends on the previous predictions and the OOB error estimation */
     boolean[] used = null;
     do {
        actualDS = new myDataset(originalDS);
        used = actualDS.importanceSampling(originalDS, (int)(originalDS.getnData() / 2), predictions, e);
     } while (actualDS.vacio());

     /* The instances of this data-set are stored to perform the OOB (after training) */
      this.trainingSetsOOB.add(used.clone());

      /* The SPIDER preprocessing is carried out in the actual data-set */
        SPIDER preprocess = new SPIDER(actualDS.getIS(), 3, spiderType, "HVDM");
        preprocess.ejecutar();
        preprocess = null;
        actualDS.getIS().clearInstances();
        actualDS = null;
     try {
       /* The preprocessed data-set is read */
       actualDS = new myDataset();
       actualDS.readClassificationSet("train.tra", false);
     }catch (IOException e) {
       System.err.println("There was a problem while reading the input preprocessed data-sets: " + e);
     }


   }

 /** Preparation of the data-set for DataBoost-IM
    *   First, it identifies hard examples (seeds) and then the data-set is rebalanced
    */  
  private void prepareDatasetDataBoostIM()
   {
     System.out.println("Preparing Data-set for DataBoost-IM...");
     weightsBackup = weights.clone();
     if (t < 1)
        return;

     // Identify hard examples
     int Ns = 0;
     final Integer[] indexes = new Integer[nData];
      for (int i = 0; i < nData; i++)
      {
         if (classifier.obtainClass(t - 1, originalDS.getExample(i)) != originalDS.getOutputAsInteger(i))
            Ns++;
         indexes[i] = i;
      }

      Arrays.sort(indexes, new Comparator<Integer>() {
         @Override public int compare(final Integer o1, final Integer o2) {
            if (Double.compare(weights[o2], weights[o1]) == 0)
               if (originalDS.getOutputAsInteger(o2) == minC)
                  return +1;
               else if (originalDS.getOutputAsInteger(o1) == minC)
                  return -1;
               else
                  return 0;
            else
              return Double.compare(weights[o2], weights[o1]);
          }
      });

      
      /* Compute the number of seeds for each class */
      int Nsmaj = 0, Nsmin = 0;
      for (int i = 0; i < Ns; i++)
      {
         if (originalDS.getOutputAsInteger(indexes[i]) == majC)
            Nsmaj++;
         else
            Nsmin++;
      }

      /* Compute the final number of seeds */
      int Ml = Math.min(nMaj / nMin, Nsmaj);
      int Ms = Math.min(nMaj * Ml / nMin, Nsmin);
      /* Create the syntetic instances for each class and set their weights */
      originalDS.computeStatisticsPerClass();
      double[][] Xmaj = createSynteticData(majC, Ml, nMaj);
      double[][] Xmin = createSynteticData(minC, Ms, nMin);
      double[] weightsSeedsMaj = new double[Ml], weightsSeedsMin = new double[Ms];
      int auxMl = 0, auxMs = 0;
      for (int i = 0; i < Ns; i++)
      {
         if (originalDS.getOutputAsInteger(i) == majC && auxMl < Ml) {
            weightsSeedsMaj[auxMl] = weights[indexes[i]]; auxMl++;
         }
         else if ( auxMs < Ms ) {
            weightsSeedsMin[auxMs] = weights[indexes[i]]; auxMs++;
         }
      }

     // Add syntetic data to the data-set
      actualDS = new myDataset(originalDS, majC, Xmaj, minC, Xmin);

     // Update weights
      int newNData = nData + Ml * nMaj + Ms * nMin;
      weightsBackup = weights.clone();
      weights = new double[newNData];
      int iAux = 0;
      for (int i = 0; i < nData; i++, iAux++)
            weights[iAux] = weightsBackup[i];
      for (int i = nData; i < nData + Ml; i++)
         for (int j = 0; j < nMaj; j++, iAux++)
            weights[iAux] = weightsSeedsMaj[i - nData] / (double)nMaj;
      for (int i = nData + Ml; i < nData + Ml + Ms; i++)
         for (int j = 0; j < nMin; j++, iAux++)
            weights[iAux] = weightsSeedsMin[i - nData - Ml] / (double)nMin;

      // Rebalance! weights
      double Wmaj = 0, Wmin = 0;
      for (int i = 0; i < newNData; i++)
         if (actualDS.getOutputAsInteger(i) == majC)
            Wmaj += weights[i];
         else
            Wmin += weights[i];
      if (Wmaj > Wmin)
         for (int i = 0; i < newNData; i++){
            if (actualDS.getOutputAsInteger(i) == minC)
               weights[i] *= Wmaj / Wmin;
         }
      else
         for (int i = 0; i < newNData; i++){
            if (actualDS.getOutputAsInteger(i) == majC)
               weights[i] *= Wmin / Wmaj;
         }


      double Z = 0;
      for (int i = 0; i < newNData; i++)
         Z += weights[i];
      for (int i = 0; i < newNData; i++)
         weights[i] /= Z;
      System.out.println("Preparartion finished!");
   }

  /** Create synthetic instances for the given class (DataBoost-IM)
   * 
   * @param c The class for which the examples must be created
   * @param nSets Number of instance sets to be created (number of seeds)
   * @param nExamples number of examples belonging to c in the original data-set
   */
   private double[][] createSynteticData(int c, int nSets, int nExamples)
   {
      int nInputs = originalDS.getnInputs();
      double[][] X = new double[nSets * nExamples][nInputs];
      if (nSets == 0)
         return X;
      // Necesito, de los nominales, para cada clase los norminales
      // Recorro todos los ejemplos y dep de la clase añado a uno o a otro
      // se que de cada nominal hay nMaj y nMin, pero tengo que contar cuantos hay norminales
      // a la vez que cuento, guardo media y std para numericos

      /* We need to obtain the average and std of the numeric attributes in the class
       * and for the nominal attributes the nominal values of the instances 
       */
      int nNominal = 0, nNumeric = 0;
      double[][] numeric = new double[nInputs][2];
      
      for (int i = 0; i < nInputs; i++)
      {
         if (originalDS.getTipo(i) == myDataset.NOMINAL || originalDS.getTipo(i) == myDataset.INTEGER)
         {
            nNominal++;
            numeric[i][0] = -1;
            numeric[i][1] = -1;
         }
         else
         {
            nNumeric++;
            numeric[i][0] = originalDS.getAveragePerClass()[c][i];
            numeric[i][1] = originalDS.getStdPerClass()[c][i];
         }
      }
      double[][] nominal = new double[nInputs][nExamples];
      int nAux = 0;
      for (int i = 0; i < nData; i++)
      {
         if (originalDS.getOutputAsInteger(i) == c)
         {
            for (int j = 0; j < nInputs; j++)
               if (numeric[j][0] == -1) // Nominal
                  nominal[j][nAux] = originalDS.getExample(i)[j];
            nAux++;
         }
      }

     /* Generate syntetic data based on the seeds */
      for (int i = 0; i < nSets; i++)
      {
         for  (int k = 0; k < nInputs; k++)
         {
            if (numeric[k][0] == -1) // nominal
            {
               double[] auxNominal = nominal[k].clone();
               int r;
               double aux;
               for (int j = 0; j < nExamples; j++)
               {
                  r = Randomize.RandintClosed(j, nExamples - 1);
                  aux = auxNominal[r];
                  auxNominal[r] = auxNominal[j];
                  auxNominal[j] = aux;
               }
               for (int j = 0; j < nExamples; j++)
                  X[i * nExamples + j][k] = auxNominal[j];

            }
            else // numeric
               for (int j = 0; j < nExamples; j++)
                  X[i * nExamples + j][k] = Randomize.RandGaussian() *
                          numeric[k][1] + numeric[k][0];
         }
      }
      return X;
   }

   /** This function prepares the data-set for the next bagging-based ensembles
    * iteration depending on the ensemble method selected.
    */
   private void nextBag()
   {
      if (ensembleType.equalsIgnoreCase("BAGGING"))
      {
          /* Bootstrapping */
         actualDS = new myDataset(originalDS);
         actualDS.randomSampling(originalDS, majC, minC, nMaj, nMin);
      }
      else if (ensembleType.equalsIgnoreCase("UNDERBAGGING"))
      {
          /* Undersampling */
         actualDS = new myDataset(originalDS);
         actualDS.randomUnderSampling(originalDS, majC, 50);
      }
      else if (ensembleType.equalsIgnoreCase("UNDERBAGGING2"))
      {
          /* Undersampling + sampling/bootstrapping of the minority */
         actualDS = new myDataset(originalDS);
         actualDS.randomSampling(originalDS, majC, minC, nMin, nMin);
      }
      else if (ensembleType.equalsIgnoreCase("OVERBAGGING"))
      {
          /* Oversampling */
         actualDS = new myDataset(originalDS);
         actualDS.randomUnderSampling(originalDS, minC, 50);
      }
      else if (ensembleType.equalsIgnoreCase("OVERBAGGING2"))
      {
          /* Oversampling + sampling/bootstrapping of the majority */
         actualDS = new myDataset(originalDS);
         actualDS.randomSampling(originalDS, majC, minC, nMaj, nMaj);
      }
      else if (ensembleType.equalsIgnoreCase("UNDEROVERBAGGING"))
      {
         actualDS = new myDataset(originalDS);
         // The sampling rate changes being always multiple of 10 
         if (t + 1 > (nClassifier / 10))    
             b += 10;
         /* (b% * Nmaj) instances are taken from each class */
         actualDS.randomSampling(originalDS, majC, minC, b);
      }
      else if (ensembleType.equalsIgnoreCase("SMOTEBAGGING"))
      {
          /* Both classes are balanced using SMOTE */
         actualDS = new myDataset(originalDS);
         if (t + 1 > (nClassifier / 10))
             b += 10;
         /* First resampling of b% * nMaj, then SMOTE to get a balanced set */
         actualDS.randomSampling(originalDS, majC, minC, nMaj, b * nMaj/ nMin);
         N = 50 ; // Balance
           System.out.println("Applying Preprocessing...[" + t + "]");
           // SMOTE configuration, the seed, k = 5 or nMin, use both classes, balance, quantity of balancing, and distance = HVDM
           SMOTE preprocess = new SMOTE(actualDS.getIS(),
                Math.round(Randomize.Randdouble(0, 12345678.0)),
                this.nMin > 5 ? 5 : nMin, 0, true,
                N < 100 ? 1.0 : (double)N / 100.0, "HVDM");
            preprocess.ejecutar();
           try {
               /* Read the preprocessed data-set */
             actualDS = new myDataset();
             actualDS.readClassificationSet("train.tra", false);
           }catch (IOException e) {
             System.err.println("There was a problem while reading the input preprocessed data-sets: " + e);
           }
      }
      else if (ensembleType.equalsIgnoreCase("MSMOTEBAGGING"))
      {
           /* Both classes are balanced using MSMOTE */
         actualDS = new myDataset(originalDS);
         if (t + 1 > (nClassifier / 10))
             b += 10;
         /* First resampling of b% * nMaj, then MSMOTE to get a balanced set */
         actualDS.randomSampling(originalDS, majC, minC, nMaj, b * nMaj/ nMin);
         N = 50 ;  // Balance
           System.out.println("Applying Preprocessing...[" + t + "]");
           // MSMOTE configuration, the seed, kClean = 3, k = 5 or nMin, use both classes, balance, quantity of balancing, and distance = HVDM
           MSMOTE preprocess = new MSMOTE(actualDS.getIS(),
                Math.round(Randomize.Randdouble(0, 12345678.0)), 3,
                this.nMin > 5 ? 5 : nMin, 0, true,
                N < 100 ? 1.0 : (double)N / 100.0, "HVDM");//MSMOTE("conf.txt");
           preprocess.ejecutar();
           preprocess = null;
           try {
               /* Read the preprocessed data-set */
             actualDS = new myDataset();
             actualDS.readClassificationSet("train.tra", false);
           }catch (IOException e) {
             System.err.println("There was a problem while reading the input preprocessed data-sets: " + e);
           }
      }

      /* In bagging-based ensembles the weights are uniformly distributed, there are no weights */
      weights = new double[actualDS.getnData()];
      for (int i = 0; i < actualDS.getnData(); i++)
         weights[i] = 1.0 / (float)actualDS.getnData();
   }

   /** It returns the weights' vector of the corresponding iteration. In case of AdaBoost and Boosting-based ensembles
    * the weights are given if the resampling is not selected, otherwise, the weights are uniformly distributed
    * because the resampling has been carried out considering the weight distribution
    * @return Weights' vector
    */
   double[] getWeights()
   {
      if ((ensembleType.contains("ADA") || ensembleType.contains("BOOST")) && 
              !trainMethod.equalsIgnoreCase("RESAMPLING"))
         return weights;
      else
      {
         double[] uniformWeights = new double[actualDS.getnData()];
         for (int i = 0; i < uniformWeights.length; i++)
            uniformWeights[i] = 1.0 / (double)uniformWeights.length;
         return uniformWeights;
      }
   }

   /** This function sets the costs for cost-sensitive boosting 
    * 
    * @param Cmaj Cost of misclassifying a majority class instance
    * @param Cmin Cost of misclassifying a minority class instance
    */
   void setCosts(double Cmaj, double Cmin)
   {
      this.CostMaj = Cmaj;
      this.CostMin = Cmin;
   }

   /** This method obtains the dataset for the next iteration
    * 
    * @return The new (prepared if it is neccessary) data-set
    */
   myDataset getDS()
   {
      if (prepareDSNeeded)
         prepareDataset();
      if (ensembleType.contains("BAG"))
         nextBag();
      return actualDS;
   }

   /** This method prepares the new data-set depending on the ensemle type. 
    * if the resampling is required (for boosting-based ensmbles), it is performed 
    * after preparing the data-set.
    */
   public void prepareDataset()
   {
      if (ensembleType.contains("RUSBOOST"))
         prepareDatasetRUSBoost();
      else if (ensembleType.contains("SMOTEBOOST"))
         prepareDatasetSMOTEBoost();
      else if (ensembleType.contains("IIVOTES"))
         prepareDatasetSPIDER();
      else if (ensembleType.contains("DATABOOST-IM"))
         prepareDatasetDataBoostIM();
      else if (ensembleType.contains("EASYENSEMBLE") || (ensembleType.contains("BALANCECASCADE")))
         prepareDatasetEasyEnsembleBalanceCascade();

      /* Boostrap the data-set instead of using the weights for training */
      if (resampling)
      {
         myDataset auxDS = new myDataset(actualDS, weights);
         actualDS = auxDS;
      }
   }

   /** The appropriate function to update the weights is selected depending on the method 
    * 
    * @return Wether the boosting algorithm has finished or not.
    */
   private boolean modifyWeights() {
      if (ensembleType.equalsIgnoreCase("ADABOOST"))
         return modifyWeightsAdaBoost();
      else if (ensembleType.equalsIgnoreCase("ADABOOST.M1")) // Calcula diferente la forma de cambiar los pesos
         return modifyWeightsAdaBoostM1();
      else if (ensembleType.equalsIgnoreCase("ADABOOST.M2")) // Utiliza la confianza
         return modifyWeightsAdaBoostM2();
      else if (ensembleType.equalsIgnoreCase("ADAC2"))
         return modifyWeightsAdaC2();
      else if (ensembleType.equalsIgnoreCase("RUSBOOST")
              || ensembleType.contains("SMOTEBOOST"))
      {
         weights = weightsBackup.clone();
         return modifyWeightsAdaBoostM2();
      }
      else if (ensembleType.equalsIgnoreCase("DATABOOST-IM"))
      {
         weights = weightsBackup.clone();
         return modifyWeightsAdaBoostM1();
      }
      else if (ensembleType.equalsIgnoreCase("EASYENSEMBLE")
              || ensembleType.equalsIgnoreCase("BALANCECASCADE"))
         return modifyWeightsAdaBoostActualDS();
      else return false;
   }

   /** AdaBoost algorithm
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaBoost() {
      double[] corrects = new double[nData];

      // compute alfa_t
       double r = 0, Z = 0;
      for (int i = 0; i < nData; i++)
      {
         if (classifier.obtainClass(t, originalDS.getExample(i)) == originalDS.getOutputAsInteger(i))
            corrects[i] = 1;
         else
            corrects[i] = -1;
         r += weights[i] * corrects[i];
      }
      double err = (1 - r) / 2;
      if (err < 0.001 || err >= 0.5)
      {
         if (t > 0 && err >= 0.5)
         {
            nClassifier = t;
            t = t - 1;
         }
         else
            alfa[t] = 1.0;
         return true;
      }

      alfa[t]  = 0.5 * Math.log((1 + r) / (1 - r));

      for (int i = 0; i < nData; i++)
      {
            weights[i] *= Math.exp(-1.0 * alfa[t] * corrects[i]); 
            Z += weights[i];
      }
      for (int i = 0; i < nData; i++)
         weights[i] /= Z;
     
      return false;
   }

   /** AdaBoost algorithm performed on ActualDS for BalanceCascade and EasyEnsemble
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaBoostActualDS() {
      double[] corrects = new double[actualDS.getnData()];

      // compute alfa_t
       double r = 0, Z = 0;
       double sumFail = 0;
      for (int i = 0; i < actualDS.getnData(); i++)
      {
         if (classifier.obtainClass(t, actualDS.getExample(i)) == actualDS.getOutputAsInteger(i))
            corrects[i] = 1;
         else
         {
            corrects[i] = -1;
            sumFail += weights[i];
         }

         r += weights[i] * corrects[i];
      }

      if (sumFail < 0.001 || sumFail >= 0.5)
      {
         if (sumFail < 0.5)
            alfa[t] = 1.0;
         t = (t / nBoostIterations + 1) * nBoostIterations - 1;
         if (t / nBoostIterations >= nBags - 1)
            return true;
         else
         {
            return false;
         }
      }

     // alfa[t]  = 0.5 * Math.log((1 + r) / (1 - r)); es lo mismo
      alfa[t]  = 0.5 * Math.log((1 - sumFail) / sumFail);

      for (int i = 0; i < actualDS.getnData(); i++)
      {
            weights[i] *= Math.exp(-1.0 * alfa[t] * corrects[i]);
            Z += weights[i];
      }
      for (int i = 0; i < actualDS.getnData(); i++)
         weights[i] /= Z;

      return false;
   }

   /** AdaBoost.M1 algorithm
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaBoostM1() {
      double[] corrects = new double[nData];

      // calcular alfa_t
      double Z = 0, sumFail = 0;
      for (int i = 0; i < nData; i++)
      {
         if (classifier.obtainClass(t, originalDS.getExample(i)) != originalDS.getOutputAsInteger(i))
         {
            corrects[i] = -1;
            sumFail += weights[i];
         }
         else
            corrects[i] = 1;
      }
      if (sumFail < 0.001 || sumFail >= 0.5)
      {
         if (t > 0 && sumFail > 0.5)
         {
            nClassifier = t;
            t = t - 1;
         }
         else
            alfa[t] = 1.0;
         return true;
      }
      double beta = sumFail / (1 - sumFail);

      for (int i = 0; i < nData; i++)
      {
         if (corrects[i] == 1)
            weights[i] *= beta;
            Z += weights[i];
      }
      for (int i = 0; i < nData; i++)
         weights[i] /= Z;

      alfa[t]  = Math.log(1.0 / beta);

      return false;
   }
   
   /** AdaBoost.M2 algorithm
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaBoostM2() {
      double[] corrects = new double[nData];
      double[] confianza = new double[nData];

      // calcular alfa_t
      double Z = 0, sumFail = 0;
      for (int i = 0; i < nData; i++)
      {
         confianza[i] = classifier.obtainConfidence(t, originalDS.getExample(i));
         if (classifier.obtainClass(t, originalDS.getExample(i)) != originalDS.getOutputAsInteger(i))
         {
            corrects[i] = -1;
            sumFail += 2 * weights[i] * confianza[i];
         }
         else
         {
            corrects[i] = 1;
            sumFail += weights[i] * (2 - 2 * confianza[i]);
         }
      }
      sumFail *= 0.5;
      double beta = sumFail / (1 - sumFail);

      for (int i = 0; i < nData; i++)
      {
         if (corrects[i] == 1)
            weights[i] *= Math.pow(beta, confianza[i]);
         else
            weights[i] *= Math.pow(beta, 1 - confianza[i]);
         Z += weights[i];
      }
      for (int i = 0; i < nData; i++)
         weights[i] /= Z;

      alfa[t]  = Math.log(1.0 / beta);

      return false;
   }

    /** AdaC2 boosting algorithm, where costs are instroduced within the exponent part of Adaboost
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaC2() {
      double[] corrects = new double[nData];
      double[] C = new double[nData];
      double sumFail = 0, sumCorrect = 0;

      // calcular alfa_t
      double Z = 0;
      for (int i = 0; i < nData; i++)
      {
         C[i] = majC == actualDS.getOutputAsInteger(i) ? CostMaj : CostMin;

         if (classifier.obtainClass(t, originalDS.getExample(i)) == originalDS.getOutputAsInteger(i))
         {
            corrects[i] = 1;
            sumCorrect += C[i] * weights[i];
         }
         else
         {
            corrects[i] = -1;
            sumFail += C[i] * weights[i];
         }
      }

      if (sumFail < 0.001 || sumFail >= sumCorrect)
      {
         if (t > 0 && sumFail >= sumCorrect)
         {
            t = t - 1;
            nClassifier = t;
         }
         else
            alfa[t] = 1.0;
         return true;
      }

      alfa[t]  = 0.5 * Math.log(sumCorrect / sumFail);

      for (int i = 0; i < nData; i++)
      {
            weights[i] *= Math.exp(-1.0 * alfa[t] * corrects[i]) * C[i];
            Z += weights[i];
      }
      for (int i = 0; i < nData; i++)
         weights[i] /= Z;
      

      return false;
   }

   /** Out-of-Bag error estimation algorithm
    * 
    * @param originalDS the original data-sets which contains all the instances
    * @param predictions wether the instance in the position has been correctly classified or not (only for instances which were in the bag)
    * @return Out-of-Bag error estimation
    */
   private double outOfBagEstimation(myDataset originalDS, boolean[] predictions) {

      double total = 0;
      double TP = 0, FP = 0, FN = 0, TN = 0;
      for (int i = 0; i < originalDS.getnData(); i++)
      {
         boolean counted = false;
         double[] example = originalDS.getExample(i);
         double sum = 0;
         double confidence = 1;
         for (int t = 0; t < nClassifier; t++)
         {
            if (alfa[t] != 0 && !this.trainingSetsOOB.get(t)[i])
            {
               if (!counted)
               {
                  total++; counted = true;
               }

               confidence = classifier.obtainConfidence(t, example);
               if (classifier.obtainClass(t, example) == 0)
                  sum += confidence * alfa[t];
               else
                  sum -= confidence * alfa[t];
            }
         }
         int output = -1;
         if (sum >= 0)
            output = 0;
         else if (sum < 0)
            output = 1;
         int claseReal = originalDS.getOutputAsInteger(i);
         if (output == claseReal && counted)
            predictions[i] = true;
         else
            predictions[i] = false;
        if (counted){
           if (claseReal == output && this.majC == output)
              TN++;
           else if (claseReal == output && this.majC != output)
              TP++;
           else if (claseReal != output && this.majC == output)
              FP++;
           else
              FN++;
        }

      }
        double TPrate = TP / (TP + FN);
        double TNrate = TN / (TN + FP);
        double gmean = Math.sqrt(TPrate * TNrate);
        double acc = (TN + TP) / (TN + TP + FN + FP);
      return 1 - acc;
   }

}

