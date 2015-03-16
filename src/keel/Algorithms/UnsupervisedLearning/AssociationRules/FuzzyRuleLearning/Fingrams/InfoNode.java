package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

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

	public String getNodeInfo() {
		return nodeInfo;
	}


	public String getNodeInfoToolTipText() {
		return nodeInfoToolTipText;
	}


	public double getNodeSize() {
		return nodeSize;
	}


	public String getNodeColor() {
		return nodeColor;
	}

	
	public String getBorderColor() {
		return borderColor;
	}
	
	
	public double getFontSize() {
		return fontSize;
	}


	public String getFontColor() {
		return fontColor;
	}
	
	
	
	
	
	
	
}
