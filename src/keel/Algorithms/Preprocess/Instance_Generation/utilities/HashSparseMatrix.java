/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Preprocess.Instance_Generation.utilities;

import keel.Algorithms.Preprocess.Instance_Generation.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.KNN.*;
import org.core.*;
import java.util.*;

class Element<IndexType,ElementType>
{
    IndexType i;
    IndexType j;
    ElementType e;
    
    Element(IndexType i, IndexType j, ElementType e)
    {
        this.i = i;
        this.j = j;
        this.e = e;
    }
}

/**
 *
 * @author diegoj
 */
public class HashSparseMatrix<IndexType, ElementType>
{
   /* 
    private HashMap<IndexType, HashMap<IndexType, Element<IndexType,ElementType>>> matrix;
            
    public HashSparseMatrix()
    {
        matrix = new HashMap<IndexType,HashMap<IndexType, ElementType>>();
        invertedHash = new HashMap<IndexType, IndexType>();
    }
    
    protected boolean isRow(IndexType k)
    {
        return matrix.containsKey(k);
    }
    
    protected boolean isColumn(IndexType k)
    {
        return !matrix.containsKey(k) && invertedHash.containsKey(k);
    }
    
    protected boolean isRight(IndexType i, IndexType j)
    {
        return (isRow(i) && isColumn(j));
    }
    
    protected boolean isInverted(IndexType i, IndexType j)
    {
        return (isColumn(i) && isRow(j));
    }
    
    public boolean contains(IndexType i, IndexType j)
    {
        return isRight(i,j) || isInverted(i,j);
    }
    
    public ElementType get(IndexType i, IndexType j)
    {
        //Debug.endsIf(!contains(i,j), "No los contiene");
        if(isRight(i,j))
            return matrix.get(i).get(j);
        else if(isInverted(i,j))
            return matrix.get(j).get(i);
        else
            //esto ha petao
        return null;
    }
    
    public void set(IndexType i, IndexType j, ElementType e)
    {
        if(isRight(i,j))
           matrix.get(i).put(j,e);
        else if(isInverted(i,j))
            matrix.get(j).put(i, e);
        else//Element (i,j) doesn't exist
        {   //pero puede existir la fila/columna i o la fila/columna j
            if(isRow(i))
            {
                HashMap<IndexType, ElementType> row = matrix.get(i);
                row.put(j, e);
                matrix.put(i,row);
                invertedHash.put(j, i);
            }
            else if(isColumn(i))
            {
                HashMap<IndexType, ElementType> row = matrix.get(Inveri);
                row.put(j, e);
                matrix.put(i,row);
                invertedHash.put(j, i);
            }
            else if(isRow(j))
            {
                HashMap<IndexType, ElementType> row = matrix.get(i);
                row.put(j, e);            
            }
            else
            {//ni i ni j existen
                HashMap<IndexType, ElementType> row = new HashMap<IndexType, ElementType>();
                row.put(j, e);
                matrix.put(i,row);
                invertedHash.put(j, i);           
            }
        }
    }
    
    public void addElementAt(ElementType e, IndexType i, IndexType j)
    {
        set(i, j, e);
    }
    
    public void add(IndexType i, IndexType j, ElementType e)
    {
        set(i, j, e);
    }
    
    /*public boolean remove(IndexType i, IndexType j)
    {
        boolean erased = true;
        if(isRight(i,j))        
            matrix.get(i).remove(j);         
        else if(isInverted(i,j))
            matrix.get(j).remove(i);
        else
            erased = false;
        return erased;        
    }*/
    /*
    public boolean remove(IndexType i)
    {
        boolean erased = false;
        if(isRow(i))
        {
            HashMap<IndexType,ElementType> rowInSet = matrix.get(i);
            ArrayList<IndexType> row = new ArrayList<IndexType>(rowInSet.keySet());
            for(IndexType index : row)
                invertedHash.remove(index);                
            matrix.remove(i);
            erased = true;
        }
        else if(isColumn(i))
        {
            IndexType rowI = invertedHash.get(i);
            if(!matrix.containsKey(rowI))
                Debug.goout("PEto");
            if(!matrix.get(rowI).containsKey(i))
                Debug.goout("No pertenece");
            matrix.get(rowI).remove(i);
            invertedHash.remove(i);
            erased = true;
        }
        return erased;
    }
    
    public int size()
    {
        return matrix.keySet().size();
    }*/
}
