package keel.Algorithms.Rule_Learning.REX1.Datos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionAtributosNoNominales;
import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionValoresPerdidos;

public class Dataset implements Iterable<Fila> {

	Atributo atributoSalida;
	String nombreDataset;
	List<Fila> datos;
	Map<String,Atributo> atributos;
	List<Atributo> atributosOrdenados;
	
	List<String> cabecera;

	/*
	 * La variable "permutacion" permitirá reordenar virtualmente los atributos.
	 * Dicha variable contendrá los índices de los atributos ordenados.
	 * Así, al acceder al atributo i, con el orden inicial: Fila.valor(i)
	 * 								  tras ordenar: Fila.valor(permutacion[i])
	 */
	int[] permutacion;

	public Dataset(String rutaFichero) throws FileNotFoundException, ExcepcionValoresPerdidos, ExcepcionAtributosNoNominales {
		int i, indiceComienzoSubcadena, indiceFinSubcadena;
		boolean leyendoDirectivas = true, entradasSalidasEspecificadas = false;
		String lineaFicheroEntradaLeida;
		Scanner ficheroEntrada;
		datos = new ArrayList<Fila>();
		atributosOrdenados = new ArrayList<Atributo>();
		atributos = new HashMap<String,Atributo>();
		cabecera = new ArrayList<String>();
		
		ficheroEntrada = new Scanner(new File(rutaFichero));

		while(leyendoDirectivas){

			lineaFicheroEntradaLeida = ficheroEntrada.nextLine();
			cabecera.add(lineaFicheroEntradaLeida);
			
			if(lineaFicheroEntradaLeida.startsWith("@data")){
				leyendoDirectivas = false;
			} else if(lineaFicheroEntradaLeida.startsWith("@relation")){
				nombreDataset = lineaFicheroEntradaLeida.substring("@relation".length()+1);
			} else if(lineaFicheroEntradaLeida.startsWith("@attribute")){
				indiceComienzoSubcadena = "@attribute".length()+1;
				indiceFinSubcadena = lineaFicheroEntradaLeida.indexOf('{', indiceComienzoSubcadena);

				Atributo nuevoAtributo = new Atributo(lineaFicheroEntradaLeida.substring(indiceComienzoSubcadena,indiceFinSubcadena).trim());
				
				indiceFinSubcadena = lineaFicheroEntradaLeida.indexOf('}');
				// Si en la línea no encontramos el carácter '}', quiere decir que el atributo no es nominal, por tanto hay que lanzar la excepción correspondiente 
				if(indiceFinSubcadena == -1){
					ficheroEntrada.close();
					throw new ExcepcionAtributosNoNominales();
				}
				indiceComienzoSubcadena = lineaFicheroEntradaLeida.indexOf('{')+1;
				
				String[] valores = lineaFicheroEntradaLeida.substring(indiceComienzoSubcadena, indiceFinSubcadena).split(",");
				
				for(i=0; i<valores.length; i++){
					nuevoAtributo.insertarValor(valores[i].trim());
				}
				
				atributos.put(nuevoAtributo.getNombreAtributo(),nuevoAtributo);
				atributosOrdenados.add(nuevoAtributo);
			} else if(lineaFicheroEntradaLeida.startsWith("@inputs")){
				entradasSalidasEspecificadas = true;
				
				indiceComienzoSubcadena = lineaFicheroEntradaLeida.indexOf(' ');
						
				String[] atributosEntrada = lineaFicheroEntradaLeida.substring(indiceComienzoSubcadena).split(",");
				
				for(i=0; i<atributosEntrada.length; i++){
					atributos.get(atributosEntrada[i].trim()).setTipoEntrada();
				}
			} else if(lineaFicheroEntradaLeida.startsWith("@outputs")){
				indiceComienzoSubcadena = lineaFicheroEntradaLeida.indexOf(' ');
						
				String[] atributosEntrada = lineaFicheroEntradaLeida.substring(indiceComienzoSubcadena).split(",");
				
				for(i=0; i<atributosEntrada.length; i++){
					atributoSalida = atributos.get(atributosEntrada[i].trim());
					atributoSalida.setTipoSalida();
				}
			}
		}
		
		/*
		 * Si no se especifican las directivas "@inputs" y "@outputs", entonces se
		 * consideran todos los atributos como de entrada, excepto el último, que
		 * se considerará de salida
		 */
		if(entradasSalidasEspecificadas == false){
			for(i=0;i<atributosOrdenados.size()-1; i++){
				atributosOrdenados.get(i).setTipoEntrada();
			}
			atributosOrdenados.get(i).setTipoSalida();
		}
		
		/*
		 * Ahora leemos los datos (las instancias) del dataset
		 */
		String[] valoresLeidos;
		String valorLimpio;
		while(ficheroEntrada.hasNextLine()){
			lineaFicheroEntradaLeida = ficheroEntrada.nextLine();
			
			valoresLeidos = lineaFicheroEntradaLeida.split(",");
			
			datos.add(new Fila(atributosOrdenados, valoresLeidos));
			
			for(i=0;i<atributosOrdenados.size();i++){
				valorLimpio = valoresLeidos[i].trim();
				atributosOrdenados.get(i).sumarRepeticionValor(valorLimpio);
				if(valorLimpio == "?" || valorLimpio == "<null>"){
					ficheroEntrada.close();
					throw new ExcepcionValoresPerdidos();
				}
			}
		}
		
		/*
		 * Cerramos el fichero y ponemos la permutación por defecto, la identidad, que
		 * mantiene el orden que se ha especificado en el fichero.
		 */
		ficheroEntrada.close();
		permutacion = new int[atributos.size()];
		for(i=0;i<permutacion.length;i++){
			permutacion[i] = i;
		}
		
		/*
		 * Mostramos por pantalla los atributos leídos
		 */
		System.out.println("## Dataset: " + nombreDataset + " ##");
		System.out.println("Atributos leídos y sus posibles valores:");
		Iterator<Atributo> it = atributosOrdenados.iterator();
		Iterator<String> jt;
		Atributo atributo;
		String valor;
		while(it.hasNext()){
			atributo = it.next();
			System.out.println("  " + atributo.getNombreAtributo() + " (Tipo " + (atributo.getTipo()==Atributo.TipoAtributo.ENTRADA?"entrada)":"salida)"));
			for(jt=atributo.getValores().iterator(); jt.hasNext();){
				valor = jt.next();
				System.out.println("    " + valor + "(aparece en " + atributo.getRepeticionesValor(valor.trim()) + " instancias)");	
			}
			System.out.println();
		}
		System.out.println();
	}

