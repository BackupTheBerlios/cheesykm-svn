import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
/**
*JDialog with all the fields necessary to register/edit/update a document.
*/
class RegisterDocWizard extends JDialog{
	int docID,score,currentPanel;
	Vector topics,ufEditable;
	String description,author,creadate,title,kwords,url,file,expires;
	JPanel mainPanel;
	EditableFieldGroup panel1,panel2,panel3,panel4;
	TopicSelectionTree tst;
	JSlider ratingS;
	JButton previous,next,cancel;
	Hashtable uf;
	ProgBarDialog pbd;
	/**
	*Updates (registers a new version of) a document.
	*@param doc the Document to update.
	*/
	RegisterDocWizard(Doc doc){
		this(doc,new Vector(),false);
	}
	/**
	*Updates (registers a new version of) or edits a document.
	*@param doc the Document to update.
	*@param edit if true edits doc, if false updates doc.
	*/
	RegisterDocWizard(Doc doc,boolean edit){
		this(doc,new Vector(),edit);
	}
	
	/**
	*Updates (registers a new version of) or edits or creates a document, specifying the topics in which the new Document will be.
	*@param doc the Document to update.
	*@param inTids TopicIDs of the topics in which the new Document will be, as Strings ("TXX").
	*@param edit if true edits doc, if false updates doc.
	*/
	RegisterDocWizard(Doc doc,Vector inTids,final boolean edit){
		super(CheesyKM.api);
		Container gc=this.getContentPane();
		if(doc==null){
			docID=0;
			score=80;
			topics=new Vector();
			description=new String();
			author=new String();
			creadate=new String();
			title=new String();
			kwords=new String();
			url=new String("http://");
			file=new String();
			expires=new String();
			uf=new Hashtable();
			ufEditable=new Vector();
			for(int i=0;i<CheesyKM.UFNUMBER;i++){
				//if(!CheesyKM.ufNames.get(i).toString().equals("")){
				if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
					uf.put(((Hashtable)CheesyKM.easyKMConfig.get("ext"+i)).get("name"),"");
					ufEditable.add(new Integer(i));
				}
			}
		} else {
			docID=doc.id;
			score=doc.score;
			topics=doc.topicList;
			description=doc.description;
			author=doc.author;
			creadate=CheesyKM.dateFromEasyKM(doc.creadate);
			title=doc.title;
			kwords=doc.kwords;
			url=doc.url;
			//file=doc.title+"."+doc.format;
			file="";
			expires=CheesyKM.dateFromEasyKM(doc.expires);
			uf=doc.uf;
			ufEditable=new Vector();
			for(int i=0;i<CheesyKM.UFNUMBER;i++){
				if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
					ufEditable.add(new Integer(i));
				}
			}
		}
		for(int i=0;i<inTids.size();i++){
			this.topics.add("T"+inTids.get(i).toString());
		}
		
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		mainPanel=new JPanel();
		gc.setLayout(new BorderLayout());
		gc.add(mainPanel,"Center");
		JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		currentPanel=1;
		previous=new JButton(CheesyKM.getLabel("previousStep"));
		previous.setEnabled(false);
		next=new JButton(CheesyKM.getLabel("nextStep"));
		next.setEnabled(doc!=null);
		cancel=new JButton(CheesyKM.getLabel("cancel"));
		next.setIcon(CheesyKM.loadIcon("./ressources/VCRForward.gif"));
		this.getRootPane().setDefaultButton(next);
		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				if(JOptionPane.showConfirmDialog(RegisterDocWizard.this,CheesyKM.getLabel("cancelRegister"), CheesyKM.getLabel("cancel")+" ?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					RegisterDocWizard.this.dispose();
				}
			}
		};
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
		
		previous.setIcon(CheesyKM.loadIcon("./ressources/VCRBack.gif"));
		cancel.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		
		class PreviousButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RegisterDocWizard.this.currentPanel--;
				if(RegisterDocWizard.this.currentPanel<=0){
					RegisterDocWizard.this.currentPanel=1;
					((JButton)e.getSource()).setEnabled(false);
				}
				RegisterDocWizard.this.mainPanel.removeAll();
				RegisterDocWizard.this.mainPanel.add(createPanel(RegisterDocWizard.this.currentPanel));
				RegisterDocWizard.this.validate();
				RegisterDocWizard.this.pack();
				if(RegisterDocWizard.this.currentPanel<=1){
					RegisterDocWizard.this.previous.setEnabled(false);
				}
				RegisterDocWizard.this.next.setEnabled(true);
				RegisterDocWizard.this.next.setText(CheesyKM.getLabel("nextStep"));
				RegisterDocWizard.this.next.setIcon(CheesyKM.loadIcon("./ressources/VCRForward.gif"));
				RegisterDocWizard.this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-RegisterDocWizard.this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-RegisterDocWizard.this.getHeight())/2),RegisterDocWizard.this.getWidth(),RegisterDocWizard.this.getHeight());
				RegisterDocWizard.this.validate();
				RegisterDocWizard.this.pack();
			}
		}
		
		class NextButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				RegisterDocWizard.this.currentPanel++;
				if(RegisterDocWizard.this.currentPanel==5){
					registerDoc(edit);
					RegisterDocWizard.this.dispose();
				} else {
					if(RegisterDocWizard.this.currentPanel==4){
						((JButton)e.getSource()).setText(CheesyKM.getLabel("register"));
						((JButton)e.getSource()).setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
						((JButton)e.getSource()).setEnabled(RegisterDocWizard.this.tst.getSelectedTopics().size()>0);
					}
					RegisterDocWizard.this.mainPanel.removeAll();
					RegisterDocWizard.this.mainPanel.add(createPanel(RegisterDocWizard.this.currentPanel));
					RegisterDocWizard.this.validate();
					RegisterDocWizard.this.pack();
					if(RegisterDocWizard.this.currentPanel>0){
						RegisterDocWizard.this.previous.setEnabled(true);
					}
					RegisterDocWizard.this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-RegisterDocWizard.this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-RegisterDocWizard.this.getHeight())/2),RegisterDocWizard.this.getWidth(),RegisterDocWizard.this.getHeight());
					RegisterDocWizard.this.validate();
					RegisterDocWizard.this.pack();
				}
			}
		}
		
		class CancelButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(JOptionPane.showConfirmDialog(RegisterDocWizard.this,CheesyKM.getLabel("cancelRegister"), CheesyKM.getLabel("cancel")+" ?", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					RegisterDocWizard.this.dispose();
				}
			}
		}
		
		next.addActionListener(new NextButtonListener());
		previous.addActionListener(new PreviousButtonListener());
		cancel.addActionListener(new CancelButtonListener());
		south.add(previous);
		south.add(next);
		south.add(cancel);
		initPanels(edit);
		mainPanel.add(createPanel(1));
		gc.add(south,"South");
		this.pack();
		if(this.docID==0){
			this.setTitle(CheesyKM.getLabel("registerNewDoc"));
		} else if(edit){
			this.setTitle(CheesyKM.getLabel("editDocument"));
		} else {
			this.setTitle(CheesyKM.getLabel("updateThisDoc"));
		}
		this.validate();
		this.setModal(true);
		
		this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-this.getHeight())/2),this.getWidth(),this.getHeight());
		this.show();
	}
	/**
	*Returns the panel with number "num" in the registration process.
	*@param num the number of the desired panel.
	*@return the matching JPanel.
	*/
	private JPanel createPanel(int num){
		switch(num){
			case 1:
			return panel1;
			case 2:
			return panel2;
			case 3:
			return panel3;
			case 4:
			return panel4;
			default:
			return null;
		}
	}
	/**
	*Called once at RegisterDocWizrd construction, initializes the differents panels of the registration process.
	*/
	private void initPanels(boolean edit){
		panel1=new EditableFieldGroup(this.next);
		StringValue titleS=new StringValue(CheesyKM.getLabel("title")+" :",title);
		titleS.setHasToBeSet(true);
		panel1.addEditableField(titleS);
		StringValue authorS=new StringValue(CheesyKM.getLabel("author"),author);
		panel1.addEditableField(authorS);
		DateValue creaDateD=new DateValue(CheesyKM.getLabel("creaDate")+" :",creadate);
		panel1.addEditableField(creaDateD);
		JButton todayB=new JButton(CheesyKM.getLabel("today"));
		class TodayBListener implements ActionListener{
			JTextField tf;
			TodayBListener(JTextField tf){this.tf=tf;}
			public void actionPerformed(ActionEvent e){
				tf.setText(CheesyKM.today());
			}
		}
		todayB.addActionListener(new TodayBListener(creaDateD.tf));
		creaDateD.add(todayB);
		DateValue expiresD=new DateValue(CheesyKM.getLabel("expires")+" :",expires);
		panel1.addEditableField(expiresD);
		
		panel2=new EditableFieldGroup(this.next);
		BigStringValue descS=new BigStringValue(CheesyKM.getLabel("description")+" :",description);
		panel2.addEditableField(descS);
		StringValue kwordsS=new StringValue(CheesyKM.getLabel("kwords")+" :",kwords);
		panel2.addEditableField(kwordsS);
		Enumeration ufKeys=uf.keys();
		Vector keysV=new Vector();
		while(ufKeys.hasMoreElements())keysV.add(ufKeys.nextElement());
		for(int i=0;i<keysV.size();i++){
			String key=keysV.get(i).toString();
			if(!key.equals("")){
				Vector possibleValues=(Vector)((Vector)((Hashtable)CheesyKM.easyKMConfig.get("ext"+ufEditable.get(i).toString())).get("values")).clone();
				if(possibleValues.size()==0){
					panel2.addEditableField(new StringValue(key+" :",uf.get(key).toString()));
				} else {
					possibleValues.insertElementAt(CheesyKM.getLabel("blank"),0);
					DropDownListValue ddlv=new DropDownListValue(key+" :",possibleValues);
					if(uf.get(key).equals("")){
						ddlv.value.setSelectedItem(((Hashtable)CheesyKM.easyKMConfig.get("ext"+ufEditable.get(i).toString())).get("default"));
					} else {
						ddlv.value.setSelectedItem(uf.get(key));
					}
					panel2.addEditableField(ddlv);
				}
			}
		}
		
		
		panel3=new EditableFieldGroup(this.next);
		if(edit){
			panel3.addEditableField(new BooleanValue(CheesyKM.getLabel("updateEditDate"),false));
		} else {
			OpenFileNameValue fileS=new OpenFileNameValue(CheesyKM.getLabel("file")+" :",file);
			panel3.addEditableField(fileS);
		}
		StringValue urlS=new StringValue(CheesyKM.getLabel("webSite")+" :",url);
		panel3.addEditableField(urlS);
		
		panel4=new EditableFieldGroup(this.next);
		panel4.add(new JLabel(CheesyKM.getLabel("topicsToPutIn")+" :"));
		tst=new TopicSelectionTree(this.topics,false,this.next,Topic.RIGHT_RW);
		JScrollPane tstScroll=new JScrollPane(tst);
		tst.expandPathToTopics(this.topics);
		tstScroll.setPreferredSize(new Dimension(300,300));
		panel4.add(tstScroll);
		
		ratingS=new JSlider(JSlider.HORIZONTAL,0,100,this.score);
		ratingS.setMajorTickSpacing(20);
		ratingS.setPaintTicks(true);
		ratingS.setPaintLabels(true);
		
		if(!edit&&Integer.parseInt(CheesyKM.easyKMConfig.get("eval").toString())==1){
			panel4.add(new JLabel(CheesyKM.getLabel("rating")));
			panel4.add(ratingS);
		}
	}
	
	/**
	*Builds a registerDoc request Hashtable, and calls the "registerDoc"  or "updateDoc" RPC method.
	*@param edit true if it is an edition of a document, false if it is a registration/update of a document.
	*/
	private void registerDoc(boolean edit){
		Hashtable metadata=new Hashtable();
		metadata.put("docid",new Integer(this.docID));
		this.topics.addAll(tst.getSelectedTopics());
		metadata.put("topics",tst.getSelectedTopics());
		metadata.put("description",panel2.getValues().get(0));
		metadata.put("author",panel1.getValues().get(1));
		metadata.put("creadate",CheesyKM.dateToEasyKM(panel1.getValues().get(2).toString()));
		metadata.put("title",panel1.getValues().get(0));
		metadata.put("kwords",panel2.getValues().get(1));
		metadata.put("url",panel3.getValues().get(1));
		if(metadata.get("url").equals("http://")) metadata.put("url","");
		if(edit){
			if(((Boolean)panel3.getValues().get(0)).booleanValue()){
				metadata.put("modstamp",new Integer(1));
			} else {
				metadata.put("modstamp",new Integer(0));
			}
		} else {
			metadata.put("file",panel3.getValues().get(0));
		}
		if(!edit)metadata.put("score",new Integer(this.ratingS.getValue()));
		metadata.put("expires",CheesyKM.dateToEasyKM(panel1.getValues().get(3).toString()));
		for(int i=0;i<CheesyKM.UFNUMBER;i++){
			if(ufEditable.contains(new Integer(i))){
				if(panel2.getValues().get(ufEditable.indexOf(new Integer(i))+2).equals(CheesyKM.getLabel("blank"))){
					metadata.put("uf"+i,"");
				} else {
					metadata.put("uf"+i,panel2.getValues().get(ufEditable.indexOf(new Integer(i))+2));
				}
			} else {
				metadata.put("uf"+i,"");
			}
		}
		boolean ok=false;
		if(!edit){
			boolean resuOkiFTP=true;
			if(!metadata.get("file").equals("")){
				String fileName=metadata.get("file").toString();
				String remoteName=new Integer(Math.abs(fileName.hashCode()+this.title.hashCode())).toString();
				String extension=fileName.substring(fileName.lastIndexOf("."));
				remoteName=remoteName+extension;
				resuOkiFTP=CheesyKM.upload(metadata.get("file").toString(),remoteName);
				metadata.put("file",remoteName);
			}
			
			Vector resu;
			if(resuOkiFTP){
				
				class Runner extends Thread{
					Vector resu;
					Hashtable metadata;
					ProgBarDialog pbd;
					Runner(Hashtable metadata,ProgBarDialog pbd){
						this.metadata=metadata;
						this.pbd=pbd;
						this.start();
					}
					public void run(){
						
						resu=CheesyKM.registerDoc(metadata);
						if(pbd!=null) pbd.dispose();
					}
				}
				pbd=new ProgBarDialog(this);
				pbd.setModal(true);
				Runner runner=new Runner(metadata,pbd);
				pbd.show();
				resu=runner.resu;
				
			} else {
				resu=new Vector();
			}
			if(pbd!=null) pbd.dispose();
			boolean resuOki=false;
			if(resu.size()==2){
				if(Integer.class.isInstance(resu.get(1))){
					resuOki=true;
				}
			}
			resuOki=resuOki&&resuOkiFTP;
			if(resuOki){
				if(((Integer)resu.get(1)).intValue()==1){
					ok=true;
					if(ok){
						Vector toUpdate=new Vector();
						for(int i=0;i<this.topics.size();i++){
							toUpdate.add(new Integer(this.topics.get(i).toString().substring(1)));
						}
						if(docID!=0)
							CheesyKM.api.modifiedTopics(toUpdate,this.docID);
					}
					JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyRegistered")+"\n"+CheesyKM.getLabel("docHasBeenIndexed"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
				} else {
					ok=true;
					JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyRegistered"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
				}
				
			} else {
				if(resu.size()>0){
					JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileRegistering")+"\n"+CheesyKM.getLabel("sameDocsExists"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
					for(int i=0;i<resu.size();i++){
						Doc doc=(Doc)resu.get(i);
						for(int j=0;j<doc.topicList.size();j++){
							CheesyKM.api.thematique.expandPathToTopic(doc.topicList.get(j).toString());
						}
					}
				} else {
					JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileRegistering"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
				}
			}
			
		} else {
			class Runner extends Thread{
				Object resu;
				Hashtable metadata;
				ProgBarDialog pbd;
				Runner(Hashtable metadata,ProgBarDialog pbd){
					this.metadata=metadata;
					this.pbd=pbd;
					this.start();
				}
				public void run(){
					
					resu=CheesyKM.updateDoc(metadata);
					if(pbd!=null) pbd.dispose();
				}
			}
			pbd=new ProgBarDialog(this);
			pbd.setModal(true);
			Runner runner=new Runner(metadata,pbd);
			pbd.show();
			Object resu=runner.resu;
			
			
			if(Integer.class.isInstance(resu)){
				JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileEditing"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
			} else {
				ok=true;
				JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyEdited"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if(ok){
			Vector toUpdate=new Vector();
			for(int i=0;i<this.topics.size();i++){
				toUpdate.add(new Integer(this.topics.get(i).toString().substring(1)));
			}
			CheesyKM.api.modifiedTopics(toUpdate,this.docID);
		}
		
	}
}
