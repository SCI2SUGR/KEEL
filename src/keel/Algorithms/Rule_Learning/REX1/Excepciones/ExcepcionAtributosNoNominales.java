package keel.Algorithms.Rule_Learning.REX1.Excepciones;

public class ExcepcionAtributosNoNominales extends Throwable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6214133806175084033L;

	@Override
	public String getMessage() {
		return "El dataset especificado contiene atributos que no son nominales. REX-1 sólo puede trabajar con este tipo de atributos. Por favor, elija otro dataset.";
	}

}
