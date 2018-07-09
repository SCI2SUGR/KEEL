package keel.Algorithms.Rule_Learning.REX1.Reglas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import keel.Algorithms.Rule_Learning.REX1.Datos.Atributo;
import keel.Algorithms.Rule_Learning.REX1.Datos.Fila;
import keel.Algorithms.Rule_Learning.REX1.Utilidades.Pair;

public class Regla {
	Map<Atributo, String> antecedente;
	Pair<Atributo, String> consecuente;
	
	public Regla(Atributo atributoEntrada, String valorEntrada, Atributo atributoSalida, String valorSalida){
		antecedente = new HashMap<Atributo, String>();
		antecedente.put(atributoEntrada, valorEntrada);
		
		consecuente = new Pair<Atributo, String>(atributoSalida, valorSalida);
	}
	
	public Regla(List<Pair<Atributo, String>> antecedente, Pair<Atributo, String> consecuente) {
		
		Pair<Atributo, String> elemento;
		
		this.antecedente = new HashMap<Atributo, String>();
		
		for(Iterator<Pair<Atributo, String>> it = antecedente.iterator(); it.hasNext();){
			elemento =  it.next();
			this.antecedente.put(elemento.first, elemento.second);
		}
		
		this.consecuente = consecuente;
	}

	public Regla(List<Pair<Atributo, String>> antecedente) {
		this(antecedente, null);
	}

	/*
	 * La siguiente funcion permite evaluar si la regla cubre una fila, es decir si se verifica el antecedente para esa regla
	 * No es necesario comprobar el consecuente
	 */
	public boolean cubre(Fila fila){
		
		Entry<Atributo, String> elemento;
		String valorActual;
		
		for(Iterator<Entry<Atributo, String>> it = antecedente.entrySet().iterator(); it.hasNext();){
			elemento = it.next();
			valorActual = fila.obtenerValor(elemento.getKey());
			if(valorActual==null || !valorActual.contentEquals(elemento.getValue())) return false;
		}
		
		return true;
	}
	
	public Pair<Atributo, String> evaluar(Fila fila){
		if(this.cubre(fila)) return consecuente;
		else return null;
	}
	
	@Override
	public String toString() {
		Iterator<Entry<Atributo, String>> i = antecedente.entrySet().iterator();
		Entry<Atributo, String> elemento;
		String toRet=null;
		
		if(i.hasNext()){
			elemento = i.next();
			toRet = new String("IF "+elemento.getKey().getNombreAtributo()+"="+elemento.getValue()+" ");
		
			while(i.hasNext()){
				elemento = i.next();
				toRet = toRet.concat("AND "+elemento.getKey().getNombreAtributo()+"="+elemento.getValue()+" ");
			}
			
			toRet = toRet+" THEN "+consecuente.first.getNombreAtributo()+"="+consecuente.second;
			
		}
		
		return toRet;
	}

	public void setConsecuente(Pair<Atributo, String> nuevoConsecuente) {
		this.consecuente = nuevoConsecuente;
	}
	
	
}
