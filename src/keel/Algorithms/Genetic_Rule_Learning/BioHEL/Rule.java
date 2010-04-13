package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: Rule</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import org.core.Randomize;
import java.util.Vector;


public class Rule{
	
    private int ruleSize;                   //number of attributes
    private Vector<Integer> selectedAtts;   //selected attributes
    private Vector<Double> limits;          //low and upper limits of attributes
    private int clase;						//
    public double fitness;
    private int NumAttributes;				//number of attributes of train set
    public double TP,TN,FP,FN;
	public double exceptionsLength;			//For MDL fitness function
	int numInstancesMatched;
	int numInstancesCorrectlyCovered;
	int numInstancesWithSameClass;


    //static parameters
    static private int expectedRuleSize;
    static private int NumInstances;
    static private double coverageBreakpoint;
    static private double coverageRatio;
    static private double generalizeProbability;
    static private double specializeProbability;
    static private int defaultClass;
    
    
    static void setParameters(int expectedRS, double covBrkPoint, double covRat, double gp, double sp, int dclass){
        expectedRuleSize=expectedRS;
        NumInstances=BioHEL.train.getnData();
        coverageBreakpoint=covBrkPoint;
        coverageRatio=covRat;
        generalizeProbability=gp;
        specializeProbability=sp;
        defaultClass=dclass;
    }
    
    
    //*********************************************************************
    //***************** Constructor ***************************************
    //*********************************************************************
    
    public Rule(){    
        NumAttributes=BioHEL.train.getnInputs();
        selectedAtts=new Vector<Integer>(NumAttributes);
        limits=new Vector<Double>(2*NumAttributes);
        fitness=1000000000;
    }
    
    
    //*********************************************************************
    //***************** Clonation of a rule *******************************
    //*********************************************************************
    
    public Rule cloneRule(){
    	
        Rule newSon=new Rule();
        newSon.clase=clase;
        newSon.ruleSize=ruleSize;
            
        for(int i=0 ; i<ruleSize ; ++i){
            newSon.selectedAtts.add(selectedAtts.get(i));
            newSon.limits.add(getLowLimit(i));
            newSon.limits.add(getUpperLimit(i));                
        }
        
        newSon.fitness=fitness;
        newSon.NumAttributes=NumAttributes;
        newSon.TP=TP;
        newSon.TN=TN;
        newSon.FP=FP;
        newSon.FN=FN;
        newSon.exceptionsLength=exceptionsLength;
        newSon.numInstancesMatched=numInstancesMatched;
        newSon.numInstancesCorrectlyCovered=numInstancesCorrectlyCovered;
        newSon.numInstancesWithSameClass=numInstancesWithSameClass;
        
        return newSon;
    }
    

    //*********************************************************************
    //***************** Creation of a rule ********************************
    //*********************************************************************
    
    public void createRule(int pos){
        
        //coger una instancia no removida
        double instance[]=BioHEL.train.getExample(pos);
        
        //elegir la clase de la instancia->no sea defaultClass
        clase=BioHEL.train.getOutputAsInteger(pos);
        int newClass=0;
        while(clase==defaultClass || (BioHEL.train.getInstancesPerClass()[clase]==0)){
	    	newClass = Randomize.Randint(0, BioHEL.train.getnClasses());
	        clase=newClass;
        }
        
        // 1) attribute selection
    	double probIrr= 1-(double)expectedRuleSize/(double)NumAttributes;
        ruleSize=0;

        for(int i=0 ; i<NumAttributes ; ++i){
            if(Randomize.RandClosed()>=probIrr){     //attribute accepted
                selectedAtts.add(i);
                ruleSize++;
            }
        }
        
        // 2) attribute's limits
        double max,min,sizeD,minD,maxD,size;
        double emax[]=BioHEL.train.getemax();
        double emin[]=BioHEL.train.getemin();
        
        for(int i=0 ; i<ruleSize ; i++){    
        	int att=selectedAtts.get(i);
            minD=emin[att];
            maxD=emax[att];
            sizeD=maxD-minD;
            size=(Randomize.RandClosed()*0.5+0.25)*sizeD;

            //limits contruction
            double val=instance[att];
            min=val-size/2.0;
            max=val+size/2.0;
            if(min<minD) {
                max+=(minD-min);
                min=minD;
            }
            if(max>maxD) {
                min-=(max-maxD);
                max=maxD;
            }
            
            limits.add(min);
            limits.add(max);
        }    
    }
    
    
    //*********************************************************************
    //***************** Fitness Function **********************************
    //*********************************************************************
        
