import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
*Represents a Topic.<br>
*Can be of different types : 'T'=topic;'P'=topicPlus(topic with sub-topics);'D'=document;'W'=WebPage:'F'=Filetransfert
*/
class Topic{
	
	public static final int RIGHT_R=1;
	public static final int RIGHT_RW=2;
	public static final int RIGHT_RWM=3;
	/**ID of this topic*/
	int id;
	/**<code>Vector</code> of Docs directly placed in this topic*/
	Vector docs;
	/**<code>Vector</code> of the nodes of the Docs directly placed in this topic*/
	private Vector docsNodes;
	/**tree node of this topic*/
	private DefaultMutableTreeNode myNode;
	/**Type of this Topic ('T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert)*/
	private char nodeType;//'T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert
	/**This topic has been count ?*/
	boolean hasBeenCount=false;
	/**This topic is counting*/
	boolean isCounting=false;
	/**This topic should display ?*/
	boolean shouldDisplay=true;
	/**Rights that the current user has on this Topic (Topic.RIGHT_R,Topic.RIGHT_RW,Topic.RIGHT_RWM)*/
	int rights;
	
	/**
	*New Topic.<br>
	*@param id Topic ID as an <code>int</code>.
	*/
	Topic(int id) {
		this.id=id;
		this.docs=null;
		docsNodes=new Vector();
	}
	/**
	*New Topic, with id=-1;
	*/
	Topic(){
		this(-1);
	}
	
	/**
	*Updates this Topics tree node.
	*@param node DefaultMutableTreeNode, Topics tree node.
	*/
	public void setNode(DefaultMutableTreeNode node){myNode=node;}
	
	/**
	*Updates this Topics type ('T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert).
	*@param t nouveau type :('T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert).
	*/
	public void setNodeType(char t){nodeType=t;}
	
	/**
	*Returns this Topics type ('T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert).
	*@return type :('T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert).
	*/
	public char getNodeType(){return nodeType;}
	
	
	/**
	*New Topic.<br>
	*@param id Topic ID as a <code>String</code>.
	*/
	Topic(String id){ this.id=Integer.parseInt(id.substring(1));}
	/**
	*Returns the ID of this Topic
	*@return this topics ID as an <code>int</code>.
	*/
	public int id() {return this.id;}
	
	/**
	*Returns the name of this Topic.
	*@return String name of this Topic.
	*/
	public String name() {return CheesyKM.getTopicNameById(this.id);}
	
	public DefaultMutableTreeNode getNode(){return this.myNode;}
	
	/**
	*Returns the name of this Topic.
	*@return String name of this Topic.
	*/
	public String toString() {
		return name();
	}
	
	/**
	*Counts the number of Docs directly located in this Topic, gets them from EasyKM, stores them and adds them to the topic tree view.
	*@return int number of Docs directly located in this Topic.
	*/
	public int docCount(boolean expand){
		if(this.docs==null){
			this.isCounting=true;
			docs=new Vector();
			Vector docsV=CheesyKM.getDocsInTopic(this.id);
			for(int i=0;i<docsV.size();i++){
				try{
				docs.add(new Doc((Hashtable)docsV.get(i),this));
				} catch(Exception e){
					JOptionPane.showMessageDialog(null, CheesyKM.getLabel("error")+e, CheesyKM.getLabel("error")+this.toString(), JOptionPane.ERROR_MESSAGE);
				}
				DefaultMutableTreeNode n=new DefaultMutableTreeNode(docs.get(i));
				((Topic)docs.get(i)).setNode(n);
				((Topic)docs.get(i)).setNodeType('D');
				synchronized(CheesyKM.api.thematique){
				((DefaultTreeModel)CheesyKMAPI.thematique.getModel()).insertNodeInto(n,myNode,myNode.getChildCount());
				}
				docsNodes.add(n);
				CheesyKM.docsInMem.add(n);
			}
			if(this.nodeType=='T'){
				//CheesyKM.echo("j'ai chargé "+this.toString());
				CheesyKM.topicsInMem.add(this.myNode);
			}
			this.hasBeenCount=true;
			this.isCounting=false;
		}
		if(this.docs.size()>0&&expand){
			CheesyKM.api.thematique.expandPath(new TreePath(myNode.getPath()));
			CheesyKM.api.thematique.setSelectionPath(new TreePath(myNode.getPath()));
			CheesyKM.api.thematique.scrollPathToVisible(new TreePath(myNode.getPath()));
		}
		return this.docs.size();
	}
	
	/**
	*Unload this Topic, and all its sub-topics and documents from memory.<br>
	*Called by {@link MemoryMonitor}.
	*/
	public void decharger(){
		//CheesyKM.echo(((Topic)((DefaultMutableTreeNode)this.myNode.getParent()).getUserObject())+" should not display");
		TreePath path=new TreePath(((DefaultMutableTreeNode)myNode.getParent()).getPath());
		CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(path));
		((Topic)((DefaultMutableTreeNode)this.myNode.getParent()).getUserObject()).dechargerTypeP();
		System.gc();
	}
	/*
	public void dechargerIci(){
		TreePath path=new TreePath(((DefaultMutableTreeNode)myNode.getParent()).getPath());
		CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(path));
		((Topic)this.myNode.getUserObject()).dechargerTypeP();
		System.gc();
	}
	*/
	private void dechargerTypeT(){
		//CheesyKM.echo("decharge (T) de "+this);
		this.hasBeenCount=false;
		this.isCounting=false;
		for(int i=0;i<this.docsNodes.size();i++){
			((DefaultTreeModel)CheesyKMAPI.thematique.getModel()).removeNodeFromParent((MutableTreeNode)docsNodes.get(i));
			CheesyKM.docsInMem.remove(docsNodes.get(i));
		}
		this.docsNodes=new Vector();
		this.docs=null;
		CheesyKM.topicsInMem.remove(this.myNode);
		System.gc();
	}
	
	private void dechargerTypeP(){
		//CheesyKM.echo("decharge (P) de "+this);
		this.shouldDisplay=false;
		this.hasBeenCount=false;
		this.isCounting=false;
		Vector childrenV=new Vector();
		Enumeration children=myNode.children();
		while(children.hasMoreElements()){
			childrenV.add(children.nextElement());
		}
		for(int i=0;i<childrenV.size();i++){
			DefaultMutableTreeNode nChild=(DefaultMutableTreeNode)childrenV.get(i);
			Topic tChild=(Topic)nChild.getUserObject();
			//CheesyKM.echo("CHILD:"+tChild);
			if(tChild.getNodeType()=='P'){
				CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(new TreePath(nChild.getPath())));
				tChild.dechargerTypeP();
			} else if(tChild.getNodeType()=='T'){
				CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(new TreePath(nChild.getPath())));
				tChild.dechargerTypeT();
			} else {
				//CheesyKM.echo("decharge (D) de "+tChild);
				CheesyKM.docsInMem.remove(nChild);
				//nChild.removeFromParent();
				((DefaultTreeModel)CheesyKMAPI.thematique.getModel()).removeNodeFromParent(nChild);
				//CheesyKM.echo(tChild+" dechargé");
			}
		}
		this.docsNodes=new Vector();
		this.docs=null;
		System.gc();
	}
	
	/**
	*override Object equals, checks the IDs of two Topics
	*@return <code>true</code> if the IDs are equal, <code>false</code> else.
	*/
	public boolean equals(Object o){
		return this.id==((Topic)o).id&&this.toString().equals(o.toString());
	}
}


