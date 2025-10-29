package com.cesarmarcos.petshop.entities.dto;

import com.cesarmarcos.petshop.entities.Role;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

  private String id;

  @NotBlank
  private String username;

  private String password;

  private Boolean estado;

  private Role rol;

}
