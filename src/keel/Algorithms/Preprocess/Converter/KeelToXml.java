/*
 * KeelToXml.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

/**
 * <p>
 * <b> KeelToXml </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a un fichero con formato Xml.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToXml extends Exporter {


    /*
     * Constructor de la Clase KeelToXml. Inicializa el valor
     * de la variable miembro nullValue (valor nulo para el texto de una etiqueta xml).
     * Este valor se inicializa a cadena vacía.
     *
     */
    public KeelToXml() {
        nullValue = "";
    }

    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear el fichero de datos Xml indicado en el
     * parámetro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos
     * de salida con formato Xml.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        super.Start(pathnameInput);

        Save(pathnameOutput);

    }//end Start()

    /*
     * Método utilizado para crear el fichero con formato Xml
     * indicada la ruta por el parámetro pathnameOutput. Este fichero se crea a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato Xml.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int k;
        String value = new String();
        String filename = new String();
        String nameElement = new String();
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"á", "é", "í", "ó", "ú", "Á", "É", "Í", "Ó", "Ú"};

        /* Comprobamos si el nombre del fichero tiene la extensión .xml, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".xml")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".xml");
        }
        Element root = new Element("root");
        Document myDocument = new Document(root);

        for (i = 0; i < data[0].size(); i++) {
            nameRelation = NameLabelValid(nameRelation);

            if (nameRelation.equals("")) {
                nameRelation = "root";
            }
            Element childrenRoot = new Element(nameRelation);

            for (j = 0; j < numAttributes; j++) {
                nameElement = attribute[j].getName();

                nameElement = NameLabelValid(nameElement);

                if (nameElement.equals("?") || nameElement.equals("<null>")) {
                    nameElement = "ATTRIBUTE_" + (j + 1) + "";
                }
                value = (String) data[j].elementAt(i);

                value = value.replace("\"", "");

                for (k = 0; k < vowel.length; k++) {
                    value = value.replace(vowel_accent[k], "&" + vowel[k] + "acute;");
                }
                Element children = new Element(nameElement).addContent(value);

                childrenRoot.addContent(children);
            }
            root.addContent(childrenRoot);
        }

        try {


            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

            outputter.output(myDocument, new FileWriter(pathnameOutput));

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


        File f = new File(filename);


        System.out.println("Fichero " + f.getName() + " creado correctamente");


    }//end Save()


    /*
     * Método utilizado para convertir el nombre de un atributo del fichero
     * keel a un nombre válido para una etiqueta xml.
     *
     * @param String nameLabelUser. Variable de tipo String con
     * el nombre de un atributo del fichero keel.
     *
     * @return String. Devuelve el nombre del atributo pasado pero con algunos cambios
     * para poder ser el nombre de una etiqueta xml.
     *
     * @throws Exception
     */
    public String NameLabelValid(String nameLabelUser) throws Exception {
        String nameLabel = new String();

        nameLabel = nameLabelUser;

        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");

        nameLabel = nameLabel.replace("'", "");
        nameLabel = nameLabel.replace("\"", "");
        nameLabel = nameLabel.replace(" ", "_");
        nameLabel = nameLabel.replace(":", "_");
        nameLabel = nameLabel.replace(".", "_");
        nameLabel = nameLabel.replace("-", "_");

        String nameAux = nameLabel.toLowerCase();
        if (nameAux.startsWith("xml")) {
            nameLabel = nameLabel.substring(3);
        }
        p = Pattern.compile("[^A-ZÑa-zñ0-9_]+");
        m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");

        p = Pattern.compile("^[0-9]+");
        m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");


        return nameLabel;

    }//end NameLabelValid()
}// end KeelToXml
