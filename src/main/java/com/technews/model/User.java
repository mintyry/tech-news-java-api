package com.technews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

//persisting object so it can map to a table
@Entity
//ignore these properties when converted to JSON object
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//Name the table
@Table(name = "user")
public class User {
}
