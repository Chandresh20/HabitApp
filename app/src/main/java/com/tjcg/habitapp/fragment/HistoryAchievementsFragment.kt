package com.tjcg.habitapp.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjcg.habitapp.Animator
import com.tjcg.habitapp.R
import com.tjcg.habitapp.databinding.FragmentHistoryAchievementsBinding
import com.tjcg.habitapp.databinding.RecyclerItemAchievementBadgeBinding

const val BADGES_SPAN = 3
const val BADGES_FADE_DURATION = 500L

class HistoryAchievementsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentHistoryAchievementsBinding
    private lateinit var ctx: Context
    private var colorBlack : Int = 0
    private var colorWhite : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryAchievementsBinding.inflate(
            inflater, container, false
        )
        colorBlack = ResourcesCompat.getColor(ctx.resources, R.color.black, ctx.theme)
        colorWhite = ResourcesCompat.getColor(ctx.resources, R.color.white, ctx.theme)
        binding.tabA1.setOnClickListener(this)
        binding.tabA2.setOnClickListener(this)
        binding.tabA3.setOnClickListener(this)
        setTabA(0)
        generateHabitAchievementBadges(17)
        generatePerfectDaysBadges(18)
        generateBestStreaksBadges(19)
        return binding.root
    }

    override fun onClick(tabA: View?) {
        when (tabA?.id) {
            R.id.tabA1 -> setTabA(0)
            R.id.tabA2 -> setTabA(1)
            R.id.tabA3 -> setTabA(2)
        }
    }

    private fun setTabA(position: Int) {
        binding.tabA1.isSelected = false
        binding.tabA2.isSelected = false
        binding.tabA3.isSelected = false
        binding.textView1A1.setTextColor(colorBlack)
        binding.textView2A1.setTextColor(colorBlack)
        binding.textView3A1.setTextColor(colorBlack)
        binding.textView1A2.setTextColor(colorBlack)
        binding.textView2A2.setTextColor(colorBlack)
        binding.textView3A2.setTextColor(colorBlack)
        binding.textView1A3.setTextColor(colorBlack)
        binding.textView2A3.setTextColor(colorBlack)
        binding.textView3A3.setTextColor(colorBlack)
        //      binding.bestStreakRecycler.visibility = View.GONE
        //      binding.daysPerfectRecycler.visibility = View.GONE
        //      binding.habitFinishedRecycler.visibility = View.GONE
        Animator.fadeOut(binding.habitFinishedRecycler, BADGES_FADE_DURATION)
        Animator.fadeOut(binding.daysPerfectRecycler, BADGES_FADE_DURATION)
        Animator.fadeOut(binding.bestStreakRecycler, BADGES_FADE_DURATION)
        when (position) {
            0 -> {
                binding.tabA1.isSelected = true
                binding.textView1A1.setTextColor(colorWhite)
                binding.textView2A1.setTextColor(colorWhite)
                binding.textView3A1.setTextColor(colorWhite)
                //               binding.habitFinishedRecycler.visibility = View.VISIBLE
                Animator.fadeIn(binding.habitFinishedRecycler, BADGES_FADE_DURATION)
            }
            1 -> {
                binding.tabA2.isSelected = true
                binding.textView1A2.setTextColor(colorWhite)
                binding.textView2A2.setTextColor(colorWhite)
                binding.textView3A2.setTextColor(colorWhite)
                //            binding.daysPerfectRecycler.visibility = View.VISIBLE
                Animator.fadeIn(binding.daysPerfectRecycler, BADGES_FADE_DURATION)
            }
            2 -> {
                binding.tabA3.isSelected = true
                binding.textView1A3.setTextColor(colorWhite)
                binding.textView2A3.setTextColor(colorWhite)
                binding.textView3A3.setTextColor(colorWhite)
                //              binding.bestStreakRecycler.visibility = View.VISIBLE
                Animator.fadeIn(binding.bestStreakRecycler, BADGES_FADE_DURATION)
            }
        }
    }

    // This function will set the HabitAchievement view as per the number of finished habits
    private fun generateHabitAchievementBadges(finishHabits: Int) {
        val list = ArrayList<BadgePreview>()
        if (finishHabits >= 3) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_3_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_3)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_3_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_3)))
        }
        if (finishHabits >= 10) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_10_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_10)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_10_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_10)))
        }
        if (finishHabits >= 20) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_20_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_20)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_20_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_20)))
        }
        if (finishHabits >= 50) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_50_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_50)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_50_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_50)))
        }
        if (finishHabits >= 100) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_100_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_100)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_100_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_100)))
        }
        if (finishHabits >= 300) {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_300_fin, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_300)))
        } else {
            list.add(BadgePreview
                (ResourcesCompat.getDrawable(ctx.resources, R.drawable.habit_300_unf, ctx.theme)!!,
                ctx.resources.getString(R.string.habit_finished_300)))
        }
        binding.habitFinishedRecycler.layoutManager = GridLayoutManager(ctx, BADGES_SPAN)
        binding.habitFinishedRecycler.adapter = BadgePreviewAdapter(ctx, list)
    }

    private fun generatePerfectDaysBadges(perfectDays : Int) {
        val list = ArrayList<BadgePreview>()
        if (perfectDays >= 3) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_3_fin, ctx.theme)!!,
                    "3 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_3_unf, ctx.theme)!!,
                    "3 ${resources.getString(R.string.day_perfect)}"))
        }
        if (perfectDays >= 10) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_10_fin, ctx.theme)!!,
                    "10 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_10_unf, ctx.theme)!!,
                    "10 ${resources.getString(R.string.day_perfect)}"))
        }
        if (perfectDays >= 20) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_20_fin, ctx.theme)!!,
                    "20 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_20_unf, ctx.theme)!!,
                    "20 ${resources.getString(R.string.day_perfect)}"))
        }
        if (perfectDays >= 30) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_30_fin, ctx.theme)!!,
                    "30 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_30_unf, ctx.theme)!!,
                    "30 ${resources.getString(R.string.day_perfect)}"))
        }
        if (perfectDays >= 50) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_50_fin, ctx.theme)!!,
                    "50 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_50_unf, ctx.theme)!!,
                    "50 ${resources.getString(R.string.day_perfect)}"))
        }
        if (perfectDays >= 100) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_100_fin, ctx.theme)!!,
                    "100 ${resources.getString(R.string.day_perfect)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.day_100_unf, ctx.theme)!!,
                    "100 ${resources.getString(R.string.day_perfect)}"))
        }
        binding.daysPerfectRecycler.layoutManager = GridLayoutManager(ctx, BADGES_SPAN)
        binding.daysPerfectRecycler.adapter = BadgePreviewAdapter(ctx, list)

    }

    private fun generateBestStreaksBadges(bestStreak: Int) {
        val list = ArrayList<BadgePreview>()
        if (bestStreak >= 3) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_3_fin, ctx.theme)!!,
                    "3 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_3_unf, ctx.theme)!!,
                    "3 ${resources.getString(R.string.day_streak)}"))
        }
        if (bestStreak >= 10) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_10_fin, ctx.theme)!!,
                    "10 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_10_unf, ctx.theme)!!,
                    "10 ${resources.getString(R.string.day_streak)}"))
        }
        if (bestStreak >= 20) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_20_fin, ctx.theme)!!,
                    "20 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_20_unf, ctx.theme)!!,
                    "20 ${resources.getString(R.string.day_streak)}"))
        }
        if (bestStreak >= 50) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_50_fin, ctx.theme)!!,
                    "50 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_50_unf, ctx.theme)!!,
                    "50 ${resources.getString(R.string.day_streak)}"))
        }
        if (bestStreak >= 100) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_100_fin, ctx.theme)!!,
                    "100 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_100_unf, ctx.theme)!!,
                    "100 ${resources.getString(R.string.day_streak)}"))
        }
        if (bestStreak >= 300) {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_300_fin, ctx.theme)!!,
                    "300 ${resources.getString(R.string.day_streak)}"))
        } else {
            list.add(
                BadgePreview(
                    ResourcesCompat.getDrawable(ctx.resources, R.drawable.streak_300_unf, ctx.theme)!!,
                    "300 ${resources.getString(R.string.day_streak)}"))
        }
        binding.bestStreakRecycler.layoutManager = GridLayoutManager(ctx, BADGES_SPAN)
        binding.bestStreakRecycler.adapter = BadgePreviewAdapter(ctx, list)
    }


    //Adapter for above function
    class BadgePreviewAdapter(private val ctx: Context,
                              private val previews:
                              ArrayList<BadgePreview>) :
        RecyclerView.Adapter<BadgePreviewAdapter.HabitHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BadgePreviewAdapter.HabitHolder {
            return HabitHolder(
                RecyclerItemAchievementBadgeBinding
                    .inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: BadgePreviewAdapter.HabitHolder, position: Int) {
            holder.binding.habitAchImage.setImageDrawable(previews[position].image)
            holder.binding.habitAchText.text = previews[position].text
        }

        override fun getItemCount(): Int = previews.size

        inner class HabitHolder(val binding: RecyclerItemAchievementBadgeBinding) :
            RecyclerView.ViewHolder(binding.root)

    }


    companion object {
        fun getInstance(ctx: Context) : HistoryAchievementsFragment {
            val fragment = HistoryAchievementsFragment()
            fragment.ctx = ctx
            return fragment
        }

        class BadgePreview(val image: Drawable, val text:String)
    }
}