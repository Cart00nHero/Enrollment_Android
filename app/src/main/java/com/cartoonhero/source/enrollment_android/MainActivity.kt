package com.cartoonhero.source.enrollment_android

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import com.cartoonhero.source.enrollment_android.scene.NavigationActivity
import com.cartoonhero.source.enrollment_android.databinding.ActivityMainBinding
import com.cartoonhero.source.enrollment_android.scene.qrCode.QRCodeFragment
import com.cartoonhero.source.enrollment_android.scene.visitor.VisitorScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

const val SceneBundleKey = "SCENE_BUNDLE_KEY"
const val MainFragmentContainer = R.id.main_container
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : NavigationActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val sceneName = intent.getStringExtra(SceneBundleKey)
        if (sceneName.isNullOrEmpty()) {
            setRootFragment(QRCodeFragment(),MainFragmentContainer)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
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