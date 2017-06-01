package com.stabstudio.discussionapp.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stabstudio.discussionapp.R;
import com.stabstudio.discussionapp.Models.Comment;
import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Comment> comments;

    private DatabaseReference usersRef;

    public CommentsAdapter(Context context, ArrayList<Comment> comments){
        this.context = context;
        this.comments = comments;
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(vi);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder holder, int position) {
        holder.commentAuthor.setText(comments.get(position).getAuthor());
        holder.commentBody.setText(comments.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView commentAuthor;
        private TextView commentBody;

        public ViewHolder(View itemView) {
            super(itemView);
            commentAuthor = (TextView) itemView.findViewById(R.id.comment_author);
            commentBody = (TextView) itemView.findViewById(R.id.comment_body);
        }
    }
}
