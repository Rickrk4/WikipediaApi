# Wikipedia API
## Import
### Maven
Per importare con maven aggiungere il repository e la dipendenza al file pom.xml.
```
<repositories>
    <repository>
        <id>WikipediaApi</id>
        <url>https://github.com/Rickrk4/WikipediaApi/blob/mvn-repo/</url>
    </repository>
</repositories>

<dependency>
    <groupId>org.example</groupId>
    <artifactId>WikipediaApi</artifactId>
    <version>0.1.0</version>
</dependency>
```
In alternativa è possibile importare direttamente il file jar a questo link:
`https://github.com/Rickrk4/WikipediaApi/blob/mvn-repo/org/example/WikipediaApi/0.1.0/WikipediaApi-0.1.0.jar`.

## Usage
Le componenti principali della classe sono `WikipediaApi`, `WikipediaApiQuery` e `WikipediaApiResponse`.

### Realizzare una query
`WikipediaApiQuery` si occupa di formare correttamente la query con uci interrogare wikipedia.
Ci sono diversi parametri addizionali da poter specificare per ottenere dati aggiuntivi; WikipediaApiQuery offre dei metodi semplificati per gestire questi parametri.
#### Richiedere l'url della pagina trovata.
Per richiedere l'url della pagina trovata basta usare la funzione withUrl:
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.withUrl()
```

#### Richiedere l'abstract della pagina trovata
Per richiedere la prima porzione di testo della pagina trovata basta usare la funzione withAbstract:
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.withAbstract()
```

#### Abilitare reindirizzamento
Alcune pagine di wikipedia reindirizzano automaticamente ad altr pagine, senza aggiungere uno specifico flag nella query questo non avviene correttamente e si potrebbero verificare dei problemi.
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.allowRedirect()
```

### Cercare una pagina
Per cercare una pagina di wikipedia è sufficiente passare come argomento di searchByTitle il nome della pagina da cercare. La stringa cercata deve corrispondere esattamente al titolo della pagina, altrimenti non verrà trovato nulla.
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.searchByTitle("Nelson Mandela")
```
I metodi visti possono essere utilizzati in cascata.
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.allowRedirect().withUtl().withAbstract().searchByTitle("Nelson Mandela")
```

Inoltre sono matody lazy, quindi finchè non richiederemo esplicitamente la query generata con il metodo get() questi non sortiranno alcun effetto.
```
WikipediaApiQuery query = new WikipediaApiQuery()
String str = query.allowRedirect().withUtl().withAbstract().searchByTitle("Nelson Mandela").get()
```

L'oggetto WikipediaApiQuery non è riutilizzabile: per ogni query svolta va richreato un nuovo oggetto WIkipediaApiQuery.

### Eseguire la query
DI eseguire la query si occupa l'oggetto WikipediaApi. Questo permette di fare chiamate sincrone o asincrone, in entrambi i casi richiede come input solo un oggetto WikipediaApiQuery contenente la query da effettuare.

WikipediaApi.makeQuery ritorna un oggetto WIkipediaApiResponse
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.allowRedirect().withUtl().withAbstract().searchByTitle("Nelson Mandela").get()

WikipediaApi api = new WikipediaApi()
WikipediaApiResponse response = api.makeQuery(query)
```

WikipediaApi.makeQueryAsync ritorna un oggetto CompletableFuture di tipo WikipediAPiQueryResponse.
```
WikipediaApiQuery query = new WikipediaApiQuery()
query.allowRedirect().withUtl().withAbstract().searchByTitle("Nelson Mandela").get()

WikipediaApi api = new WikipediaApi()
CompletableFuture<WikiediaApiResponse> future = api.makeQueryAsync(query)

WikipediaApiResponse response = future.get()
```

### Risposta
WikipediaApi ritorna come risposta un oggetto WikipediaApiResponse, che incapsula la l' HttpResponse.
è possibile ottenere la prima pagina trovata con il metodo getPage(). Se nessuna pagina è stata trovata il metodo ritorna null.
```
WikipediaApiResponse response = api.makeQuery(query);
JSONObject page = response.getPage();
```