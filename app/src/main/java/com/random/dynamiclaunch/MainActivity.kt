package com.random.dynamiclaunch

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.random.dynamiclaunch.databinding.ActivityMainBinding
import com.random.dynamiclaunch.utils.updateComponentState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("launcher_data")

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAIN_COMPONENT = "MainActivity"
        const val MAIN_ALIAS_COMPONENT = "LauncherAlias"
        const val LAUNCHER_1 = 1
        const val LAUNCHER_2 = 2
    }

    private val launcherIconKey = intPreferencesKey("launcher_icon")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        updateLauncherIconFromPreference()
    }

    private fun initUi() {
        binding.launcher1.setOnClickListener {
            setNewComponentState(LAUNCHER_1)

        }
        binding.launcher2.setOnClickListener {
            setNewComponentState(LAUNCHER_2)
        }
    }

    private fun setNewComponentState(newState: Int) {
        lifecycleScope.launch {
            dataStore.edit {
                it[launcherIconKey] = newState
            }
            updateUiAndComponentState(newState)
        }
    }

    private fun updateLauncherIconFromPreference() {
        lifecycleScope.launch {
            dataStore.data.flowWithLifecycle(lifecycle).map { it[launcherIconKey] ?: LAUNCHER_1 }
                .collectLatest { icon ->
                    updateUiAndComponentState(icon)
                }
        }
    }

    private fun updateUiAndComponentState(icon: Int) {
        binding.currentLauncher.text = getString(R.string.launcher_d, icon)
        binding.currentIconIV.setImageResource(if (icon == LAUNCHER_1) R.mipmap.ic_launcher else R.mipmap.ic_launcher_2)
        val icon1State = getComponentStateByPreference { icon == LAUNCHER_1 }
        val icon2State = getComponentStateByPreference { icon == LAUNCHER_2 }
        updateComponentState(MAIN_COMPONENT, icon1State)
        updateComponentState(MAIN_ALIAS_COMPONENT, icon2State)
    }

    private fun getComponentStateByPreference(block: () -> Boolean) =
        if (block()) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED

}