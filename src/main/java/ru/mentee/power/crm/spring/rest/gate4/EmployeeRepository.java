package ru.mentee.power.crm.spring.rest.gate4;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @Query("SELECT e FROM Employee e")
    List<Employee> findAllEmployees();

    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.salary = :newSalary WHERE e.id = :id")
    int updateSalaryById(@Param("id") UUID id, @Param("newSalary") BigDecimal newSalary);

    @Modifying
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.id = :id")
    void deleteEmployeeById(@Param("id") UUID id);
}
