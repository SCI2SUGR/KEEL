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

public class FuzzyClassifier {
    public double ejemplos[][];
    public double deseado[][];
    public int nentradas;
    public int nsalidas;
    public int nelem;
    public int nejemplos[];

    public final double ACT_MIN=0.000f;    // Min rule activation
    public final double PENAL_CUB=0.000f;  // not cover penalty
    public final double MIN_CONS=0.000f;   // Min consecuent

    public FuzzyPartition Etiquetas[];
    public FuzzyRule BaseConocimiento[];

    public FuzzyClassifier(double [][]vejemplos, double [][]vdeseado) {
      ejemplos=vejemplos; deseado=vdeseado;
      nentradas=ejemplos[0].length; nsalidas=deseado[0].length;
      nelem=ejemplos.length;
      nejemplos=new int[nsalidas];
      for (int i=0;i<nelem;i++) {
        for (int s=0;s<nsalidas;s++)
         if (deseado[i][s]!=0) { nejemplos[s]++; }
      }
      Etiquetas=new FuzzyPartition[nentradas];
      BaseConocimiento=new FuzzyRule[0];
    }

    public String AString(double []s) {
     String result="[";
     for (int i=0;i<s.length;i++) result=result+s[i]+" ";
     return result+"]";
    }
    public String AString(int []s) {
     String result="[";
     for (int i=0;i<s.length;i++) result=result+s[i]+" ";
     return result+"]";
    }
    public String AString(int [][]s) {
     String result="[";
     for (int i=0;i<s.length;i++) result=result+AString(s[i])+" ";
     return result+"]";
    }
    public String AString(double [][]s) {
     String result="[";
     for (int i=0;i<s.length;i++) result=result+AString(s[i])+" ";
     return result+"]";
    }

    public double [] Agrega(double []a, double []b) {
      double res[]=new double[a.length];
      for (int i=0;i<a.length;i++) {
        res[i]=a[i]+b[i];
      }
      return res;
    }

    public void EstimaParticiones(int n) { 

       double tmp[]=new double[n];

       for (int c=0;c<nentradas;c++) {
         // calc var ranges 
         double max=ejemplos[0][c];
         double min=ejemplos[0][c];
         for (int i=1;i<nelem;i++) {
           if (ejemplos[i][c]>max) max=ejemplos[i][c];
           if (ejemplos[i][c]<min) min=ejemplos[i][c];
         }
         // equal length partitions
         for (int i=0;i<n;i++) { 
           tmp[i]=min+(max-min)/(n-1)*i;
         }
         // System.out.println("tmp="+AString(tmp));
         Etiquetas[c]=new FuzzyPartition(tmp);
       }
       

    }

    public void AlmacenaParametros(double []bancoreglas) {
      int p=0;
      // First: store partitions
      for (int i=0;i<nentradas;i++) {
        // System.out.println("Almaceno particion v="+i+" size="+
        //   Etiquetas[i].vertices.length);
        bancoreglas[p++]=Etiquetas[i].vertices.length;
        for (int j=0;j<Etiquetas[i].vertices.length;j++) 
           bancoreglas[p++]=Etiquetas[i].vertices[j];
      }
      // then, store number of rules + rule definitions
      // System.out.println("Almaceno numero reglas="+BaseConocimiento.length);
      bancoreglas[p++]=BaseConocimiento.length;
      for (int i=0;i<BaseConocimiento.length;i++) {
        for (int j=0;j<BaseConocimiento[i].antecedente.length;j++)
            bancoreglas[p++]=BaseConocimiento[i].antecedente[j];
        for (int j=0;j<BaseConocimiento[i].consecuente.length;j++)
            bancoreglas[p++]=BaseConocimiento[i].consecuente[j];
      }

    }
    public void RecuperaParametros(double []bancoreglas) {
      int p=0;
      for (int i=0;i<nentradas;i++) {
         int size=(int)bancoreglas[p++];
         // System.out.println("Particion v="+i+" size="+size);
         double tmp[]=new double[size];
         for (int j=0;j<tmp.length;j++) tmp[j]=bancoreglas[p++];
         Etiquetas[i]=new FuzzyPartition(tmp);
      }
      int numreglas=(int)bancoreglas[p++];
      // System.out.println("Numero de reglas="+numreglas);
      BaseConocimiento=new FuzzyRule[numreglas];
      for (int i=0;i<numreglas;i++) {
        int tmpa[]=new int[nentradas];
        double tmpc[]=new double[nsalidas];
        for (int j=0;j<tmpa.length;j++) tmpa[j]=(int)bancoreglas[p++];
        for (int j=0;j<tmpc.length;j++) tmpc[j]=bancoreglas[p++];
        BaseConocimiento[i]=new FuzzyRule(tmpa,tmpc);
      }
    }

