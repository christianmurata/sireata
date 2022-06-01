package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.dao.DepartamentoDAO;
import br.edu.utfpr.dv.sireata.model.Departamento;

public class DepartamentoFactory extends DaoFactory {
    @Override
    public Dao<Departamento> getDao() {
        return new DepartamentoDAO();
    }
}