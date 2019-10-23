package protocols.hls;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmitryshcherbakov
 * класс, содержащий параметры hls-потока по заданному url
 * конструктор будет требовать имя потока name и ссылку на url-адрес потока
 * Эта версия предназначена для Live Media Playlists
 */

public class LivePlaylistM3u implements protocols.BasicStreamOtt{

    private String name;
    public String getName() {
        return this.name;
    }
    private String url;
    public String getUrl() {
        return this.url;
    }

    private HashMap<String,HashMap<String,String>> paramStream;
    public HashMap<String,HashMap<String,String>> getParamStream(){
        return this.paramStream;
    }

    public LivePlaylistM3u(String name, String url) throws IOException{ // конструктор
        this.name = name;
        this.url = url;
        this.paramStream = new HashMap<String,HashMap<String, String>>();
        loadMasterPlaylistData(); // загружаем Мастер-плейлист и заполняем HashMap paramStream параметрами потока
    }

    // http://stackoverflow.com/questions/4328711/read-url-to-string-in-few-lines-of-java-code
    // получем все параметры из Мастер-плейлиста + формируем ссылки по ключу mediaPlaylistURL для Медиа-плейлистов
    // сделано с учётом Медиа-плейлистов у нас
    private void loadMasterPlaylistData() {
        HashMap<String, String> allMap = new HashMap<String,String>();
        String out = null;
        try {
            out = loadUrl(this.url);
        }
        catch (IOException io) {
            System.out.println("Ошибка при загрузке Мастер Плейлиста");
            System.exit(-1);
        }

        String[] tempList = out.split("\n"); // Превращаем Мастер-плейлист в массив строк для разбора
        for (String i : tempList) {
            if (i.contains("EXT-X-I-FRAME-STREAM-INF")){ // пока что пропускаем
                continue;
            }
            if (i.contains("EXT-X-MEDIA")){ // пока что пропускаем
                continue;
            }
//            if (i.contains("EXT-X-MEDIA:TYPE=AUDIO")) {
//                parsingMasterPlaylistDataAudio(i);
//            }
            if (i.contains("EXT-X-STREAM-INF")){ // если в строке нашли заданную подстроку
                allMap = parsingMasterPlaylistDataVideo(i);
            }
            if (i.contains("m3u8")){ // в этой строке будет ключ
                // тут добавляем так же по ключу mediaPlaylistURI ссылки на Медиа-плейлисты
                String urlMediaPlaylist = (String) this.url.subSequence(0,this.url.lastIndexOf("/") + 1); // обрезали всё после слеша
                allMap.put("mediaPlaylistURL" , urlMediaPlaylist + i ); // склеиваем Url для Медиа-плейлиста и добавляем в HashMap
                this.paramStream.put(i, (HashMap)allMap.clone()); // clone() возвращает Object, приводим его к HashMap
                allMap.clear();
            }
        }
    }

    // парсим Мастер-плейлист и возвращаем HashMap с параметрами
    // аудио мультиплексировано или отдельными чанками?
    private HashMap<String, String> parsingMasterPlaylistDataVideo(String line) {
        HashMap<String, String> returnMap = new HashMap<>();
        if (line.contains("BANDWIDTH")){
            returnMap.put("bitrate", splitText(line, "BANDWIDTH=", ",") );
        }
//        if (line.contains("AVERAGE-BANDWIDTH")){
//            returnMap.put("AVERAGE-BANDWIDTH", splitText(line, "AVERAGE-BANDWIDTH=", ",") );
//        }
//        if (line.contains("CODECS")){
//            returnMap.put("CODECS", splitText(line, "CODECS=\"", "\"") );
//        }
        if (line.contains("RESOLUTION")){
            returnMap.put("resolution", splitText(line, "RESOLUTION=", ",") );
        }
//        if (line.contains("FRAME-RATE")){
//            returnMap.put("FRAME-RATE", splitText(line, "FRAME-RATE=", ",") );
//        }
//        if (line.contains("AUDIO")){
//            returnMap.put("AUDIO", splitText(line, "AUDIO=\"", "\"") );
//        }
//        if (line.contains("VIDEO")){
//            returnMap.put("VIDEO", splitText(line, "VIDEO=\"", "\"") );
//        }
//        if (line.contains("SUBTITLES")){
//            returnMap.put("SUBTITLES", splitText(line, "SUBTITLES=\"", "\"") );
//        }
//        if (line.contains("CLOSED-CAPTIONS")){
//            returnMap.put("CLOSED-CAPTIONS", splitText(line, "CLOSED-CAPTIONS=\"", "\"") );
//        }
        return returnMap;
    }

