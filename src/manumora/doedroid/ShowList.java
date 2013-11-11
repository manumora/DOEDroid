package manumora.doedroid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import manumora.doedroid.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
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

public class ShowList extends Activity {
	
	private RSSFeed myRssFeed = null;
	private String rss;
	private DatabaseHelper db;
	private int main;
	private ProgressDialog dialog;
	
	public class RssLoadingTask extends AsyncTask<Void, Void, Void> {

		 protected void onPostExecute(Void result) {
			 displayRss();
		     if (dialog.isShowing())
	            dialog.dismiss();
		 }

		 protected void onPreExecute() {
			 dialog = ProgressDialog.show(ShowList.this,"Por favor, espere...", "Obteniendo datos...", true);
		 }

		 protected void onProgressUpdate(Void... values) {}

		 protected Void doInBackground(Void... arg0) {
			 readRss();
			 return null;
		 }
	}
	
	public class MyCustomAdapter extends ArrayAdapter<RSSItem> {
		public MyCustomAdapter(Context context, int textViewResourceId, List<RSSItem> list) {
			super(context, textViewResourceId, list);	
		}
	    
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			 
			if(row==null){
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.row, parent, false);	
			}

			String title = myRssFeed.getList().get(position).getTitle();
			String description = myRssFeed.getList().get(position).getDescription();
			String date[] = myRssFeed.getList().get(position).getPubdate().split("-");
			String url = myRssFeed.getList().get(position).getLink();			
			
			if(main==0) // DOE
				((TextView)row.findViewById(R.id.listdescription)).setText(description);
			else // Portal del Ciudadano
				((TextView)row.findViewById(R.id.listdescription)).setText(title+"\n"+description);
			
			((TextView)row.findViewById(R.id.listpubdate)).setText(date[2]+"/"+date[1]+"/"+date[0]);

			ImageView img = ((ImageView)row.findViewById(R.id.downloadlink));	
				
			if(!url.endsWith(".pdf"))
		    	img.setImageResource(R.drawable.icon_world);
			
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
					String title = myRssFeed.getList().get(position).getTitle();
					String description = myRssFeed.getList().get(position).getDescription();
					String date = myRssFeed.getList().get(position).getPubdate();
					String url = myRssFeed.getList().get(position).getLink();	
					String text;

					if(main==0) // DOE
						text = description;
					else // Portal del Ciudadano
						text = title+"\n"+description;
					
