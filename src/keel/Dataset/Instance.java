package keel.Dataset;

import java.util.*;
import java.io.*;

/**
 * <p>
 * <b>Instance</b>
 * </p>
 *
 * This class keeps all the information of an instance. It stores nominal, 
 * integer and real values read from the file (in KEEL format). Also, it 
 * provides a set of methods to get information about the instance.
 *
 * @author Albert Orriols Puig
 * @version keel0.1 
 */



public class Instance {


/////////////////////////////////////////////////////////////////////////////
////////////////ATTRIBUTES OF THE INSTANCE CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////

	/**
	 * It is a vector of vectors of size 'number of attributes' where all the nominal 
	 * values will of the attributes be stored. In nominalValues[0] the input values
	 * are stored, and int nominalValues[1] the output values are stored.
	 */
	private String [][]nominalValues;

	/**
	 * It is a vector of vector of size 'number of attributes' where all the nominal 
	 * values will of the attributes be stored, but transformed to a integer value.
	 */
	private int [][]intNominalValues;


	/**
	 * The vector realValues is a vector of vectors of size 'number of attributes' 
	 * where all the integer and real attributes values will be stored. In realValues[0]
	 * all the input attribute values will be stored, while in realValues[1], the 
	 * outputs will be stored.
	 */
	private double [][]realValues;

	/**
	 * It is a vector of vectors of 'number of attributes' size that stores whichs 
	 * attributes are missing for the inputs and the outputs.
	 */
	private boolean [][]missingValues;


	/**
	 * Indicates if the instance belongs to a train BD
	 */
	private boolean isTrain;

	/**
	 * Indicates the number of input attributes per instance.
	 */
	private int numInputAttributes;

	/**
	 * Indicates the number of output attributes per instance.
	 */
	private int numOutputAttributes;

	/**
	 * Indicates the number of undefined attributes (that are neither
	 * inputs or outputs
	 */
	private int numUndefinedAttributes;

	/**
	 * It indicates if the instance has any missing value
	 */
	private boolean []anyMissingValue;


//	The next attriubtes define the position in the arrays where
//	each attribute is stored

	/**
	 * Input attributes location
	 */
	public final static int ATT_INPUT = 0;

	/**
	 * Output attributes location
	 */
	public final static int ATT_OUTPUT = 1;

	/**
	 * Non-defined direction attributes location
	 */
	public final static int ATT_NONDEF = 2;


/////////////////////////////////////////////////////////////////////////////
/////////////////// METHODS OF THE INSTANCE CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////

	/**
	 * It parses a new attribute line.
	 * @param def is the line to be parsed.
	 * @param _isTrain is a flag that indicates if the BD is for a train run.
	 * @param instanceNum is the number of the current instance. It's used to
	 * write error message with the maximum amount of information.
	 */
	public Instance(String def,boolean _isTrain, int instanceNum) {
		int currentClass = -1;
		//System.out.println ("Reading data: "+def);
		StringTokenizer st  = new StringTokenizer(def,","); //Separator: "," and " "

		initClassAttributes();
		isTrain         = _isTrain;

		int count=0, inAttCount=0, outAttCount=0, indefCount=0, inputOutput = 0, curCount;
		while (st.hasMoreTokens()) {
			//Looking if the attribute is an input, an output or it's undefined
			String att = st.nextToken().trim();
			Attribute curAt = Attributes.getAttribute(count);
			switch (curAt.getDirectionAttribute()){
			case Attribute.INPUT:
				inputOutput = Instance.ATT_INPUT;
				curCount = inAttCount++;
				break;
			case Attribute.OUTPUT:
				inputOutput = Instance.ATT_OUTPUT;
				if (curAt.getType() == Attribute.NOMINAL) {
					currentClass = curAt.convertNominalValue(att);

					//System.out.println ( " The position of the current class "+ att +" is: "+ currentClass );
				}
				curCount = outAttCount++;
				break;
			default://Attribute not defined neither as input or output. So, it is not read.
				inputOutput = Instance.ATT_NONDEF;
			curCount = indefCount++;
			}

			//The attribute is defined. So, its value is processed, and the attributes definitions
			//are checked to detect inconsistencies or to redefine undefined traits.
			processReadValue(curAt, def, att, inputOutput, count, curCount, instanceNum);

			//Finally, the counter of read attributes is updated.
			count++;
		} //end of the while

		//Checking if the instance doesn't have the same number of attributes than defined.
		if(count != Attributes.getNumAttributes()) {
			ErrorInfo er = new ErrorInfo(ErrorInfo.BadNumberOfValues, instanceNum, InstanceParser.lineCounter, 0, 0, isTrain,
					("Instance "+def+" has a different number of attributes than defined\n   > Number of attributes defined: "+Attributes.getNumAttributes()+"   > Number of attributes read:    "+count));
			InstanceSet.errorLogger.setError(er);
		}

		//Compute the statistics
		if (isTrain){
			Attribute [] atts = Attributes.getInputAttributes();

			for (int i=0; i<atts.length; i++){
				if(!missingValues[Instance.ATT_INPUT][i]){
					if (atts[i].getType() == Attribute.NOMINAL && Attributes.getOutputNumAttributes() == 1)
						atts[i].increaseClassFrequency(currentClass, nominalValues[Instance.ATT_INPUT][i]);
					else if ((atts[i].getType() == Attribute.INTEGER || atts[i].getType() == Attribute.REAL) &&
							!missingValues[Instance.ATT_INPUT][i])
						atts[i].addInMeanValue(currentClass, realValues[Instance.ATT_INPUT][i]);
				}
			}
		}

	}//end Instance

	/**
	 * Creates a deep copy of the Instance
	 * @param inst Original Instance to be copied
	 */
	public Instance(Instance inst){
		this.isTrain = inst.isTrain;
		this.numInputAttributes = inst.numInputAttributes;
		this.numOutputAttributes = inst.numOutputAttributes;
		this.numUndefinedAttributes = inst.numUndefinedAttributes;

		this.anyMissingValue = Arrays.copyOf(inst.anyMissingValue, inst.anyMissingValue.length);

		this.nominalValues = new String[inst.nominalValues.length][];
		for(int i=0;i<nominalValues.length;i++){
			this.nominalValues[i] = Arrays.copyOf(inst.nominalValues[i],inst.nominalValues[i].length);
		}

		this.intNominalValues = new int[inst.intNominalValues.length][];
		for(int i=0;i<nominalValues.length;i++){
			this.intNominalValues[i] = Arrays.copyOf(inst.intNominalValues[i],inst.intNominalValues[i].length);
		}

		this.realValues = new double[inst.realValues.length][];
		for(int i=0;i<realValues.length;i++){
			this.realValues[i] = Arrays.copyOf(inst.realValues[i],inst.realValues[i].length);
		}

		this.missingValues = new boolean[inst.missingValues.length][];
		for(int i=0;i<missingValues.length;i++){
			this.missingValues[i] = Arrays.copyOf(inst.missingValues[i],inst.missingValues[i].length);
		}
	}

