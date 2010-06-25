/* -------------------------------------------------------------------------- */
/*                                                                            */
/*          P A R T I A L   S U P P O R T   T R E E  N O D E   T O P          */
/*                                                                            */
/*                               Frans Coenen                                 */
/*                                                                            */
/*                          Wednesday 9 January 2003                          */
/*                             (Revised 5/7/2003)                             */
/*                                                                            */
/*                       Department of Computer Science                       */
/*                        The University of Liverpool                         */
/*                                                                            */
/* -------------------------------------------------------------------------- */

/** Top level Ptree node structure. <P> An array of such structures is created 
in which to store the top level of the Ptree. 
@author Frans Coenen
@version 5 July 2003 */

package keel.Algorithms.Associative_Classification.ClassifierCMAR;

/**
 * <p>Title: PtreeNodeTop</p>
 *
 * <p>Description: Class to store the top node of the P-tree</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Frans Coenen
 * @author Modified by Jesus Alcala (University of Granada) 09/02/2010
 * @author Modified by Sergio Almecija (University of Granada) 23/05/2010
 * @version 1.0
 * @since JDK1.5
 */
public class PtreeNodeTop {
    
    /*------------------------------------------------------------------------*/
    /*                                                                        */
    /*                                   FIELDS                               */
    /*                                                                        */
    /*------------------------------------------------------------------------*/
    
    /** Partial support for the rows. */
    public int support = 1;
	
    /** Pointer to child structure. */
    public PtreeNode childRef = null;
	   
    /*---------------------------------------------------------------------*/
    /*                                                                     */
    /*                           CONSTRUCTORS                              */
    /*                                                                     */
    /*---------------------------------------------------------------------*/
    
    /* Default constructor only. */
       
    }
