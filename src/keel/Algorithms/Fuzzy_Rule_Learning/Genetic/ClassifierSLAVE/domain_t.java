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
 
public class domain_t {
/**
 * <p>
 * Defines the domain of a variable, using trapezoidal fuzzy sets
 * </p>
 */
	
    int numero;
    double rango_inf, rango_sup;
    boolean inf_inf, sup_inf;
    fuzzy_t[] label;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public domain_t() {
        numero = 0;
        rango_inf = rango_sup = 0;
        inf_inf = sup_inf = false;
        label = new fuzzy_t[0];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param n int The number of label to be used in the variable
     * @param inf double The lower value for the variable range
     * @param inf double The upper value for the variable range     
     * @param menosinf boolean TRUE if the negative extreme is delimited. FALSE otherwise
     * @param masinf boolean TRUE if the positive extreme is delimited. FALSE otherwise     
     */
    public domain_t(int n, double inf, double sup, boolean menosinf,
                    boolean masinf) {
        double ancho;

        numero = n;
        rango_inf = inf;
        rango_sup = sup;
        inf_inf = menosinf;
        sup_inf = masinf;
        label = new fuzzy_t[n];

        for (int j = 0; j < n; j++) {
            label[j] = new fuzzy_t();
        }

        /* Define the labels in the domain */
        ancho = (sup - inf) / (n - 1);
        String nom_label;
        for (int i = 1; i < n - 1; i++) {
            nom_label = "L" + i;
            label[i].Set(rango_inf + (ancho * (i - 1)),
                            rango_inf + (ancho * i),
                            rango_inf + (ancho * i),
                            rango_inf + (ancho * (i + 1)), nom_label,
                            false, false);
        }
        label[0].Set(rango_inf, rango_inf, rango_inf, rango_inf + ancho,
                        "L0", menosinf,
                        false);
        nom_label = "L" + (n - 1);

        label[n -
                1].Set(rango_sup - ancho, rango_sup, rango_sup, rango_sup,
                          nom_label, false,
                          masinf);
    }


    /**
     * <p>
     * Creates a domain_t object as a copy of "x"
     * </p>
     * @param x domain_t The object used to created the new one
     */
    public domain_t(domain_t x) {
        this.numero = x.numero;
        this.rango_inf = x.rango_inf;
        this.rango_sup = x.rango_sup;
        this.inf_inf = x.inf_inf;
        this.sup_inf = x.sup_inf;
        this.label = new fuzzy_t[numero];
        for (int i = 0; i < numero; i++) {
            this.label[i] = new fuzzy_t(x.label[i]);
        }
    }


	/**
	 * <p>
	 * Creates an uniform domain with n labels from inf to sup, cutting in 0.5
	 * </p>
         * @param n int The number of label to be used in the variable
         * @param inf double The lower value for the variable range
         * @param sup double The upper value for the variable range     
         * @param menosinf boolean TRUE if the negative extreme is delimited. FALSE otherwise
         * @param masinf boolean TRUE if the positive extreme is delimited. FALSE otherwise    	 
	 */
    public void Set(int n, double inf, double sup, boolean menosinf,
                       boolean masinf) {
        double ancho;

        numero = n;
        rango_inf = inf;
        rango_sup = sup;
        inf_inf = menosinf;
        sup_inf = masinf;
        label = new fuzzy_t[n];

        for (int j = 0; j < n; j++) {
            label[j] = new fuzzy_t();
        }

        /* Define the labels in the domain */
        ancho = (sup - inf) / (n - 1);
        String nom_label;
        for (int i = 1; i < n - 1; i++) {
            nom_label = "L" + i;
            label[i].Set(rango_inf + (ancho * (i - 1)),
                            rango_inf + (ancho * i),
                            rango_inf + (ancho * i),
                            rango_inf + (ancho * (i + 1)), nom_label,
                            false, false);

        }
        label[0].Set(rango_inf, rango_inf, rango_inf, rango_inf + ancho,
                        "L0", menosinf,
                        false);
        nom_label = "L" + (n - 1);

        label[n -
                1].Set(rango_sup - ancho, rango_sup, rango_sup, rango_sup,
                          nom_label, false,
                          masinf);
    }


	/**
	 * <p>
	 * Creates a domain with n labels in [inf,sup]. The definition of each label
	 * is given by 4 vectors a, b, c and d, where
	 * [a[i],b[i],c[i],d[i]] represents the label number i in the domain.
	 * The vector name keeps the name of each label.
	 * </p>
         * @param n int The number of label to be used in the variable
         * @param inf double The lower value for the variable range
         * @param inf double The upper value for the variable range    
         * @param a double[] Parameter a for each of the trapezoidal fuzzy set    
         * @param b double[] Parameter b for each of the trapezoidal fuzzy set    
         * @param c double[] Parameter c for each of the trapezoidal fuzzy set    
         * @param d double[] Parameter d for each of the trapezoidal fuzzy set    
         * @param name String[] The name for each of the trapezoidal fuzzy set                                         
	 */
    public void Set(int n, double inf, double sup, double[] a, double[] b,
                       double[] c, double[] d, String[] name) {
        numero = n;
        rango_inf = inf;
        rango_sup = sup;
        inf_inf = true;
        sup_inf = true;
        label = new fuzzy_t[numero];

        for (int j = 0; j < n; j++) {
            label[j] = new fuzzy_t();
        }

        for (int i = 0; i < numero; i++) {
            label[i].Set(a[i], b[i], c[i], d[i], name[i]);
        }
    }


	/**
	 * <p>
	 * Returns the number of labels in the domain
	 * </p>
	 * @return int the number of labels in the domain
	 */
    public int N_labels() {
        return numero;
    }


	/**
	 * <p>
	 * Returns the number of labels in the domain
	 * </p>
	 * @return int the number of labels in the domain
	 */
    public int Size() {
        return numero;
    }


	/**
	 * <p>
	 * Returns the adaptation degree of a value x to the domain.
	 * </p>
	 * @param x double The value
	 * @return double the adaptation degree of a value x to the domain.
	 */
    public double Adaptation(double x) {
        double mayor, nuevo;
        if (numero == 0) {
            System.out.println("The domain is not created");
            return ( -1);
            //exit(1);
        }

        mayor = label[0].Adaptation(x);
        for (int i = 1; i < numero; i++) {
            nuevo = label[i].Adaptation(x);
            if (nuevo > mayor) {
                mayor = nuevo;
            }
        }
        return mayor;
    }



	/**
	 * <p>
	 * Returns the adaptation degree of a value x to a specific label in the domain (this label is
	 * given by its position in the domain).
	 * </p>
	 * @param x double The value
	 * @param etiqueta int The label in the domain	 
	 * @return double the adaptation degree of a value x to the domain.
	 */
    public double Adaptation(double x, int etiqueta) {
        if (numero == 0) {
            System.out.println("The domain is not created");
            return ( -1);
            //exit(1);
        }

        if (etiqueta >= numero) {
            System.out.println("That label is not in the domain");
            return ( -1);
            //exit(1);
        }

        return label[etiqueta].Adaptation(x) / Adaptation(x);
    }


	/**
	 * <p>
	 * Returns the adaptation degree of a value x to a set of label in the domain.
	 * The labels are given as an ordered vector of zeros and ones. Zero means absence and
	 * one represents the presence of the label.
	 * </p>
	 * @param x double The value
	 * @param etiquetas String The set of labels in the domain 
	 * @return double the adaptation degree of a value x to the domain.
	 */
    public double Adaptation(double x, String etiquetas) {
        double mayor, nuevo;
        char[] array_etiquetas = new char[etiquetas.length()];

        array_etiquetas = etiquetas.toCharArray();

        if (numero == 0) {
            System.out.println("The domain is not created");
            return ( -1);
            //exit(1);
        }

        mayor = etiquetas.length();
        if (mayor > numero) {
            System.out.println("That label is not in the domain");
            return ( -1);
            //exit(1);
        }

        mayor = 0;
        for (int i = 0; i < numero; i++) {
            if (array_etiquetas[i] == '1') {
                nuevo = label[i].Adaptation(x);
                if (nuevo > mayor) {
                    mayor = nuevo;
                }
            }
        }

        return mayor / Adaptation(x);
    }


	/**
	 * <p>
	 * Prints in a String the definition of each label in the domain
	 * </p>
	 * @return String The definition of each label in the domain
	 */
    public String PrintDefinitionToString() {
        String cadena = "";

        for (int i = 0; i < numero; i++) {
            cadena += label[i].PrintDefinitionToString();
        }

        return(cadena);
    }


	/**
	 * <p>
	 * Prints in the standard output the definition of the domain
	 * </p>
	 */
    public void PrintDefinition() {
        for (int i = 0; i < numero; i++) {
            label[i].PrintDefinition();
        }
    }

	/**
	 * <p>
	 * Prints in the standard output the label number i in the domain
	 * </p>
	 * @param i int The label's number
	 */
    public void PrintDefinition(int i) {
        label[i].PrintDefinition();
    }


	/**
	 * <p>
	 * Prints in the standard output the name of the label number i in the domain
	 * </p>
	 * @param i int The label's number
	 */
    public void Print(int i) {
        label[i].Print();
    }

	/**
	 * <p>
	 * Prints as a String the name of the label number i in the domain
	 * </p>
	 * @param i int The label's number
	 */
    public String SPrint(int i) {
        return label[i].SPrint();
    }


	/**
	 * <p>
	 * Returns the fuzzy label number i in the domain
	 * </p>
	 * @param i int The label's number
	 * @return fuzzy_t The fuzzy label
	 */
    public fuzzy_t FuzzyLabel(int i) {
        fuzzy_t aux;
        aux = label[i].FuzzyLabel();
        return aux;
    }


	/**
	 * <p>
	 * Returns the central value of the label number i in the domain
	 * </p>
	 * @param i int The label's number
	 * @return double The central value of the label
	 */	 
    public double CenterLabel(int i) {
        return label[i].CenterLabel();
    }


	/**
	 * <p>
	 * Returns if the <domain_t> object is formed by labels with all their domain been crisp.
	 * </p>
	 * @return boolean TRUE the <domain_t> object is formed by labels with all their domain been crisp. FALSE otherwise
	 */
    public boolean IsDiscrete() {
        int i = 0;
        while (i < numero && label[i].IsDiscrete()) {
            i++;
        }

        return (i == numero);
    }


	/**
	 * <p>
	 * Returns if the <domain_t> object is formed by labels with all their domain been intervals.
	 * </p>
	 * @return boolean TRUE if the <domain_t> object is formed by labels with all their domain been intervals. FALSE otherwise
	 */
    public boolean IsInterval() {
        int i = 0;
        while (i < numero && label[i].IsInterval()) {
            i++;
        }

        return (i == numero);
    }


	/**
	 * <p>
	 * Returns if the <domain_t> object is formed by labels with all their domain been fuzzy.
	 * </p>
	 * @return boolean TRUE if the <domain_t> object is formed by labels with all their domain been fuzzy. FALSE otherwise
	 */
    public boolean IsFuzzy() {
        int i = 0;
        while (i < numero && label[i].IsFuzzy()) {
            i++;
        }

        return (i == numero);
    }


	/**
	 * <p>
	 * Returns the area of the label number l in the domain
	 * </p>
	 * @param l int The label's number
	 * @return double The area of the label in the domain
	 */
    public double Area(int l) {
        return label[l].Area();
    }


	/**
	 * <p>
	 * Returns an <domain_t> object with the domain
	 * </p>
	 * @return domain_t A <domain_t> object with the domain
	 */
    public domain_t Domain() {
        domain_t aux = new domain_t(this);

        return aux;
    }


	/**
	 * <p>
	 * Returns the lower value for all the labels in the domain.
	 * </p>
	 * @return double the lower value for all the labels in the domain.
	 */
    public double Inf_Range() {
        return rango_inf;
    }

	
	/**
	 * <p>
	 * Retuns the upper value for all the labels in the domain.
	 * </p>
	 * @return double the upper value for all the labels in the domain.
	 */	 
    public double Sup_Range() {
        return rango_sup;
    }

}

