package com.capstone.clientportal.repository;
import com.capstone.clientportal.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByName(String name);
    Optional<Term> findByActiveTrue();
}
