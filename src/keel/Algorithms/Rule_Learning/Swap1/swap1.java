/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Rule_Learning.Swap1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

import keel.Dataset.HeaderFormatException;

import org.core.Files;



/**
 *
 * @author Javier Rascón Mesa
 */
public class swap1{

    private int k_counter; // contador
    private rule S; // Conjunto de casos de entrenamiento
    private rule tstSet;
    private ArrayList<rule> R; // Array de listas de reglas
    private ArrayList<rule> C; // Not used Components
    private ArrayList<rule> P;
    private ArrayList<rule> E;
    private ArrayList<Attr_pos> atributos_entrada; //Lista con todos los atributos_entrada nominales de entrada
    private ArrayList<String> atributos_salida; //Lista con todos los atributos_entrada nominales de salida

    private String trainPrediction[];
    private String testPrediction[];
    private String trainReal[];
    private String testReal[];
    private String relation;
    protected Attribute[] inputs;
	protected Attribute output;
    
    BufferedWriter bw_output;

    /**
     * Indica a qué atributo pertenece la cadena
     */
    private class Attr_pos{
	public String attr;
	public int pos;

	public Attr_pos(String _a,int _p){
	    attr=_a;
	    pos=_p;
	}
    }

    private class rule extends InstanceSet{

	private String clase=null;
	private LinkedList<Integer> attr_pos;

	public rule(rule _ru){
	    
	    super(_ru);
	    this.clase =_ru.clase;
	    this.attr_pos = new LinkedList<Integer> (_ru.attr_pos);
	}

	private rule() {
	    super();
	    this.attr_pos = new LinkedList<Integer> ();
	}

	private rule(String _clase) {
	    super();
	    clase=_clase;
	    this.attr_pos = new LinkedList<Integer> ();
	}

	@Override
	public void removeInstance(int pos){

	    super.removeInstance(pos);

	    if(attr_pos.size()!=0)
	        attr_pos.remove(pos);
	}

	public void addInstance(Instance inst, int pos){
	    super.addInstance(inst);
	    this.attr_pos.add(pos);
	}

	public int posInstance(int inst_pos){
	    return this.attr_pos.get(inst_pos);
	}

        /**
         *
	 * Hace un intercambio disminuyendo el número de errores
	 *
         * @return Una regla
         */
	@SuppressWarnings("empty-statement")
        public rule swap_min_error(){//Decrease number of errors

	    rule r_old = new rule(this);

	    boolean swap_found,d;

	    do{
		//make the single best swap for any component of R that
		//    reduces the errors made by R on cases in S
		swap_found = this.single_best_swap();

		if(!swap_found)//if no swap can be found then
		    return this;//    return the rule R
		//endif

		d=true;//D := true

		while(this.n_errors()!=0 && d){//while((R does not have 0 errors) And (D is true)) do
			//    make the single best swap for any component of R that
			//    reduces the errors made by R on cases in S
			swap_found = this.single_best_swap();

			if(!swap_found)//    if no swap can be found then
				d=false;//D := false
		}//endwhile

		//consider adding components to R to make it 100% predictive again

	    }while(this.num_covered_cases()<=r_old.num_covered_cases());

            return this;
        }

	/**
	 *
	 * @return Numero de casos cubiertos por la regla
	 */
	int num_covered_cases(){

	    int num_covered=0;
	    boolean covered;
	    int C_count=C.get(k_counter).getNumInstances();
	    int this_count=this.getNumInstances();
	    int i,j;
	    Instance C_ins,this_ins;

	    for(i=0;i<C_count;i++){
		C_ins = C.get(k_counter).getInstance(i);
		covered = true;

		for(j=0;j<this_count && covered;j++){
		    this_ins = this.getInstance(j);

		    if(!C_ins.getInputNominalValues(this.posInstance(j)).equals(this_ins.getInputNominalValues(0))){
			covered = false;
		    }
		}
		if(covered)
		    num_covered++;
	    }

	    return num_covered;
	    
	}

