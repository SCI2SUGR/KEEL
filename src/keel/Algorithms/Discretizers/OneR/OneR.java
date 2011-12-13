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

package keel.Algorithms.Discretizers.OneR;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Vector;
import keel.Algorithms.Discretizers.Basic.Discretizer;
import keel.Dataset.*;


/**
 * This class implements the OneR discretizer
 * 
 * <p>
 * @author Written by Julián Luengo Martín 28/10/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
public class OneR extends Discretizer{
	PrintStream stdout = System.out; //the standard output stored for further manipulation
	int small = 6; //minimum number of explanatory values with the same class in an interval
	
	public OneR(int minimum){
		small = minimum;
	}
	
	@Override
	protected Vector discretizeAttribute(int attribute,int []values,int begin,int end){
		int numClasses,_class,count,optClass[];
		int optimum[];
		double last,value,cp;
		double reals[],nonRepReals[];
		ArrayList<Opt> opts = new ArrayList<Opt>();
		Opt opt;
		Vector cps = new Vector();
		
		numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
		reals = realValues[attribute];
		//first lets fill the opts vector with the optimal class for each value
		//remember that values[] give us the index of the real values SORTED
		last = Double.NaN;
		for(int i=0;i<values.length;i++){
			value = reals[values[i]];
			_class = classOfInstances[values[i]];
			if(last==Double.NaN || value!=last){
				opt = new Opt(value,numClasses);
				opt.countClass(_class);
				opts.add(opt);
				last = value;
			}else{
				opt = opts.get(opts.size()-1);
				opt.countClass(_class);
			}
		}
		
		optimum = new int[opts.size()];
		nonRepReals = new double[opts.size()];
		for(int i=0;i<optimum.length;i++){
			optimum[i] = opts.get(i).getOptClass();
			nonRepReals[i] = opts.get(i).getValue();
		}
		
		//create the cutpoints
		if(nonRepReals.length > 1){
			count = 1;
			optClass = new int[numClasses]; 
			optClass[optimum[0]]++;
			cp = nonRepReals[1];
			_class = optimum[0];
			for(int i=1;i<nonRepReals.length-1;i++){
				if(count<small){
					//displace the cut point, so it includes the present value
					optClass[optimum[i]]++;
					_class = indexOfMax(optClass);
					count = optClass[_class];
				}else if(count>=small && optimum[i]==_class){
					optClass[classOfInstances[values[i]]]++;
					count++;
				}else if(count>=small && optimum[i]!=_class){
					//add the cut point, since extending the interval has failed
					cps.add(new Double(cp));
					for(int j=0;j<optClass.length;j++)
						optClass[j] = 0;
					count = 0;

				}
				cp = nonRepReals[i+1];
			}
			//for the last interval...
			if(count<small){
				//displace the cut point, so it includes the present value
				optClass[optimum[nonRepReals.length-1]]++;
				_class = indexOfMax(optClass);
				count = optClass[_class];
			}else if(count>=small && optimum[nonRepReals.length-1]==_class){
				optClass[classOfInstances[values[nonRepReals.length-1]]]++;
				count++;
			}else if(count>=small && optimum[nonRepReals.length-1]!=_class){
				//add the cut point, since extending the interval has failed
				cps.add(new Double(cp));
				for(int j=0;j<optClass.length;j++)
					optClass[j] = 0;
				count = 0;
			}
		}
		
		return cps;
		
		
	}
	
	/**
	 * Looks for the index of the maximum element in the array
	 * @param vec the array of elements
	 * @return the maximum element in vec
	 */
	public int indexOfMax(int vec[]){
		int max = 0;
		for(int i=1;i<vec.length;i++){
			if(vec[i]>vec[max])
				max = i;
		}
		
		return max;
	}
	
	
}

