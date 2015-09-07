import java.io.*;
import java.net.*;

public class HTTPUtil {

private static String charset = "UTF-8";
private static String query = null;
private static HttpURLConnection conn = null;

public static void main (String args[]) throws Exception {
	prepareRequest();
	sendPostRequest();
	System.out.println(String.format("rc=%d",conn.getResponseCode()));
//	readResponse();
	conn.disconnect();
}

public static void prepareRequest() throws Exception {
	// Prepare the parameters.
	query = String.format(
		"emailfrom=%s&" +
		"npa=%s&" +
		"exchange=%s&" +
		"number=%s&" +
		"body=%s&",
		"submitted=%s&" +
		"submit=%s&" +
		URLEncoder.encode("fsmith@ptc.com",charset),
		URLEncoder.encode("281",charset),
		URLEncoder.encode("610",charset),
		URLEncoder.encode("5645",charset),
		URLEncoder.encode("message",charset),
		URLEncoder.encode("1",charset),
		URLEncoder.encode("Send",charset));

	// Setup the post request.
	URL url = new URL("http://www.txtDrop.com");
	conn = (HttpURLConnection) url.openConnection();
	conn.setUseCaches(false);
	conn.setRequestMethod("POST");
	conn.setRequestProperty("Accept-Charset",charset);
	conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US))");
	conn.setDoOutput(true);
}

public static void sendPostRequest() throws Exception {
	OutputStreamWriter writer =
		new OutputStreamWriter(conn.getOutputStream());
	writer.write(query);
	writer.flush();
	writer.close();
	conn.connect();
}

public static void readResponse() throws Exception {
	InputStream is = conn.getInputStream();
	BufferedReader r = new BufferedReader(new InputStreamReader(is));
	String line = "";
	while ((line = r.readLine()) != null) {
		System.out.println(line);
	}
	r.close();
}

}
