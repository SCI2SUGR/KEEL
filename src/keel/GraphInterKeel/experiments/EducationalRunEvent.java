package keel.GraphInterKeel.experiments;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */


import java.util.EventObject;

public class EducationalRunEvent<A extends EducationalRun> extends EventObject {

    /**
     * <p>
     * Events for EjecucionDocente (GUI)
     * </p>
     */

    /////////////////////////////////////////////////////////////////
    // --------------------------------------- Serialization constant
    /////////////////////////////////////////////////////////////////
    /**
     * Generado por Eclipse
     */
    private static final long serialVersionUID = 1L;

    /////////////////////////////////////////////////////////////////
    // -------------------------------------------------- Propiedades
    /////////////////////////////////////////////////////////////////
    protected A educationalRun;
    protected Exception exception;

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------ Constructores
    /////////////////////////////////////////////////////////////////
    public EducationalRunEvent(A educationalRun) {
        this(educationalRun, null);
    }

    public EducationalRunEvent(A educationalRun, Exception exception) {
        super(educationalRun);
        this.educationalRun = educationalRun;
        this.exception = exception;
    }

    /////////////////////////////////////////////////////////////////
    // ------------------------------------------------------ Metodos
    /////////////////////////////////////////////////////////////////
    public final A getEducationlRun() {
        return educationalRun;
    }

    public final Exception getException() {
        return exception;
    }
}


