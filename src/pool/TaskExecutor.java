package pool;

import server.ServerRunnable;

/**
 * Created by bozhidar on 11.11.17.
 *
 * Executor of ServerRunnable
 */
public class TaskExecutor implements Runnable {
    //The queue associated with the executor
    BlockingQueue<ServerRunnable> queue;
    //The current task being ran by the executor
    public ServerRunnable currentTask;

    public TaskExecutor(BlockingQueue<ServerRunnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ServerRunnable task = queue.dequeue();
                //change the current task
                currentTask = task;
                task.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
