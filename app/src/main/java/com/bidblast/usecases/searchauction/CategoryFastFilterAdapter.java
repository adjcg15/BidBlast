package com.bidblast.usecases.searchauction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.databinding.TemplateCategoryFastFilterBinding;
import com.bidblast.model.AuctionCategory;

public class CategoryFastFilterAdapter extends ListAdapter<AuctionCategory, CategoryFastFilterAdapter.CategoryViewHolder> {
    private SearchAuctionViewModel viewModel;
    private OnFilterClickListener onFilterClickListener;

    public static final DiffUtil.ItemCallback<AuctionCategory> DIFF_CALLBACK = new DiffUtil.ItemCallback<AuctionCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull AuctionCategory oldItem, @NonNull AuctionCategory newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AuctionCategory oldItem, @NonNull AuctionCategory newItem) {
            return oldItem.equals(newItem);
        }
    };

    protected CategoryFastFilterAdapter(SearchAuctionViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CategoryFastFilterAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateCategoryFastFilterBinding binding =
            TemplateCategoryFastFilterBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryFastFilterAdapter.CategoryViewHolder holder, int position) {
        AuctionCategory category = getItem(position);
        holder.bind(category);
    }

    public void setOnFilterClickListener(OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TemplateCategoryFastFilterBinding binding;

        public CategoryViewHolder(@NonNull TemplateCategoryFastFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AuctionCategory category) {
            binding.categoryTitleTextView.setText(category.getTitle());

            binding.getRoot().setOnClickListener(v -> {
                onFilterClickListener.onFilterClick(category);
            });
            setupCategoryFiltersListListener(category);

            binding.executePendingBindings();
        }

        private void setupCategoryFiltersListListener(AuctionCategory category) {
            viewModel.getCategoryFiltersSelected().observeForever(categoryFilters -> {
                if(categoryFilters.contains(category.getId())) {
                    binding.categoryTitleTextView.setBackgroundResource(R.drawable.filled_black_rounded_border);
                    binding.categoryTitleTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.white));
                } else {
                    binding.categoryTitleTextView.setBackgroundResource(R.drawable.black_rounded_border);
                    binding.categoryTitleTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
                }
            });
        }
    }

    interface OnFilterClickListener {
        void onFilterClick(AuctionCategory category);
    }
}
