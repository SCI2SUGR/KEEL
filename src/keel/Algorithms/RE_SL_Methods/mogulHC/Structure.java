package keel.Algorithms.RE_SL_Methods.mogulHC;

class Structure {

    /* Each member of the population has this form */
    public double[] Gene;
    public char [] GeneSel;
    public int n_genes;
    public double Perf;
    public int n_e;

    public Structure(int genes) {
        n_genes = genes;
        Gene = new double[n_genes];
        GeneSel = new char [n_genes];
    }

}
