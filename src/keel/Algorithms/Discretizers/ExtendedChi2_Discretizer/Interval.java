package keel.Algorithms.Discretizers.ExtendedChi2_Discretizer;

import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;

	public class Interval {
	/**
	 * <p>
	 * Interval class.
	 * </p>
	 */	
		
		int attribute;
		int begin;
		int end;
		int []values;
		int []cd;
		int classOfInstances[];
		
		/**
		 * <p>
		 * Compute the interval ratios.
		 * </p>
		 * @param _attribute
		 * @param []_values
		 * @param _begin
		 * @param _end
		 */
		public Interval(int _attribute,int []_values,int _begin,int _end,int classes[]) {
			attribute=_attribute;
			begin=_begin;
			end=_end;
			values=_values;
			
			classOfInstances = new int[classes.length];
			for (int i=0; i<classes.length; i++) {
				classOfInstances[i] = classes[i];
			}

			computeIntervalRatios();
		}

		void computeIntervalRatios() {
			cd=classDistribution(attribute,values,begin,end);
		}
		
		/**
		 * <p>
		 * Enlarge the interval using a new "end"
		 * </p>
		 * @param newEnd indicates the new end
		 */
		public void enlargeInterval(int newEnd) {
			end=newEnd;
			computeIntervalRatios();
		}

		int []classDistribution(int attribute,int []values,int begin,int end) {
			int []classCount = new int[Parameters.numClasses];
			for(int i=0;i<Parameters.numClasses;i++) classCount[i]=0;

			for(int i=begin;i<=end;i++) classCount[classOfInstances[values[i]]]++;
			return classCount;	
		}
}
