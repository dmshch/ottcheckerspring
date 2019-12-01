package app.protocols.mpegdash;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmitryshcherbakov
 */

public class ThreadMonStream implements Runnable {

    private Thread t;
    private LivePlaylistMpd stream;

    public ThreadMonStream(LivePlaylistMpd stream){
        this.stream = stream;
        t = new Thread(this, stream.getName());
        t.start();
    }

    @Override
    public void run() {
        ArrayList<ThreadMonMediaPlaylist> thread = new ArrayList();
        String key;
        HashMap<String,HashMap<String,String>> streamData = stream.getParamStream(); // получаем HashMap со статичными данными потока
        boolean flag = true;
        while (flag){
            for (HashMap.Entry<String, HashMap<String,String>> newentry : streamData.entrySet()) {
                // тут создаём потоки для каждой ссылки на Медиа Плейлист
                key = newentry.getKey();
                ThreadMonMediaPlaylist tr = new ThreadMonMediaPlaylist(stream, key);
                thread.add(tr);
                System.out.println(key);
            }
            for (ThreadMonMediaPlaylist t: thread) {
                try {
                    t.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
