package keel.Algorithms.Rule_Learning.REX1.Datos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Esta clase representa un atributo nominal del dataset.
 * Los objetos de esta clase deben ser creados de la siguiente manera:
 *  · Al leer la directiva "@attribute" del fichero que contiene en dataset, obtenemos el nombre del atributo, con lo que podemos crear una instancia.
 *  · A continuación, se leen los posibles valores que puede tomar dicho atributo. Debe llamarse al método "insertarValor" para cada valor leído desde el fichero.
 *  · Tras leer todos los "@attribute", se proceden a leer los "@inputs". Hay que llamar a la función "setTipoEntrada" de los atributos que aparezcan en esta línea.
 *  · Análogamente al anterior, al leer "@outputs", se debe llamar al método "setTipoSalida" de los atributos que aparezcan en esta línea.
 *  · Por último, cuando se están leyendo datos (@data), se debe llamar a "sumarRepeticionValor", para sumar uno al contador de veces que aparece ese valor.
 */
public class Atributo {

	private final String nombreAtributo;

	/*
	 *  "valores" almacenará los posibles distintos valores que puede tomar este atributo, y las veces que se repite ese valor en el dataset.
	 */
	private final Map<String, Integer> valores;

	private TipoAtributo tipo;

	public enum TipoAtributo{
		ENTRADA,
		SALIDA;
	}

	public Atributo(String nombreAtributo) {
		this.nombreAtributo = nombreAtributo;
		this.valores = new HashMap<String, Integer>();
	}

	/*
	 * El siguiente método sirve para inicializar el conjunto de valores que puede tomar el atributo.
	 */
	public void insertarValor(String valor){
		valores.put(valor, 0);
	}

	/*
	 * Este método suma una unidad al contador de repeticiones del valor especificado
	 */
	public void sumarRepeticionValor(String valor){
		valores.put(valor, valores.get(valor)+1);
	}

	/*
	 * Establece el atributo como de tipo entrada
	 */
	public void setTipoEntrada(){
		tipo = TipoAtributo.ENTRADA;
	}

	/*
	 * Establece el atributo como de tipo salida
	 */
	public void setTipoSalida(){
		tipo = TipoAtributo.SALIDA;
	}

	//### GETTERS ###//

	public Set<String> getValores(){
		return valores.keySet();
	}
	
	public TipoAtributo getTipo() {
		return tipo;
	}

	public String getNombreAtributo() {
		return nombreAtributo;
	}

	public int getRepeticionesValor(String valor){
		return valores.get(valor).intValue();
	}
	
	public int getNumeroValoresDistintos(){
		return valores.size();
	}
	
}
