package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: windowingILAS</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

public class windowingILAS {
	
	int stratum;		//actual stratum
	int numStrata;		//total number of strata
	

    //*********************************************************************
    //***************** Constructor ***************************************
    //*********************************************************************
	
	public windowingILAS(int numberOfStrata){
		stratum=-1;
		numStrata=numberOfStrata;
	}
	
	
    //*********************************************************************
    //***************** Reorder examples **********************************
    //*********************************************************************
	
	public void reorderExamples(){

		int numClasses=BioHEL.train.numClassesNotRemoved();
		int numOfExamples[]=BioHEL.train.getInstancesPerClass();
		int[][] ListOfExamples=new int[numClasses][];
		
		int pos=0;
		for(int i=0 ; i<BioHEL.train.getnClasses() ; ++i){
	
			if(numOfExamples[i]>0){
				
				ListOfExamples[pos++]=BioHEL.train.examplesClass(i);
			}
		}		
		
		
		int clase;
		stratum = 0;
		int posaux=0;

		Sampling sampl[] = new Sampling[numClasses];
		for(int i=0 ; i<BioHEL.train.getnClasses() ; i++){

			if(numOfExamples[i]>0){
				sampl[posaux++] = new Sampling(numOfExamples[i]);
				}
		}

		
		posaux=0;
		for(int i=0 ; i<BioHEL.train.getnClasses() ; ++i){
			
			if(numOfExamples[i]>0){

				clase=i;
				
				while(numOfExamples[clase]>0){

					pos=sampl[posaux].getSample();

					pos=ListOfExamples[posaux][pos];
					numOfExamples[clase]--;
					
					BioHEL.train.Subset[pos]=stratum;
					stratum = (stratum + 1)%numStrata;
				}
				
				posaux++;
			}
		}
		

		stratum=0;	//to start
	}
	
	
    //*********************************************************************
    //***************** Set stratum for a new iteration *******************
    //*********************************************************************
	
	public void newIteration(int iteration){
		
		stratum=iteration%numStrata;
	}
	
	
	int numVersions(){
		return numStrata;
	}
	
	int getStratum(){
		return stratum;
	}
	
	public void mergeStrata(){
		
		for(int i=0 ; i<BioHEL.train.getnData() ; ++i)
			BioHEL.train.Subset[i]=-1;

		
	}

}