package keel.Algorithms.RE_SL_Postprocess.Post_A_T_LatAmp_FRBSs;

/**
 * It contains the functions to convert the data in other type or change
 * @author Diana Arquillos
 *
 */
public class Funciones {

	/**
	 *it changes the string to a long 
	 * @param Cad_ent char vector which is going to change
	 * @param length the size of the vector
	 * @return the long new value
	 */
	long Ctoi(char [] Cad_ent,  int length){

	      int i;
	      //unsigned
		long n;	
		
		n=0;
		for (i=0; i<length; i++)
		  {
		    n <<= 1;
		    char aux = Cad_ent[i];
		    n += (aux - (int) '0');
		  }
		return n;
	}
	
	/**
	 * it changes a long to a vector of char
	 * @param n2 the long number
	 * @param length the new size of vector
	 * @return the new vector of char
	 */
	char [] Itoc(long n2, int length){
		char [] Cad_sal = new char[length];
		int i;		
	  for (i=length-1; i>=0; i--)
	    {
		  Cad_sal[i]=(char)('0' + (n2 & 1));
	      n2 >>= 1;
	    }
	  return Cad_sal;
	}
	/**
	 * it changes the vector of char with the gray's code
	 * @param Cad_ent the vector to change
	 * @param Cad_sal the new changed vector 
	 * @param length the size of the vector
	 * @param pos the last position of the vector
	 */
	void Gray(char [] Cad_ent, char [] Cad_sal, int length,int pos){

	  int i; 
	  char last;
	  int aux;
	  last = '0';
	  for (i=0; i<length; i++)
	    {
			  if(Cad_ent[i]!=last) aux=1;
			  else aux =0;
		  
		  Cad_sal[pos+i]=(char)('0' + aux);
	      last = Cad_ent[i];
	    }
	}

}