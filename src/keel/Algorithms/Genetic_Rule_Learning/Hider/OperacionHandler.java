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
public class OperacionHandler extends DefaultHandler {

    private FuncionEvaluacionBeanHandler handlerSubir = null;
    private Operacion actual = null;
    private Operacion operacionPadre = null;
    private Operacion operacion1 = null; //Para las sub-operaciones
    private Operacion operacion2 = null; //Para las sub-operaciones
    private OperacionHandler handlerOperacionSubir = null;
    private OperacionHandler handlerOperacionBajar = null;
    private StringBuffer valor1 = null;
    private StringBuffer valor2 = null;
    private boolean etiketa = false;

    private XMLReader parser;
    private String clase1 = "", clase2 = "", signo1 = "", signo2 = "";

    private boolean primero; //It's the first operand

    private int nivel; //Nivel de anidamiento actual


    public OperacionHandler(XMLReader parser,
                            FuncionEvaluacionBeanHandler handler,
                            Operacion opActual) {
        this.parser = parser;
        this.actual = opActual;
        this.handlerSubir = handler;
        this.nivel = 0;
        primero = true;
    }

    public OperacionHandler(XMLReader parser,
                            FuncionEvaluacionBeanHandler handler,
                            OperacionHandler handlerOp, Operacion bean,
                            Operacion padre, int nivel) {
        this.parser = parser;
        this.actual = bean;
        this.operacionPadre = padre;
        this.handlerSubir = handler;
        this.handlerOperacionSubir = handlerOp;
        this.nivel = nivel;
        primero = true;
    }

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attr) throws SAXException {
        if (qName.equals("operation")) {
            if (primero) {
                this.clase1 = attr.getValue("type");
                this.operacion1 = new Operacion(clase1);
                handlerOperacionBajar = new OperacionHandler(parser,
                        handlerSubir, this, operacion1, actual, nivel + 1);
            } else {
                this.clase2 = attr.getValue("type");
                this.operacion2 = new Operacion(clase2);
                handlerOperacionBajar = new OperacionHandler(parser,
                        handlerSubir, this, operacion2, actual, nivel + 1);
            }

            parser.setContentHandler(handlerOperacionBajar);

        } else if (qName.equals("value")) {
            etiketa = true;

            if (primero) {
                signo1 = attr.getValue("sign");
                valor1 = new StringBuffer();
            } else {
                signo2 = attr.getValue("sign");
                valor2 = new StringBuffer();
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws
            SAXException {
        if (qName.equals("operation")) {
            if (this.nivel == 0) {
                if (primero) {
                    primero = false;
                } else {
                    primero = true;
                }

                FuncionEvaluacionBean.setOperacion(actual);
                parser.setContentHandler(handlerSubir);
            } else {
                if (handlerOperacionSubir.primero) {
                    operacionPadre.setValorOperacion1(actual);

                    handlerOperacionSubir.primero = false;
                } else {
                    operacionPadre.setValorOperacion2(actual);

                    //handlerOperacionSubir.primero=true;
                }

                parser.setContentHandler(this.handlerOperacionSubir);
            }

        } else if (qName.equals("value")) {
            if (primero) {
                String v1 = valor1.toString().trim();

                if (signo1.equals("negative")) {
                    if (v1.indexOf("-") >= 0) {
                        v1 = v1.replaceFirst("-", "");
                    } else {
                        v1 = "-" + v1;
                    }
                }
                actual.set(v1);

                actual.setIniOp(v1); //Set the first value in the operation for subtractions and divisions

                primero = false;

            } else {
                String v2 = valor2.toString().trim();
                if (signo2.equals("negative")) {
                    if (v2.indexOf("-") >= 0) {
                        v2 = v2.replaceFirst("-", "");
                    } else {
                        v2 = "-" + v2;
                    }
                }
                actual.set(v2);

                primero = true;

            }

            valor1 = null;
            valor2 = null;
            etiketa = false;
        }

    }


    public void characters(char[] ch, int start, int end) throws SAXException {
        if (etiketa) {
            if (primero) {
                valor1.append(new String(ch, start, end));
            } else {
                valor2.append(new String(ch, start, end));
            }
        }
    }

}

