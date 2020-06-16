package com.westeroscraft.westerosentities.persistence;

import java.util.Date;
import java.util.UUID;

public class DirewolfData {
    public UUID ownerUUID;
    public int coat;
    public String name;
    public Date dateCreated;
    public DirewolfData(UUID ownerUUID, int coat, String name, Date dateCreated) {
        this.ownerUUID = ownerUUID;
        this.coat = coat;
        this.name = name;
        this.dateCreated = dateCreated;
    }
}
