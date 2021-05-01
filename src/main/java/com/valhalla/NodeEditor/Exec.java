package com.valhalla.NodeEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Exec {
    protected List<UUID> nextConnections;
    protected List<UUID> previousConnections;
    protected SocketDirection direction;

    public Exec(SocketDirection direction) {
        nextConnections = new ArrayList<>();
        previousConnections = new ArrayList<>();
        this.direction = direction;
    }

    public Exec(SocketDirection direction, List<UUID> previousConnections, List<UUID> nextConnections) {
        this(direction);
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
        System.out.println("[next]"+nextConnections);
    }

    public void addPreviousConnector(UUID uuid) {
        previousConnections.add(uuid);
        System.out.println("[prev]"+previousConnections);
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

    // TODO
    public static Iterable<Map<UUID, UUID>> resolveConnections(Exec[] execs) {
        return null;
    }

}
