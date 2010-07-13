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

//
//  SVMSEL.java
//
//  Diego J. Romero LÃ³pez, basado en el cÃ³digo de Salvador GarcÃ­a LÃ³pez
//
//  Created by Salvador Garcï¿½a Lï¿½pez 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.HYB;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.libsvm.*;
import keel.Dataset.*;
import org.core.*;
import java.util.*;

/**
 * SVM process of selection of prototype set.
 * @author Salvador GarcÃ­a LÃ³pez, adapted by Diego J. Romero LÃ³pez
 */
public class SVMSEL extends PrototypeGenerator
{
    // Own parameters of the algorithm
    /** Kernel type used in the SVM */
    private String kernelType;
    /** Number of inputs of the prototypes */
    private int k;
    /*Set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1).*/
    private double C = 1.0;
    /*Epsilon in loss function of epsilon-SVR (default 0.1)*/
    private double eps = 0.1;
    /**Degree in kernel function (default 3)*/
    private int degree = 3;
    /** Gamma in kernel function (default 1/k) */
    private double gamma;
    /** Parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5). */
    private double nu = 0.5;
    /** I don't remember */
    private double p;
    /**  Whether to use the shrinking heuristics, 0 or 1 (default 1) */
    private int shrinking;

    
    /*
     -s svm_type : set type of SVM (default 0)
	0 -- C-SVC
	1 -- nu-SVC
	2 -- one-class SVM
	3 -- epsilon-SVR
	4 -- nu-SVR
-t kernel_type : set type of kernel function (default 2)
	0 -- linear: u'*v
	1 -- polynomial: (gamma*u'*v + coef0)^degree
	2 -- radial basis function: exp(-gamma*|u-v|^2)
	3 -- sigmoid: tanh(gamma*u'*v + coef0)
-d degree : set degree in kernel function (default 3)
-g gamma : set gamma in kernel function (default 1/k)
-r coef0 : set coef0 in kernel function (default 0)
-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)
-m cachesize : set cache memory size in MB (default 100)
-e epsilon : set tolerance of termination criterion (default 0.001)
-h shrinking: whether to use the shrinking heuristics, 0 or 1 (default 1)
-b probability_estimates: whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)
-w weight: set the parameter C of class i to weight*C, for C-SVC (default 1)
     */
        
    /**
     * Performs an selection of the training data set using SVM.     
     * @param kernelType {LINEAR, POLY, RBF, SIGMOID} linear: u'*v; polynomial: (gamma*u'*v + coef0)^degree; radial basis function: exp(-gamma*|u-v|^2); sigmoid: tanh(gamma*u'*v + coef0)
     * @param C Parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1).
     * @param eps Epsilon in loss function of epsilon-SVR (default 0.1)
     * @param degree Degree in kernel function (default 3)
     * @param gamma Gamma in kernel function (default 1/k)
     * @param nu Parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
     * @param p P-parameter of the SVM.
     * @param shrinking Whether to use the shrinking heuristics, 0 or 1 (default 1)
     */
    public SVMSEL(PrototypeSet _trainingDataSet, String kernelType, double C, double eps, int degree, double gamma, double nu, double p, int shrinking)
    {
        super(_trainingDataSet);        
        this.k = trainingDataSet.get(0).numberOfInputs();
        this.kernelType = kernelType;
        this.C = C;
        this.eps = eps;
        this.degree = degree;
        this.gamma = gamma;
        this.nu = nu;
        this.p = p;
        this.shrinking = shrinking;
    }
    
    /**
     * Performs an selection of the training data set using SVM.     
     * @param kernelType {LINEAR, POLY, RBF, SIGMOID} linear: u'*v; polynomial: (gamma*u'*v + coef0)^degree; radial basis function: exp(-gamma*|u-v|^2); sigmoid: tanh(gamma*u'*v + coef0)
     * */
        public SVMSEL(PrototypeSet _trainingDataSet, String kernelType)
        {
            super(_trainingDataSet);
            this.k = trainingDataSet.get(0).numberOfInputs();
            this.kernelType = kernelType;
            this.C = 1.0;
            this.eps = 0.1;
            this.degree = 3;
            this.gamma = 1.0/(double)k;
            this.nu = 0.5;
            this.p = 0.1;
            this.shrinking = 1;
        }

