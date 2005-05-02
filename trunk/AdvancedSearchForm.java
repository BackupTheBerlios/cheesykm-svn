import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
/**
*Advanced search form.
*/
class AdvancedSearchForm extends JPanel{
	/**Number of fields (typed-in text search criterias).*/
	int nbChamps;
	/**Where is it possible to search a word ?*/
	Object fields[] ={new Field(CheesyKM.getLabel("anywhere"),"anywhere"),
		new Field(CheesyKM.getLabel("inTitle"),"title"),
		new Field(CheesyKM.getLabel("inAuthor"),"author"),
		new Field(CheesyKM.getLabel("inKeywords"),"kwords"),
		new Field(CheesyKM.getLabel("inText"),"text")};
	/**Condition applied to the words.*/
	Object conditions[] ={new Field(CheesyKM.getLabel("may"),"may"),
		new Field(CheesyKM.getLabel("must"),"must"),
		new Field(CheesyKM.getLabel("mustnot"),"mustnot"),
		new Field(CheesyKM.getLabel("phrases"),"phrases")};
	/**Comparators (for comparable user-extended attributes)*/
	Object comparateurs[] ={"=","!=",">",">=","<","<="};
	//"print", "image", "video", "sound", "act", "web", "note"
	/**Available types of documents.*/
	Object type[] ={new Field(CheesyKM.getLabel("print"),"print"),
		new Field(CheesyKM.getLabel("image"),"image"),
		new Field(CheesyKM.getLabel("video"),"video"),
		new Field(CheesyKM.getLabel("sound"),"sound"),
		new Field(CheesyKM.getLabel("act"),"act"),
		new Field(CheesyKM.getLabel("web"),"web"),
		new Field(CheesyKM.getLabel("thread"),"thread"),
		new Field(CheesyKM.getLabel("note"),"note")};
	/**Available formats of documents.*/
	Object format[] ={
		new Field(CheesyKM.getLabel("anyFormat"),"any"),
		new Field("Adobe PDF","pdf"),
		new Field("Postscript","ps"),
		new Field("MS Word","doc"),
		new Field("MS Excel","xls"),
		new Field("MS Powerpoint","ppt"),
		new Field("MS Access","mdb"),
		new Field("HTML","html"),
		new Field("Note","000"),
		new Field("Discussion","001"),
		new Field("RTF","rtf"),
		new Field("JPEG","jpg"),
		new Field("Tiff","tif"),
		new Field("GIF","gif"),
		new Field("PNG","png"),
		new Field("MP3","mp3"),
		new Field("AVI","avi"),
		new Field("QuickTime","mov"),
		new Field("MPEG","mpg"),
		new Field("Illustrator","ai"),
		new Field("EPS","eps"),
		new Field("WMF","wmf"),
		new Field("Flash","swf")
	};
	/**Comparators for the file size.*/
	Object tailleComp[]={
		new Field(CheesyKM.getLabel("sizeGreater"),">"),
		new Field(CheesyKM.getLabel("sizeEquals"),"="),
		new Field(CheesyKM.getLabel("sizeSmaller"),"<")
	};
	/**File size units.*/
	Object tailleUnit[]={
		new Field("Ko","1024"),
		new Field("Mo","1048576")
	};
	/**Comparators for the creation date.*/
	Object dateComp[]={
		new Field(CheesyKM.getLabel("dateAfter"),">"),
		new Field(CheesyKM.getLabel("dateEquals"),"="),
		new Field(CheesyKM.getLabel("dateBefore"),"<")
	};
	/**Comparators for the modification date.*/
	Object dateModif[]={
		new Field(CheesyKM.getLabel("sinceEver"),"ever"),
		new Field(CheesyKM.getLabel("since1Month"),CheesyKM.sinceXMonth(1)),
		new Field(CheesyKM.getLabel("since3Month"),CheesyKM.sinceXMonth(3)),
		new Field(CheesyKM.getLabel("since6Month"),CheesyKM.sinceXMonth(6)),
		new Field(CheesyKM.getLabel("since12Month"),CheesyKM.sinceXMonth(12))
	};
	Vector champs,extAtts;
	Hashtable typesCheck;
	JPanel documentChamps;
	JComboBox formatCombo,tailleCompCombo,tailleUnitCombo,dateCompCombo,dateModifCombo;
	DateValue creaDate;
	IntegerValue fileSize;
	JCheckBox includeNonDated,includeOutOfDate;
	JButton removeField;
	JButton search;
	TopicSelectionTree tst;
	ProgBarDialog pbd;
	Vector resu;
	Hashtable query;
	Vector resetCheckBoxes,resetTextFields,resetComboBoxes;
	/**
	*constructor, initializes the whole form, with all its fields set to blank.
	*@param nbChamps Number of fields (typed-in text search criterias).
	*/
	AdvancedSearchForm(int nbChamps){
		super();
		resetCheckBoxes=new Vector();
		resetTextFields=new Vector();
		resetComboBoxes=new Vector();
		this.nbChamps=nbChamps;
		this.setLayout(new BorderLayout());
		JPanel document=new JPanel();
		document.setLayout(new BoxLayout(document,BoxLayout.Y_AXIS));
		document.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("document")));
		documentChamps=new JPanel();
		documentChamps.setLayout(new BoxLayout(documentChamps,BoxLayout.Y_AXIS));
		champs=new Vector();
		for(int i=0;i<nbChamps;i++){
			JPanel champ=new JPanel(new BorderLayout());
			champ.add(new JComboBox(conditions),"West");
			champ.add(new JTextField(),"Center");
			JComboBox cbb=new JComboBox(fields);
			champ.add(cbb,"East");
			champs.add(champ);
			documentChamps.add(champ);
		}
		JPanel basChamps=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton addField=new JButton(CheesyKM.getLabel("addField"));
		removeField=new JButton(CheesyKM.getLabel("removeField"));
		removeField.setEnabled(this.nbChamps>0);
		class AddFieldActionListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				AdvancedSearchForm.this.nbChamps++;
				JPanel champ=new JPanel(new BorderLayout());
				champ.add(new JComboBox(AdvancedSearchForm.this.conditions),"West");
				champ.add(new JTextField(),"Center");
				champ.add(new JComboBox(AdvancedSearchForm.this.fields),"East");
				AdvancedSearchForm.this.champs.add(champ);
				AdvancedSearchForm.this.documentChamps.add(champ);
				AdvancedSearchForm.this.documentChamps.getParent().getParent().getParent().validate();
				AdvancedSearchForm.this.documentChamps.getParent().getParent().getParent().repaint();
				AdvancedSearchForm.this.removeField.setEnabled(true);
			}
		}
		addField.addActionListener(new AddFieldActionListener());
		
		class RemoveFieldActionListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				AdvancedSearchForm.this.nbChamps--;
				AdvancedSearchForm.this.documentChamps.remove(AdvancedSearchForm.this.documentChamps.getComponentCount()-1);
				AdvancedSearchForm.this.documentChamps.getParent().getParent().getParent().validate();
				AdvancedSearchForm.this.documentChamps.getParent().getParent().getParent().repaint();
				((JComponent)e.getSource()).setEnabled(AdvancedSearchForm.this.nbChamps>0);
			}
		}
		removeField.addActionListener(new RemoveFieldActionListener());
		
		basChamps.add(addField);
		basChamps.add(removeField);
		document.add(documentChamps);
		document.add(basChamps);
		add(document,"North");
		
		JPanel center=new JPanel(new BorderLayout());
		
		JPanel extAtt=new JPanel();
		extAtts=new Vector();
		extAtt.setLayout(new BoxLayout(extAtt,BoxLayout.Y_AXIS));
		extAtt.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("extAttribs")));
		
		for(int i=0;i<CheesyKM.UFNUMBER;i++){
			if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
				if(Integer.parseInt(((Hashtable)CheesyKM.easyKMConfig.get("ext"+i)).get("ordered").toString())==1){
					JPanel ext=new JPanel(new FlowLayout(FlowLayout.LEFT));
					ext.add(new JLabel(((Hashtable)CheesyKM.easyKMConfig.get("ext"+i)).get("name").toString()));
					JComboBox jcbb=new JComboBox(comparateurs);
					ext.add(jcbb);
					resetComboBoxes.add(jcbb);
					Vector possibleValues=(Vector)((Vector)((Hashtable)(CheesyKM.easyKMConfig.get("ext"+i))).get("values")).clone();
					possibleValues.insertElementAt(CheesyKM.getLabel("ignore"),0);
					jcbb=new JComboBox(possibleValues);
					ext.add(jcbb);
					resetComboBoxes.add(jcbb);
					extAtts.add(ext);
					extAtt.add(ext);
				} else {
					extAtts.add(new JPanel());
				}
			} else {
				extAtts.add(new JPanel());
			}
		}
		center.add(extAtt,"North");
		
		JPanel avance=new JPanel(new BorderLayout());
		avance.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("advanced")));
		JPanel leftThing=new JPanel();
		leftThing.setLayout(new BoxLayout(leftThing,BoxLayout.X_AXIS));
		JPanel typeDeDoc=new JPanel();
		typeDeDoc.setLayout(new BoxLayout(typeDeDoc,BoxLayout.Y_AXIS));
		typeDeDoc.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("type")));
		JPanel typesButtons=new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		class SelectAllListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				Enumeration keys=AdvancedSearchForm.this.typesCheck.keys();
				while(keys.hasMoreElements()){
					((JCheckBox)AdvancedSearchForm.this.typesCheck.get(keys.nextElement())).setSelected(true);
				}
			}
		}
		JButton selectAllTypes=new JButton(CheesyKM.getLabel("selectAllTypes"));
		selectAllTypes.addActionListener(new SelectAllListener());
		
		class DeselectAllListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				Enumeration keys=AdvancedSearchForm.this.typesCheck.keys();
				while(keys.hasMoreElements()){
					((JCheckBox)AdvancedSearchForm.this.typesCheck.get(keys.nextElement())).setSelected(false);
				}
			}
		}
		JButton deselectAllTypes=new JButton(CheesyKM.getLabel("deselectAllTypes"));
		deselectAllTypes.addActionListener(new DeselectAllListener());
		
		typesButtons.add(selectAllTypes);
		typesButtons.add(deselectAllTypes);
		typeDeDoc.add(typesButtons);
		
		typesCheck=new Hashtable();
		int k=0;
		JPanel threeBoxes=new JPanel(new FlowLayout(FlowLayout.LEADING,5,0));
		for(int i=0;i<type.length;i++){
			JCheckBox jcb=new JCheckBox(type[i].toString());
			resetCheckBoxes.add(jcb);
			threeBoxes.add(jcb);
			typesCheck.put(((Field)type[i]).name,jcb);
			k++;
			if(k==3){
				k=0;
				typeDeDoc.add(threeBoxes);
				threeBoxes=new JPanel(new FlowLayout(FlowLayout.LEADING,5,0));
			}
		}
		typeDeDoc.add(threeBoxes);
		JPanel leftRightThing=new JPanel();
		leftRightThing.add(typeDeDoc);
		
		
		leftRightThing.setLayout(new BoxLayout(leftRightThing,BoxLayout.Y_AXIS));
		JPanel fichier=new JPanel();
		fichier.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("file")));
		fichier.setLayout(new BoxLayout(fichier,BoxLayout.Y_AXIS));
		JPanel fichierFormat=new JPanel(new FlowLayout(FlowLayout.LEFT));
		fichierFormat.add(new JLabel(CheesyKM.getLabel("fileFormat")));
		formatCombo=new JComboBox(format);
		resetComboBoxes.add(formatCombo);
		fichierFormat.add(formatCombo);
		fichier.add(fichierFormat);
		JPanel fichierTaille=new JPanel(new FlowLayout(FlowLayout.LEFT));
		fichierTaille.add(new JLabel(CheesyKM.getLabel("size")));
		tailleCompCombo=new JComboBox(tailleComp);
		resetComboBoxes.add(tailleCompCombo);
		fichierTaille.add(tailleCompCombo);
		fileSize=new IntegerValue("",0);
		fichierTaille.add(fileSize);
		tailleUnitCombo=new JComboBox(tailleUnit);
		resetComboBoxes.add(tailleUnitCombo);
		resetComboBoxes.add(tailleUnitCombo);
		fichierTaille.add(tailleUnitCombo);
		fichier.add(fichierTaille);
		leftRightThing.add(fichier);
		
		JPanel dates=new JPanel();
		dates.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("dates")));
		dates.setLayout(new BoxLayout(dates,BoxLayout.Y_AXIS));
		JPanel dateCreat=new JPanel(new FlowLayout(FlowLayout.LEFT));
		dateCreat.add(new JLabel(CheesyKM.getLabel("creation")));
		dateCompCombo=new JComboBox(dateComp);
		resetComboBoxes.add(dateCompCombo);
		dateCreat.add(dateCompCombo);
		creaDate=new DateValue("","");
		creaDate.registerForGroup(new ValidateSearchForm());
		dateCreat.add(creaDate);
		resetTextFields.add(creaDate.tf);
		dates.add(dateCreat);
		includeNonDated=new JCheckBox(CheesyKM.getLabel("includeNonDated"));
		includeOutOfDate=new JCheckBox(CheesyKM.getLabel("includeOutOfDate"));
		resetCheckBoxes.add(includeNonDated);
		resetCheckBoxes.add(includeOutOfDate);
		JPanel nonDated=new JPanel(new FlowLayout(FlowLayout.LEFT));
		nonDated.add(includeNonDated);
		dates.add(nonDated);
		JPanel outOfDate=new JPanel(new FlowLayout(FlowLayout.LEFT));
		outOfDate.add(includeOutOfDate);
		dates.add(outOfDate);
		JPanel dateModified=new JPanel(new FlowLayout(FlowLayout.LEFT));
		dateModified.add(new JLabel(CheesyKM.getLabel("modified")));
		dateModifCombo=new JComboBox(dateModif);
		resetComboBoxes.add(dateModifCombo);
		dateModified.add(dateModifCombo);
		dates.add(dateModified);
		leftRightThing.add(dates);
		
		leftThing.add(leftRightThing);
		
		avance.add(leftThing,"West");
		
		search=new JButton(CheesyKM.getLabel("search"));
		search.setEnabled(false);
		tst=new TopicSelectionTree(null,true,search,Topic.RIGHT_R);
		JPanel cadreTree=new JPanel(new BorderLayout());
		cadreTree.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("topics")));
		
		JScrollPane tstScroll=new JScrollPane(tst);
		cadreTree.setPreferredSize(new Dimension(100,100));
		cadreTree.setMaximumSize(new Dimension(100,100));
		cadreTree.add(new JScrollPane(tst),"Center");
		avance.add(cadreTree,"Center");
		center.add(avance,"South");
		
		JPanel bas=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		search.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		bas.add(search);
		class SearchButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				search();
			}
		}
		search.addActionListener(new SearchButtonListener());
		class ResetButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				Vector comboToAdd=new Vector();
				Vector TFToAdd=new Vector();
				for(int i=0;i<AdvancedSearchForm.this.champs.size();i++){
					comboToAdd.add(((JPanel)AdvancedSearchForm.this.champs.get(i)).getComponent(0));
					TFToAdd.add(((JPanel)AdvancedSearchForm.this.champs.get(i)).getComponent(1));
					comboToAdd.add(((JPanel)AdvancedSearchForm.this.champs.get(i)).getComponent(2));
				}
				AdvancedSearchForm.this.resetComboBoxes.addAll(comboToAdd);
				for(int i=0;i<AdvancedSearchForm.this.resetComboBoxes.size();i++){
					((JComboBox)AdvancedSearchForm.this.resetComboBoxes.get(i)).setSelectedIndex(0);
				}
				AdvancedSearchForm.this.resetComboBoxes.removeAll(comboToAdd);
				for(int i=0;i<AdvancedSearchForm.this.resetCheckBoxes.size();i++){
					((JCheckBox)AdvancedSearchForm.this.resetCheckBoxes.get(i)).setSelected(false);
				}
				AdvancedSearchForm.this.resetTextFields.addAll(TFToAdd);
				for(int i=0;i<AdvancedSearchForm.this.resetTextFields.size();i++){
					((JTextField)AdvancedSearchForm.this.resetTextFields.get(i)).setText("");
				}
				AdvancedSearchForm.this.resetTextFields.removeAll(TFToAdd);
				AdvancedSearchForm.this.fileSize.tf.setText("0");
				Container tstContainer=AdvancedSearchForm.this.tst.getParent();
				tstContainer.removeAll();
				AdvancedSearchForm.this.tst=new TopicSelectionTree(null,true,AdvancedSearchForm.this.search,Topic.RIGHT_R);
				tstContainer.add(AdvancedSearchForm.this.tst);
				AdvancedSearchForm.this.validateTree();
				AdvancedSearchForm.this.getParent().validate();
				AdvancedSearchForm.this.getParent().repaint();
				AdvancedSearchForm.this.search.setEnabled(false);
			}
		}
		JButton reset=new JButton(CheesyKM.getLabel("reset"));
		reset.setIcon(CheesyKM.loadIcon("./ressources/RotCWLeft.gif"));
		reset.addActionListener(new ResetButtonListener());
		bas.add(reset);
		
		class CancelButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				CheesyKM.api.hideTopic(CheesyKM.api.jtpD.getSelectedIndex());
			}
		}
		JButton cancel=new JButton(CheesyKM.getLabel("cancel"));
		cancel.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		cancel.addActionListener(new CancelButtonListener());
		bas.add(cancel);
		
		add(center,"Center");
		
		add(bas,"South");
	}
	
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
		
		public boolean equals(Object o){
			return this.name.equals(((Field)o).name);
		}
	}
	/**
	*Class to interface the "Search" button with some EditableFields elements.
	*/
	class ValidateSearchForm implements Wuffable{
		public void sayWuff(){
			AdvancedSearchForm.this.search.setEnabled(AdvancedSearchForm.this.creaDate.isValid()&&AdvancedSearchForm.this.tst.getSelectedTopics().size()>0);
		}
	}

	/**
	*Builds a search request Hashtable and calls the "search" RPC method.
	*/
	public void search(){
		query=new Hashtable();
		for(int i=0;i<this.fields.length;i++){
			Hashtable qExp=new Hashtable();
			for(int j=0;j<this.conditions.length;j++){
				Vector arrayS=new Vector();
				for(int k=0;k<this.champs.size();k++){
					if(((Field)((JComboBox)((JPanel)this.champs.get(k)).getComponent(0)).getSelectedItem()).equals(this.conditions[j])&&((Field)((JComboBox)((JPanel)this.champs.get(k)).getComponent(2)).getSelectedItem()).equals(this.fields[i])){
						if(((Field)this.conditions[j]).name.equals("prases")){
							arrayS.add(((JTextField)((JPanel)this.champs.get(k)).getComponent(1)).getText());
						} else {
							StringTokenizer strtk=new StringTokenizer(((JTextField)((JPanel)this.champs.get(k)).getComponent(1)).getText(),",; ",false);
							
							while(strtk.hasMoreElements()){
								arrayS.add(strtk.nextToken());
							}
						}
					}
				}
				qExp.put(((Field)this.conditions[j]).name,arrayS);
			}
			query.put(((Field)fields[i]).name,qExp);
		}
		query.put("searchmode","boolean");
		Vector types=new Vector();
		for(int i=0;i<this.type.length;i++){
			if(((JCheckBox)this.typesCheck.get(((Field)this.type[i]).name)).isSelected()){
				types.add(((Field)this.type[i]).name);
			}
		}
		query.put("types",types);
		query.put("topics",this.tst.getSelectedTopics());
		query.put("since",((Field)this.dateModifCombo.getSelectedItem()).name);
		if(this.creaDate.value().equals("")){
			query.put("creation","ever");
		} else {
			query.put("creation",this.creaDate.value());
		}
		query.put("creation_op",((Field)this.dateCompCombo.getSelectedItem()).name);
		if(this.includeNonDated.isSelected()){
			query.put("creation_mode",new Integer(1));
		} else {
			query.put("creation_mode",new Integer(0));
		}
		query.put("format",((Field)this.formatCombo.getSelectedItem()).name);
		query.put("size",new Integer(((Integer)this.fileSize.value()).intValue()*Integer.parseInt(((Field)this.tailleUnitCombo.getSelectedItem()).name)));
		query.put("size_op",((Field)this.tailleCompCombo.getSelectedItem()).name);
		query.put("limit",new Integer(500));
		if(this.includeOutOfDate.isSelected()){
			query.put("expired",new Integer(1));
		} else {
			query.put("expired",new Integer(0));
		}
		for(int i=0;i<CheesyKM.UFNUMBER;i++){
			if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
				if(Integer.parseInt(((Hashtable)CheesyKM.easyKMConfig.get("ext"+i)).get("ordered").toString())==1){
					query.put("ufop"+i,((JComboBox)((JPanel)this.extAtts.get(i)).getComponent(1)).getSelectedItem().toString());
					if(((JComboBox)((JPanel)this.extAtts.get(i)).getComponent(2)).getSelectedItem().equals(CheesyKM.getLabel("ignore"))){
						query.put("ufterm"+i,"");
					} else {
						query.put("ufterm"+i,((JComboBox)((JPanel)this.extAtts.get(i)).getComponent(2)).getSelectedItem().toString());
					}
				} else {
					query.put("ufterm"+i,"");
					query.put("ufop"+i,"=");
				}
			} else {
				query.put("ufterm"+i,"");
				query.put("ufop"+i,"=");
			}
		}
		//CheesyKM.echo("QUERY:"+query);
		
		this.pbd=new ProgBarDialog(CheesyKM.api,CheesyKM.getLabel("searchInProgress"),true);
		Thread t=new Thread(showFrame);
		t.start();
		pbd.show();
		//CheesyKM.echo(resu);
		JScrollPane jsp=new JScrollPane(new SearchResultTree(resu));
		if(CheesyKM.api.jtpG.getTabCount()==3) CheesyKM.api.jtpG.removeTabAt(2);    
		CheesyKM.api.jtpG.addTab(CheesyKM.getLabel("results"),null,jsp,CheesyKM.getLabel("lastSearchResults"));
		CheesyKM.api.jtpG.setSelectedIndex(2);
	}
	
	private Runnable showFrame = new Runnable() {
		public void run() {
			resu=CheesyKM.search(query);
			pbd.hide();
		}
	};
}
