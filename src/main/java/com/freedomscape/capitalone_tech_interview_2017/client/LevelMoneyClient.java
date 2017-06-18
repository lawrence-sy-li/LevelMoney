package com.freedomscape.capitalone_tech_interview_2017.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.freedomscape.capitalone_tech_interview_2017.model.CommonArgs;
import com.freedomscape.capitalone_tech_interview_2017.model.GetAllTransactionsRequest;
import com.freedomscape.capitalone_tech_interview_2017.model.GetAllTransactionsResponse;
import com.freedomscape.capitalone_tech_interview_2017.model.SpentIncome;
import com.freedomscape.capitalone_tech_interview_2017.model.Transaction;
import com.freedomscape.capitalone_tech_interview_2017.serializer.LocalDateYearMonthSerializer;

/**
 * Class that allows a user to act as a client to the LevelMoney API RESTful
 * service. Could definitely be improved in many ways if there was a little more
 * time.
 * 
 * @author lsli
 *
 */
public class LevelMoneyClient {
	private final static Logger logger = Logger.getLogger(LevelMoneyClient.class.getName());
	public final static String DEFAULT_CONFIG_FILE = "config.properties";

	private ObjectMapper objectMapper;
	private HttpsURLConnection connection;

	private Long userId;
	private String authenticationToken;
	private String apiToken;
	private String serviceUrl;
	private boolean ignoreDonuts;
	private List<String> merchantExclusions;

	public static class LevelMoneyClientBuilder {
		private ObjectMapper objectMapper;
		private HttpsURLConnection connection;

		private Long userId;
		private String authenticationToken;
		private String apiToken;
		private String serviceUrl;
		private boolean ignoreDonuts;
		private List<String> merchantExclusions;

		public LevelMoneyClientBuilder objectMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
			return this;
		}

		public LevelMoneyClientBuilder connection(HttpsURLConnection connection) {
			this.connection = connection;
			return this;
		}

		public LevelMoneyClientBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public LevelMoneyClientBuilder authenticationToken(String authenticationToken) {
			this.authenticationToken = authenticationToken;
			return this;
		}

		public LevelMoneyClientBuilder apiToken(String apiToken) {
			this.apiToken = apiToken;
			return this;
		}

