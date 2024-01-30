package com.kiencute.basesrc.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kiencute.basesrc.R
import com.kiencute.basesrc.data.entities.Entity
import com.kiencute.basesrc.databinding.FragmentSecondBinding
import com.kiencute.basesrc.extentions.load
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data : Entity? = arguments?.getParcelable("data")
        if(data != null){
            with(binding){
                imgView.load("https://robohash.org/6336ad010c0984744dd1960402d5fc6f?set=set4&bgset=&size=200x200")
                mail.text = data.email
                name.text = {data.firstName + " " +  data.lastName}.toString()
            }

        }
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}