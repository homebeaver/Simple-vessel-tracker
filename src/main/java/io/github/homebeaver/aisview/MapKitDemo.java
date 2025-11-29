package io.github.homebeaver.aisview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.MutableComboBoxModel;
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
import org.jdesktop.swingx.icon.PauseIcon;
import org.jdesktop.swingx.icon.PlayIcon;
import org.jdesktop.swingx.icon.RadianceIcon;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import dk.dma.ais.message.NavigationalStatus;
import dk.dma.ais.message.ShipTypeCargo;
import io.github.homebeaver.aismodel.AisStreamMessage;
import io.github.homebeaver.aismodel.MetaData;
import io.github.homebeaver.aismodel.MmsiMessageList;
import io.github.homebeaver.icon.Crosshair;
import swingset.AbstractDemo;

/**
 * A simple vessel tracker using {@code JXMapKit} and AISStream API
 *
 * @author EUG https://github.com/homebeaver
 */
public class MapKitDemo extends AbstractDemo implements PropertyChangeListener {

	private static final long serialVersionUID = 6899895296763622056L;
	private static final Logger LOG = Logger.getLogger(MapKitDemo.class.getName());
	private static final String DESCRIPTION = "JXMapKit with a vessel tracker";
	private static final String GITHUB_URL = "https://raw.githubusercontent.com/homebeaver/Simple-vessel-tracker/refs/heads/main/src/test/resources/data/aisstream.txt";

