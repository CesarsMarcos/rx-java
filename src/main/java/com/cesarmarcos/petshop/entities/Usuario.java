package com.cesarmarcos.petshop.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String usuario;

    private String password;

    private Boolean estado;

    private Role rol;

}
