import java.util.*;
import javax.swing.tree.*;
import java.io.*;
/**
*'Special' Topic for the TopicSelectionTree.<br>
*Just like Topic, with attributes and methods added to manage selections.
*/
class SelectionTopic extends Topic{
	boolean selected;
	boolean created=false;
	String name=null;
	File fileForNewTopic=null;
	ImportFiles.Node nodeForNewTopic=null;
	public static int COUNT=-2;
	/**
	*Default constructor.
	*/
	SelectionTopic(){
		super();
	}
	
	/**
	*New SelectionTopic.<br>
	*@param i Topic ID as an <code>int</code>.
	*/
	SelectionTopic(int i){
		super(i);
	}
	
	SelectionTopic(boolean created,String name,int rights){
		this(COUNT--);
		this.setNode(new DefaultMutableTreeNode());
		this.setNodeType('T');
		this.name=name;
		this.created=true;
		this.rights=rights;
	}
	
	SelectionTopic(boolean created,String name){
		this(created,name,Topic.RIGHT_RWM);
	}
	
	/**
	*Checks if this topic has selected children or sub-children, or is selected itself.<br>
	*Used by {@link TopicSelectionTreeCellRenderer}.
	*@return <code>true</code> if this topic or one of its children is selected, <code>false</code> else.
	*/
	boolean selectedChildren(){
		Enumeration children=this.getNode().children();
		while(children.hasMoreElements()){
			SelectionTopic child=(SelectionTopic)((DefaultMutableTreeNode)children.nextElement()).getUserObject();
			if(child.selected){
				return true;
			}
			if(child.selectedChildren()){
				return true;
			}
		}
		return false;
	}
	
	/**
	*(de)Selects this topic and all its children and sub-children.
	*@param b new selected state of this topic and its children.
	*/
	void selectChildren(boolean b){
		this.selected=b;
		Enumeration children=this.getNode().children();
		while(children.hasMoreElements()){
			SelectionTopic child=(SelectionTopic)((DefaultMutableTreeNode)children.nextElement()).getUserObject();
			child.selectChildren(b);
		}
	}
	
	/**
	*Overrides Topics {@link Topic#docCount(boolean)}, returns 0.
	*/
	public int docCount(boolean expand){
		return 0;
	}
	
	/**
	*Overrides Topics {@link Topic#decharger()}, does nothing.
	*/
	public void decharger(){
		
	}
	
	/**
	*Returns the selected children of this topic as a Vector of topic IDs.<br>
	*These IDs are Strings ("TXX").If selected, this Topics ID is in the result too.
	*@return selected topics.
	*/
	public Vector getSelectedChildren(){
		Vector resu=new Vector();
		if(this.selected) resu.add("T"+this.id);
		Enumeration children=this.getNode().children();
		while(children.hasMoreElements()){
			resu.addAll(((SelectionTopic)((DefaultMutableTreeNode)children.nextElement()).getUserObject()).getSelectedChildren());
		}
		return resu;
	}
	
	public String toString(){
		if(this.name==null){
			return super.toString();
		} else {
			return name;
		}
	}
}
