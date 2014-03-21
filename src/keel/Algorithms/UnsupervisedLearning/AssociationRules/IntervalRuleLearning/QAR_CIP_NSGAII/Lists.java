package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.QAR_CIP_NSGAII;


/**
 * <p>Title: Class List</p>
 *
 * <p>Description: In this class implements the structure and methods of a list</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Alvaro Enciso Ruiz (UGR) 10/10/2008
 * @version 1.0
 * @since JDK 1.5
 */
public class Lists {
    public int index;
    public Lists parent;
    public Lists child;
    
    public Lists(){
    	index = -1;
    	parent = null;
    	child = null;
    }
    
    /**
	 * Insert an element X into the list at location specified by NODE 
	 * @param node list in which we want introduce an element 
	 * @param x element to introduce
	 */
    public void insert (Lists node, int x) {
        Lists temp;
        
		if (node == null) {
            System.out.println("Error!! asked to enter after a NULL pointer, hence exiting ");
            System.exit(1);
        }

        temp = new Lists();
        temp.index = x;
        temp.child = node.child;
        temp.parent = node;

		if (node.child != null)  node.child.parent = temp;

        node.child = temp;
    }

    
    /**
	 * Delete the node NODE from the list  
	 * @param node node which we want to delete
	 */
    public Lists del (Lists node) {
        Lists temp;
        
		if (node==null) {
        	System.out.println(" Error!! asked to delete a NULL pointer, hence exiting");
        	System.exit(1);
        }
        
		temp = node.parent;
        temp.child = node.child;

		if (temp.child!=null)  temp.child.parent = temp;

        return (temp);
    } 
}
