package org.jxmapviewer.demos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.PositionReport;

public class AisMapViewer extends JXMapViewer {

	private static final long serialVersionUID = 8162164255394960216L;
	private static final Logger LOG = Logger.getLogger(AisMapViewer.class.getName());

	private Map<Integer, List<AisStreamMessage>> map;
//	private CompoundPainter<JXMapViewer> painters; 
	private List painters; // TODO List is a raw type. References to generic type List<E> should be parameterized
	
	public AisMapViewer() {
    	super();
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
		painters = new ArrayList<>(); // besser LinkedList
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
//    	GeoPosition location = null;
    	if(msg.getAisMessageType()==AisMessageTypes.SHIPSTATICDATA) {
    		// Pos aus Meta, kein Kurs, o mit Farbe
    	} else {
    		// POSITIONREPORT
    		// Pos + Kurs aus Msg ; > ohne Farbe
    		AisMessage amsg = msg.getAisMessage();
    		if(amsg instanceof PositionReport pr) {
//            	location = new GeoPosition(pr.getLatitude(), pr.getLongitude());
            	RadianceIcon icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M);
            	double theta = pr.getCog(); // Double
            	icon.setRotation(theta);
				WaypointPainter<Waypoint> shipLocationPainter = new WaypointPainter<Waypoint>() {
				public Set<Waypoint> getWaypoints() {
					Set<Waypoint> set = new HashSet<Waypoint>();
					set.add(new DefaultWaypoint(new GeoPosition(pr.getLatitude(), pr.getLongitude())));
					return set;
				}
				};
				int adjustx = icon.getIconWidth()/2;
				int adjusty = icon.getIconHeight()/2;;
				shipLocationPainter.setRenderer(new DefaultWaypointRenderer(adjustx, adjusty, icon));
				painters.add(shipLocationPainter);
				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
		        overlayPainter.setCacheable(false);
		        overlayPainter.setPainters(painters);
		        super.setOverlayPainter(overlayPainter);
    		}
    	}
    }
}
