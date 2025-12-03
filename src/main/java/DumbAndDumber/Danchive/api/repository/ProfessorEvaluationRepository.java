package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.ProfessorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfessorEvaluationRepository extends JpaRepository<ProfessorEvaluation, Long> {

    // 작품 하나 + 교수 하나 조합
    Optional<ProfessorEvaluation> findByExhibit_IdAndProfessor_Id(Long exhibitId, Long professorId);

    // 특정 학기(term)에 대해 전체 평가 개수 (모든 작품 + 모든 교수 포함)
    long countByExhibit_Term(String term);

    // 학기(term)별 교수 평가 요약 (작품별 평균 점수)
    @Query("""
        select pe.exhibit.id,
               avg( (pe.technicalScore + pe.impactScore + pe.creativityScore) / 3.0 )
        from ProfessorEvaluation pe
        where pe.exhibit.term = :term
        group by pe.exhibit.id
        """)
    List<Object[]> findSummaryByTerm(String term);
}