    public double CertezaAntecedenteEj(int i,FuzzyRule regla, double [][][]Mu) {

       double result=1;
       for (int v=0;v<regla.antecedente.length;v++) {
        if (regla.antecedente[v]>0) 
           result=regla.tnorma(result,Mu[i][v][regla.antecedente[v]-1]);
       }
       return result;
    }

    public double[] CertezaAntecedente(FuzzyRule regla, double [][][]Mu) {

     double []result=new double[nelem];
     for (int i=0;i<nelem;i++) {
       // antecedent = '0' => null performance 
       result[i]=CertezaAntecedenteEj(i,regla,Mu);
     }
     return result;

    }


    public double[] InferenciaBanco(int Clase, double[][][]Mu) {

     // assigned weigths for the examples by the rules bank 
     // considering only information 'ejemplo=clase' or 'ejemplo!=clase'


     double result[]=new double[nelem];
     for (int i=0;i<nelem;i++) {
       double suma[]=new double[nsalidas];
       for (int r=0;r<BaseConocimiento.length;r++) {
         if (BaseConocimiento[r].consecuente[Clase]==0) continue;
         double tr=CertezaAntecedenteEj(i,BaseConocimiento[r],Mu);
         for (int k=0;k<nsalidas;k++) 
          suma[k]+=BaseConocimiento[r].consecuente[k]*tr;
       }
       for (int k=0;k<nsalidas;k++)
         if (k==Clase) result[i]+=suma[k];
         else result[i]-=suma[k];
     }
     return result;
    }

    public int[][] CalculaI(double [][][]Mu,FuzzyRule []BC) {

      // Marc rule for which the example belongs
      // Uncover examples have null value

      int [][] I=new int[nelem][BC.length];
      for (int i=0;i<nelem;i++) {
           double max=0; int imax=0; boolean primera=true;
           for (int r=0;r<BC.length;r++) {
            double tr=CertezaAntecedenteEj(i,BaseConocimiento[r],Mu);
            if (tr==0) continue;
            double act=0;
            for (int c=0;c<BC[r].consecuente.length;c++) {
               double cact=tr*BC[r].consecuente[c];
               if (cact>act || c==0) act=cact;
            }
            // System.out.println("ejemplo="+i+" act="+act);
            if (act>max || primera) { primera=false; max=act; imax=r; }
           }
           I[i][imax]=1;
      }
      return I;

    }


    public double[] CalculaSignoDeseado(int Clase) {
     
     double dsigno[]=new double[nelem];
     for (int i=0;i<nelem;i++) {
        if (deseado[i][Clase]!=0) dsigno[i]=1; else dsigno[i]=-1;
     }
     return dsigno;

    }

    public void MuestraBase() {

     System.out.println("La base es:");
     for (int i=0;i<BaseConocimiento.length;i++) {
       System.out.println(AString(BaseConocimiento[i].antecedente)+
                          "->"+AString(BaseConocimiento[i].consecuente));
     }
    }
}


