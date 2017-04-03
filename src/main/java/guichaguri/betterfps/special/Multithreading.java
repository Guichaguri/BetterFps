package guichaguri.betterfps.special;

/**
 * @author Guilherme Chaguri
 */
public class Multithreading extends Thread {

    public static Thread start(IMultithreaded m, String task, boolean async) {
        if(async) {
            return start(m, task);
        } else {
            m.run(task);
        }
        return null;
    }

    public static Thread start(IMultithreaded m, String task) {
        Multithreading thread = new Multithreading(m, task) {
            @Override
            public void run() {
                super.run();
            }
        };
        thread.start();
        return thread;
    }

    public static void stop(Thread thread) {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    private final IMultithreaded m;
    private final String task;

    public Multithreading(IMultithreaded m, String task) {
        this.m = m;
        this.task = task;
    }

    @Override
    public void run() {
        m.run(task);
    }

    public interface IMultithreaded {
        void run(String task);
    }

}
