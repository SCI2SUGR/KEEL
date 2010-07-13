/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;
import org.core.*;

public class GeneticAlgorithmForBoosting {

    GenotypeBoosting patron; // generic individual pattern
    static Randomize r;
    
    public GeneticAlgorithmForBoosting(GenotypeBoosting p) {
        patron=p;
    }

    
    void sort(pair_fi []a) {
        // Sort pair vector GenotypeBoosting/fitness
        for (int i=1;i<a.length;i++) {
            pair_fi x = new pair_fi(a[i].first,a[i].second);
            int j;
            for (j=i-1;j>=0;--j) {
                if (a[j].first<=x.first) break;
                a[j+1].first = a[j].first;
                a[j+1].second = a[j].second;
            }
            a[j+1].first = x.first;
            a[j+1].second = x.second;
        }
    }

    public GenotypeBoosting EncuentraMinimo(
                                            int vnentradas,
                                            int vnlabels,
                                            Fitness fitness,
                                            int NUM_ITE,
                                            Randomize vr) {

        int nentradas=vnentradas;
        int nlabels=vnlabels;
        r=vr;


        // 'Simple' and 'quickly' Steady State Genetic Algorithm
        
        final int   TAM_POB=50;             // population size
        final int   SUBPOBLACIONES=10;      // Number of subpopulations
        final double PROB_MUTA=0.1f;        // Mutation Probability 
        final double PROB_INTER=0.01f;      // Interchange Prob.
        final int  TORNEO=3;                // Tournament size

        final int fuera1=TORNEO-1;
        final int fuera2=TORNEO-2;

        pair_gf[][] poblacion=new pair_gf[SUBPOBLACIONES][TAM_POB];
        int [] poblength=new int[SUBPOBLACIONES];
        double [][] media=new double[SUBPOBLACIONES][nentradas];

        double [] fitmin=new double[SUBPOBLACIONES];
        double [] fitmax=new double[SUBPOBLACIONES];

        // Create a new individual
        GenotypeBoosting individuo=patron.duplica();

        int faltan=SUBPOBLACIONES*TAM_POB;

        do {

            double fit=0;
            // do {
             individuo.inicializa();
             fit=fitness.evalua(individuo);
            // } while (fit==Fitness.NOCUBRE);
            // System.out.println(individuo.AString()+" "+fit);

            // Search the nearest subpopulation
            // (Smallest of the farest point distance)
            double dmin=0; int nmin=-1; boolean primero=true;
            for (int j=0;j<SUBPOBLACIONES;j++) {
                double dmax=0;
                for (int k=0;k<poblength[j];k++) {
                    double d=individuo.distancia(poblacion[j][k].first);
                    if (d>dmax) dmax=d;
                }
                if ((dmax<dmin || primero) && poblength[j]<TAM_POB) {
                    dmin=dmax; nmin=j; primero=false;
                }
            }
            if (nmin!=-1) {
                for (int k=0;k<nentradas;k++) {
                    media[nmin][k]*=poblength[nmin];
                    media[nmin][k]+=individuo.x[k];;
                    media[nmin][k]*=(1.0f/(poblacion[nmin].length+1));
                }
                poblacion[nmin][poblength[nmin]]=new pair_gf(individuo,fit);
                poblength[nmin]++;
                faltan--;
            }
        } while (faltan!=0);
        // System.out.println("Poblacion inicial:");
        // for (int n=0;n<SUBPOBLACIONES;n++) {
        //     System.out.print("Num="+poblength[n]+" VM=[");
        //     for (int k=0;k<nentradas;k++) System.out.print(media[n][k]+" ");
        //     System.out.println("]");
        //     System.out.println(poblacion[n][0].first.AString());
        // }

        int mejor=0;  double fit_mejor=-1; int n_mejor=-1, i_mejor=-1;
        for (int op=0; op<NUM_ITE; op++) {

            for (int n=0;n<poblacion.length;n++) {

                pair_fi[] torneo=new pair_fi[TORNEO];
                for (int i=0;i<torneo.length;i++) {
                    torneo[i]=new pair_fi();
                    torneo[i].second=(int)(r.Rand()*poblength[n]);
                    torneo[i].first=poblacion[n][torneo[i].second].second;
                }

                sort(torneo);

                pair_gg hijos=
                    poblacion[n][torneo[0].second].first.cruce(poblacion[n][torneo[1].second].first);

                if (r.Rand()<PROB_MUTA) hijos.first=hijos.first.mutacion();
                if (r.Rand()<PROB_MUTA) hijos.second=hijos.second.mutacion();

                // Recalc fitness
                double fit1=fitness.evalua(hijos.first);
                double fit2=fitness.evalua(hijos.second);


                // Insert descendants in the population 
                poblacion[n][torneo[fuera1].second].first=hijos.first;
                poblacion[n][torneo[fuera1].second].second=fit1;
                poblacion[n][torneo[fuera2].second].first=hijos.second;
                poblacion[n][torneo[fuera2].second].second=fit2;

                // Subpopulations interchange
                if (r.Rand() < PROB_INTER) {

                    // destiny subpopulation
                    int subpoblacion=(int)(r.Rand())*poblacion.length;

                    // Tournament in source subpopulation
                    torneo=new pair_fi[TORNEO];
                    for (int i=0;i<torneo.length;i++) {
                        torneo[i]=new pair_fi();
                        torneo[i].second=(int)(r.Rand()*poblength[n]);
                        torneo[i].first=poblacion[n][torneo[i].second].second;
                    }
                    sort(torneo);

                    // fitness value must fit inside min/max interval
                    if (fitmin[subpoblacion]<torneo[0].second
                        && fitmax[subpoblacion]>torneo[0].second) {

                        // Tournament in destiny subpopulation
                        pair_fi [] torneo_1=new pair_fi[TORNEO];
                        for (int i=0;i<torneo_1.length;i++) {
                            torneo_1[i]=new pair_fi();
                            torneo_1[i].second=(int)(r.Rand()*poblength[subpoblacion]);
                            torneo_1[i].first=poblacion[subpoblacion]
                                [torneo_1[i].second].second;
                        }
                        sort(torneo_1);

                        // worst/winner interchange
                        poblacion[subpoblacion][torneo_1[torneo_1.length-1].second]=
                            poblacion[n][torneo[0].second];

                    }
                }
            }


            if (op%10==0) {
                for (int n=0;n<poblacion.length;n++) {
                    double fitmedia=0;
                    fitmin[n]=poblacion[n][0].second; mejor=0;
                    fitmax[n]=poblacion[n][0].second;
                    for (int i=0;i<poblacion[n].length;i++) {
                        if (poblacion[n][i].second<fitmin[n]) {
                            fitmin[n]=poblacion[n][i].second; mejor=i;
                        }
                        if (poblacion[n][i].second>fitmax[n]) {
                            fitmax[n]=poblacion[n][i].second;
                        }
                        fitmedia+=poblacion[n][i].second;
                    }
                    fitmedia/=poblacion[n].length;
                    System.out.print("Subpoblacion "+n+" ");
                    System.out.print("Iteracion: "+op+" ");
                    System.out.print("Fitness media "+fitmedia+" ");
                    System.out.println("Mejor fitness "+fitmin[n]);

                    if (fitmin[n]<fit_mejor || n==0) {
                        fit_mejor=fitmin[n]; n_mejor=n; i_mejor=mejor;
                    }

                }

            }

        }
        // Optimization results
        GenotypeBoosting x=poblacion[n_mejor][i_mejor].first;
        double ff=poblacion[n_mejor][i_mejor].second;
        System.out.println("Solucion final "+x.AString()+" fit="+ff);
        return x;
    }


