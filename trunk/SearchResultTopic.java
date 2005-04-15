import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

class SearchResultTopic extends Topic{
	boolean isInResults=false;
	SearchResultTopic(int id){
		super(id);
		this.hasBeenCount=true;
	}
	
	public int docCount(boolean expand){
		if(this.docs!=null){
			return this.docs.size();
		} else {
			return 0;
		}
	}
	
	public void decharger(){
		
	}
}
