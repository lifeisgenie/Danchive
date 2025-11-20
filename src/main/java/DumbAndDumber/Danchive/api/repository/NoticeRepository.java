package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Notice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByCreatedAtDesc();

    default List<Notice> findTopNByOrderByCreatedAtDesc(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return findAll(pageable).getContent();
    }
}