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
