package domain;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SyncThread extends Thread {

	private static final int SYNC_TIME = 30;
	public static GameState gameState;

	public void run() {
		try {
			while (true) {
				TimeUnit.SECONDS.sleep(SYNC_TIME);
				

			}

		} catch (InterruptedException e) {
			System.err.println("Sync Thread has been interrupted.");
			e.printStackTrace();
		}

	}

}
