# Envisioned Database
An easy to use database API for Java projects that rely on the use of database. This database API supports drivers such as MYSQL, SQLite, and Postgres.  This database API supports clients such as Hikari and Hibernate as of now. New drivers and clients will be added in future updates! 

## Features
- Supports multiple drivers.
- Supports multiple clients.
- Easy system to execute updates and querys using the Statement Utility 
- Updated Frequently

# Usage 

## YouTube Breakdown 
https://www.youtube.com/watch?v=L5_FyodytZU

## Hikari Client
Here is a custom class that extends the wrapped HikariClient class
```Java
public class HikariTest extends HikariClient {


    public HikariTest() {
        Properties properties = new Properties();
        properties.setProperty("idleTimeout", "5000");
        properties.setProperty("maximumPoolSize", "3");
        //Properties is required for the init method
        //DriverType is required for the init method (Drivers supported: MYSQL, SQLITE, and Postgress)
        //ConnectionAuth takes in the connection requirements needed to connect to the database
        init(properties, DriverType.MYSQL, new ConnectionAuth("localhost", 3310, "testdb", "user1234", "root"));
        
        //Setting the connection type is needed if you want to use the StatementUtility class to interact with the database
        StatementUtility.getInstance().setConnectionType(this);
        initTables();
    }

    private void initTables() {
        //Statement Utility in use of creating a table
        StatementUtility.getInstance().executeUpdate("CREATE TABLE IF NOT EXISTS IDTABLE(ID INTEGER(10) NOT NULL AUTO_INCREMENT, SESSIONID VARCHAR(24)," +
                " PRIMARY KEY (ID))");
    }

    //Since I implemented the interface "ConnectionType" in the abstract class HikariClient, it is required to return your datasource's connection.
    //For Hibernate client it is "buildSessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();"
    @Override
    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```
## SessionID Classes
Here are two classes I used as the table I created requires an ID (auto generated increasing by +1), and a 16 character session ID

### SessionIDGenerator Class
```Java
public class SessionIDGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%&*()_+-=[]|,./?><";
    private boolean useLower;
    private boolean useUpper;
    private boolean useDigits;
    private boolean useSpecial;

    private SessionIDGenerator() {
        throw new UnsupportedOperationException("Empty constructor is never supported");
    }

    private SessionIDGenerator(SessionIDGeneratorBuilder builder) {
        this.useLower = builder.useLower;
        this.useUpper = builder.useUpper;
        this.useDigits = builder.useDigits;
        this.useSpecial = builder.useSpecial;
    }

    public static class SessionIDGeneratorBuilder {
        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean useSpecial;

        public SessionIDGeneratorBuilder() {
            this.useLower = false;
            this.useUpper = false;
            this.useDigits = false;
            this.useSpecial = false;
        }

        public SessionIDGeneratorBuilder useLower(boolean useLower) {
            this.useLower = useLower;
            return this;
        }

        public SessionIDGeneratorBuilder useUpper(boolean useUpper) {
            this.useUpper = useUpper;
            return this;
        }

        public SessionIDGeneratorBuilder useDigits(boolean useDigits) {
            this.useDigits = useDigits;
            return this;
        }

        public SessionIDGeneratorBuilder useSpecial(boolean useSpecial) {
            this.useSpecial = useSpecial;
            return this;
        }

        public SessionIDGenerator build() {
            return new SessionIDGenerator(this);
        }
    }

    public String generate() {
        int length = 16;
        StringBuilder sessionID = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> charCategories = Collections.synchronizedList(new LinkedList<>());
        if (useLower) {
            charCategories.add(LOWERCASE);
        }

        if (useUpper) {
            charCategories.add(UPPERCASE);
        }

        if (useDigits) {
            charCategories.add(DIGITS);
        }

        if (useSpecial) {
            charCategories.add(SPECIAL);
        }

        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            sessionID.append(charCategory.charAt(position));
        }

        return new String(sessionID);
    }
}
```

### SessionIDManager Class
```Java
public class SessionIDManager {

    public static String generateNew() {
        SessionIDGenerator sessionIDGenerator = new SessionIDGenerator.SessionIDGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .build();
        return sessionIDGenerator.generate();
    }
}
```
## Main Class
In my main class I init the HikariTest class, and I made the commands using a Scanner example. 

```Java
public class TestMain {

    public static void main(String[] args) {
        new HikariTest();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter any of these terms: {insert} {select}");

        while (true) {
            String text = scanner.nextLine();
            if (text.equalsIgnoreCase("Exit")) {
                break;
            }

            switch (text) {
                case "insert" -> {
                    AtomicReference<String> sessionID = new AtomicReference<>();
                    CompletableFuture.runAsync(() -> {
                        sessionID.set(SessionIDManager.generateNew());
                        StatementUtility.getInstance().executeUpdate("INSERT INTO IDTABLE (SESSIONID) " + "VALUES (?)", ps -> {
                            ps.setString(1, sessionID.get());
                            System.out.println("Inserted Session ID: " + sessionID.get());
                            System.out.println("Enter any of these terms: {insert} {select}");
                        });
                    });
                }

                case "select" -> CompletableFuture.runAsync(() -> {
                    StatementUtility.getInstance().executeQuery("SELECT * FROM IDTABLE", rs -> {
                        while (rs.next()) {
                            System.out.println("ID: " + rs.getInt("ID") + " | SessionID: " + rs.getString("SESSIONID"));
                        }
                        return rs;
                    });
                }).whenComplete((unused, throwable) -> System.out.println("Enter any of these terms: {insert} {select}"));
            }
        }
    }
}
```

## Output

Here is the output of the main class in use.

<img src="https://images2.imgbox.com/b8/12/kMiyipgE_o.png">

Here is an image of the database.

<img src="https://images2.imgbox.com/db/fa/PV6kBklT_o.png">

# Setting It Up
There are three files that are located in the releases tab. {envision-database-common, envision-database-client-hikari, and envision-database-hibernate}.
Insert the common file, and whatever client file that best suits you into the appropriate library folder. 

# Requirements
-Requires Java 17



