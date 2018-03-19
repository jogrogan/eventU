package com.eventu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Class to hold the RecyclerView adapter and view holder for events
 */
public class EventInfoAdapter extends RecyclerView.Adapter<EventInfoAdapter.EventViewHolder> {

    private final Context mContext;
    private final List<EventInfo> mEventList;

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
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        EventInfo mEventInfo = mEventList.get(position);

        //Populate the holder views with text, date and images of the event
        holder.mEventName.setText(mEventInfo.getEventName());
        holder.mEventLocation.setText(mEventInfo.getEventLocation());

        SimpleDateFormat ft = new SimpleDateFormat("E MMM dd 'at' hh:mm a", Locale.US);
        holder.mEventDate.setText(ft.format(mEventInfo.getEventDate()));
        holder.mEventDesc.setText(mEventInfo.getEventDescription());
        holder.mEventCreator.setText(mEventInfo.getEventCreator());

        //TODO allow custom image - planned for future
        holder.mEventImage.setImageResource(R.drawable.eventu_logo);

        holder.mEventPopUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu and inflating the view
                PopupMenu mPopMenu = new PopupMenu(mContext, v);
                mPopMenu.getMenuInflater().inflate(R.menu.popup_menu, mPopMenu.getMenu());

                //registering popup with OnMenuItemClickListener
                mPopMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Surprise Me")) {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse(
                                                    "https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                            mContext.startActivity(viewIntent);
                        } else {
                            Toast.makeText(mContext, "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                mPopMenu.show();
            }
        });

        holder.mEventFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                //TODO add more favorite logic here
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mEventImage;
        private final TextView mEventName;
        private final TextView mEventDesc;
        private final TextView mEventLocation;
        private final TextView mEventDate;
        private final TextView mEventCreator;
        private final ImageButton mEventFavorite;
        private final ImageButton mEventPopUpMenu;


        EventViewHolder(View itemView) {
            super(itemView);
            mEventImage = itemView.findViewById(R.id.imageViewEventImage);
            mEventName = itemView.findViewById(R.id.textViewEventName);
            mEventDesc = itemView.findViewById(R.id.textViewEventDesc);
            mEventDate = itemView.findViewById(R.id.textViewEventDate);
            mEventLocation = itemView.findViewById(R.id.textViewEventLocation);
            mEventCreator = itemView.findViewById(R.id.textViewEventCreator);
            mEventFavorite = itemView.findViewById(R.id.imagebuttonFavorite);
            mEventPopUpMenu = itemView.findViewById(R.id.imagebuttonVerticalDots);
        }
    }
}
