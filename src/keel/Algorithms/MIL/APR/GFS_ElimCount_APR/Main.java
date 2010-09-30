package keel.Algorithms.MIL.APR.GFS_ElimCount_APR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class Main {
	public static void main(String args[]) {
		
		Properties props = new Properties();

		try {
			InputStream paramsFile = new FileInputStream(args[0]);
			props.load(paramsFile);
			paramsFile.close();			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		
		// Files training and test
		String trainFile;
		String testFile;
		StringTokenizer tokenizer = new StringTokenizer(props.getProperty("inputData"));
		tokenizer.nextToken();
		trainFile = tokenizer.nextToken();
		trainFile = trainFile.substring(1, trainFile.length()-1);
		testFile = tokenizer.nextToken();
		testFile = testFile.substring(1, testFile.length()-1);
		
		tokenizer = new StringTokenizer(props.getProperty("outputData"));
		String reportTrainFile = tokenizer.nextToken();
		reportTrainFile = reportTrainFile.substring(1, reportTrainFile.length()-1);
		String reportTestFile = tokenizer.nextToken();
		reportTestFile = reportTestFile.substring(1, reportTestFile.length()-1);	
		
		try {
			
			GFS_ElimCount_APR algorithm = new GFS_ElimCount_APR();
			
			algorithm.setTrainReportFileName(reportTrainFile);
			algorithm.setTestReportFileName(reportTestFile);
			algorithm.setDatasetSettings(trainFile,testFile);

			algorithm.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}