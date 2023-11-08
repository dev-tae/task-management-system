package io.github.devtae.taskmanagementsystem.repository;

import io.github.devtae.taskmanagementsystem.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // custom query methods if needed
}
