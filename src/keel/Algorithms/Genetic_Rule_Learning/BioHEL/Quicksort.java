package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: Quicksort</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */


public class Quicksort {
	
	
	static public void ordenar(GA.rank[] vector, int primero, int ultimo){
		
    	int i=primero, j=ultimo;
    	GA.rank pivote=vector[(primero+ultimo)/2];
    	GA.rank auxiliar;
 
    	do{
    		while(vector[i].ind.fitness<pivote.ind.fitness) i++;    		
    		while(vector[j].ind.fitness>pivote.ind.fitness) j--;
 
    		if (i<=j){
    			auxiliar=vector[j];
    			vector[j]=vector[i];
    			vector[i]=auxiliar;
    			i++;
    			j--;
    		}
 
    	} while (i<=j);
 
    	if(primero<j) ordenar(vector,primero, j);
    	if(ultimo>i) ordenar(vector,i, ultimo);
    }


}
