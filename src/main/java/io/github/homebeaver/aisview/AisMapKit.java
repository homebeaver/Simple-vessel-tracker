package io.github.homebeaver.aisview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Painter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.painter.AbstractPainter;
//import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.ExtendedClassBPositionReport;
import io.github.homebeaver.aismodel.MetaData;
import io.github.homebeaver.aismodel.MmsiMessageList;
import io.github.homebeaver.aismodel.PositionReport;
import io.github.homebeaver.aismodel.ShipStaticData;
import io.github.homebeaver.aismodel.StandardClassBPositionReport;
import io.github.homebeaver.aismodel.StaticDataReport;
import io.github.homebeaver.icon.Circle;
import io.github.homebeaver.icon.Crosshair;
import io.github.homebeaver.icon.MapPin;
import io.github.homebeaver.icon.Minus;
import io.github.homebeaver.icon.Plus;
import io.github.homebeaver.icon.Vessel;

/**
 * AisMapKit is a copy of JXMapKit.
 * <p>The JXMapKit is a pair of JXMapViewers preconfigured to be easy to use
 * with common features built in.  This includes zoom buttons, a zoom slider,
 * and a mini-map in the lower right corner showing an overview of the map.
 * Each feature can be turned off using an appropriate
 * <CODE>is<I>X</I>visible</CODE> property. For example, to turn
 * off the minimap call
 * </p>
 *
 * <PRE><CODE>jxMapKit.setMiniMapVisible(false);</CODE></PRE>
 *
 * <p>
 * The JXMapViewer is preconfigured to connect to OpenStreetMap.
 * </p>
 * @author joshy (JXMapKit)
 * @author EUG https://github.com/homebeaver
 */
public class AisMapKit extends JPanel {

	private static final long serialVersionUID = -8366577998349912380L;
	private static final Logger LOG = Logger.getLogger(AisMapKit.class.getName());
	private static final int MINIMAP_ZOOMDIFF = 4;

// >> AisMapKit extension -----------------------------------------------<<
	private static final String MMSITOTRACK_PROPNAME = "mmsiToTrack";
	private static final String CANDIDATESTOTRACK_PROPNAME = "candidatesToTrack";

	/**
	 * there is one vessel to track (or nothing)
	 */
	private Integer mmsiToTrack = null;
	private RoutePainter routePainter;
	private WaypointPainter<Waypoint> crosshairPainter;
	// a large, semi-transparent (alpha=160) gray Crosshair Icon
	private WaypointRenderer<Waypoint> crosshairRenderer() {
		RadianceIcon crosshair = Crosshair.of(Crosshair.L, Crosshair.L);
		crosshair.setColorFilter(color -> new Color(128, 128, 128, 160));
		return new VesselWaypointRenderer(crosshair);
	}

	SelectionAdapter selectionAdapter;
	SelectionPainter<JXMapViewer> selectionPainter;

