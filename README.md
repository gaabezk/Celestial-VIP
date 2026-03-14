# Celestial VIP

O **Celestial VIP** é um plugin de gerenciamento de VIP para servidores **Paper/Spigot/Bukkit**.
Ele permite gerar/resgatar VIPs e Cash via chaves ou transações (Mercado Pago), executa comandos automáticos ao ativar/vencer VIPs, e disponibiliza placeholders para mostrar informações do VIP em scoreboards, tablist, etc.

---

## ✅ Recursos principais

- Resgatar **VIP** ou **Cash** via **código de pagamento (Mercado Pago)**
- Gerar chaves de ativação VIP/Cash (com validade configurável)
- Ativar/Desativar VIP automaticamente ao expirar
- Ver informações do VIP (dias restantes, data de expiração, status)
- Integração opcional com **PlaceholderAPI**
- Configuração via `config.yml` (prefixo, mensagens, anúncios, banco de dados, taxas, etc.)
- Suporte para **SQLite / MySQL / PostgreSQL**
- Logs coloridos no console (quando o servidor suporta ANSI)

---

## 🧰 Requisitos

- Java 17+ (compatível com servidores 1.19.X a 1.21.X)
- Servidor **Paper/Spigot/Bukkit** (API 1.19+)
- (Opcional) **PlaceholderAPI** para usar placeholders nas mensagens

---

## 🚀 Instalação

1. Baixe o `celestialvip.jar` da página de releases do GitHub.
2. Coloque o arquivo em `plugins/` do seu servidor.
3. Reinicie o servidor.

> ⚠️ Se você estiver usando **SQLite**, o plugin criará o banco automaticamente em `plugins/CelestialVIP/`.

---

## 🧠 Comandos

### Usuário

- `/resgatar <vip/cash> <id_transacao>` — Resgata VIP ou Cash usando o ID do pagamento (Mercado Pago).
- `/usarchave <vip/cash> <chave>` — Usa uma chave de ativação já gerada.
- `/infovip [jogador]` — Mostra todas as informações de VIP do jogador (grupo, dias restantes, data de expiração, etc.).

### Admin

- `/gerarchave vip <grupo> <dias/perm>` — Gera uma chave de VIP (com dias ou permanente).
- `/gerarchave cash <quantia>` — Gera uma chave de Cash.
- `/listarchaves <vip/cash>` — Lista todas as chaves VIP/Cash ativas e clicáveis (para copiar).
- `/apagarchave <vip/cash> <chave/all>` — Deleta uma chave específica ou todas as chaves ativas daquele tipo.
- `/darvip <jogador> <grupo> <dias/perm>` — Dá VIP manualmente a um jogador (com dias ou permanente).
- `/removervip <jogador> <grupo>` — Remove um grupo VIP específico do jogador e executa os comandos de expiração.
- `/celestialvip reload` — Recarrega configurações (`config.yml`, conexões de banco e tarefas agendadas).
- `/celestialvip check` — Executa imediatamente a checagem de VIPs expirados (mesma lógica do scheduler).
- `/celestialvip renew <jogador> <grupo> <dias>` — Renova manualmente o VIP de um jogador, estendendo a data de expiração.
- `/celestialvip listvips` — Lista todos os VIPs ativos no servidor, com grupo, expiracão e dias restantes.

---

## 🔒 Permissões

- `celestialvip.user` — Permite usar `/resgatar`, `/usarchave`, `/infovip`.
- `celestialvip.adm` — Permite todos os comandos administrativos (`/gerarchave`, `/listarchaves`, `/darvip`, etc.).
- Permissões específicas (opcionais):
  - `celestialvip.redeem`
  - `celestialvip.usekey`
  - `celestialvip.infvip`
  - `celestialvip.genkey`
  - `celestialvip.listkeys`
  - `celestialvip.delkey`
  - `celestialvip.givip`
  - `celestialvip.remvip`
  - `celestialvip.reload`

---

## ⚙️ Configuração (`config.yml`)

O arquivo de configuração possui opções importantes, como o prefixo de mensagens, intervalo de checagem de VIPs, cache de consultas e integrações.

### Exemplo de configuração (trecho)

```yaml
config:
  prefix: '&6[CelestialVIP]'
  # timezone para expiração de VIPs
  timezone: 'America/Sao_Paulo'
  # intervalo (segundos) da checagem automática de VIPs
  vip-expiration-check-interval: 1800
  # tempo de cache (segundos) para consultas de VIP
  cache-duration-seconds: 10

  # backup automático (apenas SQLite)
  backup:
    enabled: true
    interval-hours: 24

  announce:
    active: true
    # tipos: chat, actionbar, title
    type: 'chat'
    title:
      title: '%player% se tornou um'
      subtitle: '%tag%'
    chat-and-actionbar:
      message: '&a%player% se tornou um %tag%'

  key-size: 15

  database:
    # 'mysql', 'postgresql' ou 'sqlite'
    drive: 'sqlite'
    host: 'localhost'
    port: '3306'
    database: 'teste'
    user: 'root'
    password: 'password'
    tb_prefix: 'celestialvip_'

  mercadopago:
    CLIENT_ID: 'CLIENT_ID'
    CLIENT_SECRET: 'CLIENT_SECRET'

  # marcadores usados nos comandos de ativação/expiração
  # %player%, %tag%, %group%, %days%, %value%
  vips:
    diamond:
      tag: '&bVIP DIAMOND'
      # usado pelo /celestialvip renew
      price-per-day: 5
      renewable: true
      after-expiration-commands:
        - '[console] lp user %player% parent remove %group%'
      activation-commands:
        - '[console] lp user %player% parent add %group%'
        - '[console] eco give %player% 1000'
        - '[sound] ENTITY_PLAYER_LEVELUP'

    gold:
      tag: '&eVIP GOLD'
      after-expiration-commands:
        - '[console] lp user %player% parent remove %group%'
      activation-commands:
        - '[console] lp user %player% parent add %group%'
        - '[console] eco give %player% 500'
        - '[sound] ENTITY_PLAYER_LEVELUP'

    iron:
      tag: '&2VIP IRON'
      after-expiration-commands:
        - '[console] lp user %player% parent remove %group%'
      activation-commands:
        - '[console] lp user %player% parent add %group%'
        - '[console] eco give %player% 100'
        - '[sound] ENTITY_PLAYER_LEVELUP'

  # Use %value% como parâmetro para a quantidade de cash
  cash:
    activation-commands:
      - '[console] cash set %player% %value%'
      - '[message] voce comprou %value% em cash'
      - '[sound] ENTITY_PLAYER_LEVELUP'
```

### Dicas rápidas

- `cache-duration-seconds`: reduzindo este valor, o plugin consulta o banco mais frequentemente (mais atual), mas pode aumentar a carga.
- `vip-expiration-check-interval`: define com que frequência o plugin valida VIPs vencidos (em segundos).

---

## 🧩 Placeholders (PlaceholderAPI)

> Para os placeholders funcionarem, instale o plugin **PlaceholderAPI** e reinicie o servidor.

| Placeholder | Descrição |
|------------|-----------|
| `%celestialvip_status%` | Status do VIP (Ativo/Inativo) |
| `%celestialvip_group%` | Nome do grupo VIP |
| `%celestialvip_timeleft%` | Dias restantes (ou "Permanente") |
| `%celestialvip_daysleft%` | Dias restantes (número) |
| `%celestialvip_hoursleft%` | Horas restantes (número) |
| `%celestialvip_expirationdate%` | Data de expiração (dd/MM/yyyy) |
| `%celestialvip_creationdate%` | Data de ativação (dd/MM/yyyy) |
| `%celestialvip_ispermanent%` | “true” se VIP for permanente |
| `%celestialvip_isactive%` | “true” se VIP estiver ativo |
| `%celestialvip_lastupdate%` | Timestamp do último carregamento de cache |

### Usando placeholders para grupos específicos

Você pode obter valores de um grupo VIP específico adicionando `_nomeDoGrupo` ao final do placeholder:

- `%celestialvip_timeleft_diamond%`
- `%celestialvip_status_vip%`

> Quando nenhum VIP está ativo, a maioria dos placeholders retorna valores padrão (ex: `0`, `Nenhum`, `N/A`).

---

## 🧪 Compilando do código-fonte

### Maven instalado

Se você já tiver o Maven configurado no PATH:

```bash
mvn clean package
```

O JAR gerado ficará em `target/`.

---

## Contribuição

Se você encontrar um bug ou quiser contribuir para o desenvolvimento deste plugin, abra uma issue ou envie uma pull request no GitHub.

---

## Licença

Este plugin é distribuído sob a licença MIT.
