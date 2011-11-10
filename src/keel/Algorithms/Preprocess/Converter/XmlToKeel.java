/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/*
 * XmlToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> XmlToKeel </b>
 * </p>
 *
 *  Clase extendida de la clase Importer. Esta clase permite convertir
 *  los datos almacenados en un fichero con formato xml a un fichero con
 *  formato de datos Keel.
 *
 * @author Teresa Prieto L√≥pez (UCO)
 * @version 1.0
 */
public class XmlToKeel extends Importer {

//Variable que almacena el nombre de la etiqueta o nodo padre
//que contiene cada una de las instancias de datos.
    private String nameChildrenMain = new String();//Variable auxiliar para almacenar el texto de todos los descendientes de un nodo.
    private String lineAux = new String();//Variable almacena el elemento o etiqueta principal que forma el documento xml.
    private Element root;


    /*
     * Constructor de la Clase XmlToKeel. Inicializa los valores de las variables miembro
     * nullValue (valor nulo para el texto de una etiqueta xml) con el valor del par√°metro
     * nullValueUser, y la variable nameChildrenMain con el valor del par√°metro nameLabelUser.
     *
     * @param nullValueUser. Variable de tipo String con el valor nulo para el texto de
     * una etiqueta xml.
     *
     * @param nameLabelUser. Variable de tipo String con  el nombre de la etiqueta o nodo
     * padre que contiene cada una de las instancias de datos.
     *
     */
    public XmlToKeel(String nullValueUser, String nameLabelUser) {
        nullValue = nullValueUser;
        nameChildrenMain = nameLabelUser;
    }

