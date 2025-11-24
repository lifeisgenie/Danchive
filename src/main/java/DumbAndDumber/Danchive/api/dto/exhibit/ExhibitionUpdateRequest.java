package DumbAndDumber.Danchive.api.dto.exhibit;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ExhibitionUpdateRequest {

    private String title;
    private String place;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private MultipartFile image;
}
