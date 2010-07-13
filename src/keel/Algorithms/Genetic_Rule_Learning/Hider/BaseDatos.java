/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/*
 * Created on 12-feb-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.Vector;

//import org.core.Dataset;

/**
 * @author Sebas
 */
public class BaseDatos {
    /**
     * Vector for the data
     */
    private Vector base = null;
    /**
     * Index of the output attribute
     */
    private int clase;
    /**
     * Number of attributes
     */
    private int numAtributos;
    /**
     * Number of elements of the database
     */
    private int numEjemplos;
    /**
     * Ranges for the different attributes
     */
    private Vector rangos;
    /**
     * Values for the type enumerado attributes
     */
    private Vector valEnum;
    /**
     * Attributes' types
     */
    private Vector tipos;
    /**
     * Attributes' names
     */
    private Vector nombres;
    /**
     * Attributes' initial values
     */
    private Vector inicial;
    /**
     * Attributes' final values
     */
    private Vector finales;
    /**
     * Output files header
     */
    private String cabecera = null;

    /**
     * Constructor
     * @param ds
     */
    public BaseDatos(Dataset ds) {
        this.numEjemplos = ds.getNdatos();
        this.tipos = ds.getTipos();

        this.cabecera = obtieneCabecera(ds);

        //rangos, inicial, finales & valEnum
        this.convierte(ds.getRangos());

        this.nombres = ds.getAtributos();
        this.numAtributos = ds.getNvariables(); //ds.getAtributos().size();
        /*try{
            this.clase = this.nombres.indexOf(ds.getSalidas().get(0));
                 }catch(Exception e){
            this.clase = numAtributos-1; //class is the last attribute
                 }*/
        this.clase = ds.getnInputs(); //la clase es siempre el ultimo atributo

        //System.err.println("Mira -> "+clase);

        this.convierteDatos(ds); //Set base Vector
    }

    /**
     * Gets 'rangos','inicial','finales' and 'valEnum' from DataSet attributes
     * @param rangosDs
     */
    private void convierte(Vector rangosDs) {
        Vector result = new Vector();
        Vector ini = new Vector();
        Vector fin = new Vector();
        Vector valE = new Vector();
        for (int i = 0; i < rangosDs.size(); i++) {
            Vector aux = (Vector) rangosDs.get(i);
            if (((String) tipos.get(i)).equals("real")) {
                Double a = (Double) aux.get(0);
                Double b = (Double) aux.get(1);
                double rango = Discretizacion.resta(b.doubleValue(),
                        a.doubleValue());

                result.add(new Double(rango));
                ini.add(a);
                fin.add(b);
                valE.add(null); //If attribute is not an 'enumerado' attribute, then add 'null' into 'valEnum' vector
            } else if (((String) tipos.get(i)).equals("integer")) {
                Integer a = (Integer) aux.get(0);
                Integer b = (Integer) aux.get(1);
                int rango = b.intValue() - a.intValue() + 1;

                result.add(new Integer(rango));
                ini.add(a);
                fin.add(b);
                valE.add(null); //If attribute is not an 'enumerado' attribute, then add 'null' into 'valEnum' vector
            } else if (((String) tipos.get(i)).equals("enumerado")) {
                result.add(new Integer(aux.size()));
                ini.add(new Integer(0));
                fin.add(null);
                valE.add(aux);
            }
        }

        rangos = result;
        inicial = ini;
        finales = fin;
        valEnum = valE;

    }

