package hibernate.envisioned;

import database.envisioned.client.AbstractClient;
import database.envisioned.connection.ConnectionAuth;
import database.envisioned.connection.ConnectionType;
import database.envisioned.drivers.DriverType;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public abstract class HibernateClient extends AbstractClient<MetadataSources> implements ConnectionType {

    private MetadataSources metadataSource;

    private Metadata metadata;

    public HibernateClient init(Properties properties, DriverType type, ConnectionAuth connectionAuth) {
        metadataSource = new MetadataSources(getDataProperties(properties, type, connectionAuth));
        return this;
    }

    public ServiceRegistry getDataProperties(Properties properties, DriverType type, ConnectionAuth connectionAuth) {
        return new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DRIVER, type.getDriverName())
                .applySetting(AvailableSettings.URL, generateUrl(type.getDriverURL(), connectionAuth))
                .applySetting(AvailableSettings.USER, connectionAuth.user())
                .applySetting(AvailableSettings.PASS, connectionAuth.password())
                .applySettings(properties)
                .build();
    }

    @Override
    public MetadataSources getDataSource() {
        return metadataSource;
    }

    public Metadata getMetadata() {
        if (metadata == null) {
            metadata = metadataSource.buildMetadata();
        }

        return metadata;
    }

    public SessionFactory buildSessionFactory() {
        return metadata.buildSessionFactory();
    }

    //Getting connection for hibernate: buildSessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
}
