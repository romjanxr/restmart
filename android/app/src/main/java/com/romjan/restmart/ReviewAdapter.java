package com.romjan.restmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<Review> reviews;
    private final int currentUserId;
    private final OnReviewInteractionListener listener;

    public interface OnReviewInteractionListener {
        void onEditReview(Review review);
        void onDeleteReview(Review review);
    }

    public ReviewAdapter(Context context, List<Review> reviews, int currentUserId, OnReviewInteractionListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvReviewAuthor;
        private final TextView tvReviewComment;
        private final RatingBar ratingBar;
        private final LinearLayout llEditDelete;
        private final ImageButton btnEditReview;
        private final ImageButton btnDeleteReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewAuthor = itemView.findViewById(R.id.tvReviewAuthor);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            llEditDelete = itemView.findViewById(R.id.llEditDelete);
            btnEditReview = itemView.findViewById(R.id.btnEditReview);
            btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
        }

        public void bind(final Review review) {
            tvReviewAuthor.setText(review.getUser().getName());
            tvReviewComment.setText(review.getComment());
            ratingBar.setRating(review.getRating());

            if (review.getUser().getId() == currentUserId) {
                llEditDelete.setVisibility(View.VISIBLE);
                btnEditReview.setOnClickListener(v -> listener.onEditReview(review));
                btnDeleteReview.setOnClickListener(v -> listener.onDeleteReview(review));
            } else {
                llEditDelete.setVisibility(View.GONE);
            }
        }
    }
}
