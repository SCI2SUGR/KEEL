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

package keel.Algorithms.PSO_Learning.CPSO;

/**
 * <p>Title: Algorithm CPSO</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */


import java.io.IOException;
import java.util.Vector;
import keel.Dataset.Attributes;
import org.core.*;


public class CPSO {

    static public myDataset train, val, test;
    String outputTr, outputTst, outputRules;
    
    //parameters
    private long semilla;
    private int NumParticles;
    private int NumAttributes;
    private int NumInstances;
    private double ConvergenceRadius;
    private double WeightsUpperLimit;
  	private double maxUncoveredInstances;
  	private double indifferenceThreshold;
  	private double constrictionCoefficient;
  	private int numDimensions;
  	private int numCenters;
  	private int ConvergencePlatformWidth;
  	
  	private Crono cronometro;
  	
    private Vector<Particle> ruleSet;

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public CPSO(){
    }

    /**
     * It reads the data from the input files (training, validation and test) and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public CPSO(parseParameters parameters) {

        train = new myDataset();
        val = new myDataset();
        test = new myDataset();
        try {
            System.out.println("\nReading the training set: "+parameters.getTrainingInputFile());
            train.readClassificationSet(parameters.getTrainingInputFile(), true);
            System.out.println("\nReading the validation set: "+parameters.getValidationInputFile());
            val.readClassificationSet(parameters.getValidationInputFile(), false);
            System.out.println("\nReading the test set: "+parameters.getTestInputFile());
            test.readClassificationSet(parameters.getTestInputFile(), false);
        } catch (IOException e) {
            System.err.println("There was a problem while reading the input data-sets: " + e);
            somethingWrong = true;
        }

        outputTr = parameters.getTrainingOutputFile();
        outputTst = parameters.getTestOutputFile();
        outputRules = parameters.getOutputFile(0);

        //Now we parse the parameters
        semilla = Long.parseLong(parameters.getParameter(0));
        NumParticles=Integer.parseInt(parameters.getParameter(1));
        ConvergenceRadius = Double.parseDouble(parameters.getParameter(2));
        WeightsUpperLimit = Double.parseDouble(parameters.getParameter(3));
        maxUncoveredInstances = Double.parseDouble(parameters.getParameter(4));
        indifferenceThreshold = Double.parseDouble(parameters.getParameter(5));
        constrictionCoefficient = Double.parseDouble(parameters.getParameter(6));
        ConvergencePlatformWidth = Integer.parseInt(parameters.getParameter(7));
        
        //inicializar la semilla	
        Randomize.setSeed(semilla);
        
        NumAttributes=train.getnInputs();
        numDimensions=NumAttributes*2;
        numCenters=NumAttributes;
        NumInstances=train.getnData();
        ruleSet=new Vector<Particle>(25,10);
        
        Particle.InitializeParameters(indifferenceThreshold, constrictionCoefficient, WeightsUpperLimit);
        
        cronometro=new Crono();
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found, either the data-set have numerical values or missing values.");
            System.err.println("Aborting the program");
            
        }
        
        else {
        	
            train.normalize(indifferenceThreshold);
            val.normalize(indifferenceThreshold);
            test.normalize(indifferenceThreshold);
            
        	cronometro.inicializa();
            	CPSO_Method();
            cronometro.fin();
            
            //Finally we should fill the training and test output files
            double accTrain=doOutput(val, outputTr);
            double accTest=doOutput(test, outputTst);
            PrintOutputRules();
            
            double mediaAtts=0;
            for(int i=0 ; i<ruleSet.size() ; ++i)
            	mediaAtts+=ruleSet.get(i).presentAttsBest();
            mediaAtts/=ruleSet.size();
            
            System.out.print("\n\n************************************************");
            System.out.print("\nPorcertanje acierto train:\t"+accTrain);
            System.out.print("\nPorcertanje acierto test:\t"+accTest);
            System.out.print("\nNumero de reglas:\t\t"+ruleSet.size());
            System.out.print("\nNumero atributos inicial:\t"+NumAttributes);
            System.out.print("\nMedia de atributos/regla:\t"+mediaAtts);
            System.out.print("\nTiempo:\t\t\t\t"+cronometro.tiempoTotal());
            System.out.print("\n************************************************\n\n");

            System.out.println("Algorithm Finished");
        }
    }
    
    
    //*********************************************************************
    //***************** CPSO method ***************************************
    //*********************************************************************
    
    private void CPSO_Method(){
    	        
        Particle bestRule;
        System.out.println("Total de instancias sin clasificar = "+train.noClasificadas());
        
        for(int classChosen=train.ClasePredominante() ; train.QuedanMasInstancias(maxUncoveredInstances)&&train.NumClassesNotRemoved()>1 ; classChosen=train.ClasePredominante()){

        	bestRule=GetRule(classChosen);				//get bestRule
        	RemoveUnnecesaryVariables(bestRule,0);
            ruleSet.add(bestRule);						//add bestRule to ruleSet
            EliminarInstanciasClasificadas(bestRule);	//remove matched instances
            
            System.out.println("Total de instancias sin clasificar = "+train.noClasificadas());
        }
        
        //add default rule
        Particle defaultRule=new Particle(numDimensions, train.ClasePredominante());
        defaultRule.setAsDefaultRule();
        ruleSet.add(defaultRule);
        
        //remove unnecesary rules
        EliminarReglasInnecesarias();
    }
    
    
    //*********************************************************************
    //***************** PSO algorithm to get a rule ***********************
    //*********************************************************************
    
    private Particle GetRule(int classChosen){
    	
    	
    	Particle[] P=new Particle[NumParticles];
        Particle bestActual=new Particle(numDimensions,classChosen);
        Particle bestPrevious=new Particle(numDimensions,classChosen);
        boolean mejoraItActual;
    	
        
        //inicializo las posiciones y velocidades aleatoriamente
        for(int i=0 ; i<NumParticles ; ++i){
            P[i]=new Particle(numDimensions,classChosen);
            P[i].randomInitialization();
        }
        
        
        int ItActOpt=0;
        int iter=0;
        
        do{
        	
            mejoraItActual=false;
            bestActual.bestEvaluation=-1;		//the first particle will be the best of the swarm at start
            
            
            //1) evaluar el fitness de cada particula
            for(int i=0 ; i<NumParticles ; ++i){

                //1) evaluar P
                P[i].lastEvaluation=P[i].evaluation();
            
            	
                //2) actualizar Bp
                if(P[i].lastEvaluation>P[i].bestEvaluation)
                    P[i].setB(P[i].X,P[i].lastEvaluation);
                
                
                //3) actualizar Bg
                if(P[i].isBetter(bestActual))
                	bestActual=P[i].cloneParticle();
            }
            
            
            if(bestActual.isBetter(bestPrevious))
                mejoraItActual=true;

            
            //2) mover cada particula a su siguiente posicion
            for(int i=0 ; i<NumParticles ; ++i){
                 P[i].updateV(bestActual);
                 P[i].updateX();
            }
            
            //ver si en esta iteracion se mejoro el global
            if(mejoraItActual){
            	ItActOpt=0;
            }
            
            else{
            	ItActOpt++;
            	}
            
            iter++;
            
            bestPrevious=bestActual.cloneParticle();
            

        }while(ItActOpt<ConvergencePlatformWidth && !ParticulasCercanas(P,bestActual));
        
        
        Particle bestParticle=bestActual.cloneParticle();
        bestParticle.lastEvaluation=bestParticle.bestEvaluation;
        bestParticle.setX(bestParticle.B);
		
		//set attribute presence
		bestParticle.fixAttributePresence();

        return bestParticle;
    }
    
    
    //*********************************************************************
    //***************** Distance between particles ************************
    //********************************************************************* 
    
    private Boolean ParticulasCercanas(Particle[] P, Particle G){
        
        for(int i=0 ; i<NumParticles ; ++i)
            if(EuclideanDistance(P[i],G)>(ConvergenceRadius*indifferenceThreshold))
            	return false;
        
        return true;
    }
    
    private double EuclideanDistance(Particle p1, Particle p2){
    
        double distance=0;
    
        //distance for centers
        for(int d=0 ; d<numCenters ; ++d)
        	distance+=Math.pow(p1.B[d]-p2.B[d], 2);
            
        
        //distance for radius
        for(int d=numCenters ; d<numDimensions ; ++d)
        	if(train.getTipo(d-(NumAttributes))!=myDataset.NOMINAL)
        		distance+=Math.pow(p1.B[d]-p2.B[d], 2);
        
        distance=Math.sqrt(distance);
        
        //count real number of dimensions
        int cont=0;
        for(int d=0 ; d<NumAttributes ; ++d)
        	if(train.getTipo(d)!=myDataset.NOMINAL)
        		cont+=2;
        	else
        		cont++;
        
        distance=distance/(Math.sqrt(cont));
    
        return distance;
    }
    
    
    //*********************************************************************
    //***************** Remove unnecesary attributes of a rule ************
    //*********************************************************************
    
    
    private double RemoveUnnecesaryVariables(Particle rule, int pos){
    	
        double bestEvaluation=rule.evaluation();
        double newEvaluation;
    	
    	for(int i=pos ; i<NumAttributes ; ++i){
    		
    		if(rule.GetAttributePresence(i)){
    			
    			rule.SetAttributePresence(i, false);
    			newEvaluation=RemoveUnnecesaryVariables(rule, i+1);
    			
    			if(newEvaluation>=bestEvaluation)
    				bestEvaluation=newEvaluation;
    			
    			else
        			rule.SetAttributePresence(i, true);
    		}
    		
    	}
    	
    	return bestEvaluation;
    	
    }
    
   
    //*********************************************************************
    //***************** Remove unnecesary rules ***************************
    //*********************************************************************    
    
    private void EliminarReglasInnecesarias(){
        
        Boolean continuar=true;
        
        //elimino reglas que son mas amplias que otras
        for(int i=ruleSet.size()-2 ; i>=0 ; --i){
            continuar=true;
            for(int j=i-1 ; j>=0&&continuar ; --j){
                if(IsSubSet(ruleSet.get(j),ruleSet.get(i))){//veo si alguna j, es menor que i (j incluido en i)
                	ruleSet.remove(i);
                    continuar=false;
                }        
            }
        }
        
        
        //elimino reglas que predicen misma clase que la regla por defecto y estan justo antes que ella
        continuar=true;
        for(int i=ruleSet.size()-2 ; i>=0&&continuar ; --i){
        
            if(ruleSet.get(i).clase==ruleSet.get(ruleSet.size()-1).clase)
            	ruleSet.remove(i);
            
            else//en el momento que no elimine una, salgo del bucle
                continuar=false;
        }
        
        //elimino si hay reglas despues de una regla por defecto
        continuar=false;
        for(int i=0 ; i<ruleSet.size() ; ++i){

            if(continuar)
            	ruleSet.remove(i--);
            	
        	if(!continuar && ruleSet.get(i).presentAttsBest()==0)
        		continuar=true;
        }
    }
    
    
    private Boolean IsSubSet(Particle Rule1, Particle Rule2){

        //para los atributos presentes
        for(int d=0 ; d<NumAttributes ; ++d){
            
            if(Rule1.GetAttributePresence(d)){
            	
            	//ATRIBUTOS NUMERICOS
            	if(train.getTipo(d)!=myDataset.NOMINAL){
            		if(     !(Rule2.GetAttributePresence(d)) ||
            				!((Rule1.X[d]-Rule1.X[NumAttributes+d])<=(Rule2.X[d]-Rule2.X[NumAttributes+d])) ||
            				!((Rule1.X[d]+Rule1.X[NumAttributes+d])>=(Rule2.X[d]+Rule2.X[NumAttributes+d])))
            			return false;
                }
            	
            	//atributos nominales
            	else{
            		
            		int rango=(int)train.devuelveRangos()[d][1]+1;
					int valueR1=(int)((Rule1.B[d]*rango)/indifferenceThreshold);
					int valueR2=(int)((Rule2.B[d]*rango)/indifferenceThreshold);
					   
            		if(     !(Rule2.GetAttributePresence(d)) ||
            				!(valueR1==valueR2))
            			return false;
            		
            	}
            	
            }
        }
        
        return true;
    }
    

   //*********************************************************************
   //***************** Remove classified instances ***********************
   //*********************************************************************   
   
   public void EliminarInstanciasClasificadas(Particle p){

       for(int i=0 ; i<NumInstances ; ++i){
           //si satisface la clase y el antecedente se remueve
           if(p.CoverInstance(train.getExample(i))&&train.getOutputAsInteger(i)==p.clase)
               train.setRemoved(i,true);
           }
   }
   

   //*********************************************************************
   //***************** To do outputs files *******************************
   //********************************************************************* 

   public void PrintOutputRules(){
	   
	   double valor1, valor2;
	   double min[]=train.getemin();
	   double max[]=train.getemax();
	   String cad="";

	   //atributos presentes
	   for(int i=0 ; i<ruleSet.size() ; ++i){
		   cad+="\n\n\nIF\t";

		   if(ruleSet.get(i).presentAttsBest()==0)
			   cad+="TRUE\n\tTHEN CLASS = "+train.getOutputValue(ruleSet.get(i).clase);
		   
		   else{

			   for(int j=0 ; j<NumAttributes ; ++j){
				   
				   if(ruleSet.get(i).GetAttributePresence(j)){
					   
					   String nombreAtt=Attributes.getInputAttribute(j).getName();
					   
					   valor1=(ruleSet.get(i).B[j]-ruleSet.get(i).B[j+numCenters]);
					   valor2=(ruleSet.get(i).B[j]+ruleSet.get(i).B[j+numCenters]);
					   
					   valor1=train.Desnormalizar(valor1, j, indifferenceThreshold);
					   valor2=train.Desnormalizar(valor2, j, indifferenceThreshold);
					   
					   if(valor1<min[j])
						   valor1=min[j];
					   
					   if(valor2>max[j])
						   valor2=max[j];

					   if(train.getTipo(j)!=myDataset.NOMINAL)
						   cad+="\t"+nombreAtt+"\tin\t["+valor1+" , "+valor2+"]\n\tAND";

					   else{
						   int rango=(int)train.devuelveRangos()[j][1]+1;
						   int value=(int)((ruleSet.get(i).B[j]*rango)/indifferenceThreshold);
						   cad+="\t"+nombreAtt+" = "+Attributes.getInputAttribute(j).getNominalValue(value)+"\n\tAND";
					   }
					   
				   }
   				}

   				cad=cad.substring(0, cad.length()-4);
   				cad+="\tTHEN CLASS = "+train.getOutputValue(ruleSet.get(i).clase);
		   }
		   
	   }


	   Fichero.escribeFichero(outputRules, cad);
   }
   
    
    /**
     * It generates the output file from a given dataset and stores it in a file
     * @param dataset myDataset input dataset
     * @param filename String the name of the file
     */
    private double doOutput(myDataset dataset, String filename) {
        
        double aciertos=0;
        
        String output = new String("");
        output = dataset.copyHeader(); //we insert the header in the output file
        //We write the output for each example
        for (int i = 0; i < dataset.getnData(); i++) {

        	output += dataset.getOutputAsString(i) + " " + this.classificationOutput(dataset.getExample(i)) + "\n";

            if(dataset.getOutputAsString(i).equals(this.classificationOutput(dataset.getExample(i))))
                aciertos++;
        }
        
        Fichero.escribeFichero(filename, output);
        
        return aciertos/dataset.getnData();
    }

    
    /**
     * It returns the algorithm classification output given an input example
     * @param example double[] The input example
     * @return String the output generated by the algorithm
     */
    private String classificationOutput(double[] example){
    	
        String output = "";
        
        for(int i=0 ; i<ruleSet.size() ; ++i){
            //veo si coincide el antecedente
            if(ruleSet.get(i).CoverInstance(example)){//coincide el antecedente, devuelvo la primera clase que lo cumple
                output=train.getOutputValue(ruleSet.get(i).clase);
                return output;
            }
        }
        
        return output;
    }
    
 
    
}

