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

package keel.Algorithms.Fuzzy_Rule_Learning.Hybrid.FURIA.core;

import java.util.Enumeration;

/**
 * This class implements a single rule that predicts specified class.  
 *
 * A rule consists of antecedents "AND"ed together and the consequent 
 * (class value) for the classification.  
 * In this class, the Information Gain (p*[log(p/t) - log(P/T)]) is used to
 * select an antecedent and Reduced Error Prunning (REP) with the metric
 * of accuracy rate p/(p+n) or (TP+TN)/(P+N) is used to prune the rule. 
 */    
public class RipperRule extends Rule{
  
  /** for serialization */
  static final long serialVersionUID = -2410020717305262952L;
	
  /** The internal representation of the class label to be predicted */
  public double m_Consequent = -1;	
		
  /** The vector of antecedents of this rule*/
  public FastVector m_Antds = null;

  /** The minimal number of instance weights within a split*/
  double m_MinNo = 2.0;

  /** Whether in a debug mode */
  protected boolean m_Debug = false;

  /** The class distribution of the training data*/
  double[] aprioriDistribution;
  
  /** Constructor */
  public RipperRule(){    
    m_Antds = new FastVector();
  }
  
  /** Constructor
     * @param aprioriClassDistribution  apriori class distribution to be set.*/
  public RipperRule(double [] aprioriClassDistribution){    
    m_Antds = new FastVector();	
    this.aprioriDistribution = aprioriClassDistribution.clone();
  }
	
  /**
   * Sets the internal representation of the class label to be predicted
   * 
   * @param cl the internal representation of the class label to be predicted
   */
  public void setConsequent(double cl) {
    m_Consequent = cl; 
  }
  
  /**
   * Gets the internal representation of the class label to be predicted
   * 
   * @return the internal representation of the class label to be predicted
   */
  public double getConsequent() { 
    return m_Consequent; 
  }
	
  /**
   * Get a shallow copy of this rule
   *
   * @return the copy
   */
  public Object copy(){
    RipperRule copy = new RipperRule();
    copy.setConsequent(getConsequent());
    copy.m_Antds = (FastVector)this.m_Antds.copyElements();
    copy.aprioriDistribution = this.aprioriDistribution.clone();
    return copy;
  }
	
  /**
   * The degree of coverage instance covered by this rule
   * 
   * @param datum the instance in question
   * @return the degree to which the instance 
   *         is covered by this rule
   */
  public double coverageDegree(Instance datum){
    double isCover=1;

    for(int i=0; i<m_Antds.size(); i++){
	Antd antd = (Antd)m_Antds.elementAt(i);
	isCover *= antd.covers(datum);
    }
    return isCover;
  } 
  
  /**
   * Whether the instance covered by this rule
   * 
   * @param datum the instance in question
   * @return the boolean value indicating whether the instance 
   *         is covered by this rule
   */
  public boolean covers(Instance datum){ 
    if (coverageDegree(datum) == 0){
	return false;
    }else{
	return true;
    }
  }       
	
  /**
   * Whether this rule has antecedents, i.e. whether it is a default rule
   * 
   * @return the boolean value indicating whether the rule has antecedents
   */
  public boolean hasAntds(){
    if (m_Antds == null)
	return false;
    else
	return (m_Antds.size() > 0);
  }      
	
