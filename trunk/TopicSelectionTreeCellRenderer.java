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
		JPanel cell=new JPanel(new FlowLayout(FlowLayout.CENTER,1,0));
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
		cb.setPreferredSize(new Dimension(16,16));
		
		
		if(!(topic.rights>=((TopicSelectionTree)tree).minimumRightsLevel)){
			cb.setEnabled(false);
			defaultLabel.setIcon(CheesyKM.loadIcon("./ressources/mgreytopic.png"));
			defaultLabel.setForeground(Color.lightGray);
		}
		
		
		cell.add(cb);
		cell.add(defaultLabel);
		return cell;
	}
}
