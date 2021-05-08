package com.valhalla.NodeEditor.Primitive;

import com.valhalla.NodeEditor.Sockets.SocketDirection;

import java.util.*;

public class Exec implements java.io.Serializable{
    protected UUID uuid;
    public List<UUID> nextConnections;
    public List<UUID> previousConnections;
    protected SocketDirection direction;

    public Exec(UUID uuid, SocketDirection direction) {
        this.uuid = uuid;
        this.nextConnections = new ArrayList<>();
        this.previousConnections = new ArrayList<>();
        this.direction = direction;
    }

    public Exec(UUID uuid, SocketDirection direction, List<UUID> previousConnections, List<UUID> nextConnections) {
        this(uuid, direction);
        this.previousConnections = previousConnections;
        this.nextConnections = nextConnections;
    }

    public void setPreviousConnections(List<UUID> previousConnections) {
        this.previousConnections = previousConnections;
    }

    public void setNextConnections(List<UUID> nextConnections) {
        this.nextConnections = nextConnections;
    }

    public void addNextConnector(UUID uuid) {
        nextConnections.add(uuid);
    }

    public void addPreviousConnector(UUID uuid) {
        previousConnections.add(uuid);
    }

    public void removeNextConnector(UUID uuid) {
        nextConnections.remove(uuid);
    }

    public void removePreviousConnector(UUID uuid) {
        previousConnections.remove(uuid);
    }

    public Iterable<UUID> getAllNextConnections() {
        return nextConnections;
    }

    public Iterable<UUID> getAllPreviousConnections() {
        return previousConnections;
    }

    public int getNextConnectionCount() {
        return nextConnections.size();
    }

    public int getPreviousConnectionCount() {
        return previousConnections.size();
    }

    public boolean searchNextConnections(UUID uuidToSearch) {
        return nextConnections.contains(uuidToSearch);
    }

    public boolean searchPreviousConnections(UUID uuidToSearch) {
        return previousConnections.contains(uuidToSearch);
    }

    public static Map<UUID, List<UUID>> resolveConnections(Exec[] execs) {
        HashMap<UUID, List<UUID>> resolvedList = new HashMap<>();
        for (Exec exec : execs) {
            UUID execUuid = exec.uuid;
            List<UUID> nextUuids = new ArrayList<>();
            for (UUID uuid : exec.getAllNextConnections())
                nextUuids.add(uuid);
            resolvedList.put(execUuid, nextUuids);
        }
        return resolvedList;
    }

    public static Map<UUID, Iterable<UUID>> resolveConnections(Iterable<Exec> execs) {
        HashMap<UUID, Iterable<UUID>> resolvedList = new HashMap<>();
        for (Exec exec : execs) {
            UUID execUuid = exec.uuid;
            List<UUID> nextUuids = new ArrayList<>();
            for (UUID uuid : exec.getAllNextConnections())
                nextUuids.add(uuid);
            resolvedList.put(execUuid, nextUuids);
        }
        return resolvedList;
    }
}
