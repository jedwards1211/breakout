package org.andork.swing.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.swing.Timer;

public class SetTimeout {
	public static Future<Void> setTimeout(Runnable r, long timeout) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Timer timer = new Timer((int) timeout, e -> {
			try {
				r.run();
				future.complete(null);
			} catch (Throwable ex) {
				future.completeExceptionally(ex);
			}
		});
		timer.setRepeats(false);
		timer.start();
		return future;
	}
}
