package com.samsvoice.roadtorecovery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PointsMyAdapter (private val context: Context, private var dataList: List<PointsDataClass>) : RecyclerView.Adapter<MyViewHolderPoints>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderPoints {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.points_recyclerview, parent, false)
        return MyViewHolderPoints(view)
    }
    override fun onBindViewHolder(holder: MyViewHolderPoints, position: Int) {
        holder.recTitlePoints.text = dataList[position].entryName
        holder.recPointAmount.text = dataList[position].dataPoints.toString()

    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    //fun searchDataList(searchList: List<PointsDataClass>) {
     //   dataList = searchList
     //   notifyDataSetChanged()
    //}
}
class MyViewHolderPoints(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var recTitlePoints: TextView
    var recPointAmount: TextView

    init {
        recTitlePoints = itemView.findViewById(R.id.recTitlePoints)
        recPointAmount = itemView.findViewById<TextView?>(R.id.recPoints)
    }
}