    public void fitnessComputation(MDL mdl){
    	
        fitness=mdl.mdlFitness(this);
    }

	public int getNumInstancesWithSameClass(){
		return numInstancesWithSameClass;
	}
	
	public double getRecall(){
		return (double)numInstancesCorrectlyCovered/(double)numInstancesWithSameClass;
	}
    
    public double computeTheoryLength(){

    	int i;
    	double theoryLength = 0.0;
    	
        double[] min=BioHEL.train.getemin();
        double[] max=BioHEL.train.getemax();

    	for(i=0 ; i<ruleSize ; ++i){
    		
    		int att=selectedAtts.get(i);
    		double size=max[att]-min[att];
    		
    		if(size>0){
				double ruleRange=getUpperLimit(i)-getLowLimit(i);
    			theoryLength += 1.0 - (ruleRange/size);
    		}    		
    	}
    	
    	theoryLength/=NumAttributes;

    	return theoryLength;
    }
    
	public double getAccuracy1(){
		
		return (double)numInstancesCorrectlyCovered/(double)BioHEL.train.instancesNotRemoved();
	}
    
	public double getAccuracy2(){

		if(numInstancesMatched==0)
			return 0;
		
		return (double)numInstancesCorrectlyCovered/(double)numInstancesMatched;
	}
	
	 public void RuleParameters(){
	        
	        TP=0;TN=0;FP=0;FN=0;
	        numInstancesMatched=0;
	        numInstancesCorrectlyCovered=0;
	        numInstancesWithSameClass=0;
		  
	        for(int i=0 ; i<NumInstances ; ++i){
	            
	            if(!BioHEL.train.getRemoved(i)){

	                //veo si coincide el antecedente
	                if(CoverInstance(BioHEL.train.getExample(i)) &&
	                		(	(BioHEL.train.Subset[i] == GA.ilas.getStratum()) || 
	                			(BioHEL.train.Subset[i] == -1) )
	                ){
	                	
	                	numInstancesMatched++;
	                	

	                    if(BioHEL.train.getOutputAsInteger(i)==clase){
	                    	TP++;
	                    	numInstancesCorrectlyCovered++;
	                    	numInstancesWithSameClass++;	
	                    }
	                            
	                     else
	                        FP++;
	                }


	                else{
	                    if(BioHEL.train.getOutputAsInteger(i)==clase){
	                        FN++;
	                    	numInstancesWithSameClass++;    
	                    }
	                    else
	                        TN++;
	                }
	            }
	        }
	    
	    }
    
    //*********************************************************************
    //***************** Match of a instance *******************************
    //*********************************************************************

    public Boolean CoverInstance(double[] instance){
        
        //veo si coincide el antecedente
        for(int i=0 ; i<ruleSize ; ++i){

            int dim=selectedAtts.get(i);
            
            if(instance[dim]<getLowLimit(i) || instance[dim]>getUpperLimit(i)){
                return false;
            }
        }
        
        return true;
    }   


    //*********************************************************************
    //***************** Crossover Operator ********************************
    //*********************************************************************
    
