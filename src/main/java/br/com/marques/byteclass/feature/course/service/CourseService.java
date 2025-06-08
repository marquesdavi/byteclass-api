package br.com.marques.byteclass.feature.course.service;

import java.util.List;

public interface CourseService <ID, Entity, Req, Res> {
    void create(Req dto);
    List<Res> list();
    void update(ID id, Req dto);
    Res getByID(ID id);
    void delete(ID id);

}
