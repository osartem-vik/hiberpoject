package ua.world.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "world", name = "country")
public class Country {
    @Id
    private Long id;


}