	/**
	 *
	 * @return Numero de errores cometidos por la regla
	 */
        private int n_errors(){

	    int num_errors=this.num_covered_cases();
	    int i,j;
	    int C_count=C.get(k_counter).getNumInstances();
	    int this_count=this.getNumInstances();
	    boolean is_correct;
	    Instance C_ins,this_ins;

	    for(i=0;i<C_count;i++){
		C_ins = C.get(k_counter).getInstance(i);
		is_correct = true;

		for(j=0;j<this_count;j++){
		    this_ins = this.getInstance(j);

		    if(C_ins.getInputNominalValues(this.posInstance(j)).equals(this_ins.getInputNominalValues(0))){//Si esta cubierto
			if(!C_ins.getOutputNominalValues(0).equals(this.clase)){// si es correcto
			    is_correct = false;
			}
		    }
		    else{
			is_correct = false;
		    }

		}
		if(is_correct)
		    num_errors--;
	    }

            return num_errors;
        }

        /**
         *
         * @return Nivel Predictivo de la regla
         */
        private double predictive_level(){
	    
	    double level,covered=this.num_covered_cases();
	    
	    level = covered-this.n_errors();
	    level /= covered;
	    
	    return level;
        }

	/**
	 *
	 * Busca el mejor intercambio que reduzca el numero de errores y lo hace si se encuentra
	 *
	 * @return boolean that indicates if the swap has been found
	 */
	private boolean single_best_swap(){ //single best swap for any component of R that reduces the errors made by R on cases in S

	    boolean found = false; //Single best swap found
	    rule curr_r = this;//R.get(k);
	    rule copy;
   	    int curr_err; //current errors
	    int best_n_err=(curr_r.num_covered_cases() != 0)?curr_r.n_errors():Integer.MAX_VALUE;
	    int best_i=-1,best_j=-1;

	    for(int i=0;i<curr_r.getNumInstances();i++){

		for(int j=0;j<atributos_entrada.size();j++){

		    boolean permitido = true;
		    
		    for(int k=0;k<this.attr_pos.size();k++)
			if(this.attr_pos.get(k)==atributos_entrada.get(j).pos && k != i)
			    permitido = false;

		    if(permitido){
			copy = new rule(curr_r);

			//swap
			copy.removeInstance(i);
			copy.addInstance(new Instance(atributos_entrada.get(j).attr,false,copy.getNumInstances()),atributos_entrada.get(j).pos);

			//cálculos
			curr_err=copy.n_errors();
			if(curr_err<best_n_err && copy.num_covered_cases() != 0){
			    found=true;
			    best_i=i;
			    best_j=j;
			    best_n_err=curr_err;
			}
		    }
		}
	    }
	    if(found){//si se encuentra el swap... se hace
		//swap
		this.removeInstance(best_i);
		this.addInstance(new Instance(atributos_entrada.get(best_j).attr,false,this.getNumInstances()),atributos_entrada.get(best_j).pos);
	    }

	    return found;

	}

	/**
	 *
	 * @return true Si se ha añadido un atributo a la regla, false en caso contrario
	 */
	private boolean add_single_best() {

	    rule copy=null;
	    double p; //predictive value
	    double p_best=this.predictive_level(); //best predictive value
	    int best_covered=this.num_covered_cases();
	    int best_pos=-1; //best predictive value position //si no se encuentra uno mejor, se añade el primero

	    for(int i=0;i<atributos_entrada.size();i++){
		if(!this.attr_pos.contains(atributos_entrada.get(i).pos)){

		    copy= new rule(this);//R.get(k));
		    copy.addInstance(new Instance(atributos_entrada.get(i).attr,false,copy.getNumInstances()),atributos_entrada.get(i).pos);

		    p=copy.predictive_level();
		    if(p>p_best || (p_best==p && copy.num_covered_cases() > best_covered)){
			best_pos=i;
			p_best=p;
			best_covered=copy.num_covered_cases();
		    }
		}
	    }

	    if(best_pos!=-1){
		this.addInstance(new Instance(atributos_entrada.get(best_pos).attr,false,this.getNumInstances()),atributos_entrada.get(best_pos).pos);
		return true; // Se han hecho cambios
	    }
	    else
		return false; //Sin cambios

	}

