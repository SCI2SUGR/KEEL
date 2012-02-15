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
 * @author Written by Julián Luengo Martín 06/03/2006
 * @version 0.5
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.*;
import java.lang.*;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;
import keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat.*;

/*
class IPComp implements Comparator {
    public int compare(Object obj1, Object obj2) {
        double i1 = (InstanceP)obj1.Px;
        double i2 = (InstanceP)obj2.Px;
        if(i1<i2)
            return -1;
        if(i1==i2)
            return 0;
        return 1;
    }
}*/


/**
 * <p>
 * Based on the work of Wong et al., a mixed-mode probability model is approximated
 * by a discrete one. First, they discretize the continuous components using a minimum
 * loss of information criterion. Treating a mixed-mode feature n-tuple as a 
 * discrete-valued one, the authors propose a new statistical approach for synthesis
 * of knowledge based on cluster analysis. As main advantage, this method does not 
 * require neither scale normalization nor ordering of discrete values. By synthesis
 * of the data into statistical knowledge, they refer to the following processes: 
 * 1) synthesize and detect from data inherent patterns which indicate statistical 
 * interdependency; 
 * 2) group the given data into inherent clusters based on these detected interdependency; 
 * and 3) interpret the underlying patterns for each clusters identified. 
 * </p>    
 * The method of synthesis is based on author's eventcovering approach. 
 * With the developed inference method, we are able to estimate the MVs in the data. 
 * This method assumes the data is DISCRETIZED (but won't throw any error with continuous
 * data).
 */
public class EventCovering {
    
    
    double [] mean = null;
    double [] std_dev = null;
    double tempData = 0;
    String[][] X = null; //matrix of transformed data
    
    int ndatos = 0;
    int nentradas = 0;
    int tipo = 0;
    int direccion = 0;
    int nvariables = 0;
    int nsalidas = 0;
    int totalMissing = 0;
    //METHOD PARAMS
    double T = 0.07;
    int min_change_num = 0;
    double Cfactor = 1;
    
    InstanceSet IS,IStest;
    String input_train_name = new String();
    String input_test_name = new String();
    String output_train_name = new String();
    String output_test_name = new String();
    String temp = new String();
    String data_out = new String("");
    StatFunc chi;
    
    /** Creates a new instance of EventCovering
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public EventCovering(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
        IStest = new InstanceSet();
    }
    
    
    //Write data matrix X to disk, in KEEL format
    private void write_results(String output){
        //File OutputFile = new File(output_train_name.substring(1, output_train_name.length()-1));
        try {
            FileWriter file_write = new FileWriter(output);
            
            file_write.write(IS.getHeader());
            
            //now, print the normalized data
            file_write.write("@data\n");
            for(int i=0;i<ndatos;i++){
                //System.out.println(i);
                file_write.write(X[i][0]);
                for(int j=1;j<nvariables;j++){
                    file_write.write(","+X[i][j]);
                }
                file_write.write("\n");
            }
            file_write.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            System.exit(-1);
        }
    }
    
    //Reads the parameter file, and parses data into strings
    private void config_read(String fileParam){
        File inputFile = new File(fileParam);
        
        if (inputFile == null || !inputFile.exists()) {
            System.out.println("parameter "+fileParam+" file doesn't exists!");
            System.exit(-1);
        }
        //begin the configuration read from file
        try {
            FileReader file_reader = new FileReader(inputFile);
            BufferedReader buf_reader = new BufferedReader(file_reader);
            //FileWriter file_write = new FileWriter(outputFile);
            
            String line;
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0); //avoid empty lines for processing -> produce exec failure
            String out[]= line.split("algorithm = ");
            //alg_name = new String(out[1]); //catch the algorithm name
            //input & output filenames
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out= line.split("inputData = ");
            out = out[1].split("\\s\"");
            input_train_name = new String(out[0].substring(1, out[0].length()-1));
            input_test_name = new String(out[1].substring(0, out[1].length()-1));
            if(input_test_name.charAt(input_test_name.length()-1)=='"')
                input_test_name = input_test_name.substring(0,input_test_name.length()-1);
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("outputData = ");
            out = out[1].split("\\s\"");
            output_train_name = new String(out[0].substring(1, out[0].length()-1));
            output_test_name = new String(out[1].substring(0, out[1].length()-1));
            if(output_test_name.charAt(output_test_name.length()-1)=='"')
                output_test_name = output_test_name.substring(0,output_test_name.length()-1);
            
            //parameters
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("T = ");
            T = (new Double(out[1])).doubleValue(); //parse the string into a double
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("minChangeNum = ");
            min_change_num = (new Integer(out[1])).intValue(); //parse the string into a double
            
            
            do{
                line = buf_reader.readLine();
            }while(line.length()==0);
            out = line.split("Cfactor = ");
            Cfactor = (new Double(out[1])).doubleValue(); //parse the string into a double
            
            
            file_reader.close();
            
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
            System.exit(-1);
        }
    }
    
    /**
     * <p>
     * Computes the Hamming distance between 2 instances
     * </p>
     * @param i1 First Instance 
     * @param i2 Second instance
     * @return The Hamming distance
     */
    protected double dist(Instance i1, Instance i2){
        double d = 0;
        double [] a;
        double [] b;
        /*
         //Euclidean distance
        a = i1.getAllInputValues();
        b = i2.getAllInputValues();
        for(int i=0;i<nentradas;i++){
            d += (a[i]-b[i])*(a[i]-b[i]);
        }
         
        a = i1.getAllOutputValues();
        b = i2.getAllOutputValues();
        for(int i=0;i<nsalidas;i++){
            d += (a[i]-b[i])*(a[i]-b[i]);
        }
        d = Math.sqrt(d);
         */
        //Hamming distance
        a = i1.getAllInputValues();
        b = i2.getAllInputValues();
        for(int i=0;i<nentradas;i++){
            if(a[i]!=b[i])
                d++;
        }
        
        a = i1.getAllOutputValues();
        b = i2.getAllOutputValues();
        for(int i=0;i<nsalidas;i++){
            if(a[i]!=b[i])
                d++;
        }
        return d;
    }
    
