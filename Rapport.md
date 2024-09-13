API integration v.4

De apier jag har använt är OpenWeathers Geolocating API, 5 Day Forecast och current weather data.

Geolocating apiet har 3 olika parametrar på data som man kan använda sig av vilket är stad, api-key och limit.
Jag lägger till staden som ett argument som ändras beroende på vad användaren skriver och sätter limiten till 1. Antar det finns vissa nischade städer där man hade kunnat höja det till 2 kanske 3 men har inte stött på det i min testning.

Current weather data/ 5 day forecast har samma parametrar vilket är lon,lat,appid,units och lang. Jag tar longituden och latituden som jag fått från geocodingen och sätter in i detta anropet och anpassar unitsen till användarens preferenser samt sätter min api-key och språket till engelska.
Alternativet hade jag kunnat göra så att användaren kan ändra språket också men eftersom det finns regex i programmet som kollar efter engelska ord och titlar och liknande är på engelska tänkte jag att det var lättast att hålla allt på engelska.

Current weather data gör jag inget unikt med, jag castar det in i ett objekt där jag sedan displayar datan i olika text views på sidan och.

5 day forecast följer samma koncept som ovan men den större skillnaden är väl att antalet views som görs sker dynamiskt med recycler view.
