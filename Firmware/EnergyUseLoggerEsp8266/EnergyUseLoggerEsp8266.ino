/*
  EnergyUseLoggerEsp8266 - módulo de comunicação WiFi ESP8266 para processar as requisições e respotas HTTP
  Esse código e parte do projeto: https://github.com/brolam/OpenHomeAnalysis
  @author Breno Marques(https://github.com/brolam) em 12/12/2015.
  @version 1.00
  ATENÇÃO: Favor copiar o arquivo Config_model.h para Config.h e também configurar os parâmetros antes de instalar esse código no módulo ESP8266.
  
  Ultima compilação:
   O sketch usa 231801 bytes (53%) de espaço de armazenamento para programas. O máximo são 434160 bytes.
   Variáveis globais usam 32232 bytes (39%) de memória dinâmica, deixando 49688 bytes para variáveis locais. O máximo são 81920 bytes.
*/

#include "Config.h"
#include <ESP8266WiFi.h>

#define TIMEOUT 20000  //Tempo de espera para responder uma requisição  
//#define DEBUG 1      //Escrever no Monitor serial quando ativado, {@see debug()},

#define URL_LOG "log"                //Solicitação de logs
#define URL_STATUS "status"          //Solicitação(GET) ou alteração(POST) do Status do programa, para mais detalhes {@see setStatus()} e {@see sendStatus()} 
#define URL_RESET "reset"            //Solicitação para reiniciar o Arduino, para mais detalhes {@see reset()}
#define URL_CONNECTION "connection"  //Solicitação da situação da conexão.

#define OHA_REQUEST_RUNNING F("OHA_REQUEST_RUNNING") //Sinalizar que a requisição está em processamento.
#define OHA_REQUEST_TIMEOUT F("OHA_REQUEST_TIMEOUT") //Sinalizar que o tempo de processamento da requisição ultrapassou o tempo limite.
#define OHA_REQUEST_INVALID F("OHA_REQUEST_INVALID") //Sinalizar que a requisição é inválida.
#define OHA_REQUEST_END F("OHA_REQUEST_END") //Sinalizar o fim da requisição. 
const String ACTIONS[] = {URL_LOG, URL_STATUS, URL_RESET, URL_CONNECTION};
const byte ACTIONS_COUNT = 4;

WiFiServer server(80); //Definir a porta de comunicação HTTP

void debug(String title, String value) {
#ifdef DEBUG
  Serial.print(F("DBUG-ESP8266, "));
  Serial.print(title);
  Serial.println(value);
#endif
}

/* Analisar as respostas durante o processamento da requisição, na comunicação serial entre o Arduino e o módulo ESP8266.
 * @param response informar a resposta do Arduino.
 * @param startedMillis informar quando o processamento da requisição foi iniciado em milisegundos
 * @return {@see OHA_REQUEST_END} , {@see OHA_REQUEST_TIMEOUT} or {@see OHA_REQUEST_RUNNING}
*/
String parseResponse(String response, unsigned long startedMillis) {

  if ( response.indexOf(OHA_REQUEST_END) != -1 )
    return OHA_REQUEST_END;
  else if ( (millis() - startedMillis) > TIMEOUT )
    return OHA_REQUEST_TIMEOUT;
  else
    return OHA_REQUEST_RUNNING;
}

/* Analisar se a requisição HTTP é válida e extrair o tipo de requisição, POST ou GET, e a URL.
 * @param httpRequestion informar o texto da requisição HTTP.
 * @return {@see OHA_REQUEST_END} , {@see OHA_REQUEST_TIMEOUT} or {@see OHA_REQUEST_RUNNING}
 */
String parseRequestion(String httpRequestion) {
  int first = -1;
  for (int action = 0; action < ACTIONS_COUNT; action++)
  {
    first = httpRequestion.indexOf(ACTIONS[action]);
    if ( first != -1 ) break;
  }
  if ( first == -1 )
    return OHA_REQUEST_INVALID;
  int last = httpRequestion.indexOf(" HTTP");
  return (httpRequestion.indexOf("POST") != -1 ? "POST/" : "GET/") + httpRequestion.substring(first, last);
}