  /** 
   * the number of antecedents of the rule
   *
   * @return the size of this rule
   */
  public double size(){ return (double)m_Antds.size(); }		

	
  /**
   * Private function to compute default number of accurate instances
   * in the specified data for the consequent of the rule
   * 
   * @param data the data in question
   * @return the default accuracy number
   */
  private double computeDefAccu(Instances data){ 
    double defAccu=0;
    for(int i=0; i<data.numInstances(); i++){
	Instance inst = data.instance(i);
	if((int)inst.classValue() == (int)m_Consequent)
	  defAccu += inst.weight();
    }
    return defAccu;
  }
	
	
  /**
   * Build one rule using the growing data
   *
   * @param data the growing data used to build the rule
   * @throws Exception if the consequent is not set yet
   */    
    public void grow(Instances data) throws Exception {
    if(m_Consequent == -1)
	throw new Exception(" Consequent not set yet.");
	    
    Instances growData = data;	         
    double sumOfWeights = growData.sumOfWeights();
    if(!Utils.gr(sumOfWeights, 0.0))
	return;
	    
    /* Compute the default accurate rate of the growing data */
    double defAccu = computeDefAccu(growData);
    double defAcRt = (defAccu+1.0)/(sumOfWeights+1.0); 
	    
    /* Keep the record of which attributes have already been used*/    
    boolean[] used=new boolean [growData.numAttributes()];
    for (int k=0; k<used.length; k++)
	used[k]=false;
    int numUnused=used.length;
	    
    // If there are already antecedents existing
    for(int j=0; j < m_Antds.size(); j++){
	Antd antdj = (Antd)m_Antds.elementAt(j);
	if(!antdj.getAttr().isNumeric()){ 
	  used[antdj.getAttr().index()]=true;
	  numUnused--;
	} 
    }	    
	    
    double maxInfoGain;	    
    while (Utils.gr(growData.numInstances(), 0.0) && 
	     (numUnused > 0) 
	     && Utils.sm(defAcRt, 1.0)
	     ){   
		
	// We require that infoGain be positive
	/*if(numAntds == originalSize)
	  maxInfoGain = 0.0; // At least one condition allowed
	  else
	  maxInfoGain = Utils.eq(defAcRt, 1.0) ? 
	  defAccu/(double)numAntds : 0.0; */
	maxInfoGain = 0.0; 
		
	/* Build a list of antecedents */
	Antd oneAntd=null;
	Instances coverData = null;
	Enumeration enumAttr=growData.enumerateAttributes();	      
		
	/* Build one condition based on all attributes not used yet*/
	while (enumAttr.hasMoreElements()){
	  AttributeWeka att= (AttributeWeka)(enumAttr.nextElement());
	  
	  if(m_Debug)
	    System.err.println("\nOne condition: size = " 
			       + growData.sumOfWeights());
		   
	  Antd antd =null;	
	  if(att.isNumeric())
	    antd = new NumericAntd(att);
	  else
	    antd = new NominalAntd(att);
		    
	  if(!used[att.index()]){
	    /* Compute the best information gain for each attribute,
	       it's stored in the antecedent formed by this attribute.
	       This procedure returns the data covered by the antecedent*/
	    Instances coveredData = computeInfoGain(growData, defAcRt,
						    antd);
	    if(coveredData != null){
	      double infoGain = antd.getMaxInfoGain();      
	      if(m_Debug)
		System.err.println("Test of \'"+antd.toString()+
				   "\': infoGain = "+
				   infoGain + " | Accuracy = " +
				   antd.getAccuRate()+
				   "="+antd.getAccu()
				   +"/"+antd.getCover()+
				   " def. accuracy: "+defAcRt);
			    
	      if(infoGain > maxInfoGain){         
		oneAntd=antd;
		coverData = coveredData;  
		maxInfoGain = infoGain;
	      }		    
	    }
	  }
	}
		
	if(oneAntd == null) break; // Cannot find antds		
	if(Utils.sm(oneAntd.getAccu(), m_MinNo)) break;// Too low coverage
		
	//Numeric attributes can be used more than once
	if(!oneAntd.getAttr().isNumeric()){ 
	  used[oneAntd.getAttr().index()]=true;
	  numUnused--;
	}
			
	m_Antds.addElement(oneAntd);
	
	
	growData = coverData;// Grow data size is shrinking 	
	defAcRt = oneAntd.getAccuRate();
    }
  }
	
	
  /** 
   * Compute the best information gain for the specified antecedent
   *  
   * @param instances the data based on which the infoGain is computed
   * @param defAcRt the default accuracy rate of data
   * @param antd the specific antecedent
   * @param numConds the number of antecedents in the rule so far
   * @return the data covered by the antecedent
   */
  private Instances computeInfoGain(Instances instances, double defAcRt, 
				      Antd antd){
	Instances data = instances;
	
    /* Split the data into bags.
	 The information gain of each bag is also calculated in this procedure */
    Instances[] splitData = antd.splitData(data, defAcRt, 
					     m_Consequent); 
	    
    /* Get the bag of data to be used for next antecedents */
    if(splitData != null)
	return splitData[(int)antd.getAttrValue()];
    else return null;
  }
	
