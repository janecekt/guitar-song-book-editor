package songer.logback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

public class EventAppender<E> extends AppenderBase<E> {
    private static Map<String,EventAppender<?>> instances = new ConcurrentHashMap<String,EventAppender<?>>();
    
    public static EventAppender<?> getByName(String name) {
        return instances.get(name);
    }
    
    public interface LogEventListener {
        void onLogEvent(String logMessage);
    }
    
    private Layout<E> layout;
    private Collection<WeakReference<LogEventListener>> listeners = new ArrayList<WeakReference<LogEventListener>>();

    public void addListener(LogEventListener listener) {
        this.listeners.add(new WeakReference<LogEventListener>(listener));
    }

    @Override
    protected void append(E eventObject) {
        String msg = layout.doLayout(eventObject);
        for (Iterator<WeakReference<LogEventListener>> it = listeners.iterator(); it.hasNext(); ) {
            LogEventListener listener = it.next().get();
            if (listener != null) {
                listener.onLogEvent(msg);
            } else {
                it.remove();
            }
        }
    }

    @Override
    public void start() {
        // Register instance
        String name = getName();
        if (name != null && !name.isEmpty()) {
            instances.put(name,this);
        }
        super.start();
    }

    public Layout<E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

}
