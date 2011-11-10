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

package keel.Algorithms.Rule_Learning.ART;
import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;
import keel.Dataset.Attributes;



/** 
   A Java implementation of the ART algorithm
   @author Ines de la Torre Quesada (UJA)
   @version 1.0 (28-02-2010)
*/
public class ART extends Algorithm
{
	/** Root of the decomposition tree. */
	Node root = new Node();  
	
        /** Total number of Nodes in the tree */
	int NumberOfNodes;  
	
	/** Number of Leafs in the tree */
	int NumberOfLeafs;

        /** Maximum LHS itemset size*/
	int MaxSize;

        /** Minimum support threshold*/
        double MinSupp = 0.1;

        /** Minimum confidence threshold*/
        double MinConf = 1;




	/** Constructor.
	 * 
	 * @param paramFile			The parameters file.
	 * 
	 */
	public ART( String paramFile )
	{
            boolean salir = false;
            try {

            // starts the time
            long startTime = System.currentTimeMillis();

      		// Sets the options of the execution.
		StreamTokenizer tokenizer = new StreamTokenizer( new BufferedReader( new FileReader( paramFile ) ) );
    		initTokenizer( tokenizer) ;
    		setOptions( tokenizer );
    		
    		// Initializes the dataset.
    		modelDataset = new Dataset( modelFileName, true  );
    		
    		/*check if there are continous attributes*/
    		if(Attributes.hasRealAttributes() || Attributes.hasIntegerAttributes())
    		{
    			System.err.println("ART can only handle nominal attributes." );
    			//System.exit(-1);
    			salir = true;
    		}
    		if (!salir){
	    		trainDataset = new Dataset( trainFileName, false  );    		    	
	    		testDataset = new Dataset( testFileName, false  );
	          
		        NumberOfNodes = 1; //La raiz es el primer nodo del arbol
		        NumberOfLeafs = 0;
	       		
	    		// Executes the algorithm.
	    		generateTree();
	    		
	    		// Prints the results generates by the algorithm.
	    		printTrain();
			printTest();
			printResult();
		}
        } catch ( Exception e ){
  			e.printStackTrace();
  //			System.exit(-1);
	    }
	}
 	
	/** Function to read the options from the execution file and assign the values to the parameters.
	 * 
	 * @param options 		The StreamTokenizer that reads the parameters file.
	 * 
	 * @throws Exception	If the format of the file is not correct.
	 */ 
    protected void setOptions( StreamTokenizer options ) throws Exception
	{
  		options.nextToken();
		
  		// Checks that the file starts with the token algorithm.
		if ( options.sval.equalsIgnoreCase( "algorithm" ) )
		{
			options.nextToken();
			options.nextToken();

			//if (!options.sval.equalsIgnoreCase( "ID3" ) )
			//	throw new Exception( "The name of the algorithm is not correct." );

			options.nextToken();
			options.nextToken();
			//options.nextToken();
			//options.nextToken();
			
			// Reads the names of the input files.
			if ( options.sval.equalsIgnoreCase( "inputData" ) )
			{
				options.nextToken();
				options.nextToken();
				modelFileName = options.sval;
					
				if ( options.nextToken() != StreamTokenizer.TT_EOL )
				{
					trainFileName = options.sval;
					options.nextToken();
					testFileName = options.sval;					
					if( options.nextToken() != StreamTokenizer.TT_EOL )
					{
					  trainFileName = modelFileName;	
					  options.nextToken();
					}										
				}																
				
			}
			else
				throw new Exception( "The file must start with the word inputData." );
				
			
			
			while ( true )
			{
				if( options.nextToken() == StreamTokenizer.TT_EOF )
					throw new Exception( "No output file provided." );
			
				if ( options.sval == null )
					continue;
				else if ( options.sval.equalsIgnoreCase( "outputData" ) )
					break;
			}

			/* Reads the names of the output files*/
			
			
				options.nextToken();
				options.nextToken();
				trainOutputFileName = options.sval;
				options.nextToken();
				
				testOutputFileName = options.sval;
				options.nextToken();
				
				resultFileName = options.sval;
				
		}
		else
			throw new Exception( "The file must start with the word algorithm followed of the name of the algorithm." );
	}



	/**
         * Run the algorithm.
	 *
	 */ 
	public void generateTree(){
            Vector data = new Vector(getItemsets());
            MaxSize = trainDataset.numAttributes()-1;
            root.setData(data);
            art(data,root);
	}


