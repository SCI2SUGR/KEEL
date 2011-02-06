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

public class Parte {
	
	private boolean bits[];
		
	Parte(int nbits){
		bits=new boolean[nbits];
		for(int i=0;i<bits.length;i++){
			bits[i]=false;
		}
	}
	
	Parte(Parte p){
		this.bits=new boolean[p.bits.length];
		for(int i=0;i<bits.length;i++){
			this.bits[i]=p.bits[i];
		}
	}
	
	boolean esIgual(Parte p){
		boolean iguales=true;
		
		for(int i=0;i<bits.length;i++){
			if(bits[i]!=p.bits[i]) iguales=false;
		}
		return iguales;
	}
		
	void setBit(int pos, boolean valor){
		bits[pos]=valor;
	}
		
	boolean getBit(int pos){
		return bits[pos];
	}
	
	int getLength(){
		return bits.length;
	}
	
	boolean interseccionNula(Parte p){
		boolean noInterseccion=true;
		
		for(int i=0;i<bits.length;i++){
			if(bits[i] && p.bits[i]) noInterseccion=false;
		}
		return noInterseccion;
	}
	
	void OR(Parte p){
		for(int i=0;i<bits.length;i++){
			if(bits[i] || p.bits[i]) bits[i]=true;
			else bits[i]=false;
		}
	}
	
	void AND(Parte p){
		for(int i=0;i<bits.length;i++){
			if(bits[i] && p.bits[i]) bits[i]=true;
			else bits[i]=false;
		}
	}
	
	void complemento(){		
		for(int i=0;i<bits.length;i++){
			bits[i]=!bits[i];
		}
	}
	
	boolean cubre(Parte p){
		boolean cubierto=true;
		
		for(int i=0; i<bits.length;i++){
			if(!bits[i] && p.bits[i]) cubierto=false;
		}
		return cubierto;
	}
	
	boolean todoUnos(){
		boolean unos=true;
		for(int i=0;i<bits.length;i++){
			if(!bits[i]) unos=false;
		}
		return unos;
	}
	
	int numUnos(){
		int unos=0;
		for(int i=0;i<bits.length;i++){
			if(bits[i]) unos++;
		}
		return unos;
	}
		
}

