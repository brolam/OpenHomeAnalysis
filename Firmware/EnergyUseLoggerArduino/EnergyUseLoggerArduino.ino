/*
  EnergyUseLogger - Ler e registrar a utilização de energia lendo os sensores SCT - 013 conectados ao circuito.
  Esse código e parte do projeto: https://github.com/brolam/OpenHomeAnalysis
  @author Breno Marques(https://github.com/brolam) em 12/12/2015.
  @version 1.1.0
*/

#include <SD.h>
#include <SoftwareSerial.h> //Necessária para a comunicação serial com o modulo WiFi ESP8266.
#define DEBUG 1 //Escrever no Monitor serial quando ativado, {@see debug()},
#define LED_LOG_SAVE 5                  //Informar a porta do LED, para sinalizar a gravação do log da utilização de energia. 

//Constantes utilizadas na geração dos logs.
#define AMOUNT_SCT 3                    //Quantidade de sensores SCT-013 suportado no circuito.  
#define INTERVAL_TIME_SAVE_LOG 10000    //Intervalo em Milissegundo para registrar o proximo log, {@see saveLog()}    

//Constantes utilizadas na gravação dos arquivos.
#define SD_CS 10                        //The pin connected to the chip select line of the SD card, @link https://www.arduino.cc/en/Reference/SDbegin
#define F_BEGIN F("<")                  //Sinalizar o inicio do conteúdo de registro de utilização de entergia 
#define F_END F(">")                    //Sinalizar o final do conteúdo de registro de utilização de entergia
#define LINE_END '\n'                   //Sinalizar o final da linha do registro de utilização de entergia
#define F_TXT F(".txt")                 //Extensão dos arquivos de logs de registros de energia

//Constantes utilizadas na comunicação com o modulo WiFi ESP8266
#define URL_LOG F("log")                                 //Solicitação de logs
#define URL_STATUS F("status")                           //Solicitação(GET) ou alteração(POST) do Status do programa, para mais detalhes {@see setStatus()} e {@see sendStatus()} 
#define URL_RESET F("reset")                             //Solicitação para reiniciar o Arduino, para mais detalhes {@see reset()}
#define LOG_DATE_NOT_EXISTS F("LOG_DATE_NOT_EXISTS")     //Sinalizar que o log não existe na data e hora solicitada.
#define LOG_END_OF_FILE F("LOG_END_OF_FILE")             //Sinalizar que a leitura dos logs chegou ao final.
#define LOG_DELETED F("LOG_DELETED")                 //Sinalizar que o log foi excluido.
#define OHA_STATUS_FINISHED F("OHA_STATUS_FINISHED")     // Sinalizar que a geração de logs está finalizada para a data e hora solicitada
#define OHA_STATUS_RUNNING F("OHA_STATUS_RUNNING")       // Sinalizar que a geração de logs esta ativa  para a data e hora solicitada

//Constantes para definir o status de execução do programa.
#define OHA_STATUS_NOT_DATE F("OHA_STATUS_NOT_DATE")    //Sinalizar que a data do programa não está atualizada. 
#define OHA_STATUS_NOT_SD F("OHA_STATUS_NOT_SD")        //Sinalizar que o SD Card está com problema e os registros dos logs está parado.   
#define OHA_STATUS_OK F("OHA_STATUS_OK")                //Sinalizar que a data do programa está atualizada / registrando os logs de utilização de energia. 

String ohaStatus = String("");                         //Armazenar o status atual do programa.
String currentDate = String("");                       //Armazenar a data atual para o registro dos logs no formato YYYYMMDD
String currentTime = String("");                       //Armazenar a hora + minutos atual para o registro dos logs no forma HHmm
unsigned long millisOnSetTime = 0;                     //Armazenar o total de Milissegundo {@see millis()}, quando a data e hora do programa foi atualizada, para mais detalhes veja {@see setDate()}
unsigned long intervalSaveLog = 0;                     //Armazenar o tempo que o ultimo log foi salvo, por favor, veja {@see saveLog()} para mais detalhes.

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

/* Abrir um arquivo no cartão de memória conforme parametros abaixo:
   IMPORTANTE: O arquivo deve ser fechado, {@see file.close()}, no final do processo!
*/
File getFile(String fileName, boolean readOnly) {
  return SD.open(fileName, ( readOnly ? FILE_READ : FILE_WRITE));
}

