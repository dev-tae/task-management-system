package io.github.devtae.taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot be longer than 100 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    @Column(name = "description")
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed")
    private boolean isCompleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Checks if the current task entity has updates compared to the details provided.
     * Compares editable fields of the task to see if any changes were made.
     *
     * @param taskDetails the task details to compare against.
     * @return true if there are changes, false otherwise.
     */
    public boolean hasUpdates(Task taskDetails) {
        // Check title for changes, as it should not be null or empty
        if (!Objects.equals(this.title, taskDetails.getTitle())) return true;

        // Check description for changes. It could be an update from non-empty to empty.
        if (!Objects.equals(this.description, taskDetails.getDescription())) return true;

        // Check dueDate for changes. The date might have been adjusted.
        if (!Objects.equals(this.dueDate, taskDetails.getDueDate())) return true;

        // Check completed status for changes, which can be toggled by the user.
        if (this.isCompleted() != taskDetails.isCompleted()) return true;

        // If none of the fields have been changed, return false.
        return false;
    }
}
