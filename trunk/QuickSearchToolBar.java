import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;

class QuickSearchToolBar extends JToolBar{
	JComboBox where;
	JTextField what;
	JButton goForIt;
	ProgBarDialog pbd;
	Vector resu;
	QuickSearchToolBar(){
		super();
		setBorderPainted(false);
		setFloatable(true);
		
		
		Object fields[] ={new Field(CheesyKM.getLabel("anywhere"),"anywhere"),new Field(CheesyKM.getLabel("inTitle"),"title"),new Field(CheesyKM.getLabel("inAuthor"),"author"),new Field(CheesyKM.getLabel("inKeywords"),"kwords"),new Field(CheesyKM.getLabel("inText"),"text")};
		this.setLayout(new BorderLayout());
		
		where=new JComboBox(fields);
		what=new JTextField();
		what.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e){
				((JTextComponent)(e.getComponent())).selectAll();
			}
		});
		
		what.setColumns(25);
		
		
		class GoForItListener implements ActionListener{
			JTextField quoi;
			JComboBox where;
			GoForItListener(JTextField quoi,JComboBox where){
				this.quoi=quoi;
				this.where=where;
			}
			public void actionPerformed(ActionEvent e){
				
				if(quoi.getText().equals("")){
					JOptionPane.showMessageDialog(CheesyKM.api, CheesyKM.getLabel("iCannotReadInYourBrain"), CheesyKM.getLabel("emptySearch"), JOptionPane.INFORMATION_MESSAGE);
				} else {
					
					QuickSearchToolBar.this.pbd=new ProgBarDialog(CheesyKM.api,CheesyKM.getLabel("searchInProgress"),true);
					Thread t=new Thread(showFrame);
					t.start();
					pbd.show();
					//CheesyKM.echo(resu);
					JScrollPane jsp=new JScrollPane(new SearchResultTree(resu));
					if(CheesyKM.api.jtpG.getTabCount()==3) CheesyKM.api.jtpG.removeTabAt(2);
					CheesyKM.api.jtpG.addTab(CheesyKM.getLabel("results"),null,jsp,CheesyKM.getLabel("lastSearchResults"));
					CheesyKM.api.jtpG.setSelectedIndex(2);
				}
			}
		}
		goForIt=new JButton(CheesyKM.getLabel("quickSearch"));
		goForIt.addActionListener(new GoForItListener(what,where));
		what.addActionListener(new GoForItListener(what,where));
		
		
		add(where,"West");
		add(what,"Center");
		add(goForIt,"East");
		this.setEnabled(false);
	}
	
	public void setEnabled(boolean b){
		this.what.setEnabled(b);
		this.where.setEnabled(b);
		this.goForIt.setEnabled(b);
	}
	
	private Runnable showFrame = new Runnable() {
		public void run() {
			String ouca=((Field)where.getSelectedItem()).name;
			resu=CheesyKM.quickSearch(ouca,what.getText());
			pbd.hide();
			//pbd.dispose();
		}
	};
	
	class Field {
		String name;
		String fullName;
		Field(String fullName,String name){
			this.fullName=fullName;
			this.name=name;
		}
		
		public String toString(){return this.fullName;}
	}
}
