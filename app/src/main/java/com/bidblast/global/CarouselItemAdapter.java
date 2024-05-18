package com.bidblast.global;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.databinding.TemplateCarouselItemBinding;
import com.bidblast.model.HypermediaFile;

import java.util.List;

public class CarouselItemAdapter extends ListAdapter<HypermediaFile, CarouselItemAdapter.CarouselItemViewHolder> {
    private OnCarouselItemClickListener onCarouselItemClickListener;
    private final LiveData<List<HypermediaFile>> hypermediaFilesFullList;

    public static final DiffUtil.ItemCallback<HypermediaFile> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<HypermediaFile>() {
            @Override
            public boolean areItemsTheSame(@NonNull HypermediaFile oldItem, @NonNull HypermediaFile newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull HypermediaFile oldItem, @NonNull HypermediaFile newItem) {
                return oldItem.equals(newItem);
            }
        };

    protected CarouselItemAdapter(LiveData<List<HypermediaFile>> hypermediaFilesFullList) {
        super(DIFF_CALLBACK);
        this.hypermediaFilesFullList = hypermediaFilesFullList;
    }

    @NonNull
    @Override
    public CarouselItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateCarouselItemBinding binding =
            TemplateCarouselItemBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new CarouselItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselItemViewHolder holder, int position) {
        HypermediaFile hypermediaFile = getItem(position);
        holder.bind(hypermediaFile);
    }

    public void setOnCarouselItemClickListener(OnCarouselItemClickListener onCarouselItemClickListener) {
        this.onCarouselItemClickListener = onCarouselItemClickListener;
    }

    public class CarouselItemViewHolder extends RecyclerView.ViewHolder {
        private final TemplateCarouselItemBinding binding;

        public CarouselItemViewHolder(@NonNull TemplateCarouselItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HypermediaFile hypermediaFile) {
            String hypermediaType = hypermediaFile.getMimeType();

            if(hypermediaType.startsWith("image")) {
                setupImageClickListener(hypermediaFile);
            } else if (hypermediaType.startsWith("video")) {
                setupVideoClickListener(hypermediaFile);
            }
        }

        private void setupVideoClickListener(HypermediaFile currentFile) {
            binding.getRoot().setOnClickListener(v -> {
                onCarouselItemClickListener.onVideoSelected(currentFile);

                toggleCarouselItemStyle(currentFile);
            });
        }

        private void setupImageClickListener(HypermediaFile currentFile) {
            binding.getRoot().setOnClickListener(v -> {
                onCarouselItemClickListener.onImageSelected(currentFile);

                toggleCarouselItemStyle(currentFile);
            });
        }

        private void toggleCarouselItemStyle(HypermediaFile currentFile) {
            List<HypermediaFile> filesList =  hypermediaFilesFullList.getValue();
            if(filesList != null) {
                for(HypermediaFile file : filesList) {
                    if(file.getId() == currentFile.getId()) {
                        binding.itemImageView.setBackgroundResource(R.drawable.carousel_selected_item_background);
                    } else {
                        binding.itemImageView.setBackgroundResource(R.drawable.carousel_item_background);
                    }
                }
            }
        }
    }

    interface OnCarouselItemClickListener {
        void onImageSelected(HypermediaFile hypermediaFile);
        void onVideoSelected(HypermediaFile hypermediaFile);
    }
}
