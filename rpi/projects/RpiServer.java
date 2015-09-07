import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.sun.net.httpserver.*;

public class RpiServer {
	public static void main (String args[]) throws Exception {
		HttpServer server = 
			HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/pi", new MyHandler());
		server.setExecutor(null);
		server.start();
	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response = "Hi from pi";
			t.sendResponseHeaders(200,response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}

