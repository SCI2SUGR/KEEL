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

public class Cubo{
	private Parte partes[];
	private boolean expandido;
	private int unosCoincidentes;
	
	Cubo(String ex[], myDataset ds){
		int nbits, nInputs=ds.getnInputs(), posValorAtt;
		
		partes=new Parte[nInputs];
		for(int i=0;i<nInputs;i++){
			nbits=ds.numberValues(i);
			partes[i]=new Parte(nbits);
			posValorAtt=ds.posValorAtt(ex[i], i);
			for(int j=0;j<nbits;j++){
				if(posValorAtt==j) partes[i].setBit(j, true);
				else partes[i].setBit(j, false);
			}
		}
		expandido=false;
		unosCoincidentes=0;
	}
	
	Cubo(int nPartes, int nBits[]){
		partes=new Parte[nPartes];
		
		for(int i=0;i<partes.length;i++){
			partes[i]=new Parte(nBits[i]);
		}
		expandido=false;
		unosCoincidentes=0;
	}
	
	Cubo(Cubo c){
		this.partes=new Parte[c.partes.length];
		for(int i=0;i<partes.length;i++){
			this.partes[i]=new Parte(c.partes[i]);		
		}
		this.expandido=c.expandido;
		this.unosCoincidentes=c.unosCoincidentes;
	}
	
	boolean expandido(){return expandido;}
	void setExpandido(boolean e){expandido=e;}
	
	int getUnosCoincidentes(){return unosCoincidentes;}
	void setUnosCoincidentes(int n){unosCoincidentes=n;}
	
	int calcularTam(){
		int tam=1;
		for(int i=0;i<partes.length;i++){
			tam*=partes[i].numUnos();
		}
		return tam;
	}
	
	Parte getParte(int pos){
		return partes[pos];
	}
	
	void setParte(Parte p, int pos){
		partes[pos]=p;
	}
	
	int getnPartes(){
		return partes.length;
	}
	

	public boolean esIgual(Cubo c){
		boolean iguales=true;
		
		for(int i=0;i<partes.length;i++){
			for(int j=0;j<partes[i].getLength();j++){
				if(partes[i].getBit(j)!=c.partes[i].getBit(j)) iguales=false;
			}
		}
		return iguales;
	}
	
	void mezcla(Cubo c){
		for(int i=0;i<partes.length;i++){
			partes[i].OR(c.partes[i]);
		}
	}
	
	int unosCoincidentes(Cubo c){
		int unos=0;
		
		for(int i=0;i<getnPartes();i++){
			for(int j=0;j<partes[i].getLength();j++){
				if(partes[i].getBit(j) && c.partes[i].getBit(j)){
					unos++;
				}
			}
		}
		return unos;
	}
	
	boolean cubre(Cubo c){
		boolean cubierto=true;
		
		for(int i=0; i<partes.length;i++){
			if(!partes[i].cubre(c.partes[i])) cubierto=false;
		}
		return cubierto;
	}
	
	void complemento(){
		for(int i=0; i<partes.length;i++){
			partes[i].complemento();
		}
	}
	
}
