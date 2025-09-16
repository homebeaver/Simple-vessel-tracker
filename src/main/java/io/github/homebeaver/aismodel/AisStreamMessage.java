package io.github.homebeaver.aismodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/*
aistream bereitet die AIS Nachrichten auf und liefert sie im JSON-Format. 
Jede aisstream Nachricht besteht aus drei Teilen

    MetaData : "MetaData":{"MMSI":
    MessageType : "MessageType":"StaticDataReport"
    Message : "Message":{"StaticDataReport":{"MessageID":24,... XXX MessageType steht hier noch mal!

 */
public class AisStreamMessage {

	public static final String SERIALIZED_NAME_METADATA = "MetaData";
	public static final String SERIALIZED_NAME_MESSAGE_TYPE = "MessageType";
	public static final String SERIALIZED_NAME_MESSAGE = "Message";
	
	MetaData metaData;
	AisMessageTypes messageType;
	AisMessage message; // subtype of AisMessage

	public MetaData getMetaData() {
		return metaData;
	}
	public AisMessageTypes getAisMessageType() {
		return messageType;
	}

	public AisMessage getAisMessage() {
		return message;
	}

	public static AisStreamMessage fromJson(String messageJson) {
		AisStreamMessage res = new AisStreamMessage();
		try {
			JSONObject jo = new JSONObject(messageJson);
			res.messageType = AisMessageTypes.fromValue(jo.getString(SERIALIZED_NAME_MESSAGE_TYPE));
			System.out.println(">>>"+res.messageType.toString());
			res.metaData = MetaData.fromJson(jo.getJSONObject(SERIALIZED_NAME_METADATA));
			JSONObject joMsg = jo.getJSONObject(SERIALIZED_NAME_MESSAGE);
//			System.out.println(">>>"+res.messageType.toString() + res.metaData);
			switch (res.messageType) {
			case POSITIONREPORT:
				res.message = PositionReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case SHIPSTATICDATA:
				res.message = ShipStaticData.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case UNKNOWNMESSAGE:
//            	handleUnknownMessage(message.getJSONObject("Message"), metaData);
				break;
			case STANDARDCLASSBPOSITIONREPORT:
//            	handleStandardClassBPositionReport(message.getJSONObject("Message").getJSONObject("StandardClassBPositionReport"), metaData);
				break;
			default:
				System.out.println("Unhandled message type: " + res.messageType);
			}
		} catch (JSONException e) {
//	    	logger.error("Error creating AisStreamMessage ", e);
			System.out.println("Error creating AisStreamMessage "+ e);
		}
		return res.message==null ? null : res;
	}
	
	/*
	 * Universelles Interface fuer Callback-Klasse zur Entkopplung der Meldung von
	 * Zwischenergebnissen waehrend der Verarbeitung, 
	 * damit beliebige GUIs moeglich sind.
	 */
	public static interface MeldungenCallback<V> {
		void ausgabeMeldung(V v);
	}
	public static class ConsoleCallback implements MeldungenCallback<AisStreamMessage> {
		@Override
		public void ausgabeMeldung(AisStreamMessage msg) {
			if(msg!=null)
			System.out.println(""+msg.getAisMessageType() + msg.getMetaData());
		}
	}

//	public static final String GITHUB_URL =	"https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
	public static Boolean liesUrl(URL fileurl, MeldungenCallback<AisStreamMessage> meldungenCallback) {
		try {
			File file = new File(fileurl.toURI());
			return liesUrl(new BufferedReader(new FileReader(file)), meldungenCallback);
		} catch (URISyntaxException | FileNotFoundException e) {
			System.out.println("Exeption " + e);
			return Boolean.FALSE;
		}
	}
	public static Boolean liesUrl(BufferedReader reader, MeldungenCallback<AisStreamMessage> meldungenCallback) {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				meldungenCallback.ausgabeMeldung(AisStreamMessage.fromJson(line));
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Exeption " + e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	public static Boolean liesUrl(String url, MeldungenCallback<AisStreamMessage> meldungenCallback) {
		System.out.println("starting with " + url);
		try {
			InputStream input = new URL(url).openStream();
			return liesUrl(new BufferedReader(new InputStreamReader(input)), meldungenCallback);
		} catch (IOException e) {
			System.out.println("Exeption " + e);
			// meldungenCallback.ausgabeMeldung("Exeption " + e);
			return Boolean.FALSE;
		} finally {
			
		}
//		return Boolean.TRUE;
	}

}
