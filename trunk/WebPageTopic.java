
/**
*Special {@link Topic} to display a webpage in a tab.
*/
class WebPageTopic extends Topic{
	/**URL of the counter for this page*/
	String counterURL;
	/**URL of the page to browse*/
	String realURL;
	/**
	*Creates a new WebPageTopic (id = -1,node type = 'W')
	*@param counterURL URL of the counter for this page.
	*@param realURL URL of the page to browse.
	*/
	WebPageTopic(String counterURL,String realURL){
		super(-1);
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