  /**
   * Prune all the possible final sequences of the rule using the 
   * pruning data.  The measure used to prune the rule is based on
   * flag given.
   *
   * @param pruneData the pruning data used to prune the rule
   * @param useWhole flag to indicate whether use the error rate of
   *                 the whole pruning data instead of the data covered
   */    
  public void prune(Instances pruneData, boolean useWhole){
	Instances data = pruneData;
	
    double total = data.sumOfWeights();
    if(!Utils.gr(total, 0.0))
	return;
	
    /* The default accurate # and rate on pruning data */
    double defAccu=computeDefAccu(data);
	    
    if(m_Debug)	
	System.err.println("Pruning with " + defAccu + 
			   " positive data out of " + total +
			   " instances");	
	    
    int size=m_Antds.size();
    if(size == 0) return; // Default rule before pruning
	    
    double[] worthRt = new double[size];
    double[] coverage = new double[size];
    double[] worthValue = new double[size];
    for(int w=0; w<size; w++){
	worthRt[w]=coverage[w]=worthValue[w]=0.0;
    }
	    
    /* Calculate accuracy parameters for all the antecedents in this rule */
    double tn = 0.0; // True negative if useWhole
    for(int x=0; x<size; x++){
	Antd antd=(Antd)m_Antds.elementAt(x);
	Instances newData = data;
	data = new Instances(newData, 0); // Make data empty
		
	for(int y=0; y<newData.numInstances(); y++){
	  Instance ins=newData.instance(y);
		    
	  if(antd.covers(ins)>0){   // Covered by this antecedent
	    coverage[x] += ins.weight();
	    data.add(ins);                 // Add to data for further pruning
	    if((int)ins.classValue() == (int)m_Consequent) // Accurate prediction
	      worthValue[x] += ins.weight();
	  }
	  else if(useWhole){ // Not covered
	    if((int)ins.classValue() != (int)m_Consequent)
	      tn += ins.weight();
	  }			
	}
		
	if(useWhole){
	  worthValue[x] += tn;
	  worthRt[x] = worthValue[x] / total;
	}
	else // Note if coverage is 0, accuracy is 0.5
	  worthRt[x] = (worthValue[x]+1.0)/(coverage[x]+2.0);
    }
	    
    double maxValue = (defAccu+1.0)/(total+2.0);
    int maxIndex = -1;
    for(int i=0; i<worthValue.length; i++){
	if(m_Debug){
	  double denom = useWhole ? total : coverage[i];
	  System.err.println(i+"(useAccuray? "+!useWhole+"): "
			     + worthRt[i] + 
			     "="+worthValue[i]+
			     "/"+denom);
	}
	if(worthRt[i] > maxValue){ // Prefer to the 
	  maxValue = worthRt[i]; // shorter rule
	  maxIndex = i;
	}
    }
	
    if (maxIndex==-1) return;
    
    /* Prune the antecedents according to the accuracy parameters */
    for(int z=size-1;z>maxIndex;z--)
	m_Antds.removeElementAt(z);       
  }
	
