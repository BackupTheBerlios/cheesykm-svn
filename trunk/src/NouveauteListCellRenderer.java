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
		setIcon(CheesyKM.loadTypeIcon(doc.ftype));//mini
		this.setFont(this.getFont().deriveFont(Font.PLAIN));
		return this;
	}
	
	
}
