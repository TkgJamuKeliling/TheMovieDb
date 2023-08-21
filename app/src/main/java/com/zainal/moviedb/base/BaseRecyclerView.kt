package com.zainal.moviedb.base

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zainal.moviedb.model.CastItem
import com.zainal.moviedb.model.GenresItem
import com.zainal.moviedb.model.TrendingResultsItem
import com.zainal.moviedb.model.ReviewResultsItem
import com.zainal.moviedb.model.VideoResultsItem

abstract class BaseRecyclerView: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val asyncListDiffer by lazy {
        AsyncListDiffer(onGetAdapter(), object : DiffUtil.ItemCallback<Equatable>() {
            override fun areItemsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
                return when {
                    oldItem is GenresItem && newItem is GenresItem -> oldItem.id == newItem.id
                            && oldItem.name == newItem.name
                            && oldItem.icon == newItem.icon

                    oldItem is ReviewResultsItem && newItem is ReviewResultsItem -> oldItem.authorDetails == newItem.authorDetails
                            && oldItem.updatedAt == newItem.updatedAt
                            && oldItem.author == newItem.author
                            && oldItem.createdAt == newItem.createdAt
                            && oldItem.id == newItem.id
                            && oldItem.content == newItem.content
                            && oldItem.url == newItem.url
                            && oldItem.reviewSection == newItem.reviewSection

                    oldItem is CastItem && newItem is CastItem -> oldItem.castId == newItem.castId
                            && oldItem.character == newItem.character
                            && oldItem.gender == newItem.gender
                            && oldItem.creditId == newItem.creditId
                            && oldItem.knownForDepartment == newItem.knownForDepartment
                            && oldItem.originalName == newItem.originalName
                            && oldItem.popularity == newItem.popularity
                            && oldItem.name == newItem.name
                            && oldItem.profilePath == newItem.profilePath
                            && oldItem.id == newItem.id
                            && oldItem.adult == newItem.adult
                            && oldItem.order == newItem.order

                    oldItem is VideoResultsItem && newItem is VideoResultsItem -> oldItem.site == newItem.site
                            && oldItem.size == newItem.size
                            && oldItem.iso31661 == newItem.iso31661
                            && oldItem.name == newItem.name
                            && oldItem.official == newItem.official
                            && oldItem.id == newItem.id
                            && oldItem.type == newItem.type
                            && oldItem.publishedAt == newItem.publishedAt
                            && oldItem.iso6391 == newItem.iso6391
                            && oldItem.key == newItem.key

                    oldItem is TrendingResultsItem && newItem is TrendingResultsItem -> oldItem.overview == newItem.overview
                            && oldItem.originalLanguage == newItem.originalLanguage
                            && oldItem.originalTitle == newItem.originalTitle
                            && oldItem.video == newItem.video
                            && oldItem.title == newItem.title
                            && oldItem.genreIds == newItem.genreIds
                            && oldItem.posterPath == newItem.posterPath
                            && oldItem.backdropPath == newItem.backdropPath
                            && oldItem.mediaType == newItem.mediaType
                            && oldItem.releaseDate == newItem.releaseDate
                            && oldItem.popularity == newItem.popularity
                            && oldItem.voteAverage == newItem.voteAverage
                            && oldItem.id == newItem.id
                            && oldItem.adult == newItem.adult
                            && oldItem.voteCount == newItem.voteCount
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
                return when {
                    oldItem is TrendingResultsItem && newItem is TrendingResultsItem
                            || oldItem is VideoResultsItem && newItem is VideoResultsItem
                            || oldItem is ReviewResultsItem && newItem is ReviewResultsItem
                            || oldItem is CastItem && newItem is CastItem
                            || oldItem is GenresItem && newItem is GenresItem -> oldItem == newItem
                    else -> false
                }
            }
        })
    }

    fun updateData(list: List<Equatable>?) {
        with(asyncListDiffer) {
            list?.let {
                var data = it
                if (it.all { equatable ->
                        equatable is ReviewResultsItem
                }) {
                    it.map { e ->
                        (e as ReviewResultsItem).copy()
                    }.also { reviewResultsItems ->
                        data = reviewResultsItems
                    }
                }
                submitList(data)
            }
        }
    }

    fun getListModel(): MutableList<Equatable> = asyncListDiffer.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = onGetViewHolder(parent, viewType)

    override fun getItemCount() = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = onGetBindHolder(
        holder,
        asyncListDiffer.currentList[position],
        position
    )

    override fun getItemId(position: Int): Long {
        super.getItemId(position)
        return position.toLong()
    }

    abstract fun onGetAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>

    abstract fun onGetBindHolder(
        holder: RecyclerView.ViewHolder,
        model: Equatable,
        position: Int
    )

    abstract fun onGetViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
}

interface Equatable {
    override fun equals(other: Any?): Boolean
}