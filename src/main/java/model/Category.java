package model;

import java.util.Objects;
import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    public CategoryDifficultyEnum difficulty;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryDifficultyEnum getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(CategoryDifficultyEnum difficulty) {
        this.difficulty = difficulty;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
