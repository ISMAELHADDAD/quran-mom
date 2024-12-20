package com.ismaelhaddad.quranmom

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.widget.TextView
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
import com.ismaelhaddad.quranmom.viewmodel.ReciterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Total time: 16h
// TODO: Check if the audio are there. If not, download it. (3h)
// TODO: Drawer: Dropdown for reciters (2h)
// TODO: Drawer: List surahs (1h)
// TODO: Page fragment: List ayahs + font (3h)
// TODO: Page fragment: Add player (show fab for play/stopping) (3h)
// TODO: Page fragment: Highlight word played (2h)
// TODO: Page fragment: Tap word to play only that word (1h)
// TODO: Page fragment: Swipe left for continuous playing starting from that word (1h)

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var reciterViewModel: ReciterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val databaseService = DatabaseService.getDatabase(applicationContext)
        reciterViewModel = ViewModelProvider(this, ReciterViewModel.Factory(databaseService))[ReciterViewModel::class.java]

        reciterViewModel.reciters.asLiveData().observe(this) { reciters: List<Reciter> ->
            val reciterName = reciters[1].name
            val homeTextView = findViewById<TextView>(R.id.text_home)
            homeTextView.text = reciterName
        }

//        val databaseService = DatabaseService.getDatabase(applicationContext)
//
//        // Perform database operations
//        CoroutineScope(Dispatchers.IO).launch {
//            val reciterDao = databaseService.reciterDao()
//            val reciters: List<Reciter> = reciterDao.getAll()
//            val reciterName = reciters[0].name
//            val homeTextView = findViewById<TextView>(R.id.text_home)
//            homeTextView.text = reciterName
//        }

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

}