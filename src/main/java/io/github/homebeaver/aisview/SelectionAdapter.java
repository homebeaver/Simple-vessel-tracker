package io.github.homebeaver.aisview;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jxmapviewer.viewer.Waypoint;

/**
 * Creates a selection rectangle based on mouse input
 * <p>
 * Also triggers repaint events in the viewer (draws the selection rectangle)
 * and sets the candidatesToTrack List property (vessels inside the selection rectangle)
 * 
 */
public class SelectionAdapter extends MouseAdapter {

	private static final Logger LOG = Logger.getLogger(SelectionAdapter.class.getName());

	private boolean dragging;
	private AisMapKit viewer;

	private Point2D startPos = new Point2D.Double();
	private Point2D endPos = new Point2D.Double();

	/**
	 * @param viewer the jxmapviewer
	 */
	public SelectionAdapter(AisMapKit viewer) {
		this.viewer = viewer;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON3) return;

		startPos.setLocation(e.getX(), e.getY());
		endPos.setLocation(e.getX(), e.getY());

		dragging = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!dragging) return;

		endPos.setLocation(e.getX(), e.getY());

		viewer.repaint(); // draws the selection rectangle
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!dragging) return;
		if (e.getButton() != MouseEvent.BUTTON3) return;

		viewer.repaint(); // draws the selection rectangle
		
		// welche Schiffe sind ausgewählt?
		viewer.setCandidatesToTrack(findVesselsInSelectedRectangle());

		dragging = false; // end dragging

	}

	private List<Integer> findVesselsInSelectedRectangle() {
		Waypoint[] w = new Waypoint[1];
		List<Integer> mmsiList = new ArrayList<Integer>();
		viewer.locationPainters.forEach( (k,v) -> {
			assert v.getWaypoints().size()==1;
			v.getWaypoints().toArray(w);
			Point2D p = viewer.getMainMap().convertGeoPositionToPoint(w[0].getPosition());
			if (getRectangle().contains(p)) {
				LOG.fine("ausgewählt ist "+k);
				mmsiList.add(k);
			}
		});
		return mmsiList;
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getRectangle() {
		if (dragging) {
			int x1 = (int) Math.min(startPos.getX(), endPos.getX());
			int y1 = (int) Math.min(startPos.getY(), endPos.getY());
			int x2 = (int) Math.max(startPos.getX(), endPos.getX());
			int y2 = (int) Math.max(startPos.getY(), endPos.getY());

			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}

		return null;
	}

}
