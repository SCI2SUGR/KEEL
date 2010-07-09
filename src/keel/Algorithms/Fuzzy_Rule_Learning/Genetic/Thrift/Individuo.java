package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Thrift;

import org.core.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Individuo {

    public int[] Gene;
    public double Perf;
    private boolean n_e;
    private int ranking;

    public Individuo() {
    }

    public Individuo(int tam) {
        Gene = new int[tam];
        Perf = Double.MAX_VALUE;
        n_e = true;
    }

    public void generaAleatorio(int min, int max) {
        for (int i = 0; i < Gene.length; i++) {
            Gene[i] = Randomize.RandintClosed(min, max);
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

    public void copia(Individuo ind) {
        Gene = new int[ind.size()];
        for (int i = 0; i < ind.size(); i++) {
            Gene[i] = ind.getGene(i);
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

    public void setRanking(int rank){
        ranking = rank;
    }

    public int getRanking(){
        return ranking;
    }

    public Individuo copia(){
        Individuo ind = new Individuo(this.size());
        for (int i = 0; i < this.size(); i++){
            ind.Gene[i] = this.Gene[i];
        }
        ind.Perf = this.Perf;
        ind.n_e = this.n_e;
        ind.ranking = this.ranking;
        return ind;

    }
}
