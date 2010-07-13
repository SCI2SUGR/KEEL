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

public class GenotypeBoostingMaxMin extends GenotypeBoosting {


    public double alpha[];
    public boolean entra[];

    public pair_gg cruce(GenotypeBoosting b) {
        // crossover operator
        // System.out.println("antes:"+AString()+"+"+b.AString());
        double alfa=0.5f;

        pair_gg result=super.cruce(b);
        GenotypeBoostingMaxMin tmp1=(GenotypeBoostingMaxMin)result.first;
        GenotypeBoostingMaxMin tmp2=(GenotypeBoostingMaxMin)result.second;
        
        for (int i=0;i<tmp1.alpha.length;i++) {
            tmp1.alpha[i]=(tmp1.alpha[i]+(-tmp1.alpha[i]+tmp2.alpha[i])*r.Rand()*alfa);
            tmp2.alpha[i]=(tmp2.alpha[i]+(tmp1.alpha[i]-tmp2.alpha[i])*r.Rand()*alfa);
        }

        // uniform crossover
        for (int i=0;i<tmp1.entra.length;i++) {
           if (r.Rand()>0.5) {
             boolean temp=tmp1.entra[i];
             tmp1.entra[i]=tmp2.entra[i];
             tmp2.entra[i]=temp;
           } 
        }

        pair_gg res1=new pair_gg(tmp1,tmp2);
        // System.out.println("despues:"+result.first.AString()+"+"+result.second.AString());
                
        return res1;        
    }

    public GenotypeBoosting mutacion() {
        // mutation operator
        GenotypeBoostingMaxMin tmp1=(GenotypeBoostingMaxMin)super.mutacion();
        int pos=(int)(r.Rand()*alpha.length);
        tmp1.alpha[pos]=r.Rand()-0.5f;
        pos=(int)(r.Rand()*tmp1.entra.length);
        if (tmp1.entra[pos]) tmp1.entra[pos]=false; else tmp1.entra[pos]=true;
        return tmp1;
    }
    
    public GenotypeBoostingMaxMin(int nentradas, int nl, int nsalidas, int nreglas, Randomize vr) {
        super(nentradas,nl,vr);
        alpha=new double[nsalidas*nreglas];
        entra=new boolean[nreglas];
    }

    public void inicializa() {
        // init: set random values 
        super.inicializa();
        final double AMPL=1.0f;
        for (int i=0;i<alpha.length;i++) alpha[i]=AMPL*(r.Rand()-0.5f);
        for (int i=0;i<entra.length;i++) if (r.Rand()>0.5f) entra[i]=true;
    }

    public String AString() {
        String result=super.AString();
        result=result+" -> [";
        for (int i=0;i<alpha.length;i++) result=result+alpha[i]+" ";
        for (int i=0;i<entra.length;i++) result=result+entra[i]+" ";
        result=result+"]";
        return result;
    }

    public GenotypeBoosting duplica() {
        GenotypeBoostingMaxMin tmp= 
         new GenotypeBoostingMaxMin(x.length,nlabels,
          alpha.length/entra.length,entra.length,r);
        for (int i=0;i<x.length;i++) tmp.x[i]=x[i];
        for (int i=0;i<alpha.length;i++) tmp.alpha[i]=alpha[i];
        for (int i=0;i<entra.length;i++) tmp.entra[i]=entra[i];
        return tmp;
    }

    
  public double distancia(GenotypeBoosting g1) {
    // Compute the Distance between GenotipoBoostings
    // double suma=super.distancia(g1);
    GenotypeBoostingMaxMin g= (GenotypeBoostingMaxMin)g1;
    // for (int i=0;i<alpha.length;i++) suma+=(alpha[i]-g.alpha[i])*(alpha[i]-g.alpha[i]);
    double suma=0;
    for (int i=0;i<entra.length;i++) if (entra[i]!=g.entra[i]) suma++;
    return suma;
  }

}


