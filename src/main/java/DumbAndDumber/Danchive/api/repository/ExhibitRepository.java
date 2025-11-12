package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Exhibit;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExhibitRepository extends JpaRepository<Exhibit, Long>, JpaSpecificationExecutor<Exhibit> {
    @Query("select distinct e.term from Exhibit e order by e.term desc")
    List<String> findDistinctTermsOrderByDesc();
}
