package app.protocols.ss;

import java.util.HashMap;

public class LivePlaylistSs implements app.BasicStreamOtt {

    private String name;
    private String url;
    private HashMap<String,HashMap<String,String>> paramStream;

    public LivePlaylistSs(String name, String url) {
        this.name = name;
        this.url = url;
        this.paramStream = new HashMap<String,HashMap<String, String>>();
        loadMasterPlaylistData(); // загружаем Мастер-плейлист и заполняем HashMap paramStream параметрами потока
    }

    private void loadMasterPlaylistData(){

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getParamStream() {
        return this.paramStream;
    }

    @Override
    public String toString() {
        return "LivePlaylistSs{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
