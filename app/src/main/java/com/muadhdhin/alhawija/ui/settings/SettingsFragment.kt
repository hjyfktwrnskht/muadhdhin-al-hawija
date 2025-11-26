package com.muadhdhin.alhawija.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.muadhdhin.alhawija.DataStoreManager
import com.muadhdhin.alhawija.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager(requireContext())

        setupFajrAdhanSwitch()
        setupVolumeSeekBar()
        setupNightModeSwitch()
        setupClickListeners()
    }

    private fun setupFajrAdhanSwitch() {
        lifecycleScope.launch {
            dataStoreManager.fajrAdhanToggleFlow.collect { isEnabled ->
                binding.switchFajrAdhan.isChecked = isEnabled
            }
        }

        binding.switchFajrAdhan.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                dataStoreManager.saveFajrAdhanToggle(isChecked)
                // TODO: إعادة جدولة الأذان عند تغيير هذا الإعداد
            }
        }
    }

    private fun setupVolumeSeekBar() {
        lifecycleScope.launch {
            dataStoreManager.volumeFlow.collect { volume ->
                binding.seekbarVolume.progress = volume
            }
        }

        binding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // لا شيء
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // لا شيء
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { volume ->
                    lifecycleScope.launch {
                        dataStoreManager.saveVolume(volume)
                    }
                }
            }
        })
    }

    private fun setupNightModeSwitch() {
        lifecycleScope.launch {
            dataStoreManager.nightModeFlow.collect { isEnabled ->
                binding.switchNightMode.isChecked = isEnabled
            }
        }

        binding.switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                dataStoreManager.saveNightMode(isChecked)
                // TODO: تطبيق الوضع الليلي على الثيم
            }
        }
    }

    private fun setupClickListeners() {
        binding.tvSelectMuadhdhin.setOnClickListener {
            // TODO: التنقل إلى شاشة اختيار المؤذن
        }

        binding.tvNotificationSettings.setOnClickListener {
            // فتح إعدادات الإشعارات الخاصة بالتطبيق
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
            }
            startActivity(intent)
        }

        binding.tvBatterySettings.setOnClickListener {
            // فتح إعدادات تجاهل تحسينات البطارية
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
