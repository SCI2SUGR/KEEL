package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;

import java.io.Serializable;


public class RoughSetsOriginal implements Serializable {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 7659983106378537022L;
	/**
	 * 
	 */
	public Instances m_Data;
	protected int[][] equivalence_sets;
	public int[][] lower_aproximation;
	public int[][] upper_aproximation;
	protected double[] precision;
	protected Instances[] instancesByClass;
	public int[][] boundary;
	public int m_sig;
	ApproximateSets distance = new ApproximateSets();
	Arrays m_Arrays = new Arrays();
	double CutOff;

	public int[][] get_upper_aproximation() {
		return upper_aproximation;
	}

	public int[][] get_lower_aproximation() {
		return lower_aproximation;
	}

	public int[][] get_equivalence_set() {
		return equivalence_sets;
	}

	public double get_precision(int index_class) {
		if (precision != null && index_class < m_Data.numClasses()) {
			return precision[index_class];
		} else {
			return -1;
		}

	}

	public Instances[] get_instancesByClass(){
		return instancesByClass;
	}
	
	
	public RoughSetsOriginal(Instances data, int sig, double cOff) {
		m_Data = data;
		CutOff = cOff;
		m_sig = sig;
		try {
			EquivalenceClasses();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DivideInstancesByClass();
		//lower_aproximation();
		//upper_aproximation();
		// boundary();
	}

	public int[][] byClasses() {
		int[][] ins = new int[m_Data.numClasses()][];
		for (int i = 0; i < ins.length; i++) {
			int count = 0;
			int[] inst_class = new int[m_Data.numInstances()];
			for (int j = 0; j < m_Data.numInstances(); j++) {
				if (m_Data.instance(j).classValue() == i) {
					inst_class[count] = j;
					count++;
				}
			}
			int[] inst_class_end = new int[count];
			for (int j = 0; j < count; j++) {
				inst_class_end[j] = inst_class[j];
			}
			ins[i] = inst_class_end;
		}
		return ins;
	}

	public void DivideInstancesByClass() {
		instancesByClass = (new SInstances(m_Data)).byClasses();
	}

	/**
	 * Compute the equivalence classes
	 * 
	 * @throws Exception
	 */
	public void EquivalenceClasses() throws Exception {
		equivalence_sets = new int[m_Data.numInstances()][];
		for (int i = 0; i < m_Data.numInstances(); i++) {
			//System.out.println("Instancia "+i);
			equivalence_sets[i] = EquivClasses_Instance(m_Data.instance(i));
			
		}
	}

	/**
	 * Compute the equivalence classes to an instance. Return a list with the
	 * index of the equivalence clasess of an instance
	 * 
	 * @param obj
	 * @return the list with the index of the equivalence classes
	 * @throws Exception
	 */
	public int[] EquivClasses_Instance(Instance obj) throws Exception {
		int[] dist = new int[m_Data.numInstances()];
		int i = 0;
		distance.setInstances(m_Data);
		// m_Arrays.setInstances(m_Data);
		for (int j = 0; j < m_Data.numInstances(); j++) {
			double distanceValue = distance.CompRasgos(obj, j);
			if (m_sig == 1) {
				if ( distanceValue >= CutOff) {
					dist[i++] = j;
				}
			} else {
				if (distanceValue <= CutOff) {
					dist[i++] = j;
				}
			}
		}
		int[] distend = new int[i];
		for (int j = 0; j < i; j++) {
			distend[j] = dist[j];
		}
		return distend;
	}

	/**
	 * Compute the lower aproximations of the dataset
	 * 
	 * 
	 */
	public void lower_aproximation() {
		lower_aproximation = new int[m_Data.numClasses()][];
		for (int i = 0; i < m_Data.numClasses(); i++) {
			lower_aproximation[i] = lower_aproximation_Set(i);
		}
	}

	/**
	 * Compute the lower aproximation of a set
	 * 
	 * @param class_index
	 */
	public int[] lower_aproximation_Set(int class_index) {
		int[] la = new int[m_Data.numInstances()];
		int[] la_end = null;
		int count = 0;
		for (int j = 0; j < m_Data.numInstances(); j++) {
			int index = 0;
			while ((index < equivalence_sets[j].length)
					&& (m_Data.instance(equivalence_sets[j][index])
							.classValue() == class_index)) {
				index++;
			}
			if (index == equivalence_sets[j].length) {
				la[count++] = j;
				//System.out.println(j+"  Instancia equivalente");
			}
		}
		la_end = new int[count];
		for (int k = 0; k < count; k++) {
			la_end[k] = la[k];
		}
		return la_end;
	}

	/**
	 * Compute the upper aproximation
	 */
	public void upper_aproximation() {
		upper_aproximation = new int[m_Data.numClasses()][];
		for (int i = 0; i < m_Data.numClasses(); i++) {
			upper_aproximation[i] = upper_aproximation_Set(i);
		}
	}

	/**
	 * Compute the upper aproximation of a set
	 * 
	 * @param class_index
	 */
	public int[] upper_aproximation_Set(int class_index) {
		int[] ua = new int[m_Data.numInstances()];
		int[] ua_end = null;
		int count = 0;
		for (int j = 0; j < m_Data.numInstances(); j++) {
			int index = 0;
			while ((index < equivalence_sets[j].length)
					&& (m_Data.instance(equivalence_sets[j][index])
							.classValue() != class_index)) {
				index++;
			}
			if (index < equivalence_sets[j].length) {
				ua[count++] = j;
			}
		}
		ua_end = new int[count];
		for (int k = 0; k < count; k++) {
			ua_end[k] = ua[k];
		}
		return ua_end;
	}

	/**
	 * Compute the boundary of the set (Cases in the upper aproximation and not
	 * in the lower aproximation)
	 */
	public int[] boundary_Class(int index_class) {
		int[] b = new int[upper_aproximation[index_class].length
				- lower_aproximation[index_class].length];
		for (int i = 0; i < upper_aproximation[index_class].length; i++) {
			int j = 0;
			int count = 0;
			while (j < lower_aproximation[index_class].length
					&& upper_aproximation[index_class][i] != lower_aproximation[index_class][j]) {
				j++;
				count++;
			}
			if (j >= lower_aproximation[index_class].length) {
				b[count] = upper_aproximation[index_class][i];
			}
		}
		return b;
	}

	public void boundary() {
		boundary = new int[upper_aproximation.length][];
		for (int i = 0; i < m_Data.numClasses(); i++) {
			boundary[i] = boundary_Class(i);
		}
	}

	/**
	 * Convert from a lisf of index to a list of Instances from m_Data
	 * 
	 * @param Set
	 * @return list of Instances corresponded to the index in Set from m_Data
	 */
	public Instances convert_Set(int[] Set) {
		Instances inst = new Instances(m_Data, 0, 0);
		for (int i = 0; i < Set.length; i++) {
			// inst.add(m_Data.instance(i));
			inst.add(m_Data.instance(Set[i]));
		}
		return inst;
	}

}
