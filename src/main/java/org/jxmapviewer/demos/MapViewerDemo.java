/* created from jxmapviewer sample1_basics + sample3_interaction
*/ 
package org.jxmapviewer.demos;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.net.MalformedURLException;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.MutableComboBoxModel;
import javax.swing.Painter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXFrame.StartPosition;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.demos.svg.FeatheRmap_pin;
import org.jdesktop.swingx.icon.PauseIcon;
import org.jdesktop.swingx.icon.PlayIcon;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jdesktop.swingx.icon.SizingConstants;
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

import swingset.AbstractDemo;

/**
 * A demo for the {@code JXMapViewer}.
 *
 * @author Martin Steiger
 * @author EUG https://github.com/homebeaver (integrate to SwingSet3)
 */
public class MapViewerDemo extends AbstractDemo {
	
	private static final long serialVersionUID = -4946197162374262488L;
	private static final Logger LOG = Logger.getLogger(MapViewerDemo.class.getName());
	private static final String DESCRIPTION = "Demonstrates JXMapViewer, a simple simple vessel tracker using the AISStream API";
	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";
	
    /**
     * main method allows us to run as a standalone demo.
     * @param args params
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater( () -> {
			JXFrame controller = new JXFrame("controller", exitOnClose);
			AbstractDemo demo = new MapViewerDemo(controller);
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
	private static final String DEFAULT_MAP = "København - Øresund";
	private TileFactoryInfo info;
    private AisMapViewer mapViewer;

    // controller:
    private JComboBox<DisplayInfo<GeoPosition>> positionChooserCombo;
    private JSlider zoomSlider;
    private JButton animation;

    /**
     * Demo Constructor
     * 
     * @param frame controller Frame
     */
    public MapViewerDemo(Frame frame) {
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

        // Setup JXMapViewer
        mapViewer = new AisMapViewer();
        mapViewer.setName("mapViewer");
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the zoom and focus to Øresund
        mapViewer.setZoom(DEFAULT_ZOOM);
        mapViewer.setAddressLocation(nameToGeoPosition.get(DEFAULT_MAP));

        // Add interactions / verschieben , zoomen , select
// "Use left mouse button to pan, mouse wheel to zoom and right mouse to select";
// mia : class MouseInputAdapter extends MouseAdapter implements MouseInputListener
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Add painters:
        SelectionAdapter sa = new SelectionAdapter(mapViewer);
        mapViewer.addMouseMotionListener(sa); // SelectionAdapter to get the selected Rectangle
        SelectionPainter<JXMapViewer> selectionPainter = new SelectionPainter<>(sa);
        mapViewer.addMouseListener(sa);
        mapViewer.addMouseMotionListener(sa);
    	CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
//    	overlayPainter.setPainters(addressLocationPainter, selectionPainter);
        addressLocationPainter.setRenderer(new DefaultWaypointRenderer(FeatheRmap_pin.of(SizingConstants.M, SizingConstants.M)));
//        mapViewer.setOverlayPainter(overlayPainter);

        add(mapViewer, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH); // Alternativ JXStatusBar im frame
        add(createControlBar(), BorderLayout.WEST);
        
        mapViewer.addPropertyChangeListener("zoom", pce -> {
        	LOG.info("---------------------pce:"+pce);
        	getPosAndZoom();
        });
        mapViewer.addPropertyChangeListener("center", pce -> {
        	GeoPosition pos = getPosAndZoom();
        	mapViewer.setCenterPosition(pos);
        });
        getPosAndZoom();
        List<Painter<JXMapViewer>> painters = new ArrayList<>(); // besser LinkedList
        mapViewer.addMouseListener(new AddNavigationIcon(mapViewer, painters));
        painters.add(addressLocationPainter);
        painters.add(selectionPainter);
        overlayPainter.setPainters(painters);
        mapViewer.setOverlayPainter(overlayPainter);
    }

    // from JXMapKit
	private WaypointPainter<Waypoint> addressLocationPainter = new WaypointPainter<Waypoint>() {
		@Override
		public Set<Waypoint> getWaypoints() {
			Set<Waypoint> set = new HashSet<Waypoint>();
			if (mapViewer.getAddressLocation() != null) {
				set.add(new DefaultWaypoint(mapViewer.getAddressLocation()));
			} else {
				set.add(new DefaultWaypoint(0, 0));
			}
			return set;
		}
	};

