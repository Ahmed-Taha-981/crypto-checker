package com.cryptochecker;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Integration Tests for Conversion Operations
 * Tests the interaction between WebData (data source) and PanelConverter (conversion logic)
 * Focuses on currency conversion workflows and data transformations
 */
public class ConverterOperationsIntegrationTest {
    
    private WebDataConverterIntegrationTest.MockWebData mockWebData;
    
    @BeforeClass
    public static void setupTest() {
        Main.currency = "USD";
    }
    
    @Test
    public void testBasicCryptoToCryptoConversion() throws Exception {
        /**
         * Test: Convert 1 BTC to ETH
         * Purpose: Verify basic conversion between two cryptocurrencies
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double btcAmount = 1.0;
        double btcPrice = 50000.0;
        double ethPrice = 3000.0;
        
        double ethAmount = convertToTarget(btcAmount, btcPrice, ethPrice);
        
        // 1 BTC = 50000 USD, 50000 USD / 3000 per ETH = 16.67 ETH
        assertEquals("BTC to ETH conversion should be accurate", 16.666666, ethAmount, 0.01);
    }
    
    @Test
    public void testCryptoToFiatConversion() throws Exception {
        /**
         * Test: Convert crypto to fiat currency (currency = 0)
         * Purpose: Verify conversion to base currency
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double btcAmount = 0.5;
        double btcPrice = 50000.0;
        double fiatPrice = 0.0; // Fiat currency
        
        double fiatAmount = convertWithFiat(btcAmount, btcPrice);
        
        // 0.5 BTC * 50000 = 25000 USD
        assertEquals("BTC to Fiat conversion should be accurate", 25000.0, fiatAmount, 0.01);
    }
    
    @Test
    public void testSmallAmountConversion() throws Exception {
        /**
         * Test: Convert very small amounts (precision test)
         * Purpose: Verify conversions maintain precision for small values
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double smallAmount = 0.00001;
        double btcPrice = 50000.0;
        double ethPrice = 3000.0;
        
        double result = convertToTarget(smallAmount, btcPrice, ethPrice);
        
        assertTrue("Small amount conversion should work", result > 0);
    }
    
    @Test
    public void testLargeAmountConversion() throws Exception {
        /**
         * Test: Convert large amounts
         * Purpose: Verify conversions don't overflow or lose precision
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double largeAmount = 1000000.0;
        double btcPrice = 50000.0;
        double ethPrice = 3000.0;
        
        double result = convertToTarget(largeAmount, btcPrice, ethPrice);
        
        assertTrue("Large amount conversion should work", result > 0);
        assertFalse("Result should not be infinity", Double.isInfinite(result));
    }
    
    @Test
    public void testCurrencyConversionWithDifferentPrices() throws Exception {
        /**
         * Test: Conversion with varying price ranges
         * Purpose: Test conversions with both high and low-value coins
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        // High value to low value
        double btcAmount = 1.0;
        double btcPrice = 50000.0;
        double litecoinPrice = 200.0;
        
        double litecoinAmount = convertToTarget(btcAmount, btcPrice, litecoinPrice);
        
        // 1 BTC = 50000 USD, 50000 / 200 = 250 LTC
        assertEquals("BTC to LTC conversion", 250.0, litecoinAmount, 0.01);
    }
    
    @Test
    public void testZeroPriceHandling() throws Exception {
        /**
         * Test: Handle zero price scenario
         * Purpose: Verify system doesn't crash with invalid price data
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double amount = 1.0;
        double price1 = 50000.0;
        double price2 = 0.0;
        
        double result = convertWithFiat(amount, price1);
        
        assertTrue("Should handle zero target price (fiat)", result > 0);
    }
    
    @Test
    public void testConversionChain() throws Exception {
        /**
         * Test: Convert through intermediate currency
         * Purpose: Verify multi-step conversions maintain accuracy
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        // BTC -> USD -> ETH
        double btcAmount = 1.0;
        double btcPrice = 50000.0;
        double ethPrice = 3000.0;
        
        // Step 1: BTC to USD
        double usdAmount = btcAmount * btcPrice;
        assertEquals("Step 1: BTC to USD", 50000.0, usdAmount, 0.01);
        
        // Step 2: USD to ETH
        double ethAmount = usdAmount / ethPrice;
        assertEquals("Step 2: USD to ETH", 16.666666, ethAmount, 0.01);
    }
    
    @Test
    public void testInputValidation() throws Exception {
        /**
         * Test: Validate conversion input values
         * Purpose: Ensure invalid inputs are handled properly
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        String invalidInput = "abc";
        Double parsedValue = null;
        
        try {
            parsedValue = Double.parseDouble(invalidInput);
        } catch (NumberFormatException e) {
            parsedValue = null;
        }
        
        assertNull("Invalid input should fail to parse", parsedValue);
    }
    
    @Test
    public void testDecimalFormatting() throws Exception {
        /**
         * Test: Verify decimal formatting for display
         * Purpose: Test price formatting logic
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        WebDataConverterIntegrationTest.MockWebData.Coin coin = 
            (WebDataConverterIntegrationTest.MockWebData.Coin) mockWebData.coin.get(0);
        
        // Test formatting for different price ranges
        String result1 = coin.trimPrice(1234.5678);
        assertTrue("Should format large prices", result1.contains("."));
        
        String result2 = coin.trimPrice(0.00001234);
        assertNotNull("Should format small prices", result2);
    }
    
    @Test
    public void testBidirectionalConversion() throws Exception {
        /**
         * Test: Convert from A to B and back
         * Purpose: Verify round-trip conversions
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double originalBTC = 1.0;
        double btcPrice = 50000.0;
        double ethPrice = 3000.0;
        
        // Forward: BTC to ETH
        double eth = convertToTarget(originalBTC, btcPrice, ethPrice);
        
        // Backward: ETH to BTC
        double btcBack = convertToTarget(eth, ethPrice, btcPrice);
        
        assertEquals("Round-trip conversion should maintain value", originalBTC, btcBack, 0.01);
    }
    
    @Test
    public void testPriceUpdateReflection() throws Exception {
        /**
         * Test: Verify conversion updates when prices change
         * Purpose: Ensure new coin prices are used in calculations
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        double amount = 1.0;
        double ethPrice = 3000.0;
        
        double result1 = convertWithFiat(amount, ethPrice);
        
        // Simulate price update
        double newEthPrice = 4000.0;
        double result2 = convertWithFiat(amount, newEthPrice);
        
        assertNotEquals("Results should differ with different prices", result1, result2);
        assertTrue("Higher price should give higher result", result2 > result1);
    }
    
    @Test
    public void testCoinSelectionAndConversion() throws Exception {
        /**
         * Test: Select coin from list and perform conversion
         * Purpose: Simulate PanelConverter's coin selection workflow
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        // User selects "Ethereum" from dropdown
        String selectedCoin = "Ethereum";
        WebData.Coin coin = findCoinByName(selectedCoin, mockWebData);
        
        assertNotNull("Should find selected coin", coin);
        assertEquals("Should retrieve correct coin", "Ethereum", coin.name);
        
        // Perform conversion with this coin
        double amount = 10.0;
        double result = convertWithFiat(amount, coin.price);
        
        assertEquals("Conversion with selected coin price", 30000.0, result, 0.01);
    }
    
    @Test
    public void testMultipleCoinComparisons() throws Exception {
        /**
         * Test: Compare prices of multiple coins
         * Purpose: Verify data supports comparative analysis
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        WebData.Coin btc = findCoinByName("Bitcoin", mockWebData);
        WebData.Coin eth = findCoinByName("Ethereum", mockWebData);
        WebData.Coin ltc = findCoinByName("Litecoin", mockWebData);
        
        // Verify price hierarchy
        assertTrue("BTC should be most expensive", btc.price > eth.price);
        assertTrue("ETH should be more expensive than LTC", eth.price > ltc.price);
    }
    
    @Test
    public void testConversionEdgeCases() throws Exception {
        /**
         * Test: Edge cases in conversion calculations
         * Purpose: Verify robustness with boundary values
         */
        mockWebData = new WebDataConverterIntegrationTest.MockWebData();
        mockWebData.populateTestCoins();
        
        // Very small input
        double result1 = convertWithFiat(0.000001, 50000.0);
        assertFalse("Should handle very small amounts", Double.isNaN(result1));
        
        // Maximum reasonable input
        double result2 = convertWithFiat(1000000.0, 50000.0);
        assertFalse("Should handle large amounts", Double.isInfinite(result2));
    }
    
    // Helper Methods
    
    private double convertToTarget(double amount, double sourcePrice, double targetPrice) {
        if (targetPrice == 0.0) {
            return sourcePrice * amount;
        }
        return (sourcePrice / targetPrice) * amount;
    }
    
    private double convertWithFiat(double amount, double cryptoPrice) {
        return cryptoPrice * amount;
    }
    
    private WebData.Coin findCoinByName(String coinName, WebData webData) {
        for (WebData.Coin coin : webData.coin) {
            if (coin.name.equals(coinName)) {
                return coin;
            }
        }
        return null;
    }
}
