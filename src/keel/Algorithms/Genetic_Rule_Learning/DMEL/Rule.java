package keel.Algorithms.Genetic_Rule_Learning.DMEL;

import java.util.Arrays;

public class Rule {
	
	Condition regla[];
	
	public Rule () {
	
	}
	
	/**Creates an empty rule*/
	public Rule (int size) {

		regla = new Condition[size];
	}
	
	/**Creates a rule that includes a set of conditions*/	
	public Rule (Condition cond[]) {
		
		int i;
		
		regla = new Condition[cond.length];
		for (i=0; i<cond.length; i++) {
			regla[i] = new Condition(cond[i]);
		}		
		
	}
	
	/**Creates a copy of a rule*/
	public Rule (Rule a) {
		
		int i;
		
		regla = new Condition[a.getRule().length];
		for (i=0; i<regla.length; i++) {
			regla[i] = new Condition(a.getiCondition(i));
		}		
	}
	
	public Condition[] getRule() {
		return regla;
	}
	
	public Condition getiCondition(int i) {
		return regla[i];
	}
	
	public void setiCondition (int i, Condition c) {
		regla[i].setCondition(c.getAttribute(), c.getValue());
	}
	

	public String toString (myDataset train) {
		
		int i;
		String cadena = "";
				
		cadena += regla[0].toString(train);
		
		for (i=1; i<regla.length; i++) {
			cadena += " AND " + regla[i].toString(train);
		}
		
		return cadena;
	}
	
	public boolean equals (Object a) {
		
		int i, j;
		Rule tmp = (Rule) a;
		boolean mascara[] = new boolean [regla.length];
		
		Arrays.fill(mascara, false);
		
		for (i=0; i<regla.length; i++) {
			for (j=0; j<tmp.getRule().length; j++) {
				if (regla[i].equals(tmp.getiCondition(j))) {
					mascara[j] = true;
				}
			}
		}
		
		for (i=0; i<mascara.length; i++)
			if (!mascara[i])
				return false;
		
		return true;
		
	}
	
}