    /**
     * <p>
     * Estimates the mutual information between the instances in the data set
     * </p>
     * @return The mutual information for all possible combinations of 2 instances
     */
    protected double[][] computeMutualInformation(){
        double[][] I;
        Instance inst;
        FreqListPair[][] frec;
        ValuesFreq F;
        double u,v;
        double f_uv,f_u,f_v;
        double [] ent;
        double [] sal;
        double [] all;
        HashSet lu = new HashSet();
        HashSet lv = new HashSet();
        boolean found;
        String[] valores;
        String e1,e2;
        
        frec = new FreqListPair[nvariables][nvariables];
        for(int i=0;i<nvariables;i++)
            for(int j=0;j<nvariables;j++)
                frec[i][j] = new FreqListPair();
        //frec = new FreqListPair();
        //matrix of mutual summed information
        I = new double[nvariables][nvariables];
        all = new double[nvariables];
        
        for(int k=0;k<ndatos;k++){
            inst = IS.getInstance(k);
            //if(!inst.existsAnyMissingValue()){
                ent = inst.getAllInputValues();
                sal = inst.getAllOutputValues();
                for(int m=0;m<nentradas;m++){
                	if(!inst.getInputMissingValues(m))
                		all[m] = ent[m];
                	else
                		all[m] = Double.MIN_VALUE;
                }
                for(int m=0;m<nsalidas;m++)
                	if(!inst.getOutputMissingValues(m))
                		all[m+nentradas] = sal[m];
                	else
                		all[m+nentradas] = Double.MIN_VALUE;
                
                for(int i = 0; i< nvariables;i++){
                    for(int j = i+1; j < nvariables;j++){
                        u = all[i];
                        v = all[j];
                        frec[i][j].AddElement(String.valueOf(u),String.valueOf(v));
                        //store both elements
                        //  lu.add(String.valueOf(u));
                        //   lv.add(String.valueOf(v));
                        //System.out.println("("+i+","+j+") OK de ("+nvariables+","+nvariables+")");
                    }
                }
            //}
        }
        for(int i = 0; i< nvariables;i++){
            for(int j = i+1; j < nvariables;j++){
                frec[i][j].reset();
                I[i][j] = 0;
                while(!frec[i][j].outOfBounds()){
                    F = frec[i][j].getCurrent();
                    e1 = F.getValue1();
                    e2 = F.getValue2();
                    f_u = (double)frec[i][j].elem1SumFreq(e1)/ndatos;
                    f_v = (double)frec[i][j].elem2SumFreq(e2)/ndatos;
                    f_uv = (double)F.getFreq()/ndatos;
                    I[i][j] += f_uv * Math.log(f_uv/(f_u*f_v))/Math.log(2);
                    frec[i][j].iterate();
                }
            }
        }
        
        return I;
    }
    
    /**
     * <p>
     * Computes  the dependece Tree using Dijkstra algorithm
     * </p>
     * @param I The paired-mutual information of this data set
     * @return
     */
    protected Vector computeTree(double[][] I){
        double[] nodo;
        int ik = -1,jk = -1;
        int k,m;
        double max;
        nodo = new double[nvariables];
        Vector tree = new Vector();
        Pair par;
        
        for(int i=0;i<nvariables;i++)
            nodo[i] = i;
        k = 1;
        while(k-nvariables<0){
            //search for maximum I
        	//since I[i][j] is always non-negative, use initial MAX value as a negative one
        	//if we don't want consider zero-Information values, use max = 0 as initial value
            max = -1;
            for(int i=0;i<nvariables-1;i++){
                for(int j=i+1;j<nvariables;j++){
                    if(I[i][j] > max){
                        ik = i;
                        jk = j;
                        max = I[i][j];
                    }
                }
            }
            if(nodo[ik] == nodo[jk]){
                I[ik][jk] = -1;
            } else{
                par = new Pair(ik,jk);
                tree.addElement(par);
                I[ik][jk] = -1;
                m = 0;
                while(m<nvariables){
                    if(nodo[m] == nodo[jk]){
                        nodo[m] = nodo[jk];
                    }
                    m++;
                }
                k++;
            }
        }
        return tree;
    }
    
