package org.jxmapviewer.demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Painter;

import org.jdesktop.swingx.demos.svg.FeatheRcircle_blue;
import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.ais.message.ShipTypeColor;
import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.PositionReport;
import io.github.homebeaver.aismodel.ShipStaticData;

public class AisMapViewer extends JXMapViewer {

	private static final long serialVersionUID = 8162164255394960216L;
	private static final Logger LOG = Logger.getLogger(AisMapViewer.class.getName());

/*
// this works for up to 10 elements:
Map<String, String> test1 = Map.of(
    "a", "b",
    "c", "d"
);
 */
	private static Map<ShipTypeColor, Color> typeToColor = Map.of(
		ShipTypeColor.BLUE, Color.BLUE,
		ShipTypeColor.GREY, Color.LIGHT_GRAY,
		ShipTypeColor.GREEN, Color.GREEN,
		ShipTypeColor.ORANGE, Color.ORANGE,
		ShipTypeColor.PURPLE, Color.MAGENTA,
		ShipTypeColor.RED, Color.RED,
		ShipTypeColor.TURQUOISE, Color.CYAN,
		ShipTypeColor.YELLOW, Color.YELLOW
	);

	private Map<Integer, List<AisStreamMessage>> map;
//	CompoundPainter<JXMapViewer> overlayPainter;
	
//	private CompoundPainter<JXMapViewer> painters; 
	// TODO einen Painter aus der Liste entfernen, dazu LIST ==> Map<Integer, Painter>
	// List<WaypointPainter<Waypoint>>> braucht man für die Spur
	// In dierser Version ohne Spur
	private Map<Integer, WaypointPainter<Waypoint>> painters; 
	
	public AisMapViewer() {
    	super();
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		painters = new HashMap<>();
		
//		overlayPainter = new CompoundPainter<JXMapViewer>();
//		overlayPainter.setCacheable(false);
//		overlayPainter.setPainters(painters);
//		super.setOverlayPainter(overlayPainter);
	}
	
//	public void setCompoundPainter(CompoundPainter<JXMapViewer> cp) {
//		painters = cp;
//	}
    
    public void addMessage(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
//		LOG.info(">>>>>>>>>>>>>>>"+msg.getAisMessageType() + " MMSI="+key);
		if (map.containsKey(key)) {
			// mindestens zweite Nachricht
			LOG.info("mindestens zweite Nachricht für "+key);
			// ShipData : TODO letzte Position holen, wenn vorhanden Kurs holen, icon löschen und neu anlegen
			// PositionReport TODO map.get(key) 
			AisMessage amsg = msg.getAisMessage();
			Double cog = null; // Kurs
			Integer type = null; // Schiffstyp
			List<AisStreamMessage> list = map.get(key);
			Iterator<AisStreamMessage> i = list.iterator();
			int n=0;
			while (i.hasNext()) {
				AisStreamMessage next = i.next();
				n++;
				AisMessage nextamsg = next.getAisMessage();
				if (nextamsg instanceof PositionReport pr) {
					cog = pr.getCog();
				} else if (nextamsg instanceof ShipStaticData ssd) {
					type = ssd.getType();
				}
//				LOG.info(">>="+n+" "+key+":"+next.getAisMessageType() + "cog="+cog + "type="+type);
			}
			if (amsg instanceof PositionReport pr) {
				cog = pr.getCog();
			} else if (amsg instanceof ShipStaticData ssd) {
				type = ssd.getType();
//			LOG.info("==============>"+key+": "+ " LAST cog="+cog + " type="+type + " #="+n);
			}
			LOG.info("-------------->"+key+": "+ " cog="+cog + " type="+type + " #="+n);
//			list.forEach( m -> { ... NICHT so

			RadianceIcon icon;
			// cog == null ==> nur SHIPSTATICDATAs
			if(cog==null) {
				icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
			} else {
				icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M);
			}
			if(type!=null) {
				ShipTypeCargo stc = new ShipTypeCargo(type);
				ShipTypeColor c = ShipTypeColor.getColor(stc.getShipType());
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
			}
			// TODO den zuletzt erstellten Painter löschen aus painters
			WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(list, msg);
			shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
			//painters.remove(key); besser:
			painters.replace(key, shipLocationPainter);
			CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
			overlayPainter.setCacheable(false);
//			Arrays a; Arrays.asList(null)
//			WaypointPainter<Waypoint>[] a = (WaypointPainter<Waypoint>[])painters.entrySet().toArray();
//			overlayPainter.setPainters(a); // array of painters (Painter<?>... painters)
			// besser mit List:
			overlayPainter.setPainters(List.copyOf(painters.values()));
			super.setOverlayPainter(overlayPainter);
		} else {
			List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
			map.put(key, waypoints); // empty List waypoints 
			// msg ist erste Nachricht für Schiff key, wir haben keinen Kurs
			// also neues o-Schiff mit Color
			display1Vessel(msg);
		}
		map.get(key).add(msg);
    }

	private void display1Vessel(AisStreamMessage msg) {
		int key = msg.getMetaData().getMMSI();
		assert null==painters.get(key); // expected null
		if (msg.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
			// Pos aus Meta, kein Kurs, o mit Farbe
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof ShipStaticData ssd) {
				LOG.info("erste Nachricht ist "+ssd);
				RadianceIcon icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
				ShipTypeCargo stc = new ShipTypeCargo(ssd.getType());
				ShipTypeColor c = ShipTypeColor.getColor(stc.getShipType());
				icon.setColorFilter(color -> typeToColor.get(c)); // ShipTypeColor => java Color
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
				overlayPainter.setCacheable(false);
//				overlayPainter.setPainters(painters);
//				WaypointPainter<Waypoint>[] a = (WaypointPainter<Waypoint>[])painters.entrySet().toArray();
//				overlayPainter.setPainters(a); // array of painters (Painter<?>... painters)
				// besser mit List:
				overlayPainter.setPainters(List.copyOf(painters.values()));
				super.setOverlayPainter(overlayPainter);
			}
		} else {
			// POSITIONREPORT
			// Pos + Kurs aus Msg ; > ohne Farbe
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof PositionReport pr) {
				int navStatus = pr.getNavigationalStatus();
				RadianceIcon icon = null;
				//if(navStatus==5) { //= moored (festgemacht)
				if(navStatus!=0) { // Under way
					icon = FeatheRcircle_blue.of(SizingConstants.XS, SizingConstants.XS);
				} else {
					//icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M);
					icon = FeatheRnavigation_grey.of(20, 20);
					icon.setRotation(pr.getCog()); // Kurs
				}
				WaypointPainter<Waypoint> shipLocationPainter = new VesselWaypointPainter(msg);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				painters.put(key, shipLocationPainter);
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
				overlayPainter.setCacheable(false);
//				overlayPainter.setPainters(painters);
//				WaypointPainter<Waypoint>[] a = (WaypointPainter<Waypoint>[])painters.values().toArray();
//				overlayPainter.setPainters(a); // array of painters (Painter<?>... painters)
//				// besser:
				overlayPainter.setPainters(List.copyOf(painters.values()));
				super.setOverlayPainter(overlayPainter);
			}
		}
	}

}
