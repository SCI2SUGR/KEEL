/**
 *
 * File: runkeeltxt.java
 *
 * Class to process the execution of a experiment. TXT version
 *
 * @author Joaquin Derrac (University of Granada) 15/6/2009
 * @version 1.0
 * @since JDK1.5
 *
 */
package keel.RunKeelTxt;

import java.io.*;
import java.util.Vector;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import org.jdom.*;
import org.jdom.input.*;


public class runkeeltxt {

    /**
     * Main method. Executes a experiment.
     * @param args Arguments of the experiment.
     */
    public static void main(String[] args) {
        new ExecuteAll();
    }
}

class ExecuteAll {

    int step = 0;
    Execute exe;

    /**
     * Default buider
     */
    public ExecuteAll() {

        try {
            exe = new Execute("Executable");
            exe.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class StreamGobbler extends Thread {

        InputStream is;
        String type;

        /**
         * Builder
         * @param is A input stream
         * @param type Type of strem
         */
        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }
        /**
         * Run method of the thread
         */
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                isr.close();
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    class Execute extends Thread {

        Process pr;

        /**
         * Default buider
         * @param name Name of the thread
         */
        public Execute(String name) {
            super(name);
        }

        /**
         * Run method of the thread
         */
        @Override
        public void run() {

            Document doc = new Document();

            try {
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(new File("RunKeel.xml"));
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Execution XML file not found");
                return;
            }

            boolean para = false;
            List sentencias = doc.getRootElement().getChildren();
            for (int i = 0; i < sentencias.size() && !para; i++) {
                String comando = "";
                List linea = ((Element) sentencias.get(i)).getChildren();
                for (int j = 0; j < linea.size(); j++) {
                    comando += ((Element) linea.get(j)).getText() + " ";
                }

                Date now = Calendar.getInstance().getTime();
                System.out.println("*** BEGIN OF EXPERIMENT " + now + "\n");
                System.out.print("\nExecuting: " + comando);

                StreamGobbler errorGobbler;
                StreamGobbler outputGobbler;
                Runtime rt;
                Process proc;

                try {
                    rt = Runtime.getRuntime();
                    proc = rt.exec(comando);
                    errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
                    outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                    errorGobbler.start();
                    outputGobbler.start();
                    int exitVal = proc.waitFor();
                    now = Calendar.getInstance().getTime();
                    System.out.println("ExitValue: " + exitVal);
                    if (exitVal != 0) {
                        System.out.println("\n*** END OF EXPERIMENT " + now + "\n");
                        para = true;
                    } else {
                        now = Calendar.getInstance().getTime();
                        System.out.println("\n*** END OF EXPERIMENT " + now + "\n");
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }
            if (!para) {
                System.out.println("Experiment completed successfully");
            }

        }
    }

    /**
     * List the content of a directory (Not used, only for future use)
     * @param directory
     * @param result
     */
    private void listDirectory(String directory, Vector result) {

        File file = new File(directory);
        File listado[] = file.listFiles();

        System.out.println("Processing directory [" + directory + "]");
        if (listado == null) {

            System.out.println("Invalid dir [" + directory + "]");
            return;

        }

        System.out.println("Processing " + listado.length + " files");
        for (int i = 0; i < listado.length; i++) {
            System.out.println("processing " + listado[i].getName());
            if (listado[i].isFile()) {
                result.add(new String(directory + "/" + listado[i].getName()));
            } else {
                listDirectory(directory + "/" + listado[i].getName(), result);
            }
        }
    }

}
