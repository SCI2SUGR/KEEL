package keel.Algorithms.RE_SL_Methods.MamWM;

class BaseD {

    public Difuso[][] BaseDatos;
    public int n_variables;
    public int[] n_etiquetas;

    public TipoIntervalo[] extremos;
    public TipoIntervalo[][] intervalos;


    public BaseD(int MaxEtiquetas, int n_var) {
        int i, j;

        n_variables = n_var;

        intervalos = new TipoIntervalo[n_variables][MaxEtiquetas];
        BaseDatos = new Difuso[n_variables][MaxEtiquetas];

        for (i = 0; i < n_variables; i++) {
            BaseDatos[i] = new Difuso[MaxEtiquetas];
            intervalos[i] = new TipoIntervalo[MaxEtiquetas];
            for (j = 0; j < MaxEtiquetas; j++) {
                BaseDatos[i][j] = new Difuso();
                intervalos[i][j] = new TipoIntervalo();
            }
        }

        n_etiquetas = new int[n_variables];

        extremos = new TipoIntervalo[n_variables];
        for (i = 0; i < n_variables; i++) {
            extremos[i] = new TipoIntervalo();
        }
    }


    // Rounds the generated value for the semantics
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
        for (var = 0; var < n_variables; var++) {
            marca = (extremos[var].max - extremos[var].min) /
                    ((double) n_etiquetas[var] - 1);
            for (etq = 0; etq < n_etiquetas[var]; etq++) {
                valor = extremos[var].min + marca * (etq - 1);
                BaseDatos[var][etq].x0 = Asigna(valor, extremos[var].max);
                valor = extremos[var].min + marca * etq;
                BaseDatos[var][etq].x1 = Asigna(valor, extremos[var].max);
                BaseDatos[var][etq].x2 = BaseDatos[var][etq].x1;
                valor = extremos[var].min + marca * (etq + 1);
                BaseDatos[var][etq].x3 = Asigna(valor, extremos[var].max);
                BaseDatos[var][etq].y = 1;
                BaseDatos[var][etq].Nombre = "V" + (var + 1);
                BaseDatos[var][etq].Etiqueta = "E" + (etq + 1);
            }
        }
    }
}
