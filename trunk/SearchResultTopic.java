import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
/**
*Special {@link Topic} displayed in the {@link SearchResultTree}.<br>
*These topics are "special" because the search result tree is a reduced version of the topic tree view, and is not subject to memory management (it is always fully loaded in memory, be it collapsed or expanded).
*/
class SearchResultTopic extends Topic{
	boolean isInResults=false;
	/**
	*New SearchResultTopic.
	*@param id topic ID as an <code>int</code>
	*/
	SearchResultTopic(int id){
		super(id);
		this.hasBeenCount=true;
	}
	
	/**
	*Overrides Topics {@link Topic#docCount(boolean)}
	*/
	public int docCount(boolean expand){
		if(this.docs!=null){
			return this.docs.size();
		} else {
			return 0;
		}
	}
	
	/**
	*Overrides Topics {@link Topic#decharger()}
	*/
	public void decharger(){
		
	}
}
