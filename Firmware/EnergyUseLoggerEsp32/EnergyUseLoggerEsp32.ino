#include "Config.h"
#include <WiFi.h>
#include "NTPClient.h"
#include <WiFiUdp.h>
#include <HTTPClient.h>
#include "SD.h"
#include <SPI.h>

#define DEBUG 1

#define INTERVAL_TIME_SAVE_LOG 10000
#define LINE_END '\n'
#define SD_CS 5
#define pinsAmount 3
const int pins[] = {34, 35 , 32};
const char* ssid     = WIFI_SSID;
const char* password = WIFI_PASSWORD;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);
HTTPClient http;
long lastLogRegistered = 0;

void setup() {
  Serial.begin(115200);
  analogSetAttenuation(ADC_2_5db);
  for (int p = 0; p < pinsAmount ; p++) {
    pinMode(pins[p], INPUT);
  }

  // Initialize SD card
  SD.begin(SD_CS);
  if (!SD.begin(SD_CS)) {
    Serial.println("Card Mount Failed");
    espReset();
    return;
  }

  uint8_t cardType = SD.cardType();
  if (cardType == CARD_NONE) {
    Serial.println("No SD card attached");
    espReset();
    return;
  }

  Serial.println("Initializing SD card...");
  if (!SD.begin(SD_CS)) {
    Serial.println("ERROR - SD card initialization failed!");
    espReset();
    return;    // init failed
  }

  // Connect to Wi-Fi network with SSID and password
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected.");
  timeClient.begin();
  timeClient.forceUpdate();
  lastLogRegistered = millis();
}

void loop() {
  sendLogs();
  registerLog();
}

void registerLog() {
  if ( isTimetoRegisterLog(0) ) {
    return;
  }
  int pinsValues[pinsAmount];
  int pinsCount[pinsAmount];
  for (int p = 0; p < pinsAmount; p++) {
    pinsValues[p] = 0;
    pinsCount[p] = 0;
  }

  for (int t = 0; t < 20000; t++) {
    for (int p = 0; p < pinsAmount; p++) {
      int readValue = analogRead(pins[p]);
      if ( readValue > 0 ) {
        pinsValues[p] += readValue;
        pinsCount[p] ++;
      }
    }
  }

  String logFileName = "/" + timeClient.getFormattedTime() + ".txt";
  String logContent = String(timeClient.getEpochTime()) + "000|" +
                      String((millis() - lastLogRegistered) / 1000.00) + "|" +
                      String( ( pinsCount[0] == 0 ) ? 0 : pinsValues[0] / pinsCount[0]) + "|" +
                      String( ( pinsCount[1] == 0 ) ? 0 : pinsValues[1] / pinsCount[1]) + "|" +
                      String( ( pinsCount[2] == 0 ) ? 0 : pinsValues[2] / pinsCount[2]);
  File logFile = SD.open(logFileName, FILE_APPEND);
  logFile.print(logContent);
  logFile.print(LINE_END);
  logFile.close();
  lastLogRegistered = millis();
#ifdef DEBUG
  Serial.print(logFileName); Serial.print(" -> "); Serial.println(logContent);
#endif
}

void sendLogs() {
  String logFileName = "/" + timeClient.getFormattedTime() + ".txt";
  File logFile = SD.open(logFileName, FILE_READ);
  if ( logFile.seek(0)  ) {
    for (int i = 0; i < 100; i++) {
      if ( isTimetoRegisterLog(1000) ) {
        break;
      }
      String nextLogContent = logFile.readStringUntil(LINE_END);
      if ( nextLogContent.length() > 0  ) {
        Serial.print(i);
        Serial.print(" -> ");
        Serial.print(String(logFile.position()));
        Serial.print("|");
        Serial.println(nextLogContent);
        delay(1000);
      } else {
        break;
      }
    }
    logFile.close();

  }
}

bool isTimetoRegisterLog(int anyLess){
  return  (( millis() - lastLogRegistered ) > int(INTERVAL_TIME_SAVE_LOG) - anyLess ); 
}

void espReset() {
  Serial.println("Restarting in 5 seconds");
  delay(5000);
  ESP.restart();
}

/*
  #include "Config.h"
  #include <WiFi.h>
  #include "NTPClient.h"
  #include <WiFiUdp.h>
  #include <HTTPClient.h>

  //SD Card
  // Libraries for SD card
  #include "FS.h"
  #include "SD.h"
  #include <SPI.h>

  // Replace with your network credentials
  const char* ssid     = WIFI_SSID;
  const char* password = WIFI_PASSWORD;
  long mockLastSequence = 0;

  // Define CS pin for the SD card module
  #define SD_CS 5

  // Define NTP Client to get time
  WiFiUDP ntpUDP;
  NTPClient timeClient(ntpUDP);
  HTTPClient http;

  void setup() {

  // Start serial communication for debugging purposes
  Serial.begin(115200);

  // Initialize SD card
  SD.begin(SD_CS);
  if(!SD.begin(SD_CS)) {
    Serial.println("Card Mount Failed");
    return;
  }
  uint8_t cardType = SD.cardType();
  if(cardType == CARD_NONE) {
    Serial.println("No SD card attached");
    return;
  }
  Serial.println("Initializing SD card...");
  if (!SD.begin(SD_CS)) {
    Serial.println("ERROR - SD card initialization failed!");
    return;    // init failed
  }

  // Connect to Wi-Fi network with SSID and password
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected.");


  // Initialize a NTPClient to get time
  timeClient.begin();
  }

  void loop() {
  delay(2000);
  http.begin("http://192.168.0.11:8000/api/energyLogBatch/");
  http.addHeader("Content-Type", "application/json");
  String mackLogs = getMockLogs(0);
  for (int index = 1; index < 20; index++) {
    mackLogs = mackLogs + ";" + getMockLogs(index);
  }

  String httpBody ="{ \"current_sync_date\": 20181101, \"current_sync_hour\": 0,\"content\":\"" + mackLogs + "\",\"oha_device\": \"http://192.168.0.11:8000/api/device/0233556a-d8b7-4d67-8a48-8106a03c8861/\"}";
  //Serial.println(httpBody);
  int httpResponseCode = http.POST(httpBody);   //Send the actual POST request
  if (httpResponseCode > 0) {
    String response = http.getString();                       //Get the response to the request
    Serial.println(httpResponseCode);   //Print return code
    Serial.println(response);           //Print request answer
  } else {
    Serial.print("Error on sending POST: ");
    Serial.println(httpResponseCode);
  }
  http.end();
  }

  // Function to get date and time from NTPClient
  unsigned long getTimeStamp() {
  timeClient.forceUpdate();
  return timeClient.getEpochTime();
  }

  String getMockLogs(int index) {
  mockLastSequence++;
  return String(mockLastSequence) + "|" + String(getTimeStamp()) + "000|10|0|" + String(index + 1) + "|" + String(index + 2) + "|" + String(index + 2);
  }
*/
