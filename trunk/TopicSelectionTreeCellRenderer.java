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
/**
*Topic selection tree renderer, for the topic selection tree.<br>
*Called by the Swing manager to render the tree elements.
*/
class TopicSelectionTreeCellRenderer implements TreeCellRenderer{
	public Component getTreeCellRendererComponent(JTree tree,Object node,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){
		SelectionTopic topic=(SelectionTopic)((DefaultMutableTreeNode)node).getUserObject();
		JLabel defaultLabel=(JLabel)new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree,node,sel,expanded,leaf,row,hasFocus);
		defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mgreytopic.png"));
		JPanel cell=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		JCheckBox cb=new JCheckBox();
		
		if(!leaf){
			if(expanded){
				cell.add(new JLabel(CheesyKM.loadIcon("./ressources/TreeMinus.gif")));
			} else {
				cell.add(new JLabel(CheesyKM.loadIcon("./ressources/TreePlus.gif")));
			}
		} else {
			cell.add(new JLabel(CheesyKM.loadIcon("./ressources/white16.png")));
		}
		if(topic.getNodeType()=='P'){
			if(expanded){
				defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mopentopic+.png"));
			} else {
				defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mtopic+.png"));
			}
		} else {
			if(expanded){
				defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mopentopic.png"));
			} else {
				defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mtopic.png"));
			}
		}
		if(topic.selected){
			//defaultLabel.setForeground(Color.blue);
		} else if(topic.selectedChildren()){
			defaultLabel.setForeground(Color.blue);
		} else {
			
		}
		cb.setBackground(tree.getBackground());
		cell.setBackground(tree.getBackground());
		cb.setSelected(topic.selected);
		cb.setMaximumSize(new Dimension(16,16));
		cb.setPreferredSize(new Dimension(18,16));
		
		
		if(!(topic.rights>=((TopicSelectionTree)tree).minimumRightsLevel)){
			cb.setEnabled(false);
			defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mgreytopic.png"));
			defaultLabel.setForeground(Color.lightGray);
			cb.setForeground(defaultLabel.getBackground());
			cb.setBackground(defaultLabel.getBackground());
		}
		
		if(topic.created){
			defaultLabel.setBackground(Color.orange);
			defaultLabel.setForeground(Color.orange);
			if(topic.rights==-5){
				defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/Copy16.gif"));
			}
		}
		
		cell.add(cb);
		cell.add(defaultLabel);
		return cell;
	}
}
