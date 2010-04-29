package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

public class populationWrapper {
	
	geneticAlgorithm ga;
	classifierFactory cf;
	int popSize;
	

	public populationWrapper(int pPopSize){
		cf = new classifierFactory();
		ga = new geneticAlgorithm(cf);
		popSize = pPopSize;
	}



	public void activateModifiedFlag(){
		int i;

		rank[] rk=ga.getPopulationRank();
		for(i=0;i<popSize;i++) rk[i].ind.activateModified();
		ga.resetBest();
	}

	rank[] getPopulationRank()
	{
		int i;

		return ga.getPopulationRank();
	}


	public void gaIteration(){
		ga.doIterations(1);
	}

	public void releasePopulation(){	
	}

	public classifier getBestOverall(){
		return (classifier)ga.getBest();
	}

	classifier[] getPopulation(){
		return (classifier[])ga.getPopulation();
	}


	public classifier getBestPopulation(){
		rank[] rk=ga.getPopulationRank();
		return (classifier)rk[0].ind;
	}

	public classifier getWorstPopulation(){
		rank[] rk=ga.getPopulationRank();
		return (classifier)rk[popSize - 1].ind;
	}

	public double getAverageLength()
	{
		int i;
		double ave=0;

		rank[] rk=ga.getPopulationRank();

		for(i=0;i<popSize;i++) ave+=rk[i].ind.getLength();

		return ave/(double)popSize;
	}

	public Object[] getAverageAccuracies(){
		int i;
		Double ave1 = new Double(0);
		Double ave2 = new Double(0);
		rank[] rk = ga.getPopulationRank();

		for(i=0;i<popSize;i++) {
			ave1 += ((classifier)rk[i].ind).getAccuracy();
			ave2 += ((classifier)rk[i].ind).getAccuracy2();
		}

		ave1/=(double)popSize;
		ave2/=(double)popSize;
		
		Object[] res = new Object[2];
		res[0] = ave1;
		res[1] = ave2;
		
		return res;
	}


	public double getMaxAccuracy(){
		int i;

		rank[] rk=ga.getPopulationRank();
		double max=((classifier)rk[0].ind).getAccuracy();

		for(i=1;i<popSize;i++) {
			double percen=((classifier)rk[i].ind).getAccuracy();
			if(percen>max) max=percen;
		}

		return max;
	}

	public classifier cloneClassifier(classifier orig){
		return cf.cloneClassifier(orig);
	}

	public classifier createClassifier(){
		return cf.createClassifier();
	}

	public void destroyClassifier(classifier orig){
	}


}
