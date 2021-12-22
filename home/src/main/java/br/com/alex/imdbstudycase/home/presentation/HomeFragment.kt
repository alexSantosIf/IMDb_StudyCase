package br.com.alex.imdbstudycase.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import br.com.alex.imdbstudycase.home.R
import br.com.alex.imdbstudycase.home.data.model.MovieModelData
import br.com.alex.imdbstudycase.home.databinding.FragmentHomeBinding
import br.com.alex.imdbstudycase.home.presentation.adapter.HomeMoviesAdapter
import br.com.alex.imdbstudycase.router.FeatureRouter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    private val featureRouter: FeatureRouter by inject()

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeMoviesAdapter: HomeMoviesAdapter

    private var bestMoviesItems: List<MovieModelData> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setHomeMoviesList()
        getBestMovies()
        openFavorites()
        setSearchViewActions()
    }

    private fun setSearchViewActions() {
        binding.searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                binding.searchViewHome.clearFocus()
                binding.recyclerviewHomeMovies.showShimmer()
                getSearchedMovie(text)
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text.isNullOrEmpty()) {
                    showHomeMoviesList(bestMoviesItems)
                }
                return false
            }
        })

        binding.searchViewHome.setOnClickListener {
            binding.searchViewHome.isIconified = false
        }
    }

    private fun getSearchedMovie(text: String?) {
        homeViewModel.getSearchMovie(text)
        homeViewModel.searchMovie.observe(viewLifecycleOwner, { movies ->
            movies?.let { movies ->
                showHomeMoviesList(movies)
            }
        })
    }

    private fun getBestMovies() {
        homeViewModel.getMovies()
        homeViewModel.movies.observe(viewLifecycleOwner, { movies ->
            movies?.let { movies ->
                bestMoviesItems = movies
                showHomeMoviesList(movies)
            }
        })
    }

    private fun showHomeMoviesList(items: List<MovieModelData>) {
        homeMoviesAdapter =
            HomeMoviesAdapter(requireActivity(), items, featureRouter)
        binding.recyclerviewHomeMovies.adapter = homeMoviesAdapter
        binding.recyclerviewHomeMovies.hideShimmer()
    }

    private fun setHomeMoviesList() {
        binding.recyclerviewHomeMovies.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        binding.recyclerviewHomeMovies.setItemViewType { _, _ ->
            return@setItemViewType R.layout.shimmer_placeholder_layout
        }
        binding.recyclerviewHomeMovies.showShimmer()
    }

    private fun openFavorites() {
        binding.buttonFavorites.setOnClickListener {
            //            featureRouter.start(requireActivity(), OpenFavoritesAction)
        }
    }

    companion object {

        const val MOVIE_ID_KEY = "movie_id"
    }
}