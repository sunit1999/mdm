package com.sunit.myapplication.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sunit.myapplication.data.ApiService
import com.sunit.myapplication.databinding.FragmentWelcomeBinding
import com.sunit.myapplication.ui.adapter.AppsListAdapter
import com.sunit.myapplication.ui.model.AppInfo

class WelcomeFragment : Fragment() {
    private val appsListAdapter by lazy { AppsListAdapter {
        handleButtonClick(it)
    }}

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var pm: PackageManager
    private var denyList = emptyList<String>()

    private val  welcomeRepository by lazy {
        WelcomeRepository(api = ApiService.service)
    }

    private val welcomeViewModel by lazy {
        WelcomeViewModel(welcomeRepository)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pm = requireActivity().packageManager

        setUpObservers()
        initUi()
        welcomeViewModel.getDenyList()
    }

    private fun setUpObservers() {
        welcomeViewModel.denyList.observe(viewLifecycleOwner) {
            denyList = it
        }
    }

    private fun initUi() {
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        val appsList = mutableListOf<AppInfo>()
        packages.forEach {
            val info = pm.getApplicationInfo(it.packageName, 0)
            val icon = pm.getApplicationIcon(it.packageName)
            val displayName = pm.getApplicationLabel(info).toString()

            appsList.add(AppInfo(displayName, it.packageName, icon))
        }

        binding.appsListRv.adapter = appsListAdapter
        appsListAdapter.submitList(appsList)
    }

    private fun handleButtonClick(item: AppInfo) {
        if(denyList.contains(item.pkgName)) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        } else {
            val intent = pm.getLaunchIntentForPackage(item.pkgName)
            startActivity(intent)
        }
    }
}
