# JustInTime

### Material Design 3
L'applicazione è stata sviluppata per evidenziare le novità introdotte dall'aggiornamento a [Material Design 3](https://m3.material.io/),
concentrandosi principalmente sulle features legate ai colori dinamici che si adattano alle scelte cromatiche dell'utente.

L'utente può scegliere se impostare il tema di default, creato attraverso il [builder](https://material-foundation.github.io/material-theme-builder/#/custom) di Material 3 o attivare i colori dinamici per un'esperienza più immersiva.

L'interfaccia grafica è stata progettata utilizzando il design kit di Material 3 per [Figma](https://www.figma.com/).

### Descrizione del progetto
JustInTime offre diverse funzionalità per la ricerca, monitoraggio e gestione dei treni della rete ferroviaria italiana. <br> Tra le varie funzionalità è possibile creare dei planner settimanali personalizzati in modo da poter salvare, per ogni giorno, i viaggi preferiti.



I dati relativi ai treni, stazioni e soluzioni di viaggio vengono reperiti dal sito [ViaggiaTreno](http://www.viaggiatreno.it/).
















### Note sul progetto
+ L'app è stata sviluppata e testata utilizzando l'emulatore Pixel 5 API 32
+ Non sono state utilizzate librerie esterne
+ Warning/Errori sono stati ispezionati tramite LINT:
  + Nei file XML vengono segnalati Warning relativi all'uso di risorse private per rappresentare i colori dinamici, ma nei test effettuati non sono stati riscontrati problemi e gli attributi vengono caricati correttamente.
  + Altri warning significativi sono stati gestiti e commentati nel codice.

### Autori
Il progetto è stato sviluppato da:
- [Christian Marchiori](https://github.com/christianmarch)
- [Francesco Visentin](https://github.com/FrancescoVisentin)
- [Massimiliano Comin](https://github.com/Massimiliano-Comin)
