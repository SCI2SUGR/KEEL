/*
 * Created on 22-jul-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 *
 */
public class Resultado {

    private int claseEjemplo;
    private int claseRegla;


    /**
     * @param claseEjemplo
     * @param claseRegla
     */
    public Resultado(int claseEjemplo, int claseRegla) {
        this.claseEjemplo = claseEjemplo;
        this.claseRegla = claseRegla;
    }

    /**
     * @return Returns the claseEjemplo.
     */
    public int getClaseEjemplo() {
        return this.claseEjemplo;
    }

    /**
     * @param claseEjemplo The claseEjemplo to set.
     */
    public void setClaseEjemplo(int claseEjemplo) {
        this.claseEjemplo = claseEjemplo;
    }

    /**
     * @return Returns the claseRegla.
     */
    public int getClaseRegla() {
        return this.claseRegla;
    }

    /**
     * @param claseRegla The claseRegla to set.
     */
    public void setClaseRegla(int claseRegla) {
        this.claseRegla = claseRegla;
    }

    public String toString() {
        return "ClaseRegla=" + claseRegla + " ClaseEjemplo=" + claseEjemplo;
    }
}
