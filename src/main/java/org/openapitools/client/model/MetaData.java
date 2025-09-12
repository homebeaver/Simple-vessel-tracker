package org.openapitools.client.model;

import org.json.JSONException;
import org.json.JSONObject;

import dk.dma.enav.model.geometry.Position;

public class MetaData {

	public static final String MMSI = "MMSI";
	public static final String MMSI_STRING = "MMSI_String";
	public static final String SHIPNAME = "ShipName";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TIME_UTC = "time_utc";

//	private Double mmsi; // oderso:
	private long mmsi;
	private String mmsi_string;
	private String shipName;
	private Position position; // type aus dk.dma.enav.model.geometry.Position
	private String time_utc;

	public static MetaData fromJson(JSONObject jo) {
		MetaData res = new MetaData();
		// Extract individual fields from JSONObject
		try {
			res = res.mmsi(jo.getLong(MMSI))
					.mmsi_string(String.valueOf(jo.getLong(MMSI_STRING))) // long => String
					.shipName(jo.getString(SHIPNAME).trim())
					.position(getPosition(jo))
					.time_utc(jo.getString(TIME_UTC));
		} catch (JSONException e) {
//		        logger.error("Error creating MetaData", e);
			System.out.println("Error creating MetaData " + e);
		}
		return res;
	}

	private MetaData() {}

	public MetaData mmsi(long mmsi) {
		this.mmsi = mmsi;
		return this;
	}
	public MetaData mmsi_string(String mmsi_string) {
		this.mmsi_string = mmsi_string;
		return this;
	}
	public MetaData shipName(String shipName) {
		this.shipName = shipName;
		return this;
	}
	public MetaData position(Position position) {
		this.position = position;
		return this;
	}
	public MetaData time_utc(String time_utc) {
		this.time_utc = time_utc;
		return this;
	}

	private static final char COLON = ':';
	private static final char COMMA = ',';
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class MetaData {");
		sb.append(TIME_UTC).append(COLON).append(time_utc);
		sb.append(COMMA).append(MMSI).append(COLON).append(mmsi);
		sb.append(COMMA).append(MMSI_STRING).append(COLON).append(mmsi_string);
		sb.append(COMMA).append(SHIPNAME).append(COLON).append(shipName);
		sb.append(COMMA).append("Position").append(COLON).append(position);
		sb.append("}");
		return sb.toString();
	}

	private static Position getPosition(JSONObject jo) {
		return getPosition(jo.getDouble(LATITUDE), jo.getDouble(LONGITUDE));
	}

	private static Position getPosition(double latitude, double longitude) {
		return Position.create(latitude, longitude);
	}

}
