package com.cesarmarcos.petshop.repository;


import com.cesarmarcos.petshop.entities.Usuario;
import io.reactivex.Maybe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuario, String> {

   @Query("SELECT u FROM Usuario u WHERE u.id = :id")
   Maybe<Usuario> findByUsuarioId(@Param("id") String id);

   Optional<Usuario> findById(String id);

}

