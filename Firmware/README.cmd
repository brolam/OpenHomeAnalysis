 Para realizar testes, segue exemplos de requisições HTTP:
   Atualizar a Data e Hora: 
     curl --data "" http://192.168.0.11/status/20170101/0000/
   Recuperar a situação:  
     curl -i -H "Accept: application/text" -H "Content-Type: application/text" http://192.168.0.11/status/20170101/00/
   Recuperar a situação da conexão com o Access Point:  
    curl -i -H "Accept: application/text" -H "Content-Type: application/text" http://192.168.0.11/connection/
   Recuperar as logs de consumo de energia:
    curl -i -H "Accept: application/text" -H "Content-Type: application/text" http://192.168.0.11/log/20170101/0000/1/500/20161231/
  Reiniciar o Arduino: 
     curl --data "" http://192.168.0.11/reset/


     /*
  EnergyUseLogEsp8266 Configuração - Favor configurar os parâmetros abaixo antes de instalar esse código no módulo ESP8266. 
                 Esse código e parte do projeto: https://github.com/brolam/OpenHomeAnalysis
  @author Breno Marques(https://github.com/brolam) em 12/12/2015.
  @version 1.00
*/


const char* ESP8266_NAME = "OHA#1" //Informar o nome do ESP8266, se possível, no formato OHA_EUL_[Número] para faciliar a localização na lista de redes WiFi.
const char* ESP8266_PASSWORD = "oha.2015"; //Informar a senha do ESP8266.
const char* HOME_WIFI_SSID = "bfw"; //Informar o mesmo nome da rede WiFi onde o table ou smartphone está conectado.
const char* HOME_WIFI_PASSWORD = "master@123"; //Informar a senha da rede WiFi. 