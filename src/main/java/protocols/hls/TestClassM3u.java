package protocols.hls;

import java.util.HashMap;

class TestClassM3u {
    public static void main(String args[]) throws Exception {
        LivePlaylistM3u testObj = new LivePlaylistM3u("TEST STREAM",
                "http://video.beeline.tv/live/h/channel64007.isml/index-ios_mobile.m3u8");
        System.out.println(testObj);
        // проверка содержимого в HashMap
        System.out.println(testObj.getParamStream());
        // Проверяем работу в цикле
        // Сначала получаем первый набор данных из Медиа Плейлиста
        HashMap<String, String> data = testObj.loadMediaPlaylistURI( testObj.getParamStream().get("v0-289.m3u8?bk-teardown=1").get("mediaPlaylistURL") );
        while (true){
            System.out.println(data.get("urlMediaSegment"));
            Thread.sleep(Long.valueOf(data.get("EXTINF")).longValue() * 1000); // сек в мс
            data = testObj.loadMediaPlaylistURI( testObj.getParamStream().get("v0-289.m3u8?bk-teardown=1").get("mediaPlaylistURL") );
        }
    }
}