    /**
     * <p>
     * Computes the conjunctive probabilities using the second order probabilities.
     * </p>
     * @param tree The dependence tree of this data set
     * @return An array of probabilites for each instance (in the same order)
     */
    protected double[] computePx(Vector tree){
        double [] Px;
        Instance inst,e;
        double a,b;
        double x1,x2;
        int count,total;
        Pair p;
        
        Px = new double[ndatos];
        for(int i = 0;i < ndatos;i++){
            inst = IS.getInstance(i);
            if(!inst.existsAnyMissingValue()){
                a = (inst.getAllInputValues())[0];
                count = 0;
                for(int j = 0;j < ndatos;j++){
                    e = IS.getInstance(j);
                    if((e.getAllInputValues())[0] == a)
                        count++;
                }
                Px[i] = (double)count/ndatos;
                for(int j=0;j<tree.size();j++){
                    p = (Pair)tree.elementAt(j);
                    if(p.e1<nentradas)
                        a = (inst.getAllInputValues())[p.e1];
                    else
                        a = (inst.getAllOutputValues())[p.e1-nentradas];
                    if(p.e2<nentradas)
                        b = (inst.getAllInputValues())[p.e2];
                    else
                        b = (inst.getAllOutputValues())[p.e2-nentradas];
                    count = 0;
                    total = 0;
                    for(int k = 0;k < ndatos;k++){
                        e = IS.getInstance(k);
                        if(p.e1<nentradas)
                            x1 = (e.getAllInputValues())[p.e1];
                        else
                            x1 = (e.getAllOutputValues())[p.e1-nentradas];
                        if(p.e2<nentradas)
                            x2 = (e.getAllInputValues())[p.e2];
                        else
                            x2 = (e.getAllOutputValues())[p.e2-nentradas];
                        
                        if(x1==a){
                            total++;
                            if(x2==b)
                                count++;
                        }
                    }
                    Px[i] *= (double)count/total;
                }
            } else{
                Px[i] = -1; //instance with missing data, do not count for cluster making!
                totalMissing++;
            }
        }
        return Px;
    }
    
