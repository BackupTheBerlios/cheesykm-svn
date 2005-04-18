import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
*Renderer for the news list.<br>
*A renderer tells the L&F how to display a component, here it permits to set a custom icon, depending on the type of the document.
*/
class NouveauteListCellRenderer extends DefaultListCellRenderer{
	
	NouveauteListCellRenderer(){
		
	}
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
		super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
		Doc doc=(Doc)value;
		setIcon(new ImageIcon("./ressources/m"+doc.ftype+".png"));
		this.setFont(this.getFont().deriveFont(Font.PLAIN));
		return this;
	}
	
	
}