    public void crossover(Rule parent2, Rule son1, Rule son2){
        
        //1) elegir un atributo de corte en el padre 1
    	int numAttr=Randomize.Randint(0, ruleSize);
        
        if(ruleSize==0)
        	return;
        	
        int a1=selectedAtts.get(numAttr);
        
        //2) ver si ese atributo esta en el padre2
        Boolean presence=false;
        int pos=0;
        for(int i=0 ; i<parent2.ruleSize ; ++i)
            if(parent2.selectedAtts.get(i)==a1){
                presence=true;
                pos=i;
            }
        
        //3) si el atributo esta en los dos padres
        if(presence){
            int cutPoint1=2*numAttr+1;  //inicio de segunda cadena en padre 1
            int cutPoint2=2*pos+1;      //inicio de segunda cadena en padre 2    
            cruzarPadres(son1,son2,cutPoint1,cutPoint2,numAttr,pos);
        }
        
        
        else{

            //buscar la posicion del siguiente atributo en la lista
            pos=0;
            int ii=0;
            for(ii=0 ; ii<son2.selectedAtts.size() ; ++ii){
                if(son2.selectedAtts.get(ii)>a1){
                    pos=ii;
                	break;
                }
            }
            
            if(ii==son2.selectedAtts.size())
            	pos=son2.selectedAtts.size();
            
            int cutPoint1=2*numAttr+2;  //inicio de segunda cadena en padre 1
            int cutPoint2=2*pos;      //inicio de segunda cadena en padre 2 
 
            cruzarPadres(son1,son2,cutPoint1,cutPoint2,numAttr,pos-1);
        }
                
    
    
    }
    
    public void cruzarPadres(Rule son1, Rule son2, int cutPoint1, int cutPoint2, int posAttr1, int posAttr2){
    	
        //************ fix bounds
    
        //quito elementos del primer padre y los meto en un vector auxiliar
        Vector<Double> aux=new Vector<Double>(limits.size()-cutPoint1);
        for(int i=cutPoint1 ; i<limits.size() ; ++i){
            aux.add(son1.limits.get(i));
        }
        
        for(int i=limits.size()-1 ; i>=cutPoint1 ; --i){
            son1.limits.remove(i);
        }

        //los del segundo padre los meto en el primero
        for(int i=cutPoint2 ; i<son2.limits.size() ; ++i){
            son1.limits.add(son2.limits.get(i));
         }
        
         //quito los del segundo padre
         for(int i=son2.limits.size()-1 ; i>=cutPoint2 ; --i){
            son2.limits.remove(i);
         }
         
         //meto los axiliares en el segundo padre
         for(int i=0 ; i<aux.size() ; ++i){
            son2.limits.add(aux.get(i));
        }
               
         
        //************ fix attributes
        
        //quito elementos del primer padre y los meto en un vector auxiliar

        Vector<Integer> auxAttr=new Vector<Integer>(son1.selectedAtts.size()-posAttr1-1);
        for(int i=posAttr1+1 ; i<son1.selectedAtts.size() ; ++i){
            auxAttr.add(son1.selectedAtts.get(i));
        }
        for(int i=son1.selectedAtts.size()-1 ; i>posAttr1 ; --i){
            son1.selectedAtts.remove(i);
        }

        //los del segundo padre los meto en el primero
        for(int i=posAttr2+1 ; i<son2.selectedAtts.size() ; ++i){
            son1.selectedAtts.add(son2.selectedAtts.get(i));
         }
        
         //quito los del segundo padre
         for(int i=son2.selectedAtts.size()-1 ; i>posAttr2 ; --i){
            son2.selectedAtts.remove(i);
         }
         
         //meto los axiliares en el segundo padre
         for(int i=0 ; i<auxAttr.size() ; ++i){
            son2.selectedAtts.add(auxAttr.get(i));
        }
        
        //************ fix rule sizes
         
        son1.ruleSize=son1.selectedAtts.size();
        son2.ruleSize=son2.selectedAtts.size();
        
        
        //************ fix predicted classes
        
        if(Randomize.RandClosed()<=0.5){
        	int auxClase=son1.clase;
        	son1.clase=son2.clase;
        	son2.clase=auxClase;
        }
        
        //************ swap bounds if lower bound is higher than the upper bound
        //for son1
        for(int i=0 ; i<son1.ruleSize ;++i){
       	 if(son1.getLowLimit(i)>son1.getUpperLimit(i)){
       		 
       		 double auxLimit=son1.getLowLimit(i);
       		 son1.setLowLimit(i, son1.getUpperLimit(i));
       		 son1.setUpperLimit(i, auxLimit);
       	 }
        }
        
        //for son2         
        for(int i=0 ; i<son2.ruleSize ;++i){
       	 if(son2.getLowLimit(i)>son2.getUpperLimit(i)){
       		 
       		 double auxLimit=son2.getLowLimit(i);
       		 son2.setLowLimit(i, son2.getUpperLimit(i));
       		 son2.setUpperLimit(i, auxLimit);
       	 }
        }
        	
            
    }
    
    
    //*********************************************************************
    //***************** Mutation Operator *********************************
    //*********************************************************************
    
