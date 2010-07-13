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
 * @author Written by Luciano Sanchez (University of Oviedo) 20/01/2004 
 * @author Modified by J.R. Villar (University of Oviedo) 18/12/2008
 * @version 1.0 
 * @since JDK1.4 
 * </p> 
 */ 

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes;
import java.util.Vector;
import org.core.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;



public class GenotypeFuzzyGAP extends Genotype {
/** 
* <p> 
* GenotypeFuzzyGAP is the base clase to represent the genotype when a fuzzy
* model is to be learned with the genetic algorithm and programming (GAP).
* Both the constants and the tree node are learned.
* 
* The Fuzzy model variables are characterized with {@link FuzzyPartition}s.
* The root node is a {@link NodeRuleBase}.
* </p> 
*/ 
    // Sintactical tree is representation of an individual, 
    // so the fenotype is the same as genotype
    // Maximum height of the generated trees
	static int MAXH;                   
    //The root node
    Node rootNode;                         // Pointer to tree root.
	//The list of nodes
    Vector theNodes;                         // Vector of Pointers to each node 
	//The list of parents of each node
    Vector theParents;                         // Vector of Pointers to parents of each node
	//The number of nodes from the current until the node.
    Vector theDepthAtEachNode;                // Number of nodes till root. height.
    //The input variables partitions
    FuzzyPartition[] A;
	//The output variable partitions
    FuzzyPartition B;
   	
