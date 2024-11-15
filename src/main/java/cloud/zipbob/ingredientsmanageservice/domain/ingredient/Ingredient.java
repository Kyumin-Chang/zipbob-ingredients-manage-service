package cloud.zipbob.ingredientsmanageservice.domain.ingredient;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "ingredients")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IngredientType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    @CreatedDate
    private LocalDate addedDate;

    @Column(nullable = false)
    private LocalDate expiredDate;

    @ManyToOne
    @JoinColumn(name = "refrigerator_id")
    @JsonBackReference
    private Refrigerator refrigerator;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
