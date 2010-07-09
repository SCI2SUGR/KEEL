package keel.Algorithms.RE_SL_Postprocess.MamSelect;
class Structure {

	/* each member of the population has this form */
	public char [] Gene;
	public int n_genes;
	public double Perf;
	public int n_e;

	public Structure(int genes) {
		n_genes = genes;
		Gene = new char[n_genes];
	}

}
