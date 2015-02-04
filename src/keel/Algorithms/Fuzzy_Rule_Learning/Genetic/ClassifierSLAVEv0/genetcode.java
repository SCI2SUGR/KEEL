
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVEv0;
import java.io.*;


public class genetcode {
	
	/**	
	 * <p>
	 * It contains the methods for handling the genetic information of the individuals
	 * </p>
	 */
	
	int binary;
	int[] nbinary;
	char[][] mbinary;
	int integer;
	int[] ninteger;
	int[][] minteger;
	int real;
	int[] nreal;
	double[][] mreal;
	
	
	/**
	 * <p>
	 * Default Constructor
	 * </p>
	 */
	
	genetcode (){
		binary = integer = real = 0;
		nbinary = ninteger = nreal = null;
		mbinary = null;
		minteger = null;
		mreal = null;
	}
	
	genetcode (genetcode x){
		this.binary = x.binary;
		this.nbinary = new int [binary];
		for (int i=0; i<binary;i++)
			nbinary[i] = x.nbinary[i];
		this.mbinary = new char [binary][];
		
		for (int i=0; i<binary; i++){
			mbinary[i] = new char [nbinary[i]];
			for (int j=0; j<nbinary[i]; j++)
				mbinary[i][j] = x.mbinary[i][j];
		}
			
		this.integer = x.integer;
		this.ninteger = new int [integer];
		for (int i=0; i<integer;i++)
			ninteger[i] = x.ninteger[i];
		
		this.minteger = new int [integer][];
		for (int i=0; i<integer; i++){
			minteger[i] = new int [ninteger[i]];
			for (int j=0; j<ninteger[i]; j++)
				minteger[i][j] = x.minteger[i][j];
		}
		
		this.real = x.real;
		this.nbinary = new int [real];
		for (int i=0; i<real;i++)
			nreal[i] = x.nreal[i];
		
		this.mreal = new double [real][];
		for (int i=0; i<real; i++){
			mreal[i] = new double [nreal[i]];
			for (int j=0; j<nreal[i]; j++)
				mreal[i][j] = x.mreal[i][j];
		}
	}
	
	public void PutBinary (int bin, int[] nbin, char[][] mbin){
		binary = bin;
		nbinary = new int [binary];
		for (int i=0; i<binary; i++)
		    nbinary[i] = nbin[i];
		
		mbinary = new char [binary][];
		for (int i=0; i<binary; i++){
		    mbinary[i] = new char [nbinary[i]];
		    for (int j=0; j<nbinary[i]; j++)
		      mbinary[i][j] = mbin[i][j];
		  }
	}
	
	public void PutInteger (int ent, int[] nent, int[][] ment){
		integer = ent;
		ninteger = new int [integer];
		for (int i=0; i<integer; i++)
		    ninteger[i] = nent[i];
		
		minteger = new int [integer][];
		for (int i=0; i<integer; i++){
		    minteger[i] = new int [ninteger[i]];
		    for (int j=0; j<ninteger[i]; j++)
		      minteger[i][j] = ment[i][j];
		  }
	}
	
	public void PutReal (int rea, int[] nrea, double[][] mrea){
		real = rea;
		nreal = new int [real];
		for (int i=0; i<real; i++)
		    nreal[i] = nrea[i];
		  
		mreal = new double [real][];
		for (int i=0; i<real; i++){
		    mreal[i] = new double [nreal[i]];
		    for (int j=0; j<nreal[i]; j++)
		      mreal[i][j] = mrea[i][j];
		  }
	}
	
	public void PutValueBinary (int fila, int columna, char value){
		mbinary[fila][columna] = value;
	}
	
	public void PutValueInteger (int fila, int columna, int value){
		minteger[fila][columna] = value;
	}
	
	public void PutValueReal (int fila, int columna, double value){
		mreal[fila][columna] = value;
	}

	public int SizeBinary (int fila){
		return nbinary[fila];
	}
	
	public int SizeInteger (int fila){
		return ninteger[fila];
	}
	
	public int SizeReal (int fila){
		return nreal[fila];
	}
	
	public int GetBinary1 (){
		return binary;
	}
	
	public int[] GetBinary2 (){
		return nbinary;
	}
	
	public char[][] GetBinary3 (){
		return mbinary;
	}
	
	public int GetInteger1 (){
		return integer;
	}
	
	public int[] GetInteger2 (){
		return ninteger;
	}
	
	public int[][] GetInteger3 (){
		return minteger;
	}
	
	public int GetReal1 (){
		return real;
	}
	
	public int[] GetReal2 (){
		return nreal;
	}
	
	public double[][] GetReal3 (){
		return mreal;
	}
	
	public char GetValueBinary (int fila, int columna){
		return mbinary[fila][columna];
	}
	
	public int GetValueInteger (int fila, int columna){
		return minteger[fila][columna];
	}
	
	public double GetValueReal (int fila, int columna){
		return mreal[fila][columna];
	}
	

	
}
