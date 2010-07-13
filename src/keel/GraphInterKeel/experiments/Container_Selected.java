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

/*
 * Container.java
 *
 * Created on 8 de abril de 2010, 23:15
 */

package keel.GraphInterKeel.experiments;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JTextField;
import java.awt.*;
import javax.swing.*;
import org.jdom.Element;
import org.jfree.data.general.Dataset;

/**
 *
 * @author  tua
 */
public class Container_Selected extends javax.swing.JDialog {
    
    Parameters parameterData;
    Node orig;
    Vector<DinamicParameter> copia = new Vector<DinamicParameter>();
    Vector<String> all_datasets = new Vector<String>();
    Vector<Vector<Integer>> positions = new Vector<Vector<Integer>>();
    Vector<Vector<Integer>> datano= new Vector<Vector<Integer>>();
    public Experiments exp;
    JPanel expan2 = new JPanel();
    int nexp;
    Joint way =new Joint();
    Vector<String> problems = new Vector<String>();
    Vector<Boolean> fromdata = new Vector<Boolean>();
    
    /** Creates new form Container */
     public Container_Selected(java.awt.Frame parent, boolean modal) {
     super(parent, modal);
     }
     
     //show the node
      public Container_Selected(java.awt.Frame parent, boolean modal,String title, Node destino,
            Experiments expe) {
      
          
          //We have to take into account if the node saved in destino
          //is a type_dataset or other. If is a type_dataset we can
          //select other new datasets, else only we can change the parameters.
          
        super(parent, modal);
        initComponents();
        exp=expe;
        
        this.setTitle(title);
        this.jLabel1.setText("Algoritm: "+destino.id+"."+destino.dsc.getName(0));
        this.jLabel1.setForeground(Color.BLUE);   
        int nu=15;
        parameterData = (Parameters) (destino.par.elementAt(0));
       
        
        //The imagen of the node is modified whether this imagen contain a *
        if(destino.image  == Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD_ast.gif")))
            destino.image =Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocessLQD.gif"));
        
        for(int i=0;i<destino.dsc.arg.size();i++)
        {
           // System.out.println("This is the joint with position "+i);
            if(destino.dsc.arg.get(i).before.getType()==Node.type_Dataset)
            {
               // System.out.println("The origen node is type_dataset ");
                javax.swing.JLabel titulo= new javax.swing.JLabel();       
                 titulo.setForeground(Color.BLUE);       
                 titulo.setBounds(10, nu, 450, 25);   
                 String tipo="";
                     
                DatasetXML[] files = null;
                       
                 if(destino.dsc.arg.get(i).before.type_lqd==Node.LQD)
                 {
                     tipo="LQD";
                     files= exp.listData;
                 }
                 else if(destino.dsc.arg.get(i).before.type_lqd==Node.LQD_C)
                 {
                     tipo="LQD_C";
                     files=exp.listDataLQD_C;        
                 }
                 else if(destino.dsc.arg.get(i).before.type_lqd==Node.C_LQD)
                 {
                     tipo="C_LQD";
                      files=exp.listDataC_LQD;        
                 }
                 else if(destino.dsc.arg.get(i).before.type_lqd==Node.CRISP2)
                 {
                     tipo="CRISP";
                      files=exp.listDataC;
                 }
                 titulo.setText("Datasets from "+tipo);       
                 jPanel2.add(titulo);
                 nu=nu+25;
                
                for(int j=0;j<destino.dsc.arg.get(i).before.dsc.getNamesLength();j++)
                {
                    
                    if(destino.dsc.arg.get(i).contain(destino.dsc.arg.get(i).before.dsc.getName(j))==true)
                    {
                       // System.out.println("The dataset is contained in destine ");
                        javax.swing.JCheckBox data= new javax.swing.JCheckBox();
                        data.setForeground(Color.red);
                        data.setBounds(1, nu, 400, 25);
                        data.setText(destino.dsc.arg.get(i).before.dsc.getName(j));
                        all_datasets.addElement(destino.dsc.arg.get(i).before.dsc.getName(j));
                        fromdata.addElement(true);
                        data.addActionListener(new Container_Selected_data_Show_actionAdapter(this,data,destino.dsc.arg.get(i)));
                        jPanel2.add(data);
                        data.setSelected(true);
                        int pos=0;
                        int para_pos=0;
                        int fin=0;
                        javax.swing.JPanel panel = new javax.swing.JPanel();        
                        panel.setBackground(new Color(201, 216, 237)); // NOI18N                  
                        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);        
                        panel.setLayout(panelLayout);
                        nu=nu+25;
                        int posicion_para=destino.dsc.arg.get(i).position_name(destino.dsc.arg.get(i).before.dsc.getName(j));
                        for(int p=0;p<destino.dsc.arg.get(i).parameters.get(posicion_para).size();p++)
                        {
                            Vector<String> contain = destino.dsc.arg.get(i).parameters.get(posicion_para).get(p);
                            javax.swing.JLabel ins= new javax.swing.JLabel();
                            ins.setBounds(20+pos, nu, 130, 25);
                            pos=pos+150;
                            if(p==0)
                                ins.setText("Instances: "+contain.get(0));
                            else if(p==1)
                            {
                                Vector<String> cla=destino.dsc.arg.get(i).parameters.get(posicion_para).get(3);
                                ins.setText("Classes: "+contain.get(0)+" "+cla);
                            }
                            else if (p==2)
                                ins.setText("Attributes: "+contain.get(0));
                            
                            jPanel2.add(ins);
                            
                            //nu=nu+15;
                            if(p>3 && p<destino.dsc.arg.get(i).parameters.get(posicion_para).size()-1) //the dinamic parameters
                            {   
                                    javax.swing.JLabel par= new javax.swing.JLabel();           
                                    par.setText("Parameter "+para_pos+": "+parameterData.descriptions.get(para_pos));               
                                    panel.add(par);  
                                    par.setBounds(50, fin, 150, 25); 
                                    javax.swing.JLabel type= new javax.swing.JLabel();
            
                                   /* if(parameterData.parameterType.get(para_pos).toString().compareTo("list")==0 || 
                                       parameterData.parameterType.get(para_pos).toString().compareTo("List")==0)
                                     {
                                        javax.swing.JComboBox value = new javax.swing.JComboBox();
                                        panel.add(value); 
                                        value.setBounds(220, fin, 100, 25);  
                                        value.setVisible(true);     
                
                                        System.out.println(parameterData.domain.get(para_pos));    
                                         for (int v = 0; v < parameterData.getDomain(p-4).size(); v++) 
                                         {
                                                value.addItem( parameterData.getDomainValue(para_pos, v));
                                                if(parameterData.getDomainValue(para_pos,v).compareTo(contain.toString())==0)
                                                    value.setSelectedIndex(v);
                                         }
                
                                        
                                        
               
                                        
                
                                        value.setEnabled(true);
                                        value.addActionListener(new Container_Selected_Property_actionAdapter(this,value,destino.dsc.arg.get(i),destino.dsc.arg.get(i).before.dsc.getName(j)));
                
                
                
                                        type.setText(parameterData.parameterType.get(para_pos).toString());               
                                    }*/
                                    //else
                                   // {
                                        javax.swing.JTextField value = new javax.swing.JTextField();
                                    
                                        value.setText(contain.toString());
                                        if(parameterData.descriptions.get(para_pos).toString().compareTo("Classes")==0 ||
                                            parameterData.descriptions.get(para_pos).toString().compareTo("Costs")==0)
                                        {
                                            if(parameterData.descriptions.get(para_pos).toString().compareTo("Classes")==0)
                                                value.setEnabled(false);
                                            else
                                                value.setEnabled(true);
                                            type.setText("Set of "+parameterData.parameterType.get(para_pos).toString());               
                                        }        
                                        else
                                        {
                                            type.setText(parameterData.parameterType.get(para_pos).toString());               
                                        }
                                        value.setBounds(220, fin, 100, 25);  
                                        String nombre=destino.dsc.arg.get(i).before.dsc.name[j].toString();                                   
                                        value.addActionListener(new Container_Selected_Value_Show_actionAdapter(this,value,p,destino.dsc.arg.get(i),nombre));
                                        panel.add(value); 
                                  
                                    //}
                                   
                                    panel.add(type);  
                                    type.setBounds(340, fin, 75, 25); 
                                    fin=fin+25;
                                    para_pos++;
                                     
                            }
                            if(p==destino.dsc.arg.get(i).parameters.get(posicion_para).size()-1)
                            {
                                
                                javax.swing.JLabel par= new javax.swing.JLabel();           
                                   par.setText("Parameter: Dataset ");                
                                    panel.add(par);  
                                    par.setBounds(50, fin, 150, 25);
                                    
                                javax.swing.JTextField type_data_used = new javax.swing.JTextField();
                                panel.add(type_data_used); 
                                type_data_used.setBounds(220, fin, 100, 25); 
                                type_data_used.setVisible(true);     
                                fin=fin+25;
                                type_data_used.setEnabled(false);
                                type_data_used.setText(contain.toString());
                                problems.addElement(destino.dsc.arg.get(i).problem.get(posicion_para));
          
                                
                            }
                            
                        }
                         panel.setBounds(45,nu+35,this.getWidth()-100,fin); 
                          jPanel2.add(panel);
                        nu=nu+fin+55;
                    } // if detino contain the dataset
                    
                    else // Save the node no selected
                    {
                     //   destino.dsc.arg.get(i).before.dsc.getName(j)
                                
                       int position=0;
                        for (int f = 0; f < files.length; f++) 
                        {
                            //System.out.println (" Data de la lista: "+files[f].nameAbr + " en el nodo " +  destino.dsc.arg.get(i).before.dsc.getName(j) );
                            if (files[f].nameAbr.equalsIgnoreCase(destino.dsc.arg.get(i).before.dsc.getName(j)))
                            {
                              //  System.out.println(files[f].fuzzy+ " nombre seria "+destino.dsc.arg.get(i).before.dsc.getName(j));
                               // System.out.println(((Parameters)destino.par.elementAt(0)).fuzzy);
                                position=f;
                                break;
                            }
                        }
                        if((files[position].fuzzy==true && ((Parameters)destino.par.elementAt(0)).fuzzy==false)
                        || (files[position].exh_test==false && destino.dsc.getName(0).compareTo("FGFS_LQD_Base")==0))
                            position=0;
                        
                            
                            //JOptionPane.showMessageDialog(this,
                            //"The algorithm "+destino.dsc.getName(0)+ "only support interval data. File "+destino.dsc.arg.get(i).before.dsc.getName(j)+" is fuzzy.","Warning", JOptionPane.WARNING_MESSAGE);
                   
                        else
                        {
                            //System.out.println("The dataset is not contained in destine ");
                            Vector<Integer> data_joint = new Vector<Integer>();
                            data_joint.addElement(j);
                            data_joint.addElement(i);
                            datano.addElement(data_joint);
                        }
                    }
                    
   
                }//FOR all the datasets in the previous node (node saved in destine)

                for(int c=0;c<destino.dsc.arg.get(i).parameters.size();c++)
                {
                    DinamicParameter contain = new DinamicParameter();
                
                    for(int j=0;j<destino.dsc.arg.get(i).parameters.get(c).size();j++)
                    {
                        Vector<String> v1= new Vector<String>();
                        for(int v=0;v<destino.dsc.arg.get(i).parameters.get(c).get(j).size();v++)
                        {
                        v1.addElement(destino.dsc.arg.get(i).parameters.get(c).get(j).get(v));
                        }
                        contain.parameter_data.addElement(v1);
                    }
                
                    copia.addElement(contain);
                }
                
            } // for the node is type_dataset
            else // is other type so we can not choose datasets only chance parrameters
            {
                 javax.swing.JLabel titulo= new javax.swing.JLabel();       
                 titulo.setForeground(Color.BLUE);       
                 titulo.setBounds(10, nu, 450, 25);       
                 titulo.setText("Datasets from "+destino.dsc.arg.get(i).before.dsc.getName());       
                 jPanel2.add(titulo);
                 nu=nu+25;
                for(int j=0;j<destino.dsc.arg.get(i).data_selected.size();j++)
                {
                    
                        javax.swing.JLabel data= new javax.swing.JLabel();
                        data.setForeground(Color.red);
                        data.setBounds(10, nu, 400, 25);
                        data.setText(destino.dsc.arg.get(i).data_selected.get(j));
                        data.setText(destino.dsc.arg.get(i).problem.get(j));
                        jPanel2.add(data);  
                        all_datasets.addElement(destino.dsc.arg.get(i).data_selected.get(j));                        
                        fromdata.addElement(false);
                        problems.addElement(destino.dsc.arg.get(i).problem.get(j)); 
                        nu=nu+25;
                       
                        int pos=0;
                        int para_pos=0;
                        int fin=0;
                        javax.swing.JPanel panel = new javax.swing.JPanel();        
                        panel.setBackground(new Color(201, 216, 237)); // NOI18N                  
                        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);        
                        panel.setLayout(panelLayout);
                        
                        for(int p=0;p<destino.dsc.arg.get(i).parameters.get(j).size();p++)
                        {
                            Vector<String> contain = destino.dsc.arg.get(i).parameters.get(j).get(p);
                            javax.swing.JLabel ins= new javax.swing.JLabel();
                            ins.setBounds(pos+20, nu, 130, 25);
                            pos=pos+150;
                            if(p==0)
                                ins.setText("Instances: "+contain.get(0));
                            else if(p==1)
                            {
                                Vector<String> cla=destino.dsc.arg.get(i).parameters.get(j).get(3);
                                ins.setText("Classes: "+contain.get(0)+" "+cla);
                            }
                            else if (p==2)
                                ins.setText("Attributes: "+contain.get(0));
                            
                            jPanel2.add(ins);
                            
                            //nu=nu+15;
                            if(p>3 && p!=destino.dsc.arg.get(i).parameters.get(j).size()-1) //the dinamic parameters
                            {   
                                    javax.swing.JLabel par= new javax.swing.JLabel();           
                                    par.setText("Parameter "+para_pos+": "+parameterData.descriptions.get(para_pos));               
                                    panel.add(par);  
                                    par.setBounds(50, fin, 150, 25); 
            
                                    javax.swing.JLabel type= new javax.swing.JLabel();
                                            
                                    /*if(parameterData.parameterType.get(para_pos).toString().compareTo("list")==0 || 
                                       parameterData.parameterType.get(para_pos).toString().compareTo("List")==0)
                                     {
                                        javax.swing.JComboBox value = new javax.swing.JComboBox();
                                        panel.add(value); 
                                        value.setBounds(220, fin, 100, 25);  
                                        value.setVisible(true);     
                
                                        
                                        System.out.println(parameterData.domain.get(para_pos));    
                                         for (int v = 0; v < parameterData.getDomain(para_pos).size(); v++) 
                                         {
                                                value.addItem( parameterData.getDomainValue(para_pos, v));
                                                if(parameterData.getDomainValue(para_pos,v).compareTo(contain.toString())==0)
                                                    value.setSelectedIndex(v);
                                         }
                
                                        
                                        
               
                                        
                
                                        value.setEnabled(true);
                                        value.addActionListener(new Container_Selected_Property_actionAdapter(this,value,destino.dsc.arg.get(i),destino.dsc.arg.get(i).before.dsc.getName(j)));
                
                
                
                                        type.setText(parameterData.parameterType.get(para_pos).toString());               
                                    }*/
                            
                                   // else
                                    //{
                                        javax.swing.JTextField value = new javax.swing.JTextField();
                                    
                                        value.setText(contain.toString());
                                        if(parameterData.descriptions.get(para_pos).toString().compareTo("Classes")==0 ||
                                            parameterData.descriptions.get(para_pos).toString().compareTo("Costs")==0)
                                        {
                                            if(parameterData.descriptions.get(para_pos).toString().compareTo("Classes")==0)
                                                value.setEnabled(false);
                                            type.setText("Set of "+parameterData.parameterType.get(para_pos).toString());               
                                        }           
                                        else
                                        {
                                            type.setText(parameterData.parameterType.get(para_pos).toString());               
                                        }
                                        value.setBounds(220, fin, 100, 25);  
                                        //String nombre=destino.dsc.arg.get(i).data_selected.get(j);                                   
                                        String nombre=destino.dsc.arg.get(i).problem.get(j);                                   
                                        value.addActionListener(new Container_Selected_Value_Show_Ant_actionAdapter(this,value,p,destino.dsc.arg.get(i),nombre));
                                        panel.add(value); 
                                   // }
                                   panel.add(type);  
                                    type.setBounds(340, fin, 75, 25); 
                                    fin=fin+25;
                                    para_pos++;
                                     
                            }
                            if(p==destino.dsc.arg.get(i).parameters.get(j).size()-1)
                            {
                                  javax.swing.JLabel par= new javax.swing.JLabel();           
                                    par.setText("Parameter: Dataset ");               
                                    panel.add(par);  
                                    par.setBounds(50, fin, 150, 25);
                                    
                                javax.swing.JTextField type_data_used = new javax.swing.JTextField();
                                panel.add(type_data_used); 
                                type_data_used.setBounds(220, fin, 100, 25); 
                                type_data_used.setVisible(true);     
                                fin=fin+25;
                                type_data_used.setEnabled(false);
                                type_data_used.setText(contain.toString());
                                                               
          
                                
                            }
                            
                        }
                         panel.setBounds(45,nu+35,this.getWidth()-100,fin); 
                          jPanel2.add(panel);
                        nu=nu+fin+55;
                        
                }
            
                
                 
                for(int c=0;c<destino.dsc.arg.get(i).parameters.size();c++)
                {
                    DinamicParameter contain = new DinamicParameter();
                
                    for(int j=0;j<destino.dsc.arg.get(i).parameters.get(c).size();j++)
                    {
                        Vector<String> v1= new Vector<String>();
                        for(int v=0;v<destino.dsc.arg.get(i).parameters.get(c).get(j).size();v++)
                        {
                        v1.addElement(destino.dsc.arg.get(i).parameters.get(c).get(j).get(v));
                        }
                        contain.parameter_data.addElement(v1);
                    }
                
                    copia.addElement(contain);
                }
            }
            
            
         destino.dsc.arg.get(i).information();  
                
        }
        
