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
/**
*Special {@link Topic} to display the advanced search form in a tab.
*/
class AdvancedSearchTopic extends Topic{
	
	/**
	*Creates a new AdvancedSearchTopic (id = -x,node type = 'A')
	*/
	AdvancedSearchTopic(){
		super(CheesyKM.api.tabIndex--);
		this.setNodeType('A');
	}
	/**
	*Returns the title of the tab.
	*/
	public String toString(){
		return CheesyKM.getLabel("advancedSearch");
	}
}
