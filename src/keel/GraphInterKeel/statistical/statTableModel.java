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

/**
 * File: statTableModel.java.
 *
 * Specific table model for the data table
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @author Modified by Joaquin Derrac (University of Granada) 1/12/2010
 * @version 1.0
 * @since JDK1.5
*/

package keel.GraphInterKeel.statistical;

import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.table.DefaultTableModel;

public class statTableModel extends DefaultTableModel{

    private int numColumns;
    private int rows;
    private String columnNames[];
    private Class classes[];
    private Object values [][];

    private String separator;

    private static final int CORRECT = 0;
    private static final int VOID = 1;
    private static final int INCORRECT = 2;

    /**
     * Initializes the table model
     */
    public void initComponents(){

        numColumns=7;
        rows=10;

        resetData();
    }//end-method

    /**
     * Gets the number of columns of the table
     *
     * @return Number of columns of the table
     */
    @Override
    public int getColumnCount() {
        return numColumns;
    }//end-method

    /**
     * Gets the number of rows of the table
     *
     * @return Number of rows of the table
     */
    @Override
    public int getRowCount() {
        return rows;
    }//end-method

    /**
     * Get the name of a column
     *
     * @param col Column to inspect
     *
     * @return Name of the column
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }//end-method

    /**
     * Gets the value of a cell
     *
     * @param row Row of the cell
     * @param col Column of the cell
     *
     * @return Value of the cell
     */
    @Override
    public Object getValueAt(int row, int col) {
        return values[row][col];
    }//end-method

    /**
     * Get the class of a column
     *
     * @param c Column to inspect
     *
     * @return Class of the column
     */
    @Override
    public Class getColumnClass(int c) {
        return classes[c];
    }//end-method

    /**
     * Tets if a cell is editable
     *
     * @param row Row of the cell
     * @param col Column of the cell
     *
     * @return True if the cell is editable. False, if not
     */
    @Override
    public boolean isCellEditable(int row, int col) {

        return true;
        
    }//end-method

    /**
     * Sets the value of a cell
     *
     * @param value Value to set
     * @param row Row of the cell
     * @param col Column of the cell
     *
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        values[row][col] = value;
        fireTableCellUpdated(row, col);
    }//end-method

    /**
     * Resizes a table
     *
     * @param newData New number of data sets
     * @param newMethod New number of methods
     */
    public void resizeTable(int newData, int newMethod){

        numColumns=newMethod+1;
        rows=newData;

        resetData();
    }//end-method

    /**
     * Resets data of the table to defaukt values
     */
    public void resetData(){

        values=new Object[rows][numColumns];
        
        for(int i=0;i<rows;i++){

            values[i][0]= "Data set "+(i+1);
            
            for(int j=1; j<numColumns; j++){
                 values[i][j]=(double)0;
            }
        }

        columnNames=new String [numColumns];
        columnNames[0]="Data sets";

        for(int i=1;i<numColumns;i++){
            columnNames[i]="Algorithm "+i;
        }

        classes= new Class [numColumns];
        classes[0]=String.class;

        for(int i=1; i <numColumns; i++){
            classes[i]=Double.class;
        }

        this.fireTableStructureChanged();
        
    }//end-method

    /**
     * Exports the contents of the table in CSV format
     *
     * @return String with the contents of the table in CSV format
     */
    public String generateCSVOutput(){

        String output="";

        //First line: Algorithms names
        for(int i=0;i<numColumns-1;i++){
            output+=columnNames[i]+",";
        }
        output+=columnNames[numColumns-1]+"\n";

        //Rest of the data

        DecimalFormat formatter;

        formatter = (DecimalFormat)DecimalFormat.getNumberInstance();

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(10);

        for(int i=0; i<rows;i++){

            output+=values[i][0]+",";
            
            for(int j=1;j<numColumns-1;j++){
                output+=formatter.format(values[i][j])+",";
            }
            output+=formatter.format(values[i][numColumns-1])+"\n";
        }

        output+="\n";
        
        return output;
    }//end-method

