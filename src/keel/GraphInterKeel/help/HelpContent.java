package keel.GraphInterKeel.help;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.io.IOException;

public class HelpContent extends JPanel {


  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JEditorPane contenido = new JEditorPane();

  public HelpContent() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    contenido.setFont(new java.awt.Font("Arial", 0, 11));
    contenido.setEditable(false);
    this.setFont(new java.awt.Font("Arial", 0, 11));
    jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11));
    this.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(contenido, null);
  }

  public void muestraURL(URL url) {
    try {
      contenido.setPage(url);
    } catch (IOException e) { }
  }
}