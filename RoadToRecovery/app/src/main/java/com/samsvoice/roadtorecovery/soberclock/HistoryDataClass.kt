package com.samsvoice.roadtorecovery.soberclock

class HistoryDataClass {

    var startTime: String? = null
    var goalData: String? = null
    var endDate: String? = null
    var reason: String? = null

    constructor() {
        // Default no-argument constructor required by Firebase
    }
    constructor (startTime: String?, dueDate: String?,endDate:String?,reason:String?) {
        this.startTime = startTime
        this.goalData = dueDate
        this.endDate = endDate
        this.reason = reason

    }
}