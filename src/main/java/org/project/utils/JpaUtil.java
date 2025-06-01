package org.project.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class JpaUtil {
    public static EntityManager getEntityManager() {
        // Create a new EntityManager - setting a new connection to DB
       return Persistence.createEntityManagerFactory("hrPU").createEntityManager();
    }
}
