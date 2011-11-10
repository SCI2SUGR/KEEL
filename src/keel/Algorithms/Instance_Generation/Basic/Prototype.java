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

package keel.Algorithms.Instance_Generation.Basic;

import keel.Dataset.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import java.util.Collections.*;
import java.util.*;

/**
 * Representation of a prototype. Contains several inputs an several outputs and common operations.
 * @author diegoj
 */
public class Prototype
{
    /** Informs if the prototype is normalized in [0, 1]. */
    boolean normalized = false;
    /** Informs that the prototype has not got a definided index. */
    public static final int UNKNOW_INDEX = -1;
    /** Index of the prototype in the set, used as an identifier. */
    protected int index = UNKNOW_INDEX;
    /** Type of each attribute of the set. */
    protected static int[] type;
    /** Integer type of attributes. */
    public static final int INTEGER = 0;
    /** Double type of attributes. */
    public static final int DOUBLE = 1;
    /** Nominal type of attributes. */
    public static final int NOMINAL = 2;
    
    protected static double normalize(double min, double max, double value)
    {
        if(value <= min)
            return 0.0;
        return (value-min)/(max-min);
    }
    
    /**
     * Update the type of each attribute of the set.
     * @param att InstanceAttributes object.
     */
    public static void setAttributesTypes(InstanceAttributes att)
    {
        int numAtt = att.getNumAttributes();
        type = new int[numAtt];
        for(int i=0; i<numAtt; ++i)
        {
            Attribute a = att.getAttribute(i);
            if(a.getType() == Attribute.INTEGER)
                type[i]= INTEGER;
            else if(a.getType() == Attribute.REAL)
                type[i] = DOUBLE;
            else if(a.getType() == Attribute.NOMINAL)
                type[i] = NOMINAL;
        }
    }
    
     /**
     * Return the type of the attribute.
     * @param i ith attribute.
     */
    public static int getTypeOfAttribute(int i)
    {
        return type[i];
    }
    
    /**
     * Informs of the index of the prototype.
     * @return Index of the prototype.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Assigns a new index to the prototype.
     * @param index New index to be assigned.
     */
    public void setIndex(int index)
    {
        this.index = index;
    }
    
    /** Normalized inputs of the prototype (values in [0,1]). */
    protected double[] inputs = null; //inputs of the example
    
    /** Normalized outputs of the prototype (values in [0,1]). */
    protected double[] outputs = null; //output of the example  
    
    
    /*
     *   constructs a null prototype.
     * 
     */
    public Prototype(){
    	this.inputs = null;
    	this.outputs = null;
    	this.index = UNKNOW_INDEX;
    }
    
    
    /**
     * 
     */
    
    public Prototype(int numInput, int numOutput){
    	this.inputs =  new double[numInput];
    	this.outputs =  new double[numOutput];
    	this.index = UNKNOW_INDEX;
    }
    /**
     * Constructs a Prototype.
     * @param inputs Inputs that will contain the new prototype
     * @param outputs Ouputs that will contain the new prototype
     */        
    public Prototype(double[] inputs, double[] outputs)
    {
        this.inputs = inputs;
        this.outputs = outputs;
        this.index = UNKNOW_INDEX;
    }
    
