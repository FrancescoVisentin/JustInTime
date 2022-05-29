package it.unipd.dei.esp2022.app_embedded

var trainList = mutableListOf<Train>()
var trainList2 = mutableListOf<Train>()

class Train (
    var numero: String,
    var orario: String,
    var luogo: String,
    var binario: String,
    var categoria: String,
    var ritardo: String?,
    var tipo:Int,
)