	private int noOfVesselsClassA = 0;
	// a list of messages per vessel with MMSI as vessel key
	MmsiMessageList mmsiList = new MmsiMessageList(); // TODO init in ctor
	public int getNoOfVessels() {
		return mmsiList.size();
	}
	public int getNoOfVesselsClassA() {
		return noOfVesselsClassA;
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
		List<AisStreamMessage> ret = mmsiList.get(mmsi);
		if(mmsiToTrack==mmsi) {
			// kein neuer routePainter, evtl neuer listener
			super.removePropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			super.addPropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			overlayPainter.removePainter(crosshairPainter);
			crosshairPainter = new VesselWaypointPainter(ret);
			crosshairPainter.setRenderer(crosshairRenderer());
			overlayPainter.addPainter(crosshairPainter);
		} else {
			// neuer routePainter, evtl neuer listener
			overlayPainter.removePainter(routePainter);
//			Integer shipType = mmsiList.getType(mmsi); // color of track ==> BLACK ist besser
//			routePainter = new RoutePainter(shipType==null ? Color.GRAY : ColorLegend.typeToColor(shipType));
			routePainter = new RoutePainter(Color.BLACK);
			routePainter.setTrack(ret);
			overlayPainter.addPainter(routePainter);
			if(ret!=null) {
				overlayPainter.removePainter(crosshairPainter);
				crosshairPainter = new VesselWaypointPainter(ret);
				crosshairPainter.setRenderer(crosshairRenderer());
				overlayPainter.addPainter(crosshairPainter);
			}
			super.removePropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
			mmsiToTrack = mmsi;
			super.addPropertyChangeListener(MMSITOTRACK_PROPNAME, listener);
		}
		return ret;
	}
	/*
	 * pro Schiff (MMSI) den letzen shipLocationPainter merken, damit kann man in overlayPainter
	 * den letzten shipLocationPainter löschen:
	 *   overlayPainter.removePainter(painters.get(key));
	 * bevor man für die neue Position einen neuen erstellt:
	 * 	 overlayPainter.addPainter(shipLocationPainter);
	 */
	Map<Integer, VesselWaypointPainter> locationPainters = new HashMap<>();
	private CompoundPainter<JXMapViewer> overlayPainter;
	void setOverlayPainter(CompoundPainter<JXMapViewer> p) {
		overlayPainter = p;
		this.getMainMap().setOverlayPainter(p);
	}
	/**
	 * called in SwingWorker MessageLoader.process for every data chunks message received
	 * @param msg the new ais message received
	 */
	public void addMessage(AisStreamMessage msg) {
		int mmsi = msg.getMetaData().getMMSI();
		List<AisStreamMessage> old = null;
		if(mmsiToTrack!=null && mmsi==mmsiToTrack) {
			old = List.copyOf(mmsiList.get(mmsi));
		}
		int oldNoOfVessels = getNoOfVessels();
		if (mmsiList.addShipClassA(msg)) {
			// mmsiList is changed - msg was send by a commercial ship (ClassA)
			noOfVesselsClassA = noOfVesselsClassA + getNoOfVessels() - oldNoOfVessels;
		} else {
			if (!isShowAllVessels()) return;
			// to show all vessels (classA and classB) ships:
			if (!mmsiList.addShip(msg)) return;
		}
		List<AisStreamMessage> msgList = mmsiList.get(mmsi);
		// msg send by vessel with mmsi
		if (msgList.size()==1) {
			// erste Nachricht
			AisMessage amsg = msg.getAisMessage();
			if (amsg instanceof ShipStaticData || amsg instanceof StaticDataReport) {
				// colored circle, bei StaticDataReport Part A ohne Color
				Integer shipType = mmsiList.getType(mmsi);
//				Integer shipLenght = map.getShipLength(mmsi);
//				LOG.info("type="+msg.getAisMessageType()+" "+mmsi+": #=1 NavigationalStatus=?, cog=null, shipType="+shipType 
//						+ ", shipLenght="+shipLenght);
				int iconsize = amsg instanceof ShipStaticData ? Circle.XS : 7;
				RadianceIcon icon = Circle.of(iconsize, iconsize);
				if (shipType!=null) {
					icon.setColorFilter(color -> ColorLegend.typeToColor(shipType)); // ShipType => java Color
				}
				VesselWaypointPainter shipLocationPainter = new VesselWaypointPainter(msgList);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				locationPainters.put(mmsi, shipLocationPainter);
				overlayPainter.addOrReplacePainter(shipLocationPainter);
			} else if (amsg instanceof PositionReport
					|| amsg instanceof StandardClassBPositionReport
					|| amsg instanceof ExtendedClassBPositionReport) {
				// Vessel ohne color, fixed size
				int iconsize = amsg instanceof PositionReport ? Vessel.S : Vessel.XS;
				RadianceIcon icon = Vessel.of(iconsize, iconsize);
				icon.setRotation(Math.toRadians(mmsiList.getLastCog(mmsi))); // Kurs in rad
				VesselWaypointPainter shipLocationPainter = new VesselWaypointPainter(msgList);
				shipLocationPainter.setRenderer(new VesselWaypointRenderer(icon));
				locationPainters.put(mmsi, shipLocationPainter);
				overlayPainter.addOrReplacePainter(shipLocationPainter);
			}
		} else {
			// mindestens zweite (kann aber UNKNOWNMESSAGE oder anders sein ... TODO
			Double cog = mmsiList.getLastCog(mmsi); // Kurs
			Integer shipLenght = mmsiList.getShipLength(mmsi);
			RadianceIcon icon;
			// cog == null ==> nur SHIPSTATICDATAs
			if(cog==null) {
				icon = Circle.of(Circle.XS, Circle.XS);
			} else {
				int iconsize = shipLenght==null ? Vessel.XS : shipLenght/10; 
				// TODO iconsize bis zoom 10 OK 
				// würde man statt const 10 den Wert von mainMap.getZoom(), so müssten bei zoomänderung
				// alle shipLocationPainter neu berechnet werden
				if (mmsiList.isShipClassB(mmsi) && iconsize<Vessel.XS) iconsize = Vessel.XS;
				if (mmsiList.isShipClassA(mmsi) && iconsize<Vessel.S) iconsize = Vessel.S;
				icon = Vessel.of(iconsize, iconsize);
				icon.setRotation(Math.toRadians(cog)); // Kurs in rad
			}
			Integer shipType = mmsiList.getType(mmsi);
			if (shipType!=null) {
				icon.setColorFilter(color -> ColorLegend.typeToColor(shipType)); // ShipType => java Color
			}
			VesselWaypointPainter lp = locationPainters.get(mmsi);
			lp.setMsgList(msgList);
			lp.setRenderer(new VesselWaypointRenderer(icon));
			overlayPainter.addOrReplacePainter(lp);
		}
//		if(map.get(key).add(msg) && mmsiToTrack!=null && key==mmsiToTrack) {
		if(mmsiToTrack!=null && mmsi==mmsiToTrack) {
			LOG.info("firePropertyChange mmsiToTrack="+mmsiToTrack+ " key= "+mmsi+" old value#="+old.size());
			List<AisStreamMessage> l = mmsiList.get(mmsi);
			firePropertyChange(MMSITOTRACK_PROPNAME, old, l);
			routePainter.setTrack(l);
			overlayPainter.removePainter(crosshairPainter);
			crosshairPainter = new VesselWaypointPainter(l);
			crosshairPainter.setRenderer(crosshairRenderer());
			overlayPainter.addPainter(crosshairPainter);
		}
		setOverlayPainter(overlayPainter);
	}
	/**
	 * there are a couple vessels as candidates to track (can be empty)
	 */
	private List<MetaData> candidatesToTrack = new Vector<MetaData>();
	void setCandidatesToTrack(List<Integer> candidates) {
		List<MetaData> old = this.candidatesToTrack;
		List<MetaData> neu = new Vector<MetaData>();
		candidates.forEach( i -> {
			neu.add(mmsiList.get(i).get(0).getMetaData());
		});
		this.candidatesToTrack = neu;
		firePropertyChange(CANDIDATESTOTRACK_PROPNAME, old, neu);
	}
	private boolean showAllVessels = false;
	boolean isShowAllVessels() {
		return showAllVessels;
	}
	/**
	 * Sets if only ClassA vessels (commercial) are shown (this is the default).
	 * 
	 * @param showAllVessels true to show all vessels, false to show large commercial vessels only
	 */
	public void setShowAllVessels(boolean showAllVessels) {
		boolean old = this.isShowAllVessels();
		this.showAllVessels = showAllVessels;
		firePropertyChange("showAllVessels", old, this.isShowAllVessels());
	}
// << End of AisMapKit extension -----------------------------------------------<<

