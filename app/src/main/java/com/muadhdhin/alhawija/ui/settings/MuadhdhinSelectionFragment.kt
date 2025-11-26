package com.muadhdhin.alhawija.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.muadhdhin.alhawija.DataStoreManager
import com.muadhdhin.alhawija.R
import com.muadhdhin.alhawija.databinding.FragmentMuadhdhinSelectionBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MuadhdhinSelectionFragment : Fragment() {

    private var _binding: FragmentMuadhdhinSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMuadhdhinSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataStoreManager = DataStoreManager(requireContext())

        loadCurrentSelection()
        setupSaveButton()
    }

    private fun loadCurrentSelection() {
        lifecycleScope.launch {
            dataStoreManager.adhanSoundFlow.collect { index ->
                val radioButtonId = when (index) {
                    1 -> R.id.radio_adhan_1
                    2 -> R.id.radio_adhan_2
                    3 -> R.id.radio_adhan_3
                    else -> R.id.radio_adhan_1
                }
                binding.radioGroupAdhanSounds.check(radioButtonId)
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveSelection.setOnClickListener {
            val selectedId = binding.radioGroupAdhanSounds.checkedRadioButtonId
            val selectedIndex = when (selectedId) {
                R.id.radio_adhan_1 -> 1
                R.id.radio_adhan_2 -> 2
                R.id.radio_adhan_3 -> 3
                else -> 1
            }

            lifecycleScope.launch {
                dataStoreManager.saveAdhanSound(selectedIndex)
                Toast.makeText(requireContext(), "تم حفظ اختيار المؤذن", Toast.LENGTH_SHORT).show()
                // TODO: العودة إلى شاشة الإعدادات
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
