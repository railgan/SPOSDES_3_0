package ch.ice.controller.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ch.ice.controller.interf.SearchEngine;
import ch.ice.exceptions.NoUrlFoundException;
import ch.ice.exceptions.SearchEngineRequestLimitReachedException;
import ch.ice.utils.Config;
import ch.ice.utils.JSONStandardizedKeys;
import ch.ice.utils.JSONUtil;
import ch.ice.utils.XMLParser;

public class GoogleSearchEngine implements SearchEngine {

	private static final Logger logger = LogManager.getLogger(GoogleSearchEngine.class.getName());
	private static Map<String, String> country2tld = XMLParser.getTLDOfCountry();

	@Override
	public JSONArray search(String requestedQuery, int limitSearchResult, String countryCode) throws NoUrlFoundException, SearchEngineRequestLimitReachedException {
		try {

			String accountKey = "";
			String config_cx = "";

			PropertiesConfiguration config;

			/*
			 * Load Configuration File
			 */
			config = Config.PROPERTIES;

			accountKey = config.getString("searchEngine.google.accountKey");
			config_cx = config.getString("searchEngine.google.cx");
			
			String charset = Charset.defaultCharset().name();

			final String apiKey = URLEncoder.encode(accountKey, charset);
			final String cx =  URLEncoder.encode(config_cx, charset);

			// country code and google host mapping
			if(country2tld.get(countryCode.toLowerCase()).isEmpty() || country2tld.get(countryCode.toLowerCase()) == null){
				countryCode = "com";
			} else {
				countryCode = country2tld.get(countryCode.toLowerCase());
			}

			/*
			 * for field options check:
			 * https://developers.google.com/apis-explorer/?hl=de#p/customsearch/v1/search.cse.list
			 */
			final String fields =  URLEncoder.encode("items(link,title),searchInformation/searchTime", charset);
			final String googleHost =  URLEncoder.encode("google."+countryCode, charset);
			
			if(requestedQuery.isEmpty() | requestedQuery == "")
				return new JSONArray();
			
			requestedQuery = URLEncoder.encode(requestedQuery, charset);

			// google only ever returns max 10 results
			if(limitSearchResult <= 1){
				limitSearchResult = 1;
			} else if(limitSearchResult >= 10){
				limitSearchResult = 10;
			}

			String googleSearchUrl = "https://www.googleapis.com/customsearch/v1?q="+ requestedQuery +"&key="+ apiKey +"&cx="+ cx +"&googlehost="+ googleHost +"&fields="+ fields+"&num="+limitSearchResult;
			logger.info("Lookup Google with request URL: "+googleSearchUrl);
			
			URL url = new URL(googleSearchUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();


			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} catch (IOException httpResponseError) {
				logger.error(httpResponseError.getMessage());
				throw new SearchEngineRequestLimitReachedException("There has been a problem. Either the Google searchengine has reached its request limit or you dont have a connection to the internet.");
			}

			String inputLine;
			final StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			JSONObject json = new JSONObject(response.toString());
			JSONArray googleSarchResults = json.getJSONArray("items");
			logger.info("Returned results from Google: "+googleSarchResults.length());

			final int resultsLength = googleSarchResults.length();

			if(resultsLength < 1) 
				throw new NoUrlFoundException("The Search engine delivered " +resultsLength+ " results for ["+requestedQuery+"]. Please change your query");

			// remove unused Elements trim url
			JSONUtil.keepLablesInJSONArray = new ArrayList<String>(
					// default ones for bing
					Arrays.asList(
							"link",
							"title"
							)
					);

			JSONUtil.urlLabel = "link";

			googleSarchResults = JSONUtil.cleanUp(googleSarchResults);

			// standardize elements
			Map<String, String> keyNodeMap = new HashMap<String,String>();
			keyNodeMap.put("link", JSONStandardizedKeys.URL);
			keyNodeMap.put("title", JSONStandardizedKeys.TITLE);

			googleSarchResults = this.standardizer(googleSarchResults, keyNodeMap);
			
			return googleSarchResults;

		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return null;
	}
	
	@Override
	public JSONArray search(String requestedQuery, int limitSearchResult) throws NoUrlFoundException, SearchEngineRequestLimitReachedException {
		return search(requestedQuery, limitSearchResult, "us");
	}

	@Override
	public String buildQuery(List<String> params){
		String query = "";

		for (String string : params) {
			query += string+" ";
		}

		return query;
	}

	@Override
	public JSONArray standardizer(JSONArray results, Map<String, String> keyNodeMap) {
		return JSONUtil.keyNodeMapper(results, keyNodeMap);
	}
}