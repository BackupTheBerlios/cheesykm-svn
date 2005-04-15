import java.util.*;
import javax.swing.*;
import java.awt.*;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.secure.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import javax.swing.text.*;
import java.lang.*;

/**
*Classe principale du client, contient le main.<br>
*Contient toutes les méthodes liées directement aux webservices XML-RPC, ainsi que les constantes globales de l'application.<br>
*<br>SSL :<br>
*La machine virtuelle du client doit avoir accès au fichier de clé (keystore) contenant le certificat SSL de la connection.<br>
*COMMANDE pour creer le fichier de clé "kslabo" à partir du certificat "cacert.crtf" :<br>
*keytool -import -alias labo -file ~/cacert.crtf -keystore kslabo<br>
*/
public class CheesyKM{
	static CheesyKMAPI api;
	static String login=null;
	private static String pass=null;

	static Vector topicsInMem;
	static Vector docsInMem;
	private static Hashtable tNames;
	static Hashtable tRelations;
	static Vector rootTopics;
	
	//Paramètres généraux de l'application
	public static String EASYKMROOT;//racine d'EasyKM
	public static int INITHEIGHT;
	public static int INITWIDTH;
	public static int INITX;
	public static int INITY;
	public static String SEARCHTOOLBARLOCATION;
	public static String BUTTONTOOLBARLOCATION;
	public static boolean REMEMBERLASTLOGIN;//se souvenir du dernier login
	public static String LASTLOGIN;//dernier login
	public static int MAXDOCSINMEM;//nombre maxi de documents dans l'arbre
	public static boolean AUTOCOLLAPSE;//activer le forçage du collapse de l'arbre (experimental...)
	public static int MAXDOCSINMEMBEFOREAUTOCOLLAPSE;//nombre maxi de documetns dans l'arbre avant forçage de collapse
	public static int MAXDOCTABSTITLESIZE;//taille maxi du titre des tabs (en caractères)
	public static int DEFAULTACTIONCLICKCOUNT;//nombre de clic pour les action par défaut (ex. afficher un doc)
	public static boolean MULTIPLETABSDOCS;//aurise l'affichage de plusieurs tabs d'affichage détaillé de doc ou non
	public static String WEBBROWSERPATH;//chemin complet absolu du nevigateur web local.
	public static boolean USELOCALWEBBROWSER;//utiliser ou non le navigateur local pour afficher les pages web.
	public static boolean USERLOCALWEBBROWSERTODLFILES;//utiliser ou non le navigateur local pour voir les fichiers;
	public static int NOMBREDENOUVEAUTES;//Nombre de nouveautés à afficher dans la liste des nouveautés
	public static boolean EXPANDSEARCHRESULT;//expand the search results by default
	
	
	//pour la demo labo :
	
	public static String KEYSTOREPATH;
	public static String KEYSTOREPASS;
	
	
	
	//pour la demo web :
	/*
	private static final String addresseWebservices=EASYKMROOT+"webservices.php";
	private static final String KEYSTOREPATH="./ressources/ksweb";
	private static final String KEYSTOREPASS="easykm";
	*/
	
	
	static Config config;
	static ResourceBundle labels;
	
	public static void main(String[] args) {
		labels=ResourceBundle.getBundle("ressources.labels.Labels",Locale.getDefault());
		//labels=ResourceBundle.getBundle("ressources.labels.Labels",Locale.FRENCH);
		Object fonts[]=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		config=new Config();
		config.loadConfig();
		//XmlRpc.setDebug(true);
		
		XmlRpc.setEncoding("UTF8");
		XmlRpc.setInputEncoding("UTF-8");
		
		/*
		try{
		XmlRpc.setDriver("xerces");
		} catch(Exception e){
			echo(e);
		}
		*/
		
		topicsInMem=new Vector();
		docsInMem=new Vector();
		api=new CheesyKMAPI();
		new MemoryMonitor();
		
		api.buttonToolBarLocation();
		
	}
	