	@Override
	public Iterator<Fila> iterator() {
		return datos.iterator();
	}

	public Iterator<Fila> iterator(CondicionInterface condicion) {
		return new IteradorCondicional(condicion, this);
	}

	public List<Atributo> getAtributosOrdenados() {
		return atributosOrdenados;
	}
	
	public Atributo getAtributoPorOrden(int orden){
		return atributosOrdenados.get(permutacion[orden]);
	}
	
	public int getNumeroInstancias(){
		return datos.size();
	}
	
	public Atributo getAtributoSalida(){
		return atributoSalida;
	}
	
	public boolean estanTodasFilasMarcadas(){
		
		for(Iterator<Fila> it = datos.iterator(); it.hasNext();){
			if(it.next().isMarcada()==false) return false;
		}
		
		return true;
	}
	
	public void marcaFilas(CondicionInterface condicion){
		Fila elemento;
		for(Iterator<Fila> it = datos.iterator(); it.hasNext();){
			elemento = it.next();
			if(condicion.cumpleCondicion(elemento)){
				elemento.marcar();
			}
		}
	}
	
	public String getNombreDataset() {
		return nombreDataset;
	}
	
	public int getNumeroAtributos(){
		return atributos.size();
	}
	
	public void escribeCabecera(PrintStream fichero){
		for(Iterator<String> it = cabecera.iterator(); it.hasNext();){
			fichero.println(it.next());
		}
	}
}
