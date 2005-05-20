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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
*Displays a JSlider (0-100) in a JDialog.
*/
class RateDocDialog extends JDialog{
	JSlider ratingS;
	Doc doc;
	/**
	*Displays a JSlider (0-100) in a modal JDialog with OK/Cancel buttons. The RPC "rateDoc" method is called when the user clicks "OK".
	*@param doc the document to rate.
	*/
	RateDocDialog(Doc doc){
		super(CheesyKM.api);
		this.doc=doc;
		ratingS=new JSlider(JSlider.HORIZONTAL,0,100,doc.score);
		ratingS.setMajorTickSpacing(20);
		ratingS.setPaintTicks(true);
		ratingS.setPaintLabels(true);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(ratingS,"Center");
		JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton oki=new JButton(CheesyKM.getLabel("okidoki"));
		oki.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		class OkiButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RateDocDialog.this.doc.score=CheesyKM.rateDoc(RateDocDialog.this.doc,RateDocDialog.this.ratingS.getValue());
				int i=CheesyKM.api.getIndexForDisplayedTopic(RateDocDialog.this.doc.id);
				if(i!=-1){
					CheesyKM.api.hideTopic(i);
					CheesyKM.api.displayTopic(RateDocDialog.this.doc);
				}
				RateDocDialog.this.dispose();
			}
		}
		oki.addActionListener(new OkiButtonListener());
		JButton cancel=new JButton(CheesyKM.getLabel("cancel"));
		cancel.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		class CancelButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RateDocDialog.this.dispose();
			}
		}
		cancel.addActionListener(new CancelButtonListener());
		south.add(oki);
		south.add(cancel);
		this.getContentPane().add(south,"South");
		this.getContentPane().add(new JLabel(CheesyKM.getLabel("rateDoc"),JLabel.CENTER),"North");
		this.setModal(true);
		this.setUndecorated(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-this.getHeight())/2),this.getWidth(),this.getHeight());
		this.setVisible(true);
	}
}
