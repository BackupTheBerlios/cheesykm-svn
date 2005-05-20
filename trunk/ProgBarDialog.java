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

/**
*Shows an animated bar, above all other frames.<br>
*Used when something should be long.
*/
public class ProgBarDialog extends JDialog {
	JProgressBar progBar;
	/**
	*Shows a modal ProgBarDialog.
	*/
	public ProgBarDialog(JDialog parent){
		super(parent);
		this.setModal(true);
		this.setUndecorated(true);
		this.setResizable(false);
		int maHauteur=30;
		int maLargeur=250;
		this.setBounds(parent.getX()+((parent.getWidth()-maLargeur)/2),parent.getY()+((parent.getHeight()-maHauteur)/2),maLargeur,maHauteur);
		final Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		progBar=new JProgressBar();
		progBar.setIndeterminate(true);
		cp.add(new JLabel(CheesyKM.getLabel("pleaseWait"),SwingConstants.CENTER),"North");
		cp.add(progBar,"Center");
	}
	/**
	*Shows a non-modal ProgBarDialog.
	*/
	public ProgBarDialog(JFrame parent){
		//new ProgBarDialog(CheesyKM.api,"Demande en cours...",false);
		super(parent);
		//this.setModal(modal);
		this.setUndecorated(true);
		this.setResizable(false);
		int maHauteur=30;
		int maLargeur=250;
		this.setBounds(parent.getX()+((parent.getWidth()-maLargeur)/2),parent.getY()+((parent.getHeight()-maHauteur)/2),maLargeur,maHauteur);
		final Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		progBar=new JProgressBar();
		progBar.setIndeterminate(true);
		cp.add(new JLabel(CheesyKM.getLabel("pleaseWait"),SwingConstants.CENTER),"North");
		cp.add(progBar,"Center");
	}
	
	/**
	*Shows a ProgBarDialog.
	*@param label Label to display above the animated bar.
	*@param modal <code>true</code> if modal, <code>false</code> else.
	*/
	public ProgBarDialog(JFrame parent,String label,boolean modal){
		super(parent);
		this.setModal(modal);
		this.setUndecorated(true);
		this.setResizable(false);
		int maHauteur=30;
		int maLargeur=250;
		this.setBounds(parent.getX()+((parent.getWidth()-maLargeur)/2),parent.getY()+((parent.getHeight()-maHauteur)/2),maLargeur,maHauteur);
		final Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		progBar=new JProgressBar();
		progBar.setIndeterminate(true);
		cp.add(new JLabel(label,SwingConstants.CENTER),"North");
		cp.add(progBar,"Center");
	}
}
