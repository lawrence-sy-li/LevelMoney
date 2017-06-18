package com.freedomscape.capitalone_tech_interview_2017.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;

import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freedomscape.capitalone_tech_interview_2017.model.GetAllTransactionsResponse;
import com.freedomscape.capitalone_tech_interview_2017.model.SpentIncome;
import com.freedomscape.capitalone_tech_interview_2017.model.Transaction;

/**
 * Some unit tests for the LevelMoneyClient class.
 * 
 * TODO: Could probably do some more comprehensive tests...
 * 
 * @author lsli
 *
 */
@RunWith(EasyMockRunner.class)
public class LevelMoneyClientTests {
	/**
	 * Yeah, a little lazy using nice mocks... May improve later...
	 */
	@Mock(type = MockType.NICE)
	private HttpsURLConnection mockConnection;

	private ObjectMapper objectMapper;

	private LevelMoneyClient levelMoneyClient;

	private GetAllTransactionsResponse getAllTransactionsResponse;
	private GetAllTransactionsResponse expectedGetAllTransactionsResponse = new GetAllTransactionsResponse();

	@Before
	public void setup() throws Exception {
		Properties properties = new Properties();
		properties.load(ClassLoader.getSystemResourceAsStream(LevelMoneyClient.DEFAULT_CONFIG_FILE));
		Long userId = Long.parseLong(properties.getProperty("userId"));
		String authenticationToken = properties.getProperty("authenticationToken");
		String apiToken = properties.getProperty("apiToken");
		String serviceUrl = properties.getProperty("serviceUrl");
		String merchantExclusionsString = properties.getProperty("merchantExclusions");
		List<String> merchantExclusions = Arrays.asList(merchantExclusionsString.split(","));
		boolean ignoreDonuts = false;
		objectMapper = new ObjectMapper();
		levelMoneyClient = new LevelMoneyClient.LevelMoneyClientBuilder().userId(userId)
				.authenticationToken(authenticationToken).apiToken(apiToken).serviceUrl(serviceUrl)
				.ignoreDonuts(ignoreDonuts).merchantExclusions(merchantExclusions).connection(mockConnection)
				.objectMapper(objectMapper).build();
	}

	private void setupErrorData() {
		String errorString = "token-expired";
		expectedGetAllTransactionsResponse = new GetAllTransactionsResponse();
		expectedGetAllTransactionsResponse.setError(errorString);
	}

	private void setupGoodData() {
		// Setup some test data for certain tests
		String errorString = "no-error";
		expectedGetAllTransactionsResponse = new GetAllTransactionsResponse();
		expectedGetAllTransactionsResponse.setError(errorString);
		Transaction transaction1 = new Transaction();
		transaction1.setAmount(-34300);
		transaction1.setPending(false);
		transaction1.setAccountId("nonce:comfy-cc/hdhehe");
		transaction1.setClearDate(1412790480000L);
		transaction1.setTransactionId("1412790480000");
		transaction1.setRawMercant("7-ELEVEN 23853");
		transaction1.setCategorization("Unknown");
		transaction1
				.setTransactionTime(LocalDateTime.ofInstant(Instant.parse("2014-10-07T12:59:00.000Z"), ZoneOffset.UTC));
		Transaction transaction2 = new Transaction();
		transaction2.setAmount(-30200);
		transaction2.setPending(false);
		transaction2.setAccountId("nonce:comfy-cc/hdhehe");
		transaction2.setClearDate(1412985120000L);
		transaction2.setTransactionId("1412985120000");
		transaction2.setRawMercant("SUNOCO 0299792200");
		transaction2.setCategorization("Unknown");
		transaction2
				.setTransactionTime(LocalDateTime.ofInstant(Instant.parse("2014-10-07T17:29:00.000Z"), ZoneOffset.UTC));
		Transaction transaction3 = new Transaction();
		transaction3.setAmount(-99000);
		transaction3.setPending(false);
		transaction3.setAccountId("nonce:comfy-cc/hdhehe");
		transaction3.setClearDate(1412845980000L);
		transaction3.setTransactionId("1412845980000");
		transaction3.setRawMercant("Krispy Kreme Donuts");
		transaction3.setCategorization("Unknown");
		transaction3
				.setTransactionTime(LocalDateTime.ofInstant(Instant.parse("2014-10-08T01:56:00.000Z"), ZoneOffset.UTC));
		Transaction transaction4 = new Transaction();
		transaction4.setAmount(16911700);
		transaction4.setPending(false);
		transaction4.setAccountId("nonce:comfy-checking/hdhehe");
		transaction4.setClearDate(1497119640000L);
		transaction4.setTransactionId("1497119640000");
		transaction4.setRawMercant("ZENPAYROLL");
		transaction4.setCategorization("Paycheck");
		transaction4
				.setTransactionTime(LocalDateTime.ofInstant(Instant.parse("2017-06-09T00:00:00.000Z"), ZoneOffset.UTC));
		transaction4.setMemoOnlyForTesting("Example Memo");
		transaction4.setPayeeNameOnlyForTesting("ZENPAYROLL");

		expectedGetAllTransactionsResponse
				.setTransactions(Arrays.asList(transaction1, transaction2, transaction3, transaction4));
	}

