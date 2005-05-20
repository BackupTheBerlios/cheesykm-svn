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
import javax.swing.text.*;

/**
*Login prompt dialog.<br>
*Checks if the identifier and password are OK and calls {@link CheesyKMAPI#initAtLogon()}, shows an error dialog else.
*/
class Login extends JDialog{
	private ProgBarDialog pbd;
	Vector topicMatrix;
	JTextField id;
	JPasswordField pw;
	JButton bValider;
	Login(Frame parent){
		this(parent,false,null,null);
	}
	
	Login(Frame parent,boolean relog,String user,String pass){
		super(parent);
		int maHauteur=220;
		int maLargeur=300;
		this.setBounds(parent.getX()+((parent.getWidth()-maLargeur)/2),parent.getY()+((parent.getHeight()-maHauteur)/2),maLargeur,maHauteur);
		final Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel login=new JPanel();
		login.setLayout(new GridLayout(7,3));
		
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(CheesyKM.getLabel("identifier")+" :"));
		id=new JTextField();
		login.add(id);
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(CheesyKM.getLabel("password")+" :"));
		pw=new JPasswordField();
		login.add(pw);
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		bValider=new JButton(CheesyKM.getLabel("okidoki"));
		
		class textFieldFocusListener extends FocusAdapter{
			public void focusGained(FocusEvent e){
				((JTextComponent)(e.getComponent())).selectAll();
			}
		}
		id.addFocusListener(new textFieldFocusListener());
		pw.addFocusListener(new textFieldFocusListener());
		
		class idListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				pw.requestFocus();
			}
		}
		id.addActionListener(new idListener());
		
		
		
		class bValiderListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				valider();
			}
		}
		bValider.addActionListener(new bValiderListener());
		pw.addActionListener(new bValiderListener());
		
		
		
		login.add(bValider);
		cp.add(login,"East");
		this.setResizable(false);
		this.setTitle(CheesyKM.getLabel("loginPrompt"));
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		if(relog){
			id.setText(user);
			pw.setText(pass);
			valider();
		} else {
			this.show();
			this.requestFocus();
			this.toFront();
			id.requestFocus();
			if(CheesyKM.REMEMBERLASTLOGIN){
				id.setText(CheesyKM.LASTLOGIN);
				pw.requestFocus();
			}
		}
	}
	
	public void valider(){
		
		if(id.getText().length()==0||new String(pw.getPassword()).length()==0){
			JOptionPane.showMessageDialog(this, CheesyKM.getLabel("whatDoYouThink"), CheesyKM.getLabel("typeError"), JOptionPane.ERROR_MESSAGE);
		} else {
			id.setEnabled(false);
			pw.setEnabled(false);
			bValider.setEnabled(false);
			CheesyKM.setLogin(id.getText(),new String(pw.getPassword()));
			this.pbd=new ProgBarDialog(this);
			Thread t=new Thread(showFrame);
			t.start();
			pbd.show();
			if(topicMatrix==null){
				CheesyKM.setLogin(null,null);
				pw.setText("");
				JOptionPane.showMessageDialog(this, CheesyKM.getLabel("youHaventSaidTheMagicWord"), CheesyKM.getLabel("wrongPass"), JOptionPane.ERROR_MESSAGE);
				id.setEnabled(true);
				pw.setEnabled(true);
				bValider.setEnabled(true);
				id.requestFocus();
			} else {
				this.dispose();
				((CheesyKMAPI)(this.getParent())).menuDeconnecter.setEnabled(true);
				CheesyKM.tRelations=(Hashtable)topicMatrix.get(1);
				CheesyKM.setTNames((Hashtable)topicMatrix.get(0));
				CheesyKM.tRights=(Hashtable)topicMatrix.get(2);
				Enumeration keys=CheesyKM.tRights.keys();
				while(keys.hasMoreElements()){
					int right=((Integer)CheesyKM.tRights.get(keys.nextElement())).intValue();
					if(right>CheesyKM.maximumRightLevel) CheesyKM.maximumRightLevel=right;
				}
				CheesyKM.rootTopics=(Vector)topicMatrix.get(3);
				((CheesyKMAPI)(this.getParent())).initAtLogon();
				CheesyKM.LASTLOGIN=CheesyKM.login;
			}
		}
	}
	
	private Runnable showFrame = new Runnable() {
		public void run() {
			topicMatrix=CheesyKM.getTopicMatrix();
			pbd.hide();
		}
	};
}