	/**
	 *
	 * @return Devuelve un InstanceSet de los casos satisfechos por la regla
	 */
	public rule satisfied_cases(){

	    int C_count=C.get(k_counter).getNumInstances();
	    int this_count=this.getNumInstances();
	    int i,j;
	    boolean satisfied;
	    Instance C_ins,this_ins;
	    rule r_local = new rule();


	    for(i=0;i<C_count;i++){

		C_ins = C.get(k_counter).getInstance(i);
		satisfied=true;

		for(j=0;j<this_count&&satisfied;j++){
		    this_ins = this.getInstance(j);

		    if(!C_ins.getInputNominalValues(this.posInstance(j)).equals(this_ins.getInputNominalValues(0)))
			    satisfied=false;

		}
		if(satisfied)
		    r_local.addInstance(C_ins);

	    }

	    return r_local;
	}

	/**
	 * 
	 * @param ins1 Intancia a ser comparada
	 * @param ins2 Intancia a ser comparada
	 * @return true si son iguales, false en caso contrario
	 */
	private boolean equal_attr(Instance ins1,Instance ins2){
	    return equal_inst(ins1,ins2,-1,-1);
	}

	/**
	 *
	 * @param ins1 Intancia a ser comparada
	 * @param ins2 Intancia a ser comparada
	 * @param pos1 Posicion del atributo a comparar
	 * @param pos2 Posicion del atributo a comparar
	 * @return true si son iguales, false en caso contrario
	 */
	private boolean equal_inst(Instance ins1,Instance ins2,int pos1,int pos2){

	    if(pos1==-1 && pos2==-1){

		boolean iguales=true;

		for(int i=0;i<Attributes.getInputNumAttributes() && iguales;i++)
		    if(!ins1.getInputNominalValues(i).equalsIgnoreCase(ins2.getInputNominalValues(i)))
			iguales=false;

		return iguales;
	    }
	    else
		return ins1.getInputNominalValues(pos1).equalsIgnoreCase(ins2.getInputNominalValues(pos2));
	}

	/**
	 * 
	 * Une la regla pasada por parámetro a this sin insertar los repetidos
	 * 
	 * @param _r Regla que unir
	 */
	public void union(rule _r){
    	    Instance ins1,ins2;
	    boolean found;

	    for(int i=0;i<_r.getNumInstances();i++){
		ins1=_r.getInstance(i);
		found=false;

		for(int j=0;j<this.getNumInstances() && !found;j++){
		    ins2=this.getInstance(j);
		    if(equal_inst(ins1,ins2,0,0))//Si son iguales
			found=true;
		}

		if(!found){
		    this.addInstance(ins1,_r.posInstance(i));
		}

	    }
	}

	/**
	 * 
	 * Borra los elementos de _r que haya en this
	 * 
	 * @param _r Elementos que borrar
	 */
	public void remove(rule _r){

	    Instance ins1,ins2;
	    boolean deleted;

	    for(int i=0;i<_r.getNumInstances();i++){
		ins1=_r.getInstance(i);
		deleted =false;

		for(int j=0;j<this.getNumInstances() && !deleted;j++){

		    ins2=this.getInstance(j);
		    if(equal_attr(ins1,ins2)){//Si son iguales
			this.removeInstance(j);//eliminar el elemento
			deleted = true;
		    }
		}
	    }
	}

	/**
	 * 
	 * @return true si la regla está vacía, false en caso contrario
	 */
	public boolean isEmpty(){
	    return 0==super.getNumInstances();
	}

	/**
	 *
	 * @return Clase sobre la que está trabajando la regla
	 */
	public String get_clase(){
	    return clase;
	}

    }

