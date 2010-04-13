package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import java.util.Vector;
import org.core.Randomize;

/**
 * <p>Title: GA</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

public class GA {
	
	public class rank{
		
		public int pos;
		public Rule ind;
		
		public rank(){};
		
	}
    
    private int popSize;
    private Rule[] population;
    private Rule[] offspringPopulation;

    private int currentIteration;
    private int NumAttributes;
    private Rule[] bestIndividual;
    private int tournamentSize;

    private double crossoverProbability;
    private double mutationProbability;
    private boolean elitismEnabled;
    private int numGenerations;
    private int numIterations;
    private int numStages;
    
    private int numVersions;
    private rank[] populationRank;
    
    
           
    private MDL mdl;
    static windowingILAS ilas;
        
    
    /**
     * constructor
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public GA(int n, int ts, int expecRuleSz, double crossoverprob, double mutationProb, boolean elitism, int generations, double cbrp, double covRat, double gprob1, double sprob2, int dclass, double itlr, double wrf, int numstrata, int numstg){

        popSize = n;
        tournamentSize = ts;
        crossoverProbability=crossoverprob;
        mutationProbability=mutationProb;
        elitismEnabled=elitism;
        numGenerations=generations;
        numStages=numstg;
                
        Rule.setParameters(expecRuleSz,cbrp,covRat,gprob1,sprob2,dclass);
        NumAttributes=BioHEL.train.getnInputs();
        
        numVersions=numstrata;

        bestIndividual=new Rule[numVersions];
        for(int i=0 ; i<numVersions ; ++i)
        	bestIndividual[i]=null;
        
        numIterations=numGenerations-1;
        
        mdl=new MDL(cbrp,covRat,itlr,wrf);
        ilas=new windowingILAS(numstrata);
    }

    
    /**
     * return best rule (individual)
     * @param parameters parseParameters It contains the input files, output files and parameters
     */    
    public Rule RunGA(){
    	
        // 1) inicialize population
        initializePopulation();
        
        // 2) do numGenerations iterations
        Integer n=new Integer(numIterations);
        doIterations(n);      
        
        // 3) return best individual of last generation
        Rule bestRule=bestIndividual[ilas.getStratum()].cloneRule();
        
        return bestRule;
    }
    
    //*********************************************************************
    //***************** Initialization ************************************
    //*********************************************************************
    
    public void initializePopulation(){
    	
    	Sampling sampl=new Sampling(BioHEL.train.getnData(), BioHEL.train.instancesNotRemoved());
    	int pos;
                
        population = new Rule[popSize];
        offspringPopulation = new Rule[popSize];
        populationRank = new rank[popSize];
        
        
        for(int i=0 ; i<popSize ; ++i){
            population[i]=new Rule();
        	pos=sampl.getSample2();
            population[i].createRule(pos);
        }
       
    }	
    
    //*********************************************************************
    //***************** Do Iterations *************************************
    //*********************************************************************

    public Rule bestAccIndividual(){
    	
    	double bestAcc=population[0].getAccuracy1();
        int pos=0;
            
        for(int i=1 ; i<popSize ; ++i){
        	if(population[i].getAccuracy1()>bestAcc){
        		bestAcc=population[i].getAccuracy1();
                pos=i;
            }
        }
        
       return population[pos];
    }
    
    public boolean newBest(){
    	
    	if(currentIteration<2)
    		return false;
    	
    	else{
        	Rule actual=bestIndividual[ilas.getStratum()].cloneRule();
        	Rule past=bestIndividual[((ilas.numStrata + ((ilas.getStratum()-1)%ilas.numStrata)))%ilas.numStrata];
        	actual.fitnessComputation(mdl);
        	past.fitnessComputation(mdl);
        	if(actual.fitness<past.fitness)
        		return true;
    	} 
    	
    	return false;
    }
    
    public void doIterations(int n){
    	    	
    	currentIteration=0;
    	
    	//inicializar parametros mdl
        doFitnessComputations();

    	//fijo el primer subconjunto
    	ilas.reorderExamples();
    	mdl.newIteration(0,bestAccIndividual(),newBest());
        
        currentIteration=1;

        for ( ; n>0; n--){

            selectionAlgorithm();       //population   <- selection individuals from this generation
            crossover();                //offspringPopulation   <- crossover of parents
            mutation();                 //offspringPopulation   <- mutation of new inidividuals
            replacementAlgorithm();     //population   <- offspringPopulation

        	//guardo el mejor indivudo de la iteracion actual
            doFitnessComputations();
            checkBestIndividual();
            
            //actualizar el parametro W, para nueva iteracion
        	mdl.newIteration(currentIteration,bestIndividual[ilas.getStratum()],newBest());
        	
          	if(n>1)
        		ilas.newIteration(currentIteration);
        	else
        		ilas.mergeStrata();
        	
        	
            currentIteration++;
        }
    }
    
    
    
    
    //*********************************************************************
    //***************** Selection Algorithm *******************************
    //*********************************************************************
    
    public void selectionAlgorithm(){
    	
        int winner, candidate;

        // 1) select "popSize" individuals by torunament
        for(int i=0; i<popSize; ++i){
            
            //There can be only one
            winner=Randomize.Randint(0, popSize);
            
            for (int j=1; j<tournamentSize ; ++j) {
                candidate=Randomize.Randint(0, popSize);
                if(population[candidate].fitness<population[winner].fitness){
                    winner = candidate;
                }
            }


            offspringPopulation[i]=population[winner].cloneRule();
	}
        
        // 2) copy individuals to population
        for(int i=0 ; i<popSize ; ++i)
            population[i]=offspringPopulation[i].cloneRule();
    }
    
  
    //*********************************************************************
    //***************** Crossover Algorithm *******************************
    //*********************************************************************
    
    public void crossover(){

        int countCross=0;
        int p1=-1, p2;
        Sampling samp=new Sampling(popSize);
        
        
        for(int j=0 ; j<popSize ; j++){
        	
        	if(Randomize.RandClosed() < crossoverProbability){
                if(p1==-1)
                    p1 = samp.getSample();
                
                else{
                    p2 = samp.getSample();
                    crossTwoParents(p1, p2, countCross,	countCross+1);
                    countCross+=2;
                    p1 = -1;
                }
            }
            
            else
            	crossOneParent(samp.getSample(), countCross++);
		}
	        
		if (p1 != -1)
			crossOneParent(p1, countCross++);
    }
    
    public void crossTwoParents(int parent1, int parent2, int son1, int son2){

		offspringPopulation[son1] = population[parent1].cloneRule();
		offspringPopulation[son2] = population[parent2].cloneRule();

		population[parent1].crossover(population[parent2] ,offspringPopulation[son1], offspringPopulation[son2]);
    }

    public void crossOneParent(int parent, int son){
    	offspringPopulation[son] = population[parent].cloneRule();
    }
    



  
    //*********************************************************************
    //***************** Mutation Operator *********************************
    //*********************************************************************
    
    public void mutation(){
    	individualMutation();
    	specialStages();    
    }
    
    public void individualMutation(){

        for(int i=0; i<popSize; ++i)
            if(Randomize.RandClosed()<mutationProbability)
                offspringPopulation[i].mutation();
    }

    public void specialStages(){
    	
    	int i,j;
    	
    	for(i=0;i<numStages;i++){
    		for(j=0;j<popSize; j++) {
    			offspringPopulation[j].doSpecialStage(i);
    		}
    	}
    }
    


    //*********************************************************************
    //***************** Replacement Algorithm *****************************
    //*********************************************************************
    
    public void replacementAlgorithm(){
    	
        totalReplacement();
        doFitnessComputations();
        createPopulationRank();
       
        if(elitismEnabled)
            doElitism();
    }
    
    
    public void createPopulationRank(){

    	for (int i = 0; i < popSize; i++){
    		populationRank[i]=new rank();
    		populationRank[i].pos = i;
    		populationRank[i].ind = population[i];
    	}
    	
    	Quicksort.ordenar(populationRank, 0, popSize-1);
    }
    
    
    public void totalReplacement(){
        
        // copy offspring to population
        for(int i=0 ; i<popSize ; ++i)
            population[i]=offspringPopulation[i].cloneRule();

    }    
    
    
    public void doElitism(){
    	
    	int i,j;
    	int numV=ilas.numVersions();
    	
    	for(i=0 ; i<numV ; ++i){

    		if(bestIndividual[i]!=null){

    			bestIndividual[i].fitnessComputation(mdl);
    		}
    }
    	


    	Vector<Integer> priorities=new Vector<Integer>(popSize+numV,0);

    	for(i=0;i<popSize;i++)
    		priorities.addElement(populationRank[i].pos);
    	
    	for(i=0 ; i<numV ; i++) {
    		
    		if(bestIndividual[i]!=null){
    			
    			int size=priorities.size();
    			
    			for(j=0;j<size;j++) {
    				
    				Rule ind;
    				
    				int pos=priorities.get(j);
    				
    				if(pos>=popSize) {
    					ind=bestIndividual[pos-popSize];
    				}
    				
    				else{
    					ind=population[pos];
    				}

    				if(bestIndividual[i].fitness<ind.fitness){
    					priorities.insertElementAt(popSize+i,j);
    					break;
    				}
    			}
    			
    			if(j==size) {
    				priorities.addElement(popSize+i);
    			}
    		}
    	}

    	Vector<Integer> elite= new Vector<Integer>(10,10);
    	for(i=0;i<popSize;i++) {
    		if(priorities.get(i)>=popSize) {
    			elite.addElement(priorities.get(i)-popSize);
    		}
    	}
    	
    	int index=0;
    	int size=priorities.size();
    	
    	for(i=popSize ; i<size ; i++){
    		
    		if(priorities.get(i)<popSize){
    			int pos=priorities.get(i);
    			population[pos]= bestIndividual[elite.get(index++)].cloneRule();
    		}
    	}
    	
    }

    //*********************************************************************
    //***************** Best individual ***********************************
    //*********************************************************************
    
    
    public void checkBestIndividual(){
    	
    	int currVer=ilas.getStratum();

    	if(bestIndividual[currVer]==null)
    		bestIndividual[currVer]=populationRank[0].ind.cloneRule();
    	
    	
    	else{
    		bestIndividual[currVer].fitnessComputation(mdl);
    		if (bestIndividual[currVer].fitness>populationRank[0].ind.fitness){
    			bestIndividual[currVer] =populationRank[0].ind.cloneRule();
    		}
    	}
    }
  
    
	public void doFitnessComputations(){
		
        for(int i=0 ; i<popSize ; ++i)
        	population[i].fitnessComputation(mdl);
	}
    
}
