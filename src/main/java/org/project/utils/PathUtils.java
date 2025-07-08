package org.project.utils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@SuppressWarnings({"unchecked"})
public class PathUtils {
    public <Y> Path<Y> getPath(Path<?> path, Class<?> javaType) {
        if (javaType == String.class) {
            return (Path<Y>) path.as(String.class);
        } else if (javaType == Integer.class) {
            return (Path<Y>) path.as(Integer.class);
        } else if (javaType == Long.class) {
            return (Path<Y>) path.as(Long.class);
        } else if (javaType == Double.class) {
            return (Path<Y>) path.as(Double.class);
        } else if (javaType == Float.class) {
            return (Path<Y>) path.as(Float.class);
        } else if (javaType == BigDecimal.class)
            return (Path<Y>) path.as(BigDecimal.class);
        return (Path<Y>) path;
    }

    public Path<Object> getRealPath(Root<?> root, String fieldName, JoinType joinType) {
        String[] fieldParts = fieldName.split("\\.");
        Join<?, ?> join = root.join(fieldParts[0], joinType);
        for (int i = 1; i < fieldParts.length - 1; i++) {
            join = join.join(fieldParts[i], JoinType.LEFT);
        }
        return join.get(fieldParts[fieldParts.length - 1]);
    }
}
