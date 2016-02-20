package cs490.labbroadcaster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewLabActivity extends ActionBarActivity {

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
        String linkText = "";
        /*TODO: USE WEBVIEW AND CHROME CUSTOM TABS DEPENDING ON ANDRIOD VERSION*/
        /*TODO: FIX BUG WHERE STRING ROOM BECOMES NULL WHEN RETURN FROM WEBVIEW ACTIVITY*/
        if("LWSN B146".equals(room)){
//            linkText = "<a href='\\http://lwsnb146-cam.cs.purdue.edu/view/index.shtml\\'>Click here to view the webcam for this room!</a>";
            linkText = "http://lwsnb146-cam.cs.purdue.edu/view/index.shtml";
        }else if("LWSN B148".equals(room)){
//            linkText = "<a href='http://lwsnb148-cam.cs.purdue.edu/view/index.shtml'>Click here to view the webcam for this room!</a>";
            linkText = "http://lwsnb148-cam.cs.purdue.edu/view/index.shtml";
        }else if("LWSN B158".equals(room)){
//            linkText = "<a href='http://lwsnb158-cam.cs.purdue.edu/view/index.shtml'>Click here to view the webcam for this room!</a>";
            linkText = "http://lwsnb158-cam.cs.purdue.edu/view/index.shtml";
        }else if("HAAS 257".equals(room)){
//            linkText = "<a href='http://haas257-cam.cs.purdue.edu/view/index.shtml'>Click here to view the webcam for this room!</a>";
            linkText = "http://haas257-cam.cs.purdue.edu/view/index.shtml";
        }
        final String finalLinkText = linkText;
        webcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webview = new Intent(ViewLabActivity.this, WebCamView.class);
                webview.putExtra("webcamURL", finalLinkText);
                webview.putExtra("room", room);
                startActivity(webview);
            }
        });
//        webcam.setMovementMethod(LinkMovementMethod.getInstance());
//        webcam.setText(Html.fromHtml(linkText));
//        webcam.setText(linkText);
        webcam.setTypeface(robotoMono);

    }
}
