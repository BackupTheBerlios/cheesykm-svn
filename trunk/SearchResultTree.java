import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
/**
*Search results tree, a reduced version of the topic tree view.
*/
class SearchResultTree extends JTree {
	/**
	*Builds a new search result tree, fully loaded in memory, fully expanded if {@link CheesyKM#EXPANDSEARCHRESULT} is <code>true</code>, fully collapsed else, and i'm fully fed up with that doc.
	*/
	SearchResultTree(Vector searchResult){
		super();
		//CheesyKM.echo("SR:"+searchResult);
		if(searchResult!=null){
			if(searchResult.size()==0) searchResult=null;
		}
		if(searchResult!=null){
			Vector docs=new Vector();
			Vector nodes=new Vector();
			Hashtable topicsToDisplay=new Hashtable();
			SearchResultTopic racineT=new SearchResultTopic(1);
			DefaultMutableTreeNode racine= new DefaultMutableTreeNode(racineT);
			nodes.add(racine);
			racineT.setNode(racine);
			topicsToDisplay.put(new Integer(1),racineT);
			for(int i=0;i<searchResult.size();i++){
				Doc d=new Doc((Hashtable)searchResult.get(i),null);
				//CheesyKM.echo("Traitement du DOC:"+d);
				//DefaultMutableTreeNode n=new DefaultMutableTreeNode();
				//d.setNode(n);
				//n.setUserObject(d);
				docs.add(d);
				for(int k=0;k<d.topicList.size();k++){
					//CheesyKM.echo("Traitement de son theme:"+CheesyKM.getTopicNameById(d.topicList.get(k).toString()));
					if(!topicsToDisplay.containsKey(new Integer(((String)d.topicList.get(k)).replaceAll("T","")))){
						SearchResultTopic srt=new SearchResultTopic(Integer.parseInt(((String)d.topicList.get(k)).replaceAll("T","")));
						srt.setNodeType('T');
						DefaultMutableTreeNode tn=new DefaultMutableTreeNode();
						nodes.add(tn);
						srt.setNode(tn);
						tn.setUserObject(srt);
						DefaultMutableTreeNode n=new DefaultMutableTreeNode(d);
						nodes.add(n);
					//	CheesyKM.echo("ajout de "+n+" à "+tn+"+CREATION");
						tn.add(n);
						topicsToDisplay.put(new Integer(((String)d.topicList.get(k)).replaceAll("T","")),srt);
					} else {
						SearchResultTopic parent=(SearchResultTopic)topicsToDisplay.get(new Integer(((String)d.topicList.get(k)).replaceAll("T","")));
						DefaultMutableTreeNode newNode=new DefaultMutableTreeNode(d);
						nodes.add(newNode);
						parent.getNode().add(newNode);
					//	CheesyKM.echo("ajout de "+newNode+" à "+parent+"+AJOUT SIMPLE");
					}
				}
			}
			Enumeration keys=topicsToDisplay.keys();
			while(keys.hasMoreElements()){
				Integer topicID=(Integer)keys.nextElement();
				//CheesyKM.echo("Traitement du topic:"+CheesyKM.getTopicNameById(topicID.intValue()));
				boolean linked=false;
				while(!linked&&!(topicID.equals(new Integer(0))||topicID.equals(new Integer(1)))){
					Integer parentTopicID=new Integer((CheesyKM.tRelations.get("T"+topicID)).toString().replaceAll("T",""));
				//	CheesyKM.echo("Traitement du topic parent:"+CheesyKM.getTopicNameById(parentTopicID.intValue()));
					if(parentTopicID.equals(new Integer(0))) parentTopicID=new Integer(1);
					if(!topicsToDisplay.containsKey(parentTopicID)){
						SearchResultTopic srt=new SearchResultTopic(parentTopicID.intValue());
						srt.setNodeType('P');
						DefaultMutableTreeNode node=new DefaultMutableTreeNode();
						nodes.add(node);
						srt.setNode(node);
						node.setUserObject(srt);
						topicsToDisplay.put(parentTopicID,srt);
				//		CheesyKM.echo("ajout de "+((SearchResultTopic)topicsToDisplay.get(topicID)).getNode()+" à "+node);
						node.add(((SearchResultTopic)topicsToDisplay.get(topicID)).getNode());
					} else {
						SearchResultTopic parent=(SearchResultTopic)topicsToDisplay.get(parentTopicID);
						SearchResultTopic child=(SearchResultTopic)topicsToDisplay.get(topicID);
						parent.getNode().add(child.getNode());
						parent.setNodeType('P');
				//		CheesyKM.echo("ajout de  "+child+" à "+parent);
						linked=true;
					}
					topicID=parentTopicID;
				}
			}
			
			DefaultTreeModel treeModel=new DefaultTreeModel(((SearchResultTopic)topicsToDisplay.get(new Integer(1))).getNode());
			setModel(treeModel);
			setRootVisible(false);
			setShowsRootHandles(true);
			setExpandsSelectedPaths(false);
			setToggleClickCount(CheesyKM.DEFAULTACTIONCLICKCOUNT);
			
			ToolTipManager.sharedInstance().registerComponent(this);
			TopicTreeCellRenderer ttcr=new TopicTreeCellRenderer();
			setCellRenderer(ttcr);
			
			
			MouseListener ml = new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					int selRow = SearchResultTree.this.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = SearchResultTree.this.getPathForLocation(e.getX(), e.getY());
					
					if(selRow != -1) {
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)selPath.getLastPathComponent();
						Topic topic=(Topic)node.getUserObject();
						if(e.getClickCount() == CheesyKM.DEFAULTACTIONCLICKCOUNT&&e.getButton()==MouseEvent.BUTTON1) {
							if(topic.getNodeType()=='D'){
								CheesyKM.api.displayTopic(topic);
							}
						}
					}
				}
				
				public void mouseClicked(MouseEvent e){
					int selRow = SearchResultTree.this.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = SearchResultTree.this.getPathForLocation(e.getX(), e.getY());
					
					if(selRow != -1&&e.getButton()==MouseEvent.BUTTON3) {
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)selPath.getLastPathComponent();
						Topic topic=(Topic)node.getUserObject();
						//CheesyKM.echo(topic);
						SearchResultTree.this.setSelectionPath(selPath);
						new TopicPopupMenu(e.getComponent(),e.getX(),e.getY(),topic,true);
					}
				}
			};
			addMouseListener(ml);
			if(CheesyKM.EXPANDSEARCHRESULT){
				for(int i=0;i<nodes.size();i++){
					this.expandPath(new TreePath(((DefaultMutableTreeNode)nodes.get(i)).getPath()));
				}
			}
			
		} else {
			class EmptySearchResultTopic extends Doc{
				EmptySearchResultTopic(){
					this.title=CheesyKM.getLabel("noResults");
					this.id=-2;
				}
			}
			DefaultMutableTreeNode empty=new DefaultMutableTreeNode(new EmptySearchResultTopic());
			DefaultTreeModel treeModel=new DefaultTreeModel(empty);
			setModel(treeModel);
		}
		//CheesyKM.echo("resultat de recherche terminé");
	}
	/**
	*Overrides JTrees getToolTipText, and uses {@link Thematique}s one.
	*/
	public String getToolTipText(MouseEvent e) {
		return new Thematique().getToolTipText(e);
	}
}