    // The string part is not stored separately. It's build when a crossing or mutation are made.
 /**
* <p>
* Class constructor with the following parameters:
* </p>
* @param a the {@link FuzzyPartition} array, a {@link FuzzyPartition} for each input variable
* @param b the {@link FuzzyPartition} for the output variable
* @param mh the maximum valid height of a tree 
* @param r the {@link Randomize} object
*/
   public GenotypeFuzzyGAP(FuzzyPartition[] a, FuzzyPartition b, int mh, Randomize r) {
        super(r);
        theNodes=new Vector(); theParents=new Vector(); theDepthAtEachNode=new Vector();
        rootNode=new NodeRuleBase(new NodeRule[0]);
        // A=a; B=b;
        A = new FuzzyPartition[a.length]; 
        for (int i=0;i<a.length;i++) A[i]=a[i].clone();
        B = b.clone();
        MAXH=mh;
    }
    
/**
* <p>
* The copy constructor.
* </p>
* @param p the {@link GenotypeFuzzyGAP} to be copied
*/
     public GenotypeFuzzyGAP(GenotypeFuzzyGAP p) {
        super(p.rand);
        rootNode=p.rootNode.clone();
        A = new FuzzyPartition[p.A.length]; 
        for (int i=0;i<p.A.length;i++) A[i]=p.A[i].clone();
        B = p.B.clone();
        buildNodeLists();
    }
    
/**
* <p>
* This method copies the given parameter into the current object.
* </p>
* @param p the {@link GenotypeFuzzyGAP} to be copied
*/
    public void set(GenotypeFuzzyGAP p) {
        rootNode=p.rootNode.clone();
        A = new FuzzyPartition[p.A.length]; 
        for (int i=0;i<p.A.length;i++) A[i]=p.A[i].clone();
        B = p.B.clone();
        buildNodeLists();
    }
    
/**
* <p>
* This method determines if the given {@link Genotype} is of the same
* type thant the current object. 
* The nodes are analyzed to be the same tree without considering the
* constants values.
* </p>
* @param p the {@link Genotype} to be compared
* @return true if both objects are related
*/
    public boolean isRelated(Genotype g) {
        // It's compared only expresional part, not constants.
        GenotypeFuzzyGAP p=(GenotypeFuzzyGAP)g;
        return rootNode.compatible(p.rootNode);
    }
    
/**
* <p>
* This method is intended for generating a perfect copy of the current Genotype.
* </p>
* @return the newly created {@link Genotype} which is a perfect copy of current individual
*/
    public Genotype clone() {
        return  new GenotypeFuzzyGAP(this);
    }
    
/**
* <p>
* This method is intended to rebuild the current object if needed.
* In this case, nothing has to be done.
* </p>
* @param g the {@link Genotype} to rebuild.
*/
    public void rebuild(Genotype g) {};
    
/**
* <p>
* Method for carrying out the mutation genetic operations.
* </p>
* @param alpha double value kept for compatibility, not used.
* @param mutationID an int with the crossover operation to be carried out:
*                   {@link OperatorIdent.GAPMUTAGA} for the genetic algorithm mutation
*                   {@link OperatorIdent.GAPMUTAGP} for the genetic programming mutation
* @throws {@link invalidMutation} if mutationID is not valid
*/
    public void mutation(double alpha, int mutationID) throws invalidMutation {
        
        if (mutationID!=OperatorIdent.GAPMUTAGA && mutationID !=OperatorIdent.GAPMUTAGP) throw new invalidMutation();
        
        // An individual is generated at random
        GenotypeFuzzyGAP offspng1 = new GenotypeFuzzyGAP(A,B,MAXH,rand);
        GenotypeFuzzyGAP offspng2 = new GenotypeFuzzyGAP(A,B,MAXH,rand);
        GenotypeFuzzyGAP randomGenotype = new GenotypeFuzzyGAP(A,B,MAXH,rand);
        randomGenotype.Random();
        
        try {
            if (mutationID==OperatorIdent.GAPMUTAGA) {
                crossover(randomGenotype,offspng1,offspng2,OperatorIdent.GAPCROSSGA);
                set(offspng1);
            } else {
                crossover(randomGenotype,offspng1,offspng2,OperatorIdent.GAPCROSSGP);
                set(offspng1);
            }
        } catch (invalidCrossover c) {
            System.out.println("Internal error within the mutacion of a GenotypeFuzzyGAP");
        }
        
    }
    
    
/**
* <p>
* Private method for carrying out the crossover of the output partitions.
* </p>
* @param cont3 the first individual to be crossed, represents the also the first offspring.
* @param cont4 the second individual to be crossed, represents the also the second offspring.
*/
    private void partBInternalCrossover(double []cont3, double[]cont4)
    {    
        double []ind3; double []ind4;
        
        ind3=new double[cont3.length];
        ind4=new double[cont4.length];
        
        
        double min=cont3[0]; double max=cont3[cont3.length-1]; double factor=(max-min);
        for (int j=0;j<ind3.length;j++) ind3[j]=(cont3[j]-min)/factor;
        for (int j=0;j<ind4.length;j++) ind4[j]=(cont4[j]-min)/factor;
        
        
        double alpha=(rand.Rand()-0.5)*0.25;
        
        // The crossover
        for (int j=0;j<ind3.length;j++) {
            double val1=ind3[j]+alpha*(ind4[j]-ind3[j]);
            double val2=ind4[j]+alpha*(ind3[j]-ind4[j]);
            ind3[j]=val1;
            ind4[j]=val2;
        }
        
        // Children are normalized
        for (int j=0;j<ind3.length-1;j++) 
            if (ind3[j+1]-ind3[j]<0) ind3[j+1]=ind3[j];
        for (int j=0;j<ind4.length-1;j++) 
            if (ind4[j+1]-ind4[j]<0) ind4[j+1]=ind4[j];
        
        for (int j=0;j<ind3.length;j++) ind3[j]/=ind3[ind3.length-1];
        for (int j=0;j<ind4.length;j++) ind4[j]/=ind4[ind4.length-1];
        
        // Adjust children ranges
        for (int j=0;j<ind3.length;j++) cont3[j]=ind3[j]*factor+min;
        for (int j=0;j<ind4.length;j++) cont4[j]=ind4[j]*factor+min;
        
    }
    
/**
* <p>
* The method for carrying out the crossover genetic operations.
* </p>
* @param parent the second parent in the crossover operation, it's an {@link Genotype} object
* @param of1 the {@link Genotype} object with the first offspring
* @param of2 the {@link Genotype} object with the second offspring
* @param crossoverID an int with the crossover operation to be carried out:
*                    {@link OperatorIdent.GAPCRUCEGA} for genetic algorithm crossover
*                    {@link OperatorIdent.GAPCRUCEGP} for genetic programming crossover
* @throws {@link invalidCrossover} if crossoverID is not valid
*/
    public void crossover(Genotype parent, Genotype of1, Genotype of2, int crossoverID) throws invalidCrossover {
        
        // Two sub-trees are swaped, avoiding raise the maximum allowed heigth
        
        // A node from the first sub-tree is selected.
        // When a node 'variable', 'consecuente' or 'regla' is mutated 
        // the tree must be repared

        final boolean debug=false;
        
        if (crossoverID!=OperatorIdent.GAPCROSSGA && crossoverID!=OperatorIdent.GAPCROSSGP) throw new invalidCrossover("Cruce no valido en FuzzyGAP"); 
        
        GenotypeFuzzyGAP f3=(GenotypeFuzzyGAP)of1;
        GenotypeFuzzyGAP f4=(GenotypeFuzzyGAP)of2;
        f3.set(this);
        f4.set((GenotypeFuzzyGAP)parent);
        
        if (crossoverID==OperatorIdent.GAPCROSSGA) {
            
            
            // Fuzzy partitions are crossed
            double []cont3; double[]cont4; 
            for (int i=0;i<f3.A.length;i++) {
                cont3=f3.A[i].toVector();
                cont4=f4.A[i].toVector();
                
                if (debug) {
                    // Tracing
                    System.out.println("Before crossing:");
                    for (int j=0;j<cont3.length;j++) 
                        System.out.print(cont3[j]+" ");
                    System.out.println();
                    for (int j=0;j<cont4.length;j++) 
                        System.out.print(cont4[j]+" ");
                    System.out.println();
                }
                
                partBInternalCrossover(cont3, cont4);
                
                if (debug) {
                    // Tracing
                    System.out.println("after crossing:");
                    for (int j=0;j<cont3.length;j++) 
                        System.out.print(cont3[j]+" ");
                    System.out.println();
                    for (int j=0;j<cont4.length;j++) 
                        System.out.print(cont4[j]+" ");
                    System.out.println();
                }
                
                // -----------------------------------------------
                f3.A[i] = new FuzzyPartition(cont3);
                f4.A[i] = new FuzzyPartition(cont4);
            }
            // This section of code is suitable for modelling problems. 
            // For classification problems indexes from clases don't evolve.
            cont3=f3.B.toVector();
            cont4=f4.B.toVector();
            
            if (debug) {
                System.out.println("Before crossing:");
                for (int j=0;j<cont3.length;j++) 
                    System.out.print(cont3[j]+" ");
                System.out.println();
                for (int j=0;j<cont4.length;j++) 
                    System.out.print(cont4[j]+" ");
                System.out.println();
            }
            
            if (cont3.length>0) {   
                
                partBInternalCrossover(cont3, cont4);
                f3.B = new FuzzyPartition(cont3);
                f4.B = new FuzzyPartition(cont4);
                
            }	
            
            if (debug) {
                // Tracing
                System.out.println("after crossing:");
                for (int j=0;j<cont3.length;j++) 
                    System.out.print(cont3[j]+" ");
                System.out.println();
                for (int j=0;j<cont4.length;j++) 
                    System.out.print(cont4[j]+" ");
                System.out.println();
            }
        } else {
            
            
            int firstCrossPoint=0;
            Node tmp;
            int lTypeOfNode;
            do {
                firstCrossPoint=(int)(rand.Rand()*(f3.theNodes.size()-1))+1;
                tmp=(Node)(f3.theNodes.elementAt(firstCrossPoint));
                lTypeOfNode=tmp.type();
            } while(lTypeOfNode==Node.NVariable || lTypeOfNode==Node.NConsequent || lTypeOfNode==Node.NRule);
            
            
            GenotypeFuzzyGAP f2=(GenotypeFuzzyGAP)parent;
            
            // It's calcutaded a list with compatible nodes with second tree
            tmp=(Node)(f3.theNodes.elementAt(firstCrossPoint));
            int T1=tmp.type();
            Vector ableToCrossNodes=new Vector();
            
            switch (T1) {
                case Node.NVariable:
                case Node.NLabel:
                case Node.NConsequent:
                case Node.NRule:
                case Node.NRuleBase: {
                    for (int i=0;i<f4.theNodes.size();i++) {
                        tmp=(Node)f4.theNodes.elementAt(i);
                        Integer itmp=(Integer)theDepthAtEachNode.elementAt(firstCrossPoint);
                        if (tmp.type()==T1 && MAXH-itmp.intValue()>=getHeight(tmp)) ableToCrossNodes.addElement(new Integer(i));
                    }
                    break;
                }
                case Node.NAnd:
                case Node.NOr:
                case Node.NEs: {
                    for (int i=0;i<f4.theNodes.size();i++) {
                        tmp=(Node)f4.theNodes.elementAt(i);
                        lTypeOfNode=tmp.type();
                        Integer itmp=(Integer)theDepthAtEachNode.elementAt(firstCrossPoint);
                        if ((lTypeOfNode==Node.NAnd || lTypeOfNode==Node.NOr || lTypeOfNode==Node.NEs ) &&
                            MAXH-itmp.intValue()>=getHeight(tmp)) ableToCrossNodes.addElement(new Integer(i));
                    }
                    break;
                }
            }
            
            
            //Trees can't be crossed (it doesn't be)
            if (ableToCrossNodes.size()!=0) {
                
                //A node from second tree is selected
                int secondCrossPoint=(int)(rand.Rand()*ableToCrossNodes.size());
                
                
                // Selected nodes are swaped
                int nh=-1;
                tmp=(Node)f3.theParents.elementAt(firstCrossPoint);
                Node tmp1=(Node)f3.theNodes.elementAt(firstCrossPoint);
                
                for (int i=0;i<tmp.nChildren();i++) {
                    if (tmp.child(i)==tmp1) {
                        nh=i; break;
                    }
                }
                
                int nh1=-1;
                Integer itmp=(Integer)ableToCrossNodes.elementAt(secondCrossPoint);
                tmp=(Node)f4.theParents.elementAt(itmp.intValue());
                tmp1=(Node)f4.theNodes.elementAt(itmp.intValue());
                for (int i=0;i<tmp.nChildren();i++) {
                    if (tmp.child(i)==tmp1) {
                        nh1=i; break;
                    }
                }            
                
                tmp=(Node)f3.theParents.elementAt(firstCrossPoint);
                itmp=(Integer)ableToCrossNodes.elementAt(secondCrossPoint);
                tmp1=(Node)f4.theParents.elementAt(itmp.intValue());
                Node tmpswap=tmp.child(nh);
                tmp.changeChild(tmp1.child(nh1),nh);
                tmp1.changeChild(tmpswap,nh1);
                
                f3.buildNodeLists();
                f4.buildNodeLists();
                
            }
        }        
    }
    
/**
* <p>
* The method intended to randomly initialize a Genotype and then the corresponding individual.
* </p>
*/
    public void Random() {
        
        rootNode=(NodeRuleBase)(RandomTree(Node.NRuleBase,MAXH,0));
        
	//Fuzzy partitions are filled at random
        double []cont; double[]ind;
        for (int i=0;i<A.length;i++) {
            cont=A[i].toVector();
            ind=new double[cont.length];
            // Positions between 1 and length-2 are filled
            ind[0]=0;
            for (int j=1;j<ind.length;j++) ind[j]=ind[j-1]+rand.Rand();
            for (int j=1;j<ind.length;j++) ind[j]/=ind[ind.length-1];
            double min=cont[0]; double max=cont[cont.length-1]; double factor=(max-min);
            for (int j=0;j<ind.length;j++) cont[j]=ind[j]*factor+min;
            
            A[i]= new FuzzyPartition(cont);
        }
        // This section of code is suitable for modelling problems. 
        // For classification problems indexes from clases don't evolve.
        cont=B.toVector();
        if (cont.length>0) {
            ind=new double[cont.length];
            // Positions between 1 and length-2 are filled
            ind[0]=0;
            for (int j=1;j<ind.length;j++) ind[j]=ind[j-1]+rand.Rand();
            for (int j=1;j<ind.length;j++) ind[j]/=ind[ind.length-1];
            double min=cont[0]; double max=cont[cont.length-1]; double factor=(max-min);
            for (int j=0;j<ind.length;j++) cont[j]=ind[j]*factor+min;
            
            // Consecuente values are filled
            B = new FuzzyPartition(cont);
	}	
        buildNodeLists();
    }
    
/**
* <p>
* This method is intended for printing debug information.
* </p>
*/
    public void debug() {
        rootNode.debug();
    }
    
/**
* <p>
* This method returns the root node.
* </p>
* @return the root {@link Node}
*/
    public Node getRootNode() {
        return rootNode;
    }
    
/**
* <p>
* This method returns the number of consequents.
* </p>
* @return the size of the {@link FuzzyPartition} B
*/
    private int getNumConsequents()  {
        return B.size();
    }
    
/**
* <p>
* This method returns the number of input variables.
* </p>
* @return the size of the array of {@link FuzzyPartition} A
*/
    private int getNumInputs() {
        return A.length;
    }
    
/**
* <p>
* This method returns the corresponding label of an input variable.
* </p>
* @param nv  the int with the index of the desired input variable
* @param nlabel the index of the desired label
* @return the desired {@link Fuzzy} label
*/
    private Fuzzy getLabel(int nv, int nlabel) {
        return A[nv].getComponent(nlabel);
    }
    
/**
* <p>
* This method returns the number of labels for a given input variable.
* </p>
* @param nv  the index of the input variable
* @return the number of labels for the given nv input variable
*/
    int getNumLabels(int nv) {
        return A[nv].size();
    }
    
/**
* <p>
* This method returns the height of the tree from a given node.
* </p>
* @param n the {@link Node} to analyze
* @return an int with the height of the tree
*/
    private int getHeight(Node n) {
        int mh=0;
        for (int i=0;i<n.nChildren();i++) {
            int h=getHeight(n.child(i));
            if (h>mh) mh=h;
        }
        return 1+mh;
    }
    
/**
* <p>
* This method fills recursively all the list of nodes: {@link theNodes}, {@link theParents} 
* and {@link theDepthAtEachNode}.
* </p>
* @param n the {@link Node} to start with
* @param parent the parent node of n
* @param p the depth of node n
*/
    private void insertNode(Node n, Node parent, int p)  {
        theNodes.addElement(n);
        theParents.addElement(parent);
        theDepthAtEachNode.addElement(new Integer(p));
        for (int i=0;i<n.nChildren();i++) insertNode(n.child(i),n,p+1);
    }
    
/**
* <p>
* This method fills all the list of nodes: {@link theNodes}, {@link theParents} 
* and {@link theDepthAtEachNode}.
* Is the function to be called if the previous lists are desired to be updated.
* </p>
*/
    private void buildNodeLists() {
        theNodes=new Vector();
        theParents=new Vector();
        theDepthAtEachNode=new Vector();
        insertNode(rootNode,null,0);
    }
    
    
/**
* <p>
* This method builds a random tree from the scratch. 
* </p>
* @param t the type of node to start with. Valid types are those from {@link Node}:
*          {@link Node.NVariable},  {@link Node.NLabel},  {@link Node.NConsecuente}, 
*          {@link Node.NAnd},  {@link Node.NOr},  {@link Node.NEs},  {@link Node.NRegla}, 
*          {@link Node.NBaseReglas}
* @param maxh the maximum valid height of the tree
* @param par  the number of partitions
*/
    Node RandomTree(int t, int maxh, int par) {
        
        switch(t) {
            case Node.NVariable:
                return new NodeVariable(par);
                
            case Node.NLabel: {
                int nlabel=(int)(rand.Rand()*getNumLabels(par));
                return new NodeLabel(getLabel(par,nlabel));
            }
                
            case Node.NConsequent:
                return new NodeConsequent((int)(par));
                
                
            case Node.NAnd: {
                // Asserts are 'AND', 'OR' and 'ES'
                int[] tr = new int[2];
                for (int i=0;i<2;i++) {
                    if (maxh<=2) tr[i]=Node.NEs;
                    else {
                        int typeOfAssert=(int)(rand.Rand()*3);
                        if (typeOfAssert==0) tr[i]=Node.NAnd;
                        else if (typeOfAssert==1) tr[i]=Node.NOr;
                        else if (typeOfAssert==2) tr[i]=Node.NEs;
                    }
                }
                NodeAssert na1=(NodeAssert)(RandomTree(tr[0],maxh-1,0));
                NodeAssert na2=(NodeAssert)(RandomTree(tr[1],maxh-1,0));
                NodeAnd result=new NodeAnd(na1, na2);
                return result;
            }
                
                
            case Node.NOr:{
                // Asserts are 'AND', 'OR' and  'ES'
                int[] tr = new int[2];
                for (int i=0;i<2;i++) {
                    if (maxh<=2) tr[i]=Node.NEs;
                    else {
                        int typeOfAssert=(int)(rand.Rand()*3);
                        if (typeOfAssert==0) tr[i]=Node.NAnd;
                        else if (typeOfAssert==1) tr[i]=Node.NOr;
                        else if (typeOfAssert==2) tr[i]=Node.NEs;
                    }
                }
                NodeAssert na1=(NodeAssert)(RandomTree(tr[0],maxh-1,0));
                NodeAssert na2=(NodeAssert)(RandomTree(tr[1],maxh-1,0));
                NodeOr result=new NodeOr(na1, na2);
                return result;
            }
                
                
            case Node.NEs:{
                int nv=(int)(rand.Rand()*getNumInputs());
                NodeVariable nvar=(NodeVariable)(RandomTree(Node.NVariable,maxh-1,nv));
                NodeLabel nval=(NodeLabel)(RandomTree(Node.NLabel,maxh-1,nv));
                NodeIs result=new NodeIs(nvar,nval);
                return result;
            }
                
                
            case Node.NRule: {
                // Asserts are 'AND', 'OR' and 'ES'
                int typeOfAssert=(int)(rand.Rand()*3);
                int tr=Node.NAnd;
                if (typeOfAssert==0) tr=Node.NAnd;
                else if (typeOfAssert==1) tr=Node.NOr;
                else if (typeOfAssert==2) tr=Node.NEs;
                NodeAssert na=(NodeAssert)(RandomTree(tr,maxh-1,0));
                // Consecuents are 0..numberOfConsequents()
                int ncons=(int)(rand.Rand()*getNumConsequents());
                NodeConsequent nc=(NodeConsequent)(RandomTree(Node.NConsequent,maxh-1,par));

                NodeRule result=new NodeRule(na,nc,1);
                return result;
            }
                
            case Node.NRuleBase: {
                NodeRule[] proot=new NodeRule[getNumConsequents()];
                for (int i=0;i<proot.length;i++) {
                    // The third parameter is the consecuent
                    proot[i]=(NodeRule)(RandomTree(Node.NRule,maxh-1,i));
                }
                NodeRuleBase result=new NodeRuleBase(proot);
                return result;
            }
                
        }
        
        return new NodeVariable(par);
    }

    
    
}

