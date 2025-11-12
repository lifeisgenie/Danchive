package DumbAndDumber.Danchive.api.service.thumbnail;

public interface ImageThumbService {
    // 원본 이미지 받아서 썸네일(일부분 크롭 or 축소) 파일 바이트 배열 반환
    byte[] toThumbnail(byte[] originalBytes, int width, int height, boolean centerCrop);
}
