package com.bidblast.usecases.consultcreatedauctions;

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
            ClipData clip = ClipData.newPlainText("label", auction.getAuctioneer().getPhoneNumber());
            clipboard.setPrimaryClip(clip);
        });
        holder.binding.auctioneerEmailImageButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", auction.getAuctioneer().getEmail());
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
            if (auction.getAuctionState() == STATE_PUBLISHED) {

            }

            binding.executePendingBindings();
        }
    }
}
