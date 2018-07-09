package keel.Algorithms.Rule_Learning.REX1.Datos;

public class CondicionFilaNoMarcada implements CondicionInterface {

	/*
	 * Esta clase representa la condici�n de que la fila est� marcada.
	 */
	@Override
	public boolean cumpleCondicion(Fila fila) {
		return !fila.isMarcada();
	}

}
