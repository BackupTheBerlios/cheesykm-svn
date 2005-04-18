import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
*News List.
*/
class Nouveaute extends JList {
	/**
	*New news list, contains {@link CheesyKM#NOMBREDENOUVEAUTES} news.
	*/
	Nouveaute(){
		super();
		Vector docsH=CheesyKM.getLastDocs();
		Vector docs=new Vector();
		for(int i=0;i<docsH.size();i++){
			docs.add(new Doc((Hashtable)docsH.get(i),null));
		}
		this.setListData(docs);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(this);
		this.setCellRenderer(new NouveauteListCellRenderer());
		
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getClickCount() == CheesyKM.DEFAULTACTIONCLICKCOUNT&&e.getButton()==MouseEvent.BUTTON1) {
					int indexByLocation=Nouveaute.this.locationToIndex(e.getPoint());
					Rectangle resultByCellBounds=Nouveaute.this.getCellBounds(indexByLocation,indexByLocation);
					if(resultByCellBounds.contains(e.getPoint())){
						Doc doc=(Doc)Nouveaute.this.getModel().getElementAt(indexByLocation);
						CheesyKM.api.displayTopic(doc);
					}
				}
			}
			
			public void mouseClicked(MouseEvent e){
				if(e.getButton()==MouseEvent.BUTTON3) {
					int indexByLocation=Nouveaute.this.locationToIndex(e.getPoint());
					Rectangle resultByCellBounds=Nouveaute.this.getCellBounds(indexByLocation,indexByLocation);
					if(resultByCellBounds.contains(e.getPoint())){
						Doc doc=(Doc)Nouveaute.this.getModel().getElementAt(indexByLocation);
						Nouveaute.this.setSelectedIndex(Nouveaute.this.locationToIndex(e.getPoint()));
						new TopicPopupMenu(e.getComponent(),e.getX(),e.getY(),doc,true);
					}
				}
			}
		};
		addMouseListener(ml);
		
	}
	
	/**
	*Overrides JLists getToolTipText method.<br>
	*Called by the ToolTipManager to display tooltips.
	*/
	public String getToolTipText(MouseEvent e) {
		//this.getModel().getElementAt(this.locationToIndex(e.getPoint()))
		if(this.getModel().getElementAt(this.locationToIndex(e.getPoint()))!=null){
			Doc underMouse=((Doc)(this.getModel().getElementAt(this.locationToIndex(e.getPoint()))));
			return underMouse.getToolTip();//sur un doc
		} else {
			return null;//pas sur un doc
		}
	}
}
