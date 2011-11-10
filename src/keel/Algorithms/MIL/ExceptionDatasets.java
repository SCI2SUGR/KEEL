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

package keel.Algorithms.MIL;

import java.io.IOException;

public class ExceptionDatasets{ 
   
    
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet m_data;
	 protected Exception m_FailReason = null;
    
    /**
     * initializes the capability with the given flags
     * 
     */
    public ExceptionDatasets(String nameFile) throws IOException {
    
    	m_data =  new org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet();
		m_data.setFileName(nameFile);
		m_data.open();
    }
    
  
        
    public org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet getDataset(){
    	return m_data;
    }
    
    public void setDataset(org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet m_data){
    	this.m_data = m_data;
    }
    
    public boolean checkDataset(){
    	
    	// Controlamos que el primer atributo sea string 
          if (m_data.getMetadata().getAttribute(m_data.getMetadata().numberOfAttributes()-1).getType() != org.ayrna.jclec.util.dataset.AttributeType.Categorical) {
        	  m_FailReason = new Exception (createMessage("Incorrect Multi-Instance format, the first attribute must be categorical!"));
            return false;
          }
         
          if(m_data.getMetadata().numberOfAttributes() < 3){
         	  m_FailReason = new Exception(createMessage("Incorrect Multi-Instance format, the number of attribut must be at least three!"));
       	
         	  return false;
          }
          return true;
        
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
