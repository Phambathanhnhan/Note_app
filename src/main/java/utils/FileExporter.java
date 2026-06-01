package utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FileExporter {
    public static void exporttxt(File file, String title, String content) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            writer.write("TIÊU ĐỀ: " + title);
            writer.write("\n\nNỘI DUNG:\n" + content);
        } catch (Exception e) { e.printStackTrace(); }
    }
}