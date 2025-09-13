package io.github.homebeaver.aismodel;

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

	public static AisStreamMessage fromJson(String messageJson) {
		AisStreamMessage res = new AisStreamMessage();
		try {
			JSONObject jo = new JSONObject(messageJson);
			res.messageType = AisMessageTypes.fromValue(jo.getString(SERIALIZED_NAME_MESSAGE_TYPE));
//			System.out.println(">>>"+res.messageType.toString());
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
}
