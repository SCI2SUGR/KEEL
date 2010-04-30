package keel.Algorithms.RE_SL_Methods.MamWM;

class Adap {
    public double EC, EL;

    public MiDataset tabla, tabla_tst;
    public BaseR base_reglas;

    public Adap(MiDataset training, MiDataset test, BaseR base) {
        tabla = training;
        tabla_tst = test;
        base_reglas = base;
    }

    public static double Minimo(double x, double y) {
        if (x < y) {
            return (x);
        } else {
            return (y);
        }
    }


    public static double Maximo(double x, double y) {
        if (x > y) {
            return (x);
        } else {
            return (y);
        }
    }


/* -------------------------------------------------------------------------
                               FITNESS FUNCTION
  ------------------------------------------------------------------------- */


	/** Mean Square Error(MSE) and Mean Linear Error(MLE) for training */
    public void Error_tra() {
        int j;
        double suma1, suma2, fuerza;

        for (j = 0, suma1 = suma2 = 0.0; j < tabla.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla.datos[j].ejemplo);
            suma1 += 0.5 *
                    Math.pow(tabla.datos[j].ejemplo[tabla.n_var_estado] -
                             fuerza,
                             2.0);
            suma2 +=
                    Math.abs(tabla.datos[j].ejemplo[tabla.n_var_estado] -
                             fuerza);
        }

        EC = suma1 / (double) tabla.long_tabla;
        EL = suma2 / (double) tabla.long_tabla;
    }

	/** Mean Square Error(MSE) and Mean Linear Error(MLE) for testing */
    public void Error_tst() {
        int j;
        double suma1, suma2, fuerza;

        for (j = 0, suma1 = suma2 = 0.0; j < tabla_tst.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla_tst.datos[j].ejemplo);
            suma1 += 0.5 *
                    Math.pow(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] -
                             fuerza, 2.0);
            suma2 +=
                    Math.abs(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] -
                             fuerza);
        }

        EC = suma1 / (double) tabla_tst.long_tabla;
        EL = suma2 / (double) tabla_tst.long_tabla;
    }


    /* -------------------------------------------------------------------------
                               COMMON FUNCTIONS
     ------------------------------------------------------------------------- */

    /* output */
    public String getSalidaObli(MiDataset tabla_datos) {
        int j;
        double fuerza;
        String salida;

        salida = "@data\n";
        for (j = 0; j < tabla_datos.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla_datos.datos[j].ejemplo);
            salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] +
                    " " + fuerza + " " + "\n";
        }

        salida = salida.substring(0, salida.length() - 1);

        return (salida);
    }
}
