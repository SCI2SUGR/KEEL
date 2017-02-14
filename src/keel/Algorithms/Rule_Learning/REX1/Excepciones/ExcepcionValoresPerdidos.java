package keel.Algorithms.Rule_Learning.REX1.Excepciones;

public class ExcepcionValoresPerdidos extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 2551979991331967660L;

	@Override
	public String getMessage() {
		return "El dataset especificado contiene valores perdidos. REX-1 sólo puede trabajar con datasets completos. Por favor, elija otro dataset.";
	}
}
