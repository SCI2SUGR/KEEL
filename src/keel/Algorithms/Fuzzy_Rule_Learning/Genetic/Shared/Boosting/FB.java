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

/** Fuzzy classifiers induction using LogitBoost algorithm */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

import java.io.*;
import org.core.*;
import java.util.Vector;
import java.util.StringTokenizer;

class FitnessABD extends Fitness {
    int nentradas;
    int nlabels;
    int Nsalidas;
    int Clase;
    double []W;
    double [][][]Mu;
    double []Dsigno;
    AdaBoost ad;

    FitnessABD(AdaBoost vad, int vnentradas,int nsalidas,int nl,int clase,
               double []w, double[][][]mu, double []dsigno) {
        ad=vad; nentradas=vnentradas; nlabels=nl;
        Nsalidas=nsalidas; Clase=clase; W=w; Mu=mu; Dsigno=dsigno;
    }

    FuzzyRule ConstruyeRegla(GenotypeBoosting g) {
        FuzzyRule TestRegla=
        new FuzzyRule(new int[g.x.length],new double[Nsalidas]);

        for (int i=0;i<g.x.length;i++) TestRegla.antecedente[i]=g.x[i];
        TestRegla.consecuente[Clase]=1;
        return TestRegla;
    }

    public double evalua(GenotypeBoosting g) {
        FuzzyRule ref[]=new FuzzyRule[1]; ref[0]=ConstruyeRegla(g);
        double fc=ad.CambiaConsReglaABD(ref,W,Clase,Mu,Dsigno);
        return fc;
    }
}

class FitnessABDMaxMin extends Fitness {
    int nentradas;
    int nlabels;
    int Nsalidas;
    double [][][]Mu;
    double [][]alpha;
    boolean []entra;
    AdaBoostMaxMin ad;
    static Randomize r;

    FitnessABDMaxMin(AdaBoostMaxMin vad, int vnentradas,int nsalidas,int nl, double[][][]mu, Randomize vr) {
        ad=vad; nentradas=vnentradas; nlabels=nl;
        Nsalidas=nsalidas; Mu=mu;
        alpha=new double[ad.BaseConocimiento.length+1][Nsalidas];
        entra=new boolean[ad.BaseConocimiento.length+1];
        Cuentait=new int[4];
        r=vr;
    }

    public double evalua(GenotypeBoosting g) {
        // a chromosome will be composed of the antecedent + initial points of all consequents
        GenotypeBoostingMaxMin gmm=(GenotypeBoostingMaxMin)g;

        // New rule creation
        FuzzyRule TestRegla=new FuzzyRule(new int[nentradas],new double[Nsalidas]);

        // Store antecedent values
        for (int i=0;i<nentradas;i++) TestRegla.antecedente[i]=gmm.x[i];

        // Store initial value of alpha
        int k=0;
        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
            for (int j=0;j<Nsalidas;j++) alpha[i][j]=gmm.alpha[k++];

// ------------------------------------------------------------------------

        // Experiment: random alpha values 
        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
            for (int j=0;j<Nsalidas;j++) alpha[i][j]=5.0f*(r.Rand()-0.5f);

// ------------------------------------------------------------------------

        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
            // entra[i]=gmm.entra[i];  // Con GA
            entra[i]=true;   // Automatic

        // fitness calculation
        double fc=ad.MinimizaFitness(TestRegla,alpha,Mu,entra,true,r);

        // Change alpha value 
        k=0;
        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
           for (int j=0;j<Nsalidas;j++) gmm.alpha[k++]=alpha[i][j];

        if (fc==Fitness.ITERACIONES) Fitness.Cuentait[0]++;
        if (fc==Fitness.NEGATIVO) Fitness.Cuentait[1]++;
        if (fc==Fitness.SINGULAR) Fitness.Cuentait[2]++;
        if (fc==Fitness.NOCUBRE) Fitness.Cuentait[3]++;

        if (fc>=Fitness.NOELEGIR) fc=Fitness.NOELEGIR;

        return fc;
    }

    public double evalua_sin_optimizar(GenotypeBoosting g) {
        // a chromosome will be composed of the antecedent + initial points of all consequents
        GenotypeBoostingMaxMin gmm=(GenotypeBoostingMaxMin)g;

        // New rule creation
        FuzzyRule TestRegla=new FuzzyRule(new int[nentradas],new double[Nsalidas]);

        // store antecedent values
        for (int i=0;i<nentradas;i++) TestRegla.antecedente[i]=gmm.x[i];

        // store initial alpha values
        int k=0;
        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
            for (int j=0;j<Nsalidas;j++) alpha[i][j]=gmm.alpha[k++];

        for (int i=0;i<ad.BaseConocimiento.length+1;i++)
            // entra[i]=gmm.entra[i];  // Con GA
            entra[i]=true;   // Automatico

        // fitness calculation
        double fc=ad.MinimizaFitness(TestRegla,alpha,Mu,entra,false,r);

        if (fc==Fitness.ITERACIONES) Fitness.Cuentait[0]++;
        if (fc==Fitness.NEGATIVO) Fitness.Cuentait[1]++;
        if (fc==Fitness.SINGULAR) Fitness.Cuentait[2]++;
        if (fc==Fitness.NOCUBRE) Fitness.Cuentait[3]++;

        if (fc>=Fitness.NOELEGIR) fc=Fitness.NOELEGIR;

        return fc;
    }

}


class AdaBoost extends FuzzyClassifier {

    static Randomize r;
    AdaBoost(double [][]vejemplos, double [][]vdeseado, Randomize vr) {
        super(vejemplos,vdeseado);
        r=vr;
    }

    void CalculaPesosEjemplosABD(double []w, int Clase,
                                 double [][][]Mu,double[]dsigno) {

        double rr[]=InferenciaBanco(Clase,Mu);

        for (int i=0;i<nelem;i++) w[i]=1;
        for (int i=0;i<nelem;i++) {
            if (Math.abs(rr[i])>ACT_MIN)  // It must be ACT_MIN*w[i] !!
                w[i]=w[i]*(double)Math.exp(-dsigno[i]*rr[i]);
            else w[i]=w[i]*(double)Math.exp(PENAL_CUB);
        }

        // Normalization weights
        double suma=0;
        for (int i=0;i<nelem;i++) suma+=w[i];
        for (int i=0;i<nelem;i++) w[i]/=suma;

        // for (int i=0;i<nelem;i++) {
        // ---------------------------------------
        // System.out.print("Ejemplo "+i+":");
        // for (int j=0;j<ejemplos[i].length;j++)
        //  System.out.print(ejemplos[i][j]+" ");
        // System.out.print("->");
        // for (int j=0;j<deseado[i].length;j++)
        //  System.out.print(deseado[i][j]+" ");
        // System.out.print(" r=");
        // System.out.print(rr[i]+" ");
        // if (rr[i]*dsigno[i]>0) {
        //   System.out.print("BIEN");
        // } else if (rr[i]*dsigno[i]<0) {
        //   System.out.print("MAL");
        // } else System.out.print("NO OPINA");
        // System.out.println(" Peso="+w[i]);
        // ---------------------------------------

        // }

    }


    double Z(FuzzyRule regla,double w[],int Clase,double alpha,
            double[][][]Mu, double[]dsigno) {
        double fitness=0.0f;
        double ca[]=CertezaAntecedente(regla,Mu);
        for (int i=0;i<nelem;i++) {
            if (Math.abs(ca[i])>ACT_MIN)  // It must be ACT_MIN*w[i] !!
                fitness+=w[i]*Math.exp(-alpha*ca[i]*dsigno[i]);
            else fitness+=w[i]*Math.exp(Math.abs(alpha)*PENAL_CUB);
        }
        return fitness;
    }

