package org.project.exception.sql;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityClass) {
        super(String.format("Entity of type %s not found", entityClass.getName()));
    }

    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super(String.format("Entity of type %s with ID %d not found", entityClass.getName(), id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