        /**
         * Funcion art que construye el árbol
         * @param data	Vector que contiene los itemset a clasificar
         * @param nodo	Nodo del árbol a expandir
	 *
         */
        private void art(Vector data, Node nodo){
            int k = 1;
            double corte = MinConf - MinSupp;
            TBAR tbar = new TBAR(MaxSize,MinSupp,data,trainDataset.attributes);
            Vector<Vector<Rule>> conjuntos;
            Vector<Rule> conjunto = new Vector(), c;
            Vector<Node> children;
            double confidence;
            int maxSupp, supp = 0;
            Vector datos;

            //Mientras el tamaño del antecedente sea menor o igual al numero de
            //atributos y el arbol este vacio
            while(k <= MaxSize && nodo.getAttributes().size()==0){
                
                //extraccion de reglas: tbar
                conjuntos = tbar.ruleExtraction(k);

                //Si existen reglas por las que ramificar
                if(conjuntos.size()>0){
                    confidence = conjuntos.get(0).get(0).getConfidence();
                    if(confidence >= corte){ //Las reglas estan por encima del valor de corte
                        
                        //Seleccion de reglas (como tienen la misma confianza, nos
                        //quedamos con el conjunto que abarque mas ejemplos de training)
                        maxSupp = 0;
                        for(int i=0; i<conjuntos.size(); i++){
                            supp = 0;
                            c = conjuntos.get(i);
                            for(int j = 0; j<c.size(); j++)
                                supp+=c.get(j).getSupport();


                            if(supp > maxSupp){
                                maxSupp = supp;
                                conjunto = c;
                            }
                        } //En conjunto ya tenemos las reglas para ramificar el arbol

                        //Ramificacion del arbol
                        nodo.setAttributes(conjunto.get(0).getAttributes());
                        nodo.setValues(null);
                        nodo.setSupport(data.size());
                        children = new Vector();

                        //para cada regla, crear un nuevo nodo hoja
                        for(int i=0; i<conjunto.size(); i++){
                            Node n = new Node();
                            n.setAttributes(null);
                            n.setValues(conjunto.get(i).getValues());
                            n.setClas(conjunto.get(i).getClas());
                            n.setSupport(conjunto.get(i).getSupport());
                            n.setParent(nodo);
                            children.add(n);
                            NumberOfNodes+=1;
                        }
                        
                        nodo.setChildren(children);
                        NumberOfLeafs+=children.size();

                        //Eliminar de data los ejemplos cubiertos por las reglas
                        datos = uncoveredData(data, nodo);

                        nodo.setData(null);
                        //Subarbol para rama else
                        if(datos.size()>0){
                            //crear nodo hoja para rama else
                            Node n = new Node();
                            n.setParent(nodo);
                            n.setData(datos);
                            n.setSupport(datos.size());
                            children.add(n);

                            NumberOfNodes+=1;
                            art(datos,nodo.getChildren(nodo.numChildren()-1));
                        }
                        
                    }else k++;
                }else{
                    k++;
                }
            }

            if(nodo.getAttributes().size()==0){ //Si no se ha construido arbol
                int index = this.mostFrequentClass(data); //indice de la clase mas frecuente
                //etiquetar con la clase mas frecuente
                nodo.setClas(index);
                nodo.setSupport(data.size());
            }
        }



        /**
         * Funcion que devuelve el indice de la clase mas frecuente
         * @return indice de la clase mas frecuente
         * @param data  Los datos de los que hay que extraer la informacion
         */
         private int mostFrequentClass(Vector<Itemset> data){
             Attribute a =  this.trainDataset.getClassAttribute();
             int[] frequencies = new int[a.numValues()];
             double index;
             int max = 0;
             int clas = -1;

             for(int i=0; i<frequencies.length; i++){
                frequencies[i] = 0;
             }

             for(int i=0; i<data.size(); i++){
                 index = data.get(i).getClassValue();
                 frequencies[(int)index]++;
             }

             for(int i=0; i<frequencies.length; i++){
                if(frequencies[i]>max){
                    max = frequencies[i];
                    clas = i;
                }
             }

             return clas;
         }