		public LevelMoneyClientBuilder serviceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
			return this;
		}

		public LevelMoneyClientBuilder ignoreDonuts(boolean ignoreDonuts) {
			this.ignoreDonuts = ignoreDonuts;
			return this;
		}

		public LevelMoneyClientBuilder merchantExclusions(List<String> merchantExclusions) {
			this.merchantExclusions = merchantExclusions;
			return this;
		}

		public LevelMoneyClient build() {
			return new LevelMoneyClient(this);
		}
	}

	private LevelMoneyClient(LevelMoneyClientBuilder builder) {
		if (builder.objectMapper == null) {
			this.objectMapper = new ObjectMapper();
		}
		this.connection = builder.connection;
		this.userId = builder.userId;
		this.authenticationToken = builder.authenticationToken;
		this.apiToken = builder.apiToken;
		this.serviceUrl = builder.serviceUrl;
		this.ignoreDonuts = builder.ignoreDonuts;
		this.merchantExclusions = builder.merchantExclusions;
	}

	/**
	 * Calculates the monthly spend and income as a result of calling the
	 * GetAllTransactions RESTful service and returns the summarized results in
	 * a string. The string summarizes the spend and income by year/month. It
	 * also includes an average monthly spend. The format is the following:
	 * 
	 * {"2014-10": {"spent": "$200.00", "income": "$500.00"}, "2014-11":
	 * {"spent": "$1510.05", "income": "$1000.00"}, ... "2015-04": {"spent":
	 * "$300.00", "income": "$500.00"}, "average": {"spent": "$750.00",
	 * "income": "$950.00"}}
	 * 
	 * @return String representing the summarized monthly spend and income,
	 *         along with monthly average
	 * @throws Exception
	 *             Thrown if there was an issue generating the string
	 */
	public String getAllTransactionsString() throws Exception {
		try {
			// Build connection to service URL
			if (connection == null) {
				connection = buildConnection();
			}

			// Now make POST call
			GetAllTransactionsResponse getAllTransactionsResponse = executeRequest(connection, objectMapper);

			// Generate summary, creating map of months as keys, SpentIncome as
			// values
			Map<LocalDate, SpentIncome> dateSpentIncomeMap = generateSummary(getAllTransactionsResponse, ignoreDonuts,
					merchantExclusions);

			// Now create a nice JSON string of summary results
			return generateSummaryString(objectMapper, dateSpentIncomeMap);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Builds the HttpsURLConnection object for use later.
	 * 
	 * @return HttpsURLConnection object
	 * @throws IOException
	 *             Thrown if there was an issue building the connection, which
	 *             may include using an incorrect request method, unable to open
	 *             the URL connection, etc.
	 */
	private HttpsURLConnection buildConnection() throws IOException {
		logger.log(Level.FINEST, "Calling buildConnection()");
		URL url = new URL(serviceUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");
		return connection;
	}

	/**
	 * Actually attempts to connect with the LevelMoney RESTful service and
	 * returns a response object encapsulating the response which may contain
	 * zero or more transactions..
	 * 
	 * @param connection
	 *            HttpsURLConnection object
	 * @param objectMapper
	 *            Jackson objectMapper object
	 * @return The GetAllTransactionsResponse from calling the
	 *         getAllTransactions RESTful service
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	GetAllTransactionsResponse executeRequest(final HttpsURLConnection connection, final ObjectMapper objectMapper)
			throws JsonGenerationException, JsonMappingException, JsonParseException, JsonProcessingException,
			IOException {
		logger.log(Level.FINEST, "connection: {0}, objectMapper: {1}", new Object[] { connection, objectMapper });
		GetAllTransactionsRequest getAllTransactionsRequest = new GetAllTransactionsRequest();
		CommonArgs commonArgs = new CommonArgs();
		commonArgs.setUserId(userId);
		commonArgs.setAuthenticationToken(authenticationToken);
		commonArgs.setApiToken(apiToken);
		getAllTransactionsRequest.setCommonArgs(commonArgs);

		// Perhaps not really necessary to pretty print...
		// objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(getAllTransactionsRequest);
		OutputStream outputStream = connection.getOutputStream();
		objectMapper.writeValue(outputStream, getAllTransactionsRequest);

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			logger.log(Level.SEVERE, "HTTP error code contacting RESTful service: {0}", connection.getResponseCode());
			throw new RuntimeException(
					"Unable to successfully reach service.  HTTP error code: " + connection.getResponseCode());
		}

		// Deserialize REST service call into GetAllTransactionsResponse object
		GetAllTransactionsResponse getAllTransactionsResponse = objectMapper
				.readValue(new InputStreamReader(connection.getInputStream()), GetAllTransactionsResponse.class);

		logger.log(Level.FINER, "Result of RESTful service call - getAllTransactionResponse: {0}",
				getAllTransactionsResponse);

		return getAllTransactionsResponse;
	}

	/**
	 * Create a tree map (key ordered by date - year/month) with month/year as
	 * the key and a SpentIncome object as the value. The spent income object
	 * represents the sum total of what was spent or what was income for that
	 * particular month.
	 * 
	 * @param getAllTransactionsResponse
	 *            This is the GetAllTransactionsResponse object which contains
	 *            the results of the GetAllTransactions RESTful service and in
	 *            which we will parse the results for the summarized
	 *            transactions data.
	 * @param ignoreDonuts
	 *            If set to true, do not consider any donut related transactions
	 *            in the spend values if the merchant is included in the
	 *            merchantExclusions list.
	 * @param merchantExclusions
	 *            A list of merchants which should not be included in the
	 *            summarized spend values. Note, this only takes effect if the
	 *            ignoreDonuts value is set to true.
	 * @return Tree map consisting of a LocalDate object (All dates/times within
	 *         a single month are grouped to the first day of the month) as a
	 *         key and SpentIncome object as the value
	 */
	Map<LocalDate, SpentIncome> generateSummary(final GetAllTransactionsResponse getAllTransactionsResponse,
			final boolean ignoreDonuts, final List<String> merchantExclusions) {
		logger.log(Level.FINEST, "getAllTransactionsResponse: {0}, ignoreDonuts: {1}, merchantExclusions: {2}",
				new Object[] { getAllTransactionsResponse, ignoreDonuts, merchantExclusions });

		if (Objects.isNull(getAllTransactionsResponse)) {
			logger.log(Level.SEVERE, "getAllTransactionsResponse object must not be null");
			throw new IllegalArgumentException("getAllTransactionsResponse object must not be null");
		}

		Map<LocalDate, SpentIncome> dateSpentIncomeMap = new TreeMap<>();

		for (Transaction transaction : getAllTransactionsResponse.getTransactions()) {
			/*
			 * We iterate through each transaction in the response object.
			 * Transactions are grouped by month according to their transaction
			 * date/time. All transactions within a certain month/year are
			 * grouped to the first day of the month/year
			 */
			LocalDate monthYear = transaction.getTransactionTime().with(TemporalAdjusters.firstDayOfMonth())
					.truncatedTo(ChronoUnit.DAYS).toLocalDate();

			if (dateSpentIncomeMap.containsKey(monthYear)) {
				SpentIncome spentIncome = dateSpentIncomeMap.get(monthYear);
				if (transaction.getAmount() < 0) {
					// Do not count towards spend if ignoreDonuts is true and
					// the merchant is in the merchant exclusion list
					if (!ignoreDonuts || merchantExclusions == null
							|| !merchantExclusions.contains(transaction.getRawMercant())) {
						spentIncome.setSpent(spentIncome.getSpent() + Math.abs(transaction.getAmount()));
					}
				} else {
					spentIncome.setIncome(spentIncome.getSpent() + transaction.getAmount());
				}
			} else {
				if (transaction.getAmount() < 0) {
					SpentIncome tempSpentIncome = new SpentIncome(Math.abs(transaction.getAmount()), 0L);
					// Do not count towards spend if ignoreDonuts is true and
					// the merchant is in the merchant exclusion list
					if (!ignoreDonuts || merchantExclusions == null
							|| !merchantExclusions.contains(transaction.getRawMercant())) {
						dateSpentIncomeMap.put(monthYear, tempSpentIncome);
					}
				} else {
					SpentIncome tempSpentIncome = new SpentIncome(0L, Math.abs(transaction.getAmount()));
					dateSpentIncomeMap.put(monthYear, tempSpentIncome);
				}
			}
		}

		addAverageStatistics(dateSpentIncomeMap);

		return dateSpentIncomeMap;
	}

	/**
	 * Calculate the statistics that generate the average spend and income
	 * values per month based on data that we have already accumulated.
	 * 
	 * @param dateSpentIncomeMap
	 *            Tree map (ordered by dates) consisting of a LocalDate object
	 *            (All dates/times within a single month are grouped to the
	 *            first day of the month) as a key and SpentIncome object as the
	 *            value
	 */
	private void addAverageStatistics(final Map<LocalDate, SpentIncome> dateSpentIncomeMap) {
		logger.log(Level.FINEST, "dateSpentIncomeMap: {0}", dateSpentIncomeMap);
		long spentSumTotal = 0;
		long incomeSumTotal = 0;
		for (SpentIncome spentIncome : dateSpentIncomeMap.values()) {
			spentSumTotal += spentIncome.getSpent();
			incomeSumTotal += spentIncome.getIncome();
		}
		int numMonths = dateSpentIncomeMap.size();
		logger.log(Level.FINE, "Average stats calculated, spentSumTotal: {0}, incomeSumTotal: {1}, numMonths: {2}",
				new Object[] { spentSumTotal, incomeSumTotal, numMonths });
		/*
		 * Note, we have a special hack to put the average stats into the
		 * dateSpentIncomeMap tree map. We use a special month/year - in this
		 * case the maximum allowable date as a special key for the average.
		 * This way, we can still use the tree map to store not only the month
		 * by month spend and income, but also the average aggregate monthly
		 * spend/income as well.
		 */
		dateSpentIncomeMap.put(LocalDate.MAX, new SpentIncome(spentSumTotal / numMonths, incomeSumTotal / numMonths));
	}

	/**
	 * Generates a string that displays the month/year along with the
	 * corresponding spend/income for that particular month.
	 * 
	 * @param objectMapper
	 *            Jackson object mapper
	 * @param dateSpentIncomeMap
	 *            Tree map (ordered by the date) consisting of a LocalDate
	 *            object (All dates/times within a single month are grouped to
	 *            the first day of the month) as a key and SpentIncome object as
	 *            the value
	 * @return String in JSON-like format that summarizes the month/year
	 *         spend/income
	 * @throws JsonProcessingException
	 */
	String generateSummaryString(final ObjectMapper objectMapper, final Map<LocalDate, SpentIncome> dateSpentIncomeMap)
			throws JsonProcessingException {
		SimpleModule simpleModule = new SimpleModule("MyModule",
				new Version(1, 0, 0, null, "com.freedomscape", "capitalone-tech-interview-2017"));
		simpleModule.addKeySerializer(LocalDate.class, new LocalDateYearMonthSerializer());

		objectMapper.registerModule(simpleModule);

		return objectMapper.writeValueAsString(dateSpentIncomeMap);
	}

	public static void main(String[] args) {
		logger.setLevel(Level.SEVERE);
		logger.log(Level.FINE, "args: {0}", args);

		// Check arguments
		if (args.length >= 2 || (args.length == 1 && !"--ignore-donuts".equals(args[0]))) {
			System.err.println(displayCommandLineOptions());
			System.exit(1);
		}

		boolean ignoreDonuts = (args.length == 1);
		// TODO: Probably use Apache Configuration for more options on how to
		// load configuration files, especially when I have multiple values per
		// key
		Properties properties = new Properties();
		Long userId = null;
		String authenticationToken = null;
		String apiToken = null;
		String serviceUrl = null;
		List<String> merchantExclusions = null;
		try {
			// By default, load configuration from config.properties file
			properties.load(ClassLoader.getSystemResourceAsStream(DEFAULT_CONFIG_FILE));
			userId = Long.parseLong(properties.getProperty("userId"));
			authenticationToken = properties.getProperty("authenticationToken");
			apiToken = properties.getProperty("apiToken");
			serviceUrl = properties.getProperty("serviceUrl");
			String merchantExclusionsString = properties.getProperty("merchantExclusions");
			if (merchantExclusionsString != null) {
				merchantExclusions = Arrays.asList(merchantExclusionsString.split(","));
			}
			logger.log(Level.FINER,
					"Opening up properties file to load defaults for properties file: {0}, user ID: {1}, authentication token: {2}, API token: {3}, service URL: {4}, merchant exclusions: {5}",
					new Object[] { DEFAULT_CONFIG_FILE, userId, authenticationToken, apiToken, serviceUrl,
							merchantExclusions });
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unable to open properties file: " + DEFAULT_CONFIG_FILE, e);
			System.exit(1);
		}

		try {
			LevelMoneyClient levelMoneyClient = new LevelMoneyClient.LevelMoneyClientBuilder().userId(userId)
					.authenticationToken(authenticationToken).apiToken(apiToken).serviceUrl(serviceUrl)
					.ignoreDonuts(ignoreDonuts).merchantExclusions(merchantExclusions).build();
			System.out.println(levelMoneyClient.getAllTransactionsString());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "There was an issue retrieving all transactions", e);
			System.exit(1);
		}

	}

	private static String displayCommandLineOptions() {
		return "Arguments:\n" + "[--ignore-donuts]\n" + "where options include:\n"
				+ "--ignore-donuts	Disregard all donut-related transactions from the spending\n"
				+ "Example:\njava -jar levelmoney-0.0.1-SNAPSHOT-jar-with-dependencies.jar or\n"
				+ "java -jar levelmoney-0.0.1-SNAPSHOT-jar-with-dependencies.jar --ignore-donuts";
	}
}
