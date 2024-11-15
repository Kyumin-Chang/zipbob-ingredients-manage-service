package cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRefrigeratorId(Long refrigeratorId);

    List<Ingredient> findByType(IngredientType type);

    Optional<Ingredient> findByRefrigeratorIdAndType(Long refrigeratorId, IngredientType type);
}