    /**
    * Executes the SVM prototype selection.
    * @return Selected prototypes by SVM method.
    */
    public PrototypeSet doSVMSelection()
    {
        //SVM WTF!
        PrototypeSet T = trainingDataSet.copy();
        int Tsize = T.size();
        int protSize = this.k;                
        svm_parameter SVMparam = new svm_parameter();
        svm_problem SVMp = null;
        svm_model svr = null;
        double exTmp[];
       
        //SVM PARAMETERS
        SVMparam.C = C;
        SVMparam.cache_size = 20; //20MB of cache
        SVMparam.degree = degree;
        SVMparam.eps = eps;
        SVMparam.gamma = gamma;
        SVMparam.nr_weight = 0;
        SVMparam.nu = nu;
        SVMparam.p = p;
        SVMparam.shrinking = shrinking;
        SVMparam.probability = 0;
        if (kernelType.compareTo("LINEAR") == 0)
            SVMparam.kernel_type = svm_parameter.LINEAR;
        else if (kernelType.compareTo("POLY") == 0)
            SVMparam.kernel_type = svm_parameter.POLY;
        else if (kernelType.compareTo("RBF") == 0)
            SVMparam.kernel_type = svm_parameter.RBF;
        else if (kernelType.compareTo("SIGMOID") == 0)
            SVMparam.kernel_type = svm_parameter.SIGMOID;
        
        SVMparam.svm_type = svm_parameter.C_SVC;

        SVMp = new svm_problem();
        SVMp.l = Tsize;
        SVMp.y = new double[SVMp.l];
        SVMp.x = new svm_node[SVMp.l][protSize + 1];
        for (int i = 0; i < SVMp.l; i++)
            for (int j = 0; j < protSize + 1; j++)
                SVMp.x[i][j] = new svm_node();
       
        
        for (int i = 0; i < Tsize; i++)
        {
            SVMp.y[i] = T.get(i).label();
            for (int j = 0; j < protSize; j++)
            {
                SVMp.x[i][j].index = j;
                SVMp.x[i][j].value = T.get(i).getInput(j);
            }
            //end of instance
            SVMp.x[i][protSize].index = -1;
        }

        if (svm.svm_check_parameter(SVMp, SVMparam) != null)
        {
            Debug.errorln("SVM parameter error in training: ");
            Debug.errorln(svm.svm_check_parameter(SVMp, SVMparam));
            Debug.goout("Error in SVM parameters");                 
        }

        //Train the SVM
        svr = svm.svm_train(SVMp, SVMparam);
        exTmp = new double[protSize];
        boolean[] marcas = new boolean[Tsize];
        Arrays.fill(marcas, false);
        int nSel = 0;
        for (int i = 0; i < svr.getSV().length; i++)
        {
            for (int j = 0; j < svr.getSV()[i].length - 1; j++)
                exTmp[j] = svr.getSV()[i][j].value;

            boolean coincide = false;
            for (int j = 0; j < Tsize && !coincide; j++)
            {
                boolean igual = true;
                for (int l = 0; l < protSize && igual; l++)
                    igual = (exTmp[l] == T.get(j).getInput(l));
                        
                if (igual)
                {
                    marcas[j] = true;
                    nSel++;
                    coincide = true;
                }
            }
        }
        PrototypeSet S = new PrototypeSet(nSel);
        for(int i=0; i<Tsize; ++i)
            if(marcas[i])
                S.add(T.get(i));
        return S;
    }

    /**
     * Reduction of the original prototype set by the SVM.
     * @return Prototypes selected by SVM.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        return doSVMSelection();
    }
}

