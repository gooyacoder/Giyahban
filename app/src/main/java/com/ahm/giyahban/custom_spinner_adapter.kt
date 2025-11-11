package com.ahm.giyahban
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView



class custom_spinner_adapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent, isDropdown = false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent, isDropdown = true)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup, isDropdown: Boolean): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_spinner_view, parent, false)

        val textView = view.findViewById<TextView>(R.id.textItem)
        val divider = view.findViewById<View>(R.id.divider)

        textView.text = items[position]

        // Hide the divider for the last item
        divider.visibility = if (position == items.lastIndex) View.GONE else View.VISIBLE

        return view
    }
}
