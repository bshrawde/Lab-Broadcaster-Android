package cs490.labbroadcaster;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.ContactsContract;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import cs490.labbroadcaster.adapters.MainRecyclerAdapter;
import cs490.labbroadcaster.adapters.ViewLabsRecyclerAdapter;

public class ViewLabFragment extends Fragment {

    Context context = getActivity();
    private String room;
    TextView mTitle;
    TextView mCapacity;
    ImageView groupimage;
    private String capacity;
    RecyclerView recyclerView;
    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    private ViewLabsRecyclerAdapter adapter;

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
        mCapacity = (TextView) view.findViewById(R.id.currentusers);
        groupimage = (ImageView) view.findViewById(R.id.groupimage);
        String t = "";
        if(capacity!= ""){
            if(capacity.charAt(0) == '0'){
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_people_outline_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current Users");
            }else if(capacity.charAt(0) == '1' && capacity.charAt(1) == '/'){
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current User");
            }else{
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_group_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current Users");
            }
            if(capacity.charAt(1) != '/'){ //double digit number
                t = capacity.substring(0,2);
            }else{
                t = capacity.substring(0,1);
            }
            int currentcap = Integer.parseInt(t);
            for(int i = 0; i<currentcap; i++){
                username.add("nmoorthy");
                status.add("I need help with eating cookies");
            }
        }




        mCapacity.setTypeface(robotoMono);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ViewLabsRecyclerAdapter(getActivity(), username,status, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getActivity(), "View Profile TODO", Toast.LENGTH_SHORT).show();
                View labView = getActivity().findViewById(R.id.viewlab);
                Intent intent = new Intent(getActivity(), ViewUserProfile.class);
                intent.putExtra("user", username.get(position));
                intent.putExtra("status", status.get(position));
                startActivity(intent);
                boolean isDual = labView != null && labView.getVisibility() == View.VISIBLE;
                if(isDual){

                }else{

                }
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabcalendar = (FloatingActionButton) view.findViewById(R.id.fab_calendar);
        FloatingActionButton fabwebcam = (FloatingActionButton) view.findViewById(R.id.fab_webcam);

        FloatingActionsMenu fab = (FloatingActionsMenu) view.findViewById(R.id.fab);

        if(mTitle.getText().equals("")){
            fab.setVisibility(View.GONE);

        }else{
            fab.setVisibility(View.VISIBLE);
        }
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