String getLogFileName(String date, String strTime) {
  return date.substring(0, 6) + strTime.substring(0, 2) + String(F_TXT);
}

String getStatusByDateHour(String strDate, String strHour) {
  return ((strDate == currentDate) && (currentTime.substring(0, 2) == strHour.substring(0, 2)) ? String(OHA_STATUS_RUNNING) : String(OHA_STATUS_FINISHED));
}

/* Preencher uma lista com a média da leituras dos sensores SCT 13 */
void fillReads(double reads[AMOUNT_SCT])
{
  int counts[AMOUNT_SCT];
  double readSum[AMOUNT_SCT];
  //Inicializa os valores das listas com zero:
  for (byte z = 0; z < AMOUNT_SCT; z++) {
    counts[z] = 0;
    readSum[z] = 0.00;
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

/* Registrar um log de utilização de energía no cartão de memória.*/
void saveLog() {
  double reads[AMOUNT_SCT];
  fillReads(reads);

  //Verifica se o tempo de intervalo para gravar o próximo log foi alcançado:
  while ( (millis() - intervalSaveLog) < INTERVAL_TIME_SAVE_LOG ) {
    delay(100);
  }
  //Reiniciar o intervalo do tempo que o log deve ser gavado nesse ponto, para não contabilizar o tempo
  //da gravação do próximo logo.
  intervalSaveLog = millis();
#ifdef DEBUG
  debug(F("Sensors Reads: "), (String(reads[0]) + String(" / ") + String(reads[1]) + String(" / ") + String(reads[2])));
#endif
  String fileName = getLogFileName(currentDate, currentTime); // Nome do arquivo do log.
  File fileLog = getFile(fileName, false);
  fileLog.print(F_BEGIN); // Inicio do conteúdo do log
  fileLog.print(currentTime.substring(0, 6)); fileLog.print('|'); //Ultima hora e minuto atualizada no programa.
  fileLog.print(getVolts()); fileLog.print('|');
  //Gerar um coluna para cada leitura dos SCT 013 disponíveis no circuito:
  for (byte s = 0; s < AMOUNT_SCT; s++)
  {
    fileLog.print((reads[s] > 0 ? reads[s] : 0.00)); fileLog.print('|');
  }
  fileLog.print(millis() - millisOnSetTime); //Total de Milissegundo desde a ultima atualização da data e hora no programa, para mais detalhes, veja B4/Observação 02.
  fileLog.print(F_END); //Final do conteudo do log, somente logs com essa sinalização serão considerados válidos.
  fileLog.print(LINE_END); //Sinaliza o final do log ou final da linha no arquivo de log.
  fileLog.close();
#ifdef DEBUG
  debug(F("path:"), fileName);
#endif
}

/* Enviar uma lista de logs para o módulo ESP8266 via comunicação serial conforme parametros abaixo:
   @param strDate informar texto com data no formato YYMMDD
   @param strHour informar texto com hora e minuto no formato HHmm
   @param start informar o inicio da sequência do log.
   @param amount informar a quantidade de logs.
   @param strDelDate informar a data dos logs que podem ser excluidos para liberar espaço no cartão de memória.
*/
void sendLog(String strDate, String strHour, int startPosition, int amount ) {
  int countReadLogs = 0;
  String pathLog = getLogFileName(strDate, strHour);
  if ( !SD.exists(pathLog) ) {
    esp8266.println(LOG_DATE_NOT_EXISTS);
    return;
  }
  File logFile = getFile(pathLog, true);
  logFile.seek(startPosition);
  do {
    String nextLogContent = logFile.readStringUntil(LINE_END);
    if (nextLogContent.length() > 0) {
      esp8266.println(String(logFile.position()) + ':' + nextLogContent);
#ifdef DEBUG
      debug(F("SendLog: "), String(logFile.position()) + ':' + nextLogContent);
#endif
    } else {
      esp8266.println(String(LOG_END_OF_FILE));
#ifdef DEBUG
      debug(F("SendLog: "), String(LOG_END_OF_FILE));
#endif
      break;
    }
    //Necessário para sincronizar a comunicação serial e evitar dados truncados.
    delay(300);
    countReadLogs++;
  } while (countReadLogs <= amount);
  esp8266.println( getStatusByDateHour(strDate, strHour));
#ifdef DEBUG
  debug(F("SendLog: "), getStatusByDateHour(strDate, strHour));
#endif
  logFile.close();
}

/* Atualizar o status do programa e também a data e hora utilizada no registro do log de utilização de energía:
   @param strDate informar texto com data no formato YYYYMMDD
   @param strDate informar texto com hora e minuto no formato HHmm
*/
void setStatus(String strDate, String strTime) {
  //Atualizar o diretório atual para gravação de novos logs conforme a data e hora informada,
  //Ex: 20150101/00 , sendo importante destacar, que a hora também é adicionada como um subdiretório para
  //tornar mais rapido a recuperação dos logs no cartão de memória.
  currentDate = strDate;
  currentTime = strTime;
  millisOnSetTime = millis(); //Reiniciar a contagem de milissegundos para a nova data e hora, para mais informações vaje B3/Obeservação 02.
  ohaStatus = OHA_STATUS_OK; //Sinalizar que a gravação dos logs estão sendo realizadas sem problemas.
  delay(100);
}

/* Evniar para o módulo ES8266 vai comunicação serial as informações do status do programa.
   @param strDate informar texto com data no formato YYYYMMDD
   @param strHour informar texto com hora e minuto no formato HHmm
*/
void sendStatus(String strDate, String strTime) {
  String path = getLogFileName( strDate, strTime);
  if ( !SD.exists(path) )
  {
    esp8266.println(LOG_DATE_NOT_EXISTS);
  } else {
    /*Status da sequencia:
         RUNNING - Novas logs estão sendo geradas para a data e hora informada;
         STOPPED - Geração de logs finalizada para a data e hora informada.
    */
    esp8266.print(F_BEGIN); // Inicio do conteúdo do log
    esp8266.print(getStatusByDateHour(strDate, strTime));
    esp8266.print("|");
    esp8266.print(millis()); // Informar o tempo que o dispositivo está funcionando desde a ultima inicialização.
    esp8266.println(F_END);
  }
  esp8266.flush();
}

/*
  Excluir logs de utilização de energía para liberar espaço no cartão de memória.
*/
void deleteLogs(String strDate, String strHour)
{
  String deleteLog = getLogFileName(strDate, strHour);
  if ( SD.exists(deleteLog)) {
#ifdef DEBUG
    debug(F("Try delete: "), deleteLog);
#endif
    SD.remove(deleteLog);
    esp8266.println(LOG_DELETED);
    esp8266.println(F_END);
#ifdef DEBUG
    debug(F("Deleted: "), deleteLog);
#endif
  }
}

/* Reservada para realizar a leitura da tensão, se o valor for zero, considerar o valor padrão
  da tensão, exemplo: 220 ou 110*/
double getVolts() {
  return 0.00;
}

/* Final do B4*/

/***************************************************************************************************************************************************************
   Inicio do B5 - Funcionalidades para realizar a comunicação como o módulo WiFi.
   Visão geral: Nesse bloco de códigos estão as funcionalidades para a comunicação com o módulo ESP8266 via serial.
 ***************************************************************************************************************************************************************/

/* Transformar uma URL em uma lista de parâmetros.
   @param url informar uma URL válida.
   @param params informar uma lista de textos com 10 itens.
*/
void parseUrl(String url, String params[]) {
#ifdef DEBUG
  debug(F("URL: "), url);
#endif
  //Dividir a url por "/" e atribuir para cada item do params[].
  for (byte iParam = 0; iParam < 10; iParam++ ) {
    int index = url.indexOf("/");
    if ( index != -1 ) {
      params[iParam] = url.substring(0, index);
      url.remove(0, index + 1);
    }
  }
}

/* Executar a funcionalidade informada na Url enviada via o módulo ESP8266.
   ATENÇÃO: A quantidade de parâmetros, incluindo o tipo do Web Method POST ou GET, é
   limitado a 10 itens!
   @param url informar uma URL válida.
*/
void doUrl(String url) {
  String params[10];
  parseUrl(url, params); //Transformar a Url em uma lista de parâmtros, sendo o item 0 o tipo do Web Method, GET ou POST.
  boolean isPost = ( params[0].indexOf("POST") != -1);
  boolean isDelete = ( params[0].indexOf("DELETE") != -1);
  //Se o cartão de memória não estiver disponível a funcionalidade nas URL abaixo não será executada.
  if ( ohaStatus != OHA_STATUS_NOT_SD) {
    //Atualizar a data do programa.
    if (isPost && ( params[1] == URL_STATUS ) ) {
      setStatus(params[2], params[3]);
    }
    //Somente executar as funcionalidade abaixo se o Status do programa for igual a OHA_STATUS_OK
    else if ( ohaStatus == OHA_STATUS_OK ) {
      //Enviar logs de utilização de energia:
      if ( !isPost && ( params[1] == URL_LOG ) ) { //Enviar logs
        sendLog(params[2], params[3], params[4].toInt(), params[5].toInt());
      } else if ( isDelete &&  ( params[1] == URL_LOG ) ) { //Excluir Logs 
        deleteLogs(params[2], params[3]);
      } else if (!isPost && params[1] == URL_STATUS) { //Enviar status do programa.
        sendStatus(params[2], params[3]);
      } else {
#ifdef DEBUG
        debug(F("Url invalid :"), params[1] );
#endif
      }
    }
  }
  //Enviar somente o status do programa se houver algum problema.
  if ( ohaStatus != OHA_STATUS_OK) {
    esp8266.println(ohaStatus);
  }
  esp8266.println("OHA_REQUEST_END"); //Sinalizar o final da comunicação com o módulo ESP8266.
  delay(300);
}

/*Reiniciar o módulo ESP8266*/
void esp8266Reset() {
  const byte PIN_ESP8255_RST = 4;
  pinMode(PIN_ESP8255_RST, OUTPUT);
  digitalWrite(PIN_ESP8255_RST, LOW);
  delay(300);
  digitalWrite(PIN_ESP8255_RST, HIGH);
}
/*Inicio do B5*/

/*Sinalizar via LED a gravação do log de utilização de energía.
  @param flashTime informar o tempo em milissegundos para acender e apagar o LED.
*/
void saveLogBlink(long flashTime)
{
  digitalWrite(LED_LOG_SAVE, HIGH);
  delay(flashTime);
  digitalWrite(LED_LOG_SAVE, LOW);
}

/*Configuração do programa*/
void setup()
{
#ifdef DEBUG
  Serial.begin(74880); // A velocidde 74880 foi a mais estável na comunicação com o Módulo ESP8266.
#endif
  analogReference(INTERNAL); //Referência igual a 1,1 volts
  esp8266Reset(); //Reiniciar o módulo ESP8266 na primeira conexão para garantir que o módulo não esteja travado.
  esp8266.begin(74880);  // A velocidade 74880 foi a mais estável na comunicação com o Arduino.
  pinMode(SD_CS, OUTPUT); //The pin connected to the chip select line of the SD card, @link https://www.arduino.cc/en/Reference/SDbegin
  pinMode(LED_LOG_SAVE, OUTPUT); //Pin do LED que sinaliza a gravação do log de utilização de energia.
#ifdef DEBUG
  debug(F("Setup in five minuntes"));
#endif
  delay(5000);
  //Iniciar e verificar o funcionamento do cartão de memória:
  if (!SD.begin(SD_CS))
  {
#ifdef DEBUG
    debug(F("initialization failed!"));
#endif
    ohaStatus = OHA_STATUS_NOT_SD;
  } else {
    ohaStatus = OHA_STATUS_OK;
  }
}

/*Executar o fluxo do programa*/
void loop()
{
  //Se o cartão de memória estiver funcionando:
  if ( ohaStatus != OHA_STATUS_NOT_SD ) {
    //Verificar se a data e hora do sistema está atualizada.
    if ( currentDate.length() == 0 )
    {
      ohaStatus = OHA_STATUS_NOT_DATE;
      //Salvar log de utilização de energía se a data e hora do program estiver atualizada:
    } else {
      saveLogBlink(500);
      saveLog();
      saveLogBlink(400);
      saveLogBlink(400);
    }
  }
  //Verificar se existe requisições realizadas via o módulo ESP8266:
  if ( esp8266.available() )
  {
    doUrl(esp8266.readString()); //Executar a funcionalidade informada na URL.
  }
  delay(3000);
  if ( ohaStatus != OHA_STATUS_OK)
  {
#ifdef DEBUG
    debug(F("Status: "), ohaStatus);
#endif
  }
}