    class ZFun extends Fun {
        // Wrapper for Z(alpha) optimization
        FuzzyRule r;
        double w[];
        double Mu[][][];
        int C;
        double dsigno[];

        ZFun(FuzzyRule vr,double vw[],int vC,
             double [][][]vMu, double []vdsigno) {
            r=vr; w=vw; C=vC; Mu=vMu; dsigno=vdsigno;
        }
        public double evalua(double []alpha) {
            return Z(r,w,C,alpha[0],Mu,dsigno);
        }
    }


    double CambiaConsReglaABD(FuzzyRule r[], double w[],
                             int Clase, double Mu[][][], double []dsigno) {

        // we use a Brent's method simplified version

        double alphaminimo=0;

        ZFun MinZ=new ZFun(r[0],w,Clase,Mu,dsigno);

        double XBus[]=new double[1]; XBus[0]=0;
        double DBus[]=new double[1]; DBus[0]=1;
        LinearSearchBrent BL=new LinearSearchBrent(MinZ,DBus,XBus);
        alphaminimo=BL.EncuentraMinimoSimple();

        double fitness=Z(r[0],w,Clase,alphaminimo,Mu,dsigno);

        for (int i=0;i<r[0].consecuente.length;i++) r[0].consecuente[i]=0;
        r[0].consecuente[Clase]=alphaminimo;

        return fitness;
    }

    int argmaxabs(double []x) {
        double max=Math.abs(x[0]); int imax=0;
        for (int i=1;i<x.length;i++)
            if (Math.abs(x[i])>max) { max=Math.abs(x[i]); imax=i; }
                return imax;
    }

    void AnadeReglaABD2C(int c,double [][][]Mu, double[]w, double[]dsigno) {
        // Find the best rule according to 'Adaboost descriptive' method
        // in a two classes problem

        int []antecedente = new int[nentradas];
        double []consecuente = new double[nsalidas];

        FuzzyRule result =
            new FuzzyRule(new int[nentradas], new double[nsalidas]);

        // Recalculate the importance of the examples
        double minfit=0;

        FuzzyRule TestRegla=new FuzzyRule(antecedente,consecuente);

        // ----------------------------------------------------------

        int permutaciones=1;
        boolean maxint=false;
        for (int i=0;i<result.antecedente.length;i++){
          // prevents int overflow
          if (permutaciones < Integer.MAX_VALUE/(Etiquetas[i].vertices.length+1))
            permutaciones*=(Etiquetas[i].vertices.length+1);
          else maxint=true;
        }
        if (maxint==true) {
          System.out.println("Total permutaciones > "+Integer.MAX_VALUE);
          permutaciones=Integer.MAX_VALUE;
        }
        else System.out.println("Total permutaciones="+permutaciones);
                                                                                                                                   
        if (permutaciones<1000) {

            // Exhaustive search

            double fc=0, minfc=0; int q=1, perc=0;
            for (int p=0;p<permutaciones;p++) {
                q++;
                if (permutaciones>100) {
                    if (q>permutaciones/100) {
                        System.out.println("Terminado "+perc+"%");
                        q=0; perc++;
                    }
                }

                int cp=p;
                for (int i=0;i<result.antecedente.length;i++) {
                    TestRegla.antecedente[i]=cp%(Etiquetas[i].vertices.length+1);
                    cp=cp/(Etiquetas[i].vertices.length+1);
                }
                TestRegla.consecuente[c]=1;

                FuzzyRule ref[]=new FuzzyRule[1]; ref[0]=TestRegla;
                fc=CambiaConsReglaABD(ref,w,c,Mu,dsigno);

                // System.out.print("Probando regla "+p+" Ct");
                // for (int i=0;i<TestRegla.antecedente.length;i++)
                //  System.out.print(TestRegla.antecedente[i]+" ");
                // System.out.print("->");
                // for (int i=0;i<TestRegla.consecuente.length;i++)
                //  System.out.print(TestRegla.consecuente[i]+" ");
                // System.out.println(": Fitness="+fc);


                if (fc<minfc || p==0) {
                    for (int i=0;i<result.antecedente.length;i++)
                        result.antecedente[i]=TestRegla.antecedente[i];
                    for (int i=0;i<result.consecuente.length;i++)
                        result.consecuente[i]=TestRegla.consecuente[i];
                    minfc=fc;
                }

            }

            //------------------------------------------------------

        } else {

            // it is necessary to review the shutdown conditions!!!
            int nlabels=Etiquetas[0].vertices.length;
            FitnessABD fitness=new FitnessABD(this,nentradas,nsalidas,
                                              nlabels,c,w,Mu,dsigno);
            GeneticAlgorithmForBoosting AG=new GeneticAlgorithmForBoosting(new GenotypeBoosting(nentradas,nlabels,r));
            GenotypeBoosting gen=AG.EncuentraMinimo(
                                                    nentradas,nlabels,fitness,250,r);
            FuzzyRule ref[]=new FuzzyRule[1];
            ref[0]=fitness.ConstruyeRegla(gen);
            double fc=CambiaConsReglaABD(ref,w,c,Mu,dsigno);
            System.out.println("Z="+fc);
            result=ref[0];
        }


        // If the importance of rule is smaller than minimum allowed,
        // the rule is not added to the base
        if (Math.abs(result.consecuente[argmaxabs(result.consecuente)])<MIN_CONS) {
            System.out.println("No se incorpora la regla");
            return;
        }

        // incorporate the rule to the knowledge base
        FuzzyRule NuevoBanco[]=
            new FuzzyRule[BaseConocimiento.length+1];

        for (int i=0;i<BaseConocimiento.length;i++) {
            NuevoBanco[i]=BaseConocimiento[i];
        }

        NuevoBanco[BaseConocimiento.length]=
            new FuzzyRule(new int[nentradas],new double[nsalidas]);
        for (int i=0;i<nentradas;i++)
            NuevoBanco[BaseConocimiento.length].antecedente[i]=
                result.antecedente[i];
        for (int i=0;i<nsalidas;i++)
            NuevoBanco[BaseConocimiento.length].consecuente[i]=
                result.consecuente[i];

        BaseConocimiento = NuevoBanco;

        System.out.println("Regla anadida"+
                           AString(result.antecedente)+
                           "->"+AString(result.consecuente));


    }



}

class AdaBoostMaxMin extends FuzzyClassifier {

    final int MAX_NIT=4; // If it does not finish in MAX_NIT iterations, aborts
    final double MAXLIMALPHA=1.0f; // Maximum difference allowed in alpha
    int cdeseado[];
    double w[];
    static Randomize r;

    AdaBoostMaxMin(double [][]vejemplos, double [][]vdeseado, Randomize vr) {
        super(vejemplos,vdeseado);
        cdeseado=new int[vejemplos.length];
        for (int i=0;i<cdeseado.length;i++) {
         for (int j=0;j<nsalidas;j++) 
           if (vdeseado[i][j]!=0) {
              cdeseado[i]=j; 
              break; 
           }
        }
        w=new double[1];
        r=vr;
    }

    private int[] duplica(int []a) {
        int [] result=new int[a.length];
        for (int i=0;i<a.length;i++) result[i]=a[i];
        return result;
    }
    private double[] duplica(double []a) {
        double [] result=new double[a.length];
        for (int i=0;i<a.length;i++) result[i]=a[i];
        return result;
    }
    private double[][] duplica(double [][]a) {
        double [][] result=new double[a.length][];
        for (int i=0;i<a.length;i++) result[i]=duplica(a[i]);
        return result;
    }
    private void asigna(double result[],double []a) {
        for (int i=0;i<a.length;i++) result[i]=a[i];
    }
    private void asigna(double result[][],double [][]a) {
        for (int i=0;i<a.length;i++) asigna(result[i],a[i]);
    }