	public static String getLabel(String key){
		try{
			return labels.getString(key);
		} catch(MissingResourceException mre){
			return "MISSING:"+key;
		}
	}
	
	/**
	*Met à jour les identifiants de l'utilisateur. Ils seront utilisés durant tout le reste de la session.
	*@param nlogin String Identifiant de l'utilisateur.
	*@param npass String Mot de passe de l'utilisateur.
	*/
	public static void setLogin(String nlogin,String npass){
		login=nlogin;
		pass=npass;
	}
	
	private static SecureXmlRpcClient client(){
		try {
			//echo("Path:"+KEYSTOREPATH+"  Pass:"+KEYSTOREPASS);
			System.setProperty("javax.net.ssl.keyStore",KEYSTOREPATH);
			System.setProperty("javax.net.ssl.keyStorePassword",KEYSTOREPASS);
			System.setProperty("javax.net.ssl.trustStore",KEYSTOREPATH);
			System.setProperty("javax.net.ssl.trustStorePassword",KEYSTOREPASS);
			return new SecureXmlRpcClient(EASYKMROOT+"webservices.php");
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	*Appel de la procédure RPC "getTopicMatrix".
	*@return Un Vector contenant (dans l'ordre) : <br>-Une Hashtable (Tid -> nom du thème)<br>-Une Hashtable (Tid -> Tid du parent (1 si racine))<br>-Un Vector des tid de base de l'utilisateur.
	*/
	public static Vector getTopicMatrix(){
		try{
			//echo("client créé");
			//System.setProperty("javax.net.debug","all");
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			Vector resu;
			return (Vector)client().execute("getTopicMatrix",params);
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	*Appel de la procédure RPC "getDocsInTopic".
	*@param tid TopicID, de la forme 'TXX'.
	*@return Un Vector de Hashtable documents.
	*/
	public static synchronized Vector getDocsInTopic(String tid){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(tid);
			Vector resu;
			return (Vector)client().execute("getDocsInTopic",params);
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	*Appel de la procédure RPC "getDocsInTopic".
	*@param tid TopicID, int n° du topic.
	*@return Un Vector de Hashtable documents.
	*/
	public static Vector getDocsInTopic(int tid){
		return getDocsInTopic("T"+tid);
	}
	
	
	/**
	*Appel de la procédure RPC "getLastDocs".
	*@return Un Vector contenant les NOMBREDENOUVEAUTES derniers documents, sous forme de Hashtable.
	*/
	public static Vector getLastDocs(){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(new Integer(CheesyKM.NOMBREDENOUVEAUTES));
			Vector resu;
			return (Vector)client().execute("getLastDocs",params);
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	
	/*6 search
	Pour effectuer une recherche dans la base (avec filtrage selon le profil).
	Paramètres : (string) user, (string) password, (struct) query
	query est ainsi définie :
	
	title -> struct { 
	may -> array of (string) words that may be contained in this field
	must -> array of (string) words that must be contained in this field
	mustnot -> array of (string) words that must be contained in this field
	phrases -> array of exact phrases that must be matched in this field
	} as QExp
	author -> struct QExp
	kwords -> struct QExp
	text -> struct QEXp (full text matches in description and attached file)
	anywhere -> struct QExp (matches title or author or kwords or text)
	
	searchmode -> (string) is "boolean" (classical) 
	types -> array of (string) types among "print", "image", "video", "sound", "act", "web", "note"
	topics -> array of (string) root topic ids to search in recursively
	since -> (string) equal to "ever" or a date in the form "YYYY-MM-DD" (filters docs SINCE last entry modification date)
	creation -> (string) equal to "ever" or a date in the form "YYYY-MM-DD" (attached document or site creation date)
	creation_op -> (string) one operator among ">", "<", "=" (for creation date comparison)
	creation_mode -> (int) 0 to exclude undated documents, 1 to always include them
	format -> (string) equal to "any" or one file extension
	size -> (int) size to compare to, in bytes
	size_op -> (string) comparison operator among ">", "<", "="
	limit -> (int) maximum number of document returned
	expired -> (int) should we include expired documents?
	ufopX -> (string) operator for testing user extended attribute X (X = 0,1,2). Possible operators: =, !=, >=, <=, <, >
	uftermX -> (string) value to compare to user extended attribute X (X = 0,1,2) with operator ufopX (set to empty string to ignore)*/

	public static Vector quickSearch(String where, String what){
		Vector fields=new Vector();
		fields.add("title");
		fields.add("author");
		fields.add("kwords");
		fields.add("text");
		fields.add("anywhere");
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			
			Vector whatV=new Vector();
			StringTokenizer whatSTRTK=new StringTokenizer(what);
			while(whatSTRTK.hasMoreTokens()){
				whatV.add(whatSTRTK.nextToken());
			}
			
			Hashtable query=new Hashtable();
			
			for(int i=0;i<fields.size();i++){
				Hashtable qExp=new Hashtable();
				qExp.put("may",new Vector());
				if(where.equals(fields.get(i))){
					qExp.put("must",whatV);
				} else {
					qExp.put("must",new Vector());
				}
				qExp.put("mustnot",new Vector());
				qExp.put("phrases",new Vector());
				query.put(fields.get(i),qExp);
			}
			query.put("searchmode","boolean");
			//types -> array of (string) types among "print", "image", "video", "sound", "act", "web", "note"
			Vector types=new Vector();
			types.add("print");
			types.add("image");
			types.add("video");
			types.add("sound");
			types.add("act");
			types.add("web");
			types.add("note");
			query.put("types",types);
			Vector topics=new Vector();
			for(int i=0;i<rootTopics.size();i++){
				topics.add("T"+rootTopics.get(i));
			}
			query.put("topics",topics);
			
			query.put("since","ever");
			query.put("creation","ever");
			query.put("creation_op","=");
			query.put("creation_mode",new Integer(1));
			query.put("format","any");
			query.put("size",new Integer(0));
			query.put("size_op",">");
			query.put("limit",new Integer(500));
			query.put("expired",new Integer(1));
			for(int i=0;i<3;i++){
				query.put("ufop"+i,"=");
				query.put("ufterm"+i,"");
			}
			
			params.add(query);
			return (Vector)client().execute("search",params);
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	*Pour le debug, affiche o sur la sortie standard.
	*/
	public static void echo(Object o){
		System.out.println(o);
	}
	
	/**
	*Met à jour la Hashtable des noms de Topic (Tid -> nom du thème) de la session.
	*@param newTNames nouvelle table.
	*/
	public static void setTNames(Hashtable newTNames){
		tNames=newTNames;
	}
	
	/**
	*Retourne la Hashtable des noms de Topic (Tid -> nom du thème) de la session.
	*@return Hashtable des noms de Topic (Tid -> nom du thème) de la session.
	*/
	public static Hashtable getTNames(){return tNames;}
	
	/**
	*Retourne la nom d'un topic à partir de son tid.
	*@param tid TopicID, de la forme 'TXX'.
	*@return Un String nom du topic.
	*/
	public static String getTopicNameById(String tid){
		return (String)tNames.get(tid);
	}
	
	/**
	*Retourne la nom d'un topic à partir de son tid.
	*@param tid TopicID, int n° du topic.
	*@return Un String nom du topic.
	*/
	public static String getTopicNameById(int tid){
		return getTopicNameById("T"+tid);
	}
	
	/**
	*Réinitialise les paramètres de session (login, documents, etc..)
	*/
	public static void deconnecter(){
		setLogin(null,null);
		tNames=null;
		rootTopics=null;
		topicsInMem=new Vector();
		docsInMem=new Vector();
		System.gc();
	}
	
	/**
	*renvoie le nom complet d'un topic (tout le chemin dans l'arborescence, séparé par de "/").
	*/
	public static String getTopicFullName(String topic){
		TreePath path=api.thematique.getPathForTopic(Integer.parseInt(topic.substring(1)));
		String resu=new String();
		for(int i=1;i<path.getPathCount();i++){
			resu=resu+((DefaultMutableTreeNode)(path.getPathComponent(i))).getUserObject().toString();
			if(i!=path.getPathCount()-1) resu=resu+"/";
		}
		return resu;
	}
	
	/**
	*Ouvre une page web.
	*/
	public static void openURL(String url,String realURL){
		if(!CheesyKM.USELOCALWEBBROWSER){
			WebPageTopic t=new WebPageTopic(url,realURL);
			CheesyKM.api.displayTopic(t);
		} else {
			try{
				Runtime.getRuntime().exec(CheesyKM.WEBBROWSERPATH+" "+url);
			} catch(IOException ioe){
				JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	*ouvre la page web associée à un document en mettant le compteur à jour
	*/
	public static void openURL(Doc doc){
		if(!doc.url.equals("")){
			String docURL=new String(doc.url);
			docURL=docURL.replaceAll(":","%3A");
			docURL=docURL.replaceAll("/","%2F");
			docURL=EASYKMROOT+"counter.php?target=site&ref="+docURL+"&docid="+doc.id;
			openURL(docURL,doc.url);
			doc.visits++;
		}
	}
	/**
	*Copie un String dans le presse-papier(clipboard) système.
	*/
	public static void copyToClipBoard(String s){
		StringSelection select=new StringSelection(s);
		try{
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(select,select);
		} catch(IllegalStateException ise){
			JOptionPane.showMessageDialog(null, getLabel("errorClipboard"), getLabel("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	*télécharge le fichier associè à un Doc d.
	*/
	public static void download(Doc d,boolean forceDownload){
		if(USERLOCALWEBBROWSERTODLFILES&&!forceDownload){
			try{
				String urlCompteur=new String(d.file);
				urlCompteur=urlCompteur.replaceAll("/","%2F");
				urlCompteur=EASYKMROOT+"counter.php?target=file&ref="+urlCompteur+"&docid="+d.id;
				Runtime.getRuntime().exec(WEBBROWSERPATH+" "+urlCompteur);
				d.downloads++;
			} catch(IOException ioe){
				JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			
			String nomFic=FileChooserDialog.showChooser(api,getLabel("saveFileAs"),new FiltreFics("."+d.format),true,d.title+"."+d.format);
			if(nomFic!=null){
				String nomComplet=nomFic;
				if(!nomComplet.endsWith("."+d.format)) nomComplet=nomComplet+"."+d.format;
				File fichierDest=new File(nomComplet);
				if(fichierDest.exists()){
					if(JOptionPane.showConfirmDialog(api,getLabel("theFile")+nomComplet+getLabel("alreadyExists"), getLabel("overwriteExistingFile"), JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
						download(nomComplet,d);
					} else {
						download(d,forceDownload);
					}
				} else {
					download(nomComplet,d);
				}
				
			}
		}
	}
	
	private static void download(String fichierDest,Doc d){
		String urlCompteur=new String(d.file);
		urlCompteur=urlCompteur.replaceAll("/","%2F");
		urlCompteur=EASYKMROOT+"counter.php?target=file&ref="+urlCompteur+"&docid="+d.id;
		JEditorPane bidon=new JEditorPane();
		try{
			bidon.setPage(urlCompteur);
		} catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorCounterAccess"), JOptionPane.ERROR_MESSAGE);
		}
		download(fichierDest,EASYKMROOT+d.file,d.size);
	}
	
	private static void download(String nomComplet,String url,long size){
		new Download(nomComplet,url,size);
	}
	
}
