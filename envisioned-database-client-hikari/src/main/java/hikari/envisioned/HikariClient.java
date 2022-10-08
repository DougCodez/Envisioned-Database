package hikari.envisioned;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import database.envisioned.client.AbstractClient;
import database.envisioned.connection.ConnectionAuth;
import database.envisioned.connection.ConnectionType;
import database.envisioned.drivers.DriverType;

import java.util.Properties;

public abstract class HikariClient extends AbstractClient<HikariDataSource> implements ConnectionType {

    private HikariDataSource dataSource;

    public HikariClient init(Properties properties, DriverType type, ConnectionAuth auth) {
        dataSource = new HikariDataSource(getDataProperties(properties, type, auth));
        return this;
    }

    public HikariConfig getDataProperties(Properties properties, DriverType type, ConnectionAuth auth) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(type.getDriverName());
        config.setUsername(auth.user());
        config.setPassword(auth.password());
        config.setJdbcUrl(generateUrl(type.getDriverURL(), auth));
        //Set your properties at your own stance: Key: String & Value: Object
        config.setDataSourceProperties(properties);
        return config;
    }

    @Override
    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
