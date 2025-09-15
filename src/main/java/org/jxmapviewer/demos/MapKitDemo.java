/* created from jxmapviewer sample6_mapkit
*/ 
package org.jxmapviewer.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.MutableComboBoxModel;
import javax.swing.Painter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXFrame.StartPosition;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.demos.svg.FeatheRflag;
import org.jdesktop.swingx.demos.svg.FeatheRnavigation_grey;
import org.jdesktop.swingx.icon.ChevronIcon;
import org.jdesktop.swingx.icon.PlayIcon;
//import org.jdesktop.swingx.icon.PlayIcon;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
//import org.pushingpixels.trident.api.Timeline;

import dk.dma.ais.message.ShipTypeCargo;
import dk.dma.ais.message.ShipTypeColor;
import dk.dma.enav.model.geometry.Position;
import io.github.homebeaver.aismodel.AisMessage;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MessageReader;
import io.github.homebeaver.aismodel.PositionReport;
import io.github.homebeaver.aismodel.ShipStaticData;
import swingset.AbstractDemo;

/**
 * A demo for the {@code JXMapKit}.
 *
 * @author Martin Steiger
 * @author EUG https://github.com/homebeaver (integrate to SwingSet3)
 */
public class MapKitDemo extends AbstractDemo { // AbstractDemo extends JXPanel
	
	private static final long serialVersionUID = 1811042967620854708L;
	private static final Logger LOG = Logger.getLogger(MapKitDemo.class.getName());
	private static final String DESCRIPTION = "Demonstrates JXMapKit that shows a map with zoom slider and a mini-map";

