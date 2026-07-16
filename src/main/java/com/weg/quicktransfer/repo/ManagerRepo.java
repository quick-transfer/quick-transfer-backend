package com.weg.quicktransfer.repo;

import org.apache.catalina.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepo extends JpaRepository<Manager, Long>{
    
}
