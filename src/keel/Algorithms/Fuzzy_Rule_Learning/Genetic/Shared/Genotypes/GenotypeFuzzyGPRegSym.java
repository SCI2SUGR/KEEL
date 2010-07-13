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

// If you like add a new node, you must modidy RandomTree, mutacion, cruce functions


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Genotypes;
import java.util.Vector;
import org.core.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public class GenotypeFuzzyGPRegSym extends Genotype {
/** 
* <p> 
* GenotypeFuzzyGPRegSym is the base clase to represent the genotype when a fuzzy
* model symbolic regression is to be learned with the genetic programming (GP).
* Both the constants and the tree node are learned.
* 
* The Fuzzy model variables are characterized with {@link FuzzyPartition}s.
* The root node is a {@link NodeExprHold}.
* </p> 
*/ 
    // Sintactical tree is representation of an individual,
    // so the fenotype is the same as genotype    
    // Maximum height of the generated trees
    static int MAXH;             // Maximum height for trees
    static int LENCADENA;        // Number of parameters in GAP
    //The root node
    Node rootNode;                      // Pointer to tree root
	//The list of nodes
    Vector theNodes;                      // Vector of Pointers to each node
	//The list of parents of each node
    Vector theParents;                      // Vector of Pointers to parents of each
	//The number of nodes from the current until the node.
    Vector theDepthAtEachNode;             // Number of nodes till root. height.
	//The maximum value a constant can vary
    static double KMAX;
	//The minimum value a constant can vary
    static double KMIN;
	//The number of input variables
    static int nInputs;
	//The type of value used: crisp, interval or fuzzy values
    static int typeOfValue;
    
    
    // GAP implementation includes a vector of fuzzysets with each tree of the population
    FuzzyAlphaCut[] fsChain;
    
 /**
* <p>
* Class constructor with the following parameters:
* </p>
* @param kmin  the minimum double value for each constant
* @param kmax  the maximum double value for each constant
* @param pNInput the number of input variables
* @param cteType the type of value used:  {@link FuzzyRegressor.Crisp} for crisp values, 
*                {@link FuzzyRegressor.Interval} for interval values and 
*				 {@link FuzzyRegressor.Fuzzy} for fuzzy values
* @param vChainLength the length of the {@link FuzzyAlphaCut}array
* @param mh the maximum valid tree height
* @param r the {@link Randomize} object
*/
    public GenotypeFuzzyGPRegSym(double kmin, double kmax, int pNInputs, int cteType, int vChainLength, int mh, Randomize r) {
        super(r);
        theNodes=new Vector(); theParents=new Vector(); theDepthAtEachNode=new Vector();
        rootNode=new NodeExprHold(new NodeExprArit[0]);
        KMIN=kmin; KMAX=kmax; nInputs=pNInputs; typeOfValue=cteType; LENCADENA=vChainLength; MAXH=mh;

        fsChain=new FuzzyAlphaCut[LENCADENA];
        RandomCadena();

    }
    
/**
* <p>
* The copy constructor.
* </p>
* @param p the {@link GenotypeFuzzyGPRegSym} to be copied
*/
    public GenotypeFuzzyGPRegSym(GenotypeFuzzyGPRegSym p) {
        super(p.rand);
        rootNode=p.rootNode.clone();
        fsChain=new FuzzyAlphaCut[LENCADENA];
        for (int i=0;i<p.fsChain.length;i++) fsChain[i]=(FuzzyAlphaCut)p.fsChain[i].clone();
        buildNodeLists();
    }
    
/**
* <p>
* This method copies the given parameter into the current object.
* </p>
* @param p the {@link GenotypeFuzzyGPRegSym} to be copied
*/
    public void setTree(GenotypeFuzzyGPRegSym p) {
        rootNode=p.rootNode.clone();
        fsChain=new FuzzyAlphaCut[LENCADENA];
        for (int i=0;i<p.fsChain.length;i++) fsChain[i]=(FuzzyAlphaCut)p.fsChain[i].clone();
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
        
        if (!(g instanceof GenotypeFuzzyGPRegSym)) return false;
        
        GenotypeFuzzyGPRegSym p=(GenotypeFuzzyGPRegSym)g;        
        return rootNode.compatible(p.rootNode);
    }
    
    
/**
* <p>
* This method is intended for generating a perfect copy of the current Genotype.
* </p>
* @return the newly created {@link Genotype} which is a perfect copy of current individual
*/
    public Genotype clone() {
        return new GenotypeFuzzyGPRegSym(this);
    }
    
/**
* <p>
* This method is intended to rebuild the current object if needed.
* In this case, nothing has to be done.
* </p>
* @param g the {@link Genotype} to rebuild.
*/
    public void rebuild(Genotype g) {};
    
    public int getNumInputs() {
        return nInputs;
    }
    
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
    public void mutation(double AMPL, int mutationID) throws invalidMutation {
      // Part of one sub-tree is replaced with other one generated at random
      // avoiding raise the maximum allowed heigth
        		
        if (mutationID!=OperatorIdent.GAPMUTAGA && mutationID!=OperatorIdent.GAPMUTAGP) throw new invalidMutation(); 
      
		
        if (mutationID==OperatorIdent.GAPMUTAGA) {
            
            GenotypeFuzzyGPRegSym randomGenotype=new GenotypeFuzzyGPRegSym(KMIN,KMAX,nInputs,typeOfValue,LENCADENA,MAXH,rand);
            randomGenotype.RandomCadena();
			double alpha=AMPL*(-0.1+0.6*rand.Rand());		
			for (int i=0;i<fsChain.length;i++) {
				fsChain[i].linearComb(fsChain[i],randomGenotype.fsChain[i],alpha);
			}
			
        } else {
            
            int mutationPoint=0;
            Node tmp;
            int typeOfValue;
            mutationPoint=(int)(rand.Rand()*(theNodes.size()-1))+1;
            tmp=(Node)(theNodes.elementAt(mutationPoint));
            typeOfValue=tmp.type();
            
            int nh=-1;
            tmp=(Node)(theParents.elementAt(mutationPoint));
            for (int i=0;i<tmp.nChildren();i++) {
                Node tmp1=(Node)(theParents.elementAt(mutationPoint));
                Node tmp2=(Node)(theNodes.elementAt(mutationPoint));
                if (tmp1.child(i)==tmp2) { nh=i; break; }
            }
            
            tmp=(Node)(theNodes.elementAt(mutationPoint));
            int T=tmp.type(), TN=Node.NValue;
            
            int val=(int)(rand.Rand()*8);
            if (val==0) TN=Node.NVariable;
            else if (val==1) TN=Node.NValue;
            else if (val==2) TN=Node.NSum;
            else if (val==3) TN=Node.NMinus;
            else if (val==4) TN=Node.NProduct;
            else if (val==5) TN=Node.NSquareRoot;
            else if (val==6) TN=Node.NLog;
            else if (val==7) TN=Node.NExp;
            
            int par=(int)(rand.Rand()*(getNumInputs()));
            
            tmp=(Node)theParents.elementAt(mutationPoint);
            Integer itmp=(Integer)theDepthAtEachNode.elementAt(mutationPoint);
            tmp.changeChild(RandomTree(TN,MAXH-itmp.intValue(),par),nh);
            buildNodeLists();
        }
        
    }
    
/**
* <p>
* The method for carrying out the crossover genetic operations.
* </p>
* @param p2 the second parent in the crossover operation, it's an {@link Genotype} object
* @param p3 the {@link Genotype} object with the first offspring
* @param p4 the {@link Genotype} object with the second offspring
* @param crossoverID an int with the crossover operation to be carried out:
*                    {@link OperatorIdent.GAPCRUCEGA} for genetic algorithm crossover
*                    {@link OperatorIdent.GAPCRUCEGP} for genetic programming crossover
* @throws {@link invalidCrossover} if crossoverID is not valid
*/
    public void crossover(Genotype p2, Genotype p3, Genotype p4, int crossoverID) throws invalidCrossover {
       // Two sub-trees are swaped, avoiding raise the maximum allowed heigth
       // A node from the first sub-tree is selected.
       // When a node 'variable', 'consecuente' or 'regla' is mutated
       // the tree must be repared
        if (crossoverID!=OperatorIdent.GAPCROSSGA && crossoverID!=OperatorIdent.GAPCROSSGP) throw new invalidCrossover(); 
        
		
        GenotypeFuzzyGPRegSym f2=(GenotypeFuzzyGPRegSym)p2;
        GenotypeFuzzyGPRegSym f3=(GenotypeFuzzyGPRegSym)p3;
        GenotypeFuzzyGPRegSym f4=(GenotypeFuzzyGPRegSym)p4;
        f3.setTree(this);
        f4.setTree(f2);
        
        
        if (crossoverID==OperatorIdent.GAPCROSSGA) {
            
           double alpha=-0.1+0.6*rand.Rand();		
           for (int i=0;i<fsChain.length;i++) {
			   // System.out.println("Cruce: "+fsChain[i].aString()+" con "+f2.fsChain[i].aString());
               f3.fsChain[i].linearComb(fsChain[i],f2.fsChain[i],alpha);
			   f4.fsChain[i].linearComb(fsChain[i],f2.fsChain[i],1-alpha);
           }
            
        } else {
               
            int firstCrossPoint=0;
            Node tmp;
            int typeOfValue;
            firstCrossPoint=(int)(rand.Rand()*(f3.theNodes.size()-1))+1;
            tmp=(Node)(f3.theNodes.elementAt(firstCrossPoint));
            typeOfValue=tmp.type();
            
            
            tmp=(Node)(f3.theNodes.elementAt(firstCrossPoint));
            int T1=tmp.type();
            Vector ableToCrossNodes=new Vector();
            
            
            for (int i=0;i<f4.theNodes.size();i++) {
                tmp=(Node)f4.theNodes.elementAt(i);
                typeOfValue=tmp.type();
                Integer itmp=(Integer)theDepthAtEachNode.elementAt(firstCrossPoint);
                if ((typeOfValue==Node.NVariable || 
                     typeOfValue==Node.NValue || 
                     typeOfValue==Node.NSum ||
                     typeOfValue==Node.NMinus ||
                     typeOfValue==Node.NProduct ||
                     typeOfValue==Node.NSquareRoot ||
                     typeOfValue==Node.NLog ||
                     typeOfValue==Node.NExp) &&
                    MAXH-itmp.intValue()>=getHeight(tmp)) 
                    ableToCrossNodes.addElement(new Integer(i));
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
                
            }
            
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
        
        rootNode=(NodeExprHold)(RandomTree(Node.NExprHold,MAXH,0));
        RandomCadena();
        buildNodeLists();
        
    }
    
/**
* <p>
* This method is intended for printing debug information.
* </p>
*/
    public void debug() {
        System.out.println("Cadena=[");
        for (int i=0;i<fsChain.length;i++) System.out.println(fsChain[i].aString()+" ");
        System.out.println("]");
        rootNode.debug();
        System.out.println();
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
        
        //  pointers are tranformed to string using NodeValue
        if (n instanceof NodeValue) {
            NodeValue nv=(NodeValue)n;
            nv.setString(fsChain);
        }
        for (int i=0;i<n.nChildren();i++) {
            insertNode(n.child(i),n,p+1);
        }
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
* This method fills the {@link fsChain} with valid values shich types are: {@link FuzzyRegressor.Crisp},
* {@link FuzzyRegressor.Interval} and {@link FuzzyRegressor.Fuzzy}.
* </p>
*/
	void RandomCadena() {
		
        // The chain of constants is randomly initialized
        double iz=0, ce=0, de=0;
				
        for (int i=0;i<fsChain.length;i++) {
            switch (typeOfValue) {
                case FuzzyRegressor.Crisp:
                    iz=rand.Rand()*(KMAX-KMIN)+KMIN;
                    FuzzySingleton bs=new FuzzySingleton(iz); 
                    fsChain[i]=new FuzzyAlphaCut(bs);
                    break;
                    
                case FuzzyRegressor.Interval:
                    iz=rand.Rand()*(KMAX-KMIN)+KMIN;
                    FuzzyInterval bi=new FuzzyInterval(iz,iz); 
                    fsChain[i]=new FuzzyAlphaCut(bi);
                    break;
                    
                case FuzzyRegressor.Fuzzy:
					iz=rand.Rand()*(KMAX-KMIN)+KMIN;
                    FuzzyNumberTRIANG bt=new FuzzyNumberTRIANG(iz,iz,iz); 
                    fsChain[i]=new FuzzyAlphaCut(bt);
                    break;
            }
        }
    }
    
/**
* <p>
* This method builds a random tree from the scratch. 
* </p>
* @param t the type of node to start with. Valid types are those from {@link Node}:
*          {@link Node.NVariable},  {@link Node.NExprHold},  {@link Node.NValor}, 
*          {@link Node.NSuma},  {@link Node.NResta},  {@link Node.NProducto},
*          {@link Node.NRaizCuadrada},  {@link Node.NExp},  {@link Node.NLog},
* @param maxh the maximum valid height of the tree
* @param par  the number of partitions
*/
    Node RandomTree(int t, int maxh, int par) {
        
        int opciones=8;
        switch(t) {
            case Node.NExprHold:
                NodeExprArit[] h=new NodeExprArit[1];
                for (int i=0;i<h.length;i++) {
                    par=(int)(rand.Rand()*(getNumInputs()));
                    int val=(int)(rand.Rand()*(opciones));
                    switch (val) {
                        case 0: h[i]=(NodeExprArit)(RandomTree(Node.NVariable,maxh-1,par)); break;
                        case 1: h[i]=(NodeExprArit)(RandomTree(Node.NValue,maxh-1,par)); break;
                        case 2: h[i]=(NodeExprArit)(RandomTree(Node.NSum,maxh-1,par)); break;
                        case 3: h[i]=(NodeExprArit)(RandomTree(Node.NMinus,maxh-1,par)); break;
                        case 4: h[i]=(NodeExprArit)(RandomTree(Node.NProduct,maxh-1,par)); break;
                        case 5: h[i]=(NodeExprArit)(RandomTree(Node.NSquareRoot,maxh-1,par)); break;
                        case 6: h[i]=(NodeExprArit)(RandomTree(Node.NExp,maxh-1,par)); break;
                        case 7: h[i]=(NodeExprArit)(RandomTree(Node.NLog,maxh-1,par)); break;
                    }
                    
                }
                return new NodeExprHold(h);
                
            case Node.NVariable:
                return new NodeVariable(par);
                
            case Node.NValue: {
                // NodeValue constains index referecing a fuzzyset in GAP String
                int indice=(int)(rand.Rand()*(LENCADENA));
                return new NodeValue(indice,fsChain);
            }
                
            case Node.NSum:
            case Node.NMinus:
            case Node.NProduct:
            case Node.NSquareRoot:
            case Node.NExp:
            case Node.NLog:
            {
                
                if (maxh<=1) opciones=2; else opciones=8;
                int[] tr = new int[2];
                for (int i=0;i<2;i++) {
                    
                    int val=(int)(rand.Rand()*(opciones));
                    switch (val) {
                        case 0: tr[i]=Node.NVariable; break;
                        case 1: tr[i]=Node.NValue; break;
                        case 2: tr[i]=Node.NSum; break;
                        case 3: tr[i]=Node.NMinus; break;
                        case 4: tr[i]=Node.NProduct; break;
                        case 5: tr[i]=Node.NSquareRoot; break;
                        case 6: tr[i]=Node.NExp; break;
                        case 7: tr[i]=Node.NLog; break;
                    }
                
                    par=(int)(rand.Rand()*(getNumInputs()));
                    NodeExprArit na1=(NodeExprArit)(RandomTree(tr[0],maxh-1,par));
                    par=(int)(rand.Rand()*(getNumInputs()));
                    NodeExprArit na2=(NodeExprArit)(RandomTree(tr[1],maxh-1,par));
                    switch(t) {
                        case Node.NSum: return new NodeAdd(na1, na2);
                        case Node.NMinus: return new NodeMinus(na1, na2);
                        case Node.NProduct: return new NodeProduct(na1, na2);
                        case Node.NSquareRoot: return new NodeSquareRoot(na1);
                        case Node.NExp: return new NodeExp(na1);
                        case Node.NLog: return new NodeLog(na1);
                    }
                }
                
            }
        }
        
        System.out.println("Depura esto");

        return new NodeVariable(par);
    }
    
/**
* <p>
* This method returns the centre of weights of the vector {@link fsChain}.
* </p>
* @return the double value array
*/
    public double[] getChainValue() {
        double[] result=new double[fsChain.length];
        for (int i=0;i<result.length;i++) result[i]=fsChain[i].massCentre();
        return result;
        
    }
    
    
/**
* <p>
* This method updates the list of used constants.
* </p>
* @param n the {@link Node} to analyze
* @param used a boolean array with tre/false for each constants
*/
    void setUsedConstants(Node n, boolean[] used) {
     
        if (n instanceof NodeValue) {
            NodeValue nv=(NodeValue)n;   
            used[nv.getIndex()]=true;
        } else {
            for (int i=0;i<n.children().length;i++)
                setUsedConstants(n.children()[i],used);
        }
    }
    
/**
* <p>
* This method returns the list of used constants.
* </p>
* @return  a boolean array with tre/false for each constants
*/
    public boolean[] getUsedConstants() {
        boolean[] result=new boolean[fsChain.length];
        for (int i=0;i<rootNode.children().length;i++) setUsedConstants(rootNode.children()[i],result);
        return result;
    }
    
/**
* <p>
* This method sets the fsChain with the given array value
* </p>
* @param x the new double array for the fsChain
*/
    public void setChain(double []x) {
        
        // Only centers are moved and amplitudes are keeped
        double []centros=getChainValue();
        for (int i=0;i<centros.length;i++) {
            FuzzySingleton s = new FuzzySingleton(x[i]-centros[i]);
            FuzzyAlphaCut disp = new FuzzyAlphaCut(s);
            fsChain[i].set(fsChain[i].sum(disp));
        }
    }
    
    
}

    

