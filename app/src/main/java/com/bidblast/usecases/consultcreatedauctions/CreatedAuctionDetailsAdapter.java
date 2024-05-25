package com.bidblast.usecases.consultcreatedauctions;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
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
import com.bidblast.model.AuctionReview;
import com.bidblast.model.HypermediaFile;
import com.bidblast.model.Offer;
import com.bidblast.model.User;
import com.bidblast.usecases.consultcompletedauctions.CompletedAuctionDetailsAdapter;

public class CreatedAuctionDetailsAdapter extends ListAdapter<Auction, CreatedAuctionDetailsAdapter.CreatedAuctionViewHolder> {
    private Context context;
    private static final String STATE_PROPOSED = "PROPUESTA";
    private static final String STATE_PUBLISHED = "PUBLICADA";
    private static final String STATE_REJECTED= "RECHAZADA";
    private static final String STATE_CLOSED = "CERRADA";
    private static final String STATE_CONCRETE = "PUBLICADA";
    private static final String STATE_FINISHED = "FINALIZADA";

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

    protected CreatedAuctionDetailsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public CreatedAuctionDetailsAdapter.CreatedAuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateAuctionDetailsWithStateBinding binding =
                TemplateAuctionDetailsWithStateBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new CreatedAuctionDetailsAdapter.CreatedAuctionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CreatedAuctionDetailsAdapter.CreatedAuctionViewHolder holder, int position) {
        Auction auction = getItem(position);
        holder.bind(auction);
        holder.binding.auctioneerPhoneNumberImageButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", auction.getLastOffer().getCustomer().getPhoneNumber());
            clipboard.setPrimaryClip(clip);
        });
        holder.binding.auctioneerEmailImageButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", auction.getLastOffer().getCustomer().getEmail());
            clipboard.setPrimaryClip(clip);
        });
    }

    public static class CreatedAuctionViewHolder extends RecyclerView.ViewHolder {
        private final TemplateAuctionDetailsWithStateBinding binding;

        public CreatedAuctionViewHolder(@NonNull TemplateAuctionDetailsWithStateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Auction auction) {
            Offer lastOffer;
            AuctionReview review;
            
            HypermediaFile auctionImage = auction.getMediaFiles().get(0);
            binding.auctionMainImageImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctionImage.getContent()));

            if (auction.getAuctionState().equals(STATE_PUBLISHED)) {
                Log.d("PUBLICADA", "PUBLICADA");
                binding.customerInformationLinearLayout.setVisibility(View.GONE);
                binding.auctionFirstTitleTextView.setVisibility(View.GONE);
                binding.auctionRejectedStateMessageTextView.setVisibility(View.GONE);
                binding.auctionMinimumBidTitleTextView.setVisibility(View.GONE);
                binding.auctionMinimumBidTextView.setVisibility(View.GONE);
                binding.auctionSecondTitleTextView.setText(auction.getTitle());
                String timeLeft = DateToolkit.parseToFullDateWithHour(auction.getUpdatedDate());
                binding.auctionDescriptionTextView.setText(
                        String.format(
                                binding.getRoot().getContext().getString(
                                        R.string.consultcreatedauctions_available_date_message
                                ),
                                timeLeft
                        )
                );
                if (auction.getLastOffer() != null) {
                    binding.auctionWithoutOffersStateMessageTextView.setVisibility(View.GONE);
                    lastOffer = auction.getLastOffer();
                    binding.auctionFinalAmountTextView.setText(CurrencyToolkit.parseToMXN(lastOffer.getAmount()));
                } else {
                    binding.auctionWithoutOffersStateMessageTextView.setText(
                            R.string.consultcreatedauctions_auction_published_without_offers_text
                    );
                    binding.auctionLastOfferTitleTextView.setVisibility(View.GONE);
                    binding.auctionFinalAmountTextView.setVisibility(View.GONE);
                    binding.viewMadeOffersButton.setVisibility(View.GONE);
                }
            }

            binding.executePendingBindings();
        }
    }
}
