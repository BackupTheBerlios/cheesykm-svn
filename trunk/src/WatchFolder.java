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
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
*Thread for the auto-import watching folder content feature.<br>
*When something is added into {@link CheesyKM#WATCHEDFOLDER}, CheesyKM proposes to import these files, with various options, including a silent auto-import.
*/
public class WatchFolder extends Thread{
	/**Content of the watched folder 1 (one) second ago*/
	Vector lastFiles=new Vector();
	/**Should this feature run ?*/
	boolean shouldRun=true;
	/**Is the silent auto-import activated ?*/
	boolean autoImport=false;
	private JCheckBox autoAdd,showDialogOnImport;
	private Vector topics=null;
	/**
	*Default constructor, does not start() this Thread.
	*/
	WatchFolder(){
	}
	/**
	*Scans {@link CheesyKM#WATCHEDFOLDER} every second and checks it for content changes.
	*/
	public void run(){
		shouldRun=true;
		while(CheesyKM.USEFOLDERWATCHING&&shouldRun&&CheesyKM.maximumRightLevel>=Topic.RIGHT_RW){
			try{sleep(1000);}catch(InterruptedException ie){CheesyKM.echo(ie);}
			File folder=new File(CheesyKM.WATCHEDFOLDER);
			File[] files=folder.listFiles();
			Vector actualFiles=new Vector();
			for(int i=0;i<files.length;i++){
				if(files[i].getAbsolutePath().indexOf(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported")==-1&&files[i].getAbsolutePath().indexOf(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported")==-1)
				actualFiles.add(files[i]);
			}
			if(lastFiles.size()!=actualFiles.size()||!lastFiles.containsAll(actualFiles)){
				//CheesyKM.echo("FOLDER CHANGED");
				if(actualFiles.size()!=0)folderChanged();
			}
			lastFiles=actualFiles;
		}
	}
	/**
	*Stop this Thread.
	*/
	public void kill(){
		shouldRun=false;
	}
	
	private void folderChanged(){
		if(!this.autoImport){
			if(JOptionPane.showConfirmDialog(CheesyKM.api,CheesyKM.getLabel("doYouWantToImportFilesAddedTo")+" "+CheesyKM.WATCHEDFOLDER, CheesyKM.getLabel("autoImport"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
				JDialog optionsDialog=new JDialog(CheesyKM.api);
				optionsDialog.setModal(true);
				optionsDialog.setResizable(false);
				optionsDialog.setTitle(CheesyKM.getLabel("autoImportOptions"));
				JPanel options=new JPanel();
				options.setLayout(new BoxLayout(options,BoxLayout.Y_AXIS));
				JPanel autoAddP=new JPanel(new FlowLayout(FlowLayout.LEFT));
				autoAdd=new JCheckBox(CheesyKM.getLabel("doNotShowAnymore"));
				if(CheesyKM.SHOWTOOLTIPS)autoAdd.setToolTipText(CheesyKM.getLabel("toolTipDoNotShow"));
				autoAddP.add(autoAdd);
				JPanel showDialogOnImportP=new JPanel(new FlowLayout(FlowLayout.LEFT));
				showDialogOnImport=new JCheckBox(CheesyKM.getLabel("showDialogOnImport"));
				if(CheesyKM.SHOWTOOLTIPS)showDialogOnImport.setToolTipText(CheesyKM.getLabel("toolTipShowDialogOnImport"));
				showDialogOnImport.setSelected(true);
				showDialogOnImportP.add(showDialogOnImport);
				JPanel labelP=new JPanel(new FlowLayout(FlowLayout.CENTER));
				labelP.add(new JLabel(CheesyKM.getLabel("optionsForThisSessionOnly")));
				options.add(labelP);
				options.add(autoAddP);
				options.add(showDialogOnImportP);
				JPanel bas=new JPanel(new FlowLayout(FlowLayout.RIGHT));
				JButton oki=new JButton(CheesyKM.getLabel("okidoki"));
				oki.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
				JButton annuler=new JButton(CheesyKM.getLabel("cancel"));
				annuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
				class AnnulerListener implements ActionListener{
					JDialog toDispose;
					AnnulerListener(JDialog toDispose){
						this.toDispose=toDispose;
					}
					public void actionPerformed(ActionEvent ae){
						toDispose.dispose();
					}
				}
				annuler.addActionListener(new AnnulerListener(optionsDialog));
				class OkiListener implements ActionListener{
					JDialog toDispose;
					OkiListener(JDialog toDispose){
						this.toDispose=toDispose;
					}
					public void actionPerformed(ActionEvent ae){
						toDispose.dispose();
						WatchFolder.this.autoImport=WatchFolder.this.autoAdd.isSelected();
						WatchFolder.this.makeImport();
					}
				}
				oki.addActionListener(new OkiListener(optionsDialog));
				bas.add(oki);
				bas.add(annuler);
				options.add(bas);
				optionsDialog.getContentPane().add(options,"Center");
				optionsDialog.pack();
				optionsDialog.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-optionsDialog.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-optionsDialog.getHeight())/2),optionsDialog.getWidth(),optionsDialog.getHeight());
				optionsDialog.show();
			}
		} else {
			makeImport();
		}
	}
	
	private void makeImport(){
		if(topics==null&&!this.showDialogOnImport.isSelected()){
			JDialog selTopics=new JDialog(CheesyKM.api);
			JButton oki=new JButton(CheesyKM.getLabel("okidoki"));
			TopicSelectionTree tst=new TopicSelectionTree(new Vector(),false,oki,Topic.RIGHT_RW);
			oki.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
			JButton annuler=new JButton(CheesyKM.getLabel("cancel"));
			annuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
			JPanel bas=new JPanel(new FlowLayout(FlowLayout.RIGHT));
			class AnnulerListener implements ActionListener{
				JDialog toDispose;
				AnnulerListener(JDialog toDispose){
					this.toDispose=toDispose;
				}
				public void actionPerformed(ActionEvent ae){
					toDispose.dispose();
				}
			}
			annuler.addActionListener(new AnnulerListener(selTopics));
			class OkiListener implements ActionListener{
				JDialog toDispose;
				TopicSelectionTree tst;
				OkiListener(JDialog toDispose,TopicSelectionTree tst){
					this.toDispose=toDispose;
					this.tst=tst;
				}
				public void actionPerformed(ActionEvent ae){
					WatchFolder.this.topics=tst.getSelectedTopics();
					toDispose.dispose();
				}
			}
			oki.addActionListener(new OkiListener(selTopics,tst));
			bas.add(oki);
			oki.setSelected(false);
			bas.add(annuler);
			JScrollPane tstP=new JScrollPane(tst);
			tstP.setPreferredSize(new Dimension(300,300));
			selTopics.getContentPane().add(tstP,"Center");
			selTopics.getContentPane().add(bas,"South");
			selTopics.setTitle(CheesyKM.getLabel("autoImportOptions"));
			selTopics.getContentPane().add(new JLabel(CheesyKM.getLabel("selectTopics")),"North");
			selTopics.setModal(true);
			selTopics.setResizable(false);
			selTopics.pack();
			selTopics.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-selTopics.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-selTopics.getHeight())/2),selTopics.getWidth(),selTopics.getHeight());
			selTopics.setVisible(true);
		}
		if(!this.showDialogOnImport.isSelected()&&this.topics==null){
			
		} else {
			//CheesyKM.echo("MAKE IMPORT");
			File folder=new File(CheesyKM.WATCHEDFOLDER);
			File[] files=folder.listFiles();
			if(!new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported").exists())new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported").mkdirs();
			if(!new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported").exists())new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported").mkdirs();
			for(int i=0;i<files.length;i++){
				if(files[i].getAbsolutePath().indexOf(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported")==-1&&files[i].getAbsolutePath().indexOf(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported")==-1){
					File file=files[i];
					//CheesyKM.echo("start of import:"+file);
					if(file.isFile()){
						RegisterDocWizard rdw=null;
						if(this.showDialogOnImport.isSelected()){
							rdw=new RegisterDocWizard(file,topics,true);
						} else {
							rdw=new RegisterDocWizard(file,topics,false);
							rdw.registerDoc(false);
						}
						if(rdw.ok){
							File newLocation=new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported"+System.getProperty("file.separator")+file.getName());
							file.renameTo(newLocation);
						} else {
							File newLocation=new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported"+System.getProperty("file.separator")+file.getName());
							file.renameTo(newLocation);
						}
					} else if(file.isDirectory()){
						new ImportFiles(file.getAbsolutePath());
						File newLocation=new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported"+System.getProperty("file.separator")+file.getName());
						file.renameTo(newLocation);
					}
				}
			}
		}
	}
	
	/**
	*Deletes a folder and its whole content.
	*@param folder the folder to delete.
	*/
	public void deleteFolder(File folder){
		File[] sub=folder.listFiles();
		for(int i=0;i<sub.length;i++){
			if(sub[i].isDirectory()){
				deleteFolder(sub[i]);
			} else {
				sub[i].delete();
			}
		}
		folder.delete();
	}
}
