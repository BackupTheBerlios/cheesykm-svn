import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

class TopicPopupMenu extends JPopupMenu{
	
	TopicPopupMenu(Component invoker,int x,int y,Topic ntopic,boolean overTree){
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
		} else if(topic.getNodeType()=='W'){
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
		}
		
		//CheesyKM.echo("Tabruncount:"+CheesyKM.api.jtpD.getTabRunCount());
		show(invoker,x,y);
	}
}
