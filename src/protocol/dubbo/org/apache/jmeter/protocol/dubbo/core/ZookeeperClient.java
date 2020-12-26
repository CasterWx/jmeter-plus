package org.apache.jmeter.protocol.dubbo.core;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author AntzUhl
 * @Date 2020/12/26 17:21
 * @Description
 */
public class ZookeeperClient {

    private static final Logger log = LoggingManager.getLoggerForClass();

    public static ZooKeeper zkClient(String url, int timeout) {
        ZooKeeper zooKeeper = null;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zooKeeper = new ZooKeeper(url, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            log.info("Init Zookeeper connect state: " + zooKeeper.getState());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
