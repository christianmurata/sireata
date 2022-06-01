package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.AtaDAO;
import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.model.Ata;

public class AtaFactory extends DaoFactory {
    @Override
    public Dao<Ata> getDao() {
        return new AtaDAO();
    }
}