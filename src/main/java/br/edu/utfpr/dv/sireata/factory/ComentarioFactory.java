package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.ComentarioDAO;
import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.model.Comentario;

public class ComentarioFactory extends DaoFactory {
    @Override
    public Dao<Comentario> getDao() {
        return new ComentarioDAO();
    }
}