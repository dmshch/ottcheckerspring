package controllers;

import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import protocols.BasicStreamOtt;
import protocols.hls.LivePlaylistM3u;
import protocols.mpegdash.LivePlaylistMpd;

@RestController
public class MainController {

        private static final String template = "OTT stream is: %s!";

        @RequestMapping("/checking")
        public BasicStreamOtt checking(@RequestParam(value="url", defaultValue="") String url) {

            BasicStreamOtt stream = null;
            // Необходимо будет проверить ссылку и вызвать соответствующий конструктор для текущего протокола
            try {
                if (url.contains(".m3u8")) {
                    stream = new LivePlaylistM3u("StreamLive", url);
                }
                if (url.contains(".mpd")) {
                    stream = new LivePlaylistMpd("StreamLive", url);
                }
            } catch (IOException e) {
                url = e.toString();
            }
            return stream;
        }
}
