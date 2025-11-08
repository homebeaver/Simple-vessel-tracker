package tracker;

import java.net.URISyntaxException;

import io.github.homebeaver.aismodel.AisStreamKeyProvider;
import io.github.homebeaver.aismodel.AisStreamWebsocketClient;
import io.github.homebeaver.aisview.Regions;

/**
 * Class to create AIS Stream messages. The messages are printed to System.out.
 */
public class CreateAisMassages {

	public static void main(String[] args) throws URISyntaxException {
		if (args.length > 0) {
			AisStreamKeyProvider.getInstance().setKey(args[0]);;
		} else {
			AisStreamKeyProvider.getInstance().run();
		}
		
		AisStreamWebsocketClient client = 
			new AisStreamWebsocketClient(true, AisStreamKeyProvider.getInstance(),
					Regions.getInstance().getBoundingBox("Global"));
		client.connect();
	}

}
