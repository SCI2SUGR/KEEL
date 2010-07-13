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
 * <p>
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 12/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45Rules;


class IncrementalMask {
/**
 * <p>
 * Representation of a mask over a MyDataset.
 * It allows to select a given set of entries without changing the MyDataset.
 * In fact, it acts as a multiplexer over the MyDataset's entries.
 * It also provides a cursor over those elements.
 *
 * The differences over a normal mask is that the IncrementalMask takes into account how
 * many rules covers an exemple. So it is possible to add (or to substract) two
 * IncrementalMasks.
 * </p>
 **/
	
  //this vector indicates the number of rules that cover each entry of the dataset
  private int[] mask; 
  //number of active entries
  private int nactivos; 
  //cursor, always indicates an active entry
  private int index; 

  public IncrementalMask(){}
  /**
   * Constructs a Mask of a given length. By default, it deactives all entries.
   * The cursor is set atBegin (that's a non valid position and it will be necessary a next() to reach the first active position).
   * @param size int the Mask's length
   */
  public IncrementalMask(int size) {
    mask=new int[size];
    nactivos=0;
    for(int i=0;i<size;i++)
      mask[i]=0;
    index=-1;
  }

  /**
   * Constructs a Mask of a given length
   * The cursor is set atBegin (that's a non valid position and it will be necessary a next() to reach  the first active position).
   * @param size int the Mask's length
   * @param initial boolean initial value of the all entries
   */
  public IncrementalMask(int size,int initial) {
    mask=new int[size];
    if (initial!=0) nactivos=size;
    for(int i=0;i<size;i++)
      mask[i]=initial;
    index=-1;
  }

  /* private constructor */
  private IncrementalMask(int[] mask,int nactivos) {
    this.mask=mask;
    this.nactivos=nactivos;
    index=-1;
  }

  /**
   * Returns a copy of this Mask
   * @return a copy of this Mask
   */
  public IncrementalMask copy(){
    int[] replicate=new int[mask.length];
    for (int i=0;i<mask.length;i++)
      replicate[i]=mask[i];
    return new IncrementalMask(replicate,nactivos);
  }

  /**
   * Copies this Mask into another Mask
   * @param replicate Mask a future copy of this Mask
   */
  public void copyTo(IncrementalMask replicate){
    for (int i=0;i<this.mask.length;i++)
      replicate.mask[i]=this.mask[i];
    replicate.nactivos=this.nactivos;
    replicate.index=-1;
  }

  /**
   * Set the value of a given position.
   * @param i int number of entry to active
   * @param value int value of the entry
   */
  public void set(int i,int value){
    if (mask[i]==0 && value!=0)
      nactivos++;
    mask[i]=value;
  }

  /**
   * Set the value of the position pointed by the cursor.
   * @param value int value of the entry
   */
  public void set(int value){
    if (mask[index]==0 && value!=0)
      nactivos++;
    mask[index]=value;
  }

  /**
   * Returns the value of a given position
   * @param i int the position
   * @return the value of a given position
   */
  public int get(int i){
    return mask[i];
  }

  /**
   * Return the number of active entries
   * @return the number of active entries
   */
  public int getnActive(){
    return nactivos;
  }

  /**
   * Advances the cursor to the next active entry.
   * If the cursor reach the end of the mask, it returns false.
   * @return false if the cursor has reached the end of the mask.
   */
  public boolean next(){
    do{
      index++;
    }while (index<mask.length && mask[index]==0);
    return index<mask.length;
  }

  /**
   * Returns the position pointed by the cursors.
   * @return the position pointed by the cursors.
   */
  public int getIndex(){
    return index;
  }

  /**
   * Sets the cursor at atBegin
   * (that's a non valid position and it will be necessary a next() to reach the first active position).
   */
  public void resetIndex(){
    index=-1;
  }

  /**
   * Returns the mask that it's the outcome of the bolean operation 'and' between this and a given mask.
   * @param m Mask the other mask
   * @return (this & m)
   */
  public Mask and(Mask m){
    Mask output=new Mask(this.mask.length);
    for (int i=0;i<this.mask.length;i++){
      output.set(i,(this.mask[i]!=0 && m.isActive(i)));
   }
   return output;
 }

