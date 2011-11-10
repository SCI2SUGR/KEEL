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
 * PropertyListToKeel.java
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
 * <b> PropertyListToKeel </b>
 * </p>
 *
 *  Clase extendida de la clase Importer. Esta clase permite convertir
 *  un fichero de datos con formato property list (con sintaxis xml) a
 *  formato de datos Keel.
 *
 * @author Teresa Prieto L√≥pez (UCO)
 * @version 1.0
 */
public class PropertyListToKeel extends Importer {

//Variable auxiliar para almacenar el texto de todos los descendientes de un nodo.
    private String lineAux = new String();//Variable almacena el elemento o etiqueta principal que forma el documento xml.
    private Element root;

    /*
     *   Metodo utilizado para convertir los datos almacenados dentro del fichero
     *   con formato property list indicado mediante la variable pathnameInput a
     *   formato keel en el fichero indicado por la ruta pathnameOutput
     *
     * @param pathnameInput ruta del fichero con formato property list.
     * @param pathnameOutput ruta con los datos en formato keel.
     *
     * @throws Exception */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        Pattern p;
        Matcher m;
        int numElements = 0;
        int j = 0;
        int i = 0;
        int k = 0;
        int cont = 0;
        int type;
        int actualValueInt;
        double min;
        double max;
        double actualValue;
        String nameAttribute = new String();
        String nameAttributeInitial = new String();
        String value = new String();
        String nameChildren = new String();
        List<Element> firstInstance;
        Element children;
        Element instance;
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"·", "È", "Ì", "Û", "˙", "¡", "…", "Õ", "”", "⁄"};

        try {

            SAXBuilder builder = new SAXBuilder(false);

            Document doc = builder.build(new File(pathnameInput));
//construyo el arbol en memoria desde el fichero
// que se lo pasar√© por parametro.

            root = doc.getRootElement();

            FindParent(root, "dict");

//todos los hijos que tengan
            List instances = root.getChildren();

            numElements = instances.size();

//Buscamos un hijo con nombre dict para a partir de √©l saber el n√∫mero de atributos
            if (numElements > 0) {
                i = 0;
                children = (Element) instances.get(i);
                nameChildren = children.getName();

                while (!nameChildren.equalsIgnoreCase("dict")) {
                    i++;
                    children = (Element) instances.get(i);
                    nameChildren = children.getName();
                }

                firstInstance = ((Element) instances.get(i)).getChildren();

            } else {
                System.out.println("No hay instancias");
                return;
            }



            for (i = 0; i < firstInstance.size(); i++) {
                children = (Element) firstInstance.get(i);
                nameChildren = children.getName();
                if (nameChildren.equalsIgnoreCase("key")) {
                    numAttributes++;
                }
            }


//Reservamos memoria para almacenar la definici√≥n de los atributos y de los datos
            attribute = new keel.Dataset.Attribute[numAttributes];
            data = new Vector[numAttributes];
            types = new Vector[numAttributes];

            for (i = 0; i < numAttributes; i++) {
                attribute[i] = new keel.Dataset.Attribute();
                data[i] = new Vector();
                types[i] = new Vector();
            }


            Iterator it = instances.iterator();

            i = 0;
            k = 0;

            while (it.hasNext()) {

                instance = (Element) it.next();


                if (instance.getName().equalsIgnoreCase("dict")) {
                    List element = instance.getChildren();
                    numElements = element.size();

                    j = 0;
                    i = 0;

                    while (j < numElements) {
                        children = (Element) element.get(j);

                        if (children.getName().equalsIgnoreCase("key")) {

                            nameAttribute = children.getText();

                            p = Pattern.compile("^\\s+");
                            m = p.matcher(nameAttribute);
                            nameAttribute = m.replaceAll("");

                            p = Pattern.compile("\\s+$");
                            m = p.matcher(nameAttribute);
                            nameAttribute = m.replaceAll("");

                            nameAttribute = nameAttribute.replace("'", "");
                            nameAttribute = nameAttribute.replace("\"", "");
                            nameAttribute = nameAttribute.replace("\r", " ");
                            nameAttribute = nameAttribute.replace("\n", " ");
                            nameAttribute = nameAttribute.replace("&nbsp;", "");
                            nameAttribute = nameAttribute.replace("&lt;", "<");
                            nameAttribute = nameAttribute.replace("&gt;", ">");
                            nameAttribute = nameAttribute.replace("&quot;", "\"");
                            nameAttribute = nameAttribute.replace("&shy;", "-");
                            nameAttribute = nameAttribute.replace("&amp;", "&");
                            nameAttribute = nameAttribute.replace("&lt;", "<");
                            nameAttribute = nameAttribute.replace("&gt;", ">");

                            for (cont = 0; cont < vowel.length; cont++) {
                                nameAttribute = nameAttribute.replace("&" + vowel[cont] + "acute;", vowel_accent[cont]);
                            }
                            p = Pattern.compile("\\s+");
                            m = p.matcher(nameAttribute);
                            nameAttribute = m.replaceAll(" ");

                            if (nameAttribute.contains(" ")) {
                                StringTokenizer tokenUcfirts = new StringTokenizer(nameAttribute, " ");
                                String lineUcfirts = "";
                                if (tokenUcfirts.hasMoreTokens()) {
                                    lineUcfirts = tokenUcfirts.nextToken();
                                }
                                while (tokenUcfirts.hasMoreTokens()) {
                                    lineUcfirts = lineUcfirts.concat(UcFirst(tokenUcfirts.nextToken()));
                                }

                                nameAttribute = lineUcfirts;

                            }


                            if (nameAttribute.equals("") || nameAttribute.equals("?") || nameAttribute.equals("<null>")) {
                                nameAttribute = "ATTRIBUTE_" + (i + 1) + "";
                            }
                            if (k > 0) {
                                nameAttributeInitial = attribute[i].getName();

                                if (!nameAttributeInitial.equalsIgnoreCase(nameAttribute)) {
                                    System.out.println("Los nombres de los atributos no coinciden en todas las instancias");
                                    return;
                                }

                            }//end if


                            attribute[i].setName(nameAttribute);

                        }//end if

                        //El contador 'j' se usa para recorrer los hijos de "dict"
                        j++;

                        children = (Element) element.get(j);

                        if (children.getName().equalsIgnoreCase("array") || children.getName().equalsIgnoreCase("dict")) {
                            value = ListChildrenText(children, 0);
                        } else {
                            value = "";
                            value = children.getText();
                        }

                        p = Pattern.compile("^\\s+");
                        m = p.matcher(value);
                        value = m.replaceAll("");

                        p = Pattern.compile("\\s+$");
                        m = p.matcher(value);
                        value = m.replaceAll("");

                        value = value.replace("\r", " ");
                        value = value.replace("\n", " ");

                        value = value.replace("&nbsp;", "");
                        value = value.replace("&lt;", "<");
                        value = value.replace("&gt;", ">");
                        value = value.replace("&quot;", "\"");
                        value = value.replace("&shy;", "-");
                        value = value.replace("&amp;", "&");
                        value = value.replace("&lt;", "<");
                        value = value.replace("&gt;", ">");

                        for (cont = 0; cont < vowel.length; cont++) {
                            value = value.replace("&" + vowel[cont] + "acute;", vowel_accent[cont]);
                        }
                        if (value.equals("") || value.equals("<null>") || value == null) {
                            value = "?";
                        }
                        data[i].addElement(value);

                        j++;

                        //El valor de 'i' recorre los atributos
                        i++;

                    }//end while(j<numElements)

                    //El contador 'k' recorre las intancias
                    k++;

                }//end if

            }//end while


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

                        if (m.find() && !value.startsWith("'") && !value.endsWith("'") && !value.equals("?")) {
                            value = "'" + value + "'";
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

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        File f = new File(pathnameInput);
        nameRelation = f.getName();
        p = Pattern.compile("\\.[A-Za-z]+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        p = Pattern.compile("\\s+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        super.Save(pathnameOutput);


    }//end Start()

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

    } //end FindParent()


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
}//end PropertyListToKeel

