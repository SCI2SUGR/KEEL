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
