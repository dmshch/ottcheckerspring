package app.protocols.mpegdash;

import app.BasicStreamOtt;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dmitryshcherbakov
 */

public class LivePlaylistMpd implements app.BasicStreamOtt{

    private String name;
    private String url;

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getUrl() {
        return url;
    }

    private HashMap<String,HashMap<String,String>> paramStream;
    @Override
    public HashMap<String, HashMap<String, String>> getParamStream() {
        return paramStream;
    }

    public LivePlaylistMpd(String name, String url) {
        this.name = name;
        this.url = url;
        this.paramStream = new HashMap<String,HashMap<String, String>>();

        loadMasterPlaylistData();
    }

    private void loadMasterPlaylistData() {
        
        String out;
        try {
            out = loadUrl(this.url);
            // Тест
            System.out.println(out);
        }
        catch (IOException io) {
            System.out.println("Ошибка при загрузке Мастер Плейлиста");
            System.exit(-1);
        }

        try {
            parsingMasterPlaylistData();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println("Ошибка при разборе XML:\n");
            e.printStackTrace();
        }
    }

    // Разбираем XML
    private void parsingMasterPlaylistData() throws ParserConfigurationException, SAXException, IOException{
        HashMap<String, String> mapForAddInParamStream = new HashMap<String,String>();
        // Получение фабрики, чтобы после получить билдер документов.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Получили из фабрики билдер, который парсит XML, создает структуру Document в виде иерархического дерева.
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Запарсили XML, создав структуру Document. Теперь у нас есть доступ ко всем элементам, каким нам нужно.
        Document document = builder.parse(BasicStreamOtt.getInput(this.url));

        // Получение списка всех выбранных элементов внутри корневого элемента (getDocumentElement возвращает ROOT элемент XML файла).
        NodeList mpdElements = document.getDocumentElement().getElementsByTagName("AdaptationSet");

        // Перебор всех элементов
        for (int i = 0; i < mpdElements.getLength(); i++) {
            Node nodeOne = mpdElements.item(i);
            // Получение атрибутов каждого элемента
            NamedNodeMap attributes = nodeOne.getAttributes();
            // Проверка. Атрибут - тоже Node, потому нам нужно получить значение атрибута с помощью метода getNodeValue()
            if (attributes.getNamedItem("mimeType").getNodeValue().equals("video/mp4")) {
                //System.out.println("video/mp4: " + attributes.getNamedItem("mimeType").getNodeValue());
                // необходимо получить:
                // в SegmentTemplate media для формирования шаблона
                // и время в SegmentTemplate и пройти по каждому Representation id
                // согласовать формат сохраняемых данных в returnMap

                // получаем производные узлы и проходимся по ним
                NodeList listNodeOne = nodeOne.getChildNodes();
                for (int j = 0; j<listNodeOne.getLength(); j++){
                    Node nodeTwo = listNodeOne.item(j);
                    // ищем первый элемент SegmentTemplate для получения его атрибута media
                    if (nodeTwo.getNodeName().equals("SegmentTemplate")){
                        // получаем шаблон для формирования ссылки на медиа-сегменты
                        //System.out.println("media in video " + nodeTwo.getAttributes().getNamedItem("media").getNodeValue());
                        NodeList listNodeTwo = nodeTwo.getChildNodes();
                        //System.out.println("size of listNodeTwo " + listNodeTwo.getLength());
                        // проверяем
                        for (int n = 0; n < listNodeTwo.getLength(); n++){
                            //System.out.println(listNodeTwo.item(n).getNodeName());
                            Node nodeThree = listNodeTwo.item(n);
                            if (nodeThree.getNodeName().equals("SegmentTimeline")){
                                //System.out.println("in SegmentTimeline:");
                                // берём все атрибуты из первой дочерней ноды, без проверки что это элемент S
                                System.out.println("-> t in video :" + nodeThree.getFirstChild().getAttributes().getNamedItem("t").getNodeValue());
                                //System.out.println(nodeThree.getFirstChild().getAttributes().getNamedItem("d").getNodeValue());
                                //System.out.println(nodeThree.getFirstChild().getAttributes().getNamedItem("r").getNodeValue());
                            }
                        }
                    }
                    // ищем элементы Representation для получения данных о профилях
                    if (nodeTwo.getNodeName().equals("Representation")){
                        mapForAddInParamStream = new HashMap<String,String>();
                        String id = nodeTwo.getAttributes().getNamedItem("id").getNodeValue();
                        mapForAddInParamStream.put("type", "video");
                        mapForAddInParamStream.put("bitrate", nodeTwo.getAttributes().getNamedItem("bandwidth").getNodeValue());
                        String width = nodeTwo.getAttributes().getNamedItem("width").getNodeValue();
                        String height = nodeTwo.getAttributes().getNamedItem("height").getNodeValue();
                        mapForAddInParamStream.put("resolution", width + "x" + height);
                        // сохраняем в paramStream
                        paramStream.put(id, mapForAddInParamStream);
                    }
                }
            }
            if (attributes.getNamedItem("mimeType").getNodeValue().equals("audio/mp4")) {
                //System.out.println("audio/mp4: " + attributes.getNamedItem("mimeType").getNodeValue());
                // Так же делаем и для аудио
                String lang = attributes.getNamedItem("lang").getNodeValue();
                // получаем производные узлы и проходимся по ним
                NodeList listNodeOne = nodeOne.getChildNodes();
                for (int j = 0; j<listNodeOne.getLength(); j++){
                    Node nodeTwo = listNodeOne.item(j);
                    // ищем первый элемент SegmentTemplate для получения его атрибута media
                    if (nodeTwo.getNodeName().equals("SegmentTemplate")){
                        // получаем шаблон для формирования ссылки на медиа-сегменты
                        //System.out.println("media in audio " + nodeTwo.getAttributes().getNamedItem("media").getNodeValue());
                        NodeList listNodeTwo = nodeTwo.getChildNodes();
                        for (int n = 0; n < listNodeTwo.getLength(); n++){
                            Node nodeThree = listNodeTwo.item(n);
                            if (nodeThree.getNodeName().equals("SegmentTimeline")){
                                //System.out.println("in SegmentTimeline:");
                                // берём все атрибуты из первой дочерней ноды, без проверки что это элемент S
                                System.out.println("-> t in audio :" + nodeThree.getFirstChild().getAttributes().getNamedItem("t").getNodeValue());
                                //System.out.println(nodeThree.getFirstChild().getAttributes().getNamedItem("d").getNodeValue());
                                // Периодически пропадает с манифеста, проверить документацию
                                //System.out.println(nodeThree.getFirstChild().getAttributes().getNamedItem("r").getNodeValue());
                            }
                        }
                    }
                    // ищем элементы Representation для получения данных о профилях
                    if (nodeTwo.getNodeName().equals("Representation")){
                        mapForAddInParamStream = new HashMap<String,String>();
                        String id = nodeTwo.getAttributes().getNamedItem("id").getNodeValue();
                        mapForAddInParamStream.put("type", "audio");
                        mapForAddInParamStream.put("bitrate", nodeTwo.getAttributes().getNamedItem("bandwidth").getNodeValue());
                        mapForAddInParamStream.put("language",lang );
                        // сохраняем в paramStream
                        paramStream.put(id, mapForAddInParamStream);
                    }
                }
            }
        }
    }

    // для обновления манифеста что бы получить новую информацию по сегментам (или таймингу)
    private void parsingMediaPlaylistData(String line) {

    }

    @Override
    public String toString() {
        return "LivePlaylistMpd{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
