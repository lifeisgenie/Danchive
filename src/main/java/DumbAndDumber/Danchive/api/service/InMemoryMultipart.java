package DumbAndDumber.Danchive.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class InMemoryMultipart implements MultipartFile {
    private final String name;
    private final String contentType;
    private final byte[] bytes;

    public InMemoryMultipart(String name, String contentType, byte[] bytes) {
        this.name = name; this.contentType = contentType; this.bytes = bytes;
    }
    @Override public String getName() { return name; }
    @Override public String getOriginalFilename() { return name; }
    @Override public String getContentType() { return contentType; }
    @Override public boolean isEmpty() { return bytes.length == 0; }
    @Override public long getSize() { return bytes.length; }
    @Override public byte[] getBytes() { return bytes; }
    @Override public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }
    @Override public void transferTo(File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) { fos.write(bytes); }
    }
}
