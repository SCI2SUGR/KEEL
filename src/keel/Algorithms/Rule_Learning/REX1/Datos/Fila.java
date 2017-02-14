package keel.Algorithms.Rule_Learning.REX1.Datos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import keel.Algorithms.Rule_Learning.REX1.Utilidades.Pair;

public class Fila implements Iterable<Entry<Atributo, String>> {
	Map<Atributo, String> valores;
	boolean marcada;

	public Fila(List<Pair<Atributo, String>> valores) {
		this.valores = new HashMap<Atributo, String>();
		
		for(Iterator<Pair<Atributo, String>> i = valores.iterator(); i.hasNext();){
			Pair<Atributo, String> elemento = i.next();
			this.valores.put(elemento.first, elemento.second);
		}
		
		this.marcada = false;
	}
	
	public Fila(List<Atributo> atributosOrdenados, String[] valoresOrdenados) {
		valores = new HashMap<>();
		
		for(int i=0; i<atributosOrdenados.size(); i++){
			valores.put(atributosOrdenados.get(i), valoresOrdenados[i].trim());
		}
		
	}
	
	public String obtenerValor(Atributo atributo){
		String valor = valores.get(atributo);
		if(valor != null) return valor;
		
		/*
		 * El siguiente código es necesario para los casos en los que se busque un atributo, que en realidad está contenido en el mapa "valores", pero que ha sido creado por un dataset distinto al de entrenamiento.
		 * Se cogen todos los valores almacenados en el mapa y se busca el atributo cuyo nombre y tipo coinciden con el que se especifica por parámetro
		 */
		
		Entry<Atributo,String> elemento;
		
		for(Iterator<Entry<Atributo,String>> it = valores.entrySet().iterator(); valor==null && it.hasNext();){
			elemento = it.next();
			if(elemento.getKey().getNombreAtributo().contentEquals(atributo.getNombreAtributo()) && elemento.getKey().getTipo()==atributo.getTipo()) valor = elemento.getValue();
		}
		
		return valor;
	}
	
	public void marcar(){
		marcada = true;
	}
	
	public boolean isMarcada(){
		return marcada;
	}
	
	@Override
	public Iterator<Entry<Atributo, String>> iterator() {
		return valores.entrySet().iterator();
	}
	
	/*
	 * ¡ATENCIÓN!
	 * La siguiente función fué programada un viernes a las 1:00 de la madrugada, tras 6 horas de programación y depuración. No me hago responsable de las bajas que pueda ocasionar intentar comprender el código. Debido a esto se detalla su funcionamiento:
	 * 	· A esta función se le pasan el tamaño de las combinaciones y una lista de atributos ordenados por entropía reformulada.
	 *  · Devuelve una lista de combinaciones (una combinación es una lista de pares atributo-valor)
	 *  · Tenemos un array de índices, llamado "referencias" que indicará las posiciones de los atributos (en la lista de atributos ordenados) que formarán la combinación que habrá que añadir a la lista de salida. Es decir, movemos los índices, y luego insertamos los atributos que indiquen dichos índices para crear la combinación. El proceso se repite para crear todas las combinaciones.
	 *  · Supongamos un array de 3 atributos: atributos={A,B,C} y queremos crear combinaciones de (tamannoGrupos=2) atributos
	 *  1. Inicializamos el array de referencias={0,1} (esto lo hace el primer bucle for)
	 *  2. Esta es la primera combinacion: {atributos[referencias[0]], atributos[referencias[1]]} = {atributos[0],[atributos[1]} = {A,B}	(esto lo hace el trozo de código entre los comentarios "Aqui insertamos en la lista"
	 *  3. Mientras no hayamos llegado al final (llegamos al final cuando referencias[0] llega a su posición final)
	 *    4. Si el último elemento de referencias ha llegado al último atributo (en nuestro caso, referencias[1] sea igual a 2 (porque C es el atributo último atributo y tiene índice 2)
	 *      5. Entonces, buscamos hacia atrás el primer elemento que no ha llegado a su posición final
	 *      6. Movemos ese elemento una posición hacia adelante
	 *      7. A partir de este elemento que hemos movido, los que se encuentran en adelante en el array referencias se establecen consecutivos a este
	 *    	8. Creamos una nueva combinación
	 *    
	 *    	9. Si no, movemos el último elemento de referencias una posición hacia adelante
	 *    	10. Creamos una nueva combinación
	 */
	
	public List<List<Pair<Atributo, String>>> creaCombinaciones(int tamannoGrupos, List<Pair<Atributo,Double>> atributos) {
		int [] referencias = new int[tamannoGrupos];
		int j,k;
		Atributo atributoInsertar;
		List<List<Pair<Atributo, String>>> toRet = new ArrayList<List<Pair<Atributo, String>>>();
		List<Pair<Atributo, String>> listaInsertar;
		
		for(int i=0; i<tamannoGrupos; i++) referencias[i]=i;
		
		// Aqui insertamos en la lista
		listaInsertar = new ArrayList<Pair<Atributo,String>>();
		for(k=0;k<tamannoGrupos;k++){
			atributoInsertar = atributos.get(referencias[k]).first;
			listaInsertar.add(new Pair<Atributo, String>(atributoInsertar, valores.get(atributoInsertar)));
		}
		toRet.add(listaInsertar);
		/////////////
		
		while(referencias[0]!=atributos.size()-tamannoGrupos){
			
			if(referencias[tamannoGrupos-1]==atributos.size()-1){
				for(j=tamannoGrupos-2;referencias[j+1]-referencias[j]==1;j--);
				referencias[j]++;
				for(j=j+1;j<tamannoGrupos;j++){referencias[j] = referencias[j-1]+1;}
				//Aqui insertamos en la lista
				listaInsertar = new ArrayList<Pair<Atributo,String>>();
				for(k=0;k<tamannoGrupos;k++){
					atributoInsertar = atributos.get(referencias[k]).first;
					listaInsertar.add(new Pair<Atributo, String>(atributoInsertar, valores.get(atributoInsertar)));
				}
				toRet.add(listaInsertar);
				/////////////
			} else {
			
				referencias[tamannoGrupos-1]++;
			
				// Aqui insertamos en la lista
				listaInsertar = new ArrayList<Pair<Atributo,String>>();
				for(k=0;k<tamannoGrupos;k++){
					atributoInsertar = atributos.get(referencias[k]).first;
					listaInsertar.add(new Pair<Atributo, String>(atributoInsertar, valores.get(atributoInsertar)));
				}
				toRet.add(listaInsertar);
				/////////////
			
			}
		}

		
		return toRet;
	}
}
