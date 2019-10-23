package protocols.hls;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmitryshcherbakov
 * тут создаем треды на каждый медиаплейлист в потоке
 */
public class ThreadMonStream implements Runnable{

    Thread t;
    private LivePlaylistM3u stream;

    public ThreadMonStream(LivePlaylistM3u stream){
        this.stream = stream;
        t = new Thread(this, stream.getName());
        t.start();
    }

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
            }
            for (ThreadMonMediaPlaylist t: thread) {
                try {
                    t.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            thread = new ArrayList();
        }
    }
}
