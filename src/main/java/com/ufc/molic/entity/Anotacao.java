package com.ufc.molic.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Entity
public class Anotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String note;

    public Anotacao() {
    }

    public Anotacao(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Anotacao [id=" + id + ", note=" + note + "]";
    }
}
