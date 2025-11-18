package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.PopularVote;
import DumbAndDumber.Danchive.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PopularVoteRepository extends JpaRepository<PopularVote, Long> {

    boolean existsByTermAndVoter(String term, User voter);

    @Query("""
        select pv.exhibit.id,
               avg( (pv.creativityScore + pv.completionScore) / 2.0 ),
               count(pv)
        from PopularVote pv
        where pv.term = :term
        group by pv.exhibit.id
        """)
    List<Object[]> findSummaryByTerm(String term);
}