	/**
	 * Creates an instance from a set of given values. It is supposed that the values
	 * correspond to the current Attributes static definition or InstanceAttributes 
	 * non-static definition (it depends on the InstanceSet to which this new instance 
	 * will belong). If ats is null, we will use the Attributes static definiton. If not
	 * the ats definition instead.
	 * @param values A double array with the values (either real or nominals' index). Missing values are stored as Double.NaN
	 * @param ats The definition of the attributes (optional, if null we use Attributes definition).
	 */
	public Instance(double values[],InstanceAttributes ats){
		Attribute curAt,allat[];
		int inOut,in,out,undef;
		
		//initialise structures
		anyMissingValue = new boolean[3];
		anyMissingValue[0] = false; 
		anyMissingValue[1] = false;
		anyMissingValue[2] = false;
		if(ats==null){
			numInputAttributes  = Attributes.getInputNumAttributes();
			numOutputAttributes = Attributes.getOutputNumAttributes();
			numUndefinedAttributes = Attributes.getNumAttributes() - (numInputAttributes+numOutputAttributes);
		}else{
			numInputAttributes  = ats.getInputNumAttributes();
			numOutputAttributes = ats.getOutputNumAttributes();
			numUndefinedAttributes = ats.getNumAttributes() - (numInputAttributes+numOutputAttributes);
		}
		intNominalValues = new int[3][];
		nominalValues = new String[3][];
		realValues    = new double[3][];
		missingValues = new boolean[3][];
		nominalValues[0]    = new String[numInputAttributes];
		nominalValues[1]    = new String[numOutputAttributes];
		nominalValues[2]    = new String[numUndefinedAttributes];
		intNominalValues[0] = new int[numInputAttributes];
		intNominalValues[1] = new int[numOutputAttributes];
		intNominalValues[2] = new int[numUndefinedAttributes];
		realValues[0]       = new double[numInputAttributes];
		realValues[1]       = new double[numOutputAttributes];
		realValues[2]       = new double[numUndefinedAttributes];
		missingValues[0]    = new boolean[numInputAttributes];
		missingValues[1]    = new boolean[numOutputAttributes];
		missingValues[2]    = new boolean[numUndefinedAttributes];

		for(int i=0;i<numInputAttributes;i++)       missingValues[0][i]=false;   
		for(int i=0;i<numOutputAttributes;i++)      missingValues[1][i]=false;  
		for(int i=0;i<numUndefinedAttributes; i++)  missingValues[2][i]=false;
		
		//take the correct set of Attributes
		if(ats!=null){
			allat = ats.getAttributes();
		}else{
			allat = Attributes.getAttributes();
		}

		//fill the data structures
		in = out = undef = 0;
		for(int i=0;i<values.length;i++){
			curAt = allat[i];
			inOut = 2;
			if(curAt.getDirectionAttribute()==Attribute.INPUT)
				inOut = 0;
			else if(curAt.getDirectionAttribute()==Attribute.OUTPUT)
				inOut = 1;
			
			//is it missing?
			if(new Double(values[i]).isNaN()){
				if(inOut==0){
					missingValues[inOut][in] = true;
					anyMissingValue[inOut] = true;
					in++;
				}else if(inOut==1){
					missingValues[inOut][out] = true;
					anyMissingValue[inOut] = true;
					out++;
				}else{
					missingValues[inOut][undef] = true;
					anyMissingValue[inOut] = true;
					undef++;
				}
			}else if(!(curAt.getType()==Attribute.NOMINAL)){ //is numerical?
				if(inOut==0){
					realValues[inOut][in] = values[i];
					in++;
				}else if(inOut==1){
					realValues[inOut][out] = values[i];
					out++;
				}else{
					realValues[inOut][undef] = values[i];
					undef++;
				}
			}else{ //is nominal
				if(inOut==0){
					intNominalValues[inOut][in] = (int)values[i];
					realValues[inOut][in] = values[i];
					nominalValues[inOut][in] = curAt.getNominalValue((int)values[i]);
					in++;
				}else if(inOut==1){
					intNominalValues[inOut][out] = (int)values[i];
					realValues[inOut][out] = values[i];
					nominalValues[inOut][out] = curAt.getNominalValue((int)values[i]);
					out++;
				}else{
					intNominalValues[inOut][undef] = (int)values[i];
					realValues[inOut][undef] = values[i];
					nominalValues[inOut][undef] = curAt.getNominalValue((int)values[i]);
					undef++;
				}
			}			
		}
	}


