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
import java.awt.*;
/**
*Monitors a file transfer, and updates its associated progress bar.
*/
class ProgressBarMonitor{
	/**Total file size*/
	int total;
	/**Current transferred size*/
	int actuel;
	/**Transfer in progress ?*/
	boolean isRunning=true;
	/**Associated progress bar*/
	JProgressBar bar;
	/**destination file name*/
	String fileName;
	/**Associated {@link Download}*/
	Download d;
	/**Associated {@link FileTransferTopic}*/
	FileTransferTopic topic;
	/**
	*New ProgressBarMonitor.
	*@param total Total file size.
	*@param fileName Destination file name.
	*@param d Associated {@link Download}.
	*/
	ProgressBarMonitor(int total,String fileName,Download d){
		this.total=total;
		this.d=d;
		this.actuel=0;
		this.bar=new JProgressBar(0,total);
		bar.setStringPainted(true);
		bar.setIndeterminate(true);
		bar.setPreferredSize(new Dimension(200,50));
		this.fileName=fileName;
		topic=new FileTransferTopic(fileName,this);
		CheesyKM.api.displayTopic(topic);
		
	}
	/**
	*Updates the progress of the transfer, and the progress bar.
	*@param actuel Current transferred size.
	*/
	public void setValue(int actuel){
		bar.setIndeterminate(false);
		this.actuel=actuel;
		bar.setValue(actuel);
		bar.setString(new Float(((float)((actuel+0.0)/(total+0.0)))*100).intValue()+"%");
		if(actuel>=total){
			isRunning=false;
			topic.button.setText(CheesyKM.getLabel("closeTab"));
		}
	}
	
}