    /**
     * Constructs a Prototype from an instance.
     * @param inst Instance that contains inputs and outputs that will be assigned to the new prototype.
     */        
    public Prototype(Instance inst)
    {
        normalized = true;
        
    	//this.inputs = inst.getNormalizedInputValues();
    	this.inputs = inst.getAllInputValues();
        //this.print();
        //this.inputs = inst.getAllInputValues();              //Cambio para normalizaciï¿½n.
    	this.outputs = inst.getNormalizedOutputValues();
        this.index = UNKNOW_INDEX;
        //System.out.println("INTPUTS");
        int size = getInputs().length;
        for(int i=0; i<size; ++i)
        {
            double maxi = Attributes.getAttribute(i).getMaxAttribute();
            double mini = Attributes.getAttribute(i).getMinAttribute();
            if(type[i]==INTEGER || type[i]==DOUBLE)
            {
            	//System.err.println("Entero o double");
             //   System.err.println("["+mini+","+maxi+"]");                
               // System.err.println("Antes: "+inputs[i]);
               inputs[i] = normalize(mini, maxi, inputs[i]);       //TOKADO PARA NORMALIZR
                //System.err.println("Despues: "+inputs[i]);
                if(inputs[i]<0)
                    inputs[i] = 0.0;
            }
            else if(type[i]==NOMINAL)
            {
            	//System.err.println("Nominal");
                maxi = Attributes.getAttribute(i).getNumNominalValues()-1;
                mini = 0;
                //System.err.println("["+mini+","+maxi+"]");
                //System.err.println("Antes: "+inputs[i]);
               inputs[i] = normalize(mini, maxi, inputs[i]);    //TOKADO PARA NORMALIZR
                //System.err.println("Despues: "+inputs[i]);
            }
        }

        //int i = 0;
        /*for(Double d : this.inputs)
        {
            Debug.force(d>=0.0 && d<=1.0, "Sale fuera, el atributo " + i + " vale " + d + " es un " + getTypeOfAttribute(i) );
            i++;
        }*/
            
        //count1++;
    }
    
    /**
     * Constructs a Prototype from another protoype.
     * @param original Prototype that will be copied into the new object.
     */        
    public Prototype(Prototype original)
    {
        this.inputs = Arrays.copyOf(original.inputs, original.inputs.length);
        this.outputs = Arrays.copyOf(original.outputs, original.outputs.length);        
        //this.index = UNKNOW_INDEX;
        this.index = original.index;
        this.normalized = original.normalized;
    }

    /**
     * Copy the values of a Prototype from another protoype.
     * @param original Prototype that will be copied into the new object.
     */ 
    public void set(Prototype original)
    {
        this.inputs = Arrays.copyOf(original.inputs, original.inputs.length);
        this.outputs = Arrays.copyOf(original.outputs, original.outputs.length);        
        this.normalized = original.normalized;
    }
    
    /**
     * Returns the inputs of the protoype.
     * @return Array with the inputs of the prototype.
     */     
    public double[] getInputs()
    {
        return inputs;
    }
    
    /**
     * Returns a specific input of the protoype.
     * @param i Index of the input attribute.
     * @return Value of the input of that attribute.
     */         
    public double getInput(int i)
    {
        return inputs[i];
    }

    
    /**
     * Set a input for an attribute
     * @param i Index of the input attribute.
     * @param valor Value to set.
     */
    public void setInput(int i, double valor){
    	inputs[i] =valor;
     }
    
    /**
     * Returns the outputs of the protoype.
     * @return Array with the outputs of the prototype.
     */     
    public double[] getOutputs()
    {
        return inputs;
    }
    
    /**
     * Returns a specific output of the protoype. Not to be used. Use label() instead.
     * @param i Index of the output attribute.
     * @return Value of the output of that attribute.
     */     
    public double getOutput(int i)
    {
        return outputs[i];
    }

    /**
     * Returns the first output of the protoype.
     * @return Value of the output of the first attribute.
     */ 
    public double firstOutput()
    {
        return outputs[0];
    }
    
    /**
     * Returns the label of the prototype (aka first output).
     * @return Label of the prototype (AKA assigned class or first output).
     */ 
    public double label()
    {
        return firstOutput();
    }
    
    /**
     * Returns the assigned class of the prototype (same value as the first output of the protoype).
     * @return Value of the assigned class.
     */ 
    public double assignedClass()
    {
        return firstOutput();
    }    
    
    /**
     * Returns the number of attributes that has an input.
     * @return Number of inputs of an example (prototype).
     */
    public int numberOfInputs()
    {
        return inputs.length;
    }
    
