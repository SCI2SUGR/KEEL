package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class SInstances extends Instances {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5106817083345264343L;

	public SInstances(Reader reader) throws IOException {
		super(reader);
		// TODO Auto-generated constructor stub
	}

	public SInstances(Reader reader, int capacity) throws IOException {
		super(reader, capacity);
		// TODO Auto-generated constructor stub
	}

	public SInstances(Instances dataset) {
		super(dataset);
		// TODO Auto-generated constructor stub
	}

	public SInstances(Instances dataset, int capacity) {
		super(dataset, capacity);
		// TODO Auto-generated constructor stub
	}

	public SInstances(Instances source, int first, int toCopy) {
		super(source, first, toCopy);
		// TODO Auto-generated constructor stub
	}

	public SInstances(String name, FastVector attInfo, int capacity) {
		super(name, attInfo, capacity);
		// TODO Auto-generated constructor stub
	}

	public Instances[] byClasses(){
		Instances[] ins = new Instances[numClasses()];
		for (int i = 0; i < ins.length; i++){
			ins[i] = new Instances(this,0);
			for (int j = 0; j < numInstances(); j++){
				if (instance(j).classValue() == i){
					ins[i].add(instance(j));
				}
			}
		}
		return ins;
	}
	
	public static void main(String[] args){
		SInstances i;
		try {
			i = new SInstances(new FileReader(new File("data\\iris.arff")));
			i.setClassIndex(i.numAttributes()-1);
			Instances[] i2 = i.byClasses();
			System.out.print(i2.length);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