	/**
	 * It processes the read value for an attribute
	 * @param curAtt is the current attribute (the value read is from this attribute)
	 * @param def is the whole String
	 * @param inOut is an integer that indicates if the attribute is an input or an output attribute
	 * @param count is a counter of attributes.
	 * @param curCount is an attribute counter relative to the inputs or the output. So, it indicates
	 * that the attribute is the ith attribute of the input or the output.
	 * @param instanceNum is the number of the current instance. It's needed to write output messages
	 * with the maximum possible amount of information.
	 */
	private void processReadValue(Attribute curAtt, String def, String att, int inOut, 
			int count, int curCount, int instanceNum){
		//Checking if there is a missing value.
		if(att.equalsIgnoreCase("<null>") || att.equalsIgnoreCase("?")) {
			Attributes.hasMissing = true;
			missingValues[inOut][curCount]=true;
			anyMissingValue[inOut] = true;
			if (inOut == 1){ //If the output is a missing value, an error is generated.
				ErrorInfo er = new ErrorInfo (ErrorInfo.OutputMissingValue, instanceNum, 
						InstanceParser.lineCounter, curCount, Attribute.OUTPUT, 
						isTrain,
						("Output attribute "+count+" of "+def+" with missing value."));
				InstanceSet.errorLogger.setError(er);
			}
		} else if(Attributes.getAttribute(count).getType()==Attribute.INTEGER ||
				Attributes.getAttribute(count).getType()==Attribute.REAL) {
			try {
				realValues[inOut][curCount]=Double.parseDouble(att);
			} catch(NumberFormatException e) {
				ErrorInfo er = new ErrorInfo(ErrorInfo.BadNumericValue, instanceNum, InstanceParser.lineCounter, curCount, Attribute.INPUT+inOut, isTrain, 
						("Attribute "+count+" of "+def+" is not an integer or real value."));
				InstanceSet.errorLogger.setError(er);
			}
			//Checking if the new train value exceedes the bounds definition in train. The condition
			//also checks if the attribute is defined (is an input or an output).
			if (isTrain && inOut != 2){ 
				if (curAtt.getFixedBounds() && !curAtt.isInBounds(realValues[inOut][curCount])){
					ErrorInfo er = new ErrorInfo(ErrorInfo.TrainNumberOutOfRange, instanceNum, InstanceParser.lineCounter, curCount, Attribute.INPUT+inOut, isTrain, 
							("ERROR READING TRAIN FILE. Value "+realValues[inOut][curCount]+" read for a numeric attribute that is not in the bounds fixed in the attribute '"+curAtt.getName()+"' definition."));
					InstanceSet.errorLogger.setError(er);
				}       
				curAtt.enlargeBounds(realValues[inOut][curCount]);
			}
			else if (inOut!=2){ //In test mode
				realValues[inOut][curCount] = curAtt.rectifyValueInBounds(realValues[inOut][curCount]);
			}
		} else if(Attributes.getAttribute(count).getType()==Attribute.NOMINAL) {
			nominalValues[inOut][curCount]= att; 
			//Testing special cases.
			if (isTrain && inOut!=2){
				if (curAtt.getFixedBounds() && !curAtt.isNominalValue(nominalValues[inOut][curCount])){
					ErrorInfo er = new ErrorInfo(ErrorInfo.TrainNominalOutOfRange, instanceNum, InstanceParser.lineCounter, curCount, Attribute.INPUT+inOut, isTrain, 
							("ERROR READING TRAIN FILE. Value '"+nominalValues[inOut][curCount]+"' read for a nominal attribute that is not in the possible list of values fixed in the attribute '"+curAtt.getName()+"' definition."));
					InstanceSet.errorLogger.setError(er);
				}
				curAtt.addNominalValue(nominalValues[inOut][curCount]);
			}else if (inOut!=2){
				if (curAtt.addTestNominalValue(nominalValues[inOut][curCount])){
					ErrorInfo er = new ErrorInfo(ErrorInfo.TestNominalOutOfRange, instanceNum, InstanceParser.lineCounter, curCount, Attribute.INPUT+inOut, isTrain, 
							("ERROR READING TEST FILE. Value '"+nominalValues[inOut][curCount]+"' read for a nominal attribute that is not in the possible list of values fixed in the attribute '"+curAtt.getName()+"' definition."));
					InstanceSet.errorLogger.setError(er);
				}
			}

			if (inOut != -2){
				intNominalValues[inOut][curCount] = curAtt.convertNominalValue(nominalValues[inOut][curCount]);
				realValues[inOut][curCount] = intNominalValues[inOut][curCount];
			}
		}
	}//end processReadValue



	/**
	 * It reserves all the memory necessary for this instance
	 */
	private void initClassAttributes(){ 
		anyMissingValue = new boolean[3];
		anyMissingValue[0] = false; 
		anyMissingValue[1] = false;
		anyMissingValue[2] = false;
		numInputAttributes  = Attributes.getInputNumAttributes();
		numOutputAttributes = Attributes.getOutputNumAttributes();
		numUndefinedAttributes = Attributes.getNumAttributes() - (numInputAttributes+numOutputAttributes);
		intNominalValues = new int[3][];
		nominalValues = new String[3][];
		realValues    = new double[3][];
		missingValues = new boolean[3][];
		nominalValues[0]    = new String[numInputAttributes];
		nominalValues[1]    = new String[numOutputAttributes];
		nominalValues[2]    = new String[numUndefinedAttributes];
		intNominalValues[0] = new int[numInputAttributes];
		intNominalValues[1] = new int[numOutputAttributes];
		intNominalValues[2] = new int[numUndefinedAttributes];
		realValues[0]       = new double[numInputAttributes];
		realValues[1]       = new double[numOutputAttributes];
		realValues[2]       = new double[numUndefinedAttributes];
		missingValues[0]    = new boolean[numInputAttributes];
		missingValues[1]    = new boolean[numOutputAttributes];
		missingValues[2]    = new boolean[numUndefinedAttributes];

		for(int i=0;i<numInputAttributes;i++)       missingValues[0][i]=false;   
		for(int i=0;i<numOutputAttributes;i++)      missingValues[1][i]=false;  
		for(int i=0;i<numUndefinedAttributes; i++)  missingValues[2][i]=false;

	}//end initClassAttributes



