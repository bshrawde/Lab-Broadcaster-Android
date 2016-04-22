package cs490.labbroadcaster;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ViewUserProfile extends AppCompatActivity {
    public String username;
    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        Toolbar toolbar =  (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        username = intent.getStringExtra("user");
        status = intent.getStringExtra("status");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        Typeface robotoMono = Typeface.createFromAsset(getAssets(), "fonts/roboto-mono-regular.ttf");
        mTitle.setTypeface(robotoMono);
        mTitle.setText(username);
        TextView statusr = (TextView) findViewById(R.id.status);
        statusr.setText(status);

/*        TextView statustw = (TextView) findViewById(R.id.statusw);
        statustw.setText(status);

        TextView ctakenw = (TextView) findViewById(R.id.coursesw);
        ctakenw.setTypeface(robotoMono);

        TextView status = (TextView) findViewById(R.id.status);
        status.setTypeface(robotoMono);

        TextView ccoursesw = (TextView) findViewById(R.id.ccoursesw);
        ccoursesw.setTypeface(robotoMono);

        TextView chelpw = (TextView) findViewById(R.id.chelp);
        chelpw.setTypeface(robotoMono);

        TextView lang = (TextView) findViewById(R.id.lang);
        lang.setTypeface(robotoMono);

        TextView tafor = (TextView) findViewById(R.id.tafor);
        tafor.setTypeface(robotoMono);*/

    }
}
