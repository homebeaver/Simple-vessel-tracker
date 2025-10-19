package io.github.homebeaver.aisview;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import dk.dma.ais.message.NavigationalStatus;
import dk.dma.ais.message.ShipTypeCargo;
import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.PositionReport;
import io.github.homebeaver.aismodel.ShipStaticData;
import io.github.homebeaver.aismodel.StandardClassBPositionReport;
import io.github.homebeaver.icon.Circle;
import io.github.homebeaver.icon.Crosshair;
import io.github.homebeaver.icon.Vessel;

public class AisMapViewer extends JXMapViewer {

	private static final long serialVersionUID = 8162164255394960216L;
	private static final Logger LOG = Logger.getLogger(AisMapViewer.class.getName());

	// a list of messages per vessel with MMSI as vessel key
	private Map<Integer, List<AisStreamMessage>> map;
	CompoundPainter<JXMapViewer> overlayPainter;
	
	private static final String MMSITOTRACK_PROPNAME = "mmsiToTrack";
	/**
	 * there is one vessel to track (or nothing)
	 */
	private Integer mmsiToTrack = null;

	RoutePainter routePainter;
	WaypointPainter<Waypoint> crosshairPainter;

	/*
	 * pro Schiff (MMSI) den letzen shipLocationPainter merken, damit kann man in overlayPainter
	 * den letzten shipLocationPainter löschen:
	 *   overlayPainter.removePainter(painters.get(key));
	 * bevor man für die neue Position einen neuen erstellt:
	 * 	 overlayPainter.addPainter(shipLocationPainter);
	 */
	private Map<Integer, WaypointPainter<Waypoint>> locationPainters;
	
	public AisMapViewer() {
		super();
		map = new HashMap<Integer, List<AisStreamMessage>>();
		locationPainters = new HashMap<>();
		
//		super.setOverlayPainter(overlayPainter); // XXX das funktioniert nicht
	}
	public int getNoOfVessels() {
		return map.size();
	}

