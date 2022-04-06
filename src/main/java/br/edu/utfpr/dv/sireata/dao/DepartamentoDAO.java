package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Departamento;

public class DepartamentoDAO {

	public Departamento buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT departamentos.*, campus.nome AS nomeCampus " +
				"FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
				"WHERE idDepartamento = ?");
		
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
	
	public Departamento buscarPorOrgao(int idOrgao) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT departamentos.*, campus.nome AS nomeCampus " +
				"FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
				"INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
				"WHERE orgaos.idOrgao = ?");
		
			connectionDao.setInt(1, idOrgao);
			
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
	
	public List<Departamento> listarTodos(boolean apenasAtivos) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus " +
				"FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " + 
				(apenasAtivos ? " WHERE departamentos.ativo=1" : "") + " ORDER BY departamentos.nome");
		
			List<Departamento> list = new ArrayList<Departamento>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Departamento> listarPorCampus(int idCampus, boolean apenasAtivos) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus " +
				"FROM departamentos INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
				"WHERE departamentos.idCampus=" + String.valueOf(idCampus) + (apenasAtivos ? " AND departamentos.ativo=1" : "") + " ORDER BY departamentos.nome");
		
			List<Departamento> list = new ArrayList<Departamento>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Departamento> listarParaCriacaoAta(int idCampus, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus FROM departamentos " +
				"INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
				"INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
				"WHERE departamentos.ativo=1 AND departamentos.idCampus=" + String.valueOf(idCampus) + " AND (orgaos.idPresidente=" + String.valueOf(idUsuario) + " OR orgaos.idSecretario=" + String.valueOf(idUsuario) + 
				") ORDER BY departamentos.nome");
		
			List<Departamento> list = new ArrayList<Departamento>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Departamento> listarParaConsultaAtas(int idCampus, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT departamentos.*, campus.nome AS nomeCampus FROM departamentos " +
				"INNER JOIN campus ON campus.idCampus=departamentos.idCampus " +
				"INNER JOIN orgaos ON orgaos.idDepartamento=departamentos.idDepartamento " +
				"INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
				"INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
				"WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND departamentos.idCampus=" + String.valueOf(idCampus) + " AND ataParticipantes.idUsuario=" + String.valueOf(idUsuario) + 
				" ORDER BY departamentos.nome");
		
			List<Departamento> list = new ArrayList<Departamento>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Departamento departamento) throws SQLException{
		boolean insert = (departamento.getIdDepartamento() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO departamentos(idCampus, nome, logo, ativo, site, nomeCompleto) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE departamentos SET idCampus=?, nome=?, logo=?, ativo=?, site=?, nomeCompleto=? WHERE idDepartamento=?");
			}
			
			connectionDao.setInt(1, departamento.getCampus().getIdCampus());
			connectionDao.setString(2, departamento.getNome());
			if(departamento.getLogo() == null){
				connectionDao.setInt(3, Types.BINARY);
			}else{
				connectionDao.setBytes(3, departamento.getLogo());	
			}
			connectionDao.setInt(4, departamento.isAtivo() ? 1 : 0);
			connectionDao.setString(5, departamento.getSite());
			connectionDao.setString(6, departamento.getNomeCompleto());
			
			if(!insert){
				connectionDao.setInt(7, departamento.getIdDepartamento());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					departamento.setIdDepartamento(rs.getInt(1));
				}
			}
			
			return departamento.getIdDepartamento();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Departamento carregarObjeto(ResultSet rs) throws SQLException{
		Departamento departamento = new Departamento();
		
		departamento.setIdDepartamento(rs.getInt("idDepartamento"));
		departamento.getCampus().setIdCampus(rs.getInt("idCampus"));
		departamento.setNome(rs.getString("nome"));
		departamento.setNomeCompleto(rs.getString("nomeCompleto"));
		departamento.setLogo(rs.getBytes("logo"));
		departamento.setAtivo(rs.getInt("ativo") == 1);
		departamento.setSite(rs.getString("site"));
		departamento.getCampus().setNome(rs.getString("nomeCampus"));
		
		return departamento;
	}
	
}