    /**
     * <p>
     * Initializes the set of clusters using information of the data set
     * </p>
     * @param Px The second order probablity estimation
     * @return a initinal set of clusters
     */
    protected Vector clusterInitation(double[] Px){
        int k,t;
        double muMean;
        double Dst;
        double P_;
        double d,dmax,dtop,p;
        int max,tmax,choosenV;
        int alreadyTaken;
        boolean Dfound,found;
        Vector L = new Vector();
        Vector Dist = new Vector();
        Vector Ps = new Vector();
        Vector Index = new Vector();
        Instance x;
        Vector Clusters = new Vector();
        Cluster cluster;
        //InstanceSet IS ;
        k = 0;
        t = 0;
        
        //IS.readSet(input_train_name,true);
        muMean = 0;
        max = 0;
        for(int i=0;i<ndatos;i++){
            if(Px[i]>=0){
                muMean += Px[i];
                if(Px[i]>Px[max])
                    max = i;
            }
        }
        muMean = muMean/ndatos;
        //T = 1; //threshold for cluster size
        tmax = 0;
        for(int j = 0; j < nvariables;j++){
            Attribute a = Attributes.getAttribute(j);
            if(a.getType()!=Attribute.NOMINAL){
                if(a.getMaxAttribute()-a.getMinAttribute() > tmax)
                    tmax = (int)(a.getMaxAttribute()-a.getMinAttribute());
            } else{
                if(a.getNumNominalValues() > tmax)
                    tmax = a.getNumNominalValues();
            }
        }
        //T = T *tmax/8;
        //dummy cluster
        Cluster C0 = new Cluster();
        /*
        Cluster C1 = new Cluster();
        C1.addInstance(IS.getInstance(max));
        Clusters.addElement(C1);
         */
        alreadyTaken = 0;
        
        
        while(alreadyTaken<ndatos-totalMissing){
            if(ndatos-alreadyTaken>T)
                P_ = muMean;
            else
                P_ = 0;
            //List all x in L, provided x has P(x) > P_
            for(int i=0;i<ndatos;i++){
                if(Px[i]>P_){
                    L.addElement(IS.getInstance(i));
                    Ps.addElement(new Double(Px[i]));
                    Index.addElement(new Integer(i));
                }
            }
            //compute D for each x
            Dist.clear();
            for(int i=0;i<L.size();i++){
                x = (Instance)L.elementAt(i);
                Dist.addElement(new Double(D(x,L)));
                //System.out.println("i = "+i);
            }
            //get D*
            dtop = Double.MAX_VALUE;
            do{
                dmax = 0;
                for(int i=0;i<Dist.size();i++){
                    d = ((Double)Dist.elementAt(i)).doubleValue();
                    if(dmax<d && d<dtop)
                        dmax = d;
                }
                //avoid isolated values, making D* such exist at least
                //one 'x' with D*-1
                Dfound = false;
                for(int i=0;i<Dist.size()&&!Dfound;i++){
                    d = ((Double)Dist.elementAt(i)).doubleValue();
                    if((int)(dmax-1)<=d)
                        Dfound = true;
                }
                if(!Dfound)
                    dtop = dmax;
            }while(!Dfound && dtop > 1);
            Dst = dmax; //D* found
            do{
                dmax = 0;
                //locate the x with maximum P(x)
                for(int i=0;i<L.size();i++){
                    p = ((Double)Ps.elementAt(i)).doubleValue();
                    if(p>dmax){
                        max = i;
                        dmax = p;
                    }
                }
                x = (Instance) L.elementAt(max);
                found = false;
                Vector cv = new Vector();
                for(int i=0;i<Clusters.size();i++){
                    cluster = (Cluster)Clusters.elementAt(i);
                    d = D(x,cluster.C);
                    if(d<Dst){
                        cv.addElement(new Integer(i));
                    }
                }
                if(cv.size()==1){
                    cluster = (Cluster)Clusters.elementAt(((Integer)cv.firstElement()).intValue());
                    cluster.C.addElement(x);
                } else{
                    if(cv.size()>1){
                        found = false;
                        for(int i=0;i<cv.size() && !found;i++){
                            if(((Integer)cv.elementAt(i)).intValue() < k){
                                C0.C.addElement(x);
                                found = true;
                            }
                        }
                        //merge all clusters
                        if(!found){
                            cluster = (Cluster)Clusters.elementAt(((Integer)cv.firstElement()).intValue());
                            for(int i=1;i<cv.size();i++){
                                choosenV = ((Integer)cv.elementAt(i)).intValue();
                                cluster.C.addAll( ( (Cluster) Clusters.elementAt(((Integer)cv.elementAt(i)).intValue()) ).C );
                                Clusters.removeElementAt(((Integer)cv.elementAt(i)).intValue());
                                t--;
                                //shift left remaining cluster index
                                for(int j=i+1;j<cv.size();j++){
                                    if( ((Integer)cv.elementAt(j)).intValue() > choosenV){
                                        cv.set(j,new Integer(((Integer)cv.elementAt(j)).intValue()-1));
                                    }
                                }
                                //cv.removeElementAt(i);
                                //i--; //compensate the shift left
                            }
                        }
                    }
                    //x will form a new cluster by himself
                    else{
                        cluster = new Cluster();
                        cluster.addInstance(x);
                        Clusters.addElement(cluster);
                        t++;
                    }
                }
                alreadyTaken++;
                L.removeElementAt(max);
                Ps.removeElementAt(max);
                Px[((Integer)Index.elementAt(max)).intValue()] = -1; //so it cant be choosen again
                Index.removeElementAt(max);
            }while(L.size()>0);
            k = t;
            muMean = 0;
            max = 0;
            for(int i=0;i<ndatos;i++){
                if(Px[i]>=0){
                    muMean += Px[i];
                    if(Px[i]>Px[max])
                        max = i;
                }
            }
            muMean = muMean/ndatos;
        }
 
        for(int i=0;i<t;i++){
            cluster = (Cluster)Clusters.elementAt(i);
            if(cluster.C.size()<T)
                C0.C.addAll(cluster.C);
        }
        Clusters.add(0, C0);
        
        //assign identifier to each cluster
        for(int i=0;i<Clusters.size();i++)
            ((Cluster)Clusters.elementAt(i)).setNumber(i);
        
        return Clusters;
    }
    
