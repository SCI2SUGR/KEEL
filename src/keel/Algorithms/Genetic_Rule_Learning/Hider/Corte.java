/*
 * Created on 03-abr-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * @author Sebas
 */
public class Corte {

    private int clase = -1;
    private double corte = -1;
    private double bondad = -1;

    /**
     * Empty constructor
     *
     */
    public Corte() {
        clase = -1;
        corte = -1;
        bondad = -1;
    }

    /**
     * Constructor
     * @param clase
     */
    public Corte(int clase) {
        this.clase = clase;
        corte = -1;
        bondad = -1;
    }

    /**
     * Constructor
     * @param corte
     */
    public Corte(double corte) {
        clase = -1;
        this.corte = corte;
        bondad = -1;
    }

    /**
     * Constructor
     * @param corte
     */
    public Corte(Double corte) {
        clase = -1;
        this.corte = corte.doubleValue();
        bondad = -1;
    }

    /**
     * Constructor
     * @param clase
     * @param corte
     */
    public Corte(int clase, double corte) {
        this.clase = clase;
        this.corte = corte;
        bondad = -1;
    }

    /**
     * Constructor
     * @param clase
     * @param corte
     */
    public Corte(int clase, Double corte) {
        this.clase = clase;
        this.corte = corte.doubleValue();
        bondad = -1;
    }

    /**
     * Constructor
     * @param clase
     * @param corte
     * @param bondad
     */
    public Corte(int clase, Double corte, double bondad) {
        this.clase = clase;
        this.corte = corte.doubleValue();
        this.bondad = bondad;
    }

    /**
     * Constructor
     * @param clase
     * @param corte
     */
    public Corte(Integer clase, Double corte) {
        this.clase = clase.intValue();
        this.corte = corte.doubleValue();
        bondad = -1;
    }

    /**
     * Constructor
     * @param clase
     * @param corte
     * @param bondad
     */
    public Corte(Integer clase, Double corte, double bondad) {
        this.clase = clase.intValue();
        this.corte = corte.doubleValue();
        this.bondad = bondad;
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

    /**
     * @return Returns the corte.
     */
    public double getCorte() {
        return this.corte;
    }

    /**
     * @param corte The corte to set.
     */
    public void setCorte(double corte) {
        this.corte = corte;
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
     *
     */
    public String toString() {
        String s = "";

        s += "Corte: " + this.corte;
        s += " Clase: " + this.clase;
        s += " Bondad: " + this.bondad;

        return s;
    }
}
