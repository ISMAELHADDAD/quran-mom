package com.ismaelhaddad.quranmom

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.ismaelhaddad.quranmom.databinding.ActivityMainBinding
import com.ismaelhaddad.quranmom.model.Surah
import com.ismaelhaddad.quranmom.service.DatabaseService
import com.ismaelhaddad.quranmom.ui.page.PageFragment

// Total time: 16h
// DONE: Check if the audio are there. If not, download it. (3h)
// DONE: Drawer: Dropdown for reciters (2h)
// DONE: Drawer: List surahs (1h)
// TODO: Page fragment: List ayahs + font (3h)
// TODO: Page fragment: Add player (show fab for play/stopping) (3h)
// TODO: Page fragment: Highlight word played (2h)
// TODO: Page fragment: Tap word to play only that word (1h)
// TODO: Page fragment: Swipe left for continuous playing starting from that word (1h)

const val QURANMOM_PREFERENCES = "QuranMomPreferences"
const val QURANMOM_PREFERENCES_IS_FIRST_TIME = "isFirstTime"
const val QURANMOM_PREFERRED_RECITER_ID = "preferredReciterId"
const val QURANMOM_AUDIO_DIR = "audio"

class MainActivity : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var progressDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val databaseService = DatabaseService.getDatabase(applicationContext)
        mainActivityViewModel = ViewModelProvider(this, MainActivityViewModel.Factory(databaseService))[MainActivityViewModel::class.java]

        val sharedPreferences = applicationContext.getSharedPreferences(QURANMOM_PREFERENCES, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(QURANMOM_PREFERENCES_IS_FIRST_TIME, true)

        if (isFirstTime) {
            // Show a progress dialog if it's the first launch
            showProgressDialog()

            // Set the flag so it won't show again
            sharedPreferences.edit().putBoolean(QURANMOM_PREFERENCES_IS_FIRST_TIME, false).apply()

            // Start the download process
            mainActivityViewModel.checkAndDownloadAudios(this) { progress, total ->
                updateProgress(progress, total)
                if (progress == total) {
                    progressDialog.dismiss()
                }
            }
        }

        val navView: NavigationView = binding.navView
        val menu: Menu = navView.menu

        setSupportActionBar(binding.toolbar)
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        mainActivityViewModel.surahs.asLiveData().observe(this) { surahs: List<Surah> ->
            menu.clear()
            surahs.forEach { surah ->
                menu.add(0, surah.number, Menu.NONE, "${surah.number}. ${surah.name}").setOnMenuItemClickListener {
                    val fragment = PageFragment.newInstance(surah.number)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                    supportActionBar?.title = "${surah.number}. ${surah.name}"
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }

            // Set last surah as first page
            val lastSurah = surahs.last()
            val fragment = PageFragment.newInstance(lastSurah.number)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            supportActionBar?.title = "${lastSurah.number}. ${lastSurah.name}"
        }
    }

    private fun showProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null)
        progressBar = dialogView.findViewById(R.id.progressBar)
        progressText = dialogView.findViewById(R.id.progressText)

        dialogBuilder.setView(dialogView)
            .setCancelable(false)

        progressDialog = dialogBuilder.create()
        progressDialog.show()
    }

    private fun updateProgress(progress: Int, total: Int) {
        progressBar.max = total
        progressBar.progress = progress
        progressText.text = "Descargando ($progress/$total) surahs"
    }
}