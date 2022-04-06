package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Campus;

public class CampusDAO {
	
	public Campus buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT * FROM campus WHERE idCampus = ?");
		
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
	
	public Campus buscarPorDepartamento(int idDepartamento) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT idCampus FROM departamentos WHERE idDepartamento=?");
		
			connectionDao.setInt(1, idDepartamento);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.buscarPorId(rs.getInt("idCampus"));
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Campus> listarTodos(boolean apenasAtivos) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT * FROM campus " + (apenasAtivos ? " WHERE ativo=1" : "") + " ORDER BY nome");
		
			List<Campus> list = new ArrayList<Campus>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Campus> listarParaCriacaoAta(int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();

		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT campus.* FROM campus " +
				"INNER JOIN departamentos ON departamentos.idCampus=campus.idCampus " +
				"INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
				"WHERE campus.ativo=1 AND (orgaos.idPresidente=" + String.valueOf(idUsuario) + " OR orgaos.idSecretario=" + String.valueOf(idUsuario) + 
				") ORDER BY campus.nome");
		
			List<Campus> list = new ArrayList<Campus>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Campus> listarParaConsultaAtas(int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT campus.* FROM campus " +
				"INNER JOIN departamentos ON departamentos.idCampus=campus.idCampus " +
				"INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
				"INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
				"INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND ataParticipantes.idUsuario=" + String.valueOf(idUsuario) + 
				" ORDER BY campus.nome");
		
			List<Campus> list = new ArrayList<Campus>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Campus campus) throws SQLException{
		boolean insert = (campus.getIdCampus() == 0);
		
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO campus(nome, endereco, logo, ativo, site) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE campus SET nome=?, endereco=?, logo=?, ativo=?, site=? WHERE idCampus=?");
			}
			
			connectionDao.setString(1, campus.getNome());
			connectionDao.setString(2, campus.getEndereco());
			if(campus.getLogo() == null){
				connectionDao.setInt(3, Types.BINARY);
			}else{
				connectionDao.setBytes(3, campus.getLogo());	
			}
			connectionDao.setInt(4, campus.isAtivo() ? 1 : 0);
			connectionDao.setString(5, campus.getSite());
			
			if(!insert){
				connectionDao.setInt(6, campus.getIdCampus());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					campus.setIdCampus(rs.getInt(1));
				}
			}
			
			return campus.getIdCampus();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Campus carregarObjeto(ResultSet rs) throws SQLException{
		Campus campus = new Campus();
		
		campus.setIdCampus(rs.getInt("idCampus"));
		campus.setNome(rs.getString("nome"));
		campus.setEndereco(rs.getString("endereco"));
		campus.setLogo(rs.getBytes("logo"));
		campus.setAtivo(rs.getInt("ativo") == 1);
		campus.setSite(rs.getString("site"));
		
		return campus;
	}

}
