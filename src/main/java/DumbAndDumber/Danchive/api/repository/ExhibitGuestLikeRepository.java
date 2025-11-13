package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.*;

public interface ExhibitGuestLikeRepository extends JpaRepository<ExhibitGuestLike, Long> {
    long countByExhibit(Exhibit exhibit);

    @Modifying
    @Query(value = """
        insert into exhibit_guest_likes(gid, exhibit_id)
        select ?1, ?2 from dual
        where not exists (
            select 1 from exhibit_guest_likes where gid=?1 and exhibit_id=?2
        )
        """, nativeQuery = true)
    void saveIfAbsent(String gid, Exhibit exhibit);

    @Modifying
    @Query("delete from ExhibitGuestLike e where e.gid = ?1 and e.exhibit = ?2")
    void deleteByGidAndExhibit(String gid, Exhibit exhibit);
}