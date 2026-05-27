package com.zafta.client.agent.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public final class SingleInstanceLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceLock.class);
    private static FileLock lock;
    private static RandomAccessFile lockFile;

    private SingleInstanceLock() {}

    public static boolean isAlreadyRunning() {
        try {
            File file = new File("app.lock");
            lockFile = new RandomAccessFile(file, "rw");
            lock = lockFile.getChannel().tryLock();
            return lock == null;
        } catch (IOException e) {
            LOGGER.error("Lock error", e);
            return true;
        }
    }

    public static void release() {
        try {
            if (lock != null) lock.release();
            if (lockFile != null) lockFile.close();
        } catch (IOException e) {
            LOGGER.error("Lock release error", e);
        }
    }
}
