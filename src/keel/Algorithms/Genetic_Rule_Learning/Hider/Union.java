/*
 * Created on 13-abr-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 */
public class Union {
    private Corte corte = new Corte();
    private double bondad = 0;
    private int clase = -1;

    /**
     * Empty constructor
     *
     */
    public Union() {
        this.corte = new Corte();
        this.bondad = 0;
        this.clase = -1;
    }

    /**
     * Constructor
     * @param corte punto de corte
     * @param bondad valor de bondad
     * @param clase clase a la que pertence
     */
    public Union(Corte corte, double bondad, int clase) {
        this.corte = corte;
        this.bondad = bondad;
        this.clase = clase;
    }

    /**
     * @return Returns the bondad.
     */
    public double getBondad() {
        return this.bondad;
    }

    /**
     * @param bondad The bondad to set.
     */
    public void setBondad(double bondad) {
        this.bondad = bondad;
    }

    /**
     * @return Returns the corte.
     */
    public Corte getCorte() {
        return this.corte;
    }

    /**
     * @param corte The corte to set.
     */
    public void setCorte(Corte corte) {
        this.corte = corte;
    }

    public String toString() {
        String s = "";
        s += "Corte: " + this.corte.getCorte() + " ";
        s += "Bondad: " + this.bondad + " ";
        s += "Clase: " + this.clase;

        return s;
    }

    /**
     * @return Returns the clase.
     */
    public int getClase() {
        return this.clase;
    }

    /**
     * @param clase The clase to set.
     */
    public void setClase(int clase) {
        this.clase = clase;
    }
}