    public GenotypeBoosting EncuentraMinimoSA(
                                            Fitness fitness,
                                            int NUM_ITE,
                                            double T0,
                                            double alpha,
                                            Randomize vr) {

        r=vr;

        // Simulated annealing 
        
        double fit, fitnuevo, fitmejor,delta;
        GenotypeBoosting individuo=patron.duplica();
        GenotypeBoosting aleatorio=patron.duplica();
        GenotypeBoosting candidato=patron.duplica();
        GenotypeBoosting mejor=patron.duplica();
        individuo.inicializa();
        fit=fitness.evalua(individuo);
        fitmejor=fit;
        System.out.println("Estimando T0");
        double T=T0; 
        double P0=0.25f; double P1=0.0001f;
        int NESTIMA=100;

        

        int ndeltas=0; double mediadelta=0.0f;
        boolean estimandoT0=true; 
        for (int cuenta=0;cuenta<NUM_ITE;cuenta++) {
            if (cuenta==NESTIMA) {
               // P0 prob. 
               T0=-mediadelta/ndeltas/(double)Math.log(P0);
               double T1=-mediadelta/ndeltas/(double)Math.log(P1);
               alpha=(double)Math.exp(1.0f/NUM_ITE*(double)Math.log(T1/T0));
               T=T0;
            }
            aleatorio.inicializa();
            pair_gg hijos=aleatorio.cruce(individuo);
            candidato=hijos.second;
            // Fitness function modify 'candidate'
            fitnuevo=fitness.evalua(candidato);
            delta=fitnuevo-fit;
            if (estimandoT0 && delta>0 && fitnuevo!=Fitness.NOELEGIR) {
              mediadelta+=delta;
              ndeltas++;
            }
            // double d=individuo.distancia(poblacion[j][k].first);
            if (fitnuevo<fitmejor) {
               mejor=candidato.duplica();
               fitmejor=fitnuevo;
            }
            if (delta<0 || r.Rand()<Math.exp(-delta/T)) {
               individuo=candidato.duplica();
               fit=fitnuevo;
            } 
            T=T*alpha;
            // if (cuenta%100==0) System.out.println("Mejor="+mejor.AString()+" fm="+fitmejor+" Actual="+individuo.AString()+" fit="+fit+" Candidato="+candidato.AString()+" t="+T+" it="+cuenta);
            if (cuenta%100==0) System.out.println(" fm="+fitmejor+" fit="+fitnuevo+" t="+T+" it="+cuenta+" delta="+delta+" pf="+Math.exp(-delta/T));
        }
        System.out.println("Solucion final "+mejor.AString()+" fit="+fitmejor);
        return mejor;
    }



   

}


