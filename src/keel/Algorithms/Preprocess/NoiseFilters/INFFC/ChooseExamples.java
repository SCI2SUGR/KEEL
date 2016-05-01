package keel.Algorithms.Preprocess.NoiseFilters.INFFC;

import java.util.Arrays;
import java.util.Vector;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;


public class ChooseExamples {
	
	public ChooseExamples(){
	}
	
	public void ElegirEjemplosAEliminarDistancia_3(String tstset, Vector noisyInstances) throws Exception{
		
		InstanceSet ised = new InstanceSet();
		ised.readSet(tstset, false);
		Instance[] instancestsset = ised.getInstances();
		
		
		int num_vecinos = 5;
		int[][] neighbors = new int[noisyInstances.size()][num_vecinos];
		int[][] clase_vecino = new int[noisyInstances.size()][num_vecinos];
		boolean[][] noisyneighbor = new boolean[noisyInstances.size()][num_vecinos];
		for(int i = 0 ; i < noisyInstances.size() ; ++i)
			Arrays.fill(noisyneighbor[i], false);
		
		Distance dist = new Distance(tstset);
		
		int[] vecinos_ruido = new int[noisyInstances.size()];
		Arrays.fill(vecinos_ruido, 0);
		
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			dist.computeKNN(num_vecinos, (Integer) noisyInstances.get(i), -1/*clase*/, neighbors[i], clase_vecino[i]);
			
			// ver cuantos de esos vecinos estan dentro del conjunto con ruido
			for(int v = 0 ; v < num_vecinos ; ++v){
				boolean seguir = true;
				for(int j = 0 ; j < noisyInstances.size() && seguir ; ++j){
					if( neighbors[i][v] == ((Integer) noisyInstances.get(j)) ){
						noisyneighbor[i][v] = true;
						vecinos_ruido[i]++;
						seguir = false;
					}
				}
			}
			
		}
		
		
		// contar cuantas veces aparece cada ejemplo en vecinos con ruido		
		int[] es_vecino = new int[instancestsset.length];
		Arrays.fill(es_vecino, 0);
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			for(int k = 0 ; k < num_vecinos ; ++k){
				es_vecino[neighbors[i][k]]++;
			}
		}
		
		
		
		//calcular el score de ruido para cada ejemplo
		double[] score = new double[noisyInstances.size()];
		for(int i = 0 ; i < score.length ; ++i){
			int exampleclass = instancestsset[(Integer) noisyInstances.get(i)].getOutputNominalValuesInt(0);
			score[i] = ComputeScore(i, exampleclass, neighbors[i], clase_vecino[i], noisyneighbor[i], es_vecino, tstset, noisyInstances);
		}
		
		
		// aqui veo las clases de cada uno de los k ejemplos con ruido
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			Parameters.LOG_OUT.println("Noisy example = " + noisyInstances.get(i) + " (score = " + score[i] + ")");
		}

		
		int[] idfi = new int[noisyInstances.size()];
		for(int i = 0 ; i < noisyInstances.size() ; ++i)
			idfi[i] = (Integer) noisyInstances.get(i);
		
		noisyInstances.clear();
		for(int i = 0 ; i < idfi.length ; ++i)
			if(score[i] > Parameters.threshold)
				noisyInstances.add(idfi[i]);
	}
	
	
	double ComputeScore(int index, int exampleclass, int[] vecinos, int[] clase_vec, boolean[] noisyneighbor, int[] veces_vecino, String tstset, Vector noisyInstances) throws Exception{
				
		// diferencias
		double diferencias = 0;
		for(int k = 0 ; k < vecinos.length ; ++k){

			if(exampleclass != clase_vec[k]){
				
				double aux = calcular_clean(vecinos[k], !noisyneighbor[k], tstset, noisyInstances);				
				double modificador = 1 / (Math.sqrt( 1 + (veces_vecino[vecinos[k]]^2) ));
				diferencias += modificador*aux;
			}
		}
		diferencias /= vecinos.length;
		
		
		// coincidencias
		double coincidencias = 0;
		for(int k = 0 ; k < vecinos.length ; ++k){
			if(exampleclass == clase_vec[k]){
				
				double aux = calcular_clean(vecinos[k], !noisyneighbor[k], tstset, noisyInstances);
				double modificador = 1 / (Math.sqrt( 1 + (veces_vecino[vecinos[k]]^2) ));
				coincidencias += modificador*aux;
			}
		}
		coincidencias /= vecinos.length;
		
		// calculo resultado
		double modificador = 1 / ( Math.sqrt( 1 + (veces_vecino[index]^2) ));
		double res = modificador*(diferencias - coincidencias);
		
		return res;
	}
	
	
	public double calcular_clean(int example, boolean is_clean, String tstset, Vector noisyInstances) throws Exception{
		
		int num_vecinos = 5;
		int[] neighbors = new int[num_vecinos];
		int[] clase_vecino = new int[num_vecinos];
		
		Distance dist = new Distance(tstset);		
		dist.computeKNN(num_vecinos, example, -1/*clase*/, neighbors, clase_vecino);
			
		// ver cuantos de esos vecinos estan dentro del conjunto con ruido
		int vecinos_ruido = 0;
		for(int v = 0 ; v < num_vecinos ; ++v){
			boolean seguir = true;
			for(int j = 0 ; j < noisyInstances.size() && seguir ; ++j){
				if( neighbors[v] == ((Integer) noisyInstances.get(j)) ){
					vecinos_ruido++;
					seguir = false;
				}
			}
		}
			
		double vecinos_limpios = 5 - vecinos_ruido;
		
		double res = 0;
		
		if(is_clean){
			res = (1 + ( (double) vecinos_limpios / 5.0)) / 2.0;
		}
		
		else{
			res = ( (double) vecinos_ruido / 5.0) / 2.0;
		} 

		return res;
	}

}