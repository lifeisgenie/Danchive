package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.exhibit.*;
import DumbAndDumber.Danchive.api.entity.*;
import DumbAndDumber.Danchive.api.repository.*;
import DumbAndDumber.Danchive.api.service.storage.FileStorageService;
import DumbAndDumber.Danchive.api.service.thumbnail.ImageThumbService;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ExhibitService {

    private final ExhibitRepository exhibitRepository;
    private final ExhibitLikeRepository likeRepository;
    private final ExhibitGuestLikeRepository guestLikeRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final FileStorageService fileStorageService;
    private final ImageThumbService imageThumbService;

    public List<String> getTerms() { return exhibitRepository.findDistinctTermsOrderByDesc(); }

    public Page<ExhibitListItemDto> search(String term, Set<ExhibitCategory> categories, String q, boolean awardOnly, Pageable pageable) {
        Specification<Exhibit> spec = (root, cq, cb) -> {
            List<Predicate> ands = new ArrayList<>();
            ands.add(cb.equal(root.get("term"), term));
            if (categories != null && !categories.isEmpty()) ands.add(root.join("categories").in(categories));
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim() + "%";
                ands.add(cb.or(cb.like(root.get("title"), like), cb.like(root.get("intro"), like), cb.like(root.get("shortIntro"), like)));
            }
            if (awardOnly) ands.add(cb.isNotEmpty(root.get("awards")));
            return cb.and(ands.toArray(Predicate[]::new));
        };
        Page<Exhibit> page = exhibitRepository.findAll(spec, pageable);
        return page.map(e -> ExhibitListItemDto.builder()
                .id(e.getId()).term(e.getTerm()).title(e.getTitle())
                .teamName(e.getTeam().getName())
                .thumbnailUrl(e.getThumbnailUrl())
                .shortIntro(e.getShortIntro())
                .categories(e.getCategories())
                .awards(e.getAwards())
                .build());
    }

    @Transactional(readOnly = true)
    public ExhibitDetailDto get(Long id) {
        Exhibit e = exhibitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("exhibit not found"));
        long likesUsers = likeRepository.countByExhibit(e);
        long likesGuests = guestLikeRepository.countByExhibit(e);
        long likes = likesUsers + likesGuests;

        List<ExhibitDetailDto.MemberDto> members = teamMembershipRepository.findByTeam(e.getTeam()).stream()
                .map(m -> ExhibitDetailDto.MemberDto.builder().name(m.getUser().getName()).role(m.getRole()).build())
                .toList();

        ExhibitDetailDto.PptDto ppt = null;
        if (e.getPptName() != null) {
            ppt = ExhibitDetailDto.PptDto.builder()
                    .name(e.getPptName()).previewUrl(e.getPptPreviewUrl()).downloadUrl(e.getPptDownloadUrl()).build();
        }

        return ExhibitDetailDto.builder()
                .id(e.getId()).term(e.getTerm()).title(e.getTitle()).intro(e.getIntro())
                .categories(e.getCategories()).members(members)
                .thumbnailUrl(e.getThumbnailUrl()).posterUrl(e.getPosterUrl())
                .ppt(ppt).stats(new ExhibitDetailDto.StatsDto(e.getViews(), likes)).awards(e.getAwards())
                .build();
    }

    public Long create(Long teamId, ExhibitCreateRequest req) {
        User me = SecurityUtil.getCurrentUserOrThrow();
        assertLeader(teamId, me);

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NoSuchElementException("team not found"));
        if (req.getPoster() == null || req.getPoster().isEmpty()) throw new IllegalArgumentException("poster is required");
        String posterUrl = upload("posters/", req.getPoster());
        String thumbUrl  = generateThumbAndUpload(req.getPoster(), "thumbnails/");

        String pptName = null, pptDownloadUrl = null;
        if (req.getPpt() != null && !req.getPpt().isEmpty()) {
            pptName = Optional.ofNullable(req.getPpt().getOriginalFilename()).orElse("발표자료.pptx");
            pptDownloadUrl = upload("ppt/origin/", req.getPpt());
        }

        Exhibit e = Exhibit.builder()
                .term(req.getTerm()).title(req.getTitle()).intro(req.getIntro()).shortIntro(req.getShortIntro())
                .categories(new LinkedHashSet<>(req.getCategories()))
                .team(team).posterUrl(posterUrl).thumbnailUrl(thumbUrl)
                .pptName(pptName).pptDownloadUrl(pptDownloadUrl).build();

        return exhibitRepository.save(e).getId();
    }

    public Long update(Long exhibitId, ExhibitUpdateRequest req) {
        User me = SecurityUtil.getCurrentUserOrThrow();
        Exhibit e = exhibitRepository.findById(exhibitId).orElseThrow(() -> new NoSuchElementException("exhibit not found"));

        boolean isLeader = isLeader(e.getTeam().getId(), me);
        boolean isMember = isMember(e.getTeam().getId(), me);
        if (!isLeader && !isMember) throw new SecurityException("not a team member");

        if (req.getTitle()!=null) e.setTitle(req.getTitle());
        if (req.getIntro()!=null) e.setIntro(req.getIntro());
        if (req.getShortIntro()!=null) e.setShortIntro(req.getShortIntro());
        if (req.getCategories()!=null && !req.getCategories().isEmpty())
            e.setCategories(new LinkedHashSet<>(req.getCategories()));

        if (isLeader) {
            if (req.getPoster()!=null && !req.getPoster().isEmpty()) {
                e.setPosterUrl(upload("posters/", req.getPoster()));
                e.setThumbnailUrl(generateThumbAndUpload(req.getPoster(), "thumbnails/"));
            }
            if (req.getPpt()!=null && !req.getPpt().isEmpty()) {
                e.setPptName(Optional.ofNullable(req.getPpt().getOriginalFilename()).orElse("발표자료.pptx"));
                e.setPptDownloadUrl(upload("ppt/origin/", req.getPpt()));
            }
        }
        return e.getId();
    }

    public void delete(Long exhibitId) {
        Exhibit e = exhibitRepository.findById(exhibitId).orElseThrow(() -> new NoSuchElementException("exhibit not found"));
        exhibitRepository.delete(e);
    }

    public long increaseViews(Long id) {
        Exhibit e = exhibitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("exhibit not found"));
        e.setViews(e.getViews()+1);
        return e.getViews();
    }

    public Map<String,Object> like(Long id) {
        Exhibit e = exhibitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("exhibit not found"));
        if (SecurityUtil.isGuest()) {
            String gid = Optional.ofNullable(SecurityUtil.getGuestIdOrNull()).orElseThrow(() -> new IllegalStateException("invalid guest"));
            guestLikeRepository.saveIfAbsent(gid, e);
        } else {
            User me = SecurityUtil.getCurrentUserOrThrow();
            likeRepository.findByExhibitAndUser(e, me).ifPresent(l -> { throw new IllegalStateException("already liked"); });
            likeRepository.save(ExhibitLike.builder().exhibit(e).user(me).build());
        }
        long cnt = likeRepository.countByExhibit(e) + guestLikeRepository.countByExhibit(e);
        return Map.of("likes", cnt, "liked", true);
    }

    public Map<String,Object> unlike(Long id) {
        Exhibit e = exhibitRepository.findById(id).orElseThrow(() -> new NoSuchElementException("exhibit not found"));
        if (SecurityUtil.isGuest()) {
            String gid = Optional.ofNullable(SecurityUtil.getGuestIdOrNull()).orElseThrow(() -> new IllegalStateException("invalid guest"));
            guestLikeRepository.deleteByGidAndExhibit(gid, e);
        } else {
            User me = SecurityUtil.getCurrentUserOrThrow();
            likeRepository.findByExhibitAndUser(e, me).ifPresent(likeRepository::delete);
        }
        long cnt = likeRepository.countByExhibit(e) + guestLikeRepository.countByExhibit(e);
        return Map.of("likes", cnt, "liked", false);
    }

    private void assertLeader(Long teamId, User me){ if (!isLeader(teamId, me)) throw new SecurityException("only team leader"); }
    private boolean isLeader(Long teamId, User me){
        return teamMembershipRepository.findByTeam_IdAndUser_Id(teamId, me.getId()).map(m -> m.getRole()==TeamRole.LEADER).orElse(false);
    }
    private boolean isMember(Long teamId, User me){
        return teamMembershipRepository.findByTeam_IdAndUser_Id(teamId, me.getId()).isPresent();
    }

    private String upload(String basePath, MultipartFile file){ return fileStorageService.upload(basePath, file); }
    private String generateThumbAndUpload(MultipartFile poster, String basePath){
        try {
            byte[] thumb = imageThumbService.toThumbnail(poster.getBytes(), 640, 360, true);
            return fileStorageService.upload(basePath, new InMemoryMultipart("thumb.jpg","image/jpeg",thumb));
        } catch (Exception ex) { throw new RuntimeException("thumbnail generation failed", ex); }
    }
}