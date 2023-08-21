package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zainal.moviedb.databinding.FragmentTvShowBinding
import com.zainal.moviedb.viewmodel.TvShowViewModel
import org.koin.android.ext.android.inject

class TvShowFragment : Fragment() {

    private lateinit var tvShowBinding: FragmentTvShowBinding
    val tvShowViewModel by inject<TvShowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tvShowBinding = FragmentTvShowBinding.inflate(inflater, container, false)
        return tvShowBinding.root
    }
}