	private boolean miniMapVisible = true;
	private boolean zoomSliderVisible = true;
	private boolean zoomButtonsVisible = true;
	private final boolean sliderReversed = false;

	public enum DefaultProviders {
		OpenStreetMaps, Custom
	}

	private DefaultProviders defaultProvider = DefaultProviders.OpenStreetMaps;

	private boolean addressLocationShown = true;

	private boolean dataProviderCreditShown = true;

	protected Icon setZoomOutIcon() {
		return Minus.of(RadianceIcon.XS, RadianceIcon.XS);
	}

	protected Icon setZoomInIcon() {
		return Plus.of(RadianceIcon.XS, RadianceIcon.XS);
	}

	private void initZoomButtons() {
		try {
			this.zoomOutButton.setIcon(setZoomOutIcon());
			this.zoomOutButton.setText("");
		} catch (Throwable thr) {
			LOG.warning(thr.getMessage());
			thr.printStackTrace();
		}
		try {
			this.zoomInButton.setIcon(setZoomInIcon());
			this.zoomInButton.setText("");
		} catch (Throwable thr) {
			LOG.warning(thr.getMessage());
			thr.printStackTrace();
		}
	}

	/**
	 * Creates a new Instance
	 */
	public AisMapKit() {
		initComponents();
		setDataProviderCreditShown(true);

		zoomSlider.setOpaque(false);
		initZoomButtons();

		TileFactoryInfo info = new OSMTileFactoryInfo();
		TileFactory tileFactory = new DefaultTileFactory(info);
		setTileFactory(tileFactory);

		mainMap.setCenterPosition(new GeoPosition(0, 0));
		miniMap.setCenterPosition(new GeoPosition(0, 0));
		mainMap.setRestrictOutsidePanning(true);
		miniMap.setRestrictOutsidePanning(true);

		this.selectionAdapter = new SelectionAdapter(this);
		getMainMap().addMouseMotionListener(selectionAdapter); // to get the selected Rectangle
		getMainMap().addMouseListener(selectionAdapter);
		selectionPainter = new SelectionPainter<>(selectionAdapter);
		addressLocationPainter.setRenderer(new DefaultWaypointRenderer(MapPin.of(MapPin.M, MapPin.M)));
		rebuildMainMapOverlay();

// nicht notwendig:
//		// adapter to move the minimap after the main map has moved 
//		MouseInputAdapter ma = new MouseInputAdapter() {
//			public void mouseReleased(MouseEvent e) {
//				miniMap.setCenterPosition(mapCenterPosition);
//			}
//		};
//		mainMap.addMouseMotionListener(ma);
//		mainMap.addMouseListener(ma);
//
//		// "center" prop in JXMapViewer mainMap changed ==> adjust miniMap (funktioniert auch ohne)
//		mainMap.addPropertyChangeListener("center", pce -> {
//			LOG.info("miniMap.setCenterPosition pce:" + pce);
//			Point2D mapCenter = (Point2D) pce.getNewValue();
//			TileFactory tf = mainMap.getTileFactory();
//			GeoPosition mapPos = tf.pixelToGeo(mapCenter, mainMap.getZoom());
//			miniMap.setCenterPosition(mapPos);
//		});

		// "centerPosition" prop in JXMapViewer mainMap changed ==> adjust miniMap
		mainMap.addPropertyChangeListener("centerPosition", pce -> {
			LOG.finest("miniMap.setCenterPosition + setCenter + repaint pce:" + pce);
			mapCenterPosition = (GeoPosition) pce.getNewValue();
			miniMap.setCenterPosition(mapCenterPosition);
			Point2D pt = miniMap.getTileFactory().geoToPixel(mapCenterPosition, miniMap.getZoom());
			miniMap.setCenter(pt);
			miniMap.repaint();
		});

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mainMap);
        mainMap.addMouseListener(mia);
        mainMap.addMouseMotionListener(mia);