    /**
     * Adapts data from the 'DataSet' object to de 'BaseDatos' object
     * @param ds Data set
     */
    private void convierteDatos(Dataset ds) {
        boolean seguir;
        base = new Vector();
        Vector ejemplo = null;

        for (int i = 0; i < ds.getNdatos(); i++) {
            ejemplo = new Vector();
            seguir = true;
            for (int j = 0; (j < ds.getNvariables()-1) && seguir; j++) {
                //'DataSet' object stores data in 'String' objects, but 'BaseDatos' does it in 'Double' or 'Integer' objects
                if (!ds.isMissing(i,j)){
                    if (tipos.get(j).equals("real")) {
                        ejemplo.add(new Double(ds.getExample(i)[j]));
                    } else if (tipos.get(j).equals("integer")) {
                        ejemplo.add(new Integer((int)ds.getExample(i)[j]));
                    } else if (tipos.get(j).equals("enumerado")) {
                        Integer dato = new Integer((int)ds.getExample(i)[j]);
                        ejemplo.add(dato);
                    }
                }
                /*if (!ds.getDatosIndex(i, j).equals("?") &&
                    !ds.getDatosIndex(i, j).equals("<null>")) {

                    if (tipos.get(j).equals("real")) {
                        ejemplo.add(new Double(ds.getDatosIndex(i, j)));
                    } else if (tipos.get(j).equals("integer")) {
                        ejemplo.add(new Integer(ds.getDatosIndex(i, j)));
                    } else if (tipos.get(j).equals("enumerado")) {
                        Integer dato = new Integer(((Vector) valEnum.get(j)).
                                indexOf(ds.getDatosIndex(i, j)));
                        ejemplo.add(dato);
                    }
                } */
                else {
                    seguir = false;
                }
            }
            if (seguir) {
                ejemplo.add(new Integer(ds.getOutputAsInteger(i)));
                base.add(ejemplo);
            }
        }
        numEjemplos = base.size();
    }

