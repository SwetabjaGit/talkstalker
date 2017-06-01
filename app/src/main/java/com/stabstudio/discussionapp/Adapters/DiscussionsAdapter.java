package com.stabstudio.discussionapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stabstudio.discussionapp.Fragments.DiscussionFragment;
import com.stabstudio.discussionapp.Models.Discussion;
import com.stabstudio.discussionapp.Models.Places;
import com.stabstudio.discussionapp.Models.User;
import com.stabstudio.discussionapp.UI.DiscussionActivity;

import com.stabstudio.discussionapp.R;

import org.joda.time.DateTime;

import java.util.List;

public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionsAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<Discussion> discussionList;

    private DatabaseReference usersRef;
    private DatabaseReference placeRef;

    public DiscussionsAdapter(Context context) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        discussionList = DiscussionFragment.discussionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    public Discussion getValueAt(int position) {
        return discussionList.get(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Discussion tempDiscussion = discussionList.get(position);

        final String subject = tempDiscussion.getSubject();
        int likes = tempDiscussion.getLikes();
        int comments = tempDiscussion.getComments();
        final String userId = tempDiscussion.getUser_id();
        final String placeId = tempDiscussion.getPlace_id();

        String timeStamp = tempDiscussion.getTimestamp();
        holder.subjectText.setText(subject);
        holder.likesNo.setText(String.valueOf(likes));
        holder.commentsNo.setText(String.valueOf(comments));
        getUsernameAndPlace(holder, userId, placeId);

        calculateTimeAGO(holder, timeStamp);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DiscussionActivity.class);
                intent.putExtra(DiscussionActivity.EXTRA_NAME, subject);
                intent.putExtra("dissId", discussionList.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String likes = holder.likesNo.getText().toString();
                discussionList.get(position).incrementLike();
                notifyDataSetChanged();
            }
        });

        /*Glide.with(holder.mImageView.getContext())
                .load(TestData.getRandomCheeseDrawable())
                .fitCenter()
                .into(holder.mImageView);*/
    }

    private void getUsernameAndPlace(final ViewHolder holder, final String userId, final String placeId){
        usersRef = FirebaseDatabase.getInstance() .getReference().child("Users");
        placeRef = FirebaseDatabase.getInstance().getReference().child("Places");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(userId).getValue(User.class);
                holder.userName.setText(user.getFirst_name() + " " + user.getLast_name());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Places place = dataSnapshot.child(placeId).getValue(Places.class);
                holder.address.setText(place.getAddress());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void calculateTimeAGO(final ViewHolder holder, String timeStamp){

        String[] chars = timeStamp.split("/");

        int second = Integer.parseInt(chars[0]);
        int minute = Integer.parseInt(chars[1]);
        int hour = Integer.parseInt(chars[2]);
        int day = Integer.parseInt(chars[3]);
        int month = Integer.parseInt(chars[4]);
        int year = Integer.parseInt(chars[5]);

        int secondNow = DateTime.now().getSecondOfMinute();
        int minuteNow = DateTime.now().getMinuteOfHour();
        int hourNow = DateTime.now().getHourOfDay();
        int dayNow = DateTime.now().getDayOfMonth();
        int monthNow = DateTime.now().getMonthOfYear();
        int yearNow = DateTime.now().getYear();

        int displayTime;

        if(yearNow - year != 0){
            displayTime = yearNow - year;
            holder.timeText.setText(displayTime + " years ago");
        }else if(monthNow - month != 0){
            displayTime = monthNow - month;
            holder.timeText.setText(displayTime + " months ago");
        }else if(dayNow - day != 0){
            displayTime = dayNow - day;
            holder.timeText.setText(displayTime + " days ago");
        }else if(hourNow - hour != 0){
            displayTime = hourNow - hour;
            holder.timeText.setText(displayTime + " hours ago");
        }else if(minuteNow - minute != 0){
            displayTime = minuteNow - minute;
            holder.timeText.setText(displayTime + " minutes ago");
        }else{
            displayTime = secondNow - second;
            holder.timeText.setText(displayTime + " seconds ago");
        }

    }

    @Override
    public int getItemCount() {
        return discussionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView subjectText;
        public TextView userName;
        public TextView timeText;
        public TextView address;
        public TextView likesNo;
        public TextView commentsNo;
        public ImageView likeIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            subjectText = (TextView) view.findViewById(R.id.dis_subject);
            userName = (TextView) view.findViewById(R.id.dis_author);
            timeText = (TextView) view.findViewById(R.id.dis_timestamp);
            address = (TextView) view.findViewById(R.id.dis_address);
            likesNo = (TextView) view.findViewById(R.id.like_count);
            commentsNo = (TextView) view.findViewById(R.id.comment_count);
            likeIcon = (ImageView) view.findViewById(R.id.like_icon);
        }

        /*@Override
        public String toString() {
            return super.toString() + " '" + mTextView.getText();
        }*/
    }

}
