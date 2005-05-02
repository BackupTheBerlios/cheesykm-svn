import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
/**
*Popup menu (right click) over a Topic. used on news list, topic tree view, search resutl tree and right tabbed pane.
*/
class TopicPopupMenu extends JPopupMenu{
	
	/**
	*Pops a new menu under the mouse.
	*@param invoker Source component.
	*@param x position of the mouse relative to <code>invoker</code>
	*@param y position of the mouse relative to <code>invoker</code>
	*@param ntopic {@link Topic} under the mouse.
	*@param overTree <code>true</code> if the mouse is over a tree, <code>false</code> else.
	*/
	TopicPopupMenu(Component invoker,int x,int y,Topic ntopic,boolean overTree){
		this(invoker,x,y,ntopic,overTree,false);
	}
	
	/**
	*Pops a new menu under the mouse.
	*@param invoker Source component.
	*@param x position of the mouse relative to <code>invoker</code>
	*@param y position of the mouse relative to <code>invoker</code>
	*@param ntopic {@link Topic} under the mouse.
	*@param overTree <code>true</code> if the mouse is over a tree, <code>false</code> else.
	*@param overNouveaute <code>true</code> if the mouse is over the news list, <code>false</code> else.
	*/
	TopicPopupMenu(Component invoker,int x,int y,Topic ntopic,boolean overTree,boolean overNouveaute){
		super(ntopic.toString());
		final Topic topic=ntopic;
		if(topic.getNodeType()=='D'){
			
			if(overTree){
				class TopicPopupAfficherListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.api.displayTopic(topic);
					}
				}
				JMenuItem afficher=add(CheesyKM.getLabel("displayDocumentTab"));
				afficher.addActionListener(new TopicPopupAfficherListener());
			}
			
