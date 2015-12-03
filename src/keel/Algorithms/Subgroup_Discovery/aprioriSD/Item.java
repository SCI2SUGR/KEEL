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

package keel.Algorithms.Subgroup_Discovery.aprioriSD;

/**
 * <p>Title: Item</p>
 * Implements an item of a dataset, with their values and their attributes/columns references.
 *
 * <p>Copyright: Alberto Copyright (c) 2006</p>
 *
 * <p>Company: Mi Casa</p>
 *
 * @author Alberto
 * @version 1.0
 */
public class Item {
    int [] item; //valores de los items
    int [] columnas; //columna a la que pertenece cada item
    int support; //support para el item

    /**
     * Default constructor.
     */
    public Item() {
    }

    /**
     * Creates an item with k values but empty.
     * @param k int Número de values.
     */
    public Item(int k){
        item = new int[k];
        columnas = new int[k];
    }

    /**
     * Creates an item with one value.
     * @param valor int the given value
     * @param columna int index of the attribute/column
     * @param support int support value for the item.
     */
    public Item(int valor, int columna,int support){
        item = new int[1];
        item[0] = valor;
        columnas = new int[1];
        columnas[0] = columna;
        this.support = support;
    }

    /**
     * Creates a new item (if possible) of k+1 size from two k items.
     * @param uno Item first ITEM
     * @param dos Item second ITEM
     * @return True if it has been created, false otherwise.
     */
    public boolean creaItem(Item uno, Item dos){
        int [] item1 = uno.getItem();
        int [] columnas1 = uno.getColumnas();
        int [] item2 = dos.getItem();
        int [] columnas2 = dos.getColumnas();
        int k = item1.length;

        item = new int[k+1];
        columnas = new int[k+1];

        boolean seguir = true;
        for (int l = 0; (l < k-1) && seguir; l++) {
            if (item1[l] == item2[l]) {
                item[l] = item1[l];
                columnas[l] = columnas1[l];
            } else {
                seguir = false;
            }
        }
        if (seguir) { //hay que insertar
            item[k -1] = item1[k - 1];
            columnas[k - 1] = columnas1[k-1];
            item[k] = item2[k - 1];
            columnas[k] = columnas2[k-1];
        }
        return seguir;
    }

    /**
     * Returns a copy of the values of the item.
     * @return a copy of the values of the item.
     */
    public int [] getItem(){
        int tam = this.item.length;
        int [] aux = new int[tam];
        for (int i = 0; i < tam; i++){
            aux[i] = item[i];
        }
        return aux;
    }

    /**
     * Returns a copy of the columns references for each value.
     * @return int[] a copy of the columns references for each value.
     */
    public int [] getColumnas(){
        int tam = this.columnas.length;
        int [] aux = new int[tam];
        for (int i = 0; i < tam; i++){
            aux[i] = columnas[i];
        }
        return aux;
    }

    /**
     * Sets the support value for the item with the one given.
     * @param support int the value to be set.
     */
    public void setSupport(int support){
        this.support = support;
    }

    /**
     * Returns the support value for the item.
     * @return the support value for the item.
     */
    public int getSupport(){
        return support;
    }

    /**
     * Prints on the standard output the item information.
     */
    public void print(){
        System.out.print("(");
        for (int j = 0; j < (item.length)-1; j++) {
            System.out.print(item[j] + ",");
        }
        System.out.println(item[item.length-1]+"); S:" +support);
    }
}

