package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.ProfessorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfessorEvaluationRepository extends JpaRepository<ProfessorEvaluation, Long> {

    Optional<ProfessorEvaluation> findByExhibit_IdAndProfessor_Id(Long exhibitId, Long professorId);

    @Query("""
        select pe.exhibit.id,
               avg( (pe.technicalScore + pe.impactScore + pe.creativityScore) / 3.0 )
        from ProfessorEvaluation pe
        where pe.exhibit.term = :term
        group by pe.exhibit.id
        """)
    List<Object[]> findSummaryByTerm(String term);
}