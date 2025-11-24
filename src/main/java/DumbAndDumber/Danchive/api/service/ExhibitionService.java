package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionCreateRequest;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionSummaryDto;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionUpdateRequest;
import DumbAndDumber.Danchive.api.entity.Exhibition;
import DumbAndDumber.Danchive.api.repository.ExhibitionRepository;
import DumbAndDumber.Danchive.api.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final FileStorageService fileStorageService;

    private static final Pattern TERM_PATTERN = Pattern.compile("^\\d{4}-(1|2)$");

    @Transactional(readOnly = true)
    public List<ExhibitionSummaryDto> getExhibitions() {
        return exhibitionRepository.findAllByOrderByDateDesc().stream()
                .map(ExhibitionSummaryDto::from)
                .toList();
    }

    // === ADMIN ===

    public Long createExhibition(ExhibitionCreateRequest req) {
        validateTerm(req.getTerm());

        String imageUrl = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            imageUrl = fileStorageService.upload("exhibitions/", req.getImage());
        }

        Exhibition e = Exhibition.builder()
                .term(req.getTerm())
                .title(req.getTitle())
                .place(req.getPlace())
                .date(req.getDate())
                .imageUrl(imageUrl)
                .build();

        return exhibitionRepository.save(e).getId();
    }

    public Long updateExhibition(Long id, ExhibitionUpdateRequest req) {
        Exhibition e = exhibitionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("exhibition not found"));

        if (req.getTitle() != null) e.setTitle(req.getTitle());
        if (req.getPlace() != null) e.setPlace(req.getPlace());
        if (req.getDate() != null) e.setDate(req.getDate());

        if (req.getImage() != null && !req.getImage().isEmpty()) {
            String imageUrl = fileStorageService.upload("exhibitions/", req.getImage());
            e.setImageUrl(imageUrl);
        }

        return e.getId();
    }

    public void deleteExhibition(Long id) {
        Exhibition e = exhibitionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("exhibition not found"));
        exhibitionRepository.delete(e);
    }

    private void validateTerm(String term) {
        if (term == null || !TERM_PATTERN.matcher(term).matches()) {
            throw new IllegalArgumentException("학기 형식이 올바르지 않습니다. 예) 2025-2");
        }
    }
}