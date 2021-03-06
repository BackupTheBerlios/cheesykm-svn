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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
*Delete document confirmation dialog.
*/
class DeleteDoc extends JDialog{
	JRadioButton last,all;
	DeleteDoc(final Doc doc){
		super(CheesyKM.api);
		if(doc.version==1){
			if(JOptionPane.showConfirmDialog(CheesyKM.api,CheesyKM.getLabel("doYouReallyWannaDelete")+"\n"+doc.title, CheesyKM.getLabel("confirmDelete"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
				if(CheesyKM.deleteDocRPC(doc)){
					JOptionPane.showMessageDialog(null, CheesyKM.getLabel("docDeleted"), CheesyKM.getLabel("success"), JOptionPane.INFORMATION_MESSAGE);
					CheesyKM.api.thematique.repaint();
					if(CheesyKM.api.isAffiche(doc)!=-1)
						CheesyKM.api.hideTopic(doc);
				} else {
					JOptionPane.showMessageDialog(null, CheesyKM.getLabel("errorDeleting"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			ButtonGroup bg=new ButtonGroup();
			last=new JRadioButton(CheesyKM.getLabel("deleteLastVersion"),true);
			all=new JRadioButton(CheesyKM.getLabel("deleteAllVersions"),false);
			bg.add(last);
			bg.add(all);
			JPanel center=new JPanel();
			center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
			center.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("severalVersions")));
			center.add(last);
			center.add(all);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(center,"Center");
			JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT));
			south.add(new JLabel("             "));
			JButton delete=new JButton(CheesyKM.getLabel("okidoki"));
			delete.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
			south.add(delete);
			class DeleteButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e){
					if(all.isSelected()){
						if(CheesyKM.deleteDocRPC(doc)){
							JOptionPane.showMessageDialog(null, CheesyKM.getLabel("docDeleted"), CheesyKM.getLabel("success"), JOptionPane.INFORMATION_MESSAGE);
							CheesyKM.api.thematique.repaint();
							if(CheesyKM.api.isAffiche(doc)!=-1)
								CheesyKM.api.hideTopic(doc);
						} else {
							JOptionPane.showMessageDialog(null, CheesyKM.getLabel("errorDeleting"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
						}
					} else {
						if(CheesyKM.rollbackDocRPC(doc)){
							JOptionPane.showMessageDialog(null, CheesyKM.getLabel("lastVersionOfDocDeleted"), CheesyKM.getLabel("success"), JOptionPane.INFORMATION_MESSAGE);
							CheesyKM.api.thematique.repaint();
							if(CheesyKM.api.isAffiche(doc)!=-1)
								CheesyKM.api.hideTopic(doc);
						} else {
							JOptionPane.showMessageDialog(null, CheesyKM.getLabel("errorDeletingLastVersion"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
						}
					}
					DeleteDoc.this.dispose();
				}
			}
			delete.addActionListener(new DeleteButtonListener());
			
			JButton cancel=new JButton(CheesyKM.getLabel("cancel"));
			cancel.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
			south.add(cancel);
			class CancelButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e){
					DeleteDoc.this.dispose();
				}
			}
			cancel.addActionListener(new CancelButtonListener());
			
			this.getContentPane().add(south,"South");
			this.pack();
			this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-this.getHeight())/2),this.getWidth(),this.getHeight());
			this.setModal(true);
			this.setResizable(false);
			this.setTitle(CheesyKM.getLabel("confirmDelete"));
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.show();
		}
		
	}
}
