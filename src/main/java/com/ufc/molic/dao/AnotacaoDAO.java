package com.ufc.molic.dao;

import com.ufc.molic.entity.Anotacao;

import java.util.List;

public interface AnotacaoDAO {

    void save(Anotacao anotacao);
    void update(Anotacao anotacao);
    void delete(int id);
    Anotacao find(String note);
    List<Anotacao> find();

}
