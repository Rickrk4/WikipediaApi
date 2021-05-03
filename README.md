# Wikipedia API
## Import
### Maven
Per importare con maven aggiungere questa dipendenza al file pom.xml.
```
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

### eseguire la query
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

### Gestire la risposta
WikipediaApiResponse è un oggetto che si pone l'obbiettivo di semplificare l'utilizzo e la consultazione della risponsta alla chiamata all'api di wikipedia.
Implementa il metodo getPage(), che ritorna un oggetto JSONObject contenente la pagina cercata oppure null se la richiesta non ha prodotto risultati.

## Rendere un progetto importabile da github
Di default maven non riesce ad importare un progetto da un repository github, quindi occorre utilizzare un plug-in.
Nel seguito si riporta sinteticamente il contenuto di questa [guida](https://dev.to/iamthecarisma/hosting-a-maven-repository-on-github-site-maven-plugin-9ch).

### Credenziali di accesso al repository remoto
Le credenziali di accesso vanno salvate nel file ~/.m2/settings.xml, da cui saranno poi disponibili a tutti i progetti.
```
<settings>
<servers>
<server>
<id>github</id>
<username>GitHubLogin</username>
<password>GitHubPassw0rd</password>
</server>
</servers>
</settings>
```

Se si utilizza l'autenticazione a due fattori per accederea github allora al posto della password dell'account va generato un token per poter accedere. Per sapere come fare seguire la guida messaa  disposzione direttamente da github:

### Aggiungere repository locale
Prima di tutto aggiungere queste righe al file pom.xml del progetto da rendere importabile per salvare il file .jar in un repository locale prefissato.
```
<distributionManagement>
<repository>
<id>internal.repo</id>
<name>Temporary Staging Repository</name>
<url>file://${project.build.directory}/mvn-repo</url>
</repository>
</distributionManagement>
```

Poi aggiugnere anche il plug-in di maven maven-deploy-plugin, che ad ogni deploy si occuperà in automatico di caricare il file jar in una branca apposita del repository remoto, da cui poi saròà posisbile importarlo utilizzando maven.
```
<plugin>
<artifactId>maven-deploy-plugin</artifactId>
<version>2.8.2</version>
<configuration>
<altDeploymentRepository>internal.repo::default::file://${project.build.directory}/mvn-repo</altDeploymentRepository>
</configuration>
</plugin>
```
Aggiungere poi anche queste righe:
```
<properties>
<github.global.server>github</github.global.server>
</properties>
```

ed infine configurare il plugin site-maven-plugin che si occupa di fare una push del repository locale nella branca mvn-repo del repository remoto, dalla quale è possibile importarlo con maven in altri progetti.

```
<plugin>
<groupId>com.github.github</groupId>
<artifactId>site-maven-plugin</artifactId>
<version>0.11</version>
<configuration>
<message>Maven artifacts for ${project.version}</message> <!-- git commit message -->
<noJekyll>true</noJekyll>  <!-- disable webpage processing -->
<outputDirectory>${project.build.directory}/mvn-repo</outputDirectory> <!-- matches distribution management repository url above -->
<branch>refs/heads/mvn-repo</branch> <!-- remote branch name -->
<includes><include>**/*</include></includes>
<repositoryName>YOUR-REPOSITORY-NAME</repositoryName> <!-- github repo name -->
<repositoryOwner>THE-REPOSITORY-OWNER</repositoryOwner> <!-- organization or user name  -->
</configuration>
<executions> <!-- run site-maven-plugin's 'site' target as part of the build's normal 'deploy' phase -->
<execution>
<goals>
<goal>site</goal>
</goals>
<phase>deploy</phase>
</execution>
</executions>
</plugin>
```

### Importare il progetto
Per importare il progetto da un altro progetto con maven basta settare il repository da cui prelevare il pachetto, aggiungindo queste righe al file pom.xml:
```
<repository>
<id>ARTIFACT-ID</id>
<url>https://raw.github.com/REPOSITORYOWNER/REPOSITORY-NAME/mvn-repo/</url>
</repository>
```

e poi importare il progetto nella versione scelta:
```
<dependency>
<groupId>YOUR.PROJECT.GROUPID</groupId>
<artifactId>ARTIFACT-ID</artifactId>
<version>VERSION</version>
</dependency>
```