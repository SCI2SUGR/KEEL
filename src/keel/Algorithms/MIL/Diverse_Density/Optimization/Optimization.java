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

package keel.Algorithms.MIL.Diverse_Density.Optimization;

import java.util.ArrayList;

/**
 * Optimization auxiliary methods
 */

public abstract class Optimization
{
	// ///////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Properties
	// ///////////////////////////////////////////////////////////////

	protected double ALFA = 1.0e-4;
	protected double BETA = 0.9;    
	protected double TOLX = 1.0e-6;
	protected double STPMX = 100.0;
	
	protected int maxIterations = 200;

	protected double minFunction;    

	protected double slope;

	protected boolean alphaLEQZero = false;

	protected double[] varValues;

	protected double epsilon, zero; 
	
	// ///////////////////////////////////////////////////////////////
	// --------------------------------------------------- Constructor
	// ///////////////////////////////////////////////////////////////
	
	public Optimization()
	{
		epsilon=1.0;
		while(1.0+epsilon > 1.0){
			epsilon /= 2.0;	    
		}
		epsilon *= 2.0;
		zero = Math.sqrt(epsilon);
	}


	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////		

	protected abstract double evaluate(double[] x) throws Exception;

	protected abstract double[] gradient(double[] x) throws Exception;

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////	
	
	public double getEpsilon() {
		return epsilon;
	}

	public double getZero() {
		return zero;
	}

	public double getMinFunction() {
		return minFunction;
	}

	public double[] getVarValues() {
		return varValues;
	}

