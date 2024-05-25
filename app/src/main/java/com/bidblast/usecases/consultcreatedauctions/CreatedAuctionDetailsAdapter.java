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
    private static final String STATE_CONCRETE = "CONCRETADA";
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
            HypermediaFile auctionImage = auction.getMediaFiles().get(0);

            setTemplateElementsVisible();

            binding.auctionMainImageImageView.setImageBitmap(ImageToolkit.parseBitmapFromBase64(auctionImage.getContent()));

            if (auction.getAuctionState().equals(STATE_PUBLISHED)) {
                loadPublishedAuctionInformation(auction);
            }
            if (auction.getAuctionState().equals(STATE_PROPOSED)) {
                loadProposedAuctionInformation(auction);
            }
            if (auction.getAuctionState().equals(STATE_CLOSED)) {
                loadClosedAuctionInformation(auction);
            }
            if (auction.getAuctionState().equals(STATE_REJECTED)) {
                loadRejectedAuctionInformation(auction);
            }
            if (auction.getAuctionState().equals(STATE_CONCRETE) || auction.getAuctionState().equals(STATE_FINISHED)) {
                loadConcreteOrFinishedAuctionInformation(auction);
            }

            binding.executePendingBindings();
        }

        private void setTemplateElementsVisible() {
            binding.customerInformationLinearLayout.setVisibility(View.VISIBLE);
            binding.auctionFirstTitleTextView.setVisibility(View.VISIBLE);
            binding.auctionRejectedStateMessageTextView.setVisibility(View.VISIBLE);
            binding.auctionMinimumBidTitleTextView.setVisibility(View.VISIBLE);
            binding.auctionMinimumBidTextView.setVisibility(View.VISIBLE);
            binding.auctionSecondTitleTextView.setVisibility(View.VISIBLE);
            binding.auctionWithoutOffersStateMessageTextView.setVisibility(View.VISIBLE);
            binding.auctionLastOfferTitleTextView.setVisibility(View.VISIBLE);
            binding.auctionFinalAmountTextView.setVisibility(View.VISIBLE);
            binding.viewMadeOffersButton.setVisibility(View.VISIBLE);
        }

        private void loadPublishedAuctionInformation(Auction auction) {
            Offer lastOffer;
            binding.customerInformationLinearLayout.setVisibility(View.GONE);
            binding.auctionFirstTitleTextView.setVisibility(View.GONE);
            binding.auctionRejectedStateMessageTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTextView.setVisibility(View.GONE);
            binding.auctionSecondTitleTextView.setText(auction.getTitle());
            String timeLeft = DateToolkit.parseToFullDateWithHour(auction.getClosesAt());
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

        private void loadProposedAuctionInformation(Auction auction) {
            binding.customerInformationLinearLayout.setVisibility(View.GONE);
            binding.auctionFirstTitleTextView.setVisibility(View.GONE);
            binding.auctionRejectedStateMessageTextView.setVisibility(View.GONE);
            binding.auctionWithoutOffersStateMessageTextView.setVisibility(View.GONE);
            binding.viewMadeOffersButton.setVisibility(View.GONE);
            binding.auctionSecondTitleTextView.setText(auction.getTitle());
            binding.auctionLastOfferTitleTextView.setText(
                    R.string.consultcreatedauctions_auction_base_price_title
            );

            String firstPart = binding.getRoot().getContext().getString(
                    R.string.consultcreatedauctions_first_proposed_time_message
            );
            String secondPart = binding.getRoot().getContext().getString(
                    R.string.consultcreatedauctions_second_proposed_time_message
            );
            String combinedMessageTemplate = firstPart + secondPart;
            String fullMessage = String.format(combinedMessageTemplate, "", auction.getDaysAvailable());
            binding.auctionDescriptionTextView.setText(fullMessage);
            binding.auctionFinalAmountTextView.setText(CurrencyToolkit.parseToMXN(auction.getBasePrice()));
            binding.auctionMinimumBidTextView.setText(CurrencyToolkit.parseToMXN(auction.getMinimumBid()));
        }

        private void loadClosedAuctionInformation(Auction auction) {
            binding.customerInformationLinearLayout.setVisibility(View.GONE);
            binding.auctionFirstTitleTextView.setVisibility(View.GONE);
            binding.auctionRejectedStateMessageTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTextView.setVisibility(View.GONE);
            binding.auctionLastOfferTitleTextView.setVisibility(View.GONE);
            binding.auctionFinalAmountTextView.setVisibility(View.GONE);
            binding.viewMadeOffersButton.setVisibility(View.GONE);

            binding.auctionSecondTitleTextView.setText(auction.getTitle());
            binding.auctionDescriptionTextView.setText(
                String.format(
                    binding.getRoot().getContext().getString(
                            R.string.consultcreatedauctions_closed_date_message
                    ),
                    DateToolkit.parseToFullDate(auction.getUpdatedDate())
                )
            );
            binding.auctionWithoutOffersStateMessageTextView.setText(
                R.string.consultcreatedauctions_auction_closed_without_offers_text
            );
        }

        private void loadRejectedAuctionInformation(Auction auction) {
            AuctionReview review = auction.getReview();
            binding.customerInformationLinearLayout.setVisibility(View.GONE);
            binding.auctionSecondTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTextView.setVisibility(View.GONE);
            binding.auctionLastOfferTitleTextView.setVisibility(View.GONE);
            binding.auctionFinalAmountTextView.setVisibility(View.GONE);
            binding.viewMadeOffersButton.setVisibility(View.GONE);
            binding.auctionWithoutOffersStateMessageTextView.setVisibility(View.GONE);

            binding.auctionFirstTitleTextView.setText(auction.getTitle());
            binding.auctionRejectedStateMessageTextView.setText(
                String.format(
                    binding.getRoot().getContext().getString(
                            R.string.consultcreatedauctions_rejected_date_message
                    ),
                    DateToolkit.parseToFullDateWithHour(auction.getUpdatedDate())
                )
            );
            binding.auctionDescriptionTextView.setText(review.getComments());
        }

        private void loadConcreteOrFinishedAuctionInformation(Auction auction) {
            User customer = auction.getLastOffer().getCustomer();
            Offer lastOffer = auction.getLastOffer();

            binding.auctionFirstTitleTextView.setVisibility(View.GONE);
            binding.auctionRejectedStateMessageTextView.setVisibility(View.GONE);
            binding.auctionLastOfferTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTitleTextView.setVisibility(View.GONE);
            binding.auctionMinimumBidTextView.setVisibility(View.GONE);
            binding.auctionWithoutOffersStateMessageTextView.setVisibility(View.GONE);
            binding.viewMadeOffersButton.setVisibility(View.GONE);

            binding.auctionSecondTitleTextView.setText(auction.getTitle());
            HypermediaFile auctionImage = auction.getMediaFiles().get(0);
            binding.auctionMainImageImageView.setImageBitmap(
                    ImageToolkit.parseBitmapFromBase64(auctionImage.getContent())
            );
            String soldDate = DateToolkit.parseToFullDateWithHour(auction.getUpdatedDate());
            binding.auctionDescriptionTextView.setText(
                String.format(
                    binding.getRoot().getContext().getString(
                        R.string.consultcompletedauctions_purchased_date_message
                    ),
                    soldDate
                )
            );

            binding.auctioneerNameTextView.setText(customer.getFullName());
            String customerPhoneNumber = customer.getPhoneNumber();
            if (customerPhoneNumber == null || customerPhoneNumber.isEmpty()) {
                binding.auctioneerPhoneNumberImageButton.setVisibility(View.GONE);
            }
            String customerAvatar = customer.getAvatar();
            if(customerAvatar != null && !customerAvatar.isEmpty()) {
                binding.auctioneerAvatarImageView.setImageBitmap(
                        ImageToolkit.parseBitmapFromBase64(customerAvatar)
                );
            }

            binding.auctionFinalAmountTextView.setText(CurrencyToolkit.parseToMXN(lastOffer.getAmount()));
        }
    }
}