    /**
     * main method allows us to run as a standalone demo.
     * @param args params
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater( () -> {
			JXFrame controller = new JXFrame("controller", exitOnClose);
			AbstractDemo demo = new MapKitDemo(controller);
			JXFrame frame = new JXFrame(DESCRIPTION, exitOnClose);
			frame.setStartPosition(StartPosition.CenterInScreen);
        	frame.getContentPane().add(demo);
        	frame.pack();
        	frame.setVisible(true);
			
			controller.getContentPane().add(demo.getControlPane());
			controller.pack();
			controller.setVisible(true);
    	});
    }

	private static final int DEFAULT_ZOOM = 10; // OSM MAX_ZOOM is 19;
	private static final String DEFAULT_POS = "København - Øresund";
	private TileFactoryInfo info;
	private JXMapKit mapKit;
	private SelectionAdapter selectionAdapter;
//    private RoutePainter routePainter = new RoutePainter(Color.RED);
	private SelectionPainter selectionPainter;
	private List painters;
	private CompoundPainter<JXMapViewer> overlayPainter;

    // controller:
    private JComboBox<DisplayInfo<GeoPosition>> positionChooserCombo;
    private JCheckBox drawTileBorder;
    private JCheckBox miniMapVisible;
    private JSlider zoomSlider; // JSlider extends JComponent
    // controller prop name
//	private static final String SLIDER = "zoomSlider";
    private JButton zoomOut;
    private JButton zoomIn;
//    private JSlider trackSlider;
    // Animation
    private JButton animation;
//    Timeline timeline;
//    public void setTrackProp(float newValue) {
//    	trackSlider.setValue((int)(newValue*routePainter.getTrackSize()+0.5));
//    }

    /**
     * Demo Constructor
     * 
     * @param frame controller Frame
     */
    public MapKitDemo(Frame frame) {
    	super(new BorderLayout());
    	frame.setTitle(getBundleString("frame.title", DESCRIPTION));
    	super.setPreferredSize(PREFERRED_SIZE);
    	super.setBorder(new BevelBorder(BevelBorder.LOWERED));

        // Create a TileFactoryInfo for OpenStreetMap
        info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapKit
        mapKit = new JXMapKit() {
            protected Icon setZoomOutIcon() {
//            	return FeatheRminus.of(SizingConstants.XS, SizingConstants.XS);
            	// use "v" instead of "-" 
            	return ChevronIcon.of(RadianceIcon.XS, RadianceIcon.XS);
            }
            protected Icon setZoomInIcon() {
            	RadianceIcon icon = ChevronIcon.of(RadianceIcon.XS, RadianceIcon.XS);
            	icon.setRotation(RadianceIcon.SOUTH);
            	return icon;
//            	return FeatheRplus.of(SizingConstants.XS, SizingConstants.XS);
            }
        };
        
        // sync zoomSlider:
        mapKit.getZoomSlider().addChangeListener(changeEvent -> {
        	if(zoomSlider!=null) zoomSlider.setValue(mapKit.getZoomSlider().getValue());
        });
        mapKit.getZoomOutButton().addChangeListener(changeEvent -> {
        	if(zoomSlider!=null) zoomSlider.setValue(mapKit.getZoomSlider().getValue());
        });
        mapKit.getZoomInButton().addChangeListener(changeEvent -> {
        	if(zoomSlider!=null) zoomSlider.setValue(mapKit.getZoomSlider().getValue());
        });
        
        mapKit.setName("mapKit");
        mapKit.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the zoom and focus to DEFAULT_POS / Oeresund
        mapKit.setZoom(DEFAULT_ZOOM);
        mapKit.setAddressLocation(nameToGeoPosition.get(DEFAULT_POS));
//        mapKit.getMainMap().setRestrictOutsidePanning(true); // ???
//        mapKit.getMainMap().setHorizontalWrapped(false);

        // Add interactions / verschieben , zoomen , select
// "Use left mouse button to pan, mouse wheel to zoom and right mouse to select";
//        mapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
        MouseInputListener mia = new PanMouseInputListener(mapKit.getMainMap());
        mapKit.addMouseListener(mia);
        mapKit.addMouseMotionListener(mia);

        mapKit.addMouseListener(new CenterMapListener(mapKit.getMainMap()));

        mapKit.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapKit.getMainMap()));

        mapKit.addKeyListener(new PanKeyListener(mapKit.getMainMap()));

        selectionAdapter = new SelectionAdapter(mapKit.getMainMap());
        // Add painter
        mapKit.getMainMap().addMouseListener(selectionAdapter);
        mapKit.getMainMap().addMouseMotionListener(selectionAdapter);
        selectionPainter = new SelectionPainter(selectionAdapter);
        addressLocationPainter.setRenderer(new DefaultWaypointRenderer(4*SizingConstants.M/SizingConstants.M, SizingConstants.M
        		, FeatheRflag.of(SizingConstants.M, SizingConstants.M)));
        painters = new ArrayList<>(); // besser LinkedList
        painters.add(addressLocationPainter);
        painters.add(selectionPainter);
        //CompoundPainter<JXMapViewer> 
        overlayPainter = new CompoundPainter<JXMapViewer>();
        overlayPainter.setCacheable(false);
//        overlayPainter.setPainters(addressLocationPainter, selectionPainter);
        overlayPainter.setPainters(painters);
        //public void setPainters(List<? extends Painter<T>> painters) {
        mapKit.getMainMap().setOverlayPainter(overlayPainter);

        LOG.info("isAddressLocationShown():"+mapKit.isAddressLocationShown());
        
        add(mapKit);
        
        mapKit.getMainMap().addPropertyChangeListener("zoom", pce -> {
//        	LOG.info("---------------------pce:"+pce);
        	getPosAndZoom();
        });
        mapKit.getMainMap().addPropertyChangeListener("center", pce -> {
        	GeoPosition pos = getPosAndZoom();
        	mapKit.setCenterPosition(pos);
        });
        getPosAndZoom();
        
        mapKit.getMainMap().addMouseListener(new AddNavigationIcon(mapKit.getMainMap(), painters));
