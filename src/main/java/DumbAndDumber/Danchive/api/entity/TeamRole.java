package DumbAndDumber.Danchive.api.entity;

public enum TeamRole {
    LEADER, MEMBER;
    public String toWire(){ return this==LEADER? "leader":"member"; }
}