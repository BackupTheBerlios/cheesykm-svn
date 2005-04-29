
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