  /**
   * Prints this rule
   *
   * @param classAttr the class attribute in the data
   * @return a textual description of this rule
   */
  public String toString(AttributeWeka classAttr) {
    StringBuffer text =  new StringBuffer();
    if(m_Antds.size() > 0){
	for(int j=0; j< (m_Antds.size()-1); j++)
	  text.append("(" + ((Antd)(m_Antds.elementAt(j))).toString()+ ") and ");
	text.append("("+((Antd)(m_Antds.lastElement())).toString() + ")");
    }
    text.append(" => " + classAttr.name() +
		  "=" + classAttr.value((int)m_Consequent));
	    
    return text.toString();
  }
  

  
  /**
   * This function fits the rule to the data which it overlaps. 
   * This way the rule can only interpolate but not extrapolate.
   * @param instances The data to which the rule shall be fitted
   */
  public void fitAndSetCoreBound(Instances instances) {
    if (m_Antds == null) return;
    boolean[] antExistingForDimension = new boolean[instances.numAttributes()-1];
    for (int i = 0; i < m_Antds.size(); i++){
	antExistingForDimension[((Antd)m_Antds.elementAt(i)).att.index()] = true;
    }

    FastVector newAntds = new FastVector(10);
//    for (int i=0; i < instances.numAttributes()-1; i++){
    for (int iterator=0; iterator < m_Antds.size(); iterator++){
	int i = ((Antd)m_Antds.elementAt(iterator)).getAttr().index();
	
	if (!antExistingForDimension[i]) continue; // Excluding non existant antecedents
	Instances instancesWithoutMissingValues = new Instances(instances); 
	instancesWithoutMissingValues.deleteWithMissing(i);

	if (instancesWithoutMissingValues.attribute(i).isNumeric() && instancesWithoutMissingValues.numInstances()>0){
	  boolean bag0AntdExists = false;
	  boolean bag1AntdExists = false;
	  for (int j =0; j < m_Antds.size(); j++){
	    if (((Antd)m_Antds.elementAt(j)).att.index() == i){
	      if (((Antd)m_Antds.elementAt(j)).value == 0){
		bag0AntdExists = true;
	      }else{
		bag1AntdExists = true;
	      }
	      newAntds.addElement((Antd)m_Antds.elementAt(j));
	    }
	  }

	  double higherCore = Double.NaN;
	  double lowerCore = Double.NaN;


	  if (!bag0AntdExists){
	    if (Double.isNaN(higherCore))
	      higherCore = instancesWithoutMissingValues.kthSmallestValue(i, instancesWithoutMissingValues.numInstances());
	    NumericAntd antd;
	    antd = new NumericAntd(instancesWithoutMissingValues.attribute(i));
	    antd.value = 0;
	    antd.splitPoint = higherCore;
	    newAntds.addElement(antd);
	  }


	  if (!bag1AntdExists){
	    if (Double.isNaN(lowerCore))
	      lowerCore = instancesWithoutMissingValues.kthSmallestValue(i, 1);
	    NumericAntd antd;
	    antd = new NumericAntd(instancesWithoutMissingValues.attribute(i));
	    antd.value = 1;
	    antd.splitPoint = lowerCore;
	    newAntds.addElement(antd);
	  }
	}else{
	  for (int j =0; j < m_Antds.size(); j++){
	    if (((Antd)m_Antds.elementAt(j)).att.index() == i){
	      newAntds.addElement(m_Antds.elementAt(j));
	    }
	  }
	}				
    }
    m_Antds = newAntds;		
  }

