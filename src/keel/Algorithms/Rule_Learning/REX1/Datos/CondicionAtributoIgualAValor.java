package keel.Algorithms.Rule_Learning.REX1.Datos;

import java.util.Iterator;
import java.util.List;

import keel.Algorithms.Rule_Learning.REX1.Utilidades.Pair;

/*
 * Esta clase representa la condición de que unos atributos tengan un cierto valor.
 * Es decir, el método cumpleCondicion() devolverá true si los atributos tienen el valor especificado para cada uno.
 */
public class CondicionAtributoIgualAValor implements CondicionInterface {

	List<Pair<Atributo, String>> valoresDeAtributos;
	
	public CondicionAtributoIgualAValor(List<Pair<Atributo, String>> valoresDeAtributos) {
		this.valoresDeAtributos = valoresDeAtributos;
	}

	@Override
	public boolean cumpleCondicion(Fila fila) {
		for(Iterator<Pair<Atributo, String>> i = valoresDeAtributos.iterator(); i.hasNext();){
			Pair<Atributo, String> elemento = i.next();
			
			if(! fila.obtenerValor(elemento.first).equals(elemento.second)) return false;
		}
		return true;
	}

}
