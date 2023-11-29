package com.samsvoice.roadtorecovery

class DiaryDataClass {

    var dataDate:String?=null
    var dataDesc:String?=null
    var dataEntryName : String? = null
    //var dataamount:Int? = 0

    constructor(dataDate:String?,dataDesc:String?,dataEntryDate : String? ){
        this.dataDate=dataDate
        this.dataDesc=dataDesc
        this.dataEntryName=dataEntryDate
       // this.dataamount = dataamount  //,dataamount:Int?
    }
    constructor()

}// end class


