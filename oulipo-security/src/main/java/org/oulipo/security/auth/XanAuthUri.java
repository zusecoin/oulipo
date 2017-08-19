/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.security.auth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.bitcoinj.core.ECKey;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Strings;

public final class XanAuthUri {

	public static class Response {

		public final String message;

		public final int resultCode;

		public Response(int resultCode, String message) {
			this.resultCode = resultCode;
			this.message = message;
		}

		@Override
		public String toString() {
			return "Response{" + "resultCode=" + resultCode + ", message='"
					+ message + '\'' + '}';
		}
	}

	public final class ResultCode {

		public static final int NO_CONNECTION = -1;

		public static final int OK = 0;

		public static final int UNKNOWN_ERROR = 199;

	}

	private static String asString(InputStream inputStream) throws IOException {
		try {
			return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Create a XanAuth URI from specified string URI
	 * 
	 * @param uri xanauth uri
	 * @param key client key
	 * @return 
	 * @throws URISyntaxException
	 */
	public static XanAuthUri create(String uri, ECKey key)
			throws URISyntaxException {
		if (Strings.isNullOrEmpty(uri)) {
			throw new IllegalArgumentException("xanAuthUri is null");
		}

		XanAuthUri authUri = new XanAuthUri();
		authUri.mRawUri = uri;
		authUri.mUri = URI.create(uri);
		authUri.mKey = key;

		verifyParams(authUri.mUri);

		authUri.mToken = getQueryParameter(authUri.mUri, "token");
		String uValue = getQueryParameter(authUri.mUri, "secured");
		authUri.mIsSecured = Boolean.getBoolean(uValue);//TODO: check null

		return authUri;
	}

	private static String getQueryParameter(URI uri, String param) {
		try {
			Map<String, String> params = split(uri);
			return params.get(param);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// TODO: Remove
	public static Map<String, String> split(URI url)
			throws UnsupportedEncodingException {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
					URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

	private static void verifyParams(URI uri) throws URISyntaxException {
		if (Strings.isNullOrEmpty(getQueryParameter(uri, "token"))) {
			throw new URISyntaxException(uri.toString(),
					"Missing token parameter");
		}

		/*
		String uValue = getQueryParameter(uri, "u");
		if (!Strings.isNullOrEmpty(uValue) && (!uValue.equals("0"))
				&& !uValue.equals("1")) {
			throw new URISyntaxException(uValue, "Illegal value for u param");
		}
		*/
	}

	private HttpURLConnection mConnection;

	private boolean mIsSecured;

	private ECKey mKey;

	private String mRawUri;

	private String mToken;

	private URI mUri;

	private XanAuthUri() {
	}

	private String buildRequest() throws JSONException,
			UnsupportedEncodingException, NoSuchAlgorithmException,
			InvalidKeyException {
		return JwtBuilder.buildRequest(mKey, this);
	}

	public String getToken() {
		return mToken;
	}

	public String getRawUri() {
		return mRawUri;
	}

	public URI getUri() {
		return mUri;
	}

	public boolean isSecured() {
		return mIsSecured;
	}

	public Response makeRequest() {
		try {
			openConnection();
			writeRequest(buildRequest());
			return readResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Response(ResultCode.UNKNOWN_ERROR, null);
	}

	private void openConnection() throws IOException, URISyntaxException {
		mConnection = (HttpURLConnection) toCallbackURI().toURL()
				.openConnection();
		mConnection.setRequestMethod("POST");
		mConnection.setRequestProperty("Content-Type", "application/json");
		mConnection.setDoInput(true);
		mConnection.setDoOutput(true);
		mConnection.setUseCaches(false);

		mConnection.connect();
	}

	private Response readResponse() throws IOException {
		int rc = mConnection.getResponseCode();
		if (rc == -1) {
			return new Response(ResultCode.NO_CONNECTION, null);
		}
		if (rc < 300 && rc >= 200) {
			return new Response(ResultCode.OK,
					asString(mConnection.getInputStream()));
		} else if (rc >= 400) {
			String message = asString(mConnection.getErrorStream());
			try {
				JSONObject jo = new JSONObject(message);
				return new Response(jo.getInt("code"), jo.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println(message);
				return new Response(-1, e.getMessage());
			}
		} else {
			return new Response(ResultCode.UNKNOWN_ERROR, null);
		}
	}

	public URI toCallbackURI() throws URISyntaxException {
		return new URI(mIsSecured ? "https" : "http", null, mUri.getHost(),
				mUri.getPort(), mUri.getPath(), null, null);
	}

	@Override
	public String toString() {
		String url = null;
		try {
			url = toCallbackURI().toString();
		} catch (URISyntaxException e) {

		}

		return "XanAuthUri{" + "mRawUri='" + mRawUri + '\'' + ", callbackUri="
				+ url + '}';
	}

	private void writeRequest(String message) throws IOException {
		DataOutputStream dos = new DataOutputStream(
				mConnection.getOutputStream());
		dos.write(message.getBytes());
		dos.close();
	}
}
