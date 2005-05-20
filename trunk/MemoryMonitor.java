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
*Thread managing the number of docs loaded in memory.<br>
*Launched at CheesyKMs start, check each 5sec. if {@link CheesyKM#MAXDOCSINMEM} is reached. 
*It it is, Docs loaded in memory and not currently visible (part of an expanded path) are unloaded.
*if CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE is reached and CheesyKM.AUTOCOLLAPSE is <code>true</code>, then the docs will be unloaded, even if they were displayed (topic tree view will collapse).
*/
class MemoryMonitor extends Thread{
	/**
	*Starts a MemoryMonitor.
	*/
	MemoryMonitor(){
		start();
	}
	public void run(){
		while(true){
			try {
				sleep(5000);
			} catch(Exception e){CheesyKM.echo(e);}
			//CheesyKM.echo("Docs:"+CheesyKM.docsInMem.size());
			if(CheesyKM.docsInMem.size()>CheesyKM.MAXDOCSINMEM){
				int i=0;
				while(i<CheesyKM.topicsInMem.size()&&CheesyKM.api.thematique.isVisible((new TreePath(((DefaultMutableTreeNode)(CheesyKM.topicsInMem.get(i))).getPath())))) i++;
				if(CheesyKM.docsInMem.size()>CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE&&CheesyKM.AUTOCOLLAPSE){
					//CheesyKM.echo("autocollapse :");
					i=0;
				}
				if(i==CheesyKM.topicsInMem.size()){
					//CheesyKM.echo("Rien de virable");
				} else {
					Topic topic=((Topic)((DefaultMutableTreeNode)CheesyKM.topicsInMem.get(i)).getUserObject());
					//CheesyKM.echo("Je vire "+topic);
					topic.decharger();
					//CheesyKM.topicsInMem.remove((DefaultMutableTreeNode)CheesyKM.topicsInMem.get(i));
					System.gc();
					//CheesyKM.echo("nouveauDocs:"+CheesyKM.docsInMem.size());
				}
			}
		}
	}
}
