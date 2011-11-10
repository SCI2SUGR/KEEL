package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;

import java.util.Arrays;

public class RoughSetsCuttoff {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 7659983106378537022L;
	/**
	 * @param args
	 */
	private Instances m_Data;
	protected int[][] equivalence_sets;
	private int[][] lower_aproximation;
	private int[][] upper_aproximation;
	private int[][] edit3RS;
	protected double[] precision;
	protected Instances[] instancesByClass;
	private int[][] boundary;
	private int m_sig;
	private ApproximateSets distance = new ApproximateSets();
	private int newInstancesIndex;
	private double CutOff;

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

	public RoughSetsCuttoff(Instances data, int sig, double cOff) {
		m_Data = data;
		CutOff = cOff;
		m_sig = sig;
		DivideInstancesByClass();
	}

	public RoughSetsCuttoff(Instances data, int newInstsIndex, int sig, double cOff) {
		m_Data = data;
		CutOff = cOff;
		m_sig = sig;
		newInstancesIndex = newInstsIndex;
		DivideInstancesByClass();
	}

	/**
	 * Convert from a lisf of index to a list of Instances from m_Data
	 * 
	 * @param Set
	 * @return list of Instances corresponded to the index in Set from m_Data
	 */
	public Instances getInstances_by_index(int[] Set) {
		Instances inst = new Instances(m_Data, 0, 0);
		for (int i = 0; i < Set.length && Set[i] != -1; i++) {
			inst.add(m_Data.instance(Set[i]));
		}
		return inst;
	}

	public void DivideInstancesByClass() {
		Instances[] ins = new Instances[m_Data.numClasses()];
		ins[0] = new Instances(m_Data,0);
		ins[1] = new Instances(m_Data,0);
		for (int j = 0; j < m_Data.numInstances(); j++) {
			int classValue = (int)m_Data.instance(j).classValue();
			ins[classValue].add(m_Data.instance(j));
			
		}
	    instancesByClass = ins;
	}

	/**
	 * Compute the equivalence classes
	 * 
	 * @throws Exception
	 */
	public void EquivalenceClasses() throws Exception {
		int cant = m_Data.numInstances() - newInstancesIndex;
		equivalence_sets = new int[cant][];
		for (int i = 0, j = newInstancesIndex; j < m_Data.numInstances(); i++, j++) {
			equivalence_sets[i] = EquivClasses_Instance(m_Data.instance(j));
		}
	}

