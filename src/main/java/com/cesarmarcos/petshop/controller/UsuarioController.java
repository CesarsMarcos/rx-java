package com.cesarmarcos.petshop.controller;

import com.cesarmarcos.petshop.entities.Usuario;
import com.cesarmarcos.petshop.services.UsuarioService;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/usuarios/")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public Single<ResponseEntity<List<Usuario>>> list(@RequestParam(value = "limit", defaultValue = "5") int limit,
                                                    @RequestParam(value = "page", defaultValue = "0") int page){
        return usuarioService.list(limit, page)
                .subscribeOn(Schedulers.io())
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Single<ResponseEntity<?>> save(@RequestBody Usuario usuario) {
        return usuarioService
                .addUser(usuario)
                .subscribeOn(Schedulers.io())
                .map(x -> ResponseEntity
                        .created(URI.create("/api/usuarios/" + usuario.getId()))
                        .build());
    }

    @GetMapping("details/{id}")
    public Single<ResponseEntity<Usuario>> details(@PathVariable String id){
         return usuarioService.getDetails(id)
                 .subscribeOn(Schedulers.io())
                 .map(ResponseEntity::ok);
    }

    @PutMapping("{id}")
    public Single<ResponseEntity<Void>> update(@PathVariable String id,@RequestBody Usuario usuario){
        return usuarioService.update(id,usuario)
                .subscribeOn(Schedulers.io())
                .andThen(Single.fromCallable(() -> ResponseEntity.ok().<Void>build()));
    }

    @DeleteMapping("{id}")
    public Single<ResponseEntity<Void>> delete(@PathVariable String id){
        return usuarioService.delete(id)
                .subscribeOn(Schedulers.io())
                .andThen(Single.fromCallable(() -> ResponseEntity.ok().<Void>build()));
    }

}
