package com.example.sah.advertisement_app.db;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sah.advertisement_app.AdvAdapter;
import com.example.sah.advertisement_app.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {


    private JSONObject jsonObj, jImage;
    private String jTitle, jText, jName, imgName, scaleType;
    private ArrayList<String> data;
    private Context context;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef;



    public FavoritesAdapter(ArrayList<String> strings, Context c) {
        data = strings;
        context = c;
    }


    @Override
    public FavoritesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FavoritesAdapter.MyViewHolder viewHolder = new FavoritesAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_favorites, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.MyViewHolder holder, int position) {


        try {
            jsonObj = new JSONObject(data.get(position));
            holder.tv_text.setText(jsonObj.getString("text"));
            holder.tv_title.setText(jsonObj.getString("title"));
            holder.tv_author_name.setText(jsonObj.getString("author"));
            holder.tv_author_email.setText(jsonObj.getString("email"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jImage = jsonObj.getJSONObject("image");
            if (!jImage.toString().equals("")){
                holder.imageView.setVisibility(View.VISIBLE);
                imgName = jImage.getString("imageName");
                scaleType = jImage.getString("scaleType");


                storageRef = fStorage.getReferenceFromUrl("gs://advertisementapp-c96d6.appspot.com/images/" + imgName);
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(storageRef)
                        .into(holder.imageView);
                if (scaleType.equals("centerCrop")) {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else if (scaleType.equals("fitCenter")) {
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_text, tv_author_name, tv_author_email;
        ImageView imageView;

        private MyViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.fav_title);
            tv_text = (TextView) itemView.findViewById(R.id.fav_descr);
            imageView = (ImageView) itemView.findViewById(R.id.fav_image);
            tv_author_name = (TextView) itemView.findViewById(R.id.fav_author_name);
            tv_author_email = (TextView) itemView.findViewById(R.id.fav_author_email);
        }
    }

}