        mainMap.addMouseListener(new CenterMapListener(mainMap));

        mainMap.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mainMap));

        //mainMap.addKeyListener(new PanKeyListener(mainMap));

        // example of usage of map attribution painter,
        // if you want to have it with other painters - use compound painter

        /*AttributionPainter atp = new AttributionPainter();
        atp.setFontColor(...);
        atp.setBackgroundColor(...);
        atp.setFont(...);
        atp.setMargin(...);
        atp.setPosition(...); */

//        mainMap.setOverlayPainter(new AttributionPainter()); // default painter aus 179


        mainMap.addPropertyChangeListener("zoom", propertyChangeEvent -> {
            zoomSlider.setValue(mainMap.getZoom());
            miniMap.setZoom(mainMap.getZoom() + MINIMAP_ZOOMDIFF);
        });

        // an overlay for the mini-map which shows a rectangle representing the main map
        miniMap.setOverlayPainter(new Painter<JXMapViewer>()
        {
            @Override
            public void paint(Graphics2D g, JXMapViewer map, int width, int height)
            {
                // get the viewport rect of the main map
                Rectangle mainMapBounds = mainMap.getViewportBounds();

                // convert to Point2Ds
                Point2D upperLeft2D = mainMapBounds.getLocation();
                Point2D lowerRight2D = new Point2D.Double(upperLeft2D.getX() + mainMapBounds.getWidth(), 
                		upperLeft2D.getY() + mainMapBounds.getHeight());

                // convert to GeoPostions
                GeoPosition upperLeft = mainMap.getTileFactory().pixelToGeo(upperLeft2D, mainMap.getZoom());
                GeoPosition lowerRight = mainMap.getTileFactory().pixelToGeo(lowerRight2D, mainMap.getZoom());

                // convert to Point2Ds on the mini-map
                upperLeft2D = map.getTileFactory().geoToPixel(upperLeft, map.getZoom());
                lowerRight2D = map.getTileFactory().geoToPixel(lowerRight, map.getZoom());

                g = (Graphics2D) g.create();
                Rectangle rect = map.getViewportBounds();
                // p("rect = " + rect);
                g.translate(-rect.x, -rect.y);
//                Point2D centerpos = map.getTileFactory().geoToPixel(mapCenterPosition, map.getZoom());
                // p("center pos = " + centerpos);
                g.setPaint(Color.RED);
                // g.drawRect((int)centerpos.getX()-30,(int)centerpos.getY()-30,60,60);
                g.drawRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(),
                        (int) (lowerRight2D.getX() - upperLeft2D.getX()),
                        (int) (lowerRight2D.getY() - upperLeft2D.getY()));
                g.setPaint(new Color(255, 0, 0, 50));
                g.fillRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(),
                        (int) (lowerRight2D.getX() - upperLeft2D.getX()),
                        (int) (lowerRight2D.getY() - upperLeft2D.getY()));
                // g.drawOval((int)lowerRight2D.getX(),(int)lowerRight2D.getY(),1,1);
                g.dispose();
            }
        });

        if (getDefaultProvider() == DefaultProviders.OpenStreetMaps)
        {
            setZoom(10);
        }
        else
        {
            setZoom(3);// joshy: hack, i shouldn't need this here
        }
        this.setCenterPosition(new GeoPosition(0, 0));
    }

	// private Point2D mapCenter = new Point2D.Double(0,0);
	private GeoPosition mapCenterPosition = new GeoPosition(0, 0);
	private boolean zoomChanging = false;

	/**
	 * Set the current zoomlevel for the main map. 
	 * The minimap will be updated accordingly
	 * 
	 * @param zoom the new zoom level
	 */
	public void setZoom(int zoom) {
		zoomChanging = true;
		mainMap.setZoom(zoom);
		miniMap.setZoom(mainMap.getZoom() + MINIMAP_ZOOMDIFF);
		if (sliderReversed) {
			zoomSlider.setValue(zoomSlider.getMaximum() - zoom);
		} else {
			zoomSlider.setValue(zoom);
		}
		zoomChanging = false;
	}

	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		mainMap = new JXMapViewer();
		miniMap = new JXMapViewer();
		jPanel1 = new JPanel();
		zoomInButton = new JButton();
		zoomOutButton = new JButton();
		zoomSlider = new JSlider();

		setLayout(new GridBagLayout());

		mainMap.setLayout(new GridBagLayout());

		miniMap.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		miniMap.setMinimumSize(new Dimension(100, 100));
		miniMap.setPreferredSize(new Dimension(100, 100));
		miniMap.setLayout(new GridBagLayout());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		mainMap.add(miniMap, gridBagConstraints);

		jPanel1.setOpaque(false);
		jPanel1.setLayout(new GridBagLayout());
