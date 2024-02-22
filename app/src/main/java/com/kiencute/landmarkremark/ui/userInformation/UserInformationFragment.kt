package com.kiencute.landmarkremark.ui.userInformation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kiencute.landmarkremark.databinding.FragmentUserInfoBinding
import com.kiencute.landmarkremark.datastore.DataStoreManager
import com.kiencute.landmarkremark.extentions.collectIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserInformationFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreManager: DataStoreManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            dataStoreManager.userId.collectIn(viewLifecycleOwner) {
                binding.uName.text = it.toString()
            }
            dataStoreManager.username.collectIn(viewLifecycleOwner) {
                binding.uEmail.text = it.toString()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}