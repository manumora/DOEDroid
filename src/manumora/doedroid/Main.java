package manumora.doedroid;

import manumora.doedroid.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);	
    }
    
    public void buttonClick(View view) {
    	String section= String.valueOf(view.getTag());

    	if(section.equals("2")){ //Favourites section

	        Intent i = new Intent(this, ShowFavourites.class);
	        startActivity(i);
	        
    	}else if(section.equals("3")){ //Info section
        	Dialog dialog = new Dialog(this);

        	dialog.setContentView(R.layout.toastinfo);
        	dialog.setTitle("DOEdroid v0.4 Beta");

        	TextView description = (TextView) dialog.findViewById(R.id.description);
        	description.setText("Aplicación no oficial del\nDiario Oficial de Extremadura\ny del\nPortal del Ciudadano\n\n\nDesarrollado por:\n Manu Mora Gordillo\n<manuito@gmail.com>");

        	ImageView image = (ImageView) dialog.findViewById(R.id.image);
        	image.setImageResource(R.drawable.icon);
        	
        	dialog.show();
	    }else{	    	
	        Intent i = new Intent(this, ShowList.class);
	        Bundle bundle = new Bundle();
	        bundle.putString("main", section);  
	        i.putExtras(bundle);
	        startActivity(i);
	    }
    }
}