package keel.Algorithms.Rule_Learning.REX1.Datos;

import keel.Algorithms.Rule_Learning.REX1.Reglas.Regla;

public class CondicionCubreRegla implements CondicionInterface {
	
	Regla regla;
	
	public CondicionCubreRegla(Regla regla) {
		this.regla = regla;
	}

	@Override
	public boolean cumpleCondicion(Fila fila) {
		return regla.cubre(fila);
	}

}
