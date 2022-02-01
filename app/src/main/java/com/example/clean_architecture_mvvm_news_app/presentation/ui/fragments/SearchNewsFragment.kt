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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clean_architecture_mvvm_news_app.R
import com.example.clean_architecture_mvvm_news_app.presentation.adapters.NewsAdapter
import com.example.clean_architecture_mvvm_news_app.databinding.FragmentSearchNewsBinding
import com.example.clean_architecture_mvvm_news_app.presentation.viewmodels.SearchNewsViewModel
import com.example.clean_architecture_mvvm_news_app.domain.common.Constants
import com.example.clean_architecture_mvvm_news_app.domain.common.Constants.SEARCH_NEWS_TIME_DELAY
import com.example.clean_architecture_mvvm_news_app.domain.common.Constants.TYPE_SEARCH_AND_BREAKING_FRAGMENT
import com.example.clean_architecture_mvvm_news_app.domain.common.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {


    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchNewsViewModel by viewModels()

    lateinit var newsAdapter: NewsAdapter

    val TAG = "Error_SNF"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        onClick(view)


        search()

        searchNewsObserver()

    }

    private fun search() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->

            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().trim().isNotEmpty()) {
                        viewModel.searchNewsPage = 1
                        viewModel.searchNewsResponse = null
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

    }


    private fun searchNewsObserver() {
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //determine for last
                        //+2 = +1 FOR rounding and another +1 because the last page of our response will always  be empty
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = false

    }

    private fun setUpRv() {
        newsAdapter = NewsAdapter(TYPE_SEARCH_AND_BREAKING_FRAGMENT)
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }


    private fun onClick(view: View) {
        newsAdapter.setonItemClickListener {
            val uri = Uri.parse(it.url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            requireActivity().startActivity(intent)
        }
        newsAdapter.setOnSaveButtonClickListener { article ->
            viewModel.saveArticle(article = article)
            val snackBar = Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                "Article saved Successfully",
                Snackbar.LENGTH_SHORT
            )
            snackBar.setAction("OK") { // Call your action method here
                snackBar.dismiss()
            }
            snackBar.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                totalItemCount >= Constants.QUERY_PAGE_SIZE //check total item is more than visible
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling


            if (shouldPaginate) {
                viewModel.searchNews(binding.etSearch.text.toString().trim())
                isScrolling = false
            }

        }
    }
}