	/**
	 * Compute the equivalence classes to an instance. Return a list with the
	 * index of the equivalence clasess of an instance
	 * 
	 * @param obj
	 * @throws Exception
	 * @throws Exception
	 */
	public int[] EquivClasses_Instance(Instance obj) throws Exception {
		int[] dist = new int[m_Data.numInstances()];
		int i = 0;
		distance.setInstances(m_Data);
		for (int j = 0; j < m_Data.numInstances(); j++) {
			double distanceValue = distance.CompRasgos(obj, j);
			if (m_sig == 1) {
				if (distanceValue >= CutOff) {
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

	public void boundary() {
		boundary = new int[upper_aproximation.length][];
		for (int i = 0; i < m_Data.numClasses(); i++) {
			boundary[i] = boundary_Class(i);
		}
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

	// ////////////////////////////my code//////////////////////my
	// code//////////////////////////////////////////

	/**
	 * Compute the lower aproximations of the dataset
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	public void lower_aproximation() throws Exception {
		setLower_Approximation();
		int contClass0 = 0;
		int contClass1 = 0;
		int numInst = m_Data.numInstances();
		for (int i = 0; i < numInst; i++) {
			Instance instance = m_Data.instance(i);
			//System.out.println("_________ " + i);
			if (is_A_Lower_App_Instance(instance)) {
				int actualClass = (int) instance.classValue();
				if (actualClass == 0)
					lower_aproximation[actualClass][contClass0++] = i;
				else
					lower_aproximation[actualClass][contClass1++] = i;
				//System.out.println("selecionado:  " + i + "class: " + actualClass);
			}
		}
		if (contClass0 == 0 && contClass1 == 0)
			lower_aproximation = null;
	}

	public void lower_approximation_set(int classIndex) throws Exception {
		if (classIndex < m_Data.numClasses()) {
			lower_approximation_set(classIndex, 0);
		}
	}

	public void lower_approximation_set(int classValue, int newsIndex)
			throws Exception {
		if (classValue < m_Data.numClasses()) {
			setLower_Approximation();
			int cont = 0;
			int numInst = m_Data.numInstances();
			for (int i = newsIndex; i < numInst; i++) {
				Instance instance = m_Data.instance(i);
				//System.out.println("_________ " + i + "  actual class:  "+ instance.classValue()+ " finder class:  "+ classValue);
				int actualClass = (int) instance.classValue();
				if (actualClass == classValue){
					//System.out.println("misma clase");
					if (is_A_Lower_App_Instance(instance)) {
						lower_aproximation[actualClass][cont++] = i;
						//System.out.println("selecionado:  " + i+ "class: " + actualClass);
					}
				}
			}
			if (cont == 0)
				lower_aproximation = null;
		}
	}

	private boolean is_A_Lower_App_Instance(Instance obj) throws Exception {
		int objClassValue = (int) obj.classValue();
		int opositiClassValue = Math.abs(objClassValue - 1);
			boolean isLower = true;
			distance.setInstances(instancesByClass[opositiClassValue]);
			for (int j = 0; j < instancesByClass[opositiClassValue].numInstances() && isLower; j++) {
				double distanceValue = distance.CompRasgos(obj, j);
				if (m_sig == 1) {
					isLower = !(distanceValue >= CutOff);
				} else {
					isLower = !(distanceValue <= CutOff);
				}
			}
			
			if (isLower) {
				distance.setInstances(instancesByClass[objClassValue]);
				for (int j = 0; j < instancesByClass[objClassValue].numInstances(); j++) {
					double distanceValue = distance.CompRasgos(obj, j);
					if (m_sig == 1) {
						if (distanceValue >= CutOff && distanceValue != 1) {
							return true;
						}
					} else {
						if (distanceValue <= CutOff && distanceValue != 1) {
							return true;
						}
					}
				}
			}
		return false;
	}

	/**
	 * all new instances are in upper approximation and they are in boundary to
	 * because in this case the list have not lower approximation instances
	 * 
	 * @throws Exception
	 */
	public void edit3RS() throws Exception {
		int[][] temporal_list = new int[m_Data.numClasses()][m_Data
				.numInstances()];
		int[] cont_by_class = new int[m_Data.numClasses()];
		for (int i = 0; i < m_Data.numClasses(); i++) {
			cont_by_class[i] = 0;
		}
		for (int i = 0; i < m_Data.numInstances(); i++) {
			Instance instance = m_Data.instance(i);
			int classIndex = (int) instance.classValue();
			if (is_edit3RS_Instance(instance)) {
				temporal_list[classIndex][cont_by_class[classIndex]++] = i;
			}
		}
		for (int i = 0; i < m_Data.numClasses(); i++) {
			edit3RS[i] = new int[cont_by_class[i]];
			System.arraycopy(temporal_list[i], 0, edit3RS[i], 0,
					cont_by_class[i]);
		}
	}

	private boolean is_edit3RS_Instance(Instance instance) throws Exception {
		int objClassValue = (int) instance.classValue();
		int opositiClassValue = Math.abs(objClassValue - 1);
		int contSemejansasMismaClase = 0;
		distance.setInstances(instancesByClass[objClassValue]);
		for (int j = 0; j < instancesByClass[objClassValue].numInstances(); j++) {
			double distanceValue = distance.CompRasgos(instance, j);
			if (m_sig == 1) {
				if (distanceValue >= CutOff && distanceValue != 1) {
					contSemejansasMismaClase++;
				}
			} else {
				if (distanceValue <= CutOff && distanceValue != 1) {
					contSemejansasMismaClase++;
				}
			}
		}
		int contSemejansasOpositiClass = 0;
		distance.setInstances(instancesByClass[opositiClassValue]);
		for (int j = 0; j < instancesByClass[opositiClassValue].numInstances(); j++) {
			double distanceValue = distance.CompRasgos(instance, j);
			if (m_sig == 1) {
				if (distanceValue >= CutOff && distanceValue != 1) {
					contSemejansasOpositiClass++;
				}
			} else {
				if (distanceValue <= CutOff && distanceValue != 1) {
					contSemejansasOpositiClass++;
				}
			}
			if (contSemejansasOpositiClass >= contSemejansasMismaClase)
				return false;
		}
		return true;
	}

	public int getNewInstancesIndex() {
		return newInstancesIndex;
	}

	public void setNewInstancesIndex(int newInstancesIndex) {
		this.newInstancesIndex = newInstancesIndex;
	}

	public Instances getLower_aproximation(int classIndex) {
		if (lower_aproximation != null)
			return getInstances_by_index(lower_aproximation[classIndex]);
		return null;
	}

	public Instances getUpper_aproximation(int classIndex) {
		return getInstances_by_index(upper_aproximation[classIndex]);
	}

	public Instances getBoundary(int classIndex) {
		return getInstances_by_index(boundary[classIndex]);
	}

	private void setLower_Approximation() {
		int numInstancesClass0 = instancesByClass[0].numInstances();
		int numInstancesClass1 = instancesByClass[1].numInstances();
		int mayor = (numInstancesClass0 > numInstancesClass1) ? numInstancesClass0
				: numInstancesClass1;
		lower_aproximation = new int[m_Data.numClasses()][mayor];
		Arrays.fill(lower_aproximation[0], -1);
		Arrays.fill(lower_aproximation[1], -1);
	}

	public Instances[] getInstancesByClass() {
		return instancesByClass;
	}
	
	public int getInstancesSizeByClass(int classValue){
		return instancesByClass[classValue].numInstances();
	}

	
}