	/**
	 * Test to see a runtime exception is thrown when a non 200 HTTP return code
	 * is returned from the RESTful service.
	 * 
	 * @throws Exception
	 *             We expect a runtime exception thrown here
	 */
	@Test(expected = RuntimeException.class)
	public void testExecuteRequestBadHttpResponse() throws Exception {
		// Recording
		EasyMock.expect(mockConnection.getOutputStream()).andReturn(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
		EasyMock.expect(mockConnection.getResponseCode()).andReturn(HttpURLConnection.HTTP_BAD_REQUEST);
		EasyMock.replay(mockConnection);

		// Test - we expect a runtime exception thrown here
		levelMoneyClient.executeRequest(mockConnection, objectMapper);
	}

	/**
	 * Tests when the RESTful service returns JSON (an error) that indicates it
	 * could not retrieve data...
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecuteRequestErrorResponse() throws Exception {
		// Recording
		EasyMock.expect(mockConnection.getOutputStream()).andReturn(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
		EasyMock.expect(mockConnection.getResponseCode()).andReturn(HttpURLConnection.HTTP_OK);
		String errorString = "token-expired";
		String noResults = "{\"error\": \"" + errorString + "\"}";
		EasyMock.expect(mockConnection.getInputStream()).andReturn(new ByteArrayInputStream(noResults.getBytes()));
		EasyMock.replay(mockConnection);

		// Test
		setupErrorData();
		getAllTransactionsResponse = levelMoneyClient.executeRequest(mockConnection, objectMapper);
		Assert.assertEquals(expectedGetAllTransactionsResponse, getAllTransactionsResponse);
	}

	/**
	 * Simple test where the connection returns a few transaction records. We
	 * just make sure the transactions are deserialized from JSON to a Java
	 * object(s) properly. Here is the expected JSON returned:
	 * 
	 * <pre>
	 * {
	"error": "no-error",
	"transactions": [{
	    "amount": -34300,
	    "is-pending": false,
	    "aggregation-time": 1412686740000,
	    "account-id": "nonce:comfy-cc/hdhehe",
	    "clear-date": 1412790480000,
	    "transaction-id": "1412790480000",
	    "raw-merchant": "7-ELEVEN 23853",
	    "categorization": "Unknown",
	    "merchant": "7-Eleven 23853",
	    "transaction-time": "2014-10-07T12:59:00.000Z"
	}, {
	    "amount": -30200,
	    "is-pending": false,
	    "aggregation-time": 1412702940000,
	    "account-id": "nonce:comfy-cc/hdhehe",
	    "clear-date": 1412985120000,
	    "transaction-id": "1412985120000",
	    "raw-merchant": "SUNOCO 0299792200",
	    "categorization": "Unknown",
	    "merchant": "Sunoco",
	    "transaction-time": "2014-10-07T17:29:00.000Z"
	}, {
	    "amount": -99000,
	    "is-pending": false,
	    "aggregation-time": 1412733360000,
	    "account-id": "nonce:comfy-cc/hdhehe",
	    "clear-date": 1412845980000,
	    "transaction-id": "1412845980000",
	    "raw-merchant": "Krispy Kreme Donuts",
	    "categorization": "Unknown",
	    "merchant": "Krispy Kreme Donuts",
	    "transaction-time": "2014-10-08T01:56:00.000Z"
	}, {
	    "amount": 16911700,
	    "is-pending": false,
	    "payee-name-only-for-testing": "ZENPAYROLL",
	    "aggregation-time": 1496985257003,
	    "account-id": "nonce:comfy-checking/hdhehe",
	    "clear-date": 1497119640000,
	    "memo-only-for-testing": "Example Memo",
	    "transaction-id": "1497119640000",
	    "raw-merchant": "ZENPAYROLL",
	    "categorization": "Paycheck",
	    "merchant": "Zenpayroll",
	    "transaction-time": "2017-06-09T00:00:00.000Z"
	}]}
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecuteRequestSuccess() throws Exception {
		// Recording
		EasyMock.expect(mockConnection.getOutputStream()).andReturn(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
		EasyMock.expect(mockConnection.getResponseCode()).andReturn(HttpURLConnection.HTTP_OK);
		String errorString = "no-error";
		String jsonTransactionString = "\"transactions\":["
				+ "{\"amount\":-34300,\"is-pending\":false,\"aggregation-time\":1412686740000,\"account-id\":\"nonce:comfy-cc/hdhehe\",\"clear-date\":1412790480000,\"transaction-id\":\"1412790480000\",\"raw-merchant\":\"7-ELEVEN 23853\",\"categorization\":\"Unknown\",\"merchant\":\"7-Eleven 23853\",\"transaction-time\":\"2014-10-07T12:59:00.000Z\"}"
				+ ",{\"amount\":-30200,\"is-pending\":false,\"aggregation-time\":1412702940000,\"account-id\":\"nonce:comfy-cc/hdhehe\",\"clear-date\":1412985120000,\"transaction-id\":\"1412985120000\",\"raw-merchant\":\"SUNOCO 0299792200\",\"categorization\":\"Unknown\",\"merchant\":\"Sunoco\",\"transaction-time\":\"2014-10-07T17:29:00.000Z\"}"
				+ ",{\"amount\":-99000,\"is-pending\":false,\"aggregation-time\":1412733360000,\"account-id\":\"nonce:comfy-cc/hdhehe\",\"clear-date\":1412845980000,\"transaction-id\":\"1412845980000\",\"raw-merchant\":\"Krispy Kreme Donuts\",\"categorization\":\"Unknown\",\"merchant\":\"Krispy Kreme Donuts\",\"transaction-time\":\"2014-10-08T01:56:00.000Z\"}"
				+ ",{\"amount\":16911700,\"is-pending\":false,\"payee-name-only-for-testing\":\"ZENPAYROLL\",\"aggregation-time\":1496985257003,\"account-id\":\"nonce:comfy-checking/hdhehe\",\"clear-date\":1497119640000,\"memo-only-for-testing\":\"Example Memo\",\"transaction-id\":\"1497119640000\",\"raw-merchant\":\"ZENPAYROLL\",\"categorization\":\"Paycheck\",\"merchant\":\"Zenpayroll\",\"transaction-time\":\"2017-06-09T00:00:00.000Z\"}]";
		String jsonResults = "{\"error\": \"" + errorString + "\", " + jsonTransactionString + "}";
		EasyMock.expect(mockConnection.getInputStream()).andReturn(new ByteArrayInputStream(jsonResults.getBytes()));
		EasyMock.replay(mockConnection);

		// Test
		setupGoodData();
		getAllTransactionsResponse = levelMoneyClient.executeRequest(mockConnection, objectMapper);
		Assert.assertEquals(expectedGetAllTransactionsResponse, getAllTransactionsResponse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateSummaryNullGetAllTransactionsResponse() {
		// Test
		levelMoneyClient.generateSummary(null, false, null);
	}

	@Test
	public void testGenerateSummaryGoodData() {
		// Test
		setupGoodData();
		Map<LocalDate, SpentIncome> dataSpentIncomeMap = levelMoneyClient
				.generateSummary(expectedGetAllTransactionsResponse, false, Arrays.asList("Krispy Kreme Donuts"));
		Map<LocalDate, SpentIncome> expectedDataSpentIncomeMap = new TreeMap<>();
		expectedDataSpentIncomeMap.put(LocalDate.of(2014, 10, 1), new SpentIncome(163500, 0));
		expectedDataSpentIncomeMap.put(LocalDate.of(2017, 6, 1), new SpentIncome(0, 16911700));
		expectedDataSpentIncomeMap.put(LocalDate.MAX, new SpentIncome(81750, 8455850));

		Assert.assertEquals(expectedDataSpentIncomeMap, dataSpentIncomeMap);
	}

	@Test
	public void testGenerateSummaryGoodDataWithDonutFilter() {
		// Test
		setupGoodData();
		Map<LocalDate, SpentIncome> dataSpentIncomeMap = levelMoneyClient
				.generateSummary(expectedGetAllTransactionsResponse, true, Arrays.asList("Krispy Kreme Donuts"));
		Map<LocalDate, SpentIncome> expectedDataSpentIncomeMap = new TreeMap<>();
		expectedDataSpentIncomeMap.put(LocalDate.of(2014, 10, 1), new SpentIncome(64500, 0));
		expectedDataSpentIncomeMap.put(LocalDate.of(2017, 6, 1), new SpentIncome(0, 16911700));
		expectedDataSpentIncomeMap.put(LocalDate.MAX, new SpentIncome(32250, 8455850));

		Assert.assertEquals(expectedDataSpentIncomeMap, dataSpentIncomeMap);
	}

	@Test
	public void testGenerateSummaryStringGoodData() throws Exception {
		// Test
		setupGoodData();
		Map<LocalDate, SpentIncome> expectedDataSpentIncomeMap = new TreeMap<>();
		expectedDataSpentIncomeMap.put(LocalDate.of(2014, 10, 1), new SpentIncome(64500, 0));
		expectedDataSpentIncomeMap.put(LocalDate.of(2017, 6, 1), new SpentIncome(0, 16911700));
		expectedDataSpentIncomeMap.put(LocalDate.MAX, new SpentIncome(32250, 0));

		String expectedJsonString = "{\"2014-10\":{\"spent\":\"$6.45\",\"income\":\"$0.00\"},\"2017-06\":{\"spent\":\"$0.00\",\"income\":\"$1691.17\"},\"average\":{\"spent\":\"$3.22\",\"income\":\"$0.00\"}}";
		String jsonString = levelMoneyClient.generateSummaryString(objectMapper, expectedDataSpentIncomeMap);

		Assert.assertEquals(expectedJsonString, jsonString);
	}
}
