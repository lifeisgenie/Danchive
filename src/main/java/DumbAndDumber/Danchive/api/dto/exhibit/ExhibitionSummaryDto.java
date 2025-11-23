package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.Exhibition;

import java.time.LocalDate;

public record ExhibitionSummaryDto(
        Long id,
        String term,
        String title,
        String imageUrl,
        String place,
        LocalDate date
) {
    public static ExhibitionSummaryDto from(Exhibition e) {
        return new ExhibitionSummaryDto(
                e.getId(),
                e.getTerm(),
                e.getTitle(),
                e.getImageUrl(),
                e.getPlace(),
                e.getDate()
        );
    }
}