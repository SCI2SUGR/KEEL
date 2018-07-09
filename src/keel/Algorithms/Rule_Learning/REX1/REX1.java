package keel.Algorithms.Rule_Learning.REX1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import keel.Algorithms.Rule_Learning.REX1.Datos.Atributo;
import keel.Algorithms.Rule_Learning.REX1.Datos.CondicionAtributoIgualAValor;
import keel.Algorithms.Rule_Learning.REX1.Datos.CondicionCubreRegla;
import keel.Algorithms.Rule_Learning.REX1.Datos.CondicionFilaNoMarcada;
import keel.Algorithms.Rule_Learning.REX1.Datos.Dataset;
import keel.Algorithms.Rule_Learning.REX1.Datos.Fila;
import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionAtributosNoNominales;
import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionValoresPerdidos;
import keel.Algorithms.Rule_Learning.REX1.FormulasEntropia.EntropiaInterface;
import keel.Algorithms.Rule_Learning.REX1.FormulasEntropia.FormulasCalculoEntropia;
import keel.Algorithms.Rule_Learning.REX1.Reglas.Regla;
import keel.Algorithms.Rule_Learning.REX1.Utilidades.ComparadorPairAtributoDouble;
import keel.Algorithms.Rule_Learning.REX1.Utilidades.Pair;

public class REX1 {

	/*
	 * Ficheros de salida de datos
	 */
	private PrintStream ficheroSalidaEntrenamiento;
	private PrintStream ficheroSalidaTest;
	private PrintStream ficheroSalidaEjecucion;

	/*
	 * Los dataSets de entrada
	 */
	private Dataset datasetEntrenamiento;
	private Dataset datasetTest;
	private Dataset datasetValidacion;

	/*
	 * Algoritmo de cálculo de la entropía seleccionado
	 */
	private final EntropiaInterface entropiaSeleccionada;

	/*
	 * Base de reglas calculada
	 */
	private List<Regla> baseReglas;
	
	/*
	 * Indica si estamos en k-fold Cross Validation, si la ejecución actual es la última, y el número de ejecuciones que hay que hacer
	 */
	private boolean ultimaEjecucion;
	private int numeroEjecuciones;
	
	/*
	 * Carpeta de salida. Nos servirá para calcular al final las estadísticas
	 */
	private File[] ficherosSalida;
	
