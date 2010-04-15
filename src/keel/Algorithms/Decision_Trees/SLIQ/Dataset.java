package keel.Algorithms.Decision_Trees.SLIQ;
import java.util.*;

import keel.Dataset.*;


/** 
 * Clase que representa un dataset o conjunto de datos
 */

public class Dataset {
	/** Nombre del dataset. */
	protected String name = "";
	
	/** Atributos que contiene. */
	protected Vector attributes;
	
	/** Conjuntos de elementos. */
	protected Vector itemsets;	
		
	/** Índice del atributo de clase. */
	protected int classIndex;
	
	/** Keel dataset InstanceSet **/
	protected InstanceSet IS;

	/** Método encargado de leer el archivo .dat que contiene la información del dataset.
	 * 
	 * @param name 		El objeto lector en el que serán leidos los conjuntos de elementos.
	 * @param train     Indica si el fichero es de entrenamiento
	 */
  	public Dataset(String name, boolean train) {
 		try {
            // Crea el conjunto de instancias
            IS = new InstanceSet();
            // Lee los conjuntos de elementos.
            IS.readSet(name,train);
		} catch(DatasetException e) {
			System.out.println("Error al leer instancias del dataset");
			e.printStackTrace();
			System.exit(-1);
		} catch(HeaderFormatException e) {
			System.out.println("Error al leer instancias del dataset");
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Almacenar los atributos del dataset
		readHeader();

  		itemsets = new Vector(IS.getNumInstances());
  		
  		// Leer todos los conjuntos de elementos
  		getItemsetFull();  		  		
	}
 
	/** Constructor que obtiene una copia de un dataset.
	 * 
	 * @param dataset		El dataset a copiar.
	 */
  	public Dataset(Dataset dataset) {
  		this(dataset, dataset.numItemsets());
  		dataset.copyItemsets(0, this, dataset.numItemsets());
	}
  	
  	
  	/** Constructor para copiar todos los atributos de otro dataset excepto los conjuntos de elementos.
  	 * 
  	 * @param dataset		El dataset a copiar.
  	 * @param capacity		El n�mero de conjuntos de elementos.
  	 */
  	public Dataset(Dataset dataset, int capacity)  {
  		if(capacity < 0) 
  			capacity = 0;
    
  		classIndex = dataset.classIndex;
  		name = dataset.getName();
  		attributes = dataset.attributes;
  		itemsets = new Vector(capacity);
	}

  	/** Método para almacenar la cabecera de un archivo de datos.
  	 * 
   	 */ 
  	private void readHeader()  {
  		String attributeName;
  		Vector attributeValues;
  		int i;

		name = Attributes.getRelationName();
		
  		// Vectores para almacenar temporalmente la informaci�n.
  		attributes = new Vector();
 
  		keel.Dataset.Attribute at;
  		
  		// almacenar atributo, entradas y salidas de la cabecera
  		for (int j=0; j<Attributes.getNumAttributes(); j++) {
  			at=Attributes.getAttribute(j);
  			attributeName = at.getName();
  			
  			// Comprobar si es de tipo real
  			if(at.getType()==2) {
  			 float min = (float) at.getMinAttribute();
  			 float max = (float) at.getMinAttribute();
  			 attributes.addElement(new Attribute(attributeName, j));
 			 Attribute att = (Attribute)attributes.elementAt(j);
			 att.setRange(min, max);  	
			 att.activate();
  			} else {
             // Comprobar si el tipo es entero
  			 if(at.getType()==1) {	
  			 	int min = (int) at.getMinAttribute();
  			 	int max = (int) at.getMinAttribute();  			 	  			 	  			 	
  			 	attributes.addElement(new Attribute(attributeName, j));
  			 	Attribute att = (Attribute)attributes.elementAt(j);
  			 	att.setRange(min, max);  	
  			 	att.activate();
  			 } else { // es discreto
  			 	attributeValues = new Vector();
  			 	for(int k=0; k<at.getNumNominalValues();k++) {
  			 		attributeValues.addElement(at.getNominalValue(k));
  			 	}
  			 	attributes.addElement(new Attribute(attributeName, attributeValues, j));
  			 	Attribute att = (Attribute)attributes.elementAt(j);
  			 	att.activate();
  			 }  			 	
  			}  			 		
  		} // for
  			
  		// Establecer el �ndice de la clase de salida
  		classIndex = Attributes.getNumAttributes() - 1;  				
	}

  	
  	
  	/** Método para leer un conjunto de elementos y añadirlo al dataset
  	 * 
  	 * @return					True si se ha leído el conjunto de elementos sin problemas.
  	 * 
  	 */
  	private boolean getItemsetFull()  {
   		// rellenar el conjunto de elementos
  		for(int j=0; j<IS.getNumInstances(); j++) {
  	  		double[] itemset = new double[Attributes.getNumAttributes()];

  			// Obtener valores para todos los atributos de entrada.
  			for(int i=0; i<Attributes.getInputNumAttributes(); i++) {
  				// comprobar el tipo y si es nulo
  				if(IS.getInstance(j).getInputMissingValues(i))
  					itemset[i] = Itemset.getMissingValue();
  				else {
  					if(Attributes.getInputAttribute(i).getType()==0) { // nominal
  						for(int k=0; k<Attributes.getAttribute(i).getNumNominalValues();k++)	
  							if(Attributes.getAttribute(i).getNominalValue(k).equals(IS.getInstance(j).getInputNominalValues(i)))
  								itemset[i]=(double)k;	
  					} else { // real y entero
  						itemset[i]=IS.getInstance(j).getInputRealValues(i);  				    				
  					}
  				} // else  				            
			} //for
  	
  			// Obtener valores para el atributo de salida.
  			int i=Attributes.getInputNumAttributes();
  			
  			// comprobar el tipo y si es nulo
            if(IS.getInstance(j).getOutputMissingValues(0))
                itemset[i] = Itemset.getMissingValue();
            else {
                if(Attributes.getOutputAttribute(0).getType()==0) { //nominal
                    for(int k=0; k<Attributes.getOutputAttribute(0).getNumNominalValues();k++)
                        if(Attributes.getOutputAttribute(0).getNominalValue(k).equals(IS.getInstance(j).getOutputNominalValues(0)))
                            itemset[i]=(double)k;
                } else { // real y entero
                    itemset[i]=IS.getInstance(j).getOutputRealValues(0);
                }
            } // else

            // Agregar el conjunto de elementos al dataset
            addItemset(new Itemset(1, itemset));
  			
		} // for
  		return true;
	}

	/** Método para agregar un conjunto de elementos al dataset.
	 * 
	 * @param itemset	El conjunto de elementos a añadir.
	 */
  	public final void addItemset(Itemset itemset) {
  		Itemset newItemset = (Itemset)itemset.copy();
 		
  		newItemset.setDataset(this);
  		itemsets.addElement(newItemset);  		  	  		
	}

  	/** Devuelve el nombre del dataset.
  	 * 
  	 */
  	public String getName() {
  		return name;
  	}

  	/** Devuelve el atributo correspondiente a un cierto índice.
  	 * 
  	 * @param index		El índice del atributo.
  	 */
	public final Attribute getAttribute(int index) {
  		return (Attribute) attributes.elementAt(index);
	}

  	/** Devuelve el atributo que tiene un cierto nombre.
  	 * 
  	 * @param name	El nombre del atributo.
  	 */
	public final Attribute getAttribute(String name) {
  		for(int i=0; i<attributes.size(); i++)
  			if(((Attribute)attributes.elementAt(i)).name().equalsIgnoreCase(name)) 
  				return (Attribute) attributes.elementAt(i);
  			
  		return null;
	}

	/** Devuelve el atributo de clase.
	 * 
	 */
	public final Attribute getClassAttribute() {
  		if(classIndex < 0) {
  			System.err.println("�ndice de clase incorrecto:"+classIndex);
  			return null;
  		}
  		return getAttribute(classIndex);
	}

	/** Devuelve el índice del atributo de clase.
	 * 
	 */
	public final int getClassIndex() {
  		return classIndex;
	}
 
	/** Devuelve el número de atributos.
	 * 
	 */
	public final int numAttributes() {
  		return attributes.size();
	}

	/** Devuelve el número de posibles valores del atributo de clase.
	 * 
	 */
	public final int numClasses() {
  		if (classIndex < 0) {
  			System.err.println("�ndice de clase err�neo:"+classIndex);
  			return -1;
  		}
   		return getClassAttribute().numValues();
	}

	/** Devuelve el número de conjuntos de datos.
	 * 
	 */
	public final int numItemsets() {
  		return itemsets.size();
	}

  	/** Método que elimina el conjunto de elementos correspondiente a un índice.
  	 * 
  	 * @param index 	Índice del conjunto de datos a borrar.
  	 */
  	public final void delete(int index) {
  		itemsets.removeElementAt(index);
	}

	/** Método para eliminar todos los atributos en los que falten valores.
	 * 
	 * @param attIndex		Índice del atributo.
	 */
  	public final void deleteWithMissing(int attIndex) {
  		Vector newItemsets = new Vector(numItemsets());

  		for(int i=0; i<numItemsets(); i++)
  			if(!itemset(i).isMissing(attIndex))
  				newItemsets.addElement(itemset(i));

  		itemsets = newItemsets;
	}

	/** Enumera todos los atributos.
	 * 
	 * @return Una enumeración que contiene todos los atributos.
	 */
	public Enumeration enumerateAttributes()  {
  		Vector help = new Vector(attributes.size() - 1);
  		
  		for(int i=0; i<attributes.size(); i++)
  			if(i != classIndex)
  				help.addElement(attributes.elementAt(i));
  		
  		return help.elements();
	}

	/** Enumera todos los conjuntos de elementos.
	 * 
	 * @return	Una enumeración que contiene todos los conjutnos de elementos.
	 */
	public final Enumeration enumerateItemsets() {
  		return itemsets.elements();
	}

	/** Devuelve el conjunto de elementos de una cierta posición.
	 * 
	 * @param index	El índice del conjunto de elementos.
	 */	
	public final Itemset itemset(int index) {
  		return (Itemset)itemsets.elementAt(index);
	}

	/** Devuelve el último conjunto de elementos.
	 * 
	 */
	public final Itemset lastItemset() {
  		return (Itemset)itemsets.lastElement();
	}

	
  	/** Método para agregar las instancias de un conjunto al final de otro.
  	 * 
  	 * @param from	El índice del primero a copiar.
  	 * @param dest	El dataset al que van a copiarse los conjuntos de elementos.
  	 * @param num	Número de conjuntos de elementos a copiar.
  	 */
  	private void copyItemsets(int from, Dataset dest, int num) {
	    for(int i=0; i<num; i++)
	      dest.addItemset(itemset(from + i));
    }

  	/** Método que calcula la suma de los pesos de todos los conjuntos de elementos.
  	 * 
  	 * @return	El peso de todos los conjuntos de elementos.
  	 */
  	public final double sumOfWeights() {
  		double sum = 0;

  		for(int i=0; i<numItemsets(); i++)
  			sum += itemset( i ).getWeight();

  		return sum;
	}

  	/** Método para ordenar el dataset en base a un atributo dado.
  	 * 
  	 * @param attIndex	índice del atributo.
  	 */
  	public final void sort(int attIndex)  {
  		int i, j;

  		// Llevar al final todo el dataset con datos ausentes
  		j = numItemsets() - 1;
  		i = 0;
    
  		while(i <= j) {
  			if(itemset(j).isMissing(attIndex))
  				j--;
  			else {
  				if(itemset(i).isMissing(attIndex))  {
  					swap(i, j);
  					j--;
  				}
  				i++;
  			}
  		}
    
  		quickSort(attIndex, 0, j);
	}

  	/** Método que implementa el algoritmo quicksort.
  	 * 
  	 * @param attIndex		Índice del atributo utilizado para ordenar.
  	 * @param lo0			Valor mínimo.
  	 * @param hi0			Valor máximo.
  	 */
  	private void quickSort(int attIndex, int lo0, int hi0) {
  		int lo = lo0, hi = hi0;
  		double mid, midPlus, midMinus;
    
  		if (hi0 > lo0) {
  			// Arbitrarily establishing partition element as the 
  			// midpoint of the array.
  			mid = itemset((lo0 + hi0 ) / 2).getValue(attIndex);
  			midPlus = mid + 1e-6;
  			midMinus = mid - 1e-6;

  			// loop through the array until indices cross
  			while(lo <= hi) {
  				// find the first element that is greater than or equal to 
  				// the partition element starting from the left Index.
  				while((itemset(lo).getValue(attIndex) < midMinus) && (lo < hi0))
  					++lo;
	
  				// find an element that is smaller than or equal to
  				// the partition element starting from the right Index.
  				while ((itemset(hi).getValue(attIndex)  > midPlus) && (hi > lo0))
  					--hi;

  				// if the indexes have not crossed, swap
  				if(lo <= hi) {
  					swap( lo,hi );
  					++lo;
  					--hi;
  				}
  			}

  			// If the right index has not reached the left side of array
  			// must now sort the left partition.
  			if(lo0 < hi)
  				quickSort(attIndex, lo0, hi);

  			// If the left index has not reached the right side of array
  			// must now sort the right partition.
  			if(lo < hi0)
  				quickSort(attIndex, lo, hi0);
  		}
	}

  	/** Function to swap two itemsets. 
  	 * 
  	 * @param i		The first itemset.
  	 * @param j		The second itemset.
  	 */
  	private void swap(int i, int j) {
        Object help = itemsets.elementAt( i );

        itemsets.insertElementAt(itemsets.elementAt(j), i);
        itemsets.removeElementAt(i + 1);
        itemsets.insertElementAt(help, j);
        itemsets.removeElementAt(j + 1);
	}
  	


}