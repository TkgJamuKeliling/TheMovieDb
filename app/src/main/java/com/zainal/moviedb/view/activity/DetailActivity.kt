package com.zainal.moviedb.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseActivity
import com.zainal.moviedb.databinding.ActivityDetailBinding
import com.zainal.moviedb.databinding.TrailerDialogBinding
import com.zainal.moviedb.model.CastItem
import com.zainal.moviedb.model.ReviewResultsItem
import com.zainal.moviedb.model.VideoResultsItem
import com.zainal.moviedb.util.Constant
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
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
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
        setContentView(detailBinding.root)

        initData()
        initView()
        observeView()
    }

    private fun observeView() {
        with(detailViewModel) {
            vmShimmerState().observe(this@DetailActivity) {
                with(detailBinding) {
                    when (it) {
                        ShimmerState.START -> {
                            appBarLayout.visibility = INVISIBLE
                            nestedScrollView.visibility = INVISIBLE
                            detailShimmer.apply {
                                visibility = VISIBLE
                                if (!isShimmerStarted) {
                                    startShimmer()
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
                            appBarLayout.visibility = VISIBLE
                            nestedScrollView.visibility = VISIBLE
                        }
                    }
                }
            }

            vmBgUrlPoster().observe(this@DetailActivity) {
                it?.let {
                    Picasso.get()
                        .load("${Constant.BASE_URL_POSTER}$it")
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                detailBinding.detailHeader.root.apply {
                                    background = BitmapDrawable(resources, bitmap)
                                    backgroundTintMode = PorterDuff.Mode.MULTIPLY
                                }
                            }

                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                        })
                }
            }

            vmUrlPoster().observe(this@DetailActivity) {
                it?.let {
                    Picasso.get()
                        .load("${Constant.BASE_URL_POSTER}$it")
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.poster_placeholder)
                        .into(detailBinding.detailHeader.acivPoster)
                }
            }

            vmTag().observe(this@DetailActivity) {
                it?.let {
                    detailBinding.mtvTag.text = it
                }
            }

            vmTitle().observe(this@DetailActivity) {
                it?.let { strings ->
                    if (strings.isNotEmpty()) {
                        detailBinding.toolbar.title = when(strings.size) {
                            1 -> strings[0]
                            else -> getString(
                                R.string.placeholder_template,
                                strings[0],
                                strings[1]
                            )
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
                it?.let {
                    detailBinding.mtvOverview.text = it
                }
            }

            vmVideoResultItems().observe(this@DetailActivity) {
                trailerAdapter.setupData(it)
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

            vmLoadingView().observe(this@DetailActivity) {
                detailBinding.bottomLoading.root.visibility = it
            }

            vmStuckView().observe(this@DetailActivity) {
                detailBinding.stuckView.root.visibility = it
            }
        }
    }

    private fun getDetailData() {
        detailViewModel.getDetailData(
            idTvOrMovie,
            0,
            typeCategory,
            ::setupSwipeLayout
        )
    }

    private fun setupSwipeLayout(enable: Boolean) {
        detailBinding.root.isEnabled = enable
    }

    private fun initView() {
        with(detailBinding) {

            root.apply {
                setOnRefreshListener {
                    isRefreshing = false
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
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        detailViewModel.getMoreReviewsData(typeCategory)
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
            Picasso.get()
                .load("${Constant.BASE_URL_AVATAR}${castItem.profilePath}")
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
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
                    Picasso.get()
                        .load("${Constant.BASE_URL_AVATAR}$it")
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(50, 50)
                        .centerCrop()
                        .transform(RoundedCornersTransformation(14, 2))
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