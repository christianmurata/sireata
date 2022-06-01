package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.AnexoDAO;
import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.model.Anexo;

public class AnexoFactory extends DaoFactory {
    @Override
    public Dao<Anexo> getDao() {
        return new AnexoDAO();
    }
}