	/**
	 * It prints the instance to the specified PrintWriter.
	 * @param out is the PrintWriter where to print.
	 */
	public void print (PrintWriter out){
		out.print("    > Inputs: ");
		for (int i=0; i<numInputAttributes; i++){
			switch(Attributes.getInputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_INPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_INPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_INPUT][i]);
				break;
			}
		}
		out.print("\n    > Outputs: ");
		for (int i=0; i<numOutputAttributes; i++){
			switch(Attributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			}
		}
		out.print("\n    > Undefined: ");
		for (int i=0; i<numUndefinedAttributes; i++){
			switch(Attributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			}  
		}
	}//end print


	/**
	 * It prints the instance to the specified PrintWriter.
	 * The attribtes order is the same as the one in the 
	 * original file.
	 * @param out is the PrintWriter where to print.
	 */
	public void printAsOriginal (PrintWriter out){
		int inCount = 0, outCount = 0, undefCount=0, count;
		int numAttributes = Attributes.getNumAttributes();
		for (count=0; count<numAttributes; count++){
			Attribute at = Attributes.getAttribute(count);
			switch(at.getDirectionAttribute()){
			case Attribute.INPUT:
				printAttribute(out, Instance.ATT_INPUT,   inCount, at.getType());
				inCount++;
				break;
			case Attribute.OUTPUT:
				printAttribute(out, Instance.ATT_OUTPUT, outCount, at.getType());
				outCount++;
				break;
			case Attribute.DIR_NOT_DEF:
				printAttribute(out, Instance.ATT_NONDEF, undefCount, at.getType());
				undefCount++;
				break;
			}
			if (count+1 <numAttributes) out.print(",");
		}
	}//end printAsOriginal


	/**
	 * Does print an attribute to a PrintWriter
	 */
	private void printAttribute(PrintWriter out, int inOut, int ct, int type){

		if (missingValues[inOut][ct]){
			out.print("<null>");
		}
		else{
			switch(type){
			case Attribute.NOMINAL:
				out.print(nominalValues[inOut][ct]);      
				break;
			case Attribute.INTEGER:
				out.print((int)realValues[inOut][ct]);
				break;
			case Attribute.REAL:
				out.print(realValues[inOut][ct]);
				break;
			}
		}
	}//end printAttribute




	/**
	 * It does print the instance information
	 */
	public void print (){
		System.out.print("  > Inputs ("+numInputAttributes+"): ");
		for (int i=0; i<numInputAttributes; i++){
			if (missingValues[Instance.ATT_INPUT][i]){
				System.out.print("?");
			}
			else{
				switch(Attributes.getInputAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_INPUT][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_INPUT][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_INPUT][i]);
					break;
				}
			}
			System.out.print("  ");
		}
		System.out.print("  > Outputs ("+numOutputAttributes+"): ");
		for (int i=0; i<numOutputAttributes; i++){
			if (missingValues[Instance.ATT_OUTPUT][i]){
				System.out.print("?");
			}
			else{
				switch(Attributes.getOutputAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_OUTPUT][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_OUTPUT][i]);
					break;
				}
			}
			System.out.print("  ");
		}

		System.out.print("  > Undefined ("+numUndefinedAttributes+"): ");
		for (int i=0; i<numUndefinedAttributes; i++){
			if (missingValues[Instance.ATT_NONDEF][i]){
				System.out.print("?");
			}
			else{
				switch(Attributes.getUndefinedAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_NONDEF][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_NONDEF][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_NONDEF][i]);
					break;
				}
			}
			System.out.print("  ");
		}
	}//end print



/////////////////////////////////////////////////////////////////////////////
////////////////////////GET AND SET METHODS ////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////
//	Functions to get all the input attributes, or all the output attributes //
/////////////////////////////////////////////////////////////////////////////

	/**
	 * Get Input Real Values
	 * @return a double[] of size equal to the number of input attributes
	 * with the values of real attributes. Positions of the vector that doesn't
	 * correspond to a real attribute has no rellevant data.
	 */
	public double[] getInputRealValues(){
		return realValues[0];
	}//end getInputRealAttributes

	/**
	 * Get Input Nominal Values
	 * @return a string[] of size equal to the number of input attributes
	 * with the values of nominal attributes. Positions of the vector that 
	 * doesn't correspond to a nominal attribute has no rellevant data.
	 */
	public String[] getInputNominalValues(){
		return nominalValues[0];
	}//end getInputNominalValues

	/**
	 * Get Input Missing Values
	 * @return a boolean[] of size equal to the number of input attributes.
	 * A true value in the position i of a vector indicates that the ith
	 * input value is not known.
	 */  
	public boolean[] getInputMissingValues(){
		return missingValues[0];
	}//end getINputMissingValues


	/**
	 * Get Output Real Values
	 * @return a double[] of size equal to the number of output attributes
	 * with the values of real attributes. Positions of the vector that doesn't
	 * correspond to a real attribute has no rellevant data.
	 */
	public double[] getOutputRealValues(){
		return realValues[1];
	}//end getOutputRealAttributes

	/**
	 * Get Output Nominal Values
	 * @return a string[] of size equal to the number of Output attributes
	 * with the values of nominal attributes. Positions of the vector that 
	 * doesn't correspond to a nominal attribute has no rellevant data.
	 */
	public String[] getOutputNominalValues(){
		return nominalValues[1];
	}//end getOutputNominalValues

	/**
	 * Get Output Missing Values
	 * @return a boolean[] of size equal to the number of Output attributes.
	 * A true value in the position i of a vector indicates that the ith
	 * Output value is not known.
	 */  
	public boolean[] getOutputMissingValues(){
		return missingValues[1];
	}//end getOutputMissingValues


/////////////////////////////////////////////////////////////////////////////
//	Functions to get one term of an input or output attribute          //
/////////////////////////////////////////////////////////////////////////////  


	/**
	 * Get Input Real Values
	 * @return a double with the indicated real input value.
	 */
	public double getInputRealValues(int pos){
		return realValues[0][pos];
	}//end getInputRealAttributes


	/**
	 * Get Input Nominal Values
	 * @return a string with the indicated nominal input value.
	 */
	public String getInputNominalValues(int pos){
		return nominalValues[0][pos];
	}//end getInputNominalValues


	/**
	 * It does return the input nominal value at the specified position. The
	 * nominal value is returned as an integer.
	 * @param pos is the position.
	 * @return an int with the nominal value.
	 */  
	public int getInputNominalValuesInt(int pos){
		return intNominalValues[0][pos];
	}//end getInputNominalValues


	/**
	 * It does return all the input nominal values.
	 * @param pos is the position.
	 * @return an int with the nominal value.
	 */  
	public int[] getInputNominalValuesInt(){
		return intNominalValues[0];
	}//end getInputNominalValues


	/**
	 * Get Input Missing Values
	 * @return a boolean indicating if that input value is missing.
	 */  
	public boolean getInputMissingValues(int pos){
		return missingValues[0][pos];
	}//end getINputMissingValues


	/**
	 * Get Output Real Values
	 * @return a double with the indicated real output value.
	 */
	public double getOutputRealValues(int pos){
		return realValues[1][pos];
	}//end getOutputRealAttributes


	/**
	 * Get Output Nominal Values
	 * @return a string with the indicated nominal output value.
	 */
	public String getOutputNominalValues(int pos){
		return nominalValues[1][pos];
	}//end getOutputNominalValues


	/**
	 * It does return the output value at the specified position
	 * @param pos is the position.
	 * @return an int with the nominal value.
	 */
	public int getOutputNominalValuesInt(int pos){
		return intNominalValues[1][pos];
	}//end getInputNominalValues



	/**
	 * It does return the output value at the specified position
	 * @param pos is the position.
	 * @return an int with the nominal value.
	 */
	public int[] getOutputNominalValuesInt(){
		return intNominalValues[1];
	}//end getInputNominalValues


	/**
	 * Get Output Missing Values
	 * @return a boolean indicating if that output value is missing.
	 */  
	public boolean getOutputMissingValues(int pos){
		return missingValues[1][pos];
	}//end getOutputMissingValues



