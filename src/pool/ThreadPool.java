package pool;

import server.ServerRunnable;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by bozhidar on 11.11.17.
 */
public class ThreadPool {
    BlockingQueue <ServerRunnable> queue;
    private TaskExecutor[] executors;

    public ThreadPool(int queueSize, int nThread) {
        queue = new BlockingQueue<>(queueSize);
        executors = new TaskExecutor[nThread];
        //Initialise executors
        for (int count = 0; count < nThread; count++) {
            executors[count] = new TaskExecutor(queue);
            Thread thread = new Thread(executors[count]);
            thread.start();
        }
    }

    /**
     * Submits a new ServerRunnable to the pool.
     *
     * @param task
     * @throws InterruptedException
     */
    public void submitTask(ServerRunnable task) throws InterruptedException {
        queue.enqueue(task);
    }

    /**
     * It sends an ElectionCompleteMessage to all currently working ServerRunnable.
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void sendMessageToAll() throws InvocationTargetException, IllegalAccessException {
        for (TaskExecutor e : executors) {
            if (e.currentTask != null) {
                try {
                    e.currentTask.getProtocol().sendResult();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
