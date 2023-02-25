package org.clsa;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MarketDataProcessor {
    private static final int MAX_CALLS_PER_SECOND = 100;
    private static final Duration SLIDING_WINDOW_DURATION = Duration.ofSeconds(1);
    private Map<String, MarketData> latestMarketData = new HashMap<>();
    private Map<String, Integer> callsPerSecondMap = new HashMap<>();
    private Instant slidingWindowStart = Instant.now();
    private static final Duration SLEEP_DURATION = Duration.ofMillis(10);

    public void onMessage(MarketData data) {
        // Check if symbol has already been processed in this sliding window
        Instant now = Instant.now();
        if (now.isBefore(slidingWindowStart.plus(SLIDING_WINDOW_DURATION))) {
            Integer calls = callsPerSecondMap.getOrDefault(data.getSymbol(), 0);
            System.out.println("IF calls per sec " + calls);
            if (calls >= MAX_CALLS_PER_SECOND) {
                return; // Limit exceeded, ignore this data
            }
            callsPerSecondMap.put(data.getSymbol(), calls + 1);
        } else {
            Integer calls = callsPerSecondMap.getOrDefault(data.getSymbol(), 0);
            System.out.println("ELSE calls per sec " + calls);
            slidingWindowStart = now;
            callsPerSecondMap.clear();
            callsPerSecondMap.put(data.getSymbol(), 1);
        }

        // Check if market data for this symbol is newer than the latest data
        MarketData latestData = latestMarketData.get(data.getSymbol());
        if (latestData == null || data.getUpdateTime().isAfter(latestData.getUpdateTime())) {
            latestMarketData.put(data.getSymbol(), data);
            publishAggregatedMarketData(data);
        }
    }

    public static void main(String args[]) throws InterruptedException {

        MarketDataProcessor processor = new MarketDataProcessor();
        int numMessages = 1000;
        double initialPrice = 100.0;
        double priceIncrement = 0.1;
        String symbol = "ABC";

        // Send market data messages at a rate of 50 per second
        for (int i = 0; i < numMessages; i++) {
            Instant now = Instant.now();
            MarketData data = new MarketData(symbol, initialPrice + i * priceIncrement, now);
            processor.onMessage(data);
            Thread.sleep(SLEEP_DURATION.toMillis());
        }

    }

    public void publishAggregatedMarketData(MarketData data) {
        // Implementation for publishing data to other applications
    }
}

class MarketData {
    private String symbol;
    private double price;
    private Instant updateTime;

    public MarketData(String symbol, double price, Instant updateTime) {
        this.symbol = symbol;
        this.price = price;
        this.updateTime = updateTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }
}