    public int fuzzyclasificamaxmin(int ej, double [][][]Mu, double[]Peso) {

        int nsalidas=BaseConocimiento[0].consecuente.length;
        // double maximos[]=new double[nsalidas];
        double max=0; int imax=-1; int cmax=0; boolean primera=true;
        for (int r=0;r<BaseConocimiento.length;r++) {
            double act=0; int cmaxr=-1;
            double tr=CertezaAntecedenteEj(ej,BaseConocimiento[r],Mu);
            if (tr==0) continue;
            for (int c=0;c<BaseConocimiento[r].consecuente.length;c++) {
                double cact=tr*BaseConocimiento[r].consecuente[c];
                if (cact>act || c==0) { act=cact; cmaxr=c; }
            }
            // System.out.println("ejemplo="+i+" act="+act);
            if (act>max || primera) { 
               primera=false; max=act; imax=r; cmax=cmaxr; 
            }
        }

        Peso[0]=max;
        return cmax;

        // calculate the probabilities codified in the rule
        // for (int i=0;i<nsalidas;i++) {
        //    if (imax==-1) {
        //        maximos[i]=1.0f/nsalidas;
        //        if (i==0) maximos[i]+=0.01f;
        //        else maximos[i]-=0.01f/(nsalidas-1);
        //    }
        //    else maximos[i]=BaseConocimiento[imax].consecuente[i];
        // }

        // double den=0;
        // for (int i=0;i<nsalidas;i++) den+=(double)Math.exp(maximos[i]);
        // for (int i=0;i<nsalidas;i++) 
        //      maximos[i]=(double)Math.exp(maximos[i])/den;

        // return maximos;

    }

    double FuncionCosteChi(double Mu[][][]) {

            // Chi square criterion
            double L=0,po,pd;
            for (int i=0;i<nelem;i++) {
             int o=fuzzyclasificamaxmin(i,Mu,w);
             int d=cdeseado[i];
             double ew=(double)Math.exp(w[0]);
             double ew1=(double)Math.exp(-w[0]/(nsalidas-1));
             double den=ew+(nsalidas-1)*ew1;
             for (int s=0;s<nsalidas;s++) {
               if (s==o) po=ew/den; else po=ew1/den;
               if (s==d) pd=1; else pd=0;
               L+=(po-pd)*(po-pd);
             }
            }
            return L;
   }

    double FuncionCosteRMS(double Mu[][][]) {

            double L=0,po,pd;
            for (int i=0;i<nelem;i++) {
             int o=fuzzyclasificamaxmin(i,Mu,w);
             int d=cdeseado[i];
             double ew=w[0];
             double ew1=-w[0]/(nsalidas-1);
             for (int s=0;s<nsalidas;s++) {
               if (s==o) po=ew; else po=ew1;
               if (s==d) pd=2; else pd=-2;
               L+=(po-pd)*(po-pd);
             }
            }
            return L;
   }

   double FuncionCosteERR(double Mu[][][]) {

            double L=0,po,pd;
            for (int i=0;i<nelem;i++) {
             int o=fuzzyclasificamaxmin(i,Mu,w);
             int d=cdeseado[i];
             if (o!=d) L++;
            }
            return L;
   }

   double FuncionCoste(double Mu[][][]) {
            return FuncionCosteRMS(Mu);
   }



    double MinimizaFitness(FuzzyRule regla,double [][]alpha, 
        double[][][]Mu, boolean entra[], boolean optimiza, Randomize rand) {

        // fitness of the new bank when adding the new rule "regla"

        // If the new rule does not cover any example we do not waste the time

        // System.out.println("Evaluando "+AString(alpha));

        boolean cubrir=false;
        for (int i=0;i<nelem;i++) {
            if (CertezaAntecedenteEj(i,regla,Mu)!=0) { cubrir=true; break; }
        }
        if (cubrir==false) { return Fitness.NOCUBRE; }

        int [][]I=new int[nelem][BaseConocimiento.length+1];
        int [][]I_old=new int[I.length][I[0].length];
        double [][]Y=new double[nsalidas][nelem];
        double [][]F=new double[BaseConocimiento.length+1][nelem];

        // store the previous base
        FuzzyRule GuardaBanco[]=BaseConocimiento;

        // we prove the new rule adding it to the previous base
        
        FuzzyRule MiNuevoBanco[]=
                 new FuzzyRule[BaseConocimiento.length+1];

        // we copy the base
        for (int i=0;i<BaseConocimiento.length;i++) {
                MiNuevoBanco[i]=new FuzzyRule(
                     duplica(BaseConocimiento[i].antecedente),
                     duplica(BaseConocimiento[i].consecuente));
        }

        // we add the new rule
        MiNuevoBanco[BaseConocimiento.length]=
                new FuzzyRule(new int[nentradas],new double[nsalidas]);
        for (int i=0;i<nentradas;i++)
                MiNuevoBanco[BaseConocimiento.length].antecedente[i]=
                   regla.antecedente[i];
        for (int i=0;i<nsalidas;i++)
                MiNuevoBanco[BaseConocimiento.length].consecuente[i]=
                   regla.consecuente[i];

        // we set the initial alpha values
        for (int i=0;i<entra.length;i++) {
                for (int Clase=0;Clase<nsalidas;Clase++)
                MiNuevoBanco[i].consecuente[Clase]=alpha[i][Clase];
        }

        // we replace the knowledge base
        BaseConocimiento = MiNuevoBanco;
        double [][]NuevoAlpha=duplica(alpha);
        double fitness=0,Rfitness=0;

        // we calculate the values of I
        if (optimiza) I=CalculaI(Mu,MiNuevoBanco);

        int nit=0; 
        do {
            // System.out.println("nit="+nit+" "+AString(NuevoAlpha));

            if (nit==0 && !optimiza) {
                   // System.out.println("Optim nit="+nit+" f="+fitness);
                   fitness=FuncionCoste(Mu);
                   break; 
            }

            for (int i=0;i<F.length;i++) {
                for (int j=0;j<nelem;j++) {
                  for (int Clase=0;Clase<nsalidas;Clase++) {
                     F[i][j]=CertezaAntecedenteEj(j,MiNuevoBanco[i],Mu)*I[j][i];
                     Y[Clase][j]=(double)((deseado[j][Clase]-0.5f)/(0.25f));
                  }
                }
            }

            // we eliminate the null rows 
            int ientra=0;
            for (int i=0;i<entra.length;i++) {
               entra[i]=false;
               for (int j=0;j<nelem;j++){
                 if (F[i][j]!=0) { entra[i]=true; break; }
               }
               if (entra[i]) ientra++;
            }

            double Fentra[][]=new double[ientra][];
            double NuevoAlphaEntra[][]=new double[ientra][nsalidas];

            ientra=0;
            for (int i=0;i<entra.length;i++) {
               if (entra[i]) {
                 Fentra[ientra]=F[i];
                 ientra++;
               }
            }

            double [][]NA=duplica(NuevoAlpha);
            if (ientra==0) { 
                asigna(alpha,NuevoAlpha);
                //  System.out.println("Sing 1 nit="+nit+" f="+fitness);
                fitness=Fitness.SINGULAR;
                break;
            } else {
		try {
		    NuevoAlphaEntra=
                            MatrixCalcs.tr(MatrixCalcs.matmul(Y,
			    MatrixCalcs.matmul(MatrixCalcs.tr(Fentra),
			    MatrixCalcs.inv(MatrixCalcs.matmul(
                              Fentra,MatrixCalcs.tr(Fentra))))));
		} catch(MatrixCalcs.ErrorDimension e) {
		    System.err.println(e);
		    System.exit(0);
		} catch(MatrixCalcs.ErrorSingular e) {
                    asigna(alpha,NuevoAlpha);
                    // System.out.println("Sing 2 nit="+nit+" f="+fitness);
                    fitness=Fitness.SINGULAR;
                    break;
		}
            }

            ientra=0;
            for (int i=0;i<entra.length;i++) {
              if (entra[i]) {
                for (int j=0;j<nsalidas;j++) {
                  NuevoAlpha[i][j]=NuevoAlphaEntra[ientra][j];
                }
                ientra++;
              } else {
                for (int j=0;j<nsalidas;j++) {
                  NuevoAlpha[i][j]=0.0f;
                }
              }
            }

            // smoothing
            double lambda=0.75f;
            double maxdifalpha=0.0f;
            if (nit>0)
                for (int Clase=0;Clase<nsalidas;Clase++) 
                  for (int i=0;i<NuevoAlpha.length;i++) {
                    double dif=Math.abs(NuevoAlpha[i][Clase]-NA[i][Clase]);
                    if (dif>maxdifalpha) maxdifalpha=dif;
                    NuevoAlpha[i][Clase]=
                     lambda*NA[i][Clase]+(1.0f-lambda)*NuevoAlpha[i][Clase];
                  }

            
//            we return as fitness the residual from the approach |y-alpha*F|
//            Rfitness=0;
//            for (int i=0;i<nelem;i++) {
//                double out=0;
//                for (int Clase=0;Clase<nsalidas;Clase++) {
//                  out=0;
//                  for (int r=0;r<alpha.length;r++) {
//                      out+=F[r][i]*NuevoAlpha[r][Clase];
//                  }
//                  double err=out-Y[Clase][i];
//                  Rfitness+=err*err;
//               }
//            }

            // we update alphas in the base copy
            for (int i=0;i<alpha.length;i++)
                for (int Clase=0;Clase<nsalidas;Clase++) {
                  MiNuevoBanco[i].consecuente[Clase]=NuevoAlpha[i][Clase];
                }

            for (int i=0;i<I.length;i++)
                for (int j=0;j<I[0].length;j++) I_old[i][j]=I[i][j];

            I=CalculaI(Mu,MiNuevoBanco);

            int distintos=0; 
            for (int i=0;i<I.length;i++)
                for (int j=0;j<I[0].length;j++) {
                  if (I_old[i][j]!=I[i][j]) distintos++;
                }

            // we verify that at least one of the weights is positive
            // in all base rows
            boolean negativo=true;
            for (int r=0;r<MiNuevoBanco.length;r++) {
              negativo=true;
              for (int i=0;i<nsalidas;i++)
                if (MiNuevoBanco[r].consecuente[i]>0) {
                   negativo=false; break; 
                }
              if (negativo) break;
            }

            if (negativo) { fitness=Fitness.NEGATIVO; break; }
            if (nit>=MAX_NIT) { 
               fitness=FuncionCoste(Mu);
               asigna(alpha,NuevoAlpha);
               // fitness=Fitness.ITERACIONES; 
               // fitness*=10;
               break; 
            }
            if (distintos==0 && maxdifalpha<=MAXLIMALPHA) { 
               fitness=FuncionCoste(Mu);
               asigna(alpha,NuevoAlpha);
               break;
            }

            nit++;

        } while (true);

        // we leave unchanged the knowledge base
        BaseConocimiento = GuardaBanco;
        // System.out.println("Coste="+fitness);
        return fitness;

    }