	public double[] find(double[] xold, double[] gradient, double[] directionVector, double maxStep, boolean[] isFixed, double[][] nonworkingSetBounds, ArrayList<Integer> workingSetBoundIndex) throws Exception  
	{
		int length=xold.length, fixedOne=-1;
		double lambda, minLambda;
		double temp,test,alpha = Double.POSITIVE_INFINITY;
		double fold=minFunction; 
		double tmp1,lambda2=0,tmp2,disc=0,maxLambda=1.0,tmpLambda;
		double[] newvarValues = new double[length];
		double sum = 0.0, newSlope = 0.0;

		for(int i = 0; i < length; i++)
			if(!isFixed[i])
				sum += directionVector[i]*directionVector[i];

		sum = Math.sqrt(sum);

		if(sum > maxStep){
			for(int i = 0; i < length; i++)
				if(!isFixed[i])
					directionVector[i] *= maxStep/sum;		
		}
		else
			maxLambda = maxStep/sum;

		slope = 0.0;
		
		for(int i = 0; i < length; i++){
			newvarValues[i] = xold[i];
			if(!isFixed[i])
				slope += gradient[i]*directionVector[i];
		}

		if(Math.abs(slope) <= zero)
			return newvarValues;

		test = 0.0;
		
		for(int i = 0; i < length; i++){	    
			if(!isFixed[i]){
				temp = Math.abs(directionVector[i])/Math.max(Math.abs(newvarValues[i]),1.0);
				if (temp > test)
					test = temp;
			}
		}

		if(test > zero)
			minLambda = TOLX/test;
		else
			return newvarValues;

		for(int i = 0; i < length; i++)
		{
			if(!isFixed[i])
			{
				double aux;
				if((directionVector[i] < -epsilon) && !Double.isNaN(nonworkingSetBounds[0][i]))
				{
					aux = (nonworkingSetBounds[0][i]-xold[i])/directionVector[i];
					if(aux <= zero)
					{
						newvarValues[i] = nonworkingSetBounds[0][i];
						isFixed[i]=true; 
						alpha = 0.0;
						nonworkingSetBounds[0][i]=Double.NaN; 
						workingSetBoundIndex.add(i);
					}
					else if(alpha > aux){
						alpha = aux;
						fixedOne = i;
					}			
				}
				else if((directionVector[i] > epsilon) && !Double.isNaN(nonworkingSetBounds[1][i]))
				{
					aux = (nonworkingSetBounds[1][i]-xold[i])/directionVector[i];
					if(aux <= zero)
					{
						newvarValues[i] = nonworkingSetBounds[1][i];
						isFixed[i]=true;
						alpha = 0.0;
						nonworkingSetBounds[1][i]=Double.NaN;
						workingSetBoundIndex.add(i);
					}
					else if(alpha > aux){
						alpha = aux;
						fixedOne = i;
					}			
				}				
			}
		}	

		if(alpha <= zero){  
			alphaLEQZero = true;
			return newvarValues;
		}

		lambda = alpha;
		if(lambda > 1.0)
			lambda = 1.0;

		double init=fold, hi=lambda, lamdaOld=lambda, high=minFunction, low=minFunction;
		double[] newGrad;

		for (int k = 0; ;k++)
		{
			for (int i = 0; i < length;i++)
			{
				if(!isFixed[i])
				{
					newvarValues[i] = xold[i]+lambda*directionVector[i];
					if(!Double.isNaN(nonworkingSetBounds[0][i]) && (newvarValues[i]<nonworkingSetBounds[0][i]))    
						newvarValues[i] = nonworkingSetBounds[0][i];	
					else if(!Double.isNaN(nonworkingSetBounds[1][i]) && (newvarValues[i]>nonworkingSetBounds[1][i]))		
						newvarValues[i] = nonworkingSetBounds[1][i];
				}
			}

			minFunction = evaluate(newvarValues);


			while(Double.isInfinite(minFunction))
			{
				lambda *= 0.5;
				if(lambda <= epsilon)
					return newvarValues;

				for (int i = 0; i < length; i++)
					if(!isFixed[i])
						newvarValues[i] = xold[i]+lambda*directionVector[i]; 

				minFunction = evaluate(newvarValues); 

				init = Double.POSITIVE_INFINITY;
			}

			if(minFunction <= fold + ALFA*lambda*slope)
			{
				newGrad = gradient(newvarValues);
				newSlope = 0.0;

				for(int i=0; i < length; i++)
					if(!isFixed[i])
						newSlope += newGrad[i]*directionVector[i];

				if(newSlope >= BETA*slope)
				{
					if((fixedOne!=-1) && (lambda>=alpha))
					{
						if(directionVector[fixedOne] > 0){
							newvarValues[fixedOne] = nonworkingSetBounds[1][fixedOne];
							nonworkingSetBounds[1][fixedOne]=Double.NaN;
						}
						else{
							newvarValues[fixedOne] = nonworkingSetBounds[0][fixedOne];
							nonworkingSetBounds[0][fixedOne]=Double.NaN;
						}

						isFixed[fixedOne]=true;
						workingSetBoundIndex.add(fixedOne);
					}		
					return newvarValues;
				}
				else if(k==0)
				{
					double upper = Math.min(alpha,maxLambda); 
					while(!((lambda>=upper) || (minFunction>fold+ALFA*lambda*slope)))
					{
						lamdaOld = lambda;
						low = minFunction;
						lambda *= 2.0;
						if(lambda >= upper)
							lambda = upper;

						for (int i = 0; i < length;i++)
							if(!isFixed[i])
								newvarValues[i] = xold[i]+lambda*directionVector[i];

						minFunction = evaluate(newvarValues);

						newGrad = gradient(newvarValues);

						newSlope = 0.0;
						for(int i = 0; i < length; i++)
							if(!isFixed[i])
								newSlope += newGrad[i]*directionVector[i];

						if(newSlope >= BETA*slope)
						{
							if((fixedOne!=-1) && (lambda>=alpha))
							{
								if(directionVector[fixedOne] > 0){
									newvarValues[fixedOne] = nonworkingSetBounds[1][fixedOne];
									nonworkingSetBounds[1][fixedOne]=Double.NaN;
								}
								else{
									newvarValues[fixedOne] = nonworkingSetBounds[0][fixedOne];
									nonworkingSetBounds[0][fixedOne]=Double.NaN;
								}

								isFixed[fixedOne]=true;
								workingSetBoundIndex.add(fixedOne);
							}		 				    
							return newvarValues;
						}
					}
					hi = lambda;
					high = minFunction;			
					break;
				}
				else
				{
					hi = lambda2;
					lamdaOld = lambda;
					low = minFunction;
					break;
				}		    
			}        
			else if (lambda < minLambda)
			{   
				if(init<fold)
				{ 
					lambda = Math.min(1.0,alpha);
					for (int i = 0; i < length; i++)
						if(!isFixed[i])
							newvarValues[i] = xold[i]+lambda*directionVector[i];

					if((fixedOne!=-1) && (lambda>=alpha))
					{
						if(directionVector[fixedOne] > 0){
							newvarValues[fixedOne] = nonworkingSetBounds[1][fixedOne];
							nonworkingSetBounds[1][fixedOne]=Double.NaN;
						}
						else{
							newvarValues[fixedOne] = nonworkingSetBounds[0][fixedOne];
							nonworkingSetBounds[0][fixedOne]=Double.NaN;
						}

						isFixed[fixedOne]=true;
						workingSetBoundIndex.add(fixedOne);
					}		 		    
				}
				else{
					for(int i = 0; i < length;i++) 
						newvarValues[i]=xold[i];
					minFunction=fold;
				}

				return newvarValues; 
			}
			else
			{ 
				if(k==0)
				{
					if(!Double.isInfinite(init))
						init = minFunction;		    
					tmpLambda = -0.5*lambda*slope/((minFunction-fold)/lambda-slope);
				}
				else
				{    
					tmp1 = ((minFunction-fold-lambda*slope)/(lambda*lambda)-(high-fold-lambda2*slope)/(lambda2*lambda2))/(lambda-lambda2);
					tmp2 = (-lambda2*(minFunction-fold-lambda*slope)/(lambda*lambda)+lambda*(high-fold-lambda2*slope)/(lambda2*lambda2))/(lambda-lambda2);

					if (tmp1 == 0.0)
						tmpLambda = -slope/(2.0*tmp2);
					else
					{
						disc=tmp2*tmp2-3.0*tmp1*slope;
						if (disc < 0.0)
							disc = 0.0;

						double numerator = -tmp2+Math.sqrt(disc);
						if(numerator >= Double.MAX_VALUE)
							numerator = Double.MAX_VALUE;

						tmpLambda=numerator/(3.0*tmp1);
					}
					if (tmpLambda>0.5*lambda)
						tmpLambda=0.5*lambda;  
				}
			}
			lambda2=lambda;
			high=minFunction;
			lambda=Math.max(tmpLambda,0.1*lambda);        
		} 

		double diff = hi-lamdaOld, incr;

		while((newSlope < BETA*slope) && (diff >= minLambda))
		{
			incr = -0.5*newSlope*diff*diff/(high-low-newSlope*diff);

			if(incr < 0.2*diff)
				incr = 0.2*diff;
			lambda = lamdaOld+incr;

			if(lambda >= hi){
				lambda=hi;
				incr=diff;
			}

			for(int i = 0; i < length;i++)
				if(!isFixed[i])
					newvarValues[i] = xold[i]+lambda*directionVector[i];

			minFunction = evaluate(newvarValues);

			if(minFunction>fold+ALFA*lambda*slope){ 
				diff = incr;
				high = minFunction;
			}	    
			else
			{
				newGrad = gradient(newvarValues);
				newSlope = 0.0;

				for(int i = 0; i < length; i++)
					if(!isFixed[i])
						newSlope += newGrad[i]*directionVector[i];

				if(newSlope < BETA*slope){
					lamdaOld = lambda;
					diff -= incr;
					low = minFunction;
				}
			}
		} 

		if(newSlope < BETA*slope)
		{
			lambda=lamdaOld;
			for (int i = 0; i < length; i++)
				if(!isFixed[i])
					newvarValues[i] = xold[i]+lambda*directionVector[i];
			minFunction = low;
		}

		if((fixedOne!=-1) && (lambda>=alpha))
		{
			if(directionVector[fixedOne] > 0){
				newvarValues[fixedOne] = nonworkingSetBounds[1][fixedOne];
				nonworkingSetBounds[1][fixedOne]=Double.NaN; 
			}
			else{
				newvarValues[fixedOne] = nonworkingSetBounds[0][fixedOne]; 
				nonworkingSetBounds[0][fixedOne]=Double.NaN;
			}

			isFixed[fixedOne]=true;
			workingSetBoundIndex.add(fixedOne);
		}

		return newvarValues;
	}

