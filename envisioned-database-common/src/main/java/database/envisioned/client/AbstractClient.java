package database.envisioned.client;

import database.envisioned.connection.ConnectionAuth;

public abstract class AbstractClient<Source> implements Client<Source> {

    public AbstractClient(){

    }

    public String generateUrl(String url, ConnectionAuth connectionAuth) {
        String urlImpl = url.replace("{host}", connectionAuth.host());
        urlImpl = urlImpl.replace("{port}", String.valueOf(connectionAuth.port()));
        urlImpl = urlImpl.replace("{database}", connectionAuth.database());
        return urlImpl;
    }
}
