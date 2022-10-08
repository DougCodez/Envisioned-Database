package database.envisioned.drivers;

public enum DriverType {

    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}"),
    SQLITE("org.sqlite.JDBC", "jdbc:sqlite:{database}"),
    POSTGRES("org.postgresql.Driver", "jdbc:postgresql://{host}:{port}/{database}");

    private final String driverName;
    private final String driverURL;

    DriverType(String driverName, String driverURL) {
        this.driverName = driverName;
        this.driverURL = driverURL;
    }

    public static DriverType fromName(String name) {
        for (DriverType type : values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return SQLITE;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverURL() {
        return driverURL;
    }
}
