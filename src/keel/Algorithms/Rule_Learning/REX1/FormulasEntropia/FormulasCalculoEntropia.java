package keel.Algorithms.Rule_Learning.REX1.FormulasEntropia;

import java.util.HashMap;
import java.util.Map;

/*
 *	FormulasCalculoEntropia es una enumeración cuyo objetivo es relacionar cada algoritmo
 *	de cálculo de entropía con su código (el código del algoritmo es el valor del parámetro ENTROP�?A.
 *	Ver fichero /dist/algorithm/methods/REX-1.xml para más información)
 *
 *	Para incluir otro algoritmo de cálculo de la entropía en el sistema, tan sólo será necesario implementar
 *	la interfaz EntropiaInterface, añadir el campo correspondiente en la enumeración y modificar REX-1.xml para permitir el nuevo valor.
 *	(Actualmente sólo permite 2 valores, porque tenemos 2 algoritmos de cálculo de entropía)
 */
public enum FormulasCalculoEntropia {
	ENTROPIA1(new Entropia1()),
	ENTROPIA2(new Entropia2());

	private static Map<Integer, EntropiaInterface> entropias;

	FormulasCalculoEntropia(EntropiaInterface entropia) {
		insertaAlgoritmoEntropia(entropia.getCodigo(), entropia);
	}

	private void insertaAlgoritmoEntropia(Integer codigo, EntropiaInterface entropia){
		if(FormulasCalculoEntropia.entropias == null){
			FormulasCalculoEntropia.entropias = new HashMap<Integer, EntropiaInterface>();
		}
		FormulasCalculoEntropia.entropias.put(codigo, entropia);
	}

	public static EntropiaInterface obtenerAlgoritmo(Integer codigo){
		return FormulasCalculoEntropia.entropias.get(codigo);
	}

}
