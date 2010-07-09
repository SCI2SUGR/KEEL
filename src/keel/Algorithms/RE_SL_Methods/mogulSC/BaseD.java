package keel.Algorithms.RE_SL_Methods.mogulSC;

class BaseD {

    public Difuso[][] BaseDatos;
    public int[] n_etiquetas;
    public MiDataset tabla;
    public TipoIntervalo[][] intervalos;

    public BaseD(int MaxEtiquetas, MiDataset t) {
        int i, j;

		tabla = t;

        intervalos = new TipoIntervalo[tabla.n_variables][MaxEtiquetas];
        BaseDatos = new Difuso[tabla.n_variables][MaxEtiquetas];

        for (i = 0; i < tabla.n_variables; i++) {
            BaseDatos[i] = new Difuso[MaxEtiquetas];
            intervalos[i] = new TipoIntervalo[MaxEtiquetas];
            for (j = 0; j < MaxEtiquetas; j++) {
                BaseDatos[i][j] = new Difuso();
                intervalos[i][j] = new TipoIntervalo();
            }
        }

        n_etiquetas = new int[tabla.n_variables];
        for (i = 0; i < tabla.n_variables; i++)
			n_etiquetas[i] = MaxEtiquetas;
    }


    /** Rounds the generated value for the semantics */
    public double Asigna(double val, double tope) {
        if ((val > -1E-4) && (val < 1E-4)) {
            return (0);
        }
        if ((val > tope - 1E-4) && (val < tope + 1E-4)) {
            return (tope);
        }

        return (val);
    }


    /** Generates the semantics of the linguistic variables with triangular fuzzy sets and the mutation intervals to mutate */
    public void Semantica() {
        int var, etq;
        double marca, valor;
        double[] punto = new double[3];
        double[] punto_medio = new double[2];

        /* we generate the fuzzy partitions of the variables */
        for (var = 0; var < tabla.n_variables; var++) {
            marca = (tabla.extremos[var].max - tabla.extremos[var].min) / ((double) n_etiquetas[var] - 1);
            for (etq = 0; etq < n_etiquetas[var]; etq++) {
                valor = tabla.extremos[var].min + marca * (etq - 1);
                BaseDatos[var][etq].x0 = Asigna(valor, tabla.extremos[var].max);
                valor = tabla.extremos[var].min + marca * etq;
                BaseDatos[var][etq].x1 = Asigna(valor, tabla.extremos[var].max);
                BaseDatos[var][etq].x2 = BaseDatos[var][etq].x1;
                valor = tabla.extremos[var].min + marca * (etq + 1);
                BaseDatos[var][etq].x3 = Asigna(valor, tabla.extremos[var].max);
                BaseDatos[var][etq].y = 1;
                BaseDatos[var][etq].Nombre = "V" + (var + 1);
                BaseDatos[var][etq].Etiqueta = "E" + (etq + 1);
            }
        }

        /* we generate the mutation intervals for each gene */
        for (var = 0; var < tabla.n_variables; var++) {
            for (etq = 0; etq < n_etiquetas[var]; etq++) {
                punto[0] = BaseDatos[var][etq].x0;
                punto[1] = BaseDatos[var][etq].x1;
                punto[2] = BaseDatos[var][etq].x3;
                punto_medio[0] = (punto[1] - punto[0]) / 2.0;
                punto_medio[1] = (punto[2] - punto[1]) / 2.0;
                intervalos[var][etq].min = punto[0] - punto_medio[0];
                intervalos[var][etq].max = punto[2] + punto_medio[1];
            }
        }
    }

}
