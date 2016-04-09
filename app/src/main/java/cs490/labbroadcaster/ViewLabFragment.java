package cs490.labbroadcaster;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

public class ViewLabFragment extends Fragment {

    Context context = getActivity();
    private String room;
    TextView mTitle;
    TextView mCapacity;
    private String capacity;
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_lab_fragment, container, false);
        Toolbar toolbar =  (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);


        Bundle args = getArguments();
        if(args != null){
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        room = (args != null) ? args.getString("labRoom") : "";
        capacity = (args != null) ? args.getString("capacity") : "";
//        if(getActivity().getIntent().hasExtra("labRoom")){
//            room = getActivity().getIntent().getStringExtra("labRoom");
//        }
//        if(getActivity().getIntent().hasExtra("capacity")){
//            capacity = getActivity().getIntent().getStringExtra("capacity");
//        }

//        Log.e("Room",room);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(room);
        Typeface robotoMono = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto-mono-regular.ttf");
        mTitle.setTypeface(robotoMono);

        mCapacity = (TextView) view.findViewById(R.id.capacity);
        mCapacity.setText(capacity);
        mCapacity.setTypeface(robotoMono);


        FloatingActionButton fabcalendar = (FloatingActionButton) view.findViewById(R.id.fab_calendar);
        FloatingActionButton fabwebcam = (FloatingActionButton) view.findViewById(R.id.fab_webcam);

        fabwebcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "http://lwsnb146-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "http://lwsnb148-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "http://lwsnb158-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "http://haas257-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "http://lwsnb160-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "http://lwsnb131-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "http://haasg040-cam.cs.purdue.edu/view/index.shtml";
                } else if ("HAAS G56".equals((mTitle.getText()))) {

                    linkText = "http://haasg056-cam.cs.purdue.edu/view/index.shtml";
                }
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getActivity(), Uri.parse(linkText));
            }
        });

        fabcalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb146.html";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb148.html";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb158.html";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haas257.html";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb160.html";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb131.html";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg40.html";
                } else if ("HAAS G56".equals((mTitle.getText()))) {
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg56.html";
                }
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getActivity(), Uri.parse(linkText));
            }
        });
        return view;
    }

    public void changeText(String room, String cap){
        TextView mTitle = (TextView) getView().findViewById(R.id.toolbar_title);
        mTitle.setText(room);
        TextView mCapacity = (TextView) getView().findViewById(R.id.capacity);
        mCapacity.setText(cap);

    }
}
