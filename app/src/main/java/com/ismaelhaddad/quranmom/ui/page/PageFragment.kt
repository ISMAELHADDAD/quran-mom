package com.ismaelhaddad.quranmom.ui.page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ismaelhaddad.quranmom.QURANMOM_PREFERENCES
import com.ismaelhaddad.quranmom.QURANMOM_PREFERRED_RECITER_ID
import com.ismaelhaddad.quranmom.R
import com.ismaelhaddad.quranmom.databinding.FragmentPageBinding
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.service.DatabaseService
import kotlinx.coroutines.launch

class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageViewModel: PageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val databaseService = DatabaseService.getDatabase(requireContext().applicationContext)
        pageViewModel = ViewModelProvider(this, PageViewModel.Factory(databaseService))[PageViewModel::class.java]

        pageViewModel.setSelectedSurahNumber(arguments?.getInt("surahNumber")?: -1)

        val reciterButton = requireActivity().findViewById<ImageButton>(R.id.reciter_button)
        val reciterPopupMenu = PopupMenu(requireContext(), reciterButton)
        var currentReciters: List<Reciter> = emptyList()

        viewLifecycleOwner.lifecycleScope.launch {
            pageViewModel.reciters.collect { reciters ->
                currentReciters = reciters

                val sharedPreferences = requireContext().applicationContext.getSharedPreferences(QURANMOM_PREFERENCES, Context.MODE_PRIVATE)
                val preferredReciterId = sharedPreferences.getInt(QURANMOM_PREFERRED_RECITER_ID, -1)
                val preferredReciter = reciters.find { it.id == preferredReciterId}
                if (preferredReciter != null) {
                    pageViewModel.setSelectedReciter(preferredReciter)
                }

                reciterPopupMenu.menu.clear()

                reciters.forEach { reciter ->
                    reciterPopupMenu.menu.add(Menu.NONE, reciter.id, Menu.NONE, reciter.name)
                }
            }
        }

        reciterButton.setOnClickListener {
            reciterPopupMenu.show()
        }

        reciterPopupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedReciter = currentReciters.find { it.id == menuItem.itemId }
            selectedReciter?.let {
                val sharedPreferences = requireContext().applicationContext.getSharedPreferences(QURANMOM_PREFERENCES, Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putInt(QURANMOM_PREFERRED_RECITER_ID, it.id)
                    .apply()

                pageViewModel.setSelectedReciter(it)
            }
            true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pageViewModel.selectedReciter.collect { reciter ->
                reciter?.let {
                    Toast.makeText(context, "Reciter: ${reciter.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            pageViewModel.combinedText.collect { combinedText ->
                binding.pageTextView.text = combinedText
            }
        }

        return root
    }

    companion object {
        fun newInstance(surahNumber: Int): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt("surahNumber", surahNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
