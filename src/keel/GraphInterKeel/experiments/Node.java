/**
 * @author Julian Luengo Martin (modifications 19/04/2009)
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.Vector;

public abstract class Node implements Serializable {

    public ExternalObjectDescription dsc;
    public int type;
    public Vector par;
    protected int id;
    protected Point centre;
    static final int type_Dataset = 0;
    static final int type_Algorithm = 1;
    static final int type_userMethod = 2;
    static final int type_Jclec = 3;
    static final int type_Preprocess = 4;
    static final int type_Method = 5;
    static final int type_Postprocess = 6;
    static final int type_Test = 7;
    static final int type_Multiplexor = 8;
    static final int type_Undefined = 9;
    static final int type_Visor = 10;
    static final int m_siNoneError = 100;
    static final int m_siErrorTypeContinuous = 101;
    static final int m_siErrorTypeInteger = 102;
    static final int m_siErrorTypeNominal = 103;
    static final int m_siErrorTypeMissing = 104;
    static final int m_siErrorTypeImprecise = 105;
    static final int m_siErrorTypeMultiClass = 106;
    static final int m_siErrorTypeMultiOutput = 107;
    protected transient Shape figure;
    protected transient Image image;
    protected transient GraphPanel pd;
    protected transient JDialog dialog;
    public boolean m_bInputContinuous;
    public boolean m_bInputInteger;
    public boolean m_bInputNominal;
    public boolean m_bInputMissing;
    public boolean m_bInputImprecise;
    public boolean m_bInputMultiClass;
    public boolean m_bInputMultiOutput;
    public boolean m_bOutputContinuous;
    public boolean m_bOutputInteger;
    public boolean m_bOutputNominal;
    public boolean m_bOutputMissing;
    public boolean m_bOutputImprecise;
    public boolean m_bOutputMultiClass;
    public boolean m_bOutputMultiOutput;
    public String m_sDatasetHasContinuous;
    public String m_sDatasetHasInteger;
    public String m_sDatasetHasNominal;
    public String m_sDatasetHasMissing;
    public String m_sDatasetHasImprecise;
    public String m_sDatasetHasMultiClass;
    public String m_sDatasetHasMultiOutput;

    /*
    public boolean m_bStateContinuous;
    public boolean m_bStateInteger;
    public boolean m_bStateNominal;
    public boolean m_bStateMissing;
    public boolean m_bStateImprecise;
    public boolean m_bStateMultiClass;
    public boolean m_bStateMultiOutput;
     */
    public Node() {
    }

    public Node(ExternalObjectDescription mydsc, Point position, int id) {
        dsc = new ExternalObjectDescription(mydsc);
        centre = new Point(position);
        this.id = id;
    }

    /**
     * Gets the id of the node
     * @return the integer with the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the new id of the node
     * @param iden the new id
     */
    public void setId(int iden) {
        id = iden;
    }

    /**
     * Gets the type of the node
     * @return the type of the node
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the node
     * @param _type the new type of the node
     */
    public void setType(int _type) {
        type = _type;
    }

    /**
     * Sets the position of the node in the panel
     * @param position The current position
     */
    public void setPosicion(Point position) {
        centre.x = position.x;
        centre.y = position.y;
        pd.mainGraph.setModified(true);
    }

    /**
     * Gets the current position of the node
     * @return the spatial position
     */
    public Point getPosicion() {
        return centre;
    }

    /**
     * Sets the position of the node in the panel
     * @param position The current position
     */
    public void setPosition(Point position) {
        centre = position;
    }

    /**
     * Gets the current position of the node
     * @return the spatial position
     */
    public Point getPosition() {
        return centre;
    }

    /**
     * Draws the node in a 2D component
     * @param g2 the 2D graphic element
     * @param select if the node is selected
     */
    public abstract void draw(Graphics2D g2, boolean select);

    /**
     * Test if the provided point is inside of this node
     * @param point Point to be tested
     * @return True if inside, false otherwise
     */
    public boolean isInside(Point point) {
        return figure.contains(point);
    }

    String isPartialFlowCorrect(Node dest) {
//	  correctCurrentState(previous);

        /*System.out.println ( ">> Checking the partial flow correctness " );
        System.out.println ( "  Origen m_bInputContinuous= "+ m_bOutputContinuous + "\tdest continuous = "		+dest.m_bInputContinuous);
        System.out.println ( "  Origen m_bInputInteger = "  + m_bOutputInteger + "\tdest m_bOutputInteger = "	+dest.m_bInputInteger );
        System.out.println ( "  Origen m_bInputInteger= "   + m_bOutputNominal + "\tdest m_bOutputNominal = "	+dest.m_bInputNominal);
        System.out.println ( "  Origen m_bInputMissing = "  + m_bOutputMissing + "\tdest m_bOutputMissing = "	+dest.m_bInputMissing);
        System.out.println ( "  Origen m_bInputImprecise = "+ m_bOutputImprecise + "\tdest m_bOutputImprecise = "	 +dest.m_bInputImprecise);
        System.out.println ( "  Origen m_bInputMultiClass= "+ m_bOutputMultiClass + "\tdest m_bOutputMultiOutput = "+dest.m_bInputMultiOutput );*/

        String errorMessage = "";
        boolean errorOccurred = false;

        if (dest.type != Node.type_Test && dest.type != Node.type_Visor) {


            if (type == Node.type_Dataset) {


                if (!dest.m_bInputContinuous && m_bOutputContinuous) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasContinuous + "' have continuous data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputInteger && m_bOutputInteger) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasInteger + "' have integer data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputNominal && m_bOutputNominal) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasNominal + "' have nominal data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMissing && m_bOutputMissing) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasMissing + "' have missing data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputImprecise && m_bOutputImprecise) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasImprecise + "' have imprecise data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMultiClass && m_bOutputMultiClass) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasMultiClass + "' have multiclass data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMultiOutput && m_bOutputMultiOutput) {
                    errorMessage = errorMessage + "The data sets '" + m_sDatasetHasMultiOutput + "' have multioutput data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }


                if (errorOccurred) {
                    errorMessage = "Conflict with this connection.\n" + errorMessage;
                }
            } else {

                if (!dest.m_bInputContinuous && m_bOutputContinuous) {
                    errorMessage = errorMessage + "The source may generate continuous data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputInteger && m_bOutputInteger) {
                    errorMessage = errorMessage + "The source may generate integer data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputNominal && m_bOutputNominal) {
                    errorMessage = errorMessage + "The source may generate nominal data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMissing && m_bOutputMissing) {
                    errorMessage = errorMessage + "The source may generate missing data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputImprecise && m_bOutputImprecise) {
                    errorMessage = errorMessage + "The source may generate imprecise data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMultiClass && m_bOutputMultiClass) {
                    errorMessage = errorMessage + "The source may generate multiclass data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }
                if (!dest.m_bInputMultiOutput && m_bOutputMultiOutput) {
                    errorMessage = errorMessage + "The source may generate multioutput data and the destination node does not accept it.\n";
                    errorOccurred = true;
                }

                if (errorOccurred) {
                    errorMessage = "Possible conflict with this connection.\n" + errorMessage;
                }

            }
        }

        return errorMessage;
    //return m_siNoneError; //Valor distinto
    }

    /*
    private void correctCurrentState(Nodo previous){
    m_bStateContinuous = previous.m_bStateContinuous && m_bOutputContinuous;
    m_bStateInteger = previous.m_bStateInteger && m_bOutputInteger;
    m_bStateNominal = previous.m_bStateNominal && m_bOutputNominal;
    m_bStateMissing = previous.m_bStateMissing && m_bOutputMissing;
    m_bStateImprecise = previous.m_bStateImprecise && m_bOutputImprecise;
    m_bStateMultiClass = previous.m_bStateMultiClass && m_bOutputMultiClass;
    m_bStateMultiOutput = previous.m_bStateMultiOutput && m_bOutputMultiOutput;
    }
     */
    /**
     * Shows the new dialog associated to this node
     */
    public abstract void showDialog();

    /**
     * It does update the state of the node, regarding the variables
     * that define the types of values accepted as input and output
     */
    public void updateState() {
        //m_bInputContinuous = m_bInputInteger = m_bInputNominal = m_bInputMissing = m_bInputImprecise = m_bInputMultiClass = m_bInputMultiOutput = false;
        //m_bOutputContinuous = m_bOutputInteger = m_bOutputNominal = m_bOutputMissing = m_bOutputImprecise = m_bOutputMultiClass = m_bOutputMultiOutput = false;
    }

    /**
     * Test the input/output capabilities described by the ExternalObjectDescriptor
     * and stores them
     * @param dsc the external object descriptor associated
     * @param p the Draw panel in which this node is contained and depicted
     */
    public void actInputOutput(ExternalObjectDescription dsc, GraphPanel p) {
    }
}
