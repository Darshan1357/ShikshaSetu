package com.shikshasetu.backend.service;

import com.shikshasetu.backend.model.Course;
import com.shikshasetu.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.shikshasetu.backend.model.User;
import com.shikshasetu.backend.specification.CourseSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    public Optional<Course> getCourseById(Long id) {
    return courseRepository.findById(id);
    }

    public Course updateCourse(Long id, Course updated, User instructor) {
    Course course = courseRepository.findById(id).orElseThrow();
    if (!course.getInstructor().getId().equals(instructor.getId())) {
        throw new RuntimeException("Not your course to update!");
    }

    course.setTitle(updated.getTitle());
    course.setDescription(updated.getDescription());
    course.setDurationInWeeks(updated.getDurationInWeeks());

    return courseRepository.save(course);
    }

    public void deleteCourse(Long id, User instructor) {
    Course course = courseRepository.findById(id).orElseThrow();
    if (!course.getInstructor().getId().equals(instructor.getId())) {
        throw new RuntimeException("Not your course to delete!");
    }

    courseRepository.delete(course);
    }

    public Page<Course> searchCourses(String keyword, String category, String instructor,
                                  Double minPrice, Double maxPrice, int page, int size, String sortBy) {

    Specification<Course> spec = Specification.where(null);

    if (keyword != null && !keyword.isEmpty())
        spec = spec.and(CourseSpecification.hasTitleLike(keyword));

    if (category != null && !category.isEmpty())
        spec = spec.and(CourseSpecification.hasCategory(category));

    if (instructor != null && !instructor.isEmpty())
        spec = spec.and(CourseSpecification.hasInstructor(instructor));

    if (minPrice != null && maxPrice != null)
        spec = spec.and(CourseSpecification.hasPriceBetween(minPrice, maxPrice));

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
    return courseRepository.findAll(spec, pageable);
    }
}
