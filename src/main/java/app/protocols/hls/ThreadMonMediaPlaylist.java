package app.protocols.hls;

import app.BasicStreamOtt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dmitryshcherbakov
 */
public class ThreadMonMediaPlaylist implements Runnable{

    private String key;
    Thread t;
    private LivePlaylistM3u stream;

    ThreadMonMediaPlaylist(LivePlaylistM3u stream, String key){
        this.stream = stream;
        this.key = key;
        t = new Thread(this, stream.getName());
        t.start();
    }

    @Override
    public void run() {
        String urlMS = stream.getParamStream().get(key).get("mediaPlaylistURL");
        HashMap<String,String> dataMediaPlaylist = null;
        String sequence = null;
        // в этот HashMap возвращаются динамические данные из Медиа Плейлиста и ссылка на сегмент,
        // его теперь нужно будет периодически обновлять с интервалом EXTINF или больше
        try {
            dataMediaPlaylist = stream.loadMediaPlaylistURI(urlMS);
            sequence = dataMediaPlaylist.get("EXT-X-MEDIA-SEQUENCE");
            System.out.println("Медиаплейлист по ссылке " + urlMS + " доступен.");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                // недоступен плейлист -> недоступно и EXTINF, какую паузу делать?
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return; // делаем паузу и пропускаем итерацию если не удалось считать
        }

        // ******************************
        // тут проверяем как-либо сегмент, в простом случаем - возможность получить его
        // в сложном - загрузка сегмента и разбор MPEG TS
        BufferedInputStream in = null;

        try {

            in = new BufferedInputStream(BasicStreamOtt.getInput(dataMediaPlaylist.get("urlMediaSegment")));

            final byte data[] = new byte[1024];
            int count;
            if ((count = in.read(data, 0, 1024)) != -1) {
                // данные по ссылке есть
                // отдаём подключённому клиенту
                System.out.println("Чанк " + dataMediaPlaylist.get("urlMediaSegment") + " доступен.");
            } else {
                // иначе отправляем сообщение об ошибке на данном потоке
            }
        } catch (IOException e) {
            e.printStackTrace();
            // в случае исключения так же формируем сообщение об ошибке на данном потоке
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // ждём обновления плейлиста
        try {
            Thread.sleep(Long.valueOf(dataMediaPlaylist.get("EXTINF")).longValue() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ждём если EXT-X-MEDIA-SEQUENCE не обновился
//    boolean ToSleepOrNotToSleep(String one, String two, Integer time){
//        boolean flag = false;
//        try {
//            if (one.equals(two)) {
//                Thread.sleep(time * 1000 / 2); // ждём обновления плейлиста
//                flag = true;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return flag;
//    }
}