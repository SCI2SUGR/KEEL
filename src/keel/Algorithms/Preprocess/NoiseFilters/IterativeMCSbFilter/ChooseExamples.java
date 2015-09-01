package keel.Algorithms.Preprocess.NoiseFilters.IterativeMCSbFilter;

import java.util.Arrays;
import java.util.Vector;

import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;


public class ChooseExamples {
	
	public ChooseExamples(){
	}
	
	/*public void ElegirEjemplosAEliminarRandom(Vector noisyInstances){
		
		if(Parameters.MaxPercRemoved == 1){
			return;
		}
		
		int numej = (int) (Parameters.numInstances*Parameters.MaxPercRemoved);
		
		if(noisyInstances.size() <= numej){
			return;
		}

		
		boolean[] usado = new boolean[noisyInstances.size()];
		Arrays.fill(usado, false);
		
		for(int i = 0 ; i < numej ; ++i){
			boolean correct = false;
			
			while(!correct){
				int rand_ind = Randomize.Randint(0, noisyInstances.size());
				
				if(!usado[rand_ind]){
					usado[rand_ind] = true;
					correct = true;
				}
			}
		}
		
		// actualizar noisyInstances
		int[] indicesej = new int[numej];
		int cont = 0;
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			if(usado[i])
				indicesej[cont++] = (Integer) noisyInstances.get(i);
		}
		
		
		//noisyInstances = new Vector();
		noisyInstances.clear();
		for(int i = 0 ; i < numej ; ++i){
			noisyInstances.add(indicesej[i]);
		}
	}*/
	
	/*
	public void ElegirEjemplosAEliminarDistancia(String tstset, Vector noisyInstances) throws Exception{
		
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
			dist.computeKNN(num_vecinos, (Integer) noisyInstances.get(i), -1, neighbors[i], clase_vecino[i]);
			
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
		
		for(int i = 0 ; i < noisyInstances.size() ; ++i)
			Parameters.LOG_OUT.println("Vecinos ruido "+ i + " = " + vecinos_ruido[i]);
		
		
		
		// aqui veo las clases de cada uno de los k ejemplos con ruido
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
			Parameters.LOG_OUT.println("Noisy example = " + noisyInstances.get(i) + "(CLASS = " + instancestsset[(Integer) noisyInstances.get(i)].getOutputNominalValuesInt(0) + ") ==> " + vecinos_ruido[i] + "noisy - " + (num_vecinos - vecinos_ruido[i]) + " clean neighbors");
			for(int j = 0 ; j < num_vecinos ; ++j){
				if(noisyneighbor[i][j])
					Parameters.LOG_OUT.println("\t==> NOISY neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
				else
					Parameters.LOG_OUT.println("\t==> clean neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
			}
		}
		
		
		// contar cuantos "ejemplos con ruido" tienen "k vecinos con ruido"
		int[] total_vecinoruido = new int[num_vecinos+1];
		Arrays.fill(total_vecinoruido, 0);
		
		for(int i = 0 ; i < noisyInstances.size() ; ++i)
			total_vecinoruido[vecinos_ruido[i]]++;
		
		// eliminare de menor numero de vecinos a mayor numero de vecinos con ruido
		int[] indices = new int[noisyInstances.size()];
		for(int i = 0 ; i < indices.length ; ++i)
			indices[i] = (Integer) noisyInstances.get(i);
		
		//noisyInstances = new Vector();
		noisyInstances.clear();
		boolean seguir = true;
		for(int j = 0 ; j < total_vecinoruido.length && seguir ; ++j){
			
			if(Parameters.PriorityNoisyNeighbors.equals("LOWEST_FIRST")){
				if(total_vecinoruido[j] > 0){
					
					for(int i = 0 ; i < indices.length ; ++i){
						if(vecinos_ruido[i] == j){
							noisyInstances.add(indices[i]);
						}
					}
					
					seguir = false;
				}
			}
			
			else if(Parameters.PriorityNoisyNeighbors.equals("HIGHEST_FIRST")){
				if(total_vecinoruido[total_vecinoruido.length-j-1] > 0){
					
					for(int i = 0 ; i < indices.length ; ++i){
						if(vecinos_ruido[i] == (total_vecinoruido.length-j-1)){
							noisyInstances.add(indices[i]);
						}
					}
					
					seguir = false;
				}
			}

		}
		
	}
	*/
	
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
			//Parameters.LOG_OUT.println("Noisy example = " + noisyInstances.get(i) + " - SCORE = " + score[i] + " (CLASS = " + instancestsset[(Integer) noisyInstances.get(i)].getOutputNominalValuesInt(0) + ", VECINO = " + es_vecino[(Integer) noisyInstances.get(i)] + ") ==> " + vecinos_ruido[i] + " noisy - " + (num_vecinos - vecinos_ruido[i]) + " clean neighbors");
			/*for(int j = 0 ; j < num_vecinos ; ++j){
				if(noisyneighbor[i][j])
					Parameters.LOG_OUT.println("\t==> NOISY neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
				else
					Parameters.LOG_OUT.println("\t==> clean neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
			}*/
		}
		
		/*Parameters.LOG_OUT.println("#NoiseEx\tSCORE\tCLASS\tNEIGHBOR\tNoisyNeighbors\tCleanNeighbors");
		for(int i = 0 ; i < noisyInstances.size() ; ++i){
		Parameters.LOG_OUT.println(noisyInstances.get(i) + "\t" + score[i] + "\t" + instancestsset[(Integer) noisyInstances.get(i)].getOutputNominalValuesInt(0) + "\t" + veces_que_es_vecino[i] + "\t" + vecinos_ruido[i] + "\t" + (num_vecinos - vecinos_ruido[i]));
			for(int j = 0 ; j < num_vecinos ; ++j){
				if(noisyneighbor[i][j])
					Parameters.LOG_OUT.println("\t==> NOISY neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
				else
					Parameters.LOG_OUT.println("\t==> clean neighbor = " + neighbors[i][j] + " - Class = " + clase_vecino[i][j]);
			}
		}*/
		
			
		/*int[] index;
		if(score.length > 1){
			index = Quicksort.sort(score, score.length, Quicksort.HIGHEST_FIRST);
		}
		else{
			index = new int[1];
			index[0] = 0;
		}
			
		for(int i = 0; i < score.length ; ++i)
			Parameters.LOG_OUT.println(score[index[i]]);*/
		 

		// me quedo con el 10% de los ejemplos con ruido
		/*int num_fin = (int) (noisyInstances.size()*0.5);
		
		double[] idfi = new double[num_fin];
		for(int i = 0 ; i < num_fin ; ++i)
			idfi[i] = (((Integer) noisyInstances.get(index[i]))*1.0);
		
		if(idfi.length > 1){
			index = Quicksort.sort(idfi, idfi.length, Quicksort.LOWEST_FIRST);
		}
		else{
			index = new int[1];
			index[0] = 0;
		}
		
		noisyInstances.clear();
		for(int i = 0 ; i < idfi.length ; ++i)
			noisyInstances.add((int)idfi[index[i]]);
		*/
		
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
			res = (1 + (vecinos_limpios/5)) / 2;
		}
		
		else{
			res = (vecinos_ruido/5) / 2;
		} 

		return res;
	}

}