    public void mutation(){
        
        int attribute, attIndex, newClass;
        double newValue, minOffset, maxOffset;
        
        
        //class mutation, prob 0.1
        if(BioHEL.train.getnClasses()>2 && Randomize.RandClosed()<0.1){

        	do {
                newClass = Randomize.Randint(0, BioHEL.train.getnClasses());
            }while((newClass==clase&& BioHEL.train.numClassesNotRemoved()>2) || newClass==defaultClass || 
                    (BioHEL.train.getInstancesPerClass()[newClass]==0)		
            );
            clase=newClass;
        }
        
        //bound mutation, prob 0.9
        else{
            
            if(ruleSize>0){
            	
                attIndex=Randomize.Randint(0, ruleSize);
                attribute=selectedAtts.get(attIndex);
                double emax[]=BioHEL.train.getemax();
                double emin[]=BioHEL.train.getemin();
         

                minOffset = maxOffset = 0.5 * (emax[attribute]-emin[attribute]);

                // 1) random bound selection and mutation
                if(Randomize.RandClosed()<0.5){             
                    newValue = mutationOffset(getLowLimit(attIndex), minOffset, maxOffset);

                    if (newValue < emin[attribute]) newValue = emin[attribute];
                    if (newValue > emax[attribute]) newValue = emax[attribute];                      
                    setLowLimit(attIndex, newValue);        //apply the mutation
                }
                
                else{
                	
                    newValue = mutationOffset(getUpperLimit(attIndex), minOffset, maxOffset);
                    if (newValue < emin[attribute]) newValue = emin[attribute];
                    if (newValue > emax[attribute]) newValue = emax[attribute];
                    setUpperLimit(attIndex, newValue);      //apply the mutation                 
                }
                
                
                // 2) swap bounds if necessary
                if(getLowLimit(attIndex)>getUpperLimit(attIndex)){
                    double aux=getLowLimit(attIndex);
                    setLowLimit(attIndex,getUpperLimit(attIndex));
                    setUpperLimit(attIndex,aux);
                }
            }
        }    
    
    }
    
    private double mutationOffset(double geneValue, double offsetMin, double offsetMax){
    	
    	double newValue;
    	
		if(Randomize.RandClosed()<0.5)
			newValue = geneValue + (Randomize.RandClosed()*offsetMax);
	        
	    else
	    	newValue = geneValue - (Randomize.RandClosed()*offsetMin);
		
		return newValue;
    }
    
    
    //*********************************************************************
    //***************** Special Stages ************************************
    //*********************************************************************
    
