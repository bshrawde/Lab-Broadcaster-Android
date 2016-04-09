package cs490.labbroadcaster.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cs490.labbroadcaster.CustomItemClickListener;
import cs490.labbroadcaster.R;

/**
 * Created by Nishant on 4/9/2016.
 */
public class ViewLabsRecyclerAdapter extends RecyclerView.Adapter<ViewLabsRecyclerAdapter.MyViewHolder> {
    private LayoutInflater inflator;
    private int lastPosition = -1;
    private Context mcontext;
    CustomItemClickListener listener;

    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> statuses = new ArrayList<>();
    public ViewLabsRecyclerAdapter(Context context, ArrayList<String> usernames,ArrayList<String> statuses, CustomItemClickListener listener){
        this.mcontext = context;
        this.statuses = statuses;
        this.usernames = usernames;
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //change layout
        View v = LayoutInflater.from(mcontext).inflate(R.layout.recycler_view_labs, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, myViewHolder.getPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Typeface robotoMono = Typeface.createFromAsset(mcontext.getAssets(), "fonts/roboto-mono-regular.ttf");
        String username = usernames.get(position);
        String status = statuses.get(position);
        //TODO able to change capactiy string to reflect current status of labs
        holder.username.setText(username);
        holder.status.setText(status);
        holder.username.setTypeface(robotoMono);
/*        holder.status.setTypeface(robotoMono);*/
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView username, status;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            status = (TextView) itemView.findViewById(R.id.status);

        }

    }

}
