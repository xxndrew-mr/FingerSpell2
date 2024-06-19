package com.example.fingerspell.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fingerspell.R
import com.example.fingerspell.data.Finger

class ReviewAdapter(private val listFinger: ArrayList<Finger>) : RecyclerView.Adapter<ReviewAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.text1)
        val tvPhoto: ImageView = itemView.findViewById(R.id.image1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_finger, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listFinger.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, photo) = listFinger[position]
        holder.tvName.text = name
        holder.tvPhoto.setImageResource(photo)
    }
}
