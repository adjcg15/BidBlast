package com.bidblast.usecases.searchauction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.databinding.TemplateAuctionDetailsBinding;
import com.bidblast.lib.CurrencyToolkit;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.model.Offer;
import com.bidblast.model.User;

public class AuctionDetailsAdapter extends ListAdapter<Auction, AuctionDetailsAdapter.AuctionViewHolder> {
    public static final DiffUtil.ItemCallback<Auction> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Auction>() {
            @Override
            public boolean areItemsTheSame(@NonNull Auction oldItem, @NonNull Auction newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Auction oldItem, @NonNull Auction newItem) {
                return oldItem.equals(newItem);
            }
        };

    protected AuctionDetailsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public AuctionDetailsAdapter.AuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateAuctionDetailsBinding binding =
            TemplateAuctionDetailsBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new AuctionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionDetailsAdapter.AuctionViewHolder holder, int position) {
        Auction auction = getItem(position);
        holder.bind(auction);
    }

    public static class AuctionViewHolder extends RecyclerView.ViewHolder {
        private final TemplateAuctionDetailsBinding binding;

        public AuctionViewHolder(@NonNull TemplateAuctionDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Auction auction) {
            User auctioneer = auction.getAuctioneer();
            Offer lastOffer = auction.getLastOffer();

            binding.auctionTitleTextView.setText(auction.getTitle());
            HypermediaFile auctionImage = auction.getMediaFiles().get(0);
            binding.auctionMainImageImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctionImage.getContent()));
            String closingDate = DateToolkit.parseToFullDateWithHour(auction.getClosesAt());
            binding.auctionClosingDateTextView.setText(
                String.format(
                    binding.getRoot().getContext().getString(R.string.searchauction_available_until),
                    closingDate
                )
            );

            binding.auctioneerNameTextView.setText(auction.getAuctioneer().getFullName());
            String auctioneerAvatar = auctioneer.getAvatar();
            if(auctioneerAvatar != null && !auctioneerAvatar.isEmpty()) {
                binding.auctioneerAvatarImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctioneerAvatar));
            }

            if(lastOffer != null) {
                binding.auctionLastofferTextView.setText(CurrencyToolkit.parseToMXN(lastOffer.getAmount()));
            } else {
                binding.auctionLastofferTextView.setText(R.string.searchauction_cta_empty_offers);
            }

            binding.executePendingBindings();
        }
    }
}
