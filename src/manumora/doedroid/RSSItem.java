package manumora.doedroid;

public class RSSItem {
	
	private String title = null;
	private String description = null;
	private String link = null;
	private String pubdate = null;
	
	RSSItem(){}
	
	RSSItem(String _title, String _description, String _link, String _pubdate){
		title = _title;
		description = _description;
		link = _link;
		pubdate = _pubdate;
	}
	
	void setTitle(String value){
		title = value;
	}
	
	void setDescription(String value){
		description = value;
	}
	
	void setLink(String value){
		link = value;
	}
	
	void setPubdate(String value){
		pubdate = value;
	}
	
	String getTitle(){
		return title;
	}
	
	String getDescription(){
		return description;
	}
	
	String getLink(){
		return link;
	}
	
	String getPubdate(){
		return pubdate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return title;
	}
}