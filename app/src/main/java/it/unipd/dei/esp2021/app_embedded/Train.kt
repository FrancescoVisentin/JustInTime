package it.unipd.dei.esp2021.app_embedded

var trainList = mutableListOf<Train>()
var trainList2 = mutableListOf<Train>()

class Train (
    var number: String,
    var orario: String,
    var state: String,
    val id:Int? = trainList.size
)