const int analogPin = A0;
const int digitalPin =  7;


void setup() {
  Serial.begin(115200);
  pinMode(digitalPin, INPUT);    

}

void loop() {
  int valAnalog = analogRead(analogPin);
  int valDigital = digitalRead(digitalPin);  
  Serial.print("Analog Read: "); Serial.println(valAnalog); 
  Serial.print("Digital Read: "); Serial.println(valDigital);
  Serial.print("Volts Read: "); Serial.println((5.00 / 1023) * valAnalog); 
  delay(500);   

}