/* TODO zum Herausfinden der Ecken:
NW: GeoPosition:[56.15625856755953, 11.612548828125] (56 09.376N, 011 36.753E)
NE: GeoPosition:[56.15625856755953, 13.458251953125] (56 09.376N, 013 27.495E)
SW: GeoPosition:[55.24311788040884, 11.612548828125] (55 14.587N, 011 36.753E)
mit NE + SW kann man die AIS Positionsmeldungen abfragen
 */
//        start();
    }

//    public void createAnimation(long duration, float to) {
//    	Timeline.builder(this)
//			.addPropertyToInterpolate("trackProp", 0.0f, to)
//			.setDuration(duration)
//			.play(); // show track animated
//    	
//    	timeline = Timeline.builder(this)
//			.addPropertyToInterpolate("trackProp", 0.0f, 1.0f)
//			.setDuration(duration)
//			.build();
//    	LOG.info("Animation Duration = " + timeline.getDuration());
//    }

    // from JXMapKit
	private WaypointPainter<Waypoint> addressLocationPainter = new WaypointPainter<Waypoint>() {
		@Override
		public Set<Waypoint> getWaypoints() {
			Set<Waypoint> set = new HashSet<Waypoint>();
			if (mapKit.getMainMap().getAddressLocation() != null) {
				set.add(new DefaultWaypoint(mapKit.getMainMap().getAddressLocation()));
			} else {
				set.add(new DefaultWaypoint(0, 0));
			}
			return set;
		}
	};

    private GeoPosition getPosAndZoom() {
        double lat = mapKit.getCenterPosition().getLatitude();
        double lon = mapKit.getCenterPosition().getLongitude();
        int zoom = mapKit.getZoomSlider().getValue();
        if(zoomSlider!=null) zoomSlider.setValue(zoom);

//        LOG.info(String.format("Lat/Lon=(%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
        return new GeoPosition(lat, lon);
    }

    @Override
	public JXPanel getControlPane() {
		@SuppressWarnings("serial")
		JXPanel controls = new JXPanel() {
			public Dimension getMaximumSize() {
				return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
			}
		};
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

		JXLabel selectLabel = new JXLabel("select another location:");
		selectLabel.setName("selectLabel");
		selectLabel.setText(getBundleString("selectLabel.text"));
		selectLabel.setAlignmentX(JXLabel.LEFT_ALIGNMENT);
		controls.add(selectLabel);

        // Create the combo chooser box:
		positionChooserCombo = new JComboBox<DisplayInfo<GeoPosition>>();
		positionChooserCombo.setName("positionChooserCombo");
		positionChooserCombo.setModel(createCBM());
//		positionChooserCombo.setAlignmentX(JXComboBox.LEFT_ALIGNMENT);
//        ComboBoxRenderer renderer = new ComboBoxRenderer(); wie in MirroringIconDemo mit Flagge TODO
		
		positionChooserCombo.addActionListener(ae -> {
			int index = positionChooserCombo.getSelectedIndex();
			DisplayInfo<GeoPosition> item = (DisplayInfo<GeoPosition>)positionChooserCombo.getSelectedItem();
			LOG.info("Combo.SelectedItem=" + item.getDescription());
			mapKit.setAddressLocation(item.getValue());
			mapKit.setZoom(DEFAULT_ZOOM);
	        zoomSlider.setValue(DEFAULT_ZOOM);
			positionChooserCombo.setSelectedIndex(index);
		});
		controls.add(positionChooserCombo);
		selectLabel.setLabelFor(positionChooserCombo);
		controls.add(Box.createRigidArea(VGAP15));

        drawTileBorder = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
        drawTileBorder.setSelected(true);
        mapKit.getMainMap().setDrawTileBorders(drawTileBorder.isSelected());
        drawTileBorder.setName("drawTileBorder");
        drawTileBorder.setText(getBundleString("drawTileBorder.text"));
        drawTileBorder.addActionListener( ae -> {
        	mapKit.getMainMap().setDrawTileBorders(drawTileBorder.isSelected());
        });
        controls.add(drawTileBorder);

        miniMapVisible = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
        miniMapVisible.setSelected(true);       
        mapKit.setMiniMapVisible(miniMapVisible.isSelected());
        miniMapVisible.setName("miniMapVisible");
        miniMapVisible.setText(getBundleString("miniMapVisible.text"));
        miniMapVisible.addActionListener( ae -> {
            mapKit.setMiniMapVisible(miniMapVisible.isSelected());
        });
        controls.add(miniMapVisible);

        // to fill up the remaining space
		JPanel fill = new JPanel();
		fill.setOpaque(false);
		fill.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
		fill.add(makeZoomSlider(), gridBagConstraints);		
		
		gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        zoomOut = new JButton();
        zoomOut.setName("zoomOut");
        zoomOut.setText(getBundleString("zoomOut.text"));
        zoomOut.setIcon(ChevronIcon.of(RadianceIcon.XS, RadianceIcon.XS));
        zoomOut.addActionListener( ae -> {
	    	mapKit.setZoom(zoomSlider.getValue()+1);
		    zoomSlider.setValue(mapKit.getZoomSlider().getValue());
        });
    	fill.add(zoomOut, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    	zoomIn = new JButton();
    	zoomIn.setName("zoomIn");
    	zoomIn.setText(getBundleString("zoomIn.text"));
    	zoomIn.setHorizontalTextPosition(SwingConstants.LEFT);
    	RadianceIcon icon = ChevronIcon.of(RadianceIcon.XS, RadianceIcon.XS);
    	icon.setRotation(RadianceIcon.SOUTH);
    	zoomIn.setIcon(icon);
    	zoomIn.addActionListener( ae -> {
	    	mapKit.setZoom(zoomSlider.getValue()-1);
		    zoomSlider.setValue(mapKit.getZoomSlider().getValue());
        });
    	fill.add(zoomIn, gridBagConstraints);

		controls.add(fill, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
		JXLabel trackLabel = new JXLabel("show track:");
		trackLabel.setName("trackLabel");
		trackLabel.setText(getBundleString("trackLabel.text"));
		fill.add(trackLabel, gridBagConstraints);		

		gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
//		fill.add(makeTrackSlider(), gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        animation = new JButton();
        animation.setName("animation");
        animation.setText(getBundleString("animation.text"));
        animation.setIcon(PlayIcon.of(RadianceIcon.XS, RadianceIcon.XS));
        animation.addActionListener( ae -> {
//        	setTrackProp(0f);
//        	timeline.play(); // show track animated
//        	start();
        	animation.setEnabled( false );
            // Starte Extra-Thread per SwingWorker, 
        	//damit der Event Dispatch Thread (EDT) nicht blockiert wird:
//            (new TextDateiLesenSwingWorker( textdatei.getText(), charEncod.getSelectedItem().toString(), button, textArea ))
//            .execute();
        	(new MessageLoader(null, null)).execute();
        });
    	fill.add(animation, gridBagConstraints);

		return controls;
	}

//    private JComponent makeTrackSlider() {  	
//    	if(trackSlider!=null) {
//    		LOG.warning("already instantiated "+trackSlider);
//    		return trackSlider;
//    	}
//    	trackSlider = new JSlider();
//    	trackSlider.setName("trackSlider");
//    	trackSlider.setOpaque(false);
//	    //zoomSlider.setPaintLabels(true);
//    	trackSlider.setMinimum(0);
//    	trackSlider.setMaximum(routePainter.getTrackSize());
//    	trackSlider.setValue(trackSlider.getMaximum()/2);
//    	trackSlider.addChangeListener(changeEvent -> {
//    		routePainter.setMaxSize(trackSlider.getValue());
//    		repaint();
//	    });
//    	trackSlider.setPaintTicks(false);
////    	trackSlider.setMajorTickSpacing(1);
//		return trackSlider;
//    }

    private JComponent makeZoomSlider() {  	
    	if(zoomSlider!=null) {
    		LOG.warning("already instantiated "+zoomSlider);
    		return zoomSlider;
    	}
//	    LOG.info("min/max/zoom:"+info.getMinimumZoomLevel()+" "+info.getMaximumZoomLevel()+" "+mapViewer.getZoom());
//	    zoomSlider = new JSlider(JSlider.HORIZONTAL, info.getMinimumZoomLevel(), info.getMaximumZoomLevel(), mapKit.getZoomSlider().getValue());
	    zoomSlider = new JSlider();
	    zoomSlider.setName("zoomSlider");
	    zoomSlider.setOpaque(false);
	    //zoomSlider.setPaintLabels(true);
	    zoomSlider.setMinimum(info.getMinimumZoomLevel());
	    zoomSlider.setMaximum(info.getMaximumZoomLevel());
	    zoomSlider.setValue(mapKit.getZoomSlider().getValue());
	    zoomSlider.addChangeListener(changeEvent -> {
	    	mapKit.setZoom(zoomSlider.getValue());
	    });
	    zoomSlider.setPaintTicks(true);
	    zoomSlider.setMajorTickSpacing(1);
		return zoomSlider;
    }
    
    private ComboBoxModel<DisplayInfo<GeoPosition>> createCBM() {
        MutableComboBoxModel<DisplayInfo<GeoPosition>> model = new DefaultComboBoxModel<DisplayInfo<GeoPosition>>();
        nameToGeoPosition.forEach((k,v) -> {
        	model.addElement(new DisplayInfo<GeoPosition>(k, v));
        });
        return model;
    }

    @SuppressWarnings("serial")
	private static final Map<String, GeoPosition> nameToGeoPosition = new HashMap<>(){
        { // oordinaten: 	♁55° 41′ N, 12° 35′ OKoordinaten: 55° 41′ N, 12° 35′ O | | OSM
            put("Berlin",            new GeoPosition(52,31,0, 13,24,0));
            put("Darmstadt",         new GeoPosition(49,52,0,  8,39,0));
            put("Frankfurt am Main", new GeoPosition(50.11, 8.68));
            put("Java, Mt.Merapi",   new GeoPosition(-7.541389, 110.446111));
            put("Eugene Oregon",     new GeoPosition(44.058333, -123.068611));
            put("London",            new GeoPosition(51.5, 0));
            put("Madeira (Trail)",   new GeoPosition(32.81, -17.141)); // with track
            put(DEFAULT_POS,         new GeoPosition(55.70, 12.54)); // København
        }
    };

    /**
     * Callback method for demo loader. 
     */
    public void start() {
//        // Use SwingWorker to asynchronously load data
//        // create SwingWorker which will load the data on a separate thread
//        SwingWorker<?, ?> loader = new MessageLoader(MapKitDemo.class.getResource("data/aisstream.txt"), new MessageReader());
//        loader.addPropertyChangeListener( propertyChangeEvent -> {
//        	if ("state".equals(propertyChangeEvent.getPropertyName())) {
//        		StateValue state = (StateValue)propertyChangeEvent.getNewValue();
//                LOG.info("loader StateValue:" + state); // damit ampel steuern
////                updateStatusBar();
//                if (state == StateValue.DONE) {
////                	statusBarLeft.remove(progressBar);
////                	statusBarLeft.remove(actionStatus);
//                	revalidate();
//                	repaint();
//                }
//        	}
//        	if ("progress".equals(propertyChangeEvent.getPropertyName())) {
//        		int progress = (Integer)propertyChangeEvent.getNewValue();
//                LOG.info("loader progress:" + progress);
////        		progressBar.setValue(progress);
////        		updateStatusBar();
//        	}
//        });
//        StateValue state = loader.getState(); // PENDING - STARTED - DONE
//        LOG.info("loader StateValue:" + state);
//        loader.execute();

    	final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
		AisStreamMessage.liesUrl(GITHUB_URL, new AisStreamMessage.ConsoleCallback());
//		URL url = MapKitDemo.class.getClassLoader().getResource("data/aisstream.txt");
//		System.out.println("starting with " + url);
//		int cnt = 0;
//	    MessageReader mr = new MessageReader();
//		try {
//			File file = new File(url.toURI());
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			while (reader.ready()) {
//				String line = reader.readLine();
//				System.out.println(line);
//				AisStreamMessage asm = mr.readMessage(line); // kann null sein
//				if(asm!=null) {
//					
//				}
//				cnt++;
//			}
//			reader.close();
//		} catch (IOException | URISyntaxException e) {
//			System.out.println("Exeption " + e);
//		}
////		new AddNavigationIcon(mapKit.getMainMap(), painters)
//		mr.getMap().forEach( (mmsi,v) -> {
//			if(mmsi==219005904) { // AURELIA : ShipStaticData,PositionReport
//				Position pos = v.get(1).getMetaData().getPosition();
//				GeoPosition location = new GeoPosition(pos.getLatitude(), pos.getLongitude());
//				
//		    	RadianceIcon icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M);
//		    	icon.setColorFilter(color -> Color.ORANGE);
//				// color aus ShipTypeColor ermitteln
//				AisMessage m0 = v.get(0).getAisMessage();
//				AisMessage m1 = v.get(1).getAisMessage();
//				if(m0 instanceof ShipStaticData ssd) {
//				    ShipTypeCargo stc = new ShipTypeCargo(ssd.getType());
//				    ShipTypeColor color = ShipTypeColor.getColor(stc.getShipType());
//					System.out.println("\tMMSI="+mmsi +":"+v.size() + " "
//							+v.get(0).getMetaData().getShipName()+" "+color);
//				}
//				if(m1 instanceof PositionReport pr) {
//					  /**
//					   * Course over Ground Course over ground in 1/10 = (0-3599). 
//					   * 3600 (E10h) = not available = default. 
//					   * 3601-4095 should not be used
//					   */
//					double theta = pr.getCog(); // Double
//					icon.setRotation(theta);
//					System.out.println("\tMMSI="+mmsi +":"+v.size() + " "
//							+v.get(0).getMetaData().getShipName()+" "+theta);
//					
////					location = new GeoPosition(pr.getLatitude(), pr.getLongitude());
//				}
//				
//
//			//if(v.size()>1) {
////				System.out.println("\tMMSI="+mmsi +":"+v.size() + " "
////						+v.get(0).getMetaData().getShipName()+v.get(0).getMetaData().getPosition());
////				Position pos = v.get(0).getMetaData().getPosition();
////				GeoPosition location = new GeoPosition(pos.getLatitude(), pos.getLongitude());
////		    	RadianceIcon icon = FeatheRnavigation_grey.of(SizingConstants.M, SizingConstants.M);
//				WaypointPainter<Waypoint> shipLocationPainter = new WaypointPainter<Waypoint>() {
//					public Set<Waypoint> getWaypoints() {
//						Set<Waypoint> set = new HashSet<Waypoint>();
//						set.add(new DefaultWaypoint(location));
//						return set;
//					}
//				};
//				int adjustx = icon.getIconWidth()/2;
//				int adjusty = icon.getIconHeight()/2;;
//				shipLocationPainter.setRenderer(new DefaultWaypointRenderer(adjustx, adjusty, icon));
//				painters.add(shipLocationPainter);
//				CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
//		        overlayPainter.setCacheable(false);
//		        overlayPainter.setPainters(painters);
//		        mapKit.getMainMap().setOverlayPainter(overlayPainter);
//			}
//		});
    }

}
