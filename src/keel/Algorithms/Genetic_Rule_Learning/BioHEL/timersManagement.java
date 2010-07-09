package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import java.util.Vector;

public class timersManagement {
	
	Vector<timingProcess> timers;
	int iteration;
	public timerMDL tMDL;
	void addTimer(timingProcess tp){timers.addElement(tp);}
	

	public timersManagement(){
		
		timers = new Vector<timingProcess>();
		
		iteration = -1;
		tMDL = new timerMDL();
		addTimer(tMDL);
	}




	public void incIteration(int lastIteration){
		iteration++;

		int i;
		for(i=0;i<timers.size();i++)
			timers.get(i).newIteration(iteration,lastIteration);
	}

	public void reinit(){
		iteration=-1;

		int i;
		for(i=0;i<timers.size();i++)
			timers.get(i).reinit();
	}


	public void dumpStats()
	{
		int i;
		for(i=0;i<timers.size();i++)
			timers.get(i).dumpStats(iteration);
	}

	public void setPW(populationWrapper pPW)
	{
		int i;
		for(i=0;i<timers.size();i++)
			timers.get(i).initialize(pPW);
	}

}
