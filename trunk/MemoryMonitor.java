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
