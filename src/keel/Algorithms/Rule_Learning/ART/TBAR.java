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

package keel.Algorithms.Rule_Learning.ART;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
   A Java implementation of the TBAR algorithm
   @author Ines de la Torre Quesada (UJA)
   @version 1.0 (28-02-2010)
*/
public class TBAR {
    int MaxSize;
    double MinSupp;

    Vector data;

    List<Attribute> attributes;

    Node root;

    Vector<Node> nodosNivel;

    /** Constructor que crea e inicializa el arbol con los candidatos de nivel 1*/
    public TBAR(int MaxSize, double MinSupp, Vector data, List<Attribute> attributes) {
        this.MaxSize = MaxSize;
        this.MinSupp = MinSupp;
        this.data = data;
        this.attributes = attributes;
        root = new Node();
        Vector nodes = new Vector();

        for(int i=0; i<this.attributes.size()-1; i++){
            Attribute a = this.attributes.get(i);
            for(int j = 0; j<a.numValues(); j++){
                Node n = new Node();
                Vector<Integer> ats = new Vector<Integer>();
                Vector<Integer> vals = new Vector<Integer>();

                ats.add(i);
                vals.add(j);

                n.setAttributes(ats);
                n.setValues(vals);
                n.setParent(root);
                
                nodes.add(n);
            }
        }

        root.setChildren(nodes);
        support(1);
    }

    public int getMaxSize() {
        return MaxSize;
    }

    public void setMaxSize(int MaxSize) {
        this.MaxSize = MaxSize;
    }

    public double getMinSupp() {
        return MinSupp;
    }

    public void setMinSupp(double MinSupp) {
        this.MinSupp = MinSupp;
    }

    public Vector getData() {
        return data;
    }