	static final URL GITHUB_URL() {
		try {
			return new URL(GITHUB_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * main method allows us to run as a standalone demo.
	 * 
	 * @param args params : laf to start with {nimbus,Metal+STEEL,Metal+OCEAN}
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			List<String> a = Arrays.asList(args);
			if (a.get(0).startsWith("key=")) {
				LOG.info("------------------"+a.get(1)+"<");
//				AisStreamKeyProvider.getInstance().setKey(a.get(0).substring(4)); //BUG XXX ?
/* 
java.lang.NullPointerException: Cannot invoke "javax.swing.SwingWorker.execute()" because "this.swingWorker" is null
	at io.github.homebeaver.aisview.StartStopComponent.liveButtonActionPerformed(StartStopComponent.java:190)
	at io.github.homebeaver.aisview.StartStopComponent.lambda$2(StartStopComponent.java:150)
	at java.desktop/javax.swing.AbstractButton.fireActionPerformed(AbstractButton.java:1972)
	at java.desktop/javax.swing.AbstractButton$Handler.actionPerformed(AbstractButton.java:2313)
	at java.desktop/javax.swing.DefaultButtonModel.fireActionPerformed(DefaultButtonModel.java:405)
	at java.desktop/javax.swing.DefaultButtonModel.setPressed(DefaultButtonModel.java:262)
	at java.desktop/javax.swing.plaf.basic.BasicButtonListener.mouseReleased(BasicButtonListener.java:279)
	at java.desktop/java.awt.Component.processMouseEvent(Component.java:6626)
	at java.desktop/javax.swing.JComponent.processMouseEvent(JComponent.java:3389)
	at java.desktop/java.awt.Component.processEvent(Component.java:6391)
	at java.desktop/java.awt.Container.processEvent(Container.java:2266)
	at java.desktop/java.awt.Component.dispatchEventImpl(Component.java:5001)
	at java.desktop/java.awt.Container.dispatchEventImpl(Container.java:2324)
	at java.desktop/java.awt.Component.dispatchEvent(Component.java:4833)
	at java.desktop/java.awt.LightweightDispatcher.retargetMouseEvent(Container.java:4948)
	at java.desktop/java.awt.LightweightDispatcher.processMouseEvent(Container.java:4575)
	at java.desktop/java.awt.LightweightDispatcher.dispatchEvent(Container.java:4516)
	at java.desktop/java.awt.Container.dispatchEventImpl(Container.java:2310)
	at java.desktop/java.awt.Window.dispatchEventImpl(Window.java:2780)
	at java.desktop/java.awt.Component.dispatchEvent(Component.java:4833)
	at java.desktop/java.awt.EventQueue.dispatchEventImpl(EventQueue.java:775)
	at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:720)
	at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:714)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
	at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
	at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:97)
	at java.desktop/java.awt.EventQueue$5.run(EventQueue.java:747)
	at java.desktop/java.awt.EventQueue$5.run(EventQueue.java:745)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
	at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:86)
	at java.desktop/java.awt.EventQueue.dispatchEvent(EventQueue.java:744)
	at java.desktop/java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:203)
	at java.desktop/java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:124)
	at java.desktop/java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:113)
	at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:109)
	at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
	at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:90)

 */
				LaFUtils.setLAFandTheme(a, 1);
			}
		}
		SwingUtilities.invokeLater(() -> {
			JXFrame controller = new JXFrame("controller", exitOnClose);
			AbstractDemo demo = new MapKitDemo(controller);
			JXFrame frame = new JXFrame(DESCRIPTION, exitOnClose);
			frame.setStartPosition(StartPosition.CenterInScreen);
			frame.getContentPane().add(demo);
			frame.pack();
			frame.setVisible(true);

			controller.getContentPane().add(demo.getControlPane());
			controller.pack();
			controller.setState(Frame.ICONIFIED);
			controller.setVisible(true);
		});
	}

	private static final String DEFAULT_REGION = Regions.DEFAULT_REGION;
	private TileFactoryInfo info;
	private AisMapKit mapKit;

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

		// Create a TileFactoryInfo for OpenStreetMap TODO in swingx 
		info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
		// Overlay tiles https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);

		// Setup local file cache
		File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

		// Setup JXMapKit TODO RadianceIcons in JXMapKit verwenden
		mapKit = new AisMapKit();
		mapKit.setName("mapKit");
//		mapKit.setZoomSliderVisible(true); // this is the default
		mapKit.setTileFactory(tileFactory);

		// threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(2);

		JPanel ss = mapKit.getStartStop();
		if(ss instanceof StartStopComponent startStop) {
			createPositionChooserCombo();
			DisplayInfo<Regions.Region> item = (DisplayInfo<Regions.Region>) positionChooserCombo.getSelectedItem();
			// Zu diesem Zeitpunkt ist noch nichts selektiert: item = København - Øresund
			LOG.info("noch nichts selektiert : DisplayInfo<Regions.Region> item = "+item);
			swingWorker = new MessageLoader((URL) null, mapKit, getCounter(), item.getValue().getBoundingBox());
			MessageLoader ml = new MessageLoader(GITHUB_URL(), mapKit, getCounter(), null);
			ml.setSleep(50);
			startStop.setSwingWorker(swingWorker, positionChooserCombo.getModel(), ml);
		}
		// Set the zoom and focus to Øresund
		mapKit.setZoom(Regions.getInstance().getZoom(DEFAULT_REGION));
		mapKit.setAddressLocation(Regions.getInstance().getCenter(DEFAULT_REGION));

		mapKit.getMainMap().setRestrictOutsidePanning(true); // ???
		mapKit.getMainMap().setHorizontalWrapped(false);

		// Add interactions / verschieben , zoomen , select
// "Use left mouse button to pan, mouse wheel to zoom and right mouse to select";
// mia : class MouseInputAdapter extends MouseAdapter implements MouseInputListener
		MouseInputListener mia = new PanMouseInputListener(mapKit.getMainMap());
		mapKit.getMainMap().addMouseListener(mia);
		mapKit.getMainMap().addMouseMotionListener(mia);

		mapKit.getMainMap().addMouseListener(new CenterMapListener(mapKit.getMainMap()));

		mapKit.getMainMap().addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapKit.getMainMap()));

		mapKit.getMainMap().addKeyListener(new PanKeyListener(mapKit.getMainMap()));

		add(mapKit, BorderLayout.CENTER);
