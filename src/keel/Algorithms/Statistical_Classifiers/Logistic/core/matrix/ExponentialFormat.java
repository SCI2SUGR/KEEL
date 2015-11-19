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
 *    ExponentialFormat.java
 *    Copyright (C) 2002 University of Waikato, Hamilton, New Zealand
 *
 */

package keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * <code>ExponentialFormat</code> is a concrete subclass of
 * <code>DecimalFormat</code> that formats exponential numbers. It has a variety of
 * features designed to make it possible to parse and format exponential numbers in any
 * locale, including support for Western, Arabic, and Indic digits.  
 * 
 * @author Yong Wang
 * @version $Revision: 1.1 $
 */
public class ExponentialFormat
  extends DecimalFormat {

  /** for serialization */
  private static final long serialVersionUID = -5298981701073897741L;
    
    /**
     * Decimal format of this exponential one.
     */
    protected DecimalFormat nf ;

    /**
     * Sign flag.
     */
    protected boolean sign;

    /**
     * Number of digits of the base.
     */
    protected int digits;

    /**
     * Exponent size.
     */
    protected int exp;

    /**
     * Trailing flag.
     */
    protected boolean trailing = true;

    /**
     * Default constructor.  Creates a ExponentialFormat with 5 digits, 2 exp digits, with the sign flag as true and without trailing.
     */
    public ExponentialFormat () {
    this( 5 );
  }
    
    /**
     * Constructor. Creates a ExponentialFormat with the given value of digits and 2 exp digits, with the sign flag as true and without trailing.
     * @param digits given base size.
     */
    public ExponentialFormat( int digits ) {
    this( digits, false );
  }

    /**
     * Constructor. Creates a ExponentialFormat with the given value of digits and given trailing flag, 2 exp digits and with the sign flag as true.
     * @param digits given base size.
     * @param trailing given trailing flag.
     */
    public ExponentialFormat( int digits, boolean trailing ) {
    this( digits, 2, true, trailing );
  }
    
    /**
     ** Constructor. Creates a ExponentialFormat with the given arguments.
     * @param digits given base size.
     * @param exp given exponent size.
     * @param sign given sign flag.
     * @param trailing given trailing flag.
     */
    public ExponentialFormat( int digits, int exp, boolean sign, 
			    boolean trailing ) {
    this.digits = digits;
    this.exp = exp;
    this.sign = sign;
    this.trailing = trailing;
    nf = new DecimalFormat( pattern() );
    nf.setPositivePrefix("+");
    nf.setNegativePrefix("-");
  }
    
    /**
     * Returns the width of the exponential numbers format.
     * @return the width of the exponential numbers format.
     */
    public int width () {
    if( !trailing ) throw new RuntimeException( "flexible width" );
    if( sign ) return 1 + digits + 2 + exp;
    else return digits + 2 + exp;
  }

  @Override
  public StringBuffer format(double number, StringBuffer toAppendTo, 
			     FieldPosition pos) {
    StringBuffer s = new StringBuffer( nf.format(number) );
    if( sign ) {
      if( s.charAt(0) == '+' ) s.setCharAt(0, ' ');
    }
    else {
      if( s.charAt(0) == '-' ) s.setCharAt(0, '*');
      else s.deleteCharAt(0);
    }
	
    return toAppendTo.append( s );
  }
    
  private String  pattern() {
    StringBuffer s = new StringBuffer();      // "-##0.00E-00"   // fw.d
    s.append("0.");
    for(int i = 0; i < digits - 1; i ++)
      if( trailing ) s.append('0');
      else s.append('#');
	
    s.append('E');
    for(int i = 0; i < exp; i ++)
      s.append('0');
	
    return s.toString();
  }
}