//		jPanel1.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED)); // to show the jPanel1

		zoomInButton.setIcon(setZoomInIcon());
		zoomInButton.setMargin(new Insets(2, 2, 2, 2));
		zoomInButton.setMaximumSize(new Dimension(20, 20));
		zoomInButton.setMinimumSize(new Dimension(20, 20));
		zoomInButton.setOpaque(false);
		zoomInButton.setPreferredSize(new Dimension(20, 20));
		zoomInButton.addActionListener(evt -> {
			setZoom(mainMap.getZoom() - 1);
			zoomInButtonActionPerformed(evt);
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(zoomInButton, gridBagConstraints);

		zoomOutButton.setIcon(setZoomOutIcon());
		zoomOutButton.setMargin(new Insets(2, 2, 2, 2));
		zoomOutButton.setMaximumSize(new Dimension(20, 20));
		zoomOutButton.setMinimumSize(new Dimension(20, 20));
		zoomOutButton.setOpaque(false);
		zoomOutButton.setPreferredSize(new Dimension(20, 20));
		zoomOutButton.addActionListener(evt -> {
			setZoom(mainMap.getZoom() + 1);
			zoomOutButtonActionPerformed(evt);
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weighty = 1.0;
		jPanel1.add(zoomOutButton, gridBagConstraints);

		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setMaximum(15);
		zoomSlider.setMinimum(10);
		zoomSlider.setMinorTickSpacing(1);
		zoomSlider.setOrientation(SwingConstants.VERTICAL);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMinimumSize(new Dimension(35, 100));
		zoomSlider.setPreferredSize(new Dimension(35, 190));
		zoomSlider.addChangeListener(changeEvent -> {
			zoomSliderStateChanged(changeEvent);
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		jPanel1.add(zoomSlider, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		mainMap.add(jPanel1, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		startStop = new StartStopComponent(null);
		mainMap.add(startStop, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(mainMap, gridBagConstraints);
	}

	private void zoomInButtonActionPerformed(ActionEvent evt) {
		// TODO add your handling code here:
	}
	private void zoomOutButtonActionPerformed(ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void zoomSliderStateChanged(ChangeEvent evt) {
		if (!zoomChanging) {
			setZoom(zoomSlider.getValue());
		}
	}

	private JPanel jPanel1;
	private JPanel startStop;
	private JXMapViewer mainMap;
	// in mainMap :   private Painter<? super JXMapViewer> overlay;
	private JXMapViewer miniMap;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JSlider zoomSlider;

	public JPanel getStartStop() {
		return startStop;
	}
	/**
	 * Indicates if the mini-map is currently visible
	 * 
	 * @return the current value of the mini-map property
	 */
	public boolean isMiniMapVisible() {
		return miniMapVisible;
	}

	/**
	 * Sets if the mini-map should be visible
	 * 
	 * @param miniMapVisible a new value for the miniMap property
	 */
	public void setMiniMapVisible(boolean miniMapVisible) {
		boolean old = this.isMiniMapVisible();
		this.miniMapVisible = miniMapVisible;
		miniMap.setVisible(miniMapVisible);
		firePropertyChange("miniMapVisible", old, this.isMiniMapVisible());
	}

	/**
	 * Indicates if the zoom slider is currently visible
	 * 
	 * @return the current value of the zoomSliderVisible property
	 */
	public boolean isZoomSliderVisible() {
		return zoomSliderVisible;
	}

	/**
	 * Sets if the zoom slider should be visible
	 * 
	 * @param zoomSliderVisible the new value of the zoomSliderVisible property
	 */
	public void setZoomSliderVisible(boolean zoomSliderVisible) {
		boolean old = this.isZoomSliderVisible();
		this.zoomSliderVisible = zoomSliderVisible;
		zoomSlider.setVisible(zoomSliderVisible);
		firePropertyChange("zoomSliderVisible", old, this.isZoomSliderVisible());
	}

	/**
	 * Indicates if the zoom buttons are visible. 
	 * This is a bound property and can be listed for using a PropertyChangeListener
	 * 
	 * @return current value of the zoomButtonsVisible property
	 */
	public boolean isZoomButtonsVisible() {
		return zoomButtonsVisible;
	}

	/**
	 * Sets if the zoom buttons should be visible. 
	 * This is a bound property and can be listed for using a PropertyChangeListener
	 * 
	 * @param zoomButtonsVisible new value of the zoomButtonsVisible property
	 */
	public void setZoomButtonsVisible(boolean zoomButtonsVisible) {
		boolean old = this.isZoomButtonsVisible();
		this.zoomButtonsVisible = zoomButtonsVisible;
		zoomInButton.setVisible(zoomButtonsVisible);
		zoomOutButton.setVisible(zoomButtonsVisible);
		firePropertyChange("zoomButtonsVisible", old, this.isZoomButtonsVisible());
	}

	/**
	 * Sets the tile factory for both embedded JXMapViewer components. 
	 * Calling this method will also reset the center and zoom levels of both maps, 
	 * as well as the bounds of the zoom slider.
	 * 
	 * @param fact the new TileFactory
	 */
	public void setTileFactory(TileFactory fact) {
		mainMap.setTileFactory(fact);
		mainMap.setZoom(fact.getInfo().getDefaultZoomLevel());
		mainMap.setCenterPosition(new GeoPosition(0, 0));
		miniMap.setTileFactory(fact);
		miniMap.setZoom(fact.getInfo().getDefaultZoomLevel() + 3);
		miniMap.setCenterPosition(new GeoPosition(0, 0));
		zoomSlider.setMinimum(fact.getInfo().getMinimumZoomLevel());
		zoomSlider.setMaximum(fact.getInfo().getMaximumZoomLevel());
	}

	/**
	 * @param pos the new center position
	 */
	public void setCenterPosition(GeoPosition pos) {
		mainMap.setCenterPosition(pos);
		miniMap.setCenterPosition(pos);
	}

	/**
	 * @return the center geo position
	 */
	public GeoPosition getCenterPosition() {
		return mainMap.getCenterPosition();
	}

	/**
	 * @return the adress location
	 */
	public GeoPosition getAddressLocation() {
		return mainMap.getAddressLocation();
	}

	/**
	 * @param pos the address location
	 */
	public void setAddressLocation(GeoPosition pos) {
		mainMap.setAddressLocation(pos);
	}

	/**
	 * Returns a reference to the main embedded JXMapViewer component
	 * 
	 * @return the main map
	 */
	public JXMapViewer getMainMap() {
		return this.mainMap;
	}

	/**
	 * Returns a reference to the mini embedded JXMapViewer component
	 * 
	 * @return the minimap JXMapViewer component
	 */
	public JXMapViewer getMiniMap() {
		return this.miniMap;
	}

	/**
	 * returns a reference to the zoom in button
	 * 
	 * @return a jbutton
	 */
	public JButton getZoomInButton() {
		return this.zoomInButton;
	}

	/**
	 * returns a reference to the zoom out button
	 * 
	 * @return a jbutton
	 */
	public JButton getZoomOutButton() {
		return this.zoomOutButton;
	}

	/**
	 * returns a reference to the zoom slider
	 * 
	 * @return a jslider
	 */
	public JSlider getZoomSlider() {
		return this.zoomSlider;
	}

	/**
	 * @param b the visibility flag
	 */
	public void setAddressLocationShown(boolean b) {
		boolean old = isAddressLocationShown();
		this.addressLocationShown = b;
		addressLocationPainter.setVisible(b);
		firePropertyChange("addressLocationShown", old, b);
		repaint();
	}

	/**
	 * @return true if the address location is shown
	 */
	public boolean isAddressLocationShown() {
		return addressLocationShown;
	}

	/**
	 * @param b the visibility flag
	 */
	public void setDataProviderCreditShown(boolean b) {
		boolean old = isDataProviderCreditShown();
		this.dataProviderCreditShown = b;
		dataProviderCreditPainter.setVisible(b);
		repaint();
		firePropertyChange("dataProviderCreditShown", old, b);
	}

	/**
	 * @return true if the data provider credit is shown
	 */
	public boolean isDataProviderCreditShown() {
		return dataProviderCreditShown;
	}

	private void rebuildMainMapOverlay() {
		CompoundPainter<JXMapViewer> cp = new CompoundPainter<JXMapViewer>();

		// setOverlayPainter will einen painter; das kann ein CompoundPainter painter sein
		// den wir mit allen notwendigen paintern befüllen (kein painter darf null sein => NPE):
//		cp.setPainters(dataProviderCreditPainter, addressLocationPainter, selectionPainter);
// das führt zu UnsupportedOperationException bei overlayPainter.addPainter(shipLocationPainter) XXX ?

		// Alternativ statt CompoundPainter eine Liste von Paintern, die sich erweitern lässt:
		List<Painter<JXMapViewer>> painters = new ArrayList<>();
		painters.add(dataProviderCreditPainter);
		painters.add(addressLocationPainter);
		painters.add(selectionPainter);
		cp.setPainters(painters);

		setOverlayPainter(cp);
	}

	/**
	 * @param prov the default provider
	 */
	public void setDefaultProvider(DefaultProviders prov) {
		DefaultProviders old = this.defaultProvider;
		this.defaultProvider = prov;
		if (prov == DefaultProviders.OpenStreetMaps) {
			TileFactoryInfo info = new OSMTileFactoryInfo();
			TileFactory tf = new DefaultTileFactory(info);
			setTileFactory(tf);
			setZoom(11);
			setAddressLocation(new GeoPosition(51.5, 0));
		}
		firePropertyChange("defaultProvider", old, prov);
		repaint();
	}

	/**
	 * @return the default provider
	 */
	public DefaultProviders getDefaultProvider() {
		return this.defaultProvider;
	}

	private AbstractPainter<JXMapViewer> dataProviderCreditPainter = new AbstractPainter<JXMapViewer>(false) {
		@Override
		protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
			g.setPaint(Color.BLACK);
			g.drawString("Map data from OpenStreetMap", 50, map.getHeight() - 10);
		}
	};

	/**
	 * @return the dataProviderCreditPainter
	 */
	public AbstractPainter<JXMapViewer> getDataProviderCreditPainter() {
		return dataProviderCreditPainter;
	}

	private WaypointPainter<Waypoint> addressLocationPainter = new WaypointPainter<Waypoint>() {
		@Override
		public Set<Waypoint> getWaypoints() {
			Set<Waypoint> set = new HashSet<Waypoint>();
			if (getAddressLocation() != null) {
				set.add(new DefaultWaypoint(getAddressLocation()));
			} else {
				set.add(new DefaultWaypoint(0, 0));
			}
			return set;
		}
	};

}
