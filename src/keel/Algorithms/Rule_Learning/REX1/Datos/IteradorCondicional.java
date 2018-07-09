package keel.Algorithms.Rule_Learning.REX1.Datos;

import java.util.Iterator;

/*
 * Este iterador es un iterador que sólo devuelve aquellos elementos que cumplen una determinada condición.
 * Su uso es EXACTAMENTE igual que el de cualquier otro Iterator, salvo que en el constructor, hay que especificar la condición
 * mediante un objeto que implemente la interfaz CondicionInterface.
 */
public class IteradorCondicional implements Iterator<Fila> {

	private final Iterator<Fila> iterador;
	private final CondicionInterface condicion;

	/*
	 * A algún brillantísimo ingeniero de Sun se le ocurrió que nadie en la vida iba
	 * a querer clonar un iterador, asíque hay que liar todo esto para que funcione.
	 */
	private Boolean resultadoUltimoHasNext;
	private Fila fila;

	public IteradorCondicional(CondicionInterface condicion, Dataset dataset) {
		this.condicion = condicion;
		this.iterador = dataset.iterator();
		this.resultadoUltimoHasNext = null;
	}

	/*
	 * Este método hace avanzar el iterador hasta una posición que cumpla la condición
	 */
	private void avanzaIterador(){
		while(iterador.hasNext()){
			fila = iterador.next();
			if(condicion.cumpleCondicion(fila)){
				resultadoUltimoHasNext = Boolean.TRUE;
				return;
			}
		}
		fila = null;
		resultadoUltimoHasNext = Boolean.FALSE;
	}

	@Override
	public boolean hasNext() {
		if(resultadoUltimoHasNext == null){
			avanzaIterador();
		}
		return resultadoUltimoHasNext.booleanValue();
	}

	@Override
	public Fila next() {
		if(resultadoUltimoHasNext == null){
			avanzaIterador();
		}
		resultadoUltimoHasNext = null;
		return fila;
	}

	/*
	 * No se va a permitir eliminar elementos.
	 */
	@Override
	public void remove() {}

}
