package app;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.protocols.hls.*;
import app.protocols.mpegdash.*;

@RestController
@RequestMapping("/checking")
public class MainController {

        private static final String template = "OTT stream is: %s!";

        @GetMapping
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
