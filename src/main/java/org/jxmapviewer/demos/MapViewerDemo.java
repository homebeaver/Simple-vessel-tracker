/* created from jxmapviewer sample1_basics + sample3_interaction
*/ 
package org.jxmapviewer.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
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
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.binding.DisplayInfo;
import org.jdesktop.swingx.binding.LabelHandler;
import org.jdesktop.swingx.demos.svg.FeatheRcrosshair;
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dk.dma.ais.message.ShipTypeCargo;
import io.github.homebeaver.aismodel.AisMessageTypes;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.ShipStaticData;
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
	private static final String DESCRIPTION = "A simple vessel tracker using JXMapViewer and AISStream API";
	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";

	/**
	 * main method allows us to run as a standalone demo.
	 * 
	 * @param args params
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
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
		addressLocationPainter.setRenderer(new DefaultWaypointRenderer(FeatheRmap_pin.of(SizingConstants.M, SizingConstants.M)));

		add(mapViewer, BorderLayout.CENTER);
//		add(createStatusBar(), BorderLayout.SOUTH); // Alternativ JXStatusBar im frame

		mapViewer.addPropertyChangeListener("zoom", pce -> {
			LOG.info("---------------------pce:" + pce);
			getPosAndZoom();
		});
		mapViewer.addPropertyChangeListener("center", pce -> {
			GeoPosition pos = getPosAndZoom();
			mapViewer.setCenterPosition(pos);
		});
		mapViewer.addPropertyChangeListener("mmsiToTrack", pce -> {
			@SuppressWarnings("unchecked")
			List<AisStreamMessage> ls = (List<AisStreamMessage>)pce.getNewValue();
			setShipStaticDataFields(ls);
			// safe with all checks:
//			Object o = pce.getNewValue();
//			if(o instanceof List l) {
//				for (Object item : l) {
//					if(item instanceof AisStreamMessage asm) {
//						if(asm.getAisMessageType()==AisMessageTypes.SHIPSTATICDATA) {
//							AisMessage m = asm.getAisMessage();
//							if(m instanceof ShipStaticData ssd) {
//								nameField.setText(ssd.getName());
//								imoField.setText(ssd.getImoNumber().toString());
//							}
//						}
//					}
//				}
//			}
		});
		
		getPosAndZoom();
		List<Painter<JXMapViewer>> painters = new ArrayList<>(); // besser LinkedList?
		mapViewer.addMouseListener(new AddNavigationIcon(mapViewer, painters));
		painters.add(addressLocationPainter);
		painters.add(selectionPainter);
		CompoundPainter<JXMapViewer> overlayPainter = new CompoundPainter<JXMapViewer>();
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
		if (zoomSlider != null) {
			zoomSlider.setValue(zoom);
		}
		LOG.info(String.format("Lat/Lon=(%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
		return new GeoPosition(lat, lon);
	}

	MessageLoader ml; // SwingWorker
	
	// controller:
	private JComboBox<DisplayInfo<GeoPosition>> positionChooserCombo;
	private JSlider zoomSlider;
	private JButton miniDemoButton; // data from GitHub
	private JButton fileDemoButton; // data from local file
	private JButton liveButton; // data from aisstream
	private JButton crosshairButton; // show ship static data and trace
	
	static private RadianceIcon start = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon stop = PauseIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon play = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon crosshair = FeatheRcrosshair.of(RadianceIcon.M, RadianceIcon.M);
	
	static private RadianceIcon playDisabled() {
		RadianceIcon ri = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
		ri.setColorFilter(color -> Color.LIGHT_GRAY);
		return ri;
	}

	/*
	 * N: map selector 
	 * W: Zoom 
	 * E: Start Demo+Live, Legende 
	 * S: Status 
	 * C: SHIPSTATICDATA + last Pos TODO
	 */
	@Override
	public JXPanel getControlPane() {
		@SuppressWarnings("serial")
		JXPanel controls = new JXPanel(new BorderLayout()) {
			public Dimension getMaximumSize() {
				return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
			}
		};
		controls.add(createMapSelector(), BorderLayout.NORTH);
		controls.add(createZoomer(), BorderLayout.WEST);
		controls.add(createControlBar(), BorderLayout.EAST);
		controls.add(createStatusBar(), BorderLayout.SOUTH);
		controls.add(createCenter(), BorderLayout.CENTER);
		return controls;
	}

	JXPanel centerControls;
	private JTextField mmsiField;
	private JTextField nameField;
	private JTextField imoField;
	private JTextField callSignField;
	private JTextField typeField;
	private JTextField dimensionField;
	private JTextField destinationField;

	private void setShipStaticDataFields(List<AisStreamMessage> ls) {
		ls.forEach(asm -> {
			if (asm.getAisMessageType() == AisMessageTypes.SHIPSTATICDATA) {
				ShipStaticData ssd = (ShipStaticData) (asm.getAisMessage());
				nameField.setText(ssd.getName());
				imoField.setText(ssd.getImoNumber().toString());
				callSignField.setText(ssd.getCallSign());
				ShipTypeCargo stype = new ShipTypeCargo(ssd.getType());
				typeField.setText(stype.toString());
				dimensionField.setText("" + ssd.getDimension().getLength() 
						+ " / " + ssd.getDimension().getWidth()
						+ " / " + ssd.getMaximumStaticDraught() + " m"
						);
				destinationField.setText(ssd.getDestination());
			} else {
				nameField.setText(asm.getMetaData().getShipName());
			}
		});
	}

	protected Container createCenter() {
		centerControls = new JXPanel(new BorderLayout());
//		 jgoodies layout and builder:
        FormLayout formLayout = new FormLayout(
                "5dlu, r:d:n, l:4dlu:n, f:d:g", // 2 columns
                "c:d:n " + // controlSeparator
                ", t:4dlu:n, c:d:n" +  // mmsiField
                ", t:4dlu:n, c:d:n" +  // nameField
                ", t:4dlu:n, c:d:n" +  // imoField
                ", t:4dlu:n, c:d:n" +  // callSignField
                ", t:4dlu:n, c:d:n" +  // typeField
                ", t:4dlu:n, c:d:n" +  // dimensionField
                ", t:4dlu:n, c:d:n" +  // destinationField
                ", t:4dlu:n, c:d:n" // button
                ); // rows
        PanelBuilder builder = new PanelBuilder(formLayout, centerControls);
        builder.setBorder(Borders.DIALOG_BORDER);
        CellConstraints cl = new CellConstraints();
        CellConstraints cc = new CellConstraints();
        
        JXTitledSeparator controlSeparator = new JXTitledSeparator();
        controlSeparator.setName("controlSeparator");
        controlSeparator.setTitle(getBundleString("controlSeparator.title"));
        builder.add(controlSeparator, cc.xywh(1, 1, 4, 1));
        
        int labelColumn = 2;
        int widgetColumn = labelColumn + 2;
        int currentRow = 3;
        mmsiField = new JTextField(20);
        mmsiField.setName("mmsiField");
        mmsiField.setText(getBundleString("mmsiField.text", "219230000"));
        JLabel mmsiLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
                mmsiField, cc.xywh(widgetColumn, currentRow, 1, 1));
        mmsiLabel.setName("mmsiLabel");
        mmsiLabel.setText(getBundleString("mmsiLabel.text", mmsiLabel));
        mmsiField.addActionListener(ae -> {
        	//titledPanel.setTitle(titleField.getText());
        });        
        currentRow += 2;
        
        nameField = new JTextField(20);
        nameField.setName("nameField");       
        nameField.setText(getBundleString("nameField.text"));
        JLabel nameLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		nameField, cc.xywh(widgetColumn, currentRow, 1, 1));
        nameLabel.setName("nameLabel");
        nameLabel.setText(getBundleString("nameLabel.text", nameLabel));
        LabelHandler.bindLabelFor(nameLabel, nameField);
        currentRow += 2;
        
        imoField = new JTextField(20);
        imoField.setName("imoField");       
        imoField.setText(getBundleString("imoField.text"));
        JLabel imoLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		imoField, cc.xywh(widgetColumn, currentRow, 1, 1));
        imoLabel.setName("imoLabel");
        imoLabel.setText(getBundleString("imoLabel.text", imoLabel));
        LabelHandler.bindLabelFor(imoLabel, imoField);
        currentRow += 2;
        
        callSignField = new JTextField(20);
        callSignField.setName("callSignField");       
        callSignField.setText(getBundleString("callSignField.text"));
        JLabel callSignLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		callSignField, cc.xywh(widgetColumn, currentRow, 1, 1));
        callSignLabel.setName("callSignLabel");
        callSignLabel.setText(getBundleString("callSignLabel.text", callSignLabel));
        LabelHandler.bindLabelFor(callSignLabel, callSignField);
        currentRow += 2;
        
        typeField = new JTextField(20);
        typeField.setName("typeField");       
        typeField.setText(getBundleString("typeField.text"));
        JLabel typeLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		typeField, cc.xywh(widgetColumn, currentRow, 1, 1));
        typeLabel.setName("typeLabel");
        typeLabel.setText(getBundleString("typeLabel.text", typeLabel));
        LabelHandler.bindLabelFor(typeLabel, typeField);
        currentRow += 2;
        
        dimensionField = new JTextField(20);
        dimensionField.setName("dimensionField");       
        dimensionField.setText(getBundleString("dimensionField.text"));
        JLabel dimensionLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		dimensionField, cc.xywh(widgetColumn, currentRow, 1, 1));
        dimensionLabel.setName("dimensionLabel");
        dimensionLabel.setText(getBundleString("dimensionLabel.text", dimensionLabel));
        LabelHandler.bindLabelFor(dimensionLabel, dimensionField);
        currentRow += 2;
        
        destinationField = new JTextField(20);
        destinationField.setName("destinationField");       
        destinationField.setText(getBundleString("destinationField.text"));
        JLabel destinationLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		destinationField, cc.xywh(widgetColumn, currentRow, 1, 1));
        destinationLabel.setName("destinationLabel");
        destinationLabel.setText(getBundleString("destinationLabel.text", destinationLabel));
        LabelHandler.bindLabelFor(destinationLabel, destinationField);
        currentRow += 2;
        // ... TODO AIS-Flagge

		crosshairButton = fileDemoButton("crosshairButton", getBundleString("crosshairButton.text"));
		crosshairButton.setIcon(crosshair);
		builder.add(crosshairButton, cc.xywh(widgetColumn, currentRow, 1, 1));
		crosshairButton.addActionListener(ae -> {
			LOG.info("Show TYCHO BRAHE (IMO 9007116, MMSI 219230000).");
			List<AisStreamMessage> v = mapViewer.getVesselTrace(219230000);
			if(v!=null) {
				setShipStaticDataFields(v);
			} else {
				System.out.println("nix gefunden für TYCHO BRAHE");
			}
			
		});
		
		return centerControls;
	}
	protected Container createMapSelector() {
		JXPanel controls = new JXPanel(new BorderLayout());
		JXLabel selectLabel = new JXLabel("select another location:");
		selectLabel.setName("selectLabel");
		selectLabel.setText(getBundleString("selectLabel.text"));
		selectLabel.setAlignmentX(JXLabel.LEFT_ALIGNMENT);
		controls.add(selectLabel, BorderLayout.NORTH);

		// Create the combo chooser box:
		positionChooserCombo = new JComboBox<DisplayInfo<GeoPosition>>();
		positionChooserCombo.setName("positionChooserCombo");
		positionChooserCombo.setModel(createCBM());
		positionChooserCombo.setAlignmentX(JXComboBox.LEFT_ALIGNMENT);

		positionChooserCombo.addActionListener(ae -> {
			int index = positionChooserCombo.getSelectedIndex();
			@SuppressWarnings("unchecked")
			DisplayInfo<GeoPosition> item = (DisplayInfo<GeoPosition>) positionChooserCombo.getSelectedItem();
			LOG.info("Combo.SelectedItem=" + item.getDescription());
			mapViewer.setAddressLocation(item.getValue());
			mapViewer.setZoom(DEFAULT_ZOOM);
			zoomSlider.setValue(DEFAULT_ZOOM);
			positionChooserCombo.setSelectedIndex(index);
		});
		controls.add(positionChooserCombo);
		selectLabel.setLabelFor(positionChooserCombo);
		return controls;
	}

	protected Container createZoomer() {
		zoomSlider = new JSlider(JSlider.VERTICAL, info.getMinimumZoomLevel(), info.getMaximumZoomLevel(), mapViewer.getZoom());
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.addChangeListener(changeEvent -> {
			// LOG.info(""+zoomSlider.getValue());
			mapViewer.setZoom(zoomSlider.getValue());
		});

//		JPanel controls = new JPanel(new BorderLayout()); // ??? TODO new BoxLayout
//		controls.add(new JLabel(getBundleString("zoomOut.text")), BorderLayout.NORTH);
//		controls.add(zoomSlider, BorderLayout.WEST);
//		controls.add(new JLabel(getBundleString("zoomIn.text")), BorderLayout.SOUTH);
		// viel besser ist es nicht XXX
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(new JLabel(getBundleString("zoomOut.text")));
		controls.add(zoomSlider);
		controls.add(new JLabel(getBundleString("zoomIn.text")));
		return controls;
	}

	private JButton fileDemoButton(String name, String text) {
		JButton b = new JButton();
		b.setName(name);
		b.setText(text);
		b.setVerticalTextPosition(SwingConstants.BOTTOM);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		b.setIcon(play);
		return b;
	}

	// EAST:
	protected Container createControlBar() {
		JToolBar toolBar = new JToolBar(SwingConstants.VERTICAL);
		// two Demo Buttons
		toolBar.add(Box.createVerticalStrut(10));
		JPanel grid = new JPanel(new GridLayout(3, 1, 10, 10));
		miniDemoButton = fileDemoButton("miniDemoButton", getBundleString("miniDemoButton.text"));
		miniDemoButton.setDisabledIcon(playDisabled());
		miniDemoButton.addActionListener(ae -> {
			miniDemoButton.setEnabled(false);
			// Starte Extra-Thread per SwingWorker,
			// damit der Event Dispatch Thread (EDT) nicht blockiert wird:
			// file aus GITHUB gebremst 50ms
			try {
				MessageLoader ml = new MessageLoader(new URL(GITHUB_URL), mapViewer, getCounter());
				ml.setSleep(50);
				ml.execute();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		grid.add(miniDemoButton);
//    	toolBar.add(Box.createVerticalStrut(10));
		fileDemoButton = fileDemoButton("fileDemoButton", getBundleString("fileDemoButton.text"));
		fileDemoButton.setDisabledIcon(playDisabled());
		fileDemoButton.addActionListener(ae -> {
			fileDemoButton.setEnabled(false);
			// Starte Extra-Thread per SwingWorker,
			// damit der Event Dispatch Thread (EDT) nicht blockiert wird:
			MessageLoader ml = new MessageLoader("src/test/java/aisstream.txt", mapViewer, getCounter());
			ml.setSleep(10);
			ml.execute();
		});
		grid.add(fileDemoButton);

//    	toolBar.add(Box.createVerticalStrut(10));
		liveButton = fileDemoButton("liveButton", getBundleString("liveButton.text"));
		liveButton.setIcon(start);
		liveButton.addActionListener(ae -> {
			// Start->Stop
			if (liveButton.getIcon() == start) {
				liveButton.setIcon(stop);
				// Live:
				ml = new MessageLoader((URL) null, mapViewer, getCounter());
				ml.execute();
			} else {
				liveButton.setIcon(start);
				if (ml.cancel(true)) {
					LOG.info("canceled.");
				}
			}
		});
//    	toolBar.add(Box.createHorizontalGlue());
//    	toolBar.add(Box.createRigidArea(new Dimension(0, 10)));
		grid.add(liveButton);
		toolBar.add(grid);

		toolBar.add(Box.createVerticalStrut(10));
		toolBar.add(ColorLegend.SINGLETON.blueLabel());
		toolBar.add(ColorLegend.SINGLETON.redLabel());
		toolBar.add(ColorLegend.SINGLETON.greenLabel());
		toolBar.add(ColorLegend.SINGLETON.orangeLabel());
		toolBar.add(ColorLegend.SINGLETON.magentaLabel());
		toolBar.add(ColorLegend.SINGLETON.cyanLabel());
		toolBar.add(ColorLegend.SINGLETON.yellowLabel());
		toolBar.add(ColorLegend.SINGLETON.greyLabel());
//        JComponent bar = Box.createHorizontalBox();
//        bar.add(tableStatus);
//        tableRows = new JLabel("0");
//        bar.add(tableRows);
//        
//        statusBar.add(bar);
		toolBar.add(Box.createVerticalStrut(10));
		return toolBar;
	}

	private ComboBoxModel<DisplayInfo<GeoPosition>> createCBM() {
		MutableComboBoxModel<DisplayInfo<GeoPosition>> model = new DefaultComboBoxModel<DisplayInfo<GeoPosition>>();
		nameToGeoPosition.forEach((k, v) -> {
			model.addElement(new DisplayInfo<GeoPosition>(k, v));
		});
		return model;
	}

    // SOUTH:
//    private JComponent statusBarLeft;
//    private JLabel actionStatus;
    private JLabel tableStatus;
    private JLabel tableRows;
    public JLabel getCounter() {
    	return tableRows;
    }
//    private JProgressBar progressBar;
    protected Container createStatusBar() {

        JXStatusBar statusBar = new JXStatusBar();
        statusBar.putClientProperty("auto-add-separator", Boolean.FALSE);
        // Left status area
        statusBar.add(Box.createRigidArea(new Dimension(0, 22)));
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
//        statusBar.add(Box.createRigidArea(new Dimension(50, 0)));

        // Right status area
        tableStatus = new JLabel(); 
        tableStatus.setName("rowCountLabel");
        tableStatus.setText(getBundleString("rowCountLabel.text"));
        JComponent bar = Box.createHorizontalBox();
        bar.add(tableStatus);
        tableRows = new JLabel("0");
        bar.add(tableRows);
        bar.add(Box.createRigidArea(new Dimension(5, 22)));
//        JPanel tb = new JPanel(new GridLayout(1,8, 0, 5));
//    	toolBar.add(ColorLegend.SINGLETON.blueLabel());
//    	toolBar.add(ColorLegend.SINGLETON.redLabel());
//    	toolBar.add(ColorLegend.SINGLETON.greenLabel());
//    	toolBar.add(ColorLegend.SINGLETON.orangeLabel());
//    	toolBar.add(ColorLegend.SINGLETON.magentaLabel());
//    	toolBar.add(ColorLegend.SINGLETON.cyanLabel());
//    	toolBar.add(ColorLegend.SINGLETON.yellowLabel());
//    	toolBar.add(ColorLegend.SINGLETON.greyLabel());
// sieht nicht aus:
//        tb.add(ColorLegend.SINGLETON.blueButton());
//        tb.add(ColorLegend.SINGLETON.redButton());
//        tb.add(ColorLegend.SINGLETON.greenButton());
//        tb.add(ColorLegend.SINGLETON.orangeButton());
//        tb.add(ColorLegend.SINGLETON.magentaButton());
//        tb.add(ColorLegend.SINGLETON.cyanButton());
//        tb.add(ColorLegend.SINGLETON.yellowButton());
//        tb.add(ColorLegend.SINGLETON.greyButton());
//        bar.add(tb);

        statusBar.add(bar);
        statusBar.add(Box.createHorizontalStrut(12));
        return statusBar;
    }

	@SuppressWarnings("serial")
	private static final Map<String, GeoPosition> nameToGeoPosition = new HashMap<>() {
		{
			put("Berlin", new GeoPosition(52, 31, 0, 13, 24, 0));
			put("Darmstadt", new GeoPosition(49, 52, 0, 8, 39, 0));
			put("Frankfurt am Main", new GeoPosition(50.11, 8.68));
			put("Java, Mt.Merapi", new GeoPosition(-7.541389, 110.446111));
			put("Eugene Oregon", new GeoPosition(44.058333, -123.068611));
			put("London", new GeoPosition(51.5, 0));
			put("Madeira (Trail)", new GeoPosition(32.81, -17.141)); // with track
			put(DEFAULT_MAP, new GeoPosition(55.70, 12.54)); // // "København - Øresund"
		}
	};

}
