package ru.IDIS;

import java.util.concurrent.atomic.AtomicLong;

public class Ips {
    public Ips() {
    }
    
    public void start() {
        synchronized (this) {
            _received.set(0);
            _started.set(System.currentTimeMillis());
            _current.set(_started.get());
        }
    }
    
    public boolean isStarted() {
        return _started.get() != 0;
    }
    
    public void reset() {
        synchronized (this) {
            _received.set(0);
            _started.set(0);
            _current.set(0);
        }
    }
    
    public void increment() {
        _received.set(_received.get() + 1);
    }
    
    public float getIps() {
        _current.set(System.currentTimeMillis());
        long diff = _current.get() - _started.get();
        return diff == 0 ? 0f : ((float)_received.get() / (float)diff) * 1000f;
    }
    
    private AtomicLong _received    = new AtomicLong();
    private AtomicLong _started     = new AtomicLong();
    private AtomicLong _current     = new AtomicLong();
}
