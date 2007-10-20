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
import javax.swing.*;
/**
*Special {@link Topic}, to display informations about a file transfer in the right tabbed pane.
*/
class FileTransferTopic extends Topic{
	/**path to the destination file*/
	String fileName;
	/**Associated {@link ProgressBarMonitor}*/
	ProgressBarMonitor pi;
	/**Associated "Abort transfer" button*/
	JButton button;
	/**
	*Creates a new FileTransferTopic (id=-1,nodeType='F')
	*@param fileName Path to the destination file.
	*@param pi Associated {@link ProgressBarMonitor}.
	*/
	FileTransferTopic(String fileName,ProgressBarMonitor pi){
		super(-1);
		this.setNodeType('F');
		this.fileName=fileName;
		this.pi=pi;
		this.button=null;
	}
	
	/**
	*@return the filename of this FileTransferTopic.
	*/
	public String toString(){
		return this.fileName;
	}
}
