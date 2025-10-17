/* created from jxmapviewer sample2_waypoints
*/ 
package org.jxmapviewer.demos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import io.github.homebeaver.aismodel.AisStreamMessage;

/**
 * Paints a route
 * @author Martin Steiger
 */
public class RoutePainter implements Painter<JXMapViewer> {
	
	private Color color = Color.RED;
	private boolean antiAlias = true;
//	private int max = 0; // max track waypoints to show

	private List<GeoPosition> track = new ArrayList<GeoPosition>();

	public RoutePainter(Color color) {
		this.color = color;
//		setMaxSize(getTrackSize());
	}

	public RoutePainter(Color color, List<GeoPosition> track) {
		this.color = color;
		this.track = track;
//		setMaxSize(getTrackSize());
	}

	/**
	 * @param track the track
	 */
	public RoutePainter(List<GeoPosition> track) {
		// copy the list so that changes in the
		// original list do not have an effect here
		this.track = new ArrayList<GeoPosition>(track);
	}

	public void setTrack(List<AisStreamMessage> l) {
		if(l==null) return;
		track = new ArrayList<GeoPosition>(l.size());
		l.forEach( i -> {
			track.add(new GeoPosition(i.getMetaData().getLatitude(), i.getMetaData().getLongitude()));
		});
//		setMaxSize(l.size());
	}
	
	public int getTrackSize() {
		return track==null ? -1 : track.size();
	}
	
//	public void setMaxSize(int maxTrackSizeToShow) {
//		max = maxTrackSizeToShow;
//	}
//	public int getMaxSize() {
//		return track==null ? -1 : max;
//	}
	
	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		g = (Graphics2D) g.create();

		// convert from viewport to world bitmap
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		if (antiAlias)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// do the drawing
//		g.setColor(Color.BLACK);
//		g.setStroke(new BasicStroke(4));
//		drawRoute(g, map);

		// do the drawing again with Color color
		g.setColor(color);
		g.setStroke(new BasicStroke(2));
		drawRoute(g, map);

		g.dispose();
	}

	/**
	 * @param g   the graphics object
	 * @param map the map
	 */
	private void drawRoute(Graphics2D g, JXMapViewer map) {
		int lastX = 0;
		int lastY = 0;

		boolean first = true;

//		for(int i=0; i<track.size() &&i<getMaxSize(); i++) {
//			GeoPosition gp = track.get(i);
		for (GeoPosition gp : track) {
			// convert geo-coordinate to world bitmap pixel
			Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

			if (first) {
				first = false;
			} else {
				g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
			}

			lastX = (int) pt.getX();
			lastY = (int) pt.getY();
		}
	}

/*
	private static final List<GeoPosition> DEFAULT_TRACK = Arrays.asList(
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.032975, 12.619913333333333}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.03397833333334, 12.62347}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.0353, 12.628018333333333}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.03670666666667, 12.63423}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.03742666666667, 12.639431666666667}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.037796666666665, 12.64348}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.038109999999996, 12.647508333333333}
//MMSI_String:219230000,ShipName:TYCHO BRAHE,latitude:56.03832666666666, 12.653676666666666}
			new GeoPosition(56.032975, 12.619913333333333), 
			new GeoPosition(56.03397833333334, 12.62347),
			new GeoPosition(56.0353, 12.628018333333333), 
			new GeoPosition(56.03670666666667, 12.63423),
			new GeoPosition(56.03742666666667, 12.639431666666667), 
			new GeoPosition(56.037796666666665, 12.64348),
			new GeoPosition(56.038109999999996, 12.647508333333333),
			new GeoPosition(56.03832666666666, 12.653676666666666)
	);
 */
}