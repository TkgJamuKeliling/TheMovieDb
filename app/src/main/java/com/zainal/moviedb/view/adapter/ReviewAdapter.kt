package com.zainal.moviedb.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.base.BaseRecyclerView
import com.zainal.moviedb.base.Equatable
import com.zainal.moviedb.databinding.ReviewItemBinding
import com.zainal.moviedb.model.response.ReviewResultsItem

class ReviewAdapter(var callback: (ReviewResultsItem, ReviewItemViewHolder) -> Unit): BaseRecyclerView() {

    fun setupData(listModel: List<ReviewResultsItem>?) = updateData(listModel)

    class ReviewItemViewHolder(reviewItemBinding: ReviewItemBinding): RecyclerView.ViewHolder(reviewItemBinding.root) {
        val mtvTitle = reviewItemBinding.reviewItemHeader.mtvReviewAuthor
        val voteContainer = reviewItemBinding.reviewItemHeader.voteContainer
        val mtvVoteValue = reviewItemBinding.reviewItemHeader.voteValue
        val mtvSubTitle = reviewItemBinding.reviewItemHeader.mtvReviewDate
        val acivAvatar = reviewItemBinding.reviewItemHeader.acivAvatar
        val mtvAvatar = reviewItemBinding.reviewItemHeader.mtvAvatar
        val mtvReview = reviewItemBinding.mtvReviewValue
        val mtvContinue = reviewItemBinding.mtvContinue
    }

    override fun onGetAdapter() = this

    override fun onGetBindHolder(holder: RecyclerView.ViewHolder, model: Equatable) {
        if (model is ReviewResultsItem && holder is ReviewItemViewHolder) {
            holder.acivAvatar.layout(0, 0, 0, 0)
            callback(
                model,
                holder
            )
        }
    }

    override fun onGetViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReviewItemViewHolder(
            ReviewItemBinding.inflate(inflater, parent, false)
        ).also {
            it.setIsRecyclable(false)
        }
    }
}