    private GeoPosition getPosAndZoom() {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();
        if(zoomSlider!=null) zoomSlider.setValue(zoom);

        LOG.info(String.format("Lat/Lon=(%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
        return new GeoPosition(lat, lon);
    }

    @Override
	public JXPanel getControlPane() {
		JXPanel controls = new JXPanel() {
			public Dimension getMaximumSize() {
				return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
			}
		};
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(Box.createRigidArea(VGAP15));

		JXLabel selectLabel = new JXLabel("select another location:");
		selectLabel.setName("selectLabel");
		selectLabel.setText(getBundleString("selectLabel.text"));
		selectLabel.setAlignmentX(JXLabel.LEFT_ALIGNMENT);
		controls.add(selectLabel);

        // Create the combo chooser box:
		positionChooserCombo = new JComboBox<DisplayInfo<GeoPosition>>();
		positionChooserCombo.setName("positionChooserCombo");
		positionChooserCombo.setModel(createCBM());
		positionChooserCombo.setAlignmentX(JXComboBox.LEFT_ALIGNMENT);
//        ComboBoxRenderer renderer = new ComboBoxRenderer(); wie in MirroringIconDemo mit Flagge TODO
		
		positionChooserCombo.addActionListener(ae -> {
			int index = positionChooserCombo.getSelectedIndex();
			DisplayInfo<GeoPosition> item = (DisplayInfo<GeoPosition>)positionChooserCombo.getSelectedItem();
			LOG.info("Combo.SelectedItem=" + item.getDescription());
			mapViewer.setAddressLocation(item.getValue());
	        mapViewer.setZoom(DEFAULT_ZOOM);
	        zoomSlider.setValue(DEFAULT_ZOOM);
			positionChooserCombo.setSelectedIndex(index);
		});
		controls.add(positionChooserCombo);
		selectLabel.setLabelFor(positionChooserCombo);

//	    LOG.info("min/max/zoom:"+info.getMinimumZoomLevel()+" "+info.getMaximumZoomLevel()+" "+mapViewer.getZoom());
	    zoomSlider = new JSlider(JSlider.VERTICAL, info.getMinimumZoomLevel(), info.getMaximumZoomLevel(), mapViewer.getZoom());
	    zoomSlider.addChangeListener(changeEvent -> {
	    	//LOG.info(""+zoomSlider.getValue());
	    	mapViewer.setZoom(zoomSlider.getValue());
	    });
	    zoomSlider.setPaintTicks(true);
	    zoomSlider.setMajorTickSpacing(1);
//        Dictionary<Integer, JComponent> labels = new Hashtable<Integer, JComponent>();
        // can we fill these labels from the properties file? Yes, we can! but I do not
//        String labelTable = getBundleString(SLIDER+".labelTable");
//        labels.put(zoomSlider.getMinimum(), new JLabel("zoom in"));
//        labels.put(zoomSlider.getMaximum(), new JLabel("zoom out"));
//        zoomSlider.setLabelTable(labels);

        // to fill up the remaining space
		JPanel fill = new JPanel(new BorderLayout());
		fill.add(new JLabel(getBundleString("zoomOut.text")), BorderLayout.NORTH);		
		fill.add(zoomSlider, BorderLayout.WEST);		
		fill.add(new JLabel(getBundleString("zoomIn.text")), BorderLayout.SOUTH);		
		controls.add(fill);

        animation = new JButton();
        animation.setName("animation");
        animation.setText(getBundleString("animation.text"));
        animation.setIcon(PlayIcon.of(RadianceIcon.XS, RadianceIcon.XS));
        animation.addActionListener( ae -> {
        	animation.setEnabled( false );
            // Starte Extra-Thread per SwingWorker, 
        	// damit der Event Dispatch Thread (EDT) nicht blockiert wird:
        	MessageLoader ml = new MessageLoader("src/test/java/aisstream.txt", mapViewer);
        	ml.setSleep(10);
        	ml.execute();
        	// file aus GITHUB gebremst 50ms
//			try {
//				MessageLoader ml = new MessageLoader(new URL(GITHUB_URL), mapViewer);
//				ml.setSleep(50);
//				ml.execute();
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        });
    	fill.add(animation, BorderLayout.EAST);

		return controls;
	}

    private ComboBoxModel<DisplayInfo<GeoPosition>> createCBM() {
        MutableComboBoxModel<DisplayInfo<GeoPosition>> model = new DefaultComboBoxModel<DisplayInfo<GeoPosition>>();
        nameToGeoPosition.forEach((k,v) -> {
        	model.addElement(new DisplayInfo<GeoPosition>(k, v));
        });
        return model;
    }
    // WEST:
    private JButton startButton;
    RadianceIcon start = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
    RadianceIcon stop = PauseIcon.of(RadianceIcon.M, RadianceIcon.M);
    MessageLoader ml;
    protected Container createControlBar() {
    	JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
    	startButton = new JButton();
    	startButton.setName("startButton");
    	startButton.setText(getBundleString("startButton.text"));
    	startButton.setVerticalTextPosition(SwingConstants.BOTTOM);
    	startButton.setHorizontalTextPosition(SwingConstants.CENTER);
    	startButton.setIcon(start);
    	startButton.addActionListener( ae -> {
    		//startButton.setEnabled( false ); // Nein Start->Stop
    		if (startButton.getIcon()==start) {
            	startButton.setIcon(stop);
            	// Live:
            	ml = new MessageLoader((URL)null, mapViewer);
    			ml.execute();
    		} else {
    	    	startButton.setIcon(start);
    	    	if (ml.cancel(true)) {
    	    		LOG.info("canceled.");
    	    	}
    		}
    	});
    	toolBar.add(Box.createRigidArea(new Dimension(22, 10)));
    	toolBar.add(Box.createHorizontalGlue());
    	toolBar.add(Box.createRigidArea(new Dimension(0, 50)));
    	toolBar.add(startButton);
    	toolBar.add(ColorLegend.SINGLETON.blueButton());
    	toolBar.add(ColorLegend.SINGLETON.redButton());
    	toolBar.add(ColorLegend.SINGLETON.greenButton());
    	toolBar.add(ColorLegend.SINGLETON.orangeButton());
    	toolBar.add(ColorLegend.SINGLETON.magentaButton());
    	toolBar.add(ColorLegend.SINGLETON.cyanButton());
    	toolBar.add(ColorLegend.SINGLETON.yellowButton());
    	toolBar.add(ColorLegend.SINGLETON.greyButton());
//        JComponent bar = Box.createHorizontalBox();
//        bar.add(tableStatus);
//        tableRows = new JLabel("0");
//        bar.add(tableRows);
//        
//        statusBar.add(bar);
    	toolBar.add(Box.createVerticalStrut(12));
    	return toolBar;
    }
    // SOUTH:
    private JComponent statusBarLeft;
//    private JLabel actionStatus;
    private JLabel tableStatus;
    private JLabel tableRows;
//    private JProgressBar progressBar;
    protected Container createStatusBar() {

        JXStatusBar statusBar = new JXStatusBar();
        statusBar.putClientProperty("auto-add-separator", Boolean.FALSE);
        // Left status area
        statusBar.add(Box.createRigidArea(new Dimension(10, 22)));
//        statusBarLeft = Box.createHorizontalBox();
//        statusBar.add(statusBarLeft, JXStatusBar.Constraint.ResizeBehavior.FILL);
//        actionStatus = new JLabel();
//        actionStatus.setName("loadingStatusLabel");
//        actionStatus.setText(getBundleString("loadingStatusLabel.text"));
//        actionStatus.setHorizontalAlignment(JLabel.LEADING);
//        statusBarLeft.add(actionStatus);
        // display progress bar while data loads
//        progressBar = new JProgressBar();
//        statusBarLeft.add(progressBar);   

        // Middle (should stretch)
        statusBar.add(Box.createVerticalGlue());
        statusBar.add(Box.createRigidArea(new Dimension(50, 0)));

        // Right status area
        tableStatus = new JLabel(); 
        tableStatus.setName("rowCountLabel");
        tableStatus.setText(getBundleString("rowCountLabel.text"));
        JComponent bar = Box.createHorizontalBox();
        bar.add(tableStatus);
        tableRows = new JLabel("0");
        bar.add(tableRows);
        
        statusBar.add(bar);
        statusBar.add(Box.createHorizontalStrut(12));
        return statusBar;
    }

    @SuppressWarnings("serial")
	private static final Map<String, GeoPosition> nameToGeoPosition = new HashMap<>(){
        {
            put("Berlin",            new GeoPosition(52,31,0, 13,24,0));
            put("Darmstadt",         new GeoPosition(49,52,0,  8,39,0));
            put("Frankfurt am Main", new GeoPosition(50.11, 8.68));
            put("Java, Mt.Merapi",   new GeoPosition(-7.541389, 110.446111));
            put("Eugene Oregon",     new GeoPosition(44.058333, -123.068611));
            put("London",            new GeoPosition(51.5, 0));
            put("Madeira (Trail)",   new GeoPosition(32.81, -17.141)); // with track
            put(DEFAULT_MAP,         new GeoPosition(55.70, 12.54)); // // "København - Øresund"
        }
    };

}
