/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/08/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.jdom.Element;

public class EducationalMethodReport extends EducationalReport
{
    /**
     * <p>
     * This class creates a report in the experiment directory.
     * A file "report.txt" is creates in the same result directory.
     * The report is for methods.
     * </p>
     */

	private String[] classes = null;
	//training and test
	private String set = "";
	private int[][] confusionMatrix = null;
	private int n_partition = -1;
	private List<Double> listECMParticion=null;
	private double ecmBest = 0.0, ecmAverage = 0.0, ecmDeviation = 0;

	/**
     * <p>
	 * Constructor
	 * </p>
	 * @param	sentences Total of sentences for RunKeel.xml
	 * @param	experimentType Type of experiment, classification o regression
	 */
	public EducationalMethodReport(ArrayList<Element> sentences, int experimentType)
	{				
		super(sentences, experimentType);
	}

	/**
     * <p>
	 * This method has to invoque for to create the report.
	 * Verify the type of problem, type partition and paths for
	 * to create the report. Read in iterative way the files of
	 * results
	 * </p>
	 */
	public void running()
	{		
		StringTokenizer st = null;	
		int totalGoods = 0;
		int totalInstances = 0;
		String partitionPercentages = "";
		String cuadraticPartitionPercentages = "";
		double totalPercentage = 0.0;
		String relation = "";	
		double ecmTotal = 0.0;

                
						
		if(experimentType == CLASSIFICATION)
			this.calculateClasses();


                String modelContents="";

                //read model
                if(listPathFilesExtra.size()>0){
                    modelContents+="\n\n===================================\n Model generated \n===================================\n";
                    modelContents+=Files.readFile((String)listPathFilesExtra.get(0));
                }
                else{
                    modelContents+="\n\nThis method does not provide information about its model.\n";
                }

		//for training and test
		for (int p=0; p<2; p++)
		{
			totalInstances = 0;
			totalGoods = 0;
			partitionPercentages = "";
			cuadraticPartitionPercentages = "";
			ecmTotal = 0.0;
			String hoped = "";
			String obtained = "";
			int instancesNumber = 0;
			n_partition = 0;
			ecmBest = 0.0;
			ecmAverage = 0.0;
			ecmDeviation = 0;
			listECMParticion = new ArrayList<Double>();
			
			if (p==0)
				set = "training";
			else
				set = "test";
			
			//for each file
			for (int i=p; i<pathOutputFiles.length; i = i+2)
			{	
				n_partition++;
				try 
				{

					fr = new FileReader(pathOutputFiles[i]);
					br = new BufferedReader(fr);
				} 
				catch (FileNotFoundException e) 
				{			
					e.printStackTrace();
				}
							
				switch (experimentType)
				{
		    		case CLASSIFICATION:		    					    					    					    	
		    			int goods = 0;
		    			String cad = "";
		    			instancesNumber = 0; //>
		    			double partialPercentage = 0.0;
		    			
		    			try 
		    			{
		    				cad = br.readLine();
		    			} 
		    			catch (IOException e) 
		    			{			
		    				e.printStackTrace();
		    			}
		    			while (cad!=null)
		    			{
		    				if (cad.startsWith("@") == false)
		    				{	    					
		    					st = new StringTokenizer(cad);
		    					hoped = st.nextToken();
		    					obtained = st.nextToken();
		    					if (obtained.equals(hoped) == true)
		    					{
		    						goods++;
		    					}
		    					instancesNumber++;
		    					totalInstances++;
		    					
		    					this.calcularConfusion(hoped,obtained);
		    				}
		    				else if (cad.startsWith("@relation")==true)
		    				{
		    					st = new StringTokenizer(cad);
		    					st.nextToken(); //@relation
		    					relation = st.nextToken();
		    				}
		    				
		    				try 
		    				{
		    					cad = br.readLine();
		    				} 
		    				catch (IOException e) 
		    				{			
		    					e.printStackTrace();
		    				}				
		    			}
		    			//Partial percentage partition		    			
		    			partialPercentage = (double)((double)goods/(double)instancesNumber);
		    			partialPercentage = EducationalMethodReport.round(partialPercentage,3);
		    			partitionPercentages = partitionPercentages + n_partition + "\t" + Double.toString(partialPercentage) + "\n";
		    			totalGoods = totalGoods + goods;
		    			break;
		    		
		    		case REGRESSION:		    			
		    			instancesNumber = 0;
		    			String cadAux = "";
		    			double ecmParcial = 0.0;
		    			double hopedValue = 0.0;
		    			double obtainedValue = 0.0;
		    			
		    			try 
		    			{
		    				cadAux = br.readLine();
		    			} 
		    			catch (IOException e) 
		    			{			
		    				e.printStackTrace();
		    			}		    			
		    			while (cadAux!=null)
		    			{
		    				if (cadAux.startsWith("@") == false)
		    				{	    					
		    					st = new StringTokenizer(cadAux);
		    					//row 1 ->obtained
		    					obtained = st.nextToken();
		    					//row 2-> expected or real
		    					hoped = st.nextToken();
		    					hopedValue = Double.valueOf(hoped);
		    					obtainedValue = Double.valueOf(obtained);
		    					double aux = (double)Math.abs((double)hopedValue - (double)obtainedValue);
		    					double partial = (double)Math.pow(aux,2);
		    					ecmParcial = ecmParcial + partial;
		    					ecmTotal = ecmTotal + partial;
		    					instancesNumber++;
		    					totalInstances++;
		    				}
		    				else if (cadAux.startsWith("@relation")==true)
		    				{
		    					st = new StringTokenizer(cadAux);
		    					st.nextToken(); //@relation
		    					relation = st.nextToken();
		    				}		    				
		    				try 
		    				{
		    					cadAux = br.readLine();
		    				} 
		    				catch (IOException e) 
		    				{			
		    					e.printStackTrace();
		    				}				
		    			}
		    			double ecmParticion = 0.0;
		    			ecmParticion = (double)((double)ecmParcial/(double)instancesNumber);
		    			ecmParticion = EducationalMethodReport.round(ecmParticion,3);
		    			listECMParticion.add(Double.valueOf(ecmParticion));
		    			cuadraticPartitionPercentages = cuadraticPartitionPercentages +
    					n_partition + "\t" + Double.toString(ecmParticion) + "\n";
		    			break;
				}
				
				try 
				{
					br.close();
				}
				catch (IOException e) 
				{			
					e.printStackTrace();
				}
			}//for partitions
			
			// All partitions have finished
			switch (experimentType)
			{						
	    		case CLASSIFICATION:		    				    						
						    				    			
	    			totalPercentage = (double)((double)totalGoods / (double)totalInstances);
	    			totalPercentage = EducationalMethodReport.round(totalPercentage,3);
	    			try 
	    			{
	    				if (set.equals("training")==true)
	    				{
	    					bw.write("Relation: " + relation);
	    					bw.newLine();
	    				}
	    				else
	    				{
	    					bw.newLine();
	    				}
	    				
	    				bw.newLine();
	    				bw.write("Set:" + set);
	    				bw.newLine();
	    				
	    				bw.write("Total percentage of successes:");
	    				bw.newLine();
	    				bw.write(Double.toString(totalPercentage));
	    				bw.newLine();
	    				
						bw.write("Percentage of successes in each partition:");
						bw.newLine();
						bw.write(partitionPercentages);
						
						bw.write("Confusion matrix (rows=real class;columns=obtained class):");
						//bw.newLine();
						
						for (int i=0; i<confusionMatrix.length; i++)
						{
							bw.newLine();
							String filaConfusion = "";
							for (int j=0; j<confusionMatrix[0].length; j++)
							{
								filaConfusion = filaConfusion + Integer.toString(confusionMatrix[i][j]) + "\t";
							}						
							bw.write(filaConfusion);							
						}

                                                for (int i=0; i<confusionMatrix.length; i++){
                                                    Arrays.fill(confusionMatrix[i],0);
                                                }
					} 
	    			catch (IOException e) 
	    			{				
						e.printStackTrace();
					}
	    			
	    			
	    			break;
	    			
	    		case REGRESSION:	
	    			
	    			ecmBest = (double)listECMParticion.get(0);
	    			for(int i=0; i<listECMParticion.size(); i++)
	    			{
	    				if((double)listECMParticion.get(i) < ecmBest)
							ecmBest = (double)listECMParticion.get(i);
	    				ecmAverage = (double)(ecmAverage + (double)listECMParticion.get(i));
	    			}	    				    			
	    			ecmAverage = (double)(ecmAverage / n_partition );
	    				    			
	    			for(int i=0; i<listECMParticion.size(); i++)
	    			{
	    				ecmDeviation += Math.pow(((double)listECMParticion.get(i)-(double)ecmAverage), 2);
	    			}
	    			ecmDeviation /= n_partition;
	    			ecmDeviation = Math.sqrt(ecmDeviation);
	    			
	    			ecmBest = EducationalMethodReport.round(ecmBest,3);
	    			ecmAverage = EducationalMethodReport.round(ecmAverage,3);
	    			ecmDeviation = EducationalMethodReport.round(ecmDeviation,3);
	    								
	    			try 
	    			{
	    				bw.newLine();
	    				bw.write("Set:" + set);
	    				bw.newLine();	    					    			
	    				
						bw.write("Partial Mean Squared Error in each partition:");
						bw.newLine();
						bw.write(cuadraticPartitionPercentages);
						//bw.newLine(); "\n" In porcentajesCuadraticosParticiones
						bw.newLine();
						
						bw.write("Best\tMean\tStandar Deviation:");
						bw.newLine();
						bw.write(Double.toString(ecmBest)+"\t"+Double.toString(ecmAverage)+"\t"+
								Double.toString(ecmDeviation));
						bw.newLine();
						
						if(set.equals("test")==true)
						{
							bw.newLine();
							bw.write("------ Experiments Expresions ------\n");
							bw.newLine();
							bw.write("Partial MSE = 1/N*(Sum[(Di-Yi)^2]), where\n" +
									"\"Di\" is desired result in pattern \"i\",\n" +
									"\"Yi\" is obtained result in pattern \"i\",\n" +
									"and \"N\" is number of patterns\n");
							bw.newLine();
							bw.write("Global MSE = sum(MSEi)/n), where\n" +
									"\"MSEi\" is partial MSE for partition \"i\",\n" +
									"and \"n\" is number of partitions\n"); 
							bw.newLine();
							bw.write("Standar Deviation = SQRT(1/n*(Sum[(GMSE-PMSEi)^2])), where\n" +
									"\"GMSE\" is Global MSE,\n" +
									"\"PMSEi\" is Partial MSE in partition \"i\",\n" +
									"and \"n\" is number of partitions\n");
							bw.newLine();
							bw.write("------ Experiments Expresions ------");
						}

					} 
	    			catch (IOException e) 
	    			{				
						e.printStackTrace();
					}
	    			
	    			break;			
			}//switch		
		}//for training and test
		// All partitions have finished

		try 
		{
                        bw.write(modelContents);
			bw.close();
		}
		catch (IOException e) 
		{		
			e.printStackTrace();
		}		
	}

