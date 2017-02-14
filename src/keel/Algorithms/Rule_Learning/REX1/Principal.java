package keel.Algorithms.Rule_Learning.REX1;

import java.io.FileNotFoundException;
import java.io.IOException;

import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionAtributosNoNominales;
import keel.Algorithms.Rule_Learning.REX1.Excepciones.ExcepcionValoresPerdidos;

public class Principal {

	private static REX1 implementacion;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 1) {
			System.err.println("N�mero de par�metros incorrecto. Terminado.");
		} else {
			try{
				implementacion = new REX1(args[0]);
				implementacion.ejecutar();
			} catch (FileNotFoundException e) {
				// ### IMPLEMENTAR ### No se ha encontrado el fichero
				e.printStackTrace();
			} catch (ExcepcionAtributosNoNominales e) {
				System.err.println("ERROR: El dataset contiene atributos num�ricos. Esta implementaci�n de REX-1 requiere que todos los atributos sean nominales.");
				System.err.println("Por favor, modifique el dataset o especifique otro para trabajar s�lo con atributos nominales.");
				e.printStackTrace();
			} catch (ExcepcionValoresPerdidos e) {
				System.err.println("ERROR: El dataset contiene valores perdidos. Esta implementaci�n de REX-1 requiere que no existan valores perdidos en el dataset.");
				System.err.println("Por favor, modifique el dataset o especifique otro que contenga valores para todo el dataset.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Excepci�n gen�rica de E/S. No tenemos m�s informaci�n, pero algo chungo debe pasar (�te faltan permisos?, �disco duro lleno?, �disco duro fastidiado?...).");
				e.printStackTrace();
			}
		}

	}

}
