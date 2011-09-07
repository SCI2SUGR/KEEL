package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;

import java.io.Serializable;

public class Arrays implements Serializable{
	/** The instances */
	private Instances m_Instances;

	/**
	 * Sets the instances comprising the current neighbourhood.
	 * 
	 * @param insts -
	 *            The set of instances on which the nearest neighbour search is
	 *            carried out. Usually this set is the training set.
	 */
	public void setInstances(Instances insts) throws Exception {
		m_Instances = insts;
	}

	/**
	 * Updates the Edit2RS to cater for the new added instance. This
	 * implementation only updates the ranges of the Similarity class, since our
	 * set of instances is passed by reference and should already have the newly
	 * added instance.
	 * 
	 * @param ins -
	 *            The instance to add. Usually this is the instance that is
	 *            added to our neighbourhood i.e. the training instances.
	 */
	public void update(Instance ins) throws Exception {
		if (m_Instances == null)
			throw new Exception(
					"No instances supplied yet. Cannot update without"
							+ "supplying a set of instances first.");
		// m_DistanceFunction.update(ins);
	}

	/**
	 * Adds the given instance info. This implementation updates the range
	 * datastructures of the EuclideanDistance.
	 * 
	 * @param ins -
	 *            The instance to add the information of. Usually this is the
	 *            test instance supplied to update the range of attributes in
	 *            the distance function.
	 */
	public void addInstanceInfo(Instance ins) {
		if (m_Instances != null)
			try {
				update(ins);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}

	/**
	 * Returns a string describing this nearest neighbour search algorithm.
	 * 
	 * @return a description of the algorithm for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Class implementing the brute force search algorithm for nearest "
				+ "neighbour search.";
	}

	public double Max(int index) {
		Instance arr1;
		arr1 = m_Instances.instance(0);
		double max = arr1.value(index);
		for (int i = 1; i < m_Instances.numInstances(); i++) {
			Instance currenti = m_Instances.instance(i);
			if (max < currenti.value(index)) {
				max = currenti.value(index);
			}
		}
		return max;
	}

	public double Min(int index) {
		Instance arr1;
		arr1 = m_Instances.instance(0);
		double min = arr1.value(index);
		for (int i = 1; i < m_Instances.numInstances(); i++) {
			Instance currenti = m_Instances.instance(i);
			if (min > currenti.value(index)) {
				min = currenti.value(index);
			}
		}
		return min;
	}

	public int[][] AjustFilas(int[][] arr) {
		int[][] orig = (int[][]) arr.clone();
		int g = orig.length;
		arr = new int[g + 1][];
		int p = arr.length;
		arr[p - 1] = new int[0];
		for (int i = 0; i < orig.length; i++) {
			int k = orig[i].length;
			arr[i] = new int[k];
			for (int j = 0; j < k; j++) {
				int h = orig[i][j];
				arr[i][j] = h;
			}
		}
		return arr;
	}

	public int[][] AjustColumnas(int[][] arr) {

		int[][] orig = (int[][]) arr.clone();
		int fila = orig.length - 1;
		int col = orig[fila].length;
		arr[fila] = new int[col + 1];
		for (int i = 0; i < orig.length; i++) {
			for (int j = 0; j < orig[i].length; j++) {
				int h = orig[i][j];
				arr[i][j] = h;
			}
		}
		return arr;
	}

	public int[] AjustVector(int[] arr) {
		int[] orig = (int[]) arr.clone();
		arr = new int[orig.length + 1];
		for (int i = 0; i < orig.length; i++) {
			arr[i] = orig[i];
		}
		return arr;
	}

	public double[] AjustVector(double[] arr) {
		double[] orig = (double[]) arr.clone();
		arr = new double[orig.length + 1];
		for (int i = 0; i < orig.length; i++) {
			arr[i] = orig[i];
		}
		return arr;
	}

	public double[][] AjustFilasD(double[][] arr) {
		double[][] orig = (double[][]) arr.clone();
		int g = orig.length;
		arr = new double[g + 1][];
		int p = arr.length;
		arr[p - 1] = new double[0];
		for (int i = 0; i < orig.length; i++) {
			int k = orig[i].length;
			arr[i] = new double[k];
			for (int j = 0; j < k; j++) {
				double h = orig[i][j];
				arr[i][j] = h;
			}
		}
		return arr;
	}

	public double[][] AjustColumnasD(double[][] arr) {
		double[][] orig = (double[][]) arr.clone();
		int fila = orig.length - 1;
		int col = orig[fila].length;
		arr[fila] = new double[col + 1];
		for (int i = 0; i < orig.length; i++) {
			for (int j = 0; j < orig[i].length; j++) {
				double h = orig[i][j];
				arr[i][j] = h;
			}
		}
		return arr;
	}

	public double[] InsertVector(double[] arr, double value, int j) {
		double[] orig = (double[]) arr.clone();
		arr = new double[orig.length + 1];
		if (j == 0) {
			arr[0] = value;
		} else {
			for (int i = 0; i < j; i++) {
				arr[i] = orig[i];
				arr[i + 1] = value;
			}
		}
		if (arr.length > j + 1) {
			for (int h = j + 1; h <= orig.length; h++) {
				arr[h] = orig[h - 1];
			}
		}
		return arr;
	}

	public int[] InsertVector(int[] arr, int value, int j) {
		int[] orig = (int[]) arr.clone();
		;
		arr = new int[orig.length + 1];
		if (j == 0) {
			arr[0] = value;
		} else {
			for (int i = 0; i < j; i++) {
				arr[i] = orig[i];
				arr[i + 1] = value;
			}
		}
		if (arr.length > j + 1) {
			for (int h = j + 1; h <= orig.length; h++) {
				arr[h] = orig[h - 1];
			}
		}
		return arr;
	}

	public int[] DeleteVector(int[] arr, int j) {
		int[] orig = (int[]) arr.clone();
		;
		arr = new int[j];
		for (int i = 0; i < j; i++) {
			arr[i] = orig[i];
		}

		return arr;
	}

}
