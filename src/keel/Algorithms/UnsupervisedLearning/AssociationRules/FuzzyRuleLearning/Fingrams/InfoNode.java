package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

/**
 * InfoNode Class.
 * InfoNode objects store graphical information of a node used when the Figrams are displayed
 * @author sergio
 */
public class InfoNode {

	// Info printed in the node
	private String nodeInfo;
	
	// Text showed when moving the mouse over a node
	private String nodeInfoToolTipText;
	
	// Size of the node
	private double nodeSize;
	
	// Color of the node
	private String nodeColor;
	
	// Color of the border of the node
	private String borderColor;
	
	// Font size
	private double fontSize;

	// Font color of the node
	private String fontColor;

	
	// /////////// METHODS /////////////

	// // // // BUILDERS // // // //

    /**
     * Default Constructor. Initiates all basic structures.
     */
    	public InfoNode(){
		super();
		this.nodeInfo = "";
		this.nodeInfoToolTipText = "";
		this.nodeSize = 0.0;
		this.nodeColor = "";
		this.borderColor="";
		this.fontSize = 0.0f;
		this.fontColor = "";
	}

    /**
     * Parameter Constructor. Initiates all basic structures with the arguments given. 
     * @param nodeInfo given Info printed in the node.
     * @param nodeInfoToolTipText given Text showed when moving the mouse over a node.
     * @param nodeSize given Size of the node.
     * @param nodeColor given Color of the node.
     * @param borderColor given Color of the border of the node.
     * @param fontSize given Font size.
     * @param fontColor given Font color of the node.
     */
    public InfoNode(String nodeInfo, String nodeInfoToolTipText,
			double nodeSize, String nodeColor, String borderColor, double fontSize, String fontColor) {
		super();
		this.nodeInfo = nodeInfo;
		this.nodeInfoToolTipText = nodeInfoToolTipText;
		this.nodeSize = nodeSize;
		this.nodeColor = nodeColor;
		this.borderColor = borderColor;
		this.fontSize = fontSize;
		this.fontColor = fontColor;
	}

	// // // // GETS & SETS // // // //

    /**
     * Returns Info printed in the node 
     * @return Info printed in the node
     */
	public String getNodeInfo() {
		return nodeInfo;
	}

    /**
     * Returns Text showed when moving the mouse over a node.
     * @return Text showed when moving the mouse over a node.
     */
    public String getNodeInfoToolTipText() {
		return nodeInfoToolTipText;
	}

    /**
     * Returns Size of the node.
     * @return Size of the node.
     */
    public double getNodeSize() {
		return nodeSize;
	}

    /**
     * Returns the Color of the node.
     * @return the Color of the node.
     */
    public String getNodeColor() {
		return nodeColor;
	}

    /**
     * Returns the Color of the border of the node.
     * @return the Color of the border of the node.
     */
    public String getBorderColor() {
		return borderColor;
	}
	
    /**
     * Returns the Font size.
     * @return the Font size.
     */
    public double getFontSize() {
		return fontSize;
	}

    /**
     * Returns the Font color of the node.
     * @return the Font color of the node.
     */ 
    public String getFontColor() {
		return fontColor;
	}
	
	
	
	
	
	
	
}
