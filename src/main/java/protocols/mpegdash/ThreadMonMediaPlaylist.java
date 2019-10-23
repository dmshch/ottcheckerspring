package protocols.mpegdash;

/**
 * Created by dmitryshcherbakov
 */

public class ThreadMonMediaPlaylist implements Runnable {

    private String key;
    Thread t;
    private LivePlaylistMpd stream;

    ThreadMonMediaPlaylist(LivePlaylistMpd stream, String key){
        this.stream = stream;
        this.key = key;
        t = new Thread(this, stream.getName());
        t.start();
    }

    @Override
    public void run() {
        System.out.println("ThreadMonMediaPlaylist");
        // 1 - Получаем из объекта stream ссылку для формирования пути к чанку, шаблон
        // 2 - получаем свежий тайминг
        // 3 - формируем ссылку на чанк
        // 4 - переполучаем тайминг и формируем свежие ссылки на чанки в цикле
    }
}
