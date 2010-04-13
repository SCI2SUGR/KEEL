/*
 * Parser.java
 *
 * Created on 24 de enero de 2005, 10:43
 */

package keel.Dataset;
import java.io.*;
import java.util.*;

/**
 * <p>
 * <b> InstanceParser </b>
 * </p>
 * This class is a parser for the instances. It reads an instance (a line
 * from the data file) and returns it. It also mantain some information as
 * the relation name.
 *
 * @author  Albert Orriols Puig
 * @version keel0.1
 *
 */
public class InstanceParser{
    
/////////////////////////////////////////////////////////////////////////////
////////////////// ATTRIBUTES OF THE PARSER CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////
    
/**
 * A Buffered Reader to the DB input file.
 */
  private BufferedReader br;

/**
 * A flag indicating if the DB is a train or a test DB. The difference between
 * them is that a test DB doesn't modify any parameter definition.
 */
  private boolean isTrain;
  
/**
 * It counts the attribute number.
 */
  private int attributeCount;
  
/**
 * String where de file header is stored
 */
  private String header;
  
/**
 * String where the relation name is stored
 */
  private String relation;
  
/**
 * Counter of the line
 */
  static int lineCounter;
  
/////////////////////////////////////////////////////////////////////////////
/////////////////// METHODS OF THE PARSER CLASS /////////////////////////////
/////////////////////////////////////////////////////////////////////////////

/** 
 * It does create a new instance of ParserARFF.
 * @param fileName is the file name of the DB file.
 * @param _isTrain is a flag that indicates if the DB is for a train. 
 */
  public InstanceParser( String fileName, boolean _isTrain ) {
    try {
        br=new BufferedReader(new FileReader(fileName));
        lineCounter = 0;
    } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }
    isTrain=_isTrain;
    attributeCount=0;
  }//end of Parser constructor

  
  
  
/**
 * It returns all the header read in parseHeader.
 * @return a string with the header information.
 */  
  public String getHeader() {
    return header;
  }//end getHeader



/**
 * It returns the relation name
 * @return a string with the relation name.
 */
  public String getRelation() {
    return relation;
  } //end getRelation

    
/**
 * It returns an instance
 * @return an string with the instance.
 */
  public String getInstance() {
        return getLine();
  }//end getInstance


/**
 * It returns the number of attributes
 * @return an integer with the number of attributes.
 */
  public int getAttributeNum(){
    return attributeCount;
  }

  
  
/**
 * This method reads one valid line of the file. So, it ingores the comments, 
 * and empty lines.
 * @return a string with the new line read.
 */
  public String getLine() {
        String st=null;
        do {
                try {
                        st=br.readLine();
                        lineCounter++;
                } catch(Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                }
        } while(st!=null && (st.startsWith("%") || st.equals("")));
        return st;
  }//end getLine
    
}//end of Parser class
