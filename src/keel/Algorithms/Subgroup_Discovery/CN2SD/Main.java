package keel.Algorithms.Subgroup_Discovery.CN2SD;

import java.util.StringTokenizer;
import org.core.Fichero;

/**
 * <p>Title: Clase principal del programa</p>
 *
 * <p>Description: Lee los parametros y lanza el algoritmo CN2SD</p>
 *
 * <p>Copyright: Copyright (c) Alberto 2006</p>
 *
 * <p>Company: Yo</p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class Main {
    private String ficheroTrain; //Fichero de entramiento -> Por ejemplo discretizado
    private String ficheroEval; //Fichero de evaluación -> original
    private String ficheroTest;
    private String ficheroSalidatr;
    private String ficheroSalidatst;
    private String ficheroSalida;
    private double nu;
    private double seCubre;
    private int tamEstrella;
    private int multi;
    private int eficacia;

    /** Constructor por defecto */
    public Main() {
    }

    /**
     * Obtiene toda la información necesaria del fichero de parametros<br/>
     * En primer lugar lee el nombre de los archivos de datos de entrenamiento y test<br/>
     * Posteriormente lee los ficheros donde queremos guardar las salidas<br/>
     * Por último lee los parametros del algoritmo, tales como la semilla o el nº de iteraciones<br/>
     *
     * @param nomFichero Nombre del fichero de parametros
     *
     */
    private void preparaArgumentos(String nomFichero) {
        StringTokenizer linea, datos;
        String fichero = Fichero.leeFichero(nomFichero); //guardo todo el fichero como un String para procesarlo:
        String una_linea;
        linea = new StringTokenizer(fichero, "\n\r");
        linea.nextToken(); //Paso del nombre del algoritmo
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //inputData
        ficheroTrain = datos.nextToken();
        ficheroEval = datos.nextToken(); //fichero de evaluación
        ficheroTest = datos.nextToken();
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //outputData
        ficheroSalidatr = datos.nextToken();
        ficheroSalidatst = datos.nextToken();
        ficheroSalida = datos.nextToken();
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //nu
        nu = Double.parseDouble(datos.nextToken());
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //seCubre
        seCubre = Double.parseDouble(datos.nextToken());
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //tamEstrella
        tamEstrella = Integer.parseInt(datos.nextToken());
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //multi
        String multiAux = datos.nextToken();
        multi = 1;
        if (multiAux.compareTo("NO") == 0){
          multi = 0;
        }
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //eficacia
        String eficaciaAux = datos.nextToken();
        eficacia = 0;
        if (eficaciaAux.compareTo("YES") == 0){
          eficacia = 1;
        }
    };

    /**Lanza el programa CN2
     */
    private void ejecutar() {
        CN2SD cn2sd = new CN2SD(ficheroTrain, ficheroEval, ficheroTest, ficheroSalidatr,
                                ficheroSalidatst, ficheroSalida,
                                tamEstrella, nu, seCubre, multi,eficacia);
        if (cn2sd.todoBien()){
            cn2sd.ejecutar();
        }
    }

    /**
     * Programa principal
     * @param args Contendra el nombre del fichero de parametros<br/>
     * Formato:<br/>
     * <em>algorith = &lt;nombre algoritmo></em><br/>
     * <em>inputData = "&lt;fichero training&gt;" "&lt;fichero validacion&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <em>outputData = "&lt;fichero training&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = valor</em> (si se usa semilla)<br/>
     * <em>&lt;Descripcion1&gt; = &lt;valor1&gt;</em><br/>
     * <em>&lt;Descripcion2&gt; = &lt;valor2&gt;</em> ... (por si hay mas argumentos)<br/>
     */
    public static void main(String args[]) {
        Main mimain = new Main();
        mimain.preparaArgumentos(args[0]); //Solo cogere el primer argumento (nombre del fichero)
        System.err.println("Launching CN2SD.");
        mimain.ejecutar();
    }
}
