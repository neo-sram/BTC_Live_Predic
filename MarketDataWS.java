//Imports
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.logging.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MarketDataWS {
    //initializing logger and writer
    private static final Logger logger = Logger.getLogger(MarketDataWS.class.getName());
    private static PrintWriter writer;
    public static void main(String[] args) {
        //Creating WebSocket client
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder().buildAsync(URI.create("wss://stream.binance.com:9443/ws/btcusdt@ticker_1h"),
        //Creating WebSocket listener
                new WebSocket.Listener() {
                    @Override
                    public void onOpen(WebSocket ws) {
                        System.out.println("Connected");
                        try {
                            writer = new PrintWriter(new FileWriter("market_data.log", true));
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Error opening log file", e);
                        }
                        ws.request(1);
                    }
                    //Handling incoming text messages
                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        String json = data.toString();
                        Map<String, String> fields = new HashMap<>();
                        String[] parts = json.substring(1, json.length() - 1).split(",");
                        for (String part : parts) {
                            String[] kv = part.split(":", 2);
                            if (kv.length == 2) {
                                String key = kv[0].replace("\"", "");
                                String value = kv[1].replace("\"", "");
                                fields.put(key, value);
                            }
                        }
                        //Extracting required fields
                        String o = fields.get("o");
                        String h = fields.get("h");
                        String l = fields.get("l");
                        String c = fields.get("c");
                        String n = fields.get("n");
                        if (o != null && h != null && l != null && c != null && n != null) {
                            writer.println("Open: " + o + ", High: " + h + ", Low: " + l + ", Close: " + c + ", Volume: " + n);
                            writer.flush();
                        }
                        ws.request(1);
                        return null;
                    }
                    //Handling errors
                    @Override
                    public CompletionStage<?> onClose(WebSocket ws, int code, String reason) {
                        if (writer != null) {
                            writer.close();
                        }
                        return null;
                    }
                });
        try { Thread.sleep(Long.MAX_VALUE); } catch (Exception e) {}
    }
}