        nexp=nu;        
        int hei=0;  
              
        for(int v=0;v<datano.size() ;v++)
        {
                    System.out.println("The "+v+" dataset not contained in the joint "+datano.get(v).get(1));
                    JButton data= new JButton();
                    data.setForeground(Color.black);
                    //data.setBackground(new Color(255, 253, 202));
                      data.setBackground(Color.yellow);
                    data.setText(destino.dsc.arg.get(datano.get(v).get(1)).before.dsc.getName(datano.get(v).get(0)));
                    data.addActionListener(new Container_Selected_datano_actionAdapter(this,v,destino.dsc.arg.get(datano.get(v).get(1)),destino));
                    expan2.add(data);
                    hei=hei+15;
        }
                expan2.setBounds(15, nexp, jPanel2.getWidth()-30,hei+15);
                jPanel2.add(expan2);
                
                 
                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, nu+hei+10, Short.MAX_VALUE)
        );
          
          
      }
      
      //create the form when the node is different a type_dataset
       public Container_Selected(java.awt.Frame parent, boolean modal,Node origen, Node destino,Experiments expe) {
      
       
        
        super(parent, modal);
        initComponents();
        this.setTitle("Datasets and its parameters");
        this.jLabel1.setText("Algoritm: "+destino.id+"."+destino.dsc.getName(0));
        this.jLabel1.setForeground(Color.BLUE);       
        orig=origen;
       
       
            int nu=15;
            
            //destino.dsc.arg.addElement(new Joint());
            //destino.dsc.arg.get(destino.dsc.arg.size()-1).setNode(origen);
            way.setNode(orig);
            
            
            way.type_lqd= origen.dsc.arg.get(0).type_lqd; // all the same type (determined in the connections)
            DatasetXML[] files = null;
            if(origen.getTypelqd()==Node.LQD)
                    files= expe.listData; 
                else if(origen.getTypelqd()==Node.LQD_C)
                   files=expe.listDataLQD_C;        
                else if(origen.getTypelqd()==Node.C_LQD)
                        files=expe.listDataC_LQD;        
                else if(origen.getTypelqd()==Node.CRISP2)
                        files=expe.listDataC;
            int position=0;
            for(int i=0;i<origen.dsc.arg.size();i++)
            {        
                
                    for(int d=0;d<origen.dsc.arg.get(i).data_selected.size();d++)
                    {
                         for (int j = 0; j < files.length; j++) 
                         {
                            //System.out.println (" Data de la lista: "+files[j].nameAbr + " en el nodo " +  origen.dsc.arg.get(i).data_selected.get(d));
                            if (files[j].nameAbr.equalsIgnoreCase(origen.dsc.arg.get(i).data_selected.get(d)))
                            {
                            //System.out.println(files[j].fuzzy+ " nombre seria "+origen.dsc.name[i]);
                            //System.out.println(((Parameters)destino.par.elementAt(0)).fuzzy);
                            position=j;
                            break;
                            }
                         }
                     
                        if(files[position].fuzzy==true && ((Parameters)destino.par.elementAt(0)).fuzzy==false)
                            JOptionPane.showMessageDialog(this,
                            "The algorithm "+destino.dsc.getName(0)+ " only support interval data. File "+origen.dsc.arg.get(i).data_selected.get(d)+" is fuzzy.","Warning", JOptionPane.WARNING_MESSAGE);
                         else if (files[position].exh_test==false && destino.dsc.getName(0).compareTo("FGFS_LQD_Base")==0)
                            JOptionPane.showMessageDialog(this,
                            "The algorithm "+destino.dsc.getName(0)+ " only support exhaustive test. File "+origen.dsc.arg.get(i).data_selected.get(d)+" no suppor it.","Warning", JOptionPane.WARNING_MESSAGE);
                        else
                        {
                        
                            javax.swing.JLabel data= new javax.swing.JLabel();
                   
                            data.setForeground(Color.red);
                            data.setBounds(10, nu, 400, 25);
                            //data.setText(origen.dsc.arg.get(i).data_selected.get(d)); 
                            data.setText(origen.id+"."+origen.dsc.getName()+"-"+origen.dsc.arg.get(i).problem.get(d));
                            jPanel2.add(data);
                
                            way.insertproblem(origen.id+"."+origen.dsc.getName()+"-"+origen.dsc.arg.get(i).problem.get(d));
                            way.insertDataSelected(origen.dsc.arg.get(i).data_selected.get(d));
                            way.getdataSelected();
                
                            nu=nu+25;
                            int more=0;
                            more=copy_description_dataset(d,origen.dsc.arg.get(i),destino,nu);
                            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
                                   
                            nu=nu+more; 
                        }
                    }
                
            }
            
          
             for(int i=0;i<way.parameters.size();i++)
            {
                DinamicParameter contain = new DinamicParameter();
                
                for(int j=0;j<way.parameters.get(i).size();j++)
                {
                    Vector<String> v1= new Vector<String>();
                    for(int v=0;v<way.parameters.get(i).get(j).size();v++)
                    {
                       v1.addElement(way.parameters.get(i).get(j).get(v));
                    }
                    contain.parameter_data.addElement(v1);
                }
                
                copia.addElement(contain);
            }
            
        destino.dsc.arg.addElement(way);
         javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, nu+20, Short.MAX_VALUE)
        );
        
    }
    
       //create the form when the node is type-dataset
    public Container_Selected(java.awt.Frame parent, boolean modal,String title,Node origen, Node destino,
            Experiments exp) {
      
        //We obtain the form when the origen node is a type-dataset and therefore
        //we can select the datasets and its parameters
        
        super(parent, modal);
        initComponents();
        this.setTitle(title);
        this.jLabel1.setText("Algoritm: "+destino.id+"."+destino.dsc.getName(0));
        this.jLabel1.setForeground(Color.BLUE);       
        orig=origen;
         
        
         
       
            int nu=15;
            
            //destino.dsc.arg.addElement(new Joint());
            //destino.dsc.arg.get(destino.dsc.arg.size()-1).setNode(origen);
            way.setNode(orig);
            if(orig.type_lqd==Node.LQD)
                way.type_lqd = "LQD";
            else if(orig.type_lqd==Node.LQD_C)
                way.type_lqd = "CRISP";
            else if(orig.type_lqd==Node.C_LQD)
                way.type_lqd = "LQD";
            else if(orig.type_lqd==Node.CRISP2)
                way.type_lqd = "CRISP";
      
         
            DatasetXML[] files=exp.listData;
            if(origen.getTypelqd()==Node.LQD)
                    files= exp.listData; 
                else if(origen.getTypelqd()==Node.LQD_C)
                   files=exp.listDataLQD_C;        
                else if(origen.getTypelqd()==Node.C_LQD)
                        files=exp.listDataC_LQD;        
                else if(origen.getTypelqd()==Node.CRISP2)
                        files=exp.listDataC;
            int position=0;
            for(int i=0;i<origen.dsc.name.length;i++)
            {     
                
                for (int j = 0; j < files.length; j++) 
                {
                 
                    System.out.println (" Data de la lista: "+files[j].nameAbr + " en el nodo " +  origen.dsc.name[i] );
                    if (files[j].nameAbr.equalsIgnoreCase(origen.dsc.name[i]))
                    {
                        System.out.println(files[j].fuzzy+ " nombre seria "+origen.dsc.name[i]);
                        System.out.println(((Parameters)destino.par.elementAt(0)).fuzzy);
                        position=j;
                        break;
                    }
                }
                     
                    if(files[position].fuzzy==true && ((Parameters)destino.par.elementAt(0)).fuzzy==false)
                        JOptionPane.showMessageDialog(this,
                            "The algorithm "+destino.dsc.getName(0)+ " only support interval data. File "+origen.dsc.name[i]+" is fuzzy.","Warning", JOptionPane.WARNING_MESSAGE);
                   else if (files[position].exh_test==false && destino.dsc.getName(0).compareTo("FGFS_LQD_Base")==0)
                            JOptionPane.showMessageDialog(this,
                            "The algorithm "+destino.dsc.getName(0)+ " only support exhaustive test. File "+origen.dsc.name[i]+" no suppor it.","Warning", JOptionPane.WARNING_MESSAGE);
                    else
                    {
                        javax.swing.JCheckBox data= new javax.swing.JCheckBox();
                        data.setForeground(Color.red);
                        data.setSelected(true);
                        //data.setBorderPainted(true);
                        data.setBounds(1, nu, 400, 25);
                        data.setText(origen.dsc.name[i]); 
                        //data.addActionListener(new Container_Selected_data_actionAdapter(this,data,destino.dsc.arg.get(destino.dsc.arg.size()-1)));
                        data.addActionListener(new Container_Selected_data_actionAdapter(this,data,way));
                        jPanel2.add(data);
                        nu=nu+25;
                
                        int more=0;
                          //Save the data of the first dataset
                        if(origen.getTypelqd()==Node.LQD)
                        {
                            more=load_description_dataset(i,origen,exp.listData,way,destino,nu);
                            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.orange));
                        }
                        else if(origen.getTypelqd()==Node.LQD_C)
                        {
                            more=load_description_dataset(i,origen,exp.listDataLQD_C,way,destino,nu);
                            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.yellow));
                        }
                        else if(origen.getTypelqd()==Node.C_LQD)
                        {
                            more=load_description_dataset(i,origen,exp.listDataC_LQD,way,destino,nu);
                            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLUE));
                        }
                        else if(origen.getTypelqd()==Node.CRISP2)
                        {   
                            more=load_description_dataset(i,origen,exp.listDataC,way,destino,nu);
                            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray));
                        }
                
                        nu=nu+more;
                
                    }
                
               
        }
            
          
             for(int i=0;i<way.parameters.size();i++)
            {
                DinamicParameter contain = new DinamicParameter();
                
                for(int j=0;j<way.parameters.get(i).size();j++)
                {
                    Vector<String> v1= new Vector<String>();
                    for(int v=0;v<way.parameters.get(i).get(j).size();v++)
                    {
                       v1.addElement(way.parameters.get(i).get(j).get(v));
                    }
                    
                    contain.parameter_data.addElement(v1);
                }
                
                copia.addElement(contain);
            }
            
        destino.dsc.arg.addElement(way);
    
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, nu+20, Short.MAX_VALUE)
        );
       
                
    }
    
   
       /*
     * We obtain the name of all dataset and the number of instances, class and atributtes
     * of each one of them. An these information is saved in the destine node. Also we save
     * the parameters that we need to run the algorithm (destine node)
     */
    private int copy_description_dataset(int position,Joint origen,Node destino, int nu)
    {
        DinamicParameter parar_data = new DinamicParameter();            
        
        javax.swing.JLabel ins= new javax.swing.JLabel();
        ins.setBounds(20, nu, 100, 25);                  
        ins.setText("Instances: "+origen.parameters.get(position).parameter_data.get(0));                 
        jPanel2.add(ins);                  
        javax.swing.JLabel cla= new javax.swing.JLabel();                 
        cla.setBounds(150, nu, 150, 25);                  
        cla.setText("Classes: "+origen.parameters.get(position).parameter_data.get(1)+" "+origen.parameters.get(position).parameter_data.get(3));                 
        jPanel2.add(cla);                  
        javax.swing.JLabel atri= new javax.swing.JLabel();                 
        atri.setBounds(310, nu, 100, 25);                 
        atri.setText("Attributes: "+origen.parameters.get(position).parameter_data.get(2));                  
        jPanel2.add(atri);
                          
                          
        //Save the instances in the vector
        Vector<String> parameterI = new Vector<String>();                  
        parameterI.addElement(""+origen.parameters.get(position).parameter_data.get(0).get(0));                  
        parar_data.insert(parameterI);                  
        Vector<String> parameterC = new Vector<String>();
        parameterC.addElement(""+origen.parameters.get(position).parameter_data.get(1).get(0));                  
        parar_data.insert(parameterC);                  
        Vector<String> parameterA = new Vector<String>();                  
        parameterA.addElement(""+origen.parameters.get(position).parameter_data.get(2).get(0));                  
        parar_data.insert(parameterA);                                            
        parar_data.insert(origen.parameters.get(position).parameter_data.get(3));
                          
                    
        int pos=nu;
        //Rest of parameters. Needed by the algorithm
        parameterData = (Parameters) (destino.par.elementAt(0));
                    
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new Color(201, 216, 237)); // NOI18N          
        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
               
        int fin=0;
        for(int pa=0;pa<parameterData.getNumParameters();pa++)
        {
            //System.out.println("number of parameters "+parameterData.getNumParameters());
            /*System.out.println( " default values "+
                    parameterData.getDefaultValue(pa));*/
            Vector<String> p= new Vector<String>();
           
            javax.swing.JLabel par= new javax.swing.JLabel();           
            par.setText("Parameter "+pa+": "+parameterData.descriptions.get(pa));               
            panel.add(par);  
            par.setBounds(50, fin, 150, 25); 
            
            javax.swing.JLabel type= new javax.swing.JLabel();                  
            
            /*if(parameterData.parameterType.get(pa).toString().compareTo("list")==0 ||
                    parameterData.parameterType.get(pa).toString().compareTo("List")==0)
            {
                javax.swing.JComboBox value = new javax.swing.JComboBox();                  
                panel.add(value);                         
                value.setBounds(220, fin, 100, 25);  
                value.setVisible(true);     
                
                System.out.println(parameterData.domain.get(pa));    
                for (int v = 0; v < parameterData.getDomain(pa).size(); v++) 
                {
                    value.addItem( parameterData.getDomainValue(pa, v));                                               
                 
                }
                
                p.add(value.getSelectedItem().toString());
                
                value.setEnabled(true);
                String nombre=origen.data_selected.get(position);
                value.addActionListener(new Container_Selected_Property_actionAdapter(this,value,way,nombre) );
                
                
                
                                       
                type.setText(parameterData.parameterType.get(pa).toString());               
            }*/
            
            javax.swing.JTextField value = new javax.swing.JTextField();
              int fc=100;
               if(parameterData.descriptions.get(pa).toString().contains("Files")==true
                       && origen.data_selected.get(position).contains("C_LQD")==true)
                {
                   //System.out.println("deberia de entrar "+parameterData.descriptions.get(pa)+" y el valor del dataset "+ destin.before.dsc.getName(i));
                  value.setText("10");
                    type.setText(parameterData.parameterType.get(pa).toString());                
               }
                       
               else if(parameterData.descriptions.get(pa).toString().compareTo("Classes")==0)
            {
                value.setText(parar_data.get(3).toString());
                value.setEnabled(false);
                type.setText("Set of "+parameterData.parameterType.get(pa).toString());               
            }
            
            else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0 
                        && destino.dsc.getSubtype()==Node.type_Preprocess)
            {
                value.setText("[");
                for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                {
                    if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                    {   
                        if(Integer.parseInt(parar_data.get(1).get(0))==2)
                            value.setText(value.getText()+9+"]");
                        else
                            value.setText(value.getText()+5+"]");
                    }
                    else
                    {
                        if(Integer.parseInt(parar_data.get(1).get(0))==2)
                            value.setText(value.getText()+4+",");
                        else
                            value.setText(value.getText()+5+",");
                    }
                }
                type.setText("Set of "+parameterData.parameterType.get(pa).toString());               
            }
             else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0
                        && destino.dsc.getSubtype()==Node.type_Method)
                     {
                         value.setText("[");
                         
                          for(int f=0;f<Integer.parseInt(parar_data.get(1).get(0));f++)
                          {
                              fc=fc+10;
                            for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                            {
                                 if(f==(Integer.parseInt(parar_data.get(1).get(0)))-1 && cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {   
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+9+"]");
                                    else
                                        value.setText(value.getText()+5+"]");
                                }
                                /* else if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+"\n");
                                    else
                                        value.setText(value.getText()+5+"\n");
                                }*/
                                else
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+",");
                                    else
                                        value.setText(value.getText()+5+",");
                                }
                            }
                            
                          }
                         
                        type.setText("Set of Cost");   
                        
                     }
               
            else
            {
                value.setText(parameterData.getDefaultValue(pa));
                type.setText(parameterData.parameterType.get(pa).toString());               
            }
            
            value.setBounds(220, fin, fc, 25);  
            String nombre=origen.data_selected.get(position);
            value.addActionListener(new Container_Selected_Value_actionAdapter(this,value,pa,way,nombre));
            panel.add(value);  
           
            panel.add(type);  
            type.setBounds(240+fc, fin, 75, 25); 
            
            
            if(value.getText().contains("{")==true || value.getText().contains("[")==true)
            {
               int posi=0;
               int inicio=1;
               
               posi=value.getText().toString().indexOf(',',posi);
               while(posi!=-1)
               {
                  p.addElement(value.getText().substring(inicio, posi));
                  inicio=posi+1;
                  posi=value.getText().toString().indexOf(',',posi+1);
               }
                  p.addElement(value.getText().substring(inicio, value.getText().length()-1));
            }
            else
            {
                p.addElement(value.getText());
            }
                
            parar_data.insert(p);                          
            fin=fin+25;
        }
        
        //Insert the type of dataset that we can use
        javax.swing.JLabel par= new javax.swing.JLabel();           
        par.setText("Parameter: Dataset ");               
        panel.add(par);  
        par.setBounds(50, fin, 150, 25); 
            
         
         javax.swing.JTextField type_data_used = new javax.swing.JTextField();
         type_data_used.setEnabled(false);
         panel.add(type_data_used); 
         type_data_used.setBounds(220, fin, 100, 25); 
         type_data_used.setVisible(true);     
         fin=fin+25;
        
         if(origen.parameters.get(position).parameter_data.get(origen.parameters.get(position).parameter_data.size()-1).get(0).compareTo("O-100boost")==0)
            type_data_used.setText("100boost");
         else if (origen.parameters.get(position).parameter_data.get(origen.parameters.get(position).parameter_data.size()-1).get(0).compareTo("O-10cv")==0)
             type_data_used.setText("10cv");
         else if (origen.parameters.get(position).parameter_data.get(origen.parameters.get(position).parameter_data.size()-1).get(0).compareTo("10cv")==0)
             type_data_used.setText("10cv");
         else if (origen.parameters.get(position).parameter_data.get(origen.parameters.get(position).parameter_data.size()-1).get(0).compareTo("100boost")==0)
             type_data_used.setText("100boost");
         
             
         Vector<String> p = new Vector<String>();
         p.add(type_data_used.getText());
         parar_data.insert(p);                          
                    
         
        panel.setBounds(45,pos+35,this.getWidth()-100,fin); 
        jPanel2.add(panel);
        way.insertParameter(parar_data);
                  
                    
        return fin+55;
         
    }
    
    /*
     * We obtain the name of all dataset and the number of instances, class and atributtes
     * of each one of them. An these information is saved in the destine node. Also we save
     * the parameters that we need to run the algorithm (destine node)
     */
    private int load_description_dataset(int i,Node origen,DatasetXML[] list,Joint way,Node destino, int nu)
    {
        DinamicParameter parar_data = new DinamicParameter();
                    for (int j = 0; j < list.length; j++) 
                    {
                        System.out.println (" Data de la lista: "+list[j].nameAbr + " en el nodo " +  origen.dsc.name[i] );
                        if (list[j].nameAbr.equalsIgnoreCase(origen.dsc.name[i]))
                        {
                          javax.swing.JLabel ins= new javax.swing.JLabel();
                          ins.setBounds(100, nu, 100, 25);
                          ins.setText("Instances: "+list[j].nInstances);
                          jPanel2.add(ins);
                          javax.swing.JLabel cla= new javax.swing.JLabel();
                          cla.setBounds(240, nu, 150, 25);
                          cla.setText("Classes: "+list[j].nClasses+" {"+list[j].classes+"}");
                         jPanel2.add(cla);
                          javax.swing.JLabel atri= new javax.swing.JLabel();
                          atri.setBounds(390, nu, 100, 25);
                          atri.setText("Attributes: "+list[j].nAttributes);
                          jPanel2.add(atri);
                          
                          //Save the instances in the vector
                          Vector<String> parameterI = new Vector<String>();
                          parameterI.addElement(""+list[j].nInstances);
                          parar_data.insert(parameterI);
                          Vector<String> parameterC = new Vector<String>();
                          parameterC.addElement(""+list[j].nClasses);
                          parar_data.insert(parameterC);
                          Vector<String> parameterA = new Vector<String>();
                          parameterA.addElement(""+list[j].nAttributes);
                          parar_data.insert(parameterA);                          
                          parar_data.insert(list[j].classes);
                          
                          
                          way.insertDataSelected(origen.dsc.name[i]);
                          way.getdataSelected();
                          
                          break;
                        }
                    }
                    
        int pos=nu;
        //Rest of parameters. Needed by the algorithm
        parameterData = (Parameters) (destino.par.elementAt(0));
                    
                    
   
    
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new Color(201, 216, 237)); // NOI18N          
        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
               
        int fin=0;
        for(int pa=0;pa<parameterData.getNumParameters();pa++ )
        {
            Vector<String> p= new Vector<String>();
           
            javax.swing.JLabel par= new javax.swing.JLabel();           
            par.setText("Parameter "+pa+": "+parameterData.descriptions.get(pa));               
            panel.add(par);  
            par.setBounds(50, fin, 150, 25); 
            
             javax.swing.JLabel type= new javax.swing.JLabel();           
           /* if((parameterData.parameterType.get(pa).toString().compareTo("list")==0 || 
                    parameterData.parameterType.get(pa).toString().compareTo("List")==0))
            {
                javax.swing.JComboBox value = new javax.swing.JComboBox();
                panel.add(value); 
                value.setBounds(220, fin, 100, 25);  
                value.setVisible(true);     
                
                
                System.out.println(parameterData.domain.get(pa));    
                for (int v = 0; v < parameterData.getDomain(pa).size(); v++) 
                    value.addItem( parameterData.getDomainValue(pa, v));
                
               
                p.add(value.getSelectedItem().toString());
                
                value.setEnabled(true);
               value.addActionListener(new Container_Selected_Property_actionAdapter(this,value,way,origen.dsc.name[i].toString()) );
                
                type.setText(parameterData.parameterType.get(pa).toString());               
            }*/
            //else
           // {
                javax.swing.JTextField value = new javax.swing.JTextField();
                int fc=100;
                if(parameterData.descriptions.get(pa).toString().contains("Files")==true
                       && origen.dsc.name[i].contains("C_LQD")==true)
                {
                   //System.out.println("deberia de entrar "+parameterData.descriptions.get(pa)+" y el valor del dataset "+ destin.before.dsc.getName(i));
                  value.setText("10");
                    type.setText(parameterData.parameterType.get(pa).toString());                
               }
               
                else if(parameterData.descriptions.get(pa).toString().compareTo("Classes")==0)
                {
                    value.setText(parar_data.get(3).toString());
                    value.setEnabled(false);
                    type.setText("Set of "+parameterData.parameterType.get(pa).toString());               
                    
                }
            
                else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0
                        && destino.dsc.getSubtype()==Node.type_Preprocess)
                {
                    value.setText("[");
                    for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                    {
                         if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                        {   
                            if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                value.setText(value.getText()+9+"]");
                            else
                                value.setText(value.getText()+5+"]");
                        }
                        else
                        {
                            if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                value.setText(value.getText()+4+",");
                            else
                                value.setText(value.getText()+5+",");
                        }
                    }
                    type.setText("Set of Cost");               
                    
                }
               
                     else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0
                        && destino.dsc.getSubtype()==Node.type_Method)
                     {
                         //value.setText("BOTON MATRIZ");
                         
                         
                         value.setText("[");
                         
                          for(int f=0;f<Integer.parseInt(parar_data.get(1).get(0));f++)
                          {
                              fc=fc+10;
                              
                            for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                            {
                                 if(f==(Integer.parseInt(parar_data.get(1).get(0)))-1 && cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {   
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+9+"]");
                                    else
                                        value.setText(value.getText()+5+"]");
                                }
                                /* else if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+"\n");
                                    else
                                        value.setText(value.getText()+5+"\n");
                                }*/
                                else
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+",");
                                    else
                                        value.setText(value.getText()+5+",");
                                }
                            }
                            
                          }
                         
                        type.setText("Set of Cost");   
                        
                     }
               
                else
                {
                    value.setText(parameterData.getDefaultValue(pa));
                    type.setText(parameterData.parameterType.get(pa).toString());               
                }
            
                value.setBounds(220, fin, fc, 25);  
                String nombre=origen.dsc.name[i].toString();
                value.addActionListener(new Container_Selected_Value_actionAdapter(this,value,pa,way,nombre));
                 panel.add(value);  
            
           
                          
           
                if(value.getText().contains("{")==true || value.getText().contains("[")==true)
                {
                    int position=0;
                    int inicio=1;
               
                    position=value.getText().toString().indexOf(',',position);
                    while(position!=-1)
                    {
                        p.addElement(value.getText().substring(inicio, position));
                        inicio=position+1;
                        position=value.getText().toString().indexOf(',',position+1);
                    }
                    p.addElement(value.getText().substring(inicio, value.getText().length()-1));
                }
                else
                {
                    p.addElement(value.getText());
                }
            //} 
             
             panel.add(type);  
             type.setBounds(240+fc, fin, 75, 25); 
            
            
            parar_data.insert(p);                          
            fin=fin+25;
        }
        
        //Insert the type of dataset that we can use
        javax.swing.JLabel par= new javax.swing.JLabel();           
        par.setText("Parameter: Partitions_Data ");               
        panel.add(par);  
        par.setBounds(50, fin, 150, 25); 
            
         
         javax.swing.JComboBox type_data_used = new javax.swing.JComboBox();
         panel.add(type_data_used); 
         type_data_used.setBounds(220, fin, 100, 25); 
         type_data_used.setVisible(true);     
         fin=fin+25;
        for(int p=0;p<parameterData.dataset_used.size();p++)
        {
            System.out.println(parameterData.dataset_used.get(p));
            type_data_used.addItem(parameterData.dataset_used.get(p));
        
        }
         if(parameterData.dataset_used.size()==0)
             type_data_used.addItem("10cv");
         else
             type_data_used.addActionListener(new Container_Selected_Property_actionAdapter(this,type_data_used,way,origen.dsc.name[i].toString()) );
             
         Vector<String> p = new Vector<String>();
         p.add(type_data_used.getSelectedItem().toString());
         parar_data.insert(p);                          
                    
         
        panel.setBounds(45,pos+35,this.getWidth()-100,fin); 
        jPanel2.add(panel);
        way.insertParameter(parar_data);
                  
        way.problem.addElement(origen.dsc.name[i]+"-"+type_data_used.getSelectedItem().toString());
        problems.addElement(origen.dsc.name[i]+"-"+type_data_used.getSelectedItem().toString());
                    
        return fin+55;
         
    }
    
     public void expan(ActionEvent e,int v,Joint destin,Node destino)
    {
         
          javax.swing.JCheckBox data= new javax.swing.JCheckBox();
          data.setForeground(Color.red);
          data.setSelected(true);
          data.setBounds(1, nexp, 400, 25);              
          data.setText(destin.before.dsc.getName(datano.get(v).get(0))); 
          all_datasets.addElement(destin.before.dsc.getName(datano.get(v).get(0))); 
          fromdata.addElement(true);
          //System.out.println("extiende el "+destin.before.dsc.getName(datano.get(v).get(0)));
          destin.data_selected.addElement(destin.before.dsc.getName(datano.get(v).get(0)));
          
          data.addActionListener(new Container_Selected_data_Show_actionAdapter(this,data,destin));              
          jPanel2.add(data);
          
         int more=0;
         if(destin.before.getTypelqd()==Node.LQD)  
         {              
             more=load_button(datano.get(v).get(0),destin,exp.listData,destino,nexp);
             jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.orange));
         }
                       
         else if(destin.before.getTypelqd()==Node.LQD_C)
         {
             more=load_button(datano.get(v).get(0),destin,exp.listDataLQD_C,destino,nexp);
            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.yellow));
         }
          
         else if(destin.before.getTypelqd()==Node.C_LQD)
         {
             more=load_button(datano.get(v).get(0),destin,exp.listDataC_LQD,destino,nexp);
            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLUE));
         }
          
         else if(destin.before.getTypelqd()==Node.CRISP2)
         {
             more=load_button(datano.get(v).get(0),destin,exp.listDataC,destino,nexp);
            jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(Color.gray));
         }
         
         
         //Reload copia
          
                    
                
         
         
         
         //datano.remove(v);
         expan2.getComponent(v).setVisible(false);
         expan2.setBounds(15, more+nexp, expan2.getWidth(),expan2.getHeight());
         
         nexp=more+nexp;
         
          javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, nexp+expan2.getHeight()+20, Short.MAX_VALUE)
        );
       
         
     }
        
     private int load_button(int i,Joint destin,DatasetXML[] list,Node destino, int nu)
    {
        DinamicParameter parar_data = new DinamicParameter();            
        for (int j = 0; j < list.length; j++) 
        {
            if (list[j].nameAbr.equalsIgnoreCase(destin.before.dsc.name[i]))
            {
                javax.swing.JLabel ins= new javax.swing.JLabel();
                ins.setBounds(100, nu, 100, 25);          
                ins.setText("Instances: "+list[j].nInstances);          
                jPanel2.add(ins);          
                javax.swing.JLabel cla= new javax.swing.JLabel();          
                cla.setBounds(240, nu, 150, 25);          
                cla.setText("Classes: "+list[j].nClasses+" {"+list[j].classes+"}");         
                jPanel2.add(cla);          
                javax.swing.JLabel atri= new javax.swing.JLabel();          
                atri.setBounds(390, nu, 100, 25);                          
                atri.setText("Attributes: "+list[j].nAttributes);          
                jPanel2.add(atri);
                                    
                //Save the instances in the vector          
                Vector<String> parameterI = new Vector<String>();          
                parameterI.addElement(""+list[j].nInstances);          
                parar_data.insert(parameterI);          
                Vector<String> parameterC = new Vector<String>();          
                parameterC.addElement(""+list[j].nClasses);          
                parar_data.insert(parameterC);          
                Vector<String> parameterA = new Vector<String>();          
                parameterA.addElement(""+list[j].nAttributes);          
                parar_data.insert(parameterA);                                    
                parar_data.insert(list[j].classes);
                break;
            }
        }
                    
        int pos=nu;
        //Rest of parameters. Needed by the algorithm
        parameterData = (Parameters) (destino.par.elementAt(0));
                    
                    
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new Color(201, 216, 237)); // NOI18N          
        org.jdesktop.layout.GroupLayout panelLayout = new org.jdesktop.layout.GroupLayout(panel);
        panel.setLayout(panelLayout);
               
        int fin=0;
        for(int pa=0;pa<parameterData.getNumParameters();pa++ )
        {
            Vector<String> p= new Vector<String>();
           
            javax.swing.JLabel par= new javax.swing.JLabel();           
            par.setText("Parameter "+pa+": "+parameterData.descriptions.get(pa));               
            panel.add(par);  
            par.setBounds(50, fin, 150, 25); 
            
            javax.swing.JLabel type= new javax.swing.JLabel();           
            
           /* if(parameterData.parameterType.get(pa).toString().compareTo("list")==0 || 
                    parameterData.parameterType.get(pa).toString().compareTo("List")==0)
            {
                javax.swing.JComboBox value = new javax.swing.JComboBox();
                panel.add(value); 
                value.setBounds(220, fin, 100, 25);  
                value.setVisible(true);     
                
                    System.out.println(parameterData.domain.get(pa));    
                 System.out.println(parameterData.domain.get(pa));    
                for (int v = 0; v < parameterData.getDomain(pa).size(); v++) 
                    value.addItem( parameterData.getDomainValue(pa, v));
                
               
                p.add(value.getSelectedItem().toString());
                
                value.setEnabled(true);
                value.addActionListener(new Container_Selected_Property_actionAdapter(this,value,destin,destin.before.dsc.getName(i)) );
                
                
                
                type.setText(parameterData.parameterType.get(pa).toString());               
            }*/
                
           // else
            //{
                javax.swing.JTextField value = new javax.swing.JTextField();
               int fc=100;
            
               //System.out.println("si es file "+parameterData.descriptions.get(pa)+" y el valor del dataset "+ destin.before.dsc.getName(i));
               if(parameterData.descriptions.get(pa).toString().contains("Files")==true
                       && destin.before.dsc.getName(i).contains("C_LQD")==true)
                {
                   //System.out.println("deberia de entrar "+parameterData.descriptions.get(pa)+" y el valor del dataset "+ destin.before.dsc.getName(i));
                  value.setText("10");
                    type.setText(parameterData.parameterType.get(pa).toString());                
               }
                
               else if(parameterData.descriptions.get(pa).toString().compareTo("Classes")==0)
                {
                    value.setText(parar_data.get(3).toString());
                    value.setEnabled(false);
                    type.setText("Set of "+parameterData.parameterType.get(pa).toString());               
                }
            
                else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0
                && destino.dsc.getSubtype()==Node.type_Preprocess)
                {
                    value.setText("[");
                    for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                    {
                         if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                        {   
                            if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                value.setText(value.getText()+9+"]");
                            else
                                value.setText(value.getText()+5+"]");
                        }
                        else
                        {
                            if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                value.setText(value.getText()+4+",");
                            else
                                value.setText(value.getText()+5+",");
                        }
                    }
                    type.setText("Set of "+parameterData.parameterType.get(pa).toString());               
                }
                   else if(parameterData.descriptions.get(pa).toString().compareTo("Costs")==0
                        && destino.dsc.getSubtype()==Node.type_Method)
                     {
                         //value.setText("BOTON MATRIZ");
                         
                         
                         value.setText("[");
                         
                          for(int f=0;f<Integer.parseInt(parar_data.get(1).get(0));f++)
                          {
                              fc=fc+10;
                              
                            for(int cl=0;cl<Integer.parseInt(parar_data.get(1).get(0));cl++)
                            {
                                 if(f==(Integer.parseInt(parar_data.get(1).get(0)))-1 && cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {   
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+9+"]");
                                    else
                                        value.setText(value.getText()+5+"]");
                                }
                                /* else if(cl==(Integer.parseInt(parar_data.get(1).get(0)))-1)
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+"\n");
                                    else
                                        value.setText(value.getText()+5+"\n");
                                }*/
                                else
                                {
                                    if(Integer.parseInt(parar_data.get(1).get(0))==2)
                                        value.setText(value.getText()+4+",");
                                    else
                                        value.setText(value.getText()+5+",");
                                }
                            }
                            
                          }
                         
                        type.setText("Set of Cost");   
                        
                     }
                else
                {   
                    value.setText(parameterData.getDefaultValue(pa));
                    type.setText(parameterData.parameterType.get(pa).toString());               
                }
                value.setBounds(220, fin, fc, 25);  
                String nombre=destin.before.dsc.name[i].toString();            
                value.addActionListener(new Container_Selected_Value_Show_actionAdapter(this,value,pa+4,destin,nombre));
                panel.add(value);  
                                      
               
            
            
                if(value.getText().contains("{")==true || value.getText().contains("[")==true)
                {
                    int position=0;
                    int inicio=1;
               
                    position=value.getText().toString().indexOf(',',position);
                    while(position!=-1)
                    {
                        p.addElement(value.getText().substring(inicio, position));
                        inicio=position+1;
                        position=value.getText().toString().indexOf(',',position+1);
                    }
                    p.addElement(value.getText().substring(inicio, value.getText().length()-1));
                }
                else
                {
                    p.addElement(value.getText());
                }
            //}
            
             panel.add(type);     
             type.setBounds(240+fc, fin, 75, 25); 
                
            parar_data.insert(p);                          
            fin=fin+25;
        }
        
         //Insert the type of dataset that we can use
        javax.swing.JLabel par= new javax.swing.JLabel();           
        par.setText("Parameter: Dataset ");               
        panel.add(par);  
        par.setBounds(50, fin, 150, 25); 
            
         
         javax.swing.JComboBox type_data_used = new javax.swing.JComboBox();
         panel.add(type_data_used); 
         type_data_used.setBounds(220, fin, 100, 25); 
         type_data_used.setVisible(true);     
         fin=fin+25;
        for(int p=0;p<parameterData.dataset_used.size();p++)
        {
            System.out.println(parameterData.dataset_used.get(p));
            type_data_used.addItem(parameterData.dataset_used.get(p));
        
        }
         if(parameterData.dataset_used.size()==0)
             type_data_used.addItem("10cv");
         
         type_data_used.setEnabled(false);
         
             
         Vector<String> p = new Vector<String>();
         p.add(type_data_used.getSelectedItem().toString());
         parar_data.insert(p); 
         problems.addElement(destin.before.dsc.name[i]+"-"+type_data_used.getSelectedItem().toString());
         destin.problem.addElement(destin.before.dsc.name[i]+"-"+type_data_used.getSelectedItem().toString());
                    
         
        panel.setBounds(45,pos+35,this.getWidth()-100,fin); 
        jPanel2.add(panel);
        destin.insertParameter(parar_data);
        
        
        DinamicParameter contain = new DinamicParameter();            
        
        Vector<String> v1= new Vector<String>();
        for(int va=0;va<parar_data.parameter_data.size();va++)
        {               
            
            for(int v=0;v<parar_data.parameter_data.get(va).size();v++)
             {               
                v1.addElement(parar_data.parameter_data.get(va).get(v));
             }    
        }
                        
        contain.parameter_data.addElement(v1);
                                
        copia.addElement(contain);
        
                  
                    
        return fin+55;
         
    }
    public void check(ActionEvent e,javax.swing.JCheckBox data_selected,Joint destin)
    {
       
       if(data_selected.isSelected()==false)
       {
             if(destin.data_selected.size()==1) 
           {
               JOptionPane.showMessageDialog(this, "At least one dataset must be selected",
                    "Datasets selected", JOptionPane.ERROR_MESSAGE);
               data_selected.setSelected(true);
           }
        else{
           
            int position=0;
            for(int i=0;i<destin.data_selected.size();i++)
            {
                if(destin.data_selected.get(i).compareTo(data_selected.getText())==0)
                {
                    position=i;
                    break;
                }
                    
            }
            destin.removeParameters(position);
            destin.removeDataset(position);
            destin.removeProblem(position);
            data_selected.setForeground(Color.black);
        }
            
       }
       else
       {
            destin.insertDataSelected(data_selected.getText());
            
            
            
            
            for(int i=0;i<orig.dsc.name.length;i++)
            {
                if(orig.dsc.name[i].compareTo(data_selected.getText())==0)
                {
                    destin.insertproblem(problems.get(i));
                    destin.insertParameter(copia.get(i));
                    break;
                }
            }
            
            data_selected.setForeground(Color.red);
            
       }
       
       
       destin.information();
       
    }
    
    
     public void check_show(ActionEvent e,javax.swing.JCheckBox data_selected,Joint destin)
    {
       destin.information();
       
       if(data_selected.isSelected()==false)
       {
            if(destin.data_selected.size()==1) 
           {
               JOptionPane.showMessageDialog(this, "At least one dataset must be selected",
                    "Datasets selected", JOptionPane.ERROR_MESSAGE);
               data_selected.setSelected(true);
           }
        else{
            int position=0;
            for(int i=0;i<destin.data_selected.size();i++)
            {
                if(destin.data_selected.get(i).compareTo(data_selected.getText())==0)
                {
                    position=i;
                    break;
                }
                    
            }
            destin.removeParameters(position);
            destin.removeDataset(position);
            destin.removeProblem(position);
            data_selected.setForeground(Color.black);
        }
            
       }
       else
       {
            destin.insertDataSelected(data_selected.getText());
            
            
            
            for(int i=0;i<all_datasets.size();i++)
            {
                if(all_datasets.get(i).compareTo(data_selected.getText())==0 && fromdata.get(i))
                {

                    destin.insertproblem(problems.get(i));
                    destin.insertParameter(copia.get(i));
                    break;
                    
                }
            }
            
            data_selected.setForeground(Color.red);
            
       }
       
       
       destin.information();
       
    }
    
    public void value(ActionEvent e,javax.swing.JTextField value,int pa,Joint destin,String nombre)
    {
        
      // System.out.println("estmaos modificando "+value.getText()+" del dataset "+nombre+ " y es el parametro "+pa);
       
           int posi=0;
            for(int i=0;i<destin.data_selected.size();i++)
            {
                if(destin.data_selected.get(i).compareTo(nombre)==0)
                {
                    posi=i;
                 //   System.out.println("la position es del datasetttttttttt  "+posi);
                    break;
                }
                    
            }
        
     
         
            Vector<String> p  = new Vector<String>();
            if(value.getText().contains("{")==true || value.getText().contains("[")==true)
            {
               int position=0;
               int inicio=1;
               
               position=value.getText().toString().indexOf(',',position);
               while(position!=-1)
               {
                  p.addElement(value.getText().substring(inicio, position));
                  inicio=position+1;
                  position=value.getText().toString().indexOf(',',position+1);
               }
                  p.addElement(value.getText().substring(inicio, value.getText().length()-1));
            }
            else
            {
                p.addElement(value.getText());
            }
           
           if(validation(p, parameterData.parameterType.get(pa).toString(),parameterData.descriptions.get(pa).toString(),pa,value)==true)
           {
                destin.getParametersP(posi).set(pa+4,p);
                value.setBackground(new Color(255, 253, 202));
            
                for(int i=0;i<orig.dsc.name.length;i++)
                {
                    if(orig.dsc.name[i].compareTo(nombre)==0)
                    {
                        copia.set(i, destin.getParametersP(posi));
                        break;
                    }
                }
           }
           
           
  
    }
    public boolean validation(Vector<String> p,String type,String description, int pa,javax.swing.JTextField value)
    {
        
       
                 
        //System.out.println("la descripcion es "+description);
          //      System.out.println("y el tipo "+type);
        System.out.println(parameterData.domain.get(pa));               
        //for (int v = 0; v < parameterData.getDomain(pa).size(); v++) 
           
        System.out.println("the ] is "+p.get(0).toString().indexOf(']'));
            System.out.println(" the length is "+(p.get(0).length()));
            if((p.get(0).toString().indexOf(']')<p.get(0).toString().length())
                    && p.get(0).toString().indexOf(']')!=-1 )
            {
             
                JOptionPane.showMessageDialog(this,"Introduce the value between the []", "Value incorrected", 2);          
                return false;
            }
        
            else if((type.compareTo("integer")==0 || type.compareTo("Integer")==0 
                || type.compareTo("real")==0 || type.compareTo("Real")==0) 
                && description.contains("Costs")==false && description.contains("NP")==false 
                && description.contains("MP")==false)
        {
            //control that the number does not contain letter (we have to do id)
                
            if(Float.parseFloat(p.get(0))<Float.parseFloat(parameterData.getDomainValue(pa, 0))
                || Float.parseFloat(p.get(0))>Float.parseFloat(parameterData.getDomainValue(pa, 1))) 
                {
                    value.setText(parameterData.getDomainValue(pa, 0));
                     JOptionPane.showMessageDialog(this,"The value inserted is incorrect. Must be contained between "+parameterData.getDomainValue(pa, 0) +" and "+parameterData.getDomainValue(pa, 1), "Value incorrected", 2);          
                     return false;
                }
        }
        
        else if (type.compareTo("List")==0 || type.compareTo("list")==0 )
        {
            boolean found=false;
            for (int v = 0; v < parameterData.getDomain(pa).size(); v++)
            {
                if(p.get(0).compareTo(parameterData.getDomainValue(pa, v))==0)    
                {
                 found=true;
                 break;
                }
            }
            if(found==false)
            {
                value.setText(parameterData.getDomainValue(pa, 0));
               JOptionPane.showMessageDialog(this,"The value inserted is incorrect. Must be "+parameterData.domain.get(pa), "Value incorrected", 2);          
                     return false;  
            }
        }
                
                
        
         
          
        
        //}
        return true;
    }
     public void value_show(ActionEvent e,javax.swing.JTextField value,int pa,Joint destin,String nombre)
    {
        
     //  System.out.println("estmaos en SHOWWWWWWWW modificando "+value.getText()+" del dataset "+nombre+ " y es el parametro "+pa);
       
           int posi=0;
            for(int i=0;i<destin.data_selected.size();i++)
            {
                if(destin.data_selected.get(i).compareTo(nombre)==0)
                {
                    posi=i;
                    break;
                }
                    
            }
        
        //Falta comprobar que value sea el tipo correcto y que tenga tantas variables
        //como debe cada parametro (tantas como antes tenia guardadas)
           
            
            Vector<String> p  = new Vector<String>();
            if(value.getText().contains("{")==true || value.getText().contains("[")==true)
            {
               int position=0;
               int inicio=1;
               
               position=value.getText().toString().indexOf(',',position);
               while(position!=-1)
               {
                  p.addElement(value.getText().substring(inicio, position));
                  inicio=position+1;
                  position=value.getText().toString().indexOf(',',position+1);
               }
                  p.addElement(value.getText().substring(inicio, value.getText().length()-1));
            }
            else
            {
                p.addElement(value.getText());
            }
           
            
            if(validation(p, parameterData.parameterType.get(pa-4).toString(),parameterData.descriptions.get(pa-4).toString(),pa-4,value)==true)
           {
            destin.getParametersP(posi).set(pa,p);
            value.setBackground(new Color(255, 253, 202));
            
            
            
            DinamicParameter contain = new DinamicParameter();
             
            for(int i=0;i<all_datasets.size();i++)
            {
                if(all_datasets.get(i).compareTo(nombre)==0)
                {
                    for(int j=0;j<destin.getParametersP(posi).size();j++)
                    {
                        Vector<String> v= new Vector<String>();
                        for(int k=0;k<destin.getParametersP(posi).get(j).size();k++)
                            v.addElement(destin.getParametersP(posi).get(j).get(k));
                        contain.parameter_data.addElement(v);    
                    }
                    
                    copia.set(i,contain);
                    break;
                }
            }
            }
            
          
        
      
    }
     
      public void value_show_ant(ActionEvent e,javax.swing.JTextField value,int pa,Joint destin,String nombre)
    {
        
       //System.out.println("estmaos en SHOWWWWWWWW anttt modificando "+value.getText()+" del dataset "+nombre+ " y es el parametro "+pa);
       int posi=-1;
       
        for(int j=0;j<destin.data_selected.size();j++)
        {
           if(destin.problem.get(j).contains(nombre)==true)
           {
               posi=j;
               break;
           }
        }
       
       /*for(int pro=0;pro<problems.size();pro++)
       {
          
           if(problems.get(pro).contains(nombre)==true)
           {
              posi=pro;
              break;
           }
       } */  
       
       
          // System.out.println("position es "+posi+" tamÃ±ano es "+destin.parameters.size());
        
        //Falta comprobar que value sea el tipo correcto y que tenga tantas variables
        //como debe cada parametro (tantas como antes tenia guardadas)
           
            
            Vector<String> p  = new Vector<String>();
            if(value.getText().contains("{")==true || value.getText().contains("[")==true)
            {
               int position=0;
               int inicio=1;
               
               position=value.getText().toString().indexOf(',',position);
               while(position!=-1)
               {
                  p.addElement(value.getText().substring(inicio, position));
                  inicio=position+1;
                  position=value.getText().toString().indexOf(',',position+1);
               }
                  p.addElement(value.getText().substring(inicio, value.getText().length()-1));
            }
            else
            {
                p.addElement(value.getText());
            }
           
            if(validation(p, parameterData.parameterType.get(pa-4).toString(),parameterData.descriptions.get(pa-4).toString(),pa-4,value)==true)
           {
            destin.getParametersP(posi).set(pa,p);
            value.setBackground(new Color(255, 253, 202));
            
            
            
            DinamicParameter contain = new DinamicParameter();
             
            for(int i=0;i<all_datasets.size();i++)
            {
                if(all_datasets.get(i).compareTo(nombre)==0)
                {
                    for(int j=0;j<destin.getParametersP(posi).size();j++)
                    {
                        Vector<String> v= new Vector<String>();
                        for(int k=0;k<destin.getParametersP(posi).get(j).size();k++)
                            v.addElement(destin.getParametersP(posi).get(j).get(k));
                        contain.parameter_data.addElement(v);    
                    }
                    
                    copia.set(i,contain);
                    break;
                }
            }
            
            }
        
      
    }
    
     public void properties(ActionEvent e,javax.swing.JComboBox value,Joint destin,String nombre)
    {
        
      //System.out.println("estmaos modificando "+value.getSelectedItem().toString()+" del dataset "+nombre);
       
           int posi=0;
            for(int i=0;i<destin.data_selected.size();i++)
            {
               // System.out.println("el nombre de los data que tenemos  "+destin.data_selected.get(i));
                if(destin.data_selected.get(i).compareTo(nombre)==0)
                {
                    posi=i;
                   //System.out.println("la position es del datasetttttttttt  "+posi);
                    break;
                }
                    
            }
                   
            
            Vector<String> p  = new Vector<String>();
                p.addElement(value.getSelectedItem().toString());
            
           
            destin.getParametersP(posi).set(destin.getParametersP(posi).size()-1,p);
            destin.problem.set(posi,destin.data_selected.get(posi)+"-"+value.getSelectedItem().toString());
            value.setBackground(new Color(255, 253, 202));
            
            
            
            if(orig!=null)
            {
            for(int i=0;i<orig.dsc.name.length;i++)
            {
                if(orig.dsc.name[i].compareTo(nombre)==0)
                {
                    copia.set(i, destin.getParametersP(posi));
                    break;
                }
            }
            }
            else
            {
              //  System.out.println(" si que es null");
                 DinamicParameter contain = new DinamicParameter();
             
                for(int i=0;i<all_datasets.size();i++)
                {
                    if(all_datasets.get(i).compareTo(nombre)==0)
                    {
                        for(int j=0;j<destin.getParametersP(posi).size();j++)
                        {
                            Vector<String> v= new Vector<String>();
                            for(int k=0;k<destin.getParametersP(posi).get(j).size();k++)
                                v.addElement(destin.getParametersP(posi).get(j).get(k));
                            contain.parameter_data.addElement(v);    
                        }
                    
                        copia.set(i,contain);
                        break;
                    }
                }
            
            }
  
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();

        jFrame1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(keel.GraphInterKeel.datacf.DataCFApp.class).getContext().getResourceMap(Container_Selected.class);
        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setForeground(resourceMap.getColor("jButton1.foreground")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jPanel2.setAutoscrolls(true);
        jPanel2.setName("jPanel2"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 551, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(207, 207, 207)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(271, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        this.setVisible(false);
        
        
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    /**

     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {                
                
                Container_Selected dialog = new Container_Selected(new javax.swing.JFrame(), true);//,"x",n,m,d,0);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}


class Container_Selected_datano_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public int data_selected;
    public Joint destin;
    public Node destino;


   
   

    Container_Selected_datano_actionAdapter(Container_Selected adaptee,int v,
            Joint a,Node des) {
        this.adaptee = adaptee;
        this.data_selected = v;
        this.destin=a;
        this.destino=des;
    }

    public void actionPerformed(ActionEvent e) {
       adaptee.expan(e, data_selected, destin, destino);
    }

   /* public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
} 
class Container_Selected_data_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public javax.swing.JCheckBox data_selected;
    public Joint destin;
   

    Container_Selected_data_actionAdapter(Container_Selected adaptee,javax.swing.JCheckBox data,
            Joint a) {
        this.adaptee = adaptee;
        this.data_selected = data;
        this.destin=a;
    }

    public void actionPerformed(ActionEvent e) {
       adaptee.check(e, data_selected, destin);
    }

   /* public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}

class Container_Selected_data_Show_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public javax.swing.JCheckBox data_selected;
    public Joint destin;
   

    Container_Selected_data_Show_actionAdapter(Container_Selected adaptee,javax.swing.JCheckBox data,
            Joint a) {
        this.adaptee = adaptee;
        this.data_selected = data;
        this.destin=a;
    }

    public void actionPerformed(ActionEvent e) {
      
            adaptee.check_show(e, data_selected, destin);
    }

   /* public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
}
 
class Container_Selected_Value_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public int parametro;
    public Joint destin;
    public String dataset;
    javax.swing.JTextField value;

    
   
    Container_Selected_Value_actionAdapter(Container_Selected aThis, JTextField valu, int pa, Joint a,
            String nombre) {
         this.adaptee = aThis;
        this.parametro=pa;
        this.destin=a;
        this.dataset =   nombre;
        this.value=valu;
    }

 

    public void actionPerformed(ActionEvent e) {
       adaptee.value(e, value, parametro, destin, dataset);
    }
}
    
    
    class Container_Selected_Value_Show_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public int parametro;
    public Joint destin;
    public String dataset;
    javax.swing.JTextField value;

    
   
    Container_Selected_Value_Show_actionAdapter(Container_Selected aThis, JTextField valu, int pa, Joint a,
            String nombre) {
         this.adaptee = aThis;
        this.parametro=pa;
        this.destin=a;
        this.dataset =   nombre;
        this.value=valu;
    }

 

    public void actionPerformed(ActionEvent e) {
       adaptee.value_show(e, value, parametro, destin, dataset);
    }
    }
    
       class Container_Selected_Value_Show_Ant_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public int parametro;
    public Joint destin;
    public String dataset;
    javax.swing.JTextField value;

    
   
    Container_Selected_Value_Show_Ant_actionAdapter(Container_Selected aThis, JTextField valu, int pa, Joint a,
            String nombre) {
         this.adaptee = aThis;
        this.parametro=pa;
        this.destin=a;
        this.dataset =   nombre;
        this.value=valu;
    }

 

    public void actionPerformed(ActionEvent e) {
       adaptee.value_show_ant(e, value, parametro, destin, dataset);
    }
    }
    
     class Container_Selected_Property_actionAdapter implements ActionListener {

    private Container_Selected adaptee;
    public Joint destin;
    javax.swing.JComboBox value;
    String dataset;

    
   
    Container_Selected_Property_actionAdapter(Container_Selected aThis, JComboBox valu, Joint a, String nombre) {
         this.adaptee = aThis;
        this.destin=a;
        this.value=valu;
        this.dataset=nombre;
    }

 

    public void actionPerformed(ActionEvent e) {
       adaptee.properties(e, value, destin,dataset);
    }
    }

   /* public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/

