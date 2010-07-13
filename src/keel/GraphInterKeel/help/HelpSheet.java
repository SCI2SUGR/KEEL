/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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
