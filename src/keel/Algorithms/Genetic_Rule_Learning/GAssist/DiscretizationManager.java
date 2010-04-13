/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.GAssist;

import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Discretizers.ChiMerge_Discretizer.*;
import keel.Algorithms.Discretizers.Fayyad_Discretizer.*;
import keel.Algorithms.Discretizers.Id3_Discretizer.*;
import keel.Algorithms.Discretizers.UniformFrequency_Discretizer.*;
import keel.Algorithms.Discretizers.UniformWidth_Discretizer.*;
import keel.Algorithms.Discretizers.USD_Discretizer.*;
import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import java.util.*;

public class DiscretizationManager {
	static Vector discretizers;

	public static void init() {
		// It's ugly, I know
		discretizers = new Vector();
		addDiscretizer(Parameters.discretizer1);
		addDiscretizer(Parameters.discretizer2);
		addDiscretizer(Parameters.discretizer3);
		addDiscretizer(Parameters.discretizer4);
		addDiscretizer(Parameters.discretizer5);
		addDiscretizer(Parameters.discretizer6);
		addDiscretizer(Parameters.discretizer7);
		addDiscretizer(Parameters.discretizer8);
		addDiscretizer(Parameters.discretizer9);
		addDiscretizer(Parameters.discretizer10);
	}

	public static void addDiscretizer(String name) {
		StringTokenizer st = new StringTokenizer(name,"_");
		String discretizerName=st.nextToken();
		Discretizer disc=null;

		if(discretizerName.equalsIgnoreCase("UniformWidth")){
			if(!st.hasMoreElements()) {
				LogManager.printErr("Error in discretizer "+name+". It should have a parameter");
				System.exit(1);
			} 
			int numIntervals=Integer.parseInt(st.nextToken());
			disc=new UniformWidthDiscretizer(numIntervals);
		} else if(discretizerName.equalsIgnoreCase("UniformFrequency")){
			if(!st.hasMoreElements()) {
				LogManager.printErr("Error in discretizer "+name+". It should have a parameter");
				System.exit(1);
			} 
			int numIntervals=Integer.parseInt(st.nextToken());
			disc=new UniformFrequencyDiscretizer(numIntervals);
		} else if(discretizerName.equalsIgnoreCase("ChiMerge")){
			if(!st.hasMoreElements()) {
				LogManager.printErr("Error in discretizer "+name+". It should have a parameter");
				System.exit(1);
			} 
			double confidence=Double.parseDouble(st.nextToken());
			disc=new ChiMergeDiscretizer(confidence);
		} else if(discretizerName.equalsIgnoreCase("ID3")){
			disc=new Id3Discretizer();
		} else if(discretizerName.equalsIgnoreCase("Fayyad")){
			disc=new FayyadDiscretizer();
		} else if(discretizerName.equalsIgnoreCase("USD")){
			disc=new USDDiscretizer();
		} else if(discretizerName.equalsIgnoreCase("Disabled")){
		} else {
			LogManager.printErr("Unknown discretizer "+name);
			System.exit(1);
		}

		if(disc != null) {
			disc.buildCutPoints(PopulationWrapper.is);
			discretizers.addElement(disc);
		}
	}

	public static int getNumDiscretizers() {
		return discretizers.size();
	}

	public static Discretizer getDiscretizer(int num) {
		return (Discretizer)discretizers.elementAt(num);
	}
} 
