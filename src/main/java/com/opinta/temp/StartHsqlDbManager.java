package com.opinta.temp;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StartHsqlDbManager {

    private static final String SA_STRIGN = "sa";

    @PostConstruct
    public void startDBManager() {
        DatabaseManagerSwing.main(new String[]{
                "--url", "jdbc:hsqldb:mem:testdb",
                "--user", SA_STRIGN, "--password",
                SA_STRIGN
        });
    }
}
