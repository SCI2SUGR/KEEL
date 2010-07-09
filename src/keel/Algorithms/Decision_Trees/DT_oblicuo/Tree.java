package keel.Algorithms.Decision_Trees.DT_oblicuo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Tree {

  Tree hijoD, hijoI; //los dos hijos (arbol binario)
  Tree padre; //mi padre
  Nodo nodo; //informacion relevante (<at,op,valor> ó clase>
  boolean marcado; //para el recuento
  myDataset train;
  int indiceNodoT, indiceNodo;

  /** Number of Leafs in the tree */
  public static int nodosT = 0, nodos = 0;
  public static int maxNodos = 100;

  /** Number of examples for each leaf/class **/
  public static int ejemplos[][];

  public Tree() {
  }

  public Tree(Tree pae, myDataset train, int n_ejemplos, int [] ejemplos, int nGenerations) {
    this.train = train;
    padre = pae;
    //System.err.println("Creando nodo a partir de "+n_ejemplos);
    nodo = new Nodo(train,n_ejemplos,ejemplos,nGenerations);
    //System.err.println("Total nodos: "+nodos+"/"+nodosT);
    if (!nodo.isLeaf){
      //Calculo ejemplosI y ejemplosD
      indiceNodo = nodos;
      nodos++;
      hijoI = hijoD = null;
      if (nodo.n_ejemplos_i > 0){
        hijoI = new Tree(this, train, nodo.n_ejemplos_i, nodo.ejemplosI.clone(),
                         nGenerations);
      }
      if (nodo.n_ejemplos_d > 0){
        hijoD = new Tree(this, train, nodo.n_ejemplos_d, nodo.ejemplosD.clone(),
                         nGenerations);
      }
    }else{
      indiceNodoT = nodosT;
      nodosT++;
    }
  }

  public Tree copia(Tree padre) {
    Tree t = new Tree();
    t.padre = padre;
    t.nodo = nodo.copia();
    t.train = this.train;
    t.indiceNodo = this.indiceNodo;
    if (!nodo.isLeaf) {
      t.hijoD = this.hijoD.copia(t);
      t.hijoI = this.hijoI.copia(t);
    }
    return t;
  }

  public String printString() {
    StringBuffer text = new StringBuffer();
    printTree(0, text);
    return text.toString();
  }

  /** Function to print the tree.
   *
   * @param depth			Depth of the node in the tree.
   * @param text			The tree.
   *
   */
  private void printTree(int depth, StringBuffer text) {
    String aux = "";
    String aux2 = "";

    for (int k = 0; k < depth-1; k++) {
      aux += "\t";
    }
    for (int k = 0; k < depth; k++) {
      aux2 += "\t";
    }

    text.append(aux2);
    if (nodo.isLeaf) {
      //text.append(nodo.printString() + " ["+indiceNodoT+"]\n");
      text.append(nodo.printString() + " \n");
    }
    else if (nodo != null){
      /*text.append("[" + indiceNodo + "/" + marcado + "] if ( " +
                  nodo.printString() +
                  " ) then{\n");*/
      if (hijoI != null){
        text.append("if ( " + nodo.printString() + " ) then{\n");
        hijoI.printTree(depth + 1, text);
      }if (hijoD != null){
        text.append(aux2 + "else{ \n");
        hijoD.printTree(depth + 1, text);
      }
    }
    text.append(aux + "}\n");
  }

  public String clasificar(double[] ejemplo) {
    if (nodo.isLeaf) {
      return nodo.clase;
    }
    else if (nodo != null){
      if (nodo.cubre(ejemplo)) {
        if (hijoI != null){
          return hijoI.clasificar(ejemplo);
        }else{
          return "unclassified";
        }
      }
      else if (hijoD != null){
        return hijoD.clasificar(ejemplo);
      }else{
          return "unclassified";
        }
    }
    else{
      return "unclassified";
    }
  }

}
