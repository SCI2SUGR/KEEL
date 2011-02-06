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

package keel.Algorithms.Genetic_Rule_Learning.RMini;


/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jesús Jiménez
 * @version 1.0
 */

import java.io.IOException;
import java.util.ArrayList;
import org.core.*;

public class RMini {

	myDataset train, val, test;
	String outputTr, outputTst, fichEstadisticas;
	int nClasses;

	//We may declare here the algorithm's parameters
	private int maxIter;
	private ArrayList<Cubo> cubos;
	private ArrayList<String> salidas;
	private ArrayList<Regla> reglas;
	private ArrayList<Cubo> F;
	private ArrayList<Cubo> G;
	private ArrayList<Cubo> FF;
	private int nPartes;
	private int nBits[];
	private int numCasos;
	private int bienClasificados;
	private int numCasosTra;
	private int bienClasificadosTra;
	private int numCasosTst;
	private int bienClasificadosTst;

	private boolean somethingWrong = false; //to check if everything is correct.

	/**
	 * Default constructor
	 */
	public RMini() {
	}

	/**
	 * It reads the data from the input files (training, validation and test) and parse all the parameters
	 * from the parameters array.
	 * @param parameters parseParameters It contains the input files, output files and parameters
	 */
	public RMini(parseParameters parameters) {
		train = new myDataset();
		val = new myDataset();
		test = new myDataset();
		try {
			System.out.println("\nReading the training set: " +
					parameters.getTrainingInputFile());
			train.readClassificationSet(parameters.getTrainingInputFile(), true);			
			System.out.println("\nReading the validation set: " +
					parameters.getValidationInputFile());
			val.readClassificationSet(parameters.getValidationInputFile(), false);
			System.out.println("\nReading the test set: " +
					parameters.getTestInputFile());
			test.readClassificationSet(parameters.getTestInputFile(), false);
		} catch (IOException e) {
			System.err.println(
					"There was a problem while reading the input data-sets: " +
					e);
			somethingWrong = true;
		}

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		somethingWrong = somethingWrong || train.hasNumericalAttributes();
		somethingWrong = somethingWrong || train.hasMissingAttributes();//de momento no los acepto
		
		outputTr = parameters.getTrainingOutputFile();
		outputTst = parameters.getTestOutputFile();
		fichEstadisticas=parameters.getOutputFile(0);

		//Now we parse the parameters, for example:
		/*
 		seed = Long.parseLong(parameters.getParameter(0));
 		iterations = Integer.parseInt(parameters.getParameter(1));
 		crossOverProb = Double.parseDouble(parameters.getParameter(2));
		 */
		maxIter=Integer.parseInt(parameters.getParameter(0));//RMini solo tiene un parametro

	}
	
	/**
	 * It launches the algorithm
	 */
	public void execute() {
		if (somethingWrong) { //We do not execute the program
			System.err.println("An error was found, either the data-set have numerical values or missing values.");
			System.err.println("Aborting the program");
			//We should not use the statement: System.exit(-1);
		} else {
			
			//We do here the algorithm's operations

			nClasses = train.getnClasses();
			cubos=new ArrayList<Cubo>(train.getnData());
			salidas=new ArrayList<String>(train.getnData());
			reglas=new ArrayList<Regla>();
			
			crearCubos(train);
			crearSalidas(train);
			
			int clasesOrdenadas[]=ordenaClases(nClasses);
			
			//Muestra los cubos por la salida estandar
			/*Cubo c;
			Parte p;
			String ex[];
			for(int i=0; i<cubos.size();i++){
				c=cubos.get(i);
				ex=train.getExampleString(i);
				for(int j=0;j<train.getnInputs();j++){
					p=c.getParte(j);
					int nValoresAtt=train.numberValues(j);
					for(int k=0; k<nValoresAtt;k++){
						if(p.getBit(k)) System.out.print("1");
						else System.out.print("0");
					}
					System.out.print(" "+ex[j]);
					System.out.print("\t");
				}
				System.out.print("\n");
			}*/
			
			Randomize.setSeed(System.currentTimeMillis());
			//ejecuto el algoritmo nClases-1 veces
			for(int i=0;i<train.getNumOutputValue()-1;i++){
				crearReglas(train.getOutputValue(clasesOrdenadas[i]));
			}
			//meto la ultima clase al final(de esta no hay reglas)
			//si un ejemplo no se cubre por ninguna regla, se clasifica como de la ultima clase
			Regla r=new Regla(null, train.getOutputValue(clasesOrdenadas[train.getNumOutputValue()-1]), null, 0);
			reglas.add(r);
			

			//Finally we should fill the training and test output files
			doOutput(this.val, this.outputTr);
			numCasosTra=numCasos;
			bienClasificadosTra=bienClasificados;
			doOutput(this.test, this.outputTst);
			numCasosTst=numCasos;
			bienClasificadosTst=bienClasificados;
			ficheroEstadisticas(fichEstadisticas);

			System.out.println("Algorithm Finished");
		}
	}
	
