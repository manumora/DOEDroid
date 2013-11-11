package manumora.doedroid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSHandler extends DefaultHandler {
	
	final int state_unknown = 0;
	final int state_title = 1;
	final int state_description = 2;
	final int state_link = 3;
	final int state_pubdate = 4;
	final int state_summary = 5;
	
	int currentState = state_unknown;
    StringBuffer buffer;
    String link;
    boolean summaryState=false;
    
	RSSFeed feed;
	RSSItem item;
	
	boolean itemFound = false;
	
	RSSHandler(){}
	
	RSSFeed getFeed(){
		return feed;
	}

	@Override
	public void startDocument() throws SAXException {
		feed = new RSSFeed();
		item = new RSSItem();
	}

	@Override
	public void endDocument() throws SAXException {}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		if (localName.equalsIgnoreCase("item")){
			itemFound = true;
			item = new RSSItem();
			currentState = state_unknown;
		}else if (localName.equalsIgnoreCase("entry")){
			itemFound = true;
			item = new RSSItem();
			currentState = state_unknown;
		}else if (localName.equalsIgnoreCase("title")){
			currentState = state_title;
		}else if (localName.equalsIgnoreCase("description")){
			currentState = state_description;
		}else if (localName.equalsIgnoreCase("summary")){
		    buffer = new StringBuffer();
		    summaryState=true;
			currentState = state_summary;
		}else if (localName.equalsIgnoreCase("link")){
			link="";
	        for (int i=0; i<atts.getLength(); i++) {
	            //String name = atts.getQName(i);
	            String value = atts.getValue(i);
	            //String nsUri = atts.getURI(i);
	            String lName = atts.getLocalName(i);
	            if(lName=="href"){
	            	link = value;
	            }
	        }
			currentState = state_link;
		}else if (localName.equalsIgnoreCase("pubdate")){
			currentState = state_pubdate;
		}else if (localName.equalsIgnoreCase("updated")){
			currentState = state_pubdate;
		}else{
			currentState = state_unknown;
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub
		if (localName.equalsIgnoreCase("item")){
			feed.addItem(item);
		}else if (localName.equalsIgnoreCase("entry")){
			feed.addItem(item);
		}else if (localName.equalsIgnoreCase("summary")){

			summaryState=false;
			
			String code = buffer.toString();
			code = code.replace("<br/>", "\n");
			code = code.replace("<br />", "");
			code = code.replace("<p style=\"text-align: justify;\">", "");
			code = code.replace("<p>", "");			
			code = code.replace("</p>", "");	
			code = code.replace("&ldquo;", "\"");
			code = code.replace("&rdquo;", "\"");
			code = code.replace("&aacute;", "á");			
			code = code.replace("&eacute;", "é");
			code = code.replace("&iacute;", "í");
			code = code.replace("&oacute;", "ó");
			code = code.replace("&uacute;", "ú");
			code = code.replace("&Aacute;", "Á");			
			code = code.replace("&Eacute;", "É");
			code = code.replace("&Iacute;", "Í");
			code = code.replace("&Oacute;", "Ó");
			code = code.replace("&Uacute;", "Ú");
			code = code.replace("&ntilde;", "ñ");			
			code = code.replace("&Ntilde;", "Ñ");			
			code = code.replace("</a>", "");
			code = code.replace("Documento descargable", "");
			Pattern patron = Pattern.compile("<a .+>");
			Matcher encaja = patron.matcher(code);
			code = encaja.replaceAll("");

			item.setDescription(code);
		}
	}


	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		String strCharacters = new String(ch,start,length);

		if (itemFound==true){
		// "item" tag found, it's item's parameter
			if(summaryState==true){
				buffer.append(strCharacters);
			}else{
				switch(currentState){
					case state_title:
						item.setTitle(strCharacters);
						break;
					case state_description:
						item.setDescription(strCharacters);
						break;
					case state_link:
						if(link!=""){
							item.setLink(link);
						}else{
							item.setLink(strCharacters);
						}
						break;
					case state_pubdate:
						item.setPubdate(strCharacters.substring(0,10));
						break;	
					default:
						break;
				}
			}
		}
		else{
		// not "item" tag found, it's feed's parameter
			switch(currentState){
				case state_title:
					feed.setTitle(strCharacters); 
					break;
				case state_description: 
					feed.setDescription(strCharacters); 
					break;
				case state_link: 
					feed.setLink(strCharacters); 
					break;
				case state_pubdate: 
					feed.setPubdate(strCharacters); 
					break;	
				default: break;
			}
		}		
		currentState = state_unknown;
	}
}