    public void doSpecialStage(int stage){
    	    	
    	if(stage==0){ //Generalize
    		if(ruleSize>1 && Randomize.RandClosed()<generalizeProbability){
    			int attribute=Randomize.Randint(0, ruleSize);    			
    			selectedAtts.remove(attribute);	//selected attribute
    			limits.remove(2*attribute);		//low bound
    			limits.remove(2*attribute);		//upper bound    			
    			ruleSize--;
    		}
    	}
    	
    	
    	else { //Specialize
    		if(ruleSize < NumAttributes && Randomize.RandClosed()<specializeProbability){
    			//creo una lista de los atributos no presentes en la regla
    			int numNoPres=NumAttributes-ruleSize;
    			Vector<Integer> noPresente=new Vector<Integer>(numNoPres);
    			
    			int pos=0;
    			for(int i=0 ; i<NumAttributes ; ++i){

    				if(pos<ruleSize){
    					if(selectedAtts.get(pos)!=i)
    						noPresente.add(i);
    					else
    						pos++;
    				}
    				else
    					noPresente.add(i);
    			}
    			 			
      			//selecciono uno de esos atributos no presentes aleatoriamente
    			int attribute=noPresente.get(Randomize.Randint(0, numNoPres));
    			
    			//lo anado como atributo seleccionado en su posicion
    			for(int i=0 ; i<ruleSize ; ++i){
    				
    				if(selectedAtts.get(i)>attribute){
    					pos=i;
    					break;
    				}
    			}
    			
    			selectedAtts.add(pos, attribute);
    			
    			//anado los limites correspondientes en su posicion
    	        double max,min,sizeD,minD,maxD,size;
    	        double emax[]=BioHEL.train.getemax();
    	        double emin[]=BioHEL.train.getemin();
    	        
    	        
	            minD=emin[attribute];
	            maxD=emax[attribute];
	            sizeD=maxD-minD;
	            size=(Randomize.RandClosed()*0.5+0.25)*sizeD;
    	        
	            min=Randomize.RandClosed()*(sizeD-size)+minD;
    			max=min+size;
    			
	            if(min<minD) {
	                max+=(minD-min);
	                min=minD;
	            }
	            if(max>maxD) {
	                min-=(max-maxD);
	                max=maxD;
	            }

    			limits.add(2*pos, min);
    			limits.add(2*pos+1,max);
    			
    			ruleSize++;
    		}
    	}
    	
    }
    
    //*********************************************************************
    //***************** Default rule creation *****************************
    //*********************************************************************
    
    public void createDefaultRule(int defClase){
    	clase=defClase;
        ruleSize=0;
    }
    
    
    //*********************************************************************
    //***************** Get/set fields ************************************
    //*********************************************************************
    
    public int getNumAttributes(){
        return ruleSize;
    }
    
    public void setNumAttributes(int n){
        ruleSize=n;
    }
    
    public int get(int i){
        return selectedAtts.get(i);
    }
    
    public void set(int i, int n){
        selectedAtts.set(i, n);
    }
    
    public double getLowLimit(int i){
        return limits.get(2*i);
    }
    
    public double getUpperLimit(int i){
        return limits.get(2*i+1);
    }    
    
    public void setLowLimit(int i, double d){
        limits.set(2*i, d);
    }
    
    public void setUpperLimit(int i, double d){
        limits.set(2*i+1, d);
    }   
    
    public int PredictedClass(){
        return clase;
    }
    
    public void setPredictedClass(int c){
        clase=c;
    }
    
    //*********************************************************************
    //***************** Print rule ****************************************
    //*********************************************************************
    
    public void printRule(){
        
        System.out.println("\n\n**********************");
        System.out.print("Clase = "+clase+" ; ruleSize = "+ ruleSize+" ; Atributos = ");
        for(int i=0 ; i<ruleSize ; ++i)
            System.out.print(get(i)+",");
        System.out.println("\nLimites:");
        for(int i=0 ; i<ruleSize ; ++i)
            System.out.println("[ "+ getLowLimit(i)+" , "+getUpperLimit(i)+" ]");
        System.out.println("**********************");        
    }
    
}
