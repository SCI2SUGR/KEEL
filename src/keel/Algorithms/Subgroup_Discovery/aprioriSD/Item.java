package keel.Algorithms.Subgroup_Discovery.aprioriSD;

/**
 * <p>Titulo: Item</p>
 *
 * <p>Descripcion: Clase para guardar los Items (valor y posicion de cada valor)</p>
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
     * Constructor por defecto (no hace nada)
     */
    public Item() {
    }

    /**
     * Crea un K-item vacío
     * @param k int Número de items en este conjunto
     */
    public Item(int k){
        item = new int[k];
        columnas = new int[k];
    }

    /**
     * Crea un 1-item
     * @param valor int valor del item
     * @param columna int columna en la que está dicho valor
     * @param support int Valor para el support de este item
     */
    public Item(int valor, int columna,int support){
        item = new int[1];
        item[0] = valor;
        columnas = new int[1];
        columnas[0] = columna;
        this.support = support;
    }

    /**
     * Crea un nuevo Item (si es posible) de tamaño k+1 de dos items de tamaño k
     * @param uno Item Primer ITEM
     * @param dos Item Segundo ITEM
     * @return True si se crea el Item, false en otro caso
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
     * Devuelve una copia de los valores del item
     * @return int[] El array de valores
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
     * Devuelve una copia de las columnas de cada valor del item
     * @return int[] El array de columnas
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
     * Añade el valor de support para este item concreto
     * @param support int El valor S
     */
    public void setSupport(int support){
        this.support = support;
    }

    /**
     * Devuelve el valor de support para este item concreto
     * @return double El valor S
     */
    public int getSupport(){
        return support;
    }

    public void print(){
        System.out.print("(");
        for (int j = 0; j < (item.length)-1; j++) {
            System.out.print(item[j] + ",");
        }
        System.out.println(item[item.length-1]+"); S:" +support);
    }
}
