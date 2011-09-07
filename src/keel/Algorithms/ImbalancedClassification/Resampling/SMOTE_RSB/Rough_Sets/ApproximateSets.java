package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;

import java.io.Serializable;

public class ApproximateSets implements Serializable {
	/** The instances */
	private Instances m_Instances;
	protected Arrays m_Arrays = new Arrays();
	/** Aprox Inferior */
	public int[][] inferior = new int[0][0];
	/** Aprox Superior */
	public int[][] superior = new int[0][0];

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

	public int[][] getInf() {
		return inferior;
	}

	public void setInf(int[][] newInf) {
		inferior = newInf;
	}

	public int[][] getSup() {
		return superior;
	}

	public void setSup(int[][] newSup) {
		superior = newSup;
	}

	public void CalAproxInfSup(int value1, double value2) throws Exception {
		int i, j;
		int size = m_Instances.numInstances();
		double[][] matriz = new double[size][size];
		int[][] arrclases, equivalencia;
		m_Arrays.setInstances(m_Instances);
		arrclases = byClasses();// SetArrClases();
		for (i = 0; i < size; i++) {
			// matriz=m_Arrays.AjustFilasD(matriz);
			for (j = 0; j < size; j++) {
				Instance currenti = m_Instances.instance(i);
				// matriz=m_Arrays.AjustColumnasD(matriz);
				matriz[i][j] = CompRasgos(currenti, j);
			}
		}
		equivalencia = RelSimil(matriz, value1, value2);// esto devuelve Ok
		inferior = new int[0][0];
		superior = new int[0][0];
		inferior = InfBound(equivalencia, arrclases);
		superior = SupBound(equivalencia, arrclases);

	}

	/** Funcion para la comparacion de los rasgos */
	public double CompRasgos(Instance arr1, int indice) {
		double sumatoria = 0;
		double s, resultado = 0;
		double at = arr1.numAttributes() - 1;
		double peso = ((1.0) / (at));
		int i;
		Instance arr2;
		// Attribute rasgo;
		arr2 = m_Instances.instance(indice);
		for (i = 0; i < arr2.numAttributes() - 1; i++) {
			Attribute current2 = arr2.attribute(i);
			if (current2.isReal()) {
				// Aqui calculo los max y min
				double i1, i2;
				i1 = arr1.value(i);
				i2 = arr2.value(i);
				try {
					m_Arrays.setInstances(m_Instances);
				} catch (Exception e) {
					e.printStackTrace();
				}
				s = m_Arrays.Max(i) - m_Arrays.Min(i);
				if (s != 0) {
					resultado = 1 - ((Math.abs(i1 - i2)) / s);
					sumatoria = sumatoria + peso * resultado;
				} else {
					sumatoria = sumatoria + peso;
				}

			} else if ((current2.isString()) || (current2.isNominal())) {
				//System.out.println("valor1: " + arr1.stringValue(i)+ "; valor2: " + arr2.stringValue(i));
				if (arr1.stringValue(i).equalsIgnoreCase(arr2.stringValue(i))) {
					resultado = 1;
					sumatoria = sumatoria + peso * resultado;
				} else {
					resultado = 0;
				}
			}
		}
		return sumatoria;

	}

	/** Similaridad */
	public int[][] RelSimil(double[][] m, int signo, double valor) {
		int i, j, k;
		int[][] similaridad = new int[0][0];
		for (i = 0; i < m.length; i++) {
			if ((similaridad.length == 0) || (similaridad[i - 1].length > 0)) {
				similaridad = m_Arrays.AjustFilas(similaridad);
			} else {
				similaridad[i - 1] = new int[1];
				similaridad[i - 1][0] = -1;
				similaridad = m_Arrays.AjustFilas(similaridad);
			}
			k = 0;
			for (j = 0; j < m[i].length; j++) {
				if (signo == 1) {
					if (m[i][j] >= valor) {
						similaridad = m_Arrays.AjustColumnas(similaridad);
						similaridad[i][k] = j;
						k++;
					}
				} else {
					if (m[i][j] <= valor) {
						similaridad = m_Arrays.AjustColumnas(similaridad);
						similaridad[i][k] = j;
						k++;
					}
				}
			}
		}
		return similaridad;
	}

	public int[] Union(int[][] arr) {
		int[] cadena = new int[0];
		int i, j, d;
		int[] solucion;
		d = 0;
		for (i = 0; i < arr.length; i++) {
			if (arr[i].length == 0) {
				i++;
			} else {
				for (j = 0; j < arr[i].length; j++) {
					cadena = m_Arrays.AjustVector(cadena);
					cadena[d] = arr[i][j];
					d++;
				}
			}
		}
		if (cadena.length > 0) {
			solucion = new int[1];
			solucion[0] = cadena[0];
			i = 1;
			j = 0;
			while (i <= cadena.length - 1) {
				if (cadena[i] == solucion[j]) {
					i++;
					j = 0;
				} else {
					j++;
				}
				if (j > solucion.length - 1) {
					solucion = m_Arrays.AjustVector(solucion);
					int cad = cadena[i];
					int lon = solucion.length;
					solucion[lon - 1] = cad;
					j = 0;
					i++;
				}
			}
			return solucion;
		} else {
			cadena[0] = -1;
			return cadena;
		}

	}

