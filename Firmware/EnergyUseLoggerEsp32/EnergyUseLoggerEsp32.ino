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
