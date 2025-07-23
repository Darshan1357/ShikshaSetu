package com.shikshasetu.backend.specification;

import com.shikshasetu.backend.model.Course;
//import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
// import jakarta.persistence.criteria.CriteriaBuilder;
// import jakarta.persistence.criteria.Predicate;
// import jakarta.persistence.criteria.Root;

public class CourseSpecification {

    public static Specification<Course> hasTitleLike(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Course> hasCategory(String category) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Course> hasInstructor(String instructorName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("instructor").get("name")), "%" + instructorName.toLowerCase() + "%");
    }

    public static Specification<Course> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) ->
                cb.between(root.get("price"), min, max);
    }
}
