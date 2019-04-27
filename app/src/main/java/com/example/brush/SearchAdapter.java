package com.example.brush;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    Context context;

    Context userProfile;

    ArrayList<String> fullNameList;
    ArrayList<String> userNameList;
    ArrayList<String> userID;


    String TAG = "333";

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;

        public SearchViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.all_users_profile_username);
        }

    }

    public SearchAdapter(Context context, ArrayList<String> fullNameList, ArrayList<String> userNameList, ArrayList<String> userID) {
        this.context = context;
        this.fullNameList = fullNameList;
        this.userNameList = userNameList;
        this.userID = userID;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_display_layout, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.user_name.setText("@"+userNameList.get(position));
        Log.d(TAG, "position: " + position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "position after: " + position);
                Intent intent = new Intent(view.getContext(), PublicProfileActivity.class);
                intent.putExtra("userID", userID.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fullNameList.size();
    }
}
