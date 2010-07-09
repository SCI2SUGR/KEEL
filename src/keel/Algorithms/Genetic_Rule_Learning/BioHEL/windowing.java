package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import keel.Dataset.Instance;

public abstract class windowing {
	abstract public void setInstances(Instance[] pSet,int pHowMuch);
	abstract public Object[] newIteration();
	abstract public int numVersions();
	abstract public int getCurrentVersion();
	abstract public boolean needReEval();
}