    // Разбираем строку с данными по звуку
//    private void parsingMasterPlaylistDataAudio(String line){
//        HashMap<String, String> returnMap = new HashMap<>();
//        if (line.contains("LANGUAGE")){
//            returnMap.put("language", splitText(line, "LANGUAGE=\"", "\"") );
//        }
//    }


    // Загружаем Медиа-плейлист, вызывая метод для объекта, данные сохраняются
    // данные будут - VERSION,TARGETDURATION,EXTINF, MEDIA-SEQUENCE, ссылка на Медиа Сегмент и текущий Медиа Плейлист - на в.с.(Всякий Случай)
    // возвращаться будут в HashMap
    public HashMap<String, String> loadMediaPlaylistURI(String url) throws IOException{
        HashMap<String, String> allMap = new HashMap<>(); // отдаём его в точку вызова
        HashMap<String, String> tempMap = new HashMap<>(); // сюда возвращаем данные из parsingMediaPlaylistData
        int flag = 0; // флаг, что дальше будет ссылка на Медиа Сегмент

        String out = loadUrl(url);

        // Тут получить переадресацию?
        HttpURLConnection myUrlCon = (HttpURLConnection) new URL(url).openConnection();
        if (myUrlCon.getInstanceFollowRedirects() == false) {
            myUrlCon.setInstanceFollowRedirects(true);
        }

        // Для получения url с редиректом
        myUrlCon.getInputStream();
        //System.out.println("Redirected URL: " + myUrlCon.getURL());
        //String temp = myUrlCon.getURL().toString();

        String[] tempList = out.split("\n"); // Превращаем Медиа Плейлист в массив строк для разбора
        for (String i:tempList){
            if (flag == 1){
                // обрезаем ссылку и склеиваем новую на текущий Медиа Сегмент, подставляем url после редиректа
                String urlMediaSegment = (String) myUrlCon.getURL().toString().subSequence(0,myUrlCon.getURL().toString().lastIndexOf("/") + 1) + i; // обрезали всё после слеша
                allMap.put("urlMediaSegment",urlMediaSegment );
                break; // получили первую ссылку и уходим
            }
            if (i.contains("EXTINF")){
                flag = 1;
            }
            tempMap = parsingMediaPlaylistData(i);
            // НЕРАЦИОНАЛЬНО.. Из полученного HashMap переношу значения в allMap
            for (HashMap.Entry<String, String> entry : tempMap.entrySet()){
                allMap.put(entry.getKey(), entry.getValue());
            }
        }
        // добавляем в HashMap весь текущий Медиа Плейлист
        allMap.put("FullMediaPlaylist", out);
        return allMap;
    }

    // парсим Медиа Плейлист и возвращаем HashMap с параметрами
    private HashMap<String, String> parsingMediaPlaylistData(String line) {
        HashMap<String, String> returnMap = new HashMap<String,String>();
        if (line.contains("EXT-X-VERSION")){
            returnMap.put("EXT-X-VERSION", splitText(line, "EXT-X-VERSION:", "") );
        }
        if (line.contains("EXT-X-TARGETDURATION")){
            returnMap.put("EXT-X-TARGETDURATION", splitText(line, "EXT-X-TARGETDURATION:", "") );
        }
        if (line.contains("EXT-X-MEDIA-SEQUENCE")){
            returnMap.put("EXT-X-MEDIA-SEQUENCE", splitText(line, "EXT-X-MEDIA-SEQUENCE:", "") );
        }
        if (line.contains("EXTINF")){
            // слово + один или более цифровых символов, берём первое вхождение
            Matcher matcher = Pattern.compile("\\d+").matcher(line);
            matcher.find();
            returnMap.put("EXTINF", line.substring(matcher.start(), matcher.end()) );
        }
        return returnMap;
    }