    /**
     * <p>
     * This method refines the initial clusters obtained by clusterInitiation()
     * </p>
     * @param Clusters The set of clusters to be refined
     * @return A refined set of clusters
     */
    protected Vector refineClusters(Vector Clusters){
        FreqList [] obs;
        double [] values;
        double [] input;
        double [] output;
        double [] sum_Pcond = new double[nvariables];
        Instance inst;
        Cluster cluster;
        ValueFreq val;
        double confident = 0.05;
        double NS_denom;
        int totalFreqs;
        int nearestCluster,isAt,index;
        double exp,observed,D,I,H,tmp,minNS;
        Vector [] Eck = new Vector[nvariables];
        Vector [] Ekc = new Vector[nvariables];
        FreqListPair atr_clust = new FreqListPair();
        FreqListPair [] acj_xk = new FreqListPair[nvariables];
        double [] R = new double[nvariables];
        Vector nextGenClusters;
        boolean uncertain;
        int number_of_change, prev_changes;
        Vector foundIndex = new Vector();
        chi = new StatFunc();
        
        for(int i=0;i<nvariables;i++)
            acj_xk[i] = new FreqListPair();
        
        obs = new FreqList[nvariables];
        for(int i=0;i<nvariables;i++){
            obs[i] = new FreqList();
            Eck[i] = new Vector();
            Ekc[i] = new Vector();
        }
        //make the frequency distribution
        for(int i=0;i<ndatos;i++){
            inst = IS.getInstance(i);
            values = inst.getAllInputValues();
            for(int j=0;j<nentradas;j++){
                obs[j].AddElement(String.valueOf(values[j]));
            }
            values = inst.getAllOutputValues();
            for(int j=0;j<nsalidas;j++){
                obs[j+nentradas].AddElement(String.valueOf(values[j]));
            }
        }
        D = 0;
        //*********************************************************************
        //*********************************************************************
        //***************************BEGIN refinement**************************
        //*********************************************************************
        //*********************************************************************
        number_of_change = 0;
        do{
            //**************************** REVISAR BEGIN ********************************
            
            //compute Eck
            totalFreqs = 0;
            for(int k=0;k<nvariables;k++){
                obs[k].reset();
                Eck[k].clear();
                while(!obs[k].outOfBounds()){
                    D = 0;
                    foundIndex.clear();
                    for(int j=0;j<Clusters.size();j++){
                        cluster = (Cluster) Clusters.elementAt(j);
                        exp = obs[k].getCurrent().getFreq()*cluster.C.size();
                        exp = (double) exp/ndatos;
                        observed = cluster.getObserved(obs[k].getCurrent().getValue(), k);
                        if(observed > 0){
                            foundIndex.addElement(new Integer(cluster.getNumber()));
                        }
                        D = D + (double)(observed-exp)*(observed-exp)/exp;
                    }
                    
                    if(D>StatFunc.chiSquarePercentage(confident,Clusters.size()-1)){
                        Eck[k].addElement(obs[k].getCurrent());
                        for(int j=0;j<foundIndex.size();j++){
                            index = ((Integer) foundIndex.elementAt(j)).intValue();
                            atr_clust.AddElement(String.valueOf(index),String.valueOf(obs[k].getCurrent().getValue()));
                            acj_xk[k].AddElement(String.valueOf(index),String.valueOf(obs[k].getCurrent().getValue()));
                            totalFreqs++;
                        }
                    }
                    obs[k].iterate();
                }
            }
            //check if there was attributes selected for Ekc
            //if not, finish
            prev_changes = number_of_change;
            number_of_change = 0;
            if(totalFreqs!=0){
                //compute Ekc
                for(int k=0;k<nvariables;k++){
                    Ekc[k].clear();
                    for(int j=0;j<Clusters.size();j++){
                        D = 0;
                        cluster = (Cluster) Clusters.elementAt(j);
                        obs[k].reset();
                        while(!obs[k].outOfBounds()){
                            exp = obs[k].getCurrent().getFreq()*cluster.C.size();
                            exp = (double) exp/ndatos;
                            observed = cluster.getObserved(obs[k].getCurrent().getValue(), k);
                            D = D + (double)(observed-exp)*(observed-exp)/exp;
                            obs[k].iterate();
                        }
                        if(D>StatFunc.chiSquarePercentage(confident,Clusters.size()-1))
                            Ekc[k].addElement(Clusters.elementAt(j));
                    }
                }
                
                //**************************** REVISAR END ********************************
                
                //now, compute the interdependency redundancy measure
                //between Xck and Ck
                
                for(int k=0;k<nvariables;k++){
                    I = 0;
                    H = 0;
                    //compute expected mutual information and entropy
                    for(int u = 0;u<Eck[k].size();u++){
                        for(int s=0;s<Ekc[k].size();s++){
                            cluster = (Cluster) Ekc[k].elementAt(s);
                            val= (ValueFreq)Eck[k].elementAt(u);
                            tmp = (double)atr_clust.getPairFreq(String.valueOf(cluster.getNumber()),String.valueOf(val.getValue()))/totalFreqs;
                            if(tmp>0){
                                H -= (double)tmp*Math.log(tmp)/Math.log(2);
                                tmp = (double)tmp * Math.log(tmp/((double)val.getFreq()*cluster.C.size()/(totalFreqs*totalFreqs)))/Math.log(2);
                                I += tmp;
                            }
                        }
                    }
                    if(I!=0 && H!=0)
                        R[k] = (double) I/H;
                    else
                        R[k] = 0;
                }
                
                NS_denom = 0;
                for(int k=0;k<nvariables;k++)
                    NS_denom += R[k];
                NS_denom *= nvariables;
                
                nextGenClusters = (Vector)Clusters.clone();
                for(int i=0;i<Clusters.size();i++){
                    cluster = (Cluster)Clusters.elementAt(i);
                    for(int j=0;j<cluster.C.size();j++){
                        inst = (Instance)cluster.C.elementAt(j);
                        minNS = Double.MAX_VALUE;
                        nearestCluster = 0; //the dummy cluster C0
                        for(int u=1;u<Clusters.size();u++){
                            tmp = NS(inst,u, cluster.C.size(), R, acj_xk, Ekc,NS_denom);
                            if(i!= 0 && u==i && tmp!= -1)
                                tmp = tmp/Cfactor;
                            if(tmp!=-1 && tmp<minNS){
                                nearestCluster = u;
                                minNS = tmp;
                            }
                            
                        }
                        if(nearestCluster!=i){
                            //move the element to destination cluster
                            isAt = ((Cluster)nextGenClusters.elementAt(i)).C.indexOf(inst);
                            ((Cluster)nextGenClusters.elementAt(i)).C.removeElementAt(isAt);
                            ((Cluster)nextGenClusters.elementAt(nearestCluster)).addInstance(inst);
                            number_of_change++;
                        }
                    }
                }
                /*System.out.print("[ ");
                for(int q=0;q<Clusters.size();q++){
                    System.out.print(((Cluster)Clusters.elementAt(q)).C.size()+",");
                }
                System.out.print(" ]");*/
                Clusters.clear();
                Clusters = nextGenClusters;
            }
        }while(number_of_change>0 && Math.abs(number_of_change-prev_changes)>=min_change_num);
        //*********************************************************************
        //*********************************************************************
        //***************************END refinement****************************
        //*********************************************************************
        //*********************************************************************
        //for(int i=0)
        return Clusters;
    }
    
