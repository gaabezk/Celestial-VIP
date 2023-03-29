# Celestial VIP

O Celestial VIP é um plugin para servidores de Minecraft que oferece recursos de VIP, incluindo a possibilidade de
resgatar VIP ou Cash, gerar chaves de ativação e monitorar o tempo restante do VIP.

## Recursos

- Integração com Mercado Pago;
- Resgate de VIP ou Cash com um código;
- Gerar chaves de ativação para VIP ou Cash;
- Uso de chaves existentes;
- Mostrar os dias restantes do VIP para jogadores VIP;
- Mostrar os dias restantes do VIP de um jogador específico para administradores;
- Listar todas as chaves ativas;
- Apagar uma chave específica;
- Tirar VIP de um jogador específico;
- Avisar quando um jogador ativa um VIP (chat, actionbar, title ou bossbar);
- Remoção de VIP automatica.

## Comandos

- `/resgatar <vip/cash> <codigo>` - resgatar o VIP ou Cash com o código de pagamento;
- `/usarchave <vip/cash> <chave>` - usar uma chave existente para ativar o VIP ou Cash;
- `/tempovip` - mostrar os dias restantes do VIP do jogador atual;
- `/tempovip <jogador>` - mostrar os dias restantes do VIP do jogador especificado;
- `/gerarkeyvip <vip> <dias>` - gerar uma chave de ativação do VIP com o número especificado de dias;
- `/gerarkeycash <qtd>` - gerar uma chave de ativação de um valor específico de Cash;
- `/chaves` - listar todas as chaves ativas;
- `/apagarchave <chave>` - apagar uma chave específica;
- `/apagarchaves` - apagar todas as chaves ativas (necessario confirmar).
- `/tirarvip <jogador>` - tirar o VIP de um jogador específico;
- `/celestialvip reload` - recarregar todas as configurações do plugin Celestial VIP;

## Instalação

1. Baixe o arquivo celestialvip.jar do releases do GitHub.
2. Coloque o arquivo celestialvip.jar na pasta de plugins do seu servidor Minecraft.
3. Reinicie o servidor.

## Configuração

O plugin CelestialVIP oferece várias opções de configuração. É possível alterar a linguagem usada, definir um prefixo
para as mensagens do plugin, escolher como as ativações dos vips são anunciadas, definir o tamanho das chaves usadas
para ativar vips, escolher um tipo de banco de dados, e configurar os vips e a quantidade de cash que será dada ao
jogador ao ativar um vip.

### Opções disponíveis

- `language`: define a linguagem usada pelo plugin. As opções disponíveis atualmente são pt-BR (português brasileiro) e
  en-US (inglês).
- `prefix`: adiciona um prefixo às mensagens do plugin.
- `announce`: configura como as ativações dos vips serão anunciadas. É possível escolher um ou mais tipos de anúncio,
  como chat, actionBar, título ou bossbar.
- `key-size`: define o tamanho das chaves usadas para ativar vips.
- `database`: escolhe um tipo de banco de dados (mysql ou postgresql) e define as credenciais necessárias para
  acessá-lo.
- `mercadopago`: configura as credenciais de acesso ao MercadoPago.
- `vips`: define as características de cada vip, como o nome do grupo, a tag usada, se expira ou não, os comandos a
  serem executados após a expiração e os comandos a serem executados na ativação.
- `cash`: define os comandos a serem executados na ativação do cash.

## Contribuição

Se você encontrar um bug ou quiser contribuir para o desenvolvimento deste plugin, sinta-se à vontade para enviar uma
pull request ou abrir uma issue no repositório GitHub.

## Licença

Este plugin é distribuído sob a licença MIT.
