package manumora.doedroid;

import java.util.List;
import java.util.Vector;

import manumora.doedroid.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class ShowFavourites extends Activity {
	private DatabaseHelper db;
	private List<Favourites> itemList;
	
	public class MyCustomAdapter extends ArrayAdapter<Favourites> {

		public MyCustomAdapter(Context context, int textViewResourceId, List<Favourites> list) {
			super(context, textViewResourceId, list);	
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			//return super.getView(position, convertView, parent);
			
			View row = convertView;
			 
			if(row==null){
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.row, parent, false);	
			}

			String description = itemList.get(position).getName();
			String date[] = itemList.get(position).getDate().split("-");
			String url = itemList.get(position).getURL();			

			((TextView)row.findViewById(R.id.listdescription)).setText(description);			
			((TextView)row.findViewById(R.id.listpubdate)).setText(date[2]+"/"+date[1]+"/"+date[0]);

			ImageView img = ((ImageView)row.findViewById(R.id.downloadlink));			
			img.setTag(url);			
			img.setOnClickListener(new View.OnClickListener(){
			    public void onClick(View v){			    	
			        Intent intent = new Intent();
			        intent.setAction(Intent.ACTION_VIEW);
			        intent.addCategory(Intent.CATEGORY_BROWSABLE);
			        intent.setData(Uri.parse((String)v.getTag()));
			        startActivity(intent);
			    }
			 });

			if(db.existsFavourite(url)){
		    	((ImageView)row.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite);
			}else{
		    	((ImageView)row.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite_des);
			}	
			
			ImageView imgFavourite = (ImageView)row.findViewById(R.id.iconFavourite);
			imgFavourite.setOnClickListener(new View.OnClickListener(){
			    public void onClick(View v){
					String description = itemList.get(position).getName();
					String date = itemList.get(position).getDate();
					String url = itemList.get(position).getURL();		
					
			    	Favourites fav = new Favourites(description,date,url);
			    	DatabaseHelper db = new DatabaseHelper(getApplicationContext());
					if(db.existsFavourite(url)){
				    	((ImageView)v.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite_des);
						db.DeleteFavourite(fav);		
						readBD();
					}else{
						((ImageView)v.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite);
						db.AddFavourite(fav);
					}
			    }
			 });
			
			return row;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list);

	    ((View)findViewById(R.id.head)).setBackgroundResource(R.drawable.titlebar_favorite);		
	}
	
	public void onResume(){
		super.onResume();
    	db = new DatabaseHelper(getApplicationContext());
		readBD();
	}
	
    private void readBD(){

    	Cursor myCursor = db.getAllFavourites();

		if (myCursor!=null){
			if(myCursor.getCount()==0){
				Toast.makeText(this, "No hay datos que mostrar", 500).show();
				finish();
			}else{
				itemList = new Vector<Favourites>(0);
		    	while (myCursor.moveToNext()) {
					String name = myCursor.getString(myCursor.getColumnIndex("Name"));
					String date = myCursor.getString(myCursor.getColumnIndex("Date"));
					String url = myCursor.getString(myCursor.getColumnIndex("URL"));
					Favourites fav = new Favourites(name,date,url);

					itemList.add(fav);
				}

				MyCustomAdapter adapter = new MyCustomAdapter(this, R.layout.row, itemList);
					
				((ListView)findViewById(R.id.list)).setAdapter(adapter);
				int[] colors = {0, 0xFFFF0000, 0};
				((ListView)findViewById(R.id.list)).setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
				((ListView)findViewById(R.id.list)).setDividerHeight(1);
			}
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menufavorites, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.item01:{
        		db.DeleteAll();
        		readBD();
        		break;        
        	}
        }
        return true;
    }
}
