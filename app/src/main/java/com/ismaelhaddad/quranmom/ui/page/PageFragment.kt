package com.ismaelhaddad.quranmom.ui.page

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.ismaelhaddad.quranmom.QURANMOM_PREFERENCES
import com.ismaelhaddad.quranmom.QURANMOM_PREFERRED_RECITER_ID
import com.ismaelhaddad.quranmom.R
import com.ismaelhaddad.quranmom.databinding.FragmentPageBinding
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.database.DatabaseManager
import kotlinx.coroutines.launch

class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageViewModel: PageViewModel

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val database = DatabaseManager.getDatabase(requireContext().applicationContext)
        pageViewModel = ViewModelProvider(this, PageViewModel.Factory(database))[PageViewModel::class.java]

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
            pageViewModel.ayahs.collect { ayahWordsByAyahNumber ->
                val ayahContainer = binding.ayahContainer

                for ((ayahNumber, ayahWords) in ayahWordsByAyahNumber) {
                    val ayahFlexboxLayout = FlexboxLayout(requireContext()).apply {
                        flexDirection = FlexDirection.ROW
                        layoutDirection = View.LAYOUT_DIRECTION_RTL
                        flexWrap = FlexWrap.WRAP
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(16, 8, 16, 8)
                    }

                    for (ayahWord in ayahWords) {
                        val wordTextView = TextView(requireContext()).apply {
                            text = ayahWord.wordText
                            textSize = 40f
                            setPadding(8, 8, 8, 8)
                            setTypeface(ResourcesCompat.getFont(context, R.font.qpc_hafs_font), Typeface.NORMAL)
                            setTextColor(Color.BLACK)
                            isSingleLine = true

                            val gestureDetector = GestureDetector(context, WordGestureListener({
                                // Swipe left
                                // Play until end of surah starting from this word
                            }, {
                                // Click
                                // Play this word
                            }))

                            setOnTouchListener { _, event ->
                                gestureDetector.onTouchEvent(event)
                                true
                            }
                        }

                        ayahFlexboxLayout.addView(wordTextView)
                    }

                    val ayahNumberTextView = TextView(requireContext()).apply {
                        text = "($ayahNumber)"
                        textSize = 40f
                        setPadding(8, 8, 8, 8)
                        setTypeface(ResourcesCompat.getFont(context, R.font.qpc_hafs_font), Typeface.NORMAL)
                        setTextColor(Color.BLACK)
                        isSingleLine = true
                    }

                    ayahFlexboxLayout.addView(ayahNumberTextView)
                    ayahContainer.addView(ayahFlexboxLayout)
                }
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