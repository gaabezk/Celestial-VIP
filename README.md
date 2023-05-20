# Celestial VIP

O Celestial VIP é um plugin que oferece recursos de VIP, incluindo a possibilidade de
resgatar VIP ou Cash, gerar chaves de ativação, monitorar o tempo restante do VIP, entre outros...

## Recursos

- Integração com Mercado Pago;
- Resgate de VIP ou Cash com um código;
- Remoção de VIP automatica.
- Gerar chaves de ativação para VIP ou Cash;
- Uso de chaves existentes;
- Mostrar os dias restantes do VIP para jogadores VIP;
- Mostrar os dias restantes do VIP de um jogador específico para administradores;
- Listar todas as chaves ativas;
- Apagar uma chave específica;
- Tirar VIP de um jogador específico;
- Avisar quando um jogador ativa um VIP (chat, actionbar, title ou bossbar);

## Comandos e Permissões

- `/resgatar vip/cash <codigo>` - resgatar o VIP ou Cash com o código de pagamento - `celestialvip.redeem`.
- `/usarchave vip/cash <chave>` - usar uma chave existente para ativar o VIP ou Cash- `celestialvip.usekey`.
- `/infovip` - mostrar os dias restantes do VIP do jogador atual - `celestialvip.infvip`.
- `/infovip <jogador>` - mostrar os dias restantes do VIP de um jogador especifico - `celestialvip.infvip`.
- `/gerarchave vip <vip> <dias>` - gerar uma chave de ativação do VIP com o número especificado de dias - `celestialvip.genkey`.
- `/gerarchave cash <qtd>` - gerar uma chave de ativação de um valor específico de Cash - `celestialvip.genkey`.
- `/listarchaves vip/cash` - listar todas as chaves ativas - `celestialvip.listkeys`.
- `/apagarchave vip/cash <chave>` - apagar uma chave específica - `celestialvip.delkey`.
- `/apagarchave vip/cash all` - apagar todas as chaves ativas - `celestialvip.delkey`.
- `/darvip <jogador> <vip> <dias>/perm` - torna um jogador especifico em VIP - `celestialvip.givip`.
- `/removervip <jogador> <grupo>` - tirar o VIP de um jogador específico - `celestialvip.remvip`.
- `/celestialvip reload` - recarregar todas as configurações do plugin Celestial VIP - `celestialvip.reload`.

## Permissões

- `celestialvip.user` - Inclui `/resgatar`, `/usarchave` e `/infovip`.
- `celestialvip.adm` - Inclui todos os outros comandos.



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

- `prefix`: adiciona um prefixo às mensagens do plugin.
- `announce`: configura como as ativações dos vips serão anunciadas. É possível escolher um ou mais tipos de anúncio,
  como chat, actionBar ou title.
- `key-size`: define o tamanho das chaves usadas para ativar vips.
- `database`: escolhe um tipo de banco de dados (sqlite, mysql ou postgresql) e define as credenciais necessárias para
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