//		add(createStatusBar(), BorderLayout.SOUTH); // Alternativ JXStatusBar im frame

		mapKit.addPropertyChangeListener("candidatesToTrack", pce -> {
			@SuppressWarnings("unchecked")
			Vector<MetaData> v = (Vector<MetaData>)pce.getNewValue();
			mmsiCombo.setModel(new DefaultComboBoxModel<MetaData>(v));
			if (v.size()>0) {
				mmsiCombo.setSelectedIndex(0);
			}
			// TODO BUG in JXComboBox: beim zweiten Aufklappen ist die Klappliste zu klein
		});
		
	}

	JComponent createPositionChooserCombo() {
		if (positionChooserCombo!=null) return positionChooserCombo;
		positionChooserCombo = new JXComboBox<DisplayInfo<Regions.Region>>();
		positionChooserCombo.setName("positionChooserCombo");
		positionChooserCombo.setModel(createCBM());
		positionChooserCombo.setAlignmentX(LEFT_ALIGNMENT);
		positionChooserCombo.setComboBoxIcon(map);
		positionChooserCombo.setBorder(BorderFactory.createEmptyBorder(5, 50, 10, 50));

		positionChooserCombo.addActionListener(ae -> {
			int index = positionChooserCombo.getSelectedIndex();
			@SuppressWarnings("unchecked")
			DisplayInfo<Regions.Region> item = (DisplayInfo<Regions.Region>) positionChooserCombo.getSelectedItem();
			LOG.info("Combo.SelectedItem=" + item.getDescription());
			mapKit.setAddressLocation(item.getValue().getGeoPosition());
			mapKit.setZoom(item.getValue().getZoom());
			positionChooserCombo.setSelectedIndex(index);
		});
		return positionChooserCombo;
//		controls.add(positionChooserCombo);
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		List<AisStreamMessage> ls = (List<AisStreamMessage>)pce.getNewValue();
		setShipStaticDataFields(ls);
	}

	/*SwingWorker<?, ?>*/ MessageLoader swingWorker;
	
	// controller:
	private JXComboBox<DisplayInfo<Regions.Region>> positionChooserCombo;
	private JButton miniDemoButton; // data from GitHub
	private JButton fileDemoButton; // data from local file
