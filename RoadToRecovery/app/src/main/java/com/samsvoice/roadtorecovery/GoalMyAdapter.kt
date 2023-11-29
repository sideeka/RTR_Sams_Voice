package com.samsvoice.roadtorecovery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class GoalMyAdapter
    (private val context: Context, private var dataList: List<GoalDataClass>) : RecyclerView.Adapter<Goal_MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Goal_MyViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.goal_recyclerview, parent, false)
            return Goal_MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: Goal_MyViewHolder, position: Int) {

            holder.recTitleGoal.text = dataList[position].dataGoalTitle
            holder.recGoalDesc.text = dataList[position].dataGoalDesc
            holder.recGoalStartDate.text = dataList[position].dataGoalStartDate
            holder.recGoalStartTime.text = dataList[position].dataGoalStartTime
            holder.recGoalEndDate.text = dataList[position].dataGoalEndDate
            holder.recGoalEndTime.text = dataList[position].dataGoalEndTime
            holder.recGoalDesc.text = dataList[position].dataGoalDesc

            holder.recGoalCard.setOnClickListener {
                val intent = Intent(context, GoalEntryDetails::class.java)
                intent.putExtra("Title", dataList[holder.adapterPosition].dataGoalTitle)
                intent.putExtra("Description", dataList[holder.adapterPosition].dataGoalDesc)
                intent.putExtra("Start Date", dataList[holder.adapterPosition].dataGoalStartDate)
                intent.putExtra("Start Time", dataList[holder.adapterPosition].dataGoalStartTime)

                intent.putExtra("End Date", dataList[holder.adapterPosition].dataGoalEndDate)
                intent.putExtra("End Time", dataList[holder.adapterPosition].dataGoalEndTime)
                context.startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
        fun searchDataList(searchList: List<GoalDataClass>) {
            dataList = searchList
            notifyDataSetChanged()
        }
    }// end class


    class Goal_MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recTitleGoal: TextView
        var recGoalStartDate: TextView
        var recGoalStartTime: TextView
        var recGoalEndDate: TextView
        var recGoalEndTime: TextView
        var recGoalDesc: TextView
        var recGoalCard: CardView

        init {

            recTitleGoal = itemView.findViewById(R.id.recTitleGoal)
            recGoalDesc = itemView.findViewById(R.id.recDescGoal)
            recGoalStartDate= itemView.findViewById(R.id.recStartDate)
            recGoalStartTime= itemView.findViewById(R.id.recStartTime)
            recGoalEndDate= itemView.findViewById(R.id.recEndDateGoal)
            recGoalEndTime= itemView.findViewById(R.id.recEndTimeGoal)
            recGoalCard = itemView.findViewById(R.id.recGoalCard)
        }



    }// end classGoalViewHolder
