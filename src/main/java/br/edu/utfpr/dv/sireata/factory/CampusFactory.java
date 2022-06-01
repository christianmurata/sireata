package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.CampusDAO;
import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.model.Campus;

public class CampusFactory extends DaoFactory {
    @Override
    public Dao<Campus> getDao() {
        return new CampusDAO();
    }
}