    public swap1(String trainName, String testName) throws ExNotNominalAttr{//S: set of training cases
       
	try{

	    k_counter=0;//1

	    S = new rule();
	    P = new ArrayList<rule>();
	    E = new ArrayList<rule>();
	    R = new ArrayList<rule>();

	    S.readSet(trainName,true);
	    this.checkNominal();
	    //inicializar C
	    C = new ArrayList<rule>();
	    C.add(k_counter, S);
	    R.add(k_counter, new rule());

	    atributos_entrada = new ArrayList<Attr_pos>();

	    for(int i=0;i<Attributes.getInputNumAttributes();i++){
		Attribute a=Attributes.getInputAttribute(i);
		for(int j=0;j<a.getNumNominalValues();j++)
		    atributos_entrada.add(new Attr_pos(a.getNominalValue(j),i));
	    }

	    atributos_salida = new ArrayList<String>();

	    for(int i=0;i<Attributes.getOutputNumAttributes();i++){
		Attribute a=Attributes.getOutputAttribute(i);
		for(int j=0;j<a.getNumNominalValues();j++)
		    atributos_salida.add(a.getNominalValue(j));
	    }

	    tstSet = new rule();
	    tstSet.readSet(testName, false);

	    File archivo = new File (Parameters.logOutputFile);
	    FileWriter fw;

	    try {
		fw = new FileWriter(archivo);
		bw_output = new BufferedWriter(fw);
	    } catch (IOException ex) {
		Logger.getLogger(swap1.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    
	}catch (DatasetException ex){
	    System.out.println ("\n\n>>>TRAIN Errors");
	    ex.printAllErrors();
	    System.out.println("Error: "+ex.getMessage());
	    
	}catch (HeaderFormatException e2){
	    System.err.println ("Exception in header format: "+e2.getMessage());
	}
    }

    /**
     * Entrenemiento
     */
    public void train(){

    	StringTokenizer  tokens;
	boolean changed;
	rule b = null;
	String curr_class;

	curr_class=atributos_salida.remove(0);

	//Information for KEEL output files
	tokens = new StringTokenizer (S.getHeader()," \n\r");
    tokens.nextToken();
    relation = tokens.nextToken();
    
    inputs = Attributes.getInputAttributes();
    output = Attributes.getOutputAttribute(0);
    
        do{
	    int ins_counter=0;

	    if(!quedan_de_la_clase(curr_class))
		curr_class=atributos_salida.remove(0);

	    b = new rule(curr_class);
	    Attr_pos a_p=atributos_entrada.get(new Random().nextInt(atributos_entrada.size())); //Escoge aleatoriamente un atributo de la lista

	    b.addInstance(new Instance(a_p.attr,false,ins_counter++),a_p.pos);
	    // Create a rule B with a randomly chosen attribute as its LHS

	    changed = true;

	    //while (B is not 100% predictive) do{
	    while(changed){
		// make the single best swap for any component of B, including
		//	deleting the component, using cases in C[k]
		if(!(changed = b.single_best_swap())) // if no swap is found, add the single best component to B
		    changed = b.add_single_best();

	    }

	    P.add(k_counter,b.swap_min_error()); // P[k] := swap_min_error(B,C[k])

	    E.add(k_counter,b.satisfied_cases());//E[k] := cases in C that satisfy the single-best-rule P[k]

	    R.add(k_counter+1,P.get(k_counter)); //R[k+1] := R U {P[k]}

	    C.add(k_counter+1, new rule(C.get(k_counter)));
	    C.get(k_counter+1).remove(E.get(k_counter));//C[k+1] := C - {E[k]}

	    k_counter++;
        }while(!C.get(k_counter).isEmpty()); //until (C[k] is empty)

	int pos;

        //find a rule R in R that can be deleted without affecting performance on cases in S
        while((pos=rule_to_be_erased())!=-1){//while (R can be found){
	    R.remove(pos);
	}//}endwhile

        statistics();

    }

	/**
	 * Classifies the training set
	 */
	public void classifyTrainSet(){
				
		trainPrediction=new String[S.getNumInstances()];
		trainReal=new String[S.getNumInstances()];
		
		for(int i=0;i<S.getNumInstances();i++){
			
			trainPrediction[i]=classify(S.getInstance(i));
			trainReal[i]=(S.getInstance(i)).getOutputNominalValues(0);
		}

	}
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		testPrediction=new String[tstSet.getNumInstances()];
		testReal=new String[tstSet.getNumInstances()];
		
		for(int i=0;i<tstSet.getNumInstances();i++){
			
			testPrediction[i]=classify(tstSet.getInstance(i));
			testReal[i]=(tstSet.getInstance(i)).getOutputNominalValues(0);
		}

	}
	
	public String classify(Instance ins){

    	boolean covered = false;
    	String val="Unclassified";
    	
    	for(int j=1;j<R.size() && !covered;j++){

    		boolean correct = true;
    		rule rR = R.get(j);

    		for(int k=0;k<rR.getNumInstances() && correct ;k++){

    			Instance rR_i = rR.getInstance(k);

    			if(!ins.getInputNominalValues(rR.posInstance(k)).equals(rR_i.getInputNominalValues(0))){
    				correct = false;
    			}
    		}

    		if(correct){
    			covered = true;

    			val= rR.get_clase();

    			


    		}
    	}
    	
    	return val;
	}
	
	/**
	 * Reports the results obtained
	 */
	public void writeResults(){
		
		writeOutput(Parameters.trainOutputFile, trainReal, trainPrediction);
		writeOutput(Parameters.testOutputFile, testReal, testPrediction);

	}
	
	/**
	 * Prints KEEL standard output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	protected void writeOutput(String filename, String [] realClass, String [] prediction) {
	
		String text = "";
		
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
			
		text = "";
			
		for (int i=0; i<realClass.length; i++) {
			            
			text += "" + realClass[i] + " ";
			text += "" + prediction[i] + " ";
		    
			text += "\n"; 
			if((i%10)==9){
			    Files.addToFile(filename, text);
			    text = ""; 
			}			
			
		}
		
		if((realClass.length%10)!=0){
			Files.addToFile(filename, text);
		}
	}
		
    /**
     * Pruebas
     */
    public void test(){

	int acertados=0;

		// Perform classification of training and test sets in KEEL Format
		classifyTrainSet();
		classifyTestSet();
		writeResults();
	
	
	try{
	    bw_output.write("\n\n");
	    bw_output.write("---------------------------------------------\n");
	    bw_output.write("Inicio de las pruebas\n");
	    bw_output.write("---------------------------------------------\n");

	    for(int i=0;i<tstSet.getNumInstances();i++){
	    	
	    	Instance tst_i = tstSet.getInstance(i);

	    	boolean covered = false;

	    	for(int j=1;j<R.size() && !covered;j++){

	    		boolean correct = true;
	    		rule rR = R.get(j);

	    		for(int k=0;k<rR.getNumInstances() && correct ;k++){

	    			Instance rR_i = rR.getInstance(k);

	    			if(!tst_i.getInputNominalValues(rR.posInstance(k)).equals(rR_i.getInputNominalValues(0))){
	    				correct = false;
	    			}
	    		}

	    		if(correct){
	    			covered = true;

	    			for(int k=0;k<Attributes.getInputNumAttributes();k++){
	    				System.out.print(tst_i.getInputNominalValues(k)+" ");
	    				bw_output.write(tst_i.getInputNominalValues(k)+" ");
	    			}

	    			System.out.print(" :");
	    			bw_output.write(" :");

	    			String swap_res = rR.get_clase(), real_res = tst_i.getOutputNominalValues(0);

	    			System.out.println(" Segun SWAP-1 es: "+swap_res+" y en verdad es: "+real_res);
	    			bw_output.write(" Segun SWAP-1 es: "+swap_res+" y en verdad es: "+real_res+"\n");

	    			if(real_res.equalsIgnoreCase(swap_res)){
	    				acertados++;
	    			}


	    		}

	    	}
	    }

	    double total_ins = tstSet.getNumInstances();
	    double accuracy = acertados/total_ins;

	    System.out.println("Casos totales: "+total_ins);
	    System.out.println("Casos acertados: "+acertados);
	    System.out.println("Porcentaje de acierto: "+accuracy*100+"%");

    //	try{

		bw_output.write("Casos totales: "+total_ins+"\n");
		bw_output.write("Casos acertados: "+acertados+"\n");
		bw_output.write("Porcentaje de acierto: "+accuracy*100+"%\n");

		bw_output.close();
	    
	}
	catch(IOException ex){
	    System.out.println("Error en la escritora del ficehro de salida");
	}


    }

    /**
     *
     * Busca una regla que puede ser borrada sin que afecte a la performance
     *
     * @return Posicion de la regla que puede ser borrada
     */
    private int rule_to_be_erased(){ //find a rule R in R that can be deleted without affecting performance on cases in S

	    double p=performance(R); //obtener performance
	    ArrayList<rule> copy=null;

	    for(int i=1;i<R.size();i++){//para la cantidad de atributos_entrada de la regla
		copy=new ArrayList<rule>(R);//copiar conjunto de reglas
		
		copy.remove(i);

		//calcular y almacenar nueva performance
		if(p==performance(copy))//si la performance no ha cambiado
		    return i;//devolver at
	    }

	return -1;

    }

    /**
     * Reglas que han sido generadas por el algoritmo
     */
    private void statistics(){

	rule _r;

	try{

	    bw_output.write("---------------------------------------------\n");
	    bw_output.write("Inicio del entrenamiento\n");
	    bw_output.write("---------------------------------------------\n");

	    for(int i=1;i<R.size();i++){
		_r = R.get(i);
		int j;

		for(j=0;j<(_r.getNumInstances()-1);j++){
		    System.out.print("["+Attributes.getInputAttribute(_r.attr_pos.get(j)).getName()+"] = "+_r.getInstance(j).getInputNominalValues(0)+" && ");
		   bw_output.write("["+Attributes.getInputAttribute(_r.attr_pos.get(j)).getName()+"] = "+_r.getInstance(j).getInputNominalValues(0)+" && ");
		}

		System.out.print("["+Attributes.getInputAttribute(_r.attr_pos.get(j)).getName()+"] = "+_r.getInstance(j).getInputNominalValues(0)+" --> ");
		System.out.println(_r.get_clase());

		bw_output.write("["+Attributes.getInputAttribute(_r.attr_pos.get(j)).getName()+"] = "+_r.getInstance(j).getInputNominalValues(0)+" --> ");
		bw_output.write(_r.get_clase()+"\n");

	    }

	}
	catch(IOException ex){
	    System.out.println("Error en la escritora del ficehro de salida");
	}
    }

    /**
     * 
     * Comprueba si todos los atributos son nominales
     * 
     * @throws ExNotNominalAttr Hay atributos no nominales
     */
    private void checkNominal() throws ExNotNominalAttr{

	for(int i=0;i<Attributes.getInputNumAttributes();i++){
	    Attribute a=Attributes.getInputAttribute(i);
	    if(a.getType()!=Attribute.NOMINAL)
		throw new ExNotNominalAttr();

	}
    }

    /**
     * 
     * Busca la existencia de instancias de la clase indicada en el conjunto de elementos que todavía no están cubiertos
     * 
     * @param clase Clase que se busca
     * @return true si quedan instancias de la clase indicada
     */
    private boolean quedan_de_la_clase(String clase){

	InstanceSet a=C.get(k_counter);

	for(int i=0;i<a.getNumInstances();i++){
	    if(clase.equalsIgnoreCase(a.getInstance(i).getOutputNominalValues(0)))
		return true;
	}

	return false;
    }

    /**
     * 
     * @param is InstanceSet del que calcular la performance
     * @return Porcentaje (sobre 1) de aciertos
     */
    private double performance(ArrayList<rule> is){

	int acertados=0;

	for(int i=0;i<S.getNumInstances();i++){
	    Instance tst_i = S.getInstance(i);

	    boolean covered = false;

	    for(int j=1;j<is.size() && !covered;j++){

		boolean correct = true;
		rule rR = is.get(j);

		for(int k=0;k<rR.getNumInstances() && correct ;k++){

		    Instance rR_i = rR.getInstance(k);

		    if(!tst_i.getInputNominalValues(rR.posInstance(k)).equals(rR_i.getInputNominalValues(0))){
			    correct = false;
		    }
		}

		if(correct){
		    covered = true;

		    String swap_res = rR.get_clase(), real_res = tst_i.getOutputNominalValues(0);

		    if(real_res.equalsIgnoreCase(swap_res))
			acertados++;

		}

	    }
	}

	double total_ins = tstSet.getNumInstances();
	return acertados/total_ins;

    }

}

