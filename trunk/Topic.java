import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
*Représente un Topic (thème) en mémoire.<br>
*/
class Topic{
	int id;
	Vector docs;
	private Vector docsNodes;
	private DefaultMutableTreeNode myNode;
	private char nodeType;//'T'=topic;'P'=topicPlus;'D'=document;'W'=WebPage:'F'=Filetransfert
	boolean hasBeenCount=false;
	boolean isCounting=false;
	boolean shouldDisplay=true;
	
	/**
	*Construit un nouveau Topic.<br>
	*@param id int n°de ce Topic.
	*/
	Topic(int id) {
		this.id=id;
		this.docs=null;
		docsNodes=new Vector();
	}
	
	/**
	*Met à jour le noeud dans l'arbre "Thematique" correspondant à ce Topic.
	*@param node DefaultMutableTreeNode, noeud correspondant à ce Topic.
	*/
	public void setNode(DefaultMutableTreeNode node){myNode=node;}
	
	/**
	*Met à jour le type de ce Topic ('T'=topic;'P'=topicPlus;'D'=document).
	*@param t nouveau type :('T'=topic;'P'=topicPlus;'D'=document).
	*/
	public void setNodeType(char t){nodeType=t;}
	
	/**
	*Retourne le type de ce Topic ('T'=topic;'P'=topicPlus;'D'=document).
	*@return type :('T'=topic;'P'=topicPlus;'D'=document).
	*/
	public char getNodeType(){return nodeType;}
	
	
	/**
	*Construit un nouveau Topic.<br>
	*@param id String n°de ce Topic sous la forme 'Txx'.
	*/
	Topic(String id){ this.id=Integer.parseInt(id.substring(1));}
	
	public int id() {return this.id;}
	
	/**
	*Retourne le nom de ce Topic.
	*@return String nom de ce Topic.
	*/
	public String name() {return CheesyKM.getTopicNameById(this.id);}
	
	public DefaultMutableTreeNode getNode(){return this.myNode;}
	
	/**
	*Retourne le nom de ce Topic.
	*@return String nom de ce Topic.
	*/
	public String toString() {
		return name();
	}
	
	/**
	*Compte le nombre de documents directement situés dans ce topic, les demande au serveur, les stocke et les ajoute dans l'arborescence thématique.
	*@return int nombre de documents directement situés dans ce topic.
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
	*Décharge ce Topic ainsi que ses sous-topics et documents de la mémoire.<br>
	*Utilisé par "MemoryMonitor"
	*/
	public void decharger(){
		//CheesyKM.echo(((Topic)((DefaultMutableTreeNode)this.myNode.getParent()).getUserObject())+" should not display");
		TreePath path=new TreePath(((DefaultMutableTreeNode)myNode.getParent()).getPath());
		CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(path));
		((Topic)((DefaultMutableTreeNode)this.myNode.getParent()).getUserObject()).dechargerTypeP();
		System.gc();
	}
	
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
		Enumeration children=myNode.children();
		while(children.hasMoreElements()){
			DefaultMutableTreeNode nChild=(DefaultMutableTreeNode)children.nextElement();
			Topic tChild=(Topic)nChild.getUserObject();
			if(tChild.getNodeType()=='P'){
				CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(new TreePath(nChild.getPath())));
				tChild.dechargerTypeP();
			} else if(tChild.getNodeType()=='T'){
				CheesyKM.api.thematique.collapseRow(CheesyKM.api.thematique.getRowForPath(new TreePath(nChild.getPath())));
				tChild.dechargerTypeT();
			} else {
				CheesyKM.echo("decharge (D) de "+tChild);
				CheesyKM.docsInMem.remove(nChild);
				tChild=null;
				((DefaultTreeModel)CheesyKMAPI.thematique.getModel()).removeNodeFromParent(nChild);
			}
		}
		this.docsNodes=new Vector();
		this.docs=null;
		System.gc();
	}
	
	/**
	*override de equals de Object, teste l'égalité des topics sur leur tid.
	*@return true si les tid sont égaux, false sinon.
	*/
	public boolean equals(Object o){
		return this.id==((Topic)o).id&&this.toString().equals(o.toString());
	}
}


