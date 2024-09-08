package com.cesarmarcos.petshop.services;

import com.cesarmarcos.petshop.entities.Usuario;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface UsuarioService {

    Single<List<Usuario>> list(int limit, int page);

    Single<Usuario> addUser(Usuario usuario);

    Single<Usuario> getDetails(String id);

    Completable update(String id, Usuario usuario);

    Completable delete (String id);

}