			if(CheesyKM.api.isAffiche(topic)!=-1||!overTree){
				class TopicPopupFermerListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.api.hideTopic(topic);
					}
				}
				JMenuItem fermer=add(CheesyKM.getLabel("hideDocumentTab"));
				fermer.addActionListener(new TopicPopupFermerListener());
			}
			
			if(CheesyKM.easyKMConfig.get("eval").equals(new Integer(1))){
				this.addSeparator();
				class TopicPopupRateDoc implements ActionListener{
					public void actionPerformed(ActionEvent e){
						new RateDocDialog((Doc)topic);
					}
				}
				JMenuItem rate=add(CheesyKM.getLabel("rateDoc"));
				rate.addActionListener(new TopicPopupRateDoc());
			}
			
			if(!((Doc)topic).url.equals("")){
				this.addSeparator();
				
				class TopicPopupURLListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.openURL(((Doc)topic));
					}
				}
				JMenuItem url=add(CheesyKM.getLabel("browse")+" ["+((Doc)topic).url+"]");
				url.addActionListener(new TopicPopupURLListener());
				
				class TopicPopupURLCopyListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.copyToClipBoard(((Doc)topic).url);
					}
				}
				JMenuItem urlCopy=add(CheesyKM.getLabel("copySiteURL"));
				urlCopy.addActionListener(new TopicPopupURLCopyListener());
			}
			
			if(!((Doc)topic).file.equals("")){
				this.addSeparator();
				class TopicPopupFileDLListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.download((Doc)topic,true);
					}
				}
				JMenuItem fileDL=add(CheesyKM.getLabel("download")+" ("+((Doc)topic).format+"-"+((Doc)topic).getFSize()+")");
				fileDL.addActionListener(new TopicPopupFileDLListener());
				
				class TopicPopupFileViewListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.download((Doc)topic,false);
					}
				}
				JMenuItem fileView=add(CheesyKM.getLabel("display")+" ("+((Doc)topic).format+"-"+((Doc)topic).getFSize()+")");
				fileView.addActionListener(new TopicPopupFileViewListener());
			}
			
			if(overNouveaute){
				if(((Doc)topic).getParent().rights>=Topic.RIGHT_RWM||((Doc)topic).isOwner()){
					this.addSeparator();
					class TopicPopupUpdateDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							new RegisterDocWizard((Doc)topic);
						}
					}
					JMenuItem updateDoc=add(CheesyKM.getLabel("updateThisDoc"));
					updateDoc.addActionListener(new TopicPopupUpdateDocListener());
				
					class TopicPopupEditDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							new RegisterDocWizard((Doc)topic,true);
						}
					}
					JMenuItem editDoc=add(CheesyKM.getLabel("editDocument"));
					editDoc.addActionListener(new TopicPopupEditDocListener());
				
				
					class TopicPopupDeleteDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							CheesyKM.deleteDoc((Doc)topic);
						}
					}
					JMenuItem deleteDoc=add(CheesyKM.getLabel("deleteDocument"));
					deleteDoc.addActionListener(new TopicPopupDeleteDocListener());
				}
			} else {
				if(((Doc)topic).isOwner()){
					this.addSeparator();
					class TopicPopupUpdateDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							new RegisterDocWizard((Doc)topic);
						}
					}
					JMenuItem updateDoc=add(CheesyKM.getLabel("updateThisDoc"));
					updateDoc.addActionListener(new TopicPopupUpdateDocListener());
				
					class TopicPopupEditDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							new RegisterDocWizard((Doc)topic,true);
						}
					}
					JMenuItem editDoc=add(CheesyKM.getLabel("editDocument"));
					editDoc.addActionListener(new TopicPopupEditDocListener());
				
					class TopicPopupDeleteDocListener implements ActionListener{
						public void actionPerformed(ActionEvent e){
							CheesyKM.deleteDoc((Doc)topic);
						}
					}
					JMenuItem deleteDoc=add(CheesyKM.getLabel("deleteDocument"));
					deleteDoc.addActionListener(new TopicPopupDeleteDocListener());
				}
			}
			
		} else if(topic.getNodeType()=='W'||topic.getNodeType()=='A'){
			class TopicPopupFermerListener implements ActionListener{
				public void actionPerformed(ActionEvent e){
					CheesyKM.api.hideTopic(CheesyKM.api.jtpD.getSelectedIndex());
				}
			}
			JMenuItem fermer=add(CheesyKM.getLabel("closeTab"));
			fermer.addActionListener(new TopicPopupFermerListener());
		} else if(topic.getNodeType()=='F'){
			FileTransferTopic ftt=(FileTransferTopic)topic;
			if(ftt.pi.isRunning){
				class TopicPopupAnnulerListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						((FileTransferTopic)((TopicPane)CheesyKM.api.jtpD.getSelectedComponent()).topic).pi.d.stop=true;
					}
				}
				JMenuItem annuler=add(CheesyKM.getLabel("cancelDownload"));
				annuler.addActionListener(new TopicPopupAnnulerListener());	
			} else {
				class TopicPopupFermerListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						CheesyKM.api.hideTopic(CheesyKM.api.jtpD.getSelectedIndex());
					}
				}
				JMenuItem fermer=add(CheesyKM.getLabel("closeTab"));
				fermer.addActionListener(new TopicPopupFermerListener());
			}
		} else if(topic.getNodeType()=='T'||topic.getNodeType()=='P'){
			
			if(topic.rights>=Topic.RIGHT_RW){
				class TopicPopupDeposerDocIciListener implements ActionListener{
					public void actionPerformed(ActionEvent e){
						Vector tids=new Vector();
						tids.add(new Integer(topic.id));
						new RegisterDocWizard(null,tids,false);
					}
				}
				JMenuItem deposerDocIci=add(CheesyKM.getLabel("registerADocHere"));
				deposerDocIci.addActionListener(new TopicPopupDeposerDocIciListener());
			}
			
		}
		
		//CheesyKM.echo("Tabruncount:"+CheesyKM.api.jtpD.getTabRunCount());
		show(invoker,x,y);
	}
}
