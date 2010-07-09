package keel.GraphInterKeel.help;

import java.net.URL;

public class HelpSheet {

  public String nombre;
  public URL direccion;

  public HelpSheet(String nombre, String fichero) {
    this.nombre = nombre;
    String prefix = "file:"
        + System.getProperty("user.dir")
        + System.getProperty("file.separator");
    try {
      direccion = new URL(prefix + fichero);
    }
    catch (java.net.MalformedURLException exc) {
      direccion = null;
    }
  }

  public HelpSheet(String nombre, URL fichero) {
    this.nombre = nombre;
//    String[] fields = fichero.getFile().split("/");
//    this.nombre = fields[fields.length - 1];
    direccion = fichero;
  }

  public String toString() {
    return nombre;
  }
}