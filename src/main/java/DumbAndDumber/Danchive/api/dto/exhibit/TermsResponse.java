package DumbAndDumber.Danchive.api.dto.exhibit;

import java.util.List;

public record TermsResponse(String currentTerm, List<String> terms) {}