        /**
         * Funcion que devuelve los datos no cubiertos por el nodo
         * @return vector con los datos no cubiertos
         * @param data  Los datos que hay que determinar si estan cubiertos
         * @param n     Nodo recien expandido
         */
         private Vector uncoveredData(Vector data, Node n){
            Vector datos = new Vector();
            Vector<Integer> ats;
            Vector<Integer> vals;
            Itemset item;
            boolean enc;
            int j;

            ats = n.getAttributes();

            for(int i=0; i<data.size(); i++){
                item = (Itemset)data.get(i);

                j = 0;
                enc = false;
                while(j<n.numChildren() && !enc){
                    vals = n.getChildren(j).getValues();
                    if(covered(ats,vals,item)){
                        n.getChildren(j).addData(item);
                        enc = true;
                    }
                    else j++;
                }

                if(!enc) //Si la regla no esta cubierta
                    datos.add(item);
            }

            return datos;
         }

        /**
         * Funcion que determina si un itemset esta cubierto por unos valores de los atributos o no
         * @return true si el itemset satisface todos los valores (false en caso contrario)
	 * @param ats	Vector que contiene los atributos a evaluar
	 * @param vals	Vector que contiene los valores de los atributos a evaluar
         * @param item	Itemset a evaluar
	 * 
	 *
         */
         private boolean covered(Vector<Integer> ats, Vector<Integer> vals, Itemset item){
            boolean cover = true;
            int i=0;

            while(i<ats.size() && cover){
                if(item.getValue(ats.get(i)) != vals.get(i)){
                    cover = false;
                }else i++;
            }

            return cover;
         }



 	/** Function to write the decision tree in the form of rules.
 	 * 
 	 * @param node		The current node.
 	 * @param tab		The indentation of the current rule.
 	 * 
 	 * @return		The tree in form of rules.
 	 */
	public String writeTree( Node node, String tab ){
		int outputattr = modelDataset.getClassIndex();
		String cadena = "";
		Attribute classAtt = modelDataset.getClassAttribute();
		String attName = classAtt.name();
                Vector<Integer> ats;
                Vector<Integer> vals;

        try{
        	// Print a leaf node. 
        	if ( node.numChildren() == 0 ) {
                    String value = classAtt.value(node.getClas());

                    // Print a rule.
		    if(node.getParent()!=null){
                    	ats = node.getParent().getAttributes();
                    	vals = node.getValues();
                    	if(vals.size() != 0){
                        	cadena += tab + "if( ";

                        	for(int i=0; i<ats.size(); i++){
                            		cadena+=modelDataset.getAttribute(ats.get(i)).name() +
                                	"==" + modelDataset.getAttribute(ats.get(i)).value(vals.get(i))+ " ) and (";
                        	}

                        	cadena = cadena.substring(0, cadena.length()-5);
                        	cadena+= " then {\n";
                    	}
		    }

                    cadena+= tab+ "\t" + attName + " = \"" + value + "\"\n";
                    
                    return cadena;
        	}


                for(int i=0; i<node.numChildren(); i++){
                    if(i==0){
                        cadena += writeTree( node.getChildren().get(i), tab);
                    }else{
                        cadena += writeTree( node.getChildren().get(i), tab + "\t" );
                    }
                    cadena += tab +  "}else{\n";
                }

                cadena = cadena.substring(0, cadena.length()-7);
        	cadena+= "\n" + tab + "}";

        	return cadena;
        }catch( Exception e ){
        	System.out.println( "Error writing tree" );
	}		

        return cadena;
    }
	
	/** Function to evaluate the class which the itemset must have according to the classification of the tree.
	 * 
	 * @param itemset		The itemset to evaluate.
	 * @param node			The node that is evaluated at this time.
	 * 
	 * @return			The index of the class index predicted.
	 */
	public int evaluateItemset( Itemset itemset, Node node ) {		
		int outputattr = modelDataset.getClassIndex();
		boolean correct = false;
		String aux = null;
		Attribute classAtt = modelDataset.getClassAttribute();
		
		try {
			// if the node is a final leaf
			if ( node.numChildren() == 0 ){
                                return node.getClas();
			}
		}catch ( Exception e){
			return Integer.parseInt( aux.toString() );
                }

		// Evaluate the children of the node.
                int i=0;
                boolean enc = false;

                while(i<node.numChildren()-1 && !enc){
                    if(this.covered(node.getAttributes(),node.getChildren(i).getValues(),itemset))
                        enc = true;
                    else i++;
                }

                if(enc)
                    return(evaluateItemset(itemset,node.getChildren(i)));
                else
                    return(evaluateItemset(itemset,node.getChildren(node.numChildren()-1)));
	}

	
    
