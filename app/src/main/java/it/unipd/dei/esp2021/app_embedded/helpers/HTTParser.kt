package it.unipd.dei.esp2021.app_embedded.helpers

class HTTParser {

    companion object {
        fun parseTrainInfo(trainID: String, trainState : String, trainRoute: String): TrainInfo {
            val info = TrainInfo(trainID)
            info.delay = trainState.substringAfter("],\"ritardo\":").substringBefore(",")
            info.lastDetectionTime = trainState.substringAfter("oraUltimoRilevamento\":").substringBefore(",")
            info.lastDetectionStation = trainState.substringAfter("stazioneUltimoRilevamento\":\"").substringBefore("\",")
            info.stops  = mutableListOf()


            val trainStops = trainRoute.split(",{")
            trainStops.forEachIndexed { index, station ->
                val stationName = station.substringAfter("stazione\":\"").substringBefore("\",")
                val isCurrent = (station.indexOf("Corrente\":true,") != -1)

                val newStation = StationInfo(stationName, isCurrent)

                newStation.scheduledDeparture = station.substringAfter("partenza_teorica\":").substringBefore(",")
                newStation.scheduledArrival = station.substringAfter("arrivo_teorico\":").substringBefore(",")
                newStation.realDeparture = station.substringAfter("partenzaReale\":").substringBefore(",")
                newStation.realArrival = station.substringAfter("arrivoReale\":").substringBefore(",")
                if (index != trainStops.size-1) {
                    newStation.expectedDeparture = (newStation.scheduledDeparture.toLong() + info.delay.toLong()*60000).toString()
                }
                if (index != 0){
                    newStation.expectedArrival = (newStation.scheduledArrival.toLong() + info.delay.toLong()*60000).toString()
                }

                info.stops.add(newStation)
                if (isCurrent) {
                    info.currentIndex = index
                }
            }

            return info
        }

    }

    data class TrainInfo(val trainID: String) {
        var delay : String = ""
        var lastDetectionTime : String = ""
        var lastDetectionStation : String = ""
        var currentIndex : Int = -1
        lateinit var stops : MutableList<StationInfo>
    }

    class StationInfo(val stationName : String, current : Boolean) {
        var scheduledDeparture : String = ""
        var scheduledArrival : String = ""
        var realDeparture : String = ""
        var realArrival : String = ""
        var expectedDeparture : String = "null"
        var expectedArrival : String = "null"
    }
}