	public int[][] InfBound(int[][] equivalencia, int[][] arrclases) {
		int i = 0;
		int j = 0;
		int k = 0;
		int d = 0;
		int l = -1;
		int[][] solucion = new int[1][0];
		while ((i < equivalencia.length) || (k + 1 < arrclases.length)) {
			if (i >= equivalencia.length) {
				l = -1;
				k++;
				solucion = m_Arrays.AjustFilas(solucion);// incremento en 1
															// las filas.
				i = 0;
			}
			// Instance
			while ((j < equivalencia[i].length) && (d < arrclases[k].length)) {
				if (equivalencia[i][j] == arrclases[k][d]) {
					j++;
					d = 0;
				} else {
					d++;
				}
			}
			if (j >= equivalencia[i].length) {
				l++;
				solucion = m_Arrays.AjustColumnas(solucion);// incremento en 1
															// las columnas.
				solucion[k][l] = i;
				i++;
				j = 0;
				d = 0;
			} else {
				i++;
				j = 0;
				d = 0;
			}
		}
		return solucion;

	}

	public int[][] SupBound(int[][] equivalencia, int[][] arrclases) {
		int i, j, k, d, l;
		boolean band;
		d = 0;
		l = -1;
		i = 0;
		j = 0;
		k = 0;
		int[][] solucion = new int[1][0];
		while ((i < equivalencia.length) || (k + 1 < arrclases.length)) {
			band = true;
			if (i >= equivalencia.length) {
				l = -1;
				k++;
				solucion = m_Arrays.AjustFilas(solucion);
				i = 0;
			}
			while ((band) && (d < arrclases[k].length)) {
				if (equivalencia[i][j] == arrclases[k][d]) {
					band = false;
					d = 0;
					l++;
					solucion = m_Arrays.AjustColumnas(solucion);
					solucion[k][l] = i;
					i++;
					j = 0;
				} else {
					d++;
				}
			}
			if (band) {
				j++;
				if (j >= equivalencia[i].length) {
					i++;
					j = 0;
				}
				d = 0;
			}
		}
		return solucion;
	}

	public int[] Diferencia(int[] arr1, int[] arr2) {
		int i, j;
		int[] solucion = new int[0];
		boolean flag;
		i = 0;
		while (i <= arr1.length - 1) {
			j = 0;
			flag = false;
			while ((j <= arr2.length - 1) && (flag == false)) {
				if (arr1[i] == arr2[j]) {
					flag = true;
				} else {
					j++;
				}
			}
			if (flag == false) {
				solucion = m_Arrays.AjustVector(solucion);
				solucion[solucion.length - 1] = arr1[i];
			}
			i++;
		}
		return solucion;
	}

	public int[][] Diferencias(int[][] arr1, int[][] arr2) {
		int i, cnt;
		int[][] diferencias = new int[0][0];
		int[] dif = new int[0];
		i = 0;
		cnt = arr1.length;
		while (i < cnt) {
			dif = Diferencia(arr1[i], arr2[i]);
			if (dif.length > 0) {
				diferencias = m_Arrays.AjustFilas(diferencias);
				int x = dif.length;
				diferencias[diferencias.length - 1] = new int[x];
				diferencias[diferencias.length - 1] = dif;// (int[])
															// dif.clone();
				// diferencias[i]=Diferencia(arr1[i],arr2[i]);

			}
			i++;
		}
		return diferencias;
	}

	/*
	 * public int[][] SetArrClases() {//OK int k , i; double temp; int[][]
	 * arrclases = new int[0][0]; k=0; arrclases =
	 * m_Arrays.AjustFilas(arrclases); Instance arr1 = m_Instances.instance(0);
	 * temp = arr1.classValue();
	 * 
	 * for (i=0;i<m_Instances.numInstances();i++){ Instance currenti =
	 * m_Instances.instance(i); if (temp == currenti.classValue()){ arrclases =
	 * m_Arrays.AjustColumnas(arrclases);//incremento un elemento en la columna
	 * int y = arrclases[k].length -1; //le doy la ultima posicion de esa fila
	 * arrclases[k][y]=i;// asigno en ese ultimo lugar el elemento
	 *  } else { temp = currenti.classValue(); arrclases =
	 * m_Arrays.AjustFilas(arrclases);//incremento un elemento en la fila k++;
	 * arrclases = m_Arrays.AjustColumnas(arrclases);//incremento un elemento en
	 * la columna int t = arrclases[k].length -1; //le doy la ultima posicion de
	 * esa fila arrclases[k][t]=i;// asigno en ese ultimo lugar el elemento } }
	 * return arrclases; }
	 */
	public int[][] byClasses() {
		int[][] ins = new int[m_Instances.numClasses()][];
		for (int i = 0; i < ins.length; i++) {
			int count = 0;
			int[] inst_class = new int[m_Instances.numInstances()];
			for (int j = 0; j < m_Instances.numInstances(); j++) {
				if (m_Instances.instance(j).classValue() == i) {
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
}
