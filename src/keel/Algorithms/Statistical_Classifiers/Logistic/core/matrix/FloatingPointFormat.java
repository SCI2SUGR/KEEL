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
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or (at
 *    your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful, but
 *    WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */

/*
 *    FloatingPoint.java
 *    Copyright (C) 2002 University of Waikato, Hamilton, New Zealand
 *
 */

package keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * Class for the format of floating point numbers
 *
 * @author Yong Wang
 * @version $Revision: 1.1 $
 */
public class FloatingPointFormat
  extends DecimalFormat {

  /** for serialization */
  private static final long serialVersionUID = 4500373755333429499L;
    
    /**
     * Decimal format.
     */
    protected DecimalFormat nf ;

    /**
     * Total width of the integer number.
     */
    protected int width;

    /**
     * Decimal size.
     */
    protected int decimal;

    /**
     * Trailing flag.
     */
    protected boolean trailing = true;

  /**
   * Default constructor. Creates a FloatingPointFormat with 8 of width value and 5 decimal size.
     */
  public FloatingPointFormat () {
    this( 8, 5 );
  }

    /**
     * Constructor. Creates a FloatingPointFormat with 8 of width value and 5 decimal size.
     * @param digits given digits.
     */
    public FloatingPointFormat ( int digits ) {
    this( 8, 2 );
  }

    /**
     * Constructor. Creates a FloatingPointFormat with the given values width and decimal size.
     * @param w width given.
     * @param d decimal size given.
     */
    public FloatingPointFormat( int w, int d ) {
    width = w;
    decimal = d;
    nf = new DecimalFormat( pattern(w, d) );
    nf.setPositivePrefix(" ");
    nf.setNegativePrefix("-");
  }

    /**
     * Constructor. Creates a FloatingPointFormat with the given values width and decimal size.
     * @param w width given.
     * @param d decimal size given.
     * @param trailingZeros trailing with zeros flag.
     */
    public FloatingPointFormat( int w, int d, boolean trailingZeros ) {
    this( w, d );
    this.trailing = trailingZeros;
  }

  @Override
  public StringBuffer format(double number, StringBuffer toAppendTo, 
			     FieldPosition pos) {
    StringBuffer s = new StringBuffer( nf.format(number) );
    if( s.length() > width ) {
      if( s.charAt(0) == ' ' && s.length() == width + 1 ) {
	s.deleteCharAt(0);
      }
      else {
	s.setLength( width );
	for( int i = 0; i < width; i ++ )
	  s.setCharAt(i, '*');
      }
    }
    else {
      for (int i = 0; i < width - s.length(); i++)  // padding
	s.insert(0,' ');
    }
    if( !trailing && decimal > 0 ) { // delete trailing zeros
      while( s.charAt( s.length()-1 ) == '0' )
	s.deleteCharAt( s.length()-1 );
      if( s.charAt( s.length()-1 ) == '.' )
	s.deleteCharAt( s.length()-1 );
    }
	
    return toAppendTo.append( s );
  }

    /**
     * Generates a pattern with the given width and decimal size parameters.
     * @param w given width.
     * @param d given decimal size.
     * @return the pattern generated.
     */
    public static String  pattern( int w, int d ) {
    StringBuffer s = new StringBuffer();      // "-##0.00"   // fw.d
    s.append( padding(w - d - 3, '#') );
    if( d == 0) s.append('0');
    else {
      s.append("0.");
      s.append( padding( d, '0') );
    }
    return s.toString();
  }

  private static StringBuffer  padding( int n, char c ) {
    StringBuffer text = new StringBuffer();
	
    for(int i = 0; i < n; i++ ){
      text.append( c );
    }

    return text;
  }

    /**
     * Returns the width of the numbers format.
     * @return the width of the numbers format.
     */
    public int width () {
    if( !trailing ) throw new RuntimeException( "flexible width" );
    return width;
  }

}