	int[] ordenaClases(int nClases){
		int numEjemplos[]=new int[nClases];
		int clasesOrdenadas[]=new int[nClases];
		String clase;
		
		for(int i=0;i<salidas.size();i++){
			for(int j=0;j<nClases;j++){
				clase=salidas.get(i);
				if(clase.equals(train.getOutputValue(j))) numEjemplos[j]++;
			}
		}
		int min, posMin;
		for(int i=0;i<nClases;i++){
			min=Integer.MAX_VALUE;
			posMin=-1;
			for(int j=0;j<nClases;j++){
				if(numEjemplos[j]<min){
					min=numEjemplos[j];
					posMin=j;
				}
			}
			clasesOrdenadas[i]=posMin;
			numEjemplos[posMin]=Integer.MAX_VALUE;
		}
		return clasesOrdenadas;
	}
	
	
	void crearCubos(myDataset ds){
		Cubo c;
		
		for(int i=0; i<ds.getnData();i++){
			c=new Cubo(ds.getExampleString(i), ds);
			cubos.add(c);
		}
		if(cubos.size()>0){
			nPartes=cubos.get(0).getnPartes();
			nBits=new int[nPartes];
			for(int i=0;i<nPartes;i++){
				nBits[i]=cubos.get(0).getParte(i).getLength();
			}
		}
	}
	
	void crearSalidas(myDataset ds){
		String s;
		
		for(int i=0; i<ds.getnData();i++){
			s=new String(ds.getOutputAsString(i));
			salidas.add(s);
		}
	}
	
	void crearReglas(String claseActual){
		int ntry=0, tamF;
		
		quitaConflictivos();
		F=new ArrayList<Cubo>();
		G=new ArrayList<Cubo>();
		FF=new ArrayList<Cubo>();
		creaFyG(claseActual);
		singleDistanceMerge(F);
		singleDistanceMerge(G);
		RExpand();
		while(ntry<maxIter){
			tamF=F.size();
			Try();
			if(F.size()<tamF) ntry=0;
			else{
				tamF=F.size();
				TryR();
				if(F.size()<tamF) ntry=0;
				else ntry++;
			}
		}
		RepresentarReglas(claseActual);
	}
	
	void Try(){
		RReduce();
		RExpandM();
		RReduceR();
		RExpand();
	}
	
	void TryR(){
		RReduceR();
		RExpandMR();
		RReduceR();
		RExpandR();
	}
	
