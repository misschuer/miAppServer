package cc.mi.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.mi.app.config.ServerConfig;
import cc.mi.app.net.AppHandler;
import cc.mi.app.net.AppToGateHandler;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.manager.GameDataManager;
import cc.mi.core.net.ClientCore;

public class Startup {
	static final CustomLogger logger = CustomLogger.getLogger(Startup.class);
	private static void start() throws NumberFormatException, Exception {
		ServerConfig.loadConfig();
		GameDataManager.INSTANCE.loads();
		connectGate();
		connectCenter();
	}
	
	private static void connectGate() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(
			new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							ClientCore.INSTANCE.start(ServerConfig.getGateIp(), ServerConfig.getGatePort(), new AppToGateHandler());
						} catch (Exception e) {
							logger.errorLog(e.getMessage());
						} finally {
							logger.errorLog("连接网关服错误,系统将在1秒钟后重新连接");
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		);
	}
	
	private static void connectCenter() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(
			new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							ClientCore.INSTANCE.start(ServerConfig.getCenterIp(), ServerConfig.getCenterPort(), new AppHandler());
						} catch (Exception e) {
							logger.errorLog(e.getMessage());
						} finally {
							logger.errorLog("连接中心服错误,系统将在1秒钟后重新连接");
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		);
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		start();
	}
}
