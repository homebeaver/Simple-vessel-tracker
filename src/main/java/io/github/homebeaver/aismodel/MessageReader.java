package io.github.homebeaver.aismodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.json.JSONException;

// XXX Singleton ?
public class MessageReader {

	private static final Logger LOG = Logger.getLogger(MessageReader.class.getName());

	private Map<Integer, List<AisStreamMessage>> map = null;

	public MessageReader() {
		this.map = new HashMap<Integer, List<AisStreamMessage>>();
	}

	// Map <MMSI , track>
	public Map<Integer, List<AisStreamMessage>> getMap() {
		return this.map;
	}

	public AisStreamMessage readMessage(String messageJson) {
		try {
			AisStreamMessage asm = AisStreamMessage.fromJson(messageJson);
			if (asm == null) {
				return null;
			}
			int key = asm.metaData.getMMSI();
			if (key == 0) {
				throw new JSONException("Null key (MMSI==0) in " + asm.metaData);
			}
			if (!map.containsKey(key)) {
				List<AisStreamMessage> waypoints = new Vector<AisStreamMessage>();
				map.put(key, waypoints); // empty List
			} else {
				// LOG.info(">>>>>>>>>>>>>>>>> "+key + "\n"+map.get(key).get(0).message + "\n" + asm.message);
			}
			map.get(key).add(asm);
			return asm;
		} catch (Exception e) {
			LOG.warning("Error reading message: " + messageJson + " " + e);
			return null;
		}
	}

}
