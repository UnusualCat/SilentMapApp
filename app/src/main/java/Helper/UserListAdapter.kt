package Helper

import GeofenceUtils.GeofenceSettings
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.silentmapapp.MapsActivity
import com.example.silentmapapp.R
import com.google.android.gms.maps.model.LatLng

class UserListAdapter(private var activity: Activity , private var items: ArrayList<GeofenceSettings>): BaseAdapter() {

    private class ViewHolder(row: View?) {

        var geofenceDescription: TextView? = null
        var viewButton: Button? = null
        var deleteButton: Button? = null

        init
        {
            this.geofenceDescription = row?.findViewById(R.id.geofenceDescription)
            this.viewButton = row?.findViewById(R.id.viewButton)
            this.deleteButton = row?.findViewById(R.id.deleteButton)
        }
    }

    override fun getView(position: Int , convertView: View? , parent: ViewGroup): View
    {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {

            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.listview_row_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        }
        else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.geofenceDescription?.text = items[position].toString()

        viewHolder.viewButton?.setOnClickListener {

            val intent = Intent(view?.context, MapsActivity::class.java)
            val coordinate = LatLng(items[position].latitudine, items[position].longitudine)
            intent.putExtra("coordinate", coordinate)
            view?.context?.startActivity(intent)
        }

        viewHolder.deleteButton?.setOnClickListener {
            MapsActivity.Geofence_Help.addGeofenceToRemove(items[position].geofenceID)
            items.removeAt(position)
            FileManager.saveToFile(items, view!!.context)
            this.notifyDataSetChanged()
        }

        return view as View
    }

    override fun getItem(i: Int): GeofenceSettings {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}