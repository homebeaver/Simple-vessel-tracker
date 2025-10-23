/* created from jxmapviewer sample3_interaction
*/ 
package io.github.homebeaver.aisview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

import javax.swing.Painter;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;

/**
 * Paints a selection rectangle
 * @author Martin Steiger
 */
public class SelectionPainter<T> implements Painter<T> {

	private static final Logger LOG = Logger.getLogger(SelectionPainter.class.getName());

	private Color fillColor = new Color(128, 192, 255, 128);
	private Color frameColor = new Color(0, 0, 255, 128);

	private SelectionAdapter adapter;

	/**
	 * @param adapter the selection adapter
	 */
	public SelectionPainter(SelectionAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void paint(Graphics2D g, T t, int width, int height) {
		Rectangle rc = adapter.getRectangle();

		if (rc != null) {
			g.setColor(frameColor);
			g.draw(rc);
			g.setColor(fillColor);
			g.fill(rc);

/*
            Rectangle bounds = viewer.getViewportBounds();
            int x = bounds.x + evt.getX();
            int y = bounds.y + evt.getY();
            Point pixelCoordinates = new Point(x, y);
            mapClicked(viewer.getTileFactory().pixelToGeo(pixelCoordinates, viewer.getZoom()));

 * /
			Rectangle bounds = adapter.getViewer().getViewportBounds();
			double x = adapter.getStartPos().getX() + bounds.x;
			double y = adapter.getStartPos().getY() + bounds.y;
			Point2D startPoint = new Point2D.Double(x, y);
			Point2D endPoint = new Point2D.Double(adapter.getEndPos().getX() + bounds.x, adapter.getEndPos().getY() + bounds.y);

			//new GeoPosition(55.70, 12.54)); // "København - Øresund"
			TileFactory tf = adapter.getViewer().getTileFactory();
			Point2D topLeft = new Point2D.Double(rc.getX(), rc.getY());
			GeoPosition tl = tf.pixelToGeo(topLeft, adapter.getViewer().getZoom());
			GeoPosition sp = tf.pixelToGeo(startPoint, adapter.getViewer().getZoom());
			GeoPosition ep = tf.pixelToGeo(endPoint, adapter.getViewer().getZoom());
			// einfacher:
			GeoPosition sp2 = adapter.getViewer().convertPointToGeoPosition(adapter.getStartPos());
			GeoPosition ep2 = adapter.getViewer().convertPointToGeoPosition(adapter.getEndPos());
			LOG.info("dregging Rectangle:"+rc + " ViewportBounds:"+adapter.getViewer().getViewportBounds()
				+ "\n Top-Left="+rc.getBounds2D().getX()+","+rc.getBounds2D().getY() + " == " +topLeft + " ~ "+sp + "=="+sp2
				+ "\n Top-Right="+rc.getBounds2D().getMaxX()+","+rc.getBounds2D().getMinY()
				+ "\n Bottom-Left="+rc.getBounds2D().getMinX()+","+rc.getBounds2D().getMaxY()
				+ "\n Bottom-Right="+rc.getBounds2D().getMaxX()+","+rc.getBounds2D().getMaxY() + " ~ "+ep
				);
			//rc.contains(Point2D p) ==> 
/ *
	die Umrechnerei in GeoPosition kann man sich ersparen 
	- mit convertGeoPositionToPoint GeoPosition in Point2D umwandeln p
	- und mit java Mitteln
	Rectangle.contains(p) berechnen


 */
		}
	}
}
