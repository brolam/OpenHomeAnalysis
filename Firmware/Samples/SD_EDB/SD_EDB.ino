#include <SD.h>
#define SD_PIN 10  // SD Card CS pin

char* db_name = "edb_02.txt";
File dbFile;
int readedPosition  = 0;
int countRecs = 0;

void setup() {
  // put your setup code here, to run once:
  pinMode(SD_PIN, OUTPUT);
  digitalWrite(SD_PIN, LOW);

  Serial.begin(9600);
  Serial.println(" Extended Database Library + External SD CARD storage demo");
  Serial.println();
  if (!SD.begin(SD_PIN)) {
    Serial.println("No SD-card.");
    return;
  }
}

void selectNext() {
  int count = 0;
  //char buffer[40];
  dbFile = SD.open(db_name, FILE_READ);
  dbFile.seek(readedPosition);
  String line = dbFile.readStringUntil(';');
  while ( line.length() > 1 ) {
    Serial.print(readedPosition); Serial.print(" / ") ; Serial.println(line);
    readedPosition = dbFile.position();
    count++;
    if (count > 50) {
      break;
    }
    line = dbFile.readStringUntil(';');
  }
  // close the file:
  dbFile.close();
}

void deleteAll() {
  countRecs = 0;
  SD.remove(db_name);
}

void loop() {
  dbFile = SD.open(db_name, FILE_WRITE);
  digitalWrite(SD_PIN, HIGH);
  countRecs++;
  dbFile.print(countRecs);
  dbFile.print("<100412|1020|1021|1022|1020|252001>;");
  delay(100);
  digitalWrite(SD_PIN, LOW);
  Serial.print("Saved Rec: "); Serial.print(countRecs); Serial.print(" File Size: "); Serial.println(dbFile.size());
  dbFile.close();
  if (Serial.available()) {
    String command = Serial.readString();
    if ( command.indexOf("list") > -1 ) {
      selectNext();
    } else if ( command.indexOf("delete") > -1 ) {
      deleteAll();
    }
  }
}
