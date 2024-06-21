package com.bidblast.usecases.createauction;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;

import java.util.List;
public class MediaPagerAdapter extends RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder> {
    private final List<Object> mediaItems;
    private final Context context;

    public MediaPagerAdapter(List<Object> mediaItems, Context context) {
        this.mediaItems = mediaItems;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_upload_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Object item = mediaItems.get(position);
        if (item instanceof Integer) {
            holder.bind((Integer) item, context);
        } else if (item instanceof Uri) {
            String mimeType = context.getContentResolver().getType((Uri) item);
            if (mimeType != null && mimeType.startsWith("image")) {
                holder.bind((Uri) item);
            } else if (mimeType != null && mimeType.startsWith("video")) {
                holder.bind(new VideoItem((Uri) item));
            }
        } else if (item instanceof VideoItem) {
            holder.bind((VideoItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public void updateMedia(List<Object> newItems) {
        mediaItems.clear();
        mediaItems.addAll(newItems);
        notifyDataSetChanged();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final ImageView videoOverlay;
        private final VideoView videoView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mediaImageView);
            videoOverlay = itemView.findViewById(R.id.videoOverlay);
            videoView = itemView.findViewById(R.id.mediaVideoView);
        }

        public void bind(Integer resourceId, Context context) {
            imageView.setImageResource(resourceId);
            videoOverlay.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }

        public void bind(Uri uri) {
            imageView.setImageURI(uri);
            videoOverlay.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }

        public void bind(VideoItem videoItem) {
            videoView.setVideoURI(videoItem.getUri());
            videoView.setVisibility(View.VISIBLE);
            videoOverlay.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            // Listener para iniciar reproducción automáticamente
            videoView.setOnPreparedListener(mp -> {
                videoOverlay.setVisibility(View.GONE);
                videoView.start();
            });
            videoView.setOnCompletionListener(mp -> {
                videoOverlay.setVisibility(View.VISIBLE);
            });
        }
    }
}

class VideoItem {
    private final Uri uri;

    public VideoItem(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}

