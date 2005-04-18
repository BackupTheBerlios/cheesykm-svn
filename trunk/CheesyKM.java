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
*CheesyKM main class.<br>
*Contains all the XML-RPC methods, and the global constants<br>
*<br>SSL :<br>
*The clients VM has to have an access to a SSL key file (keystore) containing the connections SSL certificate.<br>
*UNIX COMMAND to create the key file "kslabo" from the certificate "cacert.crtf" :<br>
*keytool -import -alias labo -file ~/cacert.crtf -keystore kslabo<br>
*/
public class CheesyKM{
	/**The {@link CheesyKMAPI}*/
	static CheesyKMAPI api;
	/**Login name, <code>null</code> if no session is currently open*/
	static String login=null;
	/**session password, null if no session is currently open*/
	private static String pass=null;
	/**{@link Topic}s that are loaded in memory*/
	static Vector topicsInMem;
	/**{@link Doc}s that are loaded in memory*/
	static Vector docsInMem;
	/**<code>Hashtable</code> (<code>String</code> tid=><code>String</code> topicName) where tid="T"+"XX"; is initialised at successfull login*/
	private static Hashtable tNames;
	/**<code>Hashtable</code> (<code>String</code> tid=><code>String</code> parentsTid) where tid="T"+"XX"; is initialised at successfull login*/
	static Hashtable tRelations;
	/**<code>Vector</code> of root {@link Topic}s for the current user, containing (<code>String</code>)tids; is initialised at successfull login*/
	static Vector rootTopics;
	
	/**EasyKM Root URL*/
	public static String EASYKMROOT;
	/**Height of the main Frame*/
	public static int INITHEIGHT;
	/**Width of the main Frame*/
	public static int INITWIDTH;
	/**X position of the main frame*/
	public static int INITX;
	/**Y position of the main frame*/
	public static int INITY;
	/**Location of the quick search toolbar ("North" or "South", but is straight, never goes West ;) )*/
	public static String SEARCHTOOLBARLOCATION;
	/**Location of the general toolbar ("North","South","East" or "West", this one is totally open-minded)*/
	public static String BUTTONTOOLBARLOCATION;
	/**if <code>true</code>, remember last login, and pre-fill the {@link Login} with it*/
	public static boolean REMEMBERLASTLOGIN;
	/**identifier of the last login*/
	public static String LASTLOGIN;
	/**Maximal number of {@link Doc}s in memory before trying to unload them (see {@link MemoryMonitor})*/
	public static int MAXDOCSINMEM;
	/**if <code>true</code>, activates autocollapse funtion, which force Topic Tree View collapse to unload docs (doesnt works well yet)*/
	public static boolean AUTOCOLLAPSE;
	/**if the autocollapse function is activated, force tree collapse when this number of docs in memory is reached*/ 
	public static int MAXDOCSINMEMBEFOREAUTOCOLLAPSE;
	/**Maximum length of the docs tabs titles*/
	public static int MAXDOCTABSTITLESIZE;
	/**Click count for the default action in the topic tree view and news list (usually 1 or 2, but very nervous persons can set it to 10)*/
	public static int DEFAULTACTIONCLICKCOUNT;
	/**if <code>true</code>, use several tabs to display docs, if <code>false</code>, only one tab will be used*/
	public static boolean MULTIPLETABSDOCS;
	/**Path to the local web browser*/
	public static String WEBBROWSERPATH;
	/**if <code>true</code>, use local web browser to browse webpages, if <code>false</code> use internal (in a tab) web browser*/ 
	public static boolean USELOCALWEBBROWSER;
	/**if <code>true</code>, use local web browser to view attached documents. must be <code>true</code> (funtion not implemented yet)*/
	public static boolean USERLOCALWEBBROWSERTODLFILES;
	/**length of the news list*/
	public static int NOMBREDENOUVEAUTES;
	/**if <code>true</code>, expand the search results tree at display, if <code>false</code> this tree will be collapsed at his display*/
	public static boolean EXPANDSEARCHRESULT;
	
	
	//pour la demo labo :
	/**Path to the keystore file (SSL certificates keyring java file*/
	public static String KEYSTOREPATH;
	/**Pass of the keystore file*/
	public static String KEYSTOREPASS;
	
	
	
	//pour la demo web :
	/*
	private static final String addresseWebservices=EASYKMROOT+"webservices.php";
	private static final String KEYSTOREPATH="./ressources/ksweb";
	private static final String KEYSTOREPASS="easykm";
	*/
	
	/**The configuration associated with this insatance of CheesyKM*/
	static Config config;
	/**RessourceBundle of all the localized Strings*/
	static ResourceBundle labels;
	
	/**
	*Main method, gets a global configuration, and initializes a CheesyKMAPI (main frame).
	*Sets XmlRpcs global parameters too.
	*/
	public static void main(String[] args) {
		//Initialize a set of Strings (ResourceBundle) with the current Locale
		labels=ResourceBundle.getBundle("ressources.labels.Labels",Locale.getDefault());
		
		//Seems to be a bug with fonts on some VMs if not called
		Object fonts[]=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		//Load a global configuration
		config=new Config();
		config.loadConfig();
		
		//Set XmlRpc global parameters
		//XmlRpc.setDebug(true);
		XmlRpc.setEncoding("UTF8");
		XmlRpc.setInputEncoding("UTF-8");
		
		//the MinML parser is used by default by XmlRpc, uncomment this to use another parser (xerces, for example)
		/*
		try{
		XmlRpc.setDriver("xerces");
		} catch(Exception e){
			echo(e);
		}
		*/
		
		//some inits
		topicsInMem=new Vector();
		docsInMem=new Vector();
		
		//new main frame
		api=new CheesyKMAPI();
		new MemoryMonitor();
	}
	
