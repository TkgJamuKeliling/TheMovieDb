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
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_GENRE_DATA
import com.zainal.moviedb.util.Constant.EXTRA_LIST_GENRE
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.activity.DiscoverActivity
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
    val genreAdapter = GenreAdapter(::genreAdapterCallback)

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
                movieViewModel.fetchAllData(trendingSeason)
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
                            movieViewModel.getMovieTrendingData(trendingSeason)
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

    private fun observeView() {
        with(movieViewModel) {
            vmShimmerState().observe(viewLifecycleOwner) {
                with(movieBinding) {
                    when (it) {
                        ShimmerState.START -> {
                            nestedScrollView.visibility = INVISIBLE
                            mainShimmer.apply {
                                visibility = VISIBLE
                                if (!isShimmerStarted) {
                                    startShimmer()
                                }
                            }
                        }
                        else -> {
                            mainShimmer.apply {
                                if (isShimmerStarted) {
                                    stopShimmer()
                                }
                                visibility = INVISIBLE
                            }
                            nestedScrollView.visibility = VISIBLE
                        }
                    }
                }
            }

            vmTrendingResultsItem().observe(viewLifecycleOwner) {
                trendingAdapter.setData(it)
            }

            vmGenresItem().observe(viewLifecycleOwner) {
                genreAdapter.setData(it)
            }

            vmTrendingShimmerState().observe(viewLifecycleOwner) {
                with(movieBinding.trendingLayout) {
                    when (it) {
                        ShimmerState.START -> {
                            trendingContainer.visibility = INVISIBLE
                            trendingShimmer.apply {
                                visibility = VISIBLE
                                if (!isShimmerStarted) {
                                    startShimmer()
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
            }
        }
    }

    private fun trendingAdapterCallback(
        trendingResultsItem: TrendingResultsItem,
        holder: TrendingAdapter.TrendingViewHolder
    ) {
        with(holder) {
            Glide.with(this@MovieFragment)
                .load("${Constant.BASE_URL_POSTER}${trendingResultsItem.posterPath}")
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

            mtvCardTitle.text = trendingResultsItem.originalTitle

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