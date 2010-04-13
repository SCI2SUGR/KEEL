package keel.Algorithms.Neural_Networks.gann;

import java.util.Vector;

import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

/**
 * <p>
 * Dataset interface simplification
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class OpenDataset {
	
	/** Number of examples*/
	private int ndatos; 
	
	/** Number of variables */
	private int nvariables; 

	/** Number of inputs */
	private int nentradas; 
	
	/**  Number of outputs */
	private int nsalidas; 
	
	/** Number of classes */
	private int nclases; 

	/** Set of data instances */
	private InstanceSet IS;
	
	/**
	 * <p>
	 * Access to ndatos
	 * </p>
	 * @return number of examples
	 */
	public int getndatos() {
		return ndatos;
	}

	/**
	 * <p>
	 * Access to nvariables
	 * </p>
	 * @return number of variables
	 */
	public int getnvariables() {
		return nvariables;
	}

	/**
	 * <p>
	 * Access to nentradas
	 * </p>
	 * @return number of inputs
	 */
	public int getnentradas() {
		return nentradas;
	}

	/**
	 * <p>
	 * Access to nsalidas
	 * </p>
	 * @return number of outputs
	 */
	public int getnsalidas() {
		return nsalidas;
	}
	
	/**
	 * <p>
	 * Return type (0 nominal, 1 integer, 2 float,..) of the attribute at index
	 * </p>
	 * @param index 
	 * @return type of attribute.
	 */
	public int getTiposAt (int index) {
		return Attributes.getAttribute(index).getType();
	}
	
	/**
	 * <p>
	 * It returns the list of nominal values of an attribute
	 * </p>
	 * @param index Index of the attribute
	 * @return List of nominal value
	 */
	public Vector getRangosVar (int index) {
		return Attributes.getAttribute(index).getNominalValuesList();
	}
	
	/**
	 * <p>
	 * Return example data at index in a string separated by comma without spaces
	 * </p>
	 * @param index Index of the instance
	 * @return Representation of the instance
	 */
	public String getDatosAt (int index) {
		return IS.getInstance(index).toString();
	}
	
	/**
	 * <p>
	 * Constructor
	 * </p>
	 */
	public OpenDataset() {
		// Init a new set of instances
		IS = new InstanceSet();
	}

	/**
	 * <p>
	 * Load nfejemplos file and parse it
	 * </p>
	 * @param nfejemplos training file to read
	 * @param b Flag to see if it a training or test file
	 */
	public void processClassifierDataset(String nfejemplos, boolean b) {
		
		try {
			// Load in memory a dataset that contains a classification problem
			IS.readSet(nfejemplos, b);
			ndatos = IS.getNumInstances();
			nentradas = Attributes.getInputNumAttributes();
			nvariables = nentradas + Attributes.getOutputNumAttributes();
			nsalidas = Attributes.getOutputNumAttributes();
			
			// Check that there is only one output variable and
			// it is nominal

			if (Attributes.getOutputNumAttributes() > 1) {
				System.out.println("This algorithm can not process MIMO datasets");
				System.out.println("All outputs but the first one will be removed");
			}

			boolean noOutputs = false;
			if (Attributes.getOutputNumAttributes() < 1) {
				System.out
						.println("This algorithm can not process datasets without outputs");
				System.out.println("Zero-valued output generated");
				noOutputs = true;
			}
		
		} catch (Exception e) {
			System.out.println("DBG: Exception in readSet");
			e.printStackTrace();
			System.exit(-1);
		}

	}
}
