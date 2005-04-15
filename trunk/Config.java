import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.secure.*;
import java.net.*;

class Config{
	/*	//Paramètres généraux de l'application
	public static final String EASYKMROOT="https://lab.elikya.com/EasyKM/";//racine d'EasyKM
	private static final String KEYSTOREPATH="./ressources/kslabo";
	private static final String KEYSTOREPASS="lechsogr";
	public static final int INITHEIGHT=768;
	public static final int INITWIDTH=1024;
	public static final int INITX=0;
	public static final int INITY=0;
	public static final boolean REMEMBERLASTLOGIN=true;//se souvenir du dernier login
	public static String LASTLOGIN="sherve";//dernier login
	public static final int MAXDOCSINMEM=50;//nombre maxi de documents dans l'arbre
	public static final boolean AUTOCOLLAPSE=false;//activer le forçage du collapse de l'arbre (experimental...)
	public static final int MAXDOCSINMEMBEFOREAUTOCOLLAPSE=70;//nombre maxi de documetns dans l'arbre avant forçage de collapse
	public static final int MAXDOCTABSTITLESIZE=30;//taille maxi du titre des tabs (en caractères)
	public static final int DEFAULTACTIONCLICKCOUNT=1;//nombre de clic pour les action par défaut (ex. afficher un doc)
	public static final boolean MULTIPLETABSDOCS=true;//aurise l'affichage de plusieurs tabs d'affichage détaillé de doc ou non
	public static final String WEBBROWSERPATH="/usr/bin/firefox";//chemin complet absolu du nevigateur web local.
	public static final boolean USELOCALWEBBROWSER=true;//utiliser ou non le navigateur local pour afficher les pages web.
	public static final boolean USERLOCALWEBBROWSERTODLFILES=true;//utiliser ou non le navigateur local pour voir les fichiers;
	public static final int NOMBREDENOUVEAUTES=10;//Nombre de nouveautés à afficher dans la liste des nouveautés
	*/
	static final String nomFichierDeConf="./CheesyKM.conf";
	InitConfigPanel icp;
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
			fe.close();
			
		} catch(Exception e){
			//CheesyKM.echo(e);
			JOptionPane.showMessageDialog(null, CheesyKM.getLabel("loadingDefaultConfig"), CheesyKM.getLabel("information"), JOptionPane.INFORMATION_MESSAGE);
			this.loadDefaultConfig();
			this.saveConfig();
		}
	}
	
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
			bw.flush();
		} catch(Exception e){
			CheesyKM.echo(e);
		}
	}
	
	public void displayConfig(){
		JDialog config=new JDialog(CheesyKM.api);

		
		config.setModal(true);
		config.setResizable(false);
		config.setTitle(CheesyKM.getLabel("configuration"));
		Container gc=config.getContentPane();
		gc.setLayout(new BorderLayout());
		
		
		
		
		JPanel sud=new JPanel();
		sud.setLayout(new GridLayout(1,0));
		
		sud.add(new JLabel(" "));
		sud.add(new JLabel(" "));
		sud.add(new JLabel(" "));
		
		
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
		
		
		
		JButton bValider=new JButton(CheesyKM.getLabel("saveChanges"));
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
		//sud.add(new JLabel(" "));
		JButton bDefault=new JButton(CheesyKM.getLabel("default"));
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
	
	
	class InitConfigPanel extends JPanel{
		
		class IntegerValue extends JPanel{
				public JTextField tf;
				IntegerValue(String title,int value){
					super();
					add(new JLabel(title));
					tf=new JTextField();
					tf.setText(new Integer(value).toString());
					tf.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent e) {
							char c = e.getKeyChar();      
							if (!((Character.isDigit(c) ||(c == KeyEvent.VK_BACK_SPACE) ||(c == KeyEvent.VK_DELETE)))) {
								e.getComponent().getToolkit().beep();
								e.consume();
							}
						}
					});
					tf.setColumns(5);
					add(tf);
				}
			}
			
			class StringValue extends JPanel{
				public JTextField tf;
				StringValue(String title,String value){
					super();
					add(new JLabel(title));
					tf=new JTextField();
					tf.setText(value);
					tf.setColumns(20);
					add(tf);
				}
			}
			
			class BooleanValue extends JPanel{
				public JCheckBox value;
				BooleanValue(String title,boolean state){
					super();
					value=new JCheckBox(title,state);
					add(value);
				}
			}
			
			class FileNameValue extends JPanel{
				public JTextField fileName;
				FileNameValue(String title,String value){
					super();
					add(new JLabel(title));
					fileName=new JTextField();
					fileName.setColumns(20);
					fileName.setText(value);
					add(fileName);
					JButton browse=new JButton("...");
					class BrowseButtonListener implements ActionListener {
						JTextField fileName;
						BrowseButtonListener(JTextField tf){
							this.fileName=tf;
						}
						public void actionPerformed(ActionEvent e){
							//showChooser(JFrame parent,String title,FileFilter filtre,boolean enregistrer,String nomSuggere){
								fileName.setText(FileChooserDialog.showChooser(CheesyKM.api,CheesyKM.getLabel("selectFile"),null,false,null));
						}
					}
					browse.addActionListener(new BrowseButtonListener(fileName));
					add(browse);
				}
			}
		
		boolean avance;
		IntegerValue nombreNouveautes;
		BooleanValue rememberLastLogin;
		BooleanValue useManyTabs;
		IntegerValue titlesSize;
		IntegerValue clics;
		StringValue easyKM;
		FileNameValue ksPath;
		StringValue ksPass;
		IntegerValue docsInMem;
		FileNameValue browserPath;
		BooleanValue useLocalBrowser;
		BooleanValue expandSearchResults;
		InitConfigPanel(boolean avance){
			super();
			this.avance=avance;
			setLayout(new GridLayout(0,1));
			
			
			
			nombreNouveautes=new IntegerValue(CheesyKM.getLabel("howManyNews"),CheesyKM.NOMBREDENOUVEAUTES);
			add(nombreNouveautes);
			rememberLastLogin=new BooleanValue(CheesyKM.getLabel("rememberLastLogin"),CheesyKM.REMEMBERLASTLOGIN);
			add(rememberLastLogin);
			useManyTabs=new BooleanValue(CheesyKM.getLabel("useSeveralTabs"),CheesyKM.MULTIPLETABSDOCS);
			add(useManyTabs);
			titlesSize=new IntegerValue(CheesyKM.getLabel("tabsTitleLength"),CheesyKM.MAXDOCTABSTITLESIZE);
			add(titlesSize);
			clics=new IntegerValue(CheesyKM.getLabel("defaultActionClickCount"),CheesyKM.DEFAULTACTIONCLICKCOUNT);
			add(clics);
			expandSearchResults=new BooleanValue(CheesyKM.getLabel("expandSearchResult"),CheesyKM.EXPANDSEARCHRESULT);
			add(expandSearchResults);
			if(avance){
				easyKM=new StringValue(CheesyKM.getLabel("easyKMRoot"),CheesyKM.EASYKMROOT);
				add(easyKM);
				ksPath=new FileNameValue(CheesyKM.getLabel("keystorePath"),CheesyKM.KEYSTOREPATH);
				add(ksPath);
				ksPass=new StringValue(CheesyKM.getLabel("keystorePass"),CheesyKM.KEYSTOREPASS);
				add(ksPass);
				docsInMem=new IntegerValue(CheesyKM.getLabel("maxDocsInMem"),CheesyKM.MAXDOCSINMEM);
				add(docsInMem);
				browserPath=new FileNameValue(CheesyKM.getLabel("webBrowserPath"),CheesyKM.WEBBROWSERPATH);
				add(browserPath);
				useLocalBrowser=new BooleanValue(CheesyKM.getLabel("useLocalBrowserToBrowse"),CheesyKM.USELOCALWEBBROWSER);
				add(useLocalBrowser);
			}
		}
		
		public boolean validerConfig(){
			/*IntegerValue nombreNouveautes;
			BooleanValue rememberLastLogin;
			BooleanValue useManyTabs;
			IntegerValue titlesSize;
			IntegerValue clics;
			StringValue easyKM;
			FileNameValue ksPath;
			StringValue ksPass;
			IntegerValue docsInMem;
			FileNameValue browserPath;
			BooleanValue useLocalBrowser;*/
			
			if(configIsValide()){
				CheesyKM.NOMBREDENOUVEAUTES=Integer.parseInt(nombreNouveautes.tf.getText());
				CheesyKM.REMEMBERLASTLOGIN=rememberLastLogin.value.isSelected();
				CheesyKM.MULTIPLETABSDOCS=useManyTabs.value.isSelected();
				CheesyKM.MAXDOCTABSTITLESIZE=Integer.parseInt(titlesSize.tf.getText());
				CheesyKM.DEFAULTACTIONCLICKCOUNT=Integer.parseInt(clics.tf.getText());
				CheesyKM.EXPANDSEARCHRESULT=expandSearchResults.value.isSelected();
				if(this.avance){
					CheesyKM.EASYKMROOT=easyKM.tf.getText();
					CheesyKM.KEYSTOREPATH=ksPath.fileName.getText();
					CheesyKM.KEYSTOREPASS=ksPass.tf.getText();
					CheesyKM.MAXDOCSINMEM=Integer.parseInt(docsInMem.tf.getText());
					CheesyKM.WEBBROWSERPATH=browserPath.fileName.getText();
					CheesyKM.USELOCALWEBBROWSER=useLocalBrowser.value.isSelected();
				}
				
				return true;
			} else {
				return false;
			}
			
		}
		
		private boolean configIsValide(){
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
					if(!CheesyKM.KEYSTOREPASS.equals(ksPass.tf.getText())||!CheesyKM.KEYSTOREPATH.equals(ksPath.fileName.getText()))
				JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("changesWillBeEffectiveLater"), CheesyKM.getLabel("modifiedSSLParameters"), JOptionPane.INFORMATION_MESSAGE);
			
					return true;
				}
			} else {
				return true;
			}
			
			
		}
	}
	
	public static void loadDefaultConfig(){
		if(CheesyKM.KEYSTOREPASS!=null){
			if(!CheesyKM.KEYSTOREPASS.equals("lechsogr")||!CheesyKM.KEYSTOREPATH.equals("./ressources/kslabo"))
				JOptionPane.showMessageDialog(null,  CheesyKM.getLabel("changesWillBeEffectiveLater"),CheesyKM.getLabel("modifiedSSLParameters"), JOptionPane.INFORMATION_MESSAGE);
		}
			
		CheesyKM.EASYKMROOT="https://lab.elikya.com/EasyKM/";//racine d'EasyKM
		CheesyKM.KEYSTOREPATH="./ressources/kslabo";
		CheesyKM.KEYSTOREPASS="lechsogr";
		CheesyKM.INITHEIGHT=768;
		CheesyKM.INITWIDTH=1024;
		CheesyKM.INITX=0;
		CheesyKM.INITY=0;
		CheesyKM.BUTTONTOOLBARLOCATION="North";
		CheesyKM.SEARCHTOOLBARLOCATION="North";
		CheesyKM.REMEMBERLASTLOGIN=true;//se souvenir du dernier login
		CheesyKM.LASTLOGIN="";//dernier login
		CheesyKM.MAXDOCSINMEM=50;//nombre maxi de documents dans l'arbre
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
	}
	
	private static void effacer(String nomFic){
    		File f=new File(nomFic);
		if(f.exists())
			f.delete();
	    	f=null;
	}
}
