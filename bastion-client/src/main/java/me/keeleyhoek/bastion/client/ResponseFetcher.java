package me.keeleyhoek.bastion.client;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import me.keeleyhoek.bastion.Response;

public class ResponseFetcher {

    private static final Thread WORK_THREAD;
    private static final LinkedBlockingQueue<FetchRequest> WORK_QUEUE = new LinkedBlockingQueue<>();
    private static final HashMap<FetchRequest, Semaphore> WAKE_MAP = new HashMap<>();
    private static volatile boolean doWork = true;

    static {
        WORK_THREAD = new Thread() {
            @Override
            public void run() {
                while (doWork) {
                    try {
                        FetchRequest r = WORK_QUEUE.take();

                        r.execute();

                        synchronized (WAKE_MAP) {
                            Semaphore toWake = WAKE_MAP.get(r);
                            if (toWake != null) {
                                toWake.release();
                            }
                        }
                    } catch (InterruptedException ex) {
                        //Intentionally continue
                    } catch (Exception ex) {
                        //TODO handle this better
                        Logger.getLogger(ResponseFetcher.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, ex, "Critical Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        WORK_THREAD.setDaemon(true);
        WORK_THREAD.start();
    }
    
    public static void shutdown() {
        doWork = false;
        WORK_THREAD.interrupt();
    }

    public static <T> void enqueue(FetchRequest<T> work) {
        WORK_QUEUE.add(work);
    }

    public static <T> void enqueueSynchronous(FetchRequest<T> work) {
        if (EventQueue.isDispatchThread()) {
            throw new Error("Synch lock on EDT!");
        }

        Semaphore s = new Semaphore(1);

        synchronized (WAKE_MAP) {
            WAKE_MAP.put(work, s);
        }

        WORK_QUEUE.add(work);

        s.acquireUninterruptibly();
    }

    public static abstract class FetchRequest<T> {

        private final String service;
        private final java.lang.reflect.Type type;
        private final ProtocolDelegate.Parameter[] params;

        public FetchRequest(String service, ProtocolDelegate.Parameter... params) {
            this(service, params, true);
        }

        protected FetchRequest(String service, ProtocolDelegate.Parameter params[], boolean needsGenericType) {
            this.service = service;
            this.params = params;
            this.type = getRequestType();

            if (needsGenericType) {
                if (type == null) {
                    throw new RuntimeException("no generic type - use PushRequest instead!");
                }
            }
        }

        private java.lang.reflect.Type getRequestType() {
            java.lang.reflect.Type clazz = getClass().getGenericSuperclass();
            if (clazz instanceof Class) {
                return null;
            } else {
                return ((ParameterizedType) clazz).getActualTypeArguments()[0];
            }
        }

        public final void execute() {
            try {
                Response<T> response = NetworkUtil.getRemote().request(service, type, params);
                if (response.getStatus() == Response.Status.SUCCESS) {
                    handleSuccess(response.getObject());
                } else {
                    handleError(response.getStatus(), response.getMessage());
                }
            } catch (IOException ex) {
                StringWriter sw = new StringWriter();
                PrintWriter ps = new PrintWriter(sw);
                ex.printStackTrace(ps);
                ps.close();
                handleError(Response.Status.CLIENT_EXCEPTION, sw.toString());
            }
        }

        public abstract void handleSuccess(T obj);

        public abstract void handleError(Response.Status code, String msg);
    }

    public static abstract class PushRequest extends FetchRequest {

        public PushRequest(String service, ProtocolDelegate.Parameter... params) {
            super(service, params, false);
        }

        @Override
        public void handleSuccess(Object obj) {
            if (obj != null) {
                throw new RuntimeException("response returned when no response expected!");
            }

            handleSuccess();
        }

        public abstract void handleSuccess();
    }

    public static abstract class ErrorHandlingPushRequest extends PushRequest {

        public ErrorHandlingPushRequest(String service, ProtocolDelegate.Parameter... params) {
            super(service, params);
        }

        @Override
        public void handleError(Response.Status code, String msg) {
            JOptionPane.showMessageDialog(null, code + ":" + msg, "Critical Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