/* 
 * Enviar a situação da conexão com Access Point.
 * @param client informar um WiFiClient para escrever a situação da conexão.
 */
void sendConnectionStatus(WiFiClient client) {
  client.print("<"); //Sinalizar o inicio do conteúdo
  client.print(String(HOME_WIFI_SSID)); //Nome da rede wifi que o ESP8266 está conectado.
  client.print("," + String(WiFi.localIP().toString())); //IP na rede wifi que o ESP8266 está conectado.
  client.print("," + String(ESP8266_NAME)); //Nome do ESP8266
  client.print("," + String(WiFi.softAPIP().toString())); // IP local do ESP8266.
  client.println(">");//Sinalizar o final do conteúdo
  client.print(String(OHA_REQUEST_END)); //Sinalizar o final da requisição.
  client.flush();
}


/*Configuração do programa*/
void setup() {
  Serial.begin(74880); // A velocidde 74880 foi a mais estável na comunicação com o Arduino.
  delay(10);
  WiFi.hostname(ESP8266_NAME); //Definir o nome do ESP8266 na rede.
  WiFi.softAP(ESP8266_NAME, ESP8266_PASSWORD); //Definir o nome e senha do ESP8266.
  WiFi.begin(HOME_WIFI_SSID, HOME_WIFI_PASSWORD); //Realizar conexão com o Access Point da casa.
  //Aguardar o conexão com o Access Point.
  debug("Try to Connect WIFI : ", HOME_WIFI_SSID);
  while ( WiFi.status() != WL_CONNECTED ) {
    delay(500);
    debug("", ".");
  }
  delay(1);
  server.begin(); //Inicializar o serviço HTTP
  debug("WiFi connected: ", HOME_WIFI_SSID);
  debug("Server started Local: ", String(WiFi.localIP().toString()));
  debug("Server started    AP: ", String(WiFi.softAPIP().toString()));
}


void loop() {

  // Verificar se existe um cliente conectado:
  WiFiClient client = server.available();
  if ( !client ) {
    return;
  }

  //Esperar por uma requisição
  while (!client.available()) {
    delay(1);
  }

  // Ler a primeira linha da requisição:
  String requestion = client.readStringUntil('\r');
  client.flush();

  //Iniciar o processamento da resposta:
  client.print("HTTP/1.1 200 OK\r\nContent-Type: text/planin; charset=us-asscii\r\n\r\n");
  delay(1);
  String url = parseRequestion(requestion); //Validar a URL.
  if (url != OHA_REQUEST_INVALID) {
    //Enviar a situação da conexão com o Access Point.
    if ( url.indexOf(URL_CONNECTION) > -1 )
    {
      sendConnectionStatus(client);
    } else {
      //Enviar a requisição para o Arduino e transmitir a resposta do Arduino para o solicitante( client )
      Serial.println(url); //Enviar a URL para o Arduino.
      Serial.flush();
      String response = "";
      String requestStatus = OHA_REQUEST_RUNNING; //Informar a situação da requisição
      unsigned long startedMillis = millis();
      //Processar a requisição até o Arduino sinalizar o fim da resposta
      //ou se o processamento exceder o tempo limite.
      while (requestStatus == OHA_REQUEST_RUNNING)
      {
        response = "";
        //Enviar um requisição de reset para o Arduino
        if ( url.indexOf(URL_RESET) > -1 )
        {
          response = OHA_REQUEST_END;
          client.println(response);
        }
        //A funcionalidade {@see parseRequestion() } garante que somente URL válidas sejam processadas,
        //Sendo assim, a opção alternativa sempre será a requisição para recuperar os logs de consumo de energía:
        else if (Serial.available())
        {
          while (Serial.available())
          {
            response.concat(char(Serial.read()));
          }
          client.print(response);
          startedMillis = millis();
        }
        client.flush();
        requestStatus = parseResponse(response, startedMillis); //Verificar se foi sinalizado o final da resposta ou o tempo limite foi excedido.
        delay(300);
      }

      if ( requestStatus == OHA_REQUEST_TIMEOUT) {
        client.println(String(OHA_REQUEST_TIMEOUT));
        client.flush();
      }
    }
  } else {
    client.println(String(OHA_REQUEST_INVALID));
    client.flush();
  }
}

