package info.smartinsole.sqlite.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ObserverManager extends ObserverManagerTest {

    public static ObserverManager getInstance() {
        return ObserverManagerHolder.sObserverManager;
    }

    private static class ObserverManagerHolder {
        private static final ObserverManager sObserverManager = new ObserverManager();
    }

    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer obj) {
        observers.add(obj);
    }

    public void deleteObserver(Observer obj) {
        int i = observers.indexOf(obj);
        if (i >= 0) {
            observers.remove(obj);
        }
    }

    public void notifyObserver(BleDevice bleDevice) {
        for (int i = 0; i < observers.size(); i++) {
            Observer o = observers.get(i);
            o.disConnected(bleDevice);
        }
    }

}
