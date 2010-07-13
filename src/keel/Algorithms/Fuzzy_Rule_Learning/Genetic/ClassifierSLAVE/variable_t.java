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
 
public class variable_t {
/**
 * <p>
 * Defines a variable of the problem
 * </p>
 */
 	
    String nombre;
    domain_t dominio;
    boolean activa, antecedente;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    variable_t() {
        dominio = new domain_t();
        nombre = "Sin asignar";
        activa = false;
        antecedente = true;
    }


    /**
     * <p>
     * Creates a variable_t object as a copy of another variable_t object
     * </p>
     * @param x variable_t The variable_t object to be copied
     */
    variable_t(variable_t x) {
        this.nombre = x.nombre;
        this.activa = x.activa;
        this.antecedente = x.antecedente;
        this.dominio = new domain_t(x.dominio);
    }


	/**
	 * <p>
	 * Defines a variable in an automatic way. The domain is uniformly defined
	 * with n labels in the rank [inf,sup]. It is posible to indicate if the domain's extremes
	 * are not delimited. "name" is the name of the variable.
	 * By defect, the variable is supposed to be an antecedent and it is considered in the
	 * learning process.
	 * </p>
	 * @param n int The number of label for the variable
	 * @param inf double The lower value for the variable range
         * @param sup double The upper value for the variable range     
         * @param menosinf boolean TRUE if the negative extreme is delimited. FALSE otherwise
         * @param masinf boolean TRUE if the positive extreme is delimited. FALSE otherwise   
         * @param name String The name of the variable
         */	 	 	 	 
    public void Set(int n, double inf, double sup, boolean menosinf,
                       boolean masinf, String name) {
        nombre = name;
        activa = true;
        antecedente = true;
        dominio = new domain_t();
        dominio.Set(n, inf, sup, menosinf, masinf);
    }


	/**
	 * <p>
	 * Defines a variable in an automatic way. USED FOR THE CONSEQUENT VARIABLE
	 * </p>
	 * @param name String The name of the variable
	 * @param n int The number of labels in the variable
         * @param nombres String[] The names of the labels in the variable
         * @param ident double[] The a, b, c and d parameters for the labels in the variable
         */	
    public void Set(String name, int n, String[] nombres, double[] ident) {
        nombre = name;
        activa = true;
        antecedente = false;
        dominio = new domain_t();
        dominio.Set(n, 0.0, 0.0, ident, ident, ident, ident, nombres);
    }


	/**
	 * <p>
	 * Defines a variable based in a domain given as an ordered secuence of labels
	 * ( [a[i],b[i],c[i],d[i]] expresses the definition of the label i)
	 * "inf" and "sup" are the lower and upper limits, respectively.
	 * "varname" is the name of the variable and "name" is a vector with the names of the labels.
	 * "status" is used to express the type of variable (-1 => antecedent variable and it is not active,
	 * 0 => antecedent variable and it is active. 1 => consequent variable and it is active).
	 * </p>
	 * @param n int The number of label for the variable
	 * @param varname String The nambe of the variable
	 * @param status int -1 => antecedent variable and it is not active, 0 => antecedent variable and it is active. 1 => consequent variable and it is active.
	 * @param inf double The lower value for the variable range
         * @param sup double The upper value for the variable range    
         * @param a double[] Parameter a for each label   
         * @param b double[] Parameter b for each label    
         * @param c double[] Parameter c for each label   
         * @param d double[] Parameter d for each label             
         * @param name String The name for each label	 
	 */	 
    public void Set(int n, String varname, int status, double inf,
                       double sup, double[] a, double[] b, double[] c,
                       double[] d, String[] name) {
        nombre = varname;
        if (status == -1) {
            activa = false;
            antecedente = true;
        } else if (status == 0) {
            activa = true;
            antecedente = true;
        } else {
            activa = true;
            antecedente = false;
        }
        dominio = new domain_t();
        dominio.Set(n, inf, sup, a, b, c, d, name);
    }


	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to the variable.
	 * </p>
	 * @param x double The value
	 * @return double the adaptation degree of a value x to the domain.	 
	 */
    public double Adaptation(double x) {
        return dominio.Adaptation(x);
    }


	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to the label "etiqueta" of the variable.
	 * </p>
	 * @param x double The value
	 * @param etiqueta int The label in the domain	 
	 * @return double the adaptation degree of a value x to the domain.	 
	 */
    public double Adaptation(double x, int etiqueta) {
        return dominio.Adaptation(x, etiqueta);
    }

	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to a set of label "etiquetas" of the variable.
	 * Param "etiquetas" is given as an ordered vector (String of strings) with zeros and ones
	 * representing the absence or the presence, respectively.
	 * </p>
	 * @param x double The value
	 * @param etiquetas String The set of labels in the domain 
	 * @return double the adaptation degree of a value x to the domain.	 
	 */
    public double Adaptation(double x, String etiquetas) {
        return dominio.Adaptation(x, etiquetas);
    }


	/**
	 * <p>
	 * Prints in a String the definition of the variable
	 * </p>
	 * @return String The definition of the variable
	 */
    public String PrintDefinitionToString() {
        String cadena = "";
        cadena += "Variable: " + nombre + "\n";
        cadena += "========================\n";
        cadena += dominio.PrintDefinitionToString();

        return(cadena);
    }


	/**
	 * <p>
	 * Prints in the standard output the definition of the variable
	 * </p>
	 */
    public void PrintDefinition() {
        System.out.println("Variable: " + nombre);
        System.out.println("========================");
        dominio.PrintDefinition();
    }


	/**
	 * <p>
	 * Prints in the standard output the name of the variable
	 * </p>
	 */
    public void PrintVar() {
        System.out.println(nombre);
    }

	/**
	 * <p>
	 * Returns a string with the name of the variable.
	 * </p>
	 * @return String The name of the variable	 
	 */
    public String SPrintVar() {
        return nombre;
    }


	/**
	 * <p>
	 * Prints in the standard ouput the name of the label "value" of the variable.
	 * </p>
	 * @param value int The label of the variable
	 */
    public void PrintDomain(int value) {
        dominio.Print(value);
    }


	/**
	 * <p>
	 * Returns a string with the name of the label "value" of the variable.
	 * </p>
	 * @param value int The label of the variable
	 * @return String The name of the label "value" of the variable.	 
	 */
    public String SPrintDomain(int value) {
        return dominio.SPrint(value);
    }


	/**
	 * <p>
	 * Retuns the number of labels in the variable's domain.
	 * </p>
	 * @return int The number of labels in the variable's domain.
	 */
    public int SizeDomain() {
        return dominio.Size();
    }

	/**
	 * <p>
	 * Returns if the variable is considered in the learning process.
	 * </p>
	 * @return boolean TRUE is the variable is considered in the learning process. FALSE otherwise
	 */
    public boolean IsActive() {
        return activa;
    }


	/**
	 * <p>
	 * Retuns if the variable is an antecedent of the rule.
	 * </p>
	 * @return boolean TRUE is the variable is an antecedent of the rule. FALSE otherwise	 
	 */
    public boolean IsAntecedent() {
        return antecedente;
    }


	/**
	 * <p>
	 * Returns the number of labels in the variable's domain.
	 * </p>
	 * @return int The number of labels in the variable's domain
	 */
    public int N_labels() {
        return dominio.N_labels();
    }


	/**
	 * <p>
	 * Returns a fuzzy_t object with the definition of the label number "i" in the variable's domain.
	 * </p>
	 * @param i int The label number
	 * @return fuzzy_t A fuzzy_t object with the definition of the label number "i" in the variable's domain.
	 */
    public fuzzy_t FuzzyLabel(int i) {
        fuzzy_t aux = new fuzzy_t(dominio.FuzzyLabel(i));
        return aux;
    }


	/**
	 * <p>
	 * Returns the central value of the label number "i" in the variable's domain.
	 * </p>
	 * @param i int The label number
	 * @return double The central value of the label number "i" in the variable's domain.
	 */
    public double CenterLabel(int i) {
        return dominio.CenterLabel(i);
    }


	/**
	 * <p>
	 * Returns if the domain associated to the variable is only formed by crisp values.
	 * </p>
	 * @return TRUE if the domain associated to the variable is only formed by crisp values. FALSE otherwise
	 */
    public boolean IsDiscrete() {
        return dominio.IsDiscrete();
    }


	/**
	 * <p>
	 * Returns if the domain associated to the variable is only formed by intervals.
	 * </p>
	 * @return TRUE if the domain associated to the variable is only formed by intervals. FALSE otherwise
	 */
    public boolean IsInterval() {
        return dominio.IsInterval();
    }


	/**
	 * <p>
	 * Returns if the domain associated to the variable is only formed by fuzzy sets.
	 * </p>
	 * @return TRUE if the domain associated to the variable is only formed by fuzzy sets. FALSE otherwise
	 */
    public boolean IsFuzzy() {
        return dominio.IsInterval();
    }


	/**
	 * <p>
	 * Returns the area of the label number "l" in the variable's domain.
	 * </p>
	 * @param l int The label number
	 * @return double The area of the label number "l" in the variable's domain.
	 */
    public double Area(int l) {
        return dominio.Area(l);
    }


	/**
	 * <p>
	 * Returns a <domain_t> object with the definition of the variable's domain
	 * </p>
	 * @return domain_t A domain_t object with the definition of the variable's domain	 
	 */
    public domain_t Domain() {
        domain_t aux = new domain_t(dominio);
        return aux;
    }


	/**
	 * <p>
	 * Returns a copy of the variable
	 * </p>
	 * @return variable_t A copy of the variable
	 */
    public variable_t Variable() {
        variable_t aux = new variable_t(this);
        return aux;
    }


	/**
	 * <p>
	 * Returns the lower value of the definition interval of variable's domain.
	 * </p>
	 * @return double The lower value of the definition interval of variable's domain.
	 */
    public double Inf_Range() {
        return dominio.Inf_Range();
    }

	/**
	 * <p>
	 * Returns the upper value of the definition interval of variable's domain.
	 * </p>
	 * @return double The upper value of the definition interval of variable's domain.
	 */
    public double Sup_Range() {
        return dominio.Sup_Range();
    }

}