/////////////////////////////////////////////////////////////////////////////
//	Functions to get all the attributes in a double[]             //
///////////////////////////////////////////////////////////////////////////// 


	/**
	 * It does return all the input values. Doesn't care the type of the attributes. 
	 * Nominal attributes are transformed to an integer, that is codified with a double.
	 * And integer attributes are codified with a double to. So all the values are
	 * returnes as doubles.
	 * @return a double[] with all input values.
	 */ 
	public double[] getAllInputValues(){
		return realValues[0];
	}//end getAllInputValues


	/** 
	 * It does return the normalized values in a double[]. It means that integers are
	 * normalized o [0..N], reals to [0..1] and nominals are transformed to an integer
	 * value between [0..N], where N is the number of values that this nominal can take.
	 * In addition, missing values are represented with a -1 value.
	 */
	public double[] getNormalizedInputValues(){
		double [] norm = new double[realValues[0].length];
		for (int i=0; i<norm.length; i++){
			if (!missingValues[0][i])
				norm[i] = Attributes.getInputAttribute(i).normalizeValue(realValues[0][i]);
			else   
				norm[i] = -1.;
		}
		return norm;
	}//end getNormalizedInputValues


	/** 
	 * It does return the normalized values in a double[]. It means that integers are
	 * normalized o [0..N], reals to [0..1] and nominals are transformed to an integer
	 * value between [0..N], where N is the number of values that this nominal can take.
	 */
	public double[] getNormalizedOutputValues(){
		double [] norm = new double[realValues[1].length];
		for (int i=0; i<norm.length; i++){
			if (!missingValues[1][i])
				norm[i] = Attributes.getOutputAttribute(i).normalizeValue(realValues[1][i]);
			else
				norm[i] = -1.;
		}
		return norm;
	}//end getNormalizedOutputValues


	/**
	 * It does return all the output values. Doesn't care the type of the attributes. 
	 * Nominal attributes are transformed to an integer, that is codified with a double.
	 * And integer attributes are codified with a double to. So all the values are
	 * returnes as doubles.
	 * @return a double[] with all output values.
	 */
	public double[] getAllOutputValues(){
		return realValues[1];
	}//end getAllOutputValues





/////////////////////////////////////////////////////////////////////////////
//	Functions to set values to an instance                  //
///////////////////////////////////////////////////////////////////////////// 

	/**
	 * It changes the attribute value. If it can't do that, it returns a false
	 * value.
	 * @param pos is the attribute that has to be changed
	 * @param value is the new value
	 * @return a boolean in false state if the update of the value can't have
	 * been done.
	 */
	public boolean setInputNumericValue(int pos, double value){
		Attribute at = (Attribute)Attributes.getInputAttribute(pos);
		if (at.getType() == Attribute.NOMINAL) return false;
		else{
			if (at.isInBounds(value)){
				realValues[0][pos] = value;
				missingValues[0][pos] = false;
				anyMissingValue[0] = false;
				for (int i=0; i<missingValues[0].length; i++) 
					anyMissingValue[0] |= missingValues[0][i];
			}
			else return false;
		}
		return true;
	}// end setInputNumericValue


	/**
	 * It changes the attribute value. If it can't do that, it returns a false
	 * value.
	 * @param pos is the attribute that has to be changed
	 * @param value is the new value
	 * @return a boolean in false state if the update of the value can't have
	 * been done.
	 */
	public boolean setOutputNumericValue(int pos, double value){
		Attribute at = (Attribute)Attributes.getOutputAttribute(pos);
		if (at.getType() == Attribute.NOMINAL) return false;
		else{
			if (at.isInBounds(value)){
				realValues[1][pos] = value;
				missingValues[1][pos] = false;
				anyMissingValue[1] = false;
				for (int i=0; i<missingValues[1].length; i++) 
					anyMissingValue[1] |= missingValues[1][i];
			}    
			else return false;
		}
		return true;
	}// end setInputNumericValue


	/**
	 * It set the nominal attribute value to the one passed.
	 * @param pos is the position of the attribute.
	 * @param value is the new value. 
	 * @return boolean set to false if the update has not been done.
	 */
	public boolean setInputNominalValue(int pos, String value){
		Attribute at = (Attribute)Attributes.getInputAttribute(pos);
		if (at.getType() != Attribute.NOMINAL) return false;
		else{
			if (at.convertNominalValue(value) != -1){
				nominalValues[0][pos] = value;
				intNominalValues[0][pos] = at.convertNominalValue(value);
				realValues[0][pos] = intNominalValues[0][pos];
				missingValues[0][pos] = false;
				anyMissingValue[0] = false;
				for (int i=0; i<missingValues[0].length; i++) 
					anyMissingValue[0] |= missingValues[0][i];
			}
			else return false;
		}
		return true;
	}//end setInputNominalValue



	/**
	 * It set the nominal attribute value to the one passed.
	 * @param pos is the position of the attribute.
	 * @param value is the new value. 
	 * @return boolean set to false if the update has not been done.
	 */
	public boolean setOutputNominalValue(int pos, String value){
		Attribute at = (Attribute)Attributes.getOutputAttribute(pos);
		if (at.getType() != Attribute.NOMINAL) return false;
		else{
			if (at.convertNominalValue(value) != -1){
				nominalValues[1][pos] = value;
				intNominalValues[1][pos] = at.convertNominalValue(value);
				realValues[1][pos] = intNominalValues[0][pos];
				missingValues[1][pos] = false;
				anyMissingValue[1] = false;
				for (int i=0; i<missingValues[1].length; i++) 
					anyMissingValue[1] |= missingValues[1][i];
			}
			else return false;
		}
		return true;
	}//end setOutputNominalValue