    /*
     *   Metodo utilizado para convertir los datos almacenados dentro del fichero
     *   con formato xml indicado mediante la variable pathnameInput a
     *   formato keel en el fichero indicado por la ruta pathnameOutput
     *
     * @param pathnameInput ruta del fichero con formato xml
     * @param pathnameOutput ruta con los datos en formato keel
     *
     * @throws Exception */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        Pattern p;
        Matcher m;
        int numElements = 0;
        int j = 0;
        int i = 0;
        int k = 0;
        int type;
        int cont = 0;
        int actualValueInt;
        int labelMain = 0;
        double min;
        double max;
        double actualValue;
        String nameAttribute = new String();
        String nameAttributeInitial = new String();
        String valueAttribute = new String();
        String value = new String();
        String nameChildren = new String();
        Vector nameLabelAll = new Vector();
        Vector nameLabel = new Vector();
        List<Element> firstInstance;
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"·", "È", "Ì", "Û", "˙", "¡", "…", "Õ", "”", "⁄"};
        List elements;

        try {

            SAXBuilder builder = new SAXBuilder(false);

//construyo el arbol en memoria desde el fichero
// que se lo pasar√© por parametro.
            Document doc = builder.build(new File(pathnameInput));

//Si el usuario no introduce ningun nombre de la etiqueta principal
            if (nameChildrenMain.equals("")) {
                //cojo el elemento raiz
                root = doc.getRootElement();

                //todos los hijos que tengan
                elements = root.getChildren();

                numElements = elements.size();

                // Comprobamos si todos los hijos tienen el mismo nombre
                for (i = 0; i < numElements; i++) {
                    String text = ((Element) elements.get(i)).getName();

                    nameLabelAll.add(text);
                    if (!nameLabel.contains(text)) {
                        nameLabel.add(text);
                    }
                }

                cont = 0;
                i = nameLabel.size();
                int[] contNameLabel = new int[i];



                i = 0;
                if (nameLabel.size() > 1) {
                    for (i = 0; i < nameLabel.size(); i++) {
                        cont = 0;
                        value = (String) nameLabel.get(i);

                        for (j = 0; j < nameLabelAll.size(); j++) {
                            if (value.equals((String) nameLabelAll.get(j))) {
                                cont++;
                            }
                        }
                        contNameLabel[i] = cont;

                    }

                }

                j = 0;

                if (contNameLabel.length > 1) {
                    labelMain = contNameLabel[0];

                    for (i = 1; i < contNameLabel.length; i++) {
                        actualValueInt = contNameLabel[i];
                        if (actualValueInt > labelMain) {
                            labelMain = actualValueInt;
                            j = i;
                        }
                    }
                }

                nameChildrenMain = (String) nameLabel.get(j);
            } else {
                root = doc.getRootElement();

                FindParent(root, nameChildrenMain);

                //todos los hijos que tengan
                elements = root.getChildren();

                numElements = elements.size();
            }



//Calculamos el n√∫mero de hijos que tiene cada elemento
            if (numElements > 0) {
                i = 0;
                Element children = (Element) elements.get(i);
                nameChildren = children.getName();

                while (!nameChildren.equals(nameChildrenMain)) {
                    i++;
                    children = (Element) elements.get(i);
                    nameChildren = children.getName();
                }

                firstInstance = ((Element) elements.get(i)).getChildren();
                System.out.println("El fichero tiene " + firstInstance.size() + " atributos");
            } else {
                System.out.println("No hay elementos");
                return;
            }

//Reservamos memoria para almacenar la definici√≥n de los atributos y de los datos
            numAttributes = firstInstance.size();
            attribute = new keel.Dataset.Attribute[numAttributes];
            data = new Vector[numAttributes];
            types = new Vector[numAttributes];

            for (i = 0; i < numAttributes; i++) {
                attribute[i] = new keel.Dataset.Attribute();
                data[i] = new Vector();
                types[i] = new Vector();
            }


            Iterator it = elements.iterator();

            cont = 0;


            while (it.hasNext()) {
                Element children = (Element) it.next();
                nameChildren = (String) children.getName();

                if (nameChildren.equals(nameChildrenMain)) {
                    List instances = children.getChildren();
                    numAttributes = instances.size();

                    for (j = 0; j < numAttributes; j++) {
                        List attributes = ((Element) instances.get(j)).getAttributes();
                        nameAttribute = "";
                        nameAttributeInitial = "";
                        valueAttribute = "";


                        if (attributes.size() > 0) {
                            for (k = 0; k < attributes.size(); k++) {
                                nameAttribute = nameAttribute.concat(((org.jdom.Attribute) attributes.get(k)).getValue() + "");
                            }
                        }

                        if (attributes.size() == 0) {
                            nameAttribute = ((Element) instances.get(j)).getName();

                            nameAttribute = nameAttribute.replace("'", "");
                            nameAttribute = nameAttribute.replace("\r", " ");
                            nameAttribute = nameAttribute.replace("\n", " ");

                            p = Pattern.compile("\\s+");
                            m = p.matcher(nameAttribute);
                            nameAttribute = m.replaceAll(" ");

                            if (nameAttribute.contains(" ")) {
                                StringTokenizer token = new StringTokenizer(nameAttribute, " ");
                                String lineAux = "";
                                if (token.hasMoreTokens()) {
                                    lineAux = token.nextToken();
                                }
                                while (token.hasMoreTokens()) {
                                    lineAux = lineAux.concat(UcFirst(token.nextToken()));
                                }

                                nameAttribute = lineAux;

                            }

                        }


                        if ((((Element) instances.get(j)).getChildren()).size() == 0) {
                            valueAttribute = ((Element) instances.get(j)).getText();
                        } else {
                            valueAttribute = ListChildrenText((Element) instances.get(j), 0);
                        }
                        p = Pattern.compile("^\\s+");
                        m = p.matcher(valueAttribute);
                        valueAttribute = m.replaceAll("");

                        p = Pattern.compile("\\s+$");
                        m = p.matcher(valueAttribute);
                        valueAttribute = m.replaceAll("");

                        valueAttribute = valueAttribute.replace("\r", " ");
                        valueAttribute = valueAttribute.replace("\n", " ");
                        valueAttribute = valueAttribute.replace("&nbsp;", "");
                        valueAttribute = valueAttribute.replace("&lt;", "<");
                        valueAttribute = valueAttribute.replace("&gt;", ">");
                        valueAttribute = valueAttribute.replace("&quot;", "\"");
                        valueAttribute = valueAttribute.replace("&shy;", "-");
                        valueAttribute = valueAttribute.replace("&amp;", "&");
                        valueAttribute = valueAttribute.replace("&lt;", "<");
                        valueAttribute = valueAttribute.replace("&gt;", ">");

                        for (k = 0; k < vowel.length; k++) {
                            valueAttribute = valueAttribute.replace("&" + vowel[k] + "acute;", vowel_accent[k]);
                        }
                        if (valueAttribute.equals("") || valueAttribute.equals("<null>") || valueAttribute.equals(nullValue)) {
                            valueAttribute = "?";
                        }
                        data[j].addElement(valueAttribute);

                        if (cont > 0) {
                            nameAttributeInitial = attribute[j].getName();

                            if (!nameAttributeInitial.equals(nameAttribute)) {
                                System.out.println(" Los atributos no tienen el mismo nombre en todas las instancias");
                                System.out.println(" Hay al menos uno diferente, el atributo " + nameAttributeInitial + " de " + nameAttribute);
                                return;
                            }
                        }

                        nameAttribute = nameAttribute.replace("&nbsp;", "");
                        nameAttribute = nameAttribute.replace("&lt;", "<");
                        nameAttribute = nameAttribute.replace("&gt;", ">");
                        nameAttribute = nameAttribute.replace("&quot;", "\"");
                        nameAttribute = nameAttribute.replace("&shy;", "-");
                        nameAttribute = nameAttribute.replace("&amp;", "&");
                        nameAttribute = nameAttribute.replace("&lt;", "<");
                        nameAttribute = nameAttribute.replace("&gt;", ">");

                        for (k = 0; k < vowel.length; k++) {
                            nameAttribute = nameAttribute.replace("&" + vowel[k] + "acute;", vowel_accent[k]);
                        }
                        if (nameAttribute.equals("") || nameAttribute.equals("?") || nameAttribute.equals("<null>") || nameAttribute.equals(nullValue)) {
                            nameAttribute = "ATTRIBUTE_" + (j + 1) + "";
                        }
                        attribute[j].setName(nameAttribute);


                    }//end for

                    cont++;
                }//end if(children.getName().equals(nameChildrenMain))

            }



            for (i = 0; i < data[0].size(); i++) {
                for (j = 0; j < numAttributes; j++) {
                    value = (String) data[j].elementAt(i);
                    types[j].addElement(DataType(value));
                }
            }



            for (i = 0; i < numAttributes; i++) {
                if (types[i].contains(NOMINAL)) {
                    attribute[i].setType(NOMINAL);
                } else {
                    if (types[i].contains(REAL)) {
                        attribute[i].setType(REAL);
                    } else {
                        if (types[i].contains(INTEGER)) {
                            attribute[i].setType(INTEGER);
                        } else {
                            attribute[i].setType(-1);
                        }
                    }
                }
            }


            for (i = 0; i < data[0].size(); i++) {

                for (j = 0; j < numAttributes; j++) {

                    value = (String) data[j].elementAt(i);

                    type = attribute[j].getType();


                    if (type == NOMINAL) {
                        p = Pattern.compile("[^A-Z√ëa-z√±0-9_-]+");
                        m = p.matcher(value);

                        /**
                         * Cambio hecho para que los nominales con espacios en blanco se dejen
                         * con subrayado bajo "_" y sin comillas simples. Se a√±ade la siguiente linea
                         */
                        value = value.replace(" ", "_");


                        if (m.find() && !value.startsWith("'") && !value.endsWith("'") && !value.equals("?")) {
                            /**
                             * Cambio hecho para que los nominales con espacios en blanco se dejen
                             * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                             */
                            /*
                            //value="'"+value+"'";
                             */
                            data[j].set(i, value);
                        }


                        if (!(attribute[j].isNominalValue(value)) && !value.equals("?")) {
                            attribute[j].addNominalValue(value);
                        }
                    }


                    if (type == INTEGER) {
                        if (!value.equals("?")) {
                            actualValueInt = Integer.valueOf(value);
                            data[j].set(i, actualValueInt);

                            if ((attribute[j].getFixedBounds()) == false) {
                                attribute[j].setBounds(actualValueInt, actualValueInt);
                            } else {
                                min = attribute[j].getMinAttribute();
                                max = attribute[j].getMaxAttribute();
                                if (actualValueInt < min) {
                                    attribute[j].setBounds(actualValueInt, max);
                                }
                                if (actualValueInt > max) {
                                    attribute[j].setBounds(min, actualValueInt);
                                }
                            }
                        }

                    }

                    if (type == REAL) {
                        if (!value.equals("?")) {
                            actualValue = Double.valueOf(value);
                            data[j].set(i, actualValue);

                            if ((attribute[j].getFixedBounds()) == false) {
                                attribute[j].setBounds(actualValue, actualValue);
                            } else {
                                min = attribute[j].getMinAttribute();
                                max = attribute[j].getMaxAttribute();
                                if (actualValue < min) {
                                    attribute[j].setBounds(actualValue, max);
                                }
                                if (actualValue > max) {
                                    attribute[j].setBounds(min, actualValue);
                                }
                            }
                        }
                    }

                }//end while

            }//end while


            File fileInput = new File(pathnameInput);

            nameRelation = fileInput.getName();
            p = Pattern.compile("\\.[A-Za-z]+");
            m = p.matcher(nameRelation);
            nameRelation = m.replaceAll("");

            p = Pattern.compile("\\s+");
            m = p.matcher(nameRelation);
            nameRelation = m.replaceAll("");


        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println(e);
            System.exit(1);

        }

        super.Save(pathnameOutput);

    }//end start


    /*
     *  M√©todo recursivo que devuelve el texto que contiene todos los descendientes
     *  de un nodo o etiqueta de un elemento xml.
     *
     *   @param  Element current que indica que nodo o etiqueta xml actual.
     *   @param  int cont Variable que se utiliza como contador de descendientes.
     *
     *   @return String . Devuelve el valor de la variable auxiliar lineAux
     *   que almacena el texto de todos los descendientes de un nodo separado
     *   cada uno por un espacio en blanco.
     *
     *   @throws Exception
     */
    public String ListChildrenText(Element current, int cont) {

        if (cont == 0) {
            lineAux = "";
        }
        if ((current.getChildren()).size() == 0) {
            lineAux = lineAux.concat(current.getText() + " ");
        }
        List children = current.getChildren();
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            Element child = (Element) iterator.next();
            ListChildrenText(child, cont++);
        }

        return lineAux;

    } //end listChildrenText()

    /*
     *   M√©todo encargado de recorrer todo el √°rbol xml para encontrar
     *   el nodo padre del nodo o etiqueta cuyo nombre coincida con el valor del par√°metro  childrenName.
     *   El nodo padre de dicha etiqueta ser√° asignado a la variable miembro
     *   root.
     *
     *   @param  Element current. Elemento o nodo xml actual.
     *   @param  String childrenName. Variable String que indica el
     *   nombre de la etiqueta a buscar.
     *
     */
    public void FindParent(Element current, String childrenName) {

        if (current.getName().equalsIgnoreCase(childrenName)) {
            this.root = current.getParentElement();
            return;
        } else {
            List children = current.getChildren();
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                Element child = (Element) iterator.next();
                FindParent(child, childrenName);
            }

        }
    }
}//end XmlToKeel

