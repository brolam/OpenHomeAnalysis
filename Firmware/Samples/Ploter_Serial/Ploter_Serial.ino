int sensorPin = A0;    // select the input pin for the potentiometer
int ledPin = 13;      // select the pin for the LED
int sensorValue = 0;  // variable to store the value coming from the sensor
 
void setup() {
  // declare the ledPin as an OUTPUT:
  pinMode(ledPin, OUTPUT);
  // begin the serial monitor @ 9600 baud
  Serial.begin(115200);
}
 
void loop() {
  // read the value from the sensor:
  sensorValue = analogRead(sensorPin);
 
  Serial.println(sensorValue);
  Serial.print(" ");
}