//	private JButton liveButton; // data from aisstream
	
	static private RadianceIcon start = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon stop = PauseIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon play = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
	static private RadianceIcon crosshair = Crosshair.of(RadianceIcon.SMALL_ICON, RadianceIcon.SMALL_ICON);
	static private RadianceIcon map = io.github.homebeaver.icon.Map.of(RadianceIcon.SMALL_ICON, RadianceIcon.SMALL_ICON);

	static private RadianceIcon playDisabled() {
		RadianceIcon ri = PlayIcon.of(RadianceIcon.M, RadianceIcon.M);
		ri.setColorFilter(color -> Color.LIGHT_GRAY);
		return ri;
	}

	/*
	 * N: map selector 
	 * W: Zoom (removed)
	 * E: Start Demo+Live, Legend 
	 * S: Status 
	 * C: SHIPSTATICDATA + last Pos + Course
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
//		controls.add(createZoomer(), BorderLayout.WEST);
		controls.add(createControlBar(), BorderLayout.EAST);
		controls.add(createStatusBar(), BorderLayout.SOUTH);
		controls.add(createCenter(), BorderLayout.CENTER);
		return controls;
	}

	JXPanel centerControls;
	private JXComboBox<MetaData> mmsiCombo;
	private JTextField nameField;
	private JTextField imoField;
	private JTextField callSignField;
	private JTextField typeField;
	private JTextField dimensionField;
	private JTextField destinationField;
	private JTextField positionField;
	private JTextField navStatusField;

	private void setShipStaticDataFields(List<AisStreamMessage> mmsiMessageList) {
		LOG.info("??????? size="+mmsiMessageList.size());
		AisStreamMessage asm = mmsiMessageList.get(mmsiMessageList.size()-1); // last element
		LOG.info("??????? size="+mmsiMessageList.size() + " "+asm.getAisMessageType());
//		int mmsi = asm.getMetaData().getMMSI();
		double lat = asm.getMetaData().getLatitude();
		double lon = asm.getMetaData().getLongitude();
//		positionField.setText(String.format("Lat/Lon=(%.2f / %.2f)", lat, lon));
		nameField.setText(MmsiMessageList.getName(mmsiMessageList));
		Integer imo = MmsiMessageList.getImoNumber(mmsiMessageList);
		imoField.setText(imo == null ? "" : imo.toString());
		callSignField.setText(MmsiMessageList.getCallSign(mmsiMessageList));
		Integer type = MmsiMessageList.getType(mmsiMessageList);
		ShipTypeCargo stype = new ShipTypeCargo(type == null ? 0 : type);
		typeField.setText(stype.toString());
		Double maxDraught = MmsiMessageList.getMaximumStaticDraught(mmsiMessageList);
		dimensionField.setText("" + MmsiMessageList.getShipLength(mmsiMessageList) 
				+ " / " + MmsiMessageList.getShipWidth(mmsiMessageList) 
				+ (maxDraught == null ? "" : " / " + maxDraught + " m")
				);
		Double cog = MmsiMessageList.getLastCog(mmsiMessageList);
		positionField.setText(String.format("Lat/Lon=(%.2f / %.2f)", lat, lon)
			+ (cog == null ? "" : String.format(" Cog=%.1f°", cog)));
		Double sog = MmsiMessageList.getLastSog(mmsiMessageList);
		Integer navStatus = MmsiMessageList.getNavigationalStatus(mmsiMessageList);
		navStatusField.setText(NavigationalStatus.get(navStatus == null ? 15 : navStatus).toString()
			+ (sog == null ? "" : String.format(" Sog=%.1fkn", sog)));
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
                ", t:4dlu:n, c:d:n" +  // positionField
                ", t:4dlu:n, c:d:n" +  // navStatusField
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
		// Create the combo chooser box:
		mmsiCombo = new JXComboBox<MetaData>();
		mmsiCombo.setName("mmsiCombo");
		mmsiCombo.setComboBoxIcon(crosshair);
		mmsiCombo.setModel(new DefaultComboBoxModel<MetaData>());
		mmsiCombo.setAlignmentX(LEFT_ALIGNMENT);
		mmsiCombo.addActionListener(ae -> {
			int index = mmsiCombo.getSelectedIndex();
			if (index==-1) {
				LOG.info("index==-1");
			} else {
				MetaData item = (MetaData) mmsiCombo.getSelectedItem();
				LOG.info("Combo.SelectedItem=" + item.getMMSI() + " " + item.getShipName());
				mmsiCombo.setSelectedIndex(index);
				List<AisStreamMessage> traceList = mapKit.getVesselTrace(item.getMMSI(), MapKitDemo.this);
				if(traceList!=null) {
					setShipStaticDataFields(traceList);
				} else {
					LOG.warning("No MmsiMessageList found for selected item "+item.getMMSI());
				}
			}
		});
        JLabel mmsiLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		mmsiCombo, cc.xywh(widgetColumn, currentRow, 1, 1));
        mmsiLabel.setName("mmsiLabel");
        mmsiLabel.setText(getBundleString("mmsiLabel.text", mmsiLabel));
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
        
        positionField = new JTextField(20);
        positionField.setName("positionField");
        positionField.setText(getBundleString("positionField.text"));
        JLabel positionLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		positionField, cc.xywh(widgetColumn, currentRow, 1, 1));
        positionLabel.setName("positionLabel");
        positionLabel.setText(getBundleString("positionLabel.text", positionLabel));
        LabelHandler.bindLabelFor(positionLabel, positionField);
        currentRow += 2;
        
        navStatusField = new JTextField(20);
        navStatusField.setName("navStatusField");
        navStatusField.setText(getBundleString("navStatusField.text"));
        JLabel navStatusLabel = builder.addLabel("", cl.xywh(labelColumn, currentRow, 1, 1),
        		navStatusField, cc.xywh(widgetColumn, currentRow, 1, 1));
        navStatusLabel.setName("navStatusLabel");
        navStatusLabel.setText(getBundleString("navStatusLabel.text", navStatusLabel));
        LabelHandler.bindLabelFor(navStatusLabel, navStatusField);
        currentRow += 2;
        // ... TODO AIS-Flagge 

		return centerControls;
	}
	
    private JCheckBox showAllVessels;
    private JCheckBox showZoomSlider;
    private JCheckBox drawTileBorder;
    private JCheckBox miniMapVisible;
    
	protected Container createMapSelector() {
		JXPanel controls = new JXPanel(true);
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		JXLabel selectLabel = new JXLabel("select another location:");
		selectLabel.setName("selectLabel");
		selectLabel.setText(getBundleString("selectLabel.text"));
		selectLabel.setAlignmentX(LEFT_ALIGNMENT);
		selectLabel.setBorder(BorderFactory.createEmptyBorder(10,50,0,50));
		controls.add(selectLabel);

		// Create and add the positionChooser combo chooser box:
		controls.add(createPositionChooserCombo());
		selectLabel.setLabelFor(positionChooserCombo);
		
		showAllVessels = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
		showAllVessels.setAlignmentX(LEFT_ALIGNMENT);
		showAllVessels.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 10));
		showAllVessels.setSelected(false);
		mapKit.setShowAllVessels(showAllVessels.isSelected());
		showAllVessels.setName("showAllVessels");
		showAllVessels.setText(getBundleString("showAllVessels.text"));
		showAllVessels.addActionListener(ae -> {
			mapKit.setShowAllVessels(showAllVessels.isSelected());
		});
		controls.add(showAllVessels);

		showZoomSlider = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
		showZoomSlider.setAlignmentX(LEFT_ALIGNMENT);
		showZoomSlider.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 10));
		showZoomSlider.setSelected(true);
		mapKit.setZoomSliderVisible(showZoomSlider.isSelected());
		showZoomSlider.setName("showZoomSlider");
		showZoomSlider.setText(getBundleString("showZoomSlider.text"));
		showZoomSlider.addActionListener(ae -> {
			mapKit.setZoomSliderVisible(showZoomSlider.isSelected());
		});
		controls.add(showZoomSlider);
		
		drawTileBorder = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
		drawTileBorder.setAlignmentX(LEFT_ALIGNMENT);
		drawTileBorder.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 10));
		drawTileBorder.setSelected(false);
		mapKit.getMainMap().setDrawTileBorders(drawTileBorder.isSelected());
		drawTileBorder.setName("drawTileBorder");
		drawTileBorder.setText(getBundleString("drawTileBorder.text"));
		drawTileBorder.addActionListener(ae -> {
			mapKit.getMainMap().setDrawTileBorders(drawTileBorder.isSelected());
		});
		controls.add(drawTileBorder);

		miniMapVisible = new JCheckBox(); // JCheckBox extends JToggleButton, JToggleButton extends AbstractButton
		miniMapVisible.setAlignmentX(LEFT_ALIGNMENT);
		miniMapVisible.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 10));
		miniMapVisible.setSelected(true);
		mapKit.setMiniMapVisible(miniMapVisible.isSelected());
		miniMapVisible.setName("miniMapVisible");
		miniMapVisible.setText(getBundleString("miniMapVisible.text"));
		miniMapVisible.addActionListener(ae -> {
			mapKit.setMiniMapVisible(miniMapVisible.isSelected());
		});
		controls.add(miniMapVisible);

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
		JPanel buttonGrid = new JPanel(new GridLayout(3, 1, 10, 10)); // (int rows, int cols, int hgap, int vgap)
		miniDemoButton = fileDemoButton("miniDemoButton", getBundleString("miniDemoButton.text"));
		miniDemoButton.setDisabledIcon(playDisabled());
		miniDemoButton.addActionListener(ae -> {
			miniDemoButton.setEnabled(false);
			// Starte Extra-Thread per SwingWorker,
			// damit der Event Dispatch Thread (EDT) nicht blockiert wird:
			// file aus GITHUB gebremst 50ms
			MessageLoader ml = new MessageLoader(GITHUB_URL(), mapKit, getCounter(), null);
			ml.setSleep(50);
			ml.execute();
		});
		buttonGrid.add(miniDemoButton);

		fileDemoButton = fileDemoButton("fileDemoButton", getBundleString("fileDemoButton.text"));
		fileDemoButton.setDisabledIcon(playDisabled());
		fileDemoButton.addActionListener(ae -> {
			fileDemoButton.setEnabled(false);
			// Starte Extra-Thread per SwingWorker,
			// damit der Event Dispatch Thread (EDT) nicht blockiert wird:
			MessageLoader ml = new MessageLoader("src/test/java/aisstream.txt", mapKit, getCounter());
			ml.setSleep(10);
			ml.execute();
		});
		buttonGrid.add(fileDemoButton);

		StartStopComponent startStop = new StartStopComponent(getBundleString("liveButton.text"));
		DisplayInfo<Regions.Region> item = (DisplayInfo<Regions.Region>) positionChooserCombo.getSelectedItem();
		MessageLoader swingWorker = new MessageLoader((URL) null, mapKit, getCounter(), item.getValue().getBoundingBox());
		MessageLoader ml = new MessageLoader(GITHUB_URL(), mapKit, getCounter(), null);
		ml.setSleep(50);
		startStop.setSwingWorker(swingWorker, positionChooserCombo.getModel(), ml);
		buttonGrid.add(startStop); // remove <== busyLabel an falscher Stelle TODO

		toolBar.add(buttonGrid);
		toolBar.add(Box.createVerticalStrut(10));

		JPanel legendGrid = new JPanel(new GridLayout(8, 1, 0, 0)); // (int rows, int cols, int hgap, int vgap)
		legendGrid.add(ColorLegend.SINGLETON.blueLabel());
		legendGrid.add(ColorLegend.SINGLETON.redLabel());
		legendGrid.add(ColorLegend.SINGLETON.greenLabel());
		legendGrid.add(ColorLegend.SINGLETON.orangeLabel());
		legendGrid.add(ColorLegend.SINGLETON.magentaLabel());
		legendGrid.add(ColorLegend.SINGLETON.cyanLabel());
		legendGrid.add(ColorLegend.SINGLETON.yellowLabel());
		legendGrid.add(ColorLegend.SINGLETON.greyLabel());
		toolBar.add(legendGrid);
		return toolBar;
	}

	private ComboBoxModel<DisplayInfo<Regions.Region>> createCBM() {
		MutableComboBoxModel<DisplayInfo<Regions.Region>> model = new DefaultComboBoxModel<DisplayInfo<Regions.Region>>();
		Regions.getInstance().getRegions().forEach((k, v) -> {
			model.addElement(new DisplayInfo<Regions.Region>(k, v));
		});
		return model;
	}

    // SOUTH:
//    private JComponent statusBarLeft;
//    private JLabel actionStatus;
    private JLabel tableStatus;
    private JLabel tableRows = new JLabel("0");
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
//        tableRows = new JLabel("0");
        bar.add(tableRows);
        bar.add(Box.createRigidArea(new Dimension(5, 22)));

        statusBar.add(bar);
        statusBar.add(Box.createHorizontalStrut(12));
        return statusBar;
    }

}