	// +register for mmsiToTrack
	public List<AisStreamMessage> getVesselTrace(Integer mmsi, PropertyChangeListener listener) {
		if(mmsi==null) {
			super.removePropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			overlayPainter.removePainter(routePainter);
			overlayPainter.removePainter(crosshairPainter);
			return null;
		}
		LOG.info("register "+listener + " for "+mmsi+", Change of Property "+MMSITOTRACK_PROPNAME+".");
		List<AisStreamMessage> ret = map.get(mmsi);
		if(mmsiToTrack==mmsi) {
			// kein neuer routePainter, evtl neuer listener
			super.removePropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			super.addPropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			overlayPainter.removePainter(crosshairPainter);
			crosshairPainter = new VesselWaypointPainter(ret.get(ret.size()-1));
			crosshairPainter.setRenderer(new VesselWaypointRenderer(Crosshair.of(SizingConstants.L, SizingConstants.L)));
			overlayPainter.addPainter(crosshairPainter);
		} else {
			// neuer routePainter, evtl neuer listener
			overlayPainter.removePainter(routePainter);
			routePainter = new RoutePainter(Color.RED);
			routePainter.setTrack(ret);
			overlayPainter.addPainter(routePainter);
			if(ret!=null) {
				overlayPainter.removePainter(crosshairPainter);
				crosshairPainter = new VesselWaypointPainter(ret.get(ret.size()-1));
				crosshairPainter.setRenderer(new VesselWaypointRenderer(Crosshair.of(SizingConstants.L, SizingConstants.L)));
				overlayPainter.addPainter(crosshairPainter);
			}
			super.removePropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			mmsiToTrack = mmsi;
			super.addPropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
		}
		return ret; 
	}
	void setOverlayPainter(CompoundPainter<JXMapViewer> p) {
		overlayPainter = p;
		super.setOverlayPainter(p);
	}
    public void addMessage(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
//		if(key!=219024675 && key!=220434000 && key!=219035097 && key!=219007393) return;
/* nur grosse Potte:

525121076 : SAVIR TIGER
INFORMATION: erste Nachricht ist SHIPSTATICDATA ShipType=Tanker cargo of Undefined, shipLenght=245
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=131.3 type=80 #=2
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=131.5 type=80 #=3
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=131.4 type=80 #=4
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=130.6 type=80 #=5
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=130.5 type=80 #=6
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=130.7 type=80 #=7
INFORMATION: -------------->525121076:  ShipStaticData NavigationalStatus=? cog=130.7 type=80 #=8
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=129.2 type=80 #=9
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=129.2 type=80 #=10
INFORMATION: -------------->525121076:  NavigationalStatus=Under way using engine cog=128.1 type=80 #=11
230706000 : SAANA
INFORMATION: erste Nachricht ist POSITIONREPORT NavigationalStatus=Moored, cog=266.0
INFORMATION: -------------->230706000:  ShipStaticData NavigationalStatus=? cog=266.0 type=80 #=2
INFORMATION: -------------->230706000:  NavigationalStatus=Moored cog=266.0 type=80 #=3
INFORMATION: -------------->230706000:  ShipStaticData NavigationalStatus=? cog=266.0 type=80 #=4
INFORMATION: -------------->230706000:  NavigationalStatus=Moored cog=266.0 type=80 #=5
538010235 : ANIKITOS
INFORMATION: erste Nachricht ist POSITIONREPORT NavigationalStatus=Under way using engine, cog=172.7
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.9 type=null #=2
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.9 type=null #=3
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.8 type=null #=4
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=173.0 type=null #=5
INFORMATION: -------------->538010235:  ShipStaticData NavigationalStatus=? cog=173.0 type=80 #=6
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.6 type=80 #=7
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.7 type=80 #=8
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.2 type=80 #=9
INFORMATION: -------------->538010235:  NavigationalStatus=Under way using engine cog=172.7 type=80 #=10
629009622 : ASTRAL
INFORMATION: erste Nachricht ist POSITIONREPORT NavigationalStatus=Under way using engine, cog=185.0
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=185.0 type=null #=2
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=185.0 type=null #=3
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=187.0 type=null #=4
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=190.0 type=null #=5
INFORMATION: -------------->629009622:  ShipStaticData NavigationalStatus=? cog=190.0 type=89 #=6
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=190.0 type=89 #=7
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=190.0 type=89 #=8
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=188.0 type=89 #=9
INFORMATION: -------------->629009622:  NavigationalStatus=Under way using engine cog=188.0 type=89 #=10
247389200 : AIDANOVA
INFORMATION: erste Nachricht ist POSITIONREPORT NavigationalStatus=Moored, cog=231.2
INFORMATION: -------------->247389200:  ShipStaticData NavigationalStatus=? cog=231.2 type=60 #=2
INFORMATION: -------------->247389200:  NavigationalStatus=Moored cog=231.2 type=60 #=3
INFORMATION: -------------->247389200:  NavigationalStatus=Moored cog=141.2 type=60 #=4

 */
//		if(key!=525121076 && key!=230706000 && key!=538010235 && key!=629009622 && key!=247389200) return;
//		LOG.info(msg.getMetaData().getShipName() + " >>> "+msg.getAisMessageType() + " MMSI="+key);
		// Suche nach cyan Schiffen:
//		if(key!=219024336 && key!=305773000 && key!=219024675 && key!=220476000 && key!=219005904) return;
//		LOG.info(msg.getMetaData().getShipName() + " >>> "+msg.getAisMessageType() + " MMSI="+key);
		if (map.containsKey(key)) {
			// mindestens zweite Nachricht
//			LOG.info("mindestens zweite Nachricht für "+key + " "+msg.getAisMessageType());
			AisMessage amsg = msg.getAisMessage();
			Double cog = null; // Kurs
			Integer heading = null; // True heading Degrees
			Integer type = null; // Schiffstyp
			Integer shipLenght = null;
			Integer navStatus = null;
			List<AisStreamMessage> list = map.get(key);
			Iterator<AisStreamMessage> i = list.iterator();
			int n=1; // n sagt wievielte Nachticht ist es => mindestens die Zweite
			while (i.hasNext()) {
				AisStreamMessage next = i.next();
				n++;
				AisMessage nextamsg = next.getAisMessage();
				if (nextamsg instanceof PositionReport pr) {
					cog = pr.getCog();
					heading = pr.getTrueHeading();
				} else if (nextamsg instanceof StandardClassBPositionReport scbpr) {
					cog = scbpr.getCog();
					heading = scbpr.getTrueHeading();
				} else if (nextamsg instanceof ShipStaticData ssd) {
					type = ssd.getType();
					shipLenght = ssd.getDimension().getLength();
				}
			}
			if (amsg instanceof PositionReport pr) {
				cog = pr.getCog();
				heading = pr.getTrueHeading();
				navStatus = pr.getNavigationalStatus();
			} else if (amsg instanceof StandardClassBPositionReport scbpr) {
				cog = scbpr.getCog();
				heading = scbpr.getTrueHeading();
			} else if (amsg instanceof ShipStaticData ssd) {
				type = ssd.getType();
				shipLenght = ssd.getDimension().getLength();
			}
			LOG.info(""+msg.getAisMessageType()+" "+key+": #="+n
				+ " NavigationalStatus="+(navStatus==null ? "?" : NavigationalStatus.get(navStatus))
				+ ", cog="+cog + ", heading="+heading
				+ ", type="+(type==null ? "?" : new ShipTypeCargo(type)) + ", shipLenght="+shipLenght);
//			list.forEach( m -> { ... NICHT so

			RadianceIcon icon;
			// cog == null ==> nur SHIPSTATICDATAs
			if(cog==null) {
				icon = Circle.of(SizingConstants.XS, SizingConstants.XS);
			} else {
				int zoom = this.getZoom(); // value between 1 and 15
				// ab zoom 6 kleinste icons XS==18
				//int f = 432/48 = 9 Berechnung für zoom 5:
				int iconsize = shipLenght==null ? 0 : shipLenght/9; // XXX
				if(iconsize<18) iconsize = 18;
				icon = Vessel.of(iconsize, iconsize);
				icon.setRotation(cog); // Kurs
			}
			if(type!=null) {
				int shiptype = type;
				icon.setColorFilter(color -> ColorLegend.typeToColor(shiptype)); // ShipType => java Color
			}
			
			WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
			shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
			
			// den zuletzt erstellten shipLocationPainter löschen in overlayPainter und aus locationPainters
			overlayPainter.removePainter(locationPainters.get(key));
			overlayPainter.addPainter(shipLocationPainter);
			locationPainters.replace(key, shipLocationPainter); // den letzten painter wegwerfen/überschreiben
		} else {
			List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
			map.put(key, waypoints); // empty List waypoints 
			// msg ist erste Nachricht für Schiff mit mmsi==key, wir haben entweder Kurs oder StaticData
			display1Vessel(msg);
		}
		List<AisStreamMessage> old = null;
		if(mmsiToTrack!=null && key==mmsiToTrack) {
			old = List.copyOf(map.get(key));
		}
		if(map.get(key).add(msg) && mmsiToTrack!=null && key==mmsiToTrack) {
			LOG.info("firePropertyChange mmsiToTrack="+mmsiToTrack+ " key= "+key+" old value#="+old.size());
			List<AisStreamMessage> l = map.get(key);
			firePropertyChange(MMSITOTRACK_PROPNAME, old, l);
			routePainter.setTrack(l);
			overlayPainter.removePainter(crosshairPainter);
			crosshairPainter = new VesselWaypointPainter(l.get(l.size()-1));
			crosshairPainter.setRenderer(new VesselWaypointRenderer(Crosshair.of(SizingConstants.L, SizingConstants.L)));
			overlayPainter.addPainter(crosshairPainter);
		}

		super.setOverlayPainter(overlayPainter); // setOverlayPainter im ctor reicht nicht
    }

