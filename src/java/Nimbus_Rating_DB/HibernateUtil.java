/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nimbus_Rating_DB;


import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author nimil.christopher
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    private static final SessionFactory sessionFactoryInputCDR;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
            sessionFactoryInputCDR = new AnnotationConfiguration().configure("hibernate-InputCDR.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static SessionFactory getInputCDRSessionFactory() {
        return sessionFactoryInputCDR;
    }
}
