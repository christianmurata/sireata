package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Orgao;
import br.edu.utfpr.dv.sireata.model.OrgaoMembro;
import br.edu.utfpr.dv.sireata.model.Usuario;

public class OrgaoDAO {
	
	public Orgao buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				"WHERE orgaos.idOrgao = ?");
		
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
	
	public List<Orgao> listarTodos(boolean apenasAtivos) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				(apenasAtivos ? " WHERE orgaos.ativo=1" : "") + " ORDER BY orgaos.nome");
		
			List<Orgao> list = new ArrayList<Orgao>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Orgao> listarPorDepartamento(int idDepartamento) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				"WHERE orgaos.idDepartamento = ? ORDER BY orgaos.nome");
		
			connectionDao.setInt(1, idDepartamento);
			
			ResultSet rs = connectionDao.execute();
			
			List<Orgao> list = new ArrayList<Orgao>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Orgao> listarPorCampus(int idCampus) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				"WHERE departamentos.idCampus = ? ORDER BY departamentos.nome, orgaos.nome");
		
			connectionDao.setInt(1, idCampus);
			
			ResultSet rs = connectionDao.execute();
			
			List<Orgao> list = new ArrayList<Orgao>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Orgao> listarParaCriacaoAta(int idDepartamento, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				"WHERE orgaos.ativo=1 AND orgaos.idDepartamento=" + String.valueOf(idDepartamento) + " AND (orgaos.idPresidente=" + String.valueOf(idUsuario) + " OR orgaos.idSecretario=" + String.valueOf(idUsuario) + 
				") ORDER BY orgaos.nome");
		
			List<Orgao> list = new ArrayList<Orgao>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Orgao> listarParaConsultaAtas(int idDepartamento, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT DISTINCT orgaos.*, p.nome AS presidente, s.nome AS secretario, departamentos.nome AS departamento FROM orgaos " +
				"INNER JOIN departamentos ON departamentos.iddepartamento=orgaos.iddepartamento " +
				"INNER JOIN atas ON atas.idOrgao=orgaos.idOrgao " +
				"INNER JOIN ataParticipantes ON ataParticipantes.idAta=atas.idAta " +
				"INNER JOIN usuarios p ON p.idusuario=orgaos.idpresidente " +
				"INNER JOIN usuarios s ON s.idusuario=orgaos.idsecretario " +
				"WHERE atas.publicada=0 AND ataParticipantes.presente=1 AND orgaos.idDepartamento=" + String.valueOf(idDepartamento) + " AND ataParticipantes.idUsuario=" + String.valueOf(idUsuario) + 
				" ORDER BY orgaos.nome");
		
			List<Orgao> list = new ArrayList<Orgao>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public Usuario buscarPresidente(int idOrgao) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT idPresidente FROM orgaos WHERE idOrgao = ?");
		
			connectionDao.setInt(1, idOrgao);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				UsuarioDAO dao = new UsuarioDAO();
				
				return dao.buscarPorId(rs.getInt("idPresidente"));
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public Usuario buscarSecretario(int idOrgao) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT idSecretario FROM orgaos WHERE idOrgao = ?");
		
			connectionDao.setInt(1, idOrgao);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				UsuarioDAO dao = new UsuarioDAO();
				
				return dao.buscarPorId(rs.getInt("idSecretario"));
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public boolean isMembro(int idOrgao, int idUsuario) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam(
				"SELECT * FROM membros WHERE idOrgao = ? AND idUsuario=?");
		
			connectionDao.setInt(1, idOrgao);
			connectionDao.setInt(2, idUsuario);
			
			ResultSet rs = connectionDao.execute();
			
			return rs.next();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Orgao orgao) throws SQLException{
		boolean insert = (orgao.getIdOrgao() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		connectionDao.getConnection().setAutoCommit(false);
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO orgaos(idDepartamento, idPresidente, idSecretario, nome, nomeCompleto, designacaoPresidente, ativo) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE orgaos SET idDepartamento=?, idPresidente=?, idSecretario=?, nome=?, nomeCompleto=?, designacaoPresidente=?, ativo=? WHERE idOrgao=?");
			}
			
			connectionDao.setInt(1, orgao.getDepartamento().getIdDepartamento());
			connectionDao.setInt(2, orgao.getPresidente().getIdUsuario());
			connectionDao.setInt(3, orgao.getSecretario().getIdUsuario());
			connectionDao.setString(4, orgao.getNome());
			connectionDao.setString(5, orgao.getNomeCompleto());
			connectionDao.setString(6, orgao.getDesignacaoPresidente());
			connectionDao.setInt(7, (orgao.isAtivo() ? 1 : 0));
			
			if(!insert){
				connectionDao.setInt(8, orgao.getIdOrgao());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					orgao.setIdOrgao(rs.getInt(1));
				}
			}
			
			connectionDao.queryParam("DELETE FROM membros WHERE idOrgao=" + String.valueOf(orgao.getIdOrgao()));
			connectionDao.getPreparedStatement().execute();
			
			for(OrgaoMembro u : orgao.getMembros()){
				connectionDao.queryParam("INSERT INTO membros(idOrgao, idUsuario, designacao) VALUES(?, ?, ?)");
				
				connectionDao.setInt(1, orgao.getIdOrgao());
				connectionDao.setInt(2, u.getUsuario().getIdUsuario());
				connectionDao.setString(3, u.getDesignacao());
				
				connectionDao.getPreparedStatement().execute();
			}
			
			connectionDao.getConnection().commit();
			
			return orgao.getIdOrgao();
		}catch(SQLException e){
			connectionDao.getConnection().rollback();
			
			throw e;
		}finally{
			connectionDao.getConnection().setAutoCommit(true);
		}
	}
	
	private Orgao carregarObjeto(ResultSet rs) throws SQLException{
		Orgao orgao = new Orgao();
		
		orgao.setIdOrgao(rs.getInt("idOrgao"));
		orgao.getDepartamento().setIdDepartamento(rs.getInt("idDepartamento"));
		orgao.getDepartamento().setNome(rs.getString("departamento"));
		orgao.getPresidente().setIdUsuario(rs.getInt("idPresidente"));
		orgao.getPresidente().setNome(rs.getString("presidente"));
		orgao.getSecretario().setIdUsuario(rs.getInt("idSecretario"));
		orgao.getSecretario().setNome(rs.getString("secretario"));
		orgao.setNome(rs.getString("nome"));
		orgao.setNomeCompleto(rs.getString("nomeCompleto"));
		orgao.setDesignacaoPresidente(rs.getString("designacaoPresidente"));
		orgao.setAtivo(rs.getInt("ativo") == 1);
		
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();		
		ResultSet rs2 = connectionDao.query().executeQuery("SELECT membros.*, usuarios.nome FROM membros " +
				"INNER JOIN usuarios ON usuarios.idUsuario=membros.idUsuario " +
				"WHERE idOrgao=" + String.valueOf(orgao.getIdOrgao()) + " ORDER BY usuarios.nome");
		
				while(rs2.next()){
			OrgaoMembro membro = new OrgaoMembro();
			
			membro.getUsuario().setIdUsuario(rs2.getInt("idUsuario"));
			membro.getUsuario().setNome(rs2.getString("nome"));
			membro.setDesignacao(rs2.getString("designacao"));
			
			orgao.getMembros().add(membro);
		}
		
		return orgao;
	}

}
