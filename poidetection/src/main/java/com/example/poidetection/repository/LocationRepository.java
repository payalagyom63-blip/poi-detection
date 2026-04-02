package com.example.poidetection.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.poidetection.entity.Location;
import com.example.poidetection.entity.User;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findTopByUserOrderByTimestampDesc(User user);
}