	/*
	 * Constructor: Lee el fichero de configuración y abre los archivos necesarios. Tambien llama a las funciones para leer los ficheros de entrada
	 */
	public REX1(String rutaFicheroConfiguracion) throws ExcepcionAtributosNoNominales, ExcepcionValoresPerdidos, IOException {
		String lineaFicheroConfiguracionLeida;
		Integer codigoEntropia;
		int indiceInicioCadena=0, indiceFinCadena;
		File archivoConfiguracion = new File(rutaFicheroConfiguracion);
		Scanner fichero = new Scanner(archivoConfiguracion);
		File ficheroAbriendo;

		// Nos saltamos el nombre del algoritmo
		fichero.nextLine();

		// Leemos la línea de ficheros de entrada
		lineaFicheroConfiguracionLeida = fichero.nextLine();
		int numFicheros=0;
		for(int i=0; i<lineaFicheroConfiguracionLeida.length(); i++){
			if(lineaFicheroConfiguracionLeida.charAt(i)=='"'){numFicheros++;}
		}
		numFicheros = numFicheros/2;

		// Delimitamos el substring de la ruta del archivo de entrenamiento, y abrimos dicho fichero
		indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"")+1;
		indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
		System.out.println("DEBUG | Fichero de entrenamiento de entrada: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		datasetEntrenamiento = new Dataset(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));

		if(numFicheros >= 3){
			indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceFinCadena+1)+1;
			indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
			System.out.println("DEBUG | Fichero de validacion de entrada: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
			datasetValidacion = new Dataset(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		} else {
			datasetValidacion = null;
		}

		if(numFicheros>1){
			indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceFinCadena+1)+1;
			indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
			System.out.println("DEBUG | Fichero de test de entrada: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
			datasetTest = new Dataset(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		} else {
			System.out.println("AVISO | No se ha especificado fichero de test de entrada");
			datasetTest = null;
		}

		// Leemos la línea de ficheros de salida
		lineaFicheroConfiguracionLeida = fichero.nextLine();
		// En este caso, ambos archivos existen siempre, por lo que no comprobamos los índices
		indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"")+1;
		indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
		System.out.println("DEBUG | Fichero de entrenamiento de salida: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo = new File(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo.createNewFile();
		ficheroSalidaEntrenamiento = new PrintStream(ficheroAbriendo);
		
		indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceFinCadena+1)+1;
		indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
		System.out.println("DEBUG | Fichero de test de salida: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo = new File(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo.createNewFile();
		ficheroSalidaTest = new PrintStream(ficheroAbriendo);

		indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceFinCadena+1)+1;
		indiceFinCadena = lineaFicheroConfiguracionLeida.indexOf("\"", indiceInicioCadena);
		System.out.println("DEBUG | Fichero de salida de datos de la ejecucion: "+lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo = new File(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena, indiceFinCadena));
		ficheroAbriendo.createNewFile();
		ficheroSalidaEjecucion = new PrintStream(ficheroAbriendo);		

		// Saltamos la línea en blanco
		lineaFicheroConfiguracionLeida = fichero.nextLine();
		
		// Leemos el código de la entropía
		lineaFicheroConfiguracionLeida = fichero.nextLine();

		indiceInicioCadena = lineaFicheroConfiguracionLeida.indexOf('=');
		codigoEntropia = Integer.parseInt(lineaFicheroConfiguracionLeida.substring(indiceInicioCadena+1).trim());

		// Y obtenemos el algoritmo de cálculo de entropía asociado a ese código
		entropiaSeleccionada = FormulasCalculoEntropia.obtenerAlgoritmo(codigoEntropia);

		if(entropiaSeleccionada != null){
			System.out.println("DEBUG | Entropía seleccionada: "+entropiaSeleccionada.getCodigo());
		} else {
			System.out.println("ERROR | Código de entropía desconocido");
		}
		
		numeroEjecuciones = archivoConfiguracion.getParentFile().listFiles().length;
		int numeroEjecucionActual = Integer.parseInt(rutaFicheroConfiguracion.substring(rutaFicheroConfiguracion.length()-5,rutaFicheroConfiguracion.length()-4))+1;
		
		ultimaEjecucion = numeroEjecuciones==numeroEjecucionActual?true:false;
		
		/*
		 * Este bloque nos abre los ficheros de salida de las distintas ejecuciones del algoritmo para calcular las estadísticas finales
		 */
		if(ultimaEjecucion == true){
			ficherosSalida = ficheroAbriendo.getParentFile().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.substring(arg1.lastIndexOf('.')).contentEquals(".txt");
				}
			});
			
		}
		
		fichero.close();
		
		baseReglas = new ArrayList<Regla>();
	}

	public void ejecutar() throws FileNotFoundException {
		this.entrena();
		if(datasetTest != null) this.comprueba(datasetTest, ficheroSalidaTest, true);
		if(datasetValidacion != null) this.comprueba(datasetValidacion, ficheroSalidaEntrenamiento, false);
		
		if(ultimaEjecucion) this.calculaEstadisticas();
	}

	private void calculaEstadisticas() throws FileNotFoundException {
		
		int sumaNumeroReglas = 0, sumaCasosBienClasificadosTest = 0, sumaCasosMalClasificadosTest = 0, sumaCasosBienClasificadosEntrenamiento = 0, sumaCasosMalClasificadosEntrenamiento = 0, sumaCasosTest = 0, sumaCasosEntrenamiento = 0;
		Scanner fLeer;
		String lineaLeida;
		
		for(File f:ficherosSalida){
			fLeer = new Scanner(f);
			
			while (fLeer.hasNextLine()){
				lineaLeida = fLeer.nextLine();
				
				if(lineaLeida.startsWith("@NumberOfRules ")){
					sumaNumeroReglas += Integer.parseInt(lineaLeida.substring("@NumberOfRules ".length()));
				} else if(lineaLeida.startsWith("@NumberOfCorrectlyClassifiedTest ")){
					sumaCasosBienClasificadosTest += Integer.parseInt(lineaLeida.substring("@NumberOfCorrectlyClassifiedTest ".length()));
				} else if(lineaLeida.startsWith("@NumberOfInCorrectlyClassifiedTest ")){
					sumaCasosMalClasificadosTest += Integer.parseInt(lineaLeida.substring("@NumberOfIncorrectlyClassifiedTest ".length()));
				} else if(lineaLeida.startsWith("@NumberOfCorrectlyClassifiedTraining ")){
					sumaCasosBienClasificadosEntrenamiento += Integer.parseInt(lineaLeida.substring("@NumberOfCorrectlyClassifiedTraining ".length()));
				} else if(lineaLeida.startsWith("@NumberOfInCorrectlyClassifiedTraining ")){
					sumaCasosMalClasificadosEntrenamiento += Integer.parseInt(lineaLeida.substring("@NumberOfIncorrectlyClassifiedTraining ".length()));
				} else if(lineaLeida.startsWith("@NumberOfItemsetsTest ")){
					sumaCasosTest += Integer.parseInt(lineaLeida.substring("@NumberOfItemsetsTest ".length()));
				} else if(lineaLeida.startsWith("@NumberOfItemsetsTraining ")){
					sumaCasosEntrenamiento += Integer.parseInt(lineaLeida.substring("@NumberOfItemsetsTraining ".length()));
				}
			}
		}
		
		PrintStream salida = new PrintStream(new File(ficherosSalida[0].getAbsolutePath().substring(0, ficherosSalida[0].getAbsolutePath().indexOf(ficherosSalida[0].getName()))+"RESULTADOS_FINALES.txt"));
		salida.println("Nombre del dataset: " + datasetEntrenamiento.getNombreDataset());
		salida.println("Número de atributos del dataset: " + datasetEntrenamiento.getNumeroAtributos());
		salida.println("Número medio de reglas obtenidas: "+(sumaNumeroReglas/(float)numeroEjecuciones));
		salida.println("Número medio de casos bien clasificados en test: "+(sumaCasosBienClasificadosTest/(float)numeroEjecuciones));
		salida.println("Número medio de casos mal clasificados en test: "+(sumaCasosMalClasificadosTest/(float)numeroEjecuciones));
		salida.println("Número medio de casos bien clasificados en entrenamiento: "+(sumaCasosBienClasificadosEntrenamiento/(float)numeroEjecuciones));
		salida.println("Número medio de casos mal clasificados en entrenamiento: "+(sumaCasosMalClasificadosEntrenamiento/(float)numeroEjecuciones));
		salida.println("Ratio de error en test: "+(sumaCasosMalClasificadosTest/(float)sumaCasosTest));
		salida.println("Ratio de error en entrenamiento: "+(sumaCasosMalClasificadosEntrenamiento/(float)sumaCasosEntrenamiento));
		salida.close();
		
	}

	public void entrena(){
		List<Atributo> atributos = datasetEntrenamiento.getAtributosOrdenados();
		Atributo atributoIterando;
		String valorIterando, valorAtributoSalidaSiEntropiaCero = null;
		Map<Atributo, List<Pair<String,String>>> valoresConEntropiaCero = new HashMap<Atributo, List<Pair<String,String>>>();
		List<Pair<Atributo, String>> listaCondicion = null;
		List<Pair<Atributo, Double>> entropiasReformuladas = new ArrayList<Pair<Atributo, Double>>();
		List<Float> listaProbabilidades = new ArrayList<Float>();
		int numeroInstancias = datasetEntrenamiento.getNumeroInstancias(), contador;
		Double entropiaVariableActual;
		double entropiaValorActual;
		
		ficheroSalidaEjecucion.println("## ENTROPÍAS ##");
		
		/*
		 * PASO 1 y 2: Calculamos la entropía reformulada asociada a cada variable
		 */
		/*
		 * Este bucle itera para cada atributo del dataset.
		 * Al final del bucle se obtiene la entropía para el atributo que se está iterando
		 */
		for(Iterator<Atributo> it = atributos.iterator(); it.hasNext();){
			atributoIterando = it.next();
			if(atributoIterando.getTipo()==Atributo.TipoAtributo.ENTRADA){
			entropiaVariableActual = 0.0;
			
			/*
			 * Este bucle itera para cada valor que toma el "atributoIterando", y calcula su entropía
			 */
			for(Iterator<String> jt = atributoIterando.getValores().iterator(); jt.hasNext();){
				listaProbabilidades.clear();
				valorIterando = jt.next();
				
				/*
				 * El siguiente bucle itera para cada valor del atributo de salida (el atributo de clase)
				 * Esto se hace para calcular las probabilidades de encontrar un valor determinado en un atributo.
				 * Estas probabilidades son las que se usarán para calcular la entropía del valor del atributo.
				 */
				for(Iterator<String> kt = datasetEntrenamiento.getAtributoSalida().getValores().iterator(); kt.hasNext();){
					contador = 0;
					listaCondicion = new ArrayList<Pair<Atributo, String>>();
					listaCondicion.add(new Pair<Atributo, String>(atributoIterando, valorIterando));
					listaCondicion.add(new Pair<Atributo, String>(datasetEntrenamiento.getAtributoSalida(), kt.next()));
					
					/*
					 * Ahora podemos contar las filas que tienen el "valorIterando" en el "atributoIterando" Y el valor "kt.next()" en el atributo de salida
					 */
					for(Iterator<Fila> lt = datasetEntrenamiento.iterator(new CondicionAtributoIgualAValor(listaCondicion)); lt.hasNext();lt.next()){contador++;}
					
					/*
					 * Si hemos encontrado algún caso en el que se cumpla lo anterior, calculamos la probabilidad de ese valor y lo añadimos a "listaProbabilidades"
					 * La probabilidad del valor se calcula como el número de veces que aparece para el valor actual del atributo de salida, divido entre el número de veces que aparece ese valor
					 * Es decir, si hay, por ejemplo, 5 filas con valor "Lluvioso", 2 de las cuales clasifican a "Invierno", entonces esta probabilidad valdrá 2/5.
					 */
					if(contador>0){
						/*
						 * La siguiente línea de código aparentemente no tiene sentido. Guardamos el valor del atributo de salida para que, si el valorIterando resulta tener entropía 0, no tener que buscar
						 * cual es el valor al que clasifica. Esto puede hacerse porque si un valor, por ejemplo "Nevado", que se repite 4 veces, y clasifica siempre al valor "Invierno", va a entrar en este bloque if
						 * 4 veces, estableciendo la variable siempre al valor "Invierno". Si no se clasifica siempre el valor del atributo que estamos considerando, esta variable no tendrá sentido, pero su entropía no será 0,
						 * por tanto esta variable no se tendrá en cuenta.
						 */
						valorAtributoSalidaSiEntropiaCero = listaCondicion.get(1).second;
						listaProbabilidades.add(((float)contador)/atributoIterando.getRepeticionesValor(valorIterando));
					}
				}
				
				/*
				 * Usamos la lista de probabilidades calculadas anteriormente para obtener la entropía de ese valor.
				 * A su vez, se va calculando también la entropía de la variable actual, y se almacena en la variable "entropiaVariableActual"
				 */
				
				entropiaValorActual = entropiaSeleccionada.calculaEntropia(listaProbabilidades);
				ficheroSalidaEjecucion.println("E("+atributoIterando.getNombreAtributo()+","+valorIterando+")= "+entropiaValorActual);
				if(entropiaValorActual != 0.0){
					entropiaVariableActual = entropiaVariableActual + entropiaValorActual * atributoIterando.getRepeticionesValor(valorIterando) / numeroInstancias;
				} else {
					/*
					 * Si la entropía del valor actual vale 0, entonces hay que guardar dicho valor y el atributo al que pertenece, porque posteriormente, en el paso 4, generarán una regla directamente.
					 * El siguiente código es muy poco intuitivo, para intentar ser un poco más eficiente. Se hace lo siguiente:
					 *  · Los valores con entropía cero se guardan en un mapa, que relaciona cada atributo con una lista de valores. Iterando la lista de atributos ordenados por su entropía reformulada, podemos obtener de este mapa la lista de valores cuya entropía vale 0
					 *  · Para el primer valor con entropía 0 que calculemos, el resultado de hacer un get en el mapa va a resultar en null, porque para ese atributo aún no se ha creado el objeto lista.
					 *  · Si ya hemos encontrado otros valores con entroía 0, la lista ya estará creada, no será null, por lo que solamente hay que insertar el valor.
					 *  · Si la lista es null, entonces la creamos, la añadimos al mapa y después insertamos el valor. Esto se puede hacer en ese orden porque el mapa guarda una referencia a la lista, no una copia, por ello, aunque se inserte primero la lista y después se añada el elemento, dicho elemento se guardará en el mapa.
					 */
					List<Pair<String,String>> listaValoresEntropia = valoresConEntropiaCero.get(atributoIterando);
					if(listaValoresEntropia == null){
						listaValoresEntropia = new ArrayList<Pair<String,String>>();
						valoresConEntropiaCero.put(atributoIterando, listaValoresEntropia);	
					}
					listaValoresEntropia.add(new Pair<String, String>(valorIterando, valorAtributoSalidaSiEntropiaCero));
				}
			}
			
			/*
			 * En este punto tenemos calculada la entropía de la variable "atributoIterando". Ahora podemos calcular su entropía reformulada y
			 * asociarla a dicha variable con un mapa
			 */
			entropiasReformuladas.add(new Pair<Atributo, Double>(atributoIterando, entropiaVariableActual*atributoIterando.getNumeroValoresDistintos()));
			ficheroSalidaEjecucion.println("E(" + atributoIterando.getNombreAtributo() + ")= " + entropiaVariableActual);
			ficheroSalidaEjecucion.println("ER("+atributoIterando.getNombreAtributo()+")= "+entropiaVariableActual*atributoIterando.getNumeroValoresDistintos());
			ficheroSalidaEjecucion.println();
			}
		}
		
		/*
		 * PASO 3: Ordenamos los atributos por su entropía reformulada
		 */
		Collections.sort(entropiasReformuladas, new ComparadorPairAtributoDouble());
		
		
		/*
		 * PASO 4: Generamos reglas con los valores de los atributos cuya entropía es 0.
		 */
		Atributo atributoAntecedente;
		List<Pair<String,String>> listaValoresAntecedenteConsecuente;
		Pair<String,String> valoresAntecedenteConsecuente;
		Regla nuevaRegla;
		
		for(Iterator<Pair<Atributo, Double>> it = entropiasReformuladas.iterator(); it.hasNext();){
			atributoAntecedente = it.next().first;
			listaValoresAntecedenteConsecuente = valoresConEntropiaCero.get(atributoAntecedente);
			if(listaValoresAntecedenteConsecuente != null){
				for(Iterator<Pair<String,String>> jt = listaValoresAntecedenteConsecuente.iterator(); jt.hasNext();){
					valoresAntecedenteConsecuente = jt.next();
					nuevaRegla = new Regla(atributoAntecedente, valoresAntecedenteConsecuente.first, datasetEntrenamiento.getAtributoSalida(), valoresAntecedenteConsecuente.second);
					baseReglas.add(nuevaRegla);
					System.out.println("Encontrada nueva regla: "+nuevaRegla);
					datasetEntrenamiento.marcaFilas(new CondicionCubreRegla(nuevaRegla));
				}
			}
		}
				
		/*
		 * Ésta es la n tal y como aparece en el algoritmo
		 */
		int n = 2;
		
		/*
		 * PASO 8: Comprobamos si todos los ejemplos están clasificados.
		 * ¿Porque narices salta del 5 al 8 así? ¿No es más natural que este fuera el paso 5 y el 8 fuera: Haz 9 y 10 y ve al 5?
		 */
		Fila ejemploNoClasificado;
		List<List<Pair<Atributo, String>>> combinaciones;
		Regla reglaCandidata;
		boolean reglaCandidataValida, clasificaALaMismaClase;
		String valorAlQueClasifica;
		
		/*
		 * A partir de aquí, se implementan los pasos donde se consiguen las reglas que clasifican los ejemplos que quedan por clasificar
		 */
		while(n<entropiasReformuladas.size()){
			
			for(Iterator<Fila> it = datasetEntrenamiento.iterator(new CondicionFilaNoMarcada()); it.hasNext();){
				/*
				 * Cogemos el primer ejemplo no marcado
				 */
				ejemploNoClasificado = it.next();
			
				/*
				 * Creamos las combinaciones de n valores.
				 */				
				combinaciones = ejemploNoClasificado.creaCombinaciones(n, entropiasReformuladas);
				
				/*
				 * Primero, iteramos por orden para cada combinación creada anteriormente
				 */
				reglaCandidataValida = false;
				for(Iterator<List<Pair<Atributo, String>>> jt = combinaciones.iterator(); jt.hasNext() && !reglaCandidataValida;){
					/*
					 * Creamos una regla candidata creada a partir de la combinación que estamos iterando 
					 */
					reglaCandidata = new Regla(jt.next());
					valorAlQueClasifica=null;
					clasificaALaMismaClase = true;

					/*
					 * Ahora comprobamos que, para todas las reglas que cubre nuestra reglaCandidata, siempre clasifican al mismo valor
					 */
					for(Iterator<Fila> kt = datasetEntrenamiento.iterator(new CondicionCubreRegla(reglaCandidata)); kt.hasNext() && clasificaALaMismaClase;){
						if(valorAlQueClasifica == null){
							valorAlQueClasifica = kt.next().obtenerValor(datasetEntrenamiento.getAtributoSalida());
						} else if(!valorAlQueClasifica.contentEquals(kt.next().obtenerValor(datasetEntrenamiento.getAtributoSalida()))){
							clasificaALaMismaClase=false;
						}
					}

					/*
					 * Si lo hacen, entonces la reglaCandidata será válida. Pero ahora mismo la regla no tiene definido el consecuente, porque para comprobar los ejemplos que cubre la regla sólo es necesario el antecedente, asíque le configuramos el consecuente.
					 * Hacerlo así también facilita el comprobar el valor al que clasifica la reglaCandidata
					 */
					if(clasificaALaMismaClase){
						reglaCandidataValida = true;
						reglaCandidata.setConsecuente(new Pair<Atributo, String>(datasetEntrenamiento.getAtributoSalida(), valorAlQueClasifica));
						baseReglas.add(reglaCandidata);
						datasetEntrenamiento.marcaFilas(new CondicionCubreRegla(reglaCandidata));
					}
				}
			}
			n++;
		}
		
		/*
		 * Volcamos al fichero de datos las reglas que hemos creado
		 */
		ficheroSalidaEjecucion.println();
		ficheroSalidaEjecucion.println();
		ficheroSalidaEjecucion.println("## REGLAS ##");
		for(Iterator<Regla> it = baseReglas.iterator(); it.hasNext();){
			ficheroSalidaEjecucion.println(it.next());
			
		}
		
		ficheroSalidaEjecucion.println();
		ficheroSalidaEjecucion.println("@NumberOfRules " + baseReglas.size());
	}

	public void comprueba(Dataset datasetTrabajo, PrintStream ficheroSalida, boolean esDatasetTest){
		
		Fila filaEvaluando;
		Pair<Atributo,String> clase;
		boolean clasificado;
		int numeroCasos = 0, numeroCasosCorrectamenteClasificados = 0, numeroCasosMalClasificados = 0, numeroCasosNoClasificados = 0;
		String valorClasifica;
		
		datasetTrabajo.escribeCabecera(ficheroSalida);
		
		for(Iterator<Fila> it = datasetTrabajo.iterator(); it.hasNext();){
			filaEvaluando = it.next();
			clasificado = false;
			for(Iterator<Regla> jt = baseReglas.iterator(); jt.hasNext() && !clasificado;){
				if((clase = jt.next().evaluar(filaEvaluando))!=null){
					clasificado = true;
					valorClasifica = filaEvaluando.obtenerValor(datasetTrabajo.getAtributoSalida());
					ficheroSalida.println(valorClasifica+" "+clase.second);
					if(valorClasifica.contentEquals(clase.second)){
						numeroCasosCorrectamenteClasificados++;
					} else {
						numeroCasosMalClasificados++;
					}
				}
			}
			if(!clasificado){
				numeroCasosNoClasificados++;
				ficheroSalida.println(filaEvaluando.obtenerValor(datasetTrabajo.getAtributoSalida())+" ?"); // Estos ficheros de salida han de ser procesados con Vis-Clas-Check, por lo que necesitamos indicar con "?" que no lo hemos clasificado.
			}
			
			numeroCasos++;
		}
		
		ficheroSalidaEjecucion.println("@NumberOfItemsets"+(esDatasetTest?"Test":"Training")+" "+numeroCasos);
		ficheroSalidaEjecucion.println("@NumberOfCorrectlyClassified"+(esDatasetTest?"Test":"Training")+" "+numeroCasosCorrectamenteClasificados);
		ficheroSalidaEjecucion.println("@NumberOfItemsNotClassified"+(esDatasetTest?"Test":"Training")+" "+numeroCasosNoClasificados);
		ficheroSalidaEjecucion.println("@NumberOfInCorrectlyClassified"+(esDatasetTest?"Test":"Training")+" "+numeroCasosMalClasificados);
		
		
	}

}
