package keel.Algorithms.Rule_Learning.REX1.Datos;

public class CondicionFilaNoMarcada implements CondicionInterface {

	/*
	 * Esta clase representa la condición de que la fila esté marcada.
	 */
	@Override
	public boolean cumpleCondicion(Fila fila) {
		return !fila.isMarcada();
	}

}
