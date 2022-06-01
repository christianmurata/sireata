package br.edu.utfpr.dv.sireata.factory;

import br.edu.utfpr.dv.sireata.dao.Dao;

public abstract class DaoFactory {
    public enum DaoProvider {
        ANEXO,
        ATA,
        ATAPARTICIPANTE,
        CAMPUS,
        COMENTARIO,
        DEPARTAMENTO,
        ORGAO,
        PAUTA,
        USUARIO
    }

    public static Dao<?> select(DaoProvider daoProvider) {
        switch (daoProvider) {
            case ANEXO: return new AnexoFactory().getDao();
            case ATA: return new AtaFactory().getDao();
            case ATAPARTICIPANTE: return new AtaParticipanteFactory().getDao();
            case CAMPUS: return new CampusFactory().getDao();
            case COMENTARIO: return new ComentarioFactory().getDao();
            case DEPARTAMENTO: return new DepartamentoFactory().getDao();
            case ORGAO: return new OrgaoFactory().getDao();
            case PAUTA: return new PautaFactory().getDao();
            case USUARIO: return new UsuarioFactory().getDao();
            default: return null;
        }
    }

    public abstract Dao<?> getDao();
}
