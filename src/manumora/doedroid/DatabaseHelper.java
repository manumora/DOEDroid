package manumora.doedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	static final String dbName="DOEdroidDB";
	static final String favouritesTable="favourites";
	static final String colID="ID";
	static final String colName="Name";
	static final String colDate="Date";
	static final String colURL="URL";
	
	public DatabaseHelper(Context context) {
		super(context, dbName, null,33);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) { // TODO Auto-generated method stub		
		db.execSQL("CREATE TABLE "+favouritesTable+" ("+colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				colName+" TEXT, "+colDate+" TEXT, "+colURL+" TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+favouritesTable);
		onCreate(db);
	}
	
	 void AddFavourite(Favourites fav){
		 
		SQLiteDatabase db= this.getWritableDatabase();
		
		ContentValues cv=new ContentValues();
		
		cv.put(colName, fav.getName());
		cv.put(colDate, fav.getDate());
		cv.put(colURL, fav.getURL());
		
		db.insert(favouritesTable, null, cv);
		db.close();				
	}
	 
	 int getFavouritesCount(){
		SQLiteDatabase db=this.getWritableDatabase();
		Cursor cur= db.rawQuery("Select * from "+favouritesTable, null);
		int x= cur.getCount();
		cur.close();
		return x;
	 }

	 Cursor getFavourite(String name){
		 SQLiteDatabase db=this.getWritableDatabase();
		 
		 Cursor cur= db.rawQuery("SELECT * FROM "+favouritesTable+" WHERE "+colName+"='"+name+"'",null);
		 return cur;
	 }	 
	 
	 boolean existsFavourite(String url){
		 SQLiteDatabase db=this.getWritableDatabase();
		 
		 Cursor cur= db.rawQuery("SELECT * FROM "+favouritesTable+" WHERE "+colURL+"='"+url+"'",null);
		 int x= cur.getCount();	 
		 cur.close();
		 
		 if(x==0)
			 return false;
		 else
			 return true;
	 }
	 
	 Cursor getAllFavourites(){
		 SQLiteDatabase db=this.getWritableDatabase();
		 
		 Cursor cur= db.rawQuery("SELECT * FROM "+favouritesTable+" ORDER BY "+colID+" DESC",null);
		 return cur;
	 }
	 
	 public void DeleteFavourite(Favourites fav){
		 SQLiteDatabase db=this.getWritableDatabase();
		 db.delete(favouritesTable,colName+"=?", new String [] {String.valueOf(fav.getName())});
		 db.close();
	 }
	 public void DeleteAll(){
		 SQLiteDatabase db=this.getWritableDatabase();
		 db.delete(favouritesTable,null,null);
		 db.close();
	 }
}
