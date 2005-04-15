import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
*Affiche une barre animée "Demande en cours" devant toutes les autres fenêtres de l'application.<br>
*Utilisé quand une requête RPC risque d'être longue.
*/
public class ProgBarDialog extends JDialog {
	JProgressBar progBar;
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
