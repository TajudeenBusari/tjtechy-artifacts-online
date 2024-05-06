package com.tjtechy.artifactsOnline.artifact;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

//we will define 3 static methods using Id, Name and Description
public class ArtifactSpecs {
  public static Specification<Artifact> hasId(String providedId){
    return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("id"), providedId);
  }

  public static Specification<Artifact> containsName(String providedName){
    return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + providedName.toLowerCase() + "%");
  }

  public static Specification<Artifact> containsDescription(String providedDescription){
    return (root, query, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + providedDescription.toLowerCase() + "%");
  }

  public static Specification<Artifact> hasOwnerName(String provideOwnerName){
    return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(criteriaBuilder.lower(root.get("owner").get("name")), provideOwnerName.toLowerCase());

  }
}


/*
* root: is the persistent entity, Artifact
* this method is translated to an SQL query:
*   (select *
*   from artifact
*   where id = providedId)
* */