    int argmaxabs(double []x) {
        double max=Math.abs(x[0]); int imax=0;
        for (int i=1;i<x.length;i++)
            if (Math.abs(x[i])>max) { max=Math.abs(x[i]); imax=i; }
                return imax;
    }

    double AnadeReglaABDMM(double [][][]Mu) {
        // Find the best rule according to 'Adaboost max-min descriptive' method
        // in a 'nsalidas' classes problem (n outputs) 

        int []antecedente = new int[nentradas];
        double []consecuente = new double[nsalidas];
        double [][]minalpha = new double[nelem][nsalidas];

        FuzzyRule result =
            new FuzzyRule(new int[nentradas], new double[nsalidas]);

        double minfit=0, minfc=0;

        FuzzyRule TestRegla=new FuzzyRule(antecedente,consecuente);

        // it is necessary to review the shutdown conditions!!!
        int nlabels=Etiquetas[0].vertices.length;
        FitnessABDMaxMin fitness=new FitnessABDMaxMin(this,nentradas,nsalidas,nlabels,Mu,r);

        GenotypeBoostingMaxMin gen;

        // if (BaseConocimiento.length>0) {

        GeneticAlgorithmForBoosting AG=
            new GeneticAlgorithmForBoosting(new
               GenotypeBoostingMaxMin(nentradas,nlabels,
                     nsalidas,BaseConocimiento.length+1,r));
            gen=(GenotypeBoostingMaxMin)AG.EncuentraMinimo(
                    nentradas,nlabels,fitness,250,r);
            // gen=(GenotypeBoostingMaxMin)AG.EncuentraMinimoSA(
            //        fitness,10000,10.0f,0.9999f);

        System.out.println("Convergencias="+fitness.Cuentait[0]);
        System.out.println("Negativos="+fitness.Cuentait[1]);
        System.out.println("Singulares="+fitness.Cuentait[2]);
        System.out.println("No cubre="+fitness.Cuentait[3]);

        // } else {
        //    // null first rule
        //    gen=new GenotypeBoostingMaxMin(nentradas,nlabels,nsalidas,1);
        //    for (int i=0;i<gen.x.length;i++) gen.x[i]=0;
        //    for (int i=0;i<gen.entra.length;i++) gen.entra[i]=true;
        //    for (int i=0;i<gen.alpha.length;i++) gen.alpha[i]=0;
        //    double fc=fitness.evalua(gen);
        //    System.out.println("Fitness de la primera regla: "+fc);
        // }

        // calculate new rule from solution
        FuzzyRule solucion=new FuzzyRule(new int[nentradas],new double[nsalidas]);

        // store antecedent and consequent values
        for (int i=0;i<nentradas;i++) solucion.antecedente[i]=gen.x[i];
        for (int i=0;i<nsalidas;i++) solucion.consecuente[i]=gen.alpha[BaseConocimiento.length*nsalidas+i];

        // calculate the knowledge base consequents and fitness of the solution
        double fc=fitness.evalua_sin_optimizar(gen);
        System.out.println("Candidato= "+AString(solucion.antecedente)+" -> "+AString(solucion.consecuente)+" Z="+fc);

        // Add rule to base
        FuzzyRule NuevoBanco[]=new FuzzyRule[BaseConocimiento.length+1];
        NuevoBanco[BaseConocimiento.length]=
            new FuzzyRule(new int[nentradas],new double[nsalidas]);

        // update the alphas
        for (int i=0;i<BaseConocimiento.length;i++) { 
            NuevoBanco[i]=BaseConocimiento[i]; 
        }
        for (int r=0;r<BaseConocimiento.length;r++) {
            for (int Clase=0;Clase<nsalidas;Clase++)
              NuevoBanco[r].consecuente[Clase]=gen.alpha[r*nsalidas+Clase];
        }

        // Add the new rule 
        for (int i=0;i<nentradas;i++)
            NuevoBanco[BaseConocimiento.length].antecedente[i]=
                solucion.antecedente[i];

        for (int i=0;i<nsalidas;i++)
            NuevoBanco[BaseConocimiento.length].consecuente[i]=
                solucion.consecuente[i];

        BaseConocimiento = NuevoBanco;
        return fc;    

    }

}