/////////////////////////////////////////////////////////////////////////////
//	General questions about the instance                 //
///////////////////////////////////////////////////////////////////////////// 

	/**
	 * It returns if there is any missing value.
	 * @return a boolean indicating if there's any missing value.
	 */
	public boolean existsAnyMissingValue(){
		return (anyMissingValue[0] || anyMissingValue[1]);
	}//end existsAnyMissingValue


	/**
	 * It informs about the existence of missing values in the inputs
	 * @return a boolean indicating if there's any missing value in the input
	 */
	public boolean existsInputMissingValues(){
		return anyMissingValue[0];
	}//end existsInputMissingValues


	/**
	 * It informs about the existence of missing values in the outputs.
	 * @return a boolean indicating if there's any missing value in the outputs.
	 */
	public boolean existsOutputMissingValues(){
		return anyMissingValue[1];
	}//end existsOutputMissingValues


/////////////////////////////////////////////////////////////////////////////
//	Removing an attribute of the instance                 //
/////////////////////////////////////////////////////////////////////////////
	/**
	 * It does remove the values of one attribute of the instance. 
	 * @param attToDel is a reference to the attribute to be deleted. 
	 * @param inputAtt is a boolean that indicates if the attribute to be removed
	 * is an input attribute (otherwise is an output attribute)
	 * @param whichAtt is the position of the attribute to be deleted.
	 */
	void removeAttribute(Attribute attToDel, boolean inputAtt, int whichAtt){
		int newSize;

		//Getting the vector
		int index = 0;
		if (!inputAtt){ 
			newSize = --numOutputAttributes;
			index = 1;
		}else newSize = --numInputAttributes;

		//The number of undefined attributes is increased. 
		++numUndefinedAttributes;

		//It search the absolute position of the attribute to be
		//removed in the list of undefined attributes
		int undefPosition = Attributes.searchUndefPosition(attToDel);

		//Reserving auxiliar memory to reconstruct the input or output
		String [] nominalValuesAux  = new String[newSize];
		int [] intNominalValuesAux  = new int[newSize];
		double [] realValuesAux     = new double[newSize];
		boolean [] missingValuesAux = new boolean[newSize];

		//Reserving auxiliar memory to reconstruct the undefined att's
		String [] nominalValuesUndef    = new String[numUndefinedAttributes];
		int [] intNominalValuesUndef    = new int[numUndefinedAttributes];
		double[] realValuesUndef          = new double[numUndefinedAttributes];
		boolean []missingValuesUndef      = new boolean[numUndefinedAttributes];

		//Copying the values without the removed attribute
		int k=0;
		anyMissingValue[index] = false;
		for (int i=0; i<newSize+1; i++){
			if (i != whichAtt){
				nominalValuesAux[k] = nominalValues[index][i];
				intNominalValuesAux[k] = intNominalValues[index][i];
				realValuesAux[k] = realValues[index][i];
				missingValuesAux[k] = missingValues[index][i];
				if (missingValuesAux[k]) anyMissingValue[index] = true;
				k++;
			}
			else{
				nominalValuesUndef[undefPosition]       = nominalValues[index][i];
				intNominalValuesUndef[undefPosition]    = intNominalValues[index][i];
				realValuesUndef[undefPosition]          = realValues[index][i];
				missingValuesUndef[undefPosition]       = missingValues[index][i];
			}
		}

		//Copying the rest of the undefined values
		k=0;
		for (int i=0; i<numUndefinedAttributes; i++){
			if (i==undefPosition) continue;
			nominalValuesUndef[i]       = nominalValues[Instance.ATT_NONDEF][k];
			intNominalValuesUndef[i]    = intNominalValues[Instance.ATT_NONDEF][k];
			realValuesUndef[i]          = realValues[Instance.ATT_NONDEF][k];
			missingValuesUndef[i]       = missingValues[Instance.ATT_NONDEF][k];
			k++;
		}

		//Copying the new vectors without the information of the removed attribute.
		nominalValues[index]    = nominalValuesAux;
		intNominalValues[index] = intNominalValuesAux;
		realValues[index]       = realValuesAux;
		missingValues[index]    = missingValuesAux; 
		//The undefined attributes
		nominalValues[Instance.ATT_NONDEF] = nominalValuesUndef;
		intNominalValues[Instance.ATT_NONDEF] = intNominalValuesUndef;
		realValues[Instance.ATT_NONDEF] = realValuesUndef;
		missingValues[Instance.ATT_NONDEF] = missingValuesUndef;
	}//end removeAttribute


/////////////////////////////////////////////////////////////////////////////
//	Other Instance functions                        //
///////////////////////////////////////////////////////////////////////////// 

	/**
	 * It does return an string with the instance information. The format is the
	 * same as the read one (keel format). Only are included in the string those 
	 * attributes that are defined as inputs or outputs. So, NON-SPECIFIED-DIRECTION
	 * attributes are not included to this string.
	 * The order followed is: first, all input attributes are writen, in the order
	 * in which they have been read. After that, the output attributes are write.
	 * This can alter the initial order, but never mind if the output writen 
	 * has the inputs and outputs correctly defined. 
	 * @return a String with the attribute information.
	 */
	public String toString(){
		String aux = "";
		String ending = ",";
		for (int i=0; i<numInputAttributes; i++){
			if (i == numInputAttributes-1 &&
					numOutputAttributes == 0) ending = "";
			switch(Attributes.getInputAttribute(i).getType()){
			case Attribute.NOMINAL:
				aux += nominalValues[0][i];      
				break;
			case Attribute.INTEGER:
				aux += (new Integer((int)realValues[0][i])).toString();
				break;
			case Attribute.REAL:
				aux += (new Double (realValues[0][i])).toString();
				break;
			}
			aux += ending;
		}
		ending = ",";
		for (int i=0; i<numOutputAttributes; i++){
			if (i == numOutputAttributes-1) ending = "";
			switch(Attributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				aux += nominalValues[1][i];      
				break;
			case Attribute.INTEGER:
				aux += (new Integer((int)realValues[1][i])).toString();
				break;
			case Attribute.REAL:
				aux += (new Double(realValues[1][i])).toString();
				break;
			}
			aux += ending;
		}  
		return aux;
	}//end toString