    // возвращает отпарсенные параметры для сохранения в HashMap
    // разбор отдельных строк в Мастер-плейлисте (и Медиа-плейлисте???)
    private String splitText(String full_line, String small_line, String stop_symbol){
        String out = "";
        int lenghtFull = full_line.length(); // получаем длину всей строки
        int lenghtSmall = small_line.length(); // получаем длину искомой подстроки
        int start = full_line.indexOf(small_line); // возвращает позицию в строке или -1 если не найдено вхождение
        if (start != -1){
            // subSequence возвращает подстроку из вызывающей строки с нач по кон индекс
            String temp = (String) full_line.subSequence(start + lenghtSmall, lenghtFull );
            // находим индекс стоп-символа и извлекаем искомое методом substring()
            int stopIndex = temp.indexOf(stop_symbol);
            if (stop_symbol.equals("")){ // добавил для Медиа Плейлиста, так как по \n и \r не срабатывает
                out = temp.substring(0);
            }
            else if (stopIndex != -1){
                out = temp.substring(0, stopIndex);
            }
            else{
                System.out.println("Вхождение стоп-символа не найдено!");
            }
        }
        else{
            System.out.println("Подстрока не найдена!");
        }
        return out;
    }

    @Override
    protected void finalize(){
        System.out.println("Объект " + this + " был уничтожен");
    }

    @Override
    public String toString(){
        return "Медиапоток: " + this.name + " : " + this.url;
    }
}

/* пример Мастер-плейлиста
#EXTM3U
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=428640,RESOLUTION=480x270,CODECS="avc1.42e015,mp4a.40.2"
02.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=5235424,RESOLUTION=1280x720,CODECS="avc1.4d4029,mp4a.40.2"
06.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=171456,RESOLUTION=320x180,CODECS="avc1.42e00c,mp4a.40.2"
01.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=3695328,RESOLUTION=1280x720,CODECS="avc1.4d4020,mp4a.40.2"
05.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1025728,RESOLUTION=640x360,CODECS="avc1.42e01e,mp4a.40.2"
03.m3u8
#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1949184,RESOLUTION=960x540,CODECS="avc1.4d401f,mp4a.40.2"
04.m3u8
*/

/* Пример Медиа-плейлиста
#EXTM3U
#EXT-X-VERSION:3
#EXT-X-TARGETDURATION:11
#EXT-X-MEDIA-SEQUENCE:2442168
#EXTINF:10,
20151111T070821-01-2442168.ts
#EXTINF:10,
20151111T070821-01-2442169.ts
#EXTINF:10,
20151111T070821-01-2442170.ts
*/

/* пример заполненного HashMap
02.m3u8 : PROGRAM-ID:1, BANDWIDTH:428640, RESOLUTION:480x270 ...
06.m3u8 :
...
*/

/*
Set<Map.Entry<String, HashMap<String, String>>> set = this.paramStream.entrySet(); // получаем множество записей
// проходим по множеству, формируем ссылки и добавляем
for (Map.Entry<String, HashMap<String, String>> me : set ){

        }
*/

/* пример объекта
{v0-289.m3u8=
{CODECS=avc1.64001F,mp4a.40.2, AVERAGE-BANDWIDTH=2898682, RESOLUTION=1024x576, BANDWIDTH=4424304, AUDIO=mp4a.40.2-160000-44100-2, mediaPlaylistURL=http://video.......m3u8},
v1-289.m3u8=
{CODECS=avc1.64001E,mp4a.40.2, AVERAGE-BANDWIDTH=2703015, RESOLUTION=704x396, BANDWIDTH=4125654, AUDIO=mp4a.40.2-160000-44100-2, mediaPlaylistURL=http://video.......m3u8}}
*/