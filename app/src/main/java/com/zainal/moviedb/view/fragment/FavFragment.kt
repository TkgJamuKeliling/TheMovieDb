package com.zainal.moviedb.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zainal.moviedb.databinding.FragmentFavBinding
import com.zainal.moviedb.viewmodel.FavViewModel
import org.koin.android.ext.android.inject

class FavFragment : Fragment() {

    lateinit var favBinding: FragmentFavBinding
    val favViewModel by inject<FavViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favBinding = FragmentFavBinding.inflate(inflater, container, false)
        return favBinding.root
    }
}