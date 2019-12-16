#include <driver/adc.h>
#include "esp_adc_cal.h"

// Potentiometer is connected to GPIO 34 (Analog ADC1_CH6)
const int pin34 = 34;
const int pin35 = 35;
const int pin32 = 32;
const int pins[] = {34, 35};
int pinsValues[3] = {0, 0, 0};
int pinsCount[3] = {0, 0, 0};

void setup() {
  Serial.begin(115200);
  delay(1000);
  analogSetAttenuation(ADC_2_5db);
  //adc1_config_channel_atten(ADC1_CHANNEL_6, ADC_ATTEN_DB_11);
  for (int p = 0; p < 2; p++) {
    pinMode(pins[p], INPUT);
  }
 
}

void loop() {
  // Reading potentiometer value
  for (int t = 0; t < 20000; t++) {
    for (int p = 0; p < 2; p++) {
      int readValue = analogRead(pins[p]);
      if ( readValue > 0 ) {
        pinsValues[p] += readValue;
        pinsCount[p] ++;
      }
    }
  }
  if ( pinsCount[0] > 0 ){
    Serial.print("P 34:"); Serial.print("/"); Serial.print((pinsValues[0] / pinsCount[0]) ); Serial.print("/"); Serial.println(pinsCount[0]) ;  
  }

  if ( pinsCount[1] > 0 ){
    Serial.print("P 35:"); Serial.print("/"); Serial.print(pinsValues[1]/ pinsCount[1]); Serial.print("/"); Serial.println(pinsCount[1]) ;  
  }
  //Serial.print("P 34:"); Serial.print("/"); Serial.println((pinsValues[0] / pinsCount[0]) * (3.3 / 4095 ));
  //Serial.print("P 35:"); Serial.print("/"); Serial.println((pinsValues[1] / pinsCount[1]) * (3.3 / 4095 ));
  //Serial.print("P 32:"); Serial.print("/"); Serial.println((pinsValues[2] / pinsCount[2]) * (3.3 / 4095 ));
  for (int p = 0; p < 2; p++) {
    pinsValues[p] = 0;
    pinsCount[p] = 0;
  }
}
