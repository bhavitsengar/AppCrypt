package ml.bhavitsengar.appcrypt.fragment

import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ml.bhavitsengar.appcrypt.R

import ml.bhavitsengar.appcrypt.model.AppInfo
import android.content.pm.ApplicationInfo
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.widget.SearchView
import android.view.Menu


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [AllAppsFragment.OnListFragmentInteractionListener] interface.
 */
class UnlockedAppsFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    var list = ArrayList<AppInfo>()
    private var adap: UnlockedAppsRecyclerViewAdapter? = null //////////////
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_allapps, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {

                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val list = list
                adap = UnlockedAppsRecyclerViewAdapter(list, listener) /////////
                adapter = adap

            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override  fun onPrepareOptionsMenu(menu: Menu) {
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adap!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                adap!!.filter.filter(query)
                return false
            }
        })

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: AppInfo)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                AllAppsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }


}
