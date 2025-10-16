package DumbAndDumber.Danchive.api.dto.home.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BannerUpsertRequest {
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String altText;
}