	void RepresentarReglas(String claseActual){
		Regla r;
		String regla;
		int antecedentes;
		boolean primerAtt, primerValorAtt;
		
		for(int i=0; i<F.size();i++){
			antecedentes=0;
			regla="Si ";
			primerAtt=true;
			for(int j=0;j<nPartes;j++){
				if(!F.get(i).getParte(j).todoUnos()){
					antecedentes++;
					if(primerAtt){
						regla+=train.nombreAtributo(j)+"={";
						primerAtt=false;
					}
					else regla+="Y "+train.nombreAtributo(j)+"={";
					primerValorAtt=true;
					for(int k=0;k<nBits[j];k++){
						if(F.get(i).getParte(j).getBit(k)){
							if(primerValorAtt){
								regla+=train.valorAtributo(j, k);
								primerValorAtt=false;
							}
							else regla+=", "+train.valorAtributo(j, k);
						}
					}
					regla+="} ";
				}
			}
			regla+="ENTONCES "+train.getOutputName()+"="+claseActual+"\n";
			r=new Regla(F.get(i), claseActual, regla, antecedentes);
			reglas.add(r);
		}
	}
	
	void RReduce(){
		ArrayList<Cubo> FF1=new ArrayList<Cubo>();
		ArrayList<Cubo> FF2=new ArrayList<Cubo>();
		int cubosOrdenados[];
		Cubo f, f1, Rdef;
		
		for(int i=0; i<FF.size();i++){
			FF1.add(new Cubo(FF.get(i)));
		}
		cubosOrdenados=ordenarCubos();
		int tamF=F.size();
		for(int i=0;i<tamF;i++){
			f=F.get(cubosOrdenados[i]);
			for(int j=0; j<FF1.size();j++){
				FF2.add(new Cubo(FF1.get(j)));
			}
			for(int j=i+1;j<F.size();j++){
				f1=F.get(cubosOrdenados[j]);
				FF2=borraCubiertos(f1, FF2);		
			}
			if(FF2.size()==0){
				int cuboBorrado=cubosOrdenados[i];
				F.remove(cuboBorrado);
				for(int j=0;j<cubosOrdenados.length;j++){
					if(cubosOrdenados[j]>cuboBorrado) cubosOrdenados[j]--;
				}
			}
			else{
				Rdef=inicializaRdef(FF2);
				F.remove(cubosOrdenados[i]);
				F.add(cubosOrdenados[i], Rdef);
				FF1=actualizaFF1(FF1, Rdef);
			}
			FF2.clear();
		}
	}
	
	void RReduceR(){
		ArrayList<Cubo> FF1=new ArrayList<Cubo>();
		ArrayList<Cubo> FF2=new ArrayList<Cubo>();
		int cubosOrdenados[];
		Cubo f, f1, Rdef;
		
		for(int i=0; i<FF.size();i++){
			FF1.add(new Cubo(FF.get(i)));
		}
		cubosOrdenados=ordenarCubosR();
		int tamF=F.size();
		for(int i=0;i<tamF;i++){
			f=F.get(cubosOrdenados[i]);
			for(int j=0; j<FF1.size();j++){
				FF2.add(new Cubo(FF1.get(j)));
			}
			for(int j=i+1;j<F.size();j++){
				f1=F.get(cubosOrdenados[j]);
				FF2=borraCubiertos(f1, FF2);		
			}
			if(FF2.size()==0){
				int cuboBorrado=cubosOrdenados[i];
				F.remove(cuboBorrado);
				for(int j=0;j<cubosOrdenados.length;j++){
					if(cubosOrdenados[j]>cuboBorrado) cubosOrdenados[j]--;
				}
			}
			else{
				Rdef=inicializaRdef(FF2);
				F.remove(cubosOrdenados[i]);
				F.add(cubosOrdenados[i], Rdef);
				FF1=actualizaFF1(FF1, Rdef);
			}
			FF2.clear();
		}
	}
	
	ArrayList<Cubo> actualizaFF1(ArrayList<Cubo> FF1, Cubo Rdef){
		for(int i=0;i<FF1.size();i++){
			if(Rdef.cubre(FF1.get(i))){
				FF1.remove(i);
				i--;
			}
		}
		return FF1;
	}
	
