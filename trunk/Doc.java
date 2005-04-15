import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
*Représente un document.<br>
*Un document possède les propriétés suivantes:
*id -> (int) document id#
*topics -> (array of string) list of topic ids#
*description -> (string) description#
*author -> (string)#
*creadate -> (string) if set, original document creation date#
*title -> (string)#
*kwords -> (string) whitespace or comma separated words#
*user -> (string) full name of user who transmitted the document#
*date -> (string) date of transmission#
*editdate -> (string) if set, last modification date since creation#
*editor -> (string) if set, full name of last editor#
*url -> (string) if set, url associated with entry#
*visits -> (int) if set, number of visits to url#
*file -> (string) if set, id  of associated file#
*size -> (int) if set, size of file in bytes#
*ftype -> (string) if set, file type#
*format -> (string) if set, file format#
*downloads -> (int) if set, number of associated file downloads#
*score -> (int) rating#
*expires -> (string) expiration date or empty
*version -> (int) edition if applicable#
*ufnameX -> (string) user extended attribute name with X in [0,1,2] (maybe empty)
*ufvalueX ->(string) user extended attribute value with X in [0,1,2] (maybe empty)
*/
public class Doc extends Topic{
	public String description,author,creadate,title,kwords,user,date,editdate,editor,url,file,ftype,format,expires;
	public Hashtable uf;
	public Vector topicList;
	public int visits,size,downloads, score, version;
	
	Topic parent;
	
	Doc(){
		super(-2);
	}
	
	/**
	*Construit un Doc.
	*@param data Hashtable document résultat d'une requête RPC.
	*@param parent Topic parent de ce doc dans l'arborescence thématique affichée.
	*/
	Doc(Hashtable data,Topic parent){
		super(((Integer)data.get("id")).intValue());
		this.parent=parent;
		this.hasBeenCount=true;
		topicList=(Vector)data.get("topics");
		description=(String)data.get("description");
		author=(String)data.get("author");
		creadate=(String)data.get("creadate");
		title=(String)data.get("title");
		kwords=(String)data.get("kwords");
		user=(String)data.get("user");
		date=(String)data.get("date");
		editdate=(String)data.get("editdate");
		editor=(String)data.get("editor");
		url=(String)data.get("url");
		visits=((Integer)data.get("visits")).intValue();
		file=(String)data.get("file");
		size=((Integer)data.get("size")).intValue();
		ftype=(String)data.get("ftype");
		format=(String)data.get("format");
		downloads=((Integer)data.get("downloads")).intValue();
		score=((Integer)data.get("score")).intValue();
		expires=(String)data.get("expires");
		version=(new Integer((String)data.get("version"))).intValue();
		uf=new Hashtable();
		uf.put(data.get("ufname0"),data.get("ufvalue0"));
		uf.put(data.get("ufname1"),data.get("ufvalue1"));
		uf.put(data.get("ufname2"),data.get("ufvalue2"));
	}
	
	public Topic getParent(){return this.parent;}
	
	public int docCount(boolean b){
		return -1;
	}
	
	public String toString(){
		return title;
	}
	
	/**
	*Retourne le texte à afficher dans le tooltip (info-bulle) de ce document.
	*/
	public String getToolTip(){
		String resu=new String();
		resu="<html>"+toString()+"<br>";
		if(!editdate.equals("")){resu=resu+CheesyKM.getLabel("modifiedAt")+editdate.substring(0,10)+CheesyKM.getLabel("by")+user+"<br>";}else if(!creadate.equals("")) {resu=resu+CheesyKM.getLabel("createdAt")+creadate.substring(0,10)+CheesyKM.getLabel("by")+user+"<br>";}
		if (!author.equals("")) resu=resu+CheesyKM.getLabel("author")+author+"<br>";
		if(!format.equals("")&&!format.equals("000")){
			resu=resu+CheesyKM.getLabel("downloaded")+downloads+CheesyKM.getLabel("times")+"("+format+")"+"<br>";
		}
		resu=resu+CheesyKM.getLabel("rating")+score;
		if(version!=1){resu=resu+"<br>"+CheesyKM.getLabel("version")+version;}
		return resu;
	}
	
	public String getFType(){return this.ftype;}
	
	public String getFSize(){
		String taille;
		if(size>1024*1024){
			taille=String.valueOf(size/(1024*1024))+CheesyKM.getLabel("mByte");
		} else if (size>1024){
			taille=String.valueOf(size/(1024))+CheesyKM.getLabel("kByte");
		} else {
			taille=String.valueOf(size)+CheesyKM.getLabel("byte");
		}
		return taille;
	}
	
	public char getNodeType(){
		return 'D';
	}
}
