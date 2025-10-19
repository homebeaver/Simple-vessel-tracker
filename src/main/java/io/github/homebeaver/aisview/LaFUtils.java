package io.github.homebeaver.aisview;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.swingx.SwingXUtilities;

public class LaFUtils {

    private static final Logger LOG = Logger.getLogger(LaFUtils.class.getName());

    static final String METAL_LAF = "javax.swing.plaf.metal.MetalLookAndFeel";
    static final String METAL = "Metal";
    static final String STEEL = "javax.swing.plaf.metal.DefaultMetalTheme";
    static final String OCEAN = "javax.swing.plaf.metal.OceanTheme";
    
    private static final LaFUtils INSTANCE = new LaFUtils();
    
    private Map<String, List<String>> lafInfoMap; // info -> [classname] | [classname,themeclassname]
    private LaFUtils() {
        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
        lafInfoMap = new Hashtable<String, List<String>>();
        for (LookAndFeelInfo info : lafInfo) {
            LOG.fine(info.getName() + " ClassName="+info.getClassName());
            if (METAL.equals(info.getName())) {
                lafInfoMap.putIfAbsent(METAL+"+STEEL"
                        , Arrays.asList(info.getClassName(), STEEL));
                lafInfoMap.putIfAbsent(METAL+"+OCEAN"
                        , Arrays.asList(info.getClassName(), OCEAN));
//                JMenuItem mi = createLafMenuItem(info);
//                lafMenuGroup.add(mi);
//                menu.add(mi);
            } else {
                lafInfoMap.putIfAbsent(info.getName(), Arrays.asList(info.getClassName()));
//                JMenuItem mi = createLafMenuItem(info);
//                LOG.fine("JMenuItem mi.Action:"+mi.getAction() + " ClassName="+info.getClassName());
//                lafMenuGroup.add(mi);
//                if(info.getClassName().equals(UIManager.getLookAndFeel().getClass().getName())) {
//                    mi.setSelected(true);
//                }
//                menu.add(mi);
            }
        }
    }
    private String getCurrentLAFandThemeKey() {
    	String currentClassName = UIManager.getLookAndFeel().getClass().getName();
    	for (Map.Entry<String, List<String>> entry : lafInfoMap.entrySet()) {
    		String key = entry.getKey();
    		List<String> list = entry.getValue();
    		if (currentClassName.equals(list.get(0))) {
    			if(currentClassName.contains(METAL)) {
    	    		javax.swing.plaf.metal.MetalTheme currentTheme = javax.swing.plaf.metal.MetalLookAndFeel.getCurrentTheme();
    	    		String currentThemeClassName = currentTheme.getClass().getName();
    	    		if (currentThemeClassName.equals(list.get(1))) return key;
    			} else {
    				return key;
    			}
    		}
    	}
    	return null;
    }
    
    public static String getCurrentLAFandThemeString() {
    	return INSTANCE.getCurrentLAFandThemeKey();
    }
    
    public static Set<String> getLAFandThemeKeySet() {
    	return INSTANCE.lafInfoMap.keySet();
    }
    
    public static void setLAFandTheme(String key) {
    	setLAFandTheme(INSTANCE.lafInfoMap.get(key));
    }
    
    private static boolean setLAF(String nameSnippet) {
    	String currentClassName = UIManager.getLookAndFeel().getClass().getName();
    	if(currentClassName.contains(nameSnippet)) {
    		LOG.config("current LaF is "+currentClassName);
    	}
    	UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
    	for (LookAndFeelInfo info : lafInfo) {
    		String lafClassName = info.getClassName();
    		if(lafClassName.contains(nameSnippet)) {
    			try {
    	    		LOG.config("switch to LaF ClassName="+lafClassName + " from "+UIManager.getLookAndFeel());
					UIManager.setLookAndFeel(lafClassName);
//					if (update) SwingXUtilities.updateAllComponentTreeUIs();
					return !lafClassName.equals(currentClassName);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	LOG.warning("Look and Feel not found: "+nameSnippet);
    	setLAFandTheme(INSTANCE.lafInfoMap.get(nameSnippet));
    	return false;
    }

	public static void setLAFandTheme(List<String> argList) {
		LOG.config("args:" + argList);
		if (argList == null) {
			throw new IndexOutOfBoundsException("argList is null");
		}
		boolean lafChanged = setLAF(argList.get(0));
		if (METAL.equalsIgnoreCase(argList.get(0)) || METAL_LAF.equals(argList.get(0))) {
			String themeClassName = argList.size() > 1 ? argList.get(1) : null;
			javax.swing.plaf.metal.MetalTheme currentTheme = javax.swing.plaf.metal.MetalLookAndFeel.getCurrentTheme();
			String currentThemeClassName = currentTheme.getClass().getName();
			if (currentThemeClassName.equals(themeClassName)) {
				LOG.info("current theme:" + currentThemeClassName);
			} else if (themeClassName != null) {
				LOG.info("current theme:" + currentThemeClassName + " try set to " + themeClassName);
				Class<?> themeClass = null;
				try {
					themeClass = Class.forName(themeClassName); // throws ClassNotFoundException
				} catch (Exception e) {
					LOG.severe("Error occurred loading class: " + themeClassName);
					e.printStackTrace();
				}
				try {
					javax.swing.plaf.metal.MetalTheme theme = (javax.swing.plaf.metal.MetalTheme) themeClass.getDeclaredConstructor().newInstance();
					javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
					SwingXUtilities.updateAllComponentTreeUIs();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (lafChanged) {
			SwingXUtilities.updateAllComponentTreeUIs();
			LOG.config("Look and Feel changed.");
		}
	}

}
