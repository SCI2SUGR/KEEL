/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * An <code>Association</code> simply associates a numeric ID with a String description.
 */
public class Association {

    /** The ID */
    protected int m_ID;

    /** The descriptive text */
    protected String m_Readable;

    /**
     * Creates a new <code>Association</code> instance.
     *
     * @param ident the ID for the new Association.
     * @param readable the description for the new Association.
     */
    public Association(int ident, String readable) {
        m_ID = ident;
        m_Readable = readable;
    }

    /**
     * Gets the numeric ID of the Association.
     *
     * @return the ID of the Association.
     */
    public int getID() {
        return m_ID;
    }

    /**
     * Gets the string description of the Association.
     *
     * @return the description of the Association.
     */
    public String getReadable() {
        return m_Readable;
    }
}
