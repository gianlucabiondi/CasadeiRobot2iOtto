# Robot2iOtto
## Descrizione progetto
Interfaccia robot verso iOtto.
Vengono letti alcuni parametri direttamente dal robot via socket e spediti ad iOtto via web services REST.

## Parametri
Il programma è abbastanza parametrizzato nelle sue principali caratteristiche. I parametri di configurazione sono contenuti nel file passato come primo argomento nella riga di comando di esecuzione.
I parametri possono essere modificati anche metre il programma è in esecuzione: al successivo ciclo di lettura dei parametri del robot, verranno applicate le modifiche.
Questi sono i parametri utilizzabili:

* ROBOT_IP: contiene l'IP del robot
* ROBOT_PORT: indica la porta di ascolto del robot
* SOCKET_TIMEOUT_MS: timeout massimo per le operazioni via socket - connessione, richiesta parametri e lettura parametri
* QUERY_FREQUENCY_MS: frequenza di lettura dei parametri dal robot
* IOTTO_URL: URL destinazione di iOtto da invocare per l'invio dei parametri del robot (web service REST)
* IOTTO_USR: utente del web service di iOtto
* IOTTO_PWD: password del web service di iOtto
* ROBOT_VARIABLES: elenco di variabili da richiedere al robot. Non devono contenere spazi. Devono essere separate da ;. Eventualmente la stringa può essere iniziata da CMD_READ;

## Esecuzione - Riga di comando
Per eseguire il programma, utilizzare questa sintassi in una finestra di comandi:

java Robot2iOtto <propertyFile.txt>

## Sorgenti
I sorgenti sono contenuti in gitHub:
https://github.com/gianlucabiondi/CasadeiRobot2iOtto

