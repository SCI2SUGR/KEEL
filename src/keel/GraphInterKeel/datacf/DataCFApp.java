/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

package keel.GraphInterKeel.datacf;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * <p>
 * @author Written by Juan Carlos Fern√°ndez and Pedro Antonio Guti√©rrez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class DataCFApp extends SingleFrameApplication {

    /**
     * <p>
     * The main class of the application.
     * </p>
     */
    /**
     * <p>
     * At startup create and show the main frame of the application.
     * </p>
     */
    @Override
    protected void startup() {
        //show(new DataCFView(this));
    }

    /**
     * <p>
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root Root window
     * </p>
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * <p>
     * A convenient static getter for the application instance.
     * @return the instance of DataCFApp
     * </p>
     */
    public static DataCFApp getApplication() {
        return Application.getInstance(DataCFApp.class);
    }

    /**
     * <p>
     * Main method launching the application.
     * @param args Arguments of the program
     * </p>
     */
    public static void main(String[] args) {
        launch(DataCFApp.class, args);

    }
}

