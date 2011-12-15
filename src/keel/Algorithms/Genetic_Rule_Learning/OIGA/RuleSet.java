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

package keel.Algorithms.Genetic_Rule_Learning.OIGA;


import org.core.*;
import keel.Dataset.*;
import java.util.*;

/**
 * <p>
 * This class represents a set of rules in the OIGA algorithm
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class RuleSet implements Comparable{
	Rule reglas[];
	int nAtt;
	int numberRules;

	double fitness;
	boolean evaluated;
	
	/**
	 * <p>
	 * Default constructor. No memory allocation
	 * </p>
	 */
	public RuleSet(){
		reglas = null;
	}
	
	/**
	 *  Constructor for a fixed number of rules and attributes
	 * @param numberRules number of rules of the set
	 * @param numAtt number of attributes of each rule
	 */
	public RuleSet(int numberRules,int numAtt){
		reglas = new Rule[numberRules];
		this.numberRules = numberRules;
		nAtt = numAtt;
		evaluated = false;
		for(int i=0;i<numberRules;i++){
			reglas[i] = new Rule(numAtt);
			reglas[i].setLength(numAtt);
		}
	}
	
	/**
	 * Deep-copy constructor
	 * @param orig the set of rules which will be copied over this set
	 */
	public RuleSet(RuleSet orig){
		this.numberRules = orig.numberRules;
		reglas = new Rule[numberRules];
		for(int i=0;i<numberRules;i++)
			reglas[i] = new Rule(orig.reglas[i]);
		nAtt = orig.nAtt;
		fitness = orig.fitness;
		evaluated = orig.evaluated;
	}
	
	/**
	 * Reset the current rules, and creates a new -clean- set
	 * @param numberRules number of rules of the set
	 * @param numAtt number of attributes of each rule
	 */
	public void createRules(int numberRules,int numAtt){
		reglas = new Rule[numberRules];
		this.numberRules = numberRules;
		nAtt = numAtt;
		evaluated = false;
		for(int i=0;i<numberRules;i++){
			reglas[i] = new Rule(numAtt);
			reglas[i].setLength(numAtt);
		}
	}
	
	/**
	 * Initialize the set of rules
	 * @param IS train data set used for initialization
	 */
	public void randomizeRules(InstanceSet IS){
		Attribute a =Attributes.getOutputAttribute(0);
		int numClasses,clase;
		double median;
		Instance inst;
		double value;
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		
		for(int j =0;j<nAtt;j++){
			a = Attributes.getInputAttribute(Oiga.attributeOrder[j]);
			for(int i=0;i<numberRules;i++){
				median = a.getMaxAttribute() - a.getMinAttribute();
				median = median/2.0;
				reglas[i].setActivation(j,Randomize.Rand()<0.5);
				if(a.getType() != Attribute.NOMINAL)
					reglas[i].setLimits(j,Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute()), Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute()));
				else
					reglas[i].setLimits(j,Randomize.Randint(0, a.getNumNominalValues()), Randomize.Randint(0, a.getNumNominalValues()));
//				reglas[i].setLimits(j,Randomize.RanddoubleClosed(a.getMinAttribute(), median), Randomize.RanddoubleClosed(median, a.getMaxAttribute()));
//				if(reglas[i].getLimits(j)[0] > reglas[i].getLimits(j)[1])
//					reglas[i].setLimits(j,reglas[i].getLimits(j)[1],reglas[i].getLimits(j)[0]);

				if(j==0){
					reglas[i].setClass(Randomize.Randint(0,numClasses));
//					reglas[i].setClass(Math.abs(i-Randomize.Randint(0,numClasses))%numClasses);
				}
				
//				for(int l=0;l<IS.getNumInstances();l++){
//					inst = IS.getInstance(l);
//					
//					value = inst.getAllInputValues()[Oiga.attributeOrder[j]];
//					clase = (int)inst.getAllOutputValues()[0];
//					if(reglas[i].getActivation(j) && clase == reglas[i].getClas()){
//						if(reglas[i].getLimits(j)[0] > value){
//							reglas[i].setLimits(j,value,reglas[i].getLimits(j)[1]);
//							reglas[i].setActivation(j,false);
//						}
//						if(reglas[i].getLimits(j)[1] < value){
//							reglas[i].setLimits(j,reglas[i].getLimits(j)[0],value);
//							reglas[i].setActivation(j,false);
//						}
//					}
//						
//				}

			}
		}
	}
	
	/**
	 * Classifies the instances of a data sets, and updates the fitness function as
	 * the number of well classified instances
	 * @param ISet the data set that will be classified
	 * @return the obtained fitness
	 */
	public double classify(InstanceSet ISet){
		Attribute a =Attributes.getOutputAttribute(0);
		int numClasses;
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		AttributeCR classVotes[] = new AttributeCR[numClasses];
		Rule r;
		int classObtained;
		double inputs[];
		int output;
		Instance inst;
		
		if(!evaluated){
//			for(int i=0;i<numClasses;i++){
//				classVotes[i] = new AttributeCR(i,-1);
//			}
			fitness = 0;
			for(int i=0;i<ISet.getNumInstances();i++){
				inst = ISet.getInstance(i);
				inputs = inst.getAllInputValues();
//				for(int j=0;j<numClasses;j++){
//					classVotes[j].CR = 0;
//				}

				output = (int)inst.getAllOutputValues()[0];
//				for(int j=0;j<numberRules;j++){
//					r = reglas[j];
//					classObtained = r.evaluate(inputs);
//					if(classObtained!=-1)
//						classVotes[classObtained].CR++;
//				}
//				Arrays.sort(classVotes,Collections.reverseOrder());
//				if(classVotes[0].CR > classVotes[1].CR){
//					if(output == classVotes[0].attribute)
//						fitness+=1;
//				}
				classObtained = this.classify(inst);
				if(classObtained!=-1 && classObtained==output)
					fitness++;
			}
			fitness /= ISet.getNumInstances();
			evaluated = true;
		}
		return fitness;
		
	}
	
	/**
	 * Classifies an instance using the set of rules
	 * @param inst the instance that will be classified
	 * @return the predicted class, or -1 if not covered by any rule (unknown class)
	 */
	public int classify(Instance inst){
		Attribute a =Attributes.getOutputAttribute(0);
		int numClasses;
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		AttributeCR classVotes[] = new AttributeCR[numClasses];
		Rule r;
		int classObtained = -1;
		double inputs[];
		int output;
		
		for(int i=0;i<numClasses;i++){
			classVotes[i] = new AttributeCR(i,0);
		}
		inputs = inst.getAllInputValues();

		output = (int)inst.getAllOutputValues()[0];
		for(int j=0;j<numberRules;j++){
			r = reglas[j];
			classObtained = r.evaluate(inputs);
			if(classObtained!=-1)
				classVotes[classObtained].CR++;
		}
		Arrays.sort(classVotes,Collections.reverseOrder());
		if(classVotes[0].CR == classVotes[1].CR){
				classObtained = -1;
		}
		else{
			classObtained = classVotes[0].attribute;
		}

		return classObtained;
		
	}
	
	/**
	 * Copies the rules from cutpoint to the end of the rule set
	 * @param rs rule set from which the rules will be copied
	 * @param cutpoint_rule the rule in which there is the cutpoint
	 * @param cutpoint_variable the variable (activation, limits or class) in which the cutpoint is
	 */
	public void copyFromPointtoEnd(RuleSet rs,int cutpoint_rule,int cutpoint_variable){
		Rule rule1 = reglas[cutpoint_rule];
		Rule rule2 = rs.reglas[cutpoint_rule];
		
		//copy partial rule if needed
		int i = cutpoint_variable;
		if(cutpoint_variable%3 == 1){
//			rule1.setActivation(cutpoint_variable/3, rule2.getActivation(cutpoint_variable/3));
			rule1.setLimits(cutpoint_variable/3, rule2.getLimits(cutpoint_variable/3)[0], rule2.getLimits(cutpoint_variable/3)[1]);
			i += 2;
		}
		if(cutpoint_variable%3 == 2){
			rule1.setActivation(cutpoint_variable/3, rule2.getActivation(cutpoint_variable/3));
			rule1.setLimits(cutpoint_variable/3, rule1.getLimits(cutpoint_variable/3)[0], rule2.getLimits(cutpoint_variable/3)[1]);
			i += 1;
		}
		
		for(i = i/3;i<nAtt;i++){
//			System.err.println(nAtt+" "+i);
			rule1.setActivation(i, rule2.getActivation(i));
			rule1.setLimits(i,rule2.getLimits(i)[0], rule2.getLimits(i)[1]);
		}
		rule1.setClass(rule2.getClas());
		//copy complete rules
		for(int j=cutpoint_rule+1;j<numberRules;j++){
			reglas[j] = new Rule(rs.reglas[j]);
		}
		
	}
	
	/**
	 * Copies the rules from the beginning of the rule set to the selected cutpoint
	 * @param rs rule set from which the rules will be copied
	 * @param cutpoint_rule the rule in which there is the cutpoint
	 * @param cutpoint_variable the variable (activation, limits or class) in which the cutpoint is
	 */
	public void copyFromBegintoPoint(RuleSet rs,int cutpoint_rule,int cutpoint_variable){
		Rule rule1 = reglas[cutpoint_rule];
		Rule rule2 = rs.reglas[cutpoint_rule];
		
//		copy complete rules
		for(int j=0;j<cutpoint_rule;j++){
			reglas[j] = new Rule(rs.reglas[j]);
		}
		
		//copy partial rule if needed
		if(cutpoint_variable%3 == 1){
			rule1.setActivation(cutpoint_variable/3, rule2.getActivation(cutpoint_variable/3));
//			rule1.setLimits(cutpoint_variable/3, rule2.getLimits(cutpoint_variable/3)[0], rule1.getLimits(cutpoint_variable/3)[1]);

		}
		if(cutpoint_variable%3 == 2){
			rule1.setActivation(cutpoint_variable/3, rule2.getActivation(cutpoint_variable/3));
			rule1.setLimits(cutpoint_variable/3, rule2.getLimits(cutpoint_variable/3)[0], rule1.getLimits(cutpoint_variable/3)[1]);
		}
		
		for(int i = 0;i<cutpoint_variable/3;i++){
			rule1.setActivation(i, rule2.getActivation(i));
			rule1.setLimits(i,rule2.getLimits(i)[0], rule2.getLimits(i)[1]);
		}
		if(cutpoint_variable%3 == 0 && (cutpoint_variable+1)%numberRules ==0)
			rule1.setClass(rule2.getClas());
		
	}
	
	/**
	 * Mutate a variable of the rule set (i.e. the activation, limits or class of a rule
	 * from the rule set)
	 * @param gene the gene (variable) that will be mutated
	 */
	public void mutate(int gene){
		Attribute a = null;
		int rule = gene / (3*nAtt+1); //rule affected
		int attPos = gene%(3*nAtt+1); //position in the rule string affected
		int attNum = attPos/3; // attribute of the data set affected
		int att = attPos%3;	//attribute property (active, minlimit, maxlimit) affected
		
		if(attNum<nAtt)
			a = Attributes.getInputAttribute(Oiga.attributeOrder[attNum]);
		
		if(att == 1){ //min limit value
			if(a.getType() != Attribute.NOMINAL)
				reglas[rule].setLimits(attNum, Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute()), reglas[rule].getLimits(attNum)[1]);
			else
				reglas[rule].setLimits(attNum, Randomize.Randint(0, a.getNumNominalValues()), reglas[rule].getLimits(attNum)[1]);
		}
		if(att == 2){ //max limit value
			if(a.getType() != Attribute.NOMINAL)
				reglas[rule].setLimits(attNum, reglas[rule].getLimits(attNum)[0] ,Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute()));
			else
				reglas[rule].setLimits(attNum, reglas[rule].getLimits(attNum)[0] ,Randomize.Randint(0, a.getNumNominalValues()));
		}
		if(att==0){
			if(attPos!=(3*nAtt)) //not the class label in the rule
				reglas[rule].setActivation(attNum, !reglas[rule].getActivation(attNum));
			else //class label
				reglas[rule].setClass(Randomize.Randint(0, Attributes.getOutputAttribute(0).getNumNominalValues()));
		}
	}
	
	/**
	 * Incremental Genetic Algorithm, which increases the size of the actual rule set
	 * appending all the attributes from rs to the rules of the actual rule set. 
	 * The rules used for this task should have the same class as the rule from our
	 * rule set, but if we cannot find it, we use any rule.
	 * @param rs the rule set from which we will append the attributes
	 */
	public void iga(RuleSet rs){
		Attribute a =Attributes.getOutputAttribute(0);
		int numClasses;
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		
		Rule r,s;
		int rnd;
		int curClass;
		Vector pool = new Vector();
		
		for(int i=0;i<numberRules;i++){
			r = reglas[i];
			curClass = r.getClas();
			//if it is not a created rule, assign a random class
			//and append the attribute from SEM
			if(curClass == -1){
				r.setClass(Randomize.Randint(0, numClasses));
				curClass = r.getClas();
			}
			for(int j=0;j<rs.numberRules;j++){
				if(curClass == rs.reglas[j].getClas())
					pool.addElement(rs.reglas[j]);
			}
			if(pool.size()!=0){
				rnd = Randomize.Randint(0, pool.size());
				s = (Rule)pool.elementAt(rnd);
			}
			else{
				System.out.print(".");
				System.out.flush();
				s = rs.reglas[Randomize.Randint(0,rs.numberRules)];
			}
			r.append(s);
		}
		this.nAtt += rs.nAtt;
	}
	
	/**
	 * Test if the rule set is currently evaluated
	 * @return if the rule set is evaluated, so its fitness is OK
	 */
	public boolean isEvaluated(){
		return evaluated;
	}
	
	/**
	 * Returns the evaluation state
	 * @param eval the evaluation state (true or false) of this rule set
	 */
	public void setEvaluated(boolean eval){
		evaluated = eval;
	}
	
	/**
	 * Gets the fitness of this rule set
	 * @return the current fitness of the rule set (updated or NOT!)
	 */
	public double getFitness(){
		return fitness;
	}
	
/**
 * It compares the RuleSet with other one
 * 
 * @param o the rule set to compare
 * @return 0 if both rule sets have the same fitness, 1 if the fitness of this is higher, and -1 otherwise 
 */
	public int compareTo(Object o){
		RuleSet rs = (RuleSet) o;
		if(this.fitness < rs.fitness)
			return -1;
		if(this.fitness > rs.fitness)
			return 1;
		return 0;
	}
	
	/**
	 * Test if the fitness of the rule sets are equal
	 * @param rs the rule set which will be compared to ours
	 * @return if they have equal fitness or not
	 */
	public boolean equals(RuleSet rs){
		return (this.fitness == rs.fitness);		
	}
	
}

