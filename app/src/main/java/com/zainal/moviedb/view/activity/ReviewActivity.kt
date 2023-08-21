package com.zainal.moviedb.view.activity

import android.os.Bundle
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ReviewItemDetailBinding
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.Constant.EXTRA_POSTER_PATH
import com.zainal.moviedb.util.Constant.EXTRA_REVIEW_ID
import com.zainal.moviedb.util.Constant.EXTRA_YEAR
import com.zainal.moviedb.viewmodel.ReviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewActivity: BaseActivity() {
    lateinit var reviewItemBinding: ReviewItemDetailBinding
    val reviewViewModel by viewModel<ReviewViewModel>()

    var reviewId: String? = null
    var yearInfo: String? = null
    var posterPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewItemBinding = ReviewItemDetailBinding.inflate(layoutInflater)
        setContentView(reviewItemBinding.root)

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        with(reviewViewModel) {
            vmTitle().observe(this@ReviewActivity) {
                it?.let {
                    reviewItemBinding.mtvTitle.text = it
                }
            }

            vmSubTitle().observe(this@ReviewActivity) {
                it?.let {
                    reviewItemBinding.mtvSubTitle.text = getString(
                        R.string.review_note,
                        it[0],
                        it[1]
                    )
                }
            }

            vmContent().observe(this@ReviewActivity) {
                it?.let {
                    reviewItemBinding.mtvReviewValue.text = it
                }
            }

            reviewId?.let {
                getReviewDetail(
                    it,
                    yearInfo
                )
            }
        }
    }

    private fun initView() {
        with(reviewItemBinding) {
            posterPath?.let {
                Picasso.get()
                    .load("${Constant.BASE_URL_POSTER}$it")
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.poster_placeholder)
                    .into(acivPoster)
            }
        }
    }

    private fun initData() {
        intent?.let {
            reviewId = it.getStringExtra(EXTRA_REVIEW_ID)
            yearInfo = it.getStringExtra(EXTRA_YEAR)
            posterPath = it.getStringExtra(EXTRA_POSTER_PATH)
        }
    }
}