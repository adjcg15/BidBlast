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
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.HypermediaFile;

import java.util.List;

public class CarouselItemAdapter extends ListAdapter<HypermediaFile, CarouselItemAdapter.CarouselItemViewHolder> {
    private final CarouselViewModel carouselViewModel;

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

    public CarouselItemAdapter(CarouselViewModel carouselViewModel) {
        super(DIFF_CALLBACK);
        this.carouselViewModel = carouselViewModel;
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

    public class CarouselItemViewHolder extends RecyclerView.ViewHolder {
        private final TemplateCarouselItemBinding binding;

        public CarouselItemViewHolder(@NonNull TemplateCarouselItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HypermediaFile hypermediaFile) {
            setupSelectedItemChangeListener(hypermediaFile);
            setupItemClickListener(hypermediaFile);

            String hypermediaType = hypermediaFile.getMimeType();
            if(hypermediaType.startsWith("image")) {
                binding.itemImageView.setImageBitmap(
                    ImageToolkit.parseBitmapFromBase64(hypermediaFile.getContent())
                );
            }
        }

        private void setupItemClickListener(HypermediaFile currentFile) {
            binding.getRoot().setOnClickListener(v -> {
                carouselViewModel.setSelectedFile(currentFile);
            });
        }

        private void setupSelectedItemChangeListener(HypermediaFile currentFile) {
            carouselViewModel.getSelectedFile().observeForever(selectedFile -> {
                if(selectedFile != null && currentFile.getId() == selectedFile.getId()) {
                    binding.itemContainerConstraintLayout.setBackgroundResource(R.drawable.carousel_selected_item_background);
                } else {
                    binding.itemContainerConstraintLayout.setBackgroundResource(R.drawable.carousel_item_background);
                }
            });
        }
    }
}