	@SuppressWarnings("unchecked")
	public double[] minimum(double[] initPoint, double[][] constraints) throws Exception
	{
		int length = initPoint.length;
		boolean[] isFixed = new boolean[length];
		double[][] nonworkingSetBounds = new double[2][length];
		
		ArrayList<Integer> workingSetBoundsIndex = new ArrayList<Integer>(constraints.length); 
		ArrayList<Integer> list=null, prevList=null;	
		
		minFunction = evaluate(initPoint);
		
		double sum=0;
		double[] diagonal = new double[length];
		double[] gradient = gradient(initPoint), prevGradient, prevX;
		double[] deltaGradient=new double[length], deltaX=new double[length], direct = new double[length], vector = new double[length];
		double[][] lowerTriangle = new double[length][length];  
		
		for(int i=0; i < length; i++)
		{
			lowerTriangle[i][i] = 1.0;
			diagonal[i] = 1.0;
			direct[i] = -gradient[i];
			sum += gradient[i]*gradient[i];
			vector[i] = initPoint[i];
			nonworkingSetBounds[0][i] = constraints[0][i];
			nonworkingSetBounds[1][i] = constraints[1][i];
			isFixed[i] = false;
		}
		
		double maxStep = STPMX*Math.max(Math.sqrt(sum), length);

		for(int i = 0; i < maxIterations; i++)
		{
			prevX = vector;
			prevGradient = gradient;

			alphaLEQZero = false;
			vector = find(vector, gradient, direct, maxStep, isFixed, nonworkingSetBounds, workingSetBoundsIndex);

			if(alphaLEQZero)
			{
				for(int f=0; f<workingSetBoundsIndex.size(); f++)
					diagonal[workingSetBoundsIndex.get(f)] = 0.0;
				
				gradient = gradient(vector);
				i--;
			}
			else
			{
				boolean finish = false;
				double test=0.0;
				
				for(int j = 0; j < length; j++)
				{
					deltaX[j] = vector[j]-prevX[j];
					double tmp=Math.abs(deltaX[j])/Math.max(Math.abs(vector[j]), 1.0);
					if(tmp > test)
						test = tmp;				    
				}
				
				if(test < zero)
					finish = true;
				
				gradient = gradient(vector);
				test=0.0;
				double res=0.0, deltaXvalue=0.0, deltaGradientvalue=0.0, newlyBounded=0.0; 
				
				for(int j = 0; j < length; j++)
				{
					if(!isFixed[j])
					{ 	
						deltaGradient[j] = gradient[j] - prevGradient[j];		  
						res += deltaX[j]*deltaGradient[j];
						deltaXvalue += deltaX[j]*deltaX[j];
						deltaGradientvalue += deltaGradient[j]*deltaGradient[j];
					}
					else 
						newlyBounded +=  deltaX[j]*(gradient[j]-prevGradient[j]);

					double tmp = Math.abs(gradient[j])*Math.max(Math.abs(direct[j]),1.0)/Math.max(Math.abs(minFunction),1.0);
					if(tmp > test) 
						test = tmp;	
				}

				if(test < zero)
					finish = true;
				
				if(Math.abs(res+newlyBounded) < zero)
					finish = true;

				int size = workingSetBoundsIndex.size();
				boolean isUpdate = true;  
				
				if(finish)
				{
					if(list != null)
						prevList = (ArrayList<Integer>) list.clone();
					list = new ArrayList<Integer>(workingSetBoundsIndex.size());

					for(int j = size-1; j >= 0; j--)
					{
						int index=workingSetBoundsIndex.get(j);
						double deltaL=0.0;
						
						double L1 = 0.0, L2;
						if(vector[index] >= constraints[1][index]) 
							L1 = -gradient[index];
						else if(vector[index] <= constraints[0][index])
							L1 = gradient[index];

						
						L2 = L1 + deltaL;			
						
						boolean isConverge = (2.0*Math.abs(deltaL)) < Math.min(Math.abs(L1), Math.abs(L2));  
						
						if((L1*L2>0.0) && isConverge)
						{ 
							if(L2 < 0.0){
								list.add(index);
								workingSetBoundsIndex.remove(j);
								finish=false; 
							}
						}

						if(list != null && list.equals(prevList)) 
							finish = true;           
					}

					if(finish)
					{
						minFunction = evaluate(vector);
						return vector;
					}

					for(int mmm=0; mmm<list.size(); mmm++)
					{
						int freeIndx=list.get(mmm);
						isFixed[freeIndx] = false;
						
						if(vector[freeIndx] <= constraints[0][freeIndx])
							nonworkingSetBounds[0][freeIndx] = constraints[0][freeIndx];
						else
							nonworkingSetBounds[1][freeIndx] = constraints[1][freeIndx];
						
						lowerTriangle[freeIndx][freeIndx] = 1.0;
						diagonal[freeIndx] = 1.0;
						isUpdate = false;			
					}			
				}

				if(res<Math.max(zero*Math.sqrt(deltaXvalue)*Math.sqrt(deltaGradientvalue), zero))
					isUpdate = false; 
				
				if(isUpdate)
				{
					double coeff = 1.0/res; 
					updateCholeskyFactor(lowerTriangle,diagonal,deltaGradient,coeff,isFixed);
					coeff = 1.0/slope; 
					updateCholeskyFactor(lowerTriangle,diagonal,prevGradient,coeff,isFixed);  		    
				}
			}
			
			double[][] LD = new double[length][length]; 
			double[] b = new double[length];

			for(int k=0; k<length; k++)
			{
				if(!isFixed[k])  b[k] = -gradient[k];
				else             b[k] = 0.0;

				for(int j=k; j<length; j++)
					if(!isFixed[j] && !isFixed[k])
						LD[j][k] = lowerTriangle[j][k]*diagonal[k];
			}	    	

			double[] LDIR = solveTriangle(LD, b, true, isFixed);	    
			LD = null;

			direct = solveTriangle(lowerTriangle, LDIR, false, isFixed);
		}

		varValues = vector;
		return null;
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////
	
	protected double[] solveTriangle(double[][] t, double[] b, boolean isLower, boolean[] isZero) throws Exception
	{
		int n = b.length; 
		double[] result = new double[n];
		if(isZero == null)
			isZero = new boolean[n];

		if(isLower)
		{
			int j = 0;
			while((j<n)&&isZero[j]){result[j]=0.0; j++;}

			if(j<n)
			{
				result[j] = b[j]/t[j][j];

				for(; j<n; j++)
				{
					if(!isZero[j])
					{
						double numerator=b[j];
						for(int k=0; k<j; k++)
							numerator -= t[j][k]*result[k];
						result[j] = numerator/t[j][j];
					}
					else 
						result[j] = 0.0;
				}
			}
		}
		else
		{
			int j=n-1;
			while((j>=0)&&isZero[j]){result[j]=0.0; j--;}

			if(j>=0)
			{
				result[j] = b[j]/t[j][j];

				for(; j>=0; j--)
				{
					if(!isZero[j])
					{
						double numerator=b[j];
						for(int k=j+1; k<n; k++)
							numerator -= t[k][j]*result[k];
						result[j] = numerator/t[j][j];
					}
					else 
						result[j] = 0.0;
				}
			}
		}

		return result;
	}

	protected void updateCholeskyFactor(double[][] unitTriangleMatrix, double[] diagonal, double[] updateVector, double updateCoeffcient, boolean[] isFixed) throws Exception
	{
		double tmp1, tmp2, tmp3;
		int length = updateVector.length;
		double[] vector = new double[length];	

		for (int i = 0; i < updateVector.length; i++)
			if(!isFixed[i])
				vector[i] = updateVector[i];
			else
				vector[i] = 0.0;

		if(updateCoeffcient>0.0)
		{
			tmp1 = updateCoeffcient;	    
			for(int i = 0; i < length; i++)
			{
				if(isFixed[i]) continue;		

				tmp2 = vector[i];
				double diagonalValue = diagonal[i];
				diagonal[i] = diagonalValue + tmp1*tmp2*tmp2;

				tmp3 = tmp2*tmp1/diagonal[i];
				tmp1 *= diagonalValue/diagonal[i];
				
				for(int j = i+1; j < length; j++)
				{
					if(!isFixed[j]){
						double l=unitTriangleMatrix[j][i];
						vector[j] -= tmp2*l;
						unitTriangleMatrix[j][i] = l+tmp3*vector[j];
					}
					else
						unitTriangleMatrix[j][i] = 0.0;
				}
			}
		}
		else
		{
			double[] tri = solveTriangle(unitTriangleMatrix, updateVector, true, isFixed);	    
			tmp1 = 0.0;

			for(int i=0; i<length; i++)
				if(!isFixed[i])
					tmp1 += tri[i]*tri[i]/diagonal[i];	    	

			double sqrt = updateCoeffcient*tmp1 + 1.0;
			sqrt = (sqrt<0.0)? 0.0 : Math.sqrt(sqrt);

			double alpha=updateCoeffcient, sigma=updateCoeffcient/(1.0+sqrt), omega, kappa;

			for(int i = 0; i < length; i++)
			{
				if(isFixed[i]) continue;

				double diagonalValue = diagonal[i];
				tmp2 = tri[i]*tri[i]/diagonalValue;
				kappa = 1.0+sigma*tmp2;
				tmp1 -= tmp2; 
				if(tmp1 < 0.0) tmp1=0.0;

				double plus = sigma*sigma*tmp2*tmp1;
				if((i < length-1) && plus <= zero) 
					plus = zero;

				omega = kappa*kappa + plus;		
				diagonal[i] = omega*diagonalValue;

				tmp3=alpha*tri[i]/(omega*diagonalValue);
				alpha /= omega;
				omega = Math.sqrt(omega);
				sigma *= (1.0+omega)/(omega*(kappa+omega));
				
				for(int j = i+1; j < length; j++)
				{
					if(!isFixed[j]){
						double low = unitTriangleMatrix[j][i];
						vector[j] -= tri[i]*low;
						unitTriangleMatrix[j][i] = low+tmp3*vector[j];
					}
					else
						unitTriangleMatrix[j][i] = 0.0;
				}
			}
		}	
	}
}