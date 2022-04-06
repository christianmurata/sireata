package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Usuario;

public class UsuarioDAO {
	
	public Usuario buscarPorLogin(String login) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();

		try{
			connectionDao.queryParam("SELECT * FROM usuarios WHERE login = ?");
			connectionDao.setString(1, login);
			
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
	
	public Usuario buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT * FROM usuarios WHERE idUsuario = ?");
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
	
	public String buscarEmail(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT email FROM usuarios WHERE idUsuario = ?");
			connectionDao.setInt(1, id);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return rs.getString("email");
			}else{
				return "";
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Usuario> listarTodos(boolean apenasAtivos) throws SQLException{
		try{
			ConnectionDAO connectionDAO =  ConnectionDAO.getInstance();		
			ResultSet rs = connectionDAO.query().executeQuery("SELECT * FROM usuarios WHERE login <> 'admin' " + (apenasAtivos ? " AND ativo = 1 " : "") + " ORDER BY nome");
			List<Usuario> list = new ArrayList<Usuario>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));			
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Usuario> listar(String nome, boolean apenasAtivos, boolean apenasExternos) throws SQLException {
		String sql = "SELECT * FROM usuarios WHERE login <> 'admin' " + 
				(!nome.isEmpty() ? " AND nome LIKE ? " : "") +
				(apenasAtivos ? " AND ativo = 1 " : "") +
				(apenasExternos ? " AND externo = 1 " : "") +
				"ORDER BY nome";
		
		try{
			ConnectionDAO connectionDAO =  ConnectionDAO.getInstance();
			connectionDAO.queryParam(sql);
		
			if(!nome.isEmpty()){
				connectionDAO.setString(1, "%" + nome + "%");
			}
			
			ResultSet rs = connectionDAO.execute();
			List<Usuario> list = new ArrayList<Usuario>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Usuario usuario) throws SQLException{
		boolean insert = (usuario.getIdUsuario() == 0);
		
		try{
			ConnectionDAO connectionDAO = ConnectionDAO.getInstance();
		
			if(insert){
				connectionDAO.queryParam("INSERT INTO usuarios(nome, login, senha, email, externo, ativo, administrador) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDAO.queryParam("UPDATE usuarios SET nome=?, login=?, senha=?, email=?, externo=?, ativo=?, administrador=? WHERE idUsuario=?");
			}
			
			connectionDAO.setString(1, usuario.getNome());
			connectionDAO.setString(2, usuario.getLogin());
			connectionDAO.setString(3, usuario.getSenha());
			connectionDAO.setString(4, usuario.getEmail());
			connectionDAO.setInt(5, usuario.isExterno() ? 1 : 0);
			connectionDAO.setInt(6, usuario.isAtivo() ? 1 : 0);
			connectionDAO.setInt(7, usuario.isAdministrador() ? 1 : 0);
			
			if(!insert){
				connectionDAO.setInt(8, usuario.getIdUsuario());
			}
			
			connectionDAO.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDAO.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					usuario.setIdUsuario(rs.getInt(1));
				}
			}
			
			return usuario.getIdUsuario();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Usuario carregarObjeto(ResultSet rs) throws SQLException{
		Usuario usuario = new Usuario();
		
		usuario.setIdUsuario(rs.getInt("idUsuario"));
		usuario.setNome(rs.getString("nome"));
		usuario.setLogin(rs.getString("login"));
		usuario.setSenha(rs.getString("senha"));
		usuario.setEmail(rs.getString("email"));
		usuario.setExterno(rs.getInt("externo") == 1);
		usuario.setAtivo(rs.getInt("ativo") == 1);
		usuario.setAdministrador(rs.getInt("administrador") == 1);
		
		return usuario;
	}
	
	public String[] buscarEmails(int[] ids) throws SQLException{
		String sql = "";
		
		for(int id : ids){
			if(sql == "")
				sql = String.valueOf(id);
			else
				sql = sql + ", " + String.valueOf(id);
		}
		
		if(sql != ""){
			List<String> emails = new ArrayList<String>();
			
			try{
				ConnectionDAO connectionDAO = ConnectionDAO.getInstance();
				ResultSet rs = connectionDAO.query().executeQuery("SELECT email FROM usuarios WHERE idUsuario IN (" + sql + ")");
			
				while(rs.next()){
					emails.add(rs.getString("email"));
				}
				
				return (String[])emails.toArray();
			} catch (SQLException sqlException) {
				throw new SQLException(sqlException);
			}
		}else
			return null;
	}
	
	public boolean podeCriarAta(int idUsuario) throws SQLException{		
		try{
			ConnectionDAO connectionDAO = ConnectionDAO.getInstance();
			ResultSet rs = connectionDAO.query().executeQuery("SELECT COUNT(orgaos.idOrgao) AS qtde FROM orgaos " +
				"WHERE idPresidente=" + String.valueOf(idUsuario) + " OR idSecretario=" + String.valueOf(idUsuario));
		
			if(rs.next()){
				return (rs.getInt("qtde") > 0);
			}else{
				return false;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}

}
