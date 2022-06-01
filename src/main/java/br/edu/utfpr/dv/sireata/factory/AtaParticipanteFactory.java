package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.AtaParticipanteDAO;
import br.edu.utfpr.dv.sireata.dao.Dao;
import br.edu.utfpr.dv.sireata.model.AtaParticipante;

public class AtaParticipanteFactory extends DaoFactory {
    @Override
    public Dao<AtaParticipante> getDao() {
        return new AtaParticipanteDAO();
    } 
}