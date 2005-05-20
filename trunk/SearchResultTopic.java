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
