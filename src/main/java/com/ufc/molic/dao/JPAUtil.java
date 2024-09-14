package com.ufc.molic.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.util.Properties;

class Config {

    private static final Properties props = new Properties();

    public static Properties getConfig() {

        try {
            props.load(Config.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return props;

    }
}

public class JPAUtil {

    private static final EntityManagerFactory emf;

    private static final ThreadLocal<EntityManager> ems = new ThreadLocal<>();

    static {
        Properties props = Config.getConfig();
        String persistenceUnit = props.getProperty("persistence.unit");
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    public JPAUtil() {
    }

    /**
     * Obtém o EntityManager vinculado à Thread atual. Se não existir, é criado e vinculado à Thread atual.
     */
    public static EntityManager getEntityManager() {
        EntityManager em = ems.get();
        if (em == null) {
            em = emf.createEntityManager();
            ems.set(em);
        }
        return em;
    }

    /**
     *  Fecha o EntityManager atrelado à Thread atual e retira-o da ThreadLocal.
     */
    public static void closeEntityManager() {
        EntityManager em = ems.get();
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                tx.commit();
            }
            em.close();
            ems.remove();
        }
    }

    public static void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    public static void commit() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (tx.isActive()) {
            tx.commit();
        }
    }

    public static void rollback() {
        EntityTransaction tx = getEntityManager().getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
    }

}