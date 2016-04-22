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
 * Created by Nishant on 1/23/2016.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {
    private LayoutInflater inflator;
    private int lastPosition = -1;
    private Context mcontext;
    CustomItemClickListener listener;

    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> cap = new ArrayList<>();
    public MainRecyclerAdapter(Context context, ArrayList<String> data,ArrayList<String> cap, CustomItemClickListener listener){
        this.mcontext = context;
        this.cap = cap;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //change layout
        View v = LayoutInflater.from(mcontext).inflate(R.layout.recycler_view_main, parent, false);
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
        String cRoom = data.get(position);
        String capacity = cap.get(position);

        holder.room.setText(cRoom);
        holder.capacity.setText(capacity);
        holder.room.setTypeface(robotoMono);
        holder.capacity.setTypeface(robotoMono);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView room, capacity;
        public MyViewHolder(View itemView) {
            super(itemView);
            room = (TextView) itemView.findViewById(R.id.room);
            capacity = (TextView) itemView.findViewById(R.id.capacity);

        }

    }

}
