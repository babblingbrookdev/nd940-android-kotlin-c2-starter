package com.udacity.asteroidradar.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.udacity.asteroidradar.BR
import com.udacity.asteroidradar.MainActivity
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentDetailBinding
import com.udacity.asteroidradar.main.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            activity,
            MainViewModel.Factory(activity.application)
        )[MainViewModel::class.java]
    }

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val asteroid = DetailFragmentArgs.fromBundle(arguments!!).selectedAsteroid
        binding.asteroid = asteroid
        binding.viewModel = viewModel

        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        (activity as MainActivity).supportActionBar?.title = asteroid.codename

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailAsteroid.collect { asteroid ->
                    asteroid?.let {
                        binding.asteroid = asteroid
                        binding.notifyPropertyChanged(BR.asteroid)
                        binding.executePendingBindings()
                    }
                }
            }
        }
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(activity!!)
            .setMessage(getString(R.string.astronomica_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }
}
