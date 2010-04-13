/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

/**
 * Class to store information about an option. <p>
 *
 * Typical usage: <p>
 *
 * <code>Option myOption = new Option("Uses extended mode.", "E", 0, "-E")); </code><p>
 */
public class Information {

    /** What does this option do? */
    private String m_Description;

    /** The synopsis. */
    private String m_Synopsis;

    /** What's the option's name? */
    private String m_Name;

    /** How many arguments does it take? */
    private int m_NumArguments;

    /**
     * Creates new option with the given parameters.
     *
     * @String description the option's description
     * @String name the option's name
     * @String numArguments the number of arguments
     */
    public Information(String description, String name,
                       int numArguments, String synopsis) {

        m_Description = description;
        m_Name = name;
        m_NumArguments = numArguments;
        m_Synopsis = synopsis;
    }

    /**
     * Returns the option's description.
     *
     * @return the option's description
     */
    public String description() {

        return m_Description;
    }

    /**
     * Returns the option's name.
     *
     * @return the option's name
     */
    public String name() {

        return m_Name;
    }

    /**
     * Returns the option's number of arguments.
     *
     * @return the option's number of arguments
     */
    public int numArguments() {

        return m_NumArguments;
    }

    /**
     * Returns the option's synopsis.
     *
     * @return the option's synopsis
     */
    public String synopsis() {

        return m_Synopsis;
    }
}
