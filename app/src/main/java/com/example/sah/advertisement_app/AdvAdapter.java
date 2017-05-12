package com.example.sah.advertisement_app;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvAdapter extends RecyclerView.Adapter<AdvAdapter.MyViewHolder> {

    private OnItemClickListener onItemClickListener;
    private JSONObject jsonObj, jImage;
    private String jTitle, jText, jName, imgName, scaleType;
    private ArrayList<String> data;
    private Context context;
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    int lastPosition = -1;


    public AdvAdapter(ArrayList<String> strings, Context c) {
        data = strings;
        context = c;
    }


    @Override
    public AdvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdvAdapter.MyViewHolder holder, int position) {


        try {
            jsonObj = new JSONObject(data.get(position));
            jTitle = jsonObj.getString("title");
            jText = jsonObj.getString("text");
            holder.tv_text.setText(jText);
            holder.tv_title.setText(jTitle);

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




        if(position >lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.item_anim);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_text;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_text = (TextView) itemView.findViewById(R.id.tv_descr);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClick(data.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick (String json, int position);
    }
}
