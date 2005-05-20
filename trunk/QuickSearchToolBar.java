import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
*Search toolbar.
*/
class QuickSearchToolBar extends JToolBar{
	/**Where to search ?*/
	JComboBox where;
	/**What to search ?*/
	JTextField what;
	JButton goForIt,advancedSearch;
	ProgBarDialog pbd;
	Vector resu;
	/**
	*New search toolbar, all tiny and ready to serve you.
	*/
	QuickSearchToolBar(){
		super();
		setBorderPainted(false);
		setFloatable(true);
		
		
		Object fields[] ={new Field(CheesyKM.getLabel("anywhere"),"anywhere"),new Field(CheesyKM.getLabel("inTitle"),"title"),new Field(CheesyKM.getLabel("inAuthor"),"author"),new Field(CheesyKM.getLabel("inKeywords"),"kwords"),new Field(CheesyKM.getLabel("inText"),"text")};
		this.setLayout(new BorderLayout());
		
		where=new JComboBox(fields);
		if(CheesyKM.SHOWTOOLTIPS)where.setToolTipText(CheesyKM.getLabel("toolTipQSBarWhere"));
		what=new JTextField();
		if(CheesyKM.SHOWTOOLTIPS)what.setToolTipText(CheesyKM.getLabel("toolTipQSBarWhat"));
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
		if(CheesyKM.SHOWTOOLTIPS)goForIt.setToolTipText(CheesyKM.getLabel("toolTipQSBar"));
		goForIt.addActionListener(new GoForItListener(what,where));
		what.addActionListener(new GoForItListener(what,where));
		
		
		class AdvancedSearchListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				CheesyKM.api.displayTopic(new AdvancedSearchTopic());
			}
		}
		advancedSearch=new JButton(CheesyKM.getLabel("advancedSearch"));
		if(CheesyKM.SHOWTOOLTIPS)advancedSearch.setToolTipText(CheesyKM.getLabel("toolTipASBar"));
		advancedSearch.addActionListener(new AdvancedSearchListener());
		advancedSearch.setEnabled(false);
		JPanel east=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
		JPanel west=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
		east.add(goForIt);
		east.add(advancedSearch);
		west.add(where);
		add(west,"West");
		add(what,"Center");
		add(east,"East");
		this.setEnabled(false);
	}
	/**
	*Used to disable ("grey") the search bar when no session is open.
	*@param b <code>false</code> to disable the bar, <code>true</code> to enable it.
	*/
	public void setEnabled(boolean b){
		this.what.setEnabled(b);
		this.where.setEnabled(b);
		this.goForIt.setEnabled(b);
		this.advancedSearch.setEnabled(b);
	}
	
	private Runnable showFrame = new Runnable() {
		public void run() {
			String ouca=((Field)where.getSelectedItem()).name;
			resu=CheesyKM.quickSearch(ouca,what.getText());
			pbd.hide();
			//pbd.dispose();
		}
	};
	/**
	*Represents a search field (where to search)
	*/
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
