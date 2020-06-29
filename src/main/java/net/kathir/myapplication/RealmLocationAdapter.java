package net.kathir.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class RealmLocationAdapter extends RecyclerView.Adapter<RealmLocationAdapter.MyViewHolder>  {

    private static final String TAG = LocationAdapter.class.getSimpleName();

    Context context;
    RealmResults<LocationRealmModel> locationList;

    public RealmLocationAdapter(Context context, RealmResults<LocationRealmModel> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public RealmLocationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_adapter_item,parent,false);
        return new RealmLocationAdapter.MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RealmLocationAdapter.MyViewHolder holder, final int position) {


        holder.textLatitude.setText(String.valueOf(locationList.get(position).getLatitude()));

        holder.textLongitude.setText(String.valueOf(locationList.get(position).getLongitude()));

        Date date = new Date(locationList.get(position).getTimeStamp());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        GregorianCalendar gc = new GregorianCalendar();
        String localTime = formatter.format(date.getTime());

        holder.textTime.setText(localTime);

    }





    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.text_latitude)
        TextView textLatitude;
        @BindView(R.id.text_longitude)
        TextView textLongitude;
        @BindView(R.id.text_time)
        TextView textTime;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
