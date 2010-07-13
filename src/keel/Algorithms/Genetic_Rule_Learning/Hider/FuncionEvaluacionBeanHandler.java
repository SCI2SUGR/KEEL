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


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author Sebas
 */
public class FuncionEvaluacionBeanHandler extends DefaultHandler {

    private Operacion opActual = null;
    private OperacionHandler operacionHandler = null;
    private XMLReader parser;
    private String clase = "";


    /**
     * Constructor
     * @param parser
     */
    public FuncionEvaluacionBeanHandler(XMLReader parser) {
        this.parser = parser;
    }


    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attr) throws SAXException {
        if (qName.equals("operation")) {
            clase = attr.getValue("type");
            opActual = new Operacion(clase);
            operacionHandler = new OperacionHandler(parser, this, opActual);
            parser.setContentHandler(operacionHandler);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws
            SAXException {
        if (qName.equals("operation")) {
            FuncionEvaluacionBean.setOperacion(opActual);
        }
    }


    public void characters(char[] ch, int start, int end) throws SAXException {
        //Nothing to do...
    }

}

