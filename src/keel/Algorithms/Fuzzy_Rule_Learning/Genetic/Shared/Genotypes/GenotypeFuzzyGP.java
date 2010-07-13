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

// 1.- This representation doesn't evolve fuzzy partitions
// 2.- Each weights rules is '1'

// If you like add a new node, you must modidy RandomTree, mutacion, cruce functions

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes;
import java.util.Vector;
import org.core.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public class GenotypeFuzzyGP extends Genotype {
/** 
* <p> 
* GenotypeFuzzyGP is the base clase to represent the genotype when a fuzzy
* model is to be learned with the genetic programming (GP).
* Both the constants and the tree node are learned.
* 
* The Fuzzy model variables are characterized with {@link FuzzyPartition}s.
* The root node is a {@link NodeRuleBase}.
* </p> 
*/ 
    // Sintactical tree is representation of an individual,
    // so the fenotype is the same as genotype
    // Maximum height of the generated trees
    static int MAXH;                   // Maximum height for trees
    //The root node
    Node rootNode;                         // Pointer to tree root.
	//The list of nodes
    Vector theNodes;                         // Vector of Pointers to each node
	//The list of parents of each node
    Vector theParents;                         // Vector of Pointers to parents of each node
	//The number of nodes from the current until the node.
    Vector theDepthAtEachNode;                // Number of nodes till root. height.
    //The input variables partitions
    static FuzzyPartition[] A;
	//The output variable partitions
    static FuzzyPartition B;

 /**
* <p>
* Class constructor with the following parameters:
* </p>
* @param a the {@link FuzzyPartition} array, a {@link FuzzyPartition} for each input variable
* @param b the {@link FuzzyPartition} for the output variable
* @param mh the maximum valid height of a tree 
* @param r the {@link Randomize} object
*/
    public GenotypeFuzzyGP(FuzzyPartition[] a, FuzzyPartition b, int mh, Randomize r) {
        super(r);
        theNodes=new Vector(); theParents=new Vector(); theDepthAtEachNode=new Vector();
        rootNode=new NodeRuleBase(new NodeRule[0]);
        A=a; B=b;
        MAXH=mh;
    }

/**
* <p>
* The copy constructor.
* </p>
* @param p the {@link GenotypeFuzzyGP} to be copied
*/
    public GenotypeFuzzyGP(GenotypeFuzzyGP p) {
        super(p.rand);
        rootNode=p.rootNode.clone();
        buildNodeLists();
    }

/**
* <p>
* This method copies the given parameter into the current object.
* </p>
* @param p the {@link GenotypeFuzzyGP} to be copied
*/
    public void setTree(GenotypeFuzzyGP p) {
        rootNode=p.rootNode.clone();
        buildNodeLists();
    }
    

/**
* <p>
* This method is intended for generating a perfect copy of the current Genotype.
* </p>
* @return the newly created {@link Genotype} which is a perfect copy of current individual
*/
    public Genotype clone() {
        return  new GenotypeFuzzyGP(this);
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
      GenotypeFuzzyGP p=(GenotypeFuzzyGP)g;        
      return rootNode.compatible(p.rootNode);
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
*                   {@link OperatorIdent.MUTACIONGENERICA} for the genetic algorithm mutation
* @throws {@link invalidMutation} if mutationID is not valid
*/
    public void mutation(double alpha, int mutationID) throws invalidMutation {

        // Part of one sub-tree is replaced with other one generated at random
        // avoiding raise the maximum allowed heigth
        // Amplitude parameter is no used.
        // When a node 'variable', 'consecuente' or 'regla' is mutated
        // the tree must be repared
	if (mutationID!=OperatorIdent.GENERICMUTATION) throw new invalidMutation();
	
        int mutationPoint=0;
        Node tmp;
        int typeOfValue;
        do {
            mutationPoint=(int)(rand.Rand()*(theNodes.size()-1))+1;
            tmp=(Node)(theNodes.elementAt(mutationPoint));
            typeOfValue=tmp.type();
        } while(typeOfValue==Node.NVariable || typeOfValue==Node.NConsequent || typeOfValue==Node.NRule);


        int nh=-1;
        tmp=(Node)(theParents.elementAt(mutationPoint));
        for (int i=0;i<tmp.nChildren();i++) {
            Node tmp1=(Node)(theParents.elementAt(mutationPoint));
            Node tmp2=(Node)(theNodes.elementAt(mutationPoint));
            if (tmp1.child(i)==tmp2) { nh=i; break; }
        }

        tmp=(Node)(theNodes.elementAt(mutationPoint));
        int T=tmp.type(),TN=Node.NLabel;
        int par=0,val=0;
        switch (T) {
            case Node.NVariable: {
                NodeVariable tmp1=(NodeVariable)(theNodes.elementAt(mutationPoint));
                TN=Node.NVariable;
                par=tmp1.getN();
                break;
            }
            case Node.NLabel: {
                Node tmpr=(Node)theParents.elementAt(mutationPoint);
                NodeVariable tmp1=(NodeVariable)tmpr.child(0);
                TN=Node.NLabel;
                par=tmp1.getN();
                break;
            }
            case Node.NConsequent:
                TN=Node.NConsequent;
                break;
            case Node.NAnd:
            case Node.NOr:
            case Node.NEs:
                val=(int)(rand.Rand()*3);
                if (val==0) TN=Node.NAnd;
                else if (val==1) TN=Node.NOr;
                else if (val==2) TN=Node.NEs;
                break;
            case Node.NRule:
                TN=Node.NRule;
                break;
            case Node.NRuleBase:
                TN=Node.NRuleBase;
                break;
        }

        tmp=(Node)theParents.elementAt(mutationPoint);
        Integer itmp=(Integer)theDepthAtEachNode.elementAt(mutationPoint);
        tmp.changeChild(RandomTree(TN,MAXH-itmp.intValue(),par),nh);
        buildNodeLists();

    }

/**
* <p>
* The method for carrying out the crossover genetic operations.
* </p>
* @param p2 the second parent in the crossover operation, it's an {@link Genotype} object
* @param p3 the {@link Genotype} object with the first offspring
* @param p4 the {@link Genotype} object with the second offspring
* @param crossoverID an int with the crossover operation to be carried out:
*                    {@link OperatorIdent.CRUCEGENERICO} for genetic algorithm crossover
* @throws {@link invalidCrossover} if crossoverID is not valid
*/
    public void crossover(Genotype p2, Genotype p3, Genotype p4, int crossoverID) throws invalidCrossover {
       // Two sub-trees are swaped, avoiding raise the maximum allowed heigth
       // A node from the first sub-tree is selected.
       // When a node 'variable', 'consecuente' or 'regla' is mutated
       // the tree must be repared
                         

        if (crossoverID!=OperatorIdent.GENERICROSSOVER) throw new invalidCrossover();

        GenotypeFuzzyGP f3=(GenotypeFuzzyGP)p3;
        GenotypeFuzzyGP f4=(GenotypeFuzzyGP)p4;
        f3.setTree(this);
        f4.setTree((GenotypeFuzzyGP)p2);

        int firstCrossPoint=0;
        Node tmp;
        int typeOfValue;
        do {
            firstCrossPoint=(int)(rand.Rand()*(f3.theNodes.size()-1))+1;
            tmp=(Node)(f3.theNodes.elementAt(firstCrossPoint));
            typeOfValue=tmp.type();
        } while(typeOfValue==Node.NVariable || typeOfValue==Node.NConsequent || typeOfValue==Node.NRule);


        GenotypeFuzzyGP f2=(GenotypeFuzzyGP)p2;
                
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
                    typeOfValue=tmp.type();
                    Integer itmp=(Integer)theDepthAtEachNode.elementAt(firstCrossPoint);
                    if ((typeOfValue==Node.NAnd || typeOfValue==Node.NOr || typeOfValue==Node.NEs ) &&
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

/**
* <p>
* The method intended to randomly initialize a Genotype and then the corresponding individual.
* </p>
*/
    public void Random() {

        rootNode=(NodeRuleBase)(RandomTree(Node.NRuleBase,MAXH,0));
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
                // Asserts are  'AND', 'OR' and 'ES'
                int[] tr = new int[2];
                for (int i=0;i<2;i++) {
                    if (maxh<=2) tr[i]=Node.NEs;
                    else {
                        int tipoaserto=(int)(rand.Rand()*3);
                        if (tipoaserto==0) tr[i]=Node.NAnd;
                        else if (tipoaserto==1) tr[i]=Node.NOr;
                        else if (tipoaserto==2) tr[i]=Node.NEs;
                    }
                }
                NodeAssert na1=(NodeAssert)(RandomTree(tr[0],maxh-1,0));
                NodeAssert na2=(NodeAssert)(RandomTree(tr[1],maxh-1,0));
                NodeAnd result=new NodeAnd(na1, na2);
                return result;
            }


            case Node.NOr:{
                // Asserts are 'AND', 'OR' and 'ES'
                int[] tr = new int[2];
                for (int i=0;i<2;i++) {
                    if (maxh<=2) tr[i]=Node.NEs;
                    else {
                        int tipoaserto=(int)(rand.Rand()*3);
                        if (tipoaserto==0) tr[i]=Node.NAnd;
                        else if (tipoaserto==1) tr[i]=Node.NOr;
                        else if (tipoaserto==2) tr[i]=Node.NEs;
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
                int tipoaserto=(int)(rand.Rand()*3);
                int tr=Node.NAnd;
                if (tipoaserto==0) tr=Node.NAnd;
                else if (tipoaserto==1) tr=Node.NOr;
                else if (tipoaserto==2) tr=Node.NEs;
                NodeAssert na=(NodeAssert)(RandomTree(tr,maxh-1,0));
                //  Consecuents are 0..numberOfConsequents()
                int ncons=(int)(rand.Rand()*getNumConsequents());
                NodeConsequent nc=(NodeConsequent)(RandomTree(Node.NConsequent,maxh-1,par));
                NodeRule result=new NodeRule(na,nc,1);
                return result;
            }

            case Node.NRuleBase: {
               NodeRule[] praiz=new NodeRule[getNumConsequents()];
                for (int i=0;i<praiz.length;i++) {
                    // The third parameter is the consecuent
                    praiz[i]=(NodeRule)(RandomTree(Node.NRule,maxh-1,i));
                }
                NodeRuleBase result=new NodeRuleBase(praiz);
                return result;
            }
            
        }

        return new NodeVariable(par);
    }
    
    
    
}

