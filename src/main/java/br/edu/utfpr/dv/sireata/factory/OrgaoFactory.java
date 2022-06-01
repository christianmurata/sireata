package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.dao.OrgaoDAO;
import br.edu.utfpr.dv.sireata.model.Orgao;

public class OrgaoFactory extends DaoFactory {
    @Override
    public Dao<Orgao> getDao() {
        return new OrgaoDAO();
    }
}