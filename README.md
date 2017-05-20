# OHA - Open Home Analysis
<p>
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_supervisory_screens_01.png" height="350" hspace="10"/>
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_supervisory_screens_02.png" height="350" hspace="30"/>
</p>
OHA é uma solução desenvolvida sobre as plataformas Arduino e Android para analisar informações registradas por sensores instalados em uma residência. 

O primeiro sensor disponível registra a utilização de energia de todos os aparelhos conectados a rede elétrica da residência e todas as informações são analisadas através do aplicativo Supervisory para Android, que será responsável pelo armazenamento e disponibilização dessas informações.

É importante destacar que cada aparelho gera uma assinatura na utilização de energia na rede elétrica, dessa forma, é possível analisar a utilização de energia por aparelho e período através do aplicativo Supervisory.

Esse é um projeto Open Source / Open Hardware, sendo assim, todas as informações necessárias para desenvolve-lo estão disponíveis nesse repositório, além disso, também recomendo os vídeos abaixo disponíveis no youtube com mais detalhes do projeto: 

- [Visão Geral do Projeto](https://github.com/brolam/OpenHomeAnalysis);
- [Desenvolvendo o Registrador de Utilização de Energia](https://github.com/brolam/OpenHomeAnalysis);

## Registrador de Utilização de Energia

### Protoboard
<p align="center">
  <img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_protoboard_energy_use_log.png" width="50%" />
</p> 

### PCB
<p align="center">
  <img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_pcb_energy_use_log.png" width="50%" />
</p>
O objetivo da Protoboard na imagem acima é fornecer uma visão geral dos componentes e suas conexões, mas sugiro a construção da PCB, conforme orientações abaixo, para evitar problemas com as conexões que podem inviabilizar o funcionamento correto desse circuito. 


Segue abaixo a lista de componentes com o link das lojas onde eles foram adquiridos:
<table>
  <tr>
    <th width="30%">Componentes</th>
    <th>Descrição</th> 
    <th>Objetivo</th>
    <th>Preço</th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://www.robocore.net/loja/produtos/upload/lojavirtual/530_2_H.png?20170511161143" /></th>
    <th>Arduino UNO R3 ou compatível fabricado pela RoboCore.</th> 
    <th>Ler através das portas analógicas a utilização de energia e registrar no SD Card.</th>
    <th><a href="https://www.robocore.net/loja/produtos/arduino-blackboard.html">R$85,00</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/6690-thickbox_default/sd-card-arduino-leitor-micro-sd-card.jpg" /></th>
    <th>MicroSD Card Adapter CATALEX v1.0.</th> 
    <th>Registrar temporariamente a utilização de energia.</th>
    <th><a href="http://www.usinainfo.com.br/modulos-para-arduino/sd-card-arduino-leitor-micro-sd-card-2637.html">R$12,90</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://www.robocore.net/loja/produtos/upload/lojavirtual/652_2_H.png?20170502154613" /></th>
    <th>Módulo ESP8266-01</th> 
    <th>Disponibilizar os registros de utilização de energia através de uma conexão WiFI.</th>
    <th><a href="https://www.robocore.net/loja/produtos/modulo-wifi-esp8266.html">R$39,00</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://www.robocore.net/loja/produtos/upload/lojavirtual/522_2_H.png?20170407151541" /></th>
    <th>Conversor de Nível Lógico RC 3,3V / 5V</th> 
    <th>Converter o nível lógico de 5V do Arduino para 3,3V na comunicação serial com o Módulo ESP8266-01.</th>
    <th><a href="https://www.robocore.net/loja/produtos/conversor-de-nivel-logico.html">R$8,50</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://http2.mlstatic.com/modulo-regulador-tensao-ams1117-33v-p-esp8266-wifi-arduino-D_NQ_NP_931601-MLB20355823289_072015-O.webp"/></th>
    <th>Regulador de Tensão Ams1117 3.3v P/ Esp8266</th> 
    <th>Transformar a alimentação do circuito de 9V para 3.3V e alimentar o Módulo ESP8266-01</th>
    <th><a href="http://produto.mercadolivre.com.br/MLB-711669968-modulo-regulador-tensao-ams1117-33v-p-esp8266-wifi-arduino-_JM">R$5,00</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://blog.filipeflop.com/wp-content/uploads/2015/11/IMG_2909.png"/></th>
    <th>3 X Sensores de Corrente Não Invasivo 100A SCT-013</th> 
    <th>Realizar a leitura da utilização de energia</th>
    <th><a href="http://www.filipeflop.com/pd-2025cd-sensor-de-corrente-nao-invasivo-100a-sct-013.html?ct=&p=1&s=1">R$47.90X3 = R$143,70</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://lghttp.57222.nexcesscdn.net/803B362/magento/media/catalog/product/cache/1/thumbnail/65x/040ec09b1e35df139433887a97daa66f/4/7/470uf.jpg"/></th>
    <th>Capacitor Eletrolítico 470uF / 16V</th> 
    <th>Estabilizar a alimentação entre a fonte de 9V e o regulador Tensão Ams1117 3.3v</th>
    <th><a href="http://www.baudaeletronica.com.br/capacitor-eletrolitico-470uf-16v.html">R$0,20</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://lghttp.57222.nexcesscdn.net/803B362/magento/media/catalog/product/cache/1/thumbnail/65x/040ec09b1e35df139433887a97daa66f/1/0/10uf_16v.jpg"/></th>
    <th>Capacitor Eletrolítico 10uF / 16V</th> 
    <th>Estabilizar a alimentação entre o regulador de Tensão Ams1117 3.3v e o Módulo ESP8266-01</th>
    <th><a href="http://www.baudaeletronica.com.br/capacitor-eletrolitico-10uf-16v.html?gclid=CJGrgOLo7NMCFUlMDQodi3kNyw">R$0,20</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://lghttp.57222.nexcesscdn.net/803B362/magento/media/catalog/product/cache/1/thumbnail/65x/040ec09b1e35df139433887a97daa66f/c/f/cfr-25jb-33r.jpg"/></th>
    <th>3 X Resistores (33Ω)</th> 
    <th>Dividir a tensão do SCT-013 para os 5V nas portas analógicas do Arduino</th>
    <th><a href="http://www.baudaeletronica.com.br/resistor-33r-5-1-4w.html">R$0.10X3 = R$0,30</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://lghttp.57222.nexcesscdn.net/803B362/magento/media/catalog/product/cache/1/thumbnail/65x/040ec09b1e35df139433887a97daa66f/1/n/1n4007_1.jpg"/></th>
    <th>12 X Diodos In4007 ou equivalentes</th> 
    <th>Transformar a corrente alternada do SCT-013 em corrente contínua através de uma ponte retificadora.</th>
    <th>R$0.20X12 = R$2,40</th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/1001650-thickbox_default/conector-5045-2-pinos-macho-kit-com-5-unidades.jpg"/></th>
    <th>3 X Conectores 5045 2 Pinos Macho</th> 
    <th>Conectar os SCT-013 ao circuito.</th>
    <th><a href="http://www.usinainfo.com.br/conectores-e-soquetes/conector-5045-2-pinos-macho-kit-com-5-unidades-3485.html">R$0,32X3 = R$0,96</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/1001661-thickbox_default/conector-5051-2-pinos-femea-terminais-kit-com-5-unidades.jpg"/></th>
    <th>3 X Conectores 5051 2 Pinos Fêmea</th> 
    <th>Conectar os SCT-013 ao circuito.</th>
    <th><a href="http://www.usinainfo.com.br/conectores-e-soquetes/conector-5045-2-pinos-macho-kit-com-5-unidades-3485.html">R$0,38X3 = R$1,14</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/8944-large_default/barra-de-10-pinos-femea-conector-empilhavel.jpg"/></th>
    <th>2 X Barras de 10 pinos fêmea / Conector Empilhável</th> 
    <th>Conectar o MicroSD Card e Regulador Tensão Ams1117 ao circuito.</th>
    <th><a href="http://www.usinainfo.com.br/barras-de-pinos/barra-de-10-pinos-femea-conector-empilhavel-2530.html">R$2,00X2 = R$4,00</a></th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/9183-large_default/barra-de-40-pinos-femea-conector-empilhavel-para-pci.jpg"/></th>
    <th>Barra de 40 pinos fêmea / Conector Empilhável para PCI</th> 
    <th>Conectar o Módulo ESP8266-01 e Conversor de Nível Lógico RC 3,3V ao circuito.</th>
    <th>
        <a href="http://www.usinainfo.com.br/barras-de-pinos/barra-de-40-pinos-femea-conector-empilhavel-para-pci-3109.html?search_query=Barra+de+40+&results=100">R$3,85</a>
    </th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/7129-large_default/barra-de-pinos-macho-1x40-vias-com-passo-de-254mm-180-preto.jpg"/></th>
    <th>Barra de pinos macho 1x40 vias com passo de 2,54mm 180°</th> 
    <th>Conectar o Arduino UNO R3 ou compatível ao circuito.</th>
    <th>
        <a href="http://www.usinainfo.com.br/barras-de-pinos/barra-de-pinos-macho-1x40-vias-com-passo-de-254mm-180-preto-2748.html">R$1,90</a>
    </th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/1002976-large_default/placa-de-fenolite-cobreada-simples-10x20-cm-para-circuito-impresso.jpg"/></th>
    <th>Placa de Fenolite Cobreada Simples 10x20 cm</th> 
    <th></th>
    <th>
         <a href="http://www.usinainfo.com.br/circuito-impresso/placa-de-fenolite-cobreada-simples-10x20-cm-para-circuito-impresso-3841.html">R$6,90</a>
    </th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/7755-large_default/led-amarelo-5mm-difuso-kit-com-5-unidades.jpg"/></th>
    <th>LED 5mm Difuso e um Resistor de (330Ω) </th> 
    <th>Informar se a utilização de energia foi registrada com sucesso.</th>
    <th>
         <a href="http://www.usinainfo.com.br/leds/led-amarelo-5mm-difuso-kit-com-5-unidades-2978.html">R$0,40 + R$0,30 = R$0,70</a>
    </th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="http://static.usinainfo.com.br/1005396-large_default/fonte-de-alimentacao-para-arduino-9vdc-1a.jpg"/></th>
    <th>Fonte de Alimentação para Arduino 9VDC 1A</th> 
    <th>Alimentar todo o circuito.</th>
    <th><a href="http://www.usinainfo.com.br/fonte-de-alimentacao/fonte-de-alimentacao-para-arduino-9vdc-1a-2424.html?search_query=Fonte+9V&results=334">R$18,85</a>
    </th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://http2.mlstatic.com/carto-de-memoria-microsd-2gb-com-adaptador-D_Q_NP_14341-MLB176519648_1972-M.webp"/></th>
    <th>Cartão De Memória Microsd 2gb</th> 
    <th>Armazenar temporariamente os registros de utilização de energia.</th>
    <th>R$15,85</th>
  </tr>
</table>
O custo estimado considerando somente os componentes é de aproximadamente R$351,35, sendo importante destacar que será necessário dedicar um smartphone ou tablet Android para monitorar o Registrador de Energia.

## Construindo e Instalando o Registrador de Utilização de Energia
### PCB
<p>
  <img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_energy_use_logger_01.jpg" width="45%" hspace="10"/>
  <img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_energy_use_logger_02.jpg" width="45%" hspace="30"/>
</p>
A placa pode ser construída utilizando a técnica de termotransferência do circuito impresso via impressora laser, sendo assim, o <a href="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_pcb_energy_use_logger.pdf">PDF</a> do circuito também está disponível nesse repositório para facilitar a impressão. <br>Também clique na imagem acima para ampliá-la é visualizar com mais detalhes como cada componente deve ser instalado na placa.

### Instalação 
<table>
  <tr>
    <th th width="40%"><img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_energy_use_logger_03.jpg" /></th>
    <th>A placa junto com a fonte de 9V podem ser instaladas em uma ARANDELA medindo 20cmX10cm e 7,5cm de profundidade ao lado do quadro de distribuição elétrica;</th>
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_energy_use_logger_04.jpg" /></th>
    <th>Veja na imagem ao lado a instalação da placa junto com a fonte de 9V;</th> 
  </tr>
  <tr></tr>
  <tr>
    <th><img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_energy_use_logger_05.jpg" /></th>
    <th>Também observe a instalação dos sensores SCT-013 em cada fase.</th> 
  </tr>
</table>

## Carregando o Firmware no Módulo ESP8266 e Arduino 

### ESP8266
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_esp8266_energy_use_logger.png" />
Antes de carregar o firmware disponível em <a href="https://github.com/brolam/OpenHomeAnalysis/tree/master/Firmware/EnergyUseLoggerEsp8266">Firmware/EnergyUseLoggerEsp8266</a> no módulo ESP8266, é necessário copiar o arquivo Config_model.h para o Config.h no mesmo diretório e configurar os parâmetros exibidos na imagem acima. Caso exista alguma dificuldade para conectar o módulo ESP8266 via USP e realizar a carga, sugiro a leitura do tutorial disponível em <a href="https://www.robocore.net/loja/produtos/modulo-wifi-esp8266.html">www.robocore.net</a>.

### Arduino
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_arduino_energy_use_logger.png" />
O firmware para o Arduino está disponível em <a href="https://github.com/brolam/OpenHomeAnalysis/tree/master/Firmware/EnergyUseLoggerArduino">Firmware/EnergyUseLoggerArduino</a>. </br>Observação: se o Arduino estiver conectado a placa, também será necessário conectá-lo a fonte de 9V, porque somente a alimentação via USB não será suficiente para alimentar todo o circuito.

### Fritzing
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_fritzing.png" />
Caso seja necessário realizar alguma modificação no circuito, o arquivo .fzz também está disponível nesse repositório <a href="https://github.com/brolam/OpenHomeAnalysis/tree/master/Documents/Fritzing/OhaEnergyUseLog.fzz">Documents/Fritzing/</a>.

## Instalando e Configurando o Aplicativo Supervisory

### Instalação através do Google Play
O aplicativo Supervisory está disponível na loja de aplicativos, favor procurar por <a href="https://github.com/brolam/OpenHomeAnalysis/tree/master/Firmware/EnergyUseLoggerArduino">OHA - Open Home Analysis</a> e realizar a instalação.

### Instalação através do Android Studio
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_android_studio.png" />
O código fonte do aplicativo Supervisory também está disponível nesse repositório em <a href="https://github.com/brolam/OpenHomeAnalysis/tree/master/Documents/Fritzing/OhaEnergyUseLog.fzz">Android/</a>. Sendo assim, é possível realizar a instalação e modificações através do Android Studio.

### Configurando o Registrador de Utilização de Energia no aplicativo Supervisory
<img src="https://raw.githubusercontent.com/brolam/OpenHomeAnalysis/master/Documents/Images/oha_supervisory_screens_03.png" />
No aplicativo /Settings/Energy Use Logger, conforme imagem acima, é possível preencher os parâmetros para conectar o aplicativo ao Registrador de Utilização de Energia.

## Próximas Funcionalidades
Novas funcionalidades, melhorias e correções serão cadastradas na <a href="https://github.com/brolam/OpenHomeAnalysis/issues">Lista de Questões(Issues)</a> desse repositório, então, basta seguir esse repositório para receber notificacões sobre as novidades.

## Contribuições
Fique a vontade para contribuir e acompanhe a <a href="https://github.com/brolam/OpenHomeAnalysis/issues">Lista de Questões(Issues)</a> do projeto ou entre em contato através do e-mail breno@brolam.com.br

## Links importantes para o desenvolvimento do projeto

- <a href="https://www.embarcados.com.br/">Embarcados</a> 
- <a href="https://openenergymonitor.org/">Open Energy Monitor</a> 
- <a href="https://www.youtube.com/channel/UCpOlOeQjj7EsVnDh3zuCgsA">Eletrônica Já!</a> 
- <a href="https://www.youtube.com/channel/UClT57Bq5io_kABtihe67C7w">All Electronics</a> 
- <a href="http://www.usinainfo.com.br/">Usinainfo</a> 
- <a href="https://www.robocore.net/">Robocore</a> 
- <a href="https://www.youtube.com/channel/UCpOlOeQjj7EsVnDh3zuCgsA">Adafruit Industries</a> 
- <a href="https://www.youtube.com/channel/UCVHFbqXqoYvEWM1Ddxl0QDgw">Android Developers</a> 