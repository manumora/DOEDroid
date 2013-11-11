package manumora.doedroid;

public class Favourites {
	
	int _id;
	String _name;
	String _url;
	String _date;
	
	public Favourites(String Name,String date,String URL){
		this._name=Name;
		this._url=URL;
		this._date=date;
	}
	
	public int getID(){
		return this._id;
	}

	public String getName(){
		return this._name;
	}

	public String getDate(){
		return this._date;
	}
	
	public String getURL(){
		return this._url;
	}
	
	public void SetID(int ID){
		this._id=ID;
	}
	
	public void setName(String Name){
		this._name=Name;
	}

	public void setDate(String Date){
		this._date=Date;
	}
	
	public void setURL(String URL){
		this._url=URL;
	}
}