    /*
     public BaseDatos(String fichero) throws Exception
     {
      base=new Vector();

      if(!fichero.endsWith(".dat"))
      {
       fichero+=".dat";
      }

      try
      {
       FileReader bd = new FileReader(fichero);
       BufferedReader br=new BufferedReader(bd);

       String linea=null;

       nombres=new Vector();

       tipos=new Vector();
       rangos=new Vector();
       inicial=new Vector();
       valEnum=new Vector();
       int rango=0;


       while(!(linea=br.readLine()).equals("@data"))
       {
        if(linea.startsWith("@attribute"))
        {
         nombres.add(linea.split(" ")[1].trim()); //Store attributes' names
         int inicio=0;

         if((inicio=linea.indexOf("{")) >= 0)
         {
          //Discrete attribute
          String cadAux=linea.substring(inicio+1,linea.indexOf("}"));
          String[] valores=cadAux.split(",");

          rango=valores.length;
          Vector aux=new Vector();
          for(int v=0;v < valores.length;v++)
          {
           aux.add(valores[v].trim());
          }
          valEnum.add(aux);
          rangos.add(new Integer(rango));
          tipos.add("enumerado");
          inicial.add(new Integer(0));
         }
         else if(linea.indexOf("real") >= 0)
         {
          int ini=0;
     if((ini=linea.indexOf("[")) >= 0) //It should be always this way but...
          {
           Double[] intervalo = new Double[2];
           String inter=linea.substring(ini+1,linea.indexOf("]"));
           String[] intervAux=inter.split(",");
           intervalo[0]=new Double(intervAux[0].trim());
           intervalo[1]=new Double(intervAux[1].trim());

           rangos.add(new Double(Discretizacion.resta(intervalo[1].doubleValue(),intervalo[0].doubleValue()))); //Los de rango 'Double' son continuos

           inicial.add(intervalo[0]);
           finales.add(intervalo[1]);
          }
          else
          {
              Exception e=new Exception("La base de datos está mal formada");
              throw e;
          }
          valEnum.add(null);
          tipos.add(linea.split(" ")[2].trim()); //Guardo los tipos de los atributos en otro vector
         }
         else if(linea.indexOf("integer") >= 0)
         {
          int ini=0;
          int[] intervalo = new int[2];
     if((ini=linea.indexOf("[")) >= 0) //Esto es siempre así pero por si acaso...
          {
           String inter=linea.substring(ini+1,linea.indexOf("]"));
           String[] intervAux=inter.split(",");
           intervalo[0]=Integer.parseInt(intervAux[0].trim());
           intervalo[1]=Integer.parseInt(intervAux[1].trim());

           rango=intervalo[1]-intervalo[0]+1;
          }
          else
          {
           System.out.println("La base de datos está mal formada");
          }

          valEnum.add(null);
          rangos.add(new Integer(rango));
          tipos.add(linea.split(" ")[2].trim()); //Guardo los tipos de los atributos en otro vector
          inicial.add(new Integer(intervalo[0]));
         }
         else
         {
          System.out.println("La base de datos está mal formada");
         }
        }
        else if(linea.startsWith("@output"))
        {
         clase=nombres.indexOf(linea.split(" ")[1].trim()); //Cojo el índice del atributo clase
        }
       }

       Evolucion.imprime(rangos,"Rangos");
       Evolucion.imprime(tipos,"Tipos");
       Evolucion.imprime(inicial,"Inicial");
       Evolucion.imprime(finales,"Final");
       //int cont=0;
       while((linea=br.readLine()) != null)
       {
        String[] aux=linea.split(",");
        Vector ejemplo=new Vector();
        for(int e=0;e < aux.length;e++)
        {
         if( ((String)tipos.get(e)).equals("real"))
         {
          ejemplo.add(new Double(aux[e].trim()));
         }
         else if( ((String)tipos.get(e)).equals("integer"))
         {
          ejemplo.add(new Integer(aux[e].trim()));
         }
         else if( ((String)tipos.get(e)).equals("enumerado"))
         {
          for(int val=0;val < ((Vector)valEnum.get(e)).size();val++)
          {
           if((aux[e].trim()).equals(((Vector)valEnum.get(e)).get(val)))
           {
            ejemplo.add(new Integer(val)); //Así codifico los valores del tipo enumerado como enteros
            val=((Integer)rangos.get(e)).intValue();
           }
          }

         }
        }
        //Evolucion.imprime(ejemplo,"Ejemplo "+cont++);
        base.add(ejemplo);
       }
       br.close();
       bd.close();

       //System.out.println("\n");

       Vector nuevaFila=null;
       for(int iter=0;iter < base.size();iter++)
       {
        Vector fila=(Vector)base.get(iter);
        nuevaFila=new Vector();

        for(int i=0;i < fila.size();i++)
        {
         nuevaFila.add(fila.get(i));
         if(i != clase)
         {
          //System.out.print(nuevaFila.get(i)+",");
         }
         else
         {
          //System.out.print("Clase: "+nuevaFila.get(i));
         }
        }
        base.insertElementAt(nuevaFila,base.indexOf(fila));
        base.remove(fila);
        //System.out.print("\n");
       }
//Ya tengo listo el vector
       numAtributos=nuevaFila.size();
       numEjemplos=base.size();

      }
      catch (FileNotFoundException e2)
      {
       e2.printStackTrace();
      }
      catch (IOException e)
      {
       e.printStackTrace();
      }
     }
     */

    /**
     * @return Returns the base.
     */
    public Vector getBase() {
        return base;
    }

    /**
     *
     * @param b
     */
    public void setBase(Vector b) {
        base = b;
    }

    /**
     * @return Returns the clase.
     */
    public int getClase() {
        return clase;
    }

    /**
     *
     * @param clas
     */
    public void setClase(int clas) {
        clase = clas;
    }

    /**
     * @return Returns the numAtributos.
     */
    public int getNumAtributos() {
        return numAtributos;
    }

    /**
     * @param numAtributos The numAtributos to set.
     */
    public void setNumAtributos(int numAtributos) {
        this.numAtributos = numAtributos;
    }

    /**
     * @return Returns the numEjemplos.
     */
    public int getNumEjemplos() {
        return numEjemplos;
    }

    /**
     * @param numEj The numEjemplos to set.
     */
    public void setNumEjemplos(int numEj) {
        numEjemplos = numEj;
    }

    /**
     * @return Returns the nombres.
     */
    public Vector getNombres() {
        return this.nombres;
    }

    /**
     * @param nombres The nombres to set.
     */
    public void setNombres(Vector nombres) {
        this.nombres = nombres;
    }