	/** Function to get all the itemsets of the dataset.
	 * 
	 * @return The itemsets.
	 */
	private Vector getItemsets()
	{
		Vector itemsets = new Vector( modelDataset.numItemsets());
		
		for ( int i = 0; i < modelDataset.numItemsets(); i++ )
			itemsets.addElement( modelDataset.itemset( i ) );
		
		return itemsets;
	}
    
    /** Writes the tree and the results of the training and the test in the file.
     * 
     * @exception 	If the file cannot be written.
     */
  	public void printResult() throws IOException 
  	{
            long totalTime = ( System.currentTimeMillis() - startTime ) / 1000;
            long seconds = totalTime % 60;
            long minutes = ( ( totalTime - seconds ) % 3600 ) / 60;
            String tree = "";
            PrintWriter resultPrint;

            tree += writeTree( root, "" );
   		   		       			
            tree += "\n@TotalNumberOfNodes " + NumberOfNodes;
            tree += "\n@NumberOfLeafs " + NumberOfLeafs;
   		
            tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
            tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
            tree += "\n@PercentageOfCorrectlyClassifiedTraining " + (float)(correct*100.0)/(float)trainDataset.numItemsets() + "%" ;
            tree += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets()-correct);
            tree += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float)((trainDataset.numItemsets()-correct)*100.0)/(float)trainDataset.numItemsets() + "%" ;
  		
            tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
            tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
            tree += "\n@PercentageOfCorrectlyClassifiedTest " + (float)(testCorrect*100.0)/(float)testDataset.numItemsets() + "%" ;
            tree += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets()-testCorrect);
            tree += "\n@PercentageOfInCorrectlyClassifiedTest " + (float)((testDataset.numItemsets()-testCorrect)*100.0)/(float)testDataset.numItemsets() + "%" ;

            tree += "\n\n@ElapsedTime " + ( totalTime - minutes * 60 - seconds ) / 3600 + ":" + minutes / 60 + ":" + seconds;

            resultPrint = new PrintWriter( new FileWriter ( resultFileName ) );
            resultPrint.print( getHeader() + "\n@decisiontree\n\n" + tree );
            resultPrint.close();
  	}
    
    /**
     * Evaluates the training dataset and writes the results in the file.
     * 
     */
    public void printTrain(){
            String text = getHeader();
            for ( int i = 0; i < trainDataset.numItemsets(); i++ ){
                    try{
                            Itemset itemset = trainDataset.itemset( i );
                            int cl = evaluateItemset( itemset, root );

                            if ( cl == (int) itemset.getValue( trainDataset.getClassIndex() ) )
                                    correct++;

                            text += trainDataset.getClassAttribute().value( cl ) + " " +
                            trainDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + "\n";
                    }
                    catch ( Exception e ){
                            System.err.println( e.getMessage() );
                    }
            }

            try{
                    PrintWriter print = new PrintWriter( new FileWriter ( trainOutputFileName ) );
                    print.print( text );
                    print.close();
            }catch ( IOException e ){
                    System.err.println( "Can not open the training output file: " + e.getMessage() );
            }
    }
	
    /** Evaluates the test dataset and writes the results in the file.
     * 
     */
    public void printTest(){
            String text = getHeader();

            for ( int i = 0; i < testDataset.numItemsets(); i++){
                    try{
                        int cl = (int) evaluateItemset( testDataset.itemset( i ), root );
                        Itemset itemset = testDataset.itemset( i );

                        if ( cl == (int) itemset.getValue( testDataset.getClassIndex() ) )
                                testCorrect++;

                        text += testDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + " " +
                                testDataset.getClassAttribute().value( cl )+ "\n";
                    }
                    catch ( Exception e ){
                            System.err.println( e.getMessage());
                    }
            }

            try{
                    PrintWriter print = new PrintWriter( new FileWriter ( testOutputFileName ) );
                    print.print( text );
                    print.close();
            }catch ( IOException e ){
                    System.err.println( "Can not open the training output file." );
            }
    }
  
    /** Main function.
     *
     * @param args  The parameters file.
     */
    public static void main(String[] args) {
        if ( args.length != 1){
                    System.err.println("\nError: you have to specify the parameters file\n\tusage: java -jar ART.jar parameterfile.txt" );
                     System.exit(-1);
        }else{

            ART art = new ART(args[0]);
        }
    }
   	
}//art

