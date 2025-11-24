package DumbAndDumber.Danchive.api.dto.exhibit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ExhibitionCreateRequest {

    @NotBlank
    private String term;      // "2025-2"  (Exhibit.term 과 1:1)

    @NotBlank
    private String title;     // 전시회 제목

    @NotBlank
    private String place;     // 장소

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;   // 하루짜리 전시 날짜

    // 전시회 대표 이미지 (포스터 등)
    private MultipartFile image;
}