package com.yookos.nsconsumer;


import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * Created by jome on 2016/01/19.
 */

@SpringBootApplication
@PropertySources({@PropertySource("classpath:app.properties")})
public class Application implements CommandLineRunner{
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

    @Autowired
    Session session;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
//        testProperties();
        log.info("Starting Application");
    }
}