class FitnessLGB extends Fitness {
    final double FITNESS_MALO=1000f;  // worst Fitness 
    int nentradas;
    int nlabels;
    int Nsalidas;
    int Clase;
    double []eta;
    double [][][]Mu;
    double []Dsigno;
    double []AjusteAlpha;
    LogitBoost lg;

    FitnessLGB(LogitBoost vlg, int vnentradas,int nsalidas,int nl,int clase,
               double []veta, double[][][]mu, double []dsigno, double[]AA) {
        lg=vlg; nentradas=vnentradas; nlabels=nl;
        Nsalidas=nsalidas; Clase=clase; eta=veta; Mu=mu; Dsigno=dsigno;
        AjusteAlpha=AA;
    }

    FuzzyRule ConstruyeRegla(GenotypeBoosting g) {
        FuzzyRule TestRegla=
        new FuzzyRule(new int[g.x.length],new double[Nsalidas]);

        for (int i=0;i<g.x.length;i++) TestRegla.antecedente[i]=g.x[i];
        TestRegla.consecuente[Clase]=1;
        return TestRegla;
    }

    public double evalua(GenotypeBoosting g) {
        FuzzyRule ref[]=new FuzzyRule[1]; ref[0]=ConstruyeRegla(g);
        boolean rz=true;
        for (int i=0;i<ref[0].antecedente.length;i++) {
            if (ref[0].antecedente[i]!=0) { rz=false; break; }
        }
        double fc=FITNESS_MALO;
        if (!rz)
            fc=lg.CambiaConsReglaLGB(ref,eta,Clase,Mu,Dsigno,AjusteAlpha);
        return fc;
    }
}


class LogitBoost extends FuzzyClassifier {

    final double FITNESS_MALO=1000f;  // worst Fitness 
    boolean VersionBasica;
    static Randomize r;

    LogitBoost(double [][]vejemplos, double [][]vdeseado, boolean VB, Randomize vr) {
        super(vejemplos,vdeseado);
        VersionBasica=VB;
        r=vr;
    }

    void CalculaResiduosLGB(double []rr, int Clase,
                            double [][][]Mu,double[]dsigno) {
    }

    double CambiaConsReglaLGB(FuzzyRule regla[],  double eta[],
                             int Clase, double Mu[][][], double []dsigno,
                             double []NuevasAlphas) {

        // determine analiticaly the alpha of the rule

        // calculate the residual

        final double CERO=0.000001f;
        double r[]=new double [dsigno.length];
        double p[]=new double [dsigno.length];

        for (int i=0;i<nelem;i++) {
            double expr=(double)Math.exp(eta[i]);
            p[i]=(double)(expr/(1.0+expr));
            if (dsigno[i]>0) {
                r[i]+=(1.0f+expr)/expr;
            } else {
                r[i]-=(1.0f+expr);
            }
        }

        // and certainty of the consequent 
        double ca[]=CertezaAntecedente(regla[0],Mu);
        double sumasy=0, sumas2=0, medias=0, mediay=0, sumapeso=0;
        for (int i=0;i<ca.length;i++) {
            double peso=p[i]*(1-p[i]);
            mediay+=r[i]*peso;
            medias+=ca[i]*peso;
            sumapeso+=peso;
        }
        // assure that sumapeso is not zero
        double fitness=0, mediaerr=0, alphaminimo=0;

        if (Math.abs(sumapeso)>=CERO) {

            mediay/=sumapeso; medias/=sumapeso;

            // System.out.println("mediay="+mediay);
            // System.out.println("medias="+medias);

            for (int i=0;i<ca.length;i++) {
                double peso=p[i]*(1-p[i]);
                sumasy+=(r[i]-mediay)*(ca[i]-medias)*peso;
                sumas2+=(ca[i]-medias)*(ca[i]-medias)*peso;
            }

            if (Math.abs(sumas2)>CERO) alphaminimo=sumasy/sumas2;
            // System.out.println("alpha="+alphaminimo);

            // Now we calculate the approach error
            for (int i=0;i<ca.length;i++) {
                double peso=p[i]*(1-p[i]);
                double err=(r[i]-alphaminimo*ca[i])*peso;
                mediaerr+=err;
            }
            mediaerr/=sumapeso;

            // without considering that weak learners have null average
            if (VersionBasica) mediaerr=0;


            double debugerr=0;
            for (int i=0;i<ca.length;i++) {
                double peso=p[i]*(1-p[i]);
                double err=(r[i]-alphaminimo*ca[i]-mediaerr)*peso;
                fitness+=err*err*p[i]*(1-p[i]);
                debugerr+=err;
            }
            fitness/=sumapeso;
            // System.out.println("Debugerr="+debugerr);

        }


        // update the rule consequent
        if (Math.abs(sumas2)<=CERO) alphaminimo=mediaerr;
        for (int i=0;i<regla[0].consecuente.length;i++)
            regla[0].consecuente[i]=0;
        regla[0].consecuente[Clase]=alphaminimo;


        // Recalculate the weights of the other rules
        // with the same class in the base

        // in this version, we only update average value
        boolean PrimeraRegla=true;
        for (int i=0;i<BaseConocimiento.length;i++) {
            if (argmaxabs(BaseConocimiento[i].consecuente)==Clase) {
                NuevasAlphas[i]=BaseConocimiento[i].consecuente[Clase];
                if (PrimeraRegla) {
                    PrimeraRegla=false;
                    NuevasAlphas[i]+=mediaerr;
                }
            }
        }



        return fitness;
    }

    int argmaxabs(double []x) {
        double max=Math.abs(x[0]); int imax=0;
        for (int i=1;i<x.length;i++)
            if (Math.abs(x[i])>max) { max=Math.abs(x[i]); imax=i; }
                return imax;
    }

