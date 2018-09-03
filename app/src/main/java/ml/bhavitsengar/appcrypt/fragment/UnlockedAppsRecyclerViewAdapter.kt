package ml.bhavitsengar.appcrypt.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ml.bhavitsengar.appcrypt.R


import ml.bhavitsengar.appcrypt.fragment.UnlockedAppsFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.listitem_allapps.view.*
import ml.bhavitsengar.appcrypt.model.AppInfo
import java.nio.file.Files.size



/**
 * [RecyclerView.Adapter] that can display a [AppInfo] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class UnlockedAppsRecyclerViewAdapter(
        private val mValues: ArrayList<AppInfo>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<UnlockedAppsRecyclerViewAdapter.ViewHolder>(), Filterable {  ////////////////

    private var itemsFiltered: ArrayList<AppInfo> = mValues
    private val mOnClickListener: View.OnClickListener

    init {

        mOnClickListener = View.OnClickListener { v ->

            v.is_locked.isChecked = !v.is_locked.isChecked

        }
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val query = charSequence.toString()

                var filtered: MutableList<AppInfo> = ArrayList()

                if (query.isEmpty()) {
                    filtered = mValues
                } else {
                    for (appInfo in mValues) {
                        if (appInfo.appName!!.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(appInfo)
                        }
                    }
                }

                val results = FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            override fun publishResults(charSequence: CharSequence, results: FilterResults) {
                itemsFiltered = results.values as ArrayList<AppInfo>
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.listitem_allapps, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsFiltered[position]
        holder.appName.text = item.appName
        holder.appIcon.setImageDrawable(item.icon)
        holder.isLocked.isChecked = item.isLocked

        /**
         * Had to set recyclable false because we have to persist the state of Switch button, and because of the recycle
         * property, the recyclerview was setting the state of switch-button to OFF every-time when the list item was going out
         * of the view on scrolling.
         */
        holder.setIsRecyclable(false)
        with(holder.isLocked){

            setOnCheckedChangeListener{ _, isChecked ->

                itemsFiltered[position].isLocked = isChecked
                mListener?.onListFragmentInteraction(itemsFiltered[position])
            }

        }
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = itemsFiltered.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val appName: TextView = mView.app_name
        val appIcon: ImageView = mView.app_image
        val isLocked : Switch = mView.is_locked

        override fun toString(): String {
            return super.toString() + " '" + appName.text + "'"
        }
    }
}
