package DumbAndDumber.Danchive.api.service.thumbnail;

public interface ImageThumbService {
    byte[] toThumbnail(byte[] originalBytes, int width, int height, boolean centerCrop);
}