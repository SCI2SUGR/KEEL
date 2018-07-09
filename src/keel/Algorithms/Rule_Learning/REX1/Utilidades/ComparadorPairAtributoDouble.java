package keel.Algorithms.Rule_Learning.REX1.Utilidades;

import java.util.Comparator;

import keel.Algorithms.Rule_Learning.REX1.Datos.Atributo;

/*
 * Esta clase comparadora nos permitir� ordenar la lista de atributos por su entro�a utilizando directamente Collections.sort() sobre la lista de pares Atributo-Entrop�a
 */
public class ComparadorPairAtributoDouble implements Comparator<Pair<Atributo, Double>> {

	@Override
	public int compare(Pair<Atributo, Double> arg0, Pair<Atributo, Double> arg1) {
		if(arg0.second>arg1.second) return 1;
		if(arg0.second<arg1.second) return -1;
		return 0;
	}

}
