package com.samsvoice.roadtorecovery.soberclock

class Timer {

    var startTime: String? = null
    var dueDate: String? = null

    constructor() {
        // Default no-argument constructor required by Firebase
    }
    constructor (startTime: String?, dueDate: String?) {
        this.startTime = startTime
        this.dueDate = dueDate

    }
}