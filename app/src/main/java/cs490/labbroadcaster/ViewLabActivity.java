package cs490.labbroadcaster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewLabActivity extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        final String room = intent.getStringExtra("labRoom");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(room);
        Typeface robotoMono = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-mono-regular.ttf");
        mTitle.setTypeface(robotoMono);

        final Button webcam = (Button) findViewById(R.id.webcamLink);

        final Button calendar = (Button) findViewById(R.id.calendarLink);
        webcam.setTypeface(robotoMono);
        calendar.setTypeface(robotoMono);

        /*TODO: USE WEBVIEW AND CHROME CUSTOM TABS DEPENDING ON ANDRIOD VERSION*/
        /*TODO: FIX BUG WHERE STRING ROOM BECOMES NULL WHEN RETURN FROM WEBVIEW ACTIVITY With BACK BUTTON IN TOOLBAR*/

        webcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(room)){
                    linkText = "http://lwsnb146-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B148".equals(room)){
                    linkText = "http://lwsnb148-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B158".equals(room)){
                    linkText = "http://lwsnb158-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS 257".equals(room)){
                    linkText = "http://haas257-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B160".equals((room))){
                    linkText = "http://lwsnb160-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B131".equals((room))){
                    linkText = "http://lwsnb131-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS G40".equals((room))){
                    linkText = "http://haasg040-cam.cs.purdue.edu/view/index.shtml";
                } else if ("HAAS G56".equals((room))) {

                    linkText = "http://haasg056-cam.cs.purdue.edu/view/index.shtml";
                }
                Intent webview = new Intent(ViewLabActivity.this, LabWebView.class);
                webview.putExtra("webcamURL", linkText);
                webview.putExtra("calendarURL", "");
                webview.putExtra("room", room);
                startActivity(webview);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(room)){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb146.html";
                }else if("LWSN B148".equals(room)){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb148.html";
                }else if("LWSN B158".equals(room)){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb158.html";
                }else if("HAAS 257".equals(room)){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haas257.html";
                }else if("LWSN B160".equals((room))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb160.html";
                }else if("LWSN B131".equals((room))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb131.html";
                }else if("HAAS G40".equals((room))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg40.html";
                } else if ("HAAS G56".equals((room))) {
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg56.html";
                }

                Intent webview = new Intent(ViewLabActivity.this, LabWebView.class);
                webview.putExtra("calendarURL", linkText);
                webview.putExtra("webcamURL", "");
                webview.putExtra("room", room);
                startActivity(webview);
            }
        });
    }
}
