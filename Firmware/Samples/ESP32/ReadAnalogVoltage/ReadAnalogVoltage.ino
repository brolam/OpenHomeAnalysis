// Potentiometer is connected to GPIO 34 (Analog ADC1_CH6)
const int pin34 = 34;
const int pin35 = 35;
const int pin32 = 32;
const int pins[] = {34, 35, 32};
int pinsValues[3] = {0, 0, 0};
int pinsCount[3] = {0, 0, 0};

void setup() {
  Serial.begin(115200);
  delay(1000);
  for (int p = 0; p < 3; p++) {
    pinMode(pins[p], INPUT);
  }
 
}

void loop() {
  // Reading potentiometer value
  for (int t = 0; t < 10; t++) {
    for (int p = 0; p < 3; p++) {
      int readValue = analogRead(pins[p]);
      if ( readValue > 0 ) {
        pinsValues[p] += readValue;
        pinsCount[p] ++;
      }
    }
  }
  Serial.print("P 34:"); Serial.print("/"); Serial.println((pinsValues[0] / pinsCount[0]) * (3.3 / 4095 ));
  Serial.print("P 35:"); Serial.print("/"); Serial.println((pinsValues[1] / pinsCount[1]) * (3.3 / 4095 ));
  Serial.print("P 32:"); Serial.print("/"); Serial.println((pinsValues[2] / pinsCount[2]) * (3.3 / 4095 ));
  delay(500);
  for (int p = 0; p < 3; p++) {
    pinsValues[p] = 0;
    pinsCount[p] = 0;
  }
}
