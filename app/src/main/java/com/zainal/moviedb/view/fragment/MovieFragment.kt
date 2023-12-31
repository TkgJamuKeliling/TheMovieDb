package com.zainal.moviedb.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseFragment
import com.zainal.moviedb.databinding.MainFragmentLayoutBinding
import com.zainal.moviedb.model.response.GenresItem
import com.zainal.moviedb.model.response.TrendingResultsItem
import com.zainal.moviedb.util.Constant.BASE_URL_POSTER
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_GENRE_DATA
import com.zainal.moviedb.util.Constant.EXTRA_LIST_GENRE
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.activity.DiscoverActivity
import com.zainal.moviedb.view.activity.MainActivity
import com.zainal.moviedb.view.adapter.GenreAdapter
import com.zainal.moviedb.view.adapter.TrendingAdapter
import com.zainal.moviedb.viewmodel.MovieViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class MovieFragment : BaseFragment() {

    private lateinit var movieBinding: MainFragmentLayoutBinding
    private val movieViewModel by inject<MovieViewModel>()
    private var trendingSeason: TrendingSeason = TrendingSeason.DAY

    private val trendingAdapter = TrendingAdapter(::trendingAdapterCallback)
    private val genreAdapter = GenreAdapter(::genreAdapterCallback)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        movieBinding = MainFragmentLayoutBinding.inflate(inflater, container, false)
        return movieBinding.root
    }

    private fun initView() {
        with(movieBinding) {
            root.setOnRefreshListener {
                root.isRefreshing = false
                fetchData()
            }

            with(trendingLayout) {
                btnToggle.apply {
                    check(btnToday.id)
                    addOnButtonCheckedListener { _, checkedId, isChecked ->
                        if (isChecked) {
                            trendingSeason = when (checkedId) {
                                btnToday.id -> TrendingSeason.DAY
                                else -> TrendingSeason.WEEK
                            }
                            fetchTrendingData()
                        }
                    }
                }

                rcvTrending.apply {
                    adapter = trendingAdapter
                    setHasFixedSize(false)
                    itemAnimator = null
                }
            }

            with(genreLayout) {
                rcvGenre.apply {
                    adapter = genreAdapter
                    setHasFixedSize(false)
                    itemAnimator = null
                }
            }
        }
    }

    private fun fetchTrendingData() {
        movieViewModel.getMovieTrendingData(trendingSeason) { shimmerState, s ->
            with(movieBinding.trendingLayout) {
                when (shimmerState) {
                    ShimmerState.START -> {
                        trendingContainer.visibility = INVISIBLE
                        trendingShimmer.apply {
                            visibility = VISIBLE
                            if (!isShimmerStarted) {
                                startShimmer()
                            }
                        }
                    }

                    ShimmerState.STOP_VISIBLE -> {
                        trendingContainer.visibility = INVISIBLE
                        trendingShimmer.apply {
                            visibility = VISIBLE
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                        }
                    }

                    else -> {
                        trendingShimmer.apply {
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                            visibility = INVISIBLE
                        }
                        trendingContainer.visibility = VISIBLE
                    }
                }
            }

            s?.let {
                (activity as? MainActivity)?.showGlobalDialog(it)
            }
        }
    }

    private fun observeView() {
        with(movieViewModel) {
            vmTrendingResultsItem().observe(viewLifecycleOwner) {
                trendingAdapter.setData(it)
            }

            vmGenresItem().observe(viewLifecycleOwner) {
                genreAdapter.setData(it)
            }
        }

        fetchData()
    }

    private fun fetchData() {
        movieViewModel.fetchAllData(trendingSeason) { shimmerState, s ->
            with(movieBinding) {
                when (shimmerState) {
                    ShimmerState.START -> {
                        root.isEnabled = false
                        nestedScrollView.visibility = INVISIBLE
                        mainShimmer.apply {
                            visibility = VISIBLE
                            if (!isShimmerStarted) {
                                startShimmer()
                            }
                        }
                    }

                    ShimmerState.STOP_VISIBLE -> {
                        mainShimmer.apply {
                            visibility = VISIBLE
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                        }
                        nestedScrollView.visibility = INVISIBLE
                        root.isEnabled = true
                    }

                    else -> {
                        mainShimmer.apply {
                            if (isShimmerStarted) {
                                stopShimmer()
                            }
                            visibility = INVISIBLE
                        }
                        nestedScrollView.visibility = VISIBLE
                        root.isEnabled = true
                    }
                }
            }

            s?.let {
                (activity as? MainActivity)?.showGlobalDialog(it)
            }
        }
    }

    private fun trendingAdapterCallback(
        trendingResultsItem: TrendingResultsItem,
        holder: TrendingAdapter.TrendingViewHolder
    ) {
        with(holder) {
            Glide.with(this@MovieFragment)
                .load("${BASE_URL_POSTER}${trendingResultsItem.posterPath}")
                .override(200, 295)
                .centerCrop()
                .placeholder(R.drawable.poster_placeholder)
                .into(acivCard)

            trendingResultsItem.voteAverage?.let {
                val percentageValue = (it * 10).roundToInt()

                lifecycleScope.launch(Dispatchers.Main) {
                    for (i in 0 until percentageValue) {
                        mtvPercentageValue.text = "$i"
                        delay(10L)
                        cpiProgress.progress = i
                    }
                }
            }

            mtvCardTitle.text = trendingResultsItem.title

            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (trendingResultsItem.releaseDate.isNotEmpty()) {
                mtvReleaseDate.text = parser.parse(trendingResultsItem.releaseDate)?.let { formatter.format(it) }
            }

            root.setOnClickListener {
                openDetailActivity(
                    trendingResultsItem.id,
                    TypeCategory.MOVIE.name
                )
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    fun genreAdapterCallback(
        genresItem: GenresItem,
        holder: GenreAdapter.GenreHolder
    ) {
        with(holder) {
            val resId = requireContext().resources.getIdentifier(
                genresItem.icon,
                "drawable",
                requireContext().packageName
            )
            acivIcon.setImageResource(resId)

            mtvMenuTitle.text = genresItem.name

            mcv.setOnClickListener {
                startActivity(
                    Intent(
                    requireContext(),
                    DiscoverActivity::class.java
                ).apply {
                    putExtra(EXTRA_GENRE_DATA, genresItem)
                    putExtra(EXTRA_CATEGORY, TypeCategory.MOVIE.name)
                    putExtra(EXTRA_LIST_GENRE, movieViewModel.genreResponse.value)
                })
            }
        }
    }
}