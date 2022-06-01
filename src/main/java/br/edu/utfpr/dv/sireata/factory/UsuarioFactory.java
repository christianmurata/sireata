package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.dao.UsuarioDAO;
import br.edu.utfpr.dv.sireata.model.Usuario;

public class UsuarioFactory extends DaoFactory {
    @Override
    public Dao<Usuario> getDao() {
        return new UsuarioDAO();
    }
}