	/**
	*Get a Localized String ressource.
	*@param key Name of the ressource.
	*@return the localized String ressource matching this key, or "MISSING:"+key if the key couldn't be found.
	*/
	public static String getLabel(String key){
		try{
			return labels.getString(key);
		} catch(MissingResourceException mre){
			return "MISSING:"+key;
		}
	}
	
	/**
	*Updates the identifier and password of the current session, these parameters are used by the RPC calls during the whole session.
	*@param nlogin String user identifier.
	*@param npass String user password.
	*/
	public static void setLogin(String nlogin,String npass){
		login=nlogin;
		pass=npass;
	}
	
	/**
	*Creates a RPC client, on which RPC methods can be called (execute() method).
	*@return a tiny all-alone-transparent-SSL-managing Xml-Rpc client.
	*/
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
	*Calls the "getTopicMatrix" RPC method.
	*@return a Vector containing (in order) : <br>-a Hashtable (Tid -> topic name) (tNames)<br>-a Hashtable (Tid -> parent Tid )(1 si root)) (tRelations)<br>-a Vector of current users root Tid (tRoot).
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
	*Calls the "getDocsInTopic" RPC method.
	*@param tid TopicID, 'TXX'.
	*@return a Vector of document Hashtables.
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
	*Calls the "getDocsInTopic" RPC method.
	*@param tid TopicID, as an int.
	*@return a Vector of document Hashtables.
	*/
	public static Vector getDocsInTopic(int tid){
		return getDocsInTopic("T"+tid);
	}
	
	
	/**
	*Calls the "getLastDocs" RPC method.
	*@return a Vector of the last CheesyKM.NOMBREDENOUVEAUTES document Hashtables.
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
	/**
	*Calls the "search" RPC method, but like a simple search query (typed in the quickSearch toolbar)
	*@param where Specifies where to search ("title","author","kwords","text" or "anywhere").
	*@param what Specifies the words to search for, this String is Tokenized with default StringTokenizer settings.
	*@return a Vector of document Hashtables.
	*/
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
	*Debug puposes, displays o on Standard Output, for a lazy programmer that thinks that "System.out.println" is boring to type.
	*/
	public static void echo(Object o){
		System.out.println(o);
	}
	
	/**
	*Updates the Topics name Hashtable (Tid -> TopicName) (Tid as "TXX") for this session.
	*@param newTNames new table.
	*/
	public static void setTNames(Hashtable newTNames){
		tNames=newTNames;
	}
	
	/**
	*Returns the Topics name Hashtable (Tid -> TopicName) (Tid as "TXX") for this session.
	*@return Hashtable (Tid -> TopicName) for this session.
	*/
	public static Hashtable getTNames(){return tNames;}
	
	/**
	*Returns a Topic name from its id;
	*@param tid TopicID, as "TXX".
	*@return A String name of this Topic.
	*/
	public static String getTopicNameById(String tid){
		return (String)tNames.get(tid);
	}
	
	/**
	*Returns a Topic name from its id;
	*@param tid TopicID, as an int.
	*@return A String name of this Topic.
	*/
	public static String getTopicNameById(int tid){
		return getTopicNameById("T"+tid);
	}
	
	/**
	*Called at disconnect, clears all session-relative informations.
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
	*Returns the full name of a Topic, with all its path from root in the topic tree view, separated by a "/"
	*@param topic TopicID, as "TXX".
	*@return full path of this Topic.
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
	*Opens a webpage.<br>
	*Tries to launch the local web browser if(CheesyKM.USELOCALWEBBROWSER), opens a tab on the site else.<br>
	*Makes 1 connection to the specified counter.
	*@param url String representation of the counters URL (full, with properties)
	*@param realURL String representation of the real page URL.
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
	*Opens the webPage associated to a Doc, making a connection to the counter.<br>
	*Does nothing if doc hasn't any website URL.
	*@param doc Doc to browse the URL of.
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
	*Copies a String to the System Clipboard.<br>
	*Shows an error message dialog if the ClipBoard isn't accessible.
	*@param s the String to copy to clipboard.
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
	*Downloads the File attached to a Doc.<br>
	*Adds 1 download to EasyKMs download counter for this Doc.
	*@param d the Doc to download te attached file of.
	*@param forceDownload if true, the file will be downloaded by CheesyKMs internal download manager, if false CheesyKM.USERLOCALWEBBROWSERTODLFILES will be checked to determineif the web browser should be used.
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
	
	/**
	*Downloads the file attached to a Doc to a local Filename with the internal download manager.
	*@param fichierDest String name of the local path to save the file to.
	*@param d Doc to download the attached file of.
	*/
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
	
	/**
	*Downloads the file attached to a Doc to a local Filename with the internal download manager, creates a new instance of Download.
	*@param fichierDest String name of the local path to save the file to.
	*@param d Doc to download the attached file of.
	*@param size size of the file to download.
	*/
	private static void download(String nomComplet,String url,long size){
		new Download(nomComplet,url,size);
	}
	
}
