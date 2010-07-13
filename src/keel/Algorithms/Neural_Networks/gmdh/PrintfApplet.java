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

package keel.Algorithms.Neural_Networks.gmdh;


//
// (c) 2000 Sun Microsystems, Inc.
// ALL RIGHTS RESERVED
//
// License Grant-
//
//
// Permission to use, copy, modify, and distribute this Software and its
// documentation for NON-COMMERCIAL or COMMERCIAL purposes and without fee is
// hereby granted.
//
// This Software is provided "AS IS".  All express warranties, including any
// implied warranty of merchantability, satisfactory quality, fitness for a
// particular purpose, or non-infringement, are disclaimed, except to the extent
// that such disclaimers are held to be legally invalid.
//
// You acknowledge that Software is not designed, licensed or intended for use in
// the design, construction, operation or maintenance of any nuclear facility
// ("High Risk Activities").  Sun disclaims any express or implied warranty of
// fitness for such uses.
//
// Please refer to the file http://www.sun.com/policies/trademarks/ for further
// important trademark information and to
// http://java.sun.com/nav/business/index.html for further important licensing
// information for the Java Technology.
//


import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public class PrintfApplet extends Applet {
  public void init() {
    setLayout(new BorderLayout());
    Panel pCenter = new Panel();
    initInputPanel(pCenter);
    add(pCenter,BorderLayout.CENTER);
    Panel pSouth = new Panel();
    initOutputPanel(pSouth);
    add(pSouth,BorderLayout.SOUTH);
  }
//public static void main(String[] args) {
//  java.awt.AppletFrame ap =
//    new java.awt.AppletFrame(new PrintfApplet(),620,400);
//}
  private void initInputPanel(Panel p) {
    GridBagConstraints gbc=
      new GridBagConstraints();
    p.setLayout(new GridBagLayout());
    gbc.weightx=100;
    gbc.weighty=100;
    gbc.gridwidth=1;
    gbc.gridheight=1;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    gbc.anchor=GridBagConstraints.WEST;

    gbc.gridx=0;
    gbc.gridy=0;
    Label typeLabel=new Label("Type");
    p.add(typeLabel,gbc);
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    Label valueLabel=new Label("Value");
    p.add(valueLabel,gbc);

    gbc.gridx=0;
    gbc.gridy=1;
    Choice type=new Choice();
    type.add("Byte");
    type.add("Short");
    type.add("Character");
    type.add("Integer");
    type.add("Long");
    type.add("Float");
    type.add("Double");
    type.add("String");
    type.add("Object");
    type.select("Float");
    type.addItemListener(new TypeChoiceCommand());
    p.add(type,gbc);
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    value=new ValueChoice();
    p.add(value,gbc);

    gbc.gridx=0;
    gbc.gridy=2;
    gbc.gridwidth=1;
    gbc.fill=GridBagConstraints.NONE;
    Button reset=new Button("Reset");
    reset.addActionListener(new ResetCommand());
    p.add(reset,gbc);
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    Button position=new Button("Add");
    position.addActionListener(new AddCommand());
    p.add(position,gbc);

	gbc.gridx=0;
	gbc.gridy=3;
    gbc.gridwidth=1;
    gbc.gridheight=GridBagConstraints.REMAINDER;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    Label format = new Label("Control String");
    p.add(format,gbc);
	gbc.gridx=1;
    TextField formatString = new TextField(40);
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    formatString.addTextListener(new FormatCommand());
    p.add(formatString,gbc);
  }
  private class ResetCommand implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      currentPos=0;
      objectCount.setText(
        (new Integer(currentPos)).toString()+" objects");
    }
  }
  private class AddCommand implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
      val[currentPos%val.length]=value.getValue();
      currentPos++;
      String s=(new Integer(currentPos)).toString()+
        " object"+((currentPos!=1)?"s":"");
      objectCount.setText(s);
    }
  }
  private class FormatCommand implements TextListener {
    public void textValueChanged(TextEvent evt) {
      controlString=
        ((TextField)evt.getSource()).getText();
    }
  }
  private class TypeChoiceCommand
         implements ItemListener {
    public void itemStateChanged(ItemEvent evt) {
      String s = (String)evt.getItem();
      if (s.equals("Byte")) {
        if (currentValueType!=PrintfApplet.INT) {
          value.intChoice();
          currentValueType=PrintfApplet.INT;
        }
        currentType=PrintfApplet.BYTE;
      }
      else if (s.equals("Short")) {
        if (currentValueType!=PrintfApplet.INT) {
          value.intChoice();
          currentValueType=PrintfApplet.INT;
        }
        currentType=PrintfApplet.SHORT;
      }
      else if (s.equals("Character")) {
        if (currentValueType!=PrintfApplet.INT) {
          value.intChoice();
          currentValueType=PrintfApplet.INT;
        }
        currentType=PrintfApplet.CHAR;
      }
      else if (s.equals("Integer")) {
        if (currentValueType!=PrintfApplet.INT) {
          value.intChoice();
          currentValueType=PrintfApplet.INT;
        }
        currentType=PrintfApplet.INT;
      }
      else if (s.equals("Long")) {
        if (currentValueType!=PrintfApplet.INT) {
          value.intChoice();
          currentValueType=PrintfApplet.INT;
        }
        currentType=PrintfApplet.LONG;
      }
      else if (s.equals("Float")) {
        if (currentValueType!=PrintfApplet.FLOAT) {
          value.floatChoice();
          currentValueType=PrintfApplet.FLOAT;
        }
        currentType=PrintfApplet.FLOAT;
      }
      else if (s.equals("Double")) {
        if (currentValueType!=PrintfApplet.FLOAT) {
          value.floatChoice();
          currentValueType=PrintfApplet.FLOAT;
        }
        currentType=PrintfApplet.DOUBLE;
      }
      else if (s.equals("String")) {
        if (currentValueType!=PrintfApplet.STRING) {
          value.stringChoice();
          currentValueType=PrintfApplet.STRING;
        }
        currentType=PrintfApplet.STRING;
      }
      else if (s.equals("Object")) {
        if (currentValueType!=PrintfApplet.OBJECT) {
          value.objectChoice();
          currentValueType=PrintfApplet.OBJECT;
        }
        currentType=PrintfApplet.OBJECT;
      }
    }
  }
  private void initOutputPanel(Panel p) {
    String blanks=
      "                                        ";
    GridBagConstraints gbc=
      new GridBagConstraints();
    p.setLayout(new GridBagLayout());
    gbc.weightx=10;
    gbc.weighty=100;
    gbc.gridx=0;
    gbc.gridy=0;
    gbc.gridwidth=1;
    gbc.gridheight=1;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    gbc.anchor=GridBagConstraints.WEST;

    Button position=new Button("Format");
    position.addActionListener(new PrintfCommand());
    p.add(position,gbc);

    gbc.weightx=100;
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    objectCount=new Label("0 objects");
    objectCount.setBackground(Color.white);
    p.add(objectCount,gbc);

    gbc.weightx=10;
    gbc.gridx=0;
    gbc.gridy=1;
    gbc.gridwidth=1;
    Label jLabel = new Label("Output");
    p.add(jLabel,gbc);
    gbc.weightx=100;
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    jOutput = new Label(blanks);
    jOutput.setBackground(Color.white);
    p.add(jOutput,gbc);

    gbc.weightx=10;
    gbc.gridx=0;
    gbc.gridy=2;
    gbc.gridheight=GridBagConstraints.REMAINDER;
    gbc.gridwidth=1;
    Label eLabel = new Label("Errors");
    p.add(eLabel,gbc);
    gbc.weightx=100;
    gbc.gridx=1;
    gbc.gridwidth=GridBagConstraints.REMAINDER;
    eOutput = new Label(blanks);
    eOutput.setBackground(Color.white);
    p.add(eOutput,gbc);
  }
  private class PrintfCommand implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
       try {
         PrintfFormat pf =
           new PrintfFormat(controlString);
         Object[] o = new Object[currentPos];
         for (int ii=0; ii<currentPos; ii++)
           o[ii]=val[ii];
         String s = pf.sprintf(o);
         jOutput.setText(s);
         eOutput.setText("");
       }
       catch(Exception e) {
         jOutput.setText("");
         eOutput.setText(e.getMessage());
       }
    }
  }
  private class ValueChoice extends Choice {
    ValueChoice() {
      floatChoice();
      currentValueType=PrintfApplet.FLOAT;
      currentType=PrintfApplet.DOUBLE;
      select("0.0");
    }
    Object getValue() {
      Object ret = null;
      int ii=0;
      long lv=0L;
      double dv=0.0;
      Object ov=null;
      String sv=null;
      String s = value.getSelectedItem();
      switch(currentType) {
      case PrintfApplet.BYTE:
      case PrintfApplet.SHORT:
      case PrintfApplet.CHAR:
      case PrintfApplet.INT:
      case PrintfApplet.LONG:
        for (ii=0; ii<intChoiceArray.length; ii++)
          if (intChoiceArray[ii].equals(s)) break;
        lv = intArray[ii];
        break;
      case PrintfApplet.FLOAT:
      case PrintfApplet.DOUBLE:
        for (ii=0; ii<floatChoiceArray.length; ii++)
          if (floatChoiceArray[ii].equals(s)) break;
        dv = floatArray[ii];
        break;
      case PrintfApplet.STRING:
        for (ii=0; ii<floatChoiceArray.length; ii++)
          if (stringChoiceArray[ii].equals(s)) break;
        sv = stringArray[ii];
        break;
      case PrintfApplet.OBJECT:
        for (ii=0; ii<objectChoiceArray.length; ii++)
          if (objectChoiceArray[ii].equals(s)) break;
        ov = objectArray[ii];
      }
      switch(currentType) {
      case PrintfApplet.BYTE:
        ret = new Byte((byte)lv);
        break;
      case PrintfApplet.SHORT:
        ret = new Short((short)lv);
        break;
      case PrintfApplet.CHAR:
        ret = new Character((char)lv);
        break;
      case PrintfApplet.INT:
        ret = new Integer((int)lv);
        break;
      case PrintfApplet.LONG:
        ret = new Long(lv);
        break;
      case PrintfApplet.FLOAT:
        ret = new Float((float)dv);
        break;
      case PrintfApplet.DOUBLE:
        ret = new Double(dv);
        break;
      case PrintfApplet.STRING:
        ret = sv;
        break;
      case PrintfApplet.OBJECT:
        ret = ov;
        break;
      }
      return ret;
    }
    void intChoice() {
      removeAll();
      for (int ii=0; ii<intChoiceArray.length; ii++)
        add(intChoiceArray[ii]);
    }
    void floatChoice() {
      removeAll();
      for (int ii=0; ii<floatChoiceArray.length; ii++)
        add(floatChoiceArray[ii]);
    }
    void stringChoice() {
      removeAll();
      for (int ii=0; ii<stringChoiceArray.length; ii++)
        add(stringChoiceArray[ii]);
    }
    void objectChoice() {
      removeAll();
      for (int ii=0; ii<objectChoiceArray.length; ii++)
        add(objectChoiceArray[ii]);
    }
    long[] intArray = {
      Long.MIN_VALUE,
      Integer.MIN_VALUE,
      Short.MIN_VALUE,
      Byte.MIN_VALUE,
      -1,
      0,
      1,
      Byte.MAX_VALUE,
      Short.MAX_VALUE,
      Character.MAX_VALUE,
      Integer.MAX_VALUE,
      Long.MAX_VALUE };
    String[] intChoiceArray = {
      "Long.MIN_VALUE",
      "Integer.MIN_VALUE",
      "Short.MIN_VALUE",
      "Byte.MIN_VALUE",
      "-1",
      "0",
      "1",
      "Byte.MAX_VALUE",
      "Short.MAX_VALUE",
      "Character.MAX_VALUE",
      "Integer.MAX_VALUE",
      "Long.MAX_VALUE" };
    double[] floatArray = {
      Double.NEGATIVE_INFINITY,
      -Double.MAX_VALUE,
      -Float.MAX_VALUE,
      -1.0,
      -Float.MIN_VALUE,
      -Double.MIN_VALUE,
      0.0,
      Double.MIN_VALUE,
      Float.MIN_VALUE,
      0.99999,
      1.0,
      1.00001,
      Float.MAX_VALUE,
      Double.MAX_VALUE,
      Double.POSITIVE_INFINITY,
      Double.NaN };
    String[] floatChoiceArray = {
      "Double.NEGATIVE_INFINITY",
      "-Double.MAX_VALUE",
      "-Float.MAX_VALUE",
      "-1.0",
      "-Float.MIN_VALUE",
      "-Double.MIN_VALUE",
      "0.0",
      "Double.MIN_VALUE",
      "Float.MIN_VALUE",
      "0.99999",
      "1.0",
      "1.00001",
      "Float.MAX_VALUE",
      "Double.MAX_VALUE",
      "Double.POSITIVE_INFINITY",
      "Double.NaN" };
    String[] stringArray = {
      "printf",
      "In the middle of the journey of our life,"+
        "I came to myself in a dark wood,"+
          "for the straight way was lost.",
      "" };
    String[] stringChoiceArray = {
      "\"printf\"",
      "\"In the middle of the journey of our life,"+
        "I came to myself in a dark wood,"+
          "for the straight way was lost.\"",
      "\"\"" };
    Object[] objectArray = {
      new Object(),
      new ArithmeticException(),
      new ArithmeticException("1"),
      new Boolean(true),
      new Byte((byte)3),
      new Short((short)4),
      new Character('a'),
      new Integer(6),
      new Long(7L),
      new Float(8.f),
      new Double(9.) };
    String[] objectChoiceArray = {
      "Object()",
      "ArithmeticException()",
      "ArithmeticException(\"1\")",
      "Boolean(true)",
      "Byte(3)",
      "Short(4)",
      "Character(\'a\')",
      "Integer(6)",
      "Long(7)",
      "Float(8.f)",
      "Double(9.)" };
  }
  Label objectCount=null;
  ValueChoice value=null;
  Label eOutput=null;
  Label jOutput=null;
  private int currentPos=0;
  private int currentType=PrintfApplet.INT;
  private int currentValue=0;
  private int currentValueType=PrintfApplet.INT;
  private String controlString="";
  private Object val[]=new Object[100];
  private final static int BYTE=0;
  private final static int SHORT=1;
  private final static int CHAR=2;
  private final static int INT=3;
  private final static int LONG=4;
  private final static int FLOAT=5;
  private final static int DOUBLE=6;
  private final static int STRING=7;
  private final static int OBJECT=8;
}

