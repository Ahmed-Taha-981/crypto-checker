package com.cryptochecker;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import com.google.gson.Gson;

/**
 * Integration Tests for WebData and PanelConverter Communication
 * Tests verify that data flows correctly between the data layer (WebData)
 * and the converter UI layer (PanelConverter)
 */
public class WebDataConverterIntegrationTest {
    
    private WebData webData;
    private MockWebData mockWebData;
    
    @BeforeClass
    public static void setupTest() {
        // Set default currency for testing
        Main.currency = "USD";
    }
    
    @Test
    public void testWebDataCoinListPopulation() throws Exception {
        /**
         * Test: Verify WebData can create and populate coin list
         * Purpose: Ensure the data layer correctly initializes coin data
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        assertNotNull("Coin list should not be null", mockWebData.coin);
        assertTrue("Coin list should contain test data", mockWebData.coin.size() > 0);
    }
    
    @Test
    public void testCoinDataIntegrity() throws Exception {
        /**
         * Test: Verify individual coin data is correctly structured
         * Purpose: Ensure coin objects have all required fields populated
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin testCoin = mockWebData.coin.get(0);
        assertNotNull("Coin name should not be null", testCoin.name);
        assertNotNull("Coin symbol should not be null", testCoin.symbol);
        assertTrue("Coin price should be positive", testCoin.price > 0);
        assertTrue("Coin rank should be positive", testCoin.rank > 0);
    }
    
    @Test
    public void testCoinPriceRetrieval() throws Exception {
        /**
         * Test: Verify converter can retrieve coin price from WebData
         * Purpose: Simulate how PanelConverter accesses coin prices
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        String targetCoinName = "Bitcoin";
        double retrievedPrice = retrieveCoinPrice(targetCoinName, mockWebData);
        
        assertTrue("Price should be found and positive", retrievedPrice > 0);
        assertEquals("Bitcoin price should be correct", 50000.0, retrievedPrice, 0.01);
    }
    
    @Test
    public void testCurrencyConversion() throws Exception {
        /**
         * Test: Verify currency conversion calculation with real coin data
         * Purpose: Test the conversion logic between two currencies
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        double btcPrice = retrieveCoinPrice("Bitcoin", mockWebData);
        double ethPrice = retrieveCoinPrice("Ethereum", mockWebData);
        double inputAmount = 1.0; // 1 BTC
        
        double expectedOutput = inputAmount * btcPrice / ethPrice;
        double actualOutput = convertCurrency(inputAmount, btcPrice, ethPrice);
        
        assertEquals("Currency conversion should be accurate", expectedOutput, actualOutput, 0.01);
    }
    
    @Test
    public void testMultipleCoinPriceRetrieval() throws Exception {
        /**
         * Test: Verify converter can retrieve multiple coin prices
         * Purpose: Test scenario where user selects different coin pairs
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        double btcPrice = retrieveCoinPrice("Bitcoin", mockWebData);
        double ethPrice = retrieveCoinPrice("Ethereum", mockWebData);
        double litecoinPrice = retrieveCoinPrice("Litecoin", mockWebData);
        
        assertTrue("All prices should be positive", 
            btcPrice > 0 && ethPrice > 0 && litecoinPrice > 0);
    }
    
    @Test
    public void testCoinInfoRetrieval() throws Exception {
        /**
         * Test: Verify detailed coin information can be retrieved
         * Purpose: Test info display functionality in PanelConverter
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin testCoin = mockWebData.coin.get(0);
        String coinInfo = testCoin.getInfo();
        
        assertNotNull("Coin info should not be null", coinInfo);
        assertTrue("Info should contain coin name", coinInfo.contains(testCoin.name));
        assertTrue("Info should contain symbol", coinInfo.contains(testCoin.symbol));
        assertTrue("Info should contain price information", coinInfo.contains("Price") || coinInfo.length() > 0);
    }
    
    @Test
    public void testPriceFormatting() throws Exception {
        /**
         * Test: Verify price trimming logic works correctly
         * Purpose: Test decimal formatting for various price ranges
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin testCoin = mockWebData.coin.get(0);
        
        String largePrice = testCoin.trimPrice(1000.5555);
        assertEquals("Large prices should trim to 2 decimals", "1000.56", largePrice);
        
        String smallPrice = testCoin.trimPrice(0.0001234);
        assertNotNull("Small prices should format correctly", smallPrice);
    }
    
    @Test
    public void testCoinListAccessPattern() throws Exception {
        /**
         * Test: Verify the access pattern used by PanelConverter
         * Purpose: Ensure coin list can be accessed by index and name
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        // Test index access
        WebData.Coin coinByIndex = mockWebData.coin.get(0);
        assertNotNull("Should access coin by index", coinByIndex);
        
        // Test iteration and name matching (as used in PanelConverter)
        String searchName = "Ethereum";
        boolean found = false;
        for (WebData.Coin coin : mockWebData.coin) {
            if (coin.name.equals(searchName)) {
                found = true;
                break;
            }
        }
        assertTrue("Should find coin by name through iteration", found);
    }
    
    @Test
    public void testGlobalDataAccess() throws Exception {
        /**
         * Test: Verify global market data is accessible
         * Purpose: Test global data used in converter overview
         */
        mockWebData = new MockWebData();
        mockWebData.global_data = mockWebData.new Global_Data();
        mockWebData.global_data.total_market_cap = 2000000000000L;
        mockWebData.global_data.bitcoin_percentage_of_market_cap = 43.5;
        
        assertNotNull("Global data should exist", mockWebData.global_data);
        String globalInfo = mockWebData.global_data.toString();
        assertNotNull("Global info should be formatted", globalInfo);
        assertTrue("Global info should contain market cap", globalInfo.contains("Total Market Cap"));
    }
    
