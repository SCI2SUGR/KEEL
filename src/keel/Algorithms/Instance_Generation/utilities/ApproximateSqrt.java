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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.utilities;

/**
 *
 * @author diegoj
 */
public class ApproximateSqrt
{
    
    public static float fsqrt(float x) {
        if (x == 0)
            return 0;

        float root = x / 2;
        for (int k = 0; k < 10; k++)
            root = (root + (x / root)) / 2;
        
        return root;
    }

    public static float ffsqrt(float n) {
        if (n == 0.0f)
            return 0.0f;
        
        if (n == 1.0f)
            return 1.0f;

        float guess = n / 2.0f;
        float oldguess = 0.0f;
        while (guess != oldguess)
        {
            oldguess = guess;
            guess = (guess + n / guess) / 2.0f;
        }
        return guess;
    }

    public static float fastSqrt(float val)
    {
        // http://en.wikipedia.org/wiki/Methods_of_computing_square_roots
        //
        int tmp = Float.floatToIntBits(val);
        tmp -= 1 << 23;     /* Remove last bit to not let it go to mantissa */
        /* tmp is now an approximation to logbase2(val) */
        tmp = tmp >> 1;   /* divide by 2 */
        tmp += 1 << 29;     /* add 64 to exponent: (e+127)/2 =(e/2)+63, */
        /* that represents (e/2)-64 but we want e/2 */
        return Float.intBitsToFloat(tmp);
    }


}

