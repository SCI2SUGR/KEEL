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

package keel.Algorithms.Rule_Learning.UnoR;
/**
 * <p>Tï¿½ulo: Intervalos de los datos numericos</p>
 * <p>Descripciï¿½: Estructura para almacenar un intervalo</p>
 * @author Rosa Venzala
 * @version 1.0
 */
import org.core.*;
public class Interval /*implements Comparable*/ {

    double low; //el valor mas bajo del intervalo
    double high; //el valor mas alto del intervalo
    int claseOptima; //la clase asociada al intervalo
    int numValues;//el numero de valores del intervalo
    double[]valores;
    int []clases;//la clase optima de cada uno de los valores
    int SMALL;

    /**
     *  Constructor
     */
    public Interval(int small,int tamanio){
    	numValues=0;
	this.SMALL=small;
	valores=new double[tamanio];
	clases=new int [tamanio];
    }
    
    /*AÃ±ade un nuevo valor al intervalo
    */
    public void add(double dato){
    	if(numValues==0)low=dato;
	valores[numValues]=dato;
	numValues++;
	high=dato;
    }
    
    /*Asigna la clase a la que perteneceran los valores del intervalo
    */
    public void setClass(int c,int indice){
	clases[indice]=c;
    }

    public int getNumValues(){
    	return numValues;
    }
    public int getOptimalClass(){
    	return claseOptima;
    }
    public double[]getValues(){
    	return valores;
    }
    
    public void clear(){
    	numValues=0;
    }
    
    /*Devuelve la clase optima del intervalo en ese momento
    *Si no hay aun una clase optima para mas de SMALL valores devuelve -1
    * excepto si se trata del ultimo intervalo, que devuelve simplemente la clase
    * que mas aparece, aunque no sea para mas de SMALL
    */
    public int optimalClass(int numClases,long seed,boolean ultimoIntervalo){
    	int cuantas=0;
	int []cuantasCadaClase=new int[numClases];
    	for(int i=0;i<numClases;i++){
		cuantas=0;
		for(int j=0;j<numValues;j++)
			if(clases[j]==i)cuantas++;
	cuantasCadaClase[i]=cuantas;
	}
	int indice=getMaximo(cuantasCadaClase,seed,numClases);
	if((cuantasCadaClase[indice]<=SMALL )&& (!ultimoIntervalo))indice=-1;
	claseOptima=indice;
	return indice;
    }
    /*
    * Devuelve el indice donde se encuentra el maximo de un array de enteros
    * @return int el indice donde se encuentra el maximo valor
    */
    //ESTO HABRIA QUE HACERLO ALEATORIO
    private int getMaximo(int []num, long seed,int nclases){
    	Randomize.setSeed(seed);
    	int max=num[0];int indice=0;
	int []opciones=new int[nclases];int contador=0;
    	for(int i=1;i<num.length;i++){
		if(num[i]>max){max=num[i];indice=i;contador=0;opciones[contador]=i;}
		else{if(num[i]==max)contador++;opciones[contador]=i;}
	}
	if(contador>0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
		indice=Randomize.RandintClosed(1, contador);
		indice=opciones[indice];
	}
	return indice;
    }
    
    public void print(){
    	System.out.print("[");
    	for(int i=0;i<numValues;i++)
		System.out.print(valores[i]+" ");
	System.out.println("]");
    }
    
   
}

