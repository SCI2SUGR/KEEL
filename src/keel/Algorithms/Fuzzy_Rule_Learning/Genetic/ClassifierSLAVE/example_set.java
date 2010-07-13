/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

import org.core.*;

public class example_set {
/**
 * <p>
 * Encodes a set of examples (including information about if the example is covered by a rule, the coverage degree, ...)
 * </p>
 */

    public static final double MISSING = -999999999;
    int n_example;
    int n_variable;
    double[][] data;
    boolean[] covered;
    double[] gcovered;
    int n_partition;
    int[] partition;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    example_set() {
        n_example = 0;
        n_variable = 0;
        data = new double[0][0];
        covered = new boolean[0];
        gcovered = new double[0];
        n_partition = 0;
        partition = new int[0];
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param ejemplos int The number of examples of the problem
     * @param variables int The number of variables of the problem
     */
    example_set(int ejemplos, int variables) {
        if (ejemplos <= 0 || variables <= 0) {
            System.out.println("Negative values are not addmited");
        } else {
            n_example = ejemplos;
            n_variable = variables;
            n_partition = 1;
            covered = new boolean[n_example];
            gcovered = new double[n_example];
            data = new double[n_example][];
            partition = new int[n_example];
            for (int i = 0; i < n_example; i++) {
                data[i] = new double[variables];
                covered[i] = false;
                gcovered[i] = 0;
                partition[i] = 0;
            }
        }
    }

    /**
     * <p>
     * Creates a example_set as a copy of another set of examples
     * </p>
     * @param x example_set The set of examples used to create the new one
     */
    example_set(example_set x) {
        this.n_example = x.n_example;
        this.n_variable = x.n_variable;
        this.n_partition = x.n_partition;
        this.covered = new boolean[n_example];
        this.gcovered = new double[n_example];
        this.data = new double[n_example][];
        this.partition = new int[n_example];
        for (int i = 0; i < n_example; i++) {
            this.data[i] = new double[n_variable];
            this.covered[i] = x.covered[i];
            this.gcovered[i] = x.gcovered[i];
            this.partition[i] = x.partition[i];
        }

        for (int i = 0; i < n_example; i++) {
            for (int j = 0; j < n_variable; j++) {
                this.data[i][j] = x.data[i][j];
            }
        }
    }

    /**
     * <p>
     * Creates a example_set using the information in the data set
     * </p>
     * @param dataset myDataset The set of examples (training and test)
     */
    example_set(myDataset dataset) {
        double[] aux;

        n_example = dataset.getnData();
        n_variable = dataset.getnVars();
        n_partition = 1;
        covered = new boolean[n_example];
        gcovered = new double[n_example];
        data = new double[n_example][];
        partition = new int[n_example];
        for (int i = 0; i < n_example; i++) {
            data[i] = new double[n_variable];
            covered[i] = false;
            gcovered[i] = 0;
            partition[i] = 0;
        }

        aux = new double[n_variable];
        for (int i = 0; i < n_example; i++) {
            aux = dataset.getExample(i);
            for (int j = 0; j < n_variable - 1; j++) {
                data[i][j] = aux[j];
            }
            data[i][n_variable - 1] = dataset.getOutputAsInteger(i);
            /*            for (int j = 0; j < n_variable; j++) {
                            System.out.print(" " + data[i][j] + ",");
                        }
                        System.out.println("");*/
        }
    }


    private void Realojar(int new_examples) {
        int final_n_examples = n_example + new_examples;

        int[] aux_partition = new int[n_example];
        double[][] aux_data = new double[n_example][];
        boolean[] aux_covered = new boolean[n_example];
        double[] aux_gcovered = new double[n_example];

        for (int i = 0; i < n_example; i++) {
            aux_data[i] = new double[n_variable];
            for (int j = 0; j < n_variable; j++) {
                aux_data[i][j] = data[i][j];
            }
            aux_covered[i] = covered[i];
            aux_gcovered[i] = gcovered[i];
            aux_partition[i] = partition[i];
        }

        partition = new int[final_n_examples];
        data = new double[final_n_examples][];
        covered = new boolean[final_n_examples];
        gcovered = new double[final_n_examples];

        for (int i = 0; i < n_example; i++) {
            data[i] = new double[n_variable];
            for (int j = 0; j < n_variable; j++) {
                data[i][j] = aux_data[i][j];
            }
            covered[i] = aux_covered[i];
            gcovered[i] = aux_gcovered[i];
            partition[i] = aux_partition[i];
        }

        n_example = final_n_examples;
        n_partition++;
    }


	/**
	 * <p>
	 * Add examples the test partition to the set of examples, and mark then as test examples
	 * </p>
	 * @param dataset myDataset The test data set
	 */
    void AddPartitionTest(myDataset dataset) {
        int new_example;
        int new_var;
        int old_n_example;
        double[] aux;

        new_example = dataset.getnData();
        new_var = dataset.getnVars();
        if (new_var != n_variable && n_variable != 0) {
            System.out.println("Different number of variables");
//          exit(-1);
        }

        n_variable = new_var;
        old_n_example = n_example;
        Realojar(new_example);
        for (int i = old_n_example; i < n_example; i++) {
            data[i] = new double[n_variable];
            covered[i] = false;
            gcovered[i] = 0;
            partition[i] = 1;
        }

        aux = new double[n_variable];
        int k = 0;
        for (int i = old_n_example; i < n_example; i++) {
            aux = dataset.getExample(k);
            for (int j = 0; j < n_variable - 1; j++) {
                data[i][j] = aux[j];
            }
            data[i][n_variable - 1] = dataset.getOutputAsInteger(k);
            k++;
            /*            for (int j = 0; j < n_variable; j++) {
                            System.out.print(" " + data[i][j] + ",");
                        }
                        System.out.println("");*/
        }
    }


	/**
	 * <p>
	 * Returns the value for variable "variable" in the example in position "ejemplo" in the set of examples
	 * </p>
	 * @param ejemplo int The position of the example in the set of examples
	 * @param variable int The variable of the example
	 * @return double The value for the variable "variable" in the example "ejemplo"
	 */
    public double Data(int ejemplo, int variable) {
        return data[ejemplo][variable];
    }

	/**
	 * <p>
	 * Returns the values for all the variables in the example in position "ejemplo" in the set of examples
	 * </p>
	 * @param ejemplo int The position of the example in the set of examples
	 * @return vectordouble The values for all the variables in the example "ejemplo"
	 */
    public vectordouble Data(int ejemplo) {
        vectordouble x = new vectordouble();

        x.Put(data[ejemplo], n_variable);
        return x;
    }


	/**
	 * <p>
	 * Returns the number of examples in the set
	 * </p>
	 * @return int The number of examples in the set
	 */
    public int N_Examples() {
        return n_example;
    }

	/**
	 * <p>
	 * Returns the number of partitions forming the set of examples
	 * </p>
	 * @return int The number of partitions forming the set of examples
	 */
    public int N_Partitions() {
        return n_partition;
    }

	/**
	 * <p>
	 * Generates "num_partition" partitions using the examples in the set
	 * </p>
	 * @param num_partition int The number of partitions to be generated (including the test one)
	 */
    void Generate_Partitions(int num_partition) {
        if (num_partition < 1) {
            System.out.println("Illegal number of partitions");
            //exit(1);
        }

        n_partition = num_partition;

        if (n_partition == 1) {
            for (int i = 0; i < n_example; i++) {
                partition[i] = 1;
            }
        } else {
            for (int i = 0; i < n_example; i++) {
                partition[i] = Randomize.RandintClosed(1, num_partition);
            }
        }
    }


	/**
	 * <p>
	 * Returns if the example in position "ejemplo" belongs to the partition "particion"
	 * </p>
	 * @param ejemplo int The position of the example
	 * @param particion int The number of partition
	 * @return boolean TRUE if the example in position "ejemplo" belongs to the partition "particion". FALSE otherwise
	 */
    public boolean Is_Training_Example(int ejemplo, int particion) {
        return (partition[ejemplo] != particion);
    }

	/**
	 * <p>
	 * Returns if the example in position "ejemplo" belongs to the partition "particion"
	 * </p>
	 * @param ejemplo int The position of the example
	 * @param particion int The number of partition
	 * @return boolean TRUE if the example in position "ejemplo" belongs to the partition "particion". FALSE otherwise
	 */
    public boolean Is_Test_Example(int ejemplo, int particion) {
        return (partition[ejemplo] == particion);
    }


	/**
	 * <p>
	 * Returns the number of examples belonging to the partition "particion"
	 * </p>
	 * @param particion int The number of partition
	 * @return int The number of examples belonging to the partition "particion"
	 */
    public int Training_Example(int particion) {
        int sum = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] != particion) {
                sum++;
            }
        }

        return sum;
    }

	/**
	 * <p>
	 * Returns the number of examples belonging to the partition "particion"
	 * </p>
	 * @param particion int The number of partition
	 * @return int The number of examples belonging to the partition "particion"
	 */
    public int Test_Example(int particion) {
        int sum = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] == particion) {
                sum++;
            }
        }

        return sum;
    }


	/**
	 * <p>
	 * Returns the number of examples not covered belonging to the partition "particion"
	 * </p>
	 * @param particion int The number of partition
	 * @return int The number of examples not covered belonging to the partition "particion"
	 */
    public int Not_Covered_Training_Example(int particion) {
        int sum = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] != particion && !covered[i]) {
                sum++;
            }
        }

        return sum;
    }


	/**
	 * <p>
	 * Returns the number of examples not covered
	 * </p>
	 * @return int The number of examples not covered
	 */
    public int Not_Covered_Examples() {
        int numero = 0;
        for (int i = 0; i < n_example; i++) {
            //cout << "--> " << i << "\t" << gcovered[i]<< endl;
            if (gcovered[i] <= 0) {
                numero++;
            }
        }

        return numero;
    }


	/**
	 * <p>
	 * Calculates the number of examples per class in the problem
	 * </p>
	 * @param VarClass int The number of classes in the rule set
	 * @param particion int The partition number
	 * @param nclasses int The number of clasess in the problem
	 * @param n_examples_in_class int[] Keeps the number of examples per class
	 */
    public void Examples_per_Class(int VarClass, int particion, int nclasses,
                                   int[] n_examples_in_class) {
        for (int i = 0; i < nclasses; i++) {
            n_examples_in_class[i] = 0;
        }

// cout << "a:" <<endl;
//
// for (int i = 0; i< n_example; i++) {
// 	for (int i = 0; i< n_variable; i++)
// 		cout << i << ' ';
// 	cout << endl;
// }

        int k;
        for (int i = 0; i < n_example; i++) {
            if ( /*partition[i]!=particion &&*/(gcovered[i] <= 0)) {
                k = (int) (data[i][VarClass]);
                n_examples_in_class[k]++;
            }
        }

    }

	/**
	 * <p>
	 * Returns the number of variables of the problem
	 * </p>
	 * @return int The number of variables of the problem
	 */
    public int N_Variables() {
        return n_variable;
    }


	/**
	 * <p>
	 * Removes the examples in "v" from the set of examples
	 * </p>
	 * @param v int[] Positions of the examples to be removed from the set of examples
	 * @param tama int Number of examples to be removed
	 */
    public void Remove(int[] v, int tama) {
        int n = n_example;

        for (int i = 0; i < tama; i++) {
            if (data[v[i]] != null) {
                data[v[i]] = null;
                n--;
            }
        }

        int j = n;
        for (int i = 0; i < n; i++) {
            if (data[i] == null) {
                while (j < n_example && data[j] == null) {
                    j++;
                }
                data[i] = new double[n_variable];
                for (int k = 0; k < n_variable; k++) {
                    data[i][k] = data[j][k];
                }
                data[j] = null;
            }
        }

        n_example = n;

    }


	/**
	 * <p>
	 * Marks the examples in "v" as covered and set their coverage degree to 1
	 * </p>
	 * @param v int[] Positions of the examples to be marked in the set of examples
	 * @param tama int Number of examples to be marked
	 */
    public void Mark(int[] v, int tama) {

        for (int i = 0; i < tama; i++) {
            covered[v[i]] = true;
            gcovered[v[i]] = 1;
        }
    }

	/**
	 * <p>
	 * Marks the examples in "v" as covered and set their coverage degree to the one in "grado"
	 * </p>
	 * @param v int[] Positions of the examples to be marked in the set of examples
	 * @param tama int Number of examples to be marked
	 * @param grado double[] The coverage degrees for the examples to be marked
	 */
    public void Mark(int[] v, int tama, double[] grado) {

        for (int i = 0; i < tama; i++) {
            covered[v[i]] = true;
            gcovered[v[i]] = grado[i];
        }
    }


	/**
	 * <p>
	 * Marks the examples in "v" as uncovered and set their coverage degree to -1
	 * </p>
	 * @param v int[] Positions of the examples to be marked in the set of examples
	 * @param tama int Number of examples to be marked
	 */
    public void UnMark(int[] v, int tama) {

        for (int i = 0; i < tama; i++) {
            covered[v[i]] = false;
            gcovered[v[i]] = -1;
        }
    }


	/**
	 * <p>
	 * Marks the examples in "v" as uncovered and set their coverage degree to the negative value for the one in "grado"
	 * </p>
	 * @param v int[] Positions of the examples to be marked in the set of examples
	 * @param tama int Number of examples to be marked
	 * @param grado double[] The coverage degrees for the examples to be marked
	 */
    public void UnMark(int[] v, int tama, double[] grado) {

        for (int i = 0; i < tama; i++) {
            covered[v[i]] = false;
            gcovered[v[i]] = -grado[i];
        }
    }


	/**
	 * <p>
	 * Marks all the examples as uncovered and set their coverage degree to 0
	 * </p>
	 */
    public void UnMarkAll() {
        for (int i = 0; i < n_example; i++) {
            covered[i] = false;
            gcovered[i] = 0;
        }
    }

	/**
	 * <p>
	 * Returns if the example in position "i" is covered
	 * </p>
	 * @param i int The position of the example in the set
	 * @return boolean TRUE if the example in position "i" is covered. FALSE otherwise
	 */
    public boolean Is_Covered(int i) {
        return covered[i];
    }


	/**
	 * <p>
	 * Returns the coverage degree for the example in position "i" of the set
	 * </p>
	 * @param i int The position of the example in the set
	 * @return double The coverage degree
	 */
    public double CoverageDegree(int i) {
        return gcovered[i];
    }


	/**
	 * <p>
	 * Returns the subset of examples belonging to the partition "particion"
	 * </p>
	 * @param particion int The number of partition
	 * @return example_set The subset of examples
	 */
    public example_set Extract_Training_Set(int particion) {
        int sum = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] != particion) {
                sum++;
            }
        }

        System.out.println("examples: " + sum + "  variables: " + n_variable);
        example_set E1 = new example_set(sum, n_variable);

        int l = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] != particion) {
                for (int j = 0; j < n_variable; j++) {
                    E1.data[l][j] = data[i][j];
                }
                l++;
            }
        }

        return E1;
    }

	/**
	 * <p>
	 * Returns the subset of examples belonging to the partition "particion"
	 * </p>
	 * @param particion int The number of partition
	 * @return example_set The subset of examples
	 */
    public example_set Extract_Test_Set(int particion) {
        int sum = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] == particion) {
                sum++;
            }
        }

        example_set E1 = new example_set(sum, n_variable);

        int l = 0;
        for (int i = 0; i < n_example; i++) {
            if (partition[i] == particion) {
                for (int j = 0; j < n_variable; j++) {
                    E1.data[l][j] = data[i][j];
                }
                l++;
            }
        }

        return E1;
    }

}

