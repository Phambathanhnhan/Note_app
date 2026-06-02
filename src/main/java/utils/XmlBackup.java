package utils;
import java.io.OutputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import model.Note;

public class XmlBackup {
    public static void exportxml(OutputStream os, List<Note> notes) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("NoteList");
            doc.appendChild(root);

            for (Note n : notes) {
                Element noteEl = doc.createElement("Note");
                root.appendChild(noteEl);

                Element title = doc.createElement("Title");
                title.appendChild(doc.createTextNode(n.title));
                noteEl.appendChild(title);

                Element content = doc.createElement("Content");
                content.appendChild(doc.createTextNode(n.content));
                noteEl.appendChild(content);
            }
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(os));
        } catch (Exception e) { e.printStackTrace(); }
    }
}