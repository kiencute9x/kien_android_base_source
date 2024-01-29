package com.kiencute.basesrc.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kiencute.basesrc.R
import com.kiencute.basesrc.databinding.FragmentFirstBinding
import com.kiencute.basesrc.utils.Resource
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() , EmployeeAdapter.EItemListener {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeVM by viewModels()
    private lateinit var adapter: EmployeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(this)
        binding.charactersRv.layoutManager = LinearLayoutManager(requireContext())
        binding.charactersRv.adapter = adapter
    }

    private fun setupObservers() = viewModel.data.observe(viewLifecycleOwner) {
        when (it.status) {
            Resource.Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                Log.d("aaaaaaaa", "setupObservers: " + !it.data.isNullOrEmpty())
                if (!it.data.isNullOrEmpty())
                    adapter.setItems(ArrayList(it.data))
            }

            Resource.Status.ERROR -> {
                Log.d("aaaaaaaaaa", "setupObservers: ${it.message}")
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

            Resource.Status.LOADING ->
                binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onClickedCharacter(characterId: Int) {
//        findNavController().navigate(
//            R.id.action_charactersFragment_to_characterDetailFragment,
//            bundleOf("id" to characterId)
//        )
    }
}