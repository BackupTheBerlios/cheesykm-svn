

class WebPageTopic extends Topic{
	String counterURL;
	String realURL;
	WebPageTopic(String counterURL,String realURL){
		super(-1);
		this.counterURL=counterURL;
		this.realURL=realURL;
		this.setNodeType('W');
	}
	public String toString(){
		return this.realURL;
	}
}
