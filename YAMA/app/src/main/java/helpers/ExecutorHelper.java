package helpers;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorHelper {

    private static volatile ExecutorHelper INSTANCE;
    private static final int THREAD_COUNT = 5;

    private final Executor mainThreadExecutor;
    private final Executor networkExecutor;
    private final Executor diskExecutor;



    public ExecutorHelper(Executor mainThreadExecutor, Executor networkExecutor, Executor diskExecutor) {
        this.mainThreadExecutor = mainThreadExecutor;
        this.networkExecutor = networkExecutor;
        this.diskExecutor = diskExecutor;
    }

    public static ExecutorHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (ExecutorHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExecutorHelper(new MainThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),  Executors.newSingleThreadExecutor());
                }
            }
        }
        return INSTANCE;
    }

    public Executor diskIO() {
        return diskExecutor;
    }

    public Executor mainThread() {
        return mainThreadExecutor;
    }

    public Executor networkIO() {
        return networkExecutor;
    }

    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
