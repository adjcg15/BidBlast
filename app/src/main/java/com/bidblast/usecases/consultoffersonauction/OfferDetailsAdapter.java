package com.bidblast.usecases.consultoffersonauction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.databinding.TemplateOfferDetailsBinding;
import com.bidblast.lib.CurrencyToolkit;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.Offer;
import com.bidblast.model.User;
import com.bidblast.usecases.consultcreatedauctions.CreatedAuctionDetailsAdapter;

public class OfferDetailsAdapter extends ListAdapter<Offer, OfferDetailsAdapter.OfferViewHolder> {

    private OnAuctionClickListener onAuctionClickListener;
    private Context context;

    public static final DiffUtil.ItemCallback<Offer> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Offer>() {
                @Override
                public boolean areItemsTheSame(@NonNull Offer oldItem, @NonNull Offer newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Offer oldItem, @NonNull Offer newItem) {
                    return oldItem.equals(newItem);
                }
            };

    protected OfferDetailsAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }
    protected OfferDetailsAdapter(@NonNull DiffUtil.ItemCallback<Offer> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public OfferDetailsAdapter.OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TemplateOfferDetailsBinding binding =
                TemplateOfferDetailsBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new OfferDetailsAdapter.OfferViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferDetailsAdapter.OfferViewHolder holder, int position) {
        Offer offer = getItem(position);
        holder.bind(offer);
    }

    public void setOnAuctionClickListener(OfferDetailsAdapter.OnAuctionClickListener onAuctionClickListener) {
        this.onAuctionClickListener = onAuctionClickListener;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        private final TemplateOfferDetailsBinding binding;

        public OfferViewHolder(@NonNull TemplateOfferDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Offer offer) {
            User customer = offer.getCustomer();

            binding.blockCustomerButton.setOnClickListener(v -> {
                onAuctionClickListener.onSeeOffersOnAuctionButtonClick(offer.getCustomer().getId());
            });

            binding.customerAvatarImageView.setImageBitmap(
                    ImageToolkit.parseBitmapFromBase64(customer.getAvatar())
            );
            binding.customerNameTextView.setText(customer.getFullName());
            String offerDate = DateToolkit.parseToFullDateWithHour(offer.getCreationDate());
            binding.customerOfferDateTextView.setText(offerDate);
            String offerAmount = CurrencyToolkit.parseToMXN(offer.getAmount());
            binding.customerOfferAmountTextView.setText(offerAmount);
        }
    }

    interface OnAuctionClickListener {
        void onSeeOffersOnAuctionButtonClick(int idAuction);
    }
}
