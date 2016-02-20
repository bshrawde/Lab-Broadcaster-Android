package cs490.labbroadcaster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.TextView;

public class LabWebView extends ActionBarActivity {

    private WebView webView;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lab_webview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Intent intent = getIntent();
        final String room = intent.getStringExtra("room");
        final String webcam = intent.getStringExtra("webcamURL");
        final String calendar = intent.getStringExtra("calendarURL");
        Typeface robotoMono = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-mono-regular.ttf");
        mTitle.setTypeface(robotoMono);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        if(!webcam.equals("")){
            mTitle.setText(room+" webcam");
            webView.loadUrl(webcam);
        }else{
            mTitle.setText(room+" calendar");
            webView.loadUrl(calendar);

        }





    }
}
