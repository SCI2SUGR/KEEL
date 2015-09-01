package keel.Algorithms.Preprocess.NoiseFilters.IterativeMCSbFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

import keel.Algorithms.Decision_Trees.C45.C45;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;


public class MultipleClassifierSystem {
	
	public MultipleClassifierSystem(){
	}
	
	public void Evaluate(Instance[] evaluation_set, int k, String testset) throws Exception{		
		
		boolean[] correctlyLabeled_c45 = new boolean[evaluation_set.length];
		boolean[] correctlyLabeled_knn = new boolean[evaluation_set.length];
		boolean[] correctlyLabeled_svm = new boolean[evaluation_set.length];
		boolean[] correctlyLabeled_log = new boolean[evaluation_set.length];
		
		
		// C4.5 -----------------------------------------------------------------
		if(Parameters.use_C45){
			Attributes.clearAll();
			
			if(Parameters.numPartitions != 1)
				CreateConfigFileC45("train"+(k+1)+".dat", testset,"tra_out_c45.dat","test_out_c45.dat","log_out.dat");
			else
				CreateConfigFileC45("test"+(k+1)+".dat", testset,"tra_out_c45.dat","test_out_c45.dat","log_out.dat");
			
			String[] args = new String[1]; args[0] = "config_c45.txt";
			C45.main(args);
			
		     int[] pre = GetPredictions("test_out_c45.dat", evaluation_set.length);
		     for(int i = 0 ; i < evaluation_set.length ; ++i)
		    	 correctlyLabeled_c45[i] = (pre[i] == evaluation_set[i].getOutputNominalValuesInt(0));
		     
		     File fichero = new File("config_c45.txt");fichero.delete();
		     fichero = new File("tra_out_c45.dat");fichero.delete();
		     fichero = new File("test_out_c45.dat");fichero.delete();
		     fichero = new File("log_out.dat");fichero.delete();
		}
		
	     // 1-NN -----------------------------------------------------------------
		if(Parameters.use_KNN){
		     
		     Attributes.clearAll();
		     
		     if(Parameters.numPartitions != 1)
		    	 CreateConfigFileKNN("train"+(k+1)+".dat", testset,"tra_out_knn.dat","test_out_knn.dat","log_out_knn.dat", Parameters.k_value);
		     else
		    	 CreateConfigFileKNN("test"+(k+1)+".dat", testset,"tra_out_knn.dat","test_out_knn.dat","log_out_knn.dat", Parameters.k_value);
		     
		     String[] args = new String[1]; args[0] = "config_knn.txt";
		     keel.Algorithms.Lazy_Learning.KNN.Main.main(args);
		
				
		     int[] pre = GetPredictions("test_out_knn.dat", evaluation_set.length);
		     for(int i = 0 ; i < evaluation_set.length ; ++i)
		    	 correctlyLabeled_knn[i] = (pre[i] == evaluation_set[i].getOutputNominalValuesInt(0));				
		    
		    File fichero = new File("config_knn.txt");fichero.delete(); 
		    fichero = new File("tra_out_knn.dat");fichero.delete();
		    fichero = new File("test_out_knn.dat");fichero.delete();
			fichero = new File("log_out_knn.dat");fichero.delete();  
		}
		
		// SVM -----------------------------------------------------------------
		if(Parameters.use_SVM){
			
			Attributes.clearAll();
			
			if(Parameters.numPartitions != 1)
				CreateConfigFileSVM("train"+(k+1)+".dat", testset,"tra_out_svm.dat","test_out_svm.dat","log_out_svm.dat");
			else
				CreateConfigFileSVM("test"+(k+1)+".dat", testset,"tra_out_svm.dat","test_out_svm.dat","log_out_svm.dat");
			
			String[] args = new String[1]; args[0] = "config_svm.txt";
			keel.Algorithms.SVM.SMO.Main.main(args);
		
				
			int[] pre = GetPredictions("test_out_svm.dat", evaluation_set.length);
			for(int i = 0 ; i < evaluation_set.length ; ++i){
				correctlyLabeled_svm[i] = (pre[i] == evaluation_set[i].getOutputNominalValuesInt(0));
				//Parameters.LOG_OUT.println(correctlyLabeled_svm[i]);
			}		
			
			File fichero = new File("config_svm.txt");fichero.delete();
			fichero = new File("tra_out_svm.dat");fichero.delete();
			fichero = new File("test_out_svm.dat");fichero.delete();
			fichero = new File("log_out_svm.dat");fichero.delete();
		}
		
		
		
	     // LOGISTIC -----------------------------------------------------------------
	    if(Parameters.use_LOG){ 
			
		     Attributes.clearAll();
		     
		     if(Parameters.numPartitions != 1)
		    	 CreateConfigFileLogistic("train"+(k+1)+".dat", testset,"tra_out_log.dat","test_out_log.dat","log_out_log.dat");
		     else
		    	 CreateConfigFileLogistic("test"+(k+1)+".dat", testset,"tra_out_log.dat","test_out_log.dat","log_out_log.dat");
		     
		     String[] args = new String[1]; args[0] = "config_log.txt";
		     keel.Algorithms.Statistical_Classifiers.Logistic.Main.main(args);
		
				
		     int[] pre = GetPredictions("test_out_log.dat", evaluation_set.length);
		     for(int i = 0 ; i < evaluation_set.length ; ++i)
		    	 correctlyLabeled_log[i] = (pre[i] == evaluation_set[i].getOutputNominalValuesInt(0));				
		    
		    File fichero = new File("config_log.txt");fichero.delete(); 
		    fichero = new File("tra_out_log.dat");fichero.delete();
		    fichero = new File("test_out_log.dat");fichero.delete();
			fichero = new File("log_out_log.dat");fichero.delete(); 
	    }
		
		
		//-------- combinacion
		for(int i = 0 ; i < evaluation_set.length ; ++i){
			int contnoise = 0;
			
			if(Parameters.use_C45)
				if(!correctlyLabeled_c45[i])
					contnoise++;
			
			if(Parameters.use_KNN)
				if(!correctlyLabeled_knn[i])
					contnoise++;
			
			if(Parameters.use_SVM)
				if(!correctlyLabeled_svm[i])
					contnoise++;
			
			if(Parameters.use_LOG)
				if(!correctlyLabeled_log[i])
					contnoise++;
		 
			if(contnoise >= ((double) Parameters.NUM_ALGORITHMS/2.0)){
			//if(contnoise == NUM_ALGORITHMS){
				Parameters.correctlyLabeled[k][i] = false;
			}
		}
		

	}
	
	
	static public int[] GetPredictions(String file, int num_ex) throws Exception{
		
		int[] res = new int[num_ex];
		BufferedReader fE = new BufferedReader(new FileReader(file));
		
		String line = "";
		while( !(line = fE.readLine()).equals("@data") );
		
		for(int i = 0 ; i < num_ex ; ++i){
			res[i] = getnv(fE.readLine().split(" ")[1]);
		}
		
		fE.close();
		return res;
	}
	