    /**
     * Load the contents of the table in CSV format
     *
     * @param contents String with the contents of the table in CSV format
     */
    public int loadCSVData(String contents){

        StringTokenizer fileLines;
        StringTokenizer lineWords;
        String line,element;
        String filtered;
        int errorCode=0;
        int analize;
        int counter,counter2;
        boolean badFormat=false;

        int newAlg;
        int newData;

        scanSeparator(contents);

        contents=" "+contents;

        fileLines = new StringTokenizer (contents,"\n\r");

        line=fileLines.nextToken();

        lineWords=new StringTokenizer (line, separator);

        newAlg=lineWords.countTokens()-1;

        newData=0;

        filtered="";
        while(fileLines.hasMoreTokens()&&!badFormat){

            line=fileLines.nextToken();

            analize=analizeLine(line,newAlg);

            switch(analize){

                case CORRECT:
                    newData++;
                    filtered+=line+"\n";
                    break;

                case VOID:
                    break;

                case INCORRECT:
                    
                    badFormat=true;
                    break;
            }
        }

        if(badFormat){
            errorCode=1;
        }

        if(newData<StatisticalF.MINDATA){
            errorCode=3;
        }

        if(newAlg<StatisticalF.MINALG){
            errorCode=2;
        }

        if(newData>StatisticalF.MAXDATA){
            errorCode=5;
        }

        if(newAlg>StatisticalF.MAXALG){
            errorCode=4;
        }

        if(errorCode==0){

            numColumns=newAlg+1;
            rows=newData;

            resetData();

            fileLines = new StringTokenizer (contents,"\n\r");

            line=fileLines.nextToken();

            lineWords=new StringTokenizer (line, separator);

            counter=0;
            while(lineWords.hasMoreTokens()){
                element=lineWords.nextToken();
                columnNames[counter]=element;
                counter++;
            }

            fileLines = new StringTokenizer (filtered,"\n\r");

            counter2=0;
            while(fileLines.hasMoreTokens()){
                line=fileLines.nextToken();
                lineWords=new StringTokenizer (line, separator);

                counter=0;

                while(lineWords.hasMoreTokens()){
                    element=lineWords.nextToken();
                    if(counter==0){
                        values[counter2][counter]=element;
                    }
                    else{
                        values[counter2][counter]=Double.parseDouble(element);
                    }
                    
                    counter++;
                }
                counter2++;
            }
        }

        this.fireTableStructureChanged();

        return errorCode;
    }//end-method

    /**
     * Tries to find which separator for CSV ("," or ";") have been used
     *
     * @param contents Data to scan
     */
    private void scanSeparator(String contents){

        int numComma=0;
        int numDotComma=0;

        for(int i=0;i<contents.length();i++){
            if(contents.charAt(i)==','){
                numComma++;
            }
            if(contents.charAt(i)==';'){
                numDotComma++;
            }
        }

        if(numDotComma>numComma){
            separator=";";
        }
        else{
            separator=",";
        }

    }//end-method

    /**
     * Test if a line is correct
     *
     * @param line Contents of the line
     * @param nAlg Number of algorithms of the table
     *
     * @return 0 if the line is correct, 1 if it is void, 2 if it is incorrect
     */
    private int analizeLine(String line, int nAlg){

        int elements;
        StringTokenizer lineWords;
        String element;

        lineWords=new StringTokenizer (line, separator);

        elements=lineWords.countTokens()-1;

        if(elements==1){
            return 1;
        }
        else{
            if(elements!=nAlg){
                return 2;
            }
            lineWords.nextToken();
            while(lineWords.hasMoreTokens()){

                element=lineWords.nextToken();

                try{
                    Double.parseDouble(element.substring(0, element.length()));
                }catch(Exception e){
                    return 2;
                }
            }
        }

        return 0;
    }//end-method
    
}//end-class

