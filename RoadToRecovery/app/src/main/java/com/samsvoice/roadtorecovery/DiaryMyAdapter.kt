package com.samsvoice.roadtorecovery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class DiaryMyAdapter(private val context:Context,private var dataList:List<DiaryDataClass>):RecyclerView.Adapter<MyViewHolder>(){
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):MyViewHolder{
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.diary_recyclerview,parent,false)

        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder:MyViewHolder,position:Int){

        holder.recDate.text=dataList[position].dataDate
        holder.recDesc.text=dataList[position].dataDesc
      holder.recCard.setOnClickListener{
            val intent=Intent(context,DiaryEntryDetails::class.java)
            intent.putExtra("DiaryEntry",dataList[holder.adapterPosition].dataDesc)
            intent.putExtra("Date",dataList[holder.adapterPosition].dataDate)
            context.startActivity(intent)
        }
    }
    override fun getItemCount():Int{
        return dataList.size
    }
    fun searchDataList(searchList:List<DiaryDataClass>){
        dataList=searchList
        notifyDataSetChanged()
    }
}
class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    var recDate:TextView
    var recDesc:TextView
    var recCard:CardView
    init {
        recDate = itemView.findViewById(R.id.recTitle)
        recDesc = itemView.findViewById(R.id.recDesc)
        recCard = itemView.findViewById(R.id.recCard)
    }
}// end class