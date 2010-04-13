package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

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
import java.util.*;
import org.core.*;

public class Poblacion {
    BaseR baseReglas;
    ArrayList<Individuo> poblacion;
    ArrayList<Individuo> hijos;
    double mutProb, w_acc, w_size;
    int n_variables, tam_pobl;
    double mejor_clasif;
    int[] selectos;

    public boolean BETTER(int a, int b) {
        if (a > b) {
            return true;
        }
        return false;
    }

    public Poblacion() {
    }

    public Poblacion(BaseR baseReglas, double w_acc, double w_size,
                     int tamPoblacion, double p_include, double mutProb) {
        this.w_acc = w_acc;
        this.w_size = w_size;
        this.mutProb = mutProb;
        this.baseReglas = baseReglas;
        mejor_clasif = 0;
        init(baseReglas, tamPoblacion, p_include);
        hijos = new ArrayList<Individuo>();
        selectos = new int[tamPoblacion];
    }

    private void init(BaseR baseReglas, int tamPobl, double p_include) {
        poblacion = new ArrayList<Individuo>();
        for (int i = 0; i < tamPobl; i++) {
            Individuo ind = new Individuo(baseReglas, w_acc, w_size, p_include);
            poblacion.add(ind);
        }
    }

    public void Generacion(int n_generaciones) {
        clasifica(poblacion, 0);
        for (int i = 0; i < n_generaciones; i++) {
            selection();
            crossover();
            mutation();
            clasifica(hijos, i); //calcula la BR de cada cromosoma y obtiene su fitness
            elitist();
        }
    }

    private void selection() {
        hijos.clear();
        double[] probabilidades = new double[poblacion.size()];
        Collections.sort(poblacion);
        double f_min = poblacion.get(poblacion.size() - 1).fitness;
        double acumulado = 0;
        for (int i = 0; i < poblacion.size(); i++) {
            probabilidades[i] = poblacion.get(i).fitness - f_min;
            acumulado += (probabilidades[i] - f_min);
        }
        ArrayList<Selectos> vector = new ArrayList<Selectos>();
        for (int i = 0; i < poblacion.size(); i++) {
            probabilidades[i] /= acumulado;
            Selectos s = new Selectos(probabilidades[i], i);
            vector.add(s);
        }

        Collections.sort(vector);

        for (int i = 0; i < poblacion.size(); i++) {
            double aleatorio = Randomize.Rand();
            int j;
            for (j = 0; aleatorio < vector.get(j).probabilidad; j++) {
                ;
            }
            selectos[i] = vector.get(j).posicion;
        }
    }

    private void crossover() {
        for (int i = 0; i < poblacion.size() / 2; i++) {
            Individuo padre = poblacion.get(selectos[i]);
            Individuo madre = poblacion.get(selectos[i + 1]);
            int puntoCorte = Randomize.Randint(1, padre.size() - 1);
            Individuo hijo1 = new Individuo(padre, madre, puntoCorte);
            Individuo hijo2 = new Individuo(madre, padre, puntoCorte);
            hijos.add(hijo1);
            hijos.add(hijo2);
        }
    }

    private void mutation() {
        for (int i = 0; i < hijos.size(); i++) {
            hijos.get(i).mutar(mutProb);
        }
    }

    private void elitist() {
        Collections.sort(poblacion);
        Individuo mejor = poblacion.get(0).clone();
        poblacion.clear();
        poblacion.add(mejor);
        int posicion = Randomize.RandintClosed(0, hijos.size());
        hijos.remove(posicion);
        for (int i = 0; i < hijos.size(); i++) {
            Individuo nuevo = hijos.get(i).clone();
            poblacion.add(nuevo);
        }

    }

    private void clasifica(ArrayList<Individuo> individuos, int generation) {
        boolean entrar = false;
        for (int i = 0; i < individuos.size(); i++) {
            double acc = individuos.get(i).clasifica();
            //System.err.println("Generacion["+generation+"], Individuo("+i+") -> "+acc);
            if (acc > mejor_clasif) {
                mejor_clasif = acc;
                entrar = true;
            }
        }
        if (entrar) {
            System.out.println("Best Accuracy obtained in generation[" +
                               generation +
                               "]: " + mejor_clasif);
        }
    }

    public void escribeFichero(String fichero) {
        Collections.sort(poblacion);
        poblacion.get(0).br.escribeFichero(fichero);
    }

    public BaseR mejorBR() {
        Collections.sort(poblacion);
        return poblacion.get(0).devuelveBR();
    }

}
