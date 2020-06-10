package com.westeroscraft.westerosdirewolves.persistence;

import java.util.UUID;

public class DirewolfData {
    public UUID ownerUUID;
    public int coat;
    public String name;
    public DirewolfData(UUID ownerUUID, int coat, String name) {
        this.ownerUUID = ownerUUID;
        this.coat = coat;
        this.name = name;
    }
}
