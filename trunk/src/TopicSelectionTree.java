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
import javax.swing.plaf.basic.*;
/**
*Topic selection component.<br>
*Displays a topic tree view of all the session-accessible topics, with a checkBox at each node.
*/
class TopicSelectionTree extends Thematique{
	boolean childrenFollow;
	JComponent toActivate;
	int minimumRightsLevel;
	/**
	*Builds a topic selection tree from the topics that are accessible to the current session.
	*@param selectedTids Vector of tids to select in the tree as Strings ("TXX").
	*@param childrenFollow if <code>true</code>, children will be (de)selected when (de)selecting a node.
	*@param toActivate JComponent to <code>setEnabled(true)</code> if something is selected in the tree.
	*@param minimumRightsLevel right level (Topic.RIGHT_X), when a topic has a inferior right level, it is disabled (unselectable)
	*/
	TopicSelectionTree(Vector selectedTids,boolean childrenFollow,JComponent toActivate,int minimumRightsLevel){
		super(SelectionTopic.class,selectedTids);
		this.minimumRightsLevel=minimumRightsLevel;
		this.toActivate=toActivate;
		this.childrenFollow=childrenFollow;
		setCellRenderer(null);
		this.setRootVisible(false);
		this.setShowsRootHandles(false);
		((BasicTreeUI)this.getUI()).setCollapsedIcon(null);
		((BasicTreeUI)this.getUI()).setExpandedIcon(null);
		setCellRenderer(new TopicSelectionTreeCellRenderer());
		class TopicSelectionTreeCheckListener extends MouseAdapter{
			public void mouseClicked(MouseEvent e){
				int selRow = TopicSelectionTree.this.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = TopicSelectionTree.this.getPathForLocation(e.getX(), e.getY());
				if(selRow!=-1){
					Rectangle wholeCell=TopicSelectionTree.this.getRowBounds(selRow);
					wholeCell.width=wholeCell.height;
					Rectangle handle=new Rectangle(wholeCell);
					wholeCell.x=wholeCell.x+wholeCell.width;
					Rectangle checkBox=new Rectangle(wholeCell);
					if(checkBox.contains(e.getPoint())&&e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==1){
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)selPath.getLastPathComponent();
						SelectionTopic topic=(SelectionTopic)node.getUserObject();
						
						if(topic.rights>=TopicSelectionTree.this.minimumRightsLevel){
							if(TopicSelectionTree.this.childrenFollow){
								topic.selectChildren(!topic.selected);//Selection here
							} else {
								topic.selected=!topic.selected;
							}
						}
						
						
						TopicSelectionTree.this.repaint();
					} else {
						if(handle.contains(e.getPoint())||(e.getClickCount()==CheesyKM.DEFAULTACTIONCLICKCOUNT)){
							if(TopicSelectionTree.this.isExpanded(selPath)){
								TopicSelectionTree.this.setSelectionPath(selPath);
								TopicSelectionTree.this.collapsePath(selPath);
							} else {
								TopicSelectionTree.this.setSelectionPath(selPath);
								TopicSelectionTree.this.expandPath(selPath);
							}
						} else {
							TopicSelectionTree.this.setSelectionPath(selPath);
						}
					}
							
				}
				if(TopicSelectionTree.this.toActivate!=null){
					TopicSelectionTree.this.toActivate.setEnabled(TopicSelectionTree.this.getSelectedTopics().size()>0);
				}
			}
		}
		this.addMouseListener(new TopicSelectionTreeCheckListener());
		
		
		MouseListener[] ml=this.getMouseListeners();
		for(int i=0;i<ml.length;i++){
			this.removeMouseListener(ml[i]);
		}
		this.addMouseListener(ml[ml.length-1]);
		
	}
	/**
	*Overrides JTrees getToolTipText method.<br>
	*Called by the ToolTipManager.
	*/
	public String getToolTipText(MouseEvent e) {
		if(CheesyKM.SHOWTOOLTIPS){
			return CheesyKM.getLabel("toolTipCheckTopics");
		} else {
			return null;
		}
	}
	/**
	*Expand the path to some Topics in this tree (so that they become visible).
	*@param tids Vector of TopicIDs as Strings ("TXX").
	*/
	public void expandPathToTopics(Vector tids){
		for(int i=0;i<tids.size();i++){
			if(this.getPathForTopic(tids.get(i).toString())!=null)
			this.expandPath(this.getPathForTopic(tids.get(i).toString()).getParentPath());
		}
	}
	
	
	
	/**
	*Returns the selected topics as a Vector of topic IDs. These IDs are Strings ("TXX").
	*@return selected topics.
	*/
	public Vector getSelectedTopics(){
		return ((SelectionTopic)((DefaultMutableTreeNode)this.getModel().getRoot()).getUserObject()).getSelectedChildren();
	}
	
	/**
	*Sets the selected topics.
	*@param toSelect the selected topics as a Vector of topic IDs. These IDs are Strings ("TXX").
	*/
	public void setSelectedTopics(Vector toSelect){
		Enumeration keys=topics.keys();
		while(keys.hasMoreElements()){
			Integer key=(Integer)keys.nextElement();
			SelectionTopic t=(SelectionTopic)((DefaultMutableTreeNode)topics.get(key)).getUserObject();
			if(toSelect.contains("T"+key)){
				t.selected=true;
			} else {
				t.selected=false;
			}
		}
		expandPathToTopics(toSelect);
		repaint();
	}
	
}
