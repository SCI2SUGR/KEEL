package keel.Algorithms.Genetic_Rule_Learning.DMEL;

public class Condition {
	
	private int attribute;
	private int value;
	
	public Condition () {
		
	}
	
	/**Creates a condition*/
	public Condition (int attr, int val) {
		
		attribute = attr;
		value = val;
	
	}	
	
	/**Creates a copy of a condition*/
	public Condition (Condition c) {

		attribute = c.attribute;
		value = c.value;

	}
	
	public int getAttribute() {
		return attribute;
	}

	public int getValue() {
		return value;
	}
	
	public void setCondition(int attr, int val) {		
		attribute = attr;
		value = val;
	}


	public String toString (myDataset train) {
		
		String cadena = "";		
		cadena += train.nameAttribute(attribute) + " = " + train.valueAttribute(attribute, value);		
		return cadena;
	}
	
	public boolean equals (Object a) {
		
		Condition tmp = (Condition) a;
		
		if (attribute == tmp.attribute && value == tmp.value)
			return true;
		return false;		
	}

}
