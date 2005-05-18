import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;
/**
*Import a whole folders/files structure into EasyKM.
*/
class ImportFiles extends JDialog{
	private JPanel topicSelPanel;
	private JPanel createTopicsPanel;
	private JCheckBox includeHiddenFiles;
	private JRadioButton createTopics,createTopicsN,confirm,confirmN,createTopicsInOneTopic,createTopicsInOneTopicN,registerInOneTopic,registerInOneTopicN;
	private TopicSelectionTree tst;
	private String rootPath;
	private TextArea textLog;
	private Hashtable nodesByNegTids,nodesByFiles,docsByFile;
	private JButton annuler;
	private boolean batchFinished=false;
	private JProgressBar jpb;
	/**
	*Shows a {@link FileChooserDialog}, and the Import folders options dialog if the user chose a folder.
	*/
	ImportFiles(){
		super(CheesyKM.api);
		nodesByNegTids=new Hashtable();
		nodesByFiles=new Hashtable();
		docsByFile=new Hashtable();
		rootPath=FileChooserDialog.showChooser(CheesyKM.api,CheesyKM.getLabel("open"),null,false,"",true);
		if(rootPath!=null){
			File rootFile=new File(rootPath);
			topicSelPanel=new JPanel(new BorderLayout());
			if(rootFile.isFile()){
				//importer un fichier simple
				new RegisterDocWizard(new Doc(rootFile));
			} else {
				//importer une arborescence de dossiers/fichiers.
				includeHiddenFiles=new JCheckBox(CheesyKM.getLabel("includeHiddenFiles"));
				includeHiddenFiles.setToolTipText(CheesyKM.getLabel("toolTipIncludeHidden"));
				createTopicsInOneTopic=new JRadioButton(CheesyKM.getLabel("createTopicsInOneTopic"));
				createTopicsInOneTopic.setToolTipText(CheesyKM.getLabel("toolTipInSameTopic"));
				createTopicsInOneTopicN=new JRadioButton(CheesyKM.getLabel("createTopicsInOneTopicN"));
				createTopicsInOneTopicN.setToolTipText(CheesyKM.getLabel("toolTipNInSameTopic"));
				createTopicsInOneTopic.setSelected(true);
				ButtonGroup createInOneGroup=new ButtonGroup();
				createInOneGroup.add(createTopicsInOneTopic);
				createInOneGroup.add(createTopicsInOneTopicN);
				
				registerInOneTopic=new JRadioButton(CheesyKM.getLabel("registerInOneTopic"));
				registerInOneTopic.setToolTipText(CheesyKM.getLabel("toolTipInSameDoc"));
				registerInOneTopic.setSelected(true);
				registerInOneTopicN=new JRadioButton(CheesyKM.getLabel("registerInOneTopicN"));
				registerInOneTopicN.setToolTipText(CheesyKM.getLabel("toolTipNInSameDoc"));
				ButtonGroup registerInOneGroup=new ButtonGroup();
				registerInOneGroup.add(registerInOneTopic);
				registerInOneGroup.add(registerInOneTopicN);
				
				createTopicsPanel=new JPanel();
				createTopicsPanel.setLayout(new BoxLayout(createTopicsPanel,BoxLayout.Y_AXIS));
				createTopicsPanel.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("topicsOptions")));
				JPanel createTopicsPanelHaut=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
				createTopics=new JRadioButton(CheesyKM.getLabel("createTopicsForDirs"));
				createTopics.setToolTipText(CheesyKM.getLabel("toolTipCreate"));
				createTopicsN=new JRadioButton(CheesyKM.getLabel("createTopicsForDirsN"));
				createTopicsN.setToolTipText(CheesyKM.getLabel("toolTipNCreate"));
				createTopics.setSelected(true);
				class createTopicActionListener implements ActionListener{
					public void actionPerformed(ActionEvent e) {
						if(ImportFiles.this.createTopics.isSelected()){
							ImportFiles.this.topicSelPanel.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("topicWhereToCreateTopics")));
							ImportFiles.this.createTopicsPanel.remove(1);
							ImportFiles.this.createTopicsPanel.add(getTopicPanel(true));
							ImportFiles.this.createTopicsPanel.validate();
							ImportFiles.this.createTopicsPanel.repaint();
							ImportFiles.this.tst.minimumRightsLevel=Topic.RIGHT_RWM;
							ImportFiles.this.tst.repaint();
						} else {
							ImportFiles.this.topicSelPanel.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("topicWhereToRegisterDoc")));
							ImportFiles.this.createTopicsPanel.remove(1);
							ImportFiles.this.createTopicsPanel.add(getTopicPanel(false));
							ImportFiles.this.createTopicsPanel.validate();
							ImportFiles.this.createTopicsPanel.repaint();
							ImportFiles.this.tst.minimumRightsLevel=Topic.RIGHT_RW;
							ImportFiles.this.tst.repaint();
						}
					}
				}
				createTopics.addActionListener(new createTopicActionListener());
				createTopicsN.addActionListener(new createTopicActionListener());
				createTopicsPanelHaut.add(createTopics);
				createTopicsPanelHaut.add(createTopicsN);
				ButtonGroup createHaut=new ButtonGroup();
				createHaut.add(createTopics);
				createHaut.add(createTopicsN);
				createTopicsPanel.add(createTopicsPanelHaut);
				createTopicsPanel.add(getTopicPanel(true));
				
				
				
				tst=new TopicSelectionTree(new Vector(),false,null,Topic.RIGHT_RWM);
				topicSelPanel.add(new JScrollPane(tst),"Center");
				confirm=new JRadioButton(CheesyKM.getLabel("confirmEachDoc"));
				confirm.setToolTipText(CheesyKM.getLabel("toolTipAsk"));
				confirm.setSelected(true);
				confirmN=new JRadioButton(CheesyKM.getLabel("confirmEachDocN"));
				confirmN.setToolTipText(CheesyKM.getLabel("toolTipNAsk"));
				JPanel confirmPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
				confirmPanel.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("confirmOption")));
				confirmPanel.add(confirm);
				confirmPanel.add(confirmN);
				ButtonGroup confirmG=new ButtonGroup();
				confirmG.add(confirm);
				confirmG.add(confirmN);
				JPanel options=new JPanel();
				options.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("folderImportOptions")));
				options.setLayout(new BoxLayout(options,BoxLayout.Y_AXIS));
				options.add(includeHiddenFiles);
				options.add(confirmPanel);
				options.add(createTopicsPanel);
				topicSelPanel.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("topicWhereToCreateTopics")));
				options.add(topicSelPanel);
				this.getContentPane().setLayout(new BorderLayout());
				this.getContentPane().add(options,"North");
				JPanel sud=new JPanel(new FlowLayout(FlowLayout.RIGHT));
				JButton valider=new JButton(CheesyKM.getLabel("okidoki"));
				valider.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
				JButton annuler=new JButton(CheesyKM.getLabel("cancel"));
				annuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
				class AnnulerListener implements ActionListener{
					public void actionPerformed(ActionEvent e) {
						ImportFiles.this.dispose();
					}
				}
				annuler.addActionListener(new AnnulerListener());
				
				class ValiderListener implements ActionListener{
					public void actionPerformed(ActionEvent e) {
						boolean oki=false;
						if(ImportFiles.this.tst.getSelectedTopics().size()==0){
							if(ImportFiles.this.createTopicsN.isSelected()&&ImportFiles.this.registerInOneTopic.isSelected()){
								JOptionPane.showMessageDialog(CheesyKM.api, CheesyKM.getLabel("selectDefaultTopicToRegister"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
							} else if(ImportFiles.this.createTopics.isSelected()&&ImportFiles.this.createTopicsInOneTopic.isSelected()){
								JOptionPane.showMessageDialog(CheesyKM.api, CheesyKM.getLabel("selectDefaultTopicToCreateTopicsIn"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
							} else {
								oki=true;
							}
						} else {
							oki=true;
						}
						if(oki){
							ImportFiles.this.hide();
							ImportFiles.this.validerDossier();
						}
					}
				}
				valider.addActionListener(new ValiderListener());
				sud.add(valider);
				sud.add(annuler);
				this.getContentPane().add(sud,"South");
				this.setTitle(CheesyKM.getLabel("importFolder"));
				this.setModal(true);
				this.setResizable(false);
				this.pack();
				this.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-this.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-this.getHeight())/2),this.getWidth(),this.getHeight());
				this.show();
			}
		}
	}
	
	private JPanel getTopicPanel(boolean create){
		JPanel resu=new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
		if(create){
			resu.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("whereToCreate")));
			resu.add(createTopicsInOneTopic);
			resu.add(createTopicsInOneTopicN);
		} else {
			resu.setBorder(BorderFactory.createTitledBorder(CheesyKM.getLabel("whereToRegister")));
			resu.add(registerInOneTopic);
			resu.add(registerInOneTopicN);
		}
		return resu;
	}
	/**
	*Renderer for the file tree, called by the UI Manager.
	*/
	class FileTreeCellRenderer extends DefaultTreeCellRenderer{
		ImageIcon topicI;
		ImageIcon opentopicI;
		ImageIcon topicPI;
		ImageIcon opentopicPI;
		FileTreeCellRenderer(){
			topicI=CheesyKM.loadIcon("./ressources/mtopic.png");
			opentopicI=CheesyKM.loadIcon("./ressources/mopentopic.png");
			topicPI=CheesyKM.loadIcon("./ressources/mtopic+.png");
			opentopicPI=CheesyKM.loadIcon("./ressources/mopentopic+.png");
		}
		/**
		*Called by the UIManager to paint a tree cell.
		*/
		public Component getTreeCellRendererComponent(JTree tree,Object node,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
			super.getTreeCellRendererComponent(tree,node,sel,expanded,leaf,row,hasFocus);
			if(expanded){
				if(leaf){
					setIcon(opentopicI);
				} else {
					setIcon(opentopicPI);
				}
			} else {
				if(leaf){
					setIcon(topicI);
				} else {
					setIcon(topicPI);
				}
			}
			
			if(ImportFiles.this.createTopicsN.isSelected()){
				this.setIcon(CheesyKM.loadIcon("./ressources/Copy16.gif"));
			}
			return this;
		}
	}
	/**
	*Node for the file tree.
	*/
	class Node extends DefaultMutableTreeNode{
		/**The File corresponding to this node.*/
		File f;
		/**If <code>true</code>, children of this node will be moved/deleted with it*/
		boolean childrenFollow;
		/**Vector of the TIDs of the Topics in wich this File will be imported.*/
		Vector topics=tst.getSelectedTopics();
		/**Temporary tid of this file, when moved to the Topic Tree View ("T-XX")*/
		String tid=null;
		/**Real Tid of the Topic corresponding to this file(folder), setted after its creation in EasyKM.*/
		String realTid=null;
		/**Topic assignation corresponding to this node.*/
		AddedTopic at=null;
		/**
		*New Node.
		*@param childrenFollow if <code>true</code>, children of this node will be moved/deleted with it.
		*/
		Node(boolean childrenFollow){
			super();
			this.childrenFollow=childrenFollow;
		}
		/**
		*Returns the file associated with this Node.
		*@return the file associated with this Node.
		*/
		public File getFile(){
			return (File)this.getUserObject();
		}
		/**
		*Sets the file associated with this Node.
		*@param f the File to associate with this Node.
		*/
		public void setUserObject(File f){
			this.f=f;
			ImportFiles.this.nodesByFiles.put(f,this);
			this.setUserObject((Object)f);
		}
		/**
		*String representation of this Node (its File name).
		*/
		public String toString(){
			String resu=new String();
			if(f==null){
				resu=this.getUserObject().toString();
			} else {
				resu=f.getName();
			}
			if(ImportFiles.this.createTopicsN.isSelected()){
				resu="("+CheesyKM.getLabel("filesIn")+" "+resu+")";
			}
			return resu;
			
		}
		/**
		*Sets the Topics in wich this node will be imported.
		*@param newTopics Vector of tIDs as String ("TXX").
		*/
		public void setTopics(Vector newTopics){
			this.topics=newTopics;
			if(this.childrenFollow&&this.children!=null){
				for(int i=0;i<this.children.size();i++){
					((Node)this.children.get(i)).setTopics(newTopics);
				}
			}
		}
		/**
		*Returns the children of this node in the file tree, returns null if this Node is a leaf in the tree.
		*@return Vetor of children Nodes or null.
		*/
		public Vector getChildren(){
			return this.children;
		}
	}
	
	
	private JTree createFolderTree(boolean showFiles,boolean childrenFollow){
		Node racineTree=new Node(childrenFollow);
		racineTree.setUserObject(CheesyKM.getLabel("import"));
		
		File root=new File(this.rootPath);
		Node racine=new Node(childrenFollow);
		racineTree.add(racine);
		racine.setUserObject(root);
		
		addChildren(racine,showFiles,childrenFollow);
		
		
		DefaultTreeModel model=new DefaultTreeModel(racineTree);
		JTree resu=new JTree(model);
		resu.setShowsRootHandles(true);
		resu.setToggleClickCount(2);
		DefaultTreeSelectionModel dtsm=new DefaultTreeSelectionModel();
		dtsm.setSelectionMode(dtsm.SINGLE_TREE_SELECTION);
		resu.setSelectionModel(dtsm);
		resu.setCellRenderer(new FileTreeCellRenderer());
		resu.setRootVisible(false);
		dtsm.setSelectionPath(new TreePath(racine.getPath()));
		resu.setToolTipText(CheesyKM.getLabel("toolTipSelectFolder"));
		return resu;
	}
	
	private void addChildren(Node n,boolean showFiles,boolean childrenFollow){
		File f=n.getFile();
		File[] children=f.listFiles();
		for(int i=0;i<children.length;i++){
			if(!(children[i].isHidden()&&!this.includeHiddenFiles.isSelected())&&!(children[i].isDirectory()&&children[i].listFiles().length==0)&&!(children[i].isFile()&&!showFiles)){
				Node noeud=new Node(childrenFollow);
				n.add(noeud);
				noeud.setUserObject(children[i]);
				if(children[i].isDirectory()){
					addChildren(noeud,showFiles,childrenFollow);
				}
			}
		}
	}
	private void validerDossier(){
		
		JDialog chooseFolders=new JDialog(CheesyKM.api);
		chooseFolders.getContentPane().setLayout(new BorderLayout());
		class DummyButton extends JButton{
			JTree myTree;
			JButton toEnable;
			TopicSelectionTree topics;
			DummyButton(JTree myTree,JButton toEnable){
				super();
				this.myTree=myTree;
				this.toEnable=toEnable;
			}
			public void setEnabled(boolean b){
				if(this.myTree.getSelectionPath()!=null){
					if(b){
						((Node)this.myTree.getSelectionPath().getLastPathComponent()).topics=topics.getSelectedTopics();
					} else {
						((Node)this.myTree.getSelectionPath().getLastPathComponent()).topics.removeAllElements();
					}
					this.myTree.repaint();
					this.toEnable.setEnabled(b);
				}
			}
		}
		JTree folders=createFolderTree(false,true);
		JButton next=new JButton(CheesyKM.getLabel("nextTopic"));
		next.setEnabled(tst.getSelectedTopics().size()>0);
		DummyButton db=new DummyButton(folders,next);
		TopicSelectionTree topics;
		if(this.createTopics.isSelected()){
			topics=new TopicSelectionTree(tst.getSelectedTopics(),false,db,Topic.RIGHT_RWM);
		} else {
			topics=new TopicSelectionTree(tst.getSelectedTopics(),false,db,Topic.RIGHT_RW);
		}
		db.topics=topics;
		
		class TopicsTreeSelectionListener implements TreeSelectionListener{
			TopicSelectionTree topics;
			Node lastSelected;
			JButton next;
			TopicsTreeSelectionListener(TopicSelectionTree topics,JButton next){
				this.topics=topics;
				lastSelected=null;
				this.next=next;
			}
			public void valueChanged(TreeSelectionEvent e){
				if(e.getPath().getPathCount()==1){
					
				} else {
					if(lastSelected!=null)lastSelected.setTopics(topics.getSelectedTopics());
					//CheesyKM.echo("LASTSELECTED:"+lastSelected);
					Node node=(Node)e.getPath().getLastPathComponent();
					//topics.setSelectedTopics(node.topics);
					lastSelected=node;
					//CheesyKM.echo("NOWSELECTED:"+lastSelected);
					((JTree)e.getSource()).repaint();
					next.setEnabled(topics.getSelectedTopics().size()>0);
				}
			}
		}
		folders.addTreeSelectionListener(new TopicsTreeSelectionListener(topics,next));
		
		JPanel bas=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		next.setIcon(CheesyKM.loadIcon("./ressources/VCRForward.gif"));
		JButton previous=new JButton(CheesyKM.getLabel("previousTopic"));
		previous.setIcon(CheesyKM.loadIcon("./ressources/VCRBack.gif"));
		JButton valider=new JButton(CheesyKM.getLabel("okidoki"));
		valider.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		
		class NextButtonListener implements ActionListener{
			Vector history;
			TopicSelectionTree topics;
			JTree folders;
			JButton previous;
			NextButtonListener(Vector history,TopicSelectionTree topics,JTree folders,JButton previous){
				this.history=history;
				this.topics=topics;
				this.folders=folders;
				this.previous=previous;
			}
			
			public void actionPerformed(ActionEvent e){
				Node node=(Node)this.folders.getSelectionPath().getLastPathComponent();
				//this.history.add(new AddedTopic(node,topics.getSelectedTopics(),(Node)node.getParent()));
				Node parentNode=(Node)node.getParent();
				if(node.equals(folders.getModel().getRoot())){
					//can't move root
				} else {
					((DefaultTreeModel)folders.getModel()).removeNodeFromParent(node);
					folders.repaint();
					//CheesyKM.echo("REMOVEDNODE:"+node);
					
					Vector childenInTopics=new Vector();
					Vector parentsInTopics=new Vector();
					for(int i=0;i<node.topics.size();i++){
						SelectionTopic sel=createTopicsForFolder(node,topics);
						
						//CheesyKM.echo("NODE:"+node+" TOPICS:"+node.topics);
						((DefaultTreeModel)topics.getModel()).insertNodeInto(sel.getNode(),(DefaultMutableTreeNode)topics.topics.get(new Integer(node.topics.get(i).toString().substring(1))),0);
						childenInTopics.add(sel.getNode());
						parentsInTopics.add(sel.getNode().getParent());
						topics.expandPathToTopic("T"+sel.id);
					}
					((DefaultTreeModel)topics.getModel()).reload();
					topics.repaint();
					topics.setSelectedTopics(tst.getSelectedTopics());
					AddedTopic at=new AddedTopic(node,childenInTopics,parentNode,parentsInTopics);
					node.at=at;
					this.history.add(at);
					previous.setEnabled(true);
					((JButton)e.getSource()).setEnabled(topics.getSelectedTopics().size()>0&&folders.getSelectionCount()>0);
				}
			}
		}
		class PreviousButtonListener implements ActionListener{
			Vector history;
			TopicSelectionTree topics;
			JTree folders;
			PreviousButtonListener(Vector history,TopicSelectionTree topics,JTree folders){
				this.history=history;
				this.topics=topics;
				this.folders=folders;
			}
			
			public void actionPerformed(ActionEvent e){
				//CheesyKM.echo("HIST:"+history);
				AddedTopic at=(AddedTopic)history.lastElement();
				history.remove(at);
				at.n.setTopics(tst.getSelectedTopics());
				//CheesyKM.echo("INSERTNODE:"+at.n+" INTO:"+at.parentInFolder);
				((DefaultTreeModel)folders.getModel()).insertNodeInto(at.n,at.parentInFolder,0);
				((DefaultTreeModel)folders.getModel()).reload();
				folders.repaint();
				((JButton)e.getSource()).setEnabled(history.size()>0);
				for(int i=0;i<at.childrenInTopics.size();i++){
					nodesByNegTids.remove(new Integer(((SelectionTopic)((DefaultMutableTreeNode)at.childrenInTopics.get(i)).getUserObject()).id));
					((DefaultTreeModel)topics.getModel()).removeNodeFromParent((DefaultMutableTreeNode)at.childrenInTopics.get(i));
				}
				topics.repaint();
			}
		}
		class OkiButtonListener implements ActionListener{
			Vector hist;
			JTree folders;
			JDialog d;
			OkiButtonListener(Vector hist,JTree folders,JDialog d){
				this.hist=hist;
				this.folders=folders;
				this.d=d;
			}
			public void actionPerformed(ActionEvent e){
				if(hist.size()==0){
					JOptionPane.showMessageDialog(null,CheesyKM.getLabel("noFoldersSet"),CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
				} else {
					if(((Node)folders.getModel().getRoot()).getChildren().size()>0){
						if(JOptionPane.showConfirmDialog(CheesyKM.api,CheesyKM.getLabel("someFoldersAreNotSet"), CheesyKM.getLabel("confirm"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
							execute(hist);
						}
					} else {
						d.dispose();
						ImportFiles.this.execute(hist);
					}
				}
			}
		}
		
		Vector history=new Vector();
		next.addActionListener(new NextButtonListener(history,topics,folders,previous));
		previous.addActionListener(new PreviousButtonListener(history,topics,folders));
		previous.setEnabled(false);
		valider.addActionListener(new OkiButtonListener(history,folders,chooseFolders));
		bas.add(previous);
		bas.add(next);
		bas.add(valider);
		JButton annuler=new JButton(CheesyKM.getLabel("cancel"));
		annuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		class ButtonAnnulerListener implements ActionListener{
			JDialog d;
			ButtonAnnulerListener(JDialog d){
				this.d=d;
			}
			public void actionPerformed(ActionEvent e){
				d.dispose();
			}
		}
		annuler.addActionListener(new ButtonAnnulerListener(chooseFolders));
		bas.add(annuler);
		if((this.createTopicsInOneTopicN.isSelected()&&this.createTopics.isSelected())||(this.registerInOneTopicN.isSelected()&&this.createTopicsN.isSelected())){
			chooseFolders.getContentPane().add(bas,"South");
			JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(folders),new JScrollPane(topics));
			
			jsp.setDividerSize(5);
			chooseFolders.getContentPane().add(jsp,"Center");
			chooseFolders.pack();
			chooseFolders.setModal(true);
			chooseFolders.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-chooseFolders.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-chooseFolders.getHeight())/2),chooseFolders.getWidth(),chooseFolders.getHeight());
			jsp.setDividerLocation((double)0.5);
			chooseFolders.show();
			
		} else {
			Vector hist=new Vector();
			Vector childrenInTopics=new Vector();
			Vector parentsInTopics=new Vector();
			Node node=(Node)((Node)folders.getModel().getRoot()).getChildren().get(0);
			for(int i=0;i<tst.getSelectedTopics().size();i++){
				SelectionTopic sel=createTopicsForFolder(node,topics);
				//CheesyKM.echo("NODE:"+node+" TOPICS:"+node.topics);
				((DefaultTreeModel)topics.getModel()).insertNodeInto(sel.getNode(),(DefaultMutableTreeNode)topics.topics.get(new Integer(node.topics.get(i).toString().substring(1))),0);
				childrenInTopics.add(sel.getNode());
				parentsInTopics.add(sel.getNode().getParent());
			}
			hist.add(new AddedTopic(node,childrenInTopics,((Node)folders.getModel().getRoot()),parentsInTopics));
			execute(hist);
		}
	}
	/**
	*Represents an assignation of a local folder (or its content) into one or more EasyKM Topic(s).
	*/
	class AddedTopic{
		/**The Node of the local folder.*/
		Node n;
		/**The parent node of the Node of the local folder.*/
		Node parentInFolder;
		/**The childrens of the Node in the Topics tree view, as String TopicIDs ("TXX").*/
		Vector childrenInTopics;
		/**The parents Topics in wich this folder has been assigned, as String TopicIDs ("TXX").*/
		Vector parentsInTopics;
		/**
		*New assignation.
		*@param n The Node of the local folder.
		*@param childrenInTopics The childrens of the Node in the Topics tree view, as String TopicIDs ("TXX").
		*@param parentInFolder The parent node of the Node of the local folder.
		*@param parentsInTopics The parents Topics in wich this folder has been assigned, as String TopicIDs ("TXX").
		*/
		AddedTopic(Node n,Vector childrenInTopics,Node parentInFolder,Vector parentsInTopics){
			this.n=n;
			this.childrenInTopics=childrenInTopics;
			this.parentInFolder=parentInFolder;
			this.parentsInTopics=parentsInTopics;
		}
		/**
		*@return a String representation of this assignation, for debug purposes.
		*/
		public String toString(){
			return "NODE:"+n+" PARENTINFOLD:"+parentInFolder+" CHILDINTOPIC:"+childrenInTopics+" PARENTINTOPIC:"+parentsInTopics;
		}
	}
	
	private SelectionTopic createTopicsForFolder(Node n,TopicSelectionTree topics){
		SelectionTopic root;
		if(ImportFiles.this.createTopics.isSelected()){
			root=new SelectionTopic(true,n.toString());
		} else {
			root=new SelectionTopic(true,n.toString(),-5);
		}
		root.fileForNewTopic=n.f;
		root.nodeForNewTopic=n;
		nodesByNegTids.put(new Integer(root.id),n);
		topics.topics.put(new Integer(root.id),root.getNode());
		if(n.getChildren()!=null&&ImportFiles.this.createTopics.isSelected()){
			for(int i=0;i<n.getChildren().size();i++){
				root.getNode().add(createTopicsForFolder((Node)n.getChildren().get(i),topics).getNode());
			}
		}
		return root;
	}
	
	private void execute(Vector history){
		Vector docs=new Vector();
		
		class Runner extends Thread{
			Vector docs,history;
			ProgBarDialog pbd;
			Runner(Vector docs,Vector history,ProgBarDialog pbd){
				this.docs=docs;
				this.history=history;
				this.pbd=pbd;
				this.start();
			}
			
			public void run(){
				for(int i=0;i<history.size();i++){
					AddedTopic at=(AddedTopic)history.get(i);
					for(int j=0;j<at.parentsInTopics.size();j++){
						SelectionTopic st=(SelectionTopic)((DefaultMutableTreeNode)at.parentsInTopics.get(j)).getUserObject();
						//CheesyKM.echo("ST:"+st);
						Node n=at.n;
						//CheesyKM.echo("NODE:"+n);
						if(st.id<0){
							addDocsFromFile(docs,n,"T"+st.nodeForNewTopic.realTid);
						} else {
							addDocsFromFile(docs,n,"T"+st.id);
						}
					}
				}
				//CheesyKM.echo("DOCS:"+docs);
				pbd.dispose();
			}
		}
		
		ProgBarDialog pbd=new ProgBarDialog(CheesyKM.api,CheesyKM.getLabel("creatingTopics"),true);
		
		new Runner(docs,history,pbd);
		pbd.show();
		
		
		registerDocs(docs,CheesyKM.getLabel("successfullyCreatedTopics"));
	}
	
	private void addDocsFromFile(Vector docs,Node n,String topic){
		File f=n.f;
		//CheesyKM.echo("FILE:"+f+"|CHILDRENNODE:"+n.getChildren());
		File[] files=f.listFiles();
		String newTopic;
		if(this.createTopics.isSelected()){
			newTopic=CheesyKM.createTopic(topic,f.getName());
			n.realTid=newTopic;
		} else {
			newTopic=topic;
		}
		if(files!=null){
			for(int i=0;i<files.length;i++){
				File file=files[i];
				if(file.isDirectory()){
					//
				} else {
					//CheesyKM.echo("add doc:"+file+" in topic:"+newTopic);
					if(docsByFile.get(file)==null){
						Doc doc=new Doc(file,newTopic);
						docsByFile.put(file,doc);
						docs.add(doc);
					} else {
						((Doc)docsByFile.get(file)).topicList.add(newTopic);
					}
				}
			}
		}
		if(n.getChildren()!=null){
			for(int i=0;i<n.getChildren().size();i++){
				addDocsFromFile(docs,(Node)n.getChildren().get(i),newTopic);
			}
		}
	}
	
	private void registerDocs(Vector docs,String foldersLog){
		JDialog log=new JDialog(CheesyKM.api);
		log.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		log.getContentPane().setLayout(new BorderLayout());
		log.setTitle(CheesyKM.getLabel("progress"));
		textLog=new TextArea("",20,100,TextArea.SCROLLBARS_VERTICAL_ONLY);
		textLog.setFont(new Font("Lucida Sans Regular",Font.BOLD,10));
		JPanel sud=new JPanel(new BorderLayout());
		jpb=new JProgressBar(0,docs.size());
		jpb.setString("");
		jpb.setStringPainted(true);
		sud.add(jpb,"Center");
		annuler=new JButton(CheesyKM.getLabel("cancel"));
		annuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		class AnnulerListener implements ActionListener{
			JDialog log;
			AnnulerListener(JDialog log){
				this.log=log;
			}
			public void actionPerformed(ActionEvent e){
				if(!ImportFiles.this.batchFinished){
					if(JOptionPane.showConfirmDialog(CheesyKM.api,CheesyKM.getLabel("stopBatch"), CheesyKM.getLabel("confirm"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
						RegisterDocWizard.batchRunner.kill();
						ImportFiles.this.endOfBatch();
					}
				} else {
					log.dispose();
					CheesyKM.api.deconnecter(true);
				}
			}
		}
		annuler.addActionListener(new AnnulerListener(log));
		sud.add(annuler,"East");
		log.getContentPane().add(sud,"South");
		textLog.setEditable(false);
		log.getContentPane().add(new JScrollPane(textLog),"Center");
		log.setModal(false);
		log.pack();
		log.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-log.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-log.getHeight())/2),log.getWidth(),log.getHeight());
		log.show();
		textLog.append(foldersLog+"\n");
		RegisterDocWizard.batchRunner=new BatchRunner();
		RegisterDocWizard.batchRunner.setActive();
		RegisterDocWizard.batchRunner.setLog(this,jpb);
		for(int i=0;i<docs.size();i++){
			if(this.confirm.isSelected()){
				new RegisterDocWizard(true,true,(Doc)docs.get(i));
			} else {
				new RegisterDocWizard(false,true,(Doc)docs.get(i)).registerDoc(false);
			}
		}
		RegisterDocWizard.batchRunner.setInactive();
	}
	
	void echo(final String s){
		textLog.append(s);
		textLog.append("\n");
	}
	
	void endOfBatch(){
		batchFinished=true;
		jpb.setValue(jpb.getMaximum());
		jpb.setString(CheesyKM.getLabel("finished"));
		this.annuler.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		this.annuler.setText(CheesyKM.getLabel("okidoki"));
	}
	
	
}


