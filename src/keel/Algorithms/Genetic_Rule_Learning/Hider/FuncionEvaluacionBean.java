/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
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
 * Created on 21-ago-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Sebas
 */
public class FuncionEvaluacionBean {
    private static Operacion operacion = null;

    private FuncionEvaluacionBean(String filename) throws
            ParserConfigurationException, IOException, SAXException {
        FileReader xml;
        StringBuffer s = new StringBuffer();
        xml = new FileReader(filename);

        int aux = xml.read();
        while (aux != -1) {
            s.append((char) aux);
            aux = xml.read();
        }
        xml.close();

        parsea(s.toString());
    }

    public static Operacion getOperacion(String filename) throws
            ParserConfigurationException, IOException, SAXException {
        Operacion dev = null;

        if (operacion == null) {
            new FuncionEvaluacionBean(filename);
        }

        dev = operacion;

        return dev;
    }

    private void parsea(String xml) throws ParserConfigurationException,
            IOException, SAXException {
        SAXParserFactory spf = null;
        SAXParser saxParser = null;
        XMLReader xmlReader = null;

        spf = SAXParserFactory.newInstance();

        saxParser = spf.newSAXParser();

        xmlReader = saxParser.getXMLReader();

        xmlReader.setContentHandler(new FuncionEvaluacionBeanHandler(xmlReader));

        StringReader reader = new StringReader(xml);

        InputSource source = new InputSource(reader);

        xmlReader.parse(source);
    }

    /**
     * @param oper The operacion to set.
     */
    public static void setOperacion(Operacion oper) {
        operacion = oper;
    }
}

