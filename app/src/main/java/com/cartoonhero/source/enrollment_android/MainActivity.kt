package com.cartoonhero.source.enrollment_android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.cartoonhero.source.enrollment_android.scene.NavigationActivity
import com.cartoonhero.source.enrollment_android.databinding.ActivityMainBinding
import com.cartoonhero.source.enrollment_android.scene.openning.OpenningFragment

const val OpeningScene = "OPENING_SCENE"
const val StageResId = R.id.main_container
class MainActivity : NavigationActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val sceneName = intent.getStringExtra(OpeningScene)
        if (sceneName.isNullOrEmpty()) {
            setRootFragment(OpenningFragment(),StageResId)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}