package keel.Algorithms.RE_SL_Methods.TSKIRL;
class TTABLA {
	/* Each instance has this form */
	public double [] ejemplo; /* data */
	public int n_variables;   /* number of variables */
	public double nivel_cubrimiento, maximo_cubrimiento; /* matching degree */
	public int cubierto;      /* it's 1 if the instance is covered */

	public TTABLA (int var) {
		n_variables = var;
		ejemplo = new double[n_variables];

		nivel_cubrimiento = (double) 0.0;
		maximo_cubrimiento = (double) 0.0;
		cubierto = 0;
	}
}