     /**
     * Assigns a new value to the first output of the prototype.
     * @param val Value to be assigned to the first output (aka label) of the prototype.
     */
    public void setFirstOutput(double val)
    {
        outputs[0] = val;
    }
    
    /**
     * Assigns a new class (aka new first output) to the prototype.
     * @param val Value to be assigned to the first output (aka label) of the prototype.
     */
    public void setClass(double val)
    {
        setFirstOutput(val);
    }
    
    /**
     * Assigns a new label (aka first output, class) to the prototype.
     * @param val Value to be assigned to the first output (aka class) of the prototype.* 
     */
    public void setLabel(double val)
    {
        setFirstOutput(val);
    }
    
    /**
     * Returns the number of attributes that has an output.
     * @return Number of outputs of an example (prototype).     
     */    
    public int numberOfOutputs()
    {
        return outputs.length;
    }
 
    
    /**
     * Multiply component by component like a scalar product.
     * 
     * @return A prototype which inputs product with other..
     */ 
    public Prototype mul(Prototype other)
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        if(other.numberOfInputs() == this.numberOfInputs()){
        	for(int i=0; i<numInputs; ++i)
        		_inputs[i] = other.inputs[i]*(this.inputs[i]);
        
        }

        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);
    }
 
    /**
     * Multiply component by component like a scalar product.
     * @return A prototype which inputs product with other..
     */ 
    public double mulEscalar(Prototype other)
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
 
        
        double suma = 0;
        if(other.numberOfInputs() == this.numberOfInputs()){
        	for(int i=0; i<numInputs; ++i){
        		suma+= other.inputs[i]*(this.inputs[i]);
        		
        	}
        }

        return suma;
    }
    
    
    /**
     * Performs product operation between one prototype and a double.
     * @param weight Constant to be multiplied to each sum.
     * @return A prototype which inputs product with a weight.
     */ 
    public Prototype mul(double weight)
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = weight*(this.inputs[i]);
        
        //for(int i=0; i<numOutputs; ++i)
        //    _outputs[i] = weight*(this.outputs[i] + other.outputs[i]);
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);
    }

    
    
    /**
     * Performs sqrt operation to all the inputs of the prototype.
     * 
     * @return A prototype which inputs are the sqrt(original).
     */ 
    public Prototype sqrt()
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = Math.sqrt(this.inputs[i]);
        
        //for(int i=0; i<numOutputs; ++i)
        //    _outputs[i] = weight*(this.outputs[i] + other.outputs[i]);
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);
    }
    
    
    /**
     * 
     * @return the module of a Prototype, like the sqrt(a1^2 + a2^2...)
     */
    public double module(){
    	double result = 0.0;
    	
    	
    	for(int i=0; i<this.inputs.length; i++){
    		result += this.inputs[i] * this.inputs[i];
    	}
    	
    	return Math.sqrt(result);
    }
    
    /**
     * Add an increment to all the inputs of the prototype
     * @param increment
     *
     */
    public Prototype add(double increment){
    	Prototype p = new Prototype(this);
    	
    	for(int i=0; i< p.inputs.length; i++){
    		p.setInput(i, p.getInput(i) + increment);
    	}
    	
    	return p;
    }
    
    /**
     * Performs add operation between two prototypes.
     * @param other A protype to be added to the implicit parameter.
     * @return A prototype which inputs are the sum of another two, and outputs are a copy of implicit-ones.
     */ 
    public Prototype add(Prototype other)
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = this.inputs[i] + other.inputs[i];
        
        
        //for(int i=0; i<numOutputs; ++i)
        //    _outputs[i] = this.outputs[i] + other.outputs[i];
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);

        return new Prototype(_inputs, _outputs);
    }

     /**
     * Performs add and product operation between two prototypes.
     * @param other A protype to be added and multiplied to the implicit parameter.
     * @param weight Constant to be multiplied to each sum.
     * @return A prototype which inputs are the sum, multiplied with a weight, of another two. Outputs are a copy of implicit-ones.
     */ 
    // HERE BE DRAGONS! DON'T PASS
    public Prototype addMul(Prototype other, double weight)
    {
        /*int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = weight*(this.inputs[i] + other.inputs[i]);
        
        //for(int i=0; i<numOutputs; ++i)
        //    _outputs[i] = weight*(this.outputs[i] + other.outputs[i]);
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);*/
        return (this.add(other)).mul(weight);
    }

     /**
     * Performs add and divide operation between two prototypes.
     * @param other A protype to be added and divided to the implicit parameter.
     * @param divisor Constant that divides each sum.
     * @return A prototype which inputs are the sum, divides with divisor, of another two. Outputs are a copy of implicit-ones.
     */
    public Prototype addDiv(Prototype other, double divisor)
    {
        double weight = 1.0/divisor;
        return addMul(other, weight);
    }
    

    

    /**
     * Performs average operation between two prototypes.
     * @param other A protype to be merged to the implicit parameter.     
     * @return A prototype which inputs are the sum, divided between two of another two. Outputs are a copy of implicit-ones.
     */
    public Prototype avg(Prototype other)
    {
        /*int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = (this.inputs[i] + other.inputs[i])*0.5;
        
        for(int i=0; i<numOutputs; ++i)
            _outputs[i] = this.outputs[i];
        
        return new Prototype(_inputs,_outputs);*/
        return addMul(other,0.5);
    }
    
    /**
     * Performs average operation between two prototypes.
     * @param p1 One protype to be merged.
     * @param p2 Other protype to be merged.
     * @return A prototype which inputs are the sum, divided between two of another two. Outputs are a copy of p1.
     */    
    public static Prototype avg(Prototype p1, Prototype p2)
    {
        return p1.avg(p2);
    }
    
    /**
     * Performs averaged-based explicit operation between two prototypes.
     * @param p1 One protype to be merged.
     * @param w1 Weight of prototype p1.
     * @param p2 Other protype to be merged.
     * @param w2 Weight of prototype p2. 
     * @return A prototype which inputs are (w1*p1.p1_inputs + w2*p2_inputs)/(w1+w2). Outputs are a copy of p1.
     */       
    public static Prototype avg(Prototype p1, double w1, Prototype p2, double w2)
    {
        Prototype averaged = new Prototype(p1);
        double denominator = 1.0/(w1+w2);
        int numInputs = p1.numberOfInputs();
        for(int i=0; i<numInputs; ++i)
        {
            averaged.inputs[i] = w1*p1.inputs[i] + w2*p2.inputs[i];
            averaged.inputs[i] *= denominator;
        }
        return averaged;
    }
    
    /**
     * Performs substract operation between two prototypes.
     * @param other A protype to be substract to the implicit parameter.
     * @return A prototype which inputs are the difference of another two, and outputs are a copy of implicit-ones.
     */     
    public Prototype sub(Prototype other)
    {
        int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = this.inputs[i] - other.inputs[i];
        //for(int i=0; i<numOutputs; ++i)
          //  _outputs[i] = this.outputs[i] - other.outputs[i];
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);
    }
 
    /**
     * Performs substract and product operation between two prototypes.
     * @param other A protype to be substract and multiplied to the implicit parameter.
     * @param weight Constant to be multiplied to each difference.
     * @return A prototype which inputs are the difference (multiplied with a weight) of another two. Outputs are a copy of implicit-ones.
     */     
    // HERE BE DRAGONS! DON'T PASS
    public Prototype subMul(Prototype other, double weight)
    {
        /*int numInputs = numberOfInputs();
        double[] _inputs = new double[numInputs];
        for(int i=0; i<numInputs; ++i)
            _inputs[i] = weight*(this.inputs[i] - other.inputs[i]);
        
        int numOutputs = numberOfOutputs();
        double[] _outputs = new double[numOutputs];
        _outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(_inputs, _outputs);*/
        return (this.sub(other)).mul(weight);
    }
    
    /**
     * Converts the prototype to a String object
     * @return String representation of a prototype.
     */
    @Override
    public String toString()
    {
        String result = "";
        int nInputs = numberOfInputs();
        int nOutputs = numberOfOutputs();
        
        for(int i=0; i<nInputs; ++i)
            result += inputs[i] + " ";
        for(int i=0; i<nOutputs; ++i)
            result += outputs[i] + " ";
        return result;
    }
    
    protected static double round(double value)
    {
        String s = Double.toString(value);
        String[] comma = s.split("\\.");
        if (comma.length > 1)
        {
            int pos1, pos2;
	    if (comma[1].indexOf("E") < 0)
            {
              pos1 = comma[1].indexOf("0000");
              pos2 = comma[1].indexOf("9999");
              if (pos1 >= 0) {
                comma[1] = comma[1].substring(0,pos1);
               if (comma[1].length() == 0) comma[1] = "0";
              } else if (pos2 >= 0) {
                comma[1] = comma[1].substring(0,pos2);
//              System.out.println(comma[1]);
                if (comma[1].length() == 0) {
                  comma[1] = "0";
                  int redondo = Integer.parseInt(comma[0]);
                  redondo++;
                  comma[0] = String.valueOf(redondo);
                } else {
                  long redondo = Long.parseLong(comma[1].substring(comma[1].length()-1));
                  redondo++;
                  comma[1] = comma[1].substring(0,comma[1].length()-1) + String.valueOf(redondo);
                }
               }
            }
            return Double.valueOf(comma[0] + "." + comma[1]);
	}
        return Double.valueOf(comma[0]);
    }
    
    /**
     * Denormalize the values of the inputs and outputs.
     * @return A denormalized prototype of implicit.
     */
    public Prototype denormalize()
    {
    	//this.normalized = true; //Tokado para...
        int nInputs = numberOfInputs();
        int nOutputs = numberOfOutputs();
        //count2++;
        double[] max_inputs = new double[nInputs];
        double[] min_inputs = new double[nInputs];
        
        //double[] max_outputs = new double[nOutputs];
        //double[] min_outputs = new double[nOutputs];
        
        double[] new_inputs = new double[nInputs];
        double[] new_outputs = new double[nOutputs];
        
        for(int i=0; i<nInputs; i++)
        {
            max_inputs[i] = Attributes.getInputAttribute(i).getMaxAttribute();
            min_inputs[i] = Attributes.getInputAttribute(i).getMinAttribute();
        }
        
        /*for(int i=0; i<nOutputs; ++i)
        {
            max_outputs[i] = Attributes.getOutputAttribute(i).getMaxAttribute();
            min_outputs[i] = Attributes.getOutputAttribute(i).getMinAttribute();
        }*/
            
        //System.out.println("Prot");
        
        for(int i=0; i<nInputs; ++i)
        {
        	double value =inputs[i]*(max_inputs[i]-min_inputs[i]) + min_inputs[i];
        	//System.out.println("Input i "+ value);
           
        	if(type[i] == DOUBLE){
        	   new_inputs[i] = value;
            }
            if(type[i]==INTEGER)
            {
            	 new_inputs[i] = round(1.*inputs[i]*(max_inputs[i]-min_inputs[i]) + min_inputs[i]);
                //System.out.println("ANTES new_inputs_i " + new_inputs[i]);
                new_inputs[i] = Math.round(new_inputs[i]);
                //System.out.println("DESPUES new_inputs_i " + new_inputs[i]);
            }
            if( type[i]==NOMINAL){
            	  double maxi = Attributes.getAttribute(i).getNumNominalValues()-1;
                  double mini = 0;
                  
            	//System.out.println("ANTES inputs_i " + inputs[i]);
            	new_inputs[i] = inputs[i]*(maxi-mini) + mini;
            	//System.out.println("DESPUES new_inputs_i " + new_inputs[i]);
            }
            //System.out.println("  > " + i + " " + inputs[i]);
            //System.out.println("  > " + i + " " + new_inputs[i]);        
        }
        
        //for(int i=0; i<nOutputs; ++i)
        //    new_outputs[i] = outputs[i]*(max_outputs[i]-min_outputs[i]) + min_outputs[i];
        new_outputs = Arrays.copyOf(this.outputs, this.outputs.length);
        
        return new Prototype(new_inputs, new_outputs);
    }
    
    /**
     * Return an input as nominal. Input attribute i must be nominal, if not, it crash.
     * @param i Index of the input.
     * @return The value of the input in String representation.     
     */
    public String getInputAsNominal(int i)
    {
        int indexOfNominalAttr = 0;
        if(normalized)
        {
            //We know that the input i is nominal, if not it crashes
            double maxInput_i = Attributes.getInputAttribute(i).getNumNominalValues()-1;
            double minInput_i = 0;
            indexOfNominalAttr = (int)Prototype.normalize(minInput_i, maxInput_i, inputs[i]);
        }
        else
        {
            //Como ya estÃ¡ desnormalizado, sÃ³lo extraemos el Ã­ndice del atributo nominal
            indexOfNominalAttr = (int)(Math.round(inputs[i]));
        }
        return Attributes.getInputAttribute(i).getNominalValue(indexOfNominalAttr);
    }
    
    /**
     * Return an output as nominal. Output attribute i must be nominal, if not, it crash.
     * @param i Index of the outut.
     * @return The value of the output in String representation.
     */
    public String getOutputAsNominal(int i)
    {
        //We know that the output i is nominal, if not it crash
        int _index = (int)(Math.round(outputs[i]));
        return Attributes.getOutputAttribute(i).getNominalValue(_index);
    }
    
    /**
     * Return all the existing classes in our universe.
     * @return All the values of the outputs (clasess) that exists in the dataset.
     */
    static public ArrayList<Double> possibleValuesOfOutput()
    {
        Attribute[] a = Attributes.getOutputAttributes();
        //System.out.println("a " + a[0]);        
        
        if(a[0].getType() == Attribute.NOMINAL)
        {
            int _size = a[0].getNominalValuesList().size();
            //Vector values = a[0].getNominalValuesList();
            //for(int i=0; i<_size; ++i)
            //    System.out.println((String)(values.get(i)));
            ArrayList<Double> v = new ArrayList<Double>();
            //System.out.println("el atributo es nominal");
            //double min = 0.0;
            //double max = (double)(_size-1);
            //double inc = 1.0/(double)_size;
            //for(int i=0; i<_size; ++i)
            //    v.add( ((double)(i)-min)/(max-min) );
            for(int i=0; i<_size; ++i)
                v.add((double)i);
            return v;
        }
        //Â¡Es real el atributo de salida!
        //Incluye los extremos solamente Â¡Es erroneo!
        ArrayList<Double> v = new ArrayList<Double>();
        double max = a[0].getMaxAttribute();
        double min = a[0].getMinAttribute();
        System.out.println("el atributo NO es nominal");
        v.add( min );
        v.add( max );
        return v;
    }
    
    /**
     * Test if two prototypes are equals
     * @param other The other prototype to be compared with the caller.
     * @return true if the two prototypes are equal, false in other case.
     */
    public boolean equals(Prototype other)
    {
        return Arrays.equals(inputs, other.inputs) && Arrays.equals(outputs, other.outputs);
    }
    
    
    /**
     * Test if two prototypes have the same inputs
     * @param other The other prototype to be compared with the caller.
     * @return true if the two prototypes have the same inputs, false in other case.
     */
    public boolean equalsInputs(Prototype other)
    {
        return Arrays.equals(inputs, other.inputs);
    }
    
    
    /**
     * Change attribute values that are not in [0.0, 1.0].
     * If an attribute is greater than 1.0, assigns 1.0 to it.
     * If an attribute is smaller than 0.0, assigns 0.0 to it.
     */
    public void applyThresholds()
    {
        int nInputs = numberOfInputs();
     
        /*
        for(int i=0; i<nInputs; ++i)
        {
            if(inputs[i]>Attributes.getInputAttribute(i).getMaxAttribute())
                inputs[i] = Attributes.getInputAttribute(i).getMaxAttribute();
            else if(inputs[i]< Attributes.getInputAttribute(i).getMinAttribute())
                inputs[i] =  Attributes.getInputAttribute(i).getMinAttribute();
        
        }
     */
        for(int i=0; i<nInputs; ++i)
        {
            if(inputs[i]>1)
                inputs[i] = 1;
            else if(inputs[i]< 0)
                inputs[i] =  0;
       
        }
    }
    
    /**
     * Round integer attributes.
     */
    /*public void round()
    {
        int nInputs = numberOfInputs();
         for(int i=0; i<nInputs; ++i)
         {
            inputs[i] = Math.round(inputs[i]);
         }
    }*/
    
    /**
     * Convert the prototype in the null prototype
    */
    public void makeNull()
    {
        int nInputs = numberOfInputs();
        int nOutputs = numberOfOutputs();
        
        for(int i=0; i<nInputs; ++i)
            inputs[i] = 0.0;
        for(int i=0; i<nOutputs; ++i)
            outputs[i] = 0.0;
    }
    
    /**
     * Print the prototype.
     */
    public void print(){
    	System.out.print("\n");
    	
    	for (int i =0; i<this.inputs.length; i++){
    		
    		System.out.print(inputs[i] + "  ");
    		
    	}
    	
    	System.out.print(this.outputs[0]+"\n");
    	
    	
    }
    
    
    /**
     * Opuesto de un prototipo
     *
     */
    
    public Prototype opposite(){
    	Prototype opuesto = new Prototype(this);
    	
    	for (int i=0; i< this.inputs.length; i++){
    		opuesto.inputs[i] = 1-this.inputs[i];
    	}
    	
    	return opuesto;
    }
    
    /**
     * This function is for NOminal adaptation...
     * 
     */
    public Prototype formatear(){
    	Prototype formateado = new Prototype(this);
    	int nInputs = this.numberOfInputs();
    	
    	double[] max_inputs = new double[nInputs];
    	double[] max_inputsI = new double[nInputs];   
    	double[] min_inputsI = new double[nInputs];     
        for(int i=0; i<nInputs; i++)
        {
            max_inputs[i] = Attributes.getAttribute(i).getNumNominalValues()-1;
            max_inputsI[i] = Attributes.getInputAttribute(i).getMaxAttribute();
            min_inputsI[i] = Attributes.getInputAttribute(i).getMinAttribute();
           }
        
        double coef =0.0;
        int aux =0;
        
    	for (int i=0; i< this.inputs.length; i++){
    		if(type[i] == NOMINAL){
    			//System.out.println("Max input " + max_inputs[i]);
    			coef = 1./max_inputs[i]; // Real mode.
    			aux = (int) Math.round((1.*this.inputs[i]/coef)); // Number of "nominal value"
    			
    			formateado.inputs[i] = aux * coef; // number of nominal value * coef
    			
    		}
    		if(type[i] == INTEGER){
    			coef = 1./(max_inputsI[i] - min_inputsI[i]);
    			aux = (int) Math.round((1.*this.inputs[i]/coef));
    			formateado.inputs[i] = aux * coef;
    		}
    	}	
    	return formateado;
    }
    
}//end of Prototype.java

