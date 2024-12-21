package com.ismaelhaddad.quranmom

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.ismaelhaddad.quranmom.databinding.ActivityMainBinding
import com.ismaelhaddad.quranmom.model.Reciter
import com.ismaelhaddad.quranmom.service.DatabaseService
import com.ismaelhaddad.quranmom.viewmodel.ReciterSurahAudioViewModel

// Total time: 16h
// DONE: Check if the audio are there. If not, download it. (3h)
// TODO: Drawer: Dropdown for reciters (2h)
// TODO: Drawer: List surahs (1h)
// TODO: Page fragment: List ayahs + font (3h)
// TODO: Page fragment: Add player (show fab for play/stopping) (3h)
// TODO: Page fragment: Highlight word played (2h)
// TODO: Page fragment: Tap word to play only that word (1h)
// TODO: Page fragment: Swipe left for continuous playing starting from that word (1h)

const val QURANMOM_PREFERENCES = "QuranMomPreferences"
const val QURANMOM_PREFERENCES_IS_FIRST_TIME = "isFirstTime"
const val QURANMOM_AUDIO_DIR = "audio"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var reciterSurahAudioViewModel: ReciterSurahAudioViewModel
    private lateinit var progressDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val databaseService = DatabaseService.getDatabase(applicationContext)
        reciterSurahAudioViewModel = ViewModelProvider(this, ReciterSurahAudioViewModel.Factory(databaseService))[ReciterSurahAudioViewModel::class.java]

        val sharedPreferences = applicationContext.getSharedPreferences(QURANMOM_PREFERENCES, Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(QURANMOM_PREFERENCES_IS_FIRST_TIME, true)

        if (isFirstTime) {
            // Show a progress dialog if it's the first launch
            showProgressDialog()

            // Set the flag so it won't show again
            sharedPreferences.edit().putBoolean(QURANMOM_PREFERENCES_IS_FIRST_TIME, false).apply()

            // Start the download process
            reciterSurahAudioViewModel.checkAndDownloadAudios(this) { progress, total ->
                updateProgress(progress, total)
                if (progress == total) {
                    progressDialog.dismiss()
                }
            }
        }

        reciterSurahAudioViewModel.reciters.asLiveData().observe(this) { reciters: List<Reciter> ->
            val reciterName = reciters[1].name
            val homeTextView = findViewById<TextView>(R.id.text_home)
            homeTextView.text = reciterName
        }

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Do something", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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