    void AnadeReglaLGB2C(int c,double [][][]Mu, double[]w, double[]dsigno) {
        // Find the best rule according to 'Logitboost descriptive' method
        // in a two classes problem

        int []antecedente = new int[nentradas];
        double []consecuente = new double[nsalidas];

        FuzzyRule result =
            new FuzzyRule(new int[nentradas], new double[nsalidas]);
        double [] AjusteAlphas =
            new double[BaseConocimiento.length];

        // Recalculate the importance of the examples
        double minfit=0;

        FuzzyRule TestRegla=new FuzzyRule(antecedente,consecuente);
        double [] NuevasAlphas = new double[BaseConocimiento.length];

        boolean PrimeraRegla=true;
        for (int i=0;i<BaseConocimiento.length;i++)
            if (argmaxabs(BaseConocimiento[i].consecuente)==c) {
                PrimeraRegla=false; break;
            }

                if (VersionBasica) PrimeraRegla=false;

        // ----------------------------------------------------------
        int permutaciones=1;
        boolean maxint=false;
        for (int i=0;i<result.antecedente.length;i++){
          // prevents int overflow
          if (permutaciones < Integer.MAX_VALUE/(Etiquetas[i].vertices.length+1))
            permutaciones*=(Etiquetas[i].vertices.length+1);
          else maxint=true;
        }
        if (maxint==true) {
          System.out.println("Total permutaciones > "+Integer.MAX_VALUE);
          permutaciones=Integer.MAX_VALUE;
        }
        else System.out.println("Total permutaciones="+permutaciones);

        if (permutaciones<1000 || PrimeraRegla) {

            // Exhaustive Search


            double fc=0, minfc=0; int q=1, perc=0;
            for (int p=0;p<permutaciones;p++) {
                q++;
                if (permutaciones>100) {
                    if (q>permutaciones/100) {
                        System.out.println("Terminado "+perc+"%");
                        q=0; perc++;
                    }
                }


                int cp=p;
                for (int i=0;i<result.antecedente.length;i++) {
                    TestRegla.antecedente[i]=cp%(Etiquetas[i].vertices.length+1);
                    cp=cp/(Etiquetas[i].vertices.length+1);
                }
                TestRegla.consecuente[c]=1;

                FuzzyRule ref[]=new FuzzyRule[1]; ref[0]=TestRegla;
                double eta[]=InferenciaBanco(c,Mu);
                fc=CambiaConsReglaLGB(ref,eta,c,Mu,dsigno,NuevasAlphas);

                // don't allow that the rule [000..00] appears twice
                if (!PrimeraRegla && p==0) fc=FITNESS_MALO;

                System.out.print("Probando regla "+p+" Ct");
                for (int i=0;i<TestRegla.antecedente.length;i++)
                	System.out.print(TestRegla.antecedente[i]+" ");
                System.out.print("->");
                for (int i=0;i<TestRegla.consecuente.length;i++)
                    System.out.print(TestRegla.consecuente[i]+" ");
                System.out.println(": Fitness="+fc);


                if (fc<minfc || p==0) {
                    for (int i=0;i<result.antecedente.length;i++)
                        result.antecedente[i]=TestRegla.antecedente[i];
                    for (int i=0;i<result.consecuente.length;i++)
                        result.consecuente[i]=TestRegla.consecuente[i];
                    for (int i=0;i<AjusteAlphas.length;i++)
                        AjusteAlphas[i]=NuevasAlphas[i];
                    minfc=fc;
                }

                if (PrimeraRegla) { PrimeraRegla=false; break; }
            }


            //------------------------------------------------------

        } else {

            // it is necessary to review the shutdown conditions!!!
            int nlabels=Etiquetas[0].vertices.length;
            double eta[]=InferenciaBanco(c,Mu);
            FitnessLGB fitness=new FitnessLGB(this,nentradas,nsalidas,
                                              nlabels,c,eta,Mu,dsigno,NuevasAlphas);
            GeneticAlgorithmForBoosting AG=new GeneticAlgorithmForBoosting(
                                                new GenotypeBoosting(nentradas,nlabels,r));
            GenotypeBoosting gen=AG.EncuentraMinimo(
                                                    nentradas,nlabels,fitness,250,r);
            FuzzyRule ref[]=new FuzzyRule[1];
            ref[0]=fitness.ConstruyeRegla(gen);
            double fc=CambiaConsReglaLGB(ref,eta,c,Mu,dsigno,AjusteAlphas);
            System.out.println("ECM Regla="+fc);
            result=ref[0];
        }

        // If the importance of rule is smaller than minimum allowed,
        // the rule is not added to the base
        if (Math.abs(result.consecuente[argmaxabs(result.consecuente)])<MIN_CONS) {
            System.out.println("No se incorpora la regla");
            return;
        }

        // incorporate the rule to the knowledge base
        FuzzyRule NuevoBanco[]=
            new FuzzyRule[BaseConocimiento.length+1];

        for (int i=0;i<BaseConocimiento.length;i++) {
            NuevoBanco[i]=BaseConocimiento[i];
            if (!VersionBasica) NuevoBanco[i].consecuente[c]=AjusteAlphas[i];
        }

        NuevoBanco[BaseConocimiento.length]=
            new FuzzyRule(new int[nentradas],new double[nsalidas]);
        for (int i=0;i<nentradas;i++)
            NuevoBanco[BaseConocimiento.length].antecedente[i]=
                result.antecedente[i];
        for (int i=0;i<nsalidas;i++) {
            NuevoBanco[BaseConocimiento.length].consecuente[i]=
            result.consecuente[i];
        }
        BaseConocimiento = NuevoBanco;

        System.out.println("Regla anadida"+
                           AString(result.antecedente)+
                           "->"+AString(result.consecuente));


    }

}


public class FB {

    static double W[][];
    static Randomize r;

    public static void fuzzycreavacio(
                                      int nentradas,      // inputs number
                                      int nsalidas,       // outputs number
                                      int nlabels,        // labels number
                                      double [] train,    // training inputs
                                      double [] ytrain,   // training outputs
                                      double [] bancoreglas,    // weights
                                      Randomize vr
                                      ) {

        // Creates an empty base of rules
        // common to all the fuzzy-boosting classification algorithms 

        r=vr;
        
        int nelem=train.length/nentradas;

        System.out.println("Numero entradas="+nentradas);
        System.out.println("Numero salidas="+nsalidas);
        System.out.println("Dimension train="+train.length);
        System.out.println("Dimension ytrain="+ytrain.length);
        System.out.println("Dimension bancoreglas="+bancoreglas.length);
        System.out.println("Numero ejemplos="+nelem);
        System.out.println("Numero de etiquetas por variable="+nlabels);
        double errf=0;

        double[][] ejemplos=new double[nelem][nentradas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nentradas;j++) {
                ejemplos[i][j]=(double)train[i*nentradas+j];
            }
        }

