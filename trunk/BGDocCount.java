import java.util.*;

/**
*Thread lançant en tâche de fond des requêtes RPC "getDocsInTopic".<br>
*Utilisé par l'arbre thématique global, pour ne pas bloquer le programme lors de l'affichage dans l'arbre de topics simples (sans sous-topics).
*/
class BGDocCount extends Thread {
	private Vector toCount;
	private boolean expand;
	/**
	*Initialise et lance un Thread BGDocCount.
	*@param toCount Vector de Topics à récupérer.
	*/
	BGDocCount(Vector toCount,boolean expand){
		this.toCount=toCount;
		this.expand=expand;
		start();
	}
	
	/**
	*Initialise et lance un Thread BGDocCount.
	*@param topic Topic à récupérer.
	*/
	BGDocCount(Topic topic,boolean expand){
		this.toCount=new Vector();
		this.expand=expand;
		toCount.add(topic);
		start();
	}
	
	public void run(){
		for(int i=0;i<toCount.size();i++){
			if(!((Topic)(toCount.get(i))).isCounting){
				((Topic)(toCount.get(i))).docCount(expand);
				//CheesyKM.echo("fini de scanner "+toCount.get(i));
				if(CheesyKM.api.thematique!=null) CheesyKM.api.thematique.repaint();
			}
		}
	}
}
