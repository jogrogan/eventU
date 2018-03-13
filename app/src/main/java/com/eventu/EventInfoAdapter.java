package com.eventu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Yang Li on 3/9/18.
 * Class to hold the recylerview adapter and view holder for events
 */

public class EventInfoAdapter extends RecyclerView.Adapter<EventInfoAdapter.EventViewHolder> {

    private Context mContext;
    private List<EventInfo> mEventList;

    EventInfoAdapter(Context context, List<EventInfo> eventList) {
        mContext = context;
        mEventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.event_card_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventInfo mEventInfo = mEventList.get(position);

        //Populate the holder views with text, date and images of the event
        holder.mEventName.setText(mEventInfo.getEventName());
        holder.mEventLocation.setText(mEventInfo.getEventLocation());

        SimpleDateFormat ft = new SimpleDateFormat("E MMM dd 'at' hh:mm a", Locale.US);
        holder.mEventDate.setText(ft.format(mEventInfo.getEventDate()));
        holder.mEventDesc.setText(mEventInfo.getEventDescription());
        holder.mEventCreator.setText(mEventInfo.getEventCreator());

        //TODO allow custom image
        holder.mEventImage.setImageResource(R.drawable.eventu_logo);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView mEventImage;
        private TextView mEventName;
        private TextView mEventDesc;
        private TextView mEventLocation;
        private TextView mEventDate;
        private TextView mEventCreator;


        EventViewHolder(View itemView) {
            super(itemView);
            mEventImage = itemView.findViewById(R.id.imageViewEventImage);
            mEventName = itemView.findViewById(R.id.textViewEventName);
            mEventDesc = itemView.findViewById(R.id.textViewEventDesc);
            mEventDate = itemView.findViewById(R.id.textViewEventDate);
            mEventLocation = itemView.findViewById(R.id.textViewEventLocation);
            mEventCreator = itemView.findViewById(R.id.textViewEventCreator);
        }
    }
}
