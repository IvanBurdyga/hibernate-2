package entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "film", schema = "movie")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Long filmId;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "release_year")
    private Integer releaseYear;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;


    @Column(name = "original_language_id")
    private Long originalLanguageId;

    @Column(name = "rental_duration", nullable = false)
    private Short rentalDuration;

    @Column(name = "rental_rate", nullable = false)
    private Float rentalRate;

    @Column(name = "length")
    private Short length;

    @Column(name = "replacement_cost", nullable = false)
    private Float replacementCost;

    @Column(name = "rating")
    private String rating;

    @Column(name = "special_features") //todo
    private String specialFeatures;

    @ManyToMany
    @JoinTable(name="film_category",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "category_id"))
    private List<Category> categories;

    @ManyToMany
    @JoinTable(name="film_actor",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "actor_id"))
    private List<Actor> actors;

    @OneToOne(mappedBy = "film")
    private FilmText filmText;

    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
}
