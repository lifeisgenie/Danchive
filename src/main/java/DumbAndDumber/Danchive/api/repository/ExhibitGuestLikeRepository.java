package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitGuestLikeRepository extends JpaRepository<ExhibitGuestLike, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO exhibit_guest_likes (gid, exhibit_id)
        VALUES (:gid, :exhibitId)
        ON DUPLICATE KEY UPDATE id = id
        """,
            nativeQuery = true)
    void saveIfAbsent(@Param("gid") String gid, @Param("exhibitId") Long exhibitId);

    @Modifying
    void deleteByGidAndExhibit(String gid, Exhibit exhibit);

    long countByExhibit(Exhibit exhibit);
}