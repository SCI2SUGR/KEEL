package keel.Algorithms.RE_SL_Postprocess.Post_A_T_Lateral_FRBSs;

import java.io.*;
import java.util.*;
/**
 * The class that reads the file of the rule base
 * @author Diana Arquillos
 *
 */
public class LeerWm {


	private String rutaFichero;
	public int numReglas;
	public double []base;
	public double exit;
	/**
	 * It stores the name of the file
	 * @param ruta it contains the name of the file
	 */
	public LeerWm(String ruta) {

		this.rutaFichero = ruta;

	}

	/**
	 * It reads the file
	 * @param n_variables it contains the number of variables
	 */
	public void leer(int n_variables){


		File fichero = new File(this.rutaFichero);
		String linea = null;
		StringTokenizer tokens = null;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fichero));
			linea = reader.readLine();
			tokens = new StringTokenizer(linea,":");
			tokens.nextElement();
			numReglas = (int)Double.parseDouble(tokens.nextElement().toString());
			base = new double [numReglas*(3*n_variables)]; 			
			int i=0;
			int j=0;
			double aux;
			int contador=0;	
			
			linea = reader.readLine();
		
			while( j<((numReglas*n_variables))){
				linea = reader.readLine();
				tokens = new StringTokenizer(linea,"  ");
				
				aux = Double.parseDouble(tokens.nextElement().toString());
				base[i]=aux;
				i++;
				aux = Double.parseDouble(tokens.nextElement().toString());
				base[i]=aux;
				aux = Double.parseDouble(tokens.nextElement().toString());
				i++;
				base[i]=aux;
				i++;
				j++;
				contador++;
				if(contador==n_variables){
					contador=0;
					linea= reader.readLine();
				}
			}
				/*linea = reader.readLine();
				linea = reader.readLine();
				tokens = new StringTokenizer(linea,":");
				String salida = tokens.nextElement().toString();
				String cadena = "Salida por defecto";
				
				
				if(cadena.compareTo(salida)==0){
					exit = Double.parseDouble(tokens.nextElement().toString());
				}
				else
				  */exit = -1;
				  //exit = 3797.5;
				}
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}
	

}


