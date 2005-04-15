import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


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
