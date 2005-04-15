import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;

/**
*Thread gérant le nombre maximum de documents en mémoire.<br>
*Lancé au démarrage du programme, vérifie toutes les x sec. que le nombre de documents maxi (CheesyKM.MAXDOCSINMEM) n'est pas atteint.
*Si il est atteint, on décharge les documents qui ne sont pas actuellement visibles dans l'arborescence thématique.
*Si CheesyKM.MAXDOCSINMEMBEFOREAUTOCOLLAPSE est atteint et CheesyKM.AUTOCOLLAPSE est true, alors des documents seront déchargés de la mémoire, même si ils étaient visibles. (l'arborescence sera repliée).
*/
class MemoryMonitor extends Thread{
	/**
	*Initialise et lance un MemoryMonitor.
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
