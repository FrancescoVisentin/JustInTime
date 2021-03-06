package it.unipd.dei.esp2022.app_embedded.helpers

class HTTParser {

    companion object {
        //Parsing delle informazioni per il fragment "stato treno"
        //Viene restituito un oggetto di tipo TrainInfo contenente le info associate allo stato del treno, compresa un lista contenente le info per ogni fermata
        fun parseTrainInfo(trainID: String, trainState : String, trainRoute: String): TrainInfo {
            val info = TrainInfo(trainID)
            info.delay = trainState.substringAfter("],\"ritardo\":").substringBefore(",")
            info.lastDetectionTime = trainState.substringAfter("oraUltimoRilevamento\":").substringBefore(",")
            info.lastDetectionStation = trainState.substringAfter("stazioneUltimoRilevamento\":\"").substringBefore("\",")
            info.stops  = mutableListOf()

            //Parsing informazioni relative a orari di partenza/arrivo programmati ed effettivi per ogni stazione
            val trainStops = trainRoute.split(",{")
            trainStops.forEachIndexed { index, station ->
                val stationName = station.substringAfter("stazione\":\"").substringBefore("\",")
                val isCurrent = (station.indexOf("Corrente\":true,") != -1)

                val newStation = StationInfo(stationName, isCurrent)

                newStation.scheduledDeparture = station.substringAfter("partenza_teorica\":").substringBefore(",")
                newStation.scheduledArrival = station.substringAfter("arrivo_teorico\":").substringBefore(",")
                newStation.realDeparture = station.substringAfter("partenzaReale\":").substringBefore(",")
                newStation.realArrival = station.substringAfter("arrivoReale\":").substringBefore(",")
                if (index != trainStops.size-1 && newStation.scheduledDeparture.compareTo("null") != 0) {
                    newStation.expectedDeparture = (newStation.scheduledDeparture.toLong() + info.delay.toLong()*60000).toString()
                }
                if (index != 0 && newStation.scheduledArrival.compareTo("null") != 0){
                    newStation.expectedArrival = (newStation.scheduledArrival.toLong() + info.delay.toLong()*60000).toString()
                }

                info.stops.add(newStation)
                if (isCurrent) {
                    info.currentIndex = index
                }
            }

            return info
        }

        //Parsing delle informazioni per il fragment "Ricerca viaggio"
        //Le varie soluzioni di viaggio vengono inserite in una lista contenente oggetti di tipo SolutionInfo
        fun parseSolutionsInfo(solutions: String): MutableList<SolutionInfo> {
            val tripsList = mutableListOf<SolutionInfo>()
            var trip : SolutionInfo
            val tmp = solutions.split("{\"durata\":\"")
            var tmp2 : List<String>
            for(i in 1..tmp.lastIndex)
            {
                trip = SolutionInfo(-1)
                trip.category = mutableListOf()
                trip.trainNumber = mutableListOf()
                trip.duration = tmp[i].substringBefore("\"")
                trip.departureTime = tmp[i].substringAfter("orarioPartenza\":\"").substringBefore("\"")
                tmp2 = tmp[i].split("},{")
                trip.arrivalTime = tmp2.last().substringAfter("orarioArrivo\":\"").substringBefore("\"")
                for(j in 0..tmp2.lastIndex) {
                    (trip.category).add(tmp2[j].substringAfter("categoriaDescrizione\":\"").substringBefore("\""))
                    (trip.trainNumber).add(tmp2[j].substringAfter("numeroTreno\":\"").substringBefore("\""))
                }
                trip.changes = (tmp2.size)-1
                tripsList.add(trip)
            }
            return tripsList
        }

        //Parsing delle informazioni per il fragment "Tabellone"
        //Viene restituita un'arrayList contenente esattamente due liste
        //Nella posizione 0 dell'arrayList viene inserita una lista contenente i treni in partenza, nella poszione 1 viene inserita una lista contenente i treni in arrivo
        fun parseStationsInfo(departingTrains : String, incomingTrains : String): ArrayList<MutableList<TrainStationInfo>> {
            //Informazioni che riguardano i treni in partenza dalla stazione
            val departuresList = mutableListOf<TrainStationInfo>()
            var departureTrain : TrainStationInfo
            val tmp = departingTrains.split("\"numeroTreno\":")
            for(i in 1..tmp.lastIndex)
            {
                departureTrain = TrainStationInfo("")
                departureTrain.trainNumber = tmp[i].substringBefore(",")
                departureTrain.category = tmp[i].substringAfter("categoriaDescrizione\":\"").substringBefore("\"")
                departureTrain.departureArrivalTime = tmp[i].substringAfter("compOrarioPartenza\":\"").substringBefore("\"")
                departureTrain.destinationOrigin = tmp[i].substringAfter("destinazione\":\"").substringBefore("\"")
                departureTrain.delay = tmp[i].substringAfter("compRitardo\":[\"").substringBefore("\"")
                departureTrain.track = tmp[i].substringAfter("binarioEffettivoPartenzaDescrizione\":").substringBefore(",")
                departuresList.add(departureTrain)
            }
            //Informazioni che riguardano i treni in arrivo alla stazione
            val arrivalsList = mutableListOf<TrainStationInfo>()
            var arrivalTrain : TrainStationInfo
            val tmp2 = incomingTrains.split("\"numeroTreno\":")
            for(i in 1..tmp2.lastIndex)
            {
                arrivalTrain = TrainStationInfo("")
                arrivalTrain.trainNumber = tmp2[i].substringBefore(",")
                arrivalTrain.category = tmp2[i].substringAfter("categoriaDescrizione\":\"").substringBefore("\"")
                arrivalTrain.departureArrivalTime = tmp2[i].substringAfter("compOrarioArrivo\":\"").substringBefore("\"")
                arrivalTrain.destinationOrigin = tmp2[i].substringAfter("origine\":\"").substringBefore("\"")
                arrivalTrain.delay = tmp2[i].substringAfter("compRitardo\":[\"").substringBefore("\"")
                arrivalTrain.track = tmp2[i].substringAfter("binarioEffettivoArrivoDescrizione\":").substringBefore(",")
                arrivalsList.add(arrivalTrain)
            }
            val trainArray = ArrayList<MutableList<TrainStationInfo>>(2)
            trainArray.add(0, departuresList)
            trainArray.add(1, arrivalsList)
            return trainArray
        }

    }

    data class TrainInfo(val trainID: String) {
        var delay : String = ""
        var lastDetectionTime : String = ""
        var lastDetectionStation : String = ""
        var currentIndex : Int = -1
        lateinit var stops : MutableList<StationInfo>
    }

    data class StationInfo(val stationName : String, val current : Boolean) {
        var scheduledDeparture : String = ""
        var scheduledArrival : String = ""
        var realDeparture : String = ""
        var realArrival : String = ""
        var expectedDeparture : String = "null"
        var expectedArrival : String = "null"
    }

    data class SolutionInfo(var changes: Int)
    {
        var duration : String = ""
        var departureTime : String = ""
        var arrivalTime : String = ""
        lateinit var category : MutableList<String>
        lateinit var trainNumber : MutableList<String>
    }

    data class TrainStationInfo(var category: String)
    {
        var departureArrivalTime : String = ""
        var destinationOrigin : String = ""
        var delay : String = ""
        var track : String = ""
        var trainNumber : String = ""
    }
}