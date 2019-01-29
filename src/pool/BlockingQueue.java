package pool;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by bozhidar on 11.11.17.
 *
 * Code seen from:
 * http://www.makeinjava.com/custom-thread-pool-example-without-using-executor-framework/
 */
public class BlockingQueue<Type>  {
    private Queue<Type> queue = new LinkedList<Type>();
    private int EMPTY = 0;
    private int MAX_TASK_IN_QUEUE = -1;

    public BlockingQueue(int size){
        this.MAX_TASK_IN_QUEUE = size;
    }

    /**
     * Enqueues a task if possible. If the size is the maximum waits for the dequeue to remove an element.
     *
     * @param task is the task to be added
     * @throws InterruptedException
     */
    public synchronized void enqueue(Type task)
            throws InterruptedException  {
        while(this.queue.size() == this.MAX_TASK_IN_QUEUE) {
            wait();
        }
        if(this.queue.size() == EMPTY) {
            notifyAll();
        }
        this.queue.offer(task);
    }

    /**
     * Dequeues a task if possible. If the size is 0 it waits for the enqueue to add an element.
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized Type dequeue()
            throws InterruptedException{
        while(this.queue.size() == EMPTY){
            wait();
        }
        if(this.queue.size() == this.MAX_TASK_IN_QUEUE){
            notifyAll();
        }
        return this.queue.poll();
    }
}