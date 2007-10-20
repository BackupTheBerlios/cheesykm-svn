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
*Special {@link Topic} to display a webpage in a tab.
*/
class WebPageTopic extends Topic{
	/**URL of the counter for this page*/
	String counterURL;
	/**URL of the page to browse*/
	String realURL;
	/**
	*Creates a new WebPageTopic (id = -x,node type = 'W')
	*@param counterURL URL of the counter for this page.
	*@param realURL URL of the page to browse.
	*/
	WebPageTopic(String counterURL,String realURL){
		super(CheesyKM.api.tabIndex--);
		this.counterURL=counterURL;
		this.realURL=realURL;
		this.setNodeType('W');
	}
	/**
	*Returns this webpages URL.
	*/
	public String toString(){
		return this.realURL;
	}
}
