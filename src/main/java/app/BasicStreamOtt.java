package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by dmitryshcherbakov
 */

public interface BasicStreamOtt {

    String getName();
    String getUrl();
    String toString();

    HashMap<String, HashMap<String,String>> getParamStream();

    default String loadUrl(String url) throws IOException{

        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("", 8080));

        HttpURLConnection myUrlCon;
        // Pass proxy object if needed
        myUrlCon = (HttpURLConnection) new URL(url).openConnection();

        if (myUrlCon.getInstanceFollowRedirects() == false) {
            myUrlCon.setInstanceFollowRedirects(true);
        }

        String forReturn = "";
        InputStream inputStream = myUrlCon.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        while((inputLine = reader.readLine())!= null) {
            forReturn+=inputLine;
            forReturn+='\n';
        }
        reader.close();
        //System.out.println(forReturn);
        return forReturn;
    }

    // Нужно ли в интерфейсе для всех протоколов?
    static InputStream getInput(String url) throws IOException {
        //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("", 8080));
        HttpURLConnection myUrlCon;
        // Pass proxy object if needed
        myUrlCon = (HttpURLConnection) new URL(url).openConnection();
        if (!myUrlCon.getInstanceFollowRedirects()) {
            myUrlCon.setInstanceFollowRedirects(true);
        }
        return myUrlCon.getInputStream();
    }

}
