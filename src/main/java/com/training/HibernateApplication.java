package com.training;

import com.training.models.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Sandeep.K on 7/7/2016.
 */
public class HibernateApplication {

    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("HibernateTraining");
    private static EntityManager entityManager;

    private static Profile insertProfile(String name, String email, Experience experience){
        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setName(name);
        profile.addExperience(experience);
        return profile;
    }
    private static Experience insertExperience(Company company, String designation){
        Experience experience = new Experience();
        experience.setDesignation(designation);
        experience.setCompany(company);
        return experience;
    }
    private static Company insertCompany(Company company, String companyName, String url){
        if(company != null){
            return company;
        }
        Company newCompany = new Company();
        newCompany.setName(companyName);
        newCompany.setUrl(url);
        return newCompany;
    }

    public static Profile insertData(String name, String email, String companyName, String url, String designation, Company company){
        return insertProfile(
                name,
                email,
                insertExperience(
                        insertCompany(
                                company,
                                companyName,
                                url
                        ),
                        designation
                )
        );
    }

    private static void persistList(Object[] objects){
        Arrays.asList(objects).forEach(
            obj -> entityManager.persist(obj)
        );
    }

    private static void queryData(){
        List<Profile> profiles = entityManager.createQuery("from Profile p where p.id = :id", Profile.class)
                .setParameter("id", 1L)
                .getResultList()
                .stream()
                .collect(Collectors.toList());

        profiles.forEach(
            com.training.HibernateApplication::print
        );

        profiles.forEach(
            x -> x.getExperiences().forEach(
                com.training.HibernateApplication::print
            )
        );

        profiles.forEach(
            x -> x.getExperiences().forEach(
                y -> print(y.getCompany())
            )
        );
    }

    private static void queryCustomData(){
        List<BusinessCard> businessCards = entityManager.createQuery(
                "select new com.training.models.BusinessCard(p.name, e.designation, c.name) " +
                " from Profile p, Experience e, Company c " +
                " where p.id = e.profile.id and c.id = e.company.id",
                BusinessCard.class
        ).getResultList();

        businessCards.forEach(com.training.HibernateApplication::print);
    }

    private static void criteriaQueryData(){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

        Root<Profile> root = criteriaQuery.from(Profile.class);
        Join<Profile, Experience> experience = root.join("experiences", JoinType.INNER);

        //SELECT e.designation, p.email, p.name FROM Profile p, Experience e
        criteriaQuery.select(criteriaBuilder.array(experience.get(Experience_.designation), root.get(Profile_.email), root.get(Profile_.name)));

        //WHERE p.id = 1L OR p.name = 'Sandeep'
        criteriaQuery.where(
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get(Profile_.id), 1L),
                criteriaBuilder.equal(root.get(Profile_.name), "Sandeep")
            )
        );

//      print(entityManager.createQuery(criteriaQuery).unwrap(org.hibernate.Query.class).getQueryString());

        List<Object[]> profiles = entityManager.createQuery(criteriaQuery).getResultList();
        profiles.forEach(
            x -> Arrays.asList(x).forEach(
                com.training.HibernateApplication::print
            )
        );
    }

    private static void print(Object obj){
        System.out.println("OUTPUT:::::::::::::::  " + obj.toString());
    }

    public static void main(String[] args){
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Profile profile1 = insertData("Sandeep", "sandeep@betsol.com", "BETSOL", "betsol.com", "SE", null);
        Profile profile2 = insertData("Akshay", "akshay@betsol.com", "BETSOL", "betsol.com", "SE", profile1.getExperiences().get(0).getCompany());
        Profile profile3 = insertData("Prarthana", "prarthana@google.com", "GOOGLE", "google.com", "TL", null);
        Profile profile4 = insertData("Shrita", "shrita@google.com", "GOOGLE", "google.com", "ASE", profile3.getExperiences().get(0).getCompany());
        Profile profile5 = insertData("Santhosh", "santhosh@avaya.com", "AVAYA", "avaya.com", "Director", null);

        persistList(new Profile[] {profile1, profile2, profile3, profile4, profile5});

        //Query simple data using JPQL
        queryData();

        //Query custom data using JPQL
        queryCustomData();

        //Query data using CriteriaQuery
        criteriaQueryData();

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
