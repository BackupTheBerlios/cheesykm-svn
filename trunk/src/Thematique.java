/*
This file is part of the CheesyKM project, a graphical
client for Easy KM, which is a solution for knowledge
sharing and management.
Copyright (C) 2005  Samuel Hervé

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*/
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
*Topic tree view component.
*/
class Thematique extends JTree {
	DefaultTreeModel thematiqueTreeModel;
	/**(TID as an Integer ==> DefaulMutableTreeNode)*/
	Hashtable topics;

	/**
	*Creates a Thematique component from the result of a "getTopicMatrix" request.
	*@param topicToUse Class of topics to use. Will panic if this class doesn't extends Topic. ('panic':Exceptions are catched, but only stdouted)
	"@param toSelect IDs of topics to select as Strings ("TXX")
	*/
	Thematique(Class topicToUse,Vector toSelect){
		super();
		try{
		Hashtable tRelations=CheesyKM.tRelations;
		
		Vector tRoot=CheesyKM.rootTopics;
		Topic racineT=(Topic)topicToUse.newInstance();
		racineT.id=1;
		//Topic racineT=new Topic(1);
		DefaultMutableTreeNode racine= new DefaultMutableTreeNode(racineT);
		racineT.setNode(racine);
		topics=new Hashtable();
		topics.put(new Integer(1),racine);
		Enumeration numsTopics=CheesyKM.getTNames().keys();
		while(numsTopics.hasMoreElements()){
			int tid=Integer.parseInt(((String)(numsTopics.nextElement())).substring(1));
			//Topic t=new Topic(tid);
			Topic t=(Topic)topicToUse.newInstance();
			t.id=tid;
			t.rights=((Integer)CheesyKM.tRights.get("T"+tid)).intValue();
			DefaultMutableTreeNode n=new DefaultMutableTreeNode(t);
			t.setNodeType('T');
			t.setNode(n);
			topics.put(new Integer(tid),n);
			if(toSelect!=null){
				if(toSelect.contains("T"+tid)){
					((SelectionTopic)t).selected=true;
				}
			}
		}
		Enumeration numsTids=tRelations.keys();
		while(numsTids.hasMoreElements()){
			String cleRelationS=((String)(numsTids.nextElement()));
			int cleRelation=Integer.parseInt((cleRelationS).substring(1));
			DefaultMutableTreeNode tid=(DefaultMutableTreeNode)topics.get(new Integer(cleRelation));
			int parentsId;
			Object parentO=tRelations.get(cleRelationS);
			if(!parentO.getClass().equals(new String().getClass())){
				parentsId=1;
			} else {
				String parentS=(String)(tRelations.get(cleRelationS));
				parentsId=Integer.parseInt(parentS.substring(1));
			}
			DefaultMutableTreeNode parent=(DefaultMutableTreeNode)topics.get(new Integer(parentsId));
			
			if(cleRelation!=parentsId){
				((Topic)(parent.getUserObject())).setNodeType('P');
				parent.add(tid);
			}
			
		}
		thematiqueTreeModel = new DefaultTreeModel((DefaultMutableTreeNode)topics.get(new Integer(1)));
		setModel(thematiqueTreeModel);
		setRootVisible(false);
		setShowsRootHandles(true);
		setExpandsSelectedPaths(false);
		setToggleClickCount(CheesyKM.DEFAULTACTIONCLICKCOUNT);
		
		ToolTipManager.sharedInstance().registerComponent(this);
		TopicTreeCellRenderer ttcr=new TopicTreeCellRenderer();
		setCellRenderer(ttcr);
		
		//((Topic)((DefaultMutableTreeNode)(topics.get(new Integer(1)))).getUserObject()).docCount(false);
		
		class ThematiqueTreeExpansionListener implements TreeExpansionListener{
			public void treeCollapsed(TreeExpansionEvent event){
			}
			public void treeExpanded(TreeExpansionEvent event){
				//CheesyKM.echo(((Topic)((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()).toString()+" should now display");
				((Topic)((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).getUserObject()).shouldDisplay=true;
				
				Enumeration enfants=((DefaultMutableTreeNode)event.getPath().getLastPathComponent()).children();
				while(enfants.hasMoreElements()){
					DefaultMutableTreeNode n=(DefaultMutableTreeNode)enfants.nextElement();
					if(((Topic)n.getUserObject()).getNodeType()=='P'&&Thematique.this.isExpanded(new TreePath(n.getPath()))){
						Thematique.this.fireTreeExpanded(new TreePath(n.getPath()));
					}
				}
			}
		}
		addTreeExpansionListener(new ThematiqueTreeExpansionListener());
		
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = Thematique.this.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = Thematique.this.getPathForLocation(e.getX(), e.getY());
				
				if(selRow != -1) {
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)selPath.getLastPathComponent();
					Topic topic=(Topic)node.getUserObject();
					if(e.getClickCount() == CheesyKM.DEFAULTACTIONCLICKCOUNT&&e.getButton()==MouseEvent.BUTTON1) {
						if(topic.getNodeType()=='D'){
							CheesyKM.api.displayTopic(topic);
						}
					}
				}
			}
			
			public void mouseClicked(MouseEvent e){
				int selRow = Thematique.this.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = Thematique.this.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1&&e.getButton()==MouseEvent.BUTTON3) {
					DefaultMutableTreeNode node=(DefaultMutableTreeNode)selPath.getLastPathComponent();
					Topic topic=(Topic)node.getUserObject();
					//CheesyKM.echo(topic);
					Thematique.this.setSelectionPath(selPath);
					new TopicPopupMenu(e.getComponent(),e.getX(),e.getY(),topic,true);
				}
			}
			
		};
		addMouseListener(ml);
		} catch(java.lang.InstantiationException ie){
			CheesyKM.echo("InstantiationException:"+ie);
		} catch(java.lang.IllegalAccessException iae){
			CheesyKM.echo("IllegalAccessException:"+iae);
		}
	}
	
	/**
	*Creates a Thematique component from the result of a "getTopicMatrix" request.
	*/
	Thematique(){
		this(Topic.class,null);
	}
	
	/**
	*Overrides JTrees getToolTipText method.<br>
	*Called by the ToolTipManager.
	*/
	public String getToolTipText(MouseEvent e) {
		JTree tree=(JTree)e.getComponent();
		int overRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath overPath = tree.getPathForLocation(e.getX(), e.getY());
		if(overRow!=-1&&overPath!=null){
			Topic underMouse=(Topic)((DefaultMutableTreeNode)overPath.getLastPathComponent()).getUserObject();
			if(underMouse.hasBeenCount){
				if(underMouse.docCount(false)!=-1){
					return underMouse.docCount(false)+CheesyKM.getLabel("documents");//sur un topic
				} else {
					return ((Doc)underMouse).getToolTip();//sur un doc
				}
			} else {
				return null;//pas encore scannée
			}
		} else {
			return CheesyKM.getLabel("topicTreeView");//pas sur un topic/doc
		}
	}
	
	/**
	*Returns the path for a Topic in this Tree.
	*@param tid Topic ID as an <code>int</code>.
	*@return Treepath for this Topic, or null if not found.
	*/
	public TreePath getPathForTopic(int tid){
		DefaultMutableTreeNode t=(DefaultMutableTreeNode)topics.get(new Integer(tid));
		if(t!=null){
			return new TreePath(t.getPath());
		} else {
			return null;
		}
	}
	
	/**
	*Returns the path for a Topic in this Tree.
	*@param tid Topic ID as a String ("TXX").
	*@return Treepath for this Topic, or null if not found.
	*/
	public TreePath getPathForTopic(String tid){
		return getPathForTopic(Integer.parseInt(tid.substring(1)));
	}
	
	/**
	*Expands the path in this Tree to a {@link Topic}.
	*@param tid Topic ID as a String ("TXX").
	*/
	public void expandPathToTopic(String tid){
		new BGDocCount(((Topic)(((DefaultMutableTreeNode)(this.getPathForTopic(tid).getLastPathComponent())).getUserObject())),true);
	}
	
}





