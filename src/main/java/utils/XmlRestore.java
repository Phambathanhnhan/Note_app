package utils;

import model.Note;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlRestore {
    // Hàm đọc file XML (viết thường toàn bộ)
    public static List<Note> importxml(File file) {
        List<Note> list = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Note");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    // Rút trích Tiêu đề và Nội dung từ các thẻ XML
                    String title = eElement.getElementsByTagName("Title").item(0).getTextContent();
                    String content = eElement.getElementsByTagName("Content").item(0).getTextContent();

                    // ID đặt là 0 vì khi đẩy lên Server, MySQL sẽ tự động cấp ID mới
                    list.add(new Note(0, title, content));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}