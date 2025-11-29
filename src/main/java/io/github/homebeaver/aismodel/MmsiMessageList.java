package io.github.homebeaver.aismodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

/*
 * 	A list of messages per vessel (or base station) with MMSI as vessel key.
 */
@SuppressWarnings("serial")
public class MmsiMessageList extends HashMap<Integer, List<AisStreamMessage>> 
	implements Map<Integer, List<AisStreamMessage>> {

	private static final Logger LOG = Logger.getLogger(MmsiMessageList.class.getName());

	public MmsiMessageList() {
		super();
	}
	
	public MmsiMessageList(int mmsi) {
		super();
		List<AisStreamMessage> waypoints = //new Vector<AisStreamMessage>();
		new ArrayList<AisStreamMessage>();
		super.put(mmsi, waypoints); // empty List waypoints 
	}

	public MmsiMessageList(AisStreamMessage msg) {
		super(msg.getMetaData().getMMSI());
		this.get(msg.getMetaData().getMMSI()).add(msg);
	}

	private List<AisStreamMessage> newList(AisStreamMessage msg) {
		List<AisStreamMessage> waypoints = //new Vector<AisStreamMessage>();
		new ArrayList<AisStreamMessage>();
		waypoints.add(msg);
		return waypoints;
	}

	/**
	 * Adds AisStreamMessage to list if msg identified as a ship
	 * @param msg
	 * @return true if list collection changed as a result of the call
	 */
	public boolean addShip(AisStreamMessage msg) {
		int mmsi = msg.getMetaData().getMMSI();
		if (mmsi==0) return false;
		List<AisStreamMessage> list = get(mmsi);
		if (isShip(msg)) {
			if (list==null) {
				put(mmsi, newList(msg));
				return true;
			} else {
				return list.add(msg);
			}
		} else if (isShip(mmsi)) {
			return list.add(msg);
		}
//		System.out.println("mmsi nicht als schiff erkannt "+mmsi + " / "+msg.getAisMessageType());
		LOG.fine("Not a ship MMSI="+mmsi + " / "+msg.getAisMessageType());
		return false;
	}

	/**
	 * Adds AisStreamMessage to list if msg identified as a ship od ClassA
	 * @param msg
	 * @return true if list collection changed as a result of the call
	 */
	public boolean addShipClassA(AisStreamMessage msg) {
		int mmsi = msg.getMetaData().getMMSI();
		if (mmsi==0) return false;
		List<AisStreamMessage> list = get(mmsi);
		if (isShipClassA(msg)) {
			if (list==null) {
				put(mmsi, newList(msg));
				return true;
			} else {
				return list.add(msg);
			}
		} else if (isShipClassA(mmsi)) {
			return list.add(msg);
		}
		LOG.fine("Not a ship of ClassA, maybe ClassB MMSI="+mmsi + " / "+msg.getAisMessageType() );
		return false;
	}

	public boolean isShipClassA(int mmsi) {
		try {
			return isShipClassA(get(mmsi));
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public boolean isShipClassB(int mmsi) {
		try {
			return isShipClassB(get(mmsi));
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean isShip(int mmsi) {
		try {
			return isShipClassA(get(mmsi)) || isShipClassB(get(mmsi));
		} catch (NullPointerException e) {
			return false;
		}
	}

	/*
	 * der Schiffsname wird manuell ins AIS-Geräte eingetragen und kann daher leer sein
	 */
	public String getName(int mmsi) {
		return getName(get(mmsi));
	}

	public String getCallSign(int mmsi) {
		return getCallSign(get(mmsi));
	}

	public Integer getType(int mmsi) {
		return getType(get(mmsi));
	}

	public Integer getShipLength(int mmsi) {
		return getShipLength(get(mmsi));
	}

	public Double getLastCog(int mmsi) {
		return getLastCog(get(mmsi));
	}

	public boolean isBaseStation(int mmsi) {
		return isBaseStation(get(mmsi));
	}

	/**
	 * Get ship name from a trace list
	 * @param list a trace list for a vessel
	 * @return the last name entered (can be empty)
	 * @throws NullPointerException if the specified list is null
	 */
	public static String getName(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
		for (AisStreamMessage m : list) {
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				if (ssd.getName().isEmpty()) {
					LOG.warning("ShipName is empty ("+list.indexOf(m)+"/"+list.size()+"): "+m.getMetaData().toStringFull()
						+ "\n"+ssd); // expected not empty!
				} else {
					return ssd.getName();
				}
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportA().getValid()) {  // ReportA exists
					return sdr.getReportA().getName();
				}
			}
		}
		// not found any static data, get name from MetaData
		return list.get(0).getMetaData().getShipName();
	}

	/**
	 * Get CallSign of the vessel from a trace list
	 * @param list a trace list for a vessel
	 * @return the last sign entered (can be empty)
	 * @throws NullPointerException if the specified list is null
	 */
	public static String getCallSign(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
		for (AisStreamMessage m : list) {
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				if (ssd.getCallSign().isEmpty()) {
					LOG.warning("CallSign is empty ("+list.indexOf(m)+"/"+list.size()+"): "+m.getMetaData().toStringFull()
						+ "\n"+ssd); // expected not empty!
				} else {
					return ssd.getCallSign();
				}
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportB().getValid()) { // ReportB with CallSign exists
					return sdr.getReportB().getCallSign();
				}
			}
		}
		return null;
	}

	/**
	 * Get ships IMO number from a trace list
	 * @param list a trace list for a vessel
	 * @return the last number entered (can be empty f.i. for ClassB vessels)
	 * @throws NullPointerException if the specified list is null
	 */
	public static Integer getImoNumber(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getImoNumber();
			} 
		}
		return null;
	}

	/**
	 * Get ships Type from a trace list
	 * @param list a trace list for a vessel
	 * @return the last type entered (can be null f.i. there is no ReportB in the list TODO )
	 * @throws NullPointerException if the specified list is null
	 */
	/*
	 * der Schifftyp wird rückwärts bestimmt, die neueste (letzte) Eingabe gilt
	 */
	public static Integer getType(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // No - iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getType();
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportB().getValid()) {  // ReportB existiert
					return sdr.getReportB().getShipType();
				}
			}
		}
		return Integer.valueOf(0); // == 0 : Not available
	}

	static ShipStaticDataDimension getShipDimension(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // No - iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getDimension();
			} else if (m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) {
				StaticDataReport sdr = (StaticDataReport)m.message;
				if (sdr.getReportB().getValid()) {  // ReportB existiert
					return sdr.getReportB().getDimension();
				}
			}
		}
		return null;
	}

	public static int getShipLength(List<AisStreamMessage> list) {
		ShipStaticDataDimension dim = MmsiMessageList.getShipDimension(list);
		return dim == null ? 0 : dim.getLength();
	}

	public static int getShipWidth(List<AisStreamMessage> list) {
		ShipStaticDataDimension dim = MmsiMessageList.getShipDimension(list);
		return dim == null ? 0 : dim.getWidth();
	}

	/**
	 * Get Maximum present draught from a trace list
	 * @param list a trace list for a vessel
	 * @return the last static draught entered in 1/10 m (can be null f.i. for ClassB vessels)
	 * @throws NullPointerException if the specified list is null
	 */
	public static Double getMaximumStaticDraught(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData)m.message;
				return ssd.getMaximumStaticDraught();
			} 
		}
		return null;
	}

	/**
	 * Get Navigational status of the vessel from a trace list
	 * @param list a trace list for a vessel
	 * @return status (can be null f.i. for ClassB vessels)
	 * @throws NullPointerException if the specified list is null
	 */
	public static Integer getNavigationalStatus(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
				PositionReport pr = (PositionReport)m.message;
				return pr.getNavigationalStatus();
			} 
		}
		return null;
	}

	/**
	 * Get COG (Course Over Ground) from a trace list
	 * @param list a trace list for a vessel
	 * @return the last COG in 1/10 degrees (null if not present)
	 * @throws NullPointerException if the specified list is null
	 */
	public static Double getLastCog(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
				PositionReport pr = (PositionReport)m.message;
				return pr.getCog();
			} else if (m.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT) {
				StandardClassBPositionReport pr = (StandardClassBPositionReport)m.message;
				return pr.getCog();
			} else if (m.getAisMessageType() == AisMessageTypes.EXTENDEDCLASSBPOSITIONREPORT) {
				ExtendedClassBPositionReport pr = (ExtendedClassBPositionReport)m.message;
				return pr.getCog();
			}
		}
		return null;
	}

	/**
	 * Get SOG (Speed Over Ground) from a trace list
	 * @param list a trace list for a vessel
	 * @return the last SOG in 1/10 knots (null if not present)
	 * @throws NullPointerException if the specified list is null
	 */
	public static Double getLastSog(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
//		for (AisStreamMessage m : list) // iterate backwards:
		AisStreamMessage m;
		ListIterator<AisStreamMessage> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			m = listIterator.previous();
			// Ship ClassA ...
			if (m.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
				PositionReport pr = (PositionReport)m.message;
				return pr.getSog();
			} else if (m.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT) {
				StandardClassBPositionReport pr = (StandardClassBPositionReport)m.message;
				return pr.getSog();
			} else if (m.getAisMessageType() == AisMessageTypes.EXTENDEDCLASSBPOSITIONREPORT) {
				ExtendedClassBPositionReport pr = (ExtendedClassBPositionReport)m.message;
				return pr.getSog();
			}
		}
		return null;
	}

	static boolean isShipClassA(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
		for (AisStreamMessage m : list) {
			if (isShipClassA(m)) return true;
		}
		return false;
	}

	static boolean isShipClassA(AisStreamMessage m) {
		if (m.getAisMessageType() == AisMessageTypes.POSITIONREPORT 
		 || m.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) return true;
		return false;
	}

	private boolean isShipClassB(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
		for (AisStreamMessage m : list) {
			if (isShipClassB(m)) return true;
		}
		return false;
	}

	static boolean isShipClassB(AisStreamMessage m) {
		if (m.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT 
		 || m.getAisMessageType() == AisMessageTypes.EXTENDEDCLASSBPOSITIONREPORT
		 || m.getAisMessageType() == AisMessageTypes.STATICDATAREPORT) return true;
		return false;
	}

	static boolean isShip(AisStreamMessage m) {
		return isShipClassA(m) || isShipClassB(m);
	}

	private boolean isBaseStation(List<AisStreamMessage> list) {
		if (list==null) throw new NullPointerException();
		for (AisStreamMessage m : list) {
			if (m.getAisMessageType() == AisMessageTypes.BASESTATIONREPORT) return true;
		}
		return false;
	}
}
