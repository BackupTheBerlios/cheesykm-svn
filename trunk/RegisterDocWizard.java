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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
/**
*JDialog with all the fields necessary to register/edit/update a document.
*/
class RegisterDocWizard extends JDialog{
	static Vector toUpdate=new Vector();
	static BatchRunner batchRunner=new BatchRunner();
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
	boolean show;
	boolean ok=false;
	boolean batch;
	/**
	*Updates (registers a new version of) a document.
	*@param doc the Document to update.
	*/
	RegisterDocWizard(Doc doc){
		this(doc,new Vector(),false,true,false);
	}
	
	RegisterDocWizard(File f,Vector topics,boolean show){
		this(new Doc(f),topics,false,show,false);
	}
	
	RegisterDocWizard(boolean show,boolean batch,Doc doc){
		this(doc,new Vector(),false,show,batch);
	}
	/**
	*Updates (registers a new version of) or edits a document.
	*@param doc the Document to update.
	*@param edit if true edits doc, if false updates doc.
	*/
	RegisterDocWizard(Doc doc,boolean edit){
		this(doc,new Vector(),edit,true,false);
	}
	
	RegisterDocWizard(Doc doc,Vector inTids,boolean edit){
		this(doc,inTids,edit,true,false);
	}
	
	/**
	*Updates (registers a new version of) or edits or creates a document, specifying the topics in which the new Document will be.
	*@param doc the Document to update.
	*@param inTids TopicIDs of the topics in which the new Document will be, as Strings ("TXX").
	*@param edit if true edits doc, if false updates doc.
	*/
	RegisterDocWizard(Doc doc,Vector inTids,final boolean edit,boolean show,boolean batch){
		super(CheesyKM.api);
		this.show=show;
		this.batch=batch;
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
			file=doc.file;
			expires=CheesyKM.dateFromEasyKM(doc.expires);
			uf=doc.uf;
			ufEditable=new Vector();
			for(int i=0;i<CheesyKM.UFNUMBER;i++){
				if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
					ufEditable.add(new Integer(i));
				}
			}
			
		}
		if(description==null)description="";
		if(author==null)author="";
		if(kwords==null)kwords="";
		if(url==null)url="http://";
		if(file==null)file="";
		if(uf==null){
			uf=new Hashtable();
			ufEditable=new Vector();
			for(int i=0;i<CheesyKM.UFNUMBER;i++){
				//if(!CheesyKM.ufNames.get(i).toString().equals("")){
					if((((Hashtable)CheesyKM.easyKMConfig.get("ext"+i))!=null)){
						uf.put(((Hashtable)CheesyKM.easyKMConfig.get("ext"+i)).get("name"),"");
						ufEditable.add(new Integer(i));
					}
			}
		}
		if(inTids!=null){
			for(int i=0;i<inTids.size();i++){
				if(Integer.class.isInstance(inTids.get(i))){
					this.topics.add("T"+inTids.get(i).toString());
				} else {
					this.topics.add(inTids.get(i));
				}
			}
		}
		
		if(show){
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
			if(this.docID==0||batch){
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
		if(CheesyKM.SHOWTOOLTIPS)todayB.setToolTipText(CheesyKM.getLabel("toolTipToday"));
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
	*@return true if the document has been registered, false else.
	*/
	boolean registerDoc(boolean edit){
		
		Hashtable metadata=new Hashtable();
		if(show){
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
		} else {
			metadata.put("docid",new Integer(this.docID));
			metadata.put("topics",this.topics);
			metadata.put("description",this.description);
			metadata.put("author",this.author);
			metadata.put("creadate",CheesyKM.dateToEasyKM(this.creadate));
			metadata.put("title",this.title);
			metadata.put("kwords",this.kwords);
			metadata.put("url",this.url);
			if(metadata.get("url").equals("http://")) metadata.put("url","");
			
			metadata.put("file",this.file);
			
			metadata.put("score",new Integer(this.score));
			metadata.put("expires",this.expires);
			for(int i=0;i<CheesyKM.UFNUMBER;i++){
				metadata.put("uf"+i,"");
			}
		}
		
		if(!edit){
			boolean resuOkiFTP=true;
			if(!metadata.get("file").equals("")){
				String fileName=metadata.get("file").toString();
				String remoteName=new Integer(Math.abs(fileName.hashCode()+this.title.hashCode())).toString();
				if(fileName.indexOf(".")==-1){
					
				} else {
					String extension=fileName.substring(fileName.lastIndexOf("."));
					remoteName=remoteName+extension;
				}
				if(!batch){
					resuOkiFTP=CheesyKM.upload(metadata.get("file").toString(),remoteName);
				} else {
					batchRunner.addJob(new FTPJob(metadata.get("file").toString(),remoteName));
				}
				metadata.put("file",remoteName);
			}
			if(!batch){
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
					pbd=new ProgBarDialog(CheesyKM.api);
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
						if(!batch)JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyRegistered")+"\n"+CheesyKM.getLabel("docHasBeenIndexed"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
					} else {
						ok=true;
						if(!batch)JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyRegistered"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
					}
					
				} else {
					if(resu.size()>0){
						if(!batch)
						{
							JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileRegistering")+"\n"+CheesyKM.getLabel("sameDocsExists"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
							for(int i=0;i<resu.size();i++){
								Doc doc=new Doc((Hashtable)resu.get(i),null);
								for(int j=0;j<doc.topicList.size();j++){
									CheesyKM.api.thematique.expandPathToTopic(doc.topicList.get(j).toString());
								}
							}
						}
					} else {
						if(!batch)JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileRegistering"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				batchRunner.addJob(new RegisterDocJob(metadata));
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
			pbd=new ProgBarDialog(CheesyKM.api);
			pbd.setModal(true);
			Runner runner=new Runner(metadata,pbd);
			pbd.show();
			Object resu=runner.resu;
			
			
			if(Integer.class.isInstance(resu)){
				if(!batch)JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("errorWhileEditing"),CheesyKM.getLabel("error"),JOptionPane.ERROR_MESSAGE);
			} else {
				ok=true;
				if(!batch)JOptionPane.showMessageDialog(CheesyKM.api,CheesyKM.getLabel("docSucessfullyEdited"),CheesyKM.getLabel("success"),JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if(ok){
			for(int i=0;i<this.topics.size();i++){
				toUpdate.add(new Integer(this.topics.get(i).toString().substring(1)));
			}
			if(!batch)CheesyKM.api.modifiedTopics(toUpdate,this.docID);
		}
		return ok;
	}
}

/**
*Batch register job, used when importing whole folders into EasyKM.
*/
class BatchRunner extends Thread{
	private static Vector toDos=new Vector();
	private boolean noNewJobs;
	private ImportFiles log=null;
	private JProgressBar jpb=null;
	private boolean youKilledMe=false;
	private static synchronized Vector getToDos(){
		return toDos;
	}
	
	/**
	*Initializes a new BatchRunner, does not start it.
	*/
	BatchRunner(){
		this.noNewJobs=true;
	}
	/**
	*Sets the log ans progressbar associated with this BatchRunner.<br>
	*If log and jpb are not null, this BatchRunner will update these components while running.
	*@param log {@link ImportFiles} containing a "echo(String)" method to call when something has to be logged.
	*@param jpb JProgressBar, a bar to show the progress of this batch.
	*/
	public synchronized void setLog(ImportFiles log,JProgressBar jpb){
		this.log=log;
		this.jpb=jpb;
		
	}
	/**
	*Launches this Batch.
	*/
	public synchronized void setActive(){
		if(this.noNewJobs){
			this.noNewJobs=false;
			start();
		}
	}
	
	/**
	*Tells this batch that no new Jobs will be added, and that it can stop when its job list will ba empty.
	*/
	public synchronized void setInactive(){
		this.noNewJobs=true;
	}
	
	/**
	*run() method of this Tread, do all jobs, in the same order as they were added, and wait until new jobs are added, or {@link BatchRunner#setInactive()} has been called.
	*/
	public void run(){
		while(BatchRunner.getToDos().size()>0||(!noNewJobs)){
			if(BatchRunner.getToDos().size()==0){
				try{sleep(100);}catch(InterruptedException e){}
			} else {
				log.echo(CheesyKM.getLabel("starting")+" "+getToDos().get(0));
				if(jpb!=null)
					jpb.setString(getToDos().get(0).toString());
				if(((Job)getToDos().get(0)).doJob()){
					log.echo(getToDos().get(0)+" "+CheesyKM.getLabel("successfullyDone"));
				} else {
					log.echo(getToDos().get(0)+" "+CheesyKM.getLabel("failed"));
				}
				if(RegisterDocJob.class.isInstance(getToDos().get(0)))jpb.setValue(jpb.getValue()+1);
				this.getToDos().removeElementAt(0);
			}
			if(youKilledMe){
				this.noNewJobs=true;
				this.getToDos().removeAllElements();
				youKilledMe=false;//I'll be back!
			}
		}
		if(log!=null)log.endOfBatch();
	}
	/**
	*Adds a job to this batchs jobs list.
	*/
	public synchronized void addJob(Job job){
		getToDos().add(job);
	}
	/**
	*Forces the BatchRunner to stop, and erases all its jobs.
	*/
	public void kill(){
		youKilledMe=true;
	}
}
/**
*Represents something that can be done by the {@link BatchRunner}.
*/
interface Job{
	/**
	*Called by the {@link BatchRunner}, symply do the job.
	*/
	public boolean doJob();
}
/**
*Represents a FTP file upload Job.
*/
class FTPJob implements Job{
	String localName,remoteName;
	/**
	*New FTPJob.
	*@param here local filename of the file to upload.
	*@param overTheHillsAndFarAway remote filename.
	*/
	FTPJob(String here,String overTheHillsAndFarAway){
		this.localName=here;
		this.remoteName=overTheHillsAndFarAway;
	}
	
	public boolean doJob(){
		return CheesyKM.upload(localName,remoteName,false);
	}
	/**
	*String representation of this job, for logging purpose.
	*/
	public String toString(){
		return CheesyKM.getLabel("uploadOfFile")+" : "+localName;
	}
}
/**
*Represents a "registerDoc" RPC method call job.
*/
class RegisterDocJob implements Job{
	Hashtable meta;
	/**
	*New RegisterDocJob.
	*@param meta Hashtable to send to the "registerDoc" RPC method.
	*/
	RegisterDocJob(Hashtable meta){
		this.meta=meta;
	}
	public boolean doJob(){
		if(meta==null){
			return false;
		}
		Vector resu=CheesyKM.registerDoc(meta);
		if(resu==null){
			return false;
		} else if(resu.size()<2){
			return false;
		} else if(!Integer.class.isInstance(resu.get(1))){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	*String representation of this job, for logging purpose.
	*/
	public String toString(){
		return CheesyKM.getLabel("registerOfDoc")+" : "+meta.get("title");
	}
}
