package br.edu.utfpr.dv.sireata.dao;

import java.sql.SQLException;

public interface Dao<T> {
    public T buscarPorId(int id) throws SQLException;

    public int salvar(T data) throws SQLException;

    public void excluir(int id) throws SQLException;
}
