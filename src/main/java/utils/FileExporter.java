package utils;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileExporter {
    public static void exporttxt(OutputStream os, String title, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("TITLE: " + title);
            writer.write("\n\nCONTENT:\n" + content);
            writer.flush(); // Cực kỳ quan trọng để đẩy dữ liệu qua mạng
        } catch (Exception e) { e.printStackTrace(); }
    }
}