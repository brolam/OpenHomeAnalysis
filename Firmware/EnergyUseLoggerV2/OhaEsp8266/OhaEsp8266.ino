#include "Config.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "NTPClient.h"
#include <WiFiUdp.h>
#include "FS.h"

//#define DEBUG 1

const char* ssid     = WIFI_SSID;
const char* password = WIFI_PASSWORD;
const int INTERVAL_TIME_SAVE_LOG = 10;
long lastLogRegistered = 0;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);
HTTPClient http;

void setup() {

#ifdef DEBUG
  Serial.begin(115200);
#else
  Serial.begin(74880);
#endif

  // Connect to Wi-Fi network with SSID and password
#ifdef DEBUG
  Serial.print("Connecting to ");
  Serial.println(ssid);
#endif
  WiFi.hostname(ESP8266_NAME);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
#ifdef DEBUG
    Serial.print(".");
#endif
  }

#ifdef DEBUG
  Serial.println("WiFi connected.");
#endif

#ifdef DEBUG
  Serial.println("Initialize File System");
  delay(3000);
#endif

  //Initialize File System
  if (SPIFFS.begin())
  {
#ifdef DEBUG
    Serial.println("SPIFFS Initialize....ok");
#endif
  }
  else
  {
#ifdef DEBUG
    Serial.println("SPIFFS Initialization...failed");
#endif
  }

  //Format File System
  /*
    if (SPIFFS.format())
    {
    Serial.println("File System Formated");
    }
    else
    {
    Serial.println("File System Formatting Error");
    }
  */

#ifdef DEBUG
  FSInfo fs_info;
  SPIFFS.info(fs_info);

  Serial.print("totalBytes: ");
  Serial.println(fs_info.totalBytes);

  Serial.print("usedBytes: ");
  Serial.println(fs_info.usedBytes);

  Serial.print("blockSize: ");
  Serial.println(fs_info.blockSize);

  Serial.print("pageSize: ");
  Serial.println(fs_info.pageSize);

  Serial.print("maxOpenFiles: ");
  Serial.println(fs_info.maxOpenFiles);
#endif
  timeClient.begin();
  timeClient.forceUpdate();
  lastLogRegistered = ( timeClient.getEpochTime() - INTERVAL_TIME_SAVE_LOG) ;
}

void loop() {
  sendLogs();
#ifdef DEBUG
  Serial.print("timeClient: ");
  Serial.println(timeClient.getEpochTime());
  delay(2000);
#endif
}


void sendLogs() {

  if ( !isTimeToRegisterLog(0)) {
#ifdef DEBUG
    Serial.println("not isTimeToRegisterLog");
#endif
    return;
  }

  String batchContent = getLogs(); //"1576807563000;10.99;0;1201;1202;1203";
  batchContent.replace("\n", "");
  batchContent.replace("\r", "");
  lastLogRegistered = timeClient.getEpochTime() ;

  if ( ( batchContent.length() > 0 )) {
#ifdef DEBUG
    Serial.println("batchContent: ");
    Serial.println(batchContent);
#endif

    http.begin(String(END_POINT_URL));
    http.addHeader("Content-Type", "application/json");
    int httpResponseCode = http.POST(
                             "{\"secret_api_token\":\"" + String(SECRET_API_TOKEN) +
                             "\",\"content\":\"" + batchContent +
                             "\",\"oha_sensor\":\"" + String(SENSOR_ID) + "\" }"
                           );
    if (httpResponseCode == 201) {
#ifdef DEBUG
      Serial.print("httpResponseCode: ");
      Serial.println(httpResponseCode);   //Print return code
#endif
    } else {
#ifdef DEBUG
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
#endif
      /*
        File file = SPIFFS.open("/logs.txt", "a");
        if (!file) {
        Serial.println("Erro ao abrir arquivo!");
        } else {
        file.println(batchContent);
        Serial.print("file.size(): ");
        Serial.println(file.size());
        }
        file.close();
      */
    }
    http.end();
  }

}

String getLogs() {
  Serial.print("READ");
  while (!Serial.available()) {
  }
  return String(lastLogRegistered) + ";" + String(timeClient.getEpochTime() - lastLogRegistered ) + ";" + Serial.readString();

  /*
    File file = SPIFFS.open("/logs.txt", "r");
    if (!file) {
    Serial.println("Erro ao abrir arquivo!");
    } else {
    file.seek(0);
    int count = 0;
    while (true ) {
      String nextLogContent = file.readStringUntil('\r');
      if (nextLogContent.length() > 0 ) {
        Serial.println(nextLogContent);
        count++;
      } else {
        Serial.print("Total Logs:");
        Serial.println(count);
        Serial.print("file.size(): ");
        Serial.println(file.size());
        break;
      }
    }

    }
    file.close();
  */
}

bool isTimeToRegisterLog(int anyLess) {
  return  (( timeClient.getEpochTime() - lastLogRegistered ) > (INTERVAL_TIME_SAVE_LOG - anyLess) );
}

void espReset() {
  Serial.println("Restarting in 5 seconds");
  delay(5000);
  ESP.restart();
}
