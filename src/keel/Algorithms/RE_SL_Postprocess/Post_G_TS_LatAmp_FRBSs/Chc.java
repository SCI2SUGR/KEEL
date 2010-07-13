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

package keel.Algorithms.RE_SL_Postprocess.Post_G_TS_LatAmp_FRBSs;
/**
 * <p>Title: the cycle class of the algorithm</p>
 *
 * <p>Description: It do CHC cycle</p>
 *
 * @author Diana Arquillos
 * @version 1.0
 */

public class Chc {
	
	/*INICIALIZA VARIABLES*/
	  private int i, NUEVOCROM; 
	  private int rescue;
	  private double decrement = 1;
	  private double [] TMEJORES, TMEJORESTST;
	  private double ec_tst, ec_tra; 
	  private double bestold=0.0;
	  private int [] TMEJORESR;
	  private  Poblacion P;
	  private int Experiment;
	  private int Totalexperiments;
	  private int Totaltrials;
	  public Chc(int total){
		  Totalexperiments=1;
		  Totaltrials=total;
		  Experiment = 0;
		  rescue = 5000;
		  TMEJORESR = new int [50];
		  TMEJORES = new double [50];
		  TMEJORESTST = new double [50];
		  
	  }
	  
	  /************************************************************/
	  /**
	     * Function cycle
	     * @param long_tabla_tra It contains the size of the table of training<br/>
	     * @param vtra double [][] It contains the input training values<br/>
	     * @param stra double [] It contains the output training values<br/>
	     * @param long_tabla_tst It contains the size of the table of test<br/>
	     * @param vtest double [][] It contains the input test values<br/>
	     * @param stst [] It contains the output test values<br/>
	     * @param num It contains the number of the data <br/>
	     * @param reglas It contains the number of rules<br/>
	     * @param var It has the number of state var<br/>
	     * @param sal double it contains the value of exit<br/>
	     * @param v double [] it contains the values of data base<br/>
	     * @param pop int It has the size of population<br/>
	     * @param bits int It has the number of bits<br/>
	     * @param semilla long It has the value of seed<br/>
	     * @param fich String It has the name of exit file<br/>
	     */
	  public void cycle(int long_tabla_tra,double[][] vtra,double []stra,int long_tabla_tst,double[][] vtest,double []stst,int num,int reglas, int var, double sal,double []v,int pop, int bits,long semilla, String fich) {
	 
	  	  P = new Poblacion(pop,bits);
		  do{
		  
	      P.Initialize(long_tabla_tra, vtra,stra, long_tabla_tst, vtest,stst, num,reglas, var, sal,v,semilla);
		  P.Evaluate(0, P.getPopsize());
	      P.getBEST_CROM().set_entrado(0);
	      P.getBEST_CROM().set_perf (P.getPoblacion(0).perf());
	      decrement = P.getReduccionIni();
	     
	      do{
		  P.Cruce(P.getTrials());
		P.Evaluate(P.getPopsize(), P.getPOPSIZE());
		P.Select();
		  
		  /* COMPROBAR SI HA ENTRADO EN LA POBLACION ALGUN CROMOSOMA NUEVO */
		  for (i=NUEVOCROM=0; i<P.getPopsize(); i++){
		    if (P.getPoblacion(i).entrado()==1) {
		    	NUEVOCROM=1;
		    }
		  }
		  if (P.getBest() != bestold) {
	     		decrement = P.getReduccionIni();
		  		bestold = P.getBest();
	     }
	     else{
	     		P.setTHRESHOLD(P.getTHRESHOLD()-decrement);
	     		if (P.getTHRESHOLDR() >= 0.0)  P.setTHRESHOLDR(P.getTHRESHOLDR()-1);
	     } 
	     if (NUEVOCROM==0) {
	     	 P.setTHRESHOLD(P.getTHRESHOLD()-decrement);
	     		if (P.getTHRESHOLDR() >= 0.0)  P.setTHRESHOLDR(P.getTHRESHOLDR()-1);
		  		
	     }

		  if (P.getTHRESHOLD()<0.0 && P.getTHRESHOLDR()<0.0) {
	
			  P.ReStart();
	        if (P.getBEST_CROM().entrado()==1)
		        P.Evaluate(1, P.getPopsize());
	        else
		        P.Evaluate(0, P.getPopsize());
	        P.setTHRESHOLD((double)(P.getGenes()*P.getBITS_GEN())/4.0);
	        decrement = P.getReduccionIni();
	        P.getBEST_CROM().set_entrado(0);
	     }
		  ec_tst = P.getE().ECM_tst(P.getBEST_CROM().Gene(),P.getBEST_CROM().GeneA(),P.getBEST_CROM().GeneR(),P.getNreglasTotal());
		
		  ec_tra = P.getBEST_CROM().perf();
		  if (P.getTrials() > rescue && rescue < Totaltrials) {
	    	 rescue += 5000;
	     }
		}while (P.getTrials()<Totaltrials);    /* FINAL DE LAS ITERACIONES */

	      /* ACTUALIZAR CONTADORES DE FINAL DE EJECUCION */
	      TMEJORES[Experiment]= ec_tra/*Best*/;
	      TMEJORESTST[Experiment]=ec_tst;
	      TMEJORESR[Experiment]=P.getE().num_reglas();

	      Experiment++;

	    }while ( Experiment < Totalexperiments ); /* FINAL DE LAS EJECUCIONES */
	}
	/** FIN DEL FICHERO **/

	public Poblacion getP() {
		return P;
	}

	public void setP(Poblacion p) {
		P = p;
	}

	public double getEc_tst() {
		return ec_tst;
	}

	public void setEc_tst(double ec_tst) {
		this.ec_tst = ec_tst;
	}

	public double getEc_tra() {
		return ec_tra;
	}

	public void setEc_tra(double ec_tra) {
		this.ec_tra = ec_tra;
	}

	public int getTotalexperiments() {
		return Totalexperiments;
	}

	public void setTotalexperiments(int totalexperiments) {
		Totalexperiments = totalexperiments;
	}

	public int getTotaltrials() {
		return Totaltrials;
	}

	public void setTotaltrials(int totaltrials) {
		Totaltrials = totaltrials;
	}
	  



}

