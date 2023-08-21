package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.zainal.moviedb.R
import com.zainal.moviedb.base.BaseFragment
import com.zainal.moviedb.databinding.MainFragmentLayoutBinding
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.util.Constant
import com.zainal.moviedb.util.ShimmerState
import com.zainal.moviedb.util.TrendingSeason
import com.zainal.moviedb.util.TypeCategory
import com.zainal.moviedb.view.adapter.GenreAdapter
import com.zainal.moviedb.view.adapter.TrendingAdapter
import com.zainal.moviedb.viewmodel.TvShowViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class TvShowFragment : BaseFragment() {

    private lateinit var tvShowBinding: MainFragmentLayoutBinding
    private val tvShowViewModel by inject<TvShowViewModel>()

    private var trendingSeason: TrendingSeason = TrendingSeason.DAY

    private val trendingAdapter = TrendingAdapter(::trendingAdapterCallback)

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
        tvShowBinding = MainFragmentLayoutBinding.inflate(inflater, container, false)
        return tvShowBinding.root
    }

    private fun initView() {
        with(tvShowBinding) {
            root.setOnRefreshListener {
                root.isRefreshing = false
                tvShowViewModel.fetchAllData(trendingSeason)
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
                            tvShowViewModel.getTvTrendingData(trendingSeason)
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
        with(tvShowViewModel) {
            vmShimmerState().observe(viewLifecycleOwner) {
                with(tvShowBinding) {
                    when (it) {
                        ShimmerState.START -> {
                            nestedScrollView.visibility = View.INVISIBLE
                            mainShimmer.apply {
                                visibility = View.VISIBLE
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
                                visibility = View.INVISIBLE
                            }
                            nestedScrollView.visibility = View.VISIBLE
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
                with(tvShowBinding.trendingLayout) {
                    when (it) {
                        ShimmerState.START -> {
                            trendingContainer.visibility = View.INVISIBLE
                            trendingShimmer.apply {
                                visibility = View.VISIBLE
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
                                visibility = View.INVISIBLE
                            }
                            trendingContainer.visibility = View.VISIBLE
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
            Picasso.get()
                .load("${Constant.BASE_URL_POSTER}${trendingResultsItem.posterPath}")
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(R.drawable.poster_placeholder)
                .into(acivCard)

            trendingResultsItem.voteAverage?.let {
                val percentageValue = (it * 10).roundToInt()

                lifecycleScope.launch(Dispatchers.Main) {
                    for (i in 0 until percentageValue) {
                        mtvPercentageValue.text = "$percentageValue"
                        delay(10L)
                        cpiProgress.progress = i
                    }
                }
            }

            mtvCardTitle.text = trendingResultsItem.originalName

            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (trendingResultsItem.firstAirDate.isNotEmpty()) {
                mtvReleaseDate.text = parser.parse(trendingResultsItem.firstAirDate)?.let { formatter.format(it) }
            }

            root.setOnClickListener {
                openDetailActivity(
                    trendingResultsItem.id,
                    TypeCategory.TV.name
                )
            }
        }
    }
}