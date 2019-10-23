package protocols.mpegdash;

import java.util.HashMap;

public class TestClassMpd {
    public static void main(String args[]) throws Exception {
        LivePlaylistMpd testObj = new LivePlaylistMpd("TEST STREAM",
                "http://video.beeline.tv/live/d/channel64007.isml/manifest-stb.mpd");
        System.out.println(testObj.getParamStream().toString());
        // проверка содержимого в HashMap
//        System.out.println(testObj.getParamStream());
//        // Проверяем работу в цикле
//        // Сначала получаем первый набор данных из Медиа Плейлиста
//        HashMap<String, String> data = testObj.loadMediaPlaylistURI( testObj.getParamStream().get("v0-289.m3u8?bk-teardown=1").get("mediaPlaylistURL") );
//        while (true){
//            System.out.println(data.get("urlMediaSegment"));
//            Thread.sleep(Long.valueOf(data.get("EXTINF")).longValue() * 1000); // сек в мс
//            data = testObj.loadMediaPlaylistURI( testObj.getParamStream().get("v0-289.m3u8?bk-teardown=1").get("mediaPlaylistURL") );
//        }
    }

}
