package com.cesarmarcos.petshop.services;

import com.cesarmarcos.petshop.entities.Usuario;
import com.cesarmarcos.petshop.entities.dto.UsuarioDTO;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface UsuarioService {

    Single<List<Usuario>> list(int limit, int page);

    Single<UsuarioDTO> addUser(UsuarioDTO usuario);

    Single<UsuarioDTO> getDetails(String id);

    Completable update(String id, Usuario usuario);

    Completable delete (String id);

}
