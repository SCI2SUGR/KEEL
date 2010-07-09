/*
 * KeelToWeka.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> KeelToWeka </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a un fichero con formato Weka.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToWeka extends Exporter {


    /*
     * Constructor de la Clase KeelToWeka. Inicializa el valor
     * de la variable miembro nullValue (valor nulo para un dato).
     * Este valor se inicializa con la cadena <null>.
     */
    public KeelToWeka() {
        nullValue = "<null>";
    }


    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear el fichero de datos Weka indicado en el
     * parámetro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos de
     * salida con formato Weka.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);

    }//end Start()

    /*
     * Método utilizado para crear el fichero con formato Weka
     * indicada la ruta por el parámetro pathnameOutput. Este fichero se crea a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato Weka.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {
        Attribute attributeCurrent = new Attribute();
        String filename = pathnameOutput;
        String nameAttribute = new String();
        String ending = new String();
        String element = new String();
        int i;
        int j;
        int type;
        int numNominalValues;
        double max;
        double min;


        /* Comprobamos si el nombre del fichero tiene la extensión .dat, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".arff")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".arff");
        }
        FileWriter fileWriter = new FileWriter(filename);

        fileWriter.write("@relation " + nameRelation + "\n");

        for (i = 0; i < numAttributes; i++) {
            attributeCurrent = attribute[i];
            nameAttribute = attributeCurrent.getName();
            min = attributeCurrent.getMinAttribute();
            max = attributeCurrent.getMaxAttribute();
            type = attributeCurrent.getType();

            String aux = "@attribute " + nameAttribute;

            switch (type) {
                case 0:
                    numNominalValues = attributeCurrent.getNumNominalValues();

                    if (numNominalValues < 10) {
                        aux += " {";
                        ending = ",";
                        for (j = 0; j < numNominalValues; j++) {
                            if (j == attributeCurrent.getNumNominalValues() - 1) {
                                ending = "";
                            }
                            aux += (String) attributeCurrent.getNominalValue(j) + ending;
                        }
                        aux += '}';
                    } else {
                        aux += " STRING";
                    }
                    break;
                case 1:
                    if (min != 0.0 && max != 0.0) {
                        aux += " INTEGER [" + (new Integer((int) min)).toString();
                        aux += "," + (new Integer((int) max)).toString() + "]";
                    } else {
                        aux += " INTEGER";
                    }
                    break;
                case 2:
                    if (min != 0.0 && max != 0.0) {
                        aux += " NUMERIC [" + (new Double(min)).toString();
                        aux += "," + (new Double(max)).toString() + "]";
                    } else {
                        aux += " NUMERIC";
                    }
                    break;

            }

            fileWriter.write(aux + "\n");
        }


        fileWriter.write("@data" + "\n");

        for (i = 0; i < data[0].size(); i++) {

            for (j = 0; j < numAttributes; j++) {
                element = (String) data[j].elementAt(i);

                Pattern p = Pattern.compile("[^A-ZÑa-zñ0-9_-]+");
                Matcher m = p.matcher(element);

                if (m.find() && !element.equals("?") && !element.equals(nullValue) && attribute[j].getType() == NOMINAL) {
                    element = "'" + element + "'";
                }
                if (j == (numAttributes - 1)) {
                    fileWriter.write(element + "");
                } else {
                    fileWriter.write(element + ",");
                }
            }

            fileWriter.write("\n");
        }

        fileWriter.close();

        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");

    }//end Save()
}//end class KeelToWeka

