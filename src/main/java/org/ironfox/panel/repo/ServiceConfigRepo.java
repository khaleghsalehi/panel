package org.ironfox.panel.repo;

import org.ironfox.panel.model.ServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceConfigRepo extends JpaRepository<ServiceConfig, Long> {
    @Query("SELECT u FROM ServiceConfig u WHERE u.id > 0 ORDER BY u.id DESC")
    List<ServiceConfig> getLastConfig();
}
