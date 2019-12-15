package app;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.protocols.hls.*;
import app.protocols.mpegdash.*;

@RestController
public class MainController {

    enum Status {OK, ERROR}

    ArrayList<BasicStreamOtt> allStreams = new ArrayList<>();

    private static final String template = "OTT stream is: %s!";

    @RequestMapping("addstream")
    String addstream(@RequestParam String name, @RequestParam String url) {
        System.out.println(name + " " + url);
        // вызов функции добавление нового потока
        if (add(name, url) == Status.ERROR){
            return "func addstream error:" + "name="+name+";url="+url;
        }
        return "func addstream ok:" + "name="+name+";url="+url;
    }

    // по умолчанию возвращает все потоки, при передаче аргумента name - только запрошенный
    @RequestMapping("getstream")
    String getstream(@RequestParam String name) {

        return "getstream";
    }

    @RequestMapping("deletestream")
    String deletestream(@RequestParam String name) {
        // вызов функции удаления потока по указанному имени
        return "deletestream";
    }

    // Создаёт объект потока, добавляет его в массив и возвращает статус операции
    // Сделать проверку для совпадения имён в массиве, они должны быть уникальны
    public Status add(String name, String url) {
        BasicStreamOtt stream = null;
        // Необходимо будет проверить ссылку и вызвать соответствующий конструктор для текущего протокола
        try {
            if (url.contains(".m3u8")) {
                stream = new LivePlaylistM3u(name, url);
            }
            if (url.contains(".mpd")) {
                stream = new LivePlaylistMpd(name, url);
            }
        } catch (IOException e) {
            System.out.println(e);
            return Status.ERROR;
        }
        allStreams.add(stream);
        return Status.OK;
    }

    // Удалает из ArrayList объект потока
    public Status delete(String name){
        return Status.OK;
    }

}
