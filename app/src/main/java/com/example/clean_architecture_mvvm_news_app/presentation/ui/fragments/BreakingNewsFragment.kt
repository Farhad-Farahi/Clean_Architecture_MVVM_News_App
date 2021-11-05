package com.example.clean_architecture_mvvm_news_app.presentation.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clean_architecture_mvvm_news_app.R
import com.example.clean_architecture_mvvm_news_app.presentation.adapters.NewsAdapter
import com.example.clean_architecture_mvvm_news_app.databinding.FragmentBreakingNewsBinding
import com.example.clean_architecture_mvvm_news_app.presentation.viewmodels.BreakingNewsViewModel
import com.example.clean_architecture_mvvm_news_app.domain.common.Constants.QUERY_PAGE_SIZE
import com.example.clean_architecture_mvvm_news_app.domain.common.Constants.TYPE_SEARCH_AND_BREAKING_FRAGMENT
import com.example.clean_architecture_mvvm_news_app.domain.common.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {


    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: BreakingNewsViewModel by viewModels()


    lateinit var newsAdapter: NewsAdapter




    val TAG = "Error_BNF"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()

        onClick(view)

        breakingNewsObserver()



        swipeRefreshLayoutFunctionality()

    }

    private fun swipeRefreshLayoutFunctionality() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            manualRefresh()
        }
    }

    private fun breakingNewsObserver() {
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->

            when (response) {

                is Resource.Success -> {
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList()) //listDiffer cant work with MutableList
                        //determine for last
                        //+2 = +1 FOR rounding and another +1 because the last page of our response will always  be empty
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                    hideProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "an error occurred:$message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }


        })
    }

    private fun onClick(view: View) {
        newsAdapter.setonItemClickListener {
            val uri = Uri.parse(it.url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            requireActivity().startActivity(intent)

        }

        newsAdapter.setOnSaveButtonClickListener { article ->
            viewModel.saveArticle(article = article)
            Snackbar.make(view, "Article saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
        isLoading = false
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRv() {
        newsAdapter = NewsAdapter(TYPE_SEARCH_AND_BREAKING_FRAGMENT)
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun manualRefresh() {
        viewModel.onManualRefresh()
    }

    //pagination
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //if its scrolling
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //calculation to understand we are scrolled until the bottom
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem =
                firstVisibleItemPosition + visibleItemCount >= totalItemCount //last item is visible
            val isNotAtBeginning =
                firstVisibleItemPosition >= 0 //determine if we already scrolled little bit down
            val isTotalMoreThanVisible =
                totalItemCount >= QUERY_PAGE_SIZE //check total item is more than visible
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling


            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }

        }
    }

}