			    	Favourites fav = new Favourites(text,date,url);
			    	DatabaseHelper db = new DatabaseHelper(getApplicationContext());
					if(db.existsFavourite(url)){
				    	((ImageView)v.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite_des);
						db.DeleteFavourite(fav);				    	
					}else{
						((ImageView)v.findViewById(R.id.iconFavourite)).setImageResource(R.drawable.icon_favourite);
						db.AddFavourite(fav);
					}
			    }
			 });
			
			return row;
		}
	}

	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;
	    private Integer[] mImageIds={};
	    private Integer[] mImageIdsDOE={R.drawable.doe_zero, R.drawable.doe_one, R.drawable.doe_two, R.drawable.doe_three, R.drawable.doe_four, R.drawable.doe_five};
	    private Integer[] mImageIdsSIA={R.drawable.sia_zero, R.drawable.sia_one, R.drawable.sia_two, R.drawable.sia_three, R.drawable.sia_four};
	    
	    public ImageAdapter(Context c) {
	        mContext = c;
	        if(main==0){
	        	mImageIds=mImageIdsDOE;
	        }else{
	        	mImageIds=mImageIdsSIA;
	        }
	        TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery1);
	        mGalleryItemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
	        a.recycle();
	    }

	    public int getCount() {
	        return mImageIds.length;
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView i = new ImageView(mContext);
	        i.setImageResource(mImageIds[position]);
	        i.setLayoutParams(new Gallery.LayoutParams(220, 80));
	        i.setScaleType(ImageView.ScaleType.FIT_XY);

	        //i.setBackgroundResource(mGalleryItemBackground);
	        i.setBackgroundColor(0xFFFFFF); 
	        i.setPadding(0,5,0,5);
	        return i;
	    }
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list);
	    
	    Bundle bundle = getIntent().getExtras();
	    String tmp= bundle.getString("main");
	    main = Integer.parseInt(tmp);
	    
		switch(main){
		case 0:{
		    ((View)findViewById(R.id.head)).setBackgroundResource(R.drawable.titlebar_doe);
			rss="http://doe.juntaex.es/rss/rss.php?seccion=0"; 
			break;} //DOE
		case 1:{
		    ((View)findViewById(R.id.head)).setBackgroundResource(R.drawable.titlebar_sia);
			rss="http://sede.juntaex.es/rss/ciudadanos"; 
			break;}//SIA
		}
	    
	    Gallery g = (Gallery) findViewById(R.id.gallery);
	    g.setAdapter(new ImageAdapter(this));
		
	    g.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) { 
	    		switch(main){
					case 0:{ rss="http://doe.juntaex.es/rss/rss.php?seccion="+position; break;} //DOE
					case 1:{ //SIA
						switch(position){
							case 0:{rss="http://sede.juntaex.es/rss/ciudadanos"; break;}
							case 1:{rss="http://sede.juntaex.es/rss/empresas"; break;}
							case 2:{rss="http://sede.juntaex.es/rss/entidades"; break;}
							case 3:{rss="http://sede.juntaex.es/rss/licitaciones"; break;}
							case 4:{rss="http://sede.juntaex.es/rss/empleo"; break;}
						}
						break;
					}
				}
	    		new RssLoadingTask().execute();
	        }
	    });
	    startReadRss();
	}
		
	private void startReadRss(){
		new RssLoadingTask().execute();
	}
	
    private void readRss(){
    	db = new DatabaseHelper(getApplicationContext());
    	
        try {
        	URL rssUrl = new URL(rss);

			SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
			SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
			XMLReader myXMLReader = mySAXParser.getXMLReader();
			
			RSSHandler myRSSHandler = new RSSHandler();
			myXMLReader.setContentHandler(myRSSHandler);
			
			InputSource myInputSource = new InputSource(rssUrl.openStream());

			switch(main){
				case 0:{ myInputSource.setEncoding("ISO-8859-1"); break;} //DOE
				case 1:{ myInputSource.setEncoding("UTF-8"); break;} //SIA
			}
			
			myXMLReader.parse(myInputSource);
			
			myRssFeed = myRSSHandler.getFeed();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void displayRss(){
		if (myRssFeed!=null){
			if(myRssFeed.getList().isEmpty())
				Toast.makeText(this, "No hay datos que mostrar", 500).show();

			MyCustomAdapter adapter = new MyCustomAdapter(this, R.layout.row, myRssFeed.getList());
				
			((ListView)findViewById(R.id.list)).setAdapter(adapter);
			int[] colors = {0, 0xFFFF0000, 0};
			((ListView)findViewById(R.id.list)).setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
			((ListView)findViewById(R.id.list)).setDividerHeight(1);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menulist, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item01:{
            	Dialog dialog = new Dialog(this);

            	dialog.setContentView(R.layout.toastinfo);
            	dialog.setTitle("DOEdroid v0.4 Beta");

            	TextView description = (TextView) dialog.findViewById(R.id.description);
            	description.setText("Aplicación no oficial del\nDiario Oficial de Extremadura\ny del\nPortal del Ciudadano\n\n\nDesarrollado por:\n Manu Mora Gordillo\n<manuito@gmail.com>");

            	ImageView image = (ImageView) dialog.findViewById(R.id.image);
            	image.setImageResource(R.drawable.icon);
            	
            	dialog.show();
            	break;
            }
        }
        return true;
    }
    
	
	/*	if(!isOnline()){
	Toast.makeText(this, "No hay conexión a internet :(", 500).show();
	finish();
}*/
    public boolean isOnline() {
    	 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	 return cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
