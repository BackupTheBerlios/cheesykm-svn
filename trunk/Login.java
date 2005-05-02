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
	Login(Frame parent){
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
		final JTextField id=new JTextField();
		login.add(id);
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(CheesyKM.getLabel("password")+" :"));
		final JPasswordField pw=new JPasswordField();
		login.add(pw);
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		login.add(new JLabel(" "));
		final JButton bValider=new JButton(CheesyKM.getLabel("okidoki"));
		
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
				if(id.getText().length()==0||new String(pw.getPassword()).length()==0){
					JOptionPane.showMessageDialog(Login.this, CheesyKM.getLabel("whatDoYouThink"), CheesyKM.getLabel("typeError"), JOptionPane.ERROR_MESSAGE);
				} else {
					id.setEnabled(false);
					pw.setEnabled(false);
					bValider.setEnabled(false);
					CheesyKM.setLogin(id.getText(),new String(pw.getPassword()));
					Login.this.pbd=new ProgBarDialog(Login.this);
					Thread t=new Thread(showFrame);
					t.start();
					pbd.show();
					if(topicMatrix==null){
						CheesyKM.setLogin(null,null);
						pw.setText("");
						JOptionPane.showMessageDialog(Login.this, CheesyKM.getLabel("youHaventSaidTheMagicWord"), CheesyKM.getLabel("wrongPass"), JOptionPane.ERROR_MESSAGE);
						id.setEnabled(true);
						pw.setEnabled(true);
						bValider.setEnabled(true);
						id.requestFocus();
					} else {
						Login.this.dispose();
						((CheesyKMAPI)(Login.this.getParent())).menuDeconnecter.setEnabled(true);
						CheesyKM.tRelations=(Hashtable)topicMatrix.get(1);
						CheesyKM.setTNames((Hashtable)topicMatrix.get(0));
						CheesyKM.tRights=(Hashtable)topicMatrix.get(2);
						CheesyKM.rootTopics=(Vector)topicMatrix.get(3);
						((CheesyKMAPI)(Login.this.getParent())).initAtLogon();
						CheesyKM.LASTLOGIN=CheesyKM.login;
					}
				}
			}
		}
		bValider.addActionListener(new bValiderListener());
		pw.addActionListener(new bValiderListener());
		
		
		
		login.add(bValider);
		cp.add(login,"East");
		this.setResizable(false);
		this.setTitle(CheesyKM.getLabel("loginPrompt"));
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.show();
		this.requestFocus();
		this.toFront();
		id.requestFocus();
		if(CheesyKM.REMEMBERLASTLOGIN){
			id.setText(CheesyKM.LASTLOGIN);
			pw.requestFocus();
		}
	}
	
	private Runnable showFrame = new Runnable() {
		public void run() {
			topicMatrix=CheesyKM.getTopicMatrix();
			pbd.hide();
		}
	};
}