	/**
     * <p>
	 * This method calculate the classes for classification problem
	 * This classes are used for to create confusion matrix
	 * </p>
	 */
	private void calculateClasses()
	{
		String cad = "";
		int index = 0;
		StringTokenizer st = null;

		try 
		{						
			fr = new FileReader(pathOutputFiles[0]);
			br = new BufferedReader(fr);			
			cad = br.readLine();
		} 
		catch (FileNotFoundException e) 
		{			
			e.printStackTrace();
		}
		catch (IOException e) 
		{		
			e.printStackTrace();
		}		
		while (cad!=null)
		{
			if (cad.startsWith("@") == true)
			{					
				if ( ( index = cad.indexOf("{") ) != -1 )
				{
					cad = cad.substring(index+1, cad.length()-1);
					st = new StringTokenizer(cad,",");
					classes = new String [st.countTokens()];
					int i = 0;
					while (st.hasMoreTokens())
					{
						classes[i] = st.nextToken();
						i++;
					}					
					break;
				}
			}					
			try
			{
				cad = br.readLine();
			}
			catch (IOException e) 
			{			
				e.printStackTrace();
			}
		}
		try 
		{
			br.close();
		}
		catch (IOException e) 
		{		
			e.printStackTrace();
		}
		
		confusionMatrix = new int[classes.length][classes.length];
		for(int i=0; i<classes.length; i++)
			for(int j=0; j<classes.length; j++)
				confusionMatrix[i][j] = 0;
		
		//delete spaces, tab
		for(int i=0; i<classes.length; i++)
		{
			StringTokenizer stAux = new StringTokenizer(classes[i]);
			classes[i] = stAux.nextToken();
		}		
	}
	
	/**
     * <p>
	 * This method complete the confusion matrix
	 * </p>
	 */
	private void calcularConfusion(String hoped_, String obtained_)
	{
		int posi = 0;
		int posj = 0;
		
		for (int i=0; i<classes.length; i++)
		{
			if(classes[i].equals(hoped_) == true)
			{
				posi = i;
			}
			if(classes[i].equals(obtained_) == true)
			{
				posj = i;
			}
		}			
		confusionMatrix[posi][posj]++;
	}
	  
}