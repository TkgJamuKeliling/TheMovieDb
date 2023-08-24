package com.zainal.moviedb.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ActivityDiscoverBinding
import com.zainal.moviedb.model.response.DiscoverResultsItem
import com.zainal.moviedb.model.response.GenreResponse
import com.zainal.moviedb.model.response.GenresItem
import com.zainal.moviedb.util.BottomViewState
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_GENRE_DATA
import com.zainal.moviedb.util.Constant.EXTRA_ID
import com.zainal.moviedb.util.Constant.EXTRA_LIST_GENRE
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.adapter.DiscoverAdapter
import com.zainal.moviedb.viewmodel.DiscoverViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverActivity: BaseActivity() {
    private lateinit var discoverBinding: ActivityDiscoverBinding
    private val discoverViewModel by viewModel<DiscoverViewModel>()

    private var genresItem: GenresItem? = null
    var typeCategory: String = TypeCategory.MOVIE.name
    private var genreResponse: GenreResponse? = null

    private val discoverAdapter = DiscoverAdapter(::discoverAdapterCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        discoverBinding = ActivityDiscoverBinding.inflate(layoutInflater)
        setContentView(discoverBinding.root)

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        discoverViewModel.vmDiscoverResultsItem().observe(this@DiscoverActivity) {
            discoverAdapter.setupData(it)
        }

        getData()
    }

    private fun getData(isPageOne: Boolean = true) {
        genresItem?.let {
            discoverViewModel.getDiscoverData(
                it.id,
                typeCategory,
                isPageOne
            ) { shimmerState, scrollState, bottomViewState, s ->
                with(discoverBinding) {
                    when (shimmerState) {
                        ShimmerState.START -> {
                            rcvDiscover.visibility = INVISIBLE
                            discoverShimmer.apply {
                                visibility = VISIBLE
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
                                visibility = INVISIBLE
                            }
                            rcvDiscover.visibility = VISIBLE
                        }
                    }

                    root.isEnabled = scrollState.state
                    btnMenu.isEnabled = scrollState.state
                    nestedScrollView.isEnabled = scrollState.state

                    when (bottomViewState) {
                        BottomViewState.LOADING -> loadingView.root.visibility = VISIBLE
                        BottomViewState.STUCK -> {
                            loadingView.root.visibility = INVISIBLE
                            stuckView.root.visibility = VISIBLE
                        }
                        else -> {
                            loadingView.root.visibility = INVISIBLE
                            stuckView.root.visibility = INVISIBLE
                        }
                    }
                }

                s?.let { s1 ->
                    showGlobalDialog(s1)
                }
            }
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

            btnMenu.apply {
                text = genresItem?.name
                setOnClickListener {
                    val listGenreItems = genreResponse?.genres?.filterNotNull()

                    PopupMenu(
                        this@DiscoverActivity,
                        it
                    ).apply {
                        menu.apply {
                            listGenreItems?.forEach { item ->
                                add(item.name)
                            }
                        }
                        setOnMenuItemClickListener { menuItem ->
                            menuItem?.title?.let { s ->
                                btnMenu.text = s
                                genresItem = listGenreItems?.find { item ->
                                    item.name == s
                                }
                                getData()
                            }
                            return@setOnMenuItemClickListener true
                        }
                        show()
                    }
                }
            }

            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                    if (scrollY != 0 && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        getData(false)
                    }
                }
            )

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

            genreResponse = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> it.getParcelableExtra(
                    EXTRA_LIST_GENRE,
                    GenreResponse::class.java
                )

                else -> {
                    @Suppress("DEPRECATION")
                    it.getParcelableExtra(EXTRA_LIST_GENRE)
                }
            }
        }
    }

    private fun discoverAdapterCallback(
        discoverResultsItem: DiscoverResultsItem,
        holder: DiscoverAdapter.DiscoverViewHolder
    ) {
        with(holder.sivPoster) {
            Glide.with(this)
                .load("${BASE_URL_POSTER}${discoverResultsItem.posterPath}")
                .override(165, 250)
                .centerCrop()
                .placeholder(R.drawable.poster_placeholder)
                .into(this)

            setOnClickListener {
                startActivity(Intent(
                    this@DiscoverActivity,
                    DetailActivity::class.java
                ).apply {
                    putExtra(EXTRA_ID, discoverResultsItem.id)
                    putExtra(EXTRA_CATEGORY, typeCategory)
                })
            }
        }
    }
}