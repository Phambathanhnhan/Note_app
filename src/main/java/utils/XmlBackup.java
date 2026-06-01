package utils;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.Note;

public class XmlBackup {
    public static void exportxml(File file, List<Note> notes) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("NoteList");
            doc.appendChild(rootElement);

            for (Note note : notes) {
                Element nElement = doc.createElement("Note");
                rootElement.appendChild(nElement);

                Element id = doc.createElement("Id");
                id.appendChild(doc.createTextNode(String.valueOf(note.id)));
                nElement.appendChild(id);

                Element title = doc.createElement("Title");
                title.appendChild(doc.createTextNode(note.title));
                nElement.appendChild(title);

                Element content = doc.createElement("Content");
                content.appendChild(doc.createTextNode(note.content));
                nElement.appendChild(content);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) { e.printStackTrace(); }
    }
}