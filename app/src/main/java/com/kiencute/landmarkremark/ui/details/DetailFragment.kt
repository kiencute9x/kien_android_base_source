package com.kiencute.landmarkremark.ui.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kiencute.landmarkremark.data.entities.Entity
import com.kiencute.landmarkremark.databinding.FragmentSecondBinding
import com.kiencute.landmarkremark.extentions.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data: Entity? = arguments?.getParcelable("data")
        if (data != null) {
            with(binding) {
                imgView.load("https://robohash.org/6336ad010c0984744dd1960402d5fc6f?set=set4&bgset=&size=200x200")
                mail.text = data.email
                name.text = data.firstName + " " + data.lastName
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}