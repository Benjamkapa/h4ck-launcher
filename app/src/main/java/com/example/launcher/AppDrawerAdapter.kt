package com.example.launcher

// In app/src/main/java/com/example/launcher/AppDrawerAdapter.kt

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppDrawerAdapter(private val appsList: List<AppInfo>) :
    RecyclerView.Adapter<AppDrawerAdapter.ViewHolder>() {

    // The color we will use to tint the icons
    private val tintColor = Color.argb(255, 0, 255, 0)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appNameTextView: TextView = view.findViewById(R.id.app_name_tv)
        val appIconImageView: ImageView = view.findViewById(R.id.app_icon_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appsList[position]
        holder.appNameTextView.text = appInfo.label

        // --- THIS IS THE TINTING LOGIC ---
        val icon = appInfo.icon.mutate() // Use mutate() to avoid affecting other drawables

        // SRC_IN mode applies the color only to the non-transparent parts of the icon
        icon.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.MULTIPLY)

        holder.appIconImageView.setImageDrawable(icon)
        // --- END OF TINTING LOGIC ---

        // Set an onClick listener to launch the app
        holder.itemView.setOnClickListener {
            val launchIntent = holder.itemView.context.packageManager
                .getLaunchIntentForPackage(appInfo.packageName.toString())
            holder.itemView.context.startActivity(launchIntent)
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }
}