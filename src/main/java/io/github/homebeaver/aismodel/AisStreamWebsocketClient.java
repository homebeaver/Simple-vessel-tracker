package io.github.homebeaver.aismodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import io.github.homebeaver.aismodel.AisStreamMessage.AisStreamCallback;
import io.github.homebeaver.aisview.Regions;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
/*
Änderungen zu orig git@github.com:aisstream/example.git subdir java
- proj nature nicht maven, sondern schlicht java
-- dadurch lib notwendig mit slf4j und Java-WebSocket jars
- APIKey fest als string in OnOpen

Weitere Infos unter
https://aisstream.io/documentation
https://deepwiki.com/aisstream 
  dort unter example Geographic Filtering (BoundingBoxes)
 Case            Bounding Box                Description
 Global            [[[-180, -90], [180, 90]]]    Covers the entire world // wird hier verwendet
 North Atlantic    [[[-80, 0], [-10, 60]]]        North Atlantic region
 Mediterranean     [[[-6, 30], [36, 46]]]        Mediterranean Sea region
 The baltic sea stretches from 53°N to 66°N latitude and from 10°E to 30°E longitude
 
 Overlapping Regions: 
 If you define multiple overlapping bounding boxes, you may receive duplicate messages.
 
 Daten & RxJava:
 Static ship data from AIS reports as detected in the Australian region of interest in 2014:
 https://github.com/amsa-code/ais-ship-data-2014
 use of functional stream programming libraries RxJava 1.3.8 (archived):
 https://github.com/amsa-code/risky
 */
public class AisStreamWebsocketClient extends WebSocketClient {

	private static final String WSS_AISSTREAM_URI = "wss://stream.aisstream.io/v0/stream";

//    private static final String GLOBAL = "[[[-90,-180],[90,180]]]";
//    private static final String MEDITERRANEAN = "[[[-6, 30], [36, 46]]]";
    private static final String BALTICSEA = Regions.getInstance().getBoundingBox(Regions.BALTICSEA);
//    private static final String OERESUND = "[[[56.15625856755953, 13.458251953125], [55.24311788040884, 11.612548828125]]] ";
//    NE: GeoPosition:[56.15625856755953, 13.458251953125] (56 09.376N, 013 27.495E)
//    SW: GeoPosition:[55.24311788040884, 11.612548828125] (55 14.587N, 011 36.753E)

    String apikey;
    String boundingBoxes;

	public AisStreamWebsocketClient(boolean connect, AisStreamKey apikey) throws URISyntaxException {
		this(connect, apikey, BALTICSEA);
	}
	public AisStreamWebsocketClient(boolean connect, AisStreamKey apikey, String boundingBox) throws URISyntaxException {
		super(new URI(WSS_AISSTREAM_URI));
		this.apikey = apikey.getKey();
		this.boundingBoxes = boundingBox==null ? BALTICSEA : boundingBox;
		if (connect) {
			this.connect();
		}
	}

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // send subscription message upon connection
        send("{\"APIKey\":\""+apikey+"\",\"BoundingBoxes\":"+boundingBoxes+"}");
    }

    @Override
    public void onMessage(ByteBuffer message) {
        String jsonString = StandardCharsets.UTF_8.decode(message).toString();
        System.out.println(jsonString);
    }

    @Override
    public void onMessage(String message) {
        // unused as aisstream.io returns messages as byte buffers
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
    	//   public static final int NORMAL = 1000;
    	//                                    1004: Reserved
    	//   public static final int ABNORMAL_CLOSE = 1006; ungültiger API-Key
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
