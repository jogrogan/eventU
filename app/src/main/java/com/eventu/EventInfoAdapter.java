package com.eventu;

import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Class to hold the RecyclerView adapter and view holder for events
 */
public class EventInfoAdapter extends RecyclerView.Adapter<EventInfoAdapter.EventViewHolder> {

    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final Context mContext;
    private final Activity mActivity;
    private final List<EventInfo> mEventList;
    private final UserInfo mCurrentUserInfo;
    private final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    EventInfoAdapter(Context context, Activity activity, List<EventInfo> eventList, UserInfo user) {
        mContext = context;
        mActivity = activity;
        mEventList = eventList;
        mCurrentUserInfo = user;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.event_card_view, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Populates the EventViewHolder and handles card view actions
     */
    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        bind(holder, position);
    }

    /**
     * Populates the EventViewHolder and handles card view actions
     */
    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position, List<Object> payload) {
        if (!payload.isEmpty() && payload.get(0) instanceof EventInfo) {
            EventInfo mEventInfo = (EventInfo) payload.get(0);
            setFields(holder, mEventInfo);
        } else {
            super.onBindViewHolder(holder, position, payload);
        }
    }

    private void bind(final EventViewHolder holder, int position) {
        final EventInfo mEventInfo = mEventList.get(position);

        SimpleDateFormat ft = new SimpleDateFormat("E MMM dd 'at' hh:mm a", Locale.US);
        holder.mEventDate.setText(ft.format(mEventInfo.getEventDate()));
        holder.mEventCreator.setText(mEventInfo.getEventCreator());
        holder.mEventCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayClubPageActivity.class);
                intent.putExtra("user", mCurrentUserInfo.getUserID());
                intent.putExtra("school", mCurrentUserInfo.getSchoolName());
                intent.putExtra("club", mEventInfo.getClubID());
                mContext.startActivity(intent);
            }
        });

        setFields(holder, mEventInfo);

        // Handles pop-up menu event options
        holder.mEventPopUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu and inflating the view
                PopupMenu mPopMenu = new PopupMenu(mContext, v);
                mPopMenu.getMenuInflater().inflate(R.menu.popup_menu, mPopMenu.getMenu());
                if (mEventInfo.getClubID().equals(mCurrentUserInfo.getUserID())) {
                    mPopMenu.getMenu().findItem(R.id.edit_event).setEnabled(true);
                }

                //registering popup with OnMenuItemClickListener
                mPopMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Edit")) {
                            Intent intent = new Intent(mContext, CreateEventActivity.class);
                            intent.putExtra("eventID", mEventInfo.getEventID());
                            mActivity.startActivityForResult(intent,
                                    HomePageActivity.RESULT_IMAGE_CHANGE);
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

        // If on load the user previously had favorited this event, then re-select the icon
        // Otherwise default the icon as unselected
        if (mCurrentUserInfo.getFavorites().contains(mEventInfo.getEventID())) {
            holder.mEventFavorite.setSelected(true);
        } else {
            holder.mEventFavorite.setSelected(false);
        }

        // Handles event favoriting functionality and adds notifications
        holder.mEventFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eID = mEventInfo.getEventID();
                // Add or remove the event from the user's list of favorited events
                // Increase of decrease the tally of favorites for this event
                // Set up notifications for favorited events
                Intent intent = new Intent(mContext.getApplicationContext(),
                        NotificationIntentService.class);
                intent.putExtra("Event", mEventInfo.getEventName());
                intent.putExtra("Location", mEventInfo.getEventLocation());
                intent.putExtra("channel", eID);
                PendingIntent pendingIntent = PendingIntent.getService(
                        mContext.getApplicationContext(), eID.hashCode(), intent,
                        PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                if (alarmManager != null) {
                    if (!v.isSelected()) {
                        mCurrentUserInfo.addFavorite(eID);
                        mEventInfo.increaseTallyCount(mCurrentUserInfo.getSchoolName());

                        alarmManager.set(AlarmManager.RTC, mEventInfo.getEventDate().getTime(),
                                pendingIntent);
                    } else {
                        mCurrentUserInfo.removeFavorite(eID);
                        mEventInfo.decreaseTallyCount(mCurrentUserInfo.getSchoolName());

                        alarmManager.cancel(pendingIntent);
                    }
                }
                v.setSelected(!v.isSelected());
            }
        });
    }

    private void setFields(final EventViewHolder holder, EventInfo mEventInfo) {
        //Populate the holder views with text, date and images of the event
        holder.mEventName.setText(mEventInfo.getEventName());
        holder.mEventLocation.setText(mEventInfo.getEventLocation());
        holder.mEventDesc.setText(mEventInfo.getEventDescription());
        holder.mEventFavoriteTally.setText(
                String.format(Locale.US, "%d", mEventInfo.getEventTally()));

        if (mCurrentUser != null) {
            String mEventPath = "universities/" + mCurrentUser.getDisplayName() + "/Club Events";
            StorageReference imageStorage = mStorageReference.child(
                    mEventPath + "/" + mEventInfo.getEventID());
            imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Glide.with(mContext).load(imageURL).into(holder.mEventImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    holder.mEventImage.setImageResource(R.drawable.eventu_logo);
                    Log.i("Error", "Failed to get image");
                }
            });
        } else {
            holder.mEventImage.setImageResource(R.drawable.eventu_logo);
        }
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    /**
     * Holds all the view information for the event card view
     */
    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mEventImage;
        private final TextView mEventName;
        private final TextView mEventDesc;
        private final TextView mEventLocation;
        private final TextView mEventDate;
        private final TextView mEventCreator;
        private final TextView mEventFavoriteTally;
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
            mEventFavoriteTally = itemView.findViewById(R.id.textViewFavoriteTally);
            mEventFavorite = itemView.findViewById(R.id.imagebuttonFavorite);
            mEventPopUpMenu = itemView.findViewById(R.id.imagebuttonVerticalDots);
        }
    }
}
