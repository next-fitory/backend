package org.fitory;

import boot.XpringApplication;
import boot.XpringBootApplication;

@XpringBootApplication(port = 8080)
public class Application {
    public static void main(String[] args) {
        XpringApplication.run(Application.class);
    }
}
