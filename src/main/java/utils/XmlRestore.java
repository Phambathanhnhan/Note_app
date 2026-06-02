package utils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import model.Note;

public class XmlRestore {
    public static List<Note> importxml(InputStream is) {
        List<Note> list = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList nList = doc.getElementsByTagName("Note");
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                String title = el.getElementsByTagName("Title").item(0).getTextContent();
                String content = el.getElementsByTagName("Content").item(0).getTextContent();

                Note restoredNote = new Note();
                restoredNote.title = title;
                restoredNote.content = content;
                list.add(restoredNote);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}