//	NEW FUNCTIONS DEFINED FOR NON-STATIC ATTRIBUTES


	public void print (InstanceAttributes instAttributes, PrintWriter out){
		out.print("    > Inputs: ");
		for (int i=0; i<numInputAttributes; i++){
			switch(instAttributes.getInputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_INPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_INPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_INPUT][i]);
				break;
			}
		}
		out.print("\n    > Outputs: ");
		for (int i=0; i<numOutputAttributes; i++){
			switch(instAttributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			}
		}
		out.print("\n    > Undefined: ");
		for (int i=0; i<numUndefinedAttributes; i++){
			switch(instAttributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
				break;
			case Attribute.INTEGER:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			case Attribute.REAL:
				out.print(realValues[Instance.ATT_OUTPUT][i]);
				break;
			}  
		}
	}//end print


	/**
	 * It prints the instance to the specified PrintWriter.
	 * The attribtes order is the same as the one in the 
	 * original file.
	 * @param out is the PrintWriter where to print.
	 */
	public void printAsOriginal (InstanceAttributes instAttributes, PrintWriter out){
		int inCount = 0, outCount = 0, undefCount=0, count;
		int numAttributes = instAttributes.getNumAttributes();
		for (count=0; count<numAttributes; count++){
			Attribute at = instAttributes.getAttribute(count);
			switch(at.getDirectionAttribute()){
			case Attribute.INPUT:
				printAttribute(out, Instance.ATT_INPUT,   inCount, at.getType());
				inCount++;
				break;
			case Attribute.OUTPUT:
				printAttribute(out, Instance.ATT_OUTPUT, outCount, at.getType());
				outCount++;
				break;
			case Attribute.DIR_NOT_DEF:
				printAttribute(out, Instance.ATT_NONDEF, undefCount, at.getType());
				undefCount++;
				break;
			}
			if (count+1 <numAttributes) out.print(",");
		}
	}//end printAsOriginal



	/**
	 * It does print the instance information
	 */
	public void print ( InstanceAttributes instAttributes ){
		System.out.print("  > Inputs ("+numInputAttributes+"): ");

		for (int i=0; i<numInputAttributes; i++){
			if (missingValues[Instance.ATT_INPUT][i]){
				System.out.print("?");
			}
			else{
				switch(instAttributes.getInputAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_INPUT][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_INPUT][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_INPUT][i]);
					break;
				}
			}
			System.out.print("  ");
		}
		System.out.print("  > Outputs ("+numOutputAttributes+"): ");
		for (int i=0; i<numOutputAttributes; i++){
			if (missingValues[Instance.ATT_OUTPUT][i]){
				System.out.print("?");
			}
			else{
				switch(instAttributes.getOutputAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_OUTPUT][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_OUTPUT][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_OUTPUT][i]);
					break;
				}
			}
			System.out.print("  ");
		}

		System.out.print("  > Undefined ("+numUndefinedAttributes+"): ");
		for (int i=0; i<numUndefinedAttributes; i++){
			if (missingValues[Instance.ATT_NONDEF][i]){
				System.out.print("?");
			}
			else{
				switch(instAttributes.getUndefinedAttribute(i).getType()){
				case Attribute.NOMINAL:
					System.out.print(nominalValues[Instance.ATT_NONDEF][i]);      
					break;
				case Attribute.INTEGER:
					System.out.print((int)realValues[Instance.ATT_NONDEF][i]);
					break;
				case Attribute.REAL:
					System.out.print(realValues[Instance.ATT_NONDEF][i]);
					break;
				}
			}
			System.out.print("  ");
		}
	}//end print


	/**
	 * Obtains the normalized input attributes from a InstanceAttribute definition
	 * @param instAttributes The Attributes definition needed to normalize
	 * @return A new allocated array with the input values normalized
	 */
	public double[] getNormalizedInputValues( InstanceAttributes instAttributes ){
		double [] norm = new double[realValues[0].length];
		for (int i=0; i<norm.length; i++){
			if (!missingValues[0][i])
				norm[i] = instAttributes.getInputAttribute(i).normalizeValue(realValues[0][i]);
			else   
				norm[i] = -1.;
		}
		return norm;
	}//end getNormalizedInputValues

	/**
	 * Obtains the normalized output attributes from a InstanceAttribute definition
	 * @param instAttributes The Attributes definition needed to normalize
	 * @return A new allocated array with the output values normalized
	 */
	public double[] getNormalizedOutputValues( InstanceAttributes instAttributes ){
		double [] norm = new double[realValues[1].length];
		for (int i=0; i<norm.length; i++){
			if (!missingValues[1][i])
				norm[i] = instAttributes.getOutputAttribute(i).normalizeValue(realValues[1][i]);
			else
				norm[i] = -1.;
		}
		return norm;
	} //end getNormalizedOutputValues

	/**
	 * Set a new value of a given input attribute in this instance (integer or real)
	 * @param instAttributes The Attributes reference definition
	 * @param pos The position of the input attribute to be changed in instAttributes
	 * @param value The new value
	 * @return true if succeeded, false otherwise
	 */
	public boolean setInputNumericValue(InstanceAttributes instAttributes, int pos, double value){
		Attribute at = (Attribute)instAttributes.getInputAttribute(pos);
		if (at.getType() == Attribute.NOMINAL) return false;
		else{
			if (at.isInBounds(value)){
				realValues[0][pos] = value;
				missingValues[0][pos] = false;
				anyMissingValue[0] = false;
				for (int i=0; i<missingValues[0].length; i++) 
					anyMissingValue[0] |= missingValues[0][i];
			}
			else return false;
		}
		return true;
	}// end setInputNumericValue

	/**
	 * Set a new value of a given output attribute in this instance (integer or real)
	 * @param instAttributes The Attributes reference definition
	 * @param pos The position of the output attribute to be changed in instAttributes
	 * @param value The new value
	 * @return true if succeeded, false otherwise
	 */
	public boolean setOutputNumericValue(InstanceAttributes instAttributes, int pos, double value){
		Attribute at = (Attribute)instAttributes.getOutputAttribute(pos);
		if (at.getType() == Attribute.NOMINAL) return false;
		else{
			if (at.isInBounds(value)){
				realValues[1][pos] = value;
				missingValues[1][pos] = false;
				anyMissingValue[1] = false;
				for (int i=0; i<missingValues[1].length; i++) 
					anyMissingValue[1] |= missingValues[1][i];
			}    
			else return false;
		}
		return true;
	}// end setInputNumericValue

	/**
	 * Set a new value of a given input attribute in this instance (nominal)
	 * @param instAttributes The Attributes reference definition
	 * @param pos The position of the input attribute to be changed in instAttributes
	 * @param value The new value
	 * @return true if succeeded, false otherwise
	 */
	public boolean setInputNominalValue(InstanceAttributes instAttributes, int pos, String value){
		Attribute at = (Attribute)instAttributes.getInputAttribute(pos);
		if (at.getType() != Attribute.NOMINAL) return false;
		else{
			if (at.convertNominalValue(value) != -1){
				nominalValues[0][pos] = value;
				intNominalValues[0][pos] = at.convertNominalValue(value);
				realValues[0][pos] = intNominalValues[0][pos];
				missingValues[0][pos] = false;
				anyMissingValue[0] = false;
				for (int i=0; i<missingValues[0].length; i++) 
					anyMissingValue[0] |= missingValues[0][i];
			}
			else return false;
		}
		return true;
	}//end setInputNominalValue

	/**
	 * Set a new value of a given output attribute in this instance (nominal)
	 * @param instAttributes The Attributes reference definition
	 * @param pos The position of the output attribute to be changed in instAttributes
	 * @param value The new value
	 * @return true if succeeded, false otherwise
	 */
	public boolean setOutputNominalValue(InstanceAttributes instAttributes, int pos, String value){
		Attribute at = (Attribute)instAttributes.getOutputAttribute(pos);
		if (at.getType() != Attribute.NOMINAL) return false;
		else{
			if (at.convertNominalValue(value) != -1){
				nominalValues[1][pos] = value;
				intNominalValues[1][pos] = at.convertNominalValue(value);
				realValues[1][pos] = intNominalValues[0][pos];
				missingValues[1][pos] = false;
				anyMissingValue[1] = false;
				for (int i=0; i<missingValues[1].length; i++) 
					anyMissingValue[1] |= missingValues[1][i];
			}
			else return false;
		}
		return true;
	}//end setOutputNominalValue


	void removeAttribute(InstanceAttributes instAttributes, Attribute attToDel, boolean inputAtt, int whichAtt){
		int newSize;

		//Getting the vector
		int index = 0;
		if (!inputAtt){ 
			newSize = --numOutputAttributes;
			index = 1;
		}else newSize = --numInputAttributes;

		//The number of undefined attributes is increased. 
		++numUndefinedAttributes;

		//It search the absolute position of the attribute to be
		//removed in the list of undefined attributes
		int undefPosition = instAttributes.searchUndefPosition(attToDel);

		//Reserving auxiliar memory to reconstruct the input or output
		String [] nominalValuesAux  = new String[newSize];
		int [] intNominalValuesAux  = new int[newSize];
		double [] realValuesAux     = new double[newSize];
		boolean [] missingValuesAux = new boolean[newSize];

		//Reserving auxiliar memory to reconstruct the undefined att's
		String [] nominalValuesUndef    = new String[numUndefinedAttributes];
		int [] intNominalValuesUndef    = new int[numUndefinedAttributes];
		double[] realValuesUndef          = new double[numUndefinedAttributes];
		boolean []missingValuesUndef      = new boolean[numUndefinedAttributes];

		//Copying the values without the removed attribute
		int k=0;
		anyMissingValue[index] = false;
		for (int i=0; i<newSize+1; i++){
			if (i != whichAtt){
				nominalValuesAux[k] = nominalValues[index][i];
				intNominalValuesAux[k] = intNominalValues[index][i];
				realValuesAux[k] = realValues[index][i];
				missingValuesAux[k] = missingValues[index][i];
				if (missingValuesAux[k]) anyMissingValue[index] = true;
				k++;
			}
			else{
				nominalValuesUndef[undefPosition]       = nominalValues[index][i];
				intNominalValuesUndef[undefPosition]    = intNominalValues[index][i];
				realValuesUndef[undefPosition]          = realValues[index][i];
				missingValuesUndef[undefPosition]       = missingValues[index][i];
			}
		}

		//Copying the rest of the undefined values
		k=0;
		for (int i=0; i<numUndefinedAttributes; i++){
			if (i==undefPosition) continue;
			nominalValuesUndef[i]       = nominalValues[Instance.ATT_NONDEF][k];
			intNominalValuesUndef[i]    = intNominalValues[Instance.ATT_NONDEF][k];
			realValuesUndef[i]          = realValues[Instance.ATT_NONDEF][k];
			missingValuesUndef[i]       = missingValues[Instance.ATT_NONDEF][k];
			k++;
		}

		//Copying the new vectors without the information of the removed attribute.
		nominalValues[index]    = nominalValuesAux;
		intNominalValues[index] = intNominalValuesAux;
		realValues[index]       = realValuesAux;
		missingValues[index]    = missingValuesAux; 
		//The undefined attributes
		nominalValues[Instance.ATT_NONDEF] = nominalValuesUndef;
		intNominalValues[Instance.ATT_NONDEF] = intNominalValuesUndef;
		realValues[Instance.ATT_NONDEF] = realValuesUndef;
		missingValues[Instance.ATT_NONDEF] = missingValuesUndef;
	}//end removeAttribute

	/**
	 * Prints the instance in KEEL format, according to the given Attributes definition
	 * @param instAttributes The reference Attributes definition for printing
	 * @return A new allocated String with the instance in KEEL format (CSV).
	 */
	public String toString(InstanceAttributes instAttributes){
		String aux = "";
		String ending = ",";
		for (int i=0; i<numInputAttributes; i++){
			if (i == numInputAttributes-1 &&
					numOutputAttributes == 0) ending = "";
			switch(instAttributes.getInputAttribute(i).getType()){
			case Attribute.NOMINAL:
				aux += nominalValues[0][i];      
				break;
			case Attribute.INTEGER:
				aux += (new Integer((int)realValues[0][i])).toString();
				break;
			case Attribute.REAL:
				aux += (new Double (realValues[0][i])).toString();
				break;
			}
			aux += ending;
		}
		ending = ",";
		for (int i=0; i<numOutputAttributes; i++){
			if (i == numOutputAttributes-1) ending = "";
			switch(instAttributes.getOutputAttribute(i).getType()){
			case Attribute.NOMINAL:
				aux += nominalValues[1][i];      
				break;
			case Attribute.INTEGER:
				aux += (new Integer((int)realValues[1][i])).toString();
				break;
			case Attribute.REAL:
				aux += (new Double(realValues[1][i])).toString();
				break;
			}
			aux += ending;
		}  
		return aux;
	} //end toString 

} //end of the class Instance
