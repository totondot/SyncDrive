package com.example.syncdrive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignFeedAdapter(private val signList: MutableList<DetectedSign>) :
    RecyclerView.Adapter<SignFeedAdapter.SignViewHolder>() {

    class SignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSignName: TextView = view.findViewById(R.id.tvSignName)
        val tvSignTime: TextView = view.findViewById(R.id.tvSignTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detected_sign, parent, false)
        return SignViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignViewHolder, position: Int) {
        val sign = signList[position]
        holder.tvSignName.text = sign.signName

        // Format the timestamp into a readable time (e.g., "14:32:05")
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        holder.tvSignTime.text = sdf.format(Date(sign.timestampMs))
    }

    override fun getItemCount(): Int = signList.size

    // Controller method to add new signs to the feed dynamically
    fun addSignToFeed(newSign: DetectedSign) {
        signList.add(0, newSign) // Add to the front of the list
        notifyItemInserted(0) // Tell the UI to animate the new item in
    }
}