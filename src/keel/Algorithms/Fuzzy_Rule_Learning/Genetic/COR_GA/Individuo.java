package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.COR_GA;

import org.core.*;

/**
 * <p>Title: Individual </p>
 *
 * <p>Description: Describe a chromosome for the GA</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author A. Fernandez
 * @version 1.0
 */
public class Individuo {

    private int[] Gene;
    private double[] Peso;
    public double Perf;
    private boolean n_e;
    private int ranking;

    public Individuo() {
    }

    public Individuo(Espacio subEspacio) {
        int tam = subEspacio.size();
        Gene = new int[tam];

        for (int i = 0; i < tam; i++) {
            Gene[i] = subEspacio.get(i).getConsecuente(Randomize.RandintClosed(0, subEspacio.numConsecuentes(i)-2));
        }

        Peso = new double[tam];

        for (int i = 0; i < tam; i++) {
            Peso[i] = 1.0;
        }

        Perf = Double.MAX_VALUE;
        n_e = true;
    }

    public Individuo(int tam) {
        Gene = new int[tam];
        Peso = new double[tam];
        Perf = Double.MAX_VALUE;
        n_e = true;
    }

    public void generaAleatorio(int min, int max) {
        for (int i = 0; i < Gene.length; i++) {
            Gene[i] = Randomize.RandintClosed(min, max);
        }
    }

    public void generaAleatorio(double min, double max) {
        for (int i = 0; i < Peso.length; i++) {
            Peso[i] = Randomize.RanddoubleClosed(min, max);
        }
    }


    public int size() {
        return Gene.length;
    }

    public int [] getGene(){
        return Gene;
    }

    public int getGene(int pos) {
        return Gene[pos];
    }

    public void setGene(int pos, int value) {
        Gene[pos] = value;
    }

    public double getPeso(int pos) {
        return Peso[pos];
    }

    public double [] getPeso(){
        return Peso;
    }

    public void setPeso(int pos, double value) {
        Peso[pos] = value;
    }

    public void copia(Individuo ind) {
        Gene = new int[ind.size()];
        Peso = new double[ind.size()];
        for (int i = 0; i < ind.size(); i++) {
            Gene[i] = ind.getGene(i);
            Peso[i] = ind.getPeso(i);
        }
    }

    public boolean noEvaluado(){
        return n_e;
    }

    public void evaluado(){
        n_e = false;
    }

    public void setNoEvaluado(){
        n_e = true;
    }

    public void print(){
        for (int i = 0; i < Gene.length-1; i++){
            System.out.print(Gene[i]+", ");
        }
        System.out.println(Gene[Gene.length-1]);
    }

    public void printPeso(){
        for (int i = 0; i < Peso.length-1; i++){
            System.out.print(Peso[i]+", ");
        }
        System.out.println(Peso[Peso.length-1]);
    }


    public void setRanking(int rank){
        ranking = rank;
    }

    public int getRanking(){
        return ranking;
    }

    public Individuo copia(){
        Individuo ind = new Individuo(Gene.length);
        for (int i = 0; i < this.size(); i++){
            ind.Gene[i] = this.Gene[i];
            ind.Peso[i] = this.Peso[i];
        }
        ind.Perf = this.Perf;
        ind.n_e = this.n_e;
        ind.ranking = this.ranking;
        return ind;

    }
}
