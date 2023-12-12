package com.me.pleme.navigation.model

data class AlarmDTO (
    var destinationUid : String? = null,
    var userId : String? = null,
    var uid : String? = null,
    //0 : like alarm
    //1 : comment alarm
    //2 : follow alarm
    //3 : poke alarm
    var kind : Int? = null,
    var message : String? = null,
    var timestamp : Long? = null,
    var poke : Int? = null
)