    protected double NS(Instance inst,int numCluster,int sizeCluster,double[] R,FreqListPair [] acj_xk,Vector [] Ekc,double NS_denom){
        double prob;
        double temp;
        double xk;
        double sum_Pcond;
        double mutualI;
        double NSvalue;
        Cluster cluster;
        double [] input;
        double [] output;
        
        
        
        input = inst.getAllInputValues();
        output = inst.getAllOutputValues();
        
        mutualI = 0;
        for(int k=0;k<nvariables;k++){
            if(k<nentradas)
                xk = input[k];
            else
                xk = output[k-nentradas];
            sum_Pcond = 0;
            for(int i=0;i<Ekc[k].size();i++){
                cluster = (Cluster)Ekc[k].elementAt(i);
                sum_Pcond += (double)acj_xk[k].sumPairFreq(String.valueOf(cluster.getNumber()),String.valueOf(xk))/sizeCluster;
            }
            temp = 0;
            if(sum_Pcond>0 && sum_Pcond > T){
                prob = (double) acj_xk[k].sumPairFreq(String.valueOf(numCluster),String.valueOf(xk))/sizeCluster;
                if(prob>0){
                    temp = (double) prob/sum_Pcond;
                    temp = -Math.log(temp)/Math.log(2);
                    temp *= R[k];
                }
            }
            mutualI += temp;
        }
        if(mutualI != 0)
            NSvalue = (double)mutualI/NS_denom;
        else
            NSvalue = -1;
        
        return NSvalue;
    }
    
    protected double D(Instance x,Vector S){
        double dmin;
        double d;
        
        dmin = Double.MAX_VALUE;
        for(int i=0;i<S.size();i++){
            if(x!=(Instance)S.elementAt(i)){
                d = dist(x,(Instance)S.elementAt(i));
                if(d < dmin)
                    dmin = d;
            }
        }
        return dmin;
    }
    
    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process(){
    	int in = 0;
        int out = 0;
        int debug = 0;
        String[] row = null;
        boolean valuesRemaining = false;
        FreqList sameValue = null;
        double[] outputs = null;
        double[] outputsCandidate = null;
        double[] inputs = null;
        double[] inputsCandidate = null;
        boolean[] inputsMissing = null;
        boolean[] taken = null;
        Vector instancesSelected = new Vector();
        boolean same = true;
        boolean valueFound = false;
        ValueFreq valueTimes;
        double minD = 0;
        double dist;
        VAList candidatesList = null;
        valueAssociations va;
        Instance missing = null;
        Instance i1,i2;
        Vector Clusters = null;
        Cluster c;
        int selectedCluster = 0;
        int centroid = 0;
        
        Vector tree;
        double[][] I;
        double[] Px;
        try {
            
            // Load in memory a dataset that contains a classification problem
            IS.readSet(input_train_name,true);
            
            
            
            ndatos = IS.getNumInstances();
            nvariables = Attributes.getNumAttributes();
            nentradas = Attributes.getInputNumAttributes();
            nsalidas = Attributes.getOutputNumAttributes();
            
            X = new String[ndatos][nvariables];//matrix with transformed data
            totalMissing = 0;
            
            
            //Create clusters for all instances without data missing
            I = computeMutualInformation();
            tree = computeTree(I);
            Px = computePx(tree);
            if(totalMissing != ndatos  && totalMissing != 0){
            	Clusters = clusterInitation(Px);
            	int acum = 0;
            	for(int i=0;i<Clusters.size();i++){
            		c = (Cluster)Clusters.elementAt(i);
            		acum += c.C.size();
            	}
            	Clusters = refineClusters(Clusters);
            }
            else{
            	Cluster C0 = new Cluster();
            	Clusters = new Vector();
            	for(int i = 0;i < ndatos;i++){
            		Instance inst = IS.getInstance(i);
            		C0.C.addElement(inst);
            	}
            	Clusters.addElement(C0);
            }
            
            //process current dataset
            for(int i = 0;i < ndatos;i++){
                Instance inst = IS.getInstance(i);
                
                in = 0;
                out = 0;
                
                for(int j = 0; j < nvariables;j++){
                    Attribute a = Attributes.getAttribute(j);
                    
                    direccion = a.getDirectionAttribute();
                    tipo = a.getType();
                    
                    if(direccion == Attribute.INPUT){
                        if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                            X[i][j] = new String(String.valueOf(inst.getInputRealValues(in)));
                        } else{
                            if(!inst.getInputMissingValues(in))
                                X[i][j] = inst.getInputNominalValues(in);
                            else{
                                //missing data, we must find the cluster this
                                //instance fits better
                                minD = Double.MAX_VALUE;
                                for(int u=0;u<Clusters.size();u++){
                                    c = (Cluster) Clusters.elementAt(u);
                                    dist = D(inst,c.C);
                                    if(dist<minD){
                                        selectedCluster = u;
                                        minD = dist;
                                    }
                                }
                                //now, find the nearest element of the cluster
                                c = (Cluster)Clusters.elementAt(selectedCluster);
                                minD = Double.MAX_VALUE;
                                dist = 0;
                                for(int l=0;l<c.C.size();l++){
                                    i2 = (Instance)c.C.elementAt(l);
                                    dist = dist(inst,i2);
                                    if(i2.getInputMissingValues(in))
                                    	dist += nvariables;
                                    if(dist<minD){
                                        minD = dist;
                                        centroid = l;
                                    }
                                }
                                
                                //use the nearest attribute as reference
                                i1 = (Instance)c.C.elementAt(centroid);
                                if(i1.getInputMissingValues(in))
                                	X[i][j] = "<null>";
                                else{
                                    if(tipo != Attribute.NOMINAL){
                                        X[i][j] = new String(String.valueOf(i1.getInputRealValues(in)));
                                    } else{
                                        X[i][j] = i1.getInputNominalValues(in);
                                    }
                                }
                            }
                        }
                        in++;
                    } else{
                        if(direccion == Attribute.OUTPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                X[i][j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                            } else{
                                if(!inst.getOutputMissingValues(out))
                                    X[i][j] = inst.getOutputNominalValues(out);
                                else{
                                    //missing data, we must find the cluster this
                                    //instance fits better
                                    minD = Double.MAX_VALUE;
                                    for(int u=0;u<Clusters.size();u++){
                                        c = (Cluster) Clusters.elementAt(u);
                                        dist = D(inst,c.C);
                                        if(dist<minD){
                                            selectedCluster = u;
                                            minD = dist;
                                        }
                                    }
                                    //now, find the nearest element of the cluster
                                    c = (Cluster)Clusters.elementAt(selectedCluster);
                                    minD = Double.MAX_VALUE;
                                    dist = 0;
                                    for(int l=0;l<c.C.size();l++){
                                        i2 = (Instance)c.C.elementAt(l);
                                        dist = dist(inst,i2);
                                        if(i2.getOutputMissingValues(out))
                                        	dist += nvariables;
                                        if(dist<minD){
                                            minD = dist;
                                            centroid = l;
                                        }
                                    }
                                    //use the centroid attribute as reference
                                    i1 = (Instance)c.C.elementAt(centroid);
                                    if(i1.getOutputMissingValues(out))
                                    	X[i][j] = "<null>";
                                    else{
                                        if(tipo != Attribute.NOMINAL){
                                            X[i][j] = new String(String.valueOf(i1.getOutputRealValues(out)));
                                        } else{
                                            X[i][j] = i1.getOutputNominalValues(out);
                                        }
                                    }
                                }
                            }
                            out++;
                        }
                    }
                }
            }
            
        }catch (Exception e){
            System.out.println("Dataset exception = " + e );
            e.printStackTrace();
            System.exit(-1);
        }
        write_results(output_train_name);
        /***************************************************************************************/
        //does a test file associated exist?
        if(input_train_name.compareTo(input_test_name)!=0){
            try {
                
                // Load in memory a dataset that contains a classification problem
                IStest.readSet(input_test_name,false);
                
                
                
                ndatos = IStest.getNumInstances();
                nvariables = Attributes.getNumAttributes();
                nentradas = Attributes.getInputNumAttributes();
                nsalidas = Attributes.getOutputNumAttributes();
                
                X = new String[ndatos][nvariables];//matrix with transformed data
                totalMissing = 0;
                
                
                //Create clusters for all instances without data missing
                /*I = computeMutualInformation();
                tree = computeTree(I);
                Px = computePx(tree);
                if(totalMissing != ndatos && totalMissing != 0){
                	Clusters = clusterInitation(Px);
                	int acum = 0;
                	for(int i=0;i<Clusters.size();i++){
                		c = (Cluster)Clusters.elementAt(i);
                		acum += c.C.size();
                	}
                	Clusters = refineClusters(Clusters);
                }
                else{
                	Cluster C0 = new Cluster();
                	Clusters = new Vector();
                	for(int i = 0;i < ndatos;i++){
                		Instance inst = IS.getInstance(i);
                		C0.C.addElement(inst);
                	}
                	Clusters.addElement(C0);
                }*/
                
                //process current dataset
                for(int i = 0;i < ndatos;i++){
                    Instance inst = IStest.getInstance(i);
                    
                    in = 0;
                    out = 0;
                    
                    for(int j = 0; j < nvariables;j++){
                        Attribute a = Attributes.getAttribute(j);
                        
                        direccion = a.getDirectionAttribute();
                        tipo = a.getType();
                        
                        if(direccion == Attribute.INPUT){
                            if(tipo != Attribute.NOMINAL && !inst.getInputMissingValues(in)){
                                X[i][j] = new String(String.valueOf(inst.getInputRealValues(in)));
                            } else{
                                if(!inst.getInputMissingValues(in))
                                    X[i][j] = inst.getInputNominalValues(in);
                                else{
                                    //missing data, we must find the cluster this
                                    //instance fits better
                                    minD = Double.MAX_VALUE;
                                    for(int u=0;u<Clusters.size();u++){
                                        c = (Cluster) Clusters.elementAt(u);
                                        dist = D(inst,c.C);
                                        if(dist<minD){
                                            selectedCluster = u;
                                            minD = dist;
                                        }
                                    }
                                    //now, find the nearest element of the cluster
                                    c = (Cluster)Clusters.elementAt(selectedCluster);
                                    minD = Double.MAX_VALUE;
                                    dist = 0;
                                    for(int l=0;l<c.C.size();l++){
                                        i2 = (Instance)c.C.elementAt(l);
                                        dist = dist(inst,i2);
                                        if(i2.getInputMissingValues(in))
                                        	dist += nvariables;
                                        if(dist<minD){
                                            minD = dist;
                                            centroid = l;
                                        }
                                    }
                                    
                                    //use the nearest attribute as reference
                                    i1 = (Instance)c.C.elementAt(centroid);
                                    if(i1.getInputMissingValues(in))
                                    	X[i][j] = "<null>";
                                    else{
	                                    if(tipo != Attribute.NOMINAL){
	                                        X[i][j] = new String(String.valueOf(i1.getInputRealValues(in)));
	                                    } else{
	                                        X[i][j] = i1.getInputNominalValues(in);
	                                    }
                                    }
                                }
                            }
                            in++;
                        } else{
                            if(direccion == Attribute.OUTPUT){
                                if(tipo != Attribute.NOMINAL && !inst.getOutputMissingValues(out)){
                                    X[i][j] = new String(String.valueOf(inst.getOutputRealValues(out)));
                                } else{
                                    if(!inst.getOutputMissingValues(out))
                                        X[i][j] = inst.getOutputNominalValues(out);
                                    else{
                                        //missing data, we must find the cluster this
                                        //instance fits better
                                        minD = Double.MAX_VALUE;
                                        for(int u=0;u<Clusters.size();u++){
                                            c = (Cluster) Clusters.elementAt(u);
                                            dist = D(inst,c.C);
                                            if(dist<minD){
                                                selectedCluster = u;
                                                minD = dist;
                                            }
                                        }
                                        //now, find the nearest element of the cluster
                                        c = (Cluster)Clusters.elementAt(selectedCluster);
                                        minD = Double.MAX_VALUE;
                                        dist = 0;
                                        for(int l=0;l<c.C.size();l++){
                                            i2 = (Instance)c.C.elementAt(l);
                                            dist = dist(inst,i2);
                                            if(i2.getOutputMissingValues(out))
                                            	dist += nvariables;
                                            if(dist<minD){
                                                minD = dist;
                                                centroid = l;
                                            }
                                        }
                                        //use the centroid attribute as reference
                                        i1 = (Instance)c.C.elementAt(centroid);
                                        if(i1.getOutputMissingValues(out))
                                        	X[i][j] = "<null>";
                                        else{
	                                        if(tipo != Attribute.NOMINAL){
	                                            X[i][j] = new String(String.valueOf(i1.getOutputRealValues(out)));
	                                        } else{
	                                            X[i][j] = i1.getOutputNominalValues(out);
	                                        }
                                        }
                                    }
                                }
                                out++;
                            }
                        }
                    }
                }
                
            }catch (Exception e){
                System.out.println("Dataset exception = " + e );
                e.printStackTrace();
                System.exit(-1);
            }
            write_results(output_test_name);
        }
        
    }
}