    /**
     * Finds and sets the support bound for the known antecedents.
     * @param thisClassifiersExtension instances to extend the classifier.
     * @param allWeightsAreOne true if all weights are one.
     */
    public void findAndSetSupportBoundForKnownAntecedents(Instances thisClassifiersExtension, boolean allWeightsAreOne){
    if (m_Antds == null) return;

    double maxPurity = Double.NEGATIVE_INFINITY;
    boolean[] finishedAntecedents = new boolean[m_Antds.size()];
    int numFinishedAntecedents = 0;


    while (numFinishedAntecedents<m_Antds.size()){	
	double maxPurityOfAllAntecedents = Double.NEGATIVE_INFINITY;
	int bestAntecedentsIndex = -1;
	double bestSupportBoundForAllAntecedents = Double.NaN;

	Instances ext = new Instances(thisClassifiersExtension,0);
	for (int j = 0; j < m_Antds.size(); j++){
	  if(finishedAntecedents[j]) continue; 

	  ext = new Instances (thisClassifiersExtension);
	  /*
	   * Remove instances which are not relevant, because they are not covered
	   * by the _other_ antecedents.
	   */
	  for (int k = 0; k < m_Antds.size(); k++){
	    if (k==j) continue;
	    Antd exclusionAntd = ((Antd)m_Antds.elementAt(k));
	    for (int y = 0; y < ext.numInstances(); y++){
	      if (exclusionAntd.covers(ext.instance(y)) == 0){
		ext.delete(y--);
	      }
	    }
	  }


	  if (ext.attribute(((Antd)m_Antds.elementAt(j)).att.index()).isNumeric() && ext.numInstances()>0){
	    NumericAntd currentAntd = (NumericAntd) ((NumericAntd) m_Antds.elementAt(j)).copy();
	    currentAntd.fuzzyYet=true;
	    ext.deleteWithMissing(currentAntd.att.index());

	    double sumOfWeights = ext.sumOfWeights();
	    if(!Utils.gr(sumOfWeights, 0.0))
	      return;


	    ext.sort(currentAntd.att.index());

	    double maxPurityForThisAntecedent = 0;
	    double bestFoundSupportBound = Double.NaN;

	    double lastAccu = 0;
	    double lastCover = 0;
	    // Test all possible edge points                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	    if (currentAntd.value == 0){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	      for (int k = 1; k < ext.numInstances(); k++){
		// break the loop if there is no gain (only works when all instances have weight 1)
		if ((lastAccu+(ext.numInstances()-k-1))/(lastCover+(ext.numInstances()-k-1)) < maxPurityForThisAntecedent && allWeightsAreOne){
		  break;
		}

		// Bag 1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
		if (currentAntd.splitPoint < ext.instance(k).value(currentAntd.att.index())                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		    && ext.instance(k).value(currentAntd.att.index()) != ext.instance(k-1).value(currentAntd.att.index())){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		  currentAntd.supportBound = ext.instance(k).value(currentAntd.att.index());                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                

		  double[] accuArray = new double[ext.numInstances()];                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
		  double[] coverArray =  new double[ext.numInstances()];                       
		  for (int i = 0; i < ext.numInstances(); i++){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
		    coverArray[i] = ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		    double coverValue = currentAntd.covers(ext.instance(i));
		    if (coverArray[i] >= coverValue*ext.instance(i).weight()){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
		      coverArray[i] = coverValue*ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		      if (ext.instance(i).classValue() == m_Consequent){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			accuArray[i] = coverValue*ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		      }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
		  }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      

		  double purity = (Utils.sum(accuArray)) / (Utils.sum(coverArray));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
		  if (purity >= maxPurityForThisAntecedent){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
		    maxPurityForThisAntecedent =purity;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
		    bestFoundSupportBound =  currentAntd.supportBound;     
		  }           
		  lastAccu = Utils.sum(accuArray);
		  lastCover = Utils.sum(coverArray);
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	      }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
	    }else{                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	      for (int k = ext.numInstances()-2; k >=0; k--){
		// break the loop if there is no gain (only works when all instances have weight 1)
		if ((lastAccu+(k))/(lastCover+(k)) < maxPurityForThisAntecedent && allWeightsAreOne){
		  break;
		}
		//Bag 2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
		if (currentAntd.splitPoint > ext.instance(k).value(currentAntd.att.index())                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		    && ext.instance(k).value(currentAntd.att.index()) != ext.instance(k+1).value(currentAntd.att.index())){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		  currentAntd.supportBound = ext.instance(k).value(currentAntd.att.index());                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                

		  double[] accuArray = new double[ext.numInstances()];
		  double[] coverArray =  new double[ext.numInstances()];
		  for (int i = 0; i < ext.numInstances(); i++){
		    coverArray[i] = ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		    double coverValue = currentAntd.covers(ext.instance(i));
		    if (coverArray[i] >= coverValue*ext.instance(i).weight()){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
		      coverArray[i] = coverValue*ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		      if (ext.instance(i).classValue() == m_Consequent){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
			accuArray[i] = coverValue*ext.instance(i).weight();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		      }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
		  }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      

		  double purity = (Utils.sum(accuArray)) / (Utils.sum(coverArray));                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
		  if (purity >= maxPurityForThisAntecedent){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
		    maxPurityForThisAntecedent =purity;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
		    bestFoundSupportBound =  currentAntd.supportBound;    
		  }
		  lastAccu = Utils.sum(accuArray);
		  lastCover = Utils.sum(coverArray);
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
	      }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              

	    }             

	    if (maxPurityForThisAntecedent>maxPurityOfAllAntecedents){
	      bestAntecedentsIndex = j;
	      bestSupportBoundForAllAntecedents = bestFoundSupportBound;
	      maxPurityOfAllAntecedents = maxPurityForThisAntecedent;
	    }
	  }else{
	    //Nominal Antd
	    finishedAntecedents[j] = true;
	    numFinishedAntecedents++;
	    continue;
	  }
	}

	if (bestAntecedentsIndex==-1) {
	  return;
	}

	if (maxPurity <= maxPurityOfAllAntecedents){
	  if (Double.isNaN(bestSupportBoundForAllAntecedents)){
	    ((NumericAntd)m_Antds.elementAt(bestAntecedentsIndex)).supportBound =  ((NumericAntd)m_Antds.elementAt(bestAntecedentsIndex)).splitPoint;
	  }else{
	    ((NumericAntd)m_Antds.elementAt(bestAntecedentsIndex)).supportBound = bestSupportBoundForAllAntecedents;
	    ((NumericAntd)m_Antds.elementAt(bestAntecedentsIndex)).fuzzyYet = true;
	  }
	  
	  maxPurity = maxPurityOfAllAntecedents;
	}
	finishedAntecedents[bestAntecedentsIndex] = true;
	numFinishedAntecedents++;
    }

  }
  
    /**
     * Computes the confidences of the given data.
     * @param data given data.
     * @throws Exception if the data is not correct.
     */
    public void calculateConfidences(Instances data) throws Exception{
    RipperRule tempRule = (RipperRule) this.copy();
    
    while(tempRule.hasAntds()){
	double acc = 0;
	double cov = 0;
	for (int i = 0; i < data.numInstances(); i++){
	  double membershipValue = tempRule.coverageDegree(data.instance(i));
	  cov += membershipValue;
	  if (m_Consequent == data.instance(i).classValue()){
	    acc += membershipValue;
	  }
	}

	// m-estimate
	double m = 2.0;
	((Antd)this.m_Antds.elementAt((int)tempRule.size()-1)).m_confidence = (acc+m*(aprioriDistribution[(int)m_Consequent]/
	    Utils.sum(aprioriDistribution))) / (cov+m);
	tempRule.m_Antds.removeElementAt(tempRule.m_Antds.size()-1);
    }
  }
  
    /**
     * Returns the confidence of the last element of the antecetents.
     * @return the confidence of the last element of the antecetents.
     */
    public double getConfidence(){
    if (!hasAntds()) return Double.NaN;
    return ((Antd)m_Antds.lastElement()).m_confidence;
  }

    /**
     * String "1.0"
     * @return "1.0"
     */
    public String getRevision() {
    return "1.0";
  }
  
}