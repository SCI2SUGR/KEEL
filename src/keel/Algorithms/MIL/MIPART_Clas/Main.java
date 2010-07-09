package keel.Algorithms.MIL.MIPART_Clas;

import java.util.StringTokenizer;
import org.core.Files;

/**
 * <p>Title: MIPART Main Program</p>
 * <p>Description: This is the main class, which is executed when we launch the program</p>
 * @version 1.0
 * @since JDK1.4
 */

public class Main {

    private String ficheroTrain;
    private String ficheroEval;
    private String ficheroTest;
    private String ficheroSalidatr;
    private String ficheroSalidatst;
    private String ficheroSalida;
    private long semilla;
    private String method;
    //private double w;

    /** Default builder */
    public Main() {
    
    }

    /**
     * It obtains all the necessary information of the parameter file<br/>
     * First, it reads the names of the training and tests data-set files<br/>
     * Then, it reads the output files<br/>
     * Finally, it reads the algorithm parameters, such as the seed or the number of iterations<br/>
     *
     * @param nomFichero Name of the parameter file
     *
     */
    private void preparaArgumentos(String nomFichero) {
        StringTokenizer linea, datos;
        System.out.println("Nombre del fichero" + nomFichero);
        
        String fichero = Files.readFile(nomFichero);
        String una_linea;
        linea = new StringTokenizer(fichero, "\n\r");
        linea.nextToken(); //Algorithm name
        una_linea = linea.nextToken();
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //inputData
        ficheroTrain = datos.nextToken();
        ficheroEval = datos.nextToken(); //evaluation file
        ficheroTest = datos.nextToken();
        una_linea = linea.nextToken();
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //outputData
        ficheroSalidatr = datos.nextToken();
        ficheroSalidatst = datos.nextToken();
        ficheroSalida = datos.nextToken();
        una_linea = linea.nextToken();
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //seed
        semilla = Long.parseLong(datos.nextToken());
        una_linea = linea.nextToken();
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken();
        method = datos.nextToken();
   
    };

    /**
     * It launches the MISMO program
     */
    private void execute() {
        MIPART mi_part = new MIPART(ficheroTrain, ficheroEval, ficheroTest,
                          ficheroSalidatr, ficheroSalidatst, ficheroSalida,
                          semilla, method);
        if (mi_part.everythingOK()) {
            mi_part.execute();
        }
    }

    /**
     * Main program
     * @param args It contains the name of the parameter file<br/>
     * Format:<br/>
     * <em>algorith = &lt;algorithm name&gt;</em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;validation file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = value</em><br/>
     * <em>&lt;Description1&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;Description2&gt; = &lt;value2&gt;</em> ...<br/>
     */
    public static void main(String[] args) {
        long t_ini = System.currentTimeMillis();
        Main ppal = new Main();
        ppal.preparaArgumentos(args[0]);
        ppal.execute();
        long t_fin = System.currentTimeMillis();
        long t_exec = t_fin - t_ini;
        long hours = t_exec / 3600000;
        long rest = t_exec % 3600000;
        long minutes = rest / 60000;
        rest %= 60000;
        long seconds = rest / 1000;
        rest %= 1000;
        System.out.println("Execution Time: " + hours + ":" + minutes + ":" +
                           seconds + "." + rest);
    }

}
