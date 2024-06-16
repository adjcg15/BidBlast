package com.bidblast.usecases.consultaauctioncategories;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bidblast.R;
import com.bidblast.model.AuctionCategory;
public class AuctionCategoryAdapter extends ListAdapter<AuctionCategory, AuctionCategoryAdapter.AuctionCategoryViewHolder> {
    private Context context;

    public static final DiffUtil.ItemCallback<AuctionCategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AuctionCategory>() {
                @Override
                public boolean areItemsTheSame(@NonNull AuctionCategory oldItem, @NonNull AuctionCategory newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull AuctionCategory oldItem, @NonNull AuctionCategory newItem) {
                    return oldItem.equals(newItem);
                }
            };

    protected AuctionCategoryAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public AuctionCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.template_category_details, parent, false);
        return new AuctionCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuctionCategoryViewHolder holder, int position) {
        AuctionCategory category = getItem(position);
        holder.bind(category);
    }

    public class AuctionCategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private LinearLayout keywordsLayout;
        private Button editCategoryButton;

        public AuctionCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleCategoryTextView);
            descriptionTextView = itemView.findViewById(R.id.categoryDescriptionTextView);
            keywordsLayout = itemView.findViewById(R.id.categoryKeywordsLinearLayout);
            editCategoryButton = itemView.findViewById(R.id.editCategoryButton);
        }

        public void bind(AuctionCategory category) {
            titleTextView.setText(category.getTitle());
            descriptionTextView.setText(category.getDescription());

            keywordsLayout.removeAllViews();
            for (String keyword : category.getKeywords().split(",")) {
                TextView keywordTextView = new TextView(itemView.getContext());
                keywordTextView.setText(keyword.trim());
                keywordTextView.setBackgroundResource(R.drawable.black_rounded_border);
                keywordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                keywordTextView.setPadding(25, 25, 25, 25);
                keywordTextView.setTypeface(null, Typeface.BOLD);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 8, 8, 8);
                keywordTextView.setLayoutParams(params);

                keywordsLayout.addView(keywordTextView);
            }
        }
    }
}
