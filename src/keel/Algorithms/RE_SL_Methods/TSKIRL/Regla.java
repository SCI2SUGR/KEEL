package keel.Algorithms.RE_SL_Methods.TSKIRL;
class Regla {

    /* each rue of the population has this form */
	public Difuso [] Ant; 
	public double [] Cons;

	public Regla(int n_ant, int n_cons) {
		int i;

		Ant = new Difuso[n_ant];
		Cons = new double[n_cons];

		for (i=0; i<n_ant; i++)  Ant[i] = new Difuso();
	}

}
