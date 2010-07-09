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

    /** Devuelve el �ndice del atributo de clase.
     * 
     */
    public int classIndex() {
        if (dataset == null) {
        	System.err.println("el dataset asociado es nulo");
            return(-1);
        } else
        	return dataset.getClassIndex();
    }

    /** M�todo que comprueba si falta el atributo de clase.
     * 
     * @return	True si falta el valor del atributo de clase.
     */
    public boolean classIsMissing() {
        if(classIndex() < 0) 
            throw new RuntimeException("No se ha establecido la clase.");
        else
        	return isMissing(classIndex());
    }

    /** Devuelve el �ndice del valor de la clase
     *
     */
    public double getClassValue() {
        if(classIndex() < 0) {
        	System.err.println("El dataset asociado es nulo");
        	return (-1);
        } else
        	return getValue(classIndex());
    }

    /** Devuelve el n�mero de valores de clase.
     * 
     */
    public int numClasses() {
    	if(dataset == null) {
    		System.err.println("El dataset asociado es nulo");
    		return (-1);
    	} else
        	return dataset.numClasses();
    }

    /** Devuelve el atributo correspondiente al �ndice indicado.
     * 
     */
    public Attribute getAttribute(int index) {
        if(dataset == null) {
        	System.err.println("El dataset asociado es nulo");
        	return null;
        } else
        	return dataset.getAttribute(index);
    }

    /** M�todo para establecer un valor.
     * 
     * @param index		�ndice del atributo.
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

    /** M�todo para establecer el peso.
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

    /** M�todo para establecer el dataset.
     * 
     * @param data	El dataset.
     */
    public final void setDataset(Dataset data) {
        dataset = data;
    }

    /** M�todo para comprobar si falta un valor.
     * 
     * @param index	�ndice del atributo a comprobar.
     * 
     * @return		True si falta el valor del atributo. False en caso contrario.
     */
    public boolean isMissing(int index) {
        if(Double.isNaN(values[index])) 
        	return true;
        else
        	return false;
    }

    /** M�todo para comprobar si falta un valor dado.
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

    /** M�todo para establecer como ausente el valor de clase.
     * 
     */
    public void setClassMissing() {
        if (classIndex() < 0)
            throw new RuntimeException( "No se ha establecido la clase." );
        else
        	setMissing(classIndex());
    }

    /** M�todo para establecer un valor como ausente.
     * 
     * @param index	El �ndice del atributo.
     */
    public final void setMissing(int index) {
        setValue( index, MISSING_VALUE );
    }
    
    /** M�todo para copiar un conjunto de elementos.
     * 
     * @return	El conjunto de elementos creado.
     */
    public Object copy() {
        Itemset result = new Itemset(this);
        result.dataset = dataset;

        return result;
    }
    
    /** M�todo para imprimir el conjunto de elementos.
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