	static public int getnv(String clase){
		for(int v = 0 ; v < Attributes.getOutputAttribute(0).getNumNominalValues() ; ++v)
			if(Attributes.getOutputAttribute(0).getNominalValue(v).equals(clase))
				return v;
		return -1;
	}
	
	public static void CreateConfigFileC45(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_c45.txt"));
		String content = "";
		
		
		content += 	"algorithm = C4.5 Decision Tree";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\npruned = TRUE";
		content += "\nconfidence = 0.25";
		content += "\ninstancesPerLeaf = 2\n";

		fS.write(content);
		fS.close();
	}
	
	public static void CreateConfigFileKNN(String tra_in, String tst_in, String tra_out, String tst_out, String log_out, int k_value) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_knn.txt"));
		String content = "";
		
		
		content += 	"algorithm = K Nearest Neighbors Classifier";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nK Value = " + Parameters.k_value;
		content += "\ndistance Function = Euclidean";

		fS.write(content);
		fS.close();
	}
	
	
	public static void CreateConfigFileSVM(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_svm.txt"));
		String content = "";
		
		content += 	"algorithm = SMO";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nseed = 1286082570";
		content += "\nC = 100";
		content += "\ntoleranceParameter = 0.001";
		content += "\nepsilon = 1.0E-12";
		content += "\nRBFKernel_gamma = 0.01";
		content += "\n-Normalized-PolyKernel_exponent = 1";
		content += "\n-Normalized-PolyKernel_useLowerOrder = False";
		content += "\nPukKernel_omega = 1.0";
		content += "\nPukKernel_sigma = 1.0";
		content += "\nStringKernel_lambda = 0.5";
		content += "\nStringKernel_subsequenceLength = 3";
		content += "\nStringKernel_maxSubsequenceLength = 9";
		content += "\nStringKernel_normalize = False";
		content += "\nStringKernel_pruning = None";
		content += "\nKERNELtype = Puk";
		content += "\nFitLogisticModels = True";
		content += "\nConvertNominalAttributesToBinary = True";
		content += "\nPreprocessType = Normalize";

		fS.write(content);
		fS.close();
	}	
	
	
	public static void CreateConfigFileLogistic(String tra_in, String tst_in, String tra_out, String tst_out, String log_out) throws Exception{
		
		BufferedWriter fS = new BufferedWriter(new FileWriter("config_log.txt"));
		String content = "";
		
		
		content += 	"algorithm = Multinomial logistic regression model";
		content += "\ninputData = \"" + tra_in + "\" \"" + tra_in + "\" \"" + tst_in + "\"";
		content += "\noutputData = \"" + tra_out + "\" \"" + tst_out + "\" \"" + log_out + "\"";
		
		content += "\n\nRidge = 1e-8";
		content += "\nMaxIter = -1";

		fS.write(content);
		fS.close();
	}

}
