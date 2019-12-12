package com.example.societyfy.Activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.societyfy.Activities.models.User;
import com.example.societyfy.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){

        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView mail;
        public CircleImageView profile_image;

        public ViewHolder( View itemView){

            super(itemView);

            name = itemView.findViewById(R.id.nameOfUser);
            mail = itemView.findViewById(R.id.mailOfUser);
            profile_image = itemView.findViewById(R.id.User_imageList);
        }

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder item, int position) {

        User user = mUsers.get(position);
        item.name.setText(user.getName());
        item.mail.setText(user.getEmail());
        if(user.getImage().equals("default")){
            item.profile_image.setImageResource(R.drawable.userpic);
        }else{
            Glide.with(mContext).load(user.getImage()).into(item.profile_image);
        }


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



}
