package com.zainal.moviedb.view.activity

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ActivityReviewBinding
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.Constant.EXTRA_POSTER_PATH
import com.zainal.moviedb.util.Constant.EXTRA_REVIEW_ID
import com.zainal.moviedb.util.Constant.EXTRA_YEAR
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.viewmodel.ReviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewActivity: BaseActivity() {
    private lateinit var reviewItemBinding: ActivityReviewBinding
    private val reviewViewModel by viewModel<ReviewViewModel>()

    private var reviewId: String? = null
    private var yearInfo: String? = null
    var posterPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewItemBinding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(reviewItemBinding.root)

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        with(reviewViewModel) {
            vmErrorMsg().observe(this@ReviewActivity) {
                it?.let {
                    showGlobalDialog(it)
                    isProcessGetDetail = false
                    postErrorMsg()
                }
            }

            vmShimmerState().observe(this@ReviewActivity) {
                with(reviewItemBinding) {
                    when (it) {
                        ShimmerState.START -> {
                            mcvReview.visibility = INVISIBLE
                            nestedScrollView.visibility = INVISIBLE
                            shimmerReview.apply {
                                visibility = VISIBLE
                                if (!isShimmerStarted) {
                                    startShimmer()
                                }
                            }
                        }

                        else -> {
                            shimmerReview.apply {
                                if (isShimmerStarted) {
                                    stopShimmer()
                                }
                                visibility = INVISIBLE
                            }
                            mcvReview.visibility = VISIBLE
                            nestedScrollView.visibility = VISIBLE
                        }
                    }
                }
            }

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
        }
    }

    private fun getReviewDetail() {
        reviewId?.let {
            reviewViewModel.getReviewDetail(
                it,
                yearInfo,
                ::setupSwipeLayout
            )
        }
    }
    
    private fun setupSwipeLayout(enable: Boolean) {
        reviewItemBinding.root.isEnabled = enable
    }

    private fun initView() {
        with(reviewItemBinding) {
            root.apply {
                setOnRefreshListener {
                    isRefreshing = false
                    getReviewDetail()
                }
            }

            posterPath?.let {
                Glide.with(this@ReviewActivity)
                    .load("${BASE_URL_POSTER}$it")
                    .override(100, 150)
                    .centerCrop()
                    .placeholder(R.drawable.poster_placeholder)
                    .into(acivPoster)
            }
        }

        getReviewDetail()
    }

    private fun initData() {
        intent?.let {
            reviewId = it.getStringExtra(EXTRA_REVIEW_ID)
            yearInfo = it.getStringExtra(EXTRA_YEAR)
            posterPath = it.getStringExtra(EXTRA_POSTER_PATH)
        }
    }
}