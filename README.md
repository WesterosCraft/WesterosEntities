# WesterosEntities 1.12.2-1.3.0
### Overview
Forge-based custom entities for WesterosCraft. Currently contains Direwolves.
### DB Setup
CREATE TABLE playerboundentities (PlayerUUID VARCHAR(36), DirewolfColor INT, DirewolfName TEXT, DirewolfTimeCreated TIMESTAMP, CONSTRAINT PRIMARY KEY (PlayerUUID));


