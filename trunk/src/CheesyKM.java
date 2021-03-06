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
import java.text.*;
import com.enterprisedt.net.ftp.*;

/**
*CheesyKM main class.<br>
*Contains all the XML-RPC methods, and the global constants<br>
*<br>SSL :<br>
*The clients VM has to have an access to a SSL key file (keystore) containing the connections SSL certificate.<br>
*UNIX COMMAND to create the key file "kslabo" from the certificate "cacert.crtf" :<br>
*keytool -import -alias labo -file ~/cacert.crtf -keystore kslabo<br>
*/
public class CheesyKM{
	/**The {@link CheesyKMGUI}*/
	static CheesyKMGUI api;
	/**Login name, <code>null</code> if no session is currently open*/
	static String login=null;
	/**session password, null if no session is currently open*/
	static String pass=null;
	/**maximum right level that the current user has on all accessible topics*/
	static int maximumRightLevel=0;
	/**{@link Topic}s that are loaded in memory*/
	static Vector topicsInMem;
	/**{@link Doc}s that are loaded in memory*/
	static Vector docsInMem;
	/**<code>Hashtable</code> (<code>String</code> tid=><code>String</code> topicName) where tid="T"+"XX"; is initialised at successfull login*/
	private static Hashtable tNames;
	/**<code>Hashtable</code> (<code>String</code> tid=><code>String</code> parentsTid) where tid="T"+"XX"; is initialised at successfull login*/
	static Hashtable tRelations;
	/**<code>Hashtable</code> (<code>String</code> tid=><code>Integer</code> flag) where flag represents the rights that the current user has on the tid Topic*/
	static Hashtable tRights;
	/**<code>Vector</code> of root {@link Topic}s for the current user, containing (<code>String</code>)tids; is initialised at successfull login*/
	static Vector rootTopics;
	/**EasyKM configuration Hashtable */
	static Hashtable easyKMConfig;
	/**Watch folder thread*/
	static WatchFolder watchFolder;
	
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
	/**Path to the keystore file (SSL certificates keyring java file*/
	public static String KEYSTOREPATH;
	/**Pass of the keystore file*/
	public static String KEYSTOREPASS;
	/**FTP server hostname*/
	public static String FTPHOST;
	/**FTP server userName*/
	public static String FTPUSERNAME;
	/**FTP server pass*/
	public static String FTPPASS;
	/**Default number of search field in the advanced search form*/
	public static int DEFAULTSEARCHFIELDNUMBER;
	/**use java L&F ?*/
	public static boolean USEJAVALAF;
	/**Folder to watch the content for and auto-import*/
	public static String WATCHEDFOLDER;
	/**Use auto-import ?*/
	public static boolean USEFOLDERWATCHING;
	/**Show help tooltips ?*/
	public static boolean SHOWTOOLTIPS;
	/**Maximal number of user-extended attributes*/
	public static final int UFNUMBER=3;
	/**Vertical split of the main frame ?*/
	public static boolean VERTICAL;
	
	
	/**The configuration associated with this insatance of CheesyKM*/
	static Config config;
	/**RessourceBundle of all the localized Strings*/
	static ResourceBundle labels;
	
	
	/**
	*Main method, gets a global configuration, and initializes a CheesyKMGUI (main frame).
	*Sets XmlRpcs global parameters too.
	*/
	public static void main(String[] args){
		//Initialize a set of Strings (ResourceBundle) with the current Locale
		labels=ResourceBundle.getBundle("ressources.labels.Labels",Locale.getDefault());
		
		//Load a global configuration
		config=new Config();
		config.loadConfig();
		
		if(!USEJAVALAF){
			try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e){e.printStackTrace();}
		}
		
		/*
		EASYKMROOT="https://www.easy-km.net/demo/";
		KEYSTOREPATH="./ressources/ksweb";
		KEYSTOREPASS="easykm";
		*/
		//Set XmlRpc global parameters
		//XmlRpc.setDebug(true);
		XmlRpc.setEncoding("UTF8");
		XmlRpc.setDefaultInputEncoding("UTF8");
		
