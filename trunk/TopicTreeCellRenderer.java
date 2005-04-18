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
		topicI=new ImageIcon("./ressources/mtopic.png");
		greytopicI=new ImageIcon("./ressources/mgreytopic.png");
		opentopicI=new ImageIcon("./ressources/mopentopic.png");
		topicPI=new ImageIcon("./ressources/mtopic+.png");
		opentopicPI=new ImageIcon("./ressources/mopentopic+.png");
	}
	
	public Component getTreeCellRendererComponent(JTree tree,Object node,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
		super.getTreeCellRendererComponent(tree,node,sel,expanded,leaf,row,hasFocus);
		Topic topic=(Topic)((DefaultMutableTreeNode)node).getUserObject();
		char type=topic.getNodeType();
		if(type=='D'){
			//"print", "image", "video", "sound", "act", "web", "note"
			Doc doc=(Doc)topic;
			setIcon(new ImageIcon("./ressources/m"+doc.ftype+".png"));
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