        double[][] deseado=new double[nelem][nsalidas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nsalidas;j++) {
                deseado[i][j]=(double)ytrain[i+j*nelem];
            }
        }

        // AdaBoost cf=new AdaBoost(ejemplos,deseado);
        FuzzyClassifier cf=new FuzzyClassifier(ejemplos,deseado);

        // System.out.println("ejemplos="+cf.AString(ejemplos));
        // System.out.println("deseado="+cf.AString(deseado));

        // Particion linguistica uniforme
        cf.EstimaParticiones(nlabels);

        // Store all parameters in vector (of R)
        cf.AlmacenaParametros(bancoreglas);
        // System.out.println("BancoReglas="+cf.AString(bancoreglas));

    }


    public static double[] fuzzyclasifica(
                                         double [] x,       // imput
                                         int nsalidas,      // outputs number
                                         double [] bancoreglas    // weights
                                         ) {

        // pattern classification with a Fuzzy Classifier
        // common to all the fuzzy-boosting classification algorithms 

        double [][]ejemplos = new double[1][]; ejemplos[0]=x;
        double [][]deseado = new double[1][nsalidas];

        // Load R vector parameters
        // AdaBoost cf=new AdaBoost(ejemplos,deseado);
        FuzzyClassifier cf=new FuzzyClassifier(ejemplos,deseado);
        cf.RecuperaParametros(bancoreglas);

        double Mu[][][]=new double[1][cf.Etiquetas.length][];
        for (int i=0;i<cf.nelem;i++) {
            for (int v=0;v<cf.Etiquetas.length;v++) {
                Mu[i][v]=cf.Etiquetas[v].pertenencia(cf.ejemplos[i][v]);
            }
        }


        double suma[]=new double[nsalidas];
        for (int r=0;r<cf.BaseConocimiento.length;r++) {
            double tr=cf.CertezaAntecedenteEj(0,cf.BaseConocimiento[r],Mu);
            for (int k=0;k<nsalidas;k++)
                suma[k]+=cf.BaseConocimiento[r].consecuente[k]*tr;
        }

        return suma;

    }
    public static double[] fuzzyclasificamaxmin(
                                               double [] x,        // input
                                               int nsalidas,       // outputs number
                                               double [] bancoreglas    // weights
                                               ) {

        // pattern classification with a Fuzzy Classifier 
        // common to all the fuzzy-boosting classification algorithms 

        double [][]ejemplos = new double[1][]; ejemplos[0]=x;
        double [][]deseado = new double[1][nsalidas];

        // Load R vector parameters
        // AdaBoost cf=new AdaBoost(ejemplos,deseado);
        AdaBoostMaxMin cf=new AdaBoostMaxMin(ejemplos,deseado,r);
        cf.RecuperaParametros(bancoreglas);

        double Mu[][][]=new double[1][cf.Etiquetas.length][];
        for (int i=0;i<cf.nelem;i++) {
            for (int v=0;v<cf.Etiquetas.length;v++) {
                Mu[i][v]=cf.Etiquetas[v].pertenencia(cf.ejemplos[i][v]);
            }
        }

        double maximos[]=new double[nsalidas];
        double W[]=new double[1];
        int cmax=cf.fuzzyclasificamaxmin(0,Mu,W);
        maximos[cmax]=W[0];
        return maximos;
        

    }


    public static void fadaboostinc(
                                    int nentradas,         // inputs number
                                    int nsalidas,          // outputs number
                                    double [] train,       // training inputs
                                    double [] ytrain,      // training outputs
                                    double [] bancoreglas  // weights
                                    ) {

        // Add a new rule to the base using AdaBoost algorithm

        int nelem=train.length/nentradas;

        System.out.println("Numero entradas="+nentradas);
        System.out.println("Numero salidas="+nsalidas);
        System.out.println("Dimension train="+train.length);
        System.out.println("Dimension ytrain="+ytrain.length);
        System.out.println("Dimension bancoreglas="+bancoreglas.length);
        System.out.println("Numero ejemplos="+nelem);
        double errf=0;

        double[][] ejemplos=new double[nelem][nentradas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nentradas;j++) {
                ejemplos[i][j]=(double)train[i*nentradas+j];
            }
        }

        double[][] deseado=new double[nelem][nsalidas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nsalidas;j++) {
                deseado[i][j]=(double)ytrain[i+j*nelem];
            }
        }

        AdaBoost cf=new AdaBoost(ejemplos,deseado,r);

        // Reload Classifier parameters
        cf.RecuperaParametros(bancoreglas);

        double Mu[][][]=new double[cf.nelem][cf.Etiquetas.length][];
        for (int i=0;i<cf.nelem;i++) {
            for (int v=0;v<cf.Etiquetas.length;v++) {
                Mu[i][v]=cf.Etiquetas[v].pertenencia(cf.ejemplos[i][v]);
            }
        }
        W=new double[nsalidas][nelem];

        int limite=nsalidas; if (limite==2) limite=1;
        int Clase=-1;

        double maxerr=0;
        double []total=new double[nsalidas];

        // the rule of the class with smaller average weight is added
        // int []fallos=new int[nsalidas];
        // for (int i=0;i<nelem;i++) {
        //    double[] segs=fuzzyclasifica(ejemplos[i],nsalidas,bancoreglas);
        //      if (argmax(segs)!=argmax(deseado[i])) fallos[argmax(deseado[i])]++;
        //      total[argmax(deseado[i])]++;
        // }
        // for (int c=0;c<limite;c++) {
        //     System.out.println("Error "+c+"="+fallos[c]/(double)total[c]);
        //     if (fallos[c]/(double)total[c] > maxerr) {
        //        maxerr=fallos[c]/(double)total[c];
        //        Clase=c;
        //     }
        // }

        // the rule of the class with less number of rules 
        for (int r=0;r<cf.BaseConocimiento.length;r++) {
            total[cf.argmaxabs(cf.BaseConocimiento[r].consecuente)]++;
        }
        Clase=argmin(total);

        // In two classes problems -> class=0 
        // this way the same rules are not repeated twice
        if (nsalidas==2) Clase=0;

        System.out.println("PROCESANDO CLASE "+Clase);
        double []dsigno=cf.CalculaSignoDeseado(Clase);
        cf.CalculaPesosEjemplosABD(W[Clase],Clase,Mu,dsigno);
        cf.AnadeReglaABD2C(Clase,Mu,W[Clase],dsigno);

        // store parameters in R vector
        cf.AlmacenaParametros(bancoreglas);

        // Test
        cf.MuestraBase();

    }

    public static void flogitboostinc(
                                      int nentradas,         // inputs number
                                      int nsalidas,          // outputs number
                                      double [] train,       // training inputs
                                      double [] ytrain,      // training outputs
                                      double [] bancoreglas, // weights
                                      boolean VB             // Basic version - Prefitting
                                      ) {

        // Add a new rule to the base using LogitBoost algorithm

        int nelem=train.length/nentradas;

        System.out.println("Numero entradas="+nentradas);
        System.out.println("Numero salidas="+nsalidas);
        System.out.println("Dimension train="+train.length);
        System.out.println("Dimension ytrain="+ytrain.length);
        System.out.println("Dimension bancoreglas="+bancoreglas.length);
        System.out.println("Numero ejemplos="+nelem);
        double errf=0;

        double[][] ejemplos=new double[nelem][nentradas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nentradas;j++) {
                ejemplos[i][j]=(double)train[i*nentradas+j];
            }
        }

        double[][] deseado=new double[nelem][nsalidas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nsalidas;j++) {
                deseado[i][j]=(double)ytrain[i+j*nelem];
            }
        }

        LogitBoost cf=new LogitBoost(ejemplos,deseado,VB,r);

        // Reload Classifier
        cf.RecuperaParametros(bancoreglas);

        double Mu[][][]=new double[cf.nelem][cf.Etiquetas.length][];
        for (int i=0;i<cf.nelem;i++) {
            for (int v=0;v<cf.Etiquetas.length;v++) {
                Mu[i][v]=cf.Etiquetas[v].pertenencia(cf.ejemplos[i][v]);
            }
        }
        W=new double[nsalidas][nelem];

        int limite=nsalidas; if (limite==2) limite=1;
        int Clase=-1;

        double maxerr=0;
        double []total=new double[nsalidas];

        // a rule of the class with smaller average weight is added
        // int []fallos=new int[nsalidas];
        // for (int i=0;i<nelem;i++) {
        //    double[] segs=fuzzyclasifica(ejemplos[i],nsalidas,bancoreglas);
        //      if (argmax(segs)!=argmax(deseado[i])) fallos[argmax(deseado[i])]++;
        //      total[argmax(deseado[i])]++;
        // }
        // for (int c=0;c<limite;c++) {
        //     System.out.println("Error "+c+"="+fallos[c]/(double)total[c]);
        //     if (fallos[c]/(double)total[c] > maxerr) {
        //        maxerr=fallos[c]/(double)total[c];
        //        Clase=c;
        //     }
        // }

        // the rule of the class with less number of rules is added
        for (int r=0;r<cf.BaseConocimiento.length;r++) {
            total[cf.argmaxabs(cf.BaseConocimiento[r].consecuente)]++;
        }
        Clase=argmin(total);

        // In two classes problems -> class=0 
        // this way the same rules are not repeated twice
        if (nsalidas==2) Clase=0;

        System.out.println("PROCESANDO CLASE "+Clase);
        double []dsigno=cf.CalculaSignoDeseado(Clase);
        cf.AnadeReglaLGB2C(Clase,Mu,W[Clase],dsigno);

        // Store parameters in R vector
        cf.AlmacenaParametros(bancoreglas);

        // Test
        cf.MuestraBase();

    }


    public static void fadaboostincmaxmin(
                                          int nentradas,          // inputs number
                                          int nsalidas,           // outputs number
                                          double [] train,        // training inputs
                                          double [] ytrain,       // training outputs
                                          double [] bancoreglas,  // weights
                                          double [] oldfit
                                          ) {

        // Add a new rule to the base using AdaBoostMaxMin algorithm

        int nelem=train.length/nentradas;

        System.out.println("Numero entradas="+nentradas);
        System.out.println("Numero salidas="+nsalidas);
        System.out.println("Dimension train="+train.length);
        System.out.println("Dimension ytrain="+ytrain.length);
        System.out.println("Dimension bancoreglas="+bancoreglas.length);
        System.out.println("Numero ejemplos="+nelem);
        double errf=0;

        double[][] ejemplos=new double[nelem][nentradas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nentradas;j++) {
                ejemplos[i][j]=(double)train[i*nentradas+j];
            }
        }

        double[][] deseado=new double[nelem][nsalidas];
        for (int i=0;i<nelem;i++) {
            for (int j=0;j<nsalidas;j++) {
                deseado[i][j]=(double)ytrain[i+j*nelem];
            }
        }

        AdaBoostMaxMin cf=new AdaBoostMaxMin(ejemplos,deseado,r);

        // Reload classifier
        cf.RecuperaParametros(bancoreglas);

        // Pre-calculate the ownership of each example/label to optimize time
        double Mu[][][]=new double[cf.nelem][cf.Etiquetas.length][];
        for (int i=0;i<cf.nelem;i++) {
            for (int v=0;v<cf.Etiquetas.length;v++) {
                Mu[i][v]=cf.Etiquetas[v].pertenencia(cf.ejemplos[i][v]);
            }
        }

        double newfit=cf.AnadeReglaABDMM(Mu);

        // store parameters in R vector
        // the rule is added if it improves fitness
        if (oldfit[0]>0 && newfit>=oldfit[0]) {
           System.out.println("No se almacena la regla!");
           return; 
        } else cf.AlmacenaParametros(bancoreglas);

        // Test
        cf.MuestraBase();
        oldfit[0]=newfit;

    }





    // ------------------------------------------------------
    // TEST TEST TEST 
    // ------------------------------------------------------

    static double train[][]; static int ctrain[]; // training
    static int nfeatures;   // inputs number
    static int nclases;     // classes number

    static double test[][]; static int ctest[];   // Test
    
    static double f[][];
    static int c[];



    private static void CargaDatosClasifGranada(String nombre) {

        // Read a data file with Paco's group format:

        // First line: number of examples
        // Second line: characteristics number + 1
        // One example by line
        // The classes are coded using integers. First class = 0

        int nejemplos;
        nclases=1;

        try {
            BufferedReader in =
            new BufferedReader(new FileReader(nombre));
            String linea=in.readLine();
            nejemplos=Integer.parseInt(linea);
            linea=in.readLine();

            // An only exit, the mid-value of class
            nfeatures=Integer.parseInt(linea)-1;
            f=new double[nejemplos][nfeatures];
            c=new int[nejemplos];

            for (int i=0;i<nejemplos;i++) {
                linea=in.readLine();
                if (linea==null) break;
                StringTokenizer tokens=new StringTokenizer(linea," ,");
                for (int j=0;j<nfeatures;j++) {
                    f[i][j]=Double.parseDouble(tokens.nextToken());
                }
                c[i]=Integer.parseInt(tokens.nextToken());

                // Number of classes.
                if (c[i]+1>nclases) nclases=c[i]+1;
            }


        } catch(FileNotFoundException e) {
            System.err.println(e+" Fichero "+nombre+" no encontrado");
        } catch(IOException e) {
            System.err.println(e+" Error lectura");
        }

    }

    public static int argmax(double []x) {
        double max=x[0]; int imax=0;
        for (int i=1;i<x.length;i++)
            if (x[i]>max) { max=x[i]; imax=i; }
                return imax;
    }

    private static int argmin(double []x) {
        double min=x[0]; int imin=0;
        for (int i=1;i<x.length;i++)
            if (x[i]<min) { min=x[i]; imin=i; }
                return imin;
    }



    public static void main(String argv[]) {

        // Test Rutine

        if (argv.length==0) {
            System.out.println("Falta fichero de train");
            System.exit(1);
        }
        if (argv.length==1) {
            System.out.println("Falta fichero de test");
            System.exit(1);
        }
        if (argv.length==2) {
            System.out.println("Falta numero de reglas");
            System.exit(1);
        }
        if (argv.length==3) {
            System.out.println("Falta numero de etiquetas");
            System.exit(1);
        }


        // Load training file
        CargaDatosClasifGranada(argv[0]); train=f; ctrain=c;
        CargaDatosClasifGranada(argv[1]); test=f; ctest=c;
        int nreglas = Integer.parseInt(argv[2]);
        int nlabels = Integer.parseInt(argv[3]);

        // remake !!!
        double []bancoreglas=new double[2000];
        int nentradas=nfeatures;
        int nsalidas=nclases;
        double ytrain[][]=new double[train.length][nsalidas];
        double ytest[][]=new double[test.length][nsalidas];
        for (int i=0;i<ytrain.length;i++) ytrain[i][ctrain[i]]=1;
        for (int i=0;i<ytest.length;i++) ytest[i][ctest[i]]=1;

        int p=0;
        double lintrain[]=new double[train.length*train[0].length];
        for (int i=0;i<train.length;i++)
            for (int j=0;j<train[i].length;j++) lintrain[p++]=train[i][j];
        p=0;
        double lintest[]=new double[test.length*test[0].length];
        for (int i=0;i<test.length;i++)
            for (int j=0;j<test[i].length;j++) lintest[p++]=test[i][j];
        p=0;
        double linytrain[]=new double[ytrain.length*ytrain[0].length];
        for (int j=0;j<ytrain[0].length;j++)
            for (int i=0;i<ytrain.length;i++)
                linytrain[p++]=ytrain[i][j];
        p=0;
        double linytest[]=new double[ytest.length*ytest[0].length];
        for (int j=0;j<ytest[0].length;j++)
            for (int i=0;i<ytest.length;i++)
                linytest[p++]=ytest[i][j];

        FB fb=new FB();

        // % training accuracy
        double fallos=0;
        fb.fuzzycreavacio(nentradas,nsalidas,nlabels,
                          lintrain,linytrain,bancoreglas,r);

        int limite=0; if (nsalidas==2) limite=1; else limite=nsalidas;
        double fit[]=new double[1];

        for (int r=0;r<nreglas;r++) {
            fallos=0;
            // fb.fadaboostinc(nentradas,nsalidas,lintrain,linytrain,bancoreglas);
            // fb.flogitboostinc(nentradas,nsalidas,lintrain,linytrain,bancoreglas,false);
            fb.fadaboostincmaxmin(
               nentradas,nsalidas,lintrain,linytrain,bancoreglas,fit);
            for (int i=0;i<train.length;i++) {
                // double[] segs=fuzzyclasifica(train[i],nsalidas,bancoreglas);
                double[] segs=fuzzyclasificamaxmin(train[i],nsalidas,bancoreglas);
                for (int k=0;k<segs.length;k++) System.out.print(segs[k]+" ");
                System.out.print("Clase="+ctrain[i]);
                int ac=argmax(segs);
                if (ac!=(int)ctrain[i]) {
                  fallos++;
                  System.out.println(" Fallo");
                } else {
                  System.out.println(" Acierto");
                }
            }
            System.out.println("Error Train: ="+fallos/train.length);


            // % test accuracy
            fallos=0;
            for (int i=0;i<test.length;i++) {
             double[] segs=fuzzyclasificamaxmin(test[i],nsalidas,bancoreglas);
             if (argmax(segs)!=ctest[i]) fallos++;
            }
            System.out.println("Test completo: "+fallos/test.length);
        }
    }

}




