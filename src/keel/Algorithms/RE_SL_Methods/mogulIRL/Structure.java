package keel.Algorithms.RE_SL_Methods.mogulIRL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
class Structure {

        /* Each member of the population has this form */
        public double [] Gene;
        public int n_genes;
        public double Perf;
        public int n_e;

        public Structure(int genes) {
                n_genes = genes;
                Gene = new double[n_genes];
        }

}
