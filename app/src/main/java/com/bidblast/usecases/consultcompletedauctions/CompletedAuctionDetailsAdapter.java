package com.bidblast.usecases.consultcompletedauctions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.databinding.TemplateAuctionDetailsWithStateBinding;
import com.bidblast.lib.CurrencyToolkit;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.model.Offer;
import com.bidblast.model.User;

public class CompletedAuctionDetailsAdapter extends ListAdapter<Auction, CompletedAuctionDetailsAdapter.CompletedAuctionViewHolder> {
    private Context context;

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

    protected CompletedAuctionDetailsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public CompletedAuctionDetailsAdapter.CompletedAuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateAuctionDetailsWithStateBinding binding =
                TemplateAuctionDetailsWithStateBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new CompletedAuctionDetailsAdapter.CompletedAuctionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedAuctionDetailsAdapter.CompletedAuctionViewHolder holder, int position) {
        Auction auction = getItem(position);
        holder.bind(auction);
        holder.binding.auctioneerPhoneNumberImageButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", auction.getAuctioneer().getPhoneNumber());
            clipboard.setPrimaryClip(clip);
        });
        holder.binding.auctioneerEmailImageButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", auction.getAuctioneer().getEmail());
            clipboard.setPrimaryClip(clip);
        });
    }

    public static class CompletedAuctionViewHolder extends RecyclerView.ViewHolder {
        private final TemplateAuctionDetailsWithStateBinding binding;

        public CompletedAuctionViewHolder(@NonNull TemplateAuctionDetailsWithStateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Auction auction) {
            User auctioneer = auction.getAuctioneer();
            Offer lastOffer = auction.getLastOffer();

            binding.auctionSecondTitleTextView.setText(auction.getTitle());
            HypermediaFile auctionImage = auction.getMediaFiles().get(0);
            binding.auctionMainImageImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctionImage.getContent()));
            String purchasedDate = DateToolkit.parseToFullDateWithHour(auction.getClosesAt());
            binding.auctionFinalAmountTextView.setText(
                String.format(
                    binding.getRoot().getContext().getString(R.string.consultcompletedauctions_purchased_date_message),
                    purchasedDate
                )
            );

            binding.auctioneerNameTextView.setText(auction.getAuctioneer().getFullName());
            String auctioneerPhoneNumber = auctioneer.getPhoneNumber();
            if (auctioneerPhoneNumber == null || auctioneerPhoneNumber.isEmpty()) {
                binding.auctioneerPhoneNumberImageButton.setVisibility(View.GONE);
            }
            String auctioneerAvatar = auctioneer.getAvatar();
            if(auctioneerAvatar != null && !auctioneerAvatar.isEmpty()) {
                binding.auctioneerAvatarImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctioneerAvatar));
            }

            binding.auctionFinalAmountTextView.setText(CurrencyToolkit.parseToMXN(lastOffer.getAmount()));

            binding.executePendingBindings();
        }
    }
}
