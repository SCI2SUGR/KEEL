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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Alatasetal;

import java.util.*;

/**
 * Class for timing applications. Provides a simple interface for 
 * the basic functionality of a stop watch and printing the results.
 *
 * @author Michael Holler
 * @version 0.1, 11.03.2004 
 */
public class StopWatch {

  private Vector laps;
  private boolean running;

  /**
   * Basic constructor for getting an instance of a StopWatch.
   */
  public StopWatch() {
    this.laps = new Vector();
    this.running = false;
  }

  /**
   * Starts the stop watch, saves the start time and clears old times.
   */
  public void start() {
    this.laps.clear();
    this.running = true;
    this.laps.add(new Long(System.currentTimeMillis()));
  }

  /**
   * Stops the watch and saves the end time.
   */
  public void stop() {
    this.laps.add(new Long(System.currentTimeMillis()));
    this.running = false;
  }

  /**
   * Saves a lap time if the watch is running.
   */
  public void lap() {
    if (running)
      this.laps.add(new Long(System.currentTimeMillis()));
  }
  
  /**
   * Prints out the lap times and the total time.
   */
  public void print() {
    StringBuffer print = new StringBuffer("\n");

    if (this.laps.size() > 2) {
      Enumeration e = this.laps.elements();
      Long millis = (Long)e.nextElement();
      int i = 1;
      while (!millis.equals((Long)this.laps.lastElement())) {
        Long tmp = new Long(millis.longValue());
        millis = (Long)e.nextElement();
        print.append("lap " + i +": ");
        print.append(delta(tmp, millis));
        print.append(" seconds\n");
	i++;
      }
    }
    
    print.append(delta((Long)this.laps.firstElement(), (Long)this.laps.lastElement()));
    print.append(" seconds\n");
    
    System.out.println(print.toString());
  }

  /**
   * Prints out the lap time of the last recorded lap.
   */
  public void printLap() {
    StringBuffer print = new StringBuffer("last lap: ");
    print.append(delta((Long)this.laps.elementAt(this.laps.size()-2), (Long)this.laps.lastElement()));
    print.append(" seconds");
    
    System.out.println(print.toString());
  }

  /**
   * Calculates the difference between the two times given as parameter.
   *
   * @param	millis1		the first time point
   * @param	millis2		the second time point
   * @return			the difference of te two
   */
  private double delta(Long millis1, Long millis2) {
    return ((millis2.longValue() - millis1.longValue())*1.0/1000);
  }
}
