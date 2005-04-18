import java.util.*;

/**
*Thread to launch the RPC method "getDocsInTopic" in background.<br>
*Called by the Topic tree View, to get the content of simple topics (type='T')
*/
class BGDocCount extends Thread {
	private Vector toCount;
	private boolean expand;
	/**
	*Initialize and launch a BGDocCount Thread.
	*@param toCount <code>Vector</code> of {@link Topic} to fill the content of (from EasyKM).
	*@param expand <code>boolean</code> expand the Topics in the Topic tree view if true.
	*/
	BGDocCount(Vector toCount,boolean expand){
		this.toCount=toCount;
		this.expand=expand;
		start();
	}
	
	/**
	*Initialize and launch a BGDocCount Thread.
	*@param topic {@link Topic} to fill the content of (from EasyKM).
	*@param expand <code>boolean</code> expand the Topics in the Topic tree view if true.
	*/
	BGDocCount(Topic topic,boolean expand){
		this.toCount=new Vector();
		this.expand=expand;
		toCount.add(topic);
		start();
	}
	
	/**
	*fills the content of this BGDocCounts Topics.
	*/
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
