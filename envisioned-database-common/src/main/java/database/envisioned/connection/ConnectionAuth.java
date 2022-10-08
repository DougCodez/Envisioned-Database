package database.envisioned.connection;

public record ConnectionAuth(String host, int port, String database, String user, String password){
}
