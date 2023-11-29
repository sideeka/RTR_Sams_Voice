package com.samsvoice.roadtorecovery

import android.content.Context
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.util.*

class TaskItem(
    var name: String,
    var desc: String,
    var completedDate: String,
    var entryDate:String = LocalDate.now().toString(),//
    var id: String = UUID.randomUUID().toString()
)

{
    // No-argument constructor required by Firebase
    constructor() : this("", "", "","", "")//

    fun isCompleted() = completedDate != ""
    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imageColor(context: Context): Int = if(isCompleted()) purple(context) else black(context)

    private fun purple(context: Context) = ContextCompat.getColor(context, R.color.purple_500)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)
}