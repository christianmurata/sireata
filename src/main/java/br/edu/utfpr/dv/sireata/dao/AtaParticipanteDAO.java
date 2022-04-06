package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.AtaParticipante;

public class AtaParticipanteDAO {
	
	public AtaParticipante buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT ataparticipantes.*, usuarios.nome AS nomeParticipante FROM ataparticipantes " +
				"INNER JOIN usuarios ON usuarios.idUsuario=ataparticipantes.idUsuario " +
				"WHERE idAtaParticipante = ?");
		
			connectionDao.setInt(1, id);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<AtaParticipante> listarPorAta(int idAta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT ataparticipantes.*, usuarios.nome AS nomeParticipante FROM ataparticipantes " +
				"INNER JOIN usuarios ON usuarios.idUsuario=ataparticipantes.idUsuario " + 
				"WHERE idAta=" + String.valueOf(idAta) + " ORDER BY usuarios.nome");
		
			List<AtaParticipante> list = new ArrayList<AtaParticipante>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(AtaParticipante participante) throws SQLException{
		boolean insert = (participante.getIdAtaParticipante() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO ataparticipantes(idAta, idUsuario, presente, motivo, designacao, membro) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE ataparticipantes SET idAta=?, idUsuario=?, presente=?, motivo=?, designacao=?, membro=? WHERE idAtaParticipante=?");
			}
			
			connectionDao.setInt(1, participante.getAta().getIdAta());
			connectionDao.setInt(2, participante.getParticipante().getIdUsuario());
			connectionDao.setInt(3, (participante.isPresente() ? 1 : 0));
			connectionDao.setString(4, participante.getMotivo());
			connectionDao.setString(5, participante.getDesignacao());
			connectionDao.setInt(6, (participante.isMembro() ? 1 : 0));
			
			if(!insert){
				connectionDao.setInt(7, participante.getIdAtaParticipante());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					participante.setIdAtaParticipante(rs.getInt(1));
				}
			}
			
			return participante.getIdAtaParticipante();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public void excluir(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			connectionDao.query().executeQuery("DELETE FROM ataparticipantes WHERE idAtaParticipante=" + String.valueOf(id));
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private AtaParticipante carregarObjeto(ResultSet rs) throws SQLException{
		AtaParticipante participante = new AtaParticipante();
		
		participante.setIdAtaParticipante(rs.getInt("idAtaParticipante"));
		participante.getAta().setIdAta(rs.getInt("idAta"));
		participante.getParticipante().setIdUsuario(rs.getInt("idUsuario"));
		participante.getParticipante().setNome(rs.getString("nomeParticipante"));
		participante.setPresente(rs.getInt("presente") == 1);
		participante.setMotivo(rs.getString("motivo"));
		participante.setDesignacao(rs.getString("designacao"));
		participante.setMembro(rs.getInt("membro") == 1);
		
		return participante;
	}

}
