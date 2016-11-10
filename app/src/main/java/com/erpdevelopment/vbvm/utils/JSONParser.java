package com.erpdevelopment.vbvm.utils;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	public JSONParser() {}

//	public JSONObject getJSONFromUrl(String urlString) {
//		HttpURLConnection conn = null;
//		InputStream is = null;
//		try {
//			URL url = new URL(urlString);
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("GET");
////			InputStream is = conn.getInputStream();
//			is = conn.getInputStream();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (conn != null) conn.disconnect();
//		}
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
////			is.close();
//			json = StringEscapeUtils.unescapeHtml(sb.toString());
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		} finally {
//			if (is == null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		try {
//			jObj = new JSONObject(json);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}
//		return jObj;
//	}

//	public JSONObject getJSONFromUrl(String url) {
//
//		// Making HTTP request
//		try {
//			// defaultHttpClient
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(url);
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "UTF-8"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = StringEscapeUtils.unescapeHtml(sb.toString());
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e.toString());
//		}
//
//		// try parse the string to a JSON object
//		try {
//			jObj = new JSONObject(json);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}
//
//		// return JSON String
//		return jObj;
//
//	}

	public JSONObject getJSONFromUrl(String url) {
		JSONObject jsonObject = null;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response responses = null;
		String jsonData = "";
		try {
			responses = client.newCall(request).execute();
			if (responses != null)
				jsonData = responses.body().string();
			jsonObject = new JSONObject(jsonData);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
