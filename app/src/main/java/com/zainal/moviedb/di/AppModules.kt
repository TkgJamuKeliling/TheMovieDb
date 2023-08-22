package com.zainal.moviedb.di

import com.zainal.moviedb.helper.PrefsHelper
import com.zainal.moviedb.network.RestClientService
import com.zainal.moviedb.util.PrefsUtil
import com.zainal.moviedb.util.Repository
import com.zainal.moviedb.viewmodel.DetailViewModel
import com.zainal.moviedb.viewmodel.DiscoverViewModel
import com.zainal.moviedb.viewmodel.FavViewModel
import com.zainal.moviedb.viewmodel.MovieViewModel
import com.zainal.moviedb.viewmodel.ReviewViewModel
import com.zainal.moviedb.viewmodel.TvShowViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModules {
    val module = module {
        factory {
            PrefsUtil(androidContext())
        }

        factory {
            PrefsHelper(get())
        }

        factory {
            RestClientService(get())
        }

        factory {
            Repository(
                androidContext(),
                get()
            )
        }

        viewModel {
            MovieViewModel(get())
        }

        viewModel {
            TvShowViewModel(get())
        }

        viewModel {
            FavViewModel(get())
        }

        viewModel {
            DetailViewModel(get())
        }

        viewModel {
            ReviewViewModel(get())
        }

        viewModel {
            DiscoverViewModel(get())
        }
    }
}