package keel.Algorithms.Rule_Learning.REX1.Utilidades;

import java.util.Comparator;

public class ComparadorDoubleAscendente implements Comparator<Double> {

	@Override
	public int compare(Double arg0, Double arg1) {
		if(arg0>arg1) return -1;
		else if(arg0<arg1) return 1;
		return 0;
	}

}
