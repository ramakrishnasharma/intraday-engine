# Intraday Alert Engine

A real-time intraday trading **signal and alert engine** for NSE equities, built on Zerodha's Kite Connect WebSocket API. It streams live ticks, builds multi-timeframe candles, evaluates a pipeline of strategies, ranks and risk-filters the resulting signals, and pushes alerts to Telegram.

**This is a signal-only tool — it does not place orders.** It's a personal project for scanning the market and generating actionable alerts, not an automated trading bot.

## How it works

```
Zerodha WebSocket ticks
        │
        ▼
  TickQueueEngine ──► TickProcessor ──► MultiTimeframeEngine (candle aggregation)
        │
        ▼
  StrategyPipeline ──► StrategyEngine
        │   (Momentum / Opening Range Breakout / VWAP Reversal)
        ▼
    ScoreEngine ──► RankingEngine (top-N signals)
        │
        ▼
  Risk filters (ProfitabilityFilter, TradeCostModel, PositionSizer)
        │
        ▼
  AlertDispatcher ──► AsyncTelegramNotifier ──► Telegram
```

`SignalRouter` ties the above together per tick, alongside supporting engines for position tracking, trailing stops, cooldowns between repeat alerts, and index/sector context.

## Features

- **Multi-timeframe candle building** from raw tick data (`CandleBuilder`, `MultiTimeframeEngine`)
- **Strategy pipeline** — Momentum, Opening Range Breakout, and VWAP Reversal strategies evaluated per candle
- **Indicators** — ATR, VWAP, Relative Strength, Volume Average
- **Filters** — Liquidity, Trend, Relative Strength, Volume Spike detection
- **Market regime detection** (trending vs. ranging) to adapt strategy behavior
- **Sector & index tracking** — per-sector and index-relative context for signals
- **Risk management** — position sizing (capped risk-per-trade and max capital allocation), round-trip trading cost modeling (brokerage/STT/exchange/GST), profitability filtering, trailing stop management
- **Cooldown engine** to prevent repeat alerts on the same symbol within a window
- **Async, non-blocking Telegram alert dispatch**
- **Market simulation / E2E test harness** — mock tick generator and NSE market simulator for testing strategies without live capital or market hours

## Tech stack

- Java 11, Maven
- [Zerodha Kite Connect](https://kite.trade/docs/kiteconnect/) Java SDK (WebSocket streaming + REST)
- Telegram Bot API (alert delivery)

## Project structure

```
src/main/java/com/alertengine/
├── app/            entry point (MainApp)
├── broker/         Zerodha WebSocket client
├── candle/         tick-to-candle aggregation
├── config/         properties-based config loader
├── engine/         core pipeline: routing, scoring, ranking, position/cooldown/trailing-stop management
├── filter/         signal pre-filters (liquidity, trend, volume spike, relative strength)
├── indicator/      technical indicators (ATR, VWAP, volume average, volatility)
├── model/          domain objects (Candle, Signal, Position, TickData)
├── notification/   Telegram alert dispatch (sync + async)
├── risk/           position sizing, trade cost model, profitability filter
├── router/         SignalRouter — wires ticks through the full pipeline
├── strategy/       Momentum, Opening Range Breakout, VWAP Reversal strategies
├── test/           market simulation & E2E test harness
├── util/           market time utilities
└── watchlist/      instrument token loading, sector/index registries
```

## Setup

1. **Clone the repo**

   ```bash
   git clone <repo-url>
   cd intraday-alert-engine
   ```

2. **Configure credentials** — copy the example config and fill in your own values:

   ```bash
   cp src/main/resources/config.properties.example src/main/resources/config.properties
   ```

   | Property | Description |
   |---|---|
   | `api.key` / `api.secret` | Zerodha Kite Connect app credentials |
   | `access.token` | Kite Connect daily access token (from the login flow) |
   | `telegram.token` | Telegram bot token (from [@BotFather](https://t.me/BotFather)) |
   | `telegram.chatId` | Telegram chat ID to receive alerts |

   `config.properties` is git-ignored — **never commit real credentials.**

3. **Build**

   ```bash
   mvn clean package
   ```

4. **Run**

   ```bash
   mvn exec:java -Dexec.mainClass="com.alertengine.app.MainApp"
   ```

   The engine loads the watchlist and sector map, connects to Zerodha's WebSocket, and starts streaming alerts to Telegram during NSE market hours.

## Disclaimer

Built for personal use and learning. This is a signal/alerting tool only — no order placement — and is **not financial advice**. Use at your own risk during live market hours.

