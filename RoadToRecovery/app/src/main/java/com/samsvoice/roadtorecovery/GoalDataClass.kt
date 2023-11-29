package com.samsvoice.roadtorecovery

class GoalDataClass {

        var dataGoalTitle: String? = null
        var dataGoalStartDate: String? = null
        var dataGoalStartTime: String? = null
        var dataGoalEndDate: String? = null
        var dataGoalEndTime: String? = null
        var dataGoalDesc: String? = null

         var dataGoalPointsAdded: Boolean? = false
    var entryName:String? = null

        // var dataImage: String? = null

        constructor(  dataGoalTitle: String? , dataGoalStartDate: String?, dataGoalStartTime: String?,
                      dataGoalEndDate: String?, dataGoalEndTime: String?, dataGoalDesc: String?,
                      entryName:String? ){
            this.dataGoalTitle = dataGoalTitle
            this.dataGoalStartDate = dataGoalStartDate
            this.dataGoalStartTime = dataGoalStartTime
            this.dataGoalEndDate = dataGoalEndDate
            this.dataGoalEndTime = dataGoalEndTime
            this.dataGoalDesc = dataGoalDesc

            this.entryName = entryName
        }
        constructor()
}// end class