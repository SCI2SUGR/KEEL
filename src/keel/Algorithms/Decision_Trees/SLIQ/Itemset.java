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

package keel.Algorithms.Decision_Trees.SLIQ;


/**
 * Clase para manipular un conjunto de elementos
 */
public class Itemset {
	/** Conjunto de datos al que tiene acceso el conjunto de elementos. */
	protected Dataset dataset;		
	
	/** Valores del conjunto de elementos. */
    protected double[] values;	
    
    /** El peso del conjunto de elementos. */
    protected double weight;		

    /** Constante que representa valores que faltan. */
    protected final static double MISSING_VALUE = Double.NaN;	

    /** Constructor que copia los valores y el peso
     * 
     * @param itemset		El conjunto de elementos a copiar
     */
    public Itemset(Itemset itemset) {
        values = itemset.values;
        weight = itemset.weight;
        dataset = null;
    }

    /** Constructor que establece los valores y el peso.
     * 
     * @param w					El peso.
     * @param attributeValues	Los valores.
     */
    public Itemset(double w, double[] attributeValues) {
        values = attributeValues;
        weight = w;
        dataset = null;
    }

    /** Devuelve el ï¿½ndice del atributo de clase.
     * 
     */
    public int classIndex() {
        if (dataset == null) {
        	System.err.println("el dataset asociado es nulo");
            return(-1);
        } else
        	return dataset.getClassIndex();
    }

    /** Mï¿½todo que comprueba si falta el atributo de clase.
     * 
     * @return	True si falta el valor del atributo de clase.
     */
    public boolean classIsMissing() {
        if(classIndex() < 0) 
            throw new RuntimeException("No se ha establecido la clase.");
        else
        	return isMissing(classIndex());
    }

    /** Devuelve el ï¿½ndice del valor de la clase
     *
     */
    public double getClassValue() {
        if(classIndex() < 0) {
        	System.err.println("El dataset asociado es nulo");
        	return (-1);
        } else
        	return getValue(classIndex());
    }

    /** Devuelve el nï¿½mero de valores de clase.
     * 
     */
    public int numClasses() {
    	if(dataset == null) {
    		System.err.println("El dataset asociado es nulo");
    		return (-1);
    	} else
        	return dataset.numClasses();
    }

    /** Devuelve el atributo correspondiente al ï¿½ndice indicado.
     * 
     */
    public Attribute getAttribute(int index) {
        if(dataset == null) {
        	System.err.println("El dataset asociado es nulo");
        	return null;
        } else
        	return dataset.getAttribute(index);
    }

    /** Mï¿½todo para establecer un valor.
     * 
     * @param index		ï¿½ndice del atributo.
     * @param value		Valor.
     */
    public void setValue(int index, double value) {
        double[] help = new double[values.length];

        System.arraycopy(values, 0, help, 0, values.length);
        values = help;
        values[index] = value;
    }

    /** Devuelve el valor del atributo indicado.
     * 
     */
    public double getValue(int index) {
        return values[index];
    }

    /** Mï¿½todo para establecer el peso.
     * 
     * @param w		Peso.
     */
    public final void setWeight(double w) {
        weight = w;
    }

    /** Devuelve el peso asociado.
     * 
     */
    public final double getWeight() {
        return weight;
    }
    
    /** Devuelve el dataset asociado.
     * 
     */
    public Dataset getDataset() {
        return dataset;
    }

    /** Mï¿½todo para establecer el dataset.
     * 
     * @param data	El dataset.
     */
    public final void setDataset(Dataset data) {
        dataset = data;
    }

    /** Mï¿½todo para comprobar si falta un valor.
     * 
     * @param index	ï¿½ndice del atributo a comprobar.
     * 
     * @return		True si falta el valor del atributo. False en caso contrario.
     */
    public boolean isMissing(int index) {
        if(Double.isNaN(values[index])) 
        	return true;
        else
        	return false;
    }

    /** Mï¿½todo para comprobar si falta un valor dado.
     * 
     * @param val	El valor a comprobar.
     * 
     * @return		True si dicho valor falta. False en caso contrario.
     */
    public static boolean isMissingValue(double val) {
        return Double.isNaN( val );
    }

    /** Devuelve el valor que falta.
     * 
     */
    public static double getMissingValue()  {
        return MISSING_VALUE;
    }

    /** Mï¿½todo para establecer como ausente el valor de clase.
     * 
     */
    public void setClassMissing() {
        if (classIndex() < 0)
            throw new RuntimeException( "No se ha establecido la clase." );
        else
        	setMissing(classIndex());
    }

    /** Mï¿½todo para establecer un valor como ausente.
     * 
     * @param index	El ï¿½ndice del atributo.
     */
    public final void setMissing(int index) {
        setValue( index, MISSING_VALUE );
    }
    
    /** Mï¿½todo para copiar un conjunto de elementos.
     * 
     * @return	El conjunto de elementos creado.
     */
    public Object copy() {
        Itemset result = new Itemset(this);
        result.dataset = dataset;

        return result;
    }
    
    /** Mï¿½todo para imprimir el conjunto de elementos.
     * 
     */
    @Override
    public String toString()
	{
    	String result = "";
    	for (int i=0; i<dataset.numAttributes(); i++ ) {
    		Attribute att = dataset.getAttribute(i);
    		
    		if (att.isContinuous())
    			result += att.name() + "=" + values[i] + "\n";
    		else
    			result += att.name() + "=" + att.value((int)values[i]) + "\n";
    	}
    	
    	return result;
    }
}