 /**
  * Returns the mask that it's the outcome of the bolean operation 'and' between this and a given mask.
  * @param im IncrementalMask the other mask
  * @return (this & m)
  */
 public Mask and(IncrementalMask im){
   Mask output=new Mask(this.mask.length);
   for (int i=0;i<this.mask.length;i++){
     output.set(i, (this.mask[i] != 0 && im.mask[i] != 0));
   }
   return output;
 }

 /**
  * Returns the mask that it's the outcome of the bolean operation 'or' between this and a given mask.
  * @param m Mask the other mask
  * @return (this | m)
  */
 public Mask or(Mask m){
   Mask output=new Mask(this.mask.length);
   for (int i=0;i<this.mask.length;i++){
     output.set(i,(this.mask[i]!=0 || m.isActive(i)));
   }
   return output;
 }

 /**
  * Returns the mask that it's the outcome of the bolean operation 'or' between this and a given mask.
  * @param im IncremetalMask the other mask
  * @return (this | m)
  */
 public Mask or(IncrementalMask im){
   Mask output=new Mask(this.mask.length);
   for (int i=0;i<this.mask.length;i++){
     output.set(i, (this.mask[i] != 0 || im.mask[i] != 0));
   }
   return output;
 }

 /**
  * Implements the arithmetical addition whith another IncrementalMask
  * @param im IncrementalMask the other mask
  * @return the arithmetical addition with another IncrementalMask
  */
 public IncrementalMask plus(IncrementalMask im){
   IncrementalMask output=new IncrementalMask(this.mask.length);
   output.nactivos=0;
   for (int i=0;i<this.mask.length;i++){
     output.mask[i] = this.mask[i] + im.mask[i];
     if (output.mask[i]!=0)
       output.nactivos++;
   }
   return output;
 }

 /**
  * Implements the arithmetical addition whith another Mask
  * @param m Mask the other mask
  * @return the arithmetical addition with another IncrementalMask
  */
  public IncrementalMask plus(Mask m){
    IncrementalMask output=new IncrementalMask(this.mask.length);
    output.nactivos=0;
    for (int i=0;i<this.mask.length;i++){
      output.mask[i] = this.mask[i];
     if (m.isActive(i)) output.mask[i]++;
     if (output.mask[i]!=0)
       output.nactivos++;
   }
   return output;
  }

  /**
   * Implements the arithmetical addition whith other Masks
   * @param m IncrementalMask the other masks
   * @return the arithmetical addition with all the other Mask
   */
  public IncrementalMask plus(IncrementalMask[] m){
    IncrementalMask output=new IncrementalMask(this.mask.length,0);
    output=output.plus(this);
    for (int i=0;i<m.length;i++){
      output=output.plus(m[i]);
    }
    return output;
  }

  /**
   * Implements the arithmetical addition whith other Masks
   * @param m Mask the other masks
   * @return the arithmetical addition with all the other Mask
   */
  public IncrementalMask plus(Mask[] m){
    IncrementalMask output=new IncrementalMask(this.mask.length,0);
    output=output.plus(this);
    for (int i=0;i<m.length;i++){
      output=output.plus(m[i]);
    }
    return output;
  }

  /**
   * Implements the arithmetical substraction whith another IncrementalMask
   * @param im IncrementalMask the other mask
   * @return the arithmetical substraction with another IncrementalMask
   */
  public IncrementalMask minus(IncrementalMask im){
    IncrementalMask output=new IncrementalMask(this.mask.length);
    output.nactivos=0;
    for (int i=0;i<this.mask.length;i++){
      output.mask[i] = this.mask[i] - im.mask[i];
      if (output.mask[i]!=0)
       output.nactivos++;
   }
   return output;
 }

 /**
  * Implements the arithmetical substraction whith another IncrementalMask
  * @param m Mask the other mask
  * @return the arithmetical substraction with another IncrementalMask
  */
 public IncrementalMask minus(Mask m){
   IncrementalMask output=new IncrementalMask(this.mask.length);
   output.nactivos=0;
   for (int i=0;i<this.mask.length;i++){
     output.mask[i] = this.mask[i];
     if (m.isActive(i)) output.mask[i]--;
     if (output.mask[i]!=0)
       output.nactivos++;
   }
   return output;
 }

}
