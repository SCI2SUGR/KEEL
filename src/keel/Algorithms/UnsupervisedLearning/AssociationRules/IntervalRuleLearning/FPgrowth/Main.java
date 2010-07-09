package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

public class Main {
	/**
	 * <p>
	 * It reads the configuration file (data-set files and parameters) and launch the algorithm
	 * </p>
	 */

    private parseParameters parameters;

    /** Default Constructor */
    public Main() {
    }

    /**
     * It launches the algorithm
     * @param confFile String it is the filename of the configuration file.
     */
    private void execute(String confFile) {
        parameters = new parseParameters();
        parameters.parseConfigurationFile(confFile);
        FPgrowth method = new FPgrowth(parameters);
        method.execute();
    }

    /**
     * Main Program
     * @param args It contains the name of the configuration file<br/>
     * Format:<br/>
     * <em>algorith = &lt;algorithm name></em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;validation file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = value</em> (if used)<br/>
     * <em>&lt;Parameter1&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;Parameter2&gt; = &lt;value2&gt;</em> ... <br/>
     */
    public static void main(String args[]) {
        Main program = new Main();
        System.out.println("Executing Algorithm.");
        
        StopWatch sw = new StopWatch();
        sw.start();
        
        program.execute(args[0]);
        
        sw.stop();
        sw.print();
    }
}
