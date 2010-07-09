/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * Represents a selected value from a finite set of values, where each
 * value is a Tag (i.e. has some string associated with it). Primarily
 * used in schemes to select between alternative behaviours,
 * associating names with the alternative behaviours.
 */
public class SelectedAssociation {

    /** The index of the selected tag */
    protected int m_Selected;

    /** The set of tags to choose from */
    protected Association[] m_Tags;

    /**
     * Creates a new <code>SelectedAssociation</code> instance.
     *
     * @param tagID the id of the selected tag.
     * @param tags an array containing the possible valid Tags.
     * @exception IllegalArgumentException if the selected tag isn't in the array
     * of valid values.
     */
    public SelectedAssociation(int tagID, Association[] tags) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].getID() == tagID) {
                m_Selected = i;
                m_Tags = tags;
                return;
            }
        }
        throw new IllegalArgumentException("Selected tag is not valid");
    }

    /** Returns true if this SelectedAssociation equals another object */
    public boolean equals(Object o) {
        if ((o == null) || !(o.getClass().equals(this.getClass()))) {
            return false;
        }
        SelectedAssociation s = (SelectedAssociation) o;
        if ((s.getTags() == m_Tags)
            && (s.getSelectedTag() == m_Tags[m_Selected])) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Gets the selected Tag.
     *
     * @return the selected Tag.
     */
    public Association getSelectedTag() {
        return m_Tags[m_Selected];
    }

    /**
     * Gets the set of all valid Tags.
     *
     * @return an array containing the valid Tags.
     */
    public Association[] getTags() {
        return m_Tags;
    }
}
