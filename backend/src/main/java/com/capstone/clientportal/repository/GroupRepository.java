package com.capstone.clientportal.repository;
import com.capstone.clientportal.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface GroupRepository extends JpaRepository<StudentGroup, Long> {
    List<StudentGroup> findByTermId(Long termId);
    boolean existsByNameAndTermId(String name, Long termId);
}
