package Controllers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;

public class FileIO {
    public String[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) return new String[0];

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        }
        return lines.toArray(new String[0]);
    }

    public void writeFile(String fileName, String[] data) throws IOException {
        File file = new File(fileName);
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Cannot create folder: " + parent);
        }

        File temp = File.createTempFile("employee-", ".tmp", parent);
        try {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8))) {
                for (String line : data) {
                    if (line != null && !line.trim().isEmpty()) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            try {
                Files.move(temp.toPath(), file.toPath(),
                           StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (temp.exists()) temp.delete();
        }
    }
}
