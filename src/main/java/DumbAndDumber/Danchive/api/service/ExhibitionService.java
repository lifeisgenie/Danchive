package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionSummaryDto;
import DumbAndDumber.Danchive.api.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;

    public List<ExhibitionSummaryDto> getExhibitions() {
        return exhibitionRepository.findAllByOrderByDateDesc().stream()
                .map(ExhibitionSummaryDto::from)
                .toList();
    }
}