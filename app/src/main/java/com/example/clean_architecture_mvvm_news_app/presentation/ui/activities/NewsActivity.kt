package com.example.clean_architecture_mvvm_news_app.presentation.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.clean_architecture_mvvm_news_app.R
import com.example.clean_architecture_mvvm_news_app.databinding.ActivityNewsBinding
import com.example.clean_architecture_mvvm_news_app.presentation.ui.fragments.BreakingNewsFragment
import com.example.clean_architecture_mvvm_news_app.presentation.ui.fragments.SavedNewsFragment
import com.example.clean_architecture_mvvm_news_app.presentation.ui.fragments.SearchNewsFragment
import dagger.hilt.android.AndroidEntryPoint


private const val TAG_Breaking_News_FRAGMENT = "TAG_Breaking_News_FRAGMENT"
private const val TAG_Saved_News_FRAGMENT = "TAG_Saved_News_FRAGMENT"
private const val TAG_Search_News_FRAGMENT = "TAG_Search_News_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {



    private lateinit var binding: ActivityNewsBinding

    private lateinit var breakingNewsFragment: BreakingNewsFragment
    private lateinit var savedNewsFragment: SavedNewsFragment
    private lateinit var searchNewsFragment: SearchNewsFragment




    private val fragments: Array<Fragment>
        get() = arrayOf(
            breakingNewsFragment,
            savedNewsFragment,
            searchNewsFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (savedInstanceState == null) {
            breakingNewsFragment = BreakingNewsFragment()
            savedNewsFragment= SavedNewsFragment()
            searchNewsFragment= SearchNewsFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, breakingNewsFragment, TAG_Breaking_News_FRAGMENT)
                .add(R.id.fragment_container, savedNewsFragment, TAG_Saved_News_FRAGMENT)
                .add(R.id.fragment_container, searchNewsFragment, TAG_Search_News_FRAGMENT)
                .commit()
        } else {
            breakingNewsFragment =
                supportFragmentManager.findFragmentByTag(TAG_Breaking_News_FRAGMENT) as BreakingNewsFragment
            savedNewsFragment =
                supportFragmentManager.findFragmentByTag(TAG_Saved_News_FRAGMENT) as SavedNewsFragment
            searchNewsFragment =
                supportFragmentManager.findFragmentByTag(TAG_Search_News_FRAGMENT) as SearchNewsFragment

            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
        }

        selectFragment(selectedFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_breakingNewsFragment -> {breakingNewsFragment}
                R.id.nav_savedNewsFragment -> savedNewsFragment
                R.id.nav_searchNewsFragment -> searchNewsFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            selectFragment(fragment)
            true
        }

        binding.bottomNavigationView.setOnItemReselectedListener {

        }
    }
    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }
        transaction.commit()


        title = when (selectedFragment) {
            is BreakingNewsFragment -> getString(R.string.breakingNewsFragment)
            is SavedNewsFragment -> getString(R.string.savedNewsFragment)
            is SearchNewsFragment -> getString(R.string.searchNewsFragment)
            else -> ""
        }
    }
    override fun onBackPressed() {
        if (selectedIndex != 0) {
            binding.bottomNavigationView.selectedItemId = R.id.nav_breakingNewsFragment
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }
}