	private void display1Vessel(AisStreamMessage msg) {
/*

data/aisstream.txt : vessels with track (more then 1 waypoints):
	MMSI=265820920:2 SEA4FUN ShipStaticData,PositionReport,
	MMSI=219007393:2 VENUS (GUARD VESSEL) PositionReport,PositionReport,
	MMSI=219024675:3 DANPILOT JULIET PositionReport,PositionReport,ShipStaticData,
	MMSI=219369000:2 LEA ELIZABETH PositionReport,PositionReport,
	MMSI=219000368:2 MERCANDIA IV PositionReport,PositionReport,
	MMSI=220476000:2 FRIGGA PositionReport,ShipStaticData,
	MMSI=219005904:2 AURELIA ShipStaticData,PositionReport,
	MMSI=219024336:2 JEPPE PositionReport,PositionReport,
	MMSI=305773000:2 VOHBURG PositionReport,PositionReport,

 */
		int key = msg.getMetaData().getMMSI();
		assert null==locationPainters.get(key); // expected null
		if (msg.getAisMessageType() == AisMessageTypes.POSITIONREPORT) {
			// POSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			RadianceIcon icon = null;
			if (amsg instanceof PositionReport pr) {
				NavigationalStatus navStatus = NavigationalStatus.get(pr.getNavigationalStatus());
				//if(navStatus==5) { //= moored (festgemacht)
				if(navStatus==NavigationalStatus.UNDER_WAY_USING_ENGINE) { // XXX navStatus nicht in StandardClassBPositionReport
					//icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M); kleiner:
					icon = Vessel.of(18, 18);
					icon.setRotation(pr.getCog()); // Kurs
					//icon.setRotation(pr.getTrueHeading()); // XXX ???
				} else {
					icon = Circle.of(SizingConstants.XS, SizingConstants.XS);
				}
				LOG.info("POSITIONREPORT "+key+": #=1 NavigationalStatus="+navStatus
						+ ", cog="+pr.getCog() + ", type=?, shipLenght=`?");
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				locationPainters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		} else if (msg.getAisMessageType() == AisMessageTypes.STANDARDCLASSBPOSITIONREPORT) {
			// STANDARDCLASSBPOSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			RadianceIcon icon = Vessel.of(18, 18);
			if (amsg instanceof StandardClassBPositionReport scbpr) {
				icon.setRotation(scbpr.getCog()); // Kurs
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				locationPainters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		} else {
			// SHIPSTATICDATA
			// Pos aus Meta, kein Kurs, o mit Farbe
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof ShipStaticData ssd) {
				int shipLenght = ssd.getDimension().getLength();
				ShipTypeCargo stype = new ShipTypeCargo(ssd.getType());
				LOG.info("SHIPSTATICDATA "+key+": #=1 NavigationalStatus=?, cog=null, type="+stype 
						+ ", shipLenght="+shipLenght);
				int iconsize = SizingConstants.XS;
				RadianceIcon icon = Circle.of(iconsize, iconsize);
				icon.setColorFilter(color -> ColorLegend.typeToColor(ssd.getType())); // ShipType => java Color
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				locationPainters.put(key, shipLocationPainter);
				overlayPainter.addPainter(shipLocationPainter);
			}
		}
	}

}
