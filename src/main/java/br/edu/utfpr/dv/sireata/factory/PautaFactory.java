package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.dao.PautaDAO;
import br.edu.utfpr.dv.sireata.model.Pauta;

public class PautaFactory extends DaoFactory {
    @Override
    public Dao<Pauta> getDao() {
        return new PautaDAO();
    }
}