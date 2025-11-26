package com.muadhdhin.alhawija.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.muadhdhin.alhawija.MainViewModel
import com.muadhdhin.alhawija.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ربط البيانات مع ViewModel
        viewModel.currentTime.observe(viewLifecycleOwner) { time ->
            binding.tvCurrentTime.text = time
        }

        viewModel.hijriDate.observe(viewLifecycleOwner) { date ->
            binding.tvHijriDate.text = date
        }

        viewModel.nextPrayerName.observe(viewLifecycleOwner) { name ->
            binding.tvNextPrayerName.text = name
        }

        viewModel.countdown.observe(viewLifecycleOwner) { countdown ->
            binding.tvCountdown.text = countdown
        }

        // إضافة مستمعي الأحداث للأزرار (سيتم تنفيذ التنقل في مرحلة لاحقة)
        binding.btnSettings.setOnClickListener {
            // TODO: التنقل إلى شاشة الإعدادات
        }

        binding.btnPrayerTimes.setOnClickListener {
            // TODO: التنقل إلى شاشة مواقيت الصلاة
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
