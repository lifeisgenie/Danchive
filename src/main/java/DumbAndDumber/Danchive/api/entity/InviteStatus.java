package DumbAndDumber.Danchive.api.entity;

public enum InviteStatus {
    PENDING, ACCEPTED, REJECTED;
    public String toWire(){ return name().toLowerCase(); }
    public static InviteStatus fromWire(String s){
        return switch (s.toLowerCase()) {
            case "pending" -> PENDING;
            case "accepted" -> ACCEPTED;
            case "rejected" -> REJECTED;
            default -> PENDING;
        };
    }
}