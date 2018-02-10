# PDF-Autoprinter

## Was ist das?
Dieses kleine Tool hilft dabei PDF-Dateien, die in einem konfigurierbaren Ordner auftauchen, automatisch an einen Drucker zu schicken und das PDF danach automatisch an einen ebenfalls konfigurierbaren Ort zu verschieben.


## Wozu soll das gut sein?

Naja, dass darf jeder selbst für sich entscheiden. Mein Problem war das ich von der Fritzbox empfangene Faxe ausdrucken, als Mail versenden und gleichzeitig noch eine Zeit lang archivieren wollte. Nun ging das jedoch dummerweise nicht mit Fritzbox Boardmitteln. 
Daher konfigurierte ich die Fritzbox so, dass diese eingehende Faxe per Mail versendete und danach das Fax auf einem Speicher (USB-Stick an der FB) ablegte. Dieser Speicher wurde im Netzwerk als Samba-Share exportiert.
Mein kleines Tool hier überprüft dann das Samba-Share und nimmt alle PDF's die dort drin liegen, druckt diese aus, und verschiebt sie dann an einen gewünschten Ort.

## Was brauche ich dafür

- Nicht besonderes. Ein installiertes Java (Version >= 1.8) sollte ausreichend sein.
- Wenn man das ganze unter Windows als Service nutzen möchte, dann empfehle ich noch die Installation von JSL [(Java Service Launcher)](http://jslwin.sourceforge.net/current.html)  damit das ganze funktioniert. Eine dafür benötigte jsl.ini Datei liegt der Distribution (siehe Releases hier auf GitHub) bei. Passt einfach die Pfade an und installiert den Service unter Windows wie [hier](http://jslwin.sourceforge.net/howto.html) beschrieben. 

## Aufruf
Es handelt sich um ein reines Java Tool. Damit sollte sich der Aufruf auf folgenden Befehl beschränken.

```
java -jar faxprint-1.0-0.jar /Pfad/zur/Konfigurationsdatei
```
*Bitte beachtet das es keine graphische Anwendung ist, sondern ausschließlich auf der Konsole operiert!* 

## Konfiguration
Die Konfiguration der Software wird über eine Properties Datei getätigt.
Eine Beispiel Datei liegt der Distribution (siehe auch *src/main/resources*) bei. 

| Property-Name | Beschreibung  | Beispiel  |
|--|--|--|
|de.feuerwehr.kremmen.fax.monitor.folder.path | Der Pfad zum Ordner der auf eingehende PDF Dateien überwacht werden soll  | /tmp/eingehendes_fax |
| de.feuerwehr.kremmen.fax.monitor.folder.is.smb |Legt fest ob es sich beim zu überwachenden Ordner um einen Samba Share handelt | true oder false |
| de.feuerwehr.kremmen.smb.user.name | Der Nutzername der verwendet werden soll wenn auf das Samba-Share zugeriffen werden muss. Muss nur angegeben werden wenn es auch wirklich ein Samba Share ist | someusername |
| de.feuerwehr.kremmen.smb.user.name | Das zum User gehörende Passwort | geheim |
| de.feuerwehr.kremmen.fax.polltime | Die Zeit zwischen den Checks des Eingangsverzeichnisses (in Sekunden) | 30 |
| de.feuerwehr.kremmen.fax.archive.folder.path | Der Pfad zum Ordner in denen die Faxe nach dem Druck abgelegt werden sollen (sie werden im Eingangsverzeichnis gelöscht!!!) Hier ist noch kein Samba Share möglich! | /tmp/archive |
| de.feuerwehr.kremmen.printer.name | Der Name des Druckers (so wie er in der Druckerliste im Betriebssystem benannt ist. Eventuell sind Leerzeichen durch "-" zu ersetzen) | LexmarkX543 |

## Offene Punkte

 - Aktuell können wirklich nur PDF Dateien gedruckt werden.
	 - Andere Dateien im Eingangsverzeichnis werden ignoriert und auch nicht entfernt!
 - Archive Ordner auch auf ein Samba Share möglich machen
	 - Der Archive Ordner muss aktuell ein nicht gemountetes Verzeichnis sein. Ich hab das einfach nicht gebraucht.  