    public void setData(Vector data) {
        this.data = data;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    private void support(int level){
        Itemset item;
        Node node = null;

        nodosNivel = new Vector();
        
        nodosNivel(level,root);

        for(int i=0; i<data.size(); i++){
            item = (Itemset)data.get(i);
            for(int j = 0; j<nodosNivel.size(); j++){
                node = nodosNivel.get(j);
                if(coincide(node,item,level))
                    node.setSupport(node.getSupport()+1);
            }
        }
    }
    
    private void nodosNivel(int level, Node n){
        if(level-1 > 0){
            for(int i = 0; i<n.numChildren(); i++){
                nodosNivel(level-1,n.getChildren(i));
            }
        }else{ //Estamos en el nivel que deseamos
            for(int i = 0; i< n.numChildren(); i++){
                nodosNivel.add(n.getChildren(i));
            }
        }
    }

    private boolean coincide(Node n, Itemset item, int level){
        boolean ok;
        int atributo;
        if(level-1>0){
        	if (n.getAttributes().size() > 0) {
        		atributo = n.getAttributes().get(0);
        		if(item.getValue(atributo)==n.getValues().get(0)) ok = true;        	
        		else ok = false;
        	} else {
        		ok = false;
        	}

            return(ok && coincide(n.getParent(),item,level-1));
        }else{
        	if (n.getAttributes().size() > 0) {
        		atributo = n.getAttributes().get(0);
        		if(item.getValue(atributo)==n.getValues().get(0)) ok = true;
        		else ok = false;
        	} else {
        		ok = false;
        	}

            return(ok);
        }
    }

    private Vector candidates(int level){
        Vector<Node> nivel1 = new Vector();

        nodosNivel = new Vector();
        nodosNivel(1,root);

        nivel1 = (Vector)nodosNivel.clone();

        nodosNivel = new Vector();
        nodosNivel(level-1,root);


        for(int i=0; i<nodosNivel.size(); i++){
            expandir(nodosNivel.get(i),nivel1);
        }

        support(level);
        
        nodosNivel = new Vector();
        nodosNivel(level,root);

        return nodosNivel;
    }

    private void expandir(Node n, Vector<Node> nivel1){
        Vector nodes = new Vector();
        Node nivel;
        int i = 0;

        while(i < nivel1.size()){
            nivel = nivel1.get(i);
            if(nivel.getAttributes().get(0)>n.getAttributes().get(0)){
                Node node = new Node();
                Vector<Integer> ats = new Vector<Integer>();
                Vector<Integer> vals = new Vector<Integer>();

                ats.add(nivel.getAttributes().get(0));
                vals.add(nivel.getValues().get(0));

                node.setAttributes(ats);
                node.setValues(vals);
                node.setParent(n);

                nodes.add(node);
            }
            i++;
        }
        n.setChildren(nodes);
    }


    private Vector relevants(int level){
        double supp;
        nodosNivel = new Vector();
        nodosNivel(level,root);
        Vector nodos = new Vector(nodosNivel);

        for(int i=0; i<nodosNivel.size(); i++){
            Node n = nodosNivel.get(i);
            supp = (double)n.getSupport()/data.size();
            if(supp < MinSupp){
                nodos.remove(n);
                n.getParent().getChildren().remove(n);
            }  
        }

        return nodos;
    }


    public Vector<Vector<Rule>> ruleExtraction(int level){
        Vector<Vector<Rule>> conjuntos = new Vector();
        Vector<Rule> reglas = new Vector();
        Vector<Rule> reglasConfMax = new Vector();
        Vector<Node> nodos = new Vector();

        nodos = relevants(1);

        int k = 2;
        while(k <= level && nodos.size() >= k){
            nodos = candidates(k);
            if(nodos.size()>0) nodos = relevants(k);
            k++;
        }

        //Construir reglas con los atributos relevantes y obtener su confianza
        int sum = 0;
        Vector<Itemset> itemsets = new Vector();
        Vector<Integer> ats = new Vector();
        Vector<Integer> vals = new Vector();

        for(int i=0; i<nodos.size(); i++){
            Node n = nodos.get(i);
            itemsets = new Vector();
            ats = new Vector();
            vals = new Vector();

            while(n.getParent()!=null){
                ats.add(n.getAttributes().get(0));
                vals.add(n.getValues().get(0));
                n = n.getParent();
            }

            Collections.reverse(ats);
            Collections.reverse(vals);

            for(int j=0; j<data.size(); j++){
                if(coincide(nodos.get(i),(Itemset)data.get(j), level)){
                    itemsets.add((Itemset)data.get(j));
                }
            }

            Attribute a = attributes.get(MaxSize);
            for(int l=0; l<a.numValues(); l++){
                sum = 0;
                for(int m=0; m<itemsets.size(); m++){
                    Itemset it = itemsets.get(m);
                    if(it.getValue(MaxSize)==l) sum++;
                }
                Rule r = new Rule(ats,vals);
                r.setClas(l);
                r.setConfidence((double)sum/itemsets.size());
                r.setSupport(sum);
                reglas.add(r);
            } 
        }

        double confMax = 0;

        //Obtener confianza maxima
        for(int i=0; i<reglas.size(); i++){
            if(reglas.get(i).getConfidence()>confMax){
                confMax = reglas.get(i).getConfidence();
            }
        }

        //Obtener las reglas con confianza maxima
        for(int i=0; i<reglas.size(); i++){
            if(reglas.get(i).getConfidence() == confMax){
                reglasConfMax.add(reglas.get(i));
            }
        }

        //Obtener conjuntos de reglas
        Vector<Rule> conjunto = new Vector();

        if(reglasConfMax.size()>0){
            conjunto.add(reglasConfMax.get(0));
            ats = new Vector(reglasConfMax.get(0).getAttributes());
        }

        for(int i=1; i<reglasConfMax.size(); i++){
            if(!ats.equals(reglasConfMax.get(i).getAttributes())){ //no coincide
                conjuntos.add(conjunto);
                conjunto = new Vector();
                conjunto.add(reglasConfMax.get(i));
                ats = new Vector(reglasConfMax.get(i).getAttributes());
            }else{
                conjunto.add(reglasConfMax.get(i));
            }
        }
        
        if(conjunto.size()!=0) conjuntos.add(conjunto);

        return conjuntos;
    }
}