    /**
     * @return Returns the tipos.
     */
    public Vector getTipos() {
        return tipos;
    }

    /**
     *
     * @param i
     * @return Appropriate type name
     */
    public String getTiposIndex(int i) {
        return (String) tipos.get(i);
    }

    /**
     * @param tip The tip to set.
     */
    public void setTipos(Vector tip) {
        tipos = tip;
    }

    /**
     *
     * @param campo
     * @return A String object with every value of attribute 'campo' separated by blank spaces
     */
    public String toString(int campo) {
        String s = new String();

        for (int i = 0; i < base.size(); i++) {
            s += ((Double) ((Vector) base.get(i)).get(campo)).doubleValue() +
                    " ";
        }

        return s;
    }

    /**
     * @return Returns the rangos.
     */
    public Vector getRangos() {
        return rangos;
    }

    /**
     * @return Returns the rangos(i).
     */
    public Object getRangos(int i) {
        return rangos.get(i);
    }

    /**
     *
     * @param i
     * @param e
     * @return Value of 'enumerado' attribute
     */
    public String getRangosEnum(int i, int e) {
        return (String) ((Vector) valEnum.get(i)).get(e);
    }

    /**
     * @param ran The rans to set.
     */
    public void setRangos(Vector ran) {
        rangos = ran;
    }

    /**
     * @return Returns the inicial.
     */
    public Vector getInicial() {
        return inicial;
    }

    /**
     *
     * @param i
     * @return Appropriate initial value
     */
    public Object getInicial(int i) {
        return inicial.get(i);
    }

    /**
     * @param ini The ini to set.
     */
    public void setInicial(Vector ini) {
        inicial = ini;
    }

    /**
     * @return Returns the valEnum.
     */
    public Vector getValEnum() {
        return valEnum;
    }

    /**
     * @param valE The valE to set.
     */
    public void setValEnum(Vector valE) {
        valEnum = valE;
    }

    /**
     * @return Returns the finales.
     */
    public Vector getFinales() {
        return finales;
    }


    /**
     * @param finales The finales to set.
     */
    public void setFinales(Vector finales) {
        this.finales = finales;
    }

    /**
     * @return Returns the cabecera.
     */
    public String getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera The cabecera to set.
     */
    public void setCabecera(String cabecera) {
        this.cabecera = cabecera;
    }

    /**
     *
     * @param ds
     * @return Output files' header
     */
    public static String obtieneCabecera(Dataset ds) {
        //Obtain file header in 'cabecera'
        /*String cabecera = "@relation " + ds.getRelacion() + "\n";
                 for (int i = 0; i < ds.getAtributos().size(); i++) {
            if (!ds.getTiposIndex(i).equals("enumerado")) {
                cabecera += "@attribute " + ds.getAtributosIndex(i) + " " +
         ds.getTiposIndex(i) + " [" + ds.getRangosVar(i).get(0) +
                        ", " + ds.getRangosVar(i).get(1) + "]\n";
            } else {
                cabecera += "@attribute " + ds.getAtributosIndex(i) + " {";
                for (int j = 0; j < ds.getRangosVar(i).size(); j++) {
                    if (j > 0) {
                        cabecera += ", " + ds.getRangosVar(i).get(j);
                    } else {
                        cabecera += ds.getRangosVar(i).get(j);
                    }
                }
                cabecera += "}\n";
            }
                 }
                 cabecera += "@inputs ";
                 for (int i = 0; i < ds.getEntradas().size(); i++) {
            if (i > 0) {
                cabecera += ", " + ds.getEntradas().get(i);
            } else {
                cabecera += ds.getEntradas().get(i);
            }
                 }
                 cabecera += "\n@outputs ";
                 for (int i = 0; i < ds.getSalidas().size(); i++) {
            if (i > 0) {
                cabecera += ", " + ds.getSalidas().get(i);
            } else {
                cabecera += ds.getSalidas().get(i);
            }
                 }
                 cabecera += "\n@data\n";*/

        return ds.copyHeader();
    }
}

