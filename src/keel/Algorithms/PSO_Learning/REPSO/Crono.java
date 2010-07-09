package keel.Algorithms.PSO_Learning.REPSO;

/**
 * <p>Title: Crono</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Crono{ 

	private Calendar inicio;
    private Calendar fin;
    private Calendar diferencia;



    //*********************************************************************
    //***************** Constructor ***************************************
    //*********************************************************************

    public Crono(){
    }


    //*********************************************************************
    //***************** Initialization and stop ***************************
    //*********************************************************************

    public void inicializa() { 
        inicio = new GregorianCalendar();
     }

    
    public void fin(){

        fin = new GregorianCalendar();
        long diff = fin.getTimeInMillis() - inicio.getTimeInMillis();
        diferencia = new GregorianCalendar();
        diferencia.setTimeInMillis(diff);
    }


    //*********************************************************************
    //***************** Print total time **********************************
    //*********************************************************************

   public String tiempoTotal(){
	   
	   String tpo="";
       tpo=	diferencia.get(Calendar.MINUTE) + " min. " +
       		diferencia.get(Calendar.SECOND) + " seg. " + diferencia.get(Calendar.MILLISECOND) + " miliseg.";  
       return tpo;
   }

}