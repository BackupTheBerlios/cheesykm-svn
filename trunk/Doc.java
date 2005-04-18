import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
*Represents a document.<br>
*A document is made of:<br>
*id -> (int) document id<br>
*topics -> (array of string) list of topic ids<br>
*description -> (string) description<br>
*author -> (string)<br>
*creadate -> (string) if set, original document creation date<br>
*title -> (string)<br>
*kwords -> (string) whitespace or comma separated words<br>
*user -> (string) full name of user who transmitted the document<br>
*date -> (string) date of transmission<br>
*editdate -> (string) if set, last modification date since creation<br>
*editor -> (string) if set, full name of last editor<br>
*url -> (string) if set, url associated with entry<br>
*visits -> (int) if set, number of visits to url<br>
*file -> (string) if set, id  of associated file<br>
*size -> (int) if set, size of file in bytes<br>
*ftype -> (string) if set, file type<br>
*format -> (string) if set, file format<br>
*downloads -> (int) if set, number of associated file downloads<br>
*score -> (int) rating<br>
*expires -> (string) expiration date or empty<br>
*version -> (int) edition if applicable<br>
*ufnameX -> (string) user extended attribute name with X in [0,1,2] (maybe empty)<br>
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
	*New Doc.
	*@param data document <code>Hashtable</code>, result of a Doc related RPC method.
	*@param parent parent {@link Topic} of this Doc in the displayed Topic tree view ({@link Thematique}).
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
	/**
	*@return parent {@link Topic} of this Doc in the displayed Topic tree view ({@link Thematique}).
	*/
	public Topic getParent(){return this.parent;}
	
	/**
	*Overrides Topics {@link Topic#docCount(boolean) docCount()}.
	*@return always -1.
	*/
	public int docCount(boolean b){
		return -1;
	}
	
	/**
	*@return the title of this Doc.
	*/
	public String toString(){
		return title;
	}
	
	/**
	*Returns the <code>String</code> to display in this Docs ToolTip.<br>
	*Contains, if set : editdate/creadate,user,author,downloads,format,score,version.
	*@return the text to display in the ToolTip of this Doc.
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
	
	/**
	*Return a Localized <code>String</code> representation of the size of this Docs file.
	*@return xxMb/Kb/b
	*/
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
	/**
	*Overrides Topics {@link Topic#getNodeType() getNodeType()}.
	*@return always 'D'.
	*/
	public char getNodeType(){
		return 'D';
	}
}
