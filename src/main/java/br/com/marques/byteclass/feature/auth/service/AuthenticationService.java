package br.com.marques.byteclass.feature.auth.service;

public interface AuthenticationService<Entity, Req, Res> {
    Res authenticate(Req request);
    Entity getAuthenticated();
}
