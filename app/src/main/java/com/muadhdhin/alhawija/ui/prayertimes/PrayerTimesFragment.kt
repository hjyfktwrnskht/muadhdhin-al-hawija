package com.muadhdhin.alhawija.ui.prayertimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.muadhdhin.alhawija.PrayerTimesManager
import com.muadhdhin.alhawija.R
import com.muadhdhin.alhawija.databinding.FragmentPrayerTimesBinding
import com.muadhdhin.alhawija.databinding.ItemPrayerTimeBinding
import java.time.format.DateTimeFormatter

data class PrayerItem(val name: String, val time: String)

class PrayerTimesFragment : Fragment() {

    private var _binding: FragmentPrayerTimesBinding? = null
    private val binding get() = _binding!!
    private lateinit var prayerTimesManager: PrayerTimesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prayerTimesManager = PrayerTimesManager(requireContext())
        displayPrayerTimes()
    }

    private fun displayPrayerTimes() {
        val todayTimes = prayerTimesManager.getTodayPrayerTimes()
        if (todayTimes != null) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val prayerList = listOf(
                PrayerItem("الفجر", todayTimes.fajrSecond.format(formatter)),
                PrayerItem("الشروق", todayTimes.sunrise.format(formatter)),
                PrayerItem("الظهر", todayTimes.dhuhr.format(formatter)),
                PrayerItem("العصر", todayTimes.asr.format(formatter)),
                PrayerItem("المغرب", todayTimes.maghrib.format(formatter)),
                PrayerItem("العشاء", todayTimes.isha.format(formatter))
            )
            binding.rvPrayerTimes.adapter = PrayerTimesAdapter(prayerList)
        } else {
            // عرض رسالة خطأ أو لا توجد بيانات
            binding.tvTitle.text = "لا تتوفر مواقيت صلاة لهذا اليوم"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PrayerTimesAdapter(private val prayerList: List<PrayerItem>) :
    RecyclerView.Adapter<PrayerTimesAdapter.PrayerTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerTimeViewHolder {
        val binding = ItemPrayerTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrayerTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrayerTimeViewHolder, position: Int) {
        holder.bind(prayerList[position])
    }

    override fun getItemCount(): Int = prayerList.size

    class PrayerTimeViewHolder(private val binding: ItemPrayerTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prayerItem: PrayerItem) {
            binding.tvPrayerName.text = prayerItem.name
            binding.tvPrayerTime.text = prayerItem.time
        }
    }
}