	Cubo inicializaRdef(ArrayList<Cubo> FF2){
		Cubo Rdef=new Cubo(nPartes, nBits);
		
		for(int i=0; i<FF2.size(); i++){
			Rdef.mezcla(FF2.get(i));
		}
		return Rdef;
	}
	
	
	ArrayList<Cubo> borraCubiertos(Cubo f1, ArrayList<Cubo> FF2){
		for(int i=0; i<FF2.size();i++){
			if(f1.cubre(FF2.get(i))){
				FF2.remove(i);
				i--;
			}
		}
		return FF2;
	}
	
	int[] ordenarCubos(){
		int cubosOrdenados[]=new int[F.size()];
		int max, posMax;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setUnosCoincidentes(0);
			for(int j=0;j<F.size();j++){
				if(F.get(i)!=F.get(j)){
					F.get(i).setUnosCoincidentes(F.get(i).getUnosCoincidentes()+F.get(i).unosCoincidentes(F.get(j)));
				}
			}	
		}
		for(int i=0;i<F.size();i++){
			max=-1;
			posMax=-1;
			for(int j=0;j<F.size();j++){
				if(F.get(j).getUnosCoincidentes()>max){
					max=F.get(j).getUnosCoincidentes();
					posMax=j;
				}
			}
			cubosOrdenados[i]=posMax;
			F.get(posMax).setUnosCoincidentes(-2);
		}
		return cubosOrdenados;
	}
	
	int[] ordenarCubosR(){
		int cubosOrdenados[]=new int[F.size()], aleatorio, aux;
		
		for(int i=0;i<F.size();i++){
			cubosOrdenados[i]=i;
		}
		for(int i=0;i<F.size();i++){
			aleatorio=Randomize.Randint(0, F.size()-1);
			aux=cubosOrdenados[i];
			cubosOrdenados[i]=cubosOrdenados[aleatorio];
			cubosOrdenados[aleatorio]=aux;
		}
		return cubosOrdenados;
	}
	
	void RExpand(){
		
		int cubosSinExpandir=F.size();
		Cubo f, Cdef, Edef;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setExpandido(false);
		}
		
		while(cubosSinExpandir>0){
			f=F.get(siguienteCubo());
			Cdef=SPEdesdefOriginal(f);
			Edef=SPE(f, ordenarPartes(Cdef, f));
			reducirCubosDeF(Edef);
			Edef=reducirEdef(Edef);
			addEdef(Edef);
			cubosSinExpandir=0;
			for(int i=0;i<F.size();i++){
				if(!F.get(i).expandido()) cubosSinExpandir++;
			}
		}	
	}
	
	void RExpandR(){
		
		int cubosSinExpandir=F.size();
		Cubo f, Edef;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setExpandido(false);
		}
		
		while(cubosSinExpandir>0){
			f=F.get(siguienteCuboR());
			Edef=SPE(f, ordenarPartesR());
			reducirCubosDeF(Edef);
			Edef=reducirEdef(Edef);
			addEdef(Edef);
			cubosSinExpandir=0;
			for(int i=0;i<F.size();i++){
				if(!F.get(i).expandido()) cubosSinExpandir++;
			}
		}
	}
	
	void RExpandM(){
		
		int cubosSinExpandir=F.size(), sc;
		Cubo f, Cdef, Edef;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setExpandido(false);
		}
		
		while(cubosSinExpandir>0){
			sc=siguienteCubo();
			f=F.get(sc);
			Cdef=SPEdesdefOriginal(f);
			Edef=SPE(f, ordenarPartes(Cdef, f));
			addEdefM(Edef, sc);
			cubosSinExpandir=0;
			for(int i=0;i<F.size();i++){
				if(!F.get(i).expandido()) cubosSinExpandir++;
			}
		}	
	}
	
	void RExpandMR(){
		
		int cubosSinExpandir=F.size(), sc;
		Cubo f, Edef;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setExpandido(false);
		}
		
		while(cubosSinExpandir>0){
			sc=siguienteCuboR();
			f=F.get(sc);
			Edef=SPE(f, ordenarPartesR());
			addEdefM(Edef, sc);
			cubosSinExpandir=0;
			for(int i=0;i<F.size();i++){
				if(!F.get(i).expandido()) cubosSinExpandir++;
			}
		}
	}

	void addEdef(Cubo Edef){
		Edef.setExpandido(true);
		F.add(Edef);
	}
	
	void addEdefM(Cubo Edef, int pos){
		Edef.setExpandido(true);
		F.remove(pos);
		F.add(pos, Edef);
	}
	
	Cubo reducirEdef(Cubo Edef){
		Cubo sff=new Cubo(nPartes, nBits);
		boolean cubiertoPorF;
		
		for(int i=0;i<FF.size();i++){
			if(Edef.cubre(FF.get(i))){
				cubiertoPorF=false;
				for(int j=0;j<F.size();j++){
					if(F.get(j).cubre(FF.get(i))) cubiertoPorF=true;
				}
				if(!cubiertoPorF) sff.mezcla(FF.get(i));
			}
		}
		return sff;
	}
	
	void reducirCubosDeF(Cubo Edef){
		int nPartesCub, parteNoCub=-1;
		Cubo EdefComp=new Cubo(Edef);
		EdefComp.complemento();
		
		for(int i=0;i<F.size();i++){
			nPartesCub=0;
			for(int j=0;j<nPartes;j++){
				if(Edef.getParte(j).cubre(F.get(i).getParte(j))) nPartesCub++;
				else parteNoCub=j;
			}
			if(nPartesCub==nPartes){
				F.remove(i);
				i--;
			}
			else if(nPartesCub==nPartes-1){
				F.get(i).getParte(parteNoCub).AND(EdefComp.getParte(parteNoCub));
			}
		}
	}
	
	Cubo SPE(Cubo f, int[] partesOrdenadas){
		boolean interseccionNulaConP, interseccionNulaResto;
		ArrayList<Cubo> subG=new ArrayList<Cubo>();
		Cubo Edef=new Cubo(f);
		int p;
		
		for(int k=0; k<nPartes;k++){
			p=partesOrdenadas[k];
			for(int i=0;i<G.size();i++){
				interseccionNulaConP=false;
				interseccionNulaResto=true;
				for(int j=0;j<nPartes;j++){
					if(Edef.getParte(j).interseccionNula(G.get(i).getParte(j))){
						if(j==p) interseccionNulaConP=true;
					}
					else if(j!=p){
						interseccionNulaResto=false;
					}
				}
				if(interseccionNulaConP && !interseccionNulaResto){
					subG.add(G.get(i));
				}
			}
			Parte pNueva=new Parte(nBits[p]);
			for(int i=0;i<subG.size();i++){
				pNueva.OR(subG.get(i).getParte(p));
			}
			pNueva.complemento();
			Edef.setParte(pNueva, p);
			subG.clear();
		}
		return Edef;
	}
	
	int[] ordenarPartes(Cubo Cdef, Cubo f){
		int noCubiertosPorpdef[]=new int[nPartes];
		
		for(int i=0;i<F.size();i++){
			if(Cdef.cubre(F.get(i))){
				for(int j=0;j<nPartes;j++){
					if(!f.getParte(j).cubre(F.get(i).getParte(j))){
						noCubiertosPorpdef[j]++;
					}
				}
			}
		}
		//ordenar
		int partesOrdenadas[]=new int[nPartes];
		int max, posMax;
		for(int i=0;i<nPartes;i++){
			max=-1;
			posMax=-1;
			for(int j=0;j<nPartes;j++){
				if(noCubiertosPorpdef[j]>max){
					max=noCubiertosPorpdef[j];
					posMax=j;
				}
			}
			partesOrdenadas[i]=posMax;
			noCubiertosPorpdef[posMax]=-2;
		}
		return partesOrdenadas;
	}
	
	int[] ordenarPartesR(){
		int partesOrdenadas[]=new int[nPartes];
		int aleatorio, aux;
		
		for(int i=0;i<nPartes;i++){
			partesOrdenadas[i]=i;
		}
		for(int i=0;i<nPartes;i++){
			aleatorio=Randomize.Randint(0, nPartes-1);
			aux=partesOrdenadas[i];
			partesOrdenadas[i]=partesOrdenadas[aleatorio];
			partesOrdenadas[aleatorio]=aux;
		}
		return partesOrdenadas;
	}
	
	Cubo SPEdesdefOriginal(Cubo f){
		boolean interseccionNulaConP, interseccionNulaResto;
		ArrayList<Cubo> subG=new ArrayList<Cubo>();
		Cubo Cdef=new Cubo(nPartes, nBits);
		
		for(int p=0; p<f.getnPartes();p++){
			for(int i=0;i<G.size();i++){
				interseccionNulaConP=false;
				interseccionNulaResto=true;
				for(int j=0;j<f.getnPartes();j++){
					if(f.getParte(j).interseccionNula(G.get(i).getParte(j))){
						if(j==p)interseccionNulaConP=true;
					}
					else if(j!=p){
						interseccionNulaResto=false;
					}
				}
				if(interseccionNulaConP && !interseccionNulaResto){
					subG.add(G.get(i));
				}
			}
			Parte pNueva=new Parte(nBits[p]);
			for(int i=0;i<subG.size();i++){
				pNueva.OR(subG.get(i).getParte(p));
			}
			pNueva.complemento();
			Cdef.getParte(p).OR(pNueva);//meto la parte nueva en C(f)
			subG.clear();
		}
		return Cdef;
	}
	
	int siguienteCubo(){
		int max=-1, posMax=-1;
		
		for(int i=0;i<F.size();i++){
			F.get(i).setUnosCoincidentes(0);
			if(!F.get(i).expandido()){
				for(int j=0;j<F.size();j++){
					if(!F.get(j).expandido() && F.get(i)!=F.get(j)){
						F.get(i).setUnosCoincidentes(F.get(i).getUnosCoincidentes()+F.get(i).unosCoincidentes(F.get(j)));
					}
				}
				if(F.get(i).getUnosCoincidentes()>max){
					max=F.get(i).getUnosCoincidentes();
					posMax=i;
				}
			}
		}
		return posMax;
	}
	
	int siguienteCuboR(){
		int noExpandidos=0;
		int aleatorio;
		int posAleatorio=-1;
		for(int i=0;i<F.size();i++){
			if(!F.get(i).expandido()) noExpandidos++;
		}
		
		aleatorio=Randomize.Randint(1, noExpandidos);
		int i=0;
		while(posAleatorio==-1){
			if(!F.get(i).expandido()){
				aleatorio--;
				if(aleatorio==0) posAleatorio=i;
			}
			i++;
		}
		
		return posAleatorio;
	}
	
	void quitaConflictivos(){
		Cubo c1, c2;
		boolean conflictivo;
		
		for(int i=0;i<cubos.size()-1;i++){
			conflictivo=false;
			c1=cubos.get(i);
			for(int j=i+1;j<cubos.size();j++){
				c2=cubos.get(j);
				//si c1 y c2 son iguales y pertenecen a clases distintas...
				if(!conflictivo && c1.esIgual(c2) && 
						!salidas.get(i).equals(salidas.get(j))){
					conflictivo=true;
					cubos.remove(j);
					salidas.remove(j);
					j--;
				}
				else if(c1.esIgual(c2)){
					cubos.remove(j);
					salidas.remove(j);
					j--;
				}
			}
			if(conflictivo){
				cubos.remove(i);
				salidas.remove(i);
				i--;
			}
		}
	}
	
	
	void creaFyG(String claseActual){
		for(int i=0;i<cubos.size();i++){
			if(salidas.get(i).equals(claseActual)){
				F.add(new Cubo(cubos.get(i)));
				FF.add(new Cubo(cubos.get(i)));
			}
			else{
				G.add(new Cubo(cubos.get(i)));
			}
		}
	}
	
	void singleDistanceMerge(ArrayList<Cubo> C){
		Cubo c1, c2;
		boolean mezcla;
		int partesDiferentes;
		
		for(int i=0;i<C.size()-1;i++){
			mezcla=false;
			c1=C.get(i);
			for(int j=i+1;j<C.size();j++){
				c2=C.get(j);
				if(!mezcla){
					partesDiferentes=0;
					for(int k=0;k<c1.getnPartes();k++){
						if(!c1.getParte(k).esIgual(c2.getParte(k))) partesDiferentes++;
					}
					if(partesDiferentes<=1){
						mezcla=true;
						c1.mezcla(c2);
						C.remove(j);
						j--;
					}
				}
				else if(c1.esIgual(c2)){
					C.remove(j);
					j--;
				}
			}
		}
	}
	

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   */
  private void doOutput(myDataset dataset, String filename) {
      String output = new String(""), clase1, clase2;      
      output = dataset.copyHeader(); //we insert the header in the output file
      //We write the output for each example
      numCasos=0;
      bienClasificados=0;
      for (int i = 0; i < dataset.getnData(); i++) {
          //for classification:
      	numCasos++;
      	clase1=dataset.getOutputAsString(i);
      	clase2=this.classificationOutput(dataset.getExampleString(i), dataset);
        output += clase1 + " " +
                  clase2 + "\n";
        if(clase1.equals(clase2)) bienClasificados++;
        
		//for regression:
		//output += dataset.getOutputAsReal(i) + " " +(double)this.regressionOutput(dataset.getExample(i)) + "\n";
                  
      }
      Fichero.escribeFichero(filename, output);
  }
  void ficheroEstadisticas(String filename){
  	String output = new String("");
  	
  	for(int i=0; i<reglas.size()-1;i++){
  		output+=reglas.get(i).getRegla();
  	}
  	output+="\n\nNumero de reglas: "+(reglas.size()-1);
  	double tamMedioCubos=0;
  	double numMedioAntecedentes=0;
  	for(int i=0; i<reglas.size()-1;i++){
  		tamMedioCubos+=reglas.get(i).getCubo().calcularTam();
  		numMedioAntecedentes+=reglas.get(i).getAntecedentes();
  	}
  	tamMedioCubos/=(reglas.size()-1);
  	output+="\nTamaño medio de los cubos: "+tamMedioCubos;
  	numMedioAntecedentes/=(reglas.size()-1);
  	output+="\nNumero medio de antecedentes: "+numMedioAntecedentes;
  	output+="\nNumero medio de casos bien clasificados en tra: "+bienClasificadosTra;
  	output+="\nNumero medio de casos bien clasificados en tst: "+bienClasificadosTst;
  	output+="\nNumero medio de casos mal clasificados en tra: "+(numCasosTra-bienClasificadosTra);
  	output+="\nNumero medio de casos mal clasificados en tst: "+(numCasosTst-bienClasificadosTst);
  	output+="\nRatio de error en tra: "+((numCasosTra-bienClasificadosTra)/(float)numCasosTra);
  	output+="\nRatio de error en tst: "+((numCasosTst-bienClasificadosTst)/(float)numCasosTst);
  	
  	Fichero.escribeFichero(filename, output);
  }

  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(String[] example, myDataset ds) {
      String output = new String("?");
      Cubo c=new Cubo(example, ds);
      boolean clasificado=false;
      
      for(int i=0; i<reglas.size() && !clasificado;i++){
      	if(reglas.get(i).getCubo()!=null){
      		if(reglas.get(i).getCubo().cubre(c)){
      			clasificado=true;
      			output=new String(reglas.get(i).getValorSalida());
      		}
      	}else output=new String(reglas.get(i).getValorSalida());
      }
      
      return output;
  }

  /**
   * It returns the algorithm regression output given an input example
   * @param example double[] The input example
   * @return double the output generated by the algorithm
   */
  private double regressionOutput(double[] example) {
    double output = 0.0;
      /**
        Here we should include the algorithm directives to generate the
        regression output from the input example
       */
      return output;
  } 

}