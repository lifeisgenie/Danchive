package DumbAndDumber.Danchive.api.service.thumbnail;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class ImageThumbServiceImpl implements ImageThumbService {
    @Override
    public byte[] toThumbnail(byte[] original, int width, int height, boolean centerCrop) {
        try (var in = new ByteArrayInputStream(original);
             var out = new ByteArrayOutputStream()) {
            var builder = Thumbnails.of(in).size(width, height);
            if (centerCrop) {
                builder.crop(net.coobird.thumbnailator.geometry.Positions.CENTER)
                        .keepAspectRatio(false);
            }
            builder.outputFormat("jpg").toOutputStream(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("썸네일 생성 실패", e);
        }
    }
}
