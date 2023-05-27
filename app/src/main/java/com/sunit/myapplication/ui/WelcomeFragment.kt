package com.sunit.myapplication.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.sunit.myapplication.R
import com.sunit.myapplication.data.ApiService
import com.sunit.myapplication.databinding.FragmentWelcomeBinding
import com.sunit.myapplication.ui.adapter.AppsListAdapter
import com.sunit.myapplication.ui.model.AppInfo

class WelcomeFragment : Fragment() {
    private val appsListAdapter by lazy { AppsListAdapter {
        handleButtonClick(it)
    }}

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var searchView: SearchView
    private lateinit var pm: PackageManager
    private var denyList = emptyList<String>()
    private val appsList = mutableListOf<AppInfo>()

    private val welcomeRepository by lazy {
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
        initMenu()
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
        packages.forEach {
            val info = pm.getApplicationInfo(it.packageName, 0)
            val icon = pm.getApplicationIcon(it.packageName)
            val displayName = pm.getApplicationLabel(info).toString()

            appsList.add(AppInfo(displayName, it.packageName, icon))
        }

        appsList.sortBy {
            it.displayName
        }

        binding.appsListRv.adapter = appsListAdapter
        appsListAdapter.submitList(appsList)
    }

    private fun handleButtonClick(item: AppInfo) {
        if(denyList.contains(item.pkgName)) {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intent = pm.getLaunchIntentForPackage(item.pkgName)

        if (intent?.resolveActivity(pm) != null)
            startActivity(intent)
        else 
            Toast.makeText(context, "App cannot be launched", Toast.LENGTH_SHORT).show()
    }

    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)

                initSearchMenuItem(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initSearchMenuItem(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.action_search)
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                appsListAdapter.submitList(appsList)
                return true
            }
        })

        searchView = searchMenuItem.actionView!! as SearchView
        setSearchQueryListener()
    }

    private fun setSearchQueryListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterAppsList(query.orEmpty())
                return true
            }
        })

        searchView.onActionViewCollapsed()
    }

    private fun filterAppsList(query: String) {
        val filteredList = appsList.filter {
            it.displayName.contains(query, ignoreCase = true)
        }

        appsListAdapter.submitList(filteredList)
    }
}
