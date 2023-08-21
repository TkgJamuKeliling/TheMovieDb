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
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseFragment
import com.zainal.moviedb.databinding.FragmentMovieBinding
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.Constant.EXTRA_CATEGORY
import com.zainal.moviedb.util.Constant.EXTRA_ID
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.view.activity.DetailActivity
import com.zainal.moviedb.view.adapter.GenreAdapter
import com.zainal.moviedb.view.adapter.TrendingAdapter
import com.zainal.moviedb.viewmodel.MovieViewModel
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class MovieFragment : BaseFragment() {

    private lateinit var movieBinding: FragmentMovieBinding
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
        movieBinding = FragmentMovieBinding.inflate(inflater, container, false)
        return movieBinding.root
    }

    private fun initView() {
        with(movieBinding) {
            root.setOnRefreshListener {
                root.isRefreshing = false
                movieViewModel.fetchAllData(trendingSeason)
            }

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

            rcvGenre.apply {
                adapter = genreAdapter
                setHasFixedSize(false)
                itemAnimator = null
            }
        }
    }

    private fun observeView() {
        with(movieViewModel) {
            vmSeasonBtnState().observe(viewLifecycleOwner) {
                movieBinding.btnToggle.isEnabled = it
            }

            vmListTrendingResultsItem().observe(viewLifecycleOwner) {
                trendingAdapter.setData(it)
            }

            vmRcvTrendingState().observe(viewLifecycleOwner) {
                movieBinding.rcvTrending.visibility = it
            }

            vmTrendingShimmerState().observe(viewLifecycleOwner) {
                movieBinding.trendingCardShimmer.visibility = when (it) {
                    ShimmerState.STOP_GONE -> INVISIBLE
                    else -> VISIBLE
                }
                with(movieBinding.trendingCardShimmer) {
                    when (it) {
                        ShimmerState.START -> if (!isShimmerStarted) startShimmer()
                        else -> if (isShimmerStarted) stopShimmer()
                    }
                }
            }

            vmListGenreItem().observe(viewLifecycleOwner) {
                genreAdapter.setData(it)
            }

            vmRcvGenreState().observe(viewLifecycleOwner) {
                movieBinding.rcvGenre.visibility = it
            }

            vmGenreShimmerState().observe(viewLifecycleOwner) {
                movieBinding.genreCardShimmer.visibility = when (it) {
                    ShimmerState.STOP_GONE -> INVISIBLE
                    else -> VISIBLE
                }
                with(movieBinding.genreCardShimmer) {
                    when (it) {
                        ShimmerState.START -> if (!isShimmerStarted) startShimmer()
                        else -> if (isShimmerStarted) stopShimmer()
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
            Picasso.get()
                .load("${Constant.BASE_URL_POSTER}${trendingResultsItem.posterPath}")
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(RoundedCornersTransformation(100, 2))
                .placeholder(R.drawable.poster_placeholder)
                .into(acivCard)

            trendingResultsItem.voteAverage?.let {
                val percentageValue = (it * 10).roundToInt()

                lifecycleScope.launch(Dispatchers.Main) {
                    for (i in 0 until percentageValue) {
                        delay(10L)
                        mtvPercentageValue.text = "$percentageValue"
                        cpiProgress.progress = i
                    }
                }
            }

            mtvCardTitle.text = trendingResultsItem.originalTitle

            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            mtvReleaseDate.text = parser.parse(trendingResultsItem.releaseDate)?.let { formatter.format(it) }

            root.setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        DetailActivity::class.java
                    ).apply {
                        putExtra(EXTRA_ID, trendingResultsItem.id)
                        putExtra(EXTRA_CATEGORY, TypeCategory.MOVIE.name)
                    }
                )
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun genreAdapterCallback(
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
                //TODO
            }
        }
    }
}