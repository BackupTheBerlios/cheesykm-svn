import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class WatchFolder extends Thread{
	Vector lastFiles=new Vector();
	boolean shouldRun=true;
	boolean autoImport=false;
	JCheckBox autoAdd,showDialogOnImport;
	Vector topics=null;
	WatchFolder(){
	}
	
	public void run(){
		shouldRun=true;
		while(CheesyKM.USEFOLDERWATCHING&&shouldRun){
			try{sleep(1000);}catch(InterruptedException ie){CheesyKM.echo(ie);}
			File folder=new File(CheesyKM.WATCHEDFOLDER);
			File[] files=folder.listFiles();
			Vector actualFiles=new Vector();
			for(int i=0;i<files.length;i++){
				actualFiles.add(files[i]);
			}
			if(lastFiles.size()!=actualFiles.size()||!lastFiles.containsAll(actualFiles)){
				//CheesyKM.echo("FOLDER CHANGED");
				if(actualFiles.size()!=0)folderChanged();
			}
			lastFiles=actualFiles;
		}
	}
	
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
				autoAdd.setToolTipText(CheesyKM.getLabel("toolTipDoNotShow"));
				autoAddP.add(autoAdd);
				JPanel showDialogOnImportP=new JPanel(new FlowLayout(FlowLayout.LEFT));
				showDialogOnImport=new JCheckBox(CheesyKM.getLabel("showDialogOnImport"));
				showDialogOnImport.setToolTipText(CheesyKM.getLabel("toolTipShowDialogOnImport"));
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
	
	void makeImport(){
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
			bas.add(annuler);
			JScrollPane tstP=new JScrollPane(tst);
			tstP.setPreferredSize(new Dimension(300,300));
			selTopics.getContentPane().add(tstP,"Center");
			selTopics.getContentPane().add(bas,"South");
			selTopics.setTitle(CheesyKM.getLabel("selectTopics"));
			selTopics.setModal(true);
			selTopics.setResizable(false);
			selTopics.pack();
			selTopics.setBounds(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-selTopics.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-selTopics.getHeight())/2),selTopics.getWidth(),selTopics.getHeight());
			selTopics.setVisible(true);
		}
		if(!this.showDialogOnImport.isSelected()&&this.topics==null){
			
		} else {
			CheesyKM.echo("MAKE IMPORT");
			File folder=new File(CheesyKM.WATCHEDFOLDER);
			File[] files=folder.listFiles();
			for(int i=0;i<files.length;i++){
				File file=files[i];
				CheesyKM.echo("start of import:"+file);
				if(file.isFile()){
					if(this.showDialogOnImport.isSelected()){
						new RegisterDocWizard(file,topics,true);
					} else {
						new RegisterDocWizard(file,topics,false).registerDoc(false);
					}
					file.delete();
				} else if(file.isDirectory()){
					new ImportFiles(file.getAbsolutePath());
					deleteFolder(file);
				}
				
			}
		}
	}
	
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
