package io.github.homebeaver.aismodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AisStreamKeyProvider implements AisStreamKey {

	private static AisStreamKeyProvider instance = null; // SINGLETON

	public static AisStreamKeyProvider getInstance() {
		if (instance == null) {
			instance = new AisStreamKeyProvider();
		}
		return instance;
	}

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String apikey;

	private AisStreamKeyProvider() {
		apikey = System.getProperty("AIS_API_KEY");
	}
	@Override
	public String getKey() {
		return apikey;
	}

	public void setKey(String key) {
		apikey = key;
	}
	
	public void run() {
		System.out.print("Enter your AISStream API key : ");
		try {
			String input = br.readLine();
			if (input.isEmpty()) {
				System.out.println("No input - Exit!");
				System.exit(0);
			}
			this.apikey = input;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		if (AisStreamKeyProvider.getInstance().getKey()==null) AisStreamKeyProvider.getInstance().run();
//		System.out.println(AisStreamKeyProvider.getInstance().getKey());
	}

}
