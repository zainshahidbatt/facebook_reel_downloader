package com.example.igreeldownloader.models;


import static com.example.igreeldownloader.util.Utils.appInstalledOrNot;
import static com.example.igreeldownloader.util.Utils.deleteVideoFromFile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.igreeldownloader.R;

import java.io.File;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private final ItemClickListener itemClickListener;
    private final Context context;
    private List<FVideo> videos;

    public ListAdapter(Context context, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.video_item_layout, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        FVideo video = videos.get(position);


        if (video.getState() == FVideo.COMPLETE) {
            Glide.with(context)
                    .load(video.getFileUri())
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.ic_video_file);
        }

        holder.tvVideoTitle.setText(video.getFileName());

        //Setting video state
        switch (video.getState()) {
            case FVideo.DOWNLOADING:
                holder.cvIcons.setVisibility(View.GONE);
                holder.tvVideoState.setVisibility(View.VISIBLE);
                holder.tvVideoState.setText(R.string.downloading);
                break;
            case FVideo.PROCESSING:
                holder.cvIcons.setVisibility(View.GONE);
                holder.tvVideoState.setVisibility(View.VISIBLE);
                holder.tvVideoState.setText(R.string.processing);
                break;
            case FVideo.COMPLETE:
                //if video download and processing complete then it shows share icons
                holder.tvVideoState.setVisibility(View.GONE);
                holder.cvIcons.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        if (videos == null)
            return 0;
        return videos.size();
    }

    public void setVideos(List<FVideo> fVideos) {
        videos = fVideos;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(FVideo video);
    }

    class ListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        TextView tvVideoTitle;
        TextView tvVideoState;
        ImageView ivThumbnail;
        ConstraintLayout cvIcons;
        ImageView ivFb;
        ImageView ivMessenger;
        ImageView ivWp;
        ImageView ivDelete;
        ImageView ivShare;
        View itemView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            tvVideoTitle = itemView.findViewById(R.id.tv_video_title);
            tvVideoState = itemView.findViewById(R.id.tv_video_state);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            ivShare = itemView.findViewById(R.id.iv_share);
            ivFb = itemView.findViewById(R.id.iv_fb);
            ivMessenger = itemView.findViewById(R.id.iv_messenger);
            ivWp = itemView.findViewById(R.id.iv_wp);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            cvIcons = itemView.findViewById(R.id.cl_icons);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


            ivShare.setOnClickListener(v -> {

                FVideo video = videos.get(getAdapterPosition());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("video/mp4");

                File file = new File(video.getFileUri());
                Uri videoUri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", file);

                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
                context.startActivity(Intent.createChooser(shareIntent, "Send"));

            });
            ivDelete.setOnClickListener(v -> {
                FVideo video = videos.get(getAdapterPosition());
                deleteVideoFromFile(context, video);
            });

            ivFb.setOnClickListener(v -> {
                if (!appInstalledOrNot(context, "com.facebook.katana")) {
                    Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
                    return;
                }
                FVideo video = videos.get(getAdapterPosition());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.facebook.katana");
                shareIntent.setType("video/*");

                File file = new File(video.getFileUri());
                Uri videoUri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", file);

                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
                context.startActivity(Intent.createChooser(shareIntent, "Send"));

            });

            ivMessenger.setOnClickListener(v -> {
                if (!appInstalledOrNot(context, "com.facebook.orca")) {
                    Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
                    return;
                }

                FVideo video = videos.get(getAdapterPosition());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.facebook.orca");
                shareIntent.setType("video/*");

                File file = new File(video.getFileUri());
                Uri videoUri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", file);

                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
                context.startActivity(Intent.createChooser(shareIntent, "Send"));
            });

            ivWp.setOnClickListener(v -> {
                if (!appInstalledOrNot(context, "com.whatsapp")) {
                    Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
                    return;
                }

                FVideo video = videos.get(getAdapterPosition());

                Uri uri = Uri.parse(video.getFileUri());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.setPackage("com.whatsapp");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);

                context.startActivity(intent);
            });

        }

        @Override
        public void onClick(View v) {
            Log.d("Hello World", "onClick: id " + v.getId());

            FVideo video = videos.get(getAdapterPosition());
            itemClickListener.onItemClickListener(video);


        }

        @Override
        public boolean onLongClick(View v) {
            FVideo video = videos.get(getAdapterPosition());
            deleteVideoFromFile(context, video);
            return true;
        }

    }

}
