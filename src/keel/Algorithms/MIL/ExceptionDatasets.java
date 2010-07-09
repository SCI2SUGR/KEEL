/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * Capabilities.java
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package keel.Algorithms.MIL;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.WekaException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * A class that describes the capabilites (e.g., handling certain types of
 * attributes, missing values, types of classes, etc.) of a specific
 * classifier. By default, the classifier is capable of nothing. This
 * ensures that new features have to be enabled explicitly. <p/>
 * 
 * A common code fragment for making use of the capabilities in a classifier 
 * would be this:
 * <pre>
 * public void <b>buildClassifier</b>(Instances instances) throws Exception {
 *   // can the classifier handle the data?
 *   getCapabilities().<b>testWithFail(instances)</b>;
 *   ...
 *   // possible deletion of instances with missing class labels, etc.
 * </pre>
 * For only testing a single attribute, use this:
 * <pre>
 *   ...
 *   Attribute att = instances.attribute(0);
 *   getCapabilities().<b>testWithFail(att)</b>;
 *   ...
 * </pre>
 * Or for testing the class attribute (uses the capabilities that are 
 * especially for the class):
 * <pre>
 *   ...
 *   Attribute att = instances.classAttribute();
 *   getCapabilities().<b>testWithFail(att, <i>true</i>)</b>;
 *   ...
 * </pre>
 * 
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.3 $
 */
public class ExceptionDatasets{ 
   
    
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Instances m_data;
	 protected Exception m_FailReason = null;
    
    /**
     * initializes the capability with the given flags
     * 
     * @param flags	"meta-data" for the capability
     * @param display	the display string (must be unique!)
     */
    public ExceptionDatasets(String nameFile) {
    
    	try {
			m_data =  new Instances(new FileReader(nameFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
  
    private ExceptionDatasets() {
        
    }
    
    public Instances getDataset(){
    	return m_data;
    }
    
    public void setDataset(Instances m_data){
    	this.m_data = m_data;
    }
    
    public boolean checkDataset(){
    	
    	// Multi-Instance? -> check structure (regardless of attribute range!)
        //if (handles(Capability.ONLY_MULTIINSTANCE)) {
          // number of attributes?
          if (m_data.numAttributes() != 3) {
        	  m_FailReason = new WekaException(
                                createMessage("Incorrect Multi-Instance format, must be 'bag-id, bag, class'!"));
            return false;
          }
          m_data.setClassIndex(m_data.numAttributes() - 1);
          
          // type of attributes and position of class?
          if (    !m_data.attribute(0).isNominal() 
               || !m_data.attribute(1).isRelationValued() 
               || (m_data.classIndex() != m_data.numAttributes() - 1) ) {
            m_FailReason = new WekaException(
                createMessage("Incorrect Multi-Instance format, must be 'NOMINAL att, RELATIONAL att, CLASS att'!"));
            return false;
          }

          return true;
          // check data immediately
       //MultiInstanceCapabilitiesHandler handler = new MultiInstanceCapabilitiesHandler();
    	//cap = handler.getMultiInstanceCapabilities();
    	//boolean result;
    	//if (m_data.numInstances() > 0)
    	 // result = cap.test(data.attribute(1).relation(0));
    	//else
    	 // result = cap.test(data.attribute(1).relation());
    	
    	//if (!result) {
    	 // m_FailReason = cap.m_FailReason;
    	 // return false;
    	//}
         // }
        //}
    }
    
    public void testDataset() throws Exception {
        if (!checkDataset())
          throw m_FailReason;
      }
    
    protected String createMessage(String msg) {
        String	result;
        
        result = "";
        
        result += ": " + msg;
        
        return result;
      }
    
   
 
}
