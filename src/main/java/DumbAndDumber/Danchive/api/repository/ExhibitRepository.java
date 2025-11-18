package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Exhibit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExhibitRepository extends JpaRepository<Exhibit, Long>, JpaSpecificationExecutor<Exhibit> {
    @Query("select distinct e.term from Exhibit e where e.term is not null order by e.term desc")
    List<String> findDistinctTermsOrderByDesc();

    List<Exhibit> findByTermOrderByCreatedAtAsc(String term);
}