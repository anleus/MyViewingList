package com.example.myviewinglist.ui.collections

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myviewinglist.databinding.FragmentCollectionsBinding
import com.example.myviewinglist.model.Entry
import com.example.myviewinglist.ui.EntryListAdapter

class CollectionsFragment : Fragment(), EntryListAdapter.OnItemClickListener {

    private val viewModel by lazy { ViewModelProvider(this).get(CollectionsViewModel::class.java)}
    private lateinit var adapter: EntryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCollectionsBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = EntryListAdapter(this)
        binding.entriesList.layoutManager = LinearLayoutManager(requireContext())
        binding.entriesList.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        viewModel.entries.observe(viewLifecycleOwner, Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onItemClick(position: Int) {
        val item = viewModel.entries.value?.get(position)

        val action = CollectionsFragmentDirections.actionCollectionsFragmentToEntryFragment(entryToList(item))
        view?.findNavController()?.navigate(action)
    }

    private fun entryToList(entry: Entry?): Array<String?> {
        return arrayOf(entry?.id, entry?.name, entry?.type?.ordinal.toString(), entry?.publication)
    }
}