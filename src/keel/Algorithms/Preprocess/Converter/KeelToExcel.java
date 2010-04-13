/*
 * KeelToExcel.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.File;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

/**
 * <p>
 * <b> KeelToExcel </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a una hoja de cálculo con formato
 * microsoft Excel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToExcel extends Exporter {


    /*
     * Constructor de la Clase KeelToExcel. Inicializa el valor
     * de la variable miembro nullValue (valor nulo para un dato
     * de una celda del fichero excel) con el valor del parámetro nullValueUser.
     *
     * @param nullValueUser. Variable de tipo String con el valor nulo para un dato
     * de una celda del fichero excel
     */
    public KeelToExcel(String nullValueUser) {
        nullValue = nullValueUser;
    }

    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear el fichero de datos Excel indicado en el
     * parámetro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos
     * de salida con formato Excel.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()

    /*
     * Método utilizado para crear una hoja de cálculo de microsoft Excel
     * indicada la ruta por el parámetro pathnameOutput. Este fichero se crea a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato Excel.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int type;
        String filename = new String();
        String element = new String();

        /* Comprobamos si el nombre del fichero tiene la extensión .csv, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".xls")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".xls");
        }
        WritableWorkbook workbook = Workbook.createWorkbook(new File(pathnameOutput));
        WritableSheet sheet = workbook.createSheet("Sheet 1", 0);


        for (i = 0; i < numAttributes; i++) {
            String nameAttribute = (String) attribute[i].getName();
            nameAttribute = nameAttribute.replace("'", "");

            Label label = new Label(i, 0, nameAttribute);
            sheet.addCell(label);

        }



        for (i = 0; i < data[0].size(); i++) {
            for (j = 0; j < numAttributes; j++) {
                type = attribute[j].getType();

                element = (String) data[j].elementAt(i);

                element = element.replace("\"", "");

                if (type == REAL) {
                    if (!element.contains(".")) {
                        element = element.concat(".0");
                    }
                    Number number = new Number(j, i + 1, Double.valueOf(element));
                    sheet.addCell(number);
                } else {
                    Label label = new Label(j, i + 1, element);
                    sheet.addCell(label);
                }
            }
        }

        workbook.write();
        workbook.close();



        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");


    }//end Save()
}// end class KeelToExcel