		//the MinML parser is used by default by XmlRpc, uncomment this to use another parser (xerces, for example)
		//Parser has to be a SAX implementation
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
		ToolTipManager.sharedInstance().setInitialDelay(2000);
		ToolTipManager.sharedInstance().setDismissDelay(5000);
		//new main frame
		api=new CheesyKMGUI();
		new MemoryMonitor();
		watchFolder=new WatchFolder();
		
	}
	
	/**
	*Returns the full absolute path where CheesyKM has been installed.<br>
	*ASSUMING THAT CHEESYKM IS IN A DIRECT SUBDIRECTORY OF THE INSTALLATION DIRECTORY.
	*@return String representation of CheesyKMs installation absolute path.
	*/
	public static String getInstallationPath(){
		ClassLoader loader=CheesyKM.class.getClassLoader().getSystemClassLoader();
		URL url=null;
		try {
			url=loader.getResource("ressources/Hammer.gif");
		} catch(Exception e) {echo(e);}
		String path=url.getPath();
		path=path.replace('\u002F',System.getProperty("file.separator").charAt(0));
		path=path.substring(5,path.length()-23);
		path=toUnicode(path);
		if(path.charAt(0)=='\\'){
			path=path.substring(1);
		}
		StringTokenizer stk=new StringTokenizer(path,System.getProperty("file.separator"),true);
		String resu=new String();
		int tokens=stk.countTokens();
		for(int i=0;i<tokens-4;i++){
			resu=resu+stk.nextToken();
		}
		//echo("URL:"+url+"\nPATH:"+path+"\nRESU:"+resu);
		return resu;
	}
	
	/**
	*Converts a "%c3%20xx" String to "\u00c3\u0020xx" String.
	*@param toConvert String to convert.
	*@return converted String.
	*/
	public static String toUnicode(String toConvert){
		String temp=new String(toConvert);
		int i=temp.indexOf("%");
		while(i!=-1){
			String twoChars="00"+temp.substring(i+1,i+3);
			try{
				char newChar=(char)Integer.parseInt(twoChars,16);
				temp=temp.substring(0,i)+newChar+temp.substring(i+3);
			}catch(Exception e){echo(e);break;}
			i=temp.indexOf("%");
		}
		return temp;
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
	*Returns an ImageIcon from the <code>location</code> specified image file.<br>
	*Necessary to acces image resources within the jar file.
	*@param location path to the image file.
	*@return an ImageIcon from the specified image file.
	*/
	public static ImageIcon loadIcon(String location){
		ClassLoader loader=ClassLoader.getSystemClassLoader();
		URL url=null;
		try {
			url=loader.getResource(location.substring(2));
		} catch(Exception e) {echo(e);}
		ImageIcon ic=new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
		return ic;
	}
	
	/**
	 * Returns the appropriate ImageIcon for the filetype ftype
	 * @param ftype
	 * @return an ImageIcon representing the ftype
	 */
	public static ImageIcon loadTypeIcon(String ftype, boolean big){
		ClassLoader loader=ClassLoader.getSystemClassLoader();
		URL url=null;
		if (!big) { ftype = "m"+ftype; } 
		String iconpath = "ressources/"+ftype+".png"; 
		try {
			url=loader.getResource(iconpath);
		} catch(Exception e) {echo(e);}
		if (url == null){
			System.err.println("unknown type: "+ftype);
			return loadTypeIcon("unknown",big);
		}
		else {
			return new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
		}
		
	}
	
	public static ImageIcon loadTypeIcon(String ftype){
		return loadTypeIcon(ftype, false);
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
			System.setProperty("javax.net.ssl.keyStore",KEYSTOREPATH);
			System.setProperty("javax.net.ssl.keyStorePassword",KEYSTOREPASS);
			System.setProperty("javax.net.ssl.trustStore",KEYSTOREPATH);
			System.setProperty("javax.net.ssl.trustStorePassword",KEYSTOREPASS);
			System.setProperty("http.agent","CheesyKM"+" on "+System.getProperty("os.name"));
			SecureXmlRpcClient client = new SecureXmlRpcClient(EASYKMROOT+"webservices.php");
			return client;
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	*Calls the "getTopicMatrix" RPC method, and the "getConfig" RPC method if "getTopicMatrix" succeeded.
	*@return a Vector containing (in order) : <br>-a Hashtable (Tid -> topic name) (tNames)<br>-a Hashtable (Tid -> parent Tid )(1 si root)) (tRelations)<br>-a Vector of current users root Tid (tRoot).
	*/
	public static Vector getTopicMatrix(){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			Vector resu=(Vector)client().execute("getTopicMatrix",params);
			if(resu!=null){
				CheesyKM.easyKMConfig=(Hashtable)client().execute("getConfig",params);
			}
			return resu;
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return null;
		}catch(IOException ioe){
			System.out.print("\nerror:\n"+ioe);
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
	*Calls the "search" RPC method.
	*@param query search query
	*@return a Vector of document Hashtables.
	*/
	public static synchronized Vector search(Hashtable query){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
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
	*Calls the "getDocsInTopic" RPC method.
	*@param tid TopicID, as an int.
	*@return a Vector of document Hashtables.
	*/
	public static Vector getDocsInTopic(int tid){
		return getDocsInTopic("T"+tid);
	}
	
	/**
	*Calls the "registerDoc" RPC method.
	*@param metadata documents metadata;
	*@return a Vector [(struct) document, (int) indexed] where indexed == 1 if the document has been indexed, 0 else.
	*	or a list of already existing identical documents
	*/
	public static synchronized Vector registerDoc(Hashtable metadata){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(metadata);
			return (Vector)client().execute("registerDoc",params);
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
	*Calls the "updateDoc" RPC method.
	*@param metadata documents metadata;
	*@return an Integer==0 if the update failed, a document Hashtable else.
	*/
	public static synchronized Object updateDoc(Hashtable metadata){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(metadata);
			return client().execute("updateDoc",params);
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
	*Calls the "getLastDocs" RPC method.
	*@return a Vector of the last CheesyKM.NOMBREDENOUVEAUTES document Hashtables.
	*/
	public static Vector getLastDocs(){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(new Integer(CheesyKM.NOMBREDENOUVEAUTES));
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
		if(tid==1){
			return CheesyKM.getLabel("root");
		} else {
			return getTopicNameById("T"+tid);
		}
	}
	
	/**
	*Called at disconnect, clears all session-relative informations.
	*/
	public static void deconnecter(){
		setLogin(null,null);
		tNames=null;
		maximumRightLevel=0;
		tRelations=null;
		tRights=null;
		rootTopics=null;
		topicsInMem=new Vector();
		docsInMem=new Vector();
		easyKMConfig=null;
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
			String titleValid=new String();
			for(int i=0;i<d.title.length();i++){
				if(Character.isLetterOrDigit(d.title.charAt(i))||Character.isSpaceChar(d.title.charAt(i))){
					titleValid=titleValid+d.title.charAt(i);
				}
			}
			String nomFic=FileChooserDialog.showChooser(api,getLabel("saveFileAs"),new FiltreFics("."+d.format),true,titleValid+"."+d.format);
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
	
	/**
	*Returns the same result than the "getTopicMatrix" RPC method.
	*@return Vector, same result than the "getTopicMatrix" RPC method.
	*/
	static Vector topicMatrix(){
		Vector v=new Vector();
		v.add(CheesyKM.getTNames());
		v.add(CheesyKM.tRelations);
		v.add(CheesyKM.tRights);
		v.add(CheesyKM.rootTopics);
		return v;
	}
	
	
	/**
	*Returns todays date as a "dd-MM-yyyy" String.
	*@return todays date as a "dd-MM-yyyy" String.
	*/
	public static String today(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(Calendar.getInstance().getTime());
	}
	
	/**
	*@param date date to convert
	*@return "yyyy-MM-dd" date.
	*/
	public static String dateToEasyKM(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		return dateToEasyKM(sdf.format(date));
	}
	
	/**
	*Returns the date x month beefore today as a "yyyy-MM-dd" String.
	*@return the date x month beefore today as a "yyyy-MM-dd" String.
	*/
	public static String sinceXMonth(int x){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar today=Calendar.getInstance();
		today.add(Calendar.MONTH,-x);
		return sdf.format(today.getTime());
	}
	
	/**
	*Checks if a String represents a "dd-MM-yyyy" date or not.
	*@param date the <code>String</code> to check.
	*@return <code>true</code> if date is a date, <code>false</code> else.
	*/
	public static boolean isDate(String date){
		return date.matches("\\d\\d-\\d\\d-\\d\\d\\d\\d");
	}
	
	/**
	*@param date "dd-MM-yyyy" date to convert
	*@return "yyyy-MM-dd" date.
	*/
	public static String dateToEasyKM(String date){
		if(date.equals("")) return "";
		return date.substring(6)+"-"+date.substring(3,5)+"-"+date.substring(0,2);
	}
	
	/**
	*@param date "yyyy-MM-dd hh:mm:ss" date to convert
	*@return "dd-MM-yyyy" date.
	*/
	public static String dateFromEasyKM(String date){
		if(date==null)return "";
		if(date.equals("")) return "";
		return date.substring(8,10)+"-"+date.substring(5,7)+"-"+date.substring(0,4);
	}
	
	public static boolean upload(String localFilename,String remoteFilename){
		return upload(localFilename,remoteFilename,true);
	}
	
	/**
	*Uploads a file on the specified FTP server, in remote folder "/incoming"
	*@param localFilename path to the file to upload.
	*@param remoteFilename name of the file on the remote FTP server. (will be "/incoming/remoteFilename").
	*@return <code>true</code> if the uipload successed, <code>false</code> else.
	*/
	public static boolean upload(String localFilename,String remoteFilename,boolean showProgBar){
		
		class Runner extends Thread{
			FTPUploadMonitor monitor;
			String localFilename,remoteFilename;
			boolean success;
			Runner(FTPUploadMonitor monitor,String localFilename,String remoteFilename){
				this.monitor=monitor;
				this.localFilename=localFilename;
				this.remoteFilename=remoteFilename;
			}
			public void run(){
				try{
					//echo("FTP UPLOAD FILE:"+localFilename+" AS REMOTE:"+remoteFilename);
					FTPClient ftpClient=new FTPClient();
					ftpClient.setProgressMonitor(monitor);
					ftpClient.setRemoteHost(CheesyKM.FTPHOST);
					ftpClient.setControlPort(21);
					ftpClient.connect();
					ftpClient.login(CheesyKM.FTPUSERNAME,CheesyKM.FTPPASS);
					ftpClient.setType(FTPTransferType.BINARY);
					ftpClient.chdir("incoming");
					//echo("put start");
					ftpClient.put(localFilename,remoteFilename);
					//echo("put end");
					ftpClient.quit();
					success=true;
					if(monitor!=null)monitor.dispose();
				} catch(IOException ioe){
					success=false;
					JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
					if(monitor!=null)monitor.dispose();
				} catch(FTPException ioe){
					success=false;
					JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorFTP"), JOptionPane.ERROR_MESSAGE);
					if(monitor!=null)monitor.dispose();
				}
			}
			public boolean success(){
				while(this.isAlive()){
					try{Thread.sleep(100);}catch(InterruptedException e){}
				}
				return success;
			}
			
		}
		FTPUploadMonitor monitor=null;
		if(showProgBar) monitor=new FTPUploadMonitor(new File(localFilename).length());
		Runner runner=new Runner(monitor,localFilename,remoteFilename);
		runner.start();
		if(monitor!=null)monitor.show();
		return runner.success();
		
	}
		
		
	/**
	*Calls the "deleteDoc" RPC method.
	*@param doc the doc to delete.
	*/
	public static void deleteDoc(Doc doc){
		new DeleteDoc(doc);
	}
	
	/**
	*Calls the "deleteDoc" RPC method.
	*@param doc the doc to delete.
	*@return <code>true</code> if the Doc has successfully been deleted, <code>false</code> else.
	*/
	public static synchronized boolean deleteDocRPC(final Doc doc){
		class Runner extends Thread{
			boolean status;
			Vector params;
			ProgBarDialog pbd;
			Runner(Vector params,ProgBarDialog pbd){
				this.params=params;
				this.pbd=pbd;
				this.start();
			}
			public void run(){
				try{	
					Hashtable resu=(Hashtable)client().execute("deleteDocs",params);
					status=Integer.parseInt(resu.get("Doc "+doc.id).toString())>0;
					if(status){
						Vector toUpdate=new Vector();
						for(int i=0;i<doc.topicList.size();i++){
							toUpdate.add(new Integer(doc.topicList.get(i).toString().substring(1)));
						}
						api.modifiedTopics(toUpdate,doc.id);
					}
				}catch(MalformedURLException mue){
					JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}catch(XmlRpcException xre){
					JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}catch(IOException ioe){
					JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}
				pbd.dispose();
			}
		}
		Vector params=new Vector();
		params.add(login);
		params.add(pass);
		Vector docIDs=new Vector();
		docIDs.add(new Integer(doc.id));
		params.add(docIDs);
		ProgBarDialog pbd=new ProgBarDialog(api);
		pbd.setModal(true);
		Runner runner=new Runner(params,pbd);
		pbd.show();
		return runner.status;
	}
	
	/**
	*Calls the "rollbackDoc" RPC method.
	*@param doc the doc to rollback.
	*@return <code>true</code> if the Doc has successfully been rollbackd, <code>false</code> else.
	*/
	public static synchronized boolean rollbackDocRPC(final Doc doc){
		class Runner extends Thread{
			boolean status;
			Vector params;
			ProgBarDialog pbd;
			Runner(Vector params,ProgBarDialog pbd){
				this.params=params;
				this.pbd=pbd;
				this.start();
			}
			public void run(){
				try{	
					Integer resu=(Integer)client().execute("rollbackDoc",params);
					status=resu.intValue()>0;
					if(status){
						Vector toUpdate=new Vector();
						for(int i=0;i<doc.topicList.size();i++){
							toUpdate.add(new Integer(doc.topicList.get(i).toString().substring(1)));
						}
						api.modifiedTopics(toUpdate,doc.id);
					}
				}catch(MalformedURLException mue){
					JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}catch(XmlRpcException xre){
					JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}catch(IOException ioe){
					JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
					status=false;
				}
				pbd.dispose();
			}
		}
		Vector params=new Vector();
		params.add(login);
		params.add(pass);
		params.add(new Integer(doc.id));
		ProgBarDialog pbd=new ProgBarDialog(api);
		pbd.setModal(true);
		Runner runner=new Runner(params,pbd);
		pbd.show();
		return runner.status;
	}
	
	/**
	*Calls the "rateDoc" RPC method.
	*@param doc The Document to rate.
	*@param newRating the new rating for this doc.
	*/
	public static int rateDoc(Doc doc,int newRating){
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(new Integer(doc.id));
			params.add(new Integer(newRating));
			Integer resu=(Integer)client().execute("rateDoc",params);
			return resu.intValue();
		}catch(MalformedURLException mue){
			JOptionPane.showMessageDialog(null, getLabel("error")+mue, getLabel("errorInWSURL"), JOptionPane.ERROR_MESSAGE);
			return doc.score;
		}catch(XmlRpcException xre){
			JOptionPane.showMessageDialog(null, getLabel("error")+xre, getLabel("errorXMLRPC"), JOptionPane.ERROR_MESSAGE);
			return doc.score;
		}catch(IOException ioe){
			JOptionPane.showMessageDialog(null, getLabel("error")+ioe, getLabel("errorIO"), JOptionPane.ERROR_MESSAGE);
			return doc.score;
		}
	}
	
	/**
	*Calls the "addTopic" RPC method.
	*@param parentTid the tid of the parent of the topic to create, as a String ("TXX").
	*@param name name of the topic to create.
	*/
	public synchronized static String createTopic(String parentTid,String name){
		//echo("CREATING TOPIC:"+name+" IN PARENT TID:"+parentTid);
		try{
			Vector params=new Vector();
			params.add(login);
			params.add(pass);
			params.add(parentTid);
			params.add(name);
			Object resu=client().execute("addTopic",params);
			if(String.class.isInstance(resu)){
				//echo("SUCCESS:"+resu.toString());
				return resu.toString();
			} else {
				//echo("ECHEC");
				return "ERROR";
			}
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
	
}
