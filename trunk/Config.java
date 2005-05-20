import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.secure.*;
import java.net.*;
/**
*Manages the configuration/options of CheesyKM.<br>
*Contains methods to write, read, and restore default configuration file.
*/
class Config{
	/**Path to the configuration file*/
	static final String nomFichierDeConf=System.getProperty("user.home")+System.getProperty("file.separator")+".CheesyKM.conf";
	InitConfigPanel icp;
	JButton bValider;
	/**
	*Read the configuration from the file and updates CheesyKMs constants.<br>
	*If there's a problem during that process, the default config is loaded, and the user is warned about it.
	*/
	public void loadConfig(){
		String ligne=new String();
		BufferedReader fe=null;
		try{
			fe=new BufferedReader(new FileReader(nomFichierDeConf));
			fe.readLine();
			CheesyKM.EASYKMROOT=fe.readLine();
			fe.readLine();
			CheesyKM.KEYSTOREPATH=fe.readLine();
			fe.readLine();
			CheesyKM.KEYSTOREPASS=fe.readLine();
			fe.readLine();
			CheesyKM.INITHEIGHT=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.INITWIDTH=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.INITX=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.INITY=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.REMEMBERLASTLOGIN=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.LASTLOGIN=fe.readLine();
			fe.readLine();
			CheesyKM.MAXDOCSINMEM=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.AUTOCOLLAPSE=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.MAXDOCTABSTITLESIZE=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.DEFAULTACTIONCLICKCOUNT=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.MULTIPLETABSDOCS=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.WEBBROWSERPATH=fe.readLine();
			fe.readLine();
			CheesyKM.USELOCALWEBBROWSER=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.USERLOCALWEBBROWSERTODLFILES=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.NOMBREDENOUVEAUTES=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.BUTTONTOOLBARLOCATION=fe.readLine();
			fe.readLine();
			CheesyKM.SEARCHTOOLBARLOCATION=fe.readLine();
			fe.readLine();
			CheesyKM.EXPANDSEARCHRESULT=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.FTPHOST=fe.readLine();
			fe.readLine();
			CheesyKM.FTPPASS=fe.readLine();
			fe.readLine();
			CheesyKM.FTPUSERNAME=fe.readLine();
			fe.readLine();
			CheesyKM.DEFAULTSEARCHFIELDNUMBER=Integer.parseInt(fe.readLine());
			fe.readLine();
			CheesyKM.USEJAVALAF=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.WATCHEDFOLDER=fe.readLine();
			fe.readLine();
			CheesyKM.USEFOLDERWATCHING=fe.readLine().equals("true");
			fe.readLine();
			CheesyKM.SHOWTOOLTIPS=fe.readLine().equals("true");
			fe.close();
			
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, CheesyKM.getLabel("loadingDefaultConfig"), CheesyKM.getLabel("information"), JOptionPane.INFORMATION_MESSAGE);
			this.loadDefaultConfig();
			this.saveConfig();
		}
	}
	/**
	*Writes the configuration file, reading CheesyKMs constants.
	*/
	public void saveConfig(){
		effacer(nomFichierDeConf);
		try{
			File f=new File(nomFichierDeConf);
			f.createNewFile();
			BufferedWriter bw=new BufferedWriter(new FileWriter(f,false));
			bw.write("[EasyKM root]");
			bw.newLine();
			bw.write(CheesyKM.EASYKMROOT);
			bw.newLine();
			bw.write("[Keystore file path]");
			bw.newLine();
			bw.write(CheesyKM.KEYSTOREPATH);
			bw.newLine();
			bw.write("[Keystore file pass]");
			bw.newLine();
			bw.write(CheesyKM.KEYSTOREPASS);
			bw.newLine();
			bw.write("[main frame height]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.INITHEIGHT).toString());
			bw.newLine();
			bw.write("[main frame width]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.INITWIDTH).toString());
			bw.newLine();
			bw.write("[x position of main frame]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.INITX).toString());
			bw.newLine();
			bw.write("[y position of main frame]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.INITY).toString());
			bw.newLine();
			bw.write("[remember last login ? (true/false)]");
			bw.newLine();
			if(CheesyKM.REMEMBERLASTLOGIN) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[last login]");
			bw.newLine();
			bw.write(CheesyKM.LASTLOGIN);
			bw.newLine();
			bw.write("[max docs in memory]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.MAXDOCSINMEM).toString());
			bw.newLine();
			bw.write("[autocollapse ? (true/false)]");
			bw.newLine();
			if(CheesyKM.AUTOCOLLAPSE) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[max docs in memory before autocollapse]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE).toString());
			bw.newLine();
			bw.write("[maximum size of tabs titles]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.MAXDOCTABSTITLESIZE).toString());
			bw.newLine();
			bw.write("[tree default action clickCount (1/2)]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.DEFAULTACTIONCLICKCOUNT).toString());
			bw.newLine();
			bw.write("[use several tabs to display docs ? (true/false)]");
			bw.newLine();
			if(CheesyKM.MULTIPLETABSDOCS) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[path to local web browser]");
			bw.newLine();
			bw.write(CheesyKM.WEBBROWSERPATH);
			bw.newLine();
			bw.write("[use local web browser to browse sites ? (true/false)]");
			bw.newLine();
			if(CheesyKM.USELOCALWEBBROWSER) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[use local web browser to view files ? (true/false)]");
			bw.newLine();
			if(CheesyKM.USERLOCALWEBBROWSERTODLFILES) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[number of news to display]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.NOMBREDENOUVEAUTES).toString());
			bw.newLine();
			bw.write("[toolbar position]");
			bw.newLine();
			bw.write(CheesyKM.BUTTONTOOLBARLOCATION);
			bw.newLine();
			bw.write("[search bar position]");
			bw.newLine();
			bw.write(CheesyKM.SEARCHTOOLBARLOCATION);
			bw.newLine();
			bw.write("[Expand search results by default ? (true/false)]");
			bw.newLine();
			if(CheesyKM.EXPANDSEARCHRESULT) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[FTP hostname]");
			bw.newLine();
			bw.write(CheesyKM.FTPHOST);
			bw.newLine();
			bw.write("[FTP password]");
			bw.newLine();
			bw.write(CheesyKM.FTPPASS);
			bw.newLine();
			bw.write("[FTP username]");
			bw.newLine();
			bw.write(CheesyKM.FTPUSERNAME);
			bw.newLine();
			bw.write("[default search fields number]");
			bw.newLine();
			bw.write(new Integer(CheesyKM.DEFAULTSEARCHFIELDNUMBER).toString());
			bw.newLine();
			bw.write("[Use JAVA Look and Feel ? (true/false)]");
			bw.newLine();
			if(CheesyKM.USEJAVALAF) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[Folder to watch for]");
			bw.newLine();
			bw.write(CheesyKM.WATCHEDFOLDER);
			bw.newLine();
			bw.write("[Use folder watching ? (true/false)]");
			bw.newLine();
			if(CheesyKM.USEFOLDERWATCHING) bw.write("true"); else bw.write("false");
			bw.newLine();
			bw.write("[Show tooltips ? (true/false)]");
			bw.newLine();
			if(CheesyKM.SHOWTOOLTIPS) bw.write("true"); else bw.write("false");
			bw.flush();
		} catch(Exception e){
			JOptionPane.showMessageDialog(null, CheesyKM.getLabel("errorSavingConfig"), CheesyKM.getLabel("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	*Shows the configuration/options dialog.<br>
	*This dialog appears in "non-advanced" mode, to switch to advanced mode, te user has to check a chekbox.
	*The values of the fields of this dialog are taken from CheesyKMs constants.
	*The dialogs buttons permit to save the config, revert it to default, or cancel the changes.
	*/
	public void displayConfig(){
		JDialog config=new JDialog(CheesyKM.api);

		
		config.setModal(true);
		config.setResizable(false);
		config.setTitle(CheesyKM.getLabel("configuration"));
		Container gc=config.getContentPane();
		gc.setLayout(new BorderLayout());
		
		
		
		
		JPanel sud=new JPanel();
		sud.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		
		JCheckBox bAvance=new JCheckBox(CheesyKM.getLabel("advanced"),false);
		class BAvanceActionListener implements ActionListener {
			JDialog d;
			BAvanceActionListener(JDialog d){
				this.d=d;
			
			}
			public void actionPerformed(ActionEvent e){
				boolean state=((JCheckBox)e.getSource()).isSelected();
				d.getContentPane().remove(1);
				Config.this.icp=new InitConfigPanel(state);
				d.getContentPane().add(icp,"Center");
				d.pack();
				d.setLocation(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-d.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-d.getHeight())/2));
				d.validate();
			}
		}
		bAvance.addActionListener(new BAvanceActionListener(config));
		sud.add(bAvance);
		
		
		
		bValider=new JButton(CheesyKM.getLabel("saveChanges"));
		bValider.setIcon(CheesyKM.loadIcon("./ressources/Check.gif"));
		class BValiderActionListener implements ActionListener {
			JDialog d;
			BValiderActionListener(JDialog d){
				this.d=d;
			}
			public void actionPerformed(ActionEvent e){
				if(Config.this.icp.validerConfig()){
					Config.this.saveConfig();
					if(CheesyKM.api.thematique!=null)CheesyKM.api.thematique.setToggleClickCount(CheesyKM.DEFAULTACTIONCLICKCOUNT);
					d.dispose();
				}
			}
		}
		bValider.addActionListener(new BValiderActionListener(config));
		sud.add(bValider);
		JButton bDefault=new JButton(CheesyKM.getLabel("default"));
		bDefault.setIcon(CheesyKM.loadIcon("./ressources/RotCWLeft.gif"));
		class BDefaultActionListener implements ActionListener {
			JDialog d;
			BDefaultActionListener(JDialog d){
				this.d=d;
			}
			public void actionPerformed(ActionEvent e){
				if(JOptionPane.showConfirmDialog(d,CheesyKM.getLabel("reallyUseDefault"), CheesyKM.getLabel("confirm"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					Config.loadDefaultConfig();
					boolean avance=((InitConfigPanel)d.getContentPane().getComponent(1)).avance;
					d.getContentPane().remove(1);
					Config.this.icp=new InitConfigPanel(true);
					d.getContentPane().add(icp,"Center");
					d.pack();
					d.setLocation(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-d.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-d.getHeight())/2));
					d.validate();
				} else {
					
				}
				
			}
		}
		bDefault.addActionListener(new BDefaultActionListener(config));
		sud.add(bDefault);
		//sud.add(new JLabel(" "));
		JButton bAnnuler=new JButton(CheesyKM.getLabel("cancel"));
		bAnnuler.setIcon(CheesyKM.loadIcon("./ressources/Delete20.gif"));
		class BAnnulerActionListener implements ActionListener {
			JDialog d;
			BAnnulerActionListener(JDialog d){
				this.d=d;
			}
			public void actionPerformed(ActionEvent e){
				d.dispose();
			}
		}
		bAnnuler.addActionListener(new BAnnulerActionListener(config));
		sud.add(bAnnuler);
		
		gc.add(sud,"South");
		icp=new InitConfigPanel(false);
		gc.add(icp,"Center");
		config.pack();
		config.setLocation(CheesyKM.api.getX()+((CheesyKM.api.getWidth()-config.getWidth())/2),CheesyKM.api.getY()+((CheesyKM.api.getHeight()-config.getHeight())/2));
		config.show();
	}
	
	/**
	*Main panel of the configuration dialog
	*/
	class InitConfigPanel extends EditableFieldGroup{
		boolean avance;
		IntegerValue nombreNouveautes;
		BooleanValue rememberLastLogin;
		BooleanValue useManyTabs;
		IntegerValue titlesSize;
		IntegerValue clics;
		StringValue easyKM;
		OpenFileNameValue ksPath;
		StringValue ksPass;
		IntegerValue docsInMem,defaultSearchFields;
		OpenFileNameValue browserPath;
		OpenFolderNameValue watchedFolder;
		BooleanValue useLocalBrowser;
		BooleanValue expandSearchResults,useJavaLaf,useFolderWatching,showToolTips;
		StringValue ftpHost,ftpPass,ftpUserName;
		/**
		*Creates a new ConfigPanel.<br>
		*The accessible application settings won't be the same if the advanced mode is switched.
		*@param avance <code>true</code> for advanced mode, <code>false</code> for basic mode.
		*/
		InitConfigPanel(boolean avance){
			super(Config.this.bValider);
			this.avance=avance;
			nombreNouveautes=new IntegerValue(CheesyKM.getLabel("howManyNews"),CheesyKM.NOMBREDENOUVEAUTES);
			addEditableField(nombreNouveautes,true);
			rememberLastLogin=new BooleanValue(CheesyKM.getLabel("rememberLastLogin"),CheesyKM.REMEMBERLASTLOGIN);
			addEditableField(rememberLastLogin);
			useManyTabs=new BooleanValue(CheesyKM.getLabel("useSeveralTabs"),CheesyKM.MULTIPLETABSDOCS);
			addEditableField(useManyTabs);
			titlesSize=new IntegerValue(CheesyKM.getLabel("tabsTitleLength"),CheesyKM.MAXDOCTABSTITLESIZE);
			addEditableField(titlesSize,true);
			clics=new IntegerValue(CheesyKM.getLabel("defaultActionClickCount"),CheesyKM.DEFAULTACTIONCLICKCOUNT);
			addEditableField(clics,true);
			expandSearchResults=new BooleanValue(CheesyKM.getLabel("expandSearchResult"),CheesyKM.EXPANDSEARCHRESULT);
			addEditableField(expandSearchResults);
			defaultSearchFields=new IntegerValue(CheesyKM.getLabel("defaultSearchFields"),CheesyKM.DEFAULTSEARCHFIELDNUMBER);
			addEditableField(defaultSearchFields,true);
			useJavaLaf=new BooleanValue(CheesyKM.getLabel("useJavaLaf"),CheesyKM.USEJAVALAF);
			addEditableField(useJavaLaf);
			useFolderWatching=new BooleanValue(CheesyKM.getLabel("useFolderWatching"),CheesyKM.USEFOLDERWATCHING);
			addEditableField(useFolderWatching);
			watchedFolder=new OpenFolderNameValue(CheesyKM.getLabel("watchedFolder"),CheesyKM.WATCHEDFOLDER);
			addEditableField(watchedFolder,true);
			showToolTips=new BooleanValue(CheesyKM.getLabel("showToolTips"),CheesyKM.SHOWTOOLTIPS);
			addEditableField(showToolTips);
			if(avance){
				easyKM=new StringValue(CheesyKM.getLabel("easyKMRoot"),CheesyKM.EASYKMROOT);
				addEditableField(easyKM,true);
				ksPath=new OpenFileNameValue(CheesyKM.getLabel("keystorePath"),CheesyKM.KEYSTOREPATH);
				addEditableField(ksPath,true);
				ksPass=new StringValue(CheesyKM.getLabel("keystorePass"),CheesyKM.KEYSTOREPASS);
				addEditableField(ksPass,true);
				docsInMem=new IntegerValue(CheesyKM.getLabel("maxDocsInMem"),CheesyKM.MAXDOCSINMEM);
				addEditableField(docsInMem,true);
				browserPath=new OpenFileNameValue(CheesyKM.getLabel("webBrowserPath"),CheesyKM.WEBBROWSERPATH);
				addEditableField(browserPath,true);
				useLocalBrowser=new BooleanValue(CheesyKM.getLabel("useLocalBrowserToBrowse"),CheesyKM.USELOCALWEBBROWSER);
				addEditableField(useLocalBrowser);
				ftpHost=new StringValue(CheesyKM.getLabel("FTPHost"),CheesyKM.FTPHOST);
				addEditableField(ftpHost,true);
				ftpPass=new StringValue(CheesyKM.getLabel("FTPPass"),CheesyKM.FTPPASS);
				addEditableField(ftpPass,true);
				ftpUserName=new StringValue(CheesyKM.getLabel("FTPUserName"),CheesyKM.FTPUSERNAME);
				addEditableField(ftpUserName,true);
			}
		}
		/**
		*Updates CheesyKMs constants if the configuration is valid.<br>
		*@return <code>true</code> if the config is valid, <code>false</code> else.
		*@see Config.InitConfigPanel#configIsValide()
		*/
		public boolean validerConfig(){
			if(configIsValide()){
				CheesyKM.NOMBREDENOUVEAUTES=Integer.parseInt(nombreNouveautes.tf.getText());
				CheesyKM.REMEMBERLASTLOGIN=rememberLastLogin.value.isSelected();
				CheesyKM.MULTIPLETABSDOCS=useManyTabs.value.isSelected();
				CheesyKM.USEJAVALAF=useJavaLaf.value.isSelected();
				CheesyKM.MAXDOCTABSTITLESIZE=Integer.parseInt(titlesSize.tf.getText());
				CheesyKM.DEFAULTACTIONCLICKCOUNT=Integer.parseInt(clics.tf.getText());
				CheesyKM.EXPANDSEARCHRESULT=expandSearchResults.value.isSelected();
				CheesyKM.DEFAULTSEARCHFIELDNUMBER=((Integer)defaultSearchFields.value()).intValue();
				CheesyKM.WATCHEDFOLDER=watchedFolder.fileName.getText();
				CheesyKM.USEFOLDERWATCHING=useFolderWatching.value.isSelected();
				CheesyKM.SHOWTOOLTIPS=showToolTips.value.isSelected();
				if(CheesyKM.USEFOLDERWATCHING){
					new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported").mkdir();
					new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported").mkdir();
				}
				if(CheesyKM.USEFOLDERWATCHING&&CheesyKM.login!=null){
					if(CheesyKM.watchFolder!=null)CheesyKM.watchFolder.kill();
					CheesyKM.watchFolder=new WatchFolder();
					CheesyKM.watchFolder.start();
				}
				if(this.avance){
					CheesyKM.EASYKMROOT=easyKM.tf.getText();
					CheesyKM.KEYSTOREPATH=ksPath.fileName.getText();
					CheesyKM.KEYSTOREPASS=ksPass.tf.getText();
					CheesyKM.MAXDOCSINMEM=Integer.parseInt(docsInMem.tf.getText());
					CheesyKM.WEBBROWSERPATH=browserPath.fileName.getText();
					CheesyKM.USELOCALWEBBROWSER=useLocalBrowser.value.isSelected();
					CheesyKM.FTPHOST=ftpHost.value().toString();
					CheesyKM.FTPPASS=ftpPass.value().toString();
					CheesyKM.FTPUSERNAME=ftpUserName.value().toString();
				}
				
				return true;
			} else {
				return false;
			}
			
		}
		/**
		*Checks if the configuration is valid.<br>
		*Only file Paths are checked yet. An error dialog is shown if something is wrong with the config.
		*An information dialog is shown if some modified parameters need to restart CheesyKM to become effective.
		*@return <code>true</code> if the configuration is valid, <code>false</code> else.
		*/
		public boolean configIsValide(){
			if(this.avance){
				if(!new File(ksPath.fileName.getText()).exists()){
					JOptionPane.showMessageDialog(null,CheesyKM.getLabel("omfgNoKeystore"), CheesyKM.getLabel("fileNotFound"), JOptionPane.ERROR_MESSAGE);
					ksPath.fileName.selectAll();
					ksPath.fileName.requestFocus();
					return false;
				} else if(!new File(browserPath.fileName.getText()).exists()){
					browserPath.fileName.selectAll();
					browserPath.fileName.requestFocus();
					JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("butWhereDidYouPutTheBrowser"),  CheesyKM.getLabel("fileNotFound"), JOptionPane.ERROR_MESSAGE);
					return false;
				} else {
					if((CheesyKM.USEJAVALAF&&!useJavaLaf.value.isSelected())||(!CheesyKM.USEJAVALAF&&useJavaLaf.value.isSelected())||!CheesyKM.KEYSTOREPASS.equals(ksPass.tf.getText())||!CheesyKM.KEYSTOREPATH.equals(ksPath.fileName.getText())||(CheesyKM.SHOWTOOLTIPS&&!showToolTips.value.isSelected())||(!CheesyKM.SHOWTOOLTIPS&&showToolTips.value.isSelected()))
				JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("changesWillBeEffectiveLater"), CheesyKM.getLabel("modifiedSSLParameters"), JOptionPane.INFORMATION_MESSAGE);
			
					return true;
				}
			} else {
				if((CheesyKM.USEJAVALAF&&!useJavaLaf.value.isSelected())||(!CheesyKM.USEJAVALAF&&useJavaLaf.value.isSelected())||(CheesyKM.SHOWTOOLTIPS&&!showToolTips.value.isSelected())||(!CheesyKM.SHOWTOOLTIPS&&showToolTips.value.isSelected()))
				JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("changesWillBeEffectiveLater"), CheesyKM.getLabel("modifiedSSLParameters"), JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
			
			
		}
	}
	/**
	*Updates CheesyKMs constants to default parameters.
	*/
	public static void loadDefaultConfig(){
		if(CheesyKM.KEYSTOREPASS!=null){
			if(!CheesyKM.KEYSTOREPASS.equals("lechsogr")||!CheesyKM.KEYSTOREPATH.equals(CheesyKM.getInstallationPath()+System.getProperty("file.separator")+"keystores"+System.getProperty("file.separator")+"kslabo"))
				JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("changesWillBeEffectiveLater"),CheesyKM.getLabel("modifiedSSLParameters"), JOptionPane.INFORMATION_MESSAGE);
		}
			
		CheesyKM.EASYKMROOT="https://lab.elikya.com/EasyKM/";//racine d'EasyKM
		CheesyKM.KEYSTOREPATH=CheesyKM.getInstallationPath()+System.getProperty("file.separator")+"keystores"+System.getProperty("file.separator")+"kslabo";
		CheesyKM.KEYSTOREPASS="lechsogr";
		CheesyKM.INITHEIGHT=768;
		CheesyKM.INITWIDTH=1024;
		CheesyKM.INITX=0;
		CheesyKM.INITY=0;
		CheesyKM.DEFAULTSEARCHFIELDNUMBER=1;
		CheesyKM.BUTTONTOOLBARLOCATION="North";
		CheesyKM.SEARCHTOOLBARLOCATION="North";
		CheesyKM.REMEMBERLASTLOGIN=true;//se souvenir du dernier login
		CheesyKM.LASTLOGIN="";//dernier login
		CheesyKM.MAXDOCSINMEM=500;//nombre maxi de documents dans l'arbre
		CheesyKM.AUTOCOLLAPSE=false;//activer le forçage du collapse de l'arbre (experimental...)
		CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE=70;//nombre maxi de documetns dans l'arbre avant forçage de collapse
		CheesyKM.MAXDOCTABSTITLESIZE=30;//taille maxi du titre des tabs (en caractères)
		CheesyKM.DEFAULTACTIONCLICKCOUNT=1;//nombre de clic pour les action par défaut (ex. afficher un doc)
		CheesyKM.MULTIPLETABSDOCS=true;//autorise l'affichage de plusieurs tabs d'affichage détaillé de doc ou non
		String sep=System.getProperty("file.separator");
		if(sep.equals("\\")){
			CheesyKM.WEBBROWSERPATH="C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
		} else {
			CheesyKM.WEBBROWSERPATH="/usr/bin/mozilla";//chemin complet absolu du nevigateur web local.
		}
		CheesyKM.USELOCALWEBBROWSER=true;//utiliser ou non le navigateur local pour afficher les pages web.
		CheesyKM.USERLOCALWEBBROWSERTODLFILES=true;//utiliser ou non le navigateur local pour voir les fichiers;
		CheesyKM.NOMBREDENOUVEAUTES=10;//Nombre de nouveautés à afficher dans la liste des nouveautés
		CheesyKM.EXPANDSEARCHRESULT=true;
		CheesyKM.FTPHOST="lab.elikya.com";
		CheesyKM.FTPPASS="anonymous";
		CheesyKM.FTPUSERNAME="anonymous";
		CheesyKM.USEJAVALAF=false;
		CheesyKM.USEFOLDERWATCHING=true;
		CheesyKM.WATCHEDFOLDER=System.getProperty("user.home")+System.getProperty("file.separator")+"ImportCheesyKM";
		if(!new File(CheesyKM.WATCHEDFOLDER).exists()){
			new File(CheesyKM.WATCHEDFOLDER).mkdir();
		} else if(!new File(CheesyKM.WATCHEDFOLDER).isDirectory()){
			new File(CheesyKM.WATCHEDFOLDER).delete();
			new File(CheesyKM.WATCHEDFOLDER).mkdir();
		}
		new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"SuccessFully Imported").mkdir();
		new File(CheesyKM.WATCHEDFOLDER+System.getProperty("file.separator")+"Not Imported").mkdir();
		if(CheesyKM.USEFOLDERWATCHING&&CheesyKM.login!=null){
			if(CheesyKM.watchFolder!=null)CheesyKM.watchFolder.kill();
			CheesyKM.watchFolder=new WatchFolder();
			CheesyKM.watchFolder.start();
		}
		CheesyKM.SHOWTOOLTIPS=true;
	}
	
	/**
	*Deletes a file.
	*@param nomFic path to the file to delete.
	*/
	private static void effacer(String nomFic){
    		File f=new File(nomFic);
		if(f.exists())
			f.delete();
	    	f=null;
	}
}
