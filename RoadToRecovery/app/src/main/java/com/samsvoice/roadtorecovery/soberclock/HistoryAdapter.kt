package com.samsvoice.roadtorecovery.soberclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.samsvoice.roadtorecovery.R

//import com.roadtorecovery.navdrawer.R

class HistoryAdapter(private var dataList: List<HistoryDataClass>):
    RecyclerView.Adapter<MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):MyViewHolder{
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.history_recyclerview,parent,false)

        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder:MyViewHolder,position:Int){

        holder.startDate.text="Start Date: "+dataList[position].startTime
        holder.goalDate.text="Goal Date: "+dataList[position].goalData
        holder.endDateHistory.text="End Date: "+dataList[position].endDate
        holder.reasonHistory.text = "Reason: "+dataList[position].reason

    }
    override fun getItemCount():Int{
        return dataList.size
    }

}
class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    var startDate: TextView
    var goalDate: TextView
    var endDateHistory: TextView
    var reasonHistory: TextView

    var recHistory: CardView
    init {
        startDate = itemView.findViewById(R.id.startDate)
        goalDate = itemView.findViewById(R.id.goalDate)
        endDateHistory = itemView.findViewById(R.id.endDateHistory)
        reasonHistory = itemView.findViewById(R.id.ReasonHistory)

        recHistory = itemView.findViewById(R.id.recHistory)
    }
}// end class