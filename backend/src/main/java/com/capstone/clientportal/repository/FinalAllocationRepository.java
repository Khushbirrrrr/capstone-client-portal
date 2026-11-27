package com.capstone.clientportal.repository;
import com.capstone.clientportal.entity.FinalAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FinalAllocationRepository extends JpaRepository<FinalAllocation,Long> {
 List<FinalAllocation> findByTermIdOrderByGroupId(Long termId);
}
