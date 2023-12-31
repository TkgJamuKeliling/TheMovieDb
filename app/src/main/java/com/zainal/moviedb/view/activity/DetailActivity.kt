package com.zainal.moviedb.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ActivityDetailBinding
import com.zainal.moviedb.databinding.TrailerDialogBinding
import com.zainal.moviedb.model.response.CastItem
import com.zainal.moviedb.model.response.ReviewResultsItem
import com.zainal.moviedb.model.response.VideoResultsItem
import com.zainal.moviedb.util.BottomViewState
import com.zainal.moviedb.util.Constant.BASE_URL_AVATAR
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_ID
import com.zainal.moviedb.util.Constant.EXTRA_POSTER_PATH
import com.zainal.moviedb.util.Constant.EXTRA_REVIEW_ID
import com.zainal.moviedb.util.Constant.EXTRA_YEAR
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.adapter.CastAdapter
import com.zainal.moviedb.view.adapter.ReviewAdapter
import com.zainal.moviedb.view.adapter.TrailerAdapter
import com.zainal.moviedb.viewmodel.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity: BaseActivity() {
    lateinit var detailBinding: ActivityDetailBinding
    private val detailViewModel by viewModel<DetailViewModel>()

    private var idTvOrMovie = 0
    private var typeCategory: String = TypeCategory.MOVIE.name

    private val ytPlayers = mutableListOf<YouTubePlayerView>()

    private val trailerAdapter = TrailerAdapter(::trailerAdapterCallbck)
    private val castAdapter = CastAdapter(::castAdapterCallback)
    private val reviewAdapter = ReviewAdapter(::reviewsAdapterCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        with(detailBinding) {
            setContentView(root)
            setSupportActionBar(toolbar)
        }
        title = ""

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        with(detailViewModel) {
            vmBgUrlPoster().observe(this@DetailActivity) {
                it?.let {
                    Glide.with(this@DetailActivity)
                        .load("${BASE_URL_POSTER}$it")
                        .override(MATCH_PARENT, 250)
                        .centerCrop()
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?,
                            ) {
                                detailBinding.detailHeader.root.apply {
                                    background = resource
                                    backgroundTintMode = PorterDuff.Mode.MULTIPLY
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                }
            }

            vmUrlPoster().observe(this@DetailActivity) {
                it?.let {
                    Glide.with(this@DetailActivity)
                        .load("${BASE_URL_POSTER}$it")
                        .override(140, 250)
                        .centerCrop()
                        .placeholder(R.drawable.poster_placeholder)
                        .into(detailBinding.detailHeader.acivPoster)
                }
            }

            vmTag().observe(this@DetailActivity) {
                detailBinding.mtvTag.apply {
                    if (it.isNullOrEmpty()) {
                        visibility = GONE
                    } else {
                        text = it
                        visibility = VISIBLE
                    }
                }
            }

            vmTitle().observe(this@DetailActivity) {
                it?.let { strings ->
                    if (strings.isNotEmpty()) {
                        var mTitle = strings[0]
                        var flag = true
                        if (strings.any { s ->
                                s.isEmpty()
                            } || strings.size == 1) {
                            flag = false
                        }
                        if (flag) {
                            mTitle = getString(
                                R.string.placeholder_template,
                                strings[0],
                                strings[1]
                            )
                        }
                        var txtTitle = ""
                        lifecycleScope.launch(Dispatchers.Main) {
                            for (c in mTitle.subSequence(0, mTitle.length)) {
                                delay(25L)
                                txtTitle = buildString {
                                    append(txtTitle)
                                    append(c)
                                }
                                detailBinding.collapsToolbarLayout.title = txtTitle
                            }
                        }
                    }
                }
            }

            vmGenresItem().observe(this@DetailActivity) {
                it?.let {
                    detailBinding.detailHeader.mtvGenre.text = it
                }
            }

            vmVoteAverage().observe(this@DetailActivity) {
                it?.let {
                    with(detailBinding.detailHeader.voteAverage) {
                        if (it > 0) {
                            lifecycleScope.launch {
                                for (i in 0 until it) {
                                    mtvPercentageValue.text = "$i"
                                    delay(10L)
                                    cpiProgress.progress = i
                                }
                            }
                            return@observe
                        }
                        mtvPercentageValue.text = "$it"
                        cpiProgress.progress = it
                    }
                }
            }

            vmIsFav().observe(this@DetailActivity) {
                it?.let {
                    detailBinding.detailHeader.favBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(
                        this@DetailActivity,
                        it
                    ))
                }
            }

            vmOverview().observe(this@DetailActivity) {
                if (!it.isNullOrEmpty()) {
                    detailBinding.mtvOverview.text = it
                }
            }

            vmVideoResultItems().observe(this@DetailActivity) {
                detailBinding.mtvNoTrailer.visibility = when {
                    it.isNullOrEmpty() -> VISIBLE
                    else -> GONE.also { _ ->
                        trailerAdapter.setupData(it)
                    }
                }
            }

            vmCastItems().observe(this@DetailActivity) {
                castAdapter.setupData(it)
            }

            vmTotalReview().observe(this@DetailActivity) {
                detailBinding.mtvReviewTitle.text = getString(
                    R.string.placeholder_template,
                    getString(R.string.review_text),
                    "$it"
                )
            }

            vmReviewItems().observe(this@DetailActivity) {
                reviewAdapter.setupData(it)
            }
        }
    }

    private fun getDetailData() {
        detailViewModel.getDetailData(
            idTvOrMovie,
            0,
            typeCategory
        ) { shimmerState, s ->
            with(detailBinding) {
                when (shimmerState) {
                    ShimmerState.START -> {
                        root.isEnabled = false
                        appBarLayout.visibility = INVISIBLE
                        nestedScrollView.visibility = INVISIBLE
                        detailShimmer.apply {
                            visibility = VISIBLE
                            if (!isShimmerStarted) {
                                startShimmer()
                            }
                        }
                    }

                    ShimmerState.STOP_VISIBLE -> {
                        root.isEnabled = true
                        appBarLayout.visibility = INVISIBLE
                        nestedScrollView.visibility = INVISIBLE
                        detailShimmer.apply {
                            visibility = VISIBLE
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                        }
                    }

                    else -> {
                        detailShimmer.apply {
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                            visibility = INVISIBLE
                        }
                        root.isEnabled = true
                        appBarLayout.visibility = VISIBLE
                        nestedScrollView.visibility = VISIBLE
                    }
                }
            }

            s?.let {
                showGlobalDialog(it)
            }
        }
    }

    private fun initView() {
        with(detailBinding) {

            root.apply {
                setOnRefreshListener {
                    isRefreshing = false
                    collapsToolbarLayout.title = ""
                    getDetailData()
                }
            }

            appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
                root.isEnabled = verticalOffset >= 0
            }

            detailHeader.favBtn.setOnClickListener {
                detailViewModel.updateFav(typeCategory)
            }

            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                    if (scrollY != 0 && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        getMoreReviewsData()
                    }
                }
            )

            rcvTrailer.apply {
                adapter = trailerAdapter
                setHasFixedSize(false)
                itemAnimator = null
            }

            rcvCast.apply {
                adapter = castAdapter
                setHasFixedSize(false)
                itemAnimator = null
            }

            rcvReview.apply {
                adapter = reviewAdapter
                addItemDecoration(DividerItemDecoration(
                    this@DetailActivity,
                    DividerItemDecoration.VERTICAL
                ))
                setHasFixedSize(false)
                itemAnimator = null
            }
        }

        getDetailData()
    }

    private fun getMoreReviewsData() {
        detailViewModel.getMoreReviewsData(
            typeCategory
        ) { bottomViewState, s ->
            with(detailBinding) {
                when (bottomViewState) {
                    BottomViewState.LOADING -> bottomLoading.root.visibility = VISIBLE
                    BottomViewState.STUCK -> {
                        bottomLoading.root.visibility = INVISIBLE
                        stuckView.root.visibility = VISIBLE
                    }
                    else -> {
                        bottomLoading.root.visibility = INVISIBLE
                        stuckView.root.visibility = INVISIBLE
                    }
                }
            }

            s?.let {
                showGlobalDialog(it)
            }
        }
    }

    private fun initData() {
        intent?.let {
            idTvOrMovie = it.getIntExtra(EXTRA_ID, 0)
            typeCategory = it.getStringExtra(EXTRA_CATEGORY) ?:TypeCategory.MOVIE.name
        }
    }

    private fun trailerAdapterCallbck(
        videoResultsItem: VideoResultsItem,
        holder: TrailerAdapter.TrailerItemViewHolder
    ) {
        with(holder) {
            ytPlayer.apply {
                ytPlayers.add(this)

                getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        videoResultsItem.key?.let {
                            youTubePlayer.cueVideo(it, 0f)
                        }
                    }
                })
            }

            viewTransparent.setOnClickListener {
                val alertDialogBuilder = MaterialAlertDialogBuilder(this@DetailActivity).apply {
                    val trailerDialog = TrailerDialogBinding.inflate(layoutInflater).apply {
                        ytPlayerView.apply {
                            lifecycle.addObserver(this)
                            getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                                    videoResultsItem.key?.let {
                                        youTubePlayer.loadVideo(it, 0f)
                                    }
                                }
                            })
                        }

                        btnClose.setOnClickListener {
                            ytPlayerView.release()
                            closeDialog()
                        }
                    }
                    setCancelable(false)
                    background = ContextCompat.getDrawable(this@DetailActivity, android.R.color.transparent)
                    setView(trailerDialog.root)
                }
                alertDialog = alertDialogBuilder.create()
                showDialog()
            }
        }
    }

    private fun castAdapterCallback(
        castItem: CastItem,
        holder: CastAdapter.CastItemViewHolder
    ) {
        with(holder) {
            Glide.with(this@DetailActivity)
                .load("${BASE_URL_AVATAR}${castItem.profilePath}")
                .override(MATCH_PARENT, 100)
                .centerCrop()
                .error(when (castItem.gender) {
                    0, 2 -> R.drawable.ic_round_man_24
                    else -> R.drawable.ic_round_woman_24
                })
                .placeholder(R.drawable.ic_round_person_outline_24)
                .into(acivAvatar)

            mtvActorName.text = castItem.originalName
            mtvCastName.text = castItem.character
        }
    }

    private fun reviewsAdapterCallback(
        reviewResultsItem: ReviewResultsItem,
        holder: ReviewAdapter.ReviewItemViewHolder
    ) {
        with(holder) {
            val author = reviewResultsItem.author
            mtvTitle.text = author

            var flag = true
            reviewResultsItem.authorDetails?.rating?.let {
                if (it.isNotEmpty()) {
                    flag = false
                    mtvVoteValue.text = "${it.toDouble()}"
                }
            }
            if (flag) {
                voteContainer.visibility = GONE
            }

            flag = true
            reviewResultsItem.updatedAt?.let {
                if (it.isNotEmpty()) {
                    flag = false
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    mtvSubTitle.text = reviewResultsItem.updatedAt?.substringBefore("T")?.let { s ->
                        parser.parse(s)?.let { date ->
                            formatter.format(date)
                        }
                    }
                }
            }
            if (flag) {
                mtvSubTitle.visibility = GONE
            }

            mtvReview.text = reviewResultsItem.content

            var isPathValid = false
            reviewResultsItem.authorDetails?.avatarPath?.let {
                if (it.isNotEmpty()) {
                    isPathValid = true
                    Glide.with(this@DetailActivity)
                        .load("${BASE_URL_AVATAR}$it")
                        .override(50, 50)
                        .transform(RoundedCorners(14))
                        .centerCrop()
                        .placeholder(R.drawable.ic_round_person_outline_24)
                        .into(acivAvatar)
                }
            }
            if (!isPathValid) {
                acivAvatar.background = ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.bg_placeholder_round_square
                )
                mtvAvatar.apply {
                    visibility = VISIBLE
                    text = author?.substring(0, 1)?.uppercase()
                }
            }

            mtvContinue.setOnClickListener {
                startActivity(Intent(
                    this@DetailActivity,
                    ReviewActivity::class.java
                ).apply {
                    putExtra(EXTRA_REVIEW_ID, reviewResultsItem.id)
                    putExtra(EXTRA_YEAR, detailViewModel.title.value?.get(1))
                    putExtra(EXTRA_POSTER_PATH, detailViewModel.urlPoster.value)
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        with(ytPlayers) {
            forEach {
                lifecycleScope.launch {
                    it.release()
                }
            }
            clear()
        }
    }
}