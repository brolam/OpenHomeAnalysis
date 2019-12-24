/*
  EnergyUseLogger - Ler e registrar a utilização de energia lendo os sensores SCT - 013 conectados ao circuito.
  Esse código e parte do projeto: https://github.com/brolam/OpenHomeAnalysis
  @author Breno Marques(https://github.com/brolam) em 12/12/2015.
  @version 1.2.0
*/

#include <SoftwareSerial.h> //Necessária para a comunicação serial com o modulo WiFi ESP8266.
//#define DEBUG 1 //Escrever no Monitor serial quando ativado, {@see debug()},
#define LED_LOG_SAVE 5                  //Informar a porta do LED, para sinalizar a gravação do log da utilização de energia. 

//Constantes utilizadas na geração dos logs.
#define AMOUNT_SCT 3                    //Quantidade de sensores SCT-013 suportado no circuito.  

SoftwareSerial esp8266(2, 3); //Comunicação serial com o modulo ESP8266.

#ifdef DEBUG
void debug(String title, String value) {
  Serial.print(title); Serial.println(value);
}
#endif

#ifdef DEBUG
void debug(String title) {
  Serial.println(title);
}
#endif

/* Preencher uma lista com a média da leituras dos sensores SCT 13 */
void fillReads(float reads[AMOUNT_SCT])
{
  int counts[AMOUNT_SCT];
  double readSum[AMOUNT_SCT];
  //Inicializa os valores das listas com zero:
  for (byte i = 0; i < AMOUNT_SCT; i++) {
    counts[i] = 0;
    readSum[i] = 0.00;
  }
  //Realizar 2000 leituras nas portas analogicas. Essa quantidade de leitura gera um valor mais próximos dos valores
  //medidos com o amperímetro. Somente ler valores maior do que zero.
  for (int i = 0; i < 2000; i++)
  {
    for (int r = 0; r < AMOUNT_SCT; r++)
    {
      int _analogRead = analogRead(r);
      if (_analogRead > 0 )
      {
        readSum[r] += _analogRead;
        counts[r] ++;
      }
    }
  }

  //Aplicar a média das leitura nos sensores:
  for (byte a = 0; a < AMOUNT_SCT; a++)
  {
    reads[a] =  (( counts[a]  > 10 ) ?  (readSum[a] / counts[a]) : 0.00) ;
#ifdef DEBUG
    debug("A" + String(a) + ": " , String((counts[a] > 0 ? ( readSum[a] / counts[a] ) : 0.00)) );
    debug("C" + String(a) + ": " , String(counts[a]) );
#endif
  }
}

String getSensorsValues() {
  String readValues = "";
  float reads[AMOUNT_SCT];
  fillReads(reads);
#ifdef DEBUG
  debug(F("Sensors Reads: "), (String(reads[0]) + String(" / ") + String(reads[1]) + String(" / ") + String(reads[2])));
#endif
  for (byte p = 0; p < AMOUNT_SCT; p++)
  {
    readValues = readValues + (p > 0 ? ";" : "") + String(reads[p], 2);
  }
#ifdef DEBUG
  debug(F("readValues:"), readValues);
#endif
  return readValues;
}

void sendLogBlink(long flashTime)
{
  digitalWrite(LED_LOG_SAVE, HIGH);
  delay(flashTime);
  digitalWrite(LED_LOG_SAVE, LOW);
}


/*Configuração do programa*/
void setup()
{
#ifdef DEBUG
  Serial.begin(115200); // A velocidde 74880 foi a mais estável na comunicação com o Módulo ESP8266.
#endif
  analogReference(INTERNAL); //Referência igual a 1,1 volts
  esp8266.begin(74880);  // A velocidade 74880 foi a mais estável na comunicação com o Arduino.
  pinMode(LED_LOG_SAVE, OUTPUT); //Pin do LED que sinaliza a gravação do log de utilização de energia.
}

/*Executar o fluxo do programa*/
void loop()
{
#ifdef DEBUG
  String esp8266Command = Serial.readString();
#else
  String esp8266Command = esp8266.readString();
#endif
  if (  esp8266Command.indexOf("READ") > -1 )
  {
    sendLogBlink(500);
#ifdef DEBUG
  Serial.print(getSensorsValues());
#else
   esp8266.print(getSensorsValues());
#endif
    sendLogBlink(400);
    sendLogBlink(400);
  }

}
