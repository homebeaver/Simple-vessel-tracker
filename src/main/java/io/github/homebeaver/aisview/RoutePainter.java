/* created from jxmapviewer sample2_waypoints
*/ 
package io.github.homebeaver.aisview;

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
	
	private Color color = Color.BLACK;
	private boolean antiAlias = true;

	private List<GeoPosition> track = new ArrayList<GeoPosition>();

	public RoutePainter(Color color) {
		this.color = color;
	}

	public RoutePainter(Color color, List<GeoPosition> track) {
		this(color);
		this.track = track;
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
		if (l==null) return;
		track = new ArrayList<GeoPosition>(l.size());
		l.forEach( i -> {
			track.add(new GeoPosition(i.getMetaData().getLatitude(), i.getMetaData().getLongitude()));
		});
	}
	
	public int getTrackSize() {
		return track==null ? -1 : track.size();
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		g = (Graphics2D) g.create();

		// convert from viewport to world bitmap
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		if (antiAlias) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// do the drawing with Color color
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

}