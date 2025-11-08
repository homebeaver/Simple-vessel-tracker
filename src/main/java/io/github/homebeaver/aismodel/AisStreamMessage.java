package io.github.homebeaver.aismodel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

bei Fehlern: {"error": "Api Key Is Not Valid"}
 */
public class AisStreamMessage {

	public static final String SERIALIZED_NAME_ERROR = "error";
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
//			System.out.println(">>>"+res.messageType.toString());
			res.metaData = MetaData.fromJson(jo.getJSONObject(SERIALIZED_NAME_METADATA));
			JSONObject joMsg = jo.getJSONObject(SERIALIZED_NAME_MESSAGE);
//			System.out.println(">>>"+res.messageType.toString() + res.metaData);
/*
			try {
				Thread.sleep( 10 ); // XXX slow down
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 */
			switch (res.messageType) {
			case POSITIONREPORT: // messageType 1, 2, 3
				res.message = PositionReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case BASESTATIONREPORT: // messageType 4
				res.message = BaseStationReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case SHIPSTATICDATA: // messageType 5
				res.message = ShipStaticData.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case ADDRESSEDBINARYMESSAGE: // messageType 6
				res.message = AddressedBinaryMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case BINARYACKNOWLEDGE: // messageType 7
				res.message = BinaryAcknowledge.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case BINARYBROADCASTMESSAGE: // messageType 8
				res.message = BinaryBroadcastMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case STANDARDSEARCHANDRESCUEAIRCRAFTREPORT: // messageType 9
				res.message = StandardSearchAndRescueAircraftReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case COORDINATEDUTCINQUIRY: // messageType 10
				res.message = CoordinatedUTCInquiry.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
// Message 11: UTC/date response; For Message 11 refer to description of Message 4
			case ADDRESSEDSAFETYMESSAGE: // messageType 12
				res.message = AddressedSafetyMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
// Message 13: Safety related acknowledge; For Message 13 refer to description of Message 7
			case SAFETYBROADCASTMESSAGE: // messageType 14
				res.message = SafetyBroadcastMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case INTERROGATION: // messageType 15
				res.message = Interrogation.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case ASSIGNEDMODECOMMAND: // messageType 16
				res.message = AssignedModeCommand.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case GNSSBROADCASTBINARYMESSAGE: // messageType 17
				res.message = GnssBroadcastBinaryMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case STANDARDCLASSBPOSITIONREPORT: // messageType 18
				res.message = StandardClassBPositionReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case EXTENDEDCLASSBPOSITIONREPORT: // messageType 19
				res.message = ExtendedClassBPositionReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case DATALINKMANAGEMENTMESSAGE: // messageType 20
				res.message = DataLinkManagementMessage.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case AIDSTONAVIGATIONREPORT: // messageType 21
				res.message = AidsToNavigationReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case CHANNELMANAGEMENT: // messageType 22
				res.message = ChannelManagement.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case GROUPASSIGNMENTCOMMAND: // messageType 23
				res.message = GroupAssignmentCommand.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case STATICDATAREPORT: // messageType 24
				res.message = StaticDataReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case SINGLESLOTBINARYMESSAGE: // messageType 25
//	TODO			res.message = StaticDataReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case MULTISLOTBINARYMESSAGE: // messageType 26
//	TODO			res.message = StaticDataReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case LONGRANGEAISBROADCASTMESSAGE: // messageType 27
//	TODO			res.message = StaticDataReport.fromJson(joMsg.getJSONObject(res.messageType.getValue()));
				break;
			case UNKNOWNMESSAGE:
//            	handleUnknownMessage(message.getJSONObject("Message"), metaData);
				break;
			default:
				System.out.println("Unhandled message type: " + res.messageType);
			}
		} catch (JSONException e) {
			// {"error": "Api Key Is Not Valid"}
			JSONObject jo = new JSONObject(messageJson);
			Object error = jo.get(SERIALIZED_NAME_ERROR);
			if (error instanceof String err) {
				System.out.println(err);
//				throw new JSONException(err);
			} else {
//		    	logger.error("Error creating AisStreamMessage ", e);
				System.out.println("Error creating AisStreamMessage "+ e
						+"\n message:"+messageJson);
			}
		}
		return res.message==null ? null : res;
	}
	
	/*
	 * Universelles Interface fuer Callback-Klasse zur Entkopplung der Meldung von
	 * Zwischenergebnissen waehrend der Verarbeitung, 
	 * damit beliebige GUIs moeglich sind.
	 */
	public static interface AisStreamCallback<V> {
		void outMessage(V v);
	}
	public static class ConsoleCallback implements AisStreamCallback<AisStreamMessage> {
		@Override
		public void outMessage(AisStreamMessage msg) {
			if(msg!=null) {
				System.out.println(""+msg.getAisMessageType() + " " + msg.getMetaData().toStringFull());
			}
		}
	}

	public static Boolean liesUrl(URL fileurl, AisStreamCallback<AisStreamMessage> meldungenCallback) {
		try {
			System.out.println("starting with fileurl: " + fileurl);
			File file = new File(fileurl.toURI());
			return liesUrl(new BufferedReader(new FileReader(file)), meldungenCallback);
		} catch (URISyntaxException | FileNotFoundException e) {
			System.out.println("Exeption " + e);
			return Boolean.FALSE;
		}
	}
	// used in MessageLoader
	public static Boolean liesUrl(BufferedReader reader, AisStreamCallback<AisStreamMessage> meldungenCallback) {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				meldungenCallback.outMessage(AisStreamMessage.fromJson(line));
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Exeption " + e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public static Boolean liesUrl(FileInputStream fis, AisStreamCallback<AisStreamMessage> meldungenCallback) {
		try {
			return liesUrl(new BufferedReader(new InputStreamReader(fis)), meldungenCallback);
		} catch (Exception e) {
			System.out.println("Exeption " + e);
			return Boolean.FALSE;
		} finally {
			
		}
	}

	public static Boolean liesUrl(String url, AisStreamCallback<AisStreamMessage> meldungenCallback) {
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
	}

}
