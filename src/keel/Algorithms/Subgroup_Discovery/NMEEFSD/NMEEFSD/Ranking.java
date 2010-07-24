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

/**
 * <p>
 * Algorithm for the discovery of rules describing subgroups
 * @author Cristóbal J. Carmona
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

import java.util.*;

public class Ranking {
    /**
     * <p>
     * This class implements some facilities for ranking solutions.
     * Given a Population object, their solutions are ranked
     * according to scheme proposed in NSGA-II; as a result, a set of subsets
     * are obtained. The subsets are numbered starting from 0 (in NSGA-II, the
     * numbering starts from 1); thus, subset 0 contains the non-dominated
     * solutions, subset 1 contains the non-dominated solutions after removing those
     * belonging to subset 0, and so on.
     * </p>
     */
  
  /**
   * <p>
   * An array containing all the fronts found during the search
   * </p>
   */
  private Population[] ranking;
  
  /**
   * <p>
   * Constructor of the Ranking
   * </p>
   * @param pop              Actual population
   * @param Variables           Variables structure
   * @param nobj                Number of objectives
   * @param neje                Number of examples
   * @param RulRep              Rules representation
   * @param SDomin              Strict dominance
   */       
  public Ranking(Population pop, TableVar Variables, int nobj, int neje, String RulRep, String SDomin) {

    // dominateMe[i] contains the number of solutions dominating i        
    int [] dominateMe = new int[pop.getNumIndiv()];

    // iDominate[k] contains the list of solutions dominated by k
    List<Integer> [] iDominate = new List[pop.getNumIndiv()];

    // front[i] contains the list of individuals belonging to the front i
    List<Integer> [] front = new List[pop.getNumIndiv()+1];
        
    // flagDominate is an auxiliar variable
    int flagDominate;    

    // Initialize the fronts 
    for (int i = 0; i < front.length; i++)
      front[i] = new LinkedList<Integer>();
        
    // Fast non dominated sorting algorithm
    for (int p = 0; p < pop.getNumIndiv(); p++) {
    // Initialice the list of individuals that i dominate and the number
    // of individuals that dominate me
      iDominate[p] = new LinkedList<Integer>();
      dominateMe[p] = 0;            
      // For all q individuals , calculate if p dominates q or vice versa
      boolean centi = false;
      for (int q = 0; q < pop.getNumIndiv(); q++) {
          flagDominate = compareConstraint(pop.getIndiv(p), pop.getIndiv(q));
        //flagDominate = constraint_.compare(solutionSet.get(p),solutionSet.get(q));
        if (flagDominate == 0) {
            flagDominate = compareDominance(pop.getIndiv(p), p, pop.getIndiv(q), q,nobj, SDomin);
        }

        if (flagDominate == -1) {
          iDominate[p].add(new Integer(q));
        } else if (flagDominate == 1){
           dominateMe[p]++;
        } else {
            iDominate[p].add(new Integer(q));
        }
      }
            
      // If nobody dominates p, p belongs to the first front
      if (dominateMe[p] == 0) {
        front[0].add(new Integer(p));
        pop.getIndiv(p).setRank(0);
      }            
    }
        
    //Obtain the rest of fronts
    int i = 0;
    Iterator<Integer> it1, it2 ; // Iterators
    while (front[i].size()!= 0) {
      i++;
      it1 = front[i-1].iterator();
      while (it1.hasNext()) {
        it2 = iDominate[it1.next().intValue()].iterator();
        while (it2.hasNext()) {
          int index = it2.next().intValue();
          dominateMe[index]--;
          if (dominateMe[index]==0) {
            front[i].add(new Integer(index));
            pop.getIndiv(index).setRank(i);
          }
        }
      }
    }

    ranking = new Population[i];
    int contador;
    //0,1,2,....,i-1 are front, then i fronts
    for (int j = 0; j < i; j++) {
      ranking[j] = new Population(front[j].size(), Variables.getNVars(), nobj, neje, RulRep, Variables);
      it1 = front[j].iterator();
      contador = 0;
      while (it1.hasNext()) {
        ranking[j].CopyIndiv(contador,neje,nobj,pop.getIndiv(it1.next().intValue()));
        contador++;
      }
    }

  } // Ranking

  /**
   * <p>
   * Returns a Population containing the solutions of a given rank.
   * </p>
   * @param rank                    Value of the rank
   * @return                        Population of this rank.
   */
  public Population getSubfront(int rank) {
    return ranking[rank];
  } // getSubFront

  /**
   * <p>
   * Returns the total number of subFronts founds.
   * </p>
   * @return            Number of fronts in the population
   */
  public int getNumberOfSubfronts() {
    return ranking.length;
  } // getNumberOfSubfronts
  
  /**
   * <p>
   * Gets the comparison constraint
   * </p>
   * @param a           Individual
   * @param b           Individual
   * @return            Result of the comparison between the individuals
   */
  public int compareConstraint(Individual a, Individual b){
    
      double overall1, overall2;
      
      overall1 = a.getOverallConstraintViolation();
      overall2 = b.getOverallConstraintViolation();
       
      if ((overall1 < 0) && (overall2 < 0)) {
          if (overall1 > overall2){
              return -1;
          } else if (overall2 > overall1){
              return 1;
          } else {
              return 0;
          }
      } else if ((overall1 == 0) && (overall2 < 0)) {
          return -1;
      } else if ((overall1 < 0) && (overall2 == 0)) {        
          return 1;
      } else {
          return 0;        
      }
  }
  

  /**
   * <p>
   * Gets the comparison Dominance
   * </p>
   * @param a           Individual
   * @param posa        Position of the individual a
   * @param b           Individual
   * @param posb        Position of the individual b
   * @param nobj        Number of objectives of the algorithm
   * @param SDomin      Strict Dominance for comparison
   * @return            Result of the comparison between the individuals
   */
  public int compareDominance(Individual a, int posa, Individual b, int posb, int nobj, String SDomin){
      //NOTE for this algorithm:
      // If A   domains   B THEN flag == -1
      // If A   equals    B THEN flag == 0
      // Si A  !domains   B THEN flag == 1
      if (a == null)
          return 1;
      else if (b == null)
          return -1;
    
      int dominate1 ; // dominate1 indicates if some objective of solution1
                        // dominates the same objective in solution2. dominate2
      int dominate2 ; // is the complementary of dominate1.
      Individual solution1 = (Individual) a;
      Individual solution2 = (Individual) b;

      dominate1 = 0 ;
      dominate2 = 0 ;

      int flag = 0; //stores the result of the comparation

      if (solution1.getOverallConstraintViolation()!=
          solution2.getOverallConstraintViolation() &&
         (solution1.getOverallConstraintViolation() < 0) ||
         (solution2.getOverallConstraintViolation() < 0)){
        return (compareConstraint(solution1,solution2));
      }

      // Equal number of violated constraint. Apply a dominance Test
      double value1, value2;

      QualityMeasures medidas = new QualityMeasures(nobj);
      for (int i = 0; i < nobj; i++) {
          medidas = solution1.getMeasures();
          value1 = (medidas.getObjectiveValue(i));
          medidas = solution2.getMeasures();
          value2 = medidas.getObjectiveValue(i);
          if(SDomin.compareTo("YES")==0){
              if (value1 < value2) {
                flag = 1;
              } else if (value1 > value2){
                flag = -1;
              } else flag = 0;
          } else {
              if (value1 < value2) {
                flag = 1;
              } else {
                flag = -1;
              }
          }

          if (flag == -1) {
            dominate1 = 1;
          }

          if (flag == 1) {
            dominate2 = 1;
          }

        }

        if (dominate1 == dominate2) {
            return 0; //No one dominate the other
        }
        if (dominate1 == 1) {
            return -1; // solution1 dominate
        }
        return 1;    // solution2 dominate
        }

} // Ranking
