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
import com.kiencute.basesrc.data.entities.Entity
import com.kiencute.basesrc.databinding.FragmentFirstBinding
import com.kiencute.basesrc.utils.Resource
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() , EntityAdapter.EItemListener {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeVM by viewModels()
    private lateinit var adapter: EntityAdapter

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
        adapter = EntityAdapter(this)
        binding.charactersRv.layoutManager = LinearLayoutManager(requireContext())
        binding.charactersRv.adapter = adapter
    }

    private fun setupObservers() = viewModel.data.observe(viewLifecycleOwner) {
        when (it.status) {
            Resource.Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                if (!it.data.isNullOrEmpty())
                    adapter.setItems(ArrayList(it.data))
            }

            Resource.Status.ERROR -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

            Resource.Status.LOADING ->
                binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onClickedItem(entity: Entity) {
        val bundle = bundleOf("data" to entity)
        findNavController().navigate(
            R.id.action_FirstFragment_to_SecondFragment , bundle
        )
    }
}