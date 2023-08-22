package com.zainal.moviedb.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ActivityDiscoverBinding
import com.zainal.moviedb.model.DiscoverResultsItem
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_GENRE_DATA
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.adapter.DiscoverAdapter
import com.zainal.moviedb.viewmodel.DiscoverViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverActivity: BaseActivity() {
    lateinit var discoverBinding: ActivityDiscoverBinding
    val discoverViewModel by viewModel<DiscoverViewModel>()

    var genresItem: GenresItem? = null
    var typeCategory: String = TypeCategory.MOVIE.name

    val discoverAdapter = DiscoverAdapter(::discoverAdapterCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        discoverBinding = ActivityDiscoverBinding.inflate(layoutInflater)
        setContentView(discoverBinding.root)

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        with(discoverViewModel) {
            vmShimmerState().observe(this@DiscoverActivity) {
                with(discoverBinding) {
                    when (it) {
                        ShimmerState.START -> {
                            rcvDiscover.visibility = View.INVISIBLE
                            discoverShimmer.apply {
                                visibility = View.VISIBLE
                                if (!isShimmerStarted) {
                                    startShimmer()
                                }
                            }
                        }
                        else -> {
                            discoverShimmer.apply {
                                if (isShimmerStarted) {
                                    stopShimmer()
                                }
                                visibility = View.INVISIBLE
                            }
                            rcvDiscover.visibility = View.VISIBLE
                        }
                    }
                }
            }

            vmDiscoverResultsItem().observe(this@DiscoverActivity) {
                discoverAdapter.setupData(it)
                setupStateView(true)
            }

            getData()
        }
    }

    private fun getData() {
        genresItem?.let {
            setupStateView(false)

            discoverViewModel.getDiscoverData(
                genreId = it.id,
                typeCategory = typeCategory
            )
        }
    }

    private fun setupStateView(b: Boolean) {
        with(discoverBinding) {
            root.isEnabled = b
            nestedScrollView.isEnabled = b
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun initView() {
        with(discoverBinding) {
            root.apply {
                setOnRefreshListener {
                    isRefreshing = false
                    getData()
                }
            }

            genresItem?.let {
                acivMenuIcon.setImageResource(resources.getIdentifier(
                    it.icon,
                    "drawable",
                    packageName
                ))

                mtvTitle.text = it.name
            }

            rcvDiscover.apply {
                adapter = discoverAdapter
                setHasFixedSize(false)
                itemAnimator = null
            }
        }
    }

    private fun initData() {
        intent?.let {
            genresItem = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> it.getParcelableExtra(
                    EXTRA_GENRE_DATA,
                    GenresItem::class.java
                )

                else -> {
                    @Suppress("DEPRECATION")
                    it.getParcelableExtra(EXTRA_GENRE_DATA)
                }
            }

            typeCategory = it.getStringExtra(EXTRA_CATEGORY) ?:TypeCategory.MOVIE.name
        }
    }

    private fun discoverAdapterCallback(
        discoverResultsItem: DiscoverResultsItem,
        holder: DiscoverAdapter.DiscoverViewHolder
    ) {
        Picasso.get()
            .load("${BASE_URL_POSTER}${discoverResultsItem.posterPath}")
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .placeholder(R.drawable.poster_placeholder)
            .into(holder.sivPoster)
    }
}