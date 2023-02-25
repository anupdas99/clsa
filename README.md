# clsa
 Market Data Throttle Control Approach
1. Define a sliding window duration, which is the time interval within which MarketData for a symbol must be processed.
2. Create a HashMap to keep track of the number of calls per second for each symbol.
3. Check if the MarketData already been processed within the sliding window period.
4. If the number of calls for the symbol is less than the maximum allowed calls per second, increment the count for the symbol and continue.
5. If the number of calls for the symbol is greater than 100, ignore the data.
6. If the MarketData for the symbol is latest, update latest MarketData and publish the aggregated MarketData

Test Cases
1. To test, I have created sample 1000 message, out of 1000, around 50 messages will be called per second to process.
2. To test maximum count 100, need to reduce the SLEEP_DURATION = 5 or less or just comment the Thread sleep line. 
   Then per second more than 100 messages will be called and onMessage won't process.

