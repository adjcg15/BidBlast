package com.bidblast.usecases.searchauction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.databinding.TemplatePriceFilterBinding;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.PriceRange;

public class PriceFilterAdapter extends ListAdapter<PriceRange, PriceFilterAdapter.PriceRangeViewHolder> {
    private final SearchAuctionViewModel viewModel;
    private OnFilterClickListener onFilterClickListener;

    public static final DiffUtil.ItemCallback<PriceRange> DIFF_CALLBACK = new DiffUtil.ItemCallback<PriceRange>() {
        @Override
        public boolean areItemsTheSame(@NonNull PriceRange oldItem, @NonNull PriceRange newItem) {
            return oldItem.getLabel().equals(newItem.getLabel());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PriceRange oldItem, @NonNull PriceRange newItem) {
            return oldItem.equals(newItem);
        }
    };

    protected PriceFilterAdapter(SearchAuctionViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public PriceFilterAdapter.PriceRangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplatePriceFilterBinding binding =
            TemplatePriceFilterBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new PriceRangeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceFilterAdapter.PriceRangeViewHolder holder, int position) {
        PriceRange priceRange = getItem(position);
        holder.bind(priceRange);
    }

    public void setOnFilterClickListener(PriceFilterAdapter.OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public class PriceRangeViewHolder extends RecyclerView.ViewHolder {
        private final TemplatePriceFilterBinding binding;

        public PriceRangeViewHolder(@NonNull TemplatePriceFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PriceRange range) {
            binding.priceTextView.setText(range.getLabel());

            binding.getRoot().setOnClickListener(v -> {
                onFilterClickListener.onFilterClick(range);
            });
            setupPriceFiltersListListener(range);

            binding.executePendingBindings();
        }

        private void setupPriceFiltersListListener(PriceRange priceRange) {
            viewModel.getPriceFilterSelected().observeForever(priceFilterSelected -> {
                if(!priceRange.equals(priceFilterSelected)) {
                    binding.priceTextView.setBackgroundResource(R.drawable.black_rounded_border);
                    binding.priceTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
                } else {
                    binding.priceTextView.setBackgroundResource(R.drawable.filled_black_rounded_border);
                    binding.priceTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.white));
                }
            });
        }
    }

    interface OnFilterClickListener {
        void onFilterClick(PriceRange priceRange);
    }
}