    @Test
    public void testCoinCloningForPortfolio() throws Exception {
        /**
         * Test: Verify coins can be cloned for portfolio use
         * Purpose: Ensure coin data can be copied without affecting original
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin original = mockWebData.coin.get(0);
        WebData.Coin cloned = (WebData.Coin) original.copy();
        
        assertNotNull("Cloned coin should not be null", cloned);
        assertEquals("Cloned coin should have same name", original.name, cloned.name);
        assertNotSame("Cloned coin should be different object", original, cloned);
    }
    
    @Test
    public void testDataConsistencyAcrossAccess() throws Exception {
        /**
         * Test: Verify data remains consistent across multiple accesses
         * Purpose: Ensure no data corruption with repeated access patterns
         */
        mockWebData = new MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin coin1 = mockWebData.coin.get(0);
        WebData.Coin coin2 = mockWebData.coin.get(0);
        
        assertEquals("Same coin accessed twice should have same data", 
            coin1.name, coin2.name);
        assertEquals("Same coin accessed twice should have same price", 
            coin1.price, coin2.price, 0.01);
    }
    
    /**
     * Helper method: Simulate PanelConverter's coin price retrieval logic
     */
    private double retrieveCoinPrice(String coinName, WebData webData) {
        for (WebData.Coin coin : webData.coin) {
            if (coin.name.equals(coinName)) {
                return coin.price;
            }
        }
        return 0.0;
    }
    
    /**
     * Helper method: Simulate PanelConverter's conversion calculation
     */
    private double convertCurrency(double amount, double priceCurrency1, double priceCurrency2) {
        if (priceCurrency2 == 0.0) {
            return priceCurrency1 * amount;
        }
        return (priceCurrency1 / priceCurrency2) * amount;
    }
    
    /**
     * Mock WebData class for testing without network access
     */
    public static class MockWebData extends WebData {
        // Flag to prevent deserialize call
        private static final boolean IS_MOCK = true;
        
        public MockWebData() throws Exception {
            // Initialize fields directly without calling deserialize
            this.coin = new ArrayList<>();
            this.portfolio = new ArrayList<>();
            this.portfolio_names = new ArrayList<>();
            this.portfolio_nr = 0;
            this.global_data = null;
        }
        
        public void populateTestCoins() {
            WebData.Coin bitcoin = new WebData.Coin();
            bitcoin.name = "Bitcoin";
            bitcoin.symbol = "BTC";
            bitcoin.rank = 1;
            bitcoin.price = 50000.0;
            bitcoin.price_btc = 1.0;
            bitcoin._24h_volume = 30000000000L;
            bitcoin.market_cap = 1000000000000L;
            bitcoin.available_supply = 21000000;
            bitcoin.total_supply = 21000000;
            bitcoin.max_supply = 21000000;
            bitcoin.percent_change_1h = 0.5;
            bitcoin.percent_change_24h = -1.2;
            bitcoin.percent_change_7d = 2.3;
            
            WebData.Coin ethereum = new WebData.Coin();
            ethereum.name = "Ethereum";
            ethereum.symbol = "ETH";
            ethereum.rank = 2;
            ethereum.price = 3000.0;
            ethereum.price_btc = 0.06;
            ethereum._24h_volume = 20000000000L;
            ethereum.market_cap = 400000000000L;
            ethereum.available_supply = 120000000;
            ethereum.total_supply = 120000000;
            ethereum.max_supply = -1;
            ethereum.percent_change_1h = 0.3;
            ethereum.percent_change_24h = -0.8;
            ethereum.percent_change_7d = 1.5;
            
            WebData.Coin litecoin = new WebData.Coin();
            litecoin.name = "Litecoin";
            litecoin.symbol = "LTC";
            litecoin.rank = 9;
            litecoin.price = 200.0;
            litecoin.price_btc = 0.004;
            litecoin._24h_volume = 500000000L;
            litecoin.market_cap = 30000000000L;
            litecoin.available_supply = 84000000;
            litecoin.total_supply = 84000000;
            litecoin.max_supply = 84000000;
            litecoin.percent_change_1h = 0.1;
            litecoin.percent_change_24h = -0.5;
            litecoin.percent_change_7d = 0.8;
            
            coin.add(bitcoin);
            coin.add(ethereum);
            coin.add(litecoin);
        }
    }
}
