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
*Topic tree renderer, for the topic tree view and the search result tree.<br>
*Called by the Swing manager to render the tree elements.
*/
public class TopicTreeCellRenderer extends DefaultTreeCellRenderer {
//"print", "image", "video", "sound", "act", "web", "note"
	ImageIcon topicI;
	ImageIcon greytopicI;
	ImageIcon opentopicI;
	ImageIcon topicPI;
	ImageIcon opentopicPI;
	
	ImageIcon printI;
	ImageIcon imageI;
	ImageIcon videoI;
	ImageIcon soundI;
	ImageIcon actI;
	ImageIcon webI;
	ImageIcon noteI;
	
	
	public TopicTreeCellRenderer() {
		topicI=CheesyKM.loadIcon("./ressources/mtopic.png");
		greytopicI=CheesyKM.loadIcon("./ressources/mgreytopic.png");
		opentopicI=CheesyKM.loadIcon("./ressources/mopentopic.png");
		topicPI=CheesyKM.loadIcon("./ressources/mtopic+.png");
		opentopicPI=CheesyKM.loadIcon("./ressources/mopentopic+.png");
	}
	
	public Component getTreeCellRendererComponent(JTree tree,Object node,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
		super.getTreeCellRendererComponent(tree,node,sel,expanded,leaf,row,hasFocus);
		Topic topic=(Topic)((DefaultMutableTreeNode)node).getUserObject();
		char type=topic.getNodeType();
		if(type=='D'){
			//"print", "image", "video", "sound", "act", "web", "note"
			Doc doc=(Doc)topic;
			setIcon(CheesyKM.loadIcon("./ressources/m"+doc.ftype+".png"));
		} else {
			if(expanded){
				if(type=='T'){
					setIcon(opentopicI);
				} else {
					if(!topic.hasBeenCount){
						ProgBarDialog pbd=new ProgBarDialog(CheesyKM.api);
						new PBDThread(pbd,topic);
						//pbd.show();
					}
					setIcon(opentopicPI);
				}
			} else {
				if(type=='T'){
					if(!topic.hasBeenCount&&((Topic)((DefaultMutableTreeNode)topic.getNode().getParent()).getUserObject()).shouldDisplay){
						setIcon(greytopicI);
						new BGDocCount(topic,false);
					} else {
						setIcon(topicI);
					}
				} else {
					setIcon(topicPI);
				}
			}
		}
		setText(topic.toString());
		
		
		//CheesyKM.echo("Famille:"+this.getFont().getFamily());
		//CheesyKM.echo("Nom:"+this.getFont().getFontName());
		
		return this;
	}
	
	class PBDThread extends Thread {
		ProgBarDialog pbd;
		Topic topic;
		PBDThread(ProgBarDialog pbd,Topic topic){
			this.pbd=pbd;
			this.topic=topic;
			start();
		}
		public void run() {
			topic.docCount(false);
			pbd.dispose();
		}
	}
}
