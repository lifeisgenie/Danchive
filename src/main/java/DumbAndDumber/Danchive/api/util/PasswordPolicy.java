package DumbAndDumber.Danchive.api.util;

import java.util.regex.Pattern;

public final class PasswordPolicy {
    private static final Pattern P =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])[A-Za-z\\d[^A-Za-z0-9]]{8,16}$");
    public static boolean valid(String raw) { return raw != null && P.matcher(raw